/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.httpfetch.api;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * This class implements the FileProduct interface for the HTTP file objects
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class HttpFileProduct implements FileProduct {
   private String _uri;
   private String _name;
   private long _lastModified = 0;
   private long _size = 0;
   private boolean _readable = false;
   private boolean _exists = false;
   private boolean _delete;
   private boolean _shared = false;
   private DigestAlgorithm _digestAlgorithm = null;
   private String _digestValue = null;

   private HttpClient _httpclient = null;
   private HttpEntity _entity = null;

   public HttpFileProduct(String uri, String name) {
      this._uri = uri;
      this._name = name;
   }

   public HttpFileProduct(String uri, String name, long time, long size) {
      this._uri = uri;
      this._name = name;
      this._lastModified = time;
      this._size = size;
      if (size == 0) this._exists = false;
      this._readable = true;
      this._exists = true;
   }

   public HttpFileProduct(HttpClient httpclient, String uri, String name, long time, long size) {
      this._uri = uri;
      this._name = name;
      this._lastModified = time;
      this._size = size;
      this._readable = true;
      this._exists = true;
      if (size == 0) this._exists = false;
      this._httpclient = httpclient;
      this._shared = true;
   }

   public void close() throws IOException {
      if (this._shared) return;
      if (this._httpclient != null) {
         this._httpclient.getConnectionManager().shutdown();
      }

      this._entity = null;
      this._httpclient = null;
   }

   public String getFriendlyURI() {
      return this._uri;
   }

   public void setFriendlyURI(String uri) {
      this._uri = uri;
   }

   public DigestAlgorithm getDigestAlgorithm() {
      return this._digestAlgorithm;
   }

   public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
      this._digestAlgorithm = digestAlgorithm;
   }

   public String getDigestValue() {
      return this._digestValue;
   }

   public void setDigestValue(String digestValue) {
      this._digestValue = digestValue;
   }

   public InputStream getInputStream() throws IOException {
      if (!this._shared) {
         this._httpclient = new DefaultHttpClient();
      }
      HttpGet httpget = new HttpGet(this._uri);
      // force to close the connection when operation is complete.
      // this prevents dangling TIME_WAITS
      //httpget.setHeader("Connection", "close");
      InputStream is = null;
      try {
         HttpResponse response = this._httpclient.execute(httpget);
         this._entity = response.getEntity();
         if (this._entity != null) {
            is = this._entity.getContent();
         }
      } catch (IOException ex) {
         this.close();
         throw ex;
      } catch (RuntimeException ex) {
         httpget.abort();
         this.close();
      }
      return is;
   }

   public long getLastModifiedTime() {
      return this._lastModified;
   }

   public void setLastModifiedTime(long time) {
      this._lastModified = time;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public long getSize() {
      return this._size;
   }

   public void setSize(long size) {
      this._size = size;
   }

   public boolean isReadable() {
      return this._readable;
   }

   public void setReadable(boolean readable) {
      this._readable = readable;
   }

   public boolean isWriteable() {
      return false;
   }

   public boolean exists() {
      return this._exists;
   }

   public void setExists(boolean exists) {
      this._exists = exists;
   }

   public boolean delete() {
      return this._delete;
   }

   public void setDelete(boolean delete) {
      this._delete = delete;
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
         if (!this.getDigestValue().equals(other.getDigestValue()))
            return false;
      } else if (other.getDigestValue() != null)
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime
            * result
            + (int) (this.getLastModifiedTime() ^ (this.getLastModifiedTime() >>> 32));
      result = prime * result
            + (int) (this.getSize() ^ (this.getSize() >>> 32));
      result = prime * result + ((_uri == null) ? 0 : _uri.hashCode());
      return result;
   }

   public String toString() {
      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
      sdf.applyPattern("dd MMM yyyy HH:mm:ss z");
      return "\nName: " + this._name + "\nURL: " + this._uri
            + "\nLast Modified: " + sdf.format(new Date(this._lastModified))
            + "\nSize: " + this._size;
   }
}
