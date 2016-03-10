/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.metadatamanifest;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: ServiceProfileException.java 1234 2008-05-30 04:45:50Z thuang $
 * 
 */
public class MetadataManifestException extends Exception {
   private static final long serialVersionUID = 1L;

   public MetadataManifestException() {
      super();
   }

   public MetadataManifestException(String message) {
      super(message);
   }

   public MetadataManifestException(String message, Throwable cause) {
      super(message, cause);
   }

   public MetadataManifestException(Throwable cause) {
      super(cause);
   }
}
