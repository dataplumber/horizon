package gov.nasa.horizon.security.client.commands

import java.util.Map

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.security.client.api.Command;

class CommandAuthenticate extends Command {

	private static Log log = LogFactory.getLog(CommandAuthenticate.class)
	private String password;
	
	public CommandAuthenticate(String realm, String user, String pass ){
		super(user, realm);
		this.password = pass;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String p){
		password= p ;
	}
	
	@Override
	public CommandResponse processResponse(Object resp, Object data){
		log.debug("Processing response");
		if(resp == null && data == null)
			return new CommandResponse(false)
			
		if(resp.statusLine.statusCode == 200)
			return new CommandResponse(true)
		else
			return new CommandResponse(false)
	}
	
	@Override
	public String getUrl() {
		return "/security/auth/" + super.getRealm() + "/authenticate"
	}

	@Override
	public String getCommandName() {
		return "Authenticate"
	}

	@Override
	public Map<String, String> getParams() {
		return [user:super.getUser(),pass:this.password,realm:super.getRealm()]; 
	}

}
