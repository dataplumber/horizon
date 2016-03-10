/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.io.sock;

/**
 * Verify Exception class.
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: VerifyException.java 1846 2008-09-16 18:34:41Z thuang $
 */
public class VerifyException extends Exception {

   static final long serialVersionUID = 1L;

   /**
    * constructor
    * 
    * @param t a Throwable object to keep track of the exception trace.
    */
   public VerifyException(Throwable t) {
      super(t);
   }

   /**
    * constructor
    * 
    * @param message string describing the exception
    */
   public VerifyException(String message) {
      super(message);
   }
}
