/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: ServiceProfileException.java 1234 2008-05-30 04:45:50Z thuang $
 * 
 */
public class ServiceProfileException extends Exception {
   private static final long serialVersionUID = 1L;

   public ServiceProfileException() {
      super();
   }

   public ServiceProfileException(String message) {
      super(message);
   }

   public ServiceProfileException(String message, Throwable cause) {
      super(message, cause);
   }

   public ServiceProfileException(Throwable cause) {
      super(cause);
   }
}
