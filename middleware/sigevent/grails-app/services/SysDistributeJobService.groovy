import gov.nasa.horizon.sigevent.SysOutgoing

class SysDistributeJobService {
   private static final int MaxRows = 50
   boolean transactional = false
   def sysOutgoingService

   def distribute() {
      def outgoings = SysOutgoing.list(sort: "created", order: "asc", max: SysDistributeJobService.MaxRows)
      outgoings.each {outgoing ->
         outgoing.discard()
      
         log.debug("processing: "+outgoing.id+"@"+outgoing.created)
         try {
            sysOutgoingService.publish(outgoing.id)
         } catch(Exception exception) {
            log.debug("failed to publish: ", exception)
         }
      }
   }
}
