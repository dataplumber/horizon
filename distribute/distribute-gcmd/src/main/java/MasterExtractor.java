import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

class MasterExtractor{
	/**An ExcelReader for reading the excel**/
	private ExcelReader reader;
	
	/**LinkedList containing all of the tables, thus representing an entire workbook**/
	private LinkedList<Table> allTables;
	
	/**All the individual tables to represent each sheet**/
	private Table productTable, spatialTable, temporalTable, 
	summaryTable, personnelTable, dataCenterTable, parameterTable,
	metadataRevisionTable, sensorTable, projectsTable;
	
	/**LinkedList containing all**/
	private LinkedList<DIFRecord> allRecords;
	
	/**
	 * Constructor
	 * @param filepath the path to the excel file
	 * @throws FileNotFoundException
	 * @throws IOException
	 **/
	public MasterExtractor(String filepath) throws FileNotFoundException, IOException{    
		reader = new ExcelReader(filepath);	
		allTables = reader.getAllTables();
		
		
		productTable = reader.get(Global.PRODUCT_SHEET);
		
		 // find the empty product rows
		LinkedList<Integer> emptyRows = removeEmptyRows(productTable);
		
		//remove the empty product rows from the tables
		for(Table t : allTables)
			if(!t.equals(productTable))
				for(int row : emptyRows)
					t.removeRow(row);
		
		//set up the individual sheets
		spatialTable = reader.get(Global.SPATIAL_SHEET);
		temporalTable = reader.get(Global.TEMPORAL_SHEET);
		summaryTable = reader.get(Global.SUMMARY_SHEET);
		personnelTable = reader.get(Global.PERSONNEL_SHEET);
		dataCenterTable = reader.get(Global.DATA_CENTER_SHEET);
		parameterTable = reader.get(Global.PARAMETER_SHEET);
		metadataRevisionTable = reader.get(Global.METADATA_REVISION_SHEET);
		sensorTable = reader.get(Global.SENSOR_SHEET);
		projectsTable = reader.get(Global.PROJECTS_SHEET);
		
		allRecords = new LinkedList<DIFRecord>();
		
		//set up the list of all the records
		for(int i = 0; i < productTable.rows()- Global.FIRST_PRODUCT_ROW; i++)
			allRecords.add(new DIFRecord());
	}
	
	/**
	 * Constructor that excludes products whose numbers are inputed
	 * @param filepath the path to the excel file
	 * @param productNumbers the list of productNumbers to exclude
	 * @throws FileNotFoundException
	 * @throws IOException
	 **/
	public MasterExtractor(String filepath, LinkedList<String>productNumbers, char flag) throws FileNotFoundException, IOException{    
		this(filepath);
		if(productNumbers != null){
			LinkedList<Integer>rows = new LinkedList<Integer>();
			int col = productTable.findColumn(Global.PRODUCT_NUMBER);
			
			//exclude
			if(flag == 'e'){
				for(int row = Global.FIRST_PRODUCT_ROW; row < productTable.rows(); row++){
					for(String s : productNumbers){
						if(productTable.get(row, col).replace(".0", "").trim().equals(s.trim())){
							rows.add(row);
							continue;
						}
					}
				}	
			}

			//include
			if(flag == 'i'){
				for(int row = Global.FIRST_PRODUCT_ROW; row < productTable.rows(); row++){
					boolean remove = true;
					for(String s : productNumbers){
						if(productTable.get(row, col).replace(".0", "").trim().equals(s.trim())){
							remove = false;
						}
					}
					if(remove){
						rows.add(row);
					}
				}
			}

			for(Table t : allTables)
				for(int i = rows.size()-1; i >= 0; i--)
					t.removeRow(rows.get(i));

			for(int i = 0; i < rows.size(); i++)
				allRecords.remove();
		}
	}
	
	/**
	 * @return a LinkedList of all the records
	 **/
	public LinkedList<DIFRecord> getAllRecords(){return allRecords;}
	
	/**
	 * Method to remove empty rows
	 * @param table table to remove rows from
	 **/
	public LinkedList<Integer> removeEmptyRows(Table table){
		int r = Global.FIRST_PRODUCT_ROW + 1;
		LinkedList<Integer> ret = new LinkedList<Integer> ();
		while(r < table.rows()){
			//assume that the row is empty until proven wrong
			boolean empty = true;
			//go through all of the columns and try to find one that is not empty
			for(int c = 0; c < table.columns(r); c++)
				//if the cell contains something
				if(!Converter.isEmpty(table.get(r,c)))
					empty = false;
			//if the row is empty and do not increment row
			if(empty)
				ret.add(table.removeRow(r));
				
			//if the row is not empty, increment the row
			else r++;
		}
		return ret;
	}
	
	/**
	 * Method to add the newly generate DIF fields to the appropriate DIFRecord
	 * @param newRecords the records containing the new fields to add 
	 **/
	public void addNewFields(LinkedList<DIFRecord> newRecords){
		for(int i = 0; i < allRecords.size(); i++)
			for(int j = 0; j < newRecords.get(i).getSubfields().size(); j++){
				allRecords.get(i).addSubfields(newRecords.get(i).getSubfields());
				if(!Converter.isEmpty(newRecords.get(i).getName()) && Converter.isEmpty(allRecords.get(i).getName()))
					allRecords.get(i).setName(newRecords.get(i).getName());
			}
	}
	
	/**
	 * Method to extract Data Center field 
	 **/
	public void extractDataCenter(){
		//extract the first component of Personnel, Contact Address
		LinkedList<DIFRecord> contactAddress = 
			new DIFExtractor(Global.CONTACT_ADDRESS).extractData(personnelTable, 
					Global.CONTACT_ADDRESS_ACTUAL_NAMES, Global.CONTACT_ADDRESS_COLUMN_NAMES); 
		//extract the rest of the Personnel fields and merge them with those retrieved for Contact Address
		LinkedList<DIFRecord> personnel = 
			DIFExtractor.mergeRecords(
					new DIFExtractor(Global.PERSONNEL).extractData(personnelTable, 
							Global.PERSONNEL_ACTUAL_NAMES, Global.PERSONNEL_COLUMN_NAMES), Global.PERSONNEL , contactAddress);
		//extract a component of Data Center, Data Center Name
		LinkedList<DIFRecord> dataCenterName = 
			new DIFExtractor(Global.DATA_CENTER_NAME).extractData(dataCenterTable, 
					Global.DATA_CENTER_NAME_ACTUAL_NAMES, Global.DATA_CENTER_NAME_COLUMN_NAMES);
		//extract the rest of fields of Data Center
		LinkedList<DIFRecord> dataCenter = 
			new DIFExtractor(Global.DATA_CENTER).extractData(dataCenterTable, 
					Global.DATA_CENTER_ACTUAL_NAMES, Global.DATA_CENTER_COLUMN_NAMES);
		//merge Data Center and Data Center Name
		LinkedList<DIFRecord> dataCenterAndDataCenterName = 
			DIFExtractor.mergeNewRecordsToFront(dataCenter, Global.DATA_CENTER, dataCenterName);
		//merge the Personnel with the rest of Data Center
		LinkedList <DIFRecord> dataCenterAndDataCenterNameAndPersonnel = 
			DIFExtractor.mergeRecords(dataCenterAndDataCenterName, Global.DATA_CENTER, personnel);
		addNewFields(dataCenterAndDataCenterNameAndPersonnel);
	}
	
	/**
	 * Method to extract Data Resolution field 
	 **/
	public void extractDataResolution(){
		addNewFields(
				new DIFDataResolutionExtractor().extractDataResolutionData(spatialTable, 
						temporalTable, Global.DATA_RESOLUTION_ACTUAL_NAMES, Global.DATA_RESOLUTION_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Data Citation field 
	 **/
	public void extractDataSetCitation(){
		//extract the first component of Personnel, Contact Address
		LinkedList<DIFRecord> metaRevisionSheetFields = 
			new DIFExtractor(Global.DATA_SET_CITATION).extractData(metadataRevisionTable, 
					Global.DATA_SET_CITATION_ACTUAL_NAMES_METADATA_REVISION_SHEET, Global.DATA_SET_CITATION_COLUMN_NAMES_METADATA_REVISION_SHEET); 
		//extract the rest of the Personnel fields and merge them with those retrieved for Contact Address
		LinkedList<DIFRecord> metaRevisionAndProductSheetsFields = 
			DIFExtractor.mergeRecordsSameLevel(metaRevisionSheetFields, 
					Global.DATA_SET_CITATION , new DIFExtractor(Global.DATA_SET_CITATION).extractData(productTable, 
							Global.DATA_SET_CITATION_ACTUAL_NAMES_PRODUCT_SHEET, Global.DATA_SET_CITATION_COLUMN_NAMES_PRODUCT_SHEET));
		addNewFields(metaRevisionAndProductSheetsFields);
	}
	
	/**
	 * Method to extract DataSetLanguage field 
	 **/
	public void extractDataSetLanguage(){
		addNewFields(
				new DIFExtractor(Global.DATA_SET_LANGUAGE_ACTUAL_NAME).extractData(metadataRevisionTable, 
						Global.DATA_SET_LANGUAGE_COLUMN_NAMES));
	}
	
	
	/**
	 * Method to extract DIF Creation Date field 
	 **/
	public void extractDIFCreationDate(){
		addNewFields(
				new DIFExtractor(Global.DIF_CREATION_DATE_ACTUAL_NAME).extractData(metadataRevisionTable, 
						Global.DIF_CREATION_DATE_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Last DIF Revision Date field 
	 **/
	public void extractLastDIFRevisionDate(){
		addNewFields(
				new DIFExtractor(Global.LAST_DIF_REVISION_DATE_ACTUAL_NAME).extractData(metadataRevisionTable, 
						Global.LAST_DIF_REVISION_DATE_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Entry ID field 
	 **/
	public void extractEntryID(){
		addNewFields(
				new DIFExtractor(Global.ENTRY_ID_ACTUAL_NAME).extractData(productTable, 
						Global.ENTRY_ID_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Entry Title field 
	 **/
	public void extractEntryTitle(){
		addNewFields(
				new DIFExtractor(Global.ENTRY_TITLE_ACTUAL_NAME).extractData(productTable, 
						Global.ENTRY_TITLE_COLUMN_NAMES));
	}
	
	/**
	 * Extracts IDN_Node field. If it does not actually exist, a default value is used. 
	 **/
	public void extractIDNNode(){
		addNewFields(
				new DIFExtractor(Global.IDN_NODE).extractData(projectsTable, 
						Global.IDN_NODE_ACTUAL_NAMES, Global.IDN_NODE_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract ISO Topic Category field 
	 **/
	public void extractISOTopicCategory(){
		addNewFields(
				new DIFExtractor(Global.ISO_TOPIC_CATEGORY).extractData(parameterTable, 
						Global.ISO_TOPIC_CATEGORY_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Location field 
	 **/
	public void extractLocation(){
		addNewFields(
				new DIFExtractor(Global.LOCATION).extractData(spatialTable, 
						Global.LOCATION_ACTUAL_NAMES, Global.LOCATION_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Metadata Name field 
	 **/
	public void extractMetadataName(){
		addNewFields(
				new DIFExtractor(Global.METADATA_NAME_ACTUAL_NAME).extractData(metadataRevisionTable, Global.METADATA_NAME_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Metadata Version field 
	 **/
	public void extractMetadataVersion(){
		addNewFields(
				new DIFExtractor(Global.METADATA_VERSION_ACTUAL_NAME).extractData(metadataRevisionTable, Global.METADATA_VERSION_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Parameters field 
	 **/
	public void extractParameters(){
		addNewFields(
				new DIFParametersExtractor().extractParametersData(parameterTable, 
						Global.PARAMETERS_ACTUAL_NAMES, Global.PARAMETERS_COLUMN_NAMES)); 
	}
	
	/**
	 * Method to extract Personnel field 
	 **/
	public void extractPersonnel(){
		//extract the first component of Personnel, Contact Address
		LinkedList<DIFRecord> contactAddress = 
			new DIFExtractor(Global.CONTACT_ADDRESS).extractData(personnelTable, 
					Global.CONTACT_ADDRESS_ACTUAL_NAMES, Global.CONTACT_ADDRESS_COLUMN_NAMES); 
		//extract the rest of the Personnel fields and merge them with those retrieved for Contact Address
		addNewFields(DIFExtractor.mergeRecords(
				new DIFExtractor(Global.PERSONNEL).extractData(personnelTable, 
						Global.PERSONNEL_ACTUAL_NAMES, Global.PERSONNEL_COLUMN_NAMES), Global.PERSONNEL , contactAddress));
	}

	/**
	 * Method to extract Project field 
	 **/
	public void extractProject(){
		addNewFields(
				new DIFExtractor(Global.PROJECT).extractDataAcross(projectsTable, 
						Global.PROJECT_ACTUAL_NAMES, Global.PROJECT_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Spatial Coverage field 
	 **/
	public void extractSpatialCoverage(){
		addNewFields(
				new DIFSpatialCoverageExtractor().extractSpatialCoverageData(spatialTable, 
						Global.SPATIAL_COVERAGE_ACTUAL_NAMES, Global.SPATIAL_COVERAGE_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Sensor Name field 
	 **/
	public void extractSensorName(){
		addNewFields(
				new DIFExtractor(Global.SENSOR_NAME).extractDataAcross(sensorTable, 
						Global.SENSOR_NAME_ACTUAL_NAMES, Global.SENSOR_NAME_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Source Name/Platform field 
	 **/
	public void extractSourceName(){
		addNewFields(
				new DIFExtractor(Global.SOURCE_NAME).extractDataAcross(sensorTable, 
						Global.SOURCE_NAME_ACTUAL_NAMES, Global.SOURCE_NAME_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Summary field 
	 **/
	public void extractSummary(){
		addNewFields(
				new DIFExtractor(Global.SUMMARY_ACTUAL_NAME).extractData(summaryTable, 
						Global.SUMMARY_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract Temporal Coverage field 
	 **/
	public void extractTemporalCoverage(){
		addNewFields(
				new DIFExtractor(Global.TEMPORAL_COVERAGE).extractData(temporalTable, 
						Global.TEMPORAL_COVERAGE_ACTUAL_NAMES, Global.TEMPORAL_COVERAGE_COLUMN_NAMES));
	}
	
	/**
	 * Method to extract all "handled" fields from the excel sheet
	 **/
	public void extractAll(){
		extractEntryID();
		extractEntryTitle();
		extractDataSetCitation();
		extractPersonnel();
		extractParameters();
		extractISOTopicCategory();
		extractSensorName();
		extractSourceName();
		extractTemporalCoverage();
		extractSpatialCoverage();
		extractLocation();
		extractDataResolution();
		extractProject();
		extractDataSetLanguage();
		extractDataCenter();
		extractSummary();
		extractIDNNode();
		extractMetadataName();
		extractMetadataVersion();
		extractDIFCreationDate();
		extractLastDIFRevisionDate();
	}

	/**
	 * @return a String representation the MasterExtractor 
	 **/
	public String toString(){
		String ret = "";
		for(int i = 0; i < allRecords.size(); i++)
			ret = ret + allRecords.get(i);
		return ret;
	}

	/**
	 * @return a String that created by calling the method count() on all of the records
	 **/
	public String count(){
		String ret = "";
		for(DIFRecord rec : allRecords)
			ret = ret + rec.count() + "\n";
		return ret;
	}
}
