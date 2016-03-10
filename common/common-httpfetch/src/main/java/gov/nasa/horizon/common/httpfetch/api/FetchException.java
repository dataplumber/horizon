/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.httpfetch.api;

/**
 * The Fetch Exception class used by the HttpFetcher objects.
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class FetchException extends Exception {

   /**
    * Constructor that takes a single input message
    *
    * @param message the message
    */
   public FetchException(String message) {
      super(message);
   }

   /**
    * Constructor that takes an input message and a throwable object
    *
    * @param message the message
    * @param e       the throwable object
    */
   public FetchException(String message, Throwable e) {
      super(message, e);
   }
}
