/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

//import java.awt.GraphicsConfiguration.DefaultBufferCapabilities;

import gov.nasa.horizon.common.api.serviceprofile.SPFileDestination
import gov.nasa.horizon.common.api.serviceprofile.SPIngestProductFile
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.horizon.common.api.util.URIPath
import gov.nasa.horizon.common.api.zookeeper.api.ZkAccess
import gov.nasa.horizon.common.api.zookeeper.api.ZkFactory
import gov.nasa.horizon.common.api.zookeeper.api.constants.JobPriority
import gov.nasa.horizon.common.api.zookeeper.api.constants.RegistrationStatus
import gov.nasa.horizon.ingest.api.*
import gov.nasa.horizon.ingest.api.content.SIPHandler
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import gov.nasa.horizon.manager.domain.*
import gov.nasa.horizon.manager.utils.ManagerWatcher
import gov.nasa.horizon.sigevent.api.EventType

import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
//import org.hibernate.loader.custom.Return;
import org.springframework.dao.OptimisticLockingFailureException
import org.hibernate.StaleObjectStateException
import org.springframework.orm.hibernate4.HibernateObjectRetrievalFailureException
import org.xml.sax.SAXParseException

//import com.sun.org.apache.bcel.internal.generic.RETURN;

class TransactionService {

   private static final String SIG_EVENT_CATEGORY = "UNCATEGORIZED"
   private static final String SIG_EVENT_HORIZON_CATEGORY = "HORIZON"
   private final Watcher managerWatcher = new ManagerWatcher()


   def grailsApplication
   def inventoryService
   def sigEventService
   def storageService
   def authenticationService
   def managerWatcherService

   static int MAX_NOTE = 2999

   /**
    * Method to authenticate and retrieve user privilege
    *
    * @param protocol user input parameters in wired protocol
    * @return the result errno
    */
   def checkUser(IngestProtocol protocol) {
      IngSystemUser user
      if (!protocol.loginPassword) {
         user = IngSystemUser.findByName(protocol.user)
      } else {
         //if (authenticationService.authenticate(protocol.user, protocol.loginPassword)) {
         user = IngSystemUser.findByName(protocol.user)
         //}
      }
      long userid = Constants.NOT_SET
      long usertype = Constants.NO_ACCESS
      Errno errno = Errno.INVALID_LOGIN
      log.trace "errno = ${errno.toString()}"
      String description = "Unable to authenticate user '${protocol.user}'."
      if (user) {
         userid = user.id
         if (user.admin) {
            usertype = Constants.ADMIN
         } else if (user.readAll) {
            usertype = Constants.READ_ALL
         } else if (user.writeAll) {
            usertype = Constants.WRITE_ALL
         }
         errno = Errno.OK
         description = "User ${protocol.user} found."
         log.trace "User ${protocol.user} found.  Errno = ${errno}.  OK=${Errno.OK}.."

      }
      log.trace "checkUser errno ${errno}."
      protocol.errno = errno
      protocol.userId = userid
      protocol.userType = usertype
      protocol.description = description
      return errno
   }

   /**
    * Method to create a new user session
    *
    * @param protocol user input parameters
    * @return the result errno
    */
   def createUserSession(IngestProtocol protocol) {
      IngSystemUser user = IngSystemUser.get(protocol.userId)
      if (!user) {
         protocol.errno = Errno.DENIED
         protocol.description = "Unable to retrieve user recode id ${userid}."
         return protocol.errno
      }

      IngProductType pt = null

      if (protocol.productType) {
         pt = IngProductType.createCriteria().get {
            eq("name", protocol.productType)
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
         }
         if (!pt) {
            protocol.errno = Errno.DENIED
            protocol.description =
                    "Unable to retrieve product type ${protocol.productType} for federation ${grailsApplication.config.manager_federation}."
            return protocol.errno
         }
      }

      def token
      def userSession
      def issueTime
      def expireTime

      def loop = true
      while (loop) {
         loop = false

         issueTime = new Date()
         expireTime = issueTime + 1
         protocol.errno = Errno.OK
   
         token =
            Encrypt.encrypt("${user.name}${user.password}${issueTime.time}")
         userSession =
            IngSystemUserSession.findByUserAndProductType(user, pt, [lock: true])

         try {
            if (!userSession) {
               userSession = new IngSystemUserSession(
                     user: user,
                     productType: pt,
                     token: token,
                     issueTime: issueTime.time
               )
               if (pt)
                  protocol.description =
                     "Create new session for user ${user.name} for type ${pt.name}."
               else
                  protocol.description =
                     "Create new session for admin user ${user.name}."
            } else {
               userSession.token = token
               userSession.issueTime = issueTime.time
               if (pt)
                  protocol.description =
                     "Refresh session for user ${user.name} for type ${pt.name}."
               else
                  protocol.description =
                     "Refresh session for admin user ${user.name}."
            }

            if (user.admin) {
               userSession.expireTime = null
            } else {
               userSession.expireTime = expireTime.time
            }

            if (!userSession.save(flush: true)) {
               userSession.errors.each {
                  protocol.description += "${it}\n"
               }
               protocol.errno = Errno.DENIED
               return protocol.errno
            }

         } catch (StaleObjectStateException e) {
            log.trace(e.message, e)
            loop = true
         } catch (OptimisticLockingFailureException e) {
            log.trace(e.message, e)
            loop = true
         }

      }

      log.trace "createUserSession errno ${protocol.errno}."
      protocol.userId = user.id
      if (pt)
         protocol.productTypeId = pt.id
      protocol.sessionId = userSession.id
      protocol.sessionToken = token
      protocol.loginIssueTime = new Date(userSession.issueTime)
      protocol.loginExpireTime = userSession.expireTime ? new Date(userSession.expireTime) : null
      return protocol.errno
   }

   /**
    * Method to verify user session by checking the input session token
    *
    * @param protocol user input parameters
    * @return result errno.  OK means the session has been verified
    */
   def verifyUserSession(IngestProtocol protocol) {
      if (!protocol.description)
         protocol.description = ""

      IngSystemUser user = IngSystemUser.get(protocol.userId)
      if (!user) {
         protocol.errno = Errno.DENIED
         protocol.description += "Unable to retrieve user record id ${protocol.userId}.\n"
         return protocol.errno
      }

      IngProductType pt = null

      if (protocol.productType) {
         pt = IngProductType.createCriteria().get {
            eq("name", protocol.productType)
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
         }
         if (!pt) {
            protocol.errno = Errno.DENIED
            protocol.description += "Unable to retrieve product type ${protocol.productType} for federation ${grailsApplication.config.manager_federation}.\n"
            return protocol.errno
         }
      }

      IngSystemUserSession usersession = IngSystemUserSession.get(protocol.sessionId)
      if (!usersession) {
         protocol.errno = Errno.INVALID_LOGIN
         protocol.description += "Invalid user ${user.name} session.\n"
         return protocol.errno

      }

      if (usersession.productType && protocol.productType && usersession.productType.name != protocol.productType) {
         protocol.errno = Errno.INVALID_SESSION
         protocol.description +=
            "Session ${protocol.sessionId} is not registered by product type ${protocol.productType}.\n"
         return protocol.errno
      }

      if (!user.admin && usersession.expireTime && usersession.expireTime < new Date().time) {
         protocol.errno = Errno.SESSION_EXPIRED
         protocol.description += "User session has expired.\n"
         return protocol.errno
      }

      if (!protocol.sessionId)
         protocol.sessionId = usersession.id
      if (pt)
         protocol.productTypeId = pt.id
      if (!protocol.description)
         protocol.description = ""
      return Errno.OK
   }

   /**
    * Method called by controller for product add
    * @param protocol the request
    * @return the transaction status
    */
   def addProduct(IngestProtocol protocol) {
      return addReplaceProduct(protocol, false)
   }

   /**
    * Method called by controller for product replace
    * @param protocol the request
    * @return the transaction status
    */
   def replaceProduct(IngestProtocol protocol) {
      return addReplaceProduct(protocol, true)
   }

   /**
    * Method to support add/replace product
    * @param protocol the request protocol
    * @param forReplace flag to indicate if the transaction is for replace
    * @return the transaction status
    */
   def addReplaceProduct(IngestProtocol protocol, boolean forReplace) {
      log.debug("addReplaceProduct - ${protocol.toString()}")
      IngSystemUserRole userRole = IngSystemUserRole.findByUser(IngSystemUser.get(protocol.userId))
      if (!userRole) {
         protocol.errno = Errno.DENIED
         protocol.description = "Access role for user ${IngSystemUser.get(protocol.userId)} not found."
         return protocol.errno
      }

      IngProductType productType = IngProductType.get(protocol.productTypeId)
      if (!productType) {
         protocol.errno = Errno.DENIED
         protocol.description = "Product type ${protocol.productType} not found."
         return protocol.errno
      }

      IngProductTypeRole ptr = IngProductTypeRole.findByProductType(productType)
      if (!ptr) {
         protocol.errno = Errno.DENIED
         protocol.description = "Access role for product type ${productType.name} not found."
         return protocol.errno
      }

      if (!userRole.canAdd(ptr)) {
         protocol.errno = Errno.DENIED
         protocol.description = "Attempt to add/replace product to type ${productType.name} denied."
         return protocol.errno
      }
      /*
      // Inventory hook to check sip validity... causes manager to be a CPU hog with every addition
      def sip = ServiceProfileFactory.instance.createServiceProfileFromMessage(protocol.addMetadata)
      if(!inventoryService.isValidSip(sip)) {
         protocol.errno = Errno.IGNORED
         protocol.description = "Inventory check of SIP retured false for ${productType.name}."
         return protocol.errno
      }
      */
      def product
      Lock currentLock = Lock.ADD

      if (protocol.originalProduct && forReplace) {
         // this is a replace operation
         currentLock = Lock.REPLACE
         def original = IngProduct.findWhere(
               productType: productType,
               name: protocol.originalProduct,
               versionNumber: 1)
         if (original) {
            protocol.errno = Errno.PRODUCT_EXISTS
            protocol.description = "Illegal replace operation.  Original product '${productType.name}:${protocol.product} is currently locked for ''${original.currentLock}'."
            return protocol.errno
         }

         product = IngProduct.findWhere(
               productType: productType,
               name: protocol.product,
               versionNumber: 1)
         if (product) {
            protocol.errno = Errno.PRODUCT_EXISTS
            protocol.description = "Illegal replace operation.  Product '${productType.name}:${protocol.product}' already exist."
            return protocol.errno
         }

         def productExists = inventoryService.isExists(protocol)
         if (protocol.errno && protocol.errno != Errno.OK) {
            return protocol.errno
         }
         if (!productExists) {
            // clears the replace block and allow this to be an ADD
            protocol.originalProduct = null
            SIPHandler sipHandler = new SIPHandler(protocol.addMetadata)
            sipHandler.replace = null
            protocol.addMetadata = sipHandler.metadataText
         }
         // if it gets here, the granule is in inventory but
         // it was not ingested by Ingest or has already been purged.
         // It is possible the granule was migrated from
         // legacy system.  Therefore,
         // we will keep the REPLACE option for Inventory
      } else {
         // this is an add operation
         product = IngProduct.findWhere(productType: productType, name: protocol.product, versionNumber: 1)
         if (product || inventoryService.isExists(protocol)) {
            protocol.errno = Errno.PRODUCT_EXISTS
            protocol.description = "Product '${protocol.product}' for type '${productType.name}' already exist."
            return protocol.errno
         }
         if (protocol.errno && protocol.errno != Errno.OK) {
            return protocol.errno
         }
      }

      protocol.errno = Errno.OK

      Date now = new Date()

      log.debug "trying to add product ${protocol.product} from user ${IngSystemUser.get(protocol.userId).name}"
      product = new IngProduct(
            name: protocol.product,
            contributor: IngSystemUser.get(protocol.userId).name,
            updated: now.getTime(),
            currentLock: currentLock.toString(),
            currentState: State.RESERVED.toString(),
            initialText: protocol.addMetadata,
            productType: productType,
            notify: protocol.notify,
            versionNumber: 1, // TODO hardcode the version for now
            note: "")

      if (!product.save(flush: true)) {
         product.errors.each {
            log.trace it
         }
      }
      log.trace("Product ${product.name} saved to ID ${product.id}, current lock ${currentLock} = ${currentLock.toString()}")

      Calendar cal = Calendar.instance
      cal.time = now

      boolean localStaged = true
      ServiceProfile profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(protocol.addMetadata)
      if (profile?.submission?.ingest?.isOperationSuccess()) {
         localStaged = true
      }

      boolean loop = false

      if (!localStaged) {
         log.debug("Processing product ${product.name}")

         // set the relative path for the files to be ingested
         String path =
            sprintf("%04d${File.separator}%02d${File.separator}%02d${File.separator}%d${File.separator}%s${File.separator}",
                  [cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                        product.id, protocol.product])

         loop = true
         while (loop) {
            loop = false
            try {
               product.refresh()
               product.localRelativePath = path
               product.remoteRelativePath = path
               product.save(flush: true)
            } catch (StaleObjectStateException e) {
                log.trace(e.message, e)
                loop = true
            } catch (OptimisticLockingFailureException e) {
               log.trace(e.message, e)
               loop = true
            }
         }

         log.trace "destination ${path}"

         def files = protocol.addFiles

         for (ProductFile file in files) {
            URIPath remotePath = URIPath.createURIPath(file.source)
            def remoteSystem = IngRemoteSystem.findByRootUri(remotePath.hostURI)

            // the contributing host must be registered
            if (!remoteSystem) {
               protocol.errno = Errno.UNREGISTERED_HOST
               protocol.description = "Remote host ${remotePath.hostURI} has not been approved."
               product.delete(flush: true)
               return protocol.errno
            }
            log.trace "add product file ${file.name} ${remoteSystem.rootUri} ${path}${file.name} ${file.size}."
            IngDataFile datafile = new IngDataFile(
                  name: remotePath.filename,
                  provider: remoteSystem,
                  remotePath: remotePath.path,
                  fileSize: file.size,
                  currentLock: Lock.ADD.toString()
            )

            if (remotePath.filename.toLowerCase().endsWith(".gzip") ||
                  remotePath.filename.toLowerCase().endsWith(".gz")) {
               datafile.compression = "GZIP"
            } else if (remotePath.filename.toLowerCase().endsWith(".bzip2")) {
               datafile.compression = "BZIP2"
            } else if (remotePath.filename.toLowerCase().endsWith(".zip")) {
               datafile.compression = "ZIP"
            } else {
               datafile.compression = "NONE"
            }
            datafile.save(flush: true)
            product.addToFiles(datafile)
         }
         // queuing the product to be ingested into a managed staging area
         product.currentState = State.PENDING.toString()
         product.updated = new Date().time
         if (!product.note) {
            product.note = ''
         }
         product.note = trimNote(product.note + "{${product.currentState}}: Ingestion request received.") + "\n"
      } else {
         log.debug("Processing locally staged product ${product.name}")

         List<ProductFile> files = protocol.addFiles
         String path

         for (ProductFile file in files) {
            IngDataFile idf = new IngDataFile(
                  name: file.name,
                  fileSize: file.size,
                  checksum: file.checksum,
                  remotePath: file.source,
                  currentLock: Lock.ADD.toString(),
                  product: product
            )

            path = URIPath.createURIPath(file.source).pathOnly

            if (file.name.toLowerCase().endsWith(".gzip") ||
                  file.name.toLowerCase().endsWith(".gz")) {
               idf.compression = "GZIP"
            } else if (file.name.toLowerCase().endsWith(".bzip2")) {
               idf.compression = "BZIP2"
            } else if (file.name.toLowerCase().endsWith(".zip")) {
               idf.compression = "ZIP"
            } else {
               idf.compression = "NONE"
            }

            loop = true
            while (loop) {
               loop = false
               try {
                  idf.save(flush: true, failOnError: true)
               } catch (StaleObjectStateException e) {
                   log.trace(e.message, e)
                   loop = true
               } catch (OptimisticLockingFailureException e) {
                  log.trace(e.message, e)
                  loop = true
               }
            }
            product.addToFiles(idf)
         }

         product.localRelativePath = path
         product.remoteRelativePath = path
         product.completeText = protocol.addMetadata
         product.completed = new Date().time
         product.currentState = State.LOCAL_STAGED.toString()

         product.updated = new Date().time
         if (!product.note) {
            product.note = ''
         }
         product.note = trimNote(product.note + "{${product.currentState}}: Archive request received.") + "\n"

      }

      protocol.productId = product.id
      protocol.description = "Product ${protocol.product} of type ${productType.name} has been registered for '${product.currentLock}' with current state in '${product.currentState}'."

      loop = true
      while (loop) {
         loop = false
         try {
            product.save(flush: true, failOnError: true)
            log.debug "Product "+product.name+" initiated to state "+product.currentState
         } catch (StaleObjectStateException e) {
             log.trace(e.message, e)
             loop = true
         } catch (OptimisticLockingFailureException e) {
            log.trace(e.message, e)
            loop = true
         }
      }

      return protocol.errno
   }

   def processDeletes() {
      def loop = true
      log.debug("Process deletes.")
      def criteria = IngProduct.createCriteria()
      def aborts = criteria.list {
         eq("currentLock", Lock.DELETE.toString())
         eq("currentState", State.ABORTED.toString())
         productType {
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(20)
         order("created", 'asc')
      }
      log.info("Found "+aborts.size()+" aborted granules marked for deletion.");
      if (aborts) {
         aborts.each { IngProduct product ->
            IngStorage storage = product.contributeStorage
            //TODO Reimplement size calculation/management (missing contributeStorage value in DB) 
            //long totalsize = product.files.sum { it.fileSize } as long

            def ids = product.files.collect {
               it.id
            }

            ids.each { id ->
               IngDataFile file = IngDataFile.get(id)
               file.product.removeFromFiles(file)
               loop = true
               while (loop) {
                  loop = false
                  try {
                     file?.refresh()
                     if (!file) break
                     
                     log.debug("Deleting file "+file.name+" from database")
                     file.delete(flush: true)
                  } catch (StaleObjectStateException e) {
                      log.trace(e.message, e)
                      loop = true
                  } catch (OptimisticLockingFailureException e) {
                     log.trace(e.message, e)
                     loop = true
                  }
               }
            }

            while (loop) {
               loop = false
               try {
                  //TODO Part of that storage implementation update mentioned above.... deleting row instead
                  //storageService.updateStorage(-totalsize, new Date().time, storage.location.id)
                  //product.currentLock = Lock.NONE.toString()
                  //product.note = trimNote(product.note + "Ingestion aborted.  Data file records haven been updated.") + "\n"
                  product.delete(flush: true)
               } catch (StaleObjectStateException e) {
                   log.trace(e.message, e)
                   loop = true
               } catch (OptimisticLockingFailureException e) {
                  log.trace(e.message, e)
                  loop = true
               }
            }
         }
      }
      
      // Process deletes in round-robin manner allowing up to 2 deletes per engine
      /*
      def storageList = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.INGEST.toString())
         }
      }
      for (IngStorage storage in storageList) {
      */
         criteria = IngProduct.createCriteria()
         def pendingDeletes = criteria.list {
            //eq("contributeStorage", storage)
            eq("currentLock", Lock.DELETE.toString())
            eq("currentState", State.ARCHIVED.toString())
            productType {
               federation {
                  eq("name", grailsApplication.config.manager_federation)
               }
               order('priority', 'asc')
            }
            maxResults(20)
            order("created", "asc")
         }
         log.info("Found "+pendingDeletes.size()+" products for deletion")
         if (!pendingDeletes || pendingDeletes.size() == 0) {
            return
         }

         pendingDeletes = pendingDeletes.findAll { IngProduct product ->
            (new Date().time - product.archivedAt) / (60 * 1000) >= product.productType.purgeRate
         }
         for (IngProduct product in pendingDeletes) {
		def process = true
        	while(process) {
                	try {
                        	product.lock()
				log.debug("${product.name}: has been lock for processDeletes")
                        	process = false
               		}catch (OptimisticLockingFailureException e){
				log.debug("${product.name}: has been locked by another thread, waitting to get lock..")
                        	product.refresh()
                	}
        	}
            if (Lock.valueOf(product.currentLock) != Lock.DELETE ||
                  State.valueOf(product.currentState) != State.ARCHIVED) {
               // the product's lock/state was updated since it was queried into memory.
               continue
            }
		
            def ids = product.files.collect {
               it.id
            }

            ids.each { id ->
               IngDataFile file = IngDataFile.get(id)
               file.product.removeFromFiles(file)
               loop = true
               while (loop) {
                  loop = false
                  try {
                     file?.refresh()
                     if (!file) break
                     log.debug("Deleting file "+file.name+" from database")
                     file.delete(flush: true)
                  } catch (StaleObjectStateException e) {
                      log.trace(e.message, e)
                      loop = true
                  } catch (OptimisticLockingFailureException e) {
                     log.trace(e.message, e)
                     loop = true
                  }
               }
            }
            loop = true
            while (loop) {
               loop = false
               try {
                  product.delete(flush: true)
               } catch (StaleObjectStateException e) {
                   log.trace(e.message, e)
                   loop = true
               } catch (OptimisticLockingFailureException e) {
                  log.trace(e.message, e)
                  loop = true
               }
            }
            //TODO Removing all code related to actually having to clean up ingest engine storage
            /*
            if (!storage.location.active) {
               log.debug("Cannot move product ${product.name} to trashbin because storage ${storage.name} is inactive")
               continue
            }
            // updated
            IngEngineJob engineJob = new IngEngineJob(
                  product: product,
                  operation: product.currentLock,
                  previousState: product.currentState,
                  assigned: new Date().getTime(),
                  priority: product.productType.jobPriority.getValue(),
                  contributeStorage: product.contributeStorage
            )
            engineJob.save(flush: true)

            if (!product.name.contains("_TRASH_")) {
               // Append _TRASH_ to product name so that the same granule can be submitted for replace
               def productId = product.id
               product.discard()
               product = IngProduct.lock(productId)
               product.name += "_TRASH_${product.id}"
               product.updated = new Date().getTime()
               product.save(flush: true)
            }

            // create the ingest request to be sent to the worker engine
            IngestProtocol protocol = IngestProtocol.createEngineMoveRequest(
                  Stereotype.INGEST,
                  product.productType.federation.name,
                  product.productType.federation.id,
                  product.productType.name,
                  product.productType.id,
                  product.name,
                  product.id,
                  "${product.name}.sip.xml",
                  product.archiveText,
                  product.contributeStorage.location.localPath + product.productType.relativePath + product.localRelativePath,
                  product.contributeStorage.location.localPath + product.productType.relativePath + ".trashbin/" + product.localRelativePath
            )
            log.debug("processDelete: " + product.contributeStorage.location.localPath + product.productType.relativePath + product.localRelativePath)

            protocol.jobId = engineJob.id

            String response = null
            ZkAccess zk
            String path = product.productType.name + "/" + product.name
            def watcher = { WatchedEvent event ->
               log.debug ("TransactionService:processDelete Watcher: received event " + event.type + " " + event.state)
            }
            try {
               zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String, watcher as Watcher)
               log.debug("ZK instance " + zk + " " + zk.getSessionInfo().get("id"))
               response = zk.addToIngestQueue(product.contributeStorage.name as String, protocol.toRequest() as String, product.productType.jobPriority as JobPriority, managerWatcher)
               log.debug("[SERVER RESP] ${response}")
            } catch (IOException e) {
               if (e.message.equals("WATCHER_NOT_SET")) {
                  if (managerWatcherService.handleZkWatcher(
                        path,
                        managerWatcher)) {
                     continue
                  } else {
                     response = path
                  }
               } else {
                  log.trace(e.message, e)
                  engineJob.delete(flush: true)
                  log.trace(e.message, e)
                  continue
               }
            }

            if (!response) {
               log.debug("Problem(s) during job assignment for moving product to trashbin: response from ZooKeeper is null.")
               engineJob.delete(flush: true)
            } else {
               //Update engine job with path to queue node
               updateIngEngineJob(engineJob, response)

               def productId = product.id
               product.discard()
               product = IngProduct.lock(productId)
               if (product.currentLock == Lock.DELETE.toString()) {
                  product.note = trimNote(product.note + 'INFO: Move request has been assigned.') + "\n"
                  log.trace("Done assigning product '${product.name}' for moving to trashbin.")
                  product.currentLock = Lock.PENDING_RESERVED.toString()
                  product.updated = new Date().getTime()
                  product.save(flush: true)
               } else {
                  product.discard()
               }
            }
            log.debug("Method for moving ${product.name} terminates.")
            */
            
         }
      //}
   }

   def processAdds() {
      def loop = true
      log.debug("Process adds")
      // Abort all pending products that have already exceeded
      // the number of retries

      def abortCriteria = IngProduct.createCriteria()
      def aborts = abortCriteria.list {
         'in'("currentLock", [Lock.ADD.toString(), Lock.REPLACE.toString()])
         'in'("currentState", [State.PENDING.toString(), State.PENDING_STORAGE.toString()])
         eq("currentRetries", 0)
         productType {
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(20)
         order("created", 'asc')
      }

      if (aborts) {
         aborts.each { IngProduct product ->
            def ids = product.files.collect {
               it.id
            }

            ids.each { id ->
               IngDataFile file = IngDataFile.get(id)
               file.currentLock = Lock.RESERVED.toString()
               loop = true
               while (loop) {
                  loop = false
                  try {
                     file?.refresh()
                     if (!file) break
                     file.save(flush: true)
                  } catch (StaleObjectStateException e) {
                      //log.trace(e.message, e)
                      loop = true
                  } catch (OptimisticLockingFailureException e) {
                     //file.discard()
                     loop = true
                  }
                  log.trace("Looping: 1108")
               }
            }

            loop = true
            while (loop) {
               loop = false
               try {
                  product.refresh()
                  product.currentLock = Lock.RESERVED.toString()
                  product.currentState = State.ABORTED.toString()
                  product.note = trimNote(product.note + "{${product.currentState}}: Ingestion exceeds maximum allowable attempts.  Abort ingestion.") + "\n"
                  product.save(flush: true)
                  log.debug "Product "+product.name+" moved to state "+product.currentState
                  sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to add", "{${product.currentState}}: Ingestion exceeds maximum allowable attempts. Abort ingestion.")
               } catch (OptimisticLockingFailureException e) {
                  loop = true
               }
               log.trace("Looping: 1125")
            }
         }
      }

      log.debug 'Done processing aborts... now query for pending jobs'
      def pendingCriteria = IngProduct.createCriteria()
      def pendingAdds = pendingCriteria.list {
         'in'("currentLock", [Lock.ADD.toString(), Lock.REPLACE.toString()])
         'in'("currentState", [State.PENDING.toString(), State.PENDING_STORAGE.toString()])
         and {
            gt("currentRetries", 0)
            le("currentRetries", 10)
         }
         productType {
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(20)
         order("created", 'asc')
      }

      if (!pendingAdds || pendingAdds.size() == 0) {
         return
      }

      // get a list of active engines
      def storages = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.INGEST.toString())
         }
      }

      if (storages.size() == 0) {
         log.debug("No storage found.")
         return
      }

      for (IngProduct product in pendingAdds) {
         log.trace("Locked product ${product.name}")
         if (![Lock.ADD, Lock.REPLACE].contains(Lock.valueOf(product.currentLock)) ||
               ![State.PENDING, State.PENDING_STORAGE].contains(State.valueOf(product.currentState))) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         long totalSize = product.files.sum {
            it.fileSize
         } as Long

         log.debug("totalSize ${totalSize}")

         def storageId = storageService.getStorage(totalSize, Stereotype.INGEST, product.productType.jobPriority.getValue())

         if (storageId == -1) {
            log.debug("Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.")
            loop = true
            while (loop) {
               loop = false
               try {
                  product.refresh()
                  if (State.valueOf(product.currentState) != State.PENDING_STORAGE) {
                     product.currentState = State.PENDING_STORAGE.toString()
                     product.note = trimNote(product.note + "{${product.currentState}}: Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.") + "\n"
                     product.updated = new Date().getTime()
                     product.save(flush: true)
                     log.debug "Product "+product.name+" moved to state "+product.currentState
                  }
               } catch (StaleObjectStateException e) {
                   //log.trace(e.message, e)
                   loop = true
               } catch (OptimisticLockingFailureException e) {
                  //product.discard()
                  loop = true
               }
               log.trace("Looping: 1195")
            }
            continue
         }

         def busycount = 0
         IngStorage storage = IngStorage.get(storageId)

         log.trace("Trying to lock storage ${storage.name}")

         // create the ingest request to be sent to the worker engine
         IngestProtocol protocol = IngestProtocol.createEngineIngestRequest(
               Stereotype.INGEST,
               product.productType.federation.name,
               product.productType.federation.id,
               product.productType.name,
               product.productType.id,
               product.name,
               product.id
         )

         // create the place holder to storage the engine response
         //IngestProtocol response = new IngestProtocol()

         // use a handler to extract file information from the metadata text
         SIPHandler sipHandler = new SIPHandler(product.initialText)

         // lookup login information to the provider host
         def productFiles = sipHandler.productFiles
         productFiles.each {
            URIPath path = URIPath.createURIPath(it.source)
            IngRemoteSystem host = IngRemoteSystem.findByRootUri(path.hostURI)
            if (host) {
               it.sourceUsername = host.username
               it.sourcePassword = host.password
               it.maxConnections = host.maxConnections
            }
            it.destination =
               storage.location.localPath + product.productType.relativePath + product.localRelativePath
            log.debug("Ingest source: " + it.source + ", destination: " + it.destination)
         }

         protocol.addFiles = productFiles
         IngEngineJob engineJob = moveProductState(product, storage, totalSize, true)
         protocol.jobId = engineJob.id

         String response = null
         ZkAccess zk = null
         String path = product.productType.name + "/" + product.name
         def watcher = { WatchedEvent event ->
            log.debug ("Transaction Manager:processAdd Watcher: received event " + event.type + " " + event.state)
         }
         try {
            zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String, watcher as Watcher)
            log.debug("ZK instance " + zk + " " + zk.getSessionInfo().get("id"))
            response = zk.addToIngestQueue(storage.name, protocol.toRequest(), product.productType.jobPriority, mw)
            log.debug("[ZOOKEEPER RESP] ${response}")
         } catch (IOException e) {
            if (e.message.equals("WATCHER_NOT_SET")) {
               if (managerWatcherService.handleZkWatcher(path, mw)) {
                  continue
               } else {
                  response = path
               }
            } else {
               log.debug(e.message, e)
               revertProductState(product, storage, engineJob, totalSize)
               continue
            }
         }

         if (!response) {
            log.debug("Problem(s) during ingestions: response from ZooKeeper is null.")
            revertProductState(product, storage, engineJob, totalSize)
            continue
         } else {
            //Update engine job with path to queue node
            updateIngEngineJob(engineJob, response)
         }
      }
   }


   def processReplacePurge() {
      def loop = true
      log.debug("Processing replace purge.")

      def criteria = IngProduct.createCriteria()
      def products = criteria.list {
         eq('currentLock', Lock.INVENTORY.toString())
         eq('currentState', State.PENDING_ARCHIVE.toString())
         productType {
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(20)
         order('created', 'asc')
      }

      if (!products || products.size() == 0) {
         return
      }

      log.trace "${products.size()} products to be purged from archive for replace."

      for (IngProduct product in products) {
         if (Lock.valueOf(product.currentLock) != Lock.INVENTORY ||
               State.valueOf(product.currentState) != State.PENDING_ARCHIVE) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         // get files to delete
         ServiceProfile serviceProfile =
            ServiceProfileFactory.instance.createServiceProfileFromMessage(product.archiveText)
         List<SPFileDestination> filesToDelete = serviceProfile?.submission?.archive?.deletes
         if (!filesToDelete) {
            filesToDelete = []
         }
         for (String fileToDelete : filesToDelete) {
            log.debug('file to delete: ' + fileToDelete)
         }

         def storageId = storageService.getStorageByTypeAndPriority(Stereotype.ARCHIVE, product.productType.jobPriority.getValue())
         
         if (storageId == -1) {
            log.debug("No archive storage found")
            continue
         }
         def storage = IngStorage.get(storageId)

         // updated
         IngEngineJob engineJob = new IngEngineJob(
               product: product,
               operation: product.currentLock,
               previousState: product.currentState,
               assigned: new Date().time,
               priority: product.productType.jobPriority.value,
               contributeStorage: storage
         )
         if (!engineJob.save(flush: true)) {
            engineJob.errors.each {
               log.error it
            }
         }
         log.trace("Product ${product.name} delete job ${engineJob.id}.")

         // create the ingest request to be sent to the worker engine
         IngestProtocol protocol = IngestProtocol.createEngineDeleteRequest(
               Stereotype.PURGE,
               product.productType.federation.name,
               product.productType.federation.id,
               product.productType.name,
               product.productType.id,
               product.name,
               product.id
         )
         protocol.jobId = engineJob.id

         def destinations = []
         if (!product.productType.localStaged) {
            filesToDelete.each { SPFileDestination file ->
               destinations << new URI(file.location)
            }
         } else {
            // process locally staged product
            log.debug ("Processing locally staged product deletion for replace")
            ServiceProfile profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(product.archiveText)
            if (profile?.submission?.archive) {
               profile.submission.archive.deletes.each { SPFileDestination fd ->
                  destinations << new URI(fd.location).getPath()
               }
            }
            if (log.debugEnabled) {
               destinations.each {uri ->
                  log.debug ("${uri} will be deleted.")
               }
            }
         }
         protocol.deletes = destinations

         String response = null
         ZkAccess zk = null
         String path = product.productType.name + "/" + product.name
         def watcher = { WatchedEvent event ->
            log.debug ("TransactionService processReplacePurge Watcher: received event " + event.type + " " + event.state)
         }
         try {
            zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String, watcher as Watcher)
            log.debug("ZK instance " + zk + " " + zk.sessionInfo.get("id"))
            response = zk.addToArchiveQueue(
                  storage.name as String,
                  protocol.toRequest() as String,
                  product.productType.jobPriority as JobPriority,
                  managerWatcher)
            log.debug("[SERVER RESP] ${response}")
         } catch (IOException e) {
            if (e.message.equals("WATCHER_NOT_SET")) {
               if (managerWatcherService.handleZkWatcher(path, managerWatcher)) {
                  continue
               } else {
                  response = path
               }
            } else {
               log.trace(e.message, e)
               log.debug("Deleting job at path "+engineJob.path+" from the database")
               engineJob.delete(flush: true)
               log.trace(e.message, e)
               continue
            }
         }

         if (!response) {
            log.debug("Deleting job "+engineJob.path+" from database due to no response from ZK")
            engineJob.delete(flush: true)

            loop = true
            while (loop) {
               loop = false
               try {
                  log.trace("Delete job ${engineJob.id} for product ${product.name}.")
                  product.refresh()
                  product.note = trimNote(product.note + "ERROR: Problem(s) during deletion: response from ZooKeeper is null.") + "\n"
                  product.updated = new Date().time
                  product.save(flush: true)
               } catch (StaleObjectStateException e) {
                   log.trace(e.message, e)
                   loop = true
               } catch (OptimisticLockingFailureException e) {
                  log.trace(e.message, e)
                  loop = true
               }
            }
         } else {
            //Update engine job with path to queue node
            updateIngEngineJob(engineJob, response)

            def productId = product.id
            product.discard()
            product = IngProduct.lock(productId)
            if (product.currentLock == Lock.INVENTORY.toString()) {
               product.currentLock = Lock.PENDING_RESERVED.toString()
               product.note = trimNote(product.note + "INFO: Product '${product.name}' will be purged for replace.") + "\n"
               product.updated = new Date().time
               product.save(flush: true)
            } else {
               product.discard()
            }
         }
      }
   }

   def processArchives() {
      Date methodStart = new Date()


      def loop = true
      log.debug("Process archives")
      // Abort all pending products that have already exceeded
      // the number of retries

      def abortCriteria = IngProduct.createCriteria()
      def aborts = abortCriteria.list {
         'in'("currentLock", [Lock.ARCHIVE.toString()])
         'in'("currentState", [State.PENDING_ARCHIVE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
         eq("currentRetries", 0)
         productType {
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(20)
         order("created", 'asc')
      }

      if (aborts) {
         aborts.each { IngProduct product ->
            loop = true
            while (loop) {
               loop = false
               try {
                  product.refresh()
                  product.currentLock = Lock.RESERVED.toString()
                  product.currentState = State.ABORTED.toString()
                  product.note = trimNote(product.note + "{${product.currentState}}: Ingestion exceeds maximum allowable attempts.  Abort ingestion.") + "\n"
                  log.debug "Product "+product.name+" moved to state "+product.currentState
                  def ids = product.files.collect {
                     it.id
                  }

                  ids.each { id ->
                     IngDataFile file = IngDataFile.get(id)
                     file.currentLock = Lock.RESERVED.toString()
                     file.save(flush: true)
                  }

                  product.save(flush: true)
                  log.debug "Product "+product.name+" moved to state "+product.currentState
                  sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to archive", "{${product.currentState}}: Ingestion exceeds maximum allowable attempts.  Abort ingestion.")
               } catch (OptimisticLockingFailureException e) {
                  loop = true
               }
            }
         }
      }

      log.trace 'Done processing aborts... now query for pending archive jobs'
      def pendingCriteria = IngProduct.createCriteria()
      def pendingArchives = pendingCriteria.list {
         'in'("currentLock", [Lock.ARCHIVE.toString()])
         'in'("currentState", [State.PENDING_ARCHIVE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
         and {
            gt("currentRetries", 0)
            le("currentRetries", 10)
         }
         productType {
            federation {
               eq("name", grailsApplication.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(20)
         order("created", 'asc')
      }

      if (!pendingArchives || pendingArchives.size() == 0) {
         log.debug("processArchives: returning!")
         return
      }
      log.debug("processArchives: products=" + pendingArchives.size())

      // get a list of active engines
      //def engines = IngEngine.findAllByActive(true)
      def storageList = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.ARCHIVE.toString())
         }
      }

      if (storageList.size() == 0) {
         log.debug("No active storage found.")
         return
      }

      for (IngProduct product in pendingArchives) {
         log.trace("Trying to lock product ${product.name}")
         if (![Lock.ARCHIVE].contains(Lock.valueOf(product.currentLock)) ||
               ![State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].contains(State.valueOf(product.currentState))) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         long totalSize = product.files.sum { it.fileSize } as long
         log.debug("totalSize ${totalSize}")

         def storageId = storageService.getStorage(totalSize, Stereotype.ARCHIVE, product.productType.jobPriority.value)

         if (storageId == -1) {
            log.debug("Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.")
            def process = true
            while(process) {
                try {
                    product.lock()
                    log.debug("${product.name}: has been lock for processArchives")
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
                  if (State.valueOf(product.currentState) != State.PENDING_ARCHIVE_STORAGE) {
                     product.currentState = State.PENDING_ARCHIVE_STORAGE.toString()
                     product.note = trimNote(product.note + "{${product.currentState}}: Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.") + "\n"
                     product.updated = new Date().getTime()
                     product.save(flush: true)
                     log.debug "Product "+product.name+" moved to state "+product.currentState
                  }
               } catch (StaleObjectStateException e) {
                   log.trace(e.message, e)
                   loop = true
               } catch (OptimisticLockingFailureException e) {
                  log.trace(e.message, e)
                  loop = true
               }
            }

            continue
         }

         def busycount = 0
         IngStorage storage = IngStorage.get(storageId)

         // create the ingest request to be sent to the worker engine
         IngestProtocol protocol = IngestProtocol.createEngineIngestRequest(
               Stereotype.ARCHIVE,
               product.productType.federation.name,
               product.productType.federation.id,
               product.productType.name,
               product.productType.id,
               product.name,
               product.id
         )

         def productFiles = []

         if (!product.productType.localStaged) {
            URIPath productPath = URIPath.createURIPath(
                  inventoryService.getProductArchivePath(product.inventoryId))
            if (!productPath) {
               // Unable to get granule path from inventory service
               log.error("Unable to get granule archive path for " + product.name + " from inventory service")
               break
            }
            product.files.each {

               def pf = new ProductFile(
                     name: it.name,
                     source: product.contributeStorage.location.remoteAccessProtocol.toLowerCase() +
                           "://" + product.contributeStorage.location.hostname + "/" + product.contributeStorage.location.remotePath +
                           product.productType.relativePath + product.remoteRelativePath + it.name,
                     checksum: it.checksum,
                     size: it.fileSize
               )

               productFiles += pf
               IngRemoteSystem host = IngRemoteSystem.findByRootUri(
                     product.contributeStorage.location.remoteAccessProtocol.toLowerCase() +
                           "://" + product.contributeStorage.location.hostname)
               if (host) {
                  pf.sourceUsername = host.username
                  pf.sourcePassword = host.password
                  pf.maxConnections = host.maxConnections
               }
               pf.destination = productPath.getPath()
               log.debug("archive name: " + pf.name + ", source: " + pf.source + ", destination: " + pf.destination + ", checksum: " + pf.checksum)
            }
         } else {
            // process locally staged data
            log.debug ("Processing locally staged data for archival.")
            ServiceProfile profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(product.archiveText)
            if (profile?.submission?.archive) {
               profile.submission.archive.ingestProductFiles.each { SPIngestProductFile ipf ->
                  URIPath up = URIPath.createURIPath(ipf.fileDestination.location)

                  List<String> convertedLinks = convertToPaths(ipf.fileDestination.links)
                  def pf = new ProductFile(
                        name: ipf.productFile.file.name,
                        source: ipf.productFile.file.links.find {
                           it.startsWith("file://")
                        } as String,
                        destination: up.pathOnly,
                        links: convertedLinks,
                        checksum: ipf.productFile.file.checksumValue,
                        size: ipf.productFile.file.size
                  )
                  //TODO here
                  productFiles += pf
                  log.debug("archive name: " + pf.name + ", source: " + pf.source + ", destination: " + pf.destination + ", checksum: " + pf.checksum)
               }
            }
         }

         // check
         protocol.addFiles = productFiles

         IngEngineJob engineJob = moveProductState(product, storage, totalSize, true)
         protocol.jobId = engineJob.id

         String response = null
         ZkAccess zk = null
         String path = product.productType.name + "/" + product.name
         def watcher = { WatchedEvent event ->
            log.debug ("TransactionService processArchives Watcher: received event " + event.type + " " + event.state)
         }
         try {
            log.trace("\tprocessArchives: sending request")
            zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String, watcher as Watcher)
            log.debug("ZK instance " + zk + " " + zk.getSessionInfo().get("id"))
            response = zk.addToArchiveQueue(storage.name, protocol.toRequest(), product.productType.jobPriority, managerWatcher)
            log.debug("[SERVER RESP] ${response}")
         } catch (IOException e) {
            if (e.message.equals("WATCHER_NOT_SET")) {
               if (managerWatcherService.handleZkWatcher(path, managerWatcher)) {
                  continue
               } else {
                  response = path
               }
            } else {
               log.trace("\tprocessArchives: sending request failed")
               log.trace(e.message, e)
               revertProductState(product, storage, engineJob, totalSize)
               continue
            }
         }
         log.debug("\tprocessArchives: sending request succeeded")

         if (!response) {
            log.trace("\tprocessArchives: response error")

            log.debug("Problem(s) during ingestions: response from ZooKeeper is null.")
            revertProductState(product, storage, engineJob, totalSize)

            continue
         } else {
            //Update engine job with path to queue node
            updateIngEngineJob(engineJob, response)
         }
         log.trace("\tprocessArchives: out of loop busycount=" + busycount)
      }

      log.trace("segment method: " + (new Date().getTime() - methodStart.getTime()) + " sec.");
   }

   def processPurge() {
      def loop = true
      log.debug("Processing purge.")

      // Process purges in round-robin manner allowing up to 2 deletes per engine
      def storageList = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.INGEST.toString())
         }
      }
      for (IngStorage storage in storageList) {
         def criteria = IngProduct.createCriteria()
         def products = criteria.list {
            eq('contributeStorage', storage)
            eq('currentLock', Lock.TRASH.toString())
            eq('currentState', State.ARCHIVED.toString())
            productType {
               federation {
                  eq("name", grailsApplication.config.manager_federation)
               }
               order('priority', 'asc')
            }
            maxResults(5)
            order('created', 'asc')
         }

         if (!products || products.size() == 0) {
            continue
         }

         products = products.findAll { IngProduct product ->
            (new Date().time - product.archivedAt) / (60 * 1000) >= product.productType.purgeRate
         }

         if (!products || products.size() == 0) {
            log.trace 'No products to be trashed.'
            continue
         }

         log.trace "${products.size()} products to be purged from trashbin."

         for (IngProduct product in products) {
            if (Lock.valueOf(product.currentLock) != Lock.TRASH ||
                  State.valueOf(product.currentState) != State.ARCHIVED) {
               // the product's lock/state was updated since it was queried into memory.
               continue
            }

            if (!storage.location.active) {
               log.debug("Cannot purge product ${product.name} from trashbin because storage ${product.contributeStorage.name} is inactive")
               continue
            }

            if (product.files.size() == 0) {
               log.info("Nothing needs to be purged for product '${product.name}'.  Purge product record.")
               product.delete(flush: true)
               continue
            }

            long totalSize = product.files.sum { IngDataFile file ->
               file.fileSize
            }

            IngEngineJob engineJob = new IngEngineJob(
                  product: product,
                  operation: product.currentLock,
                  previousState: product.currentState,
                  assigned: new Date().getTime(),
                  priority: product.productType.jobPriority.getValue(),
                  contributeStorage: storage
            )
            engineJob.save(flush: true)

            log.debug("Product ${product.name} delete job ${engineJob.id}.")

            // create the ingest request to be sent to the worker engine
            IngestProtocol protocol = IngestProtocol.createEngineDeleteRequest(
                  Stereotype.INGEST,
                  product.productType.federation.name,
                  product.productType.federation.id,
                  product.productType.name,
                  product.productType.id,
                  product.name,
                  product.id
            )
            protocol.totalSize = totalSize
            protocol.jobId = engineJob.id

            def destinations = []
            product.files.each { file ->
               destinations << storage.location.localPath +
                     product.productType.relativePath +
                     product.localRelativePath +
                     file.name
            }

            // also delete the backup SIP/AIP message
            destinations << storage.location.localPath +
                  product.productType.relativePath +
                  product.localRelativePath + product.name + ".sip.xml"

            protocol.deletes = destinations

            String response = null
            ZkAccess zk = null
            String path = product.productType.name + "/" + product.name
            def watcher = { WatchedEvent event ->
               log.debug ("TransactionService:processPurage Watcher: received event " + event.type + " " + event.state)
            }
            try {
               zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String, watcher as Watcher)
               log.debug("ZK instance " + zk + " " + zk.sessionInfo.get("id"))
               response = zk.addToIngestQueue(product.contributeStorage.name, protocol.toRequest(), product.productType.jobPriority, managerWatcher)
               log.debug("[SERVER RESP] ${response}")
            } catch (IOException e) {
               if (e.message.equals("WATCHER_NOT_SET")) {
                  if (managerWatcherService.handleZkWatcher(path, managerWatcher)) {
                     continue
                  } else {
                     response = path
                  }
               } else {
                  log.debug("From process purge: "+e.message, e)
                  engineJob.delete(flush: true)
                  continue
               }
            }

            if (!response) {
               engineJob.delete(flush: true)
               def process = true
               while(process) {
                   try {
                       product.lock()
                       log.debug("${product.name}: has been lock for processPurge")
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
                     product.note = trimNote(product.note + "ERROR: Problem(s) during deletion: response from ZooKeeper is null.") + "\n"
                     product.updated = new Date().getTime()
                     product.save(flush: true)
                  } catch (StaleObjectStateException e) {
                      //log.trace(e.message, e)
                      loop = true
                  } catch (OptimisticLockingFailureException e) {
                     loop = true
                  }
               }
            } else {
               //Update engine job with path to queue node
               updateIngEngineJob(engineJob, response)

               def productId = product.id
               product.discard()
               product = IngProduct.lock(productId)
               if (product.currentLock == Lock.TRASH.toString()) {
                  product.currentLock = Lock.PENDING_RESERVED.toString()
                  product.note = trimNote(product.note + "INFO: Product '${product.name}' will be purged.") + "\n"
                  product.updated = new Date().getTime()
                  product.save(flush: true)
               } else {
                  product.discard()
               }
            }
         }
      }
   }

   /**
    * Method to check for active jobs that has been active for more than 30 minutes.  It will
    * send a query to the assigned engine to see the job is still being handled.
    */
   def checkActiveJob() {
      Date cutoff = new Date(new Date().time - 1800000L) // cutoff time: 30 mins ago

      // only check the jobs that were assigned more than 30 minutes ago
      def firstIter = true
      def jobs = null
      int offset = 0
      int maxPerPage = 10
      // Page through all the stuck jobs otherwise we might miss the next 10
      while (firstIter || jobs.size() >= maxPerPage) {
         jobs = IngEngineJob.createCriteria().list {
            lt("assigned", cutoff.getTime())
            product {
               productType {
                  federation {
                     eq("name", grailsApplication.config.manager_federation)
                  }
               }
            }
            maxResults(maxPerPage)
            firstResult(offset)
            order("assigned", "asc")
         }
         
         log.info("Found "+jobs.size()+" jobs stuck longer than 30 mins")
         firstIter = false
         offset += maxPerPage

         if (!jobs || jobs.size() == 0) return

         def loop = true

         for (IngEngineJob job in jobs) {
            IngProduct product = job.product

            ZkAccess zk = null
            def watcher = { WatchedEvent event ->
               log.debug ("TransactionService:checkActiveJob Watcher: received event " + event.type + " " + event.state)
            }
            try {
               zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String, watcher as Watcher)
               log.debug("ZK instance " + zk + " " + zk.getSessionInfo().get("id"))
               Boolean stillInQueue
               try {
                stillInQueue = zk.nodeExists(job.path);
               }
               catch(Exception e) {
                  log.warn("Problem occured when reading job ZK path, attempting to resolve...")
                  stillInQueue = false
               }
               if (job.path != null && !job.path.equals("") && stillInQueue) {
                  //Fire sigevent
/*                  sigEventService.send(
                        product.productType.eventCategory.name,
                        EventType.Warn,
                        "Job with id=" + job.id + " has been stuck for more than 1800000 ms. Check that engine is registered to process queue node at " + job.path,
                        "Job with id=" + job.id + " has been stuck for more than 1800000 ms. Check that engine is registered to process queue node at " + job.path
                  )*/
                   //Removing sigevent here so that it doesn't spam the logs. 
                  log.debug("Node " + job.path + " for " + product.name + " is still in zk queue... no action taken.")
               } else {
                  String path = product.productType.name + "/" + product.name
                  log.debug("Node " + job.path + " for " + product.name + " has been removed from queue. Checking job node " + path)
                  if (zk.processNodeExists(path)) {
                     String s = zk.readProcessNode(path, null)
                     try {
                        def xml = new XmlSlurper().parseText(s)
                        managerWatcherService.handleZkWatcher(path, null)
                     } catch (SAXParseException ex) {
                        log.trace(ex.message, ex)
                        log.debug("Job node " + path + " contains " + s)
                        String[] nodes = s.split(",")
                        // check if engine is offline
                        if (zk.checkEngineRegistration(nodes[1], nodes[0]) == RegistrationStatus.OFFLINE) {
                           // delete job node from zk
                           zk.removeProcessNode(path)

                           String previousState = job.previousState
                           String previousLock = job.operation

                           Lock previousLockEnum = Lock.valueOf(previousLock)
                           State previousStateEnum = State.valueOf(previousState)
                           // put storage back as needed
                           if ([Lock.ADD, Lock.REPLACE, Lock.ARCHIVE].find { it == Lock.valueOf(job.operation) }
                                 && [State.PENDING, State.PENDING_STORAGE, State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].find { it == State.valueOf(job.previousState) }) {
                              long totalSize = product.files.sum { it.fileSize } as long
                              storageService.updateStorage(-totalSize, new Date().time, job.contributeStorage.id)
                           }

                           // delete the job from the active job list
                           loop = true
                           while (loop) {
                              loop = false
                              try {
                                 job.refresh()
                                 if (!job) break
                                 job.delete(flush: true)
                              } catch (StaleObjectStateException e) {
                                  log.trace(e.message, e)
                                  loop = true
                              } catch (OptimisticLockingFailureException e) {
                                 log.trace(e.message, e)
                                 loop = true
                              }
                           }

                           if (State.valueOf(product.currentState) == State.PENDING_ASSIGNED || Lock.valueOf(product.currentLock) == Lock.PENDING_RESERVED) {
                               def process = true
                               while(process) {
                                   try {
                                       product.lock()
                                       log.debug("${product.name}: has been lock for checkActiveJob")
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
                                    log.trace("reset product state: ${previousState} lock: ${previousLock}")
                                    product.currentState = previousState
                                    product.currentLock = previousLock
                                    ++product.currentRetries
                                    product.save(flush: true)
                                    log.debug "Product "+product.name+" moved to state "+product.currentState
                                    sigEventService.send(
                                        product.productType.eventCategory.name,
                                        EventType.Warn,
                                        "Job for product " + product.name + " has been stuck for more then 30 mins and moved back to the its previous state/lock (" + product.currentState + "/" + product.currentLock + ") for zookeeper requeue.",
                                        "Job for product " + product.name + " has been stuck for more then 30 mins and moved back to the its previous state/lock (" + product.currentState + "/" + product.currentLock + ") for zookeeper requeue."
                                    )
                                 } catch (StaleObjectStateException e) {
                                     log.trace(e.message, e)
                                     loop = true
                                 } catch (OptimisticLockingFailureException e) {
                                    log.trace(e.message, e)
                                    loop = true
                                 }
                              }
                           }
                        } else {
                           //Fire sigevent
                           sigEventService.send(
                                 product.productType.eventCategory.name,
                                 EventType.Warn,
                                 "Job with id=" + job.id + " has been stuck for more than 1800000 ms. Check that engine (${nodes[0]}) registered with storage (${nodes[1]}) is still processing job",
                                 "Job with id=" + job.id + " has been stuck for more than 1800000 ms. Check that engine (${nodes[0]}) registered with storage (${nodes[1]}) is still processing job"
                           )
                        }
                     }
                  } else {
                     log.debug("Job node " + path + " does not exist")
                     // check if job still exists
                     boolean revert = true
                     int retries = 0
                     while (retries <= 5) {
                        try {
                           job.refresh()
                           log.debug("[checkActiveJobs] Engine job ${job.id} still exists sleeping for 3 seconds")
                           if (retries < 5) {
                              Thread.sleep(3000)
                           }
                        } catch (HibernateObjectRetrievalFailureException e) {
                           revert = false
                           log.debug("[checkActiveJobs] Engine job ${job.id} deleted")
                           break
                        }
                        retries++
                     }
                     if (revert) {
                        log.debug("[checkActiveJobs] Reverting product state. Engine job ${job.id}")
                        String previousState = job.previousState
                        String previousLock = job.operation

                        Lock previousLockEnum = Lock.valueOf(previousLock)
                        State previousStateEnum = State.valueOf(previousState)
                        // put storage back as needed
                        if ([Lock.ADD, Lock.REPLACE, Lock.ARCHIVE].find { it == Lock.valueOf(job.operation) }
                              && [State.PENDING, State.PENDING_STORAGE, State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].find { it == State.valueOf(job.previousState as String) }) {
                           long totalSize = product.files.sum { it.fileSize } as long
                           storageService.updateStorage(-totalSize, new Date().time, job.contributeStorage.id)
                        }

                        // delete the job from the active job list
                        loop = true
                        while (loop) {
                           loop = false
                           try {
                              job.refresh()
                              if (!job) break
                              job.delete(flush: true)
                           } catch (StaleObjectStateException e) {
                               log.trace(e.message, e)
                               loop = true
                           } catch (OptimisticLockingFailureException e) {
                              log.trace(e.message, e)
                              loop = true
                           }
                        }

                        if (State.valueOf(product.currentState) == State.PENDING_ASSIGNED || Lock.valueOf(product.currentLock) == Lock.PENDING_RESERVED) {
                           loop = true
                           while (loop) {
                               def process = true
                               while(process) {
                                   try {
                                       product.lock()
                                       log.debug("${product.name}: has been lock for checkActiveJob")
                                       process = false
                                   }catch (OptimisticLockingFailureException e){
                                       log.debug("${product.name}: has been locked by another thread, waitting to get lock..")
                                       product.refresh()
                                   }
                      
                               }
                              loop = false
                              try {
                                 //product.refresh()
                                 log.trace("reset product state: ${previousState} lock: ${previousLock}")
                                 product.currentState = previousState
                                 product.currentLock = previousLock
                                 ++product.currentRetries
                                 product.save(flush: true)
                                 log.debug "Product "+product.name+" moved to state "+product.currentState
                                 sigEventService.send(
                                     product.productType.eventCategory.name,
                                     EventType.Warn,
                                     "Job for product " + product.name + " has been stuck for more then 30 mins and moved back to the its previous state/lock (" + product.currentState + "/" + product.currentLock + ") for zookeeper requeue.",
                                     "Job for product " + product.name + " has been stuck for more then 30 mins and moved back to the its previous state/lock (" + product.currentState + "/" + product.currentLock + ") for zookeeper requeue."
                                 )
                              } catch (StaleObjectStateException e) {
                                  log.trace(e.message, e)
                                  loop = true
                              } catch (OptimisticLockingFailureException e) {
                                 log.trace(e.message, e)
                                 loop = true
                              }
                           }
                        }
                     }
                  }
               }
            } catch (IOException e) {
               log.error(e.message, e)
            }
         }
      }
   }


   def pingInventory() {
      if (inventoryService.ping()) {
         log.debug("Inventory service is online")
      } else {
         log.debug("Inventory service is offline")
         sigEventService.send(
               SIG_EVENT_HORIZON_CATEGORY,
               EventType.Warn,
               "Inventory service at ${grailsApplication.config.gov.nasa.horizon.inventory.host} seeems to be down",
               "Inventory service at ${grailsApplication.config.gov.nasa.horizon.inventory.host} seeems to be down"
         )
      }
   }

   /**
    * Reverts product current state. Puts back storage space used that has been allocated.
    * Deletes specified IngEngineJob. This method is used for error handling e.g. when manager
    * cannot communicate with zookeeper.
    */
   private void revertProductState(IngProduct product, IngStorage storage, IngEngineJob engineJob, long totalSize) {
      def productId = product.id
      product.discard()
      product = IngProduct.lock(productId)
      product.currentState = engineJob.previousState
      product.updated = new Date().time
      product.save(flush: true)
      log.debug "Product "+product.name+" moved to state "+product.currentState
      storageService.updateStorage(-totalSize, new Date().time, storage.location.id)

      engineJob.delete(flush: true)
   }

   /**
    * Returns IngEngineJob that has been written to db.  Updates storage space used
    * and sets product's contribute storage.
    */
   private IngEngineJob moveProductState(IngProduct product, IngStorage storage, long totalSize, boolean setContributeStorage) {
      IngEngineJob engineJob = new IngEngineJob(
            product: product,
            operation: product.currentLock,
            previousState: product.currentState,
            assigned: new Date().getTime(),
            priority: product.productType.jobPriority.getValue(),
            contributeStorage: storage
      )

      if (!engineJob.save(flush: true)) {
         engineJob.errors.each {
            log.error it
         }
      }

      def productId = product.id
      product.discard()
      product = IngProduct.lock(productId)
      --product.currentRetries
      if (setContributeStorage) {
         product.contributeStorage = storage
      }
      product.currentState = State.PENDING_ASSIGNED.toString()
      product.note = trimNote(product.note + "{${product.currentState}}: Done sending product ${product.name} to ZooKeeper for ingestion.") + "\n"
      product.updated = new Date().getTime()
      product.save(flush: true)
      log.debug "Product "+product.name+" moved to state "+product.currentState
      storageService.updateStorage(totalSize, new Date().time, storage.location.id)
      return engineJob
   }

   /**
    * Updates specified IngEngineJob with specified zookeeper path.
    */
   private void updateIngEngineJob(IngEngineJob engineJob, String path) {
      try {
         def engineJobId = engineJob.id
         engineJob.discard()
         engineJob = IngEngineJob.lock(engineJobId)
         engineJob.path = path
         engineJob.save(flush: true)
      } catch (Exception e) {
         log.debug(e.message, e)
         log.debug("Unable to save engine job's path to queue node")
      }
   }

   /**
    * Message to trim input string according to the max note length
    * @param note input string
    * @return trimmed string
    */
   String trimNote(String note) {
      def buf = new StringBuffer(note)
      buf.length = MAX_NOTE
      return buf.toString().trim()
   }
   
   /**
    * Convert list of url string to URIPath type
    */
   private List<String> convertToPaths(List<String> links) {
      links.each { link ->
         link = URIPath.createURIPath(link).pathOnly
      }
   }
}
