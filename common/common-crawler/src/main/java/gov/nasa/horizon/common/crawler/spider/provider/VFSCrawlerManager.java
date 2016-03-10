/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler.spider.provider;

import gov.nasa.horizon.common.crawler.spider.Crawler;
import gov.nasa.horizon.common.crawler.spider.CrawlerException;
import gov.nasa.horizon.common.crawler.spider.CrawlerManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.cache.DefaultFilesCache;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

/**
 * Implements the CrawlerManager interface using the Apache Virtual File System framework
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public class VFSCrawlerManager implements CrawlerManager {
   private static Log _logger = LogFactory.getLog(VFSCrawlerManager.class);

   public static final String PROVIDER_ENV = "common.crawler.providers";
   public static final String PROVIDER_FILE = "common-providers.xml";
   private static URL _providerURL = null;

   // locate the provider config info to setup the VFS manager.
   static {
      if (System.getProperty(VFSCrawlerManager.PROVIDER_ENV) != null) {
         File providerFile =
               new File(System.getProperty(VFSCrawlerManager.PROVIDER_ENV)
                     + File.separator + VFSCrawlerManager.PROVIDER_FILE);
         if (providerFile.exists()) {
            try {
               VFSCrawlerManager._providerURL = providerFile.toURL();
            } catch (MalformedURLException e) {
               VFSCrawlerManager._logger.error(e.getMessage(), e);
            }
         }
      }
      if (VFSCrawlerManager._providerURL == null) {
         VFSCrawlerManager._providerURL =
               VFSCrawlerManager.class.getResource("/META-INF/"
                     + VFSCrawlerManager.PROVIDER_FILE);
      }
   }

   private Hashtable<String, Crawler> _crawlers =
         new Hashtable<String, Crawler>();
   private StandardFileSystemManager _manager = null;
   private boolean _inited = false;

   private boolean _started = false;

   public VFSCrawlerManager() {
      this.init();
   }

   public String getName() {
      // TODO Auto-generated method stub
      return null;
   }

   public synchronized void init() {
      if (this._inited)
         return;
      try {
         this._manager = new StandardFileSystemManager();
         this._manager.setFilesCache(new DefaultFilesCache());
         if (VFSCrawlerManager._providerURL != null) {
            VFSCrawlerManager._logger.trace("Load provider info from: "
                  + VFSCrawlerManager._providerURL.toString());
            this._manager.setConfiguration(VFSCrawlerManager._providerURL);
         }
         this._manager.init();
         this._inited = true;
      } catch (FileSystemException e) {
         VFSCrawlerManager._logger.error(e.getMessage(), e);
      }
   }

   public synchronized void registerCrawler(Crawler crawler)
         throws CrawlerException {
      if (this._crawlers.contains(crawler.getRootURI()))
         throw new CrawlerException("A crawler entry for "
               + crawler.getRootURI() + " already exist.");

      this.replaceCrawler(crawler);
   }

   public synchronized void replaceCrawler(Crawler crawler) {
      Crawler oldCrawler = this._crawlers.remove(crawler.getRootURI());
      if (oldCrawler != null) {
         if (this._started)
            oldCrawler.stop();
      }
      this._manager.freeUnusedResources();

      // If this is a VFS crawler, then it should keep a reference
      // to its manager.
      /*
      if (crawler instanceof VFSCrawler) {
         VFSCrawler vcrawler = (VFSCrawler) crawler;
         vcrawler.setFileSystemManager(this._manager);
      }
      */
      this._crawlers.put(crawler.getRootURI(), crawler);
      if (this._started) {
         crawler.start();
      }
   }

   public void setName(String name) {
      // TODO Auto-generated method stub

   }

   public synchronized void start() {
      for (Crawler crawler : this._crawlers.values()) {
         crawler.start();
      }
      this._started = true;
   }

   public synchronized void stop() {
      if (this._started) {
         for (Crawler crawler : this._crawlers.values()) {
            crawler.stop();
         }
      }
      if (this._manager != null) {
         this._manager.freeUnusedResources();
         this._manager.close();
         this._manager = null;
      }
   }

   public synchronized void unregisterCrawler(Crawler crawler) {
      if (this._crawlers.contains(crawler.getRootURI())) {
         this._crawlers.remove(crawler.getRootURI());
      }
   }

   public synchronized Crawler unregisterCrawler(String uri) {
      return this._crawlers.remove(uri);
   }

}
