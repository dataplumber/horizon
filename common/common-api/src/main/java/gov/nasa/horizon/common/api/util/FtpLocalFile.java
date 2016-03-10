/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.net.URI;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: FtpLocalFile.java 244 2007-10-02 20:12:47Z axt $
 */
public class FtpLocalFile extends FtpFile {
   public FtpLocalFile(URI uri, FtpFile.Type type) throws Exception {
      super(uri, type);
   }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.common.api.util.FtpFile#_validateUri(java.net.URI)
    */
   @Override
   protected void _validateUri(URI uri) throws Exception {
      String schema = uri.getScheme();
      if((schema == null) || (!schema.toUpperCase().startsWith("FILE:"))) {
         throw new Exception("URI for FTP local file needs to start with 'file:'.");
      }
   }
}
