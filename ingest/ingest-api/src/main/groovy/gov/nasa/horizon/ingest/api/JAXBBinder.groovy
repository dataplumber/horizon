package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.common.api.util.DocumentParser
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import javax.xml.transform.sax.SAXSource
import javax.xml.validation.SchemaFactory
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.XMLReader
import org.xml.sax.helpers.XMLReaderFactory

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Aug 30, 2008
 * Time: 9:30:45 AM
 * To change this template use File | Settings | File Templates.
 */
class JAXBBinder {
   private static Log log = LogFactory.getLog(JAXBBinder.class)

   protected def jaxbObj
   protected JAXBContext context
   protected Unmarshaller unmarshaller
   protected Marshaller marshaller
   protected URL schemaUrl
   protected XMLReader xmlReader

   protected def JAXBBinder() {}

   protected def JAXBBinder(String contextpath) {
      try {
         context = JAXBContext.newInstance(contextpath)
         marshaller = context.createMarshaller()
      } catch (JAXBException e) {
         log.error(e.message, e)
      }
   }

   protected def JAXBBinder(String contextpath, URL schemaUrl, boolean usesax) {
      this(contextpath)
      this.schemaUrl = schemaUrl
      try {
         if (usesax) {
            xmlReader = XMLReaderFactory.createXMLReader()
            xmlReader.setFeature(
                  "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                  false);
            xmlReader.setFeature(
                  "http://apache.org/xml/features/validation/schema", false);
            xmlReader.setFeature(
                  "http://xml.org/sax/features/namespaces", true);
         }

         if (!context) {
            throw new JAXBException("Invalid XML binding context.")
         }

         unmarshaller = context.createUnmarshaller()
         if (!schemaUrl) {
            log.warn("Unable to locate XML schema for message validation.")
         } else {
            log.trace("Validate with schema: ${schemaUrl.toString()}.")
            unmarshaller.setSchema(SchemaFactory.newInstance(
                  "http://www.w3.org/2001/XMLSchema"
            ).newSchema(schemaUrl))
         }
      } catch (JAXBException e) {
         log.error(e.message, e)
      } catch (SAXException e) {
         log.error(e.message, e)
      }
   }

   def load(Reader reader) throws JAXBException, SAXException {
      if (unmarshaller == null) {
         log.error("Internal JAXB and/or Marshaller have been not been initialized.")
         return
      }
      if (xmlReader == null) {
         jaxbObj = unmarshaller.unmarshal(reader)
         return
      }

      SAXSource source = new SAXSource(xmlReader, new InputSource(reader))
      jaxbObj = unmarshaller.unmarshal(source)
   }

   def load(String xml) throws JAXBException, SAXException {
      StringReader reader = new StringReader(xml)
      load(reader)
      reader.close()
   }

   void toFile(String filename) throws IOException {
      if (jaxbObj == null || marshaller == null) {
         throw new IOException("Internal JAXB and/or Marshaller have been not been initialized.")
      }

      try {
         StringWriter writer = new StringWriter()
         marshaller.marshal(jaxbObj, writer)

         DocumentParser.getParser().documentToFile(
               DocumentParser.getParser().parseString(writer.toString()),
               filename)
         writer.close()
      } catch (JAXBException e) {
         log.error(e.message, e)
         throw new IOException(e.message)
      } catch (SAXException e) {
         log.error(e.message, e)
         throw new IOException(e.message)
      }
   }

   String toString() {
      String result = null;
      if (jaxbObj == null || marshaller == null) {
         log.error("Internal JAXB and/or Marshaller have been not been initialized.")
         return result
      }

      StringWriter writer = new StringWriter()
      marshaller.marshal(jaxbObj, writer)

      result =
         DocumentParser.getParser().documentToString(
               DocumentParser.getParser().parseString(writer.toString()))
      writer.close()

      return result
   }

}