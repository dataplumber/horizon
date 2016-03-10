package gov.nasa.horizon.manager.job

/**
 * Created by thuang on 8/13/15.
 */
class ProcessInventoriedJob {
   static triggers = {
      simple name: "ProcessInventoriedJob", startDelay: 60000, repeatCount: 0
   }
   def inventoryService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      try {
         def methodTime = new Date()
         inventoryService.processInventoried()
         log.debug("[LOOKATTHIS]job-inventory: processInventoried: " + (new Date().time - methodTime.time))
      } catch (Exception exception) {
         log.debug("Exception caught in processInventoried", exception)
      } finally {
         try {
            Thread.sleep(1000)
            ProcessInventoriedJob.schedule(1000, 0, [ProcessInventoriedJob:"rescheduled Job"])
         } catch (Exception exception) {
            log.debug("Exception caught in rescheduling ProcessInventoriedJob", exception)
         }
      }
   }
}
