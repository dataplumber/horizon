import java.util.LinkedList;


public class DIFParametersExtractor extends DIFExtractor {
	public DIFParametersExtractor(){
		super(Global.PARAMETERS);
	}
	
	public LinkedList<DIFRecord> extractParametersData(Table table, String[] names, String[] colNames){    
		for(int row = Global.FIRST_PRODUCT_ROW; row < table.rows(); row++){
			DIFRecord record = new DIFRecord();
			boolean go = true;
			while(go){
				go = false;
				DIFField parent = new DIFField(name);
				parent.add(new DIFSubfield(new DIFField(Global.CATEGORY, Global.DEFAULT_CATEGORY)));
				for (int c = 0; c < colNames.length; c++){
					int col = table.findColumn(colNames[c]);
					if(!isEmptyCell(row, col, table)){
						String data = table.get(row, col);
						if(data.contains(">")){
							LinkedList<String> tvNames = Converter.separate(names[c], new LinkedList<String>());
							String[] temp = next(data, ';');
							table.set(row, col, temp[1]);
							if(temp[0] != null && !Converter.isEmpty(temp[0])){
								go = true;
								LinkedList<String> terms = Converter.separate(temp[0], ">", new LinkedList<String>());
								parent.add(new DIFSubfield(new DIFField(Converter.replaceIllegalChars(tvNames.get(0)), 
										Converter.replaceIllegalChars(terms.get(0)))));
								parent.add(new DIFSubfield(new DIFField(Converter.replaceIllegalChars(tvNames.get(1)), 
										Converter.replaceIllegalChars(terms.get(1)))));
							}
						}
						else if (data.contains(";")){
							String[] temp = next(data, ';');
							table.set(row, col, temp[1]);
							if(temp[0] != null && !Converter.isEmpty(temp[0])){
								go = true;
								parent.add(new DIFSubfield(new DIFField(Converter.replaceIllegalChars(names[c]), Converter.replaceIllegalChars(temp[0]))));
							}
						}
						else
							parent.add(new DIFSubfield(new DIFField(names[c], Converter.replaceIllegalChars(data))));
					}
				}
				if(go)
					record.addField(parent);
			}
			records.add(record);
		}
		return records;
	}

}
