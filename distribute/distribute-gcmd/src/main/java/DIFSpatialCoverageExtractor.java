import java.util.LinkedList;


public class DIFSpatialCoverageExtractor extends DIFExtractor {
	public DIFSpatialCoverageExtractor(){
		super(Global.SPATIAL_COVERAGE_ACTUAL_NAME);
	}
	
	public LinkedList<DIFRecord> extractSpatialCoverageData(Table table, String[] names, String[] colName){    
		for(int row = Global.FIRST_PRODUCT_ROW; row < table.rows(); row++)
			records.add(new DIFRecord(new DIFSubfield(extractSpatialCoverageData(table, row, names, colName[0]))));
		return records;
	}

	public DIFField extractSpatialCoverageData(Table table, int row, String[] names, String colName){    
		LinkedList<DIFSubfield> children = new LinkedList<DIFSubfield>();
		int col = table.findColumn(colName);
		if(!isEmptyCell(row, col, table)){
			String data = Converter.replaceIllegalChars(table.get(row, col).replace("[", "").replace("]", ""));
			LinkedList<String> entries = Converter.separate(data, ",",new LinkedList<String>());
			String[] rearrangedEntries = {entries.get(1), entries.get(3), entries.get(0), entries.get(2)};
			for(int i = 0; i < names.length; i++){
				DIFSubfield child = new DIFSubfield(names[i]);
				child.add(new DIFField(names[i], rearrangedEntries[i]));
				children.add(child);
			}
			
		}
		return new DIFField(name, null, null, null, children, null);
	}
}
