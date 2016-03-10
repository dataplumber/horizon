import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.usermodel.*;

public class ExcelReader {
	private LinkedList<Table> allTables = new LinkedList<Table> ();
	
	public ExcelReader(String filename) throws IOException{
		HSSFWorkbook wb= new HSSFWorkbook( new POIFSFileSystem(new FileInputStream(filename)));
		for(int i = 0; i < wb.getNumberOfSheets(); i++){
			ExcelSheetReader sheetReader = new ExcelSheetReader(wb.getSheetAt(i), wb.getSheetName(i).trim());
			allTables.add(sheetReader.getTable());
		}
	}

	public Table get(int index) {return allTables.get(index);}
	public LinkedList<Table> getAllTables() {return allTables;}
	public int size(){return allTables.size();}
	
	public Table get(String name){
		Table ret = null;
		for (int i = 0; i < size(); i++){	
			if(allTables.get(i).getName().equalsIgnoreCase(name))
				return allTables.get(i);
		}
		return ret;
	}
}


class ExcelSheetReader {
	private Table table = new Table();
	private int rows, currentRow = 0;
	private HSSFSheet sheet;
	
	public ExcelSheetReader(HSSFSheet sheet, String name){
		table.setName(name);
		this.sheet = sheet;
		rows = sheet.getLastRowNum() + 1;
		while(hasNextRow())
			table.addRow(new ExcelRowReader(nextRow(), sheet.getRow(0).getPhysicalNumberOfCells()).getRowList());
	}
	
	public Table getTable() {return table;}

	private HSSFRow nextRow(){
		HSSFRow retRow = sheet.getRow(currentRow);
		currentRow++;
		return retRow;
	}
	
	private boolean hasNextRow(){
		if (currentRow == rows)
			return false;
		return true;
	}
}

class ExcelRowReader {
	private LinkedList<String> rowList = new LinkedList<String>();
	private int maxColumns, currentCell = 0;
	private HSSFRow row;
	
	public ExcelRowReader(HSSFRow row, int maxColumns){
		this.row = row;
		this.maxColumns = maxColumns;
		while(hasNextCell())
			rowList.add(nextCell());
	}
	
	public LinkedList<String> getRowList() {return rowList;}
	
	private String nextCell(){
		if(row == null){
			currentCell++;
			return "";
		}
		HSSFCell retCell = row.getCell((short)currentCell);
		currentCell++;
		if (retCell == null)
			return "";
		return retCell.toString().trim();	
	}
	
	private boolean hasNextCell(){
		if (currentCell == maxColumns)
			return false;
		return true;
	}
}

