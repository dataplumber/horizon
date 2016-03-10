/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.file;

import gov.nasa.horizon.common.api.util.DateTimeUtility;
import gov.nasa.horizon.common.api.util.ChecksumUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * Implements the required FileProduct interface using the Apache Virtual File System framework
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class VFSFileProduct implements FileProduct {
   public static Log _logger = LogFactory.getLog(VFSFileProduct.class);

   private FileObject _fileObj = null;
   private Semaphore _semaphore = new Semaphore(1);

   private long _size = -1;
   private long _modifiedTime = -1;

   private ChecksumUtility.DigestAlgorithm _digestAlgorithm = null;
   private String _digestValue = null;

   public VFSFileProduct(FileObject fileObj) {
      this._fileObj = fileObj;
      this.init();
   }

   public VFSFileProduct(FileObject fileObj, Semaphore semaphore) {
      this._fileObj = fileObj;
      this._semaphore = semaphore;
      this.init();
   }

   public String getChecksum(String algorithm) {
      String digest = null;

      if (this._fileObj != null) {
         try {
            digest = ChecksumUtility.getDigest(ChecksumUtility.DigestAlgorithm
                  .toAlgorithm(algorithm), this._fileObj.getContent().getInputStream());
         } catch (FileSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      return digest;
   }

   public ChecksumUtility.DigestAlgorithm getDigestAlgorithm() {
      return this._digestAlgorithm;
   }

   public void setDigestAlgorithm(ChecksumUtility.DigestAlgorithm digestAlgorithm) {
      this._digestAlgorithm = digestAlgorithm;
   }

   public String getDigestValue() {
      return this._digestValue;
   }

   public void setDigestValue(String digestValue) {
      this._digestValue = digestValue;
   }


   public void close() throws IOException {
      if (this._fileObj != null) {
         try {
            this._fileObj.close();
         } catch (FileSystemException e) {
            VFSFileProduct._logger.error(e.getMessage(), e);
            throw new IOException(e.getMessage());
         }
      }
      this._semaphore.release();
   }

   private void init() {
      try {
         this._size = this._fileObj.getContent().getSize();
         this._modifiedTime = this._fileObj.getContent().getLastModifiedTime();
      } catch (Exception e) {
         VFSFileProduct._logger.error("Access error for file " + this.getFriendlyURI() + ".");
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof FileProduct))
         return false;
      final FileProduct other = (FileProduct) obj;
      if (!this.getFriendlyURI().equals(other.getFriendlyURI()))
         return false;
      else if (this.getLastModifiedTime() != other.getLastModifiedTime())
         return false;
      else if (this.getSize() != other.getSize())
         return false;
      else if (this.getDigestValue() != null) {
         if (other.getDigestValue() == null) return false;
         if (!this.getDigestValue().equals(other.getDigestValue())) return false;
      } else if (other.getDigestValue() != null)
         return false;
      return true;
   }

   public String getFriendlyURI() {
      return this._fileObj.getName().getFriendlyURI();
   }

   public InputStream getInputStream() throws IOException {
      InputStream result = null;

      try {
         this._semaphore.acquire();
         result = this._fileObj.getContent().getInputStream();
      } catch (FileSystemException e) {
         VFSFileProduct._logger.error("Could not read file from filesystem: " + _fileObj.getName());
         throw new IOException(e.getMessage());
      } catch (InterruptedException e) {
         VFSFileProduct._logger.error(e.getMessage(), e);
      }

      return result;
   }

   public long getLastModifiedTime() {
      return this._modifiedTime;
   }

   public String getName() {
      return this._fileObj.getName().getBaseName();
   }

   public long getSize() {
      return this._size;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      if (this._fileObj != null) {
         result = prime * result + (int) (this.getLastModifiedTime() ^ (this.getLastModifiedTime() >>> 32));
         result = prime * result + (int) (this.getSize() ^ (this.getSize() >>> 32));
         result = prime * result + this.getFriendlyURI().hashCode();
      }
      return result;
   }

   public boolean isReadable() {
      boolean result = false;
      try {
         result = this._fileObj.isReadable();
      } catch (Exception e) {
         VFSFileProduct._logger.error(e.getMessage(), e);
      }
      return result;
   }

   public boolean isWriteable() {
      boolean result = false;
      try {
         result = this._fileObj.isWriteable();
      } catch (Exception e) {
         VFSFileProduct._logger.error(e.getMessage(), e);
      }
      return result;
   }

   public String toString() {
      return this._fileObj.getName().getFriendlyURI()
            + " "
            + DateTimeUtility.getDateCCSDSAString(new Date(this._modifiedTime)) + " "
            + this._size;
   }

   public boolean exists() {
      boolean result = false;
      try {
         result = this._fileObj.exists();
      } catch (Exception e) {
         VFSFileProduct._logger.error(e.getMessage(), e);
      }
      return result;
   }

   public boolean delete() {
      boolean result = false;
      try {
         result = this._fileObj.delete();
      } catch (Exception e) {
         VFSFileProduct._logger.error(e.getMessage(), e);
      }
      return result;
   }
}
