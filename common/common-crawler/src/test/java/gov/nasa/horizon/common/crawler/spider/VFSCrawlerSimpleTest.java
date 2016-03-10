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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * VFS Crawler tests.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public class VFSCrawlerSimpleTest {
   /**
    * Implement the callback class to handle the list of file returned.
    *
    * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
    * @version $Id: $
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

      public void onError(Throwable e) {
         VFSCrawlerTest._logger.error(e.getMessage(), e);
      }
   }

   public static Log _logger = LogFactory.getLog(VFSCrawlerTest.class);
   private String _remoteRootURI1;
   private String _remoteUser1;
   private String _remotePass1;
   private FileFilter _filter1;
   private String _cacheFile;
   private CrawlerRegistry _registry;

   @After
   public void cleanup() {
      File cacheFile = new File(this._cacheFile);
      if (cacheFile.exists())
         cacheFile.delete();
   }

   protected void _batchRun(Crawler crawler) {
      Set<FileProduct> products = crawler.crawl();
      for (FileProduct product : products) {
         VFSCrawlerSimpleTest._logger.info(product);
      }

      if (this._registry != null) {
         try {
            this._registry.save(products);

            // this._filter1.setExcludeList(this._registry.restore());
         } catch (CrawlerException e) {
            VFSCrawlerSimpleTest._logger.debug(e);
         }
      }

      for (FileProduct product : products) {
         try {
            product.close();
         } catch (IOException e) {
            VFSCrawlerSimpleTest._logger.debug(e);
         }
      }
      // crawler.stop();
   }

   protected void _managerRun(Crawler crawler) {
      CrawlerManager manager = new VFSCrawlerManager();
      manager.start();
      try {
         manager.registerCrawler(crawler);
         Thread.sleep(3000);
      } catch (CrawlerException e) {
         VFSCrawlerSimpleTest._logger.error(e.getMessage());
      } catch (InterruptedException e) {
         VFSCrawlerSimpleTest._logger.error(e.getMessage());
      }
      manager.stop();
   }

   @Test
   public void remoteFilterTest() {
      if (this._remoteRootURI1 == null) {
         VFSCrawlerSimpleTest._logger
               .info("Remote URI is not set.  Skip test.");
         return;
      }
      while (true) {
         VFSCrawler crawler = new VFSCrawler(this._remoteRootURI1);
         crawler.setAuthentication(this._remoteUser1, this._remotePass1);
         try {
            this._registry = FileCrawlerRegistry.loadCache(this._cacheFile);
         } catch (IOException e) {
            VFSCrawlerSimpleTest._logger.debug(e);
         } catch (CrawlerException e) {
            VFSCrawlerSimpleTest._logger.debug(e);
         }
         crawler.registerProductSelector(this._filter1, null);
         this._batchRun(crawler);
         try {
            Thread.sleep(1000);
         } catch (Exception e) {
         }
      }
   }

   @Before
   public void setup() {
      if (System.getProperty("common.test.remotepath1") != null) {
         this._remoteRootURI1 = System.getProperty("common.test.remotepath1");
         this._remoteUser1 = System.getProperty("common.test.remoteuser1");
         this._remotePass1 = System.getProperty("common.test.remotepass1");
      }

      if (System.getProperty("common.test.cachefile") != null) {
         this._cacheFile = System.getProperty("common.test.cachefile");
      }

      // this._filter1 = new FileFilter(new String[] { "\\S*.md5$", "\\S*.xml$"
      // });
      this._filter1 = new FileFilter(new String[]{"\\S*"});
   }

}
