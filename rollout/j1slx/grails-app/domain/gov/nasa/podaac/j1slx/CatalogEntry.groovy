package gov.nasa.podaac.j1slx

class CatalogEntry {

    Integer cycle
    String productVersion
    String author
	String approver
	
    boolean nasaApproval = false;
    boolean cnesApproval = false;

	boolean sgdrStaged = false;
    boolean gdrncStaged = false;
    boolean sgdrncStaged = false;
    boolean sshancStaged = false;

    Long gdrDate
    Long gdrArchTime
	Long sgdrDate
    Long gdrncDate
    Long sgdrncDate
    Long sshancDate
	
	static hasMany = [granules:CatalogEntryGranule]
	
    String emailId   

	public String toString(){
		return "$author/$cycle";
	}
	
    static constraints = {
	gdrDate(nullable:true)
		approver(nullable:true)
    	sgdrDate(nullable:true)
    	gdrncDate(nullable:true)
    	sgdrncDate(nullable:true)
    	sshancDate(nullable:true)
		emailId(nullable:true)
    }
}
