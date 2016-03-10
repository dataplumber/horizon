/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler.spider.provider;

import gov.nasa.horizon.common.api.util.ChecksumUtility;
import gov.nasa.horizon.common.api.util.DateTimeUtility;
import gov.nasa.horizon.common.api.file.FileProduct;

import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

/**
 * This class used to represent the FileProduct object as it was restored from
 * the persistent store (cache). It contains just enough info on the previously
 * seen files.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public class CacheFileProduct implements FileProduct {

   private String _uri;
   private long _size;
   private long _modifiedTime;
   private ChecksumUtility.DigestAlgorithm _digestAlgorithm;
   private String _digestValue;

   public CacheFileProduct(String uri, long size, long modifiedTime) {
      this._uri = uri;
      this._size = size;
      this._modifiedTime = modifiedTime;
   }

   public CacheFileProduct(String uri, long size, long modifiedTime, String digestAlgorithm, String digestValue) {
      this._uri = uri;
      this._size = size;
      this._modifiedTime = modifiedTime;
      this._digestAlgorithm = ChecksumUtility.DigestAlgorithm.toAlgorithm(digestAlgorithm);
      this._digestValue = digestValue;
   }

   public void close() throws IOException {
      // no-op
   }

   public String getFriendlyURI() {
      return this._uri;
   }

   public InputStream getInputStream() throws IOException {
      return null;
   }

   public long getLastModifiedTime() {
      return this._modifiedTime;
   }

   public String getName() {
      return this._uri.substring(this._uri.lastIndexOf("/") + 1, this._uri
            .length());
   }

   public long getSize() {
      return this._size;
   }

   public boolean isReadable() {
      return false;
   }

   public boolean isWriteable() {
      return false;
   }

   public ChecksumUtility.DigestAlgorithm getDigestAlgorithm() {
      return this._digestAlgorithm;
   }

   @Override
   public void setDigestAlgorithm(ChecksumUtility.DigestAlgorithm alg) {
      this._digestAlgorithm = alg;
   }

   public String getDigestValue() {
      return this._digestValue;
   }

   @Override
   public void setDigestValue(String digest) {
      this._digestValue = digest;
   }

   public String toString() {
      return this._uri + " "
            + DateTimeUtility.getDateCCSDSAString(new Date(this._modifiedTime))
            + " " + this._size;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (_modifiedTime ^ (_modifiedTime >>> 32));
      result = prime * result + (int) (_size ^ (_size >>> 32));
      result = prime * result + ((_uri == null) ? 0 : _uri.hashCode());
      return result;
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

   public boolean delete() {
      return false;
   }

   public boolean exists() {
      return true;
   }
}
