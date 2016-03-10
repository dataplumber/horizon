package gov.nasa.horizon.common.api.zookeeper.api.constants;

public enum RegistrationStatus {

	OFFLINE("OFFLINE"),READY("READY"),PAUSED("PAUSED");
	
	private String val;
	
	private RegistrationStatus(String val){
		this.val = val;
	}
	
	public String toString(){
		return val;
	}
}
