package gov.nasa.podaac.distribute.test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import gov.nasa.podaac.distribute.ems.EMSReport;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetSoftware;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.Provider;

import org.junit.Test;


public class EMSTest {

	/*
	 * Variables Go Here
	 */
	public String GRANULENAME = "grName";
	
	
	/*
	 * Tests/Methods
	 */
	@Test
	public void testPrintRecords(){
		String result;
		boolean archive = false;
		
		//Null grnaule
		Granule g = null;
		result = EMSReport.formatPrint(g, archive);
		assert result == null;
		
		
		//Initilized, empty granule
		g  = new Granule();
		//test empty granule
		EMSReport.formatPrint(g,archive);
		assert result == null;
		
		//Correct Granule
		g = new Granule (GRANULENAME,new Date(), new Date((new Date()).getTime() - 10000), new Date(), new Date(), new Date(),
				new Date(), 1, "read", "GZIP", "GZIP", "MD5",GranuleStatus.OFFLINE, null, "/store/temp", "2009/334");
		

		g.setGranuleId(1001);
		
		Dataset d = new Dataset();
		d.setShortName("dsShortName");
		d.setDatasetId(2002);
		
		Provider p = new Provider();
		p.setShortName("dsProvider");
		d.setProvider(p);
		
		DatasetSoftware ds = new DatasetSoftware();
		ds.setSoftwareVersion("SoftwareVersion 2.0.1");
		
		Set<DatasetSoftware> softwareSet = new HashSet<DatasetSoftware>();
		softwareSet.add(ds);
		System.out.println(softwareSet.size());
		
		d.setSoftwareSet(softwareSet);
		g.setDataset(d);
		g.getDataset().getSoftwareSet();
		
		//create archive(s)
		GranuleArchive ga1 = new GranuleArchive();
		GranuleArchive ga2 = new GranuleArchive();
		
		ga1.setName("grData1");
		ga1.setType("DATA");
		ga1.setFileSize(10000L);
		ga1.setStatus("ONLINE");
		ga2.setName("grData2");
		ga2.setType("DATA");
		ga2.setFileSize(10000L);
		ga2.setStatus("ONLINE");
		
		//g.setArchiveTimeLong(g.getArchiveTimeLong() +10000);
		
		g.getGranuleArchiveSet().add(ga1);
		g.getGranuleArchiveSet().add(ga2);
		System.out.println("ArchiveSize: " + g.getGranuleArchiveSet().size());
		
		//create GMH
		GranuleMetaHistory gmh = new GranuleMetaHistory();
		gmh.setLastRevisionDate(new Date());
		g.getMetaHistorySet().add(gmh);
		
		result = EMSReport.formatPrint(g,archive);
		System.out.println("result:\n\t" + result);
		assert result != null;
		
	}
	
	
}
