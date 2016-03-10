package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.Request
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


class Result extends IngestProtocol {

   private static final Log log = LogFactory.getLog(Result.class)

   boolean endOfTransaction = false
   Request request

   Result() {
      super()
   }

   Result (Errno errno, String description) {
      super()
      this.errno = errno
      this.description = description
   }

   Result(Request request) {
      super()
      this.request = request
   }
}
