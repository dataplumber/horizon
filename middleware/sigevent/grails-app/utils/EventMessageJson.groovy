import gov.nasa.horizon.common.api.util.DateTimeUtility
import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysNotify
//import grails.util.JSonBuilder
import grails.web.JSONBuilder
import grails.converters.JSON

/**
 * EventMessageJson
 */
public class EventMessageJson extends EventMessage {
   protected String constructDescription(List<SysEvent> events, SysNotify notify) {

      def jsBuilder = new JSONBuilder()
      JSON json = jsBuilder.build {
         Events {
            events.each {event ->
               def map = [:]
               this.addDescription(map, event)
               this.addDataSize(map, event)
               Event(map)
            }
         }

      }
      return json.toString()

/*     
      def stringWriter = new StringWriter()
      def jsonBuilder = new JSonBuilder(stringWriter)
      
      jsonBuilder.Message() {
         Events() {
            events.each {event ->
               def map = [:]
               addDescription(map, event)
               addDataSize(map, event)
               
               Event(map)
            }
         }
      }
      
      return stringWriter.toString()
*/      
   }
   
   protected String constructComplete(List<SysEvent> events, SysNotify notify) {

      def jsBuilder = new JSONBuilder()
      JSON json = jsBuilder.build {
         Events {
            events.each {event ->
               def map = [:]
               this.addDescription(map, event)
               this.addDataSize(map, event)
               this.addContent(map, event)
               Event(map)
            }
         }
      }

      return json.toString()
      
/*
      def stringWriter = new StringWriter()
      def jsonBuilder = new JSonBuilder(stringWriter)
      
      jsonBuilder.Message() {
         Events() {
            events.each {event ->
               def map = [:]
               addDescription(map, event)
               addDataSize(map, event)
               addContent(map, event)
               
               Event(map)
            }
         }
      }
      
      return stringWriter.toString()
*/      
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
      long dataSize = 0
      if(event.data) {
         dataSize = event.data.length()
      }
      
      parameters.put("DataSize", dataSize)
   }
}
