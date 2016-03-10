package gov.nasa.horizon.ingest.server

import gov.nasa.horizon.common.api.file.FileProduct
import gov.nasa.horizon.common.api.util.ChecksumUtility
import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm
import gov.nasa.horizon.common.api.util.Console
import gov.nasa.horizon.common.api.util.FileProductUtility
import gov.nasa.horizon.common.api.util.URIPath
import gov.nasa.horizon.common.api.zookeeper.api.ZkAccess
import gov.nasa.horizon.common.api.zookeeper.api.ZkFactory
import gov.nasa.horizon.common.api.zookeeper.api.constants.RegistrationStatus
import gov.nasa.horizon.ingest.api.*
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import gov.nasa.horizon.sigevent.api.EventType
import gov.nasa.horizon.sigevent.api.SigEvent
import gov.nasa.horizon.common.util.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.xml.sax.SAXException

import javax.xml.bind.JAXBException
import java.util.concurrent.Semaphore
import java.nio.file.*

class IngestServer {

   ZkAccess zk

   Domain domain
   Keychain keychain
   String domainFile = System.getProperty(Constants.PROP_DOMAIN_FILE)
   String registry

   String username
   String password
   String federation

   String serverName
   String registryUrl
   String zooKeeperUrl
   Stereotype stereotype
   String storageName
   int maxWaitTime = 10000
   boolean started = false
   String sigEventUrl

   long serverId
   long serverSessionId
   String sessionToken

   IngestProtocol serverConfig = new IngestProtocol()


   static String configFile = null

   static Integer defaultMaxUsrCon = 30
   static Integer defaultMaxAdminCon = 2

   private static final String LOCAL_HOST_NAME = InetAddress.localHost.hostName
   private static final String SIG_EVENT_CATEGORY = "UNCATEGORIZED"

   static Integer maxUsrCon
   static Integer maxAdminCon

   static Log log = LogFactory.getLog(IngestServer.class)

   private static final INSTANCE = new IngestServer()

   static getInstance() { return INSTANCE }

   private IngestServer() {}

   private void deleteFiles(files) {
      files.each {
         File f = new File(it)
         if (f.exists()) {
            f.delete()
         }
      }
   }

   void activate(args) throws IOException {
      addShutdownHook() {
         try {
            if (ServerMonitor.instance.serverInShutdown) {
               int remaingTrans = ServerMonitor.instance.startShutdown(0)
               log.warn "Server ${serverName} terminated ${remaingTrans} transactions."

               // TODO add sigevent dispatch code here
            }

            if (started) {
               // shutdown notification to sigevent only if engine has been started
               sendSigEvent(
                     EventType.Info,
                     "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been shutdown.",
                     "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been shutdown."
               )
            }
         } catch (InterruptedException e) {
            log.warn(e)
         }
      }

      Thread.currentThread().name = "HORIZON_INGEST"

      log.info Constants.COPYRIGHT
      log.info(Constants.SERVER_VERSION_STR + "\n")

      parseArgs(args)
      configure()

      try {
         readConfig()
      } catch (IOException e) {
         log.error("Unable to connect to federation '${federation}' registry '${registryUrl}'.")
         return
      }

      /*
      if (!password) {
         password = Encrypt.encrypt(Console.readPassword(">> "))
      }
      */

      ServerMonitor.instance.userListener = new ServerPortListener(
            "user")

      // map to track number of available locks for each URL
      def locks = [:]

      ServerMonitor.instance.userListener.add = { IngestProtocol request ->
         String jobPath = request.productType + "/" + request.product
         zk.createProcessNode(jobPath, "${serverName},${storageName},processing add job.")
         def productFiles = request.addFiles

         if (!request.description) {
            request.description = ""
         }

         Errno errno = Errno.OK
         Date startTime = new Date()

         List savedFiles = []

         for (ProductFile productFile in productFiles) {
            log.debug("Retrieving file ${productFile.name}")
            URIPath path = URIPath.createURIPath(productFile.source)
            Semaphore lock
            synchronized (locks) {
               lock = locks.get(path.hostPath) as Semaphore
               if (!lock) {
                  lock = new Semaphore(productFile.maxConnections, true)
                  locks[path.hostPath] = lock
               }
            }
            lock.acquire()

            productFile.description = ''
            String message

            // try local file access first
            log.debug("Trying to fetch source file on local file system: " + "file://" + path.getPath())
            FileProductUtility fpu = new FileProductUtility("file://" + path.getPath())
            FileProduct fileProduct = fpu.getFile()
            boolean foundLocally = false
            if (fileProduct.exists()) {
               if (productFile.checksum) {
                  File localFile = new File(path.getPath())
                  String checksum = ChecksumUtility.getDigest(DigestAlgorithm.MD5, localFile)
                  if (productFile.checksum.equalsIgnoreCase(checksum)) {
                     foundLocally = true
                     log.debug("File found on local file system and checksum matched!")
                  }
               }
            }
            if (!foundLocally) {
               log.debug(
                     "File not found or found but checksum missing or not matched. " +
                           "Trying as specified: " + productFile.source
               )

               fileProduct.close()
               fpu.cleanUp()
               fpu = new FileProductUtility(productFile.source)
               fpu.setAuthentication(productFile.sourceUsername, productFile.sourcePassword)
               try {
                  fileProduct = fpu.getFile()
               } catch (Exception e) {
                  errno = Errno.INGEST_ERR
                  message = "Unable to retrieve file ${productFile.name} from ${productFile.source}."
                  log.error(message, e)
                  productFile.description += message
                  request.description += message + "\n"
                  fileProduct.close()
                  fpu.cleanUp()
                  lock.release()
                  break
               }
            }
            /*
            FileProductUtility fpu = new FileProductUtility(productFile.source)
            fpu.setAuthentication(productFile.sourceUsername, productFile.sourcePassword)
            FileProduct fileProduct = fpu.getFile()
            */

            if (!fileProduct.exists()) {
               errno = Errno.INGEST_ERR
               message = "Unable to retrieve file ${productFile.name} from ${productFile.source}."
               log.error(message)
               productFile.description += message
               request.description += message + "\n"
               fileProduct.close()
               fpu.cleanUp()
               lock.release()
               break
            } else {
               URIPath destination = URIPath.createURIPath(productFile.destination)
               File des = new File(destination.path)

               log.debug("${productFile.name} expected size=${productFile.size}, actual size=${fileProduct.size}")
               if (productFile.size != fileProduct.getSize()) {
                  log.debug("File size mismatched")
                  errno = Errno.INGEST_ERR
                  message = "${productFile.name}:  Size mismatched - expected ${productFile.size}, actual ${fileProduct.size}."
                  log.error(message)
                  productFile.description += message
                  request.description += message + "\n"
                  fileProduct.close()
                  fpu.cleanUp()
                  lock.release()
                  break
               } else {
                  log.debug("File size matched")
                  if (!des.exists()) {
                     log.debug("Creating destination directory ${destination.path}")
                     if (!des.mkdirs()) {
                        errno = Errno.INGEST_ERR
                        message = "${productFile.name}: Unable to create directory ${destination.path}."
                        log.error(message)
                        productFile.description += message
                        request.description += message + "\n"
                        fileProduct.close()
                        fpu.cleanUp()
                        lock.release()
                        break
                     }
                  }

                  log.debug("Retrieve ${productFile.name} to ${destination.path}")

                  productFile.startTime = new Date()
                  String filename = productFile.destination + File.separator + productFile.name
                  File fileToCopy = new File(filename)
                  if ((fileToCopy.exists()) &&
                        (productFile.checksum) &&
                        (productFile.checksum.equalsIgnoreCase(ChecksumUtility.getDigest(DigestAlgorithm.MD5, fileToCopy)))) {
                     log.debug("Not copying file since it already exists.")
                  } else {
                     def os = new FileOutputStream(new File(filename))
                     def is = fileProduct.getInputStream()
                     try {
                        os << is
                     } catch (Exception e) {
                        errno = Errno.INGEST_ERR
                        message = "Unable to retrieve file ${fileProduct.friendlyURI} to ${filename}."
                        log.error(message)
                        log.trace(message, e)
                        request.description += message + "\n"
                        fileProduct.close()
                        fpu.cleanUp()
                        lock.release()
                        break
                     } finally {
                        try {
                           is.close()
                           log.trace("input stream closed.")
                           os.close()
                           log.trace("output stream closed.")
                           fileProduct.close()
                           log.trace("file product closed.")
                        } catch (IOException e) {
                           errno = Errno.INGEST_ERR
                           message = "INGEST ERROR: Unable to close file ${fileProduct.friendlyURI}."
                           log.error(message)
                           request.description += message + "\n"
                           fileProduct.close()
                           fpu.cleanUp()
                           lock.release()
                           break
                        }
                     }
                     
                     //TODO finish link generation
                     //Add links from productFile.links
                     Path sourcePath = Paths.get(productFile.destination)
                     //Sanity check that above copy worked
                     if(Files.exists(sourcePath)) {
                        List<String> links = productFile.links
                        links.each { link ->
                           Path linkPath = Paths.get(link)
                           try {
                              Files.createDirectories(linkPath.getParent())
                              Files.createSymbolicLink(linkPath, sourcePath)
                           }
                           catch(IOException e) {
                              errno = Errno.INGEST_ERR
                              message = "Unable to create links for ${filename}."
                              log.error(message)
                              log.trace(message, e)
                              request.description += message + "\n"
                              lock.release()
                           }
                        }
                     }

                     log.debug("Done retrieve file ${filename}... compute checksum")

                     savedFiles << filename

                     String checksum = ChecksumUtility.getDigest(DigestAlgorithm.MD5, new File(filename))

                     log.debug("Computed checksum ${checksum}")

                     if (!productFile.checksum) {
                        message = "${productFile.name}: No checksum was provided.  Used computed checksum."
                        log.warn(message)
                        productFile.description += message + "\n"
                        productFile.checksum = checksum
                     } else {
                        if (checksum != productFile.checksum) {
                           errno = Errno.CHECKSUM_ERROR
                           message = "${productFile.name}: Checksum failed.  Expects ${productFile.checksum} Computed ${checksum}."
                           log.error(message)
                           productFile.description += message + "\n"
                        }
                        message = "${productFile.name} in staging ${productFile.destination}."
                        log.debug(message)
                        productFile.description = message
                     }
                  }
                  productFile.stopTime = new Date()
               }
               fpu.cleanUp()
               lock.release()
               request.description += productFile.description + "\n"
            }
         }

         request.errno = errno
         request.addFiles = productFiles
         if (errno == Errno.OK) {
            request.operationStartTime = startTime
            request.operationStopTime = new Date()
         } else {
            log.error("Ingestion failure.  Trigger automatic cleanup.")
            deleteFiles(savedFiles)
         }

         log.trace("[STAT]: ${request.toString()}.")

         /*
         String action = HorizonServer.instance.registryUrl + Constants.ADD_UPDATE_ACTION
         request.user = HorizonServer.instance.serverName
         request.userId = HorizonServer.instance.serverId
         request.sessionToken = HorizonServer.instance.sessionToken
         request.sessionId = HorizonServer.instance.serverSessionId
         Post post = new Post(url: action)
         post.body = request.toRequest()
         log.trace("UPDATE: ${post.body}")
         IngestProtocol response = new IngestProtocol()
         String content = ''
         */
         log.debug("UPDATE: ${request.toRequest()}")
         try {
            zk.updateProcessNode(jobPath, request.toRequest())
            //content = post.text
            //response.load(content)
         } catch (Exception e) {
            //log.trace(content)
            log.error("Unable to update ZooKeeper node for add job: " + jobPath)
         }
         log.debug("Done processing ${request.productType}:${request.product}. Updated ZK at path: " + jobPath)
      }

      ServerMonitor.instance.userListener.delete = { IngestProtocol request ->
         String jobPath = request.productType + "/" + request.product
         zk.createProcessNode(jobPath, "${serverName},${storageName},processing delete job.")
         request.errno = Errno.OK
         if (!request.description) {
            request.description = ""
         }

         log.debug("Delete product ${request.product}")
         long totalFileSize = 0
         long fileSize = 0
         request.deletes.each { String uri ->
            File file = new File(uri)
            if (!file.exists()) {
               request.errno = Errno.FILE_NOT_FOUND
               request.description += "File ${uri} not found!\n"
            } else {
               fileSize = file.length()
               if (file.delete()) {
                  totalFileSize += fileSize
                  request.description += "File ${uri} has been deleted.\n"

                  // cleanup the root directory if the directory is empty
                  File parent = new File(file.parent)
                  if (parent.list().length == 0) {
                     if (parent.delete()) {
                        File grand = new File(parent.parent)
                        if (grand.list().length == 0) {
                           grand.delete()
                        }
                     }

                  }
               } else {
                  request.errno = Errno.DENIED
                  request.description += "Unable to delete file ${uri}.\n"
               }
            }
         }

         /*
         String action = HorizonServer.instance.registryUrl + Constants.DELETE_UPDATE_ACTION
         request.user = HorizonServer.instance.serverName
         request.userId = HorizonServer.instance.serverId
         request.sessionToken = HorizonServer.instance.sessionToken
         request.sessionId = HorizonServer.instance.serverSessionId
         */
         if (request.totalSize == null) {
            request.totalSize = totalFileSize
         }
         /*
         Post post = new Post(url: action)
         post.body = request.toRequest()
         log.trace("UPDATE: ${post.body}")
         IngestProtocol response = new IngestProtocol()
         String content = ''
         */
         log.debug("UPDATE: ${request.toRequest()}")
         try {
            zk.updateProcessNode(jobPath, request.toRequest())
            //content = post.text
            //response.load(content)
         } catch (Exception e) {
            //log.trace(content)
            log.error("Unable to update ZooKeeper node for delete job: " + jobPath)
         }
         log.debug("Done processing ${request.productType}:${request.product}.")

         /*
         int retries = 2
         while (retries > 0) {
           IngestProtocol response = new IngestProtocol()
           try {
             response.load(post.text)
             break
           } catch (Exception e) {
             log.trace("Unexpected response from server ${action}.")
           } finally {

             // TODO need to take a closer look at this.  The only time the retry is needed
             // is when the middleware failed to provide the proper response.

             --retries
           }
         }
         log.trace("Done processing ${request.productType}:${request.product}.")
         */
      }

      ServerMonitor.instance.userListener.move = { IngestProtocol request ->
         String jobPath = request.productType + "/" + request.product
         zk.createProcessNode(jobPath, "${serverName},${storageName},processing move job.")
         request.errno = Errno.OK
         if (!request.description) {
            request.description = ""
         }

         File source = new File(request.moveSource)
         if (!source.exists()) {
            request.errno = Errno.FILE_NOT_FOUND
            request.description += "Unable to move product ${request.product} to ${request.moveDestination}."

         } else {
            File destination = new File(request.moveDestination)
            if (!destination.exists()) {
               destination.mkdirs()
            }
            source.eachFile { File file ->
               file.renameTo(new File(request.moveDestination + File.separator + file.name))
            }

            if (request.moveMetadataFile) {
               try {
                  new File(request.moveDestination + File.separator + request.moveMetadataFile).write(request.moveMetadataText)
               } catch (Exception exception) {
                  def newMetadataFilePath = request.moveDestination + File.separator + new File(request.moveMetadataFile).getName()
                  log.warn("Failed to write to " + request.moveDestination + File.separator + request.moveMetadataFile + " so trying to write to here instead: " + newMetadataFilePath)
                  new File(newMetadataFilePath).write(request.moveMetadataText)
                  log.warn("File was successfully written to " + newMetadataFilePath)
               }
            }

            // delete the original directory
            if (source.list().length == 0) {
               if (source.delete()) {
                  File grand = new File(source.parent)
                  if (grand.list().length == 0) {
                     grand.delete()
                  }
               }
            }
            request.description += "Product ${request.product} has been moved to ${request.moveDestination}."
         }

         /*
         String action = HorizonServer.instance.registryUrl + Constants.MOVE_UPDATE_ACTION
         request.user = HorizonServer.instance.serverName
         request.userId = HorizonServer.instance.serverId
         request.sessionToken = HorizonServer.instance.sessionToken
         request.sessionId = HorizonServer.instance.serverSessionId
         Post post = new Post(url: action)
         post.body = request.toRequest()
         log.trace("UPDATE: ${post.body}")
         IngestProtocol response = new IngestProtocol()
         String content = ''
         */
         log.debug("UPDATE: ${request.toRequest()}")
         try {
            zk.updateProcessNode(jobPath, request.toRequest())
            //content = post.text
            //response.load(content)
         } catch (Exception e) {
            //log.trace(content)
            log.error("Unable to update ZooKeeper node for move job: " + jobPath)
         }
         log.debug("Done processing ${request.productType}:${request.product}.")
         /*
         int retries = 2
         while (retries > 0) {
           IngestProtocol response = new IngestProtocol()
           try {
             response.load(post.text)
             break
           } catch (Exception e) {
             log.trace("Unexpected response from server ${action}.")
           } finally {

             // TODO need to take a closer look at this.  The only time the retry is needed
             // is when the middleware failed to provide the proper response.

             --retries
           }
         }
         log.trace("Done processing ${request.productType}:${request.product}.")
         */

      }

      //ServerMonitor.instance.userListener.start()

      /* Removing admin listener
      ServerMonitor.instance.adminListener = new ServerPortListener(
              "admin",
              serverConfig.maxAdminConnections.toInteger(),
              serverConfig.protocol,
              serverConfig.bind,
              serverConfig.adminPort.toInteger(),
              serverConfig.urlPattern)
      ServerMonitor.instance.adminListener.start()
      */

      /* Removing SigEvent dependency
      // startup notification to sigevent
      def sigEvent = new SigEvent(sigEventUrl)
      sigEvent.create(
        EventType.Info,
        HorizonServer.SIG_EVENT_CATEGORY,
        "ingest",
        "ingest",
        HorizonServer.LOCAL_HOST_NAME,
        "Ingest engine ("+serverName+") has been started",
        null,
        "Ingest engine ("+serverName+") has been started."
      )
      */

      if (stereotype == Stereotype.INGEST) {
         startZooKeeperConnection(true)
      } else if (stereotype == Stereotype.ARCHIVE) {
         startZooKeeperConnection(false)
      } else {
         log.error("Unsupported stereotype " + stereotype)
      }
   }

   private void parseArgs(def args) {
      CommandLineParser clp = new CommandLineParser()

      Option foption = new Option(
            name: "Federation",
            description: 'Federation name',
            withValue: true,
            prefixes: [
                  new Prefix(name: "-f"),
                  new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
            ]
      )
      clp.options << foption

      Option noption = new Option(
            name: 'Name',
            description: 'Engine name',
            withValue: true,
            prefixes: [
                  new Prefix(name: "-n"),
                  new Prefix(name: "--name", valueSeparator: "=", isLong: true)
            ],
            required: true
      )
      clp.options << noption

      Option toption = new Option(
            name: 'Stereotype',
            description: 'Stereotype (ingest, archive)',
            withValue: true,
            prefixes: [
                  new Prefix(name: "-t"),
                  new Prefix(name: "--type", valueSeparator: "=", isLong: true)
            ],
            required: true
      )
      clp.options << toption

      Option soption = new Option(
            name: 'Storage',
            description: 'Storage Name',
            withValue: true,
            prefixes: [
                  new Prefix(name: "-s"),
                  new Prefix(name: "--storage", valueSeparator: "=", isLong: true)
            ],
            required: true
      )
      clp.options << soption

      Option woption = new Option(
            name: 'Max Wait Time',
            description: 'Maximum engine wait time in milliseconds',
            withValue: true,
            prefixes: [
                  new Prefix(name: "-w"),
                  new Prefix(name: "--wait", valueSeparator: "=", isLong: true)
            ],
            required: false
      )
      clp.options << woption

      Option uoption = new Option(
            name: 'User',
            description: 'User name',
            withValue: true,
            prefixes: [
                  new Prefix(name: '-u'),
                  new Prefix(name: '--username', valueSeparator: "=", isLong: true)
            ],
            required: false
      )
      clp.options << uoption

      Option poption = new Option(
            name: 'Password',
            description: 'Password',
            withValue: true,
            prefixes: [
                  new Prefix(name: '-p'),
                  new Prefix(name: '--password', valueSeparator: "=", isLong: true)
            ],
            required: false
      )
      clp.options << poption

      Option roption = new Option(
            name: 'Registry',
            description: 'Registry URL',
            withValue: true,
            prefixes: [
                  new Prefix(name: '-r'),
                  new Prefix(name: '--registry', valueSeparator: "=", isLong: true)
            ],
            required: false
      )
      clp.options << roption

      Option eoption = new Option(
            name: 'SigEvent',
            description: 'SigEvent URL',
            withValue: true,
            prefixes: [
                  new Prefix(name: '-e'),
                  new Prefix(name: '--sigevent', valueSeparator: "=", isLong: true)
            ],
            required: false
      )
      clp.options << eoption

      Option zoption = new Option(
            name: 'ZooKeeper',
            description: 'ZooKeeper URL',
            withValue: true,
            prefixes: [
                  new Prefix(name: '-z'),
                  new Prefix(name: '--zookeeper', valueSeparator: "=", isLong: true)
            ],
            required: false
      )
      clp.options << zoption

      Option hoption = new Option(
            name: 'Help',
            description: 'Print usage',
            withValue: false,
            prefixes: [
                  new Prefix(name: '-h'),
                  new Prefix(name: '--help', isLong: true)
            ],
            required: false
      )
      clp.options << hoption

      String app = System.getProperty(Constants.PROP_USER_APP)
      if (!app) {
         app = IngestServer.class.name
      }

      try {
         CommandLine cl = clp.parse(args)

         if (cl.hasOption(hoption)) {
            CommandLineHelp clh = new CommandLineHelp()
            log.info(clh.format(app, clp.options, []))
            System.exit(0)
         }

         if (cl.hasOption(foption)) {
            federation = cl.getOptionValue(foption)
         }

         serverName = cl.getOptionValue(noption)
         stereotype = Stereotype.valueOf(cl.getOptionValue(toption).toUpperCase())
         storageName = cl.getOptionValue(soption)
         username = cl.getOptionValue(uoption)
         zooKeeperUrl = cl.getOptionValue(zoption)
         if (cl.hasOption(woption))
            maxWaitTime = Integer.parseInt(cl.getOptionValue(woption))
         if (cl.hasOption(poption))
            password = Encrypt.encrypt(cl.getOptionValue(poption))
         registryUrl = cl.getOptionValue(roption)
         sigEventUrl = cl.getOptionValue(eoption)
      } catch (Exception e) {
         log.info(new CommandLineHelp().format(app, clp.options, []))
         System.exit(0)
      }

   }

   def void configure() throws ServerException {
      if (!federation || !registryUrl || !sigEventUrl || !zooKeeperUrl) {
         domain = new Domain()
         if (domainFile) {
            try {
               domain.load(new File(domainFile).text)
               if (!federation)
                  federation = domain.default
               if (!registryUrl)
                  registryUrl = domain.getUrl(federation)
               if (!sigEventUrl)
                  sigEventUrl = domain.getSigEvent()
               if (!zooKeeperUrl)
                  zooKeeperUrl = domain.getJobKeeper()
            } catch (JAXBException e) {
               log.debug(e.message, e)
               throw new SessionException("Unable to parse domain file: ${domainFile}.", e)
            } catch (SAXException e) {
               log.debug(e.message, e)
               throw new SessionException("Unable to parse domain file: ${domainFile}.", e)
            }
         } else {
            log.error("Missing federation and/or registry URL.")
            System.exit(0)
         }
      }
      /*
      if (!username) {
         registry = System.getProperty(Constants.PROP_RESTART_DIR)
         if (!registry) {
            registry = System.getProperty("user.home") +
                  File.separator + Constants.RESTART_DIR
         } else {
            registry = registry + File.separator + Constants.RESTART_DIR
         }

         boolean ok = true
         File f = new File(registry)
         if (!f.exists()) {
            ok = f.mkdir()
         }
         if (!ok)
            throw new SessionException(
                  "Unable to create restart directory \"$registry\".",
                  Errno.RESTART_FILE_ERR)

         File keyfile = new File(registry + File.separator + Constants.KEYCHAIN_FILE)
         keychain = new Keychain()

         if (keyfile.exists()) {
            try {
               keychain.load(keyfile.text)
               username = keychain.getFederationUsername(federation)
               password = Encrypt.encrypt(keychain.getFederationPassword(federation))
            } catch (JAXBException e) {
               log.debug(e.message, e)
               throw new SessionException("Unable to parse keychain file: ${keyfile.name}.", e)
            } catch (SAXException e) {
               log.debug(e.message, e)
               throw new SessionException("Unable to parse domain file: ${keyfile.name}.", e)
            }
         }
      }*/
   }

   private void readConfig() throws IOException {

      if (!federation) {
         federation = domain.default
      }
      /*
      if (!username || !password) {
         username = keychain.getFederationUsername(federation)
         password = keychain.getFederationPassword(federation)
         if (password) {
            password = Encrypt.encrypt(password)
         }
      }*/

      // login to checkout an authentication token
      /* Removing get configuration via manager
      IngestProtocol request = IngestProtocol.createLoginRequest(
              username,
              password,
              null)

      String action = registryUrl + Constants.INIT_ACTION

      Post post = new Post(url: action)
      post.body = request.toRequest()
      log.trace "Converted request body text: '${post.body}'."
      IngestProtocol response = new IngestProtocol()
      try {
        String re = post.text
        log.trace("Response: ${re}")
        response.load(re)
      } catch (FileNotFoundException e) {
        log.trace(e.message, e)
        throw new IOException(e.message)
      } catch (ConnectException e) {
        log.trace(e.message, e)
        throw new IOException(e.message)
      } catch (Exception e) {
        log.trace(e.message, e)
      }
      if (response.errno != Errno.OK) {
        throw new IOException(response.description)
      }
      serverId = response.userId
      serverSessionId = response.sessionId
      sessionToken = response.sessionToken

      // query for configuration parameters
      request = IngestProtocol.createEngineBootRequest(serverSessionId, sessionToken, serverId, federation, serverName)
      action = registryUrl + Constants.BOOT_ACTION
      post = new Post(url: action)
      post.body = request.toRequest()
      log.debug(post.body)
      serverConfig.load(post.text)
      if (serverConfig.errno != Errno.OK) {
        throw new IOException(serverConfig.description)
      }

      log.debug(serverConfig.toString())
      */

   }

   private void sendSigEvent(EventType eventType, String description, String data) {
      def sigEvent = new SigEvent(sigEventUrl)
      sigEvent.create(
            eventType,
            IngestServer.SIG_EVENT_CATEGORY,
            "ingest",
            "ingest",
            IngestServer.LOCAL_HOST_NAME,
            description,
            null,
            data
      )
   }

   void startZooKeeperConnection(boolean isIngest) {
      // zk requires a watcher or it will log a NPE.  This is the
      // work around to make zk happy.
      def watcher = { WatchedEvent event ->
         log.debug ("Ingest Server Watcher: received event " + event.type + " " + event.state)
      }
      try {
         zk = ZkFactory.getZk(zooKeeperUrl, watcher as Watcher)
         String path = zk.registerEngine(storageName, serverName)
         if (path == null) {
            log.error("Unable to register engine " + serverName + " with zookeeper for storage " + storageName)
            return
         }
      } catch (IOException e) {
         log.error(e.message, e)
         return
      }
      started = true
      sendSigEvent(
            EventType.Info,
            "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been started.",
            "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been started."
      )
      Thread t = new Thread() {
         public void run() {
            int i = 0
            boolean paused = false
            processLoop:
            while (true) {
               if (i < maxWaitTime)
                  i += 1000
               try {
                  log.debug("Poll ZK at storage " + storageName)
                  //check zookeeper see if engine is still registered
                  switch (zk.checkEngineRegistration(storageName, serverName)) {
                     case RegistrationStatus.READY:
                        if (paused) {
                           sendSigEvent(
                                 EventType.Info,
                                 "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been resumed.",
                                 "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been resumed."
                           )
                           i = 0
                           paused = false
                        }
                        //poll ZK for job
                        String input = null
                        if (isIngest) {
                           input = zk.getIngestJobNoBlock(storageName)
                        } else {
                           input = zk.getArchiveJobNoBlock(storageName)
                        }

                        if (input != null) {
                           i = 0
                           //if there is a job then delete it and process it
                           log.debug("Got node: " + input)

                           def xml = new XmlSlurper().parseText(input)
                           IngestProtocol manifest = new IngestProtocol()
                           manifest.load(xml.params.toString())
                           if (manifest.operation == Opcode.ENGINE_INGEST) {
                              def name = "${manifest.jobId}:${manifest.federation}:${manifest.productType}:${manifest.product}"
                              log.debug("Working on " + name)
                              ServerMonitor.instance.userListener.add(manifest)
                              log.debug("Finished calling ServerMonitor.instance.userListener.add")
                           } else if (manifest.operation == Opcode.ENGINE_DELETE) {
                              def name = "${manifest.jobId}:${manifest.federation}:${manifest.productType}:${manifest.product}"
                              log.debug("Working on " + name)
                              ServerMonitor.instance.userListener.delete(manifest)
                              log.debug("Finished calling ServerMonitor.instance.userListener.delete")
                           } else if (manifest.operation == Opcode.ENGINE_MOVE) {
                              def name = "${manifest.jobId}:${manifest.federation}:${manifest.productType}:${manifest.product}"
                              log.debug("Working on " + name)
                              ServerMonitor.instance.userListener.move(manifest)
                              log.debug("Finished calling ServerMonitor.instance.userListener.move")
                           }
                        } else {
                           //if there is no job then sleep for awhile and poll again
                           log.debug("No job so going to sleep for " + i + " ms")
                           Thread.sleep(i)
                        }
                        break
                     case RegistrationStatus.OFFLINE:
                        log.debug("Engine not registered so shutting down.")
                        break processLoop
                     case RegistrationStatus.PAUSED:
                        if (!paused) {
                           sendSigEvent(
                                 EventType.Info,
                                 "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been paused.",
                                 "${stereotype} engine (${serverName}) registered with storage (${storageName}) has been paused."
                           )
                           i = 0
                           paused = true
                        }
                        log.debug("Engine has been suspended so going to sleep for " + i + " ms")
                        Thread.sleep(i)
                        break
                  }
               } catch (IOException e) {
                  log.error(e.message, e)
               } catch (SAXException e) {
                  log.error(e.message, e)
               }
            }
         }
      }
      t.start()
   }

   public static void main(args) throws Exception {
      IngestServer.instance.activate(args)
   }

}
