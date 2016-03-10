/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.ingest.client

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class IngestClientTest extends GroovyTestCase {
   private static final String[] ARGUMENTS = [
      "login"
   ] as String[]
   
   public void test() {
      println "Arguments: "+ARGUMENTS.join(" ")
      
      try {
         IngestClient ingestClient = new IngestClient()
         ingestClient.run(ARGUMENTS)
      } catch(exception) {
         exception.printStackTrace()
      }
   }
}
