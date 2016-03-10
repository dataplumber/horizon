/*
 * Copyright (c) 2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */

class UrlMappings {

   static mappings = {
      "/$controller/$action?/$id?" {
         constraints {
            // apply constraints here
         }
      }

      "/"(view: "/index")
      "500"(view: '/error')
      "/ingest/$action?/$id?"(controller: "ingest", parseRequest: true)
      "/operator/auth"(controller: "operator", action: "authenticate", parseRequest: true)
      "/product/search"(controller: "operator", action: "searchProducts", parseRequest: true)
      "/product/update"(controller: "operator", action: "updateProducts", parseRequest: true)
      "/product/delete"(controller: "operator", action: "deleteProducts", parseRequest: true)
      "/product/count"(controller: "operator", action: "countProducts", parseRequest: true)
      "/product/countByPriority"(controller: "operator", action: "countProductsByPriority", parseRequest: true)
      "/storage/list"(controller: "operator", action: "listStorages", parseRequest: true)
      "/storage/update"(controller: "operator", action: "updateStorages", parseRequest: true)
      "/storage/countJobs"(controller: "operator", action: "countJobsByStorage", parseRequest: true)
      "/location/update"(controller: "operator", action: "updateLocations", parseRequest: true)
      "/productType/search"(controller: "operator", action: "searchProductTypes", parseRequest: true)
      "/productType/update"(controller: "operator", action: "updateProductTypes", parseRequest: true)
      "/productType/create"(controller: "operator", action: "createProductType", parseRequest: true)
      "/productType/showByName"(controller: "operator", action: "showProductTypeByName", parseRequest: true)
      "/domain/export"(controller: "operator", action: "exportDomain", parseRequest: true)
      "/job/pause"(controller: "operator", action: "pauseJob", parseRequest: true)
      "/job/resume"(controller: "operator", action: "resumeJob", parseRequest: true)
      "/job/state"(controller: "operator", action: "getJobState", parseRequest: true)
      "/heartbeat"(controller: "operator", action: "heartbeat", parseRequest: true)
   }
}
