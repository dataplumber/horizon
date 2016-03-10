package gov.nasa.horizon.ingest.server

import gov.nasa.horizon.common.crawler.spider.CrawlerManager
import gov.nasa.horizon.common.crawler.spider.Crawler
import gov.nasa.horizon.common.api.file.FileFilter
import gov.nasa.horizon.common.crawler.spider.provider.VFSCrawler
import gov.nasa.horizon.common.crawler.spider.provider.VFSCrawlerManager
import gov.nasa.horizon.common.api.file.FileProduct

import gov.nasa.horizon.common.api.util.URIPath
import gov.nasa.horizon.common.api.util.ChecksumUtility
import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import gov.nasa.horizon.common.api.file.FileProductHandler
import gov.nasa.horizon.common.crawler.spider.registry.CrawlerRegistry
import gov.nasa.horizon.common.crawler.spider.registry.provider.FileCrawlerRegistry

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Sep 2, 2008
 * Time: 11:38:45 AM
 * To change this template use File | Settings | File Templates.
 */
class FileGrabberTest extends GroovyTestCase {
   static Log log = LogFactory.getLog(FileGrabberTest.class)

   private FileFilter filter
   CrawlerRegistry registry

   def grabberMap = [
         onProducts: {
            filter.excludeList = it
            it.each {file ->
               log.debug(file.name)
               String outfile = "/tmp/${file.name}"
               new FileOutputStream(new File(outfile))  << file.inputStream
               file.close()
               String checksum = ChecksumUtility.getDigest(DigestAlgorithm.MD5, new File(outfile))
               log.debug("checksum: ${checksum}.")
            }
         }
   ]

      def echoMap = [
         onProducts: {
            it.each {file ->
               file.close()
            }
            //registry.save(it)
            //filter.excludeList = registry.restore()

            log.debug ("Found ${it.size()} files.")
            /*
            it.each {file ->
               log.debug(file.name)
               file.close()
            }
            */
         }
   ]

   def interfaces = [gov.nasa.horizon.common.api.file.FileProductHandler]
   def listener = new ProxyGenerator().instantiateAggregate(grabberMap, interfaces)
   def echoListener = new ProxyGenerator().instantiateAggregate(echoMap, interfaces)


  /*
   void testGrabFiles() {

      CrawlerManager manager = new VFSCrawlerManager()
      manager.start()

      String rootPath = "sftp://lapinta.jpl.nasa.gov/data/dev/ingest-test/ghrsst_test_data/"
      //String rootPath = "file:///Users/ingest/Development/data/dev/ingest-test/ghrsst_test_data/"

      [
            "${rootPath}/20070828-GOES11-OSDPD-L2P-GOES11_South_2100Z-v01.nc.gz",
            "${rootPath}/20070828-GOES11-OSDPD-L2P-GOES11_South_2100Z-v01.nc.gz.md5",
            "${rootPath}/FR-20070828-GOES11-OSDPD-L2P-GOES11_South_2100Z-v01.xml",
            "${rootPath}/FR-20070828-GOES11-OSDPD-L2P-GOES11_South_2100Z-v01.xml.md5"].each {
         manager.registerCrawler(createCrawler(it, "horizondev", null, 1))
      }

      manager.stop()
   }
   */


   void testCrawl () {
      CrawlerManager manager = new VFSCrawlerManager()
      manager.start()
      String rootPath = "ftp://lapinta.jpl.nasa.gov//store/ghrsst/public/data/L2P/MODIS_A/JPL/2008/188"

      registry =
               FileCrawlerRegistry.loadCache("/tmp/crawler.cache");

      10.times {
         Crawler crawler = new VFSCrawler(rootPath)
         crawler.setAuthentication('horizondev', 'perfect$storm')
         filter = new gov.nasa.horizon.common.api.file.FileFilter((String[]) ['\\S*.md5$', '\\S*.xml$', '\\S*.bz2$'])
         crawler.registerProductSelector(filter)
         crawler.registerProductHandler(echoListener)
         manager.replaceCrawler(crawler)
         Thread.currentThread().sleep(1000)
      }

     //Thread.currentThread().sleep(60000)

      log.debug "stoping manager"
      manager.stop()
      log.debug "manager stopped"


      Crawler crawler = new VFSCrawler(rootPath)
         crawler.setAuthentication('horizondev', 'perfect$storm')
         filter = new gov.nasa.horizon.common.api.file.FileFilter((String[]) ['\\S*.md5$', '\\S*.xml$', '\\S*.bz2$'])
         crawler.registerProductSelector(filter)
      def files = crawler.crawl()
      log.debug ("returned ${files.size()} files.")
      crawler.stop()

   }

   Crawler createCrawler(String url, String username, String password, int maxCon) {
      URIPath path = URIPath.createURIPath(url)

      Crawler crawler = new VFSCrawler(path.hostPath, 0)
      crawler.setAuthentication(username, password)
      filter = new gov.nasa.horizon.common.api.file.FileFilter((String[]) [
            path.filename
      ])
      crawler.registerProductSelector(filter)
      crawler.registerProductHandler(listener)
      return crawler
   }
}
