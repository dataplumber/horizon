package gov.nasa.horizon.security.client.commands

import java.util.Map

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.security.client.api.Command;

class CommandTokenize extends Command {

	private static Log log = LogFactory.getLog(CommandTokenize.class)
	private String password;
	
	public CommandTokenize(String realm, String user, String pass ){
		super(user, realm);
		this.password = pass;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String p){
		password= p ;
	}
	
	
	/*
	 * <authToken>
	 * 	<authRealm>J1SLX</authRealm>
	 * 	<authToken>otst+gyFfF7ofmoGdW14T1oxqGYYAFl4FTZnR4kuoBhBl+8QZTKUvaXZmq6LEbzE1rSDPb6GphRM+lihcN6J+Q==</authToken>
	 * </authToken>
	 */
	
	@Override
	public CommandResponse processResponse(Object resp, Object data){
		log.debug("Processing response");
		if(resp == null && data == null){
			log.debug("Null response and data.")
			return new CommandResponse() //null list
			
		}
		String result = null;
		if(resp.statusLine.statusCode == 200){
			def rootNode = new XmlParser().parse(data)
			def rlm = rootNode?.authRealm?.text()
			def tkn = rootNode?.authToken?.text()
			result = "${rlm}:${tkn}"
			//log.debug("result: $result")
			def cr = new CommandResponse()
			cr.setStringResponse(result);
			return cr;
		}
		else
			return new CommandResponse() //null list
	}
	
	@Override
	public String getUrl() {
		return "/security/auth/" + super.getRealm() + "/tokenize"
	}

	@Override
	public String getCommandName() {
		return "Tokenize"
	}

	@Override
	public Map<String, String> getParams() {
		return [user:super.getUser(),pass:this.password,realm:super.getRealm()]; 
	}

}
