package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.Encrypt
import gov.nasa.horizon.ingest.api.Session
import gov.nasa.horizon.ingest.api.SessionException

class SessionTest extends GroovyTestCase{

   //final String url = "http://lanina.jpl.nasa.gov:8080/ingest-grails-0.1/ingest"
   final String url = "http://localhost:8090/ingest-grails/ingest"

   void testSimpleCreate() {
      try {
         Session session = new Session("thuang", Encrypt.encrypt("password"), url)
         session.open()

         session.close()
         println "session closed"
      } catch (SessionException e) {
         println e.message
      }
   }

}
