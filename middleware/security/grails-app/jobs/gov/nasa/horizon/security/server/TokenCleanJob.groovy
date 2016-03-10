package gov.nasa.horizon.security.server

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class TokenCleanJob {

	def cacheService
	def concurrent = false
	
	static triggers = {
		simple name: 'cleanTokens', startDelay: ((Integer)ConfigurationHolder.config.security.token.jobStartDelay)*1000, repeatInterval: ((Integer)ConfigurationHolder.config.security.token.jobInterval)*1000L //wait a minute, then every minute
	  }
	  def group = "horizon-security"
	
	def execute(){
		log.info("Fetching token/realm data");
		def rList = Realm.list();
		
		def now = new Date().getTime();
		
		rList.each { r ->
			def expire = r.getTokenExpiration()
			def expireTime = (expire * 24 * 60 * 60 * 1000) //days in millis
			//this will go to the next entry in the closure
			if(expire == 0)
				return
			//else check the tokens
			log.info("Checking tokens for realm $r")
			r.tokens.each {t ->
			
				def diff = now - t.createDate
				log.debug("diff,expiredTime =  ($diff,$expireTime)")				
				if(diff >  expireTime) 
				{
					log.info("Token expired, Deleting.");
					r.removeFromTokens(t);
					t.delete(flush: true);
				}
			}
				
		}
		
		
	}
}
