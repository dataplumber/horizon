/** ***************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 **************************************************************************** */
package gov.nasa.horizon.ingest.client

import gov.nasa.horizon.common.api.serviceprofile.SPHeader
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.horizon.common.api.util.Console
import gov.nasa.horizon.common.api.util.DateTimeUtility;
import gov.nasa.horizon.common.api.util.FileUtility
import gov.nasa.horizon.common.api.util.StringUtility
import gov.nasa.horizon.common.util.*
import gov.nasa.horizon.ingest.api.*
import gov.nasa.horizon.ingest.api.jaxb.protocol.ListProductInfoType;
import gov.nasa.horizon.sigevent.api.*

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.ast.stmt.CatchStatement;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}* @version $Id$
 */
public class IngestClient {
   private static Log log = LogFactory.getLog(IngestClient.class)
   private static final String HEADER =
      Constants.COPYRIGHT + "\n" + Constants.CLIENT_VERSION_STR + "\n" + Constants.API_VERSION_STR
   private static final String PROGRAM_NAME = "ingest"
   private static final List NO_FILE_SIZE_CHECK_REGEXES = [
         /[Rr][Ee][Vv]_[Tt][Ii][Mm][Ee]\.[Dd][Aa][Tt]/
   ]
   private static final Map OPERATIONS = [
         ADD: new Option(
               name: "add",
               required: false,
               description: "Add operation.",
               withValue: false,
               prefixes: [
                     new Prefix(name: "add")
               ]
         ),
         REPLACE: new Option(
               name: "replace",
               required: false,
               description: "Replace operation.",
               withValue: false,
               prefixes: [
                     new Prefix(name: "replace")
               ]
         ),
         LIST: new Option(
               name: "list",
               required: false,
               description: "List operation.",
               withValue: false,
               prefixes: [
                     new Prefix(name: "list")
               ]
         ),
         LOGIN: new Option(
               name: "login",
               required: false,
               description: "Login operation.",
               withValue: false,
               prefixes: [
                     new Prefix(name: "login")
               ]
         ),
         HELP: new Option(
               name: "help",
               required: false,
               description: "Help operation.",
               withValue: false,
               prefixes: [
                     new Prefix(name: "help")
               ]
         )
   ]
   private static final Map COMMANDS = [
         ADD: [
               OPTIONS: [
                     FEDERATION: new Option(
                           name: "Federation",
                           required: false,
                           description: "Federation.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-f"),
                                 new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     PRODUCT_TYPE: new Option(
                           name: "ProductType",
                           required: false,
                           description: "Product Type.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-t"),
                                 new Prefix(name: "--type", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     PRODUCT_FILE: new Option(
                           name: "ProductFile",
                           required: false,
                           description: "Product File.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-n"),
                                 new Prefix(name: "--name", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     LOCAL_REPO: new Option(
                           name: "LocalRepo",
                           required: false,
                           description: "Run as daemon to monitor local repo.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-r"),
                                 new Prefix(name: "--repo", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     CLEAN_DELAY: new Option(
                        name: "CleanDelay",
                        required: false,
                        description: "Delay in seconds between submission sip/data clean ups. ",
                        withValue: true,
                        prefixes: [
                              new Prefix(name: "-d"),
                              new Prefix(name: "--delay", valueSeparator: "=", isLong: true)
                        ]
                     ),
                     CLEAN_BATCH_SIZE: new Option(
                        name: "CleanBatchSize",
                        required: false,
                        description: "The number of sips to process when a SIP cleanup occurs.",
                        withValue: true,
                        prefixes: [
                              new Prefix(name: "-b"),
                              new Prefix(name: "--batchsize", valueSeparator: "=", isLong: true)
                        ]
                     )
               ],
               INPUTS: [
                     PRODUCT_FILES: new Input(
                           name: "ProductFiles",
                           required: false,
                           description: "Product Files."
                     )
               ] as LinkedHashMap,
               METHOD: "addRequested"
         ],
         REPLACE: [
               OPTIONS: [
                     FEDERATION: new Option(
                           name: "Federation",
                           required: false,
                           description: "Federation.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-f"),
                                 new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     PRODUCT_TYPE: new Option(
                           name: "ProductType",
                           required: false,
                           description: "Product Type.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-t"),
                                 new Prefix(name: "--type", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     PREVIOUS_PRODUCT_NAME: new Option(
                           name: "PreviousProductName",
                           required: false,
                           description: "Previous Product Name.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-pn"),
                                 new Prefix(name: "--previous", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     NEW_PRODUCT_FILE: new Option(
                           name: "NewProductFile",
                           required: false,
                           description: "New Product File.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-nn"),
                                 new Prefix(name: "--new", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     LOCAL_REPO: new Option(
                           name: "LocalRepo",
                           required: false,
                           description: "Run as daemon to monitor local repo.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-r"),
                                 new Prefix(name: "--repo", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     CLEAN_DELAY: new Option(
                        name: "CleanDelay",
                        required: false,
                        description: "Delay in seconds between submission sip/data clean ups. ",
                        withValue: true,
                        prefixes: [
                              new Prefix(name: "-d"),
                              new Prefix(name: "--delay", valueSeparator: "=", isLong: true)
                        ]
                     ),
                     CLEAN_BATCH_SIZE: new Option(
                        name: "CleanBatchSize",
                        required: false,
                        description: "The number of sips to process when a SIP cleanup occurs.",
                        withValue: true,
                        prefixes: [
                              new Prefix(name: "-b"),
                              new Prefix(name: "--batchsize", valueSeparator: "=", isLong: true)
                        ]
                     ),
					 CLIENT_NAME: new Option(
						 name: "ClientName",
						 required: false,
						 description: "The client's instance name used for log naming.",
						 withValue: true,
						 prefixes: [
							   new Prefix(name: "-n"),
							   new Prefix(name: "--name", valueSeparator: "=", isLong: true)
						 ]
					  ),
                     PURGE_ONLY: new Option(
                         name: "PurgeOnly",
                         required: false,
                         description: "Flag to skip ingestion portion and only purge submitted products.",
                         withValue: false,
                         prefixes: [
                               new Prefix(name: "-p"),
                               new Prefix(name: "--purgeonly", isLong: true)
                         ]
                      ),
                     SKIP_PURGE: new Option(
                         name: "SkipPurge",
                         required: false,
                         description: "Flag to skip purging logic and only scan/submit.",
                         withValue: false,
                         prefixes: [
                               new Prefix(name: "-s"),
                               new Prefix(name: "--skippurge", isLong: true)
                         ]
                      ),
                  UNLIMITED_RETRIES: new Option(
                      name: "UnlimitedRetries",
                      required: false,
                      description: "Flag to make the ingest client retry manager submission indefinitely despite errors.",
                      withValue: false,
                      prefixes: [
                            new Prefix(name: "-u"),
                            new Prefix(name: "--unlimitedretries", isLong: true)
                      ]
                   ),
               ],
               INPUTS: [
                     NEW_PRODUCT_FILES: new Input(
                           name: "NewProductFiles",
                           required: false,
                           description: "New Product Files."
                     )
               ] as LinkedHashMap,
               METHOD: "replaceRequested"
         ],
         LIST: [
               OPTIONS: [
                     FEDERATION: new Option(
                           name: "Federation",
                           required: false,
                           description: "Federation.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-f"),
                                 new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     PRODUCT_TYPE: new Option(
                           name: "ProductType",
                           required: true,
                           description: "Product Type.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-t"),
                                 new Prefix(name: "--type", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     PRODUCT_NAME: new Option(
                           name: "ProductName",
                           required: true,
                           description: "Product Name.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-n"),
                                 new Prefix(name: "--name", valueSeparator: "=", isLong: true)
                           ]
                     ),
               ],
               INPUTS: [:] as LinkedHashMap,
               METHOD: "listRequested"
         ],
         LOGIN: [
               OPTIONS: [
                     FEDERATION: new Option(
                           name: "Federation",
                           required: false,
                           description: "Federation.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-f"),
                                 new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
                           ]
                     ),
                     USER_NAME: new Option(
                           name: "Username",
                           required: false,
                           description: "Username.",
                           withValue: true,
                           prefixes: [
                                 new Prefix(name: "-u"),
                                 new Prefix(name: "--user", valueSeparator: "=", isLong: true)
                           ]
                     ),
               ],
               INPUTS: [:] as LinkedHashMap,
               METHOD: "loginRequested"
         ],
         HELP: [
               OPTIONS: [:],
               INPUTS: [
                     OPERATION: new Input(
                           name: "TargetOperation",
                           required: false,
                           description: "Target for help."
                     )
               ] as LinkedHashMap,
               METHOD: "helpRequested"
         ]
   ]
   private SigEvent sigevent
   private Preference preference
   private Domain domain

   public IngestClient() throws Exception {
      preference = new Preference()
      domain = preference.getDomain()
	  sigevent = new SigEvent(domain.getSigEvent())
   }

   public void run(String[] arguments) {
      log.info(HEADER + "\n")

      CommandLineParser clp = new CommandLineParser()
      OPERATIONS.each { key, value ->
         clp.options.add(value)
      }
      CommandLine initialCommandLine = clp.parse(arguments)

      String commandName = "HELP"
      OPERATIONS.each { key, value ->
         if (initialCommandLine.hasOption(value)) {
            commandName = key
         }
      }
      prepareCommandLineParser(clp, commandName)

      List invalidArguments = getInvalidArguments(clp, arguments)
      if (invalidArguments.size() > 0) {
         invalidArguments.each { invalidArgument ->
            log.error("Invalid argument: " + invalidArgument)
         }

         arguments = [
               OPERATIONS["HELP"].prefixes[0].name,
               OPERATIONS[commandName].prefixes[0].name
         ] as String[]
         prepareCommandLineParser(clp, "HELP")
         commandName = "HELP"
      }

      try {
         CommandLine commandLine = clp.parse(arguments)
         this."${COMMANDS[commandName].METHOD}"(commandLine)
      } catch (Exception e) {
         log.error(e.message)
      }
   }

   protected void addRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.ADD.OPTIONS.FEDERATION)
      if (!isLoggedIn(federation)) {
         log.info("Please login first.")
         System.exit(0)
      }
      LoginInformation loginInformation = preference.getLoginInformation(federation)

      String localRepo = commandLine.getOptionValue(COMMANDS.ADD.OPTIONS.LOCAL_REPO)
      if (localRepo) {
         def add = { String fed, LoginInformation loginInfo, String productType, String newProduct, String localRepoPath ->
            addProduct(
                  fed,
                  loginInfo,
                  productType,
                  newProduct,
                  localRepoPath
            )
         }
         Integer cleanDelay = commandLine.getOptionValue(COMMANDS.ADD.OPTIONS.CLEAN_DELAY)
         Integer cleanBatchSize = commandLine.getOptionValue(COMMANDS.ADD.OPTIONS.CLEAN_BATCH_SIZE)
         this.processLocalRepo(loginInformation, federation, localRepo, add, cleanDelay, cleanBatchSize)
      }

      String productType = commandLine.getOptionValue(COMMANDS.ADD.OPTIONS.PRODUCT_TYPE)
      List productFiles = []
      String productFile = commandLine.getOptionValue(COMMANDS.ADD.OPTIONS.PRODUCT_FILE)
      if (productFile) {
         productFiles.add(productFile)
      } else {
         List values = commandLine.getInputValues(COMMANDS.ADD.INPUTS.PRODUCT_FILES)
         if (values) {
            productFiles.addAll(values)
         }
      }

      log.debug("Fed: ${federation}")
      log.debug("ProductType: ${productType}")
      log.debug("ProductFiles: ${productFiles}")
      log.debug("Username: ${loginInformation.userName}")
      log.debug("Password: ${loginInformation.password}")
      log.debug("Domain: ${preference.getDomainFile().getAbsolutePath()}")

      productFiles.each {
         try {
            addProduct(federation, loginInformation, productType, it)
         } catch (Exception exception) {
            log.error("Failed to add " + it + ": " + exception.getMessage())
            log.debug("Failed to add.", exception)
         }
      }
   }

   protected void helpRequested(CommandLine commandLine) {
      CommandLineHelp clh = new CommandLineHelp()
      List options = new LinkedList(OPERATIONS.values())
      List inputs = []
      boolean isLong = false

      List helpTargets = commandLine.getInputValues(COMMANDS.HELP.INPUTS.OPERATION)
      if (helpTargets) {
         Map.Entry targetEntry = OPERATIONS.find { entry ->
            entry.value.prefixes.find { it.name == helpTargets[0] } != null
         }
         if (targetEntry != null) {
            options = [targetEntry.value]
            Map commandMap = COMMANDS.get(targetEntry.key)
            options.addAll(commandMap.OPTIONS.values())
            inputs.addAll(commandMap.INPUTS.values())
            isLong = true
         }
      }

      log.info(clh.format(PROGRAM_NAME, options, inputs, isLong))
   }

   protected void processLocalRepoSubmitted(LoginInformation loginInformation, String federation, String localRepo, Integer cleanBatchSize) {
      String daDir = "${localRepo}${File.separator}staging${File.separator}data"
      String stSubmitted = "${localRepo}${File.separator}staging${File.separator}submitted"
      String erSubmit = "${localRepo}${File.separator}error${File.separator}submission"
      Integer fileMaxCount
      
      new File(stSubmitted).listFiles().each {File ptDir ->
         if (!ptDir.name.startsWith('.')) {
            String productType = ptDir.name
            List submittedList = new ArrayList()
            try {
               log.debug("Found "+ptDir.listFiles().size()+" files in the directory for PT: "+productType)
               List fullList = Arrays.asList(ptDir.listFiles())
               fileMaxCount = cleanBatchSize
               if(fullList.size() < fileMaxCount) {
                  fileMaxCount = fullList.size()
               }
               submittedList = fullList.subList(0, fileMaxCount)
               log.debug("Limiting SIP list for "+ptDir+" to "+fileMaxCount)
            }
            catch(Exception e) {
               log.error("Exception occurred when accessing array of files from the directory:" + ptDir.name, e)
            }
            submittedList.each {File sip ->
               String name = retrieveProductName(sip)
               log.debug("Querying Manager for product "+name)
               try {
                  List<Product> procs = listProducts(federation,
                        loginInformation,
                        productType,
                        name)
                  Product p = procs[0]

                  // if the product has been successfully archived and
                  // its job has been purged from Manager, the resultset
                  // should be empty
                  if (procs.size() == 0) {
                     log.info ("${productType}:${name} archive SUCCESS.  Purging from local staging.")
                     // the product is no longer in Manager.  That is, it has been archived and information have been purged
                     // remove SIP from submitted folder
                     sip.delete()

                     // purge ingested product
                     String productUniqueName = retrieveProductUniqueDir(sip)
                     File productData = new File("${daDir}${File.separator}${productType}${File.separator}${productUniqueName}")
                     productData.deleteDir()
                  } else if (p.state == State.ABORTED) {
                      def msg = "${productType}:${name} still in Manager with an ABORTED state. Moving SIP to error directory."
                     log.error(msg)
                     // error processing
                     sip.renameTo(new File("${erSubmit}${File.separator}${ptDir.name}${File.separator}${sip.name}"))
                     this.sigevent.create(EventType.Error, 'HORIZON', 'IngestClient', 'IngestClient', InetAddress.localHost.hostAddress, msg)
                  }
                  else {
                     log.debug("Product "+name+" still present in Manager with state "+p.state+" and lock "+p.lock)
//					 this.sigevent.create(EventType.Error, 'HORIZON',  'IngestClient',
//						 'IngestClient',
//						 InetAddress.localHost.hostAddress,
//						 "Product "+name+" still present in Manager with state "+p.state+" and lock "+p.lock)
                  }
                  
               } catch (Exception e) {
                  log.error ("Unable to process submitted product", e)
               }
            }
         }
      }


   }


   protected void processLocalRepo(LoginInformation loginInformation, String federation, String localRepo, Closure yield, Integer cleanDelay, Integer cleanBatchSize, Boolean purgeOnly, Boolean skipPurge, Boolean unlimitedRetries) {
      String stPending = "${localRepo}${File.separator}staging${File.separator}pending"
      String stSubmitted = "${localRepo}${File.separator}staging${File.separator}submitted"
      String erSubmit = "${localRepo}${File.separator}error${File.separator}submission"


      Date lastCheckedSubmitted = new Date();
      if(cleanDelay == null) {
         cleanDelay = 120
      }
      if(cleanBatchSize == null) {
         cleanBatchSize = 10
      }
      if(!purgeOnly) {
          log.info("Monitoring local repository ${localRepo} for new submissions....")
      }
      
      while (true) {
         // process submitted products only if it has been x minutes since the last.
          if(skipPurge) {
              log.trace("skipPurge enabled. Skipping product purging.")
          }
          else {
              if(new Date(lastCheckedSubmitted.getTime() + (cleanDelay*1000)).before(new Date())) {
                 log.debug("Performing Manager check of submitted products")
                 lastCheckedSubmitted = new Date()
                 
                 this.processLocalRepoSubmitted(loginInformation, federation, localRepo, cleanBatchSize)
                 log.debug("Finished checking submitted products. Next check in "+cleanDelay+" seconds")
              }
          }
         if(purgeOnly) {
             log.trace("purgeOnly enabled. Skipping product submission.")
         }
         else {
             // check the list of pendings and submit them
             new File(stPending).listFiles().each { File ptDir ->
                if (!ptDir.name.startsWith('.') && ptDir.isDirectory()) {
                   String productType = ptDir.name
                   
                   //Getting only the latest sip for each product type
                   def fileList = new ArrayList<File>()
                   
                   def fileMap = ptDir.listFiles().toList().groupBy {
                      def matcher = (it.name =~ /(.*)\_(\d{13})\.xml/)
                      if(matcher) {
                         log.debug("SIP "+it.name+ " with timestamp detected. Removing timestamp from product name...")
                         matcher[0][1]
                      }
                      else {
                         it.name
                      }
                   }
                   fileMap.each { key, productList ->
                      if(productList.size > 0) {
                         productList.sort()
                         productList.eachWithIndex { file, i ->
                            if( i == productList.size - 1 ) {
                               //Adding last file (most recent) to the product list to submit
                               log.debug("Adding product "+file.name+" to the submission list")
                               fileList.add(file)
                            }
                            else {
                               //run cleanup code
                               log.debug("Ignoring product "+file.name+" due to newer version being available")
                               cleanUpIgnored(localRepo, productType, file.toString())
                            }
                         }
                      }
                      
                   }
                   
                   //File list finalized, process them!
                   fileList.each {File sip ->
                      int retries = 5
                      Boolean retryFlag = false
                      for (int i=5; i>=0; --i) {
                      try {
                         yield(federation, loginInformation, productType, sip.absolutePath, localRepo)
    
                         // Success.  Move to submitted dir
                         File stSubmittedDir = new File("${stSubmitted}${File.separator}${ptDir.name}")
                         if (!stSubmittedDir.exists()) {
                            stSubmittedDir.mkdirs()
                         }
                         sip.renameTo(new File("${stSubmitted}${File.separator}${ptDir.name}${File.separator}${sip.name}"))
    
                         break
                      } catch (Exception exception) {
                      
                         if (i > 0) {
                            if(unlimitedRetries) {
                                if (!retryFlag) {
                                    log.warn ("Unable to replace ${sip.absolutePath}... retrying indefinitely")
                                }
                                retryFlag = true
                                i = 5
                            }
                            else {
                                log.warn ("Unable to replace ${sip.absolutePath}... retry attempt ${i}")
                            }
                                
                         } else {
                            // after 5 failed attempts.  Move submission to error.
                            File erSubmitDir = new File("${erSubmit}${File.separator}${ptDir.name}")
                            if (!erSubmitDir.exists()) {
                               erSubmitDir.mkdirs()
                            }
                            sip.renameTo(new File("${erSubmit}${File.separator}${ptDir.name}${File.separator}${sip.name}"))
                            String msg = "${sip.name}: ${exception.message}. Submission moved to error directory."
                            log.error(msg)
                            
    						this.sigevent.create(EventType.Error, 'HORIZON',  'IngestClient',
    							'IngestClient',
    							InetAddress.localHost.hostAddress,
    							msg)
                            log.debug(msg, exception)
                         }
                      }
                      }
    
                   }
                }
                else {
                    log.warn("Misplaced file found in pending directory: "+ptDir.name)
                }
             }
         }
         // sleep for 5 seconds
         sleep(5000)
      }
   }

   private void checkSubmitted(LoginInformation loginInformation, String federation, String localRepo) {
      String stSubmitted = "${localRepo}${File.separator}staging${File.separator}submitted"
      String erSubmit = "${localRepo}${File.separator}errors${File.separator}submission"
      String data = "${localRepo}${File.separator}staging${File.separator}data"



   }

   protected void replaceRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.REPLACE.OPTIONS.FEDERATION)
      if (!isLoggedIn(federation)) {
         log.info("Please login first.")
         System.exit(0)
      }
      LoginInformation loginInformation = preference.getLoginInformation(federation)

      String localRepo = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.LOCAL_REPO)
      String purgeOnlyS = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.PURGE_ONLY)
      String skipPurgeS = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.SKIP_PURGE)
      String unlimitedRetriesS = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.UNLIMITED_RETRIES)
      Boolean purgeOnly = false
      Boolean skipPurge = false
      Boolean unlimitedRetries = false
      if(purgeOnlyS != null)  {
          log.info("PURGE ONLY Mode Enabled")
          purgeOnly = true
      }
      if(skipPurgeS != null) {
          log.info("SKIP PURGE Mode Enabled")
          skipPurge = true 
      }
      if(unlimitedRetriesS != null) {
          log.info("UNLIMITED RETRIES Mode Enabled")
          unlimitedRetries = true
      }
      
      if (localRepo) {
         def replace = { String fed, LoginInformation loginInfo, String productType, String newProduct, String localRepoPath ->
            replaceProduct(
                  fed,
                  loginInfo,
                  productType,
                  null,
                  newProduct,
                  localRepoPath
            )
         }
         Integer cleanDelay = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.CLEAN_DELAY) as Integer
         Integer cleanBatchSize = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.CLEAN_BATCH_SIZE) as Integer
         this.processLocalRepo(loginInformation, federation, localRepo, replace, cleanDelay, cleanBatchSize, purgeOnly, skipPurge, unlimitedRetries)
      }

      String productType = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.PRODUCT_TYPE)
      String previousProductName =
         commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.PREVIOUS_PRODUCT_NAME)

      List<String> newProductFiles = []
      String newProductFile = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.NEW_PRODUCT_FILE)
      if (newProductFile) {
         newProductFiles.add(newProductFile)
      } else {
         List values = commandLine.getInputValues(COMMANDS.REPLACE.INPUTS.NEW_PRODUCT_FILES)
         if (values) {
            newProductFiles.addAll(values)
         }
      }

      log.debug("ProductType: " + productType)
      log.debug("Previous: " + previousProductName)
      log.debug("New: " + newProductFiles)

      newProductFiles.each {
         try {
            replaceProduct(
                  federation,
                  loginInformation,
                  productType,
                  previousProductName,
                  it
            )
         } catch (Exception exception) {
            log.error("Failed to replace " + it + ": " + exception.getMessage())
            log.debug("Failed to replace.", exception)
         }
      }
   }

   protected void listRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.LIST.OPTIONS.FEDERATION)
      if (!isLoggedIn(federation)) {
         log.info("Please login first.")
         System.exit(0)
      }
      LoginInformation loginInformation = preference.getLoginInformation(federation)

      String productType = commandLine.getOptionValue(COMMANDS.LIST.OPTIONS.PRODUCT_TYPE)
      String productName = commandLine.getOptionValue(COMMANDS.LIST.OPTIONS.PRODUCT_NAME)

      log.debug("ProductType: " + productType)
      log.debug("ProductName: " + productName)

      try {
         List<Product> products = listProducts(
               federation,
               loginInformation,
               productType,
               productName
         )

         log.info(products.size() + " products found.")
         products.each { product ->
            log.info(product.toString())
         }
      } catch (Exception exception) {
         log.debug("Failed to list.", exception)
         throw new Exception("Failed to list: " + exception.getMessage())
      }
   }

   protected void loginRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.LOGIN.OPTIONS.FEDERATION)
      String userName = commandLine.getOptionValue(COMMANDS.LOGIN.OPTIONS.USER_NAME)
      String password = null

      login(federation, userName, password)
   }

   protected String readLineFromStandardInput(String prompt) {
      String line = null

      BufferedReader bufferedReader = null
      try {
         print(prompt)
         bufferedReader = new BufferedReader(
               new InputStreamReader(System.in)
         )
         line = bufferedReader.readLine()
      } catch (exception) {
      } finally {
         try {
            if (bufferedReader != null) {
               bufferReader.close()
            }
         } catch (e) {
         }
      }

      return line
   }

   private void login(String federation, String userName, String password) {
      if (!userName) {
         userName = readLineFromStandardInput(federation + ":User name >> ")
      }
      if (!password) {
         password = Console.readPassword(federation + ":Password >> ")
      }

      LoginInformation loginInformation = new LoginInformation()
      loginInformation.userName = userName
      loginInformation.password = password
      preference.setLoginInformation(federation, loginInformation)
   }

   private boolean isLoggedIn(String federation) {
      return (preference.getLoginInformation(federation) != null)
   }

   private String getFederation(CommandLine commandLine, Option option) {
      String federation = commandLine.getOptionValue(option)
      if (!federation) {
         federation = domain.getDefault()
         log.info("Using default federation: " + federation)
      }

      return federation
   }

   private Session createSession(
         String federation,
         LoginInformation loginInformation
   ) throws Exception {
      return new Session(
            federation,
            loginInformation.userName,
            loginInformation.password,
            preference.getDomainFile().getAbsolutePath()
      )
   }

   private void closeSession(Session session) {
      try {
         if (session != null) {
            session.close()
         }
      } catch (exception) {
      }
   }

   private void prepareCommandLineParser(
         CommandLineParser commandLineParser,
         String commandName
   ) {
      commandLineParser.inputs.clear()
      commandLineParser.options.clear()
      commandLineParser.options.add(OPERATIONS[commandName])

      Map command = COMMANDS[commandName]
      def addAll = { list, map ->
         map.each { key, value ->
            list.add(value)
         }
      }
      addAll(commandLineParser.options, command.OPTIONS)
      addAll(commandLineParser.inputs, command.INPUTS)
   }

   private List getInvalidArguments(
         CommandLineParser commandLineParser,
         String[] arguments
   ) {
      List invalidCandidates = arguments.findAll { argument ->
         argument.startsWith("-")
      }
      List invalidArguments = invalidCandidates.findAll { candidate ->
         Option targetOption = commandLineParser.options.find { option ->
            option.prefixes.find { prefix -> prefix.name == candidate } != null
         }
         return targetOption == null
      }

      return invalidArguments
   }

   private void addProduct(
         String federation,
         LoginInformation loginInformation,
         String productType,
         String productFile,
         String localRepo = null
   ) throws Exception {
      Session session = null
      try {
         session = createSession(federation, loginInformation)
         session.open()

         String defaultCurrentDirectory = session.currentDir
         File file = new File(productFile)

         if ((!productType) || ("".equals(productType.trim()))) {
            List<String> productTypes = retrieveProductTypes(file)
            if (productTypes.size() == 1) {
               productType = productTypes.get(0)
            } else {
               throw new Exception(
                     "None or more than one of product types found: "
                           + productTypes.join(", ") + "."
               )
            }
         }
         log.debug("ProductType: " + productType)

         String currentDirectory = defaultCurrentDirectory
         if (file.getParentFile()) {
            currentDirectory = file.getParentFile().getAbsolutePath()
         }
         log.debug("dir: " + currentDirectory + ", name: " + file.getName())

         session.setCurrentDir(currentDirectory)
         session.add(productType, file.getName())
         while (session.transactionCount > 0) {
            Result result = session.result(60)

            boolean success = false
            if (result) {
               log.debug(
                     "[ " + result.getErrno() + " ] Action=Add, ProductType="
                           + productType + ", File=" + file.getName()
               )
               log.info("\t" + result.getDescription())
               if (result.getErrno() == Errno.OK) {
                  success = true
               }
               else if(result.getErrno() == Errno.IGNORED) {
                  cleanUpIgnored(localRepo, productType, productFile)
                  success = true
               }
            }
            if (!success) {
               throw new Exception(result.getDescription())
            }
         }
      } catch (SessionException exception) {
         throw exception
      } finally {
         closeSession(session)
      }
   }

   private void replaceProduct(
         String federation,
         LoginInformation loginInformation,
         String productType,
         String previousProductName,
         String newProductFile,
         String localRepo = null
   ) throws Exception {
      Session session = null
      try {
         session = createSession(federation, loginInformation)
         session.open()

         String defaultCurrentDirectory = session.currentDir
         File file = new File(newProductFile)

         if ((!productType) || ("".equals(productType.trim()))) {
            List<String> productTypes = retrieveProductTypes(file)
            if (productTypes.size() == 1) {
               productType = productTypes.get(0)
            } else {
               throw new Exception(
                     "None or more than one of product types found: "
                           + productTypes.join(", ") + "."
               )
            }
         }
         log.debug("ProductType: " + productType)

         String currentDirectory = defaultCurrentDirectory
         if (file.getParentFile()) {
            currentDirectory = file.getParentFile().getAbsolutePath()
         }
         log.debug("dir: " + currentDirectory + ", name: " + file.getName())

         session.setCurrentDir(currentDirectory)
         if (previousProductName) {
            session.replace(productType, previousProductName, file.getName())
         } else {
            session.replace(productType, file.getName())
         }
         while (session.transactionCount > 0) {
            Result result = session.result(60)

            boolean success = false
            if (result) {
               log.info(
                     "[ " + result.getErrno() + " ] Action=Replace, ProductType="
                           + productType + ", File=" + file.getName()
               )
               log.info("\t" + result.getDescription())
               if (result.getErrno() == Errno.OK) {
                  success = true
               }
               else if(result.getErrno() == Errno.IGNORED) {
                  cleanUpIgnored(localRepo, productType, newProductFile)
                  success = true
               }
            }
            if (!success) {
               throw new Exception(result.description)
            }
         }
      } catch (SessionException exception) {
         throw exception
      } finally {
         closeSession(session)
      }
   }
   
   private void cleanUpIgnored(
      String localRepo, 
      String productType, 
      String sipPath
   ) {
      String daDir = "${localRepo}${File.separator}staging${File.separator}data"
      String stSubmitted = "${localRepo}${File.separator}staging${File.separator}submitted"
      String erSubmit = "${localRepo}${File.separator}error${File.separator}submission"
      
      File sip = new File(sipPath)
      String name = sip.name.substring(0, sip.name.indexOf('.xml'))
      try {

         log.info ("${productType}:${name} archive IGNORED.  Purging from local staging.")
         sip.delete()
         // purge ingested product
         File productData = new File("${daDir}${File.separator}${productType}${File.separator}${name}")
         productData.deleteDir()
      }
      catch(Exception e) {
         log.error("Could not perform clean up of ignored SIP", e)
      }
   }

   private void resubmitPendingServiceProfiles(
         String federation,
         LoginInformation loginInformation,
         File pendingDirectory,
         File submittedDirectory,
         File errorDirectory
   ) throws Exception {
      def digFiles
      def resubmit

      digFiles = { actionSignature, File originalFile, File file ->
         if (!file.getName().startsWith(".")) {
            if (file.isDirectory()) {
               file.eachFile {
                  digFiles(actionSignature, originalFile, it)
               }
               if (file.getAbsolutePath() != originalFile.getAbsolutePath()) {
                  RepositoryUtility.deleteDirectory(file, true)
               }
            } else {
               log.info("Resubmitting " + file.getAbsolutePath())
               resubmit(actionSignature, file)
            }
         }
      }

      resubmit = { actionSignature, File file ->
         ServiceProfile serviceProfile =
            ServiceProfileFactory.instance.createServiceProfileFromMessage(file)
         SPHeader header = serviceProfile?.submission?.header
         if (header) {
            boolean submitted = false
            try {
               List<Product> products = listProducts(
                     federation,
                     loginInformation,
                     header.productType,
                     header.productName
               )
               if ((products.size() == 1) && (State.ABORTED == products.get(0).getState())) {
                  File saveDirectory = RepositoryUtility.createDirectory(errorDirectory)
                  file.renameTo(new File(
                        saveDirectory.absolutePath + File.separator + file.name
                  ))
                  FileUtility.writeAll(
                        new File(saveDirectory.absolutePath + File.separator + file.name + ".err"),
                        "Failed to perform further action due to ABORTED state."
                  )
               } else {
                  replaceProduct(
                        federation,
                        loginInformation,
                        header.productType,
                        null,
                        file.absolutePath
                  )
                  submitted = true
               }
            } catch (Exception exception) {
               log.warn("Failed to resubmit: " + exception.getMessage())
               log.debug("Failed to resubmit.", exception)
            }

            if (submitted) {
               log.debug("Resubmitted. Moving SIP: " + submittedDirectory.getAbsolutePath() + File.separator + file.getName())
               file.renameTo(new File(
                     submittedDirectory.getAbsolutePath() + File.separator + file.getName()
               ))
            }
         }
      }
   }

   private List<Product> listProducts(
         String federation,
         LoginInformation loginInformation,
         String productType,
         String productName
   ) throws Exception {
      List<Product> products = null
      Session session = null
      try {
         session = createSession(federation, loginInformation)
         session.open()

         session.list(productType, [productName] as String[])
         while (session.transactionCount > 0) {
            Result result = session.result(60)
            if (result) {
               products = (List<Product>) result.getProducts()
            }
         }
      } catch (SessionException exception) {
         throw exception
      } finally {
         closeSession(session)
      }

      return (products) ? products : []
   }

   private List<String> retrieveProductTypes(File file) throws Exception {
      LinkedList<String> productTypes = new LinkedList<String>()

      ServiceProfile serviceProfile =
         ServiceProfileFactory.getInstance().createServiceProfileFromMessage(file)
      SPHeader header = serviceProfile?.submission?.header
      if (!header) {
         throw new ServiceProfileException("Missing submission header")
      }
      productTypes << header.productType
      return productTypes
   }

   private String retrieveProductName(File sipFile) {
      String name = null
      ServiceProfile serviceProfile =
         ServiceProfileFactory.getInstance().createServiceProfileFromMessage(sipFile)
      SPHeader header = serviceProfile?.submission?.header
      if (!header) {
         throw new ServiceProfileException("Missing submission header")
      }
      name = header.productName
      return name
   }
   
   private String retrieveProductUniqueDir(File sipFile) {
      String name = null
      name = sipFile.name.substring(0, sipFile.name.indexOf('.xml'))
      return name
   }
   
   public static void main(String[] args) {
      try {
         IngestClient ingestClient = new IngestClient()
         ingestClient.run(args)
      } catch (Exception e) {
         if (log.isDebugEnabled()) {
            IngestClient.log.debug(e.message, e)
         }
         IngestClient.log.error(e.message)
      }
   }
}
