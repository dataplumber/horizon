//package gov.nasa.horizon.archive;
//import gov.nasa.horizon.archive.tool.AipSubscribe;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import junit.framework.TestCase;
//
//
//public class AIPSubscribeTest extends TestCase {
//
//	/**
//	 * This is a unit test to subscribe to Ingest for AIP.
//	 */
//	public void testAIPSubscribe(){
//
//		System.out.println("Starting AIP Subscriber");
//        try{
//        	AipSubscribe aipSub = new AipSubscribe();
//
//            // Read from command line
//            BufferedReader commandLine = new 
//                  java.io.BufferedReader(new InputStreamReader(System.in));
//
//            // Loop until the word "exit" is typed
//            while(true){
//                String s = commandLine.readLine( );
//                if (s.equalsIgnoreCase("exit")){
//                    aipSub.getJms().disconnect();	// close down connection
//                    System.exit(0);			// exit program
//                }
//            }
//        } catch (Exception e){ e.printStackTrace( ); }
//	}
//}
