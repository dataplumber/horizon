import gov.nasa.podaac.j1slx.CatalogEntry
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class BootStrap {

	def local = [
		entries:[
				[cycle:100,author:"CNES",version:"C"],
				[cycle:101,author:"CNES",version:"C"],
				[cycle:102,author:"CNES",version:"C"],
				[cycle:103,author:"CNES",version:"C"],
				[cycle:104,author:"CNES",version:"C"],
				[cycle:100,author:"NASA",version:"C"],
				[cycle:101,author:"NASA",version:"C"],
				[cycle:102,author:"NASA",version:"C"],
				[cycle:103,author:"NASA",version:"C"],
				[cycle:104,author:"NASA",version:"C"],
			]
	]
	
	def test = [entries:[]]
	
    def init = { servletContext ->
		log.debug '*** BootStrap, GrailsUtil.environment: ${GrailsUtil.environment}'
		switch (GrailsUtil.environment) {
		  case 'local':
		  case 'oracle':
			log.debug '*** detected local'
			this.config(local)
			break
		  case 'development':
			log.debug '*** detected development'
			this.config(local)
			break;
		  case 'testing':
		  log.debug '*** detected test'
		  this.config(test)
		  break;
		}
	}
    
	def destroy = {
    }

	void config(def params) {
		def entries = params.entries;
		log.debug("Processing rows");
		entries.each { 
				CatalogEntry ca = new CatalogEntry();
				log.debug("author: "+it.author);
				ca.setAuthor(it.author);
				ca.setCycle(it.cycle);
				ca.setProductVersion(it.version)
				ca.setGdrArchTime(new Date().time)
				//ca.setGdrDate(new Date().time)
				log.debug("entry: " + ca.getAuthor());
				if(!ca.save(flush: true))
				{
					ca.errors.each {
						log.debug it
					}
				}
			}
		log.debug("List of catalog entries:");
		CatalogEntry.list().each { 
			log.debug(it.id);
		}
		
	}
}
