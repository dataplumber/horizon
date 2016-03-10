class UrlMappings {
   /*
    static mappings = {
       "/group/$action?/$id?"(controller:"sysEventGroup") {
          constraints {
           // apply constraints here
         }
      }
     */

   static mappings = {
      "/events/$action" (controller: "sysEvent") {
         //action = [GET: "create", POST: "create"]
      }
      "/groups/$action" (controller: "sysEventGroup") {
         //action = [GET: "create", POST: "create"]
      }
      "/notifies/$action" (controller: "sysNotify") {
         //action = [GET: "create", POST: "create"]
      }
      "/constants/eventTypes/list" (controller: "sysConstant") {
         action = [GET: "listEventTypes", POST: "listEventTypes"]
      }
      "/constants/messageFormats/list" (controller: "sysConstant") {
         action = [GET: "listMessageFormats", POST: "listMessageFormats"]
      }
      "/constants/notifyContents/list" (controller: "sysConstant") {
         action = [GET: "listNotifyContents", POST: "listNotifyContents"]
      }
      "/constants/notifyMethods/list" (controller: "sysConstant") {
         action = [GET: "listNotifyMethods", POST: "listNotifyMethods"]
      }
      "/constants/responseFormats/list" (controller: "sysConstant") {
         action = [GET: "listResponseFormats", POST: "listResponseFormats"]
      }
      "/constants/categories/list" (controller: "sysConstant") {
         action = [GET: "listCategories", POST: "listCategories"]
      }
      "/auth" (controller: "authentication") {
         action = [GET: "authenticate", POST: "authenticate"]
      }
      "/users/$action" (controller: "sysUser") {
      }
      /*
      "/$controller/$action?/$id?" {
         constraints {
            // apply constraints here
         }
      }
      "/group/$action?/$type?/$category?"(controller: "sysEventGroup") {
         //action = [GET: 'debugAccept', PUT: 'update', DELETE: 'delete', POST: 'save']
      }
      */
      /*
      "/group/$type?/$category?/$action?/$id?"(controller:"sysEventGroup") {
      }
      */
      /*
      "/group/$type?/$category?"(controller:"sysEventGroup") {
         action = [GET:'show', PUT:'add', POST:'update', DELETE:'delete']
      }
      "/event/$type?/$category?/$description?/$id?" (controller:"sysEvent") {
         action = [GET:'show', PUT:'add', POST:'update', DELETE:'delete']
      }
      "/notify/$type?/$category?/$method?/$id?" (controller: "sysNotify") {
         action = [GET:'show', PUT:'add', POST:'update', DELETE:'delete']
      }
      */

      /*
        "/$controller/$action?/$id?"{
           constraints {
            // apply constraints here
          }
       }
       */


      "500"(view: '/error')
   }
}
