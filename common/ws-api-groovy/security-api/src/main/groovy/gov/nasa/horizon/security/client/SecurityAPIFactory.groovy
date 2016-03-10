package gov.nasa.horizon.security.client;

import gov.nasa.horizon.security.client.api.SecurityAPI;
import gov.nasa.horizon.security.client.core.SecurityImpl;

public class SecurityAPIFactory {

	private static SecurityAPI sapi = null;
	private static String host = null;
	private static Integer port = null;
	
	public static SecurityAPI getInstance(){
		if(sapi == null)
			return init();
		else
			return sapi;
	}
	
	public static  SecurityAPI getInstance(String url, int port){
		
		if(this.host != host || this.port != port)
		{
			this.host = url;
			this.port = port;
			return init();
		}
		else{
			if(sapi == null)
					return init();
			else
				return sapi;
		}
		
	}
	
	public static SecurityAPI init(){
		if(host == null || port == null)
			sapi = new SecurityImpl();
		else
			sapi = new SecurityImpl(host, port);
		return sapi;
	}
	
}
