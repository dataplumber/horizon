import gov.nasa.horizon.common.api.util.DateTimeUtility
import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysNotify

/**
 * EventMessageText
 */
public class EventMessageText extends EventMessage {
   protected String constructDescription(List<SysEvent> events, SysNotify notify) {
      def list = []
      events.each {event ->
         def map = [:]
         addDescription(map, event)
         addDataSize(map, event)
         
         list.add(map)
      }
      
      return construct(list)
   }
   
   protected String constructComplete(List<SysEvent> events, SysNotify notify) {
      def list = []
      events.each {event ->
         def map = [:]
         addDescription(map, event)
         addDataSize(map, event)
         addContent(map, event)
         
         list.add(map)
      }
      
      return construct(list)
   }
   
   private String construct(List<Map> events) {
      String message = "Message:\n"
      events.each {event ->
         message += "\tEvent:\n"
         
         event.each {key, value ->
            message += "\t\t"+key+": "+value+"\n"
         }
      }
      
      return message
   }
   
   private void addDescription(Map parameters, SysEvent event) {
      parameters.put("Description", event.description)
   }
   
   private void addContent(Map parameters, SysEvent event) {
      parameters.put("Id", event.id)
      parameters.put("Computer", event.computer)
      parameters.put("FirstReceived", DateTimeUtility.parseDate(DateTime.UTC_FORMAT, new Date(event.firstReceived)))
      parameters.put("LastReceived", DateTimeUtility.parseDate(DateTime.UTC_FORMAT, new Date(event.lastReceived)))
      parameters.put("Occurrence", event.occurrence)
      parameters.put("Provider", event.provider)
      
      String pid = ""
      if(event.pid) {
         pid = event.pid.toString()
      }
      parameters.put("Pid", pid)
      
      String data = ""
      if(event.data) {
         data = event.data
      }
      parameters.put("Data", data)
      
      String resolution = ""
      if(event.resolution) {
         resolution = event.resolution
      }
      parameters.put("Resolution", resolution)
      
      String resolvedAt = ""
      if(event.resolvedAt) {
         resolvedAt = DateTimeUtility.parseDate(DateTime.UTC_FORMAT, new Date(event.resolvedAt))
      }
      parameters.put("ResolvedAt", resolvedAt)
   }
   
   private void addDataSize(Map parameters, SysEvent event) {
      long dataSize = (event.data) ? event.data.length() : 0
      parameters.put("DataSize", dataSize)
   }
}
