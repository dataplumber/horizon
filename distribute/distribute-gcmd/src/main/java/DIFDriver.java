import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Class that actually drives the program and will handle 
 * the saving of the files as well as obtaining the file 
 * path for the excel workbook and the web address of the 
 * DIF schema.
 * MasterExtractor will handle all of the data extraction
 * @author Dawn Finney
 **/
public class DIFDriver{
	/**The help message displayed to the user**/
	private static String help = "Version 1.0.7 \tLast update August 11, 2008"
		+ "\n\nThe program can be invoked at the command line using this format: \n\n"
		+ "\tjava -jar <program_name.jar> <file>\n\n"
	    + "where <program_name.jar> is the jar file that contains the program and <file> " +
	    		"\nis the actual file that going to be processed. This file can be either " +
	    		"\nan .xls or .txt file.  More than one file can be specified by delimiting " +
	    		"\nadditional files with spaces such as:\n\n"
		+ "\tjava -jar <program_name.jar> <file> <file2> <file3>\n\n"
		+ "An actual example would be:\n\n"
		+ "\tjava -jar gcmd_dif_generator.jar workbook6.xls workbook5.xls\n\n"
		+ "For advanced users, the additional instructions provided through the text " +
				"\nfile can also be specified through environment variables. The " +
				"\nfollowing are the different environment variables:\n\n"
		+ "\toutput - change the output directory\n"
		+ "\tflag - toggle whether to include or exclude numbers included in xlist\n"
		+ "\txlist - a list of product numbers separated by commas\n"
		+ "\tgcmd - toggle gcmd validation\n"
		+ "\txml - toggle xml validation\n" 
		+ "\tmainsave - toggle whether or not to save a main report\n\n"
		+ "An example of how to use these special specifications is:\n\n"
		+ "\tjava -Doutput=<output> -Dgcmd=<gcmd>  -jar <program_name.jar> <file>\n\n"
		+ "where <output> would be a directory and because gcmd is a toggle environment " +
				"\nvariable, <gcmd> would be a value either true or false (or t or f). It " +
				"\nis important to note that text file specifications hold precedence over " +
				"\nenvironment variable specifications."
		+ "An actual example for the windows would be:\n\n"
		+ "\tjava -Dgcmd=f -Dxml=t -jar gcmd_dif_generator.jar workbook6.xls\n\n" +
				"The validation method would only be XML validation based on this configuration.";
	
	/** Variable used in the auto-generation of filenames 
	 *  if actual one is not provided. **/
	private static int autoGenFilenameCount = -1;
	
	/** Variable representing whether the incoming product 
	 *  numbers are to be included or excluded**/
	private char flag = 'd';
	
	/** Variable representing whether or not to try the 
	 *  operation again**/
	private boolean tryAgain = true;
	
	/** List of product numbers to include or exclude **/
	private LinkedList<String> inexcludeList;
	
	/** List of reports for each product**/
	private LinkedList<Global.Report> individualReports = 
		new LinkedList<Global.Report>();
	
	/** Path to the inputed file. Can actually 
	 *  be of any extension**/
	private String xlsPath;
	
	/** Output directory of the generated files. Defaults 
	 *  to blank. Input a custom text file to change this 
	 *  option. **/
	private String outputDirectory = "";
	
	/** Variable representing whether or not to 
	 *  use the GCMD validation**/
	private boolean useGCMDValidator = true; 
	
	/** Variable representing whether or not to 
	 *  use the XML validation**/
	private boolean useXMLValidator = false;

	/**Variable representing whether or not to not main the report**/
	private boolean suppressMainReportSave = false;
	
	/**Default constructor**/
	public DIFDriver(){
		String output = System.getProperty("output");
		if(output != null)
			outputDirectory = output;
		
		String gcmd = System.getProperty("gcmd");
		if(gcmd != null){
			if(Converter.isNegativeResponse(gcmd))
				useGCMDValidator = false;
		}
		
		String xml = System.getProperty("xml");
		if(xml != null){
			if(Converter.isPositiveResponse(xml))
				useXMLValidator = true;
		}
		
		String flag = System.getProperty("flag");
		if(flag != null){
			if(flag.length() == 1)
				this.flag = flag.toCharArray()[0];
		}
		
		String xlist = System.getProperty("xlist");
		if(xlist != null){
			if(xlist.contains(","))
				inexcludeList = Converter.separate(xlist, ",", new LinkedList<String>());
			else if(xlist.contains(" "))
				inexcludeList = Converter.separate(xlist, " ", new LinkedList<String>());
		}
		
		String main = System.getProperty("mainsave");
		if(main != null){
			if(Converter.isPositiveResponse(main))
				suppressMainReportSave = true;
		}
	}
	
	/**
	 * Returns an auto-generated filename with the format:
	 * "Auto-Gen_YYYYMMDD_xxxx" where YYYYMMDD is the current date 
	 * and xxxx is a number starting from 0 and can increase to 
	 * beyond 1000
	 * @return an auto-generated random filename 
	 **/
	private String generateRandomFilename(){
		autoGenFilenameCount++;
		return "Auto-Gen_" + Converter.currentDate() + "_" 
		+ zeroes(autoGenFilenameCount) + autoGenFilenameCount;
	}

	/**
	 * Processes the inputed file path based on the 
	 * file extension.<p>
	 * .xls - sent directly to <code>MasterExtractor</code> for extraction<p>
	 * .txt - is processed by the useTextFile method and then the 
	 * .xls path provided within sent to <code>MasterExtractor</code> for extraction<p>
	 * other file extension - unsupported user is prompted and takes no action for this file<p>
	 * All files are then processed by <code>MasterExtractor</code>. All generated files are 
	 * then saved. If validation methods are selected, then all generated files are sent for
	 * validation. A report is generated, saved and printed to the console.
	 * @see MasterExtractor
	 **/
	public void prompt(String path) {
		try{
			new GlobalProcessor("global.txt");
		}catch(FileNotFoundException e){
			System.out.println("global.txt was not found\n");
		}
		MasterExtractor masterExtractor = null;
		xlsPath = path;
		try{
			/* the file path has a length of greater 4 and ends in 
			 * .txt, parse the text file for instructions */
			if (xlsPath.length() > 4 && xlsPath.substring(xlsPath.length() - 4, 
					xlsPath.length()).equalsIgnoreCase(".txt"))
				useTextFile(xlsPath);
			//the file path has a length of greater 4
			if (xlsPath.length() > 4 && xlsPath.substring(xlsPath.length() - 4, 
					xlsPath.length()).equalsIgnoreCase(".xls"))
				masterExtractor = new MasterExtractor(xlsPath, 
						inexcludeList, flag);
			//the file path is not a .txt or .xls
			else
				System.out.println("Invalid file type or no file entered. " +
						"Please input .xls or .txt files.");
		}
		/* if the file path was incorrect, will attempt again by 
		 * appending the current working directory to the 
		 * beginning of the path */
		catch(FileNotFoundException fnfe){
			System.out.println(xlsPath + " was not found." );
			if(tryAgain){
				tryAgain = false;
				prompt(System.getProperty("user.dir") + path);
			}
		//If an IOException occurs, the stack trace will be printed
		}catch(IOException ioe){
			System.out.println("An Exception occurred reading "
					+ xlsPath + ". Please try again." );
			ioe.printStackTrace();
		}
		//reset the tryAgain variable
		finally{tryAgain = true;}

		/* if nothing went wrong previously, then <code>masterExtractor</code> 
		 * will not be <code>null</code>.*/
		if(masterExtractor!= null){
			//extract all of the dif fields
			masterExtractor.extractAll(); 
			
			//create a main report
			Global.Report mainReport = new Global.Report("Main Report", xlsPath);
			mainReport.addSentenceToBody("Generated on: " + 
					Converter.currentDate() + " "+ Converter.currentTime(":"));
			mainReport.addSentenceToBody("# of Records: " + 
					masterExtractor.getAllRecords().size());

			//save each record
			for(int i = 0; i < masterExtractor.getAllRecords().size(); i++){
				mainReport.addSentenceToBody("Saving... " 
						+ masterExtractor.getAllRecords().get(i).getName());
				String flag = save(masterExtractor.getAllRecords().get(i));
				Global.Report recordReport = 
					new Global.Report(masterExtractor.getAllRecords().get(i).getName());
				if(flag != null){
					mainReport.addSentenceToBody("Save Successful.");
					recordReport.addSentenceToBody("Save Successful.");
					recordReport.setPath(flag);
				}
				else{
					mainReport.addSentenceToBody("Save Failed.");
					recordReport.addSentenceToBody("Save Failed");
				}
				individualReports.add(recordReport);
			}
			mainReport.addSentenceToBody("");

			//record the current time
			long time = System.currentTimeMillis();  
			
			//validate via GCMD
			if(useGCMDValidator)
				GCMDValidator.validate(mainReport, individualReports);
			
			//validate via XML schema validation
			if(useXMLValidator)
				XMLValidator.validate(mainReport, 
						individualReports, Global.DIF_XSI_JUST_SCHEMA_LOCATION);
			
			//subtract current time from the previous time, to calculate how much time it took to validate
			time = System.currentTimeMillis() - time;
			
			//default the units to milliseconds
			String units = "milliseconds";
			
			//change the units to seconds if it took more than a second
			if(time / 1000 != 0){
				time = time / 1000;
				units = "seconds";
			}

			mainReport.addSentenceToBody("Validation took " + time + " " + units);
			
			//save the main report
			if(!suppressMainReportSave)
				save(mainReport);
			
			//print the main report to the console
			System.out.println("\n\r" + mainReport);
		}
	}
	
	/**
	 * Saves the DIFRecord in a file with the same 
	 * name as the Entry_ID. If the Entry_ID does not exist,
	 * the file name will be randomly generated.
	 * @param record the DIFRecord to save
	 * @return the filename if the save is successful, null otherwise
	 **/
	public String save(DIFRecord record){
		try{
			String filename = record.getName();
			
			//auto-generate filename
			if(Converter.isEmpty(filename)){
				System.out.println("no file name available");
				filename = generateRandomFilename();
				System.out.println("Auto-generated file name " + filename);
			}
			String path = outputDirectory + "/" + filename + ".xml";
			if(Converter.isEmpty(outputDirectory))
				path = outputDirectory + filename + ".xml";
			File file = new File(path);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			//toString the record
			String recordData = record.toString();
			/* write the record to the file, replacing the \n with newLines, 
			 * so the file will display correctly */
			for(int i = 0; i < recordData.length(); i++){
				if(recordData.charAt(i) == '\n' || recordData.charAt(i) == '\r')
					writer.newLine();
				else
					writer.write(recordData.charAt(i));
			}
			//close the writer
			writer.close();
			
			return path;
			
		}catch(FileNotFoundException fe){
			if(tryAgain){
				/* if a bad output directory is given, will 
				 * output to current working directory */
				System.out.println("Bad output directory. " +
						"Outputting to current working directory");
				tryAgain = false;
				//change the outputDirectory to the current one
				outputDirectory = "";
				return save(record);
			}
			/* if another error occurs after the first, print 
			 * the stack trace */
			fe.printStackTrace();
			
			return null;
		}
		//if an IOException occurs
		catch(IOException e){
			e.printStackTrace(); 
			return null;
		}
		//reset the tryAgain variable
		finally{tryAgain = true;}
		
	}
	
	/**
	 * Saves the main report
	 * @param report the report to be saved
	 * @return a String for the filename for the report
	 **/
	public String save(Global.Report report){
		try{
			String filename = "Main_Report_" + Converter.currentDate() + "-" 
				+ Converter.currentTime("-")+ ".txt";
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(new File(filename)));
			String reportData = report.toString();
			for(int i = 0; i < reportData.length(); i++){
				if(reportData.charAt(i) == '\n' || 
						reportData.charAt(i) == '\r')
					writer.newLine();
				else
					writer.write(reportData.charAt(i));
			}
			writer.close();
			return filename;
		}catch(IOException e){return null;}
	}
	
	/**
	 * Extracts information from a text file of instructions. 
	 * <b>Only handles:<p>
	 * 	-INPUT PATH<p>
	 *  -OUTPUT PATH<p>
	 *  -INCLUDE PRODUCT NUMBERS<p>
	 *  -EXCLUDE PRODUCT NUMBERS<p>
	 *  -GCMD VALIDATION<p>
	 *  -DIF VALIDATION</b>
	 * @param txtfile the text file of instructions
	 * @throws <code>FileNotFoundException</code>
	 **/
	public void useTextFile(String txtfile) throws FileNotFoundException{
		Scanner scan = new Scanner(new File(txtfile)).useDelimiter("\n");
		while(scan.hasNext()){
			String line = scan.next();
			if(line != null){
				//input path
				if (Converter.startsWith("INPUT PATH", line))
					xlsPath = line.replace("INPUT PATH:", "")
						.replace("INPUT PATH", "").trim();

				//output path
				else if (Converter.startsWith("OUTPUT PATH", line))
					outputDirectory = line.replace("OUTPUT PATH:", "")
						.replace("OUTPUT PATH", "").trim();
				
				//include product numbers
				else if (Converter.startsWith("INCLUDE PRODUCT NUMBERS", line)){
					line = line.replace("INCLUDE PRODUCT NUMBERS:", "")
						.replace("INCLUDE PRODUCT NUMBERS", "").trim();
					if(!Converter.isEmpty(line)){
						inexcludeList = Converter.separate(line, ",", new LinkedList<String>());
						flag = 'i';
					}
				}
				
				//exclude product numbers
				else if (Converter.startsWith("EXCLUDE PRODUCT NUMBERS", line)){
					line = line.replace("EXCLUDE PRODUCT NUMBERS:", "").replace("EXCLUDE PRODUCT NUMBERS", "").trim();
					if(!Converter.isEmpty(line)){
						inexcludeList = Converter.separate(line, ",", new LinkedList<String>());
						flag = 'e';
					}
				}
				
				//gcmd validation
				else if (Converter.startsWith("GCMD VALIDATION (Y/N)", line) 
						|| Converter.startsWith("GCMD VALIDATION", line)){
					line = line.replace("GCMD VALIDATION (Y/N):", "").replace("GCMD VALIDATION (Y/N)", 
					"").replace("GCMD VALIDATION", "").trim();
					if (line.equalsIgnoreCase("Y") || line.equalsIgnoreCase("Yes") 
							|| line.equalsIgnoreCase("T") || line.equalsIgnoreCase("True"))
						useGCMDValidator = true;
					else if (line.equalsIgnoreCase("N") || line.equalsIgnoreCase("No") 
							|| line.equalsIgnoreCase("F") || line.equalsIgnoreCase("False"))
						useGCMDValidator = false;
				}
				
				//xml schema validation
				else if (Converter.startsWith("XML SCHEMA VALIDATION (Y/N)", line) 
						|| Converter.startsWith("XML SCHEMA VALIDATION", line)){
					line = line.replace("XML SCHEMA VALIDATION (Y/N):", "").replace("XML SCHEMA VALIDATION (Y/N)", 
					"").replace("XML SCHEMA VALIDATION", "").trim();
					if (line.equalsIgnoreCase("Y") || line.equalsIgnoreCase("Yes") 
							|| line.equalsIgnoreCase("T") || line.equalsIgnoreCase("True"))
						useXMLValidator = true;
					else if (line.equalsIgnoreCase("N") || line.equalsIgnoreCase("No") 
							|| line.equalsIgnoreCase("F") || line.equalsIgnoreCase("False"))
						useXMLValidator = false;
				}
			}
		}
	}
	
	/**
	 * Helper method for auto-generating a filename adds 
	 * extra leading 0s to the incoming number. If incoming 
	 * number is greater than 1000 no leading 0s are added. 
	 * @param num the current filename
	 * @return String with the appropriate number of leading 0s
	 **/
	private String zeroes(int num){
		if(num < 10) return "000";
		if(num < 100) return "00";
		if(num < 1000) return "0";
		return "";
	}	
	
	/**
	 * Handles the file paths inputed at the command line and
	 * will send each one to another <code>DIFDriver</code>
	 * @param args 	the array of file paths to consider 
	 **/
	public static void main(String[] args){
		//debug for if no files are passed in
		if(args == null || args.length == 0)
			System.out.println(help);
		
		else{
			for(String s: args){
				System.out.println("Processing " + s);
				new DIFDriver().prompt(s);
			}
		}
	}
}
