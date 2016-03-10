package gov.nasa.horizon.manager.job

class PipelineJob {
   static triggers = {
      simple name: "PipelineJob", group: "pipelineGroup", startDelay: 60000, repeatCount: 0
   }
   def transactionService
   def inventoryService
   def concurrent = false
   def sessionRequired = true
   def group = "pipelineGroup"

   def execute() {
      def methodTime = null

      try {
         methodTime = new Date()
         transactionService.processAdds()
         log.debug("[LOOKATTHIS]job-dispatch: processAdds: " + (new Date().getTime() - methodTime.getTime()))
      } catch (Exception exception) {
         log.debug("Exception caught in processAdds", exception)
      }

      //for (int i in 1..2)
      /** Moved to ProcessStagedJob
         try {
            methodTime = new Date()
            inventoryService.processStaged()
            log.debug("[LOOKATTHIS]job-inventory: processStaged: " + (new Date().getTime() - methodTime.getTime()))
         } catch (Exception exception) {
            log.debug("Exception caught in processStaged", exception)
         }
       **/
      //}

      try {
         methodTime = new Date()
         transactionService.processReplacePurge()
         log.debug("[LOOKATTHIS]job-archive: processReplacePurge: " + (new Date().getTime() - methodTime.getTime()))
      } catch (Exception exception) {
         log.debug("Exception caught in processReplacePurge", exception)
      }

      /** Moved to ProcessInventoriedJob
      try {
         methodTime = new Date()
         inventoryService.processInventoried()
         log.debug("[LOOKATTHIS]job-inventory: processInventoried: " + (new Date().getTime() - methodTime.getTime()))
      } catch (Exception exception) {
         log.debug("Exception caught in processInventories", exception)
      }
      **/

      /** Moved to ProcessArchivesJob
      try {
         methodTime = new Date()
         transactionService.processArchives()
         log.debug("[LOOKATTHIS]job-archive: processArchives: " + (new Date().getTime() - methodTime.getTime()))
      } catch (Exception exception) {
         log.debug("Exception caught in processArchives", exception)
      }
       **/

      try {
         methodTime = new Date()
         transactionService.processDeletes()
         log.debug("[LOOKATTHIS]job-cleaner: processDeletes: " + (new Date().getTime() - methodTime.getTime()))
      } catch (Exception exception) {
         log.debug("Exception caught in processDeletes", exception)
      }

      try {
         methodTime = new Date()
         transactionService.processPurge()
         log.debug("[LOOKATTHIS]job-purge: processPurge: " + (new Date().getTime() - methodTime.getTime()))
      } catch (Exception exception) {
         log.debug("Exception caught in processPurge", exception)
      }
      
      try {
         methodTime = new Date()
         transactionService.checkActiveJob()
         log.debug("[LOOKATTHIS]job-checkActive: checkActiveJob: " + (new Date().getTime() - methodTime.getTime()))
      } catch (Exception exception) {
         log.debug("Exception caught in checkActiveJob", exception)
      }
      
      try {
         Thread.sleep(1000)
         PipelineJob.schedule(1000, 0, [PipelineJob:"rescheduled Job"])
      } catch (Exception exception) {
         log.debug("Exception caught in rescheduled PipelineJob", exception)
      }
   }
}
