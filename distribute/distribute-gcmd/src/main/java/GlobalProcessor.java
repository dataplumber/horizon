import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;


public class GlobalProcessor{
	
	public GlobalProcessor(String filepath) throws FileNotFoundException{
		Scanner scan = new Scanner(new File(filepath)).useDelimiter("\n");
		boolean rangeGo = false;
		LinkedList<Global.MType> allMeterTypes = new LinkedList<Global.MType>();
		LinkedList<Global.MType> allDegreeTypes = new LinkedList<Global.MType>();
		LinkedList<Global.MType> allSecTypes = new LinkedList<Global.MType>();
		while(scan.hasNext()){
			String line = scan.next().trim();
			if(!Converter.startsWith("//", line) && !Converter.isEmpty(line)){
				String header = getHeader(line);
				
				if(line.equals("rangeStart")){
					rangeGo = true;
					continue;
				}
				else if(line.equals("rangeEnd")){
					rangeGo = false;
					Global.allMeterTypes = Converter.toMTypeArray(allMeterTypes);
					Global.allDegreeTypes = Converter.toMTypeArray(allDegreeTypes);
					Global.allSecTypes = Converter.toMTypeArray(allSecTypes);
					continue;
				}
				
				//HORIZONTAL_RESOLUTION_RANGE possible values
				if(rangeGo && line.contains(",")){
					Global.MType mtype= new Global.MType(Converter.separate(line, ",", new LinkedList<String>()));
					if(mtype.units!= null){
						if (mtype.units.equalsIgnoreCase("meters") || mtype.units.equalsIgnoreCase("meter") || mtype.units.equalsIgnoreCase("m"))
							allMeterTypes.add(mtype);
						else if (mtype.units.equalsIgnoreCase("degrees") || mtype.units.equalsIgnoreCase("degree") || mtype.units.equalsIgnoreCase("deg"))
							allDegreeTypes.add(mtype);
						else if (mtype.units.equalsIgnoreCase("second") || mtype.units.equalsIgnoreCase("seconds") || mtype.units.equalsIgnoreCase("sec"))
							allSecTypes.add(mtype);
					}
				}
				else if(header.equalsIgnoreCase("FIRST_PRODUCT_ROW")){
					try{
						Global.FIRST_PRODUCT_ROW = Integer.parseInt(replaceFirst(line, header));
					}catch(NumberFormatException e){
						System.out.println("Bad number provided for First_Product_Row");
					}
				}
				
				//XML AND VALIDATION
				else if(header.equalsIgnoreCase("XML_VERSION")){
					try{
						Global.XML_VERSION = Double.parseDouble(replaceFirst(line, header));
					}catch(NumberFormatException e){
						System.out.println("Bad number provided for XML_Version");
					}
				}
				else if(header.equalsIgnoreCase("ENCODING"))
					Global.ENCODING = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DIF_XML_NS"))
					Global.DIF_XML_NS = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DIF_XML_NS_DIF"))
					Global.DIF_XML_NS_DIF = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DIF_XML_NS_XSI"))
					Global.DIF_XML_NS_XSI = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DIF_XSI_SCHEMA_LOCATION"))
					Global.DIF_XSI_SCHEMA_LOCATION = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DIF_VALIDATOR_WEB_ADDRESS"))
					Global.DIF_VALIDATOR_WEB_ADDRESS = replaceFirst(line, header);
				
				//EXCEL SHEETS
				else if(header.equalsIgnoreCase("TEMPORAL_SHEET"))
					Global.TEMPORAL_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SPATIAL_SHEET"))
					Global.SPATIAL_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PRODUCT_SHEET"))
					Global.PRODUCT_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SUMMARY_SHEET"))
					Global.SUMMARY_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PERSONNEL_SHEET"))
					Global.PERSONNEL_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DATA_CENTER_SHEET"))
					Global.DATA_CENTER_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PARAMETER_SHEET"))
					Global.PARAMETER_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("METADATA_REVISION_SHEET"))
					Global.METADATA_REVISION_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SENSOR_SHEET"))
					Global.SENSOR_SHEET = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SUBPRODUCT_SHEET"))
					Global.SUBPRODUCT_SHEET = replaceFirst(line, header);
				
				//PRODUCT NUMBER
				else if(header.equalsIgnoreCase("PRODUCT_NUMBER"))
					Global.PRODUCT_NUMBER = replaceFirst(line, header);
				
				//CONTACT_ADDRESS
				else if(header.equalsIgnoreCase("CONTACT_ADDRESS"))
					Global.CONTACT_ADDRESS = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("CONTACT_ADDRESS_COLUMN_NAMES")){
					try{
						Global.CONTACT_ADDRESS_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.CONTACT_ADDRESS_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("CONTACT_ADDRESS_ACTUAL_NAMES")){
					try{
						Global.CONTACT_ADDRESS_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.CONTACT_ADDRESS_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("ADDRESS"))
					Global.ADDRESS = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("CITY"))
					Global.CITY = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PROVINCE_OR_STATE"))
					Global.PROVINCE_OR_STATE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("POSTAL_CODE"))
					Global.POSTAL_CODE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("COUNTRY"))
					Global.COUNTRY = replaceFirst(line, header);
				
				//DATA_CENTER
				else if(header.equalsIgnoreCase("DATA_CENTER"))
					Global.DATA_CENTER = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DATA_CENTER_COLUMN_NAMES")){
					try{
						Global.DATA_CENTER_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_CENTER_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("DATA_CENTER_ACTUAL_NAMES")){
					try{
						Global.DATA_CENTER_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_CENTER_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("URL"))
					Global.URL = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ID"))
					Global.ID = replaceFirst(line, header);
				
				//DATA_CENTER_NAME
				else if(header.equalsIgnoreCase("DATA_CENTER_NAME"))
					Global.DATA_CENTER_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DATA_CENTER_NAME_COLUMN_NAMES")){
					try{
						Global.DATA_CENTER_NAME_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_CENTER_NAME_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("DATA_CENTER_ACTUAL_NAMES")){
					try{
						Global.DATA_CENTER_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_CENTER_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("DATA_CENTER_SHORT_NAME"))
					Global.DATA_CENTER_SHORT_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DATA_CENTER_LONG_NAME"))
					Global.DATA_CENTER_LONG_NAME = replaceFirst(line, header);
				
				//DATA_SET_CITATION
				else if(header.equalsIgnoreCase("DATA_SET_CITATION"))
					Global.DATA_SET_CITATION = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DATA_SET_CITATION_COLUMN_NAMES_PRODUCT_SHEET")){
					try{
						Global.DATA_SET_CITATION_COLUMN_NAMES_PRODUCT_SHEET = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_SET_CITATION_COLUMN_NAMES_PRODUCT_SHEET = new String[0];}
				}
				else if(header.equalsIgnoreCase("DATA_SET_CITATION_COLUMN_NAMES_METADATA_REVISION_SHEET")){
					try{
						Global.DATA_SET_CITATION_COLUMN_NAMES_METADATA_REVISION_SHEET = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_SET_CITATION_COLUMN_NAMES_METADATA_REVISION_SHEET = new String[0];}
				}
				else if(header.equalsIgnoreCase("DATA_SET_CITATION_ACTUAL_NAMES_PRODUCT_SHEET")){
					try{
						Global.DATA_SET_CITATION_ACTUAL_NAMES_PRODUCT_SHEET = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_SET_CITATION_ACTUAL_NAMES_PRODUCT_SHEET = new String[0];}
				}
				else if(header.equalsIgnoreCase("DATA_SET_CITATION_ACTUAL_NAMES_METADATA_REVISION_SHEET")){
					try{
						Global.DATA_SET_CITATION_ACTUAL_NAMES_METADATA_REVISION_SHEET = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DATA_SET_CITATION_ACTUAL_NAMES_METADATA_REVISION_SHEET = new String[0];}
				}
				else if(header.equalsIgnoreCase("DATASET_CREATOR"))
					Global.DATASET_CREATOR = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DATASET_TITLE"))
					Global.DATASET_TITLE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ONLINE_RESOURCE"))
					Global.ONLINE_RESOURCE = replaceFirst(line, header);
				
				//DIF_CREATION_DATE
				else if(header.equalsIgnoreCase("DIF_CREATION_DATE_ACTUAL_NAME"))
					Global.DIF_CREATION_DATE_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DIF_CREATION_DATE_COLUMN_NAMES")){
					try{
						Global.DIF_CREATION_DATE_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.DIF_CREATION_DATE_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("ONLINE_RESOURCE"))
					Global.ONLINE_RESOURCE = replaceFirst(line, header);
				
				//ENTRY_ID
				else if(header.equalsIgnoreCase("ENTRY_ID"))
					Global.ENTRY_ID = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ENTRY_ID_ACTUAL_NAME"))
					Global.ENTRY_ID_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ENTRY_ID_COLUMN_NAMES")){
					try{
						Global.ENTRY_ID_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.ENTRY_ID_COLUMN_NAMES = new String[0];}
				}
				
				//ENTRY_TITLE
				else if(header.equalsIgnoreCase("ENTRY_TITLE"))
					Global.ENTRY_TITLE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ENTRY_TITLE_ACTUAL_NAME"))
					Global.ENTRY_TITLE_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ENTRY_TITLE_COLUMN_NAMES")){
					try{
						Global.ENTRY_TITLE_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.ENTRY_TITLE_COLUMN_NAMES = new String[0];}
				}
				
				//IDN_NODE
				else if(header.equalsIgnoreCase("IDN_NODE"))
					Global.IDN_NODE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("IDN_NODE_ACTUAL_NAMES")){
					try{
						Global.IDN_NODE_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.IDN_NODE_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("IDN_NODE_COLUMN_NAMES")){
					try{
						Global.IDN_NODE_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.IDN_NODE_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("IDN_NODE_SHORT_NAME"))
					Global.IDN_NODE_SHORT_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("IDN_NODE_LONG_NAME"))
					Global.IDN_NODE_LONG_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DEFAULT_IDN_NODE_SHORT_NAME"))
					Global.DEFAULT_IDN_NODE_SHORT_NAME = replaceFirst(line, header);
				
				//ISO_TOPIC_CATEGORY
				else if(header.equalsIgnoreCase("ISO_TOPIC_CATEGORY"))
					Global.ISO_TOPIC_CATEGORY = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ISO_TOPIC_CATEGORY_ACTUAL_NAME"))
					Global.ISO_TOPIC_CATEGORY_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("ISO_TOPIC_CATEGORY_COLUMN_NAMES")){
					try{
						Global.ISO_TOPIC_CATEGORY_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.ISO_TOPIC_CATEGORY_COLUMN_NAMES = new String[0];}
				}
				
				//LAST_DIF_REVISION_DATE
				else if(header.equalsIgnoreCase("LAST_DIF_REVISION_DATE"))
					Global.LAST_DIF_REVISION_DATE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LAST_DIF_REVISION_DATE_ACTUAL_NAME"))
					Global.LAST_DIF_REVISION_DATE_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LAST_DIF_REVISION_DATE_COLUMN_NAMES")){
					try{
						Global.LAST_DIF_REVISION_DATE_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.LAST_DIF_REVISION_DATE_COLUMN_NAMES = new String[0];}
				}
				
				//LOCATION
				else if(header.equalsIgnoreCase("LOCATION"))
					Global.LOCATION = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LOCATION_COLUMN_NAMES")){
					try{
						Global.LOCATION_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.LOCATION_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("LOCATION_ACTUAL_NAMES")){
					try{
						Global.LOCATION_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.LOCATION_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("LOCATION_CATEGORY"))
					Global.LOCATION_CATEGORY= replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LOCATION_TYPE"))
					Global.LOCATION_TYPE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LOCATION_SUBREGION1"))
					Global.LOCATION_SUBREGION1= replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LOCATION_SUBREGION2"))
					Global.LOCATION_SUBREGION2 = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LOCATION_SUBREGION3"))
					Global.LOCATION_SUBREGION3 = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DETAILED_LOCATION"))
					Global.DETAILED_LOCATION = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("DEFAULT_LOCATION_CATEGORY"))
					Global.DEFAULT_LOCATION_CATEGORY = replaceFirst(line, header);
				
				//METADATA_NAME
				else if(header.equalsIgnoreCase("METADATA_NAME"))
					Global.METADATA_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("METADATA_NAME_ACTUAL_NAME"))
					Global.METADATA_NAME_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("METADATA_NAME_COLUMN_NAMES")){
					try{
						Global.METADATA_NAME_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.METADATA_NAME_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("DEFAULT_METADATA_NAME"))
					Global.DEFAULT_METADATA_NAME = replaceFirst(line, header);
				
				//METADATA_VERSION
				else if(header.equalsIgnoreCase("METADATA_VERSION"))
					Global.METADATA_VERSION = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("METADATA_VERSION_ACTUAL_NAME"))
					Global.METADATA_VERSION_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("METADATA_VERSION_COLUMN_NAMES")){
					try{
						Global.METADATA_VERSION_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.METADATA_VERSION_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("DEFAULT_METADATA_VERSION")){
					try{
						Double.parseDouble(replaceFirst(line, header));
						Global.DEFAULT_METADATA_VERSION = replaceFirst(line, header);
					}catch(NumberFormatException e){
						System.out.println("Bad number provided for DEFAULT_METADATA_VERSION");
					}
				}
				
				//PARAMETERS
				else if(header.equalsIgnoreCase("PARAMETERS"))
					Global.PARAMETERS = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PARAMETERS_COLUMN_NAMES")){
					try{
						Global.PARAMETERS_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.PARAMETERS_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("PARAMETERS_ACTUAL_NAMES")){
					try{
						Global.PARAMETERS_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.PARAMETERS_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("DEFAULT_CATEGORY"))
					Global.DEFAULT_CATEGORY = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("CATEGORY"))
					Global.CATEGORY= replaceFirst(line, header);
				else if(header.equalsIgnoreCase("TOPIC"))
					Global.TOPIC= replaceFirst(line, header);
				else if(header.equalsIgnoreCase("TERM_VARIABLE_L1"))
					Global.TERM_VARIABLE_L1 = replaceFirst(line, header);
				
				//PERSONNEL
				else if(header.equalsIgnoreCase("PERSONNEL"))
					Global.PERSONNEL = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PERSONNEL_COLUMN_NAMES")){
					try{
						Global.PERSONNEL_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.PERSONNEL_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("PERSONNEL_ACTUAL_NAMES")){
					try{
						Global.PERSONNEL_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.PERSONNEL_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("ROLE"))
					Global.ROLE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("FIRST_NAME"))
					Global.FIRST_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("MIDDLE_NAME"))
					Global.MIDDLE_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("LAST_NAME"))
					Global.LAST_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("EMAIL"))
					Global.EMAIL = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PHONE"))
					Global.PHONE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("FAX"))
					Global.FAX = replaceFirst(line, header);
				
				//PROJECT
				else if(header.equalsIgnoreCase("PROJECT"))
					Global.PROJECT = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PROJECT_COLUMN_NAMES")){
					try{
						Global.PROJECT_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.PROJECT_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("PROJECT_ACTUAL_NAMES")){
					try{
						Global.PROJECT_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.PROJECT_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("PROJECT_SHORT_NAME"))
					Global.PROJECT_SHORT_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("PROJECT_LONG_NAME"))
					Global.PROJECT_LONG_NAME = replaceFirst(line, header);
				
				//SENSOR_NAME
				else if(header.equalsIgnoreCase("SENSOR_NAME"))
					Global.SENSOR_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SENSOR_NAME_COLUMN_NAMES")){
					try{
						Global.SENSOR_NAME_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.SENSOR_NAME_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("SENSOR_NAME_ACTUAL_NAMES")){
					try{
						Global.SENSOR_NAME_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.SENSOR_NAME_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("SENSOR_NAME_SHORT_NAME"))
					Global.SENSOR_NAME_SHORT_NAME= replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SENSOR_NAME_LONG_NAME"))
					Global.SENSOR_NAME_LONG_NAME = replaceFirst(line, header);
				
				//SOURCE_NAME
				else if(header.equalsIgnoreCase("SOURCE_NAME"))
					Global.SOURCE_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SOURCE_NAME_COLUMN_NAMES")){
					try{
						Global.SOURCE_NAME_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.SOURCE_NAME_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("SOURCE_NAME_ACTUAL_NAMES")){
					try{
						Global.SOURCE_NAME_ACTUAL_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.SOURCE_NAME_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("SOURCE_NAME_SHORT_NAME"))
					Global.SOURCE_NAME_SHORT_NAME= replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SOURCE_NAME_LONG_NAME"))
					Global.SOURCE_NAME_LONG_NAME = replaceFirst(line, header);
				
				//SPATIAL_COVERAGE
				else if(header.equalsIgnoreCase("SPATIAL_COVERAGE_ACTUAL_NAME"))
					Global.SPATIAL_COVERAGE_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SPATIAL_COVERAGE_COLUMN_NAMES ")){
					try{
						Global.SPATIAL_COVERAGE_COLUMN_NAMES  = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.SPATIAL_COVERAGE_COLUMN_NAMES  = new String[0];}
				}
				else if(header.equalsIgnoreCase("SPATIAL_COVERAGE_ACTUAL_NAMES")){
					try{
						Global.SPATIAL_COVERAGE_ACTUAL_NAMES= Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.SPATIAL_COVERAGE_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("SPATIAL_COVERAGE"))
					Global.SPATIAL_COVERAGE = replaceFirst(line, header);
				

				//SUMMARY
				else if(header.equalsIgnoreCase("SUMMARY"))
					Global.SUMMARY = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SUMMARY_ACTUAL_NAME"))
					Global.SUMMARY_ACTUAL_NAME = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("SUMMARY_COLUMN_NAMES ")){
					try{
						Global.SUMMARY_COLUMN_NAMES  = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.SUMMARY_COLUMN_NAMES = new String[0];}
				}						
				
				//TEMPORAL_COVERAGE
				else if(header.equalsIgnoreCase("TEMPORAL_COVERAGE"))
					Global.TEMPORAL_COVERAGE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("TEMPORAL_COVERAGE_COLUMN_NAMES")){
					try{
						Global.TEMPORAL_COVERAGE_COLUMN_NAMES = Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.TEMPORAL_COVERAGE_COLUMN_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("TEMPORAL_COVERAGE_ACTUAL_NAMES")){
					try{
						Global.TEMPORAL_COVERAGE_ACTUAL_NAMES= Converter.separateToArray(replaceFirst(line, header));
					}catch(NullPointerException e){Global.TEMPORAL_COVERAGE_ACTUAL_NAMES = new String[0];}
				}
				else if(header.equalsIgnoreCase("START_DATE"))
					Global.START_DATE = replaceFirst(line, header);
				else if(header.equalsIgnoreCase("STOP_DATE"))
					Global.STOP_DATE = replaceFirst(line, header);
			}
		}//end of scanner loop
		//System.out.println(Converter.arrayToString(Global.allMeterTypes));
	}
	
	public String getHeader(String s){
		int semiColon = s.indexOf('=');
		if(semiColon != -1)
			return s.substring(0,semiColon).trim();
		return null;
	}
	
	public String replaceFirst(String line, String header){
		return new String(line).replaceFirst(header + "=", "").replaceFirst(header, "").replaceFirst("=", "").trim();
	}
	
	public static void main(String[]args) throws FileNotFoundException{
		new GlobalProcessor("C:/Users/Dawn/Documents/global.txt");
	}
}
