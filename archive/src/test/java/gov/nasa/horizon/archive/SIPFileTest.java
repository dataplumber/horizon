////Copyright 2008, by the California Institute of Technology.
////ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//package gov.nasa.horizon.archive;
//
//
//import gov.nasa.horizon.archive.core.ArchiveProperty;
////import gov.nasa.horizon.archive.core.JMSSession;
//import gov.nasa.horizon.common.api.serviceprofile.ArchiveProfile;
//import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile;
//import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException;
//import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileFactory;
//import gov.nasa.horizon.inventory.api.Inventory;
//import gov.nasa.horizon.inventory.api.InventoryFactory;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Enumeration;
//import java.util.Properties;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//
///**
// * This class reads in a SIP file, creates archive, and archive files.
// * Assuming Dataset & policies are already populated in the database;
// *
// * @author clwong
// *
// * @version
// */
//public class SIPFileTest extends TestCase {
////
////	@Before
////	public void setUp() {
////		ArchiveProperty.getInstance();
////		Properties p = new Properties();
////		try {
////			p.load(new FileInputStream("test.properties"));
////		} catch (FileNotFoundException e) {
////			e.printStackTrace();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////		Enumeration propNames = p.propertyNames();
////		while (propNames.hasMoreElements()) {
////			String name = propNames.nextElement().toString();
////			System.setProperty(name, p.getProperty(name));
////		}
////	}
////
////	/**
////	 * This is a unit test to send an AIP to Archive Subscriber.
////	 */
////	public void testSendAIP() {
////
////		ServiceProfile sp = null;
////		String sipFilename = System.getProperty("test.sipfile");
////		System.out.println(sipFilename);
////		try { 
////			sp = ServiceProfileFactory.getInstance()
////			.createServiceProfileFromMessage(new File(sipFilename)); 
////		} catch (IOException e) { 
////			e.printStackTrace(); 
////		} catch (ServiceProfileException e) { 
////			e.printStackTrace(); 
////		}
////		ArchiveProfile ap = sp.getProductProfile().getArchiveProfile();
////		if (ap == null) {
////			System.out.println("Generate AIPs based on "+sipFilename);
////			try {
////				Inventory inventory = InventoryFactory.getInstance()
////				.createInventory();
////				inventory.storeServiceProfile(sp);
////				sp.toFile(sipFilename + ".archive");
////			} catch (Exception e) {
////				System.out.println(e.getMessage());
////			}
////		}
////
////		// Compose ByteMessage
//////		try {
//////			JMSSession jms = new JMSSession(ArchiveProperty.AIP_POST);
//////			jms.publish(sp.toString());
//////			jms.disconnect();
//////
//////		} catch (Exception e) {
//////			System.getProperties().list(System.out);
//////			e.printStackTrace();
//////		}
////		System.out.println("Test Complete - aip sent");
////
////	}
//}
