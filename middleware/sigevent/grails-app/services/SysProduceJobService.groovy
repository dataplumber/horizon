import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysEventGroup
import gov.nasa.horizon.sigevent.SysNotify

class SysProduceJobService {
   private static final String All = "ALL"
   private static final int MaximumEventsPerMessage = 10
   boolean transactional = false
   def sysOutgoingService
   def sysProduceService

   def produce(isRemind) {
      log.debug("Produce started " + isRemind)
      
      Map allNotifiesTime = [:]
      def allNotifiesList = [] as Set
      
      def groups = SysEventGroup.list()
      log.debug("groups: "+groups.size())
      
      /*
      def groupAlls = SysEventGroup.findAllByCategory(SysProduceJobService.ALL)
      def notifyAlls = []
      groupAlls.each {groupAll ->
         notifyAlls.addAll(groupAll.notifies)
      }
      */
      
      groups.each {targetGroup ->
         //targetGroup.discard()
         log.debug("locking group: "+targetGroup.id+", "+targetGroup.category)
         //targetGroup = SysEventGroup.lock(targetGroup.id)
         //if(targetGroup) {
            //log.debug("locked group: "+targetGroup.id)
            //def notifies = group.notifies
            def notifies = SysNotify.withCriteria {
               if (isRemind) {
                  isNotNull("remindRate")
                  gt("remindRate", 0)
               }
               or {
                  eq("group.id", targetGroup.id)
                  'group' {
                     eq("category", SysProduceJobService.All)
                     eq("type", targetGroup.type)
                  }
               }
            }
            /*
            notifies.each {
               log.debug("\tnotify: "+it.id)
            }
            */
            //def events = group.events
            /*
            if(group.category != SysProduceJobService.ALL) {
               def extraNotifies = notifyAlls.findAll {notifyAll ->
                  notifyAll.group.type == group.type
               }
               notifies.addAll(extraNotifies)
            }
            */
            
            notifies.each {notify ->
               if (notify.group.category == SysProduceJobService.All && notifies.find { it.group.category != SysProduceJobService.All && it.contact.equalsIgnoreCase(notify.contact)}) {
                  log.debug("Skip processing ALL subscription for notify: ${notify.id}")
                  return
               }
               log.debug("\tnotify: "+notify.id)
               def currentTime = allNotifiesTime.get(notify.id) ? allNotifiesTime.get(notify.id) : new Date().getTime()
               long triggeringTime = isRemind ? (notify.lastRemind ? notify.lastRemind : 0) + (1L * notify.remindRate * (60 * 60 * 1000)) : notify.lastReport + (notify.rate * (60 * 1000))
               log.debug("currentTime: "+currentTime+", triggeringTime: "+triggeringTime)
               
               if(currentTime >= triggeringTime) {
               	boolean messagesCreated = false
                  if (isRemind) {
                     messagesCreated = createMessages(MessageType.REMINDER, notify, currentTime, targetGroup)
                  } else {
                     messagesCreated = createMessages(MessageType.NEW, notify, currentTime, targetGroup)
                     messagesCreated = createMessages(MessageType.RESOLVED, notify, currentTime, targetGroup) ? true : messagesCreated
                  }

                  if(messagesCreated) {
                     //eventsForNotify.each {
                     //   if(!consumedEventIds.contains(it.id)) {
                     //      consumedEventIds.add(it.id)
                     //      log.debug("consumed id added: "+it.id)
                     //   }
                     //}
                  //} else {
                     if (notify.group.category != SysProduceJobService.All) {
                        saveNotify(notify, currentTime, isRemind)
                     } else {
                        allNotifiesList.add(notify.id)
                     }
                  }
               }
               if (notify.group.category == SysProduceJobService.All && !allNotifiesTime.get(notify.id)) {
                  log.debug("adding notify all to allNotifiesTime map: "+notify.id)
                  allNotifiesTime[notify.id] = currentTime
               }
               
               notify.discard()
            }
            
            targetGroup.discard()
            log.debug("unlocked group: "+targetGroup.id)
         }
      //}
      
      if (allNotifiesList.size() > 0) {
         def saveAllNotifies = SysNotify.withCriteria { 'in'("id", allNotifiesList) }
         saveAllNotifies.each {notify ->
            saveNotify(notify, allNotifiesTime[notify.id], isRemind)
            allNotifiesList.remove(notify.id)
            allNotifiesTime.remove(notify.id)
            notify.discard()
         }
      }
      allNotifiesTime.clear()
      log.debug("Produce ended")
   }
   
   private void saveNotify(SysNotify notify, Long currentTime, Boolean isRemind) {
      def loop = true
      while (loop) {
         loop = false
         try {
            notify.refresh()
            if (isRemind) {
               log.debug("Update lastRemind for notify["+notify.id+"]")
               notify.lastRemind = currentTime
            } else {
               log.debug("Update lastReport for notify["+notify.id+"]")
               notify.lastReport = currentTime
            }
            notify.save(flush: true)
         } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            log.debug("SysNotify object cannot be synchronized so clear session and try again")
            SysNotify.withSession { session ->
               session.clear()
            }
            loop = true
         } catch (org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException e) {
            log.debug("SysNotify object has been deleted since last fetched so ignore update")
            loop = false
         }
         log.debug("Looping")
      }
   }
   
   private boolean createMessages(MessageType messageType, SysNotify notify, Long currentTime, SysEventGroup targetGroup) {
      boolean messagesCreated = false
      boolean moreEventsToNotify = true
      int page = 0
      //def consumedEventIds = []
      while(moreEventsToNotify) {
         //consumedEventIds.each {
         //   log.debug("consumed event: "+it)
         //}
         
         def eventsForNotify = SysEvent.withCriteria {
            //if(consumedEventIds.size() > 0) {
            //   not {
            //      'in'("id", consumedEventIds)
            //   }
            //}
            eq("group.id", targetGroup.id)
            switch (messageType) {
               case MessageType.NEW:
                  gt("firstReceived", notify.lastReport)
                  le("firstReceived", currentTime)
                  break
               case MessageType.REMINDER:
                  isNull("resolvedAt")
                  le("firstReceived", currentTime - (1L * notify.remindRate * (60 * 60 * 1000)))
                  break
               case MessageType.RESOLVED:
                  isNotNull("resolvedAt")
                  gt("resolvedAt", notify.lastReport)
                  le("resolvedAt", currentTime)
                  break
               default:
                  break
            }
            maxResults((SysProduceJobService.MaximumEventsPerMessage + 1))
            firstResult(page * SysProduceJobService.MaximumEventsPerMessage)
            order("firstReceived", "asc")
         }
         page++
         moreEventsToNotify = (eventsForNotify.size() > SysProduceJobService.MaximumEventsPerMessage)
         log.debug("Class: "+eventsForNotify.getClass().toString())
         
         log.debug("notify: "+notify.id+" has events: "+eventsForNotify.size())
         eventsForNotify.each {
            log.debug("\tevent: "+it.id+", firstReceived: "+it.firstReceived)
         }
         if(eventsForNotify) {
            messagesCreated = true
            if(moreEventsToNotify) {
               while(eventsForNotify.size() > SysProduceJobService.MaximumEventsPerMessage) {
                  eventsForNotify.remove((eventsForNotify.size() - 1))
               }
            }
            
            def messages = sysProduceService.constructMessage(notify, (List<SysEvent>)eventsForNotify.toArray())
            log.debug("messages for notify["+notify.id+"]: "+messages.size())
            
            messages.each {message ->
               log.debug("message: "+message)
               
               Map parameters = [:]
               parameters.type = targetGroup.type
               parameters.category = targetGroup.category
               parameters.method = notify.method
               parameters.contact = notify.contact
               parameters.created = new Date().getTime()
               parameters.message = message
               parameters.notify = notify
               parameters.messageType = messageType.toString()
               
               try {
                  sysOutgoingService.create(parameters)
                  log.debug("Created SysOutgoing")
               } catch(Exception exception) {
                  log.debug("Oops", exception)
               }
            }
         }
      }
      return messagesCreated
   }
}
