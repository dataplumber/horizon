package gov.nasa.horizon.common.api.util;

import gov.nasa.horizon.common.api.jaxb.hostmap.Hostmap.Host;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class Hostmap extends
   Binder<gov.nasa.horizon.common.api.jaxb.hostmap.Hostmap> {

   public class HostInfo {
      private Host _host;

      HostInfo(Host host) {
         this._host = host;
      }

      public String getAuthentication() {
         return this._host.getAuthentication();
      }

      public int getConnections() {
         return this._host.getConnections();
      }

      public String getName() {
         return this._host.getName();
      }

      public String getProtocol() {
         return this._host.getProtocol();
      }

   }

   private static final String SCHEMA_ENV = "common.config.schema";
   private static final String JAXB_CONTEXT =
      "gov.nasa.horizon.common.api.jaxb.hostmap";

   private static final String HOSTMAP_SCHEMA = "podaac_hostmap.xsd";

   private static Log _logger = LogFactory.getLog(Hostmap.class);

   private static URL _schemaUrl;

   static {
      if (System.getProperty(Hostmap.SCHEMA_ENV) != null) {
         File schemaFile =
            new File(System.getProperty(Hostmap.SCHEMA_ENV) + File.separator
               + Hostmap.HOSTMAP_SCHEMA);
         if (schemaFile.exists()) {
            try {
               Hostmap._schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               if (Hostmap._logger.isDebugEnabled())
                  e.printStackTrace();
            }
         }
      }

      if (Hostmap._schemaUrl == null) {
         Hostmap._schemaUrl =
            Hostmap.class.getResource("/META-INF/schemas/"
               + Hostmap.HOSTMAP_SCHEMA);
      }
   }

   public static Hostmap loadHostmap(String filename) throws IOException,
      SAXException {
      Hostmap hostmap = null;

      try {
         hostmap = new Hostmap(new FileReader(new File(filename)));
      } catch (JAXBException e) {
         throw new SAXException(e.getMessage());
      }
      return hostmap;
   }

   private Hostmap(Reader xmlReader) throws JAXBException, SAXException {
      super(Hostmap.JAXB_CONTEXT, Hostmap._schemaUrl, xmlReader, false);
   }

   public List<HostInfo> getHostInfo() {

      List<HostInfo> result = new LinkedList<HostInfo>();
      List<Host> hosts = this._getJaxbObj().getHost();
      for (Host host : hosts) {
         result.add(new HostInfo(host));
      }

      return result;
   }

}
