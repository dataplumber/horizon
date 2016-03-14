/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

/**
 * @author T. Huang
 * @version $Id:$
 */
public class DataHandlerException extends Exception {
   private static final long serialVersionUID = 1L;

   public DataHandlerException() {
      super();
   }

   public DataHandlerException(String message) {
      super(message);
   }

   public DataHandlerException(String message, Throwable cause) {
      super(message, cause);
   }

   public DataHandlerException(Throwable cause) {
      super(cause);
   }
}