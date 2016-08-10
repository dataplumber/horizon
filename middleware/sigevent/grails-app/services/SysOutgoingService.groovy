import gov.nasa.horizon.sigevent.SysNotify
import gov.nasa.horizon.sigevent.SysOutgoing

/**
 * SysOutgoingService
 */
class SysOutgoingService {
    
   def grailsApplication
   private static final Map Methods = [
      (NotifyMethod.Email): "publishByEmail",
      (NotifyMethod.Jms): "publishByJms",
      (NotifyMethod.Multicast): "publishByMulticast",
      (NotifyMethod.Twitter): "publishByTwitter"
   ]
   boolean transactional = false
   def sysEmailService
   def sysJmsService
   //def sysTwitterService

   public SysOutgoing create(Map parameters) throws RuntimeException {
      SysOutgoing outgoing = new SysOutgoing(parameters)
      if(!outgoing.save(flush: true)) {
         throw new RuntimeException("Failed to create SysOutgoing: "+outgoing.errors.allErrors.join())
      }
      outgoing.discard()
      
      return outgoing
   }
   
   public void publish(long id) throws RuntimeException {
      SysOutgoing outgoing = SysOutgoing.lock(id)
      if(!outgoing) {
         throw new RuntimeException("SysOutgoing not found: id="+id)
      }
      
      def entry = Methods.find{it.getKey().value == outgoing.method}
      if(entry) {
         try {
            def method = entry.getValue()
            this."$method"(outgoing)
         } catch(Exception exception) {
            log.warn("Failed to publish SysOutgoing: id="+id+ " " +exception.getMessage())
            throw new RuntimeException("Failed to publish SysOutgoing: id="+id)
         }
         
         try {
            outgoing.delete(flush: true)
         } catch(Exception exception) {
            throw new RuntimeException("Failed to delete SysOutgoing: "+exception.toString()+": "+exception.getMessage())
         }
         
         try {
            if(outgoing.notify) {
               def notify = SysNotify.lock(outgoing.notify.id)
               notify.lastNotified = new Date().getTime()
               if(!notify.save(flush: true)) {
                  throw new RuntimeException("Failed to save SysNotify.")
               }
            }
         } catch(Exception exception) {
            log.debug("Failed to update SysNotify.lastNotified but will ignore it since this is not critical.", exception)
         }
      } else {
         throw new RuntimeException("Invalid method detected!: "+outgoing.method)
      }
   }
   
   private void publishByEmail(SysOutgoing outgoing) throws Exception {
      log.debug("published by email: "+outgoing.id)
      
      def subject = "SigEvent "
      if (MessageType.detect(outgoing.messageType) != MessageType.NEW) {
			subject += outgoing.messageType.toLowerCase().capitalize() + " "
      }
      subject += "["+outgoing.type+"/"+outgoing.category+"]"
      if (outgoing.notify.group.category == "ALL") {
         subject += " triggered by ALL subscription"
      }
      def email = [
         "from": grailsApplication.config.horizon_email_from,
         "to": [outgoing.contact],
         "subject": subject,
         "text": outgoing.message
      ]
      sysEmailService.notify([email])
   }
   
   private void publishByJms(SysOutgoing outgoing) throws Exception {
      log.debug("published by jms: "+outgoing.id)
      
      sysJmsService.publish(outgoing.contact, outgoing.message)
   }

   private void publishByMulticast(SysOutgoing outgoing) throws Exception {
      log.debug("published by multicast [not implemented]: "+outgoing.id)
   }
   
   private void publishByTwitter(SysOutgoing outgoing) throws Exception {
      log.debug("published by twitter: "+outgoing.id)
      
      sysTwitterService.publish(outgoing.message)
   }
}
