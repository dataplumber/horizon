package gov.nasa.horizon.common.httpfetch.modaps;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.ChecksumUtility;
import gov.nasa.horizon.common.httpfetch.api.HttpFileHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * This is a unit test for the MODAPS HTTP data walker
 *
 * @author T. Huang
 * @version $Id: $
 */
public class MODAPSDataWalkerTest {
   private static Log _logger = LogFactory.getLog(MODAPSDataWalkerTest.class);
   private List<String> cleanups = Collections.synchronizedList(new Vector<String>());

   @Test
   public void testSimple() {
      Timer timer = new Timer();
      //MODAPSDataWalker mdw = new MODAPSDataWalker();
      HttpFileHandler handler = new TestHandler();
      List<String> expressions = new Vector<>();
      expressions.add("^RRGlobal_r\\d{2}c\\d{2}.2013233.aqua.250m.jpg$");
      expressions.add("^RRGlobal_r\\d{2}c\\d{2}.2013233.aqua.250m.jgw$");
      expressions.add("^RRGlobal_r\\d{2}c\\d{2}.2013233.aqua.txt$");

      Iterator<String> urls = new Iterator<String>() {
         int row=0;
         int col=0;

         public boolean hasNext() {
            if (row==19) return false;
            return true;
         }

         public String next() {
            String result = String.format("http://lance2.modaps.eosdis.nasa.gov/imagery/subsets/RRGlobal_r%02dc%02d/%04d%03d/",
                  row, col, 2013, 233);
            if (col==39) {
               ++row;
               col=0;
            } else {
               ++col;
            }
            return result;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };

      MODAPSDataWalker mdw = new MODAPSDataWalker(
            //"http://lance2.modaps.eosdis.nasa.gov/imagery/subsets/RRGlobal_r02c00/2013233/",
            urls,
            expressions, handler);
      timer.scheduleAtFixedRate(mdw, 5000, 5000);

      try {
         Thread.sleep(30000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      mdw.setStop(true);
      mdw.cancel();

      timer.cancel();
      timer.purge();
      // don't forget to clear the memory used by the handler
      handler.postprocess();

      // don't forget to clear the memory used by the MODAPDataWalker
      mdw.shutdown();
   }

   /**
    * Implement the HttpFileHandler to retrieve the file for each URL
    */
   class TestHandler extends HttpFileHandler {

      @Override
      public void onProducts(Set<FileProduct> fileProducts) {
         MODAPSDataWalkerTest._logger.info("New batch: " + fileProducts.size());
         for (FileProduct fileProduct : fileProducts) {
            MODAPSDataWalkerTest._logger.info(fileProduct.getName());

            String filename = fileProduct.getName();
            if (fileProduct.getSize() == 0) {
               MODAPSDataWalkerTest._logger.warn("File: " + filename + " is zero size.");
               try {
                  fileProduct.close();
               } catch (IOException e) {
                  MODAPSDataWalkerTest._logger.debug("Closing invalid file product.");
               }
               return;
            }

            try {
               if (fileProduct.exists()) {
                  InputStream is = fileProduct.getInputStream();
                  String download = "/tmp/" + fileProduct.getName();
                  OutputStream os = new FileOutputStream(new File(download));
                  String computed = ChecksumUtility.getDigest
                        (ChecksumUtility.DigestAlgorithm.MD5, is, os);
                  if (fileProduct.getDigestValue() != null) {
                     MODAPSDataWalkerTest._logger.debug("actual: " + computed + "  expected: " + fileProduct.getDigestValue());

                     assertEquals("Verify checksum", fileProduct.getDigestValue(), computed);
                  }
                  MODAPSDataWalkerTest._logger.info("Downloaded: " + filename + "; Size: " + fileProduct.getSize() + "; Checksum: " + computed);
                  cleanups.add(download);
               }
            } catch (IOException e) {
               MODAPSDataWalkerTest._logger.error(e.getMessage(), e);
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
               MODAPSDataWalkerTest._logger.info("Deleted: " + filename);
            else
               MODAPSDataWalkerTest._logger.error("Unable to delete: " +
                     filename);
         }
      }
   }
}
