package gov.nasa.podaac.j1slx

class CatalogEntryGranule {

//    Integer cycle
//	String author
    Integer granuleId
	
	Integer originalDataset
	String openDataset
	 
	boolean moved = false
	
	static belongsTo = [entry:CatalogEntry] 
	
    static constraints = {
	
    }
	
	public String toString(){
		return "$granuleId"
	}
	
}
