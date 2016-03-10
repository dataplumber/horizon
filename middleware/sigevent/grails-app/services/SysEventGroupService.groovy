import org.apache.commons.lang.math.NumberUtils

import gov.nasa.horizon.sigevent.SysEventGroup

/**
 * SysEventGroupService
 */
class SysEventGroupService {
   public static final String CountName = "count"
   public static final String ItemsName = "items"
   private static final int EntriesPerPage = 10
   private static final String DefaultSort = "category"
   private static final String DefaultOrder = "asc"
   private static final String DefaultSearchOperator = "and"
   private static final Map AllowedParameters = [
      "list": [
         (SysEventGroupService.DefaultSort): [:],
         "type": [:],
         "purgeRate": [:]
      ],
      "search": [
         "category": [:],
         "type": [:],
         "purgeRate": [:]
      ],
      "update": [
         "purgeRate": "Long.parseLong(x)"
      ]
   ]
   private static final List AllowedOrders = [
      "asc",
      "desc"
   ]
   private static final List AllowedSearchOperators = [
      SysEventGroupService.DefaultSearchOperator,
      "or"
   ]
   boolean transactional = false

   public SysEventGroup create(Map parameters) throws RuntimeException {
      SysEventGroup group = new SysEventGroup(parameters)
      if(!group.save(flush:true)) {
         throw new RuntimeException("Failed to create SysEventGroup: "+group.errors.allErrors.join())
      }
      group.discard()
      
      return group
   }
   
   public Map list(Map parameters) throws RuntimeException {
      int page = 0
      if(parameters.page) {
         page = NumberUtils.toInt(parameters.page, -1)
         if(page < 0) {
            throw new RuntimeException("Invalid value for page.")
         }
      }
      
      int entriesPerPage = SysEventGroupService.EntriesPerPage
      if((parameters.paging) && ("false".equalsIgnoreCase(parameters.paging))) {
         entriesPerPage = Integer.MAX_VALUE
         page = 0
      }
      
      // sort part
      def sortValue = SysEventGroupService.DefaultSort
      def orderValue = SysEventGroupService.DefaultOrder
      if(parameters.sort) {
         if(!SysEventGroupService.AllowedParameters.list.find{key, value -> key == parameters.sort}) {
            throw new RuntimeException(
               "Not supported sort. Supported ones are "+SysEventGroupService.AllowedParameters.list.keySet().join(", ")
            )
         }
         sortValue = parameters.sort
      }
      if(parameters.order) {
         if(!SysEventGroupService.AllowedOrders.find{it == parameters.order}) {
            throw new RuntimeException(
               "Not supported order. Supported ones are "+SysEventGroupService.AllowedOrders.join(", ")
            )
         }
         orderValue = parameters.order
      }
      
      // search operator
      def searchOperatorValue = SysEventGroupService.DefaultSearchOperator
      if(parameters.searchOperator) {
         if(!SysEventGroupService.AllowedSearchOperators.find{it == parameters.searchOperator}) {
            throw new RuntimeException(
               "Not supported search operator. Supported ones are "+SysEventGroupService.AllowedSearchOperators.join(", ")
            )
         }
         searchOperatorValue = parameters.searchOperator
      }
      log.debug("Search operator: "+searchOperatorValue)
      
      // where part
      def query = {
         def criterias = [:]
         SysEventGroupService.AllowedParameters.search.each {key, value ->
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
         
         def groupName = (SysEventGroupService.AllowedParameters.list."$sortValue".association) ?
            (SysEventGroupService.AllowedParameters.list."$sortValue".association) : "default"
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
         SysEventGroupService.AllowedParameters.search.each {key, value ->
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
         
         def groupName = (SysEventGroupService.AllowedParameters.list."$sortValue".association) ?
            (SysEventGroupService.AllowedParameters.list."$sortValue".association) : "default"
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
      def groups = SysEventGroup.createCriteria().list(
         "max": entriesPerPage,
         "offset": (page * SysEventGroupService.EntriesPerPage),
         query
      )
      def count = SysEventGroup.createCriteria().count(countQuery)
      
      
      return [(SysEventGroupService.CountName): count, (SysEventGroupService.ItemsName): groups]
   }
   
   public SysEventGroup show(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def group = SysEventGroup.get(id)
      if(!group) {
         throw new RuntimeException("SysEventGroup does not exist with ID: "+id)
      }
      
      return group
   }
   
   public List showByCategory(Map parameters) throws RuntimeException {
      if(!parameters.category) {
         throw new RuntimeException("Missing category parameter.")
      }
      
      def group = SysEventGroup.findAllByCategory(parameters.category)
      if(!group) {
         throw new RuntimeException("SysEventGroup does not exist with Category: "+parameters.category)
      }
      
      return group
   }
   
   public SysEventGroup update(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def group = SysEventGroup.lock(id)
      if(!group) {
         throw new RuntimeException("SysEventGroup does not exist with ID: "+id)
      }
      
      SysEventGroupService.AllowedParameters.update.each {key, value ->
         if(parameters."$key") {
            group."$key" = (value) ? Eval.x(parameters."$key", value) : parameters."$key"
            //log.debug("updating "+event."$key")
         }
      }
      //group.properties = parameters
      if(!group.save(flush: true)) {
         throw new RuntimeException("Failed to update SysEventGroup["+group.id+"]: "+group.errors.allErrors.join())
      }
      group.discard()
      
      return group
   }
   
   public SysEventGroup delete(Map parameters) throws RuntimeException {
      long id = NumberUtils.toLong(parameters.id, -1)
      if(id < 0) {
         throw new RuntimeException("Invalid value for id.")
      }
      
      def group = SysEventGroup.lock(id)
      if(!group) {
         throw new RuntimeException("SysEventGroup does not exist with ID: "+id)
      }
      group.delete(flush: true)
      
      return group
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
      def events = SysEventGroup.createCriteria().list(max: ENTRIES_PER_PAGE, offset: (page * ENTRIES_PER_PAGE), query)
      def count = SysEventGroup.createCriteria().count(query)
      
      return [(SysEventGroupService.COUNT_NAME): count, (SysEventGroupService.ITEMS_NAME): events]
   }
   */
   
   public int getPages(int count) throws RuntimeException {
      int pages = (int)Math.floor(count / SysEventGroupService.EntriesPerPage)
      if((count % SysEventGroupService.EntriesPerPage) > 0) {
         pages += 1
      }
      
      return pages
   }
}
