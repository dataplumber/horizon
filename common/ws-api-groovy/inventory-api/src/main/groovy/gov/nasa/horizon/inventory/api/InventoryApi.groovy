package gov.nasa.horizon.inventory.api

import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.horizon.common.api.util.DateTimeUtility;
import gov.nasa.horizon.inventory.model.*
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.ast.stmt.ThrowStatement;

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

public class InventoryApi {

   private static Log log = LogFactory.getLog(InventoryApi.class)
   private String baseUrl;
   private String relativePath
   private HTTPBuilder http;

   private String user, pass;

   public InventoryApi(String baseUrl) {
      this.baseUrl = baseUrl
      this.relativePath = "inventory"
      //URI uri = new URI(baseUrl)
      http = new HTTPBuilder(baseUrl)
   }

   public InventoryApi(String baseUrl, String relativePath) {
      this.baseUrl = baseUrl;
      this.relativePath = relativePath;
      //URI uri = new URI(baseUrl)
      http = new HTTPBuilder(baseUrl)
   }

   public void setAuthInfo(String user, String pass) {
      this.user = user;
      this.pass = pass;
   }

   Boolean heartbeat() {
      try {
         http.post(path: "/$relativePath/heartbeat", requestContentType: URLENC, contentType: TEXT, headers: [Accept: 'application/xml']) { resp, txt ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}")
               return false
            }
            return true
         }
      }
      catch (Exception e) {
         return false
      }
   }

   /*
    * SIP API
    * 
    */

   ServiceProfile ingestSip(ServiceProfile sip) {
      return ingestSip(sip, this.user, this.pass)
   }

   ServiceProfile ingestSip(ServiceProfile sip, String userName, String password) throws InventoryException {
      ServiceProfile result = null
      //sip-to-xml
      //println sip.toString()
      //now send the XML
      log.debug "http - POST - /inventory/sip/"
      log.debug "POST Data: ${sip.toString()}"
      def postBody = [sip: "$sip", userName: "$userName", password: "$password"]
      try {
         http.post(path: "/$relativePath/sip", body: postBody, requestContentType: URLENC, contentType: TEXT, headers: [Accept: 'application/xml']) { resp, txt ->
            if (resp.getStatus() != 201) {
               log.info("Status returned non-201 code: ${resp.getStatus()}; ${txt}")
            } else {
               log.debug "Status updated..."

               //create a sip from xml...
               BufferedReader br = new BufferedReader(txt);
               String line = br.readLine()
               StringBuilder sb = new StringBuilder();
               while (line != null) {
                  sb.append(line);
                  line = br.readLine();
               }

               ServiceProfile serviceProfile = ServiceProfileFactory.getInstance().createServiceProfileFromMessage(sb.toString());
               log.debug("Returning sip")
               result = serviceProfile;
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to process SIP.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   ServiceProfile backfillSip(ServiceProfile sip) {
      return ingestSip(sip, this.user, this.pass)
   }

   ServiceProfile backfillSip(ServiceProfile sip, String userName, String password) throws InventoryException {
      ServiceProfile result = null
      //sip-to-xml
      //println sip.toString()
      //now send the XML
      log.debug "http - POST - /inventory/sip/"
      log.debug "POST Data: ${sip.toString()}"
      def postBody = [sip: "$sip", userName: "$userName", password: "$password", backfill:true]
      try {
         http.post(path: "/$relativePath/sip", body: postBody, requestContentType: URLENC, contentType: TEXT, headers: [Accept: 'application/xml']) { resp, txt ->
            if (resp.getStatus() != 201) {
               log.info("Status returned non-201 code: ${resp.getStatus()}; ${txt}")
            } else {
               log.debug "Status updated..."

               //create a sip from xml...
               BufferedReader br = new BufferedReader(txt);
               String line = br.readLine()
               StringBuilder sb = new StringBuilder();
               while (line != null) {
                  sb.append(line);
                  line = br.readLine();
               }

               ServiceProfile serviceProfile = ServiceProfileFactory.getInstance().createServiceProfileFromMessage(sb.toString());
               log.debug("Returning sip")
               result = serviceProfile;
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to process SIP.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   boolean isValidSip(ServiceProfile sip) throws InventoryException {
      ServiceProfile result = null
      //sip-to-xml
      //println sip.toString()
      //now send the XML
      log.debug "http - POST - /inventory/sip/check"
      log.debug "POST Data: ${sip.toString()}"
      def postBody = [sip: "$sip"]
      try {
         http.post(path: "/$relativePath/sip/check", body: postBody, requestContentType: URLENC, contentType: TEXT) { resp, txt ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${txt}")
               return false
            } else {
               return true
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to check SIP.  ${e.message}")
         return false
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }

   /*
    * PRODUCT TYPE API
    */

   //Query Methods
   ProductType getProductType(Long ptId) throws InventoryException {
      ProductType result = null
      try {
         http.get(path: "/$relativePath/productType/$ptId", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToProductType(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find ProductType with ID ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   ProductType getProductType(String ptName) throws InventoryException {
      ProductType result = null
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName"
            if (resp.getStatus() != 200) {
               log.debug("Status returned ${resp.status} code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToProductType(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find ProductType with name ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   Long getLatestProductIdByProductType(Long ptId) throws InventoryException {
      Long result = null
      try {
         http.get(path: "/$relativePath/productType/$ptId/latestProduct/", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/$ptId/latestProduct"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               xml.children().each {
                  log.debug "Value:  ${it.text()}"
                  result = Long.valueOf(it.text().toString())
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find latest Product for Product Type ${ptId}. ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   Long getLatestProductIdByProductTypeName(String ptName) throws InventoryException {
      Long result = null
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/latestProduct/", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/$ptName/latestProduct"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               xml.children().each {
                  log.debug "Value:  ${it.text()}"
                  result Long.valueOf(it.text().toString())
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find latest Product for Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   ProductTypePolicy getProductTypePolicy(Long ptId) throws InventoryException {
      ProductTypePolicy result = null
      try {
         http.get(path: "/$relativePath/productType/$ptId/policy", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/policy"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            }
            result = mapXMLToPolicy(xml)
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to get policy for Product Type ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   ProductTypePolicy getProductTypePolicy(String ptName) throws InventoryException {
      ProductTypePolicy result = null
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/policy", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/$ptName/policy"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToPolicy(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to get policy for Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   List<ProductTypeLocationPolicy> getProductTypeLocationPolicies(Long ptId) throws InventoryException{
      List<ProductType> result = []
      try {
         http.get(path: "/$relativePath/productType/$ptId", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToLocationPolicies(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find ProductType with ID ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   ProductTypeLocationPolicy getProductTypeLocationPolicies(String ptName) throws InventoryException{
      List<ProductType> result = []
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToLocationPolicies(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find ProductType with ID ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   ProductTypeCoverage getProductTypeCoverage(Long ptId) throws InventoryException {
      ProductTypeCoverage result = null
      try {
         http.get(path: "/$relativePath/productType/$ptId/coverage", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/coverage"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")

            } else {
               result = mapXMLToCoverage(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to get coverage for Product Type ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   ProductTypeCoverage getProductTypeCoverage(String ptName) throws InventoryException {
      ProductTypeCoverage result = null
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/coverage", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/coverage"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToCoverage(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to get coverage for Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   Integer getProductCount(Long ptId) throws InventoryException{
      Integer result = null
      try {
         http.get(path: "/$relativePath/productType/$ptId/productCount/", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productType/$ptId/productCount/"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = xml.int.text() as Integer
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to get coverage for Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   Integer getProductCount(String ptName) throws InventoryException{
      Integer result = null
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/productCount", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/productCount"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = xml.int.text() as Integer
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to get coverage for Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   List<ProductType> getProductTypes() throws InventoryException {
      try {
         http.get(path: '/$relativePath/productTypes', contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypes"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return;
            }
            def result = []
            for (ptXML in xml.ProductType) {
               ProductType pt = new ProductType()
               pt.id = ptXML.id.text() as Long
               pt.identifier = ptXML.identifier.text()
               pt.description = ptXML.description.text()
               result << pt
            }
            return result
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to list Product Types. ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }

   /*
    * PRODUCT API
    * 
    */

   //Access Methods
   
   boolean updateProduct(Long productId, String root, Long ptId) throws InventoryException{
      return updateProduct(productId, root, ptId, this.user, this.pass);
   }
   boolean updateProduct(Long productId, String root, Long ptId, String userName, String password) throws InventoryException{
      //http://localhost:8080/inventory/granule/55/status/?status=ONLINE&userName=axt&password=axt388
      log.debug "http - POST - /inventory/product/$productId/update/"
      def postBody = [ptId: "$ptId", rootPath: "$root", userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/product/$productId/update/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Product updated..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to update Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   boolean updateProductStatus(Long productId, String status) throws InventoryException {
      return updateProductStatus(productId, status, this.user, this.pass)
   }
   boolean updateProductStatus(Long productId, String status, String userName, String password) throws InventoryException {
      //http://localhost:8080/inventory/granule/55/status/?status=ONLINE&userName=axt&password=axt388
      log.debug "http - POST - /inventory/product/$productId/status/"
      def postBody = [status: "$status", userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/product/$productId/status/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Status updated..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to update status for Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   boolean updateProductStatus(String ptName, String productName, String status) throws InventoryException {
      return updateProductStatus(ptName, productName, status, this.user, this.pass)
   }
   boolean updateProductStatus(String ptName, String productName, String status, String userName, String password) throws InventoryException {
      log.debug "http - POST - /$relativePath/productTypeByName/$ptName/productByName/$productName/status/"
      def postBody = [status: "$status", userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/productByName/$productName/status/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Status updated..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to update status for Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   boolean deleteProduct(Long productId, Boolean dataOnly) throws InventoryException{
      return deleteProduct(productId, dataOnly, this.user, this.pass)
   }
   boolean deleteProduct(Long productId, Boolean dataOnly, String userName, String password) throws InventoryException{
      //http://localhost:8080/inventory/granule/55/status/?status=ONLINE&userName=axt&password=axt388
      log.debug "http - POST - /inventory/product/$productId/delete"
      def postBody = [userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/product/$productId/delete/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Product $productId deleted..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to delete Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   boolean updateProductArchiveStatus(Long productId, String status) throws InventoryException {
      updateProductArchiveStatus(productId, status, this.user, this.pass)
   }
   boolean updateProductArchiveStatus(Long productId, String status, String userName, String password) throws InventoryException {
      log.debug "http - POST - /$relativePath/product/$productId/archive/status/"
      def postBody = [status: "$status", userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/product/$productId/archive/status/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Status updated..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to update status for Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   boolean updateProductArchiveChecksum(Long productId, String name, String checksum) throws InventoryException {
      updateProductArchiveChecksum(productId, name, checksum, this.user, this.pass)
   }
   boolean updateProductArchiveChecksum(Long productId, String name, String checksum, String userName, String password) throws InventoryException {
      log.debug "http - POST - /$relativePath/product/$productId/archive/checksum/"
      def postBody = [checksum: "$checksum", name:"$name", userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/product/$productId/archive/checksum/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Checksum updated..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to update checksum for Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   boolean updateProductArchiveSize(Long productId, String name, Long fsize) throws InventoryException {
      updateProductArchiveSize(productId, name, fsize, this.user, this.pass)
   }
   boolean updateProductArchiveSize(Long productId, String name, Long fsize, String userName, String password) throws InventoryException {
      log.debug "http - POST - /$relativePath/product/$productId/archive/size/"
      def postBody = [fileSize: "$fsize", name:"$name", userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/product/$productId/archive/size/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Size updated..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to update size for Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   boolean updateProductRootPath(Long productId, String root) throws InventoryException {
      updateProductRootPath(productId, root, this.user, this.pass)
   }
   boolean updateProductRootPath(Long productId, String root, String userName, String password) throws InventoryException {
      log.debug "http - POST - /$relativePath/product/$productId/rootPath/"
      def postBody = [rootPath: "$root", userName: "$userName", password: "$password"]

      try {
         http.post(path: "/$relativePath/product/$productId/rootPath/", body: postBody, requestContentType: URLENC) { resp ->
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return false;
            }
            log.debug "Rootpath updated..."
            return true;
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to update size for Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   //Query Methods
   Product getProduct(Long productId) throws InventoryException {
      Product result = null
      try {
         http.get(path: "/$relativePath/product/$productId", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/product/$productId"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToProduct(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   Product getProduct(String ptName, String productName) throws InventoryException {
      Product result = null
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/productByName/$productName", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/productByName/$productName"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToProduct(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   Product getProduct(Long ptId, String productName) throws InventoryException {
      Product result = null
      def body = [:]
      body.productName = productName
      try {
         http.post(path: "/$relativePath/productType/$ptId/productByName/", body:body, contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/productType/$ptId/productByName/$productName"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToProduct(xml)
            }
         }
      } catch (HttpResponseException e) {
         log.debug ("Unable to find Product.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   List<Product> getProductIdListAll(String ptName, Date start, Date stop, Integer page, Boolean onlineOnly = false, String pattern = null) throws InventoryException {
      List<Product> returnList = []
      def startLong = (start) ? start.getTime() : null
      def stopLong = (stop) ? stop.getTime() : null
      def body = [:]
      if (startLong)
         body.startTime = startLong
      if (stopLong)
         body.stopTime = stopLong
      if (page)
         body.page = page
      if (onlineOnly)
         body.onlineOnly = onlineOnly
      if (pattern)
         body.pattern = pattern
      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/listProducts", body: body, contentType: ContentType.XML) { resp, xml ->
            log.debug "http - POST - /inventory/productTypeByName/$ptName/listProducts"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {

               for (def product : xml.product) {
                   
                   def stopTime = (product.startTime.text() != "" && product.startTime.text() != null) ? new Date(product.startTime.text() as Long) : null
                  returnList.add(new Product(name: product.name.text(),id: product.id.text() as Long, archiveTime: new Date(product.archiveTime.text() as Long), startTime: new Date(product.startTime.text() as Long), stopTime: stopTime))
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find products for Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return returnList
   }
   List<Product> getProductIdListAll(Long ptId, Date start, Date stop, Integer page, Boolean onlineOnly = false, String pattern = null) throws InventoryException {
      List<Product> returnList = []
      def startLong = (start) ? start.getTime() : null
      def stopLong = (stop) ? stop.getTime() : null
      def body = [:]
      if (startLong)
         body.startTime = startLong
      if (stopLong)
         body.stopTime = stopLong
      if (page)
         body.page = page
      if (onlineOnly)
         body.onlineOnly = onlineOnly
      if (pattern)
         body.pattern = pattern
      try {
         http.post(path: "/$relativePath/productType/$ptId/listProducts", body: body, contentType: ContentType.XML) { resp, xml ->
            log.debug "http - POST - /inventory/productType/$ptId/listProducts"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {

               for (def product : xml.product) {
                   def stopTime = (product.startTime.text() != "" && product.startTime.text() != null) ? new Date(product.startTime.text() as Long) : null
                   def archiveTime = (product.archiveTime.text() != "" && product.archiveTime.text() != null) ? new Date(product.archiveTime.text() as Long) : null
                   returnList.add(new Product(name: product.name.text(), id: product.id.text() as Long, archiveTime: archiveTime, startTime: new Date(product.startTime.text() as Long), stopTime: stopTime))
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find products for Product Type ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage(), e)
      }
      return returnList
   }
   List<Product> getProductsByArchiveTime(String ptName, Date start, Date stop, Boolean onlineOnly) throws InventoryException{
      List<Product> returnList = []
      def startLong = (start) ? start.getTime() : null
      def stopLong = (stop) ? stop.getTime() : null
      def body = [:]
      if (startLong)
         body.startTime = startLong
      if (stopLong)
         body.stopTime = stopLong
      if (onlineOnly)
         body.onlineOnly = onlineOnly
      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/listProductsByArchiveTime", body: body, contentType: ContentType.XML) { resp, xml ->
            log.debug "http - POST - /inventory/productTypeByName/$ptName/listProductsByArchiveTime"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               for (def product : xml.product) {
                   def stopTime = (product.startTime.text() != "" && product.startTime.text() != null) ? new Date(product.startTime.text() as Long) : null
                  returnList.add(new Product(name: product.name.text(), id: product.id.text() as Long, archiveTime: new Date(product.archiveTime.text() as Long), startTime: new Date(product.startTime.text() as Long), stopTime: stopTime))
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find products for Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return returnList
   }
   List<Product> getProductsByArchiveTime(Long ptId, Date start, Date stop, Boolean onlineOnly) throws InventoryException{
      List<Product> returnList = []
      def startLong = (start) ? start.getTime() : null
      def stopLong = (stop) ? stop.getTime() : null
      def body = [:]
      if (startLong)
         body.startTime = startLong
      if (stopLong)
         body.stopTime = stopLong
      if (onlineOnly)
         body.onlineOnly = onlineOnly
      try {
         http.post(path: "/$relativePath/productType/$ptId/listProductsByArchiveTime", body: body, contentType: ContentType.XML) { resp, xml ->
            log.debug "http - POST - /inventory/productType/$ptId/listProductsByArchiveTime"
            if (resp.getStatus() != 200) {
               log.debug("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {

               for (def product : xml.product) {
                   def stopTime = (product.startTime.text() != "" && product.startTime.text() != null) ? new Date(product.startTime.text() as Long) : null
                  returnList.add(new Product(name: product.name.text(),id: product.id.text() as Long, archiveTime: new Date(product.archiveTime.text() as Long), startTime: new Date(product.startTime.text() as Long),stopTime: stopTime))
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find products for Product Type ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return returnList
   }

   Boolean isOnline(Long productId) throws InventoryException {
      Boolean result = false
      try {
         http.get(path: "/$relativePath/product/$productId", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/product/$productId"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               Product p = mapXMLToProduct(xml)
               result = (p.status == "ONLINE") ? true : false
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   Boolean isOnline(String ptName, String productName) throws InventoryException {
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/productByName/$productName", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/productTypeByName/$ptName/productByName/$productName"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
               throw new InventoryException(xml.text())
               return;
            }
            Product p = mapXMLToProduct(xml)
            return (p.status == "ONLINE") ? true : false
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }

   List<ProductArchive> getProductArchives(Long productId) throws InventoryException {
      List<Product> result = []
      try {
         http.get(path: "/$relativePath/product/$productId", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/product/$productId"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToProductArchives(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   List<ProductArchive> getProductArchives(String ptName, String productName) throws InventoryException {
      List<Product> result = []
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/productByName/$productName", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/product/$productId"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = mapXMLToProductArchives(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   String getProductArchivePath(Long productId) throws InventoryException {
      String result = null
      try {
         http.get(path: "/$relativePath/product/$productId/archivePath", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/product/$productId/archivePath"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = xml.str.text()
               log.debug("ArchivePath: ${result}")
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   String getProductArchivePath(String ptName, String productName) throws InventoryException {
      String result = null
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/productByName/$productName/archivePath", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/product/$productId/archivePath"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = xml.str.text()
               log.debug("ArchivePath: ${result}")
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }
   
   /*
    * GIBS Specific Methods
    */
   
   String getEPSG(String ptName) throws InventoryException {
      String epsgCode = null

      if (ptName == null) {
         throw new Exception("No product type")
      }

      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/projection", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/projection"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               epsgCode = xml.epsgCode
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return epsgCode
   }
   
   String getExtents(String ptName) throws InventoryException {
      String extents = null

      if (ptName == null) {
         throw new Exception("No product type")
      }

      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/projection", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/projection"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               extents = xml.nativeBounds
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return extents
   }
   
   String getColormap(String ptName) throws InventoryException {
      String colormap = null
      
      if (ptName == null) {
         throw new Exception("No product type")
      }

      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/colormap", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/colormap"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               colormap = xml.path
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return colormap
   }

   String getEmptyTile(String ptName) throws InventoryException {
      String emptyTile = null
      
      if (ptName == null) {
         throw new Exception("No product type")
      }

      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/emptyTile", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/emptyTile"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               emptyTile = xml.path
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return emptyTile
   }
   
   List<String> getImageList(String ptName, Date startDate, Date stopDate) throws InventoryException {
      List<String> imagePaths = []

      if (ptName == null || startDate == null) {
         throw new Exception("No product type or start date specified")
      }

      def bodyContent = [startTime: startDate.getTime()]

      if (stopDate) {
         bodyContent.stopTime = stopDate.getTime()
      }

      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/listImages", body: bodyContent, contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/listImages"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               def images = xml.images.image
               images.each { image ->
                  imagePaths.add(image.path.text())
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return imagePaths
   }

   List<Source> getSourceList(String ptName, Date startDate, Date stopDate) throws InventoryException {
      List<Source> sourceList = []

      if (ptName == null || startDate == null) {
         throw new Exception("No product type or start date specified")
      }

      def bodyContent = [startTime: startDate.getTime()]

      if (stopDate) {
         bodyContent.stopTime = stopDate.getTime()
      }

      try {
         http.post(path: "/$relativePath/productTypeByName/$ptName/listImages", body: bodyContent, contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /inventory/productTypeByName/$ptName/listImages"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               def sources = xml.sources.source
               sources.each { source ->
                  def sourceObj = new Source(productType: source.productType, product: source.product, repo: source.repo)
                  sourceList.add(sourceObj);
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return sourceList
   }

   ProductTypeGeneration getGeneration(Long ptId) throws InventoryException{
      if (ptId == null) {
         throw new InventoryException("No product type specified")
      }
      try {
         http.get(path: "/$relativePath/productType/$ptId/generation", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/productType/$ptId/generation"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               return mapXMLToGeneration(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   ProductTypeGeneration getGeneration(String ptName) throws InventoryException{
      if (ptName == null) {
         throw new InventoryException("No product type specified")
      }
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/generation", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/productTypeByName/$ptName/generation"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               return mapXMLToGeneration(xml)
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
   }
   
   List<Operation> getOperations(Long productId) throws InventoryException {
      List<Operation> opList = []

      if (productId == null) {
         throw new InventoryException("No product specified")
      }
      try {
         http.get(path: "/$relativePath/product/$productId/listOperations", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/product/$productId/listOperations"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               def operations = xml.operation
               operations.each { operation ->
                  def opObj = mapXMLToOperation(operation)
                  opList.add(opObj);
               }
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return opList
   }

   List<Operation> getOperations(String productTypeName, String productName) throws InventoryException {
      List<Operation> opList = new ArrayList()

      if (!productTypeName || !productName) {
         throw new InventoryException("No product type or product specified")
      }
      try {
         http.get(path: "/$relativePath/productTypeByName/$productName/productByName/$productName/listOperations", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/productTypeByName/$productName/productByName/$productName/listOperations"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text}")
            } else {
               def operations = xml.operation
               operations.each { operation ->
                  def opObj = mapXMLToOperation(operation)
                  opList.add(opObj);
               }
            }

         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${ptName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return opList

   }

   Date getOperationTime(Long productId, OperationType type) throws InventoryException {
      return getOperationTime(productId, type.value())
   }

   Date getOperationTime(Long productId, String type) throws InventoryException {
      Date result = null
      if (productId == null) {
         throw new InventoryException("No product specified")
      }
      try {
         http.get(path: "/$relativePath/product/$productId/operation/$type", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/product/$productId/operation/$type"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = (!xml.endTime.text().equals("")) ? new Date(xml.endTime.text() as Long) : null
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product Type ${productId}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   Date getOperationTime(String ptName, String productName, OperationType type) throws InventoryException {
      return getOperationTime(ptName, productName, type.value())
   }

   Date getOperationTime(String ptName, String productName, String type) throws InventoryException {
      Date result = null
      if (ptName == null || productName == null) {
         throw new InventoryException("No product/product type specified")
      }
      try {
         http.get(path: "/$relativePath/productTypeByName/$ptName/productByName/$productName/operation/$type", contentType: ContentType.XML) { resp, xml ->
            log.debug "http - GET - /$relativePath/productTypeByName/$ptName/productByName/$productName/operation/$type"
            if (resp.getStatus() != 200) {
               log.info("Status returned non-200 code: ${resp.getStatus()}; ${xml.text()}")
            } else {
               result = (!xml.endTime.text().equals("")) ? new Date(xml.endTime.text() as Long) : null
            }
         }
      }
      catch (HttpResponseException e) {
         log.debug ("Unable to find Product ${productName}.  ${e.message}")
      }
      catch (Exception e) {
         throw new InventoryException(e.getMessage())
      }
      return result
   }

   List<ProductReference> getProductAllReferences(Long ptId) throws InventoryException {
      //TODO implement this
      return null
   }
   
   /*
    * Helpers
    */
   
   ProductType mapXMLToProductType(xml) {
      ProductType pt = new ProductType()
      pt.id = xml.productTypeId.text() as Long
      pt.identifier = xml.identifier.text()
      pt.title = (!xml.title.text().equals("")) ? xml.title.text() : null
      pt.description = (!xml.description.text().equals("")) ? xml.description.text() : null
      return pt
   }

   Product mapXMLToProduct(xml) {
      Product p = new Product()
      p.id = xml.productId.text() as Long
      p.ptId = xml.productTypeId.text() as Long
      p.name = xml.name.text()
      p.startTime = (!xml.startTime.text().equals("")) ? new Date(xml.startTime.text() as Long) : null
      p.stopTime = (!xml.stopTime.text().equals("")) ? new Date(xml.stopTime.text() as Long) : null
      p.createTime = (!xml.createTime.text().equals("")) ? new Date(xml.createTime.text() as Long) : null
      p.archiveTime = (!xml.archiveTime.text().equals("")) ? new Date(xml.archiveTime.text() as Long) : null
      p.versionNum = (!xml.version.text().equals("")) ? xml.version.text() as Integer : null
      p.status = (!xml.status.text().equals("")) ? xml.status.text() : null
      p.rootPath = (!xml.rootPath.text().equals("")) ? xml.rootPath.text() : null
      p.relPath = (!xml.relPath.text().equals("")) ? xml.relPath.text() : null

      return p
   }
   
   List<ProductArchive> mapXMLToProductArchives(xml) {
      List<ProductArchive> result = []
      xml.archiveSet.archive.each { archive ->
         def archiveObj = new ProductArchive()
         archiveObj.productId = (!xml.productId.text().equals("")) ? xml.productId.text() as Long : null
         archiveObj.type = (!archive.type.text().equals("")) ? archive.type.text() as String : null
         archiveObj.fileSize = (!archive.fileSize.text().equals("")) ? archive.fileSize.text() as Long : null
         Boolean compressFlag = null
         if(archive.compressFlag.text().equals("true") || archive.compressFlag.text().equals("1")) {
            compressFlag = true
         }
         else if(archive.compressFlag.text().equals("false") || archive.compressFlag.text().equals("0")) {
            compressFlag = false
         }
         archiveObj.compressFlag = compressFlag
         archiveObj.checksum = (!archive.checksum.text().equals("")) ? archive.checksum.text() as String : null
         archiveObj.name = (!archive.name.text().equals("")) ? archive.name.text() as String : null
         archiveObj.status = (!archive.status.text().equals("")) ? archive.status.text() as String : null
         result.add(archiveObj)
      }
      return result
   }

   ProductTypePolicy mapXMLToPolicy(xml) {
      ProductTypePolicy policy = new ProductTypePolicy()
      policy.ptId = xml.pt.@id.text() as Long
      policy.dataClass = (!xml.dataClass.text().equals("")) ? xml.dataClass.text() : null
      policy.dataFrequency = (!xml.dataFrequency.text().equals("")) ? xml.dataFrequency.text() as Integer : null
      policy.dataVolume = (!xml.dataVolume.text().equals("")) ? xml.dataVolume.text() as Integer : null
      policy.dataDuration = (!xml.dataDuration.text().equals("")) ? xml.dataDuration.text() as Integer : null
      policy.dataLatency = (!xml.dataLatency.text().equals("")) ? xml.dataLatency.text() as Integer : null
      policy.deliveryRate = (!xml.dataLatency.text().equals("")) ? xml.dataLatency.text() as Integer : null
      policy.multiDay = (!xml.multiDay.text().equals("")) ? xml.multiDay.text() as Integer : null
      policy.multiDayLink = (!xml.multiDayLink.text().equals("")) ? xml.multiDayLink.text() as Boolean : null
      policy.accessType = (!xml.accessType.text().equals("")) ? xml.accessType.text() : null
      policy.basePathAppendType = (!xml.basePathAppendType.text().equals("")) ? xml.basePathAppendType.text() : null
      policy.dataFormat = (!xml.dataFormat.text().equals("")) ? xml.dataFormat.text() : null
      policy.compressType = (!xml.compressType.text().equals("")) ? xml.compressType.text() : null
      policy.checksumType = (!xml.checksumType.text().equals("")) ? xml.checksumType.text() : null
      policy.spatialType = (!xml.spatialType.text().equals("")) ? xml.spatialType.text() : null
      policy.accessConstraint = (!xml.accessConstraint.text().equals("")) ? xml.accessConstraint.text() : null
      policy.useConstraint = (!xml.useConstraint.text().equals("")) ? xml.useConstraint.text() : null
      return policy
   }

   ProductTypeCoverage mapXMLToCoverage(xml) {
      ProductTypeCoverage coverage = new ProductTypeCoverage()
      coverage.ptId = xml.pt.@id.text() as Long

      coverage.northLatitude = (!xml.northLatitude.text().equals("")) ? xml.northLatitude.text() as Float : null
      coverage.southLatitude = (!xml.southLatitude.text().equals("")) ? xml.southLatitude.text() as Float : null
      coverage.eastLongitude = (!xml.eastLongitude.text().equals("")) ? xml.eastLongitude.text() as Float : null
      coverage.westLongitude = (!xml.westLongitude.text().equals("")) ? xml.westLongitude.text() as Float : null
      coverage.startTime = (!xml.startTime.text().equals("")) ? new Date(xml.startTime.text() as Long) : null
      coverage.stopTime = (!xml.stopTime.text().equals("")) ? new Date(xml.stopTime.text() as Long) : null
      return coverage
   }

   Operation mapXMLToOperation(xml) {
      Operation op = new Operation()

      op.productId = (!xml.@product.text().equals("")) ? xml.@product.text() as Long : null
      op.agent = (!xml.agent.text().equals("")) ? xml.agent.text() : null
      op.startTime = (!xml.startTime.text().equals("")) ? new Date(xml.startTime.text() as Long) : null
      op.stopTime = (!xml.stopTime.text().equals("")) ? new Date(xml.stopTime.text() as Long) : null
      op.command = (!xml.command.text().equals("")) ? xml.operation.text() : null
      op.arguments = (!xml.arguments.text().equals("")) ? xml.arguments.text() : null

      return op
   }

   List<ProductTypeLocationPolicy> mapXMLToLocationPolicies(xml) {
      List<ProductTypeLocationPolicy> results = []
      xml.locationPolicySet.locationPolicy.each {xmlLP ->
         ProductTypeLocationPolicy lp = new ProductTypeLocationPolicy()
         lp.type = (!xmlLP.type.text().equals("")) ? xmlLP.type.text() : null
         lp.basePath = (!xmlLP.basePath.text().equals("")) ? xmlLP.basePath.text() : null
         lp.ptId = (!xml.productTypeId.text().equals("")) ? xml.productTypeId.text() as Long : null
         
         results.add(lp)
      }
      return results
   }
   
   ProductTypeGeneration mapXMLToGeneration(xml) {
      ProductTypeGeneration gen = new ProductTypeGeneration()
      
      gen.outputSizeX = (!xml.outputSizeX.text().equals("")) ? xml.outputSizeX.text() as Integer : null
      gen.outputSizeY = (!xml.outputSizeY.text().equals("")) ? xml.outputSizeY.text() as Integer : null
      gen.overviewScale = (!xml.overviewScale.text().equals("")) ? xml.overviewScale.text() as Integer : null
      gen.overviewLevels = (!xml.overviewLevels.text().equals("")) ? xml.overviewLevels.text() as Integer : null
      gen.overviewResample = (!xml.overviewResample.text().equals("")) ? xml.overviewResample.text()  : null
      gen.resizeResample = (!xml.resizeResample.text().equals("")) ? xml.resizeResample.text()  : null
      gen.reprojectionResample = (!xml.reprojectionResample.text().equals("")) ? xml.reprojectionResample.text() : null
      gen.vrtNodata = (!xml.vrtNodata.text().equals("")) ? xml.vrtNodata.text() as Integer : null
      gen.mrfBlockSize = (!xml.mrfBlockSize.text().equals("")) ? xml.mrfBlockSize.text() as Integer : null
      
      return gen
   }
   
   /*
   public static void main(String[] args) {

      InventoryApi test = new InventoryApi("http://localhost:9192/inventory")
      println test.getColormap("AIRS_CO_A_t");
      println test.getGeneration(185)
      //println test.isValidSip(ServiceProfileFactory.getInstance().createServiceProfileFromMessage('<?xml version="1.0" encoding="UTF-8"?><message xmlns="http://horizon.nasa.gov"><submission><header><productType>MORCR143LLDY</productType><productName>RRGlobal_r01c05.2013248.aqua.250m</productName><officialName>RRGlobal_r01c05.2013248.aqua.250m</officialName><version>1</version><createTime>1378422730001</createTime><status>READY</status><catalogOnly>false</catalogOnly><operations><operation><agent>MODAPSHandler</agent><operation>ACQUIRED</operation><time><start>1378425463983</start><stop>1378425463983</stop></time></operation></operations></header><metadata><temporalCoverage><start>1378353600000</start></temporalCoverage><numberOfLines>20</numberOfLines><numberOfColumns>40</numberOfColumns><spatialCoverage><rectangles><rectangle><westLongitude>-135.0</westLongitude><northLatitude>-72.0</northLatitude><eastLongitude>-126.0</eastLongitude><southLatitude>-81.0</southLatitude></rectangle></rectangles></spatialCoverage><history><version>1</version><createDate>1378422720000</createDate><lastRevisionDate>1378422720000</lastRevisionDate><revisionHistory>TBD</revisionHistory><sources><source><productType>MORCR143LLDY</productType><product>A132482110</product></source></sources></history></metadata><ingest><productFiles><IngestProductFile><productFile><file><name>RRGlobal_r01c05.2013248.aqua.txt</name><links><link>file:///Users/thuang/Development/work/shared/gibs/tie/handlers/modaps/target/modaps-handler-0.2.0a/config/../modaps_staging/staging/data/MORCR143LLDY_SRC/RRGlobal_r01c05.2013248.aqua.250m//RRGlobal_r01c05.2013248.aqua.txt</link></links><size>365</size><checksum><type>MD5</type><value>07ba94ca745d5599dee73a44eb26c03a</value></checksum><format>ASCII</format></file><type>METADATA</type></productFile></IngestProductFile><IngestProductFile><productFile><file><name>RRGlobal_r01c05.2013248.aqua.250m.jpg</name><links><link>file:///Users/thuang/Development/work/shared/gibs/tie/handlers/modaps/target/modaps-handler-0.2.0a/config/../modaps_staging/staging/data/MORCR143LLDY_SRC/RRGlobal_r01c05.2013248.aqua.250m//RRGlobal_r01c05.2013248.aqua.250m.jpg</link></links><size>125649</size><checksum><type>MD5</type><value>8c6afa653b473d5140ebc65949e4f5f2</value></checksum><format>JPEG</format></file><type>IMAGE</type></productFile></IngestProductFile><IngestProductFile><productFile><file><name>RRGlobal_r01c05.2013248.aqua.250m.jgw</name><links><link>file:///Users/thuang/Development/work/shared/gibs/tie/handlers/modaps/target/modaps-handler-0.2.0a/config/../modaps_staging/staging/data/MORCR143LLDY_SRC/RRGlobal_r01c05.2013248.aqua.250m//RRGlobal_r01c05.2013248.aqua.250m.jgw</link></links><size>96</size><checksum><type>MD5</type><value>77b8de4f3b5d1aab2d6aeb95f70c3891</value></checksum><format>JGW</format></file><type>GEOMETADATA</type></productFile></IngestProductFile></productFiles><operationSuccess>false</operationSuccess></ingest></submission><origin address="192.168.1.6" name="MODAPS Data Handler" time="1378425463982"></origin><target name="Manager:MORCR143LLDY_SRC"></target></message>'))
      //println test.getProductArchivePath(565)
      //println test.updateProductStatus(573, "ONLINE")
      //println test.getOperationTime(79, OperationType.ACQUIRED)
      
      //println test.getProductTypePolicy("MORCR143LLDY_SRC").getDataFormat()
      //println test.getProductType(1)
      //println test.getImageList("MORCR143LLDY_SRC", new Date(0), new Date())
      //println test.getProductType(31).identifier
      //println test.getLatestProductIdByProductType(31)
      //println test.getProductIdListAll("MORCR143LLDY_SRC", new Date(0), null, 1)[0].archiveTime
      //println test.getProductIdListAll("MORCR143LLDY_SRC", new Date(0), null, 1)[0].startTime
   }
*/
}
