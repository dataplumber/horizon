package gov.nasa.horizon.manager.job

/**
 * Created by thuang on 8/13/15.
 */
class ProcessArchivesJob {
   static triggers = {
      simple name: "ProcessArchivesJob", startDelay: 60000, repeatCount: 0
   }
   def transactionService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      try {
         def methodTime = new Date()
         transactionService.processArchives()
         log.debug("[LOOKATTHIS]job-archive: processArchives: " + (new Date().time - methodTime.time))
      } catch (Exception exception) {
         log.debug("Exception caught in processArchives", exception)
      } finally {
         try {
            Thread.sleep(1000)
            ProcessArchivesJob.schedule(1000, 0, [ProcessArchivesJob:"rescheduled Job"])
         } catch (Exception exception) {
            log.debug("Exception caught in rescheduling ProcessArchivesJob", exception)
         }
      }
   }
}
