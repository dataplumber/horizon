import java.util.LinkedList;


public abstract class Global {
	public static int FIRST_PRODUCT_ROW = 3;	//The first row containing a product
	
	//XML version information
	public static double XML_VERSION = 1.0;
	public static String ENCODING = "ISO-8859-1",
	DIF_XML_NS = "http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/",
	DIF_XML_NS_DIF = "http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/",
	DIF_XML_NS_XSI = "http://www.w3.org/2001/XMLSchema-instance", 
	DIF_XSI_SCHEMA_LOCATION = "http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/ http://gcmd.nasa.gov/Aboutus/xml/dif/dif_v9.7.1.xsd",
	DIF_XSI_JUST_SCHEMA_LOCATION = "http://gcmd.nasa.gov/Aboutus/xml/dif/dif_v9.7.1.xsd";
	
	//DIF Validator web address
	public static String DIF_VALIDATOR_WEB_ADDRESS = "http://gcmd.nasa.gov/OpenAPI/request.py";
	
	//Excel sheet names
	public static String TEMPORAL_SHEET = "Temporal",
	SPATIAL_SHEET = "Spatial", 
	PRODUCT_SHEET = "Product", 
	SUMMARY_SHEET = "Summary", 
	PERSONNEL_SHEET = "Personnel", 
	DATA_CENTER_SHEET = "Data Center", 
	PARAMETER_SHEET = "Parameter",
	PROJECTS_SHEET = "Projects",
	METADATA_REVISION_SHEET = "Metadata_Revision",
	SENSOR_SHEET = "Sensor",
	SUBPRODUCT_SHEET = "Subproduct";
	
	//Product_Number
	public static String PRODUCT_NUMBER = "Product#";
	
	/**An array of MTypes for HORIZONTAL_RESOLUTION_RANGE values with the base unit meter**/
	public static MType[] allMeterTypes = {new MType(-1, 1, "meter", "< 1 meter"),
		new MType(1, 30, "meters", "1 meter - < 30 meters"), 
		new MType(30, 100, "meters", "30 meters - < 100 meters"), 
		new MType(100, 250, "meters", "100 meters - < 250 meters"), 
		new MType(250, 500, "meters", "250 meters - < 500 meters"),
		new MType(500, 1000, "meters", "500 meters - < 1 km"),
		new MType(1000, 10000, "meters", "1 km - < 10 km or approximately .01 degree - < .09 degree"),
		new MType(10000, 50000, "meters", "10 km - < 50 km or approximately .09 degree - < .5 degree"),
		new MType(50000, 100000, "meters", "50 km - < 100 km or approximately .5 degree - < 1 degree"),
		new MType(100000, 250000, "meters", "100 km - < 250 km or approximately 1 degree - < 2.5 degrees"),
		new MType(250000, 500000, "meters", "250 km - < 500 km or approximately 2.5 degrees - < 5.0 degrees"),
		new MType(500000, 1000000, "meters", "500 km - < 1000 km or approximately 5.0 degrees - < 10 degrees"),
		new MType(1000000, 99999999, "meters", true, false, "> 1000km or > 10 degrees")};
	
	/**An array of MTypes for HORIZONTAL_RESOLUTION_RANGE values with the base unit degree**/
	public static MType[] allDegreeTypes = {new MType(.01, .09, "degree", "1 km - < 10 km or approximately .01 degree - < .09 degree"),
		new MType(.09, .5, "degree", "10 km - < 50 km or approximately .09 degree - < .5 degree"),
		new MType(.5, 1, "degree", "50 km - < 100 km or approximately .5 degree - < 1 degree"),
		new MType(1, 2.5, "degrees", "100 km - < 250 km or approximately 1 degree - < 2.5 degrees"),
		new MType(2.5, 5.0, "degrees", "250 km - < 500 km or approximately 2.5 degrees - < 5.0 degrees"),
		new MType(5, 10, "degrees", "500 km - < 1000 km or approximately 5.0 degrees - < 10 degrees"),
		new MType(10, 99999999, "degrees", true, false, "> 1000km or > 10 degrees")};

	/**An array of MTypes for TEMPORAL_RESOLUTION_RANGE values with the base unit seconds**/
	public static MType[] allSecTypes = {
		new MType(-1, 1, "second", "< 1 second"),
		new MType(1, 60, "seconds", "1 second - < 1 minute"),
		new MType(60, 60*60, "seconds", "1 minute - < 1 hour"),
		new MType(360, 60*60*24, "seconds", "Hourly - < Daily"), 
		new MType(60*60*24, 60*60*24*7, "seconds", "Daily - < Weekly"),
		new MType(60*60*24*7, 60*60*24*7*4, "seconds", "Weekly - < Monthly"),
		new MType(60*60*24*7*4, 60*60*24*7*4*12, "seconds", "Monthly - < Annual"),
		new MType(60*60*24*7*4*12, 60*60*24*7*4*12, "seconds", false, true, "Annual"),
		new MType(60*60*24*7*4*12, 60*60*24*7*4*12*10, "seconds", true, true, "Decadal")};
	
	////////////////////////////////// DIFField to Column  /////////////////////////////////
	/* The following are DIFField and associated column pairs and should be the only portion 
	 * that should be edited if column names are changed. Arranged in alphabetical order. */

	//CONTACT_ADDRESS
	public static String ADDRESS = "Address", 
	CITY = "City", 
	PROVINCE_OR_STATE = "State",
	POSTAL_CODE = "Postal_Code", 
	COUNTRY = "Country";
	
	//DATA_CENTER
	public static String URL = "Data_Center_URL", 
	ID = "Data_Center_ID"; 
	
	//DATA_CENTER_NAME
	public static String DATA_CENTER_SHORT_NAME = "Data_Center_Short_Name", 
	DATA_CENTER_LONG_NAME = "Data_Center_Long_Name";
	
	//DATA_RESOLUTION
	public static String LATITUDE_RESOLUTION = "Resolution_(N-S)",
	LONGITUDE_RESOLUTION = "Resolution_(E-W)",
	HORIZONTAL_RESOLUTION_RANGE = "Horizontal_Resolution_Range",
	TEMPORAL_RESOLUTION = "Resolution",
	TEMPORAL_RESOLUTION_RANGE = "Temporal Resolution Range";
	
	//DATA_SET_CITATION
	public static String DATASET_CREATOR = "Dataset Creator", 
	DATASET_TITLE = "Title", 
	ONLINE_RESOURCE = "Web_Page";
	
	//DATA_SET_LANGUAGE
	public static String DATA_SET_LANGUAGE = "Data_Set_Language", 
	DEFAULT_DATA_SET_LANGUAGE = "English";
	
	//DIF_CREATION_DATE
	public static String DIF_CREATION_DATE = "DIF_Creation_Date";
	
	//ENTRY_ID
	public static String ENTRY_ID = "Name";
	
	//ENTRY_TITLE
	public static String ENTRY_TITLE = "Title";
	
	//IDN_NODE
	public static String IDN_NODE_SHORT_NAME = "IDN_Node_Short_Name", 
	IDN_NODE_LONG_NAME = "IDN_Node_Long_Name", DEFAULT_IDN_NODE_SHORT_NAME = "USA/NASA";
	
	//ISO_TOPIC_CATEGORY
	public static String ISO_TOPIC_CATEGORY = "ISO_Topic_Category";
	
	//LAST_DIF_REVISION_DATE
	public static String LAST_DIF_REVISION_DATE = "LastDIFRevisionDate";
	
	//LOCATION
	public static String LOCATION_CATEGORY = "Location_Category", //default column name
	LOCATION_TYPE = "Location_Keyword", 
	LOCATION_SUBREGION1 = "Location_Subregion1", 		//default column name
	LOCATION_SUBREGION2 = "Location_Subregion2", 		//default column name
	LOCATION_SUBREGION3 = "Location_Subregion3", 		//default column name
	DETAILED_LOCATION = "Detailed_Location", 
	DEFAULT_LOCATION_CATEGORY = "Geographic Region"; 	//default column name
	
	//METADATA_NAME
	public static String METADATA_NAME = "Metadata_Name", DEFAULT_METADATA_NAME = "CEOS IDN DIF"; 
	
	//METADATA_VERSION
	public static String METADATA_VERSION = "Metadata_Version", DEFAULT_METADATA_VERSION = "9.7"; 
	
	//PARAMETERS
	public static String DEFAULT_CATEGORY = "Earth Science",
	TOPIC = "Topic",
	TERM_VARIABLE_L1 = "Term>Variable_1";
	
	//PERSONNEL
	public static String ROLE = "Role", 
	FIRST_NAME = "First_Name", 
	MIDDLE_NAME = "Middle_Name", 
	LAST_NAME = "Last_Name", 
	EMAIL = "Email", 
	PHONE = "Telephone", 
	FAX = "Fax";
	
	//PROJECT
	public static String PROJECT_SHORT_NAME = "Project_Short_Name", 
	PROJECT_LONG_NAME = "Project_Long_Name";
	
	//SENSOR_NAME
	public static String SENSOR_NAME_SHORT_NAME = "Sensor_Short_Name", 
	SENSOR_NAME_LONG_NAME = "Sensor_Long_Name";
	
	//SOURCE_NAME
	public static String SOURCE_NAME_SHORT_NAME = "Satellite_Short_Name", 
	SOURCE_NAME_LONG_NAME = "Satellite_Long_Name";
	
	//SPATIAL_COVERAGE
	public static String SPATIAL_COVERAGE = "Geographic_Coverage";
	
	//SUMMARY
	public static String SUMMARY = "Summary"; 
	
	//TEMPORAL_COVERAGE
	public static String START_DATE = "StartDate", 
	STOP_DATE = "EndDate";
	
	
    //////////////////////////// Global Variables Set Up Section //////////////////////////
	/* This section is where all the global variables are set up for use in DIFExtractor 
	 * and MasterExtractor. Divided by major DIFField and arranged in alphabetical order*/
	
	//CONTACT_ADDRESS
	public static String CONTACT_ADDRESS = "Contact_Address";
	public static String[] CONTACT_ADDRESS_COLUMN_NAMES = {ADDRESS, CITY, 
			PROVINCE_OR_STATE, POSTAL_CODE, COUNTRY};
	public static String[] CONTACT_ADDRESS_ACTUAL_NAMES = {"Address", "City", 
			"Province_or_State", "Postal_Code", "Country"};
	
	//DATA_CENTER
	public static String DATA_CENTER = "Data_Center";
	public static String[] DATA_CENTER_COLUMN_NAMES= {URL, ID};
	public static String[] DATA_CENTER_ACTUAL_NAMES= {"Data_Center_URL", "Data_Set_ID"};
	
	//DATA_CENTER_NAME
	public static String DATA_CENTER_NAME = "Data_Center_Name";
	public static String[] DATA_CENTER_NAME_COLUMN_NAMES = {DATA_CENTER_SHORT_NAME, DATA_CENTER_LONG_NAME};
	public static String[] DATA_CENTER_NAME_ACTUAL_NAMES= {"Short_Name", "Long_Name"};
	
	//DATA_RESOLUTION
	public static String DATA_RESOLUTION = "Data_Resolution";
	public static String[] DATA_RESOLUTION_COLUMN_NAMES = {LATITUDE_RESOLUTION, LONGITUDE_RESOLUTION,
	HORIZONTAL_RESOLUTION_RANGE, TEMPORAL_RESOLUTION, TEMPORAL_RESOLUTION_RANGE};
	public static String[] DATA_RESOLUTION_ACTUAL_NAMES = {"Latitude_Resolution", "Longitude_Resolution",
	"Horizontal_Resolution_Range", "Temporal_Resolution", "Temporal_Resolution_Range"};
	
	//DATA_SET_CITATION
	public static String DATA_SET_CITATION = "Data_Set_Citation";
	public static String[] DATA_SET_CITATION_COLUMN_NAMES_PRODUCT_SHEET = {DATASET_TITLE, ONLINE_RESOURCE}, 
	DATA_SET_CITATION_COLUMN_NAMES_METADATA_REVISION_SHEET = {DATASET_CREATOR};
	public static String[] DATA_SET_CITATION_ACTUAL_NAMES_PRODUCT_SHEET ={"Dataset_Title", "Online_Resource"},
	DATA_SET_CITATION_ACTUAL_NAMES_METADATA_REVISION_SHEET ={"Dataset_Creator"};
	
	//DATA_SET_LANGUAGE
	public static String DATA_SET_LANGUAGE_ACTUAL_NAME = "Data_Set_Language";
	public static String[] DATA_SET_LANGUAGE_COLUMN_NAMES = {DATA_SET_LANGUAGE};
	
	//DIF_CREATION_DATE
	public static String DIF_CREATION_DATE_ACTUAL_NAME = "DIF_Creation_Date";
	public static String[] DIF_CREATION_DATE_COLUMN_NAMES = {DIF_CREATION_DATE};
	
	//ENTRY_ID
	public static String ENTRY_ID_ACTUAL_NAME = "Entry_ID";
	public static String[] ENTRY_ID_COLUMN_NAMES = {ENTRY_ID};
	
	//ENTRY_TITLE
	public static String ENTRY_TITLE_ACTUAL_NAME = "Entry_Title";
	public static String[] ENTRY_TITLE_COLUMN_NAMES = {ENTRY_TITLE};
	
	//IDN_NODE
	public static String IDN_NODE = "IDN_Node";
	public static String[] IDN_NODE_COLUMN_NAMES = {IDN_NODE_SHORT_NAME, IDN_NODE_LONG_NAME};
	public static String[] IDN_NODE_ACTUAL_NAMES= {"Short_Name", "Long_Name"};
	
	//ISO_TOPIC_CATEGORY
	public static String ISO_TOPIC_CATEGORY_ACTUAL_NAME = "ISO_Topic_Category";
	public static String[] ISO_TOPIC_CATEGORY_COLUMN_NAMES = {ISO_TOPIC_CATEGORY};
	
	//LAST_DIF_REVISION_DATE
	public static String LAST_DIF_REVISION_DATE_ACTUAL_NAME = "Last_DIF_Revision_Date";
	public static String[] LAST_DIF_REVISION_DATE_COLUMN_NAMES = {LAST_DIF_REVISION_DATE};
	
	//LOCATION
	public static String LOCATION = "Location";
	public static String[] LOCATION_COLUMN_NAMES = {LOCATION_CATEGORY, LOCATION_TYPE, 
			LOCATION_SUBREGION1,LOCATION_SUBREGION2, LOCATION_SUBREGION3, DETAILED_LOCATION };
	public static String[] LOCATION_ACTUAL_NAMES= {"Location_Category","Location_Type", 
			"Location_Subregion1", "Location_Subregion2", "Location_Subregion3", "Detailed_Location"};
	
	//METADATA_NAME
	public static String METADATA_NAME_ACTUAL_NAME = "Metadata_Name";
	public static String[] METADATA_NAME_COLUMN_NAMES = {METADATA_NAME};
	
	//METADATA_VERSION
	public static String METADATA_VERSION_ACTUAL_NAME = "Metadata_Version";
	public static String[] METADATA_VERSION_COLUMN_NAMES = {METADATA_VERSION};

	//PARAMETERS
	public static String CATEGORY = "Category";
	public static String PARAMETERS = "Parameters";
	public static String[] PARAMETERS_COLUMN_NAMES = {TOPIC, TERM_VARIABLE_L1};
	public static String[] PARAMETERS_ACTUAL_NAMES = {"Topic", "Term;Variable_Level_1"};

	//PERSONNEL
	public static String PERSONNEL = "Personnel";
	public static String[] PERSONNEL_COLUMN_NAMES= {ROLE, FIRST_NAME, MIDDLE_NAME, 
			LAST_NAME, EMAIL, PHONE, FAX};
	public static String[] PERSONNEL_ACTUAL_NAMES= {"Role", "First_Name", "Middle_Name", 
			"Last_Name", "Email", "Phone", "Fax"};

	//PROJECT
	public static String PROJECT = "Project";
	public static String[] PROJECT_COLUMN_NAMES = {PROJECT_SHORT_NAME, PROJECT_LONG_NAME};
	public static String[] PROJECT_ACTUAL_NAMES= {"Short_Name", "Long_Name"};
	
	//SENSOR_NAME
	public static String SENSOR_NAME = "Sensor_Name";
	public static String[] SENSOR_NAME_COLUMN_NAMES = {SENSOR_NAME_SHORT_NAME, SENSOR_NAME_LONG_NAME};
	public static String[] SENSOR_NAME_ACTUAL_NAMES= {"Short_Name", "Long_Name"};

	//SOURCE_NAME
	public static String SOURCE_NAME = "Source_Name";
	public static String[] SOURCE_NAME_COLUMN_NAMES = {SOURCE_NAME_SHORT_NAME, SOURCE_NAME_LONG_NAME};
	public static String[] SOURCE_NAME_ACTUAL_NAMES= {"Short_Name", "Long_Name"};

	//SPATIAL_COVERAGE
	public static String SPATIAL_COVERAGE_ACTUAL_NAME = "Spatial_Coverage";
	public static String[] SPATIAL_COVERAGE_COLUMN_NAMES = {SPATIAL_COVERAGE};
	public static String[] SPATIAL_COVERAGE_ACTUAL_NAMES = {"Southernmost_Latitude", 
			"Northernmost_Latitude", "Westernmost_Longitude", "Easternmost_Longitude"};

	//SUMMARY
	public static String SUMMARY_ACTUAL_NAME = "Summary";
	public static String[] SUMMARY_COLUMN_NAMES = {SUMMARY};

	//TEMPORAL_COVERAGE
	public static String TEMPORAL_COVERAGE = "Temporal_Coverage";
	public static String[] TEMPORAL_COVERAGE_COLUMN_NAMES = {START_DATE, STOP_DATE};
	public static String[] TEMPORAL_COVERAGE_ACTUAL_NAMES = {"Start_Date", "Stop_Date"};

	/**
	 * Inner class used to house specific possible <b>DIF 
	 * HORIZONTAL_RESOLUTION_RANGE</b> and <b>TEMPORAL_RESOLUTION_RANGE</b> 
	 * values and can determine whether or not a given number is 
	 * within its range 
	 **/
	public static class MType{
		private double min, max;
		private boolean excludeMin, includeMax;
		public String units, dif;
		
		/**
		 * Constructor
		 **/
		public MType(double min, double max, String units, String dif){
			this(min, max, units, false, false, dif);
		}

		/**
		 * Constructor
		 **/
		public MType(LinkedList<String> list){
			try{
				if(list.size() == 4){
					min = Double.parseDouble(list.get(0));
					max = Double.parseDouble(list.get(1));
					units = list.get(2);
					dif = list.get(3);
				}
				else if(list.size() == 6){
					min = Double.parseDouble(list.get(0));
					max = Double.parseDouble(list.get(1));
					units = list.get(2);
					excludeMin = Converter.isPositiveResponse(list.get(3));
					includeMax = Converter.isPositiveResponse(list.get(4));
					dif = list.get(5);
				}
				else{
					System.out.println("Bad range provided in global.txt");
				}
			}catch(NumberFormatException e){
				System.out.println("Bad number provided for minimum " +
				"and/or maximum for range values in global.txt");
			}
		}
		
		/**
		 * Constructor
		 **/
		public MType(double min, double max, String units, 
				boolean excludeMin, boolean includeMax, String dif){
			this.min = min;
			this.max = max;
			this.units = units;
			this.excludeMin = excludeMin;
			this.includeMax = includeMax;
			this.dif = dif;
		}

		/**
		 * Method to determine if a given number is in the 
		 * range determined by the minimum and maximum values 
		 * and also whether to include or exclude the minimum 
		 * and maximum values  
		 * @param number the number in question
		 * @return true if the number is within the range, false otherwise
		 **/
		public boolean inRange(double number){
			//minimum < number < maximum
			if(excludeMin && !includeMax){
				if(number > min && number < max)
					return true;
			}
			//minimum <= number < maximum
			else if (!excludeMin && !includeMax){
				if(number >= min && number < max)
					return true;
			}
			//minimum < number <= maximum
			else if (excludeMin && includeMax){
				if(number > min && number <= max)
					return true;
			}
			//minimum <= number <= maximum
			else if (!excludeMin && includeMax){
				if(number >= min && number <= max)
					return true;
			}
			//otherwise return false
			return false;
		}
		
		public String toString(){
			return "min " + min + " max " + max + " units " + units + " dif " + dif + "\n\n"; 
		}
	}
	
	/**
	 * Class used to house information to generate an error report of the
	 * main class of <code>DIFDriver</code> 
	 **/
	public static class Report{
		private String name, body, error, path;
		
		public Report(String name, String body, String error, String path){
			this.name = name;
			this.body = body;
			this.error = error;
			this.path = path;
		}
		
		public Report(String name){this(name, "", "", "");}
		public Report(String name, String path){this(name, "", "", path);}
		
		/**
		 * Method to add a <code>String</code> to the body of the report
		 * @param s the <code>String</code> to be added
		 **/
		public void addToBody(String s){body = body + s;}
		public void addSentenceToBody(String s){addToBody(s + "\n");}
		public void addSentenceToError(String s){error = error + s + "\n";}
		
		public String getPath(){return path;}
		public void setPath(String path){this.path = path;}
		
		public String toString(){
			if(name.equals("Main Report"))
				return "Report for " + path + "\n" + body;
			return "----------------------------------------------------------------------\n"
			+ "Individual Summary: " + name + "\n" +path + "\n\n" + error;
		}
	}
}


