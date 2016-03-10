import gov.nasa.horizon.sigevent.SysOutgoing

/**
 * DistributeJob
 */
class DistributeJob {
   /*
   def startDelay = 45000l
   def timeout = 30000l
   */
   static triggers = {
      simple name: "DistributeJob", startDelay: 45000, repeatInterval: 30000
   }
   def concurrent = false
   def sysDistributeJobService

   def execute() {
      try {
         sysDistributeJobService.distribute()
      } catch(Exception exception) {
         log.debug("Exception in DistributeJob.execute()", exception)
      }
   }
}
