package gov.nasa.horizon.security.client.api;

import gov.nasa.horizon.security.client.commands.CommandResponse;

import java.util.Map;

public abstract class Command {

	private String user;
	private String realm;
	
	public abstract String getUrl();
	public abstract String getCommandName();
	public abstract Map<String,String> getParams();
	
	/*
	 * Constructors
	 */
	public Command(){}
	public  abstract CommandResponse processResponse(Object resp, Object data);
	public Command(String user, String realm){
		this.user=user;
		this.realm=realm;
	}
	
	/*
	 * Methods
	 */
	public void setUser(String u){
		user = u;
	}
	public String getUser(){
		return user;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
}
