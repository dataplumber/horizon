/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class is an utility class that helps to deal with
 * compressing/decompressing of content using GZIP format.
 * 
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: GzipUtility.java 244 2007-10-02 20:12:47Z axt $
 * 
 */
public class GzipUtility {
   private static final int _BUFFER_SIZE = 1024;

   /**
    * Creates new instance.
    * 
    */
   protected GzipUtility() {
   }

   /**
    * Compresses data.
    * 
    * @param dataSource Data to be compressed.
    * @return Compressed data.
    * @throws IOException If there is an error.
    */
   public static byte[] compress(byte[] dataSource) throws IOException {
      ByteArrayOutputStream baos = null;
      GZIPOutputStream gos = null;

      int bytesToWrite = 0;
      int currentPosition = 0;
      try {
         // this initial size does not have to be accurate, however, providing
         // a value close to the final size would make it faster since less
         // byte allocation happens.
         baos = new ByteArrayOutputStream(dataSource.length);
         gos = new GZIPOutputStream(baos);

         while (currentPosition < dataSource.length) {
            bytesToWrite = _BUFFER_SIZE;
            if (bytesToWrite > (dataSource.length - currentPosition)) {
               bytesToWrite = (dataSource.length - currentPosition);
            }

            gos.write(dataSource, currentPosition, bytesToWrite);
            currentPosition += bytesToWrite;
         }
      } catch (Exception exception) {
         throw new IOException("Failed to compress data.");
      } finally {
         try {
            if (gos != null) {
               gos.finish();
               gos.close();
            }
            if (baos != null) {
               baos.close();
            }
         } catch (Exception exception) {
         }
      }
      return baos.toByteArray();
   }
   
   public static void compress(
      InputStream inputStream,
      OutputStream outputStream
      ) throws IOException {
      byte[] buffer = new byte[_BUFFER_SIZE];
      GZIPOutputStream gos = null;
      
      try {
         gos = new GZIPOutputStream(outputStream);
         
         int bytesRead = 0;
         while((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            gos.write(buffer, 0, bytesRead);
         }
      } catch(IOException exception) {
         throw exception;
      } finally {
         if(gos != null) {
            gos.finish();
            gos.close();
         }
      }
   }
   
   public static void compress(
      File inputFile,
      File outputFile
      ) throws IOException {
      FileInputStream fis = null;
      FileOutputStream fos = null;
      
      try {
         fis = new FileInputStream(inputFile);
         fos = new FileOutputStream(outputFile, false);
         compress(fis, fos);
      } catch(IOException exception) {
         throw exception;
      } finally {
         if(fis != null) {
            fis.close();
         }
         if(fos != null) {
            fos.close();
         }
      }
   }

   /**
    * Decompresses data.
    * 
    * @param data Data to be decompressed.
    * @return String object in specified encoding.
    * @throws IOException If there is an IO related error.
    * @throws UnsupportedEncodingException If given encoding is not supported.
    */
   public static byte[] decompress(byte[] data)
         throws IOException, UnsupportedEncodingException {
      byte[] buffer = new byte[_BUFFER_SIZE];
      ByteArrayInputStream bais = null;
      GZIPInputStream gis = null;
      ByteArrayOutputStream baos = null;

      int byteRead = 0;
      try {
         bais = new ByteArrayInputStream(data);
         gis = new GZIPInputStream(bais);
         baos = new ByteArrayOutputStream();

         while ((byteRead = gis.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, byteRead);
         }
      } catch (Exception exception) {
         throw new IOException("Failed to decompress data.");
      } finally {
         try {
            if (baos != null) {
               baos.close();
            }

            if (gis != null) {
               gis.close();
            }

            if (bais != null) {
               bais.close();
            }
         } catch (Exception exception) {
         }
      }
      return baos.toByteArray();
   }
   
   public static void decompress(
      InputStream inputStream,
      OutputStream outputStream
      ) throws IOException {
      byte[] buffer = new byte[_BUFFER_SIZE];
      GZIPInputStream gis = null;
      
      try {
         gis = new GZIPInputStream(inputStream);
         
         int bytesRead = 0;
         while((bytesRead = gis.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
         }
      } catch(IOException exception) {
         throw exception;
      } finally {
         if(gis != null) {
            gis.close();
         }
      }
   }
   
   public static void decompress(
      File inputFile,
      File outputFile
      ) throws IOException {
      FileInputStream fis = null;
      FileOutputStream fos = null;
      
      try {
         fis = new FileInputStream(inputFile);
         fos = new FileOutputStream(outputFile);
         decompress(fis, fos);
      } catch(IOException exception) {
         throw exception;
      } finally {
         if(fis != null) {
            fis.close();
         }
         if(fos != null) {
            fos.close();
         }
      }
   }
}
