package gov.nasa.horizon.inventory.api;

import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile;
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileFactory;

import java.io.File;

public class InventoryApiGroovyTest extends GroovyTestCase{

	def HOST = "http://localhost:9192";
	
	public void testFetchProductTypes() {
		println "Running Fetch Datasets Test"
		InventoryApi api = new InventoryApi(HOST);
		List<Map<String,String>> providers = api.productTypes();
		for(Map<String, String> p :providers){
			for(String k:p.keySet()){
				System.out.print(p.get(k) + " --- ");
			}
			System.out.println();
		}
	}
	
//	public void testFetchProviders(){
//		println "Running Fetch Providers Test"
//		InventoryApi api = new InventoryApi(HOST);
//		def providers = api.getProviders();
//		providers.each{
//			for(String k:it.keySet()){
//				System.out.print(it.get(k) + " --- ");
//			}
//			System.out.println();
//		}
//	}
	
	public void testUpdateProductStatus(){
		println "Running update Granule Status"
		InventoryApi api = new InventoryApi(HOST);
		def ds = api.updateProductStatus(1,"OFFLINE","gibs","gibs");
		assert ds == true
		
		api.setAuthInfo("axt","axt388")
		ds = api.updateProductStatus(1,"ONLINE");
		
	}
	
	public void testFetchDataset()
	{
		println "Running Fetch Dataset (15) Test"
		InventoryApi api = new InventoryApi(HOST);
		def ds = api.getDataset(15);
		if(ds == null)
			assert false;
		ds.locationPolicySet.datasetLocationPolicy.each {
			println "${it.basePath} ---"
		}
		
		ds = api.getDataset("NAVO-L2P-AVHRR18_L");
		if(ds == null)
			assert false;
		ds.locationPolicySet.datasetLocationPolicy.each {
			println "${it.basePath} ---"
		}
		
		ds = api.getDataset("PODAAC-GH18L-2PN01");
		if(ds == null)
			assert false;
		ds.locationPolicySet.datasetLocationPolicy.each {
			println "${it.basePath} ---"
		}
		
	}
	
	public void testFetchCoverage(){
		println "Running Fetch Dataset (15)  Coverage Test"
		InventoryApi api = new InventoryApi(HOST);
		def cov = api.getDatasetCoverage(15)
		println cov.stopTimeLong
		println cov.startTimeLong
		
	}
	
	public void testFetchProvider(){
		println "Running Fetch Provider (15)Test"
		InventoryApi api = new InventoryApi(HOST);
		def cov = api.getProvider(15)
		println cov.shortName
		println cov.type
		
	}
	
	public void testFetchPolicy(){
		println "Running Fetch Dataset (15) Policy Test"
		InventoryApi api = new InventoryApi(HOST);
		def pol = api.getDatasetPolicy(15)
		println pol.dataClass
		println pol.dataFormat
		
	}
	
	public void testFetchLatestGranule(){
		println "Running Fetch Dataset (46) Latest Granule Test"
		InventoryApi api = new InventoryApi(HOST);
		def gId = api.getLatestGranuleIdByDataset(46)
		println "GranuleId: $gId"
	}
	
	public void testFetchGranule(){
		println "Running fetchGranule test"
		InventoryApi api = new InventoryApi(HOST);

		def gran = api.getGranuleByName("SMM_DRA_AXVCNE20110506_045328_20110506_000000_20110506_000000")
		println "Returned GranuleName: ${gran.name.text()}"
		gran = api.getGranuleById(3729599)
		println "Returned GranuleName: ${gran.name.text()}"
	}

	public void testFetchGranuleByNameAndDataset(){
		println "Running fetchGranuleByNameAndDataset test"
		InventoryApi api = new InventoryApi(HOST);
		def gran = api.getGranuleByNameAndDataset("SMM_DRA_AXVCNE20110506_045328_20110506_000000_20110506_000000",46)
		println "Returned GranuleName: ${gran.name.text()}"
		
		
	}
		
	public void testFetchGranuleArchivePath(){
		println "Running fetchGranuleArchivePath (3729599)"
		InventoryApi api = new InventoryApi(HOST);
		def gId = api.getGranuleArchivePath(3729599)
		println "Returned ArchivePath: $gId"
		
	}
	
	public void testProcessSip(){
		println "Running processSip..."
		String filename = System.getProperty("ingest.sip.test.filename");
		System.out.println("Use File: "+filename);
		ServiceProfile serviceProfile =
			ServiceProfileFactory.getInstance().
			createServiceProfileFromMessage(new File(filename));
	
			InventoryApi api = new InventoryApi(HOST);
			
			def gId = api.ingestSip(serviceProfile,"axt","axt388");
			api.setAuthInfo("axt","axt388")
			gId = api.ingestSip(serviceProfile);
			gId.getProductProfile().getArchiveProfile().getGranules().each{
				println "Gran Name: ${it.getName()}"
				it.getFiles().each{
					println "\tDestination: ${it.getDestination()}"
				}
			}
	}
	
	
	public static void main(String[] args){
//		InventoryApiGroovyTest iat = new InventoryApiGroovyTest();
//		println "Running Fetch Datasets Test"
//		//iat.testFetchDatasets();
//		println "Running Fetch Providers Test"
//		//iat.testFetchProviders();
//		iat.testFetchDataset();
	}

}
