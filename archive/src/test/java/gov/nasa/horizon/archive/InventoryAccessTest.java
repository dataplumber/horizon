package gov.nasa.horizon.archive;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.external.InventoryAccess;
import gov.nasa.horizon.archive.external.InventoryFactory;
import gov.nasa.horizon.archive.external.InventoryQuery;
//import gov.nasa.horizon.archive.external.direct.Query;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import gov.nasa.horizon.inventory.api.Constant;
import gov.nasa.horizon.inventory.api.Constant.ProductStatus;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class InventoryAccessTest extends TestCase {

	@Before
    public void setUp() {
        ArchiveProperty.getInstance();
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("test.properties" ));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Enumeration propNames = p.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = propNames.nextElement().toString();
			System.setProperty(name, p.getProperty(name));
		}
    }
	
	
//	/**
//	 * This is a unit test to setting status in GranuleArchive.
//	 */
//	@Test
//	public void testUpdateArchiveStatus() {
//		List<Integer> iList = new ArrayList();
//		iList.add(5);
//		List<AIP>archiveList =  InventoryFactory.getInstance().getQuery().getArchiveAIPByGranule(iList);
//		for (AIP archive : archiveList) archive.setStatus("ONLINE");
//		InventoryFactory.getInstance().getAccess().updateAIPArchiveStatus(archiveList);
//	}
//	
//	/**
//	 * This is a unit test to setting status in Granule.
//	 */
//	@SuppressWarnings("static-access")
//	@Test
//	public void testUpdateGranuleStatus() {
//		List<Integer>idList = new ArrayList<Integer>();
//		for (int i=1; i<=1; i++) idList.add(i);
//		InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(idList, GranuleStatus.ONLINE.toString() );
//	}

}
