package gov.nasa.horizon.archive;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.core.Command;
import gov.nasa.horizon.archive.core.Reassociate;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

public class ArchiveToolTest extends TestCase {

	private static Log log = LogFactory.getLog(ArchiveToolTest.class);	
	
	@Before
    public void setUp() {
        ArchiveProperty.getInstance();
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(System.getProperty("test.properties")));
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
	
//	public void testReassociateGranule(){
//		log.info("testing");
//	
//		Reassociate r = new Reassociate();
//		r.setTestMode(false);
//		r.granuleReassociate(400, "JASON-1_L2_OST_GDR_Ver-C_Binary");
//		
//		//JASON-1_GDR_NASA
//		
//	}
}
