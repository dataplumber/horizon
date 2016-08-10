package gov.nasa.horizon.manager.controller

import gov.nasa.horizon.ingest.api.*
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import gov.nasa.horizon.manager.domain.IngDataFile
import gov.nasa.horizon.manager.domain.IngProduct
import gov.nasa.horizon.manager.domain.IngProductType
import groovy.xml.XmlUtil
import groovy.util.slurpersupport.NodeChild

class IngestController {
   def transactionService
   
   def init = {
      log.debug("calling IngestRequest.loadRequest with params ${request.XML.params}")
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(request.XML.params as String)
      log.debug("return from loadRequest")

      flash.protocol = protocol

      if (transactionService.checkUser(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
               contentType: "text/xml",
               encoding: "UTF-8")
      } else {
         transactionService.createUserSession(protocol)
         log.debug "RESPONSE: ${protocol.toString()}."
         render(text: protocol.toString(),
               contentType: "text/xml",
               encoding: "UTF-8")
      }
   }

   def index = {
      redirect(action: list)
      //redirect { action: list }
   }

   def report = {
      def protocol = flash.protocol

      log.debug("${protocol.errno}: ${protocol.description}.")
      render(text: protocol.toString(),
            contentType: "text/xml",
            encoding: "UTF-8")
   }

   def add = {
      try {
         log.debug "Executing action $actionName ${request.XML.params}"
         IngestProtocol protocol = new IngestProtocol()
         protocol.load(request.XML.params as String)

         flash.protocol = protocol

         if (transactionService.verifyUserSession(protocol) != Errno.OK) {
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            log.debug("calling service method")

            def methodTime = new Date()
            transactionService.addProduct(protocol)
            log.trace("controller: add: " + (new Date().getTime() - methodTime.getTime()));

            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         }
      } catch (Exception e) {
         log.debug(e.message, e)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }

   /* TODO: with ZK backend, archive update should already being
   ** handled by ManagerWatcherService
   def addupdate = {
      try {
         log.debug "Executing action ${actionName}"
         IngestProtocol protocol = new IngestProtocol()
         protocol.load(params.horizon.params)

         log.debug(params.horizon.params)

         flash.protocol = protocol
         if (transactionService.verifyUserSession(protocol) != Errno.OK) {
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            def methodTime = new Date()
            transactionService.addProductUpdate(protocol)
            log.trace("controller: addupdate: " + (new Date().time - methodTime.time));

            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         }
      } catch(Exception e) {
         log.debug(e.message, e)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }
   */


   def replace = {
      try {
         log.debug "Executing action ${actionName}"
         IngestProtocol protocol = new IngestProtocol()
         protocol.load(request.XML.params as String)

         flash.protocol = protocol

         if (transactionService.verifyUserSession(protocol) != Errno.OK) {
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            def methodTime = new Date()
            transactionService.replaceProduct(protocol)
            log.trace("controller: replace: " + (new Date().time - methodTime.time));

            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         }
      } catch (Exception e) {
         log.debug(e.message, e)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }

   /* TODO: with ZK backend, archive update should already being
   ** handled by ManagerWatcherService
   def deleteupdate = {
      try {
         log.debug "Executing action ${actionName}"
         IngestProtocol protocol = new IngestProtocol()
         protocol.load(params.horizon.params)

         log.debug(params.horizon.params)

         flash.protocol = protocol
         if (transactionService.verifyUserSession(protocol) != Errno.OK) {
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            def methodTime = new Date()
            transactionService.deleteProductUpdate(protocol)
            log.trace("controller: deleteupdate: " + (new Date().time - methodTime.time));

            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         }
      } catch(Exception e) {
         log.debug(e.message, e)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }
   */


   def list = {
      try {
         log.trace "Executing action ${actionName}"
         IngestProtocol protocol = new IngestProtocol()
         protocol.load(request.XML.params as String)

         log.debug "LIST REQUEST: ${protocol.toString()}"

         flash.protocol = protocol

         def methodTime = new Date()
         if (transactionService.verifyUserSession(protocol) != Errno.OK) {
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else if (!protocol.productType) {
            protocol.errno = Errno.INVALID_TYPE
            protocol.description = 'Product type is missing.'
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            IngProductType productType = IngProductType.findByName(protocol.productType)
            if (!productType) {
               protocol.errno = Errno.INVALID_TYPE
               protocol.description = "Product type '${protocol.productType}' not found."
               log.debug("${protocol.errno}: ${protocol.description}.")
               render(text: protocol.toString(),
                     contentType: "text/xml",
                     encoding: "UTF-8")
            } else if (!protocol.product) {
               protocol.errno = Errno.PRODUCT_NOT_FOUND
               protocol.description = 'Product name is missing.'
               log.debug("${protocol.errno}: ${protocol.description}.")
               render(text: protocol.toString(),
                     contentType: "text/xml",
                     encoding: "UTF-8")
            } else {
               def criteria = IngProduct.createCriteria()
               def products = criteria.list {
                  eq("name", protocol.product)
                  eq("productType", productType)
               }
               if (!products || products.size() == 0) {
                  protocol.errno = Errno.PRODUCT_NOT_FOUND
                  protocol.description = "Product '[${productType.name}:${protocol.product}]' not found."
                  log.trace protocol.toString()
                  render(text: protocol.toString(),
                        contentType: "text/xml",
                        encoding: "UTF-8")
               } else {
                  products.each { IngProduct prod ->
                     Product product = new Product(name: prod.name,
                           state: State.valueOf(prod.currentState),
                           lock: Lock.valueOf(prod.currentLock),
                           note: prod.note,
                           archiveNote: prod.archiveNote,
                           createdTime: (prod.created) ? new Date(prod.created) : null,
                           stagedTime: (prod.completed) ? new Date(prod.completed) : null,
                           archivedTime: (prod.archivedAt) ? new Date(prod.archivedAt) : null)
                     if (prod.currentState == State.ARCHIVED) {
                        product.metadataText = prod.archiveText.toString()
                     } else if (prod.currentState == State.INVENTORIED) {
                        product.metadataText = prod.completeText.toString()
                     } else {
                        product.metadataText = prod.initialText.toString()
                     }
                     def prodFiles = []
                     if (prod.files) {
                        prod.files.each { IngDataFile file ->
                           ProductFile pFile = new ProductFile(
                                 name: file.name,
                                 size: file.fileSize,
                                 checksum: file.checksum
                           )
                           prodFiles << pFile
                        }
                     }
                     product.productFiles = prodFiles
                     protocol.addProduct(product)
                  }
                  protocol.errno = Errno.OK
                  log.debug("LIST result: " + protocol.toString())
                  render(text: protocol.toString(),
                        contentType: "text/xml",
                        encoding: "UTF-8")
               }
            }
         }

         log.trace("controller: list: " + (new Date().time - methodTime.time));
      } catch (Exception e) {
         log.debug(e.message, e)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }

   /* TODO: with ZK backend, archive update should already being
   ** handled by ManagerWatcherService
   def moveupdate = {
      try {
         log.debug "Executing action $actionName"
         IngestProtocol protocol = new IngestProtocol()
         protocol.load(params.horizon.params)
         log.debug(params.horizon.params)
         flash.protocol = protocol
         if (transactionService.verifyUserSession(protocol) != Errno.OK) {
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            def methodTime = new Date()
            transactionService.moveProductUpdate(protocol)
            log.trace("controller: moveupdate: " + (new Date().getTime() - methodTime.getTime()));

            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         }
      } catch(Exception e) {
         log.debug(e.message, e)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }
   */

   /* TODO: with ZK backend, archive update should already being
   ** handled by ManagerWatcherService
   def archiveupdate = {
      try {
         log.debug "Executing action: ${actionName}"
         IngestProtocol protocol = new IngestProtocol()
         protocol.load(params.horizon.params)
         log.debug(params.horizon.params)
         flash.protcool = protocol
         if (transactionService.verifyUserSession(protcol) != Errno.OK) {
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            def methodTime = new Date()
            transactionService.archiveProductUpdate(protocol)
            log.trace("controller: archiveupdate: " + (new Date().getTime() - methodTime.getTime()));

            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         }
      } catch(Exception e) {
         log.debug(e.message, e)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }
   */

   /* TODO: not sure if we still need this
   def boot = {
      log.debug "Executing action $actionName"

      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)

      flash.protocol = protocol

      if (transactionService.verifyUserSession(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
               contentType: "text/xml",
               encoding: "UTF-8")
      } else {

         if (!protocol.federation || !protocol.engine) {
            protocol.errno = Errno.DENIED
            protocol.description =
               'Unable to update engine boot-up info due to missing federation and/or engine name.'
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                  contentType: "text/xml",
                  encoding: "UTF-8")
         } else {
            IngFederation federation =
               IngFederation.findByName(protocol.federation)
            if (!federation) {
               protocol.errno = Errno.DENIED
               protocol.description =
                  "Unable to lookup federation ${protocol.federation}."
               log.debug("${protocol.errno}: ${protocol.description}.")
               render(text: protocol.toString(),
                     contentType: "text/xml",
                     encoding: "UTF-8")
            } else {
               IngEngine engine = IngEngine.findByFederationAndName(federation, protocol.engine)

               if (!engine) {
                  protocol.errno = Errno.DENIED
                  protocol.description =
                     "Unable to lookup engine ${protocol.engine}."
                  log.debug("${protocol.errno}: ${protocol.description}.")
                  render(text: protocol.toString(),
                        contentType: "text/xml",
                        encoding: "UTF-8")
               } else {
                  protocol.errno = Errno.OK
                  protocol.description = "${federation.name}:${engine.name}"
                  protocol.federation = federation.name
                  protocol.federationId = federation.ident()
                  protocol.engine = engine.name
                  protocol.engineId = engine.ident()
                  protocol.bind = engine.hostname
                  protocol.userPort = engine.userPort
                  protocol.adminPort = engine.adminPort
                  protocol.protocol = engine.protocol
                  protocol.urlPattern = engine.urlPattern
                  protocol.maxUserConnections = engine.maxUserConnections
                  protocol.maxAdminConnections = engine.maxAdminConnections
                  protocol.storageLocation = engine.storage.localPath
                  log.trace(protocol.toString())
                  log.debug("${protocol.errno}: ${protocol.description}.")
                  render(text: protocol.toString(),
                        contentType: "text/xml",
                        encoding: "UTF-8")

                  def loop = true
                  while (loop) {
                     loop = false
                     try {
                        def currentDate = new Date()
                        engine.refresh()
                        engine.startedAt = currentDate.getTime()
                        engine.active = true
                        engine.isOnline = true
                        engine.lastSeen = currentDate.getTime()
                        engine.updated = currentDate.getTime()
                        engine.save(flush: true)
                     } catch (OptimisticLockingFailureException e) {
                        engine.discard()
                        loop = true
                     }
                  }

                  sigEventService.send(
                        SIG_EVENT_CATEGORY,
                        EventType.Info,
                        "Ingest engine ("+protocol.engine+") has been started",
                        "Ingest engine ("+protocol.engine+") has been started."
                  )
               }
            }
         }
      }
   }
   */
}
