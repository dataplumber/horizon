package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.Constants
import gov.nasa.horizon.ingest.api.Session
import gov.nasa.horizon.ingest.api.SessionException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class OperationsTest extends GroovyTestCase {
  private static Log log = LogFactory.getLog(OperationsTest.class)


  void testListManagerOffline() {
     String productType = "xyz"
     String federation = "gibsDev"
     String domainFile = System.getProperty(Constants.PROP_DOMAIN_FILE)
     
     int transactionId = -1
     
     println ("domainFile = ${domainFile}")
     Session session = new Session(domainFile)
     
     assert session.domain.default == federation
     
     assert session.transactionCount == 0
     try {
        transactionId = session.list(productType, ['xyz'] as String[])
     } catch (SessionException e) {
        assert e.getErrno() == Errno.CONN_FAILED
        assert true
     }
     
     assert transactionId == -1
     
     assert session.transactionCount == 0
     
     assert session.result() == null
     
     transactionId = session.close()
     
     assert transactionId == -1
     
     assert session.result() == null
     
     assert session.transactionCount == 0
  }
  
  void testListInvalidProductType() {
     String productType = "xyz"
     String federation = "gibsDev"
     String domainFile = System.getProperty(Constants.PROP_DOMAIN_FILE)
     
     int transactionId = -1
     int transactionIdCounter = 0
     
     println ("domainFile = ${domainFile}")
     Session session = new Session(domainFile)
     
     assert session.domain.default == federation
     
     assert session.transactionCount == 0
     transactionId = session.list(productType, ['xyz'] as String[])
     assert transactionId == ++transactionIdCounter
     
     assert session.transactionCount == 1
     while (session.transactionCount > 0) {
        def result = session.result()
        if (result) {
          assert result.errno == Errno.DENIED
          
          String description = "Unable to retrieve product type $productType for federation $federation."
          assert result.description.trim() == description
        }
     }
     
     assert session.result() == null
     
     assert session.transactionCount == 0
     
     transactionId = session.close()
     
     assert transactionId == ++transactionIdCounter
     
     assert session.transactionCount == 1
     while (session.transactionCount > 0) {
        def result = session.result()
        if (result) {
          assert result.errno == Errno.OK
          assert result.description.trim() == "Connection closed."
        }
     }
     
     assert session.result() == null
     
     assert session.transactionCount == 0
  }
}
