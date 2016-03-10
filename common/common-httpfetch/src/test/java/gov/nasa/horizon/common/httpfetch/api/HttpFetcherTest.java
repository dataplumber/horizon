package gov.nasa.horizon.common.httpfetch.api;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.ChecksumUtility;
import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA. User: thuang Date: Jun 10, 2010 Time: 3:56:00 PM To
 * change this template use File | Settings | File Templates.
 */
public class HttpFetcherTest extends TestCase {
   public static Log _logger = LogFactory.getLog(HttpFetcherTest.class);

   @Ignore
   public void testTextGet() {
      HttpFetcher fetcher = new HttpFetcher();
      try {
         String html = fetcher
               .getTextContent("http://oceandata.sci.gsfc.nasa.gov/MODISA/L3SMI/2010/160/");
         assert html != null;
         HttpFetcherTest._logger.debug(html);
      } catch (FetchException ex) {
         ex.printStackTrace();
      } finally {
         fetcher.shutdown();
      }
   }

   @Ignore
   public void filter() {
      HttpFetcher fetcher = new HttpFetcher();
      try {
         List<String> urls = fetcher
               .getFilteredText(
                     "http://oceandata.sci.gsfc.nasa.gov/MODISA/L3SMI/2010/160/",
                     "(http://.+/(A|T)(\\d{4})(\\d{3})\\d*\\.L3m_(DAY|8D|MO|YR)_N{0,1}SST4_(4|9)\\.bz2)");
         assert urls.size() > 0;
         for (String url : urls) {
            HttpFetcherTest._logger.debug(url);
         }
      } catch (FetchException ex) {
         ex.printStackTrace();
      } finally {
         fetcher.shutdown();
      }
   }

   @Test
   public void testMODAP() {
      String path = "http://lance2.modaps.eosdis.nasa.gov/imagery/subsets/";
      HttpFetcher fetcher = new HttpFetcher();
      List<FileProduct> fps = new ArrayList<FileProduct>();
      String ydate = "2013222";

      for (int r=2; r<=19; ++r) {
         for (int c=0; c<=39; ++c) {
            String rpath = String.format("%sRRGlobal_r%02dc%02d/%s/",
                  path, r, c, ydate);
            System.out.println ("Trying " + rpath);
            try {

               String aquaprefix = String.format("RRGlobal_r\\d{2}c\\d{2}." +
                     ydate + ".aqua", r, c);
               List<String> filelinks = fetcher.getFilteredText(rpath,
                     "href=\"(" + aquaprefix + ".250m." + "(jpg|jgw))\"");
               for (String link: filelinks) {
                  if (link.endsWith("jpg")) {
                     System.out.println("IMAGE: " + rpath + "/" + link);
                  } else if (link.endsWith("jgw")) {
                     System.out.println("WORLD: " + rpath + "/" + link);
                  } else {
                     System.out.println("UNKNOWN: " + rpath + "/" + link);
                  }
               }
               System.out.println ("");
            } catch (FetchException ex) {
               HttpFetcherTest._logger.error(ex);
            }
         }
      }
      fetcher.shutdown();
   }

   @Ignore
   public void testFileProduct() {
      String path = "http://oceandata.sci.gsfc.nasa.gov/MODISA/L3SMI/2010/160/";
      HttpFetcher fetcher = new HttpFetcher();
      List<FileProduct> fps = new ArrayList<FileProduct>();
      try {
         List<String> files = fetcher
               .getFilteredText(
                     path,
                     "(http://.+/(A|T)(\\d{4})(\\d{3})\\d*\\.L3m_(DAY|8D|MO|YR)_N{0,1}SST4_(4|9)\\.bz2)");

         for (String file : files) {
            fps.add(fetcher.createFileProduct(file));
         }
      } catch (FetchException ex) {
         ex.printStackTrace();
      } finally {
         fetcher.shutdown();
      }

      for (FileProduct fp : fps) {
         HttpFetcherTest._logger.debug(fp);
         InputStream is = null;
         OutputStream os = null;
         try {
            if (fp.exists()) {
               is = fp.getInputStream();
               os = new FileOutputStream(new File("/tmp/" + fp.getName()));
               String checksum = ChecksumUtility.getDigest(DigestAlgorithm.MD5, is, os);
               HttpFetcherTest._logger.debug("checksum: " + checksum);
               //FileUtility.downloadFromStream(is, os);
            }
         } catch (IOException e) {
            HttpFetcherTest._logger.error(e.getMessage(), e);
         } finally {
            if (fp.exists()) {
               try {
                  if (is != null) is.close();
                  if (os != null) os.close();
                  fp.close();
               } catch (IOException e) {
               }
            }
         }
      }
   }

   @Ignore
   public void walker() {
      HttpFetcher fetcher = new HttpFetcher();
      try {
         String sat = "MODISA";
         String level = "L3SMI";
         String path = "http://oceandata.sci.gsfc.nasa.gov/" + sat + "/"
               + level + "/";
         List<String> years = fetcher.getFilteredText(path,
               "(href=\"(\\d{4})\")");
         Pattern p = Pattern.compile("href=\"(\\d{4})\"");
         for (int i = 0; i < years.size(); ++i) {
            Matcher m = p.matcher(years.get(i));
            if (m.find()) {
               years.set(i, path + m.group(1) + "/");
            }
         }

         List<FileProduct> fps = new ArrayList<FileProduct>();
         for (String year : years) {
            List<String> doys = fetcher.getFilteredText(year,
                  "(href=\"(\\d{3})\")");
            p = Pattern.compile("href=\"(\\d{3})\"");

            for (int i = 0; i < doys.size(); ++i) {
               Matcher m = p.matcher(doys.get(i));
               if (m.find()) {
                  doys.set(i, year + m.group(1) + "/");
               }
            }

            for (String doy : doys) {
               List<String> files = fetcher
                     .getFilteredText(
                           doy,
                           "(http://.+/(A|T)(\\d{4})(\\d{3})\\d*\\.L3m_(DAY|8D|MO|YR)_N{0,1}SST4_(4|9)\\.bz2)");

               for (String file : files) {
                  fps.add(fetcher.createFileProduct(file));
               }
            }
         }

         for (FileProduct fp : fps) {
            HttpFetcherTest._logger.info(fp);
         }
      } catch (FetchException ex) {
         ex.printStackTrace();
      } finally {
         fetcher.shutdown();
      }
   }

}
