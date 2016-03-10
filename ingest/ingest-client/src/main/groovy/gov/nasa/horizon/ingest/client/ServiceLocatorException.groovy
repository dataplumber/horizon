/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.ingest.client;

/**
 * Exception type for all service locator issues
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $id$
 * 
 */
public class ServiceLocatorException extends RuntimeException {

   static final long serialVersionUID = 8906455608484282128L;

   public ServiceLocatorException() {
      super();
   }

   public ServiceLocatorException(String message) {
      super(message);
   }

   public ServiceLocatorException(Throwable cause) {
      super(cause);
   }

   public ServiceLocatorException(String message, Throwable cause) {
      super(message, cause);
   }

}
