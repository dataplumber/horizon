package gov.nasa.horizon.security.server

import grails.converters.XML;

class AuthController {

	def verifierService
	
	static allowedMethods = [authenticate:'POST',tokenize:'POST', tokenAuth: 'POST']
	
    def index = {
		log.debug("Index");
		}
	
	
	//postonly
	def authenticate = {
		
		def ip = request.getRemoteAddr()
		if(ip == null || ip == '')
			ip = request.getHeader("X-Forwarded-For")
		if(ip == null || ip == '')
			ip = request.getHeader("Client-IP")
		log.debug("IP Address: $ip")
		
		def realm = params.realm
		def user = params.user
		def pass = params.pass
		
		log.debug("Authenticating $user for $realm")
			def r = Realm.findByName(realm)
			def t = Token.findByRealmAndUser(r,user)
		if(t?.token == pass){
			response.status=200
			render "Authenticated successfully";
			return;
		}
		else if(verifierService.authenticate(ip, realm,user,pass))
		{
			response.status = 200;
			render "Authenticated successfully";
		}
		else{
			response.status= 401
			render "Authentication failed"
		}
	}
	
	def clean = {
		verifierService.clean();
		response.status = 200;
		render "ok!"
	}
	
	def forceclean = {
		verifierService.forceClean();
		response.status = 200;
		render "ok!"
	}
	
	//postonly
	def tokenize = {
		
		def ip = request.getRemoteAddr()
		if(ip == null || ip == '')
			ip = request.getHeader("X-Forwarded-For")
		if(ip == null || ip == '')
			ip = request.getHeader("Client-IP")
		log.debug("IP Address: $ip")
		
		def realm = params.realm
		def user = params.user
		def pass = params.pass
		
		log.debug("Tokenizing $user for $realm")
		def token = verifierService.tokenize(ip,realm,user,pass);
		if(token)
		{
			response.status = 200;
			render(contentType: "text/xml") {
				"authToken"{
					"authRealm"(realm)
					"authToken"(token)
				}
			}
		}
		else{
			response.status= 401
			render "Authentication failed"
		}
	}
	
	def authorize = {
		def realm = params.realm
		def user = params.user
		def group = params.group
		
		log.debug("Authorizing $user for $realm")
		if(verifierService.authorize(realm,user,group))
		{
			response.status = 200;
			render "Authenticated successfully";
		}
		else{
			response.status= 401
			render "Authentication failed"
		}
	}
	
	def authorizedRoles = {
		def realm = params.realm
		def user = params.user
		
		log.debug("Fetching roles $user for $realm")
		def lst = verifierService.listRoles(realm,user)
		if(lst)
		{
			response.status = 200;
			// render some XML markup to the response
			render(contentType: "text/xml") {
				"accessRoles"{
					for(itm in lst)
						"accessRole"(itm)
				}
			}
			return
		}
		else{
			response.status= 401
			render "Role fetch failed"
		}
	}
}
