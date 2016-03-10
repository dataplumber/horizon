package gov.nasa.horizon.ingest.server

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.restlet.Context
import org.restlet.Finder
import org.restlet.Server
import org.restlet.data.Protocol

class ServerPortListener implements ServiceListener {
   private static Log log = LogFactory.getLog(ServerPortListener.class)

   Server server

   String type
   String protocol
   String address
   int port
   int maxCon
   String uriPattern

   int sessionCntrl = 0
   JobPool jobPool

   def add = {}
   def delete = {}
   def move = {}
   def ping = {}
   def checkJob = {}

   def ServerPortListener(String type) {
      super()
      this.type = type
      log.debug("Server Port ${type} created")
   }

   def ServerPortListener(String type,
                          int maxCon,
                          String protocol,
                          String address,
                          int port,
                          String uriPattern) {

      super()
      this.type = type
      this.maxCon = maxCon

      this.protocol = protocol
      this.port = port
      this.uriPattern = uriPattern
      log.debug("Server Port ${type} created")
   }

   void start() {
      try {
         if (this.maxCon < 1 || this.maxCon > 256) {
            throw new Exception("Max connection must be between 1..255.")
         }
         Context context = new Context()
         jobPool = new JobPool(this.maxCon)
         context.attributes.listener = this
         def finder = new Finder(context, ServerPortHandler.class)
         server = new Server(Protocol.valueOf(protocol), address, port, finder)
         server.start()
      } catch (Exception e) {
         log.error(e.message, e)
      }
   }

   void stop() {
      try {
         if (server && server.started) {
            server.stop()
            jobPool.shutdown()
            /*
            int tries = 1
            while (tries < 0 && threadPool.activeCount != 0) {
               log.info("Waiting for active threads to terminate.")
               threadPool.awaitTermination(6, TimeUnit.SECONDS)
               ++tries
            }
            if (threadPool.activeCount != 0) {
               log.warn("Giving up on waiting for active threads to terminate.")
               threadPool.shutdownNow()
            }
            */
         }
      } catch (Exception e) {
         log.error(e.message, e)
      }
   }

   Map findJob(String name) {
      return jobPool.getJob(name)
   }
}
