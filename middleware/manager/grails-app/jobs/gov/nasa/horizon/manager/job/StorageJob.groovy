package gov.nasa.horizon.manager.job

/**
 * StorageJob
 */
class StorageJob {
   /*
   def startDelay = 60000
   def timeout = 60000
   */
   static triggers = {
      simple name: "StorageJob", startDelay: 60000, repeatCount: 0
   }
   def storageService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      try {
         def methodTime = null
         
         methodTime = new Date()
         storageService.checkStorage()
         log.debug("[LOOKATTHIS]job-storage: checkStorage: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception in StorageJob.", exception)
      } finally {
         try {
            Thread.sleep(1000)
            StorageJob.schedule(1000, 0, [StorageJob:"rescheduled Job"])
         } catch (Exception exception) {
            log.debug("Exception caught in rescheduling StorageJob", exception)
         }
      }
   }
}