package gov.nasa.horizon.ingest.api

import javax.xml.bind.JAXBException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.xml.sax.SAXException

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Sep 11, 2008
 * Time: 9:47:40 PM
 * To change this template use File | Settings | File Templates.
 */

class Keychain extends JAXBBinder {
   private static Log log = LogFactory.getLog(Keychain.class)

   static final String SCHEMA_ENV = "horizon.keychain.schema"
   static final String KEYCHAIN_SCHEMA = "horizon_keychain.xsd"
   static final String JAXB_CONTEXT =
   "gov.nasa.horizon.ingest.api.jaxb.keychain"
   static URL schemaUrl

   static {
      if (System.getProperty(Keychain.SCHEMA_ENV) != null) {
         File schemaFile =
         new File(System.getProperty(Keychain.SCHEMA_ENV)
               + File.separator + Keychain.KEYCHAIN_SCHEMA);
         if (schemaFile.exists()) {
            try {
               Keychain.schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               log.debug(e.message, e)
            }
         }
      }

      if (Keychain.schemaUrl == null) {
         Keychain.schemaUrl =
            Keychain.class.getResource("/META-INF/schemas/"
                  + Keychain.KEYCHAIN_SCHEMA)
      }

      log.trace("Keychain schema: ${Domain.schemaUrl.toString()}.")
   }


   private Map fedMap = [:]

   def Keychain() {
      super(Keychain.JAXB_CONTEXT, Keychain.schemaUrl, true)
      try {
         jaxbObj = new gov.nasa.horizon.ingest.api.jaxb.keychain.Keychain()
      } catch (JAXBException e) {
         log.error(e.message, e)
      }
   }

   synchronized def load(String xml) throws JAXBException, SAXException {
      super.load(xml)

      log.trace("[LOAD]: ${xml}.")
      jaxbObj.key.each {key ->
         log.trace("${key.federation}:${key.username}:${key.password}")
         fedMap.put(key.federation, [username: key.username, password: key.password])
      }
   }

   synchronized String getFederationUsername(String federation) {
      def entry = fedMap[federation]
      if (entry) return (String)entry['username']
      return null
   }

   synchronized String getFederationPassword(String federation) {
      def entry = fedMap[federation]
      if (entry) return (String)entry['password']
      return null
   }

   synchronized Map getKey(String federation) {
      return fedMap[federation]
   }

   synchronized void addKey(String federation, String username, String password) {
      fedMap.put(federation, [username: username, password: password])
   }

   synchronized void deleteKey(String federation) {
      fedMap.remove(federation)
   }

   synchronized void toFile(String filename) throws IOException {
      jaxbObj.key.clear()
      fedMap.each {key, value ->
         jaxbObj.key << new gov.nasa.horizon.ingest.api.jaxb.keychain.Keychain.Key(federation: key, username: value.username, password: value.password)
      }
      super.toFile(filename)
   }

}
