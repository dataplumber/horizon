/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;


/**
 * This class is an utility class that helps to deal with checksum.
 *
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: ChecksumUtility.java 244 2007-10-02 20:12:47Z axt $
 */
public class ChecksumUtility {
   /**
    * As of Java 5, these are the supported Message Digest Algorithms
    */
   public enum DigestAlgorithm {

      MD2("MD2"), MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"), SHA384(
            "SHA-384"), SHA512("SHA-512"), NONE("NONE");

      private final String _value;

      DigestAlgorithm(String value) {
         _value = value;
      }

      public String toString() {
         return _value;
      }

      public MessageDigest createDigest() {
         if (this._value == null
               || DigestAlgorithm.toAlgorithm(this._value) == null)
            return null;
         try {
            return MessageDigest.getInstance(this._value);
         } catch (NoSuchAlgorithmException e) {
            return null;
         }
      }

      public static DigestAlgorithm toAlgorithm(String value) {
         if (value.equalsIgnoreCase("MD2"))
            return MD2;
         if (value.equalsIgnoreCase("MD5"))
            return MD5;
         if (value.equalsIgnoreCase("SHA-1") || value.equalsIgnoreCase("SHA1"))
            return SHA1;
         if (value.equalsIgnoreCase("SHA-256") || value.equalsIgnoreCase("SHA256"))
            return SHA256;
         if (value.equalsIgnoreCase("SHA-384") || value.equalsIgnoreCase("SHA384"))
            return SHA384;
         if (value.equalsIgnoreCase("SHA-512") || value.equalsIgnoreCase("SHA512"))
            return SHA512;
         if (value.equalsIgnoreCase("NONE"))
            return NONE;

         return null;
      }
   }

   private final static int _BUFFER_SIZE = 10240;

   /**
    * Creates new instance.
    */
   protected ChecksumUtility() {
   }

   /**
    * Gets a digest.
    *
    * @param algorithm Algorithm to be used for the digest.
    * @param content   Content to get a digest for.
    * @return Digest on success, null otherwise. Note that a length of returned
    *         string will depend on the algorithm used.
    * @see #getDigest(gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm,
    *      String)
    */
   public static String getDigest(DigestAlgorithm algorithm, String content) {
      return getDigest(algorithm, content.getBytes());
   }

   /**
    * Checks if a digest for given content matches with given digest.
    *
    * @param algorithm Algorithm to be used for the digest.
    * @param content   Content to get a digest for.
    * @param digest    Expected digest value.
    * @return True if calculated digest matches with the expected one, false
    *         otherwise.
    */
   public static boolean validate(DigestAlgorithm algorithm, String content,
                                  String digest) {
      String targetDigest = getDigest(algorithm, content);
      return ((targetDigest != null) && (targetDigest.equals(digest))) ? true
            : false;
   }

   /**
    * Gets a digest.
    *
    * @param algorithm Algorithm to be used for the digest.
    * @param content   Content to get a digest for.
    * @return Digest on success, null otherwise. Note that a length of returned
    *         string will depend on the algorithm used.
    */
   public static String getDigest(DigestAlgorithm algorithm, byte[] content) {
      ByteArrayInputStream bais = new ByteArrayInputStream(content);
      String result = getDigest(algorithm, bais);
      try {
         bais.close();
      } catch (Exception exception) {
      }

      return result;
   }

   /**
    * Checks if a digest for given content matches with given digest.
    *
    * @param algorithm Algorithm to be used for the digest.
    * @param content   Content to get a digest for.
    * @param digest    Expected digest value.
    * @return True if calculated digest matches with the expected one, false
    *         otherwise.
    */
   public static boolean validate(DigestAlgorithm algorithm, byte[] content,
                                  String digest) {
      String targetDigest = getDigest(algorithm, content);
      return ((targetDigest != null) && (targetDigest.equals(digest))) ? true
            : false;
   }

   /**
    * Gets a digest.
    *
    * @param algorithm Algorithm to be used for the digest.
    * @param file      File object representing a file to get digest for.
    * @return Digest on success, null otherwise. Note that a length of returned
    *         string will depend on the algorithm used.
    */
   public static String getDigest(DigestAlgorithm algorithm, File file) {
      String result = null;
      FileInputStream fis = null;

      try {
         fis = new FileInputStream(file);
         result = getDigest(algorithm, fis);
      } catch (Exception exception) {
      } finally {
         if (fis != null) {
            try {
               fis.close();
            } catch (Exception exception) {
            }
         }
      }

      return result;
   }


   /**
    * Checks if a digest for given content matches with given digest.
    *
    * @param algorithm Algorithm to be used for the digest.
    * @param file      File object representing a file to get digest for.
    * @param digest    Expected digest value.
    * @return True if calculated digest matches with the expected one, false
    *         otherwise.
    */
   public static boolean validate(DigestAlgorithm algorithm, File file,
                                  String digest) {
      String targetDigest = getDigest(algorithm, file);
      return ((targetDigest != null) && (targetDigest.equals(digest))) ? true
            : false;
   }

   /**
    * Gets a digest.
    *
    * @param algorithm   Algorithm to be used for the digest.
    * @param inputStream InputStream to read content from.
    * @return Digest on success, null otherwise. Note that a length of returned
    *         string will depend on the algorithm used.
    */
   public static String getDigest(DigestAlgorithm algorithm,
                                  InputStream inputStream) {
      String result = null;
      try {
         MessageDigest messageDigest =
               MessageDigest.getInstance(algorithm.toString());
         messageDigest.reset();

         int bytesRead;
         byte[] buffer = new byte[_BUFFER_SIZE];
         while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            messageDigest.update(buffer, 0, bytesRead);
         }

         byte[] digest = messageDigest.digest();
         result = "";
         for (int i = 0; i < digest.length; ++i) {
            result +=
                  Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
         }
         // for (int i = 0; i < digest.length; i++) {
         // result.append(Integer.toHexString(0xff & digest[i]));
         // }
      } catch (Exception exception) {
         result = null;
      }

      return result;
   }

   public static Long getCRC32Digest(InputStream inputStream) {
      CRC32 cksum = new CRC32();
      Long result;
      try {
         byte[] bytes = new byte[_BUFFER_SIZE];
         int len = 0;
         while ((len = inputStream.read(bytes)) >= 0) {
            cksum.update(bytes, 0, len);
         }
      } catch (IOException e) {
         result = null;
      } finally {
         try {
            inputStream.close();
         } catch (IOException e) {
         }
      }
      return cksum.getValue();
   }

   public static String getDigest(DigestAlgorithm algorithm,
                                  InputStream is, OutputStream os) {
      String result = null;
      try {
         MessageDigest messageDigest =
               MessageDigest.getInstance(algorithm.toString());
         messageDigest.reset();

         int bytesRead;
         byte[] buffer = new byte[_BUFFER_SIZE];
         while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1) {
            messageDigest.update(buffer, 0, bytesRead);
            os.write(buffer, 0, bytesRead);
         }

         byte[] digest = messageDigest.digest();
         result = "";
         for (int i = 0; i < digest.length; ++i) {
            result +=
                  Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
         }
      } catch (Exception exception) {
         result = null;
      }

      return result;
   }
}
