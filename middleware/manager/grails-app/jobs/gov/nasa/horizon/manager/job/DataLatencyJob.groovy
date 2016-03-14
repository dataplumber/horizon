package gov.nasa.horizon.manager.job

/**
 * DataLatencyJob
 */
class DataLatencyJob {
   static triggers = {
      simple name: "DataLatencyJob", startDelay: 60000, repeatCount: 0
   }
   def dataLatencyService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      try {
         //def methodTime = null
         
         //methodTime = new Date()
         //dataLatencyService.checkDataLatency()
         //log.debug("[LOOKATTHIS]job-dataLatency: checkDataLatency: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception in DataLatencyJob.", exception)
      } finally {
         try {
            Thread.sleep(1000)
            DataLatencyJob.schedule(1000, 0, [DataLatencyJob:"rescheduled Job"])
         } catch (Exception exception) {
            log.debug("Exception caught in rescheduling DataLatencyJob", exception)
         }
      }
   }
}