
package gov.nasa.horizon.security.server

import gov.nasa.horizon.security.server.api.*;

import java.util.Hashtable;
import java.util.List;
import org.apache.commons.logging.Log 
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import javax.naming.NamingEnumeration
import javax.naming.NamingException;
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls
import javax.naming.directory.SearchResult;

class LDAP implements AandA {

	//Log log = LogFactory.getLog(getClass())
	private static def log = LogFactory.getLog(this)
	
	public boolean authenticate(String user, String pass){
		
		return auth(user,pass)
		
	}
	
	public boolean authorize(String user, String group){
		
		log.debug("Searching for $user in $group");
		return authGroup(user,group)
		
	}
	
	public List<String> getRoles(Set<Role> roles, String user){
			
		def ret = [];
		def lst = listUserRoles(user);
		roles.each { 
				if(lst.contains(it.getRoleGroup()))
					ret.add(it.getRoleName())
			 }
		
		return ret;

	}
	
	private List<String> listUserRoles(String user){
		List<String> lst = new ArrayList<String>();
		
		String providerUrl = (String)ConfigurationHolder.config.security.plugins.LDAP.host
		String searchDn = (String)ConfigurationHolder.config.security.plugins.LDAP.searchDn
		def userDN   = "uid="+user+","+searchDn;
		
		  log.debug(providerUrl);
		  log.debug(searchDn);
		  log.debug(userDN);
		  
		Hashtable env = new Hashtable();
		  env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		  env.put(javax.naming.Context.PROVIDER_URL,providerUrl);
		  env.put(javax.naming.Context.SECURITY_PROTOCOL,"ssl");
		  
		  DirContext ctxBase = null;
		  
		try {
			new InitialDirContext();
			ctxBase = new InitialDirContext(env);
			
				SearchControls ctls = new SearchControls();
				String[] attrIDs = ["cn"];
		        ctls.setReturningAttributes(attrIDs);
		        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				NamingEnumeration<SearchResult> answers = ctxBase.search(searchDn,"(uniqueMember="+userDN+")",ctls);
				
				while(answers.hasMore()){
					SearchResult sr = answers.next();
					lst.add(sr.getAttributes().get("cn").get())
				}
				return lst;
				
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 finally {
			 ctxBase.close();
		}
	}
	
	private boolean auth(String user,String pass) throws NamingException
	{
		  String providerUrl = (String)ConfigurationHolder.config.security.plugins.LDAP.host
		  String searchDn = (String)ConfigurationHolder.config.security.plugins.LDAP.searchDn

		  def userdn   = "uid="+user+","+searchDn;		  
		  Hashtable env = new Hashtable();
	
		  env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		  env.put(javax.naming.Context.SECURITY_AUTHENTICATION,"simple");
		  env.put(javax.naming.Context.SECURITY_PRINCIPAL,userdn);
		  env.put(javax.naming.Context.SECURITY_CREDENTIALS, pass);
		  env.put(javax.naming.Context.PROVIDER_URL,providerUrl);
		  env.put(javax.naming.Context.SECURITY_PROTOCOL,"ssl");
		  
		  DirContext ctx=null
		  try {
			  	ctx = new InitialDirContext(env);
			  	log.debug("User $user successfully authenticated with the LDAP server.")
			  	return true;
		  } catch (NamingException e) {
				log.debug("User [$user] failed to authenticate with the LDAP server.");
		  }
		  finally{
			  	ctx?.close();
		  }
		  
		 return false;
	}
	
	private boolean authGroup(String user, String group) throws NamingException{
		
		String providerUrl = (String)ConfigurationHolder.config.security.plugins.LDAP.host
		String searchDn = (String)ConfigurationHolder.config.security.plugins.LDAP.searchDn
		def userdn   = "uid="+user+","+searchDn;
		def groupdn  = "cn=$group"
		
		Hashtable env = new Hashtable();
		
			  env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			  env.put(javax.naming.Context.PROVIDER_URL,providerUrl);
			  env.put(javax.naming.Context.SECURITY_PROTOCOL,"ssl");
			  
			  DirContext ctx = null;
			  try {
				  ctx = new InitialDirContext(env);
				  return _isMember( userdn,groupdn, ctx, searchDn);
			} catch (NamingException e) {
				log.debug("User [$user] specified invalid credentials.");
			}
			 return false;
		
	}
	
	private static boolean _isMember(String userDN, String groupRelDN, DirContext ctxBase, String searchDn) throws NamingException {
		
		
		DirContext ctx = null;
		try {
				ctx = (DirContext)ctxBase.lookup(searchDn);
				// do a compare operation
				SearchControls ctls = new SearchControls();
				ctls.setReturningAttributes(new String[0]);       // Return no attrs
				ctls.setSearchScope(SearchControls.OBJECT_SCOPE); // Search object only
				NamingEnumeration answer = ctx.search(groupRelDN,
							"(uniqueMember="+userDN+")", ctls);
				return answer.hasMore();
		} finally {
				if (ctx != null)
						ctx.close();
		}
	}
	
}
