package gov.nasa.horizon.common.api.file;

import gov.nasa.horizon.common.api.util.ChecksumUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.MalformedURLException;

/**
 * Implement wrapper class for File Product
 *
 * @author T. Huang
 * @version $Id: $
 */
public class LocalFileProduct implements FileProduct {
   private static Log _logger = LogFactory.getLog(LocalFileProduct.class);

   private String filename = null;
   private File file = null;
   private FileInputStream fis = null;
   private ChecksumUtility.DigestAlgorithm alg = ChecksumUtility.DigestAlgorithm.MD5;
   private String digest = null;

   public LocalFileProduct(String filename) {
      this.filename = filename;
   }

   private void init(String filename) {
      if (this.file != null) return;
      this.init(new File(filename));
   }

   private void init(File file) {
      if (this.file != null) return;
      this.file = file;
      try {
         this.fis = new FileInputStream(this.file);
      } catch (FileNotFoundException e) {
         _logger.error("Unable to create input stream to file: " + file.getName());
      }
   }

   @Override
   public void close() throws IOException {
      if (this.fis != null) {
         this.fis.close();
      }
   }

   @Override
   public String getFriendlyURI() {
      String result = null;
      this.init(this.filename);
      if (this.file != null) {
         try {
            result = this.file.toURI().toURL().toString();
         } catch (MalformedURLException e) {
            _logger.error("Unable to generate URL for file: " + this.file.getAbsoluteFile());
         }
      }
      return result;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      this.init(this.filename);
      return this.fis;
   }

   @Override
   public long getLastModifiedTime() {
      this.init(this.filename);
      return this.file.lastModified();
   }

   @Override
   public ChecksumUtility.DigestAlgorithm getDigestAlgorithm() {
      this.init(this.filename);
      return this.alg;
   }

   @Override
   public void setDigestAlgorithm(ChecksumUtility.DigestAlgorithm alg) {
      this.init(this.filename);
      this.alg = alg;
      if (this.file.canRead()) {
         this.digest = ChecksumUtility.getDigest(this.alg, this.file);
      }
   }

   @Override
   public String getDigestValue() {
      this.init(this.filename);
      if (this.digest == null && this.file.canRead()) {
         this.digest = ChecksumUtility.getDigest(this.alg, this.file);
      }
      return this.digest;
   }

   @Override
   public void setDigestValue(String digest) {
      this.init(this.filename);
      this.digest = digest;
   }
   
   public void setDigestValue(String digest, boolean initialize) {
	  if(initialize) { 
		  this.init(this.filename); 
	  }
      this.digest = digest;
   }

   @Override
   public String getName() {
      this.init(this.filename);
      return this.file.getName();
   }

   @Override
   public long getSize() {
      this.init(this.filename);
      return this.file.length();
   }

   @Override
   public boolean isReadable() {
      return this.file.canRead();
   }

   @Override
   public boolean isWriteable() {
      this.init(this.filename);
      return this.file.canWrite();
   }

   @Override
   public boolean exists() {
      this.init(this.filename);
      return this.file.exists();
   }

   @Override
   public boolean delete() {
      this.init(this.filename);
      return this.file.delete();
   }
}
