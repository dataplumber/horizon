/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler.spider.registry;

import gov.nasa.horizon.common.crawler.spider.CrawlerException;
import gov.nasa.horizon.common.api.file.FileProduct;

import java.util.Set;

/**
 * Interface for cache registry implementations.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 *
 */
public interface CrawlerRegistry {

   /**
    * Method to restore cached file list from registry
    *
    * @return the list of cached file data
    * @throws CrawlerException when failed
    */
   Set<FileProduct> restore() throws CrawlerException;

   /**
    * Method to save the list of file data into the registry
    *
    * @param fileProducts the list of file data
    * @throws CrawlerException when failed
    */
   void save(Set<FileProduct> fileProducts) throws CrawlerException;

}
