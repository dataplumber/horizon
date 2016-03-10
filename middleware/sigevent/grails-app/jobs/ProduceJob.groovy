import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysEventGroup
import gov.nasa.horizon.sigevent.SysNotify

/**
 * ProduceJob
 */
class ProduceJob {
   /*
   def startDelay = 30000l
   def timeout = 1000 * 60 * 1
   */
   static triggers = {
      simple name: "ProduceJob", startDelay: 30000, repeatInterval: 1000 * 60 * 1
   }
   def concurrent = false
   def sysProduceJobService

   def execute() {
      try {
         sysProduceJobService.produce(false)
      } catch(Exception exception) {
         log.debug("Exception in ProduceJob.execute()", exception)
      }
      
      try {
         sysProduceJobService.produce(true)
      } catch(Exception exception) {
         log.debug("Exception in ProduceJob.execute() isRemind = true", exception)
      }
   }
}
