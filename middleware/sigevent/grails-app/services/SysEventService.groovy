import org.apache.commons.lang.math.NumberUtils

import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysEventGroup

class SysEventService {
   public static final String CountName = "count"
   public static final String ItemsName = "items"
   private static final int EntriesPerPage = 10
   private static final String DefaultSort = "lastReceived"
   private static final String DefaultOrder = "asc"
   private static final String DefaultSearchOperator = "and"
   private static final Map AllowedParameters = [
      "list": [
         (SysEventService.DefaultSort): [:],
         "computer": [:],
         "description": [:],
         "firstReceived": [:],
         "occurrence": [:],
         "pid": [:],
         "provider": [:],
         "resolution": [:],
         "resolvedAt": [:],
         "source": [:],
         "category": ["association": "group"],
         "type": ["association": "group"]
      ],
      "search": [
         "computer": [:],
         "description": [:],
         "pid": [:],
         "provider": [:],
         "source": [:],
         "category": ["association": "group"],
         "type": ["association": "group"]
      ],
      "update": [
         "source": "",
         "provider": "",
         "computer": "",
         "pid": "Integer.parseInt(x)",
         "description": "",
         "data": "",
         "resolution": ""
      ]
   ]
   private static final List AllowedOrders = [
      SysEventService.DefaultOrder,
      "desc"
   ]
   private static final List AllowedSearchOperators = [
      SysEventService.DefaultSearchOperator,
      "or"
   ]
   static boolean transactional = false
   
   public SysEvent create(Map parameters) throws RuntimeException {
      def group = SysEventGroup.findWhere("category": parameters.category, "type": parameters.type)
      if(!group) {
         throw new RuntimeException(
            "SysEventGroup not found: category="+parameters.category+", type="+parameters.type
         )
      }
      
      def events = SysEvent.withCriteria {
         eq("group.id", group.id)
         eq("source", parameters.source)
         eq("provider", parameters.provider)
         eq("computer", parameters.computer)
         eq("description", parameters.description)
         isNull("resolvedAt")
         maxResults(1)
      }
      def event = (events) ? events[0] : null
      /*
      def event = group.events.find{
         (it.source == parameters.source) &&
         (it.provider == parameters.provider) &&
         (it.computer == parameters.computer) &&
         (it.description == parameters.description)
      }
      */
      if(event) {
         group.discard()
         event.discard()
         event = SysEvent.lock(event.id)
         if(!event) {
            throw new RuntimeException("SysEvent["+event.id+"] is gone. Ignoring to update.")
         }
         
         event.lastReceived = new Date().getTime()
         event.occurrence = (event.occurrence + 1)
         if(parameters.data) {
            event.data = parameters.data
         }
         if(!event.save(flush: true)) {
            throw new RuntimeException("Failed to update the same event: "+event.errors.allErrors.join())
         }
         
         log.debug("same event updated: "+event.id)
      } else {
         /*
         group.discard()
         group = SysEventGroup.lock(group.id)
         if(!group) {
            throw new RuntimeException(
               "SysEventGroup does not exist: category="+parameters.category+", type="+parameters.type
            )
         }
         */
         
         event = new SysEvent(parameters)
         Date date = new Date()
         event.firstReceived = date.getTime()
         event.lastReceived = date.getTime()
         event.occurrence = 1
         event.group = group
         if(!event.save(flush: true)) {
            throw new RuntimeException("Failed to create SysEvent: "+event.errors.allErrors.join())
         }
         /*
         group.addToEvents(event)
         if(!group.save(flush: true)) {
            throw new RuntimeException("Failed to create SysEvent: "+group.errors.allErrors.join())
         }
         */
      }
      
      return event
   }
   
   public Map list(Map parameters) throws RuntimeException {
      int page = 0
      if(parameters.page) {
         page = NumberUtils.toInt(parameters.page, -1)
         if(page < 0) {
            throw new RuntimeException("Invalid value for page.")
         }
      }
      
      int entriesPerPage = SysEventService.EntriesPerPage
      if((parameters.paging) && ("false".equalsIgnoreCase(parameters.paging))) {
         entriesPerPage = Integer.MAX_VALUE
         page = 0
      }
      // sort part
      def sortValue = SysEventService.DefaultSort
      def orderValue = SysEventService.DefaultOrder
      if(parameters.sort) {
         if(!SysEventService.AllowedParameters.list.find{key, value -> key == parameters.sort}) {
            throw new RuntimeException(
               "Not supported sort. Supported ones are "+SysEventService.AllowedParameters.list.keySet().join(", ")
            )
         }
         sortValue = parameters.sort
      }
      if(parameters.order) {
         if(!SysEventService.AllowedOrders.find{it == parameters.order}) {
            throw new RuntimeException(
               "Not supported order. Supported ones are "+SysEventService.AllowedOrders.join(", ")
            )
         }
         orderValue = parameters.order
      }
      // search operator
      def searchOperatorValue = SysEventService.DefaultSearchOperator
      if(parameters.searchOperator) {
         if(!SysEventService.AllowedSearchOperators.find{it == parameters.searchOperator}) {
            throw new RuntimeException(
               "Not supported search operator. Supported ones are "+SysEventService.AllowedSearchOperators.join(", ")
            )
         }
         searchOperatorValue = parameters.searchOperator
      }
      //log.debug("Search operator: "+searchOperatorValue)
      
      // Must duplicate criteria query to workaround a bug with PostgreSQL but remove any ordering logic
      def countQuery = {
         def criterias = [:]
         SysEventService.AllowedParameters.search.each {key, value ->
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
         
         def groupName = (SysEventService.AllowedParameters.list."$sortValue".association) ?
            (SysEventService.AllowedParameters.list."$sortValue".association) : "default"
         def group = criterias[groupName]
         if(!group) {
            group = ["like": [], "order": null]
            criterias[groupName] = group
         }
         
         //log.debug(criterias.inspect())
         
         // actual criteria building
         "$searchOperatorValue" {
            criterias.each {key, value ->
               if(key == "default") {
                  value.like.each {entry ->
                     or {
                        entry.values.each {searchValue ->
                           if ("true".equalsIgnoreCase(parameters.exact)) {
                              eq(entry.key, searchValue)
                           } else {
                              ilike(entry.key, '%'+searchValue+'%')
                           }
                        }
                     }
                  }
               } else {
                  "$key" {
                     "$searchOperatorValue" {
                        value.like.each {entry ->
                           or {
                              entry.values.each {searchValue ->
                                 if ("true".equalsIgnoreCase(parameters.exact)) {
                                    eq(entry.key, searchValue)
                                 } else {
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
         if(parameters.resolved) {
            if ("true".equalsIgnoreCase(parameters.resolved)) {
               isNotNull("resolvedAt")
            } else if ("false".equalsIgnoreCase(parameters.resolved)) {
               isNull("resolvedAt")
            }
         }
      }
      def query = {
         def criterias = [:]
         SysEventService.AllowedParameters.search.each {key, value ->
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
         
         def groupName = (SysEventService.AllowedParameters.list."$sortValue".association) ?
            (SysEventService.AllowedParameters.list."$sortValue".association) : "default"
         def group = criterias[groupName]
         if(!group) {
            group = ["like": [], "order": null]
            criterias[groupName] = group
         }
         group.order = ["sort": sortValue, "order": orderValue]
         
         //log.debug(criterias.inspect())
         
         // actual criteria building
         "$searchOperatorValue" {
            criterias.each {key, value ->
               if(key == "default") {
                  value.like.each {entry ->
                     or {
                        entry.values.each {searchValue ->
                           if ("true".equalsIgnoreCase(parameters.exact)) {
                              eq(entry.key, searchValue)
                           } else {
                              ilike(entry.key, '%'+searchValue+'%')
                           }
                        }
                     }
                  }
               } else {
                  "$key" {
                     "$searchOperatorValue" {
                        value.like.each {entry ->
                           or {
                              entry.values.each {searchValue ->
                                 if ("true".equalsIgnoreCase(parameters.exact)) {
                                    eq(entry.key, searchValue)
                                 } else {
                                    ilike(entry.key, '%'+searchValue+'%')
                                 }
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
         if(parameters.resolved) {
            if ("true".equalsIgnoreCase(parameters.resolved)) {
               isNotNull("resolvedAt")
            } else if ("false".equalsIgnoreCase(parameters.resolved)) {
               isNull("resolvedAt")
            }
         }
         if(criterias.default?.order) {
            order(criterias.default.order.sort as String, criterias.default.order.order as String)
         }
      }
      
      // search
      def count = SysEvent.createCriteria().count(countQuery)
      def events = SysEvent.createCriteria().list(
         "max": entriesPerPage,
         "offset": (page * SysEventService.EntriesPerPage),
         query
      )
      
      return [(SysEventService.CountName): count as Integer, (SysEventService.ItemsName): events]
   }
   
   public SysEvent show(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def event = SysEvent.get(id)
      if(!event) {
         throw new RuntimeException("SysEvent does not exist with ID: "+id)
      }
      
      return event
   }
   
   public Map update(Map parameters) throws RuntimeException {
      if(!parameters.id) {
         throw new RuntimeException("Invalid value for id.")
      }
      def ids = []
      parameters.id.split(",").each {i ->
         long id = NumberUtils.toLong(i, -1)
         if(id < 0) {
            throw new RuntimeException("Invalid value for id: " + i)
         }
         ids.add(id)
      }
      
      def events = []
      ids.each {id ->
         def event = SysEvent.lock(id)
         if(event) {
            SysEventService.AllowedParameters.update.each {key, value ->
               if(parameters."$key") {
                  event."$key" = (value) ? Eval.x(parameters."$key", value) : parameters."$key"
                  //log.debug("updating "+event."$key")
               }
            }
            //event.properties = parameters
            if(parameters.resolution) {
               event.resolvedAt = new Date().getTime()
            }
            
            if(!event.save(flush: true)) {
               log.error("Failed to update SysEvent["+event.id+"]: "+event.errors.allErrors.join())
            } else {
               events.add(event)
            }
            event.discard()
         }
      }
      return [(SysEventService.CountName): events.size(), (SysEventService.ItemsName): events]
   }
   
   public SysEvent delete(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def event = SysEvent.lock(id)
      if(!event) {
         throw new RuntimeException("SysEvent does not exist with ID: "+id)
      }
      event.delete(flush: true)
      
      return event
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
      def events = SysEvent.createCriteria().list(max: ENTRIES_PER_PAGE, offset: (page * ENTRIES_PER_PAGE), query)
      def count = SysEvent.createCriteria().count(query)
      
      return [(SysEventService.COUNT_NAME): count, (SysEventService.ITEMS_NAME): events]
   }
   */

   public int getPages(int count) throws RuntimeException {
      int pages = (int)Math.floor(count / SysEventService.EntriesPerPage)
      if((count % SysEventService.EntriesPerPage) > 0) {
         pages += 1
      }
      
      return pages
   }
}
