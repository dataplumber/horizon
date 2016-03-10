package gov.nasa.horizon.security.client.commands

import java.util.Map

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.security.client.api.Command;

class CommandListRoles extends Command {

	private static Log log = LogFactory.getLog(CommandListRoles.class)
	
	public CommandListRoles(String realm, String user ){
		super(user, realm);
	}
	
	@Override
	public CommandResponse processResponse(Object resp, Object data){
		log.debug("Processing response");
		if(resp == null && data == null){
			log.debug("Null response and data.")
			return new CommandResponse() //null list
			
		}
		List<String> lst = new ArrayList<String>();	
		if(resp.statusLine.statusCode == 200){
			def rootNode = new XmlParser().parse(data)
			rootNode?.accessRole.each {
				log.debug(it.text())
				lst.add(it.text())
			}
			return new CommandResponse(lst)
		}
		else
			return new CommandResponse() //null list
		
	}
	
	@Override
	public String getUrl() {
		return "/security/auth/" + super.getRealm() + "/authorizedRoles"
	}

	@Override
	public String getCommandName() {
		return "ListRoles"
	}

	@Override
	public Map<String, String> getParams() {
		return [user:super.getUser(),realm:super.getRealm()]; 
	}

}
