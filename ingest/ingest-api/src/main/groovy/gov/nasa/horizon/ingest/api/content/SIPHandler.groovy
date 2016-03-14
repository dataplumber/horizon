/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.ingest.api.content

import gov.nasa.horizon.common.api.serviceprofile.*
import gov.nasa.horizon.ingest.api.ProductFile
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Utility class for extract information for an input submission information package.
 * The purpose of this class is to keep the ingest programs from having direct
 * dependency on the ServiceProfile interface.  This class assumes the input
 * SIP contains just one product.
 *
 * @author T. Huang
 * @version $Id: $
 */
class SIPHandler {
   private final Log log = LogFactory.getLog(SIPHandler.class)

   private ServiceProfile profile
   //private def completeContent
   private SPIngest ingestProfile

   private SPHeader sipHeader

   /**
    * Initialize the class with the input SIP message (XML string)
    *
    * @param contents the SIP message string
    * @throws Exception when failed to create a SerivceProfile object from the input string
    */
   SIPHandler(String contents) throws Exception {
      log.trace "creating SIPHandler"
      profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(contents)
      log.trace "done create profile"
      log.debug profile.toString()

      this.sipHeader = profile?.submission?.header
      this.ingestProfile = profile?.submission?.ingest

      if (!this.sipHeader) {
         log.trace 'Ingest header is missing in SIP'
         throw new ServiceProfileException ('Invalid SIP message.')
      }

      if (!this.ingestProfile) {
         log.trace 'Ingest object is missing in SIP'
         throw new ServiceProfileException('Invalid SIP message.')
      }

      log.trace "done creating SIPHandler"
   }

   /**
    * Method to obtain the product name.
    *
    * @return the product name
    * @throws Exception when failed to find a product from the ServiceProfile object
    */
   String getProductName() throws Exception {
      SPHeader header = this.profile?.submission?.header
      if (!header) {
         throw new ServiceProfileException('Invalid SIP: Missing header.')
      }
      return header.productName
   }

   /**
    * Method to obtain the product type name
    *
    * @return the product type name
    * @throws Exception when failed  to find a product from the ServiceProfile object
    */
   String getProductType() throws Exception {
      SPHeader header = this.profile?.submission?.header
      if (!header) {
         throw new ServiceProfileException('Invalid SIP: Missing header.')
      }
      return header.productType
   }

   /**
    * Method to obtain the dataset name
    *
    * @return the dataset name
    * @throws Exception when failed to find a product from the ServiceProfile object.
    */
   String getDataset() throws Exception {
      return this.productType
   }

   /**
    * Method to to return the list of product files associated with the product
    *
    * @return the list of product files
    * @throws Exception when failed to find a product from the ServiceProfile object.
    */
   List<ProductFile> getProductFiles() throws Exception {
      log.trace "inside getProductFiles"
      List<SPIngestProductFile> ipfs = this.profile?.submission?.ingest?.ingestProductFiles
      if (!ipfs || ipfs.size() == 0) {
         throw new ServiceProfileException('Invalid SIP: Missing product files')
      }

      List<ProductFile> result = []

      ipfs.each { SPIngestProductFile ipf ->
         SPFile pf = ipf?.productFile?.file
         ProductFile file = new ProductFile(
               name: pf.name,
               source: pf.links[0],
               size: pf.size,
               checksumType: pf.checksumType.toString(),
               checksum: pf.checksumValue
         )
         result << file
      }

      log.trace "File count: ${result.size()}."
      return result
   }

   /**
    * Method to return the name of the original product to be replaced
    *
    * @return the original product name
    * @throws Exception when failed to find a product from the ServiceProfile object
    */
   String getReplace() throws Exception {
      SPHeader header = this.profile?.submission?.header
      if (!header) {
         throw new ServiceProfileException('Invalid SIP: Missing submission header')
      }
      return header.replace
   }

   /**
    * Method to set the name of the product to be replaced
    *
    * @param originalProduct the original product name
    * @throws Exception when failed to find a product from the ServiceProfile object
    */
   void setReplace(String originalProduct) throws Exception {
      SPHeader header = this.profile?.submission?.header
      if (!header) {
         throw new ServiceProfileException('Invalid SIP: Missing submission header')
      }
      header.replace = originalProduct
   }

   /**
    * Method to return the ServiceProfile object as an XML message string
    *
    * @return the XML string representing the ServiceProfile object
    */
   def getMetadataText() {
      return profile.toString()
   }

   def updateIngestDetails(SPIngest ingestObj, List<ProductFile> files) throws Exception {
      List<SPIngestProductFile> ipfs = ingestObj?.ingestProductFiles
      if (!ipfs || ipfs.size() == 0) {
         throw new ServiceProfileException('Invalid SIP: Missing product files')
      }

      ipfs.each { SPIngestProductFile ipf ->
         ProductFile pf = files.find { ProductFile f ->
            ipf?.productFile?.file?.name?.equals(f.name)
         }
         if (pf) {
            ipf.ingestStartTime = pf.startTime?.time
            ipf.ingestStopTime = pf.stopTime?.time
         }
      }
   }

   /**
    * Method to update the ingest details
    *
    * @param productType the product type name
    * @param replace the product it replaces
    * @param files the list of product files
    * @param remoteUrl the remote URL to the staging area
    * @param startTime the start time of ingestion
    * @param stopTime the stop time of ingestion
    * @throws Exception when failed to find a product from ServiceProfile object
    */
   def updateIngestDetails(List<ProductFile> files, Date startTime, Date stopTime) throws Exception {

      SPIngest ingestObj = this.profile?.submission?.ingest
      SPHeader header = this.profile?.submission?.header

      if (!header) {
         throw new ServiceProfileException('Invalid SIP: Missing submission header object.')
      }

      if (!ingestObj) {
         throw new ServiceProfileException('Invalid SIP: Missing ingest object.')
      }

      this.updateIngestDetails(ingestObj, files)

      SPOperation operation = header.createOperation()
      operation.agent = 'INGEST_SERVER'
      operation.operation = 'INGEST'
      operation.operationStartTime = startTime
      operation.operationStopTime = stopTime
      header.addOperation(operation)
   }

   /**
    * Method to update the ingest details
    *
    * @param productType the product type name
    * @param replace the product it replaces
    * @param files the list of product files
    * @param remoteUrl the remote URL to the staging area
    * @param startTime the start time of ingestion
    * @param stopTime the stop time of ingestion
    * @throws Exception when failed to find a product from ServiceProfile object
    */
   def updateArchiveDetails(def files, Date startTime, Date stopTime) throws Exception {

      SPIngest ingestObj = this.profile?.submission?.archive
      SPHeader header = this.profile?.submission?.header

      if (!header) {
         throw new ServiceProfileException('Invalid SIP: Missing submission header object.')
      }

      if (!ingestObj) {
         throw new ServiceProfileException('Invalid SIP: Missing archive object.')
      }

      this.updateIngestDetails(ingestObj, files)

      SPOperation operation = header.createOperation()
      operation.agent = 'INGEST_SERVER'
      operation.operation = 'ARCHIVE'
      operation.operationStartTime = startTime
      operation.operationStopTime = stopTime
      header.addOperation(operation)
   }
}