
import java.util.LinkedList;

public class Table {
	private String name;
	private LinkedList<LinkedList<String>> data;
	
	public Table(){this(null, new LinkedList<LinkedList<String>>());}
	
	public Table(String name, LinkedList<LinkedList<String>> data){
		this.name = name;
		this.data = data;
	}
	
	public void addColumn(int row, String cell){data.get(row).add(cell);}
	
	public void addRow(LinkedList<String> row){data.add(row);}
	
	public int columns(int row){return data.get(row).size();}
	
	public int findColumn(String columnName){
		LinkedList<String> columns = getColumnHeaders();
		for(int i = 0; i < columns.size(); i++){
			if(columns.get(i).trim().equals(columnName.trim()))
				return i;
		}
		return -1;
	}
	
	public String get(int row, int column){return data.get(row).get(column);}
	
	public String[] getArrayColumnHeaders(){
		LinkedList<String> cHeaders = getColumnHeaders();
		String[] ret = new String[cHeaders.size()];
		for(int i = 0; i < ret.length; i++)
			ret[i] = cHeaders.get(i);
		return ret;
	}
	
	public String[][] getArrayData(){
		String[][] table = new String[data.size()][data.size()]; 
		for(int i = 0; i < data.size(); i++){
			String[] ret = new String[data.get(i).size()];
			for(int j = 0; j < ret.length; j++)
				ret[j] = data.get(i).get(j);
			table[i] = ret;
		}
		return table;
	}
	
	public LinkedList<String> getColumnHeaders(){return data.get(0);}
	
	public LinkedList<LinkedList<String>> getData() {return data;}
	
	public String getName() {return name;}
	
	public boolean isEmpty(int row, int col){
		if (Converter.isEmpty(get(row, col).trim()))
			return true;
		return false;
	}
	
	public boolean isEmpty(int row){
		for(int i = 0; i < data.get(row).size(); i++)
			if(!Converter.isEmpty(data.get(row).get(i)))
				return false;
		return true;
	}
	
	public int removeRow(int row){
		try{
			data.remove(row); 
		}
		catch (java.lang.IndexOutOfBoundsException e){}
		return row;
	}
	
	public int rows(){return data.size();}
	
	public void set(int row, int column, String cell){data.get(row).set(column, cell);}
	
	public void setData(LinkedList<LinkedList<String>> data) {this.data = data;}
	
	public void setName(String name) {this.name = name;}
	
	public String specs(){
		return "Name: " + name + "Rows: " + rows();
	}
}
