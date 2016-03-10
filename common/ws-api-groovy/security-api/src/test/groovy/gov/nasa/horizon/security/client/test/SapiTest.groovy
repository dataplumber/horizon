package gov.nasa.horizon.security.client.test;

import gov.nasa.horizon.security.client.Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gov.nasa.horizon.security.client.SecurityAPIFactory
import gov.nasa.horizon.security.client.api.*
import groovy.lang.GroovyObject;


class SapiTest extends GroovyTestCase {
	
	private static Log log = LogFactory.getLog(this)
	private static String GOOD_PASS = '';
	private static String BAD_PASS = 'bad';
	private static String REALM = 'J1SLX'
	
	
	public void testFileTest(){
		def client = new Client();
//		def mp = client.readMaple();
//		println "MappleFile: $mp"
//		
//		mp.J1SLX="ABCD"
//		client.writeMaple(mp)
		
		//client.run()
	}
	
	public void testTokenTest(){
		
		def api =  SecurityAPIFactory.getInstance()
		System.out.println("-----------------------");
		System.out.println("Generate Token Test");
		System.out.println("-----------------------");
			
		System.out.println("Running tokenize, configured realm")
		def token = api.tokenize("gangl", GOOD_PASS);
		System.out.println("Genereted: $token")
		
		
		System.out.println("Running tokenize, bad pass and configured realm")
		token = api.tokenize("gangl2", BAD_PASS);
		System.out.println("Genereted: $token")
		System.out.println("Should have genereted null...")
		
		System.out.println("Running tokenize, explicit realm")
		token = api.tokenize("gangl", GOOD_PASS, REALM);
		System.out.println("Genereted: $token")
		
		
		System.out.println("Running tokenize, bad pass and explicit realm")
		token = api.tokenize("gangl2", BAD_PASS, REALM);
		System.out.println("Genereted: $token")
		System.out.println("Should have genereted null...")
		
	}
	
	public void testSapiTest(){
		println '''
-----------------------------------
**  Running the SAPI test suite  **
-----------------------------------
		'''
	
		System.out.println("Test Authentication failed:");
		def api =  SecurityAPIFactory.getInstance()
		
//		assertFalse(api.authenticate("gangl", BAD_PASS))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Successful Authenticate with configured realm")
//		assertTrue(api.authenticate("gangl", GOOD_PASS))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authenticate with explicit realm")
//		assertTrue(api.authenticate("gangl", GOOD_PASS, REALM))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authenticate with NullPassword")
//		assertTrue(api.authenticate("gangl", null))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authenticate with NullPassword and explicit realm")
//		assertTrue(api.authenticate("gangl", null, REALM))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("-----------------------");
//		System.out.println("Running Authorize Tests");
//		System.out.println("-----------------------");
//		
//		System.out.println("Running Authorize with explicit realm")
//		assertTrue(api.authorize("gangl", "USER", REALM))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authorize with explicit realm")
//		assertFalse(api.authorize("gangl", "ADMIN", REALM))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authorize with configured realm")
//		assertTrue(api.authorize("gangl", "USER"))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authorize with configured realm")
//		assertFalse(api.authorize("gangl", "ADMIN"))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authorize with configured realm")
//		assertFalse(api.authorize("gangl2", "ADMIN"))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authorize with explicit realm")
//		assertFalse(api.authorize("gangl2", "ADMIN", REALM))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authorize with unknown group and explicit realm")
//		assertFalse(api.authorize("gangl2", "UKNOWNGROUP", REALM))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running Authorize with unknown group and configured realm")
//		assertFalse(api.authorize("gangl2", "UKNOWNGROUP"))
//		System.out.println("Test status: Passed");
//		
//		System.out.println("-----------------------");
//		System.out.println("Running ListRoles Tests");
//		System.out.println("-----------------------");
//		
//		List<String> list = null;
//		System.out.println("Running AuthorizedRoles with explicit realm")
//		list = api.getRoles("gangl", REALM)
//		if(list?.size() <= 0)
//			assertFalse(true);
//		System.out.println("Test status: Passed");
//		
//		System.out.println("Running AuthorizedRoles with configured realm")
//		list = api.getRoles("gangl")
//		if(list?.size() <= 0)
//			assertFalse(true);
//		System.out.println("Test status: Passed");
//		
//		System.out.println("-----------------------");
//		System.out.println("Generate Token Test");
//		System.out.println("-----------------------");
//			
//		System.out.println("Running tokenize, configured realm")	
//		def token = api.tokenize("gangl", GOOD_PASS);
//		System.out.println("Genereted: $token")
//		
//		
//		System.out.println("Running tokenize, bad pass and configured realm")
//		token = api.tokenize("gangl", BAD_PASS);
//		System.out.println("Genereted: $token")
//		System.out.println("Should have genereted null...")
//		
//		System.out.println("Running tokenize, explicit realm")
//		token = api.tokenize("gangl", GOOD_PASS, REALM);
//		System.out.println("Genereted: $token")
//		
//		
//		System.out.println("Running tokenize, bad pass and explicit realm")
//		token = api.tokenize("gangl", BAD_PASS, REALM);
//		System.out.println("Genereted: $token")
//		System.out.println("Should have genereted null...")
		
	}
}
