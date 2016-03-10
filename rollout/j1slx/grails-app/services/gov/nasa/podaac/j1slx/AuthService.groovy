package gov.nasa.podaac.j1slx;

import org.codehaus.groovy.grails.commons.*

import gov.nasa.podaac.security.client.*;
import gov.nasa.podaac.security.client.api.*;

class AuthService {

	private static SecurityAPI sapi = null;
    static transactional = true

	public boolean authenticate(String user,String pass){
		
		def host = (String)ConfigurationHolder.config.gov.nasa.podaac.security.host
		def port = (Integer)ConfigurationHolder.config.gov.nasa.podaac.security.port
		def realm = (String)ConfigurationHolder.config.gov.nasa.podaac.security.realm
		def role  = (String)ConfigurationHolder.config.gov.nasa.podaac.security.role
		
		sapi =  SecurityAPIFactory.getInstance(host, port)
		if(sapi.authenticate(user,pass,realm))
		return sapi.authorize(user, role, realm )
		
	}
	
	//	//This is where we'll check LDAP for authentication.
	//	String providerUrl = (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.ldap.host
	//	String searchDn = (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.ldap.searchDn
	//	def groupdn  = "cn=" + ((String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.ldap.group)
	
//	private static boolean _isMember(String userDN, String groupRelDN, DirContext ctxBase, String searchDn) throws NamingException {
//		
//		
//		DirContext ctx = null;
//		try {
//				ctx = (DirContext)ctxBase.lookup(searchDn);
//				// do a compare operation
//				SearchControls ctls = new SearchControls();
//				ctls.setReturningAttributes(new String[0]);       // Return no attrs
//				ctls.setSearchScope(SearchControls.OBJECT_SCOPE); // Search object only
//				NamingEnumeration answer = ctx.search(groupRelDN,
//							"(uniqueMember="+userDN+")", ctls);
//				return answer.hasMore();
//		} finally {
//				if (ctx != null)
//						ctx.close();
//		}
//	}

//	public boolean authenticate(String user,String pass) throws NamingException
//	{
//	   
//		  
//		  def userdn   = "uid="+user+","+searchDn;
//		
//		  log.debug(providerUrl);
//		  log.debug(searchDn);
//		  log.debug(groupdn);
//		  log.debug(userdn);
//		  
//		  Hashtable env = new Hashtable();
//	
//		  env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
//		  env.put(javax.naming.Context.SECURITY_AUTHENTICATION,"simple");
////		  env.put(javax.naming.Context.SECURITY_PRINCIPAL,"uid="+user+","+searchDn);
//		  env.put(javax.naming.Context.SECURITY_PRINCIPAL,userdn);
//		  env.put(javax.naming.Context.SECURITY_CREDENTIALS, pass);
//		  env.put(javax.naming.Context.PROVIDER_URL,providerUrl);
//		  env.put(javax.naming.Context.SECURITY_PROTOCOL,"ssl");
//		  
//		  DirContext ctx = null;
//		  try {
//			  ctx = new InitialDirContext(env);
//			  return _isMember( userdn,groupdn, ctx, searchDn);
//		} catch (NamingException e) {
//			log.debug("User [$user] specified invalid credentials.");
//		}
//		 return false;
//			 
//	}
}
