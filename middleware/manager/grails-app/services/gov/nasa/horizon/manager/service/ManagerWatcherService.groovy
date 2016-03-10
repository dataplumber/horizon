/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.common.api.util.URIPath
import gov.nasa.horizon.common.api.zookeeper.api.ZkAccess
import gov.nasa.horizon.common.api.zookeeper.api.ZkFactory
import gov.nasa.horizon.ingest.api.*
import gov.nasa.horizon.ingest.api.content.SIPHandler
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import gov.nasa.horizon.manager.domain.*
import gov.nasa.horizon.sigevent.api.EventType
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.springframework.dao.OptimisticLockingFailureException
import org.xml.sax.SAXParseException


/**
 *
 * @author T. Huang
 * @version $Id: $
 */
class ManagerWatcherService {

   static transactional = true

   def grailsApplication
   def sigEventService
   def storageService
   def inventoryService

   static int MAX_NOTE = 2999

   /**
    * Method to update an existing product after it has been ingested
    * @param protocol the request protocol
    * @return the transaction status
    */
   private Errno addProductUpdate(IngestProtocol protocol) {
      boolean isIngest = (Stereotype.INGEST == protocol.stereotype)
      def loop = true

      def methodTime = new Date()
      IngProduct product = IngProduct.get(protocol.productId)
      if (!product) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product ${protocol.product}. No product found matching id "+protocol.productId
         log.error(protocol.description)
         return protocol.errno
      }
      if (product.name != protocol.product) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product ${protocol.product}. Name mismatch between DB product name ("+product.name+")." 
         log.error(protocol.description)
         return protocol.errno
      }
      
      log.trace("Trying to lock product type ${protocol.productType}")
      IngProductType productType = IngProductType.get(protocol.productTypeId)
      if (!productType || productType.name != protocol.productType) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product ${protocol.product} for product type ${protocol.productType}."
         log.error(protocol.description)
         return protocol.errno
      }

      IngEngineJob engineJob = IngEngineJob.get(protocol.jobId)
      if (!engineJob) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unable to locate job record for this product ${product.name}."
         product.note = trimNote(product.note + protocol.description) + "\n"
         product.currentState = State.ABORTED.toString()
         product.currentLock = Lock.RESERVED.toString()
         log.trace(protocol.description + " JobID: ${protocol.jobId}")
         product.save(flush: true)
         log.debug "Product "+product.name+" moved to state "+product.currentState
         log.error(protocol.description)

         sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job: ${product.name}", "Unable to locate job record for this product ${product.name}.")

         return protocol.errno
      }

      log.debug("Deleting engine job "+engineJob.id+" from DB after successful watcher call")
      while (loop) {
         loop = false
         engineJob.refresh()
         engineJob.lock()
         if (!engineJob) break
         try {
            engineJob.delete(flush: true)
            log.debug("Successful row deletion of engine job!")
         } catch (OptimisticLockingFailureException e) {
            log.trace(e.message, e)
            loop = true
         }
      }

      if (protocol.errno == Errno.CHECKSUM_ERROR || protocol.errno == Errno.INGEST_ERR) {
         log.debug("Errno INGEST_ERR caught. Running error recovery")
         
         // return the pre-allocated storage
         IngStorage storage = product.contributeStorage
         long totalSize = product.files.sum { it.fileSize } as long
         storageService.updateStorage(-totalSize, new Date().time, storage.location.id)
         
         log.debug("Attempting to revert state for product "+product.name+" after file size mismatch")
         def process = true
         while(process) {
             try {
                 product.lock()
                 log.debug("${product.name}: has been lock for addProductUpdate")
                 process = false
             }catch (OptimisticLockingFailureException e){
                 log.debug("${product.name}: has been locked by another thread, waitting to get lock..")
                 product.refresh()
             }

         }
         loop = true
         while (loop) {
            loop = false
            try {
               //product.refresh()
               //product.lock()
               // put the product back for another try
               product.currentState = (isIngest) ? State.PENDING.toString() : State.PENDING_ARCHIVE.toString()
               product.note = trimNote(product.note + "{$product.currentState}}: ${protocol.description}") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
               log.debug "Product "+product.name+" moved to state "+product.currentState
               log.debug("Product state for product "+product.name+" reverted!")
            } catch (OptimisticLockingFailureException e) {
               log.trace(e.message, e)
               loop = true
            }
         }
         return protocol.errno
      }

      if (protocol.errno == Errno.OK) {
         SIPHandler handler = null
         if (isIngest) {
            log.trace("update file metadata")
            handler = new SIPHandler(product.initialText)

            handler.updateIngestDetails(protocol.addFiles,
                  protocol.operationStartTime, protocol.operationStopTime)

            log.trace("[COMPLETE XML]: ${handler.metadataText}")
         } else {
            if (!inventoryService.updateProductStatus(product.inventoryId, 'ONLINE')) {
               protocol.errno = Errno.INVENTORY_ERR
               protocol.description = "Unable to update product status of ${product.name} to ONLINE through Inventory Service"
               log.error(protocol.description)
               return protocol.errno
            }
            log.debug("Updating granule status: ${product.inventoryId}")
         }

         //def productId = product.id
         product.discard()
         product = IngProduct.lock(protocol.productId)

         if (isIngest) {
            product.completeText = handler.metadataText
            product.currentState = State.STAGED.toString()
            product.currentLock = product.currentLock
            product.completed = new Date().getTime()

            def fileMap = [:]
            product.files.each { IngDataFile file ->
               fileMap[file.name] = file
            }

            protocol.addFiles.each { ProductFile file ->
               def filename = URIPath.createURIPath(file.source).filename
               IngDataFile idf = fileMap[filename] as IngDataFile
               idf.fileSize = file.size
               idf.checksum = file.checksum
               idf.currentLock = Lock.GET
               idf.ingestStarted = file.startTime.time
               idf.ingestCompleted = file.stopTime.time
               idf.note = file.description
            }

            product.note = trimNote(product.note + "{${product.currentState}}: All ${protocol.addFiles.size()} file(s) have been ingested.") + "\n"
         } else {
            //product.refresh()
            //product.lock()
            if (product.currentState == State.PENDING_ASSIGNED.toString()) {
                product.currentState = State.ASSIGNED.toString()
                product.note = trimNote(product.note + "{${product.currentState}}: Found completed archive text. Skipping straight to ARCHIVE.") + "\n"
                product.updated = new Date().getTime()
                //TODO catch exception and ignore
                product.save(flush: true)
                log.debug "Product "+product.name+" moved to state "+product.currentState
            }

            product.currentState = State.ARCHIVED.toString()
            product.currentLock = Lock.DELETE.toString()
            product.archiveNote = trimNote('[' + protocol.errno + ']: Product has been archived.') + "\n"
            product.archivedAt = protocol.operationStopTime.getTime()
            product.note = trimNote("${product.note}{${product.currentState}}: Archive OK.  Product will be purged from ingest in approximately ${product.productType.purgeRate / 60.0} hour(s).") + "\n"
         }
         product.updated = new Date().getTime()
         product.save(flush: true)
         log.debug "Product "+product.name+" moved to state "+product.currentState
         if (!isIngest) {
            def dateStart = new Date()
            sigEventService.send(product.productType.eventCategory.name, EventType.Info, "AIP Published: ${product.name}", product.archiveText)
            def dateEnd = new Date()
            log.debug("sigEvent.create to notify AIP: " + ((dateEnd.time - dateStart.time) / 1000.0f) + " sec.")
         }
      }

      log.trace("addProductUpdate: method: " + ((isIngest) ? "ingest" : "archive") + ": " + (new Date().time - methodTime.time))

      return protocol.errno
   }

   /**
    * Method called by the ingest engine to update on a move request
    * @param protocol the request packet
    * @return the transaction status
    */
   private Errno moveProductUpdate(IngestProtocol protocol) {
      def loop = true
      IngProduct product = IngProduct.get(protocol.productId)
      if (!product) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product '${protocol.product}' from ingest engine '${protocol.engine}'."
         log.error(protocol.description)
         return protocol.errno
      }

      IngEngineJob engineJob = IngEngineJob.get(protocol.jobId)
      if (!engineJob) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unable to locate job record for product ${product.name}."
         def process = true
         while(process) {
             try {
                 product.lock()
                 log.debug("${product.name}: has been lock for moveProductUpdate")
                 process = false
             }catch (OptimisticLockingFailureException e){
                 log.debug("${product.name}: has been locked by another thread, waitting to get lock..")
                 product.refresh()
             }

         }
         while (loop) {
            loop = false
            try {
               //product.refresh()
               //product.lock()
               product.currentState = State.ABORTED.toString()
               product.currentLock = Lock.RESERVED.toString()
               product.note = trimNote("${product.note}{${product.currentState}}: ${protocol.description}") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
               log.debug "Product "+product.name+" moved to state "+product.currentState
               sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job: ${product.name}", "Unable to locate job record for product ${product.name}.")
            } catch (OptimisticLockingFailureException e) {
               log.trace(e.message, e)
               loop = true
            }
         }
         log.error(protocol.description)
         return protocol.errno
      }

      loop = true
      while (loop) {
         loop = false
         try {
            engineJob.refresh()
            engineJob.lock()
            if (!engineJob) break
            engineJob.delete(flush: true)
         } catch (OptimisticLockingFailureException e) {
            log.trace(e.message, e)
            loop = true
         }
      }

      if (protocol.errno == Errno.FILE_NOT_FOUND) {
         log.warn("File(s) not found during deletion: '${protocol.description}'")
      }

      //def productId = product.id
      product.discard()
      product = IngProduct.lock(protocol.productId)
      product.lock()
      if (protocol.errno == Errno.OK || protocol.errno == Errno.FILE_NOT_FOUND) {
         product.currentLock = Lock.TRASH.toString()
         product.localRelativePath = '.trashbin/' + product.localRelativePath
         product.note = trimNote(product.note + "INFO: All ${product.files.size()} file(s) have been moved to trashbin.") + "\n"
      } else {
         product.currentLock = Lock.RESERVED.toString()
         product.currentState = State.ABORTED.toString()
         product.note = trimNote(product.note + "{${product.currentState}}: Problem(s) during move to trashbin: '${protocol.description}.'") + "\n"
      }
      product.updated = new Date().getTime()
      product.save(flush: true)
      log.debug "Product "+product.name+" moved to state "+product.currentState
      if (product.currentState == State.ABORTED.toString()) {
         sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job", "{${product.currentState}}: Problem(s) during move to trashbin: '${protocol.description}.'")
      }
      return protocol.errno
   }

   private Errno deleteProductUpdate(IngestProtocol protocol) {
      def loop = true
      IngProduct product = IngProduct.get(protocol.productId)
      if (!product || product.name != protocol.product) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product '${protocol.product}' from ingest engine '${protocol.engine}'."
         log.error(protocol.description)
         return protocol.errno
      }

      IngEngineJob engineJob = IngEngineJob.get(protocol.jobId)
      if (!engineJob) {
         log.debug("Warning: Unable to locate job record ${protocol.jobId} for product ${product.name}.")
         while (loop) {
            loop = false
            try {
               product.refresh()
               product.lock()
               product.currentState = State.ABORTED.toString()
               product.currentLock = Lock.RESERVED.toString()
               protocol.errno = Errno.UNEXPECTED_UPDATE
               protocol.description = "Unable to locate job record ${protocol.jobId} for product ${product.name}."
               product.note = trimNote(product.note + "{${product.currentState}}: '${protocol.description}'") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
               log.debug "Product "+product.name+" moved to state "+product.currentState
               sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job", "Unable to locate job record ${protocol.jobId} for product ${product.name}.")
            } catch (OptimisticLockingFailureException e) {
               log.trace(e.message, e)
               loop = true
            }
         }
         log.error(protocol.description)
         return protocol.errno
      } else {
         log.trace("Delete job ${engineJob.id} for product ${product.name}.")
         loop = true
         while (loop) {
            loop = false
            try {
               engineJob.refresh()
               if (!engineJob) break
               engineJob.lock()
               engineJob.delete(flush: true)
            } catch (OptimisticLockingFailureException e) {
               log.trace(e.message, e)
               loop = true
            }
         }
      }

      // check if this delete is for replace or regular purge
      boolean isReplace = (product.currentState == State.PENDING_ARCHIVE.toString())
      log.debug("isReplace: " + isReplace + ", totalSize: " + protocol.totalSize)

      if (protocol.errno == Errno.FILE_NOT_FOUND) {
         log.warn("Product '${protocol.product}' not found during purge: '${protocol.description}'.")
      }

      // purging database records
      if (protocol.errno == Errno.OK || protocol.errno == Errno.FILE_NOT_FOUND) {

         IngStorage storage = engineJob.contributeStorage
         storageService.updateStorage(-protocol.totalSize, new Date().getTime(), storage.location.id)

         // performs cascade delete
         //def productId = product.id
         product.discard()
         product = IngProduct.lock(protocol.productId)
         product.lock()
         log.trace("deleteProductUpdate: locked")
         if (!isReplace) {
            product.refresh()
            product.delete(flush: true)
         } else {
            product.refresh()
            product.currentLock = Lock.INVENTORY.toString()
            product.currentState = State.INVENTORIED.toString()
            product.note = trimNote(product.note + "INFO: Done purging for replace.") + "\n"
            product.updated = new Date().getTime()
            product.save(flush: true)
            log.debug "Product "+product.name+" moved to state "+product.currentState
         }
         log.trace("deleteProductUpdate: released")
      } else {
         loop = true
         while (loop) {
            loop = false
            try {
               product.refresh()
               product.lock()
               product.currentLock = Lock.RESERVED.toString()
               product.currentState = State.ABORTED.toString()
               product.note = trimNote(product.note + "{${product.currentState}}: Problem(s) during purge: '${protocol.description}'") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
               log.debug "Product "+product.name+" moved to state "+product.currentState
               sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to purge", "{${product.currentState}}: Problem(s) during purge: '${protocol.description}'")
            } catch (OptimisticLockingFailureException e) {
               log.trace(e.message, e)
               loop = true
            }
            log.trace("Looping: 830")
         }
      }
      return protocol.errno
   }

   String trimNote(String note) {
      def buf = new StringBuffer(note)
      buf.length = MAX_NOTE
      return buf.toString().trim()
   }

   def handleZkWatcher(String path, Watcher w) {
      if (path.contains("queue") && (path.contains("ingest") || path.contains("archive"))) {
         int i = 0
         while (i < 3) {
            IngEngineJob engineJob = IngEngineJob.findByPath(path)
            if (engineJob) {
               path = engineJob.product.productType.name + "/" + engineJob.product.name
               break
            } else {
               // The path probably just didn't get saved yet so wait for at most 3 seconds
               log.debug("Cannot locate corresponding engine job for queue node " + path + " sleeping for 1 second")
               Thread.sleep(1000)
               i++
            }
         }
         if (i == 3) {
            log.debug("Cannot locate corresponding engine job for queue node " + path)
            return false
         }
      }

      def watcher = { WatchedEvent event ->
         log.debug ("ManagerWatcherService handleZkWatcher: received event " + event.type + " " + event.state)
      }
      try {
         ZkAccess zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String, watcher as Watcher)
         // Attempt to read and put a watch on the process node created by the engine
         String s = zk.readProcessNode(path, w)
         log.debug("[process node data] " + s)
         if (s != null) {
            try {
               def xml = new XmlSlurper().parseText(s)
               if (zk.removeProcessNode(path)) {
                  log.debug("Removed process node: " + path)
                  IngestProtocol protocol = new IngestProtocol()
                  protocol.load(xml.params.toString())
                  if (protocol.operation == Opcode.ENGINE_INGEST) {
                     addProductUpdate(protocol)
                  } else if (protocol.operation == Opcode.ENGINE_DELETE) {
                     deleteProductUpdate(protocol)
                  } else if (protocol.operation == Opcode.ENGINE_MOVE) {
                     moveProductUpdate(protocol)
                  } else {
                     log.error("Unrecognized operation " + protocol.operation)
                  }
               } else {
                  log.debug("Unable to remove process node: " + path)
               }
               return true
            } catch (SAXParseException e) {
               log.trace(e.message, e)
               String[] names = path.split("/")
               String productTypeName = names[0]
               String productName = names[1]
               IngProduct product = IngProduct.createCriteria().get {
                  eq("name", productName)
                  productType { eq("name", productTypeName) }
               }
               if (product) {
                  log.debug("Got non xml data from engine so updating product note")
                  productStateUpdate(product.id, s)
                  return true
               } else {
                  log.error("Cannot locate product to update state for process node: " + path)
               }
            } catch (Exception e) {
               log.error(e.message, e)
            }
         }
      } catch (IOException e) {
         //TODO fire sigevent
         if (e.message.startsWith("Max attempts failed")) {
            log.debug(e.message)
         } else {
            log.error(e.message, e)
         }
      }
      return false
   }

   private void productStateUpdate(Long productId, String note) {
      IngProduct product = IngProduct.get(productId)
      product.lock()
      if (product) {
         product.refresh()
         if (product.currentState == State.PENDING_ASSIGNED.toString()) {
            product.currentState = State.ASSIGNED.toString()
         } else if (product.currentLock == Lock.PENDING_RESERVED.toString()) {
            product.currentLock = Lock.RESERVED.toString()
         } else {
            product.discard()
            return
         }
         product.note = trimNote(product.note + "{${product.currentState}}: " + note) + "\n"
         product.updated = new Date().getTime()
         //TODO catch exception and ignore
         product.save(flush: true)
         log.debug "Product "+product.name+" moved to state "+product.currentState
      }
   }


}
