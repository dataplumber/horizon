package gov.nasa.horizon.ingest.server

import java.util.concurrent.locks.ReentrantLock

class ServerMonitor {

   private static final INSTANCE = new ServerMonitor()

   private boolean serverInShutdown = false;

   private final ReentrantLock transactionCountLock = new ReentrantLock()

   private int transactionCount = 0

   ServerPortListener userListener
   ServerPortListener adminListener


   static getInstance() { return INSTANCE }

   private ServerMonitor() {}

   /**
    * @param timeout in seconds
    */
   int startShutdown(long timeout) throws InterruptedException {
      transactionCountLock.lock() // block until condition holds
      try {
         serverInShutdown = true
         if (timeout > 0 && transactionCount > 1) {
            transactionCountLock.wait((long) timeout * 1000)
         }

         if (userListener) {
            userListener.stop()
         }
      } finally {
         transactionCountLock.unlock()
      }
      return transactionCount
   }

   boolean incrementTransactionCount() {
      transactionCountLock.lock()
      if (serverInShutdown) {
         transactionCountLock.unlock()
         return false
      }
      ++transactionCount
      transactionCountLock.unlock()
      return true
   }

   void decrementTransactionCount() {
      transactionCountLock.lock()
      if (--transactionCount == 0 &&
            serverInShutdown) {
         transactionCountLock.notify()
      }
      transactionCountLock.unlock()
   }

   Map findUserJob(String name) {
      if (userListener) {
         return userListener.findJob(name)
      }
      return null
   }

   List getUserJobs() {
      List result = []
      if (userListener) {
         result = userListener.jobPool.jobs
      }
      return result
   }

   boolean isServerInShutdown() {
      return serverInShutdown
   }


}
