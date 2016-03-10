/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler.spider.registry.provider;

import gov.nasa.horizon.common.api.jaxb.crawlercache.Crawlercache;
import gov.nasa.horizon.common.api.jaxb.crawlercache.Crawlercache.Fileinfo;
import gov.nasa.horizon.common.api.util.Binder;
import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.crawler.spider.CrawlerException;
import gov.nasa.horizon.common.crawler.spider.provider.CacheFileProduct;
import gov.nasa.horizon.common.crawler.spider.registry.CrawlerRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of file registry using JAXB and XML file as the registry
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: FileCrawlerRegistry.java 5682 2010-08-26 17:49:47Z thuang $
 */
public class FileCrawlerRegistry extends Binder<Crawlercache> implements
      CrawlerRegistry {

   private static final String SCHEMA_ENV = "common.config.schema";
   private static final String JAXB_CONTEXT =
         "gov.nasa.horizon.common.api.jaxb.crawlercache";
   private static final String CACHE_SCHEMA = "horizon_crawlercache.xsd";
   private static Log _logger = LogFactory.getLog(FileCrawlerRegistry.class);
   private static URL _schemaUrl;
   private String _cachefilename;

   static {
      if (System.getProperty(FileCrawlerRegistry.SCHEMA_ENV) != null) {
         File schemaFile =
               new File(System.getProperty(FileCrawlerRegistry.SCHEMA_ENV)
                     + File.separator + FileCrawlerRegistry.CACHE_SCHEMA);
         if (schemaFile.exists()) {
            try {
               FileCrawlerRegistry._schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               FileCrawlerRegistry._logger.error(e.getMessage(), e);
            }
         }
      }

      if (FileCrawlerRegistry._schemaUrl == null) {
         FileCrawlerRegistry._schemaUrl =
               FileCrawlerRegistry.class.getResource("/META-INF/schemas/"
                     + FileCrawlerRegistry.CACHE_SCHEMA);
      }
   }

   /**
    * Factory method to create a registry.
    *
    * @param filename the cache file name
    * @return the CrawlerRegistry object
    * @throws IOException      when unable to access the input file
    * @throws CrawlerException when unable to setup the registry
    */
   public static CrawlerRegistry loadCache(String filename) throws IOException,
         CrawlerException {
      FileCrawlerRegistry registry;

      // if the file doesn't exist, just create an empty registry.
      if (!new File(filename).exists()) {
         return new FileCrawlerRegistry(filename);
      }

      try {
         registry = new FileCrawlerRegistry(new FileReader(new File(filename)));
         registry._cachefilename = filename;
      } catch (JAXBException e) {
         throw new CrawlerException(e);
      } catch (SAXException e) {
         throw new CrawlerException(e);
      }
      return registry;
   }

   /**
    * Constructor to create an empty registry using the input cache file name
    *
    * @param filename the cache file name.
    * @throws CrawlerException when setup failed.
    */
   public FileCrawlerRegistry(String filename) throws CrawlerException {
      super(FileCrawlerRegistry.JAXB_CONTEXT, FileCrawlerRegistry._schemaUrl,
            false);
      this._setJaxbObj(new Crawlercache());
      this._cachefilename = filename;
   }

   /**
    * Constructor to setup the file registry from the input Reader
    *
    * @param xmlReader the reader associated to XML cache data
    * @throws JAXBException when XML binding failes
    * @throws SAXException  when failed to validate
    */
   private FileCrawlerRegistry(Reader xmlReader) throws JAXBException,
         SAXException {
      super(FileCrawlerRegistry.JAXB_CONTEXT, FileCrawlerRegistry._schemaUrl,
            xmlReader, false);
   }

   public synchronized Set<FileProduct> restore() throws CrawlerException {
      Set<FileProduct> fileProducts = new HashSet<FileProduct>();

      if (new File(this._cachefilename).exists()) {
         try {
            this._setJaxbObj(Binder.<Crawlercache>_load(this._getXMLReader(),
                  this._getUnmarshaller(), new File(this._cachefilename)));
         } catch (FileNotFoundException e) {
            throw new CrawlerException(e);
         } catch (SAXException e) {
            throw new CrawlerException(e);
         } catch (JAXBException e) {
            throw new CrawlerException(e);
         }

         List<Fileinfo> fileInfos = this._getJaxbObj().getFileinfo();
         if (fileInfos != null) {
            for (Fileinfo fileInfo : fileInfos) {
               if (fileInfo.getChecksum() != null) {
                  fileProducts.add(new CacheFileProduct(fileInfo.getName(),
                        fileInfo.getSize().longValue(), fileInfo.getModified()
                              .longValue(), fileInfo.getChecksum().getAlgorithm(), fileInfo.getChecksum().getValue()));

               } else {
                  fileProducts.add(new CacheFileProduct(fileInfo.getName(),
                        fileInfo.getSize().longValue(), fileInfo.getModified()
                              .longValue()));
               }
            }
         }
      }

      return fileProducts;
   }

   public synchronized void save(Set<FileProduct> fileProducts)
         throws CrawlerException {
      try {
         List<Fileinfo> fileInfos = this._getJaxbObj().getFileinfo();
         if (fileInfos == null) {
            fileInfos = new LinkedList<Fileinfo>();
         } else {
            fileInfos.clear();
         }
         for (FileProduct product : fileProducts) {
            Fileinfo info = new Fileinfo();
            info.setName(product.getFriendlyURI());
            info.setModified(BigInteger.valueOf(product.getLastModifiedTime()));
            info.setSize(BigInteger.valueOf(product.getSize()));
            Fileinfo.Checksum checksum = new Fileinfo.Checksum();
            if (product.getDigestValue() != null) {
               checksum.setAlgorithm(product.getDigestAlgorithm().toString());
               checksum.setValue(product.getDigestValue());
               info.setChecksum(checksum);
            }
            fileInfos.add(info);
         }
         Binder.toFile(this._getJaxbObj(), this._getMarshaller(),
               this._cachefilename);
      } catch (JAXBException e) {
         throw new CrawlerException(e);
      } catch (SAXException e) {
         throw new CrawlerException(e);
      } catch (IOException e) {
         throw new CrawlerException(e);
      }
   }

}
