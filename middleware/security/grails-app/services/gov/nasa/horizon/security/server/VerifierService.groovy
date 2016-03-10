package gov.nasa.horizon.security.server

import gov.nasa.horizon.security.server.api.AandA

import javax.crypto.SecretKey
import javax.crypto.KeyGenerator
import gov.nasa.horizon.security.server.utils.DesEncrypter
import gov.nasa.horizon.security.server.LDAP;
import gov.nasa.horizon.security.server.Realm;
import gov.nasa.horizon.security.server.Role;
import java.util.UUID;

class VerifierService {

    static transactional = true

	//The available verifiers.
	private static LDAP ldap = new LDAP();
	private static Database database = new Database();
	
	def cacheService
	//private static DBMS oracle = new DBMS();
	
    def serviceMethod() {

    }
	
	def forceClean = {
		cacheService.forceCleanCache();
	}
	
	def clean = {
		cacheService.cleanCache();
	}
	
	def tokenize = { source, realm, user, pass ->
		if(!authenticate(source,realm,user,pass)){
			log.info("User authentication failed. Not creating a token.")
			return false
		}
		
		def cd = new Date().getTime();
		def uuid = UUID.randomUUID().toString().toUpperCase();
		def key = "$realm | $uuid | $cd"
		log.debug("key -> $key")
		def token = null;
		try {
			SecretKey skey = KeyGenerator.getInstance("DES").generateKey();
			DesEncrypter encrypter = new DesEncrypter(skey);

			token = encrypter.encrypt(key);
			// Decrypt
			//String decrypted = encrypter.decrypt(encrypted);
			
		} catch (Exception e) {
			log.debug("Exception thrown: ${e.getMessage()}",e);
		}
		
		log.debug("Genereated token $token")
		def r = fetchRealm(realm)
		Token t;
		t = Token.findByRealmAndUser(r,user)
		if(t == null)
		{
			t = new Token();
			t.user = user;
			t.token = token;
			t.realm = r;
			r.addToTokens(t);
			r.save();
		}else{
			t.token = token;
			t.createDate = new Date().getTime();
			t.save();
		}

		return token;
	}
	
	
	def authenticate = { source,realm, user, pass ->
		if(!user || !pass){
			log.debug("A user and password must be supplied for authentication");
			return false;	
		}
		if(realm == null){
			log.error("No realm supplied.")
			return false;
		}
		def facade = getAandA(realm);
		if(!facade)
		{
			log.error("No verifier exists for realm: [$realm]");
			return false;
		}
		
		def map = [source:source,user:user, pass:pass, realm:realm]
		def res = cacheService.checkAuthenticationCache(map)
		if(res == 1){
			log.debug("In cache.")
			return true
		}else if(res == -1){
			log.debug("cache Lock")
			return false
		}
		log.debug("Doing this the hardway.")
		
		def bool = facade.authenticate(user, pass); 
		if(bool){
			cacheService.addAuthenticationCache(map);
		}
		else{
			cacheService.addCacheMiss(map);
		}
		return bool 
	}
	
	def authorize = { realm, user, role ->
		if(!user){
			log.debug("A user must be supplied for authorization");
			return false;
		}
		if(!role){
			log.debug("A group must be supplied for authorization");
			return false;
		}
		if(realm == null){
			log.error("No realm supplied.")
			return false;
		}
		
		def facade = getAandA(realm);
		if(!facade)
		{
			log.error("No verifier exists for realm: [$realm]");
			return false;
		}
		def rlm = fetchRealm(realm)
		
		Role r = Role.findByRealmAndRoleName(rlm,role);
		if(!r)
		{
			log.error("No role [$role] for realm [$rlm] exists");
			return false
		}
		
		return facade.authorize(user, r.getRoleGroup());
		
		
	}
	
	def listRoles = { realm, user ->
		if(!user){
			log.debug("A user must be supplied for authorization");
			return null;
		}
		if(realm == null){
			log.error("No realm supplied.")
			return null;
		}
		
		def facade = getAandA(realm);
		if(!facade)
		{
			log.error("No verifier exists for realm: [$realm]");
			return null;
		}
		def lst =facade.getRoles(fetchRealm(realm).roles, user);
		return lst;
		
	}
	
	private Realm fetchRealm(String realm){
		def r = Realm.findByName(realm);
		if(r == null){
			log.error("Realm $realm does not exist.")
			return null;
		}
		return r;
	}
	private AandA getAandA(String realm){
		
		def r = fetchRealm(realm);
		if(r == null)
			return null;
		
		
		def verifier = r.verifier?.name
		log.debug("Searching for verifier: $verifier")
		
		if(verifier?.equals("LDAP"))
			return ldap;
		else if(verifier?.equals("DATABASE"))
			return database
		
		else
			return null;
	}
	
	
}
