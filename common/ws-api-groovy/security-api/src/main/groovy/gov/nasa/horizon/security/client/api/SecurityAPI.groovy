package gov.nasa.horizon.security.client.api;

import java.util.List;

public interface SecurityAPI {

	public boolean authenticate(String user, String pass);
	public boolean authenticate(String user, String pass, String realm);
	
	public String tokenize(String user, String pass);
	public String tokenize(String user, String pass, String realm);
	
	public boolean authorize(String user, String group);
	public boolean authorize(String user, String group, String realm);
	
	public List<String> getRoles(String user);
	public List<String> getRoles(String user, String realm);
	public void loadConfig();
	public String fetchConfig(String key);
	
}
