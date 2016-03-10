import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import javax.xml.transform.dom.*;
import javax.xml.validation.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Class to validate a XML file against a given schema and if invalid generate an error report.
 * Edited from public source code written by Elliotte Rusty Harold. Origin source can be found 
 * at {http://www.ibm.com/developerworks/xml/library/x-javaxmlvalidapi.html}.
 * @author Dawn Finney
 **/
public class XMLValidator{
	public static void validate(Global.Report mainReport, LinkedList<Global.Report>individualReports, 
			String schemaURL){
		
		for(Global.Report r : individualReports){
			try{
				r.addSentenceToError(validate(r.getPath(), schemaURL));
				mainReport.addSentenceToBody(r.toString());
			}
			catch (SAXException saxex){r.addSentenceToError(saxex.getMessage());}
			catch (IOException io){r.addSentenceToError(io.getMessage());}
			catch (ParserConfigurationException pce){r.addSentenceToError(pce.getMessage());}
			catch (URISyntaxException uri){r.addSentenceToError(uri.getMessage());}
		}
	}
	
	public static String validate(String xmlPath, String schemaURL) 
	throws SAXException, IOException, ParserConfigurationException, URISyntaxException {

		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		URL schemaLocation = new URL(schemaURL);
		Schema schema = factory.newSchema(schemaLocation);
		Validator validator = schema.newValidator();

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		try{
			Document doc = builder.parse(new File(xmlPath));
			validator.validate(new DOMSource(doc), new DOMResult());
		}
		catch (SAXException ex){
			return ex.getMessage();
		}
		return "Valid";
	}	
}
