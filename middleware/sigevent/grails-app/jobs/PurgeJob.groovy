import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysEventGroup

/**
 * PurgeJob
 */
class PurgeJob {
   /*
   def timeout = 60000l
   def startDelay = 60000l
   */
   static triggers = {
      simple name: "PurgeJob", startDelay: 60000, repeatInterval: 60000
   }
   def concurrent = false
   def sysPurgeJobService

   def execute() {
      try {
         sysPurgeJobService.purge()
      } catch(Exception exception) {
         log.debug("Exception in PurgeJob.execute()", exception)
      }
   }
}
