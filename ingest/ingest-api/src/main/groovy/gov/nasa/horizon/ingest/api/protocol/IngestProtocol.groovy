package gov.nasa.horizon.ingest.api.protocol

import gov.nasa.horizon.ingest.api.jaxb.protocol.*
import gov.nasa.horizon.ingest.api.*
import groovy.xml.MarkupBuilder
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import javax.xml.bind.JAXBException

/**
 * Implementation of the DMAS wire protocol that supports marshalling
 * and unmarshalling data in XML.
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id:$
 * @since 1.0.0
 */
class IngestProtocol extends JAXBBinder {
   private static Log log = LogFactory.getLog(IngestProtocol.class)

   static final String SCHEMA_ENV = "ingest.protocol.schema"
   static final String PROTOCOL_SCHEMA = "horizon_ingest_protocol.xsd"
   static final String JAXB_CONTEXT =
      "gov.nasa.horizon.ingest.api.jaxb.protocol"
   static URL schemaUrl

   static {
      if (System.getProperty(IngestProtocol.SCHEMA_ENV) != null) {
         File schemaFile =
            new File(System.getProperty(IngestProtocol.SCHEMA_ENV)
                  + File.separator + IngestProtocol.PROTOCOL_SCHEMA);
         if (schemaFile.exists()) {
            try {
               IngestProtocol.schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               log.debug(e.message, e)
            }
         }
      }

      if (IngestProtocol.schemaUrl == null) {
         IngestProtocol.schemaUrl =
            IngestProtocol.class.getResource("/META-INF/schemas/"
                  + IngestProtocol.PROTOCOL_SCHEMA)
      }
   }


   def IngestProtocol() {
      super(JAXB_CONTEXT, schemaUrl, true)
      try {
         jaxbObj = new Horizon()
      } catch (JAXBException e) {
         log.error(e.message, e)
      }
   }

   /**
    * Factory method to create a login request protocol object.
    *
    * @param user the user name
    * @param password the user password
    * @param productType the target product type
    * @return the request object
    */
   static synchronized IngestProtocol createLoginRequest(String user, String password, String productType) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.LOGIN
      request.jaxbObj.login = new LoginType()
      if (productType)
         request.productType = productType
      request.user = user
      request.loginPassword = password
      return request
   }

   /**
    * Factory method to create a client add request protocol object.
    *
    * @param sessionId the user session identifier
    * @param sessionToken the unique user session token
    * @param userId the user identifier
    * @param productType the target product type
    * @param product the name of the product to be added
    * @return the request object
    */
   static synchronized IngestProtocol createClientAddRequest(
         long sessionId,
         String sessionToken,
         long userId,
         String productType,
         String product) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.CLIENT_ADD
      request.jaxbObj.add = new Horizon.Add()
      request.sessionId = sessionId
      request.sessionToken = sessionToken
      request.userId = userId
      request.productType = productType
      request.product = product
      return request
   }

   /**
    * Factory method to create a client replace request protocol object.
    *
    * @param sessionId the user session identifier
    * @param sessionToken the unique user session token
    * @param userId the user identifier
    * @param productType the target product type
    * @param product the name of the product to be added
    * @param originalProduct the name of the product to be replaced
    * @return the request object
    */
   static synchronized IngestProtocol createClientReplaceRequest(
         long sessionId,
         String sessionToken,
         long userId,
         String productType,
         String product,
         String originalProduct) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.CLIENT_REPLACE
      request.jaxbObj.add = new Horizon.Add()
      request.sessionId = sessionId
      request.sessionToken = sessionToken
      request.userId = userId
      request.productType = productType
      request.product = product
      request.originalProduct = originalProduct
      return request
   }

   static synchronized IngestProtocol createClientListProductRequest(
         long sessionId,
         String sessionToken,
         long userId,
         String query) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.CLIENT_LIST
      request.jaxbObj.list = new ListType()
      request.sessionId = sessionId
      request.sessionToken = sessionToken
      request.userId = userId
      request.query = query
      return request
   }

   /**
    * Factory method to create a client delete request protocol object.
    *
    * @param sessionId the user session identifier
    * @param sessionToken the unique user session token
    * @param userId the user identifier
    * @param productType the target product type
    * @param product the name of the product to be added
    * @return the request object
    */
   static synchronized IngestProtocol createClientDeleteRquest(
         long sessionId,
         String sessionToken,
         long userId,
         String productType,
         String product) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.CLIENT_DELETE
      request.sessionId = sessionId
      request.sessionToken = sessionToken
      request.userId = userId
      request.productType = productType
      request.product = product
      return request
   }

   /**
    * Factory method to create an engine boot request protocol object.
    * This is used at an engin boot time to query for its configuration
    * information.
    *
    * @param sessionId the user session identifier
    * @param sessionToken the unique user session token
    * @param userId the user identifier
    * @param federation the name of the federation the engine belongs to
    * @param engine the name of the engine
    * @return the request object
    */
   static synchronized IngestProtocol createEngineBootRequest(
         long sessionId,
         String sessionToken,
         long userId,
         String federation,
         String engine) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.ENGINE_BOOT
      request.jaxbObj.boot = new EngineBootType()

      request.sessionId = sessionId
      request.sessionToken = sessionToken
      request.userId = userId
      request.federation = federation
      request.engine = engine
      return request
   }

   /**
    * Factory method to create an engine ping request protocol object.
    * This is used by the middleware to determine the livelihood of an
    * engine.
    *
    * @param federation the federation of the engine
    * @param federationId the federation identifier
    * @param engine the name of the engine
    * @param engineId the engine identifier
    * @return the request object
    */
   static synchronized IngestProtocol createEnginePingRequest(
         String federation,
         long federationId,
         String engine,
         long engineId) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.ENGINE_PING
      request.jaxbObj.ping = new EngineBootType()
      request.federation = federation
      request.federationId = federationId
      request.engine = engine
      request.engineId = engineId
      return request
   }

   /**
    * Factory method to create an engine purge request protocol object.
    * This is used by the middleware to direct a remote agent to purge
    * a product from its trashbin.
    *
    * @param stereotype the stereotype of this ingest operation (INGEST/ARCHIVE)
    * @param federation the federation of the engine
    * @param federationId the federation identifier
    * @param productType the product type
    * @param productTypeId the product type identifier
    * @param product the product name
    * @param productId the product identifier
    * @param jobId the engine job identifier
    */
   static synchronized IngestProtocol createEnginePurgeRequest(
         Stereotype stereotype,
         String federation,
         long federationId,
         String productType,
         long productTypeId,
         String product,
         long productId,
         long jobId
   ) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.ENGINE_PURGE
      request.stereotype = stereotype
      request.federation = federation
      request.federationId = federationId
      request.productType = productType
      request.productTypeId = productTypeId
      request.product = product
      request.productId = productId
      request.jobId = jobId
      return request
   }

   /**
    * Factory method to create an engine job query request protocol object.
    * This is used by the middleware to determine if the engine is serving
    * an previously assigned job.
    *
    * @param jobOperation the operation name for the job
    * @param federation the federation of the engine
    * @param engine the name of the engine
    * @param productType the product type of the job
    * @param product the name of the product.
    * @return the request object
    */
   static synchronized IngestProtocol createEngineJobQueryRequest(
         Lock jobOperation,
         String federation,
         String engine,
         String productType,
         String product) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.ENGINE_JOB
      request.jaxbObj.job = new EngineJobQueryType()
      request.jobOperation = jobOperation
      request.federation = federation
      request.engine = engine
      request.productType = productType
      request.product = product
      return request
   }


   static synchronized IngestProtocol createEngineMoveRequest(
         Stereotype stereotype,
         String federation,
         long federationId,
         String productType,
         long productTypeId,
         String product,
         long productId,
         String metadataFilename,
         String metadataText,
         String source,
         String destination
   ) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.ENGINE_MOVE
      request.jaxbObj.move = new MoveType()
      request.stereotype = stereotype
      request.federation = federation
      request.federationId = federationId
      request.productType = productType
      request.productTypeId = productTypeId
      request.product = product
      request.productId = productId
      request.moveMetadataFile = metadataFilename
      request.moveMetadataText = metadataText
      request.moveSource = source
      request.moveDestination = destination
      return request
   }

   /**
    * Factory method to create an engine ingest request protocol object.
    * This is used by the middleware to assign an ingestion job to a
    * given engine.
    *
    * @param stereotype the stereotype of this ingest operation (INGEST/ARCHIVE)
    * @param federation the federation of the engine
    * @param federationId the federation identifier
    * @param productType the product type name
    * @param prodjctTypeId the product type identifier
    * @param product the product name
    * @param productId the product identifier
    * @return the request object
    */
   static synchronized IngestProtocol createEngineIngestRequest(
         Stereotype stereotype,
         String federation,
         long federationId,
         String productType,
         long productTypeId,
         String product,
         long productId
   ) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.ENGINE_INGEST
      request.jaxbObj.add = new Horizon.Add()
      request.stereotype = stereotype
      request.federation = federation
      request.federationId = federationId
      request.productType = productType
      request.productTypeId = productTypeId
      request.product = product
      request.productId = productId
      return request
   }

   /**
    * Factory method to create an engine delete request protocol object.
    * This is used by the middleware to file deletion job to an engine.
    *
    * @param stereotype the stereotype of this ingest operation (INGEST/ARCHIVE)
    * @param federation the federation of the engine
    * @param federationId the federation identifier
    * @param productType the product type name
    * @param prodjctTypeId the product type identifier
    * @param product the product name
    * @param productId the product identifier
    * @return the request object
    */
   static synchronized IngestProtocol createEngineDeleteRequest(
         Stereotype stereotype,
         String federation,
         long federationId,
         String productType,
         long productTypeId,
         String product,
         long productId
   ) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.ENGINE_DELETE
      request.jaxbObj.delete = new DeleteType()
      request.stereotype = stereotype
      request.federation = federation
      request.federationId = federationId
      request.productType = productType
      request.productTypeId = productTypeId
      request.product = product
      request.productId = productId
      return request
   }

   /**
    * Factory method to create a logout request.
    *
    * @param productType the product type name
    * @return the request object
    */
   static synchronized IngestProtocol createLogoutRequest(String productType) {
      IngestProtocol request = new IngestProtocol()
      request.operation = Opcode.LOGOUT
      request.productType = productType
      return request
   }

   /**
    * Method to get the operation name
    *
    * @return the operation name
    */
   Opcode getOperation() {
      if (jaxbObj.operation) {
         return Opcode.valueOf(jaxbObj.operation.toUpperCase())
      }
      return null
   }

   /**
    * Method to set the operation name
    *
    * @param the operation name
    */
   void setOperation(Opcode operation) throws ProtocolException {
      try {
         if (operation) {
            jaxbObj.operation = operation.toString()
         }
      } catch (Exception e) {
         throw new ProtocolException(e.message, e)
      }
   }

   /**
    * Method to return the integer errno
    *
    * @retun the errno
    */
   Errno getErrno() {
      if (jaxbObj.errno) {
         return Errno.valueOf(jaxbObj.errno.toUpperCase())
      }
      return null
   }

   void setErrno(Errno errno) {
      if (errno) {
         jaxbObj.errno = errno.toString()
      }
   }

   String getDescription() {
      return jaxbObj.description
   }

   void setDescription(String description) {
      jaxbObj.description = description
   }

   Long getSessionId() {
      return jaxbObj.sessionId?.toLong()
   }

   void setSessionId(long sessionId) {
      jaxbObj.sessionId = BigInteger.valueOf(sessionId)
   }

   String getSessionToken() {
      return jaxbObj.sessionToken
   }

   void setSessionToken(String sessionToken) {
      jaxbObj.sessionToken = sessionToken
   }

   Long getUserId() {
      return jaxbObj.userId
   }

   void setUserId(long userId) {
      jaxbObj.userId = userId
   }

   String getUser() {
      return jaxbObj.user
   }

   void setUser(String user) {
      jaxbObj.user = user
   }

   String getUserType() {
      return jaxbObj.userType
   }

   void setUserType(String userType) {
      jaxbObj.userType = userType
   }

   Long getProductTypeId() {
      return jaxbObj.productTypeId?.toLong()
   }

   void setProductTypeId(long productTypeId) {
      jaxbObj.productTypeId = BigInteger.valueOf(productTypeId)
   }

   String getProductType() throws ProtocolException {
      return jaxbObj.productType
   }


   void setNotify(boolean notify) {
      jaxbObj.setNotify(notify)
   }

   boolean isNotify() {
      return jaxbObj.isNotify()
   }

   void setProductType(String productType) throws ProtocolException {
      jaxbObj.productType = productType
   }

   Long getProductId() {
      return jaxbObj.productId?.toLong()
   }

   void setProductId(long productId) {
      jaxbObj.productId = BigInteger.valueOf(productId)
   }

   String getProduct() {
      return jaxbObj.product
   }

   void setProduct(String product) {
      jaxbObj.product = product
   }

   Long getJobId() {
      return jaxbObj.jobId?.toLong()
   }

   void setJobId(long jobId) {
      jaxbObj.jobId = BigInteger.valueOf(jobId)
   }

   Stereotype getStereotype() {
      if (jaxbObj.stereotype) {
         return Stereotype.valueOf(jaxbObj.stereotype.toUpperCase())
      }
      return null
   }

   void setStereotype(Stereotype stereotype) {
      jaxbObj.stereotype = stereotype.toString()
   }

   protected LoginType getLogin() throws ProtocolException {
      if (operation != Opcode.LOGIN) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (!jaxbObj.login) {
         jaxbObj.login = new LoginType()
      }

      return jaxbObj.login
   }

   String getLoginPassword() throws ProtocolException {

      return login.password
   }

   void setLoginPassword(String password) throws ProtocolException {
      login.password = password
   }

   Date getLoginIssueTime() throws ProtocolException {
      if (login.issueTime) {
         return new Date(login.issueTime.toLong())
      }
      return null
   }

   void setLoginIssueTime(Date issueTime) throws ProtocolException {
      if (!issueTime)
         login.issueTime = null
      else
         login.issueTime = BigInteger.valueOf(issueTime.time)
   }

   void setLoginIssueTime(long issueTime) throws ProtocolException {
      login.issueTime = BigInteger.valueOf(issueTime)
   }

   Date getLoginExpireTime() throws ProtocolException {
      if (login.expireTime) {
         return new Date(login.expireTime.toLong())
      }
      return null
   }

   void setLoginExpireTime(Date expireTime) throws ProtocolException {
      if (!expireTime) {
         login.expireTime = null
      } else {
         login.expireTime = BigInteger.valueOf(expireTime.time)
      }
   }

   void setLoginExpireTime(long expireTime) throws ProtocolException {
      login.expireTime = BigInteger.valueOf(expireTime)
   }

   def getOpObj() {
      switch (operation) {
         case Opcode.CLIENT_ADD:
            return jaxbObj.add
         case Opcode.CLIENT_REPLACE:
            return jaxbObj.add
         case Opcode.CLIENT_DELETE:
            return jaxbObj.delete
         case Opcode.CLIENT_LIST:
            return jaxbObj.list
         case Opcode.ENGINE_BOOT:
            return jaxbObj.boot
         case Opcode.ENGINE_HOTBOOT:
            return jaxbObj.hotboot
         case Opcode.ENGINE_PING:
            return jaxbObj.ping
         case Opcode.ENGINE_INGEST:
            return jaxbObj.add
         case Opcode.ENGINE_DELETE:
            return jaxbObj.delete
         case Opcode.ENGINE_JOB:
            return jaxbObj.job
         default:
            return null
      }
   }

   Date getOperationStartTime() throws ProtocolException {
      if ([Opcode.LOGIN,
            Opcode.LOGOUT,
            Opcode.ENGINE_BOOT,
            Opcode.ENGINE_PING,
            Opcode.ENGINE_JOB,
            Opcode.ENGINE_HOTBOOT].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (opObj.operationTime && opObj.operationTime.start) {
         return new Date(opObj.operationTime.start.toLong())
      }
      return null
   }

   void setOperationStartTime(Date startTime) throws ProtocolException {
      if ([Opcode.LOGIN,
            Opcode.LOGOUT,
            Opcode.ENGINE_BOOT,
            Opcode.ENGINE_PING,
            Opcode.ENGINE_JOB,
            Opcode.ENGINE_HOTBOOT].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (!opObj.operationTime) {
         opObj.operationTime = new CommonTimeStampType()
      }
      if (!startTime)
         opObj.operationTime.start = null
      else {
         opObj.operationTime.start = BigInteger.valueOf(startTime.time)
      }
   }

   void setOperationStartTime(long startTime) throws ProtocolException {
      if ([Opcode.LOGIN,
            Opcode.LOGOUT,
            Opcode.ENGINE_BOOT,
            Opcode.ENGINE_PING,
            Opcode.ENGINE_JOB,
            Opcode.ENGINE_HOTBOOT].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (!opObj.operationTime) {
         opObj.operationTime = new CommonTimeStampType()
      }
      opObj.operationTime.start = BigInteger.valueOf(startTime)
   }

   Date getOperationStopTime() throws ProtocolException {
      if ([Opcode.LOGIN,
            Opcode.LOGOUT,
            Opcode.ENGINE_BOOT,
            Opcode.ENGINE_PING,
            Opcode.ENGINE_JOB,
            Opcode.ENGINE_HOTBOOT].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (opObj.operationTime && opObj.operationTime.stop) {
         return new Date(opObj.operationTime.stop.toLong())
      }
      return null
   }

   void setOperationStopTime(Date stopTime) throws ProtocolException {
      if ([Opcode.LOGIN,
            Opcode.LOGOUT,
            Opcode.ENGINE_BOOT,
            Opcode.ENGINE_PING,
            Opcode.ENGINE_JOB,
            Opcode.ENGINE_HOTBOOT].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }
      if (!opObj.operationTime) {
         opObj.operationTime = new CommonTimeStampType()
      }
      if (!stopTime)
         opObj.operationTime.stop = null
      else {
         opObj.operationTime.stop = BigInteger.valueOf(stopTime.time)
      }
   }

   void setOperationStopTime(long stopTime) throws ProtocolException {
      if ([Opcode.LOGIN,
            Opcode.LOGOUT,
            Opcode.ENGINE_BOOT,
            Opcode.ENGINE_PING,
            Opcode.ENGINE_JOB,
            Opcode.ENGINE_HOTBOOT].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (!opObj.operationTime) {
         opObj.operationTime = new CommonTimeStampType()
      }

      opObj.operationTime.stop = BigInteger.valueOf(stopTime)

   }

   protected Horizon.Add getAdd() throws ProtocolException {
      if (!([Opcode.CLIENT_ADD, Opcode.CLIENT_REPLACE, Opcode.ENGINE_INGEST].contains(operation))) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (!jaxbObj.add) {
         jaxbObj.add = new Horizon.Add()
      }
      return jaxbObj.add
   }

   protected List<CommonFileType> convertToFileList(List<ProductFile> files) {
      List result = []

      files.each { ProductFile pf ->
         CommonFileType newfile = new CommonFileType()
         newfile.name = pf.name
         newfile.source = pf.source
         newfile.sourceUsername = pf.sourceUsername
         newfile.sourcePassword = pf.sourcePassword
         newfile.maxConnections = pf.maxConnections
         newfile.destination = new CommonDestinationType()
         newfile.destination.location = pf.destination
         newfile.destination.link = pf.links
         newfile.size = BigInteger.valueOf(pf.size)
         newfile.checksum = pf.checksum

         CommonTimeStampType timestamp = new CommonTimeStampType()

         if (pf.startTime) {
            newfile.processTime = timestamp
            timestamp.start = BigInteger.valueOf(pf.startTime.time)
         }

         if (pf.stopTime) {
            newfile.processTime = timestamp
            timestamp.stop = BigInteger.valueOf(pf.stopTime.time)
         }

         if (pf.description) {
            newfile.description = pf.description
         }

         result << newfile
      }

      return result
   }

   protected List<ProductFile> convertToProductFileList(List<CommonFileType> files) {
      List<ProductFile> result = []

      files.each { CommonFileType cft ->
         ProductFile file = new ProductFile(
               name: cft.name,
               source: cft.source,
               sourceUsername: cft.sourceUsername,
               sourcePassword: cft.sourcePassword,
               maxConnections: cft.maxConnections,
               destination: cft.destination?.location,
               links: cft.destination?.link,
               size: cft.size.longValue(),
               checksum: cft.checksum,
               description: cft.description
         )

         if (cft.processTime) {
            if (cft.processTime.start)
               file.startTime = new Date(cft.processTime.start.toLong())

            if (it.processTime.stop)
               file.stopTime = new Date(cft.processTime.stop.toLong())
         }

         result << file
      }
      return result
   }

   void setAddFiles(List<ProductFile> files) throws ProtocolException {
      if (!add.files) {
         add.files = new Horizon.Add.Files()
      }

      add.files.file = this.convertToFileList(files)
   }

   /*
   void setAddFiles(List<CommonFileType> files) throws ProtocolException {
      if (!add.files) {
         add.files = new Horizon.Add.Files()
      }

      // the conversion is not necessary
      //this.convertToFileList(files).each {
      //   add.files.file << it
      //}


      add.files.file = files
   }
   */

   List<ProductFile> getAddFiles() throws ProtocolException {
      if (add.files) {
         return convertToProductFileList(add.files.file)
      }
      return []
   }

   String getAddMetadata() throws ProtocolException {
      if (![Opcode.CLIENT_ADD, Opcode.ENGINE_INGEST].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      return add.metadata
   }

   void setAddMetadata(String metadata) throws ProtocolException {
      if (![Opcode.CLIENT_ADD, Opcode.ENGINE_INGEST].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }
      add.metadata = metadata
   }

   String getOriginalProduct() throws ProtocolException {
      return add.originalProduct
   }

   void setOriginalProduct(String product) throws ProtocolException {
      add.originalProduct = product
   }

   Long getTotalSize() throws ProtocolException {
      if (![Opcode.CLIENT_DELETE, Opcode.ENGINE_DELETE].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }
      if (!jaxbObj.delete) {
         return null
      }
      return jaxbObj.delete.totalSize?.toLong()
   }

   void setTotalSize(long totalSize) throws ProtocolException {
      if (![Opcode.CLIENT_DELETE, Opcode.ENGINE_DELETE].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }
      if (!jaxbObj.delete) {
         jaxbObj.delete = new DeleteType()
      }
      jaxbObj.delete.totalSize = BigInteger.valueOf(totalSize)
   }

   List getTimeRange() throws ProtocolException {
      List result = []
      if (listType.timeRange) {
         result << new Date(listType.timeRange.start.longValue())
         if (listType.timeRange.stop) {
            result << new Date(listType.timeRange.stop.longValue())
         }
      }
      return result
   }

   /**
    * Method to take a list of Date objects to set time range query
    */
   void setTimeRange(List<Date> timeRange) throws ProtocolException {
      if (!listType.timeRange) {
         listType.timeRange = new ListType.TimeRange()
      }
      if (timeRange[0]) {
         listType.timeRange.start = BigInteger.valueOf(timeRange[0].time)
         if (timeRange.size() > 1) {
            listType.timeRange.stop = BigInteger.valueOf(timeRange[1].time)
         }
      }
   }

   List getDeletes() throws ProtocolException {
      if (![Opcode.CLIENT_DELETE, Opcode.ENGINE_DELETE].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (!jaxbObj.delete) {
         jaxbObj.delete = new DeleteType()
      }

      return jaxbObj.delete.delete
   }

   void setDeletes(List deletes) throws ProtocolException {
      if (![Opcode.CLIENT_DELETE, Opcode.ENGINE_DELETE].contains(operation)) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }

      if (!jaxbObj.delete) {
         jaxbObj.delete = new DeleteType()
      }

      jaxbObj.delete.delete = deletes
   }

   protected ListType getListType() throws ProtocolException {
      if (operation != Opcode.CLIENT_LIST) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }
      if (!jaxbObj.list) {
         jaxbObj.list = new ListType()
      }
      return jaxbObj.list
   }

   protected ListProductInfoType getProductResult() throws ProtocolException {
      return listType.productResult
   }

   protected ListProductType getProductTypeResult() throws ProtocolException {
      return listType.productTypeResult
   }

   String getExpression() throws ProtocolException {
      return listType.expression
   }

   void setExpression(String expression) throws ProtocolException {
      listType.expression = expression
   }

   synchronized void addProduct(Product product) throws ProtocolException {
      if (!productResult) {
         listType.productResult = new ListProductInfoType()
      }

      ListProductInfoType.Product p = new ListProductInfoType.Product(
            name: product.name,
            state: product.state.toString(),
            lock: product.lock.toString(),
            metadataText: product.metadataText,
            note: product.note,
            archiveNote: product.archiveNote,
            createdTime: product.createdTime?.time,
            stagedTime: product.stagedTime?.time,
            archivedTime: product.archivedTime?.time)

      if (product.productFiles && product.productFiles.size() > 0) {
         p.files = new ListProductInfoType.Product.Files()

         for (ProductFile pf : product.productFiles) {
            ListProductInfoType.Product.Files.File f = new ListProductInfoType.Product.Files.File()
            f.name = pf.name
            f.size = BigInteger.valueOf(pf.size)
            f.checksum = pf.checksum ? pf.checksum : ''
            p.files.file << f
         }
      }

      productResult.product << p
   }

   synchronized List<Product> getProducts() throws ProtocolException {
      List result = []
      if (!productResult || !productResult.product || productResult.product.size() == 0) {
         return result
      }

      for (ListProductInfoType.Product p : productResult.product) {
         Product product = new Product(name: p.name, state: State.valueOf(p.state), lock: Lock.valueOf(p.lock),
               metadataText: p.metadataText, note: p.note, archiveNote: p.archiveNote)
         if (p.createdTime) {
            product.createdTime = new Date(p.createdTime.longValue())
         }
         if (p.stagedTime) {
            product.stagedTime = new Date(p.stagedTime.longValue())
         }
         if (p.archivedTime) {
            product.archivedTime = new Date(p.archivedTime.longValue())
         }
         if (p.files.file && p.files.file.size() > 0) {
            for (ListProductInfoType.Product.Files.File f : p.files.file) {
               product.productFiles << new ProductFile(name: f.name, size: f.size?.longValue(), checksum: f.checksum)
            }
         }
         result << product
      }
      return result
   }


   protected def getIngestEngine() throws ProtocolException {
      if (operation == Opcode.ENGINE_BOOT) {
         if (!jaxbObj.boot) {
            jaxbObj.boot = new EngineBootType()
         }
         return jaxbObj.boot
      } else if (operation == Opcode.ENGINE_HOTBOOT) {
         if (!jaxbObj.hotboot) {
            jaxbObj.hotboot = new EngineBootType()
         }
         return jaxbObj.hotboot
      } else if (operation == Opcode.ENGINE_PING) {
         if (!jaxbObj.ping) {
            jaxbObj.ping = new EngineBootType()
         }
         return jaxbObj.ping
      } else if (operation == Opcode.ENGINE_JOB) {
         if (!jaxbObj.job) {
            jaxbObj.job = new EngineJobQueryType()
         }
         return jaxbObj.job
      } else {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }
   }

   Long getFederationId() {
      return jaxbObj.federationId?.toLong()
   }

   void setFederationId(long federationId) {
      jaxbObj.federationId = BigInteger.valueOf(federationId)
   }

   String getFederation() {
      return jaxbObj.federation
   }

   void setFederation(String federation) {
      jaxbObj.federation = federation
   }

   Long getEngineId() throws ProtocolException {
      return opObj.engineId?.toLong()
   }

   void setEngineId(long engineId) throws ProtocolException {
      opObj.engineId = BigInteger.valueOf(engineId)
   }

   String getEngine() throws ProtocolException {
      return opObj.engine
   }

   void setEngine(String engine) throws ProtocolException {
      opObj.engine = engine
   }

   String getBind() throws ProtocolException {
      return ingestEngine.bind
   }

   void setBind(String bind) throws ProtocolException {
      ingestEngine.bind = bind
   }

   Integer getUserPort() throws ProtocolException {
      return ingestEngine.userPort
   }

   void setUserPort(int port) throws ProtocolException {
      ingestEngine.userPort = port
   }

   Integer getAdminPort() throws ProtocolException {
      return ingestEngine.adminPort
   }

   void setAdminPort(int port) throws ProtocolException {
      ingestEngine.adminPort = port
   }

   String getProtocol() throws ProtocolException {
      return ingestEngine.protocol
   }

   void setProtocol(String protocol) throws ProtocolException {
      ingestEngine.protocol = protocol
   }

   String getUrlPattern() throws ProtocolException {
      return ingestEngine.urlPattern
   }

   void setUrlPattern(String urlPattern) throws ProtocolException {
      ingestEngine.urlPattern = urlPattern
   }

   Integer getMaxUserConnections() throws ProtocolException {
      return ingestEngine.maxUserConnections
   }

   void setMaxUserConnections(int max) throws ProtocolException {
      ingestEngine.maxUserConnections = max
   }

   Modifier getModifier() throws ProtocolException {
      if (jaxbObj.modifier)
         return Modifier.valueOf(jaxbObj.modifier)
      else return Modifier.NOMODIFIER
   }

   String getMoveDestination() throws ProtocolException {
      return moveType.destination
   }

   String getMoveMetadataFile() throws ProtocolException {
      return moveType.metadataInfo?.filename
   }

   String getMoveMetadataText() throws ProtocolException {
      return moveType.metadataInfo?.text
   }

   String getMoveSource() throws ProtocolException {
      return moveType.source
   }

   MoveType getMoveType() throws ProtocolException {
      if (this.operation != Opcode.ENGINE_MOVE) {
         throw new ProtocolException("Operation not supported for ${operation}.")
      }
      if (!jaxbObj.move) {
         jaxbObj.move = new MoveType()
      }
      return jaxbObj.move
   }

   void setModifier(Modifier modifier) {
      jaxbObj.modifier = modifier.toString()
   }


   void setMoveDestination(String dir) throws ProtocolException {
      moveType.destination = dir
   }

   void setMoveMetadataFile(String name) throws ProtocolException {
      if (!moveType.metadataInfo) {
         moveType.metadataInfo = new MoveType.MetadataInfo()
      }

      moveType.metadataInfo.filename = name
   }

   void setMoveMetadataText(String metadataText) throws ProtocolException {
      if (!moveType.metadataInfo) {
         moveType.metadataInfo = new MoveType.MetadataInfo()
      }
      moveType.metadataInfo.text = metadataText
   }

   void setMoveSource(String granuleDir) throws ProtocolException {
      moveType.source = granuleDir
   }

   Integer getMaxAdminConnections() throws ProtocolException {
      return ingestEngine.maxAdminConnections
   }

   void setMaxAdminConnections(int max) throws ProtocolException {
      ingestEngine.maxAdminConnections = max
   }

   String getStorageLocation() throws ProtocolException {
      return ingestEngine.storageLocation
   }

   void setStorageLocation(String storageLocation) throws ProtocolException {
      ingestEngine.storageLocation = storageLocation
   }

   Date getReceived() throws ProtocolException {

      if (ingestEngine.received) {
         return new Date(ingestEngine.received.toLong())
      }
      return null
   }

   void setReceived(Date received) throws ProtocolException {
      if (!received) {
         ingestEngine.received = null
      }

      ingestEngine.received = BigInteger.valueOf(received.time)
   }

   void setReceived(long received) throws ProtocolException {
      ingestEngine.received = BigInteger.valueOf(received)
   }

   Lock getJobOperation() throws ProtocolException {
      if (ingestEngine.jobOperation) {
         return Lock.valueOf(ingestEngine.jobOperation)
      }
      return null
   }

   void setJobOperation(Lock operation) throws ProtocolException {
      ingestEngine.jobOperation = operation.toString()
   }

   String toRequest() {
      String content = super.toString()
      def writer = new StringWriter()
      def xml = new MarkupBuilder(writer)
      xml.horizon() {
         operation(operation)
         params(content)
      }
      return writer.toString()
   }

}
