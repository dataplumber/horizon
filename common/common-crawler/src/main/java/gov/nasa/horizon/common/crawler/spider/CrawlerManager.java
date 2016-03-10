/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler.spider;

import gov.nasa.horizon.common.api.service.Service;

/**
 * Interface defined for managing a group of crawler instances. This is mainly
 * used when the crawlers are operated in daemon mode.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public interface CrawlerManager extends Service {

   /**
    * Method to register a crawler instance to the manager.
    *
    * @param crawler the crawler object.
    * @throws CrawlerException when an instance of the crawler URI is already
    *            exist.
    */
   void registerCrawler(Crawler crawler) throws CrawlerException;

   /**
    * Method to replace/register a crawler instance to the manager. This method
    * will override any existing instance of registered crawler that is crawls
    * the same URI.
    *
    * @param crawler the crawler object.
    */
   void replaceCrawler(Crawler crawler);

   /**
    * Method to remove a crawler from the manager.
    *
    * @param crawler the crawler object reference
    */
   void unregisterCrawler(Crawler crawler);

   /**
    * Method to remove a crawler from the manager.
    *
    * @param uri the input URI used to lookup for a registered crawler
    *
    * @return the crawler object or null
    */
   Crawler unregisterCrawler(String uri);

}
