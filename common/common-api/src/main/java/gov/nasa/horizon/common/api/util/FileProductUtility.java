/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;


import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.file.VFSFileProduct;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.cache.DefaultFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.TrustEveryoneUserInfo;


/**
 * A utility which will access ftp and sftp remote files via the apache VFS given a uri of a single file.
 * Returns a VFSFileProduct which implements the generic FileType interface
 *
 * @author M. Gangl {Michael.E.Gangl@jpl.nasa.gov}
 * @version $Id: $
 */


public class FileProductUtility {

   private static Log _logger = LogFactory.getLog(FileProductUtility.class);
   private String _keyFile = System.getProperty("user.home") + "/.ssh/id_dsa";

   private FileSystemOptions _opts = new FileSystemOptions();
   private String _uri;
   private DefaultFileSystemManager fileSystemManager;

   /**
    * VFSFileUtility main class for the VFS file utility which retrieves a FileProduct using the Apache VFS
    * Currently only supports FTP and SFTP connections
    *
    * @param uri the location of the remote file resource
    */
   public FileProductUtility(String uri) {
      this._uri = uri;
      if (uri.startsWith("sftp")) {
         try {
            SftpFileSystemConfigBuilder.getInstance()
                  .setStrictHostKeyChecking(
                        this._opts, "no");
            SftpFileSystemConfigBuilder.getInstance().setUserInfo(this._opts,
                  new TrustEveryoneUserInfo());
            // set up the SFTP data timeout
            SftpFileSystemConfigBuilder.getInstance().setTimeout(this._opts, 3000);
            // by default VFS uses relative path.  This should turn the default
            // off so it will work like real URLs
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(this._opts, false);
         } catch (FileSystemException e) {
            FileProductUtility._logger.error(e.getMessage(), e);
         }
      } else if (uri.startsWith("ftp")) {
         FileProductUtility._logger.trace("enable FTP passive mode");
         FtpFileSystemConfigBuilder.getInstance().setPassiveMode(this._opts,
               true);
         // set up the FTP data timeout
         FtpFileSystemConfigBuilder.getInstance().setDataTimeout(this._opts, 3000);
      }

      try {
         this.fileSystemManager = new StandardFileSystemManager();
         this.fileSystemManager.setFilesCache(new DefaultFilesCache());
         this.fileSystemManager.init();
      } catch (Exception exception) {
         FileProductUtility._logger.error("Failed to setup FileSystemManager.", exception);

      }
   }

   /**
    * GetFile is called after parameters are set (uri, authentication) and returns the given file
    *
    * @return VFSFileProduct implementing the FileProduct interface, null if the file does not exist.
    * @throws Exception if unable to obtain file product object
    */
   public FileProduct getFile() throws Exception {
      FileObject File = null;
      FileProduct file = null;
      try {
         //fsManager = VFS.getManager();

         File = this.fileSystemManager.resolveFile(this._uri, _opts);
      } catch (FileSystemException e) {
         FileProductUtility._logger.error(e.getMessage(), e);
         throw new Exception(e.getMessage());
      }

      if (File != null) {
         file = new VFSFileProduct(File);
      }
      return file;
   }

   public boolean verifyFileExistence() {
      // Verify if a file exist without writing an error to log file if it does not exist.
      boolean o_fileExistFlag = false;
      try {
         FileObject fileObject = null;
         fileObject = this.fileSystemManager.resolveFile(this._uri, _opts);
         if (fileObject.exists()) {
            o_fileExistFlag = true;
         }
      } catch (FileSystemException e) {
         // Because this is a special function to check for file exitence, we don't throw an exception.
      }
      return o_fileExistFlag;
   }

   public void writeFile(InputStream is) throws Exception {

      _logger.info("Writing file to location..." + this._uri);
      org.apache.commons.vfs2.FileObject fo;
      try {
         fo = this.fileSystemManager.resolveFile(this._uri, _opts);
         OutputStream out = fo.getContent().getOutputStream();
         int i = -1;
         do {
            i = is.read();
            if (i != -1) {
               char letter = (char) i;
               //fout.append((char) i);
               out.write(letter);
            }
         } while (i != -1);

         FileSystem fs = fo.getFileSystem();
         fo.close();
         out.close();
         is.close();
         fs.getFileSystemManager().closeFileSystem(fs);


      } catch (FileSystemException e) {
         // TODO Auto-generated catch block
         throw new Exception(e.getMessage());
      } catch (IOException e) {
         // TODO Auto-generated catch block
         throw new Exception(e.getMessage());
      }

   }

   /**
    * setAuthentication sets the username and password for an s/ftp session
    *
    * @param username the username to be used for authentication
    * @param password the password to be used for authentication
    */
   public void setAuthentication(String username, String password) {
      try {

         StaticUserAuthenticator auth =
               new StaticUserAuthenticator(null, username, password);
         //this._opts = new FileSystemOptions();
         DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(
               this._opts, auth);

         if (password == null && this._uri.startsWith("sftp") && new File(this._keyFile).exists()) {

            SftpFileSystemConfigBuilder.getInstance().setIdentities(this._opts,
                  new File[]{new File(this._keyFile)});
         }
      } catch (FileSystemException e) {
         FileProductUtility._logger.error(e.getMessage(), e);
      }

   }

   public void cleanUp() {
      this.fileSystemManager.freeUnusedResources();
      this.fileSystemManager.close();
      this.fileSystemManager = null;
   }
}
