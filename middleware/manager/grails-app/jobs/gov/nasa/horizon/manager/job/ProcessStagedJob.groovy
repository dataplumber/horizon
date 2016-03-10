package gov.nasa.horizon.manager.job

/**
 * Created by thuang on 8/13/15.
 */
class ProcessStagedJob {
   static triggers = {
      simple name: "ProcessStagedJob", startDelay: 60000, repeatCount: 0
   }
   def inventoryService
   def concurrent = false
   def sessionRequired = true

   def execute() {

      try {
         def methodTime = new Date()
         inventoryService.processStaged()
         log.debug("[LOOKATTHIS]job-inventory: processStaged: " + (new Date().time - methodTime.time))
      } catch (Exception exception) {
         log.debug("Exception caught in processStaged", exception)
      } finally {
         try {
            Thread.sleep(1000)
            ProcessStagedJob.schedule(1000, 0, [ProcessStagedJob:"rescheduled Job"])
         } catch (Exception exception) {
            log.debug("Exception caught in rescheduling ProcessStagedJob", exception)
         }
      }
   }
}
