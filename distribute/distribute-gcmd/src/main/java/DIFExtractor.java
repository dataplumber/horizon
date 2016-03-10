import java.util.LinkedList;

/**
 * Class responsible for the extract of a DIF field from the table passed in. 
 **/
public class DIFExtractor{
	/**The name of the DIFField**/
	protected String name;
	/**The list of records**/
	protected LinkedList<DIFRecord> records = new LinkedList<DIFRecord>();
	
	/**
	 * Default constructor
	 * @param name the of the DIFField
	 **/
	public DIFExtractor(String name){this.name = name;}
	
	/**
	 * Method to add the list of data to the DIFSubfield that is inputed.
	 * @param grandParent 	the DIFSubfield to add to
	 * @param childName 	the name of the child of the grandParent
	 * @param data 			the list of the data to be added
	 * @param unit 			the unit if necessary 
	 **/
	public void addAllToSubfield(DIFSubfield grandParent, String childName, LinkedList<String>data, String unit){
		for(String s : data){
			DIFField parent = new DIFField(name);
			if(unit != null)
				parent.add(new DIFSubfield(new DIFField(childName, s + " " + unit)));
			else
				parent.add(new DIFSubfield(new DIFField(childName, s)));
			grandParent.add(parent);
		}
	}
	
	/**
	 *  Extracts all data from the cell using the default delimiter.
	 *  @param table		the table in question
	 *  @param row			the row of the table
	 *  @param col			the column of the table
	 *  @return a LinkedList of the individual entries in the cell
	 **/
	public LinkedList<String> extractCell (Table table, int row, int col){
		return extractCell(table, row, col, ";");
	}
	
	/**
	 *  Extracts all data from the cell using the passed in delimiter.
	 *  @param table		the table in question
	 *  @param row			the row of the table
	 *  @param col			the column of the table
	 *  @param delimiter	the delimiter to use
	 *  @return a LinkedList of the individual entries in the cell
	 **/
	public LinkedList<String> extractCell (Table table, int row, int col, String delimiter){
		//the cell is empty, return null
		if(isEmptyCell(row, col, table))
			return null;
		//get the data converting illegal characters
		String data = Converter.replaceIllegalChars(table.get(row, col));
		//return the separated cell
		return Converter.separate(data, delimiter, new LinkedList<String>());
	}
	
	/**
	 * Extracts all product data from the table using a set of 
	 * column names and only one dif field name
	 * 
	 * @param table			the table in question
	 * @param colNames		all the column names of the field in question
	 * @return a list of DIFRecords
	 **/
	public LinkedList<DIFRecord> extractData(Table table, String[] colNames){    
		for(String colName : colNames)
			for(int row = Global.FIRST_PRODUCT_ROW; row < table.rows(); row++)
				records.add(new DIFRecord(extractData(table, row, name, colName)));
		return records;
	}	

	/**
	 * Extracts all product data from the table using a set of 
	 * column names and dif field names
	 * 
	 * @param table			the table in question
	 * @param names			all the dif subfield names in question
	 * @param colNames		all the column names of the field in question
	 * @return a list of DIFRecords
	 **/
	public LinkedList<DIFRecord> extractData(Table table, String[] names, String[] colNames){    
		for(int row = Global.FIRST_PRODUCT_ROW; row < table.rows(); row++)
			records.add(new DIFRecord(new DIFSubfield(extractData(table, row, names, colNames))));
		return records;
	}

	/**
	 * Extracts data from one row
	 * @param table 		the table in question
	 * @param row			the row in question
	 * @param names 		all the dif subfield names in question
	 * @param colNames 		all the column names of the field in question
	 * @return a DIFField
	 */
	public DIFField extractData(Table table, int row, String[] names, String[] colNames){    
		DIFField parent = new DIFField(name);
		for(int i = 0; i < colNames.length; i++){
			try{
				DIFSubfield child = extractData(table, row, names[i], colNames[i]);
				parent.add(child);
			}catch(ArrayIndexOutOfBoundsException ae){}
		}
		return parent;
	}

	/**
	 * Extracts data from one row and one column
	 * @param table 		the table in question
	 * @param row			the row in question
	 * @param name 			all the dif subfield names in question
	 * @param colName 		all the column names of the field in question
	 * @return a DIFSubfield
	 */
	public DIFSubfield extractData(Table table, int row, String name, String colName){    
		DIFSubfield child = new DIFSubfield(name);
		int col = table.findColumn(colName);
		
		//If does not exist, check if default category is provided
		if(col == -1){
			if(colName.equals(Global.LOCATION_CATEGORY))
				child.add(new DIFField(name, Global.DEFAULT_LOCATION_CATEGORY));
			if(colName.equals(Global.METADATA_NAME))
				child.add(new DIFField(name, Global.DEFAULT_METADATA_NAME));
			if(colName.equals(Global.METADATA_VERSION))
				child.add(new DIFField(name, Global.DEFAULT_METADATA_VERSION));
			if(colName.equals(Global.DIF_CREATION_DATE))
				child.add(new DIFField(name, Converter.currentDate()));	
			if(colName.equals(Global.LAST_DIF_REVISION_DATE))
				child.add(new DIFField(name, Converter.currentDate()));	
			if(colName.equals(Global.IDN_NODE_SHORT_NAME))
				child.add(new DIFField(name, Global.DEFAULT_IDN_NODE_SHORT_NAME));
			if(colName.equals(Global.DATA_SET_LANGUAGE))
				child.add(new DIFField(name, Global.DEFAULT_DATA_SET_LANGUAGE));
		}
		
		//check if the cell is empty
		if(!isEmptyCell(row, col, table)){
			String data = Converter.replaceIllegalChars(table.get(row, col));
			//manual hacks begin
			if(name.equals("Postal_Code"))
				data = data.replace(".0", "");
			//manual hacks end
			
		   if(data.contains(";") && !name.equals(Global.SUMMARY_ACTUAL_NAME) && !this.name.equals(Global.CONTACT_ADDRESS)){
				LinkedList<String> entries = Converter.separate(data, new LinkedList<String>());
				for(String s : entries)
					child.add(new DIFField(name, s));
		   }
		   else if(data.contains(",") && this.name.equals(Global.CONTACT_ADDRESS)){
				LinkedList<String> entries = Converter.separate(data, ",", new LinkedList<String>());
				for(String s : entries)
					child.add(new DIFField(name, s));
		   }
			else{
				//If the data is a date YYYYMMDD convert it to YYYY-MM-DD
				if(this.name.equals(Global.TEMPORAL_COVERAGE) || this.name.equals(Global.LAST_DIF_REVISION_DATE_ACTUAL_NAME))
					data = Converter.changeDate(data.trim());
				//If the data is a title convert it
				if(this.name.equals(Global.ENTRY_ID_ACTUAL_NAME))
					data = Converter.convertID(data.trim());
				//If the data is a state (California) make sure it is abbreviated
				if(name.equals("Province_or_State"))
					if(data.equalsIgnoreCase("California") || data.equalsIgnoreCase("ca"))
						data = "CA";
				child.add(new DIFField(name, data));
			}
		}
		return child;
	}

	/**
	 * Extracts data from a table by making multiple passes through 
	 * the same row, because the fields occur in pairs
	 * @param table 		the table in question
	 * @param names 		all the dif subfield names in question
	 * @param colNames 		all the column names of the field in question
	 * @return a LinkedList of DIFREcords.
	 */
	public LinkedList<DIFRecord> extractDataAcross(Table table, String[] names, String[] colNames){    
		for(int row = Global.FIRST_PRODUCT_ROW; row < table.rows(); row++)
			records.add(new DIFRecord(extractDataAcross(table, row, names, colNames)));
		return records;
	}

	/**
	 * Extracts data from a particular row by making multiple passes through 
	 * the same row, because the fields occur in pairs
	 * @param table 		the table in question
	 * @param row			the row in question
	 * @param names 		all the dif subfield names in question
	 * @param colNames 		all the column names of the field in question
	 * @return a LinkedList of DIFREcords.
	 */
	public DIFSubfield extractDataAcross(Table table, int row, String[] names, String[] colNames){    
		DIFSubfield grandParent = new DIFSubfield(name);
		boolean go = true;
		while(go){
			DIFField parent = new DIFField(name);
			go = false;
			for(int i = 0; i < names.length; i++){
				int col = table.findColumn(colNames[i]);
				if(!isEmptyCell(row, col, table)){
					String data = Converter.replaceIllegalChars(table.get(row, col));					
					if(data.contains(";")){
						go = true;
						String[] temp = next(data, ';');
						String nextData = temp[0];
						table.set(row, col, temp[1]);
						parent.add(new DIFSubfield(new DIFField(names[i], nextData)));
					}
					else
						parent.add(new DIFSubfield(new DIFField(names[i], data)));
				}
			}
			grandParent.add(parent);
		}
		return grandParent;
	}
	
	
	/**
	 * Finds the index of the item in question in the 
	 * list.
	 * @param list				the list to look at
	 * @param item				the item to find the index of
	 * @return the index of the item or -1 if not found
	 */
	public int find(String[]list, String item){
		return find(0, list, item);
	}
	
	/**
	 * Finds the index of the item in question in the 
	 * list from the starting index
	 * @param startingIndex		the index to start at
	 * @param list				the list to look at
	 * @param item				the item to find the index of
	 * @return	the index of the item or -1 if not found
	 */
	public int find(int startingIndex, String[]list, String item){
		for(int i = startingIndex; i < list.length; i++)
			if(list[i].equals(item))
				return i;
		return -1;
	}
	
	/**
	 * Determines whether the cell is empty or not.
	 * @param row			row of the table
	 * @param col			column of the table	
	 * @param table			the table
	 * @return true if the column number is less than 0 or the cell is empty
	 */
	public boolean isEmptyCell(int row, int col, Table table){
		if((col < 0) || (table.isEmpty(row, col)))
			return true;
		return false;
	}

	/**
	 * Merges new DIFRecords with existing DIFRecords so that only 
	 * one DIFRecord is generated for each row. New records are 
	 * merged to the front
	 * @param originalRecords	the original DIFRecords to be merged into
	 * @param parentName		the parent DIF field name
	 * @param newRecords		the new DIFRecords to be merged in
	 * @return the modified original DIFRecords list
	 */
	public static LinkedList<DIFRecord> mergeNewRecordsToFront(LinkedList<DIFRecord> originalRecords, 
			String parentName, LinkedList<DIFRecord> newRecords){
		for(int i = 0; i < originalRecords.size(); i++){
			DIFRecord origRec = originalRecords.get(i);
			DIFRecord newRec = newRecords.get(i);
			DIFSubfield parent = origRec.find(parentName);
			for(int j = 0; j < parent.getMembers().size(); j++)
				parent.getMembers().get(j).add(0, newRec.getSubfields().get(j));
		}
		return originalRecords;
	}
	
	/**
	 * Merges new DIFRecords with existing DIFRecords so that only 
	 * one DIFRecord is generated for each row.
	 * @param originalRecords	the original DIFRecords to be merged into
	 * @param parentName		the parent DIF field name
	 * @param newRecords		the new DIFRecords to be merged in
	 * @return the modified original DIFRecords list
	 */
	public static LinkedList<DIFRecord> mergeRecords(LinkedList<DIFRecord> originalRecords, 
			String parentName, LinkedList<DIFRecord> newRecords){
		for(int i = 0; i < originalRecords.size(); i++){
			DIFRecord origRec = originalRecords.get(i);
			DIFRecord newRec = newRecords.get(i);
			DIFSubfield parent = origRec.find(parentName);
			for(int j = 0; j < parent.getMembers().size(); j++)
				parent.getMembers().get(j).add(newRec.getSubfields().get(j));
		}
		return originalRecords;
	}
	
	/**
	 * Merges new DIFRecords with existing DIFRecords so that only 
	 * one DIFRecord is generated for each row but merged at the same level
	 * instead of as a subfield of the the original record
	 * @param originalRecords	the original DIFRecords to be merged into
	 * @param parentName		the parent DIF field name
	 * @param newRecords		the new DIFRecords to be merged in
	 * @return the modified original DIFRecords list
	 */
	public static LinkedList<DIFRecord> mergeRecordsSameLevel(LinkedList<DIFRecord> originalRecords, 
			String parentName, LinkedList<DIFRecord> newRecords){
		for(int i = 0; i < originalRecords.size(); i++){
			DIFRecord origRec = originalRecords.get(i);
			DIFRecord newRec = newRecords.get(i);
			DIFSubfield parent = origRec.find(parentName);
			DIFSubfield newParent = newRec.find(parentName);
			for(int j = 0; j < parent.getMembers().size(); j++)
				parent.getMembers().get(j).add(newParent.getMembers().get(j).getSubfields());
		}
		return originalRecords;
	}
	
	/**
	 * Retrieves the next entry in a cell delimited by a delimiter
	 * @param s				the String from the cell
	 * @param delimiter		the delimiter
	 * @return the next entry in a cell delimited by a delimiter
	 */
	public static String[] next(String s, char delimiter){
		String[] retArray = new String[2];
		int index = s.indexOf(delimiter);
		if(index < 0){
			retArray[0] = s.trim();
			retArray[1] = "";
		}
		else{
			String ret = s.substring(0, index);
			s = s.substring(index + 1, s.length());
			retArray[0] = ret.trim();
			retArray[1] = s.trim();
		}
		return retArray;	
	}
}
