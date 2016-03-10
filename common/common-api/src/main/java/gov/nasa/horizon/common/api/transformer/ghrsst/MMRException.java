/*****************************************************************************
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.transformer.ghrsst;

/**
 * MMR translation exception
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class MMRException extends Exception {

   private static final long serialVersionUID = 1L;

   public MMRException() {
      super();
   }

   public MMRException(String message) {
      super(message);
   }

   public MMRException(String message, Throwable cause) {
      super(message, cause);
   }

   public MMRException(Throwable cause) {
      super(cause);
   }
}
