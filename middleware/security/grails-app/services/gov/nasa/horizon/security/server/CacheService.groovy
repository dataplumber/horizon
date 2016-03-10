package gov.nasa.horizon.security.server
import gov.nasa.horizon.security.server.utils.Encrypt
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CacheService {

    static transactional = true

	def cache = [:]
	def LOCKED = -1;
	def UNKNOWN = 0;
	def SUCCESS = 1;
	
	def allowedMisses = (Integer) ConfigurationHolder.config.security.caching.misses;
	def lockTime = (Integer) ConfigurationHolder.config.security.caching.cacheTimeLimit;
	def cacheTime = (Integer) ConfigurationHolder.config.security.caching.lockTimeLimit;
		
	def limitReached(origin, interval){
		def now = new Date().getTime()
		def diff = now - origin;
		if(diff > (interval * 60 * 1000)){
			return true
		}
		else
			return false
	}
	
	/*
	 * return 1 = yes, 0 = no/not cached, -1 = locked
	 */
	def checkAuthenticationCache(map){
		log.debug("Cache"+cache)
		log.debug("Checking cache for authentication")
		def eKey = genKey(map)
		def entry = cache.getAt(eKey)
		//not cached
		if(entry == null){
			log.debug("eKey not found in cache. Using normal Authentication")
			return UNKNOWN;
		}
		//check lock status
		if(entry.locked){
			log.debug("Entry locked.")
			//check time, see if it should be unlocked
			if(limitReached(entry.lockedTime, lockTime)){
				log.debug("Unlocking cache entry")
				entry.misses = 0;
				entry.locked = false;
				entry.lockedTime = 0l;
				cache.putAt(eKey, entry)
			}
			else
				return LOCKED
		}
		//check cache time
		if(entry.cacheTime != null){
			if(limitReached(entry.cacheTime, cacheTime))
				return UNKNOWN;	
		}
		if(entry.pass == null)
			return UNKNOWN	
		if(entry.pass == Encrypt.encrypt(map.pass)){
			log.debug("Cache hit. resetting misses")
			entry.misses = 0;
			cache.putAt(eKey, entry)
			return SUCCESS
		}
	}
	
	def cleanCache(){
		def minusMap = [:]
		
		cache.each() { key, value -> 
			if(limitReached(value.cacheTime, cacheTime))
			{
				minusMap.put(key,value)
			}
		};
		cache = cache.minus(minusMap);
	}
	
	def forceCleanCache(){
		//we just cleared everything!
		cache = [:]
	}
	
	def addCacheMiss(map){
		log.debug("Cache"+cache)
		log.debug("Adding cache miss")
		def eKey = genKey(map)
		def entry = cache.getAt(eKey)
		if(entry != null){
			entry.misses++
			if(entry.misses >= allowedMisses){
				entry.misses = 0;
				entry.locked = true;
				entry.lockedTime = new Date().getTime();
			}
		}
		//No entry exists, create and set misses to 1
		else{
			log.debug("Cache miss. creating entry.")
			def source = map.source
			def realm = map.realm
			def user = map.user
			def pass = null //wrong password used, null entry 
			def cacheTime = new Date().getTime()
			def misses = 1;
			def locked = false;
			def lockedTime = 0l;
			//any processing?
			entry = [source:source,realm:realm,user:user, pass:null, cacheTime:cacheTime,misses:misses, locked:locked,lockedTime:lockedTime]
			def ekey = genKey(map)
			cache.putAt(eKey, entry)
		}
			
		
	}
	
	def addAuthenticationCache(map){
		
		log.debug("map value: $map")
		
		def source = map.source
		def realm = map.realm
		def user = map.user
		def pass = map.pass
		def cacheTime = new Date().getTime()
		def misses = 0;
		def locked = false;
		def lockedTime = 0l;
		
		//any processing?
		
		def entry = [source:source,realm:realm,user:user, pass:Encrypt.encrypt(pass), cacheTime:cacheTime,misses:misses, locked:locked,lockedTime:lockedTime]
		def eKey = genKey(map)
		
		cache.putAt(eKey, entry)
	}
	
	private genKey(map){
		def source = map.source
		def realm = map.realm
		def user = map.user
		def eKey = "$realm:$source:$user";
		log.debug("Genereated eKey: $eKey")
		return eKey
	}
}
