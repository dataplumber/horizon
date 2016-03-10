package gov.nasa.podaac.j1slx

import gov.nasa.podaac.j1slx.SystemMetadata;
import gov.nasa.podaac.j1slx.CatalogEntry;
import gov.nasa.podaac.j1slx.CatalogEntryGranule;
import org.codehaus.groovy.grails.commons.*

import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class FetchInventoryDataJob {
	
	def reassociateService
	
   // def timeout = 3600000l // execute job once every hour??
	def concurrent = false
	//def startTimeDelay = ConfigurationHolder.config.gov.nasa.podaac.j1slx.jobStartDelay;
	//def interval = ConfigurationHolder.config.gov.nasa.podaac.j1slx.jobInterval
	
	static triggers = {
		simple name: 'fetchDataTrigger', startDelay: ConfigurationHolder.config.gov.nasa.podaac.j1slx.jobStartDelay*1000, repeatInterval: ConfigurationHolder.config.gov.nasa.podaac.j1slx.jobInterval*1000L //wait a minute, then every minute
	  }
	  def group = "j1slx"
	
    def execute() {
		log.info("Fetching run data...");
		
		def config = ConfigurationHolder.config

		log.debug("Executing fetchDataTask...");
				// execute task
		//fetch the datasets
		String host = (String)config.gov.nasa.podaac.j1slx.inventory.host;
		int port = config.gov.nasa.podaac.j1slx.inventory.port;
		String user = (String)config.gov.nasa.podaac.j1slx.inventory.user;
		String pass = (String)config.gov.nasa.podaac.j1slx.inventory.pass;
		
		Service service = ServiceFactory.getInstance().createService(host,port);
		service.setAuthInfo(user, pass);
		
		def reassocList = [];
		def runTime = new Date().time;
		
		config.gov.nasa.podaac.j1slx.datasets.each {
			//get latest granules from each.
			SystemMetadata meta =  SystemMetadata.findByDatasetName(it.dataset)
			
			if(meta == null){
				log.debug("Run data is null. First time executing jobs.");
				meta = new SystemMetadata();
				
				def configTime = ConfigurationHolder.config.gov.nasa.podaac.j1slx.initialStartTime
				if(configTime == null)
					meta.setLastRunTime(runTime);
				else{
					log.debug("Using configuration for start time: $configTime");
					meta.setLastRunTime(configTime);
				}
			}
			log.debug("checking inventory for new granules in dataset: $it at time: ${meta.getLastRunTime()}")
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(meta.getLastRunTime());
			log.info("Job last run: " + new Date(meta.getLastRunTime()));
			
			
			def d = service.getDataset(it.dataset);
			def ds_version = it.version;
			def ds_mapped = it.mappedTo;
			
			def granuleIds = service.getGranuleIdListAll(d.getDatasetId(),cal,null,null);
		
			//'JASON-1_GDR_NASA'     						'JASON-1_GDR_CNES',
			//'JASON-1_L2_OST_SGDR_VER-C_BINARY_NASA'      	'JASON-1_L2_OST_SGDR_VER-C_BINARY_CNES',
			//--'JASON-1_SGDR_C_NETCDF_NASA'    				'JASON-1_SGDR_C_NETCDF_CNES',
			//--'JASON-1_GDR_NETCDF_C_NASA',					'JASON-1_GDR_NETCDF_C_CNES',
			//--'JASON-1_GDR_SSHA_NETCDF_NASA',				'JASON-1_GDR_SSHA_NETCDF_CNES'
			
			granuleIds.each {
				def granule = service.getGranuleById(it)
				log.debug("Processing granule: ${granule.getName()}")
				def cycle = Integer.valueOf(granule.getRelPath().replace("c",""))
				def author;
				//def isGdr = false;
				
				def openDataset = null;
				
				if(d.getShortName().contains("CNES"))
					author = "CNES"
				else
					author = "NASA"
				
				log.debug("Looking for entry with cycle: $cycle and author: $author")
					
				
				CatalogEntry entry = CatalogEntry.findByCycleAndAuthor(cycle, author)
				log.debug("entry author: " + entry?.getAuthor());
				
				
				//only create based on GDR...
				if(entry == null)
				{
						log.debug("entry [$author/$cycle] doesn't exist. Creating.");
						//entry doesn't exist, create it.
						CatalogEntry e = new CatalogEntry();
						e.setAuthor(author);
						e.setProductVersion(ds_version);
						e.setCycle(cycle);
						e.setGdrArchTime(granule.getArchiveTimeLong())
						
						//save the dataset
						e.save(flush:true);
						entry = e;
				}
				
				def reassociate = false;
				if(entry.isNasaApproval() && entry.isCnesApproval()){
					reassociate = true;
				}
				
				if(ds_mapped.equals('gdr'))
				{
					openDataset = (String)config.gov.nasa.podaac.j1slx.dataset.gdr
					if(reassociate){
						reassocList.add([id:granule.getGranuleId(),ds:openDataset])
						if(entry.gdrDate == null){
							entry.gdrDate = new Date().getTime();
						}
					}
					//isGdr=true
				}
				if(ds_mapped.equals('sgdr')){
					//SGDR 
					entry.setSgdrStaged(true);
					openDataset = (String)config.gov.nasa.podaac.j1slx.dataset.sgdr
					if(reassociate){
						reassocList.add([id:granule.getGranuleId(),ds:openDataset])
						if(entry.sgdrDate == null){
							entry.sgdrDate = new Date().getTime();
						}	
					}
				}
				else if(ds_mapped.equals('sgdr_netcdf')){
					//SGDR NETCDF
					entry.setSgdrncStaged(true);
					openDataset = (String)config.gov.nasa.podaac.j1slx.dataset.sgdr_netcdf
					if(reassociate){
						reassocList.add([id:granule.getGranuleId(),ds:openDataset])
						if(entry.sgdrncDate == null){
							entry.sgdrncDate = new Date().getTime();
						}
					}
				}	
				else if(ds_mapped.equals('gdr_netcdf')){
					//GDR NETCDF
					entry.setGdrncStaged(true);
					openDataset = (String)config.gov.nasa.podaac.j1slx.dataset.gdr_netcdf
					if(reassociate){
						reassocList.add([id:granule.getGranuleId(),ds:openDataset])
						if(entry.gdrncDate == null){
							entry.gdrncDate = new Date().getTime();
						}
					}
				}
				else if(ds_mapped.equals('ssha_netcdf')){
					//ssha
					entry.setSshancStaged(true)
					openDataset = (String)config.gov.nasa.podaac.j1slx.dataset.ssha_netcdf
					if(reassociate){
						reassocList.add([id:granule.getGranuleId(),ds:openDataset])
						if(entry.sshancDate == null){
							entry.sshancDate = new Date().getTime();
						}
					}
				}
				
				CatalogEntryGranule ceg = new CatalogEntryGranule();
				ceg.setGranuleId(granule.getGranuleId())
				//log.debug("setting GDR to $isGdr")
				//	ceg.setGdr(isGdr)
				ceg.setOpenDataset(openDataset)
				ceg.setOriginalDataset(d.getDatasetId())	
				entry.addToGranules(ceg);
				entry.save(flush:true);
			}
			//save the metadata for last run time.
			meta.setDatasetName(it.dataset)
			meta.setLastRunTime(runTime);
			meta.save();	
		}
		
		def successMessages = "";
		def errorMessages = "";
		
		log.debug("Reassociateing files...");
		reassocList.each {
			log.debug("Reassociating ${it.id} to ${it.ds}");
			def mp = reassociateService.reassociateGranule(it.id, it.ds);
			if(mp.success)
			{
				successMessages += mp.message + "\n";
				def ceg2 = CatalogEntryGranule.findByGranuleId(it.id)
                        ceg2.setMoved(true)
                        ceg2.save(flush:true)
			}
			else{
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
		
		
		log.info("Job completed. "+(reassocList.size)+" granules were processed.");
		log.info("Job will run next with time cutoff of: $runTime");
		log.debug("Sleeping...zzZZzzZZzz");
		log.debug("");
    }
}
