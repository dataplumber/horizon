/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Utility DOM parser class that offers simple method for setup the validation
 * schema, creation of Document object, and utility methods for exporting
 * Document object to String or data stream.
 * 
 * @author T. Huang
 * @version $Id: DocumentParser.java 244 2007-10-02 20:12:47Z axt $
 */
public class DocumentParser {
	static Log _logger = LogFactory.getLog(DocumentParser.class);

	private DocumentBuilderFactory _factory =
			DocumentBuilderFactory.newInstance();

	private SchemaFactory _schemaFactory =
			SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	private Validator _validator = null;

	protected DocumentParser() {

	}

	public static DocumentParser getParser() {
		DocumentParser parser = new DocumentParser();
		parser._factory.setNamespaceAware(true);
		return parser;
	}

	public static DocumentParser getParser(InputStream isSchema) {
		DocumentParser parser = new DocumentParser();
		parser._factory.setNamespaceAware(true);
		try {
			Source schemaSource = new StreamSource(isSchema);
			Schema schema = parser._schemaFactory.newSchema(schemaSource);
			parser._factory.setSchema(schema);
			parser._validator = schema.newValidator();
			parser._validator.setErrorHandler(parser.new DocErrorHandler());
		} catch (SAXException e) {
			DocumentParser._logger.warn(e);
		}
		return parser;
	}

	public static DocumentParser getParser(URL schemaURL) {
		try {
			return DocumentParser.getParser(schemaURL.openStream());
		} catch (IOException e) {
			DocumentParser._logger.warn("Unable to open input schema URL: "
					+ schemaURL.toString());
			DocumentParser parser = new DocumentParser();
			parser._factory.setNamespaceAware(true);
			return parser;
		}
	}

	public Document parseString(String xml) throws SAXException {
		if (xml == null)
			throw new SAXException("Unable to parse null input string");

		Document doc = null;
		try {
			doc = this.parseStream(new ByteArrayInputStream(xml.getBytes()));
		} catch (IOException e) {
			throw new SAXException(e);
		}
		return doc;
	}

	public Document parseDoc(String xmldoc) throws SAXException, IOException {
		if (xmldoc == null)
			throw new IOException("Unable to parse null document");

		if (this._validator != null) {
			FileInputStream is = new FileInputStream(xmldoc);
			this._validator.validate(new StreamSource(is));
			is.close();
		}

		FileInputStream is = new FileInputStream(xmldoc);
		Document doc = null;
		try {
			DocumentBuilder docBuilder = this._factory.newDocumentBuilder();
			doc = docBuilder.parse(is);
		} catch (ParserConfigurationException e) {
			throw new SAXException("Internal XML parser improperly configured: "
					+ e.getMessage());
		} finally {
			is.close();
		}
		return doc;
	}

	public Document parseStream(InputStream is) throws SAXException, IOException {
		if (is == null)
			throw new IOException("Unable to parse null InputStream");

		if (this._validator != null) {
			this._validator.validate(new StreamSource(is));
			is.reset();
		}

		Document doc = null;
		try {
			DocumentBuilder docBuilder = this._factory.newDocumentBuilder();
			doc = docBuilder.parse(is);
		} catch (ParserConfigurationException e) {
			throw new SAXException("Internal XML parser improperly configured: "
					+ e.getMessage());
		}
		return doc;
	}

	public String documentToString(Document document) throws SAXException {
		StringWriter writer = new StringWriter();
		DOMSource source = new DOMSource(document);

		try {
			if (this._validator != null) {
				try {
					this._validator.validate(source);
				} catch (IOException e) {
					if (DocumentParser._logger.isDebugEnabled())
						e.printStackTrace();
					throw new SAXException(e.getMessage());
				}
			}

			StreamResult streamResult = new StreamResult(writer);

			Transformer transformer = TransformerUtility.getTransformer();
			transformer.transform(source, streamResult);
		} catch (TransformerException e) {
			if (DocumentParser._logger.isDebugEnabled())
				e.printStackTrace();
			throw new SAXException(e.getMessage());
		}
		return writer.toString();
	}

	public void documentToFile(Document document, String file)
			throws SAXException, IOException {
		DOMReader domReader = new DOMReader();
		org.dom4j.Document jdomDoc = domReader.read(document);

		FileWriter writer = new FileWriter(new File(file));
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setEncoding("UTF-8");

		XMLWriter xmlWriter = new XMLWriter(writer, outformat);
		this._validate(document);
		try {
			xmlWriter.write(jdomDoc);
			xmlWriter.flush();
		} finally {
			writer.close();
		}
	}

	public void documentToFile(String content, String file) throws SAXException,
			IOException {
		DocumentBuilder documentBuilder = DocumentUtility.getDocumentBuilder();
		Document document = null;

		StringReader stringReader = new StringReader(content);
		try {
			document = documentBuilder.parse(new InputSource(stringReader));
		} finally {
			stringReader.close();
		}

		this.documentToFile(document, file);
	}

	protected void _validate(Document document) throws SAXException {
		if (this._validator != null) {
			DOMSource source = new DOMSource(document);

			try {
				this._validator.validate(source);
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
	}

	class DocErrorHandler implements ErrorHandler {
		private boolean _valid = true;

		public void warning(SAXParseException exception) throws SAXException {
			throw new SAXException("Warning: " + exception.getMessage()
					+ " at line " + exception.getLineNumber() + ", column "
					+ exception.getColumnNumber() + " in entity "
					+ exception.getSystemId());
		}

		/**
		 * This method is called when the XML parser encounters an error
		 * condition. The document does not validate successfully.
		 * 
		 * @throws SAXException
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		public void error(SAXParseException exception) throws SAXException {
			this._valid = false;

			throw new SAXException("Error: " + exception.getMessage()
					+ " at line " + exception.getLineNumber() + ", column "
					+ exception.getColumnNumber() + " in entity "
					+ exception.getSystemId());
		}

		/**
		 * This method is called when the XML parser encounters a fatal condition.
		 * The document does not validate successfully.
		 * 
		 * @throws SAXException
		 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException exception) throws SAXException {
			this._valid = false;

			throw new SAXException("Error: " + exception.getMessage()
					+ " at line " + exception.getLineNumber() + ", column "
					+ exception.getColumnNumber() + " in entity "
					+ exception.getSystemId());
		}

		/**
		 * Utility method to return the validation flag.
		 * 
		 * @return true if validation was successful.
		 */
		public boolean isValid() {
			return this._valid;
		}

	}

}
