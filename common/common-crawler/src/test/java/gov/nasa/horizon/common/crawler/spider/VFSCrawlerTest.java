/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler.spider;

import gov.nasa.horizon.common.crawler.spider.provider.VFSCrawler;
import gov.nasa.horizon.common.crawler.spider.provider.VFSCrawlerManager;
import gov.nasa.horizon.common.crawler.spider.registry.CrawlerRegistry;
import gov.nasa.horizon.common.crawler.spider.registry.provider.FileCrawlerRegistry;
import gov.nasa.horizon.common.api.file.FileFilter;
import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.file.FileProductHandler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * VFS Crawler tests.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 *
 */
public class VFSCrawlerTest {
   /**
    * Implement the callback class to handle the list of file returned.
    *
    * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
    * @version $Id: $
    *
    */
   class TestHandler implements FileProductHandler {
      private FileFilter _filter;

      public TestHandler(FileFilter filter) {
         this._filter = filter;
      }

      public void preprocess() {
         VFSCrawlerTest._logger.debug("preprocess method.");
      }

      public synchronized void onProducts(Set<FileProduct> fileProducts) {
         VFSCrawlerTest._logger.debug("Found " + fileProducts.size()
               + " files.");
         for (FileProduct fileProduct : fileProducts) {
            VFSCrawlerTest._logger.info(fileProduct);
         }

         if (this._filter != null && fileProducts.size() > 0) {
            this._filter.setExcludeList(fileProducts);
         }
      }

      public void postprocess() {
         VFSCrawlerTest._logger.debug("postprocess method.");
      }

      public void onError (Throwable e) {
         VFSCrawlerTest._logger.error(e.getMessage(), e);
      }

   }

   public static Log _logger = LogFactory.getLog(VFSCrawlerTest.class);
   private String _localRootURI = "file://tmp";
   private String _remoteRootURI1;
   private String _remoteRootURI2;
   private String _remoteUser1;
   private String _remotePass1;
   private String _remoteUser2;
   private String _remotePass2;
   private FileFilter _filter1;
   private FileFilter _filter2;
   private FileFilter _filter3;
   private String _cacheFile;

   @After
   public void cleanup() {
      File cacheFile = new File(this._cacheFile);
      if (cacheFile.exists())
         cacheFile.delete();
   }

   protected void _batchRun(Crawler crawler) {
      Set<FileProduct> products = crawler.crawl();
      for (FileProduct product : products) {
         VFSCrawlerTest._logger.info(product);
      }
      crawler.stop();
   }

   protected void _managerRun(Crawler crawler) {
      CrawlerManager manager = new VFSCrawlerManager();
      manager.start();
      try {
         manager.registerCrawler(crawler);
         Thread.sleep(3000);
      } catch (CrawlerException e) {
         VFSCrawlerTest._logger.error(e.getMessage());
      } catch (InterruptedException e) {
         VFSCrawlerTest._logger.error(e.getMessage());
      }
      manager.stop();
   }

   /**
    * Multi-crawler test. Each crawler is running in its own thread and queries
    * the remote file system with file filters
    */
   @Test
   public void remoteMultipleCrawlersFilterTest() {
      if (this._remoteRootURI1 == null || this._remoteRootURI2 == null) {
         VFSCrawlerTest._logger.info("Remote URI is not set.  Skip test.");
         return;
      }
      CrawlerManager manager = new VFSCrawlerManager();
      manager.start();
      VFSCrawler crawler1 = new VFSCrawler(this._remoteRootURI1, 1);
      crawler1.setAuthentication(this._remoteUser1, this._remotePass1);
      crawler1.registerProductHandler(new TestHandler(this._filter2));

      VFSCrawler crawler2 = new VFSCrawler(this._remoteRootURI2, 1);
      crawler2.setAuthentication(this._remoteUser2, this._remotePass2);
      crawler2.registerProductHandler(new TestHandler(this._filter3));

      crawler1.registerProductSelector(this._filter2, null);
      crawler2.registerProductSelector(this._filter3, null);

      manager.replaceCrawler(crawler1);
      manager.replaceCrawler(crawler2);
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         VFSCrawlerTest._logger.error(e.getMessage());
      }
      manager.stop();
   }

   /**
    * Test to query local file directory using scheduled thread
    */
   @Test
   public void managerTest() {
      Crawler crawler = new VFSCrawler(this._localRootURI, 1);
      crawler.registerProductHandler(new TestHandler(null));
      this._managerRun(crawler);
   }

   /**
    * Test to query remote file system using filter.
    */
   @Test
   public void remoteFilterTest() {
      if (this._remoteRootURI1 == null) {
         VFSCrawlerTest._logger.info("Remote URI is not set.  Skip test.");
         return;
      }
      VFSCrawler crawler = new VFSCrawler(this._remoteRootURI1);
      crawler.setAuthentication(this._remoteUser1, this._remotePass1);
      crawler.registerProductSelector(this._filter1, null);
      this._batchRun(crawler);
   }

   /**
    * Test to query remote file system using scheduled thread
    */
   @Test
   public void remoteManagerTest() {
      if (this._remoteRootURI1 == null) {
         VFSCrawlerTest._logger.info("Remote URI is not set.  Skip test.");
         return;
      }
      VFSCrawler crawler = new VFSCrawler(this._remoteRootURI1, 1);
      crawler.setAuthentication(this._remoteUser1, this._remotePass1);
      crawler.registerProductHandler(new TestHandler(null));
      this._managerRun(crawler);
   }

   @Test
   public void cacheTest() {
      if (this._remoteRootURI1 == null) {
         VFSCrawlerTest._logger.info("Remote URI is not set.  Skip test.");
         return;
      }

      if (this._cacheFile == null) {
         VFSCrawlerTest._logger
               .info("Cache file is not specified.  Skip test.");
         return;
      }

      if (this._filter1 == null) {
         VFSCrawlerTest._logger.info("File filter.  Skip test.");
         return;
      }

      try {
         VFSCrawler crawler = new VFSCrawler(this._remoteRootURI1, 1);
         crawler.setAuthentication(this._remoteUser1, this._remotePass1);
         crawler.registerProductSelector(this._filter1, null);

         CrawlerRegistry registry =
               FileCrawlerRegistry.loadCache(this._cacheFile);

         this._filter1.setExcludeList(registry.restore());
         Set<FileProduct> products = crawler.crawl();
         VFSCrawlerTest._logger.info("found " + products.size() + " files.");
         for (FileProduct product : products) {
            VFSCrawlerTest._logger.debug(product);
         }

         registry.save(products);

         this._filter1.setExcludeList(registry.restore());
         products = crawler.crawl();
         VFSCrawlerTest._logger.info("found " + products.size() + " files.");
         for (FileProduct product : products) {
            VFSCrawlerTest._logger.debug(product);
         }

         crawler.stop();
      } catch (IOException e) {
         VFSCrawlerTest._logger.error(e.getMessage());
         if (VFSCrawlerTest._logger.isDebugEnabled())
            e.printStackTrace();
      } catch (CrawlerException e) {
         VFSCrawlerTest._logger.error(e.getMessage());
         if (VFSCrawlerTest._logger.isDebugEnabled())
            e.printStackTrace();
      }

   }

   /**
    * Quick remote test.
    */
   @Test
   public void remoteTest() {
      if (this._remoteRootURI1 == null) {
         VFSCrawlerTest._logger.info("Remote URI is not set.  Skip test.");
      }
      VFSCrawler crawler = new VFSCrawler(this._remoteRootURI1);
      crawler.setAuthentication(this._remoteUser1, this._remotePass1);
      this._batchRun(crawler);
   }

   @Before
   public void setup() {
      if (System.getProperty("common.test.localpath") != null)
         this._localRootURI = System.getProperty("common.test.localpath");
      if (System.getProperty("common.test.remotepath1") != null) {
         this._remoteRootURI1 = System.getProperty("common.test.remotepath1");
         this._remoteUser1 = System.getProperty("common.test.remoteuser1");
         this._remotePass1 = System.getProperty("common.test.remotepass1");
      }

      if (System.getProperty("common.test.remotepath2") != null) {
         this._remoteRootURI2 = System.getProperty("common.test.remotepath2");
         this._remoteUser2 = System.getProperty("common.test.remoteuser2");
         this._remotePass2 = System.getProperty("common.test.remotepass2");
      }

      if (System.getProperty("common.test.cachefile") != null) {
         this._cacheFile = System.getProperty("common.test.cachefile");
      }

      this._filter1 = new FileFilter(new String[] { "\\S*.md5$", "\\S*.xml$" });

      this._filter2 = new FileFilter(new String[] { "\\S*.xml$" });

      this._filter3 = new FileFilter(new String[] { "\\S*.md5$" });
   }

   /**
    * Quick test to query local file directory
    */
   @Test
   public void simpleTest() {
      Crawler crawler = new VFSCrawler(this._localRootURI);
      this._batchRun(crawler);
   }

}
