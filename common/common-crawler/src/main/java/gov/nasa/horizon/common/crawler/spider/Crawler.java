/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler.spider;

import gov.nasa.horizon.common.api.service.Service;
import gov.nasa.horizon.common.api.file.FileFilter;
import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.file.FileProductHandler;
import gov.nasa.horizon.common.crawler.spider.provider.ProcessFileProduct;

import java.util.Set;

/**
 * Interface captures required functions for Crawler object
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public interface Crawler extends Service {

   /**
    * Batch method that return the list of file products from a single scan.
    *
    * @return the list of file products metadata
    */
   Set<FileProduct> crawl();

   /**
    * Method return the number of available connections configured for the
    * crawler instance.
    *
    * @return the number of available connections
    */
   int getAvailableConnections();

   /**
    * Method used by the crawler daemon thread to determine the frequency it
    * operates on checking for changes on the specified URI
    *
    * @return a long integer in seconds
    */
   long getFrequency();

   /**
    * Method return the root URI the crawler is scanning
    *
    * @return the URI string for the crawler instance
    */
   String getRootURI();

   /**
    * Method return true if the crawler will try to match much files
    * as possible from a single crawl.
    *
    * @return true if greedy is enabled
    */
   boolean isGreedy();
   
   /**
    * Method return true if the crawler is set to crawl recursively
    *
    * @return true if recursive is enabled
    */
   boolean isRecursive();

   /**
    * Method to set the greedy match flag
    *
    * @param greedy the greedy match flag
    */
   void setGreedy(boolean greedy);
   
   /**
    * Method to set the recursive flag
    *
    * @param recursive the recursive flag
    */
   void setRecursive(boolean recursive);

   /**
    * Method to set a product handler to prevent the creation of a very long list.
    * If not set, then crawler will behave as it always has in the past returning a
    * Set<FileProduct). Otherwise, the list it returns will be empty.
    * 
    * @param handler the individual FileProduct handler.
    */
   void registerDoHandler (ProcessFileProduct handler);
   
   /**
    * Method to set a product callback handler. Mainly used when operate in
    * daemon mode. The crawler thread dispatches this handler when new files are
    * found.
    *
    * @param handler the product handler object.
    */
   void registerProductHandler(FileProductHandler handler);

   /**
    * Method to register file filter object. Used by the crawler to filter files
    * found.
    *
    * @param inclusionFilter the inclusion filter object.
    */
   void registerProductSelector(FileFilter inclusionFilter);

   /**
    * Method to register file filter object. Used by the crawler to filter files
    * found.
    *
    * @param inclusionFilter the inclusion filter object.
    * @param exclusionFilter the exclusion filter object.
    */
   void registerProductSelector(FileFilter inclusionFilter, FileFilter exclusionFilter);
}
