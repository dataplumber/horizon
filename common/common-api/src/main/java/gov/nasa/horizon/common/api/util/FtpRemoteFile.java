/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.net.URI;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: FtpRemoteFile.java 244 2007-10-02 20:12:47Z axt $
 */
public class FtpRemoteFile extends FtpFile {
   public FtpRemoteFile(URI uri, FtpFile.Type type) throws Exception {
      super(uri, type);
   }
   
   protected void _validateUri(URI uri) throws Exception {
      String schema = uri.getScheme();
      if((schema == null) || (!schema.toUpperCase().startsWith("FTP:"))) {
         throw new Exception("URI for FTP remote file needs to start with 'ftp:'.");
      }
   }
}
