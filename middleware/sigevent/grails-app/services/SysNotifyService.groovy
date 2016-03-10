import org.apache.commons.lang.math.NumberUtils

import gov.nasa.horizon.sigevent.SysEventGroup
import gov.nasa.horizon.sigevent.SysNotify

/**
 * SysNotifyService
 */
class SysNotifyService {
   public static final String CountName = "count"
   public static final String ItemsName = "items"
   private static final int EntriesPerPage = 10
   private static final String DefaultSort = "contact"
   private static final String DefaultOrder = "asc"
   private static final String DefaultSearchOperator = "and"
   private static final Map AllowedParameters = [
      "list": [
         (SysNotifyService.DefaultSort): [:],
         "content": [:],
         "lastNotified": [:],
         "lastReport": [:],
         "lastRemind": [:],
         "messageFormat": [:],
         "method": [:],
         "note": [:],
         "rate": [:],
         "remindRate": [:],
         "category": ["association": "group"],
         "type": ["association": "group"]
      ],
      "search": [
         "contact": [:],
         "content": [:],
         "lastNotified": [:],
         "lastReport": [:],
         "lastRemind": [:],
         "messageFormat": [:],
         "method": [:],
         "note": [:],
         "rate": [:],
         "remindRate": [:],
         "category": ["association": "group"],
         "type": ["association": "group"]
      ],
      "update": [
         "method": "",
         "contact": "",
         "rate": "Long.parseLong(x)",
         "remindRate": "Integer.parseInt(x)",
         "messageFormat": "",
         "content": "",
         "note": ""
      ]
   ]
   private static final List AllowedOrders = [
      "asc",
      "desc"
   ]
   private static final List AllowedSearchOperators = [
      SysNotifyService.DefaultSearchOperator,
      "or"
   ]
   boolean transactional = false
   
   public SysNotify create(Map parameters) throws RuntimeException {
      SysEventGroup group = SysEventGroup.findWhere(category: parameters.category, type: parameters.type)
      if(!group) {
         throw new RuntimeException(
            "SysEventGroup not found: category="+parameters.category+", type="+parameters.type
         )
      }
      
      group.discard()
      group = SysEventGroup.lock(group.id)
      if(!group) {
         throw new RuntimeException(
            "SysEventGroup does not exist: category="+parameters.category+", type="+parameters.type
         )
      }
      
      SysNotify notify = new SysNotify(parameters)
      Date date = new Date()
      notify.lastReport = date.getTime()
      notify.lastNotified = date.getTime()
      notify.lastRemind = date.getTime()
      if(NotifyMethod.Email.value == notify.method) {
         notify.contact = notify.contact.toString().toLowerCase()
      }
      
      group.addToNotifies(notify)
      if(!group.save(flush: true)) {
         throw new RuntimeException("Failed to create SysNotify: "+group.errors.allErrors.join())
      }
      group.discard()
      notify.discard()
      
      return notify
   }
   
   public Map list(Map parameters) throws RuntimeException {
      int page = 0
      if(parameters.page) {
         page = NumberUtils.toInt(parameters.page, -1)
         if(page < 0) {
            throw new RuntimeException("Invalid value for page.")
         }
      }
      
      int entriesPerPage = SysNotifyService.EntriesPerPage
      if((parameters.paging) && ("false".equalsIgnoreCase(parameters.paging))) {
         entriesPerPage = Integer.MAX_VALUE
         page = 0
      }
      
      // sort part
      def sortValue = SysNotifyService.DefaultSort
      def orderValue = SysNotifyService.DefaultOrder
      if(parameters.sort) {
         if(!SysNotifyService.AllowedParameters.list.find{key, value -> key == parameters.sort}) {
            throw new RuntimeException(
               "Not supported sort. Supported ones are "+SysNotifyService.AllowedParameters.list.keySet().join(", ")
            )
         }
         sortValue = parameters.sort
      }
      if(parameters.order) {
         if(!SysNotifyService.AllowedOrders.find{it == parameters.order}) {
            throw new RuntimeException(
               "Not supported order. Supported ones are "+SysNotifyService.AllowedOrders.join(", ")
            )
         }
         orderValue = parameters.order
      }
      
      // search operator
      def searchOperatorValue = SysNotifyService.DefaultSearchOperator
      if(parameters.searchOperator) {
         if(!SysNotifyService.AllowedSearchOperators.find{it == parameters.searchOperator}) {
            throw new RuntimeException(
               "Not supported search operator. Supported ones are "+SysNotifyService.AllowedSearchOperators.join(", ")
            )
         }
         searchOperatorValue = parameters.searchOperator
      }
      log.debug("Search operator: "+searchOperatorValue)
      
      // where part
      def query = {
         def criterias = [:]
         SysNotifyService.AllowedParameters.search.each {key, value ->
            if(parameters[key]) {
               def groupName = (value.association) ? value.association : "default"
               def group = criterias[groupName]
               if(!group) {
                  group = ["like": [], "order": null]
                  criterias[groupName] = group
               }
               
               // it can be multiple values
               def parameterValues = (parameters[key] instanceof String) ? [parameters[key]] : parameters[key]
               group.like.add(["key": key, "values": parameterValues])
            }
         }
         
         def groupName = (SysNotifyService.AllowedParameters.list."$sortValue".association) ?
            (SysNotifyService.AllowedParameters.list."$sortValue".association) : "default"
         def group = criterias[groupName]
         if(!group) {
            group = ["like": [], "order": null]
            criterias[groupName] = group
         }
         group.order = ["sort": sortValue, "order": orderValue]
         
         // actual criteria building
         "$searchOperatorValue" {
            criterias.each {key, value ->
               if(key == "default") {
                  value.like.each {entry ->
                     or {
                        entry.values.each {searchValue ->
                           ilike(entry.key, '%'+searchValue+'%')
                        }
                     }
                  }
               } else {
                  "$key" {
                     "$searchOperatorValue" {
                        value.like.each {entry ->
                           or {
                              entry.values.each {searchValue ->
                                 ilike(entry.key, '%'+searchValue+'%')	
                              }
                           }
                        }
                     }
                     if(value.order) {
                        order(value.order.sort, value.order.order)
                     }
                  }
               }
            }
         }
         if(criterias.default?.order) {
            order(criterias.default.order.sort, criterias.default.order.order)
         }
      }
      def countQuery = {
         def criterias = [:]
         SysNotifyService.AllowedParameters.search.each {key, value ->
            if(parameters[key]) {
               def groupName = (value.association) ? value.association : "default"
               def group = criterias[groupName]
               if(!group) {
                  group = ["like": [], "order": null]
                  criterias[groupName] = group
               }
               
               // it can be multiple values
               def parameterValues = (parameters[key] instanceof String) ? [parameters[key]] : parameters[key]
               group.like.add(["key": key, "values": parameterValues])
            }
         }
         
         def groupName = (SysNotifyService.AllowedParameters.list."$sortValue".association) ?
            (SysNotifyService.AllowedParameters.list."$sortValue".association) : "default"
         def group = criterias[groupName]
         if(!group) {
            group = ["like": [], "order": null]
            criterias[groupName] = group
         }
         // actual criteria building
         "$searchOperatorValue" {
            criterias.each {key, value ->
               if(key == "default") {
                  value.like.each {entry ->
                     or {
                        entry.values.each {searchValue ->
                           ilike(entry.key, '%'+searchValue+'%')
                        }
                     }
                  }
               } else {
                  "$key" {
                     "$searchOperatorValue" {
                        value.like.each {entry ->
                           or {
                              entry.values.each {searchValue ->
                                 ilike(entry.key, '%'+searchValue+'%')
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      // search
      def notifies = SysNotify.createCriteria().list(
         "max": entriesPerPage,
         "offset": (page * SysNotifyService.EntriesPerPage),
         query
      )
      def count = SysNotify.createCriteria().count(countQuery)
      /*
      def sort = ALLOWED_PARAMETERS.list[0]
      def order = ALLOWED_ORDERS[0]
      if((parameters.sort) && (parameters.order)) {
         if(!ALLOWED_PARAMETERS.list.find{it == parameters.sort}) {
            throw new RuntimeException(
               "Not supported sort. Supported ones are "+ALLOWED_PARAMETERS.list.join(", ")
            )
         }
         if(!ALLOWED_ORDERS.find{it == parameters.order}) {
            throw new RuntimeException(
               "Not supported order. Supported ones are "+ALLOWED_ORDERS.join(", ")
            )
         }
         
         sort = parameters.sort
         order = parameters.order
      }
      
      def notifies = SysNotify.list("max": ENTRIES_PER_PAGE, "offset": (page * ENTRIES_PER_PAGE), "sort": sort, "order": order)
      def count = SysNotify.count()
      */
      
      return [(SysNotifyService.CountName): count as Integer, (SysNotifyService.ItemsName): notifies]
   }
   
   public SysNotify show(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def notify = SysNotify.get(id)
      if(!notify) {
         throw new RuntimeException("SysNotify does not exist with ID: "+id)
      }
      
      return notify
   }
   
   public SysNotify update(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def notify = SysNotify.lock(id)
      if(!notify) {
         throw new RuntimeException("SysNotifty does not exist with ID: "+id)
      }
      
      SysNotifyService.AllowedParameters.update.each {key, value ->
         if(parameters.containsKey(key)) {
            if(parameters."$key") {
               notify."$key" = (value) ? Eval.x(parameters."$key", value) : parameters."$key"
               //log.debug("updating "+event."$key")
            } else {
               //allow empty parameter to mean null value
               notify."$key" = null
            }
         }
      }
      //notify.properties = parameters
      if(!notify.save(flush: true)) {
         throw new RuntimeException("Failed to update SysNotify["+notify.id+"]: "+notify.errors.allErrors.join())
      }
      notify.discard()
      
      return notify
   }
   
   public SysNotify delete(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def notify = SysNotify.lock(id)
      if(!notify) {
         throw new RuntimeException("SysNotify does not exist with ID: "+id)
      }
      notify.delete(flush: true)
      
      return notify
   }
   
   /*
   public Map search(Map parameters) throws RuntimeException {
      int page = 0
      if(parameters.page) {
         page = NumberUtils.toInt(parameters.page, -1)
         if(page < 0) {
            throw new RuntimeException("Invalid value for page.")
         }
      }
      
      def query = {
         and {
            ALLOWED_PARAMETERS.search.each {
               if(parameters[it]) {
                  like(it, '%'+parameters[it]+'%')
               }
            }
         }
      }
      def events = SysNotify.createCriteria().list(max: ENTRIES_PER_PAGE, offset: (page * ENTRIES_PER_PAGE), query)
      def count = SysNotify.createCriteria().count(query)
      
      return [(SysNotifyService.COUNT_NAME): count, (SysNotifyService.ITEMS_NAME): events]
   }
   */
   
   public int getPages(int count) throws RuntimeException {
      int pages = (int)Math.floor(count / SysNotifyService.EntriesPerPage)
      if((count % SysNotifyService.EntriesPerPage) > 0) {
         pages += 1
      }
      
      return pages
   }
}
