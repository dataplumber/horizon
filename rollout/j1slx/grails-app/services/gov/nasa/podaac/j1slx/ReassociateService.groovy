package gov.nasa.podaac.j1slx;

import java.util.Date;

import gov.nasa.jpl.horizon.sigevent.api.EventType;
import gov.nasa.jpl.horizon.sigevent.api.Response;
import gov.nasa.jpl.horizon.sigevent.api.SigEvent;
import gov.nasa.podaac.archive.core.Reassociate;
import org.codehaus.groovy.grails.commons.*



class ReassociateService {

    static transactional = true

    def reassociateGranule = { gId, toDataset ->
		log.debug("Reassociating granule[$gId] to $toDataset");

			System.setProperty("inventory.ws.url", (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.inventory.host);
			System.setProperty("inventory.ws.user", (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.inventory.user);
            System.setProperty("inventory.ws.password", (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.inventory.pass);

		
		Reassociate r = new Reassociate();
		def testMode = ConfigurationHolder.config.gov.nasa.podaac.j1slx.testReassociate
		log.debug("testmode set to $testMode.")
		if(testMode == true)
			r.setTestMode(true)
		else	
			r.setTestMode(false)	
		
		def response = r.granuleReassociate(gId,toDataset)
		def retType = (String)response.type
		def retMsgs= response.msgs
			
		if(retType.equals("success")){
			log.debug("Return type: $retType")
			def desc = " Dataset [dataset_id] $toDataset Granule: granule_name [$gId] relocated to Dataset [dataset_id] $toDataset"
			def data = "JASON-1 granule [$gId] was successfull reassociated to $toDataset at "+ new Date().time+"\n";
			retMsgs.each { 
				data += "$it\n"
				 }			
			return [success:true,message:data];
		}
		else
		{
			log.debug("Return type: $retType")
			def desc = "Dataset [dataset_id] $toDataset Granule: FAILED! granule_name [$gId] not relocated to Dataset [dataset_id] $toDataset"
			def data = "JASON-1 granule [$gId] failed reassociation to $toDataset at "+ new Date().time +"\n"
			retMsgs.each {
				data += "$it\n"
				 }
			//sendSigEvent(EventType.Error, desc, data);
			return [success:false,message:data];	
		}
    }
	
	def sigURL = (String)ConfigurationHolder.config.gov.nasa.podaac.j1slx.sig.url
	def sigCat = (String) ConfigurationHolder.config.gov.nasa.podaac.j1slx.sig.category
	def machineName =   InetAddress.getLocalHost().getHostName();
	
	def sendSigEvent = { et, desc, data ->
		log.debug("Sending sigEvent");
		SigEvent se = new SigEvent(sigURL);
		//shortname should be either updated or added...
		Response r = se.create(et, sigCat, "J1SLX", "unknown", machineName, desc,  99/*PID*/,data);
		if(r.hasError()){
			log.debug("sig event error: " + r.getError());
		}
	}
	
	
}
