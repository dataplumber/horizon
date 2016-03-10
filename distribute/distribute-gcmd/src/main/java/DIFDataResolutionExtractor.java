import java.util.LinkedList;

/**
 * Class to extract the Data_Resolution DIF field
 * @author Dawn Finney
 **/
public class DIFDataResolutionExtractor extends DIFExtractor {
	
	/**
	 * Constructor
	 **/
	public DIFDataResolutionExtractor(){super(Global.DATA_RESOLUTION);}

	/**
	 * Method to extract all DATA_RESOLUTION data from all products
	 * @param spatialTable 		the spatial table
	 * @param temporalTable 	the temporal table
	 * @param names 			the array of actual names
	 * @param colNames 			the array of the column names
	 * @return a LinkedList of DIFRecords representing all of the products
	 **/
	public LinkedList<DIFRecord> extractDataResolutionData(Table spatialTable, 
			Table temporalTable, String[] names, String[] colNames){    
		for(int row = Global.FIRST_PRODUCT_ROW; row < spatialTable.rows(); row++)
			records.add(new DIFRecord(extractDataResolutionData(spatialTable, 
					temporalTable, row, names, colNames)));
		return records;
	}
	
	/**
	 * Method to extract the DATA_RESULUTION data from one product (one row) 
	 * @param spatialTable 		the spatial table
	 * @param temporalTable 	the temporal table
	 * @param row 				the current row
	 * @param names 			the array of actual names
	 * @param colNames 			the array of the column names
	 * @return a DIFSubfield for the DIFRecord
	 **/
	private DIFSubfield extractDataResolutionData(Table spatialTable, Table temporalTable, 
			int row, String[] names, String[] colNames){ 

		//find the specific indices for each component of DATA_RESOLUTION
		int latResIndex = find(colNames, Global.LATITUDE_RESOLUTION);
		int longResIndex = find(colNames, Global.LONGITUDE_RESOLUTION);
		int horResRangeIndex = find(colNames, Global.HORIZONTAL_RESOLUTION_RANGE);
		int tempResIndex = find(colNames, Global.TEMPORAL_RESOLUTION);
		int tempResRangeIndex = find(horResRangeIndex + 1, colNames, Global.TEMPORAL_RESOLUTION_RANGE);
		
		//initialize all columns variables to -1
		int latResCol = -1, longResCol = -1, horResRangeCol = -1, 
		tempResCol = -1, tempResRangeCol = -1;

		//if the index exists, find the corresponding columns on the table
		if(latResIndex != -1) latResCol = spatialTable.findColumn(colNames[latResIndex]);
		if(longResIndex != -1) longResCol = spatialTable.findColumn(colNames[longResIndex]);
		if(horResRangeIndex != -1) horResRangeCol = spatialTable.findColumn(colNames[horResRangeIndex]);
		if(tempResIndex!=-1) tempResCol = temporalTable.findColumn(colNames[tempResIndex]);
		if(tempResRangeIndex!=-1) tempResRangeCol = temporalTable.findColumn(colNames[tempResRangeIndex]);

		//extract the data from the cell and break it up based on a delimiter which defaults to comma
		LinkedList<String> latResData = extractCell(spatialTable, row, latResCol);
		LinkedList<String> longResData = extractCell(spatialTable, row, longResCol);
		//can change this portion to generate HORIZONTAL_RESOLUTION_RANGE data or to directly extract from a column		
		LinkedList<String> horResRangeData = extractCell(spatialTable, row, horResRangeCol);
		if(horResRangeData != null && horResRangeData.size() == 1 && !horResRangeData.get(0).contains("<"))
			horResRangeData= Converter.convertHorizontalResolutionRange(horResRangeData.get(0));
		LinkedList<String> tempResData = extractCell(temporalTable, row, tempResCol);
		//can change this portion to generate TEMPORAL_RESOLUTION_RANGE data or to directly extract from a column
		LinkedList<String> tempResRangeData = extractCell(temporalTable, row, tempResRangeCol);
		if(tempResRangeData != null && tempResRangeData.size() == 1 && !tempResRangeData.get(0).contains("<"))
			tempResRangeData = Converter.convertTemporalResolutionRange(tempResRangeData.get(0));
		
		DIFSubfield grandParent = new DIFSubfield(name);
		
		//LATITUDE_RESOLUTION & LONGITUDE_RESOLUTION & VERTICAL_RESOLUTION
		//Assumed that all three will have the same amount of entries
		if(latResData != null || longResData != null){

			//At least LATITUDE_RESOLUTION
			if(latResData!= null){
				for(int i = 0; i < latResData.size(); i++){
					DIFField parent = new DIFField(name);
					//add the LATITUDE_RESOLUTION data and its units
					parent.add(new DIFSubfield(new DIFField(names[latResIndex], latResData.get(i) + " " + spatialTable.get(Global.FIRST_PRODUCT_ROW - 1, latResCol).trim())));
					//if there is LONGITUDE_RESOLUTION data then add it
					if(longResData != null)
						parent.add(new DIFSubfield(new DIFField(names[longResIndex], longResData.get(i) + " " + spatialTable.get(Global.FIRST_PRODUCT_ROW - 1, longResCol).trim())));
					grandParent.add(parent);
				}
			}
			//At least LONGITUDE_RESOLUTION and no LATITUDE_RESOLUTION
			else if (longResData != null){
				for(int i = 0; i < longResData.size(); i++){
					DIFField parent = new DIFField(name);
					//add the LONGITUDE_RESOLUTION data and its units
					parent.add(new DIFSubfield(new DIFField(names[longResIndex], longResData.get(i)) + " " + spatialTable.get(Global.FIRST_PRODUCT_ROW - 1, longResCol).trim()));
					grandParent.add(parent);
				}
			}
		}

		/* HORIZONTAL_RESOLUTION_RANGE
		 * If there are already entries in the subfield, then we have to clone 
		 * the existing entries to accommodate the new HORIZONTAL_RESOLUTION 
		 * data because all of the subfields of DATA_RESOLUTION can only 
		 * exist once*/
		if(horResRangeData != null){
			if(grandParent.size()!= 0){
				DIFSubfield realGrandParent = new DIFSubfield(name);
				for(String s : horResRangeData){
					for(DIFField parent : grandParent.getMembers()){
						DIFField parentClone = parent.copy();
						parentClone.add(new DIFSubfield(new DIFField(names[horResRangeIndex], s.replaceAll("<", "&lt;"))));
						realGrandParent.add(parentClone);
					}
				}
				grandParent = realGrandParent;
			}
			//First subfield in the list, no need to clone
			else
				addAllToSubfield(grandParent, names[horResRangeIndex], horResRangeData, null);
		}

		
		/* TEMPORAL_RESOLUTION
		 * If there are already entries in the subfield, then we have to clone 
		 * the existing entries to accommodate the new TEMPORAL_RESOLUTION 
		 * data because all of the subfields of DATA_RESOLUTION can only 
		 * exist once*/
		if(tempResData != null){
			if(grandParent.size()!= 0){
				DIFSubfield realGrandParent = new DIFSubfield(name);
				for(String s : tempResData){
					for(DIFField parent : grandParent.getMembers()){
						DIFField parentClone = parent.copy();
						s = s.replace(".0", "");
						parentClone.add(new DIFSubfield(new DIFField(names[tempResIndex], s.replaceAll("<", "&lt;") + " " + temporalTable.get(Global.FIRST_PRODUCT_ROW - 1, tempResCol).trim())));
						realGrandParent.add(parentClone);
					}
				}
				grandParent = realGrandParent;
			}
			//First subfield in the list, no need to clone
			else
				addAllToSubfield(grandParent, names[tempResIndex], horResRangeData, temporalTable.get(Global.FIRST_PRODUCT_ROW - 1, tempResCol).trim());
		}
		
		//TEMPORAL_RESOLUTION_RANGE
		//Must clone
		if(tempResRangeData != null){
			if(grandParent.size()!= 0){
				DIFSubfield realGrandParent = new DIFSubfield(name);
				for(String s : tempResRangeData){
					for(DIFField parent : grandParent.getMembers()){
						DIFField parentClone = parent.copy();
						parentClone.add(new DIFSubfield(new DIFField(names[tempResRangeIndex], s.replaceAll("<", "&lt;"))));
						realGrandParent.add(parentClone);
					}
				}
				grandParent = realGrandParent;
			}
			//First subfield in the list, no need to clone
			else
				addAllToSubfield(grandParent, names[tempResRangeIndex], tempResRangeData, null);
		}

		//Rearrange the fields in the correct order
		for(DIFField member : grandParent.getMembers())
			member.toOrder(names);
		return grandParent;
	}
	
}
