
package gov.nasa.horizon.security.server

import gov.nasa.horizon.security.server.api.*;
import gov.nasa.horizon.security.server.utils.Encrypt;
import java.util.Hashtable;
import java.util.List;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;


class Database implements AandA {

	//Log log = LogFactory.getLog(getClass())
	private static def log = LogFactory.getLog(this)
	
	public boolean authenticate(String user, String pass){
		log.debug("autheticating")
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
				if(lst?.contains(it.getRoleGroup()))
					ret.add(it.getRoleName())
			 }
		
		return ret;

	}
	
	private List<String> listUserRoles(String user){
		List<String> lst = new ArrayList<String>();
		def u = IngSystemUser.findWhere(name: user);
		if(!u){
			log.info("Could not find user: $user");
			return null;
		}
		//check fields
		if(u.admin)
			lst.add("field:admin")
		if(u.readAll)
			lst.add("field:read_all")
		if(u.writeAll)
			lst.add("field:write_all")
		//check roles
		def roles = IngSystemUserRole.findAllByUser(u);
		for(IngSystemUserRole ur: roles){
			lst.add("ar:" + ur.getRole().getName())
		}
		return lst;
	}
	
	private boolean auth(String user,String pass)
	{
		log.debug("autheticating $user : ${Encrypt.encrypt(pass)}")
		
		def u = IngSystemUser.findWhere(name: user, password: Encrypt.encrypt(pass))
		if(u == null)
			return false;
		else 
			return true;
	}
	
	private boolean authGroup(String user, String group) {
		log.debug("processing group: $group")
		String[] split =  group.split(":");
		if(split.size() < 2){
			log.error("Cannot parse database group: $group")
			return false;
		}
		
		def u = IngSystemUser.findWhere(name: user);
		
		if(!u){
			log.info("Could not find user: $user");
			return false;
		}
		
		def type = split[0]
		if(type == 'field'){
			def field = split[1];
			log.debug("got: $type[$field]");
			return processField(u,field)
		}
		else if(type == 'ar'){
			def field = split[1];
			log.debug("got: $type[$field]");
			return processRole(u,field)
		}
		else{
			log.error("Unrecognized group type[${type}]. Must be one of 'field' or 'ar'. ");
			return false;
		}
		
		return false;
		
	}
	
	private boolean processField(IngSystemUser user, String field){
		if(field == "admin")
			return user.admin
		if(field == "read_all")
			return user.readAll
		if(field == "write_all")
			return user.writeAll
		else{
			log.error("Unrecognized field: $field");
			return false
		}
	}
	
	private boolean processRole(IngSystemUser user, String field){
		
		def roles = IngSystemUserRole.findAllByUser(user);
		for(IngSystemUserRole ur: roles){
			def rl = ur.getRole().getName()
			log.debug("role: "+ rl)
			if(rl == field)
				return true	
		}
		
		return false;
	}
	
	
}
