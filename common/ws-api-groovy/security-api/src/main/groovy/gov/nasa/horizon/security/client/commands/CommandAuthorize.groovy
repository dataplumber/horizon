package gov.nasa.horizon.security.client.commands

import java.util.Map

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.security.client.api.Command;

class CommandAuthorize extends Command {

	private static Log log = LogFactory.getLog(CommandAuthorize.class)
	private String group;
	
	public CommandAuthorize(String realm, String user, String group ){
		super(user, realm);
		this.group = group;
	}
	
	public String getGroup(){
		return group;
	}
	
	public void setPassword(String group){
		this.group= group ;
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
		return "/security/auth/" + super.getRealm() + "/authorize"
	}

	@Override
	public String getCommandName() {
		return "Authorize"
	}

	@Override
	public Map<String, String> getParams() {
		return [user:super.getUser(),group:this.group,realm:super.getRealm()]; 
	}

}
