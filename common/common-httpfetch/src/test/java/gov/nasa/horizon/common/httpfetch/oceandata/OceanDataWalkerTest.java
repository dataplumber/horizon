/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.httpfetch.oceandata;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.ChecksumUtility;
import gov.nasa.horizon.common.httpfetch.api.HttpFileHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * This is the Unit test class for the OceanDataWalker class
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class OceanDataWalkerTest {
   private static Log _logger = LogFactory.getLog(OceanDataWalker.class);
   private List<String> cleanups = new Vector<>();

   @Test
   public void testSimple() {
      OceanDataWalker odw = new OceanDataWalker();
      HttpFileHandler handler = new TestHandler();
      odw.walk("http://oceandata.sci.gsfc.nasa.gov/Aquarius/V2/V2.3.1/L3SMI/2013/",
            "(http://.+/(Q)(\\d{7})\\d*\\.L\\S*\\.bz2)",
            handler);
      int stat = odw.checkResult();
      OceanDataWalkerTest._logger.info(stat + " URLs found.");

      // don't forget to clear the memory used by the handler
      //handler.shutdown();
      handler.postprocess();

      // don't forget to clear the memory used by the OceanDataWalker
      odw.shutdown();
   }


   /**
    * Implement the HttpFileHandler to retrieve the file for each URL
    */

   class TestHandler extends HttpFileHandler {

      @Override
      public void onProducts(Set<FileProduct> fileProducts) {
         for (FileProduct fileProduct : fileProducts) {
            OceanDataWalkerTest._logger.info(fileProduct.toString());
            if (fileProduct.getSize() == 0) {
               OceanDataWalkerTest._logger.warn("File: " + fileProduct
                     .getName() + " is zero size.");
               try {
                  fileProduct.close();
               } catch (IOException e) {
                  OceanDataWalkerTest._logger.error("Unable to close file " +
                        "object: " + fileProduct.getName());
               }
               return;
            }
            try {
               if (fileProduct.exists()) {
                  InputStream is = fileProduct.getInputStream();
                  String download = "/tmp/" + fileProduct.getName();
                  OutputStream os = new FileOutputStream(new File(download));
                  String computed = ChecksumUtility.getDigest(fileProduct
                        .getDigestAlgorithm(), is, os);
                  if (fileProduct.getDigestValue() != null) {
                     assertEquals("Verify checksum",
                           fileProduct.getDigestValue(), computed);
                  }
                  OceanDataWalkerTest._logger.info("Downloaded: " +
                        fileProduct.getName() +
                        "; Size: " + fileProduct.getSize() + "; Checksum: " + computed);
                  cleanups.add(download);

               }
            } catch (IOException e) {
               OceanDataWalkerTest._logger.error(e.getMessage(), e);
            }
            try {
               fileProduct.close();
            } catch (IOException e) {
               OceanDataWalkerTest._logger.error("Unable to close file " +
                     "object: " + fileProduct.getName());
            }
         }
      }
   }

   @After
   public void finish() {
      for (String filename : this.cleanups) {
         File f = new File(filename);
         if (f.exists()) {
            if (f.delete())
               OceanDataWalkerTest._logger.info("Deleted: " + filename);
            else
               OceanDataWalkerTest._logger.error("Unable to delete: " +
                     filename);
         }
      }
   }

}
