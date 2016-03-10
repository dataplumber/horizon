/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.crawler.spider;

/**
 * Specialized exception for crawler-related issues.
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class CrawlerException extends Exception {

   private static final long serialVersionUID = 1L;

   public CrawlerException() {
      super();
   }

   public CrawlerException(String message) {
      super(message);
   }

   public CrawlerException(String message, Throwable cause) {
      super(message, cause);
   }

   public CrawlerException(Throwable cause) {
      super(cause);
   }
}
