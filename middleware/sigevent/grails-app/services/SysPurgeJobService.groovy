import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysEventGroup

class SysPurgeJobService {
   private static final int MaxRows = 100
   boolean transactional = false

   def purge() {
      def currentDate = new Date().getTime()
      def events = SysEvent.withCriteria {
         createAlias("group", "g")
         or {
            and {
               isNotNull("resolvedAt")
               sqlRestriction("$currentDate >= (resolved_at + (purge_rate * 24 * 60 * 60 * 1000))")
            }
            and {
               isNull("resolvedAt")
               sqlRestriction("$currentDate >= (first_received + (purge_rate * 24 * 60 * 60 * 1000))")
            }
         }
         maxResults(SysPurgeJobService.MaxRows)
         order("firstReceived", "asc")
      }
      events.each {event ->
            event.discard()
            event = SysEvent.lock(event.id)
            if(event) {
               event.delete(flush: true)
               log.debug("event purged: "+event.id)
            }
      }
   }
}
