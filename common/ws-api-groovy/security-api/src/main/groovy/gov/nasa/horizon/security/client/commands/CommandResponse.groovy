package gov.nasa.horizon.security.client.commands

class CommandResponse {
	
	boolean boolResponse = false
	String stringResponse = null
	List<String> listResponse = null
	
	public CommandResponse(){
	}
	public CommandResponse(boolean val){
		boolResponse = val;
	}
	public CommandResponse(String val){
		StringResponse = val;
	}
	public CommandResponse(List<String> val){
		listResponse = val;
	}
	
	public boolean getBooleanResponse(){
		return boolResponse;
	}
	
	public List<String> getListResponse(){
		return listResponse;
	}
	
	public String getStringResponse(){
		return stringResponse;
	}
	
	public void setStringResponse(String s){
		stringResponse = s;
	}
	
}
