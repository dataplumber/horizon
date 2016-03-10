package gov.nasa.podaac.j1slx

import org.codehaus.groovy.grails.commons.*
import javax.naming.Context;
import gov.nasa.jpl.horizon.sigevent.api.EventType;
import gov.nasa.jpl.horizon.sigevent.api.Response;
import gov.nasa.jpl.horizon.sigevent.api.SigEvent;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.NamingException;
import java.util.Hashtable;


class WebtoolController {

	def authService
	def reassociateService
	
    def index = { 
		 redirect(action: "list", params: params)
		 }
	
	
	def list = {
		if(params.sort == null || params.sort == ''){
			log.debug('setting default params.')
			params.sort='cycle'
			params.order='desc'
		}
		
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[catalogEntryInstanceList: CatalogEntry.list(params), catalogEntryInstanceTotal: CatalogEntry.count()]
		}
	
	def login = {
		
		 }
	
	
	def doLogin = {
		
		def user = params.user
		def pass = params.password
		
	
		log.debug("logging in as $user");
		
		if(authService.authenticate(user,pass)){
			session.user = user;
			session.loggedIn = true
			response.status = 200;
			redirect(action: 'list')
		}
		else{   
			flash.error = "Unable to authenticate! Check your username and password and try again."
			response.status = 404;
			redirect(uri: '/webtool/login.html')
		}
		
	}
	
	def logout = {
		session.user = null;
		session.loggedIn = false
		log.debug("Successfull logged out.");
		flash.message = "Successfully logged out."
		response.status = 200;
		redirect(uri: '/webtool/login.html')
	}
	
	
	def rollBack = {
		def cycle = params.cycle;
		def auth = params.auth;
		
		def ca = CatalogEntry.findByCycleAndAuthor(cycle,auth);
		
		if(ca == null){
		    flash.message = "Could not find Cycle/Author [$cycle/$auth]"
		    response.status = 404;
                    redirect(action: 'list')	
		}		

		log.debug("Undoing-reassociate.")
		ca.getGranules().each { 
			log.debug("Reassociating granuleId ${it.getGranuleId()}");
			def datasetName;
				
			if(it.isMoved())
			{
				log.debug("unrolling-off granule.");
				reassociateService.reassociateGranule(it.getGranuleId(),"${it.getOriginalDataset()}")
                                it.setMoved(false);
				it.save(flush:true);
			
			}
		}
		ca.setNasaApproval(false)
		ca.setCnesApproval(false)
		ca.setGdrDate(null)
		ca.setSgdrDate(null)
		ca.setSgdrncDate(null)
		ca.setGdrncDate(null)
		ca.setSshancDate(null)

		flash.message = "Successfully rolled back Cycle/Author [$cycle/$auth]."
		response.status = 200;
                redirect(action: 'list')
}
	
	def approve = {
		def cycle = params.cycle;
		def auth = params.auth;
		def cb = params.cb;
		def check = params.check;
		def bool = true;
		
		if(params.check == "false")
			bool = false;
		
		
		log.debug("$cycle:$cb:$auth:$check");
		def ca = CatalogEntry.findByCycleAndAuthor(cycle,auth);
		if(cb == "NASA"){
			log.debug("updating NASA");
			ca.setNasaApproval(bool);
		}
		else if (cb == "CNES")
		{
			log.debug("updating CNES");
			ca.setCnesApproval(bool);
			
		}
		if(!ca.getCnesApproval() && !ca.getNasaApproval()){
			ca.setApprover(null);
		}
		else{
			ca.setApprover(session.user);
		}
		
		if(ca.getCnesApproval() && ca.getNasaApproval()){
			//both approved. set gdr date
			ca.setGdrDate(new Date().time)
			
			//create hash of datasets
			def datasets = [:]
			ConfigurationHolder.config.gov.nasa.podaac.j1slx.dataset.each {
				//log.debug("$it");
				it.each{ 
					log.debug("SPLIT: ${it.key},${it.value}");
					datasets."${it.value}" = "${it.key}"
				}
				
				//log.debug("SPLIT:" +  it.split('=').each{ });
				//datasets."${mp[1]}" = mp[0]
			}
			
			log.debug("datasets:  $datasets")
			
			def successMessages = "";
			def errorMessages = "";
			ca.getGranules().each { 
					log.debug("Reassociating granuleId ${it.getGranuleId()}");
					def datasetName = it.getOpenDataset();
//					if(it.isGdr())
//						datasetName = (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.dataset.gdr
//					else
//						datasetName = (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.dataset.sgdr
					
					
					//[success:boolean,message:String];	
					def mp = reassociateService.reassociateGranule(it.getGranuleId(),datasetName)
					if(mp.success)
					{	
						successMessages += mp.message + "\n";
						def type = datasets."$datasetName"
						log.debug("Reassociate successful. $type/$datasetName");
						switch(type){
							case "sgdr":
								if(ca.getSgdrDate() == null){
									ca.setSgdrDate(new Date().time)
								}
								break;
							
							case "gdr":
								if(ca.getGdrDate() == null){
									ca.setGdrDate(new Date().time)
								}
								break;
							
							case "gdr_netcdf":
								if(ca.getGdrncDate() == null){
									ca.setGdrncDate(new Date().time)
								}
								break;
							
							case "sgdr_netcdf":
								if(ca.getSgdrncDate() == null){
									ca.setSgdrncDate(new Date().time)
								}
								break;
							
							case "ssha_netcdf":
								if(ca.getSshancDate() == null){
									ca.setSshancDate(new Date().time)
								}
								break;
						}
						
						it.setMoved(true)
						it.save(flush:true)
					}else{
						errorMessages += mp.message + "\n";
					}
					
				 }
				//send Error message
				if(errorMessages != ''){
					reassociateService.sendSigEvent(EventType.Error, "J1SLX Error Report", errorMessages )
				}
			
				//send Success messages
				if(successMessages != ''){
					reassociateService.sendSigEvent(EventType.Info, "J1SLX Error Report", successMessages )
				}
			
		}
		
		if(!ca.save(flush:true)){
			log.debug("Error saving entry.");
			response.status = 200;
		}
		else{
		log.debug("Retruning ok!");
		response.status = 200;
		}
		
		render "ok"
	
	}
	
	
	
}
