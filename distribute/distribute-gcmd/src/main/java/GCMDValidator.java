import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Class to validate a XML file against with GCMD's validation service. 
 * @author Dawn Finney
 **/
public class GCMDValidator{
	/**
	 * Method to send the generated files to the WEB DIF VALIDATOR.
	 * @param mainReport the path to the main report
	 **/
	public static void validate(Global.Report mainReport, LinkedList<Global.Report>individualReports){
		for(Global.Report r : individualReports){
			mainReport.addSentenceToBody(validate(r));
		}
	}
	
	/**
	 * Method to send a generated file to the WEB DIF VALIDATOR.
	 * @param r a Report to validate
	 **/
	public static String validate(Global.Report r){
		String path = r.getPath();
		try{
			InputStream iostream = ClientHttpRequest.post(new URL (Global.DIF_VALIDATOR_WEB_ADDRESS), new Object[] {
				"doctype", "dif",
				"action", "validate",
				"uploaded_file", new File(path)
			});

			Scanner scan = new Scanner(new InputStreamReader(iostream)).useDelimiter("\n");
			String report = "";
			while(scan.hasNext())
				report = report + scan.next() + "\n";
			r.addSentenceToError(report);
			scan.close();
			iostream.close();
		}
		catch(IOException io){
			r.addSentenceToError("Could not validate...\n" +
					"(probably not connected to the internet)");
			return r.toString();
		}
		return r.toString();
	}
}
