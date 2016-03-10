package gov.nasa.horizon.common.api.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Binder<T> {

   private static Log _logger = LogFactory.getLog(Binder.class);

   protected static <T> T _load(XMLReader xmlReader, Unmarshaller unmarshaller,
         File xmlfile) throws JAXBException, SAXException,
         FileNotFoundException {
      return Binder.<T> _load(xmlReader, unmarshaller, new FileReader(xmlfile));
   }

   @SuppressWarnings( { "unchecked" })
   protected static <T> T _load(XMLReader xmlReader, Unmarshaller unmarshaller,
         Reader reader) throws JAXBException, SAXException {
      if (unmarshaller == null)
         throw new JAXBException("Invalid XML unmarshaller.");
      if (xmlReader == null)
         return (T) unmarshaller.unmarshal(reader);

      SAXSource source = new SAXSource(xmlReader, new InputSource(reader));
      T result = (T) unmarshaller.unmarshal(source);
      return result;
   }

   protected static <T> T load(XMLReader xmlReader, Unmarshaller unmarshaller,
         String xml) throws JAXBException, SAXException, FileNotFoundException {
      return Binder.<T> _load(xmlReader, unmarshaller, new StringReader(xml));
   }

   private T _jaxbObj;
   private JAXBContext _context;
   private Unmarshaller _unmarshaller;
   private Marshaller _marshaller;
   private URL _schemaUrl;
   private XMLReader _xmlReader = null;

   protected Binder(String contextpath) {
      try {
         this._context = JAXBContext.newInstance(contextpath);
         this._marshaller = this._context.createMarshaller();
      } catch (JAXBException e) {
         Binder._logger
               .error("Unable to resolve context: " + contextpath + ".");
         Binder._logger.debug(e);
      }
   }

   protected Binder(String contextpath, URL schemaUrl, boolean usesax) {
      this(contextpath);
      try {
         this._schemaUrl = schemaUrl;

         if (usesax) {
            this._xmlReader = XMLReaderFactory.createXMLReader();

            this._xmlReader
                  .setFeature(
                        "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                        false);
            this._xmlReader.setFeature(
                  "http://apache.org/xml/features/validation/schema", false);
            this._xmlReader.setFeature(
                  "http://xml.org/sax/features/namespaces", true);
         }

         if (this._context == null)
            throw new JAXBException("Invalid XML binding context.");

         this._unmarshaller = this._context.createUnmarshaller();

         if (schemaUrl == null) {
            Binder._logger
                  .warn("Unable to locate XML schema for message validation.");
         } else {
            Binder._logger.trace("validate with schema: "
                  + schemaUrl.toString());
            this._unmarshaller.setSchema(SchemaFactory.newInstance(
                  "http://www.w3.org/2001/XMLSchema").newSchema(schemaUrl));
         }
      } catch (JAXBException e) {
         Binder._logger.error(e.getMessage());
         Binder._logger.debug(e);
      } catch (SAXException e) {
         Binder._logger.error(e.getMessage());
         Binder._logger.debug(e);
      }
   }

   protected Binder(String contextpath, URL schemaUrl, Reader reader,
         boolean usesax) throws JAXBException, SAXException {
      this(contextpath, schemaUrl, usesax);
      //try {
      this._jaxbObj =
            Binder.<T> _load(this._xmlReader, this._unmarshaller, reader);
      //} catch (JAXBException e) {
      //   Binder._logger.error(e.getMessage(), e);
      //} catch (SAXException e) {
      //   Binder._logger.error(e.getMessage(), e);
      //}
   }

   protected T _getJaxbObj() {
      return this._jaxbObj;
   }

   protected Marshaller _getMarshaller() {
      return this._marshaller;
   }

   protected URL _getSchemaURL() {
      return this._schemaUrl;
   }

   protected Unmarshaller _getUnmarshaller() {
      return this._unmarshaller;
   }

   protected XMLReader _getXMLReader() {
      return this._xmlReader;
   }

   protected void _setJaxbObj(T jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   @SuppressWarnings( { "unchecked" })
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Binder<T> other = (Binder<T>) obj;
      if (_context == null) {
         if (other._context != null)
            return false;
      } else if (!_context.equals(other._context))
         return false;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      if (_schemaUrl == null) {
         if (other._schemaUrl != null)
            return false;
      } else if (!_schemaUrl.equals(other._schemaUrl))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_context == null) ? 0 : _context.hashCode());
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      result =
            prime * result + ((_schemaUrl == null) ? 0 : _schemaUrl.hashCode());
      return result;
   }

   public static <T> void toFile(T jaxbObj, Marshaller marshaller,
         String filename) throws JAXBException, SAXException, IOException {
      if (jaxbObj == null || marshaller == null)
         throw new JAXBException("Invalid input to marshaller.");

      StringWriter writer = new StringWriter();
      marshaller.marshal(jaxbObj, writer);

      DocumentParser.getParser()
            .documentToFile(
                  DocumentParser.getParser().parseString(writer.toString()),
                  filename);
   }

   public static <T> String toString(T jaxbObj, Marshaller marshaller)
         throws JAXBException, SAXException {
      String result = null;
      if (jaxbObj == null || marshaller == null)
         throw new JAXBException("Invalid input to marshaller.");

      StringWriter writer = new StringWriter();
      marshaller.marshal(jaxbObj, writer);

      result =
            DocumentParser.getParser().documentToString(
                  DocumentParser.getParser().parseString(writer.toString()));
      return result;
   }
}
