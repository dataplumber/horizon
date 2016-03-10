/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.jaxb.domain.Domain as JaxbDomain

import javax.xml.bind.JAXBException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.xml.sax.SAXException

/**
 * A domain is the access interface to the horizon data service.  It is 
 * defined as an XML file the contains the name and access URL of all
 * horizon services.
 *
 * @author T. Huang
 * @version $Id: $
 */
class Domain extends JAXBBinder {
   private static Log log = LogFactory.getLog(Domain.class)

   static final String SCHEMA_ENV = "horizon.domain.schema"
   static final String DOMAIN_SCHEMA = "horizon_domain.xsd"
   static final String JAXB_CONTEXT =
   "gov.nasa.horizon.ingest.api.jaxb.domain"
   static URL schemaUrl

   static {
      if (System.getProperty(Domain.SCHEMA_ENV) != null) {
         File schemaFile =
         new File(System.getProperty(Domain.SCHEMA_ENV)
               + File.separator + Domain.DOMAIN_SCHEMA);
         if (schemaFile.exists()) {
            try {
               Domain.schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               log.debug(e.message, e)
            }
         }
      }

      if (Domain.schemaUrl == null) {
         Domain.schemaUrl =
            Domain.class.getResource("/META-INF/schemas/"
                  + Domain.DOMAIN_SCHEMA)
      }

      log.trace ("Domain schema: ${Domain.schemaUrl.toString()}.")
   }


   private Map fedMap = [:]
   private Map productTypeMap = [:]

   def Domain() {
      super(Domain.JAXB_CONTEXT, Domain.schemaUrl, true)
      try {
         jaxbObj = new JaxbDomain()
      } catch (JAXBException e) {
         log.error(e.message, e)
      }
   }

   def load(String xml) throws JAXBException, SAXException {
      super.load(xml)

      log.trace ("[LOAD]: ${xml}.")
      jaxbObj.federation.each {federation ->
         log.trace ("${federation.name}:${federation.url}")
         fedMap.put(federation.name, federation.url)
         federation.productType.each { productType ->
            productTypeMap.put(productType, federation.name)
         }
      }
   }

   String getDefault() {
      return jaxbObj.default
   }

   String getUrl(String federation) {
      return (String)fedMap[federation]
   }
   
   String getSigEvent() {
      return jaxbObj.sigevent
   }
   
   String getFederation(String productType) {
      return (String)productTypeMap[productType]
   }
   
   String getJobKeeper() {
      return jaxbObj.jobkeeper.server
   }
   
   String getJobKeeperWebService() {
      return jaxbObj.jobkeeper.webservice
   }
   
   String getDiscovery() {
      return jaxbObj.discovery
   }
   
   String getInventory() {
      return jaxbObj.inventory
   }
   
   String getSecurity() {
      return jaxbObj.security
   }

}
