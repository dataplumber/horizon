package gov.nasa.horizon.security.server

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class CacheCleanerJob{
	
	def cacheService
	def concurrent = false
	
	static triggers = {
		simple name: 'cleanerCaches', startDelay: ((Integer)ConfigurationHolder.config.security.caching.jobStartDelay)*1000, repeatInterval: ((Integer)ConfigurationHolder.config.security.caching.jobInterval)*1000L //wait a minute, then every minute
	  }
	  def group = "horizon-security-cache"
	
	def execute(){
		log.info("Cleaning the auth cache");
		cacheService.cleanCache();
	}
	
}
