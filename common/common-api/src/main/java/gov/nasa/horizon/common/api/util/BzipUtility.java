/***************************************************************************
*
* Copyright 2010, by the California Institute of Technology. ALL
* RIGHTS RESERVED. United States Government Sponsorship acknowledged.
* Any commercial use must be negotiated with the Office of Technology
* Transfer at the California Institute of Technology.
*
* @version $Id: BzipUtility.java 6511 2010-12-16 18:13:59Z qchau $
*
****************************************************************************/

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

import org.apache.commons.io.IOUtils;

import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.CompressorOutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import org.apache.commons.compress.compressors.CompressorException;

/**
 * This class is an utility class that helps to deal with
 * compressing/decompressing of content using BZIP2 format.
 * 
 * @version $Id: BzipUtility.java 6511 2010-12-16 18:13:59Z qchau $
 * 
 */

public class BzipUtility {
   private static final int m_BUFFER_SIZE = 4096;

   // Original author for GzipUtility class was A. Tagaki, modified for BzipUtility class.

   /**
    * Creates new instance.
    * 
    */
   protected BzipUtility() {
   }

   /**
    * Compresses data.
    * 
    * @param data Data to be compressed.
    * @return Compressed data.
    * @throws IOException If there is an error.
    */
   public static byte[] compress(byte[] dataSource) throws IOException 
   {

      ByteArrayOutputStream baos = null;
      BZip2CompressorOutputStream zipOutputStream = null;

      int bytesToWrite = 0;
      int currentPosition = 0;
      try {
         // this initial size does not have to be accurate, however, providing
         // a value close to the final size would make it faster since less
         // byte allocation happens.
         baos = new ByteArrayOutputStream(dataSource.length);
         zipOutputStream = new BZip2CompressorOutputStream(baos);

         while (currentPosition < dataSource.length) {
            bytesToWrite = m_BUFFER_SIZE;
            if (bytesToWrite > (dataSource.length - currentPosition)) {
               bytesToWrite = (dataSource.length - currentPosition);
            }

            zipOutputStream.write(dataSource, currentPosition, bytesToWrite);
            currentPosition += bytesToWrite;
         }
      } catch (Exception an_exception) {
         throw new IOException("Failed to compress data.");
      } finally {
         try {
            if (zipOutputStream != null) {
               zipOutputStream.finish();
               zipOutputStream.close();
            }
            if (baos != null) {
               baos.close();
            }
         } catch (Exception an_exception) {
         }
      }
      return baos.toByteArray();
   }
   
   public static void compress(InputStream  inputStream,
                               OutputStream outputStream) throws IOException 
   {
      // Notes:
      //
      //   1.  The .bz2 output file from this function will have different MD5 sum than the .bz2 output file from the bzip2 from command line.
      //       The important thing is when the bunzip2 command is applied, the uncompress file is identical to its original version.
      //   2.  This function will not delete the original uncompress file.  The user will have to delete it on his/her own.

      CompressorOutputStream cos = null; 

      try {
         //cos = new CompressorStreamFactory().createCompressorOutputStream("bzip2",outputStream);
         cos = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2,outputStream);
         IOUtils.copy(inputStream, cos);
         cos.close();
      } catch(CompressorException an_exception) {
           an_exception.printStackTrace();
      } finally {
         if (cos != null) {
            cos.close();
         }
      }
   }
   
   public static void compress(File inputFile,
                               File outputFile) throws IOException 
   {

      FileInputStream fis = null;
      FileOutputStream fos = null;
      
      try {
         fis = new FileInputStream(inputFile);
         fos = new FileOutputStream(outputFile, false);

         compress(fis, fos);

      } catch(IOException an_exception) {
         throw an_exception;
      } finally {
         if (fis != null) {
            fis.close();
         }
         if (fos != null) {
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
   public static byte[] decompress(byte[] data) throws IOException, UnsupportedEncodingException
   {

      byte[] buffer = new byte[m_BUFFER_SIZE];
      ByteArrayInputStream       bais = null;
      BZip2CompressorInputStream gis = null;
      ByteArrayOutputStream      baos = null;

      int byteRead = 0;
      try {
         bais = new ByteArrayInputStream(data);
         gis  = new BZip2CompressorInputStream(bais);
         baos = new ByteArrayOutputStream();

         while ((byteRead = gis.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, byteRead);
         }
      } catch (Exception an_exception) {
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
         } catch (Exception an_exception) {
         }
      }
      return baos.toByteArray();
   }
   
   public static void decompress(FileInputStream  inputStream,
                                 FileOutputStream outputStream) throws IOException
   {

      try {
            BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(inputStream);
            final byte[] buffer = new byte[m_BUFFER_SIZE];
            int numBytesRead = 0;
            while (-1 != (numBytesRead = bzIn.read(buffer))) {
              outputStream.write(buffer, 0, numBytesRead);
            }
            outputStream.close();
            bzIn.close();
      } catch(IOException an_exception) {
         throw an_exception;
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }
   
   public static void decompress(File inputFile,
                                 File outputFile) throws IOException
   {
      FileInputStream fis = null;
      FileOutputStream fos = null;
      
      try {
         fis = new FileInputStream(inputFile);
         fos = new FileOutputStream(outputFile);

         decompress(fis, fos);

      } catch (IOException an_exception) {
         throw an_exception;
      } finally {
         if (fis != null) {
            fis.close();
         }
         if (fos != null) {
            fos.close();
         }
      }
   }
}
