/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import java.util.Date;
import java.util.List;

/**
 * Interface represent Product Type.  A product type is the top level
 * grouping of a collection of data of common format.  This is similar to
 * a data set.
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface ProductType extends Worker {
   /**
    * The name of the product type
    *
    * @return the product type name
    */
   String getName();

   /**
    * The interval, in seconds, the product type should be pulled
    *
    * @return the time interval in seconds
    */
   int getInterval();

   /**
    * The URL reference to the Significant Event Service
    *
    * @return the sigevent URL
    */
   String getSigEventURL();

   /**
    * The top-level URL products will be harvest from
    *
    * @return URL where product will be harvested
    */
   String getSourceURL();

   /**
    * A product contains one or more files.  The filters is a collection
    * of string regular expressions.  It will be used to identify the
    * list of file that makes up a product
    *
    * @return the list of file filters
    */
   List<String> getFilesetFilter();

   /**
    * Method to add fileset filter to the product type
    *
    * @param filesetFilter the fileset filter to be included
    */
   void addFilesetFilter(String filesetFilter);

   /**
    * Method to clear all fileset filters
    */
   void clearFilesetFilter();

   /**
    * Each product has a last modification time.  This accessor gives
    * the starting datetime value to be included into the product filtering
    * logic.
    *
    * @return the start datetime.  Null means starting from current time on.
    */
   Date getStart();

   /**
    * Method to set the start datetime for filtering
    *
    * @param start the start datetime value
    */
   void setStart(Date start);

   /**
    * Each product has a last modification time.  This accessor gives
    * the ending datetime value to be included into the product filtering
    * logic.
    *
    * @return the end datetime.  Null means continue pulling for new products
    */
   Date getEnd();

   /**
    * Method to set the end datetime for filtering
    *
    * @param end the end datetime value
    */
   void setEnd(Date end);

   /**
    * Flag to indicate the product type is ready to harvest
    *
    * @return true if it is ready
    */
   boolean isReady();

   /**
    * Method to set the ready flag to indicate the product type is ready for use
    *
    * @param ready set to true if the product type is ready
    */
   void setReady(boolean ready);

   /**
    * This flag indicates this product type will run as a batch
    * (i.e. a single crawl session)
    *
    * @return true if the product type is to run as batch
    */
   boolean isBatch();

   /**
    * Method to set the batch flag to indicate the product type will run in
    * a batch
    *
    * @param batch tru if the product type is to run as a batch
    */
   void setBatch(boolean batch);

   /**
    * Accessor method for the internal Application Configurator instance
    *
    * @return the Application Configurator instance
    */
   ApplicationConfigurator getConfigurator();

   /**
    * Method to return the link to the product type cache.  The cache is
    * used to exclude files that have already been ingested.
    *
    * @return the location of the cache
    */
   String getCache();

   /**
    * Method to return the link to the cache root location.
    *
    * @return the cache root location
    */
   String getCacheRoot();

   /**
    * Method to return the product type top-level data storage location for the product
    * type.  Products will be staged under this location with product name
    * as the relative directory.
    *
    * @return the product type top-level data storage location
    */
   String getDataStorage();

   /**
    * Method to return the top-level data storage location.
    *
    * @return the top-level data storage location
    */
   String getDataStorageRoot();

   /**
    * Method to return the product type top-level pending location.  The pending location
    * is used to store Submission Information Packages (SIPs) files, one
    * for each product.
    *
    * @return the SIP pending location for the product type
    */
   String getMetadataPending();

   /**
    * Method to return the top-level pending location.
    *
    * @return the SIP pending location
    */
   String getMetadataPendingRoot();

   /**
    * Method to return the product type top-level submitted location.  The submitted
    * location is used to store Submission Information Packages (SIPs)
    * files, one for each product.  Submitted SIPs are SIPs that have been
    * received by the Manager WS, but data products still not in archive.
    *
    * @return the SIP submitted location for the product type
    */
   String getMetadataSubmitted();

   /**
    * Method to return the top-level submitted location.
    *
    * @return the SIP submitted location
    */
   String getMetadataSubmittedRoot();

   /**
    * Method to return the product type top-level submission error location.  The
    * submission error location is for storing SIPs that are not able to
    * submit into the Manager WS.
    *
    * @return the SIP submission error location for the product type
    */
   String getSubmissionError();

   /**
    * Method to return the top-level submission error location.
    *
    * @return the SIP submission error location.
    */
   String getSubmissionErrorRoot();

   /**
    * Method to return the product type top-level validation error location. Validation
    * error happens during checksum validation or metadata harvesting.  This
    * provide the top-level location to storage these data.
    *
    * @return the product validation error location for the product type
    */
   String getValidationError();

   /**
    * Method to return the top-level validation error location.
    *
    * @return the top-level validation error location
    */
   String getValidationErrorRoot();

   /**
    * Accessor method to return reference to the File Handler Factory instance
    *
    * @return File Handler Factory instance
    */
   FileHandlerFactory getFileHandlerFactory();

   /**
    * Accessor method to return reference to the Metadata Harvester Factory instance
    *
    * @return Metadata Harvester Factory instance
    */
   MetadataHarvesterFactory getMetadataHarvesterFactory();

   /**
    * Accessor method to return reference to the Product Type Factory instance
    *
    * @return Product Type Factory instance
    */
   ProductTypeFactory getProductTypeFactory();

   /**
    * Accessor method to return reference to the Workspace Factory instance
    *
    * @return Workspace Factory instance
    */
   WorkspaceFactory getWorkspaceFactory();

   /**
    * Accessor method to return reference to the Workspace instance
    *
    * @return Workspace instance
    */
   Workspace getWorkspace();

   /**
    * Hook method to cleanup any allocated resources
    */
   void cleanup();
}
