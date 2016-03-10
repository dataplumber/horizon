package gov.nasa.horizon.common.api.metadatamanifest;

public class Constant {

	public enum ObjectType {
		DATASET("DATASET"), GRANULE("GRANULE"), COLLECTION("COLLECTION"), ELEMENT("ELEMENT"), SOURCE("SOURCE"), SENSOR("SENSOR"), PROVIDER("PROVIDER"), PROJECT("PROJECT"), CONTACT("CONTACT"); 	
	    private final String type;
    	private ObjectType(String type) {
    		this.type = type;
    	}
    	public String toString(){
    		return this.type;
    	}
	}
	
	public enum ActionType {
		UPDATE("UPDATE"), CREATE("CREATE"), DELETE("DELETE"), LIST("LIST"), TEMPLATE("TEMPLATE");; 	
	    private final String ActionType;
    	private ActionType(String type) {
    		this.ActionType = type;
    	}
    	public String toString(){
    		return this.ActionType;
    	}
	}
	
}
