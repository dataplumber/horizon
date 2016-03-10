/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.net.URI;

import org.apache.commons.net.ftp.FTP;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: FtpFile.java 244 2007-10-02 20:12:47Z axt $
 */
public abstract class FtpFile {
   public enum Type {
      ASCII(FTP.ASCII_FILE_TYPE),
      BINARY(FTP.BINARY_FILE_TYPE);
      
      private final int _type;
      
      Type(int type) {
         _type = type;
      }
      
      public int getType() {
         return _type;
      }
   }
   private URI _uri;
   private Type _type;
   
   public FtpFile(URI uri, Type type) throws Exception {
      _validateUri(uri);
      
      _uri = uri;
      _type = type;
   }
   
   public URI getUri() {
      return _uri;
   }
   
   public Type getType() {
      return _type;
   }
   
   protected abstract void _validateUri(URI uri) throws Exception;
}
