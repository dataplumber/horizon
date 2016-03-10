package gov.nasa.horizon.archive.tool;

import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.external.InventoryFactory;
import gov.nasa.horizon.inventory.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ArchivePopularity {

	public static Log log = LogFactory.getLog(ArchivePopularity.class);
	private Options options = new Options();
	//private boolean monthly = false;
	//private boolean alltime = false;
	private boolean prodId = false;
	private boolean useLegacy = false;
	private String outputPath = "";
	private String inputFile = "";
	private int lineNum=1;
	private List<Record> allRecords = new ArrayList<Record>();
	 
	public static void main(String[] args) {
		ArchiveProperty.getInstance();
		ArchivePopularity ap = new ArchivePopularity();
		ap.run(args);
	}
	public ArchivePopularity(){}
	
	@SuppressWarnings("unchecked")
	public void run(String[] args){
		
		loadOptions();
		try {
			parseArgs(args);
		} catch (ParseException e) {
			System.out.println("Unable to load/parse command line options.");
			System.exit(999);
		}
		
		readInput();
		//if(monthly){
			Collections.sort(allRecords);
			
			
			 List<Record> recs = nameLookup(allRecords);
			 Collections.sort(recs);
			 for(Record r: recs){
					log.debug(r.toString());
				}
			 
			 writePopularity(recs);
		//}
	}
	
	private List<Record> nameLookup(List<Record> allRecords) {
	
		List<Record> newRecords = new ArrayList<Record>();
		//List<Record> delRecords = new ArrayList<Record>();
		
		for(Record r : allRecords)
		{
			List<String> names = null;
			String name = null;
			if(prodId){
				log.debug("looking up pid: " + r.product_id);
				//get a list of names, if names > 1, divide the count size by that number, and create new Records with the same info, but the other names in Name.
				ProductType pt = InventoryFactory.getInstance().getQuery().fetchProductType(Long.valueOf(r.product_id));;
				if(pt != null)
				   name = pt.getIdentifier();
				//System.out.println("size of names: " + names.size());
				if(names.size()==0){
					System.out.println("No productType found for product: "+ r.product_id);
					log.debug("No productType found for product: "+ r.product_id);
					r.dName=null;
				}
				else if(names.size()> 1){
					//System.out.println(r.count);
					//delRecords.add(r);
					double divCount = r.count / names.size();
					//r.count = r.count / names.size();
					//r.dName = names.get(0);
					
					for(String n : names){
						log.debug("Multiple productTypes name: " + n);
						Record newR = new Record(r.product_id, r.year,r.month,divCount);
						newR.dName= n;
						newRecords.add(newR);
						//System.out.println(newR.toString());
					}
					
				}
				else{
					log.debug("Single productTypes name: " + names.get(0));
					r.dName= names.get(0);
					newRecords.add(r);
				}
			}
			else{
				//if(useLegacy){
					log.debug("looking up pid: " + r.product_id);
					try{
					   ProductType pt = InventoryFactory.getInstance().getQuery().fetchProductType(Long.valueOf(r.product_id));
						if(pt != null) {
						   name = pt.getIdentifier();
						}
						log.debug("Found item with name: " + name);
					}catch(NullPointerException npr){
						r.dName=null;
					}
					
				//}
				//else{
				//	try{
				//		log.debug("looking up pid: " + r.product_pers_id);
				//		name = InventoryFactory.getInstance().getQuery().fetchProductTypeByPersistentId(r.product_pers_id).getShortName();
				//		log.debug("Found item with name: " + name);
				//	}catch(NullPointerException npr){
				//		r.dName=null;
				//	}
				//}
				r.dName=name;
				newRecords.add(r);
			}
			
		}
//		allRecords.removeAll(delRecords);
//		allRecords.addAll(newRecords);
		return newRecords;
		
	}
	private void writePopularity(List<Record> allRecords) {
		
		 try{
			    // Create file 
			    FileWriter fstream = new FileWriter(outputPath);
			        BufferedWriter out = new BufferedWriter(fstream);
			   
			        out.write("<Document>\n\t<Entries>\n");
			        int pop = 1;
			        for(Record r : allRecords)
					{
						if(r.dName != null)
						{
							out.write("\t\t<Entry>\n");
							out.write("\t\t\t<ProductTypeName>"+r.dName+"</ProductTypeName>\n");
		                    out.write("\t\t\t<Popularity>"+pop+"</Popularity>\n");
		                    out.write("\t\t</Entry>\n");
							pop++;
						}
						else{
							if(useLegacy || prodId)
								log.error("No productType name found for product with id=" + r.product_id);
							else
								log.error("No productType name found for product with id=" + r.product_pers_id);
						}
					}
			        out.write("\t</Entries>\n</Document>\n");
			        
			        out.close();
			    }catch (Exception e){//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
			    }
		
	}
	
	private void readInput() {
		FileReader fr = null;
		try {
			fr = new FileReader(inputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find input file. Exiting...");
		} 
		BufferedReader br = new BufferedReader(fr);
		String _input;
		try{
		while((_input = br.readLine())!=null){
			processLine(_input);
			lineNum++;
		}
		System.out.println("Processed "+ --lineNum +" lines.");
		log.info("Processed "+ --lineNum +" lines.");
		br.close();
		if(fr != null)
			fr.close();
		
		}catch(IOException ioe){
			System.out.println("Error: The input file specified could not be read.");
	    	  printHelp();
	    	  System.exit(0);
		}
	}
	
	public void processLine(String line){
		
		if(line == null)
			return;
		
		String[] tokens = line.split(" ");
		if(tokens.length!=4){
			System.out.println("Unexpected number of tokens in line "+lineNum+". Expected 4 got " + tokens.length);
			log.info("Unexpected number of tokens in line "+lineNum+". Expected 4 got " + tokens.length);
			return;
		}
		String t1b = null;
		Integer t1=null;
		Integer t2=null,t3=null;
		Double t4=null;
		if(useLegacy || prodId)
		{
			try{
				t1 = Integer.valueOf(tokens[0]);
			}catch(NumberFormatException nfe){
				System.out.println("Error converting val \""+tokens[0]+"\" to integer. Skipping line "+lineNum+" .");
				log.info("Error converting val \""+tokens[0]+"\" to integer. Skipping line "+lineNum+" .");
				return;
			}
		}
		else{
			t1b = tokens[0];
		}
		try{
			t2 = Integer.valueOf(tokens[1]);
		}catch(NumberFormatException nfe){
			System.out.println("Error converting val \""+tokens[1]+"\" to integer. Skipping line "+lineNum+" .");
			log.info("Error converting val \""+tokens[1]+"\" to integer. Skipping line "+lineNum+" .");
			return;
		}
		try{
			t3 = Integer.valueOf(tokens[2]);
		}catch(NumberFormatException nfe){
			System.out.println("Error converting val \""+tokens[2]+"\" to integer. Skipping line "+lineNum+" .");
			log.info("Error converting val \""+tokens[2]+"\" to integer. Skipping line "+lineNum+" .");
			return;
		}
		try{
			t4 = Double.valueOf(tokens[3]);
		}catch(NumberFormatException nfe){
			System.out.println("Error converting val \""+tokens[3]+"\" to integer. Skipping line "+lineNum+" .");
			log.info("Error converting val \""+tokens[3]+"\" to integer. Skipping line "+lineNum+" .");
			return;
		}
		Record r = null;
		if(useLegacy || prodId)
			r = new Record(t1,t2,t3,t4);
		else
			r = new Record(t1b,t2,t3,t4);
		//log.debug(r.toString());
		
		//might need to move this.
		allRecords.add(r);
	}
		
	public void loadOptions(){
		options.addOption ("o", "output", true, "Path to write output");
		options.addOption ("leg", "legacyId", false, "Uses the legacy, numeric productType_id.");
	    //options.addOption ("m", "monthly", false, "Generate monthly popularity index.");
	    options.addOption ("h", "help", false, "Print the help information for this file.");
	    //options.addOption ("a", "all-time", false, "Generate all-time popularity index.");
	    options.addOption ("f", "input-file", true, "Full path to the input file");
	    options.addOption ("pid", "Product Id", false, "use if the ID in the inputfiles is a product ID. Otherwise ProductType IDs will be used.");
	}
	
	public int parseArgs(String[] args) throws ParseException {
      int stat = 0;
      CommandLineParser parser = new BasicParser();
      CommandLine commandLine = parser.parse(options, args);
      
      if(commandLine.hasOption('h')){
    	  printHelp();
    	  System.exit(0);
      }
//      if(commandLine.hasOption("m"))
//    	  monthly = true;
//      if(commandLine.hasOption("a"))
//    	  alltime = true;
      if(commandLine.hasOption("pid"))
    	  prodId = true;
      if(commandLine.hasOption("leg"))
    	  useLegacy = true;
      
      outputPath = commandLine.getOptionValue("o");
      inputFile = commandLine.getOptionValue("f");
      try{
    	  File temp = new File(inputFile);
	      if(!temp.exists()){
	    	  System.out.println("Inputfile does not exist.");
	    	  printHelp();
	    	  System.exit(0);
	      }
      }catch(NullPointerException npe){
    	  System.out.println("Input file was not specified. Please specify a path from which to read.");
    	  printHelp();
    	  System.exit(0);
      }
      if(outputPath == null){
    	  System.out.println("Output path was not specified. Please specify a path to which to write.");
    	  printHelp();
    	  System.exit(0);
      }
//      if(!alltime && !monthly){
//    	  System.out.println("Monthly or alltime report must be specified.");
//    	  printHelp();
//    	  System.exit(0);
//      }
//      if(alltime && monthly){
//    	  System.out.println("Monthly or alltime must be specified, not both.");
//    	  printHelp();
//    	  System.exit(0);
//      }
      return stat;
	}
	 

	public void printHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("archivePopularity.sh", "archivePopularity", options, null, true);
	}
	
	@SuppressWarnings("unchecked")
	private class Record implements Comparable{
		public int product_id, year, month;
		public String product_pers_id;
		public double count;
		public String dName;
		
		
		public Record(int r1, int r2, int r3, double r4){
			this.product_id=r1;
			this.year = r2;
			this.month=r3;
			this.count=r4;
		}
		
		public Record(String r1, int r2, int r3, double r4){
			this.product_pers_id=r1;
			this.year = r2;
			this.month=r3;
			this.count=r4;
		}
		
		public String toString(){
			return "ID: " + this.product_id + ", YEAR: " + this.year + ", MONTH: " + this.month + ", SIZE: " + this.count;
		}
		
		//This actually does a descending list
		//@Override
		public int compareTo(Object c2) {
			Record r2 = (Record)c2;
			if(this.count > r2.count)
				return -1;
			else if(this.count < r2.count)
				return 1;
			else
				return 0;
		}
		
	}
	
	
}
