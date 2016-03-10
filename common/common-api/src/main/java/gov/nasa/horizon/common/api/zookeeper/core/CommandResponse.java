package gov.nasa.horizon.common.api.zookeeper.core;

public class CommandResponse {

	String stringResponse;
	boolean booleanResponse;
	Integer integerResponse;

	public CommandResponse(String resp){
		stringResponse = resp;
	}
	
	public CommandResponse(boolean resp){
		booleanResponse = resp;
	}

	public CommandResponse(Integer resp){
		integerResponse = resp;
	}
	
	public String getStringResponse() {
		return stringResponse;
	}
	public boolean isBooleanResponse() {
		return booleanResponse;
	}
	public Integer getIntegerResponse() {
		return integerResponse;
	}
	
}
