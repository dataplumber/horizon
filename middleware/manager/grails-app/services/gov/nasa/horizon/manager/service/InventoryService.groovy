/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.common.api.serviceprofile.*
import gov.nasa.horizon.ingest.api.Errno
import gov.nasa.horizon.ingest.api.Lock
import gov.nasa.horizon.ingest.api.State
import gov.nasa.horizon.ingest.api.Stereotype
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import gov.nasa.horizon.inventory.api.InventoryApi
import gov.nasa.horizon.inventory.api.InventoryException
import gov.nasa.horizon.inventory.model.Product
import gov.nasa.horizon.inventory.model.ProductType
import gov.nasa.horizon.inventory.model.ProductTypeCoverage
import gov.nasa.horizon.inventory.model.ProductTypePolicy
import gov.nasa.horizon.manager.domain.IngProduct
import gov.nasa.horizon.manager.domain.IngProductType
import gov.nasa.horizon.sigevent.api.EventType
import org.springframework.dao.OptimisticLockingFailureException
import org.hibernate.StaleObjectStateException

class InventoryService {

   static transactional = false

   private static final String SIG_EVENT_CATEGORY = "UNCATEGORIZED"
   private static final String SIG_EVENT_HORIZON_CATEGORY = "HORIZON"

   def grailsApplication
   def sigEventService

   static int MAX_NOTE = 2999


   def processStaged() {
      def testStartDate = new Date()
      log.debug("Processing staged.")

      // manager_federation is set in BootStrap
      def criteria = IngProduct.createCriteria()
      def pendings = criteria.list {
         'in'('currentLock', [Lock.ADD.toString(), Lock.REPLACE.toString()])
         'in'('currentState', [State.STAGED.toString(), State.LOCAL_STAGED.toString()])
         productType {
            federation {
               eq('name', grailsApplication.config.manager_federation as String)
            }
            order('priority', 'asc')
         }
         maxResults(grailsApplication.config.query.processStaged.max as Integer)
         order('created', 'asc')
      }
      if (!pendings || pendings.size() == 0)
         return

      for (IngProduct product in pendings) {
	def process = true
	while(process) {
		try {
                	product.lock()
			log.debug("${product.name}: has been lock for processStaged")
			process = false
         	}catch (OptimisticLockingFailureException e){
                	log.debug("${product.name}: has been locked by another thread, waitting to get lock..")
                	product.refresh()
         	}

	}

         if ((![Lock.ADD, Lock.REPLACE].find { it == Lock.valueOf(product.currentLock) }) ||
               !(State.valueOf(product.currentState) in [State.STAGED, State.LOCAL_STAGED])) {
            continue
         }
	
         //product.refresh()
         if (!product.productType.ingestOnly) {
            IngestProtocol protocol = IngestProtocol.createEngineIngestRequest(
                  Stereotype.ARCHIVE,
                  product.productType.federation.name,
                  product.productType.federation.id,
                  product.productType.name,
                  product.productType.id,
                  product.name,
                  product.id
            )
            protocol.addMetadata = product.completeText

            log.debug('Calling Inventory WS')
            long productId = this.updateInventory(protocol)
            log.debug("Return status ${protocol.errno}: ${protocol.description}")

            def loop = true
            while (loop) {
               loop = false
               try {
		  //product.refresh()
		  //product.lock()
                  if (productId < 0 || protocol.errno != Errno.OK) {
                     product.currentState = State.ABORTED.toString()
                     product.currentLock = Lock.RESERVED.toString()
                     product.note = this.trimNote("${product.note}{${product.currentState}}: Inventory failure - ${protocol.description}.") + '\n'
                     log.error("Error cataloging ${product.name}: ${protocol.description}.")
                     sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to catalog", "{${product.currentState}}: Inventory failure  - ${protocol.description}.")
                  } else {
                     boolean isReplace = (product.currentLock == Lock.REPLACE.toString())
                     log.debug("isReplace: ${isReplace}")
                     if (isReplace) {
                        ServiceProfile serviceProfile =
                           ServiceProfileFactory.instance.createServiceProfileFromMessage(protocol.addMetadata)
                        List<SPFileDestination> filesToDelete = serviceProfile?.submission?.archive?.deletes
                        if (!filesToDelete || filesToDelete.size() == 0) {
                           log.info("Nothing needs to be purged for product '${product.name}' to replace.")
                           isReplace = false
                        }
                     }

                     product.inventoryId = productId
                     product.archiveText = protocol.addMetadata
                     product.currentLock = Lock.INVENTORY.toString()
                     product.currentState = (isReplace) ? State.PENDING_ARCHIVE.toString() : State.INVENTORIED.toString()
                     product.note = this.trimNote("${product.note}{${product.currentState}}: Product has been inventoried.") + "\n"
                     log.debug("Done cataloging ${product.name}")
                  }
                  log.debug("${product.name} ${product.currentState} ${product.currentLock}")
                  product.updated = new Date().time
                  product.save(flush: true)
                  log.debug "Product "+product.name+" moved to state "+product.currentState
               } catch (StaleObjectStateException e) {
                   log.trace(e.message, e)
                   loop = true
               } catch (OptimisticLockingFailureException e) {
                  loop = true
               }
            }
         }
      }

      def testEndDate = new Date()
      log.debug("Processing staged done: ${testEndDate.time - testStartDate.time}")
   }

   def processInventoried() {
      def testStartDate = new Date()
      log.debug("Processing inventoried.")
      def criteria = IngProduct.createCriteria()
      def pendings = criteria.list {
         eq('currentLock', Lock.INVENTORY.toString())
         eq('currentState', State.INVENTORIED.toString())
         productType {
            federation {
               eq("name", grailsApplication.config.manager_federation as String)
            }
            order('priority', 'asc')
         }
         lock(true)
         maxResults(grailsApplication.config.query.processInventoried.max as Integer)
         order('created', 'asc')
      }
      if (!pendings || pendings.size() == 0) {
         return
      }
      def testStartDate2 = new Date()
      log.debug("TIMING after initial query: " + (testStartDate2.time - testStartDate.time))
      for (IngProduct product in pendings) {
            def process = true
            while(process) {
                try {
                    product.lock()
                    log.debug("${product.name}: has been lock for processInventoried")
                    process = false
                }catch (OptimisticLockingFailureException e){
                    log.debug("${product.name}: has been locked by another thread, waitting to get lock..")
                    product.refresh()
                }

            }
         if (Lock.valueOf(product.currentLock) != Lock.INVENTORY ||
               State.valueOf(product.currentState) != State.INVENTORIED) {
            // the product's lock/state was updated since the query
            continue
         }

         def loop = true
         def count = 0
         while (loop) {
            loop = false
            try {
                count++
               //product.refresh()
               if (!product) break
               //product.lock()
               // set granule online/offline
               product.currentLock = Lock.ARCHIVE.toString()
               product.currentState = State.PENDING_ARCHIVE.toString()
               product.currentRetries = 5
               product.note = trimNote("${product.note}{${product.currentState}}: Archive requested.") + "\n"
               log.debug("Done updated product status ${product.name}, ${product.currentState}")

               product.updated = new Date().time
               product.save(flush: true)
               def testStartDate3 = new Date()
               log.debug("TIMING after initial query (Attempt "+count+"): " + (testStartDate3.time - testStartDate2.time))
               log.debug "Product "+product.name+" moved to state "+product.currentState
            } catch (OptimisticLockingFailureException e) {
               loop = true
            } catch (StaleObjectStateException e) {
               loop = true
            }
         }
      }

      def testEndDate = new Date()
      log.debug("Processing inventoried done: " + (testEndDate.time - testStartDate.time))

   }

   long updateInventory(IngestProtocol protocol) {
      long productInventoryId = -1

      try {
         log.trace("[INVENTORY: ] ${protocol.addMetadata}")
         ServiceProfile profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(protocol.addMetadata as String)
         SPHeader header = profile?.submission?.header
         if (!header) {
            log.error("Invalid ingest protocol")
            return
         }
         log.debug("product: ${header.productType}, protocol: ${protocol.productType}")
         header.productType = protocol.productType

         InventoryApi service = this.authInventoryClient

         log.debug("Store ServiceProfile ${profile.submission.header.productType}:${profile.submission.header.productName}")
         profile = service.ingestSip(profile)
         header = profile.submission.header
         log.debug("Done storing ServiceProfile")
         if (profile) {
            protocol.addMetadata = profile.toString()
            productInventoryId = header.productInventoryId

            protocol.errno = Errno.OK
            protocol.description = "Metadata for granule ${productInventoryId} has been inventoried."

            log.trace "Metadata for granule ${productInventoryId} has been inventoried."
         } else {
            protocol.errno = Errno.INVENTORY_ERR
            protocol.description = "Unable to ingest SIP; ServiceProfile returned by Inventory Service is null"

            log.error(protocol.description)
         }
      } catch (ServiceProfileException e) {
         protocol.errno = Errno.INVALID_PROTOCOL
         protocol.description = e.message
         log.error("Invalid SIP: "+e.message, e)

         log.error(e.message, e)
      } catch (InventoryException e) {
         if (log.traceEnabled) {
            e.printStackTrace()
         }
         protocol.errno = Errno.INVENTORY_ERR
         protocol.description = e.message

         log.error(e.message, e)
      } catch (Exception e) {
         protocol.errno = Errno.INVENTORY_ERR
         protocol.description = e.message

         log.error(e.message, e)
      }
      return productInventoryId
   }

   boolean isExists(IngestProtocol protocol) {
      boolean result = false

      try {
         ServiceProfile profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(protocol.addMetadata)
         SPHeader header = profile?.submission?.header
         InventoryApi service = this.inventoryClient
         Long productTypeInventoryId = header.productTypeInventoryId
         if (!productTypeInventoryId) {
            productTypeInventoryId = service.getProductType(header.productType)?.id

         }
         if (productTypeInventoryId) {
            result = (service.getProduct(productTypeInventoryId, header.productName) != null)
         } else {
            protocol.errno = Errno.INVENTORY_ERR
            protocol.description = "Unable to retrieve product type ${header.productType}; Product Type returned by Inventory Service is null"

            log.error(protocol.description)
         }
      } catch (ServiceProfileException e) {
         protocol.errno = Errno.INVALID_PROTOCOL
         protocol.description = e.message
         
         log.error("Invalid SIP: "+e.message, e)


         log.error(e.message, e)
      } catch (InventoryException e) {
         // If granule does not exist, then inventory service throws an exception.
         // Not an error, so just log as debug.
         if (e.message.startsWith("Product id not found")) {
            log.debug(e.message)
         } else {
            protocol.errno = Errno.INVENTORY_ERR
            protocol.description = e.message
            log.error(e.message, e)
         }
      } catch (Exception e) {
         protocol.errno = Errno.INVENTORY_ERR
         protocol.description = e.message

         log.error(e.message, e)
      }

      return result
   }

   List<Map> getProductTypeStatusList() {
      List<Map> list = []
      InventoryApi service = this.inventoryClient
      int maxPerPage = 10
      def ingProductTypes = null
      boolean firstIter = true
      int offset = 0
      while (firstIter || ingProductTypes.size() >= maxPerPage) {
         firstIter = false
         ingProductTypes = IngProductType.createCriteria().list {//TODO PAGING
            federation {
               eq("name", grailsApplication.config.manager_federation as String)
            }
            isNotNull("deliveryRate")
            maxResults(maxPerPage)
            firstResult(offset)
         }
         offset += maxPerPage
         for (IngProductType ingProductType in ingProductTypes) {
            ProductType productType
            try {
               productType = service.getProductType(ingProductType.name)
            } catch (Exception e) {
               log.trace(e.message, e)
               log.debug("Not monitoring since unable to get product type from Inventory Service: ${ingProductType.name}")
               continue
            }
            if (!productType) {
               log.debug("Not monitoring since unable to get product type from Inventory Service: ${ingProductType.name}")
               continue
            }

            long productTypeInventoryId = productType.id
            ProductTypeCoverage productTypeCoverage
            ProductTypePolicy productTypePolicy
            try {
               productTypeCoverage = service.getProductTypeCoverage(productTypeInventoryId)
               productTypePolicy = service.getProductTypePolicy(productTypeInventoryId)
               if ((!productTypeCoverage) || (!productTypePolicy)) {
                  log.debug("Not monitoring since ProductTypeCoverage and/or ProductTypePolicy is missing: ${ingProductType.name}")
                  continue
               }

               Date coverageStopTime = productTypeCoverage.stopTime
               if ((coverageStopTime) && (coverageStopTime.getTime() < new Date().time)) {
                  log.debug("Not monitoring since stop time exists and it is past: ${productTypeInventoryId}")
                  continue
               }
            } catch (InventoryException e) {
               log.trace(e.message, e)
               log.debug("Not monitoring since ProductTypeCoverage and/or ProductTypePolicy is missing: ${productTypeId}")
               continue
            }

            Product product = null
            try {
               int productInventoryId = service.getLatestProductIdByProductType(productTypeInventoryId)

               if (productInventoryId) {
                  product = service.getProduct(productInventoryId)
               }
            } catch (InventoryException e) {
               log.debug("Exception in getProductTypeStatusList.", e)
            }

            Map entry = [:];
            entry['id'] = productTypeInventoryId
            entry['dataStream'] = productType.identifier
            entry['dataFrequency'] = ingProductType.deliveryRate
            entry['accessType'] = productTypePolicy.accessType
            entry['lastIngestTime'] = (product) ? service.getOperationTime(product.id, 'INGEST') : null
            entry['currentTime'] = new Date()
            entry['name'] = (product) ? product.name : null
            list.add(entry)
         }
      }

      return list
   }

   String getProductArchivePath(long inventoryId) {
      InventoryApi service = this.inventoryClient
      return service.getProductArchivePath(inventoryId)
   }


   boolean updateProductStatus(long inventoryId, String status) {
      InventoryApi service = this.authInventoryClient
      return service.updateProductStatus(inventoryId, status.toUpperCase())
   }
   
   boolean isValidSip(ServiceProfile sip) {
      InventoryApi service = this.inventoryClient
      return service.isValidSip(sip)
   }

   String trimNote(String note) {
      def buf = new StringBuffer(note)
      buf.length = MAX_NOTE
      return buf.toString().trim()
   }

   def ping() {
      InventoryApi service = this.authInventoryClient
      boolean result = service.heartbeat()
      return result
   }

   InventoryApi getAuthInventoryClient() {
      InventoryApi api = getInventoryClient()
      api.setAuthInfo(
            grailsApplication.config.gov.nasa.horizon.inventory.user as String,
            grailsApplication.config.gov.nasa.horizon.inventory.pass as String)
      return api
   }

   InventoryApi getInventoryClient() {
      return new InventoryApi(
            grailsApplication.config.gov.nasa.horizon.inventory.url as String
      )
   }

}
