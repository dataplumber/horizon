package gov.nasa.horizon.manager.controller

import gov.nasa.horizon.common.api.zookeeper.api.constants.JobPriority
import grails.converters.JSON

class OperatorController {
   def productTypeService
   def operatorService
   
   public static final String RESPONSE_OK = "OK"
   public static final String RESPONSE_ERROR = "ERROR"

   def searchProducts = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         int page = this.convertToPositiveInteger(params.page, 1)
         int rows = this.convertToPositiveInteger(params.rows, 5)

         response = operatorService.searchProducts(params, page, rows)
      }

      render response as JSON
   }

   def updateProducts = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.id) {
            response = this.constructResponse("ERROR", "Missing id.")
         } else if(!params.current_state) {
            response = this.constructResponse("ERROR", "Missing current_state.")
         } else if(!params.current_lock) {
            response = this.constructResponse("ERROR", "Missing current_lock.")
         } else if(!params.current_retries) {
            response = this.constructResponse("ERROR", "Missing retries.")
         }

         if(!response) {
            try {
               int retries = Integer.parseInt(params.current_retries)
               def ids = []
               params.id.split(",").each {id ->
                  ids.add(Long.parseLong(id))
               }

               def idsWorked = operatorService.updateProucts(ids, params.current_state, params.current_lock, retries)
               def idsNotWorked = []
               ids.each {id ->
                  if(!idsWorked.contains(id)) {
                     idsNotWorked.add(id)
                  }
               }

               if(idsNotWorked.size() > 0) {
                  throw new Exception("Update failed for IDs: "+idsNotWorked.join(", "))
               } else {
                  response = this.constructResponse("OK", "Updated.")
               }
            } catch(Exception exception) {
               //log.debug(exception)
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }

      render response as JSON
   }

   def deleteProducts = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.id) {
            response = this.constructResponse("ERROR", "Missing id.")
         }

         if(!response) {
            try {
               def ids = []
               params.id.split(",").each {id ->
                  ids.add(Long.parseLong(id))
               }

               def result = operatorService.deleteProducts(ids)
               def idsNotWorked = []
               def filesToDelete = []
               ids.each {id ->
                  def entry = result.find{it.id == id}
                  if(!entry) {
                     idsNotWorked.add(id)
                  } else if(entry.filePath) {
                     filesToDelete.add(entry.filePath)
                  }
               }

               def fileMessage = (filesToDelete.size() > 0) ? filesToDelete.join(", ") : "No files to delete."
               if(idsNotWorked.size() > 0) {
                  throw new Exception(
                        "Delete failed for IDs: "+idsNotWorked.join(", ")+
                              ". For ones deleted, clean up files under these directories manually: "+fileMessage
                  )
               } else {
                  response = this.constructResponse("OK", "Deleted. Clean up files under these directories manually: "+filesToDelete)
               }
            } catch(Exception exception) {
               //log.debug(exception)
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }

      render response as JSON
   }

   def authenticate = {
      def response = productTypeService.authenticate(params.userName, params.password)
      render response as JSON
   }

   def listStorages = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         response = operatorService.listStorages()
      }

      render response as JSON
   }

   def listEngines = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         response = operatorService.listEngines()
      }

      render response as JSON
   }

   def updateLocations = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.id) {
            response = this.constructResponse("ERROR", "Missing id.")
         } else if(!params.space_reserved) {
            response = this.constructResponse("ERROR", "Missing space_reserved.")
         } else if(!params.space_threshold) {
            response = this.constructResponse("ERROR", "Missing space_threshold.")
         } else if(!params.space_used) {
            response = this.constructResponse("ERROR", "Missing space_used.")
         }

         if(!response) {
            try {
               def ids = []
               params.id.split(",").each {id ->
                  ids.add(Long.parseLong(id))
               }
               long spaceReserved = Long.parseLong(params.space_reserved)
               long spaceThreshold = Long.parseLong(params.space_threshold)
               long spaceUsed = Long.parseLong(params.space_used)

               Boolean active = null
               if (params.active) {
                  active = params.active.toBoolean()
               }

               def idsWorked = operatorService.updateLocation(ids, spaceReserved, spaceThreshold, spaceUsed, active)
               def idsNotWorked = []
               ids.each {id ->
                  if(!idsWorked.contains(id)) {
                     idsNotWorked.add(id)
                  }
               }

               if(idsNotWorked.size() > 0) {
                  throw new Exception("Update failed for IDs: "+idsNotWorked.join(", "))
               } else {
                  response = this.constructResponse("OK", "Updated.")
               }
            } catch(Exception exception) {
               //log.debug(exception)
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }

      render response as JSON
   }

   def updateStorages = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.id) {
            response = this.constructResponse("ERROR", "Missing id.")
         } else if(!params.containsKey("priority")) {
            response = this.constructResponse("ERROR", "Missing priority.")
         }

         if(!response) {
            try {
               def ids = []
               params.id.split(",").each {id ->
                  ids.add(Long.parseLong(id))
               }

               JobPriority priority = JobPriority.DEFAULT
               if (params.priority) {
                  try {
                     priority = JobPriority.valueOf(params.priority.toUpperCase())
                  } catch (Exception e) {
                     // Unable to parse priority param -- ignore
                  }
               } else {
                  priority = null
               }

               def idsWorked = operatorService.updateStorage(ids, priority)
               def idsNotWorked = []
               ids.each {id ->
                  if(!idsWorked.contains(id)) {
                     idsNotWorked.add(id)
                  }
               }

               if(idsNotWorked.size() > 0) {
                  throw new Exception("Update failed for IDs: "+idsNotWorked.join(", "))
               } else {
                  response = this.constructResponse("OK", "Updated.")
               }
            } catch(Exception exception) {
               //log.debug(exception)
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }

      render response as JSON
   }

   def countJobsByStorage = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         response = operatorService.countJobsByStorage()
      }

      render response as JSON
   }

   def countProducts = {
      log.debug("username: "+params.userName)

      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         response = operatorService.countProducts()
      }

      render response as JSON
   }

   def countProductsByPriority = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.archivedTime) {
            response = this.constructResponse("ERROR", "Missing archivedTime.")
         }
         if (!response) {
            try {
               long archivedTime = Long.parseLong(params.archivedTime)
               response = operatorService.countProductsByPriority(archivedTime)
            } catch (Exception exception) {
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }

      render response as JSON
   }

   def searchProductTypes = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         int page = this.convertToPositiveInteger(params.page, 1)
         int rows = this.convertToPositiveInteger(params.rows, 5)

         response = operatorService.searchProductTypes(params, page, rows)
      }

      render response as JSON
   }

   def updateProductTypes = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.id) {
            response = this.constructResponse("ERROR", "Missing id.")
         }

         if(!response) {
            try {
               def ids = []
               params.id.split(",").each {id ->
                  ids.add(Long.parseLong(id))
               }

               def idsWorked = operatorService.updateProductTypes(ids, params)
               def idsNotWorked = []
               ids.each {id ->
                  if(!idsWorked.contains(id)) {
                     idsNotWorked.add(id)
                  }
               }

               if(idsNotWorked.size() > 0) {
                  throw new Exception("Update failed for IDs: "+idsNotWorked.join(", "))
               } else {
                  response = this.constructResponse("OK", "Updated.")
               }
            } catch(Exception exception) {
               //log.debug(exception)
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }

      render response as JSON
   }

   def createProductType = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.name) {
            response = this.constructResponse("ERROR", "Missing name.")
         } else {
            try {
               response = operatorService.createProductType(params.userName, params.name)
            } catch (Exception exception) {
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }
      render response as JSON
   }

   def showProductTypeByName = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         if(!params.name) {
            response = this.constructResponse("ERROR", "Missing name.")
         } else {
            try {
               response = operatorService.showProductTypeByName(params.name)
            } catch (Exception exception) {
               response = this.constructResponse("ERROR", exception.getMessage())
            }
         }
      }
      render response as JSON
   }

   private int convertToPositiveInteger(def value, int defaultValue) {
      int result = defaultValue
      if(value) {
         try {
            result = Integer.parseInt(value)
         } catch(Exception exception) {
            log.debug(exception)
         }
         if(result < 0) {
            result = defaultValue
         }
      }

      return result
   }

   private def constructResponse(def status, def content) {
      def response = ["response": status, "content": content]
      return response
   }

   private boolean isAuthenticated(def userName, def password) {
      def response = productTypeService.authenticate(userName, password)
      return RESPONSE_OK.equals(response.Status)
   }

   def pauseJob = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         try {
            operatorService.pauseJob("PipelineJob", "pipelineGroup")
            response = this.constructResponse("OK", "Paused job.")
         } catch(Exception exception) {
            log.debug(exception)
            response = this.constructResponse("ERROR", exception.getMessage())
         }
      }
      render response as JSON
   }

   def resumeJob = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         try {
            operatorService.resumeJob("PipelineJob", "pipelineGroup")
            response = this.constructResponse("OK", "Resumed job.")
         } catch(Exception exception) {
            log.debug(exception)
            response = this.constructResponse("ERROR", exception.getMessage())
         }
      }
      render response as JSON
   }

   def getJobState = {
      def response = null
      if(!this.isAuthenticated(params.userName, params.password)) {
         response = this.constructResponse("ERROR", "Required to be authenticated.")
      } else {
         try {
            response = this.constructResponse("OK", operatorService.getJobState("PipelineJob", "pipelineGroup"))
         } catch(Exception exception) {
            log.debug(exception)
            response = this.constructResponse("ERROR", exception.getMessage())
         }
      }
      render response as JSON
   }

   def exportDomain = {
      if(!this.isAuthenticated(params.userName, params.password)) {
         render this.constructResponse("ERROR", "Required to be authenticated.") as JSON
      } else {
         try {
            render(contentType:"text/xml", encoding:"UTF-8", text: operatorService.exportDomain(params.default))
         } catch(Exception exception) {
            log.debug(exception)
            render this.constructResponse("ERROR", exception.getMessage()) as JSON
         }
      }
   }

   def heartbeat = {
      render this.constructResponse("OK", "Manager Service is Online") as JSON
   }
}
