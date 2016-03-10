import gov.nasa.horizon.common.api.util.DateTimeUtility
import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysNotify
import groovy.xml.MarkupBuilder

/**
 * EventMessageXml
 */
public class EventMessageXml extends EventMessage {
   /**
    * <Message>
    *   <Events>
    *     <Event>
    *       <Description></Description>
    *       <Data></Data>
    *       <DataSize></DataSize>
    *     </Event>
    *   </Events>
    * </Message>
    */
   private static final String XmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
   
   
   protected String constructDescription(List<SysEvent> events, SysNotify notify) {
      def stringWriter = new StringWriter()
      def markupBuilder = new MarkupBuilder(stringWriter)
      
      markupBuilder.Message() {
         Events() {
            events.each {event ->
               Event() {
                  addDescription(markupBuilder, event)
                  addDataSize(markupBuilder, event)
               }
            }
         }
      }
      
      return XmlHeader+stringWriter.toString()
   }
   
   protected String constructComplete(List<SysEvent> events, SysNotify notify) {
      def stringWriter = new StringWriter()
      def markupBuilder = new MarkupBuilder(stringWriter)
      
      markupBuilder.Message() {
         Events() {
            events.each {event ->
               Event() {
                  addDescription(markupBuilder, event)
                  addDataSize(markupBuilder, event)
                  addContent(markupBuilder, event)
               }
            }
         }
      }
      
      return XmlHeader+stringWriter.toString()
   }
   
   private void addDescription(MarkupBuilder markupBuilder, SysEvent event) {
      markupBuilder.Description(event.description)
   }
   
   private void addContent(MarkupBuilder markupBuilder, SysEvent event) {
      markupBuilder.Id(event.id)
      markupBuilder.Computer(event.computer)
      markupBuilder.FirstReceived(DateTimeUtility.parseDate(DateTime.UTC_FORMAT, new Date(event.firstReceived)))
      markupBuilder.LastReceived(DateTimeUtility.parseDate(DateTime.UTC_FORMAT, new Date(event.lastReceived)))
      markupBuilder.Occurrence(event.occurrence)
      markupBuilder.Pid(event.pid)
      markupBuilder.Provider(event.provider)
      
      String pid = ""
      if(event.pid) {
         pid = event.pid.toString()
      }
      markupBuilder.Pid(pid)
      
      String data = ""
      if(event.data) {
         data = event.data
      }
      markupBuilder.Data(data)
      
      String resolution = ""
      if(event.resolution) {
         resolution = event.resolution
      }
      markupBuilder.Resolution(resolution)
      
      String resolvedAt = ""
      if(event.resolvedAt) {
         resolvedAt = DateTimeUtility.parseDate(DateTime.UTC_FORMAT, new Date(event.resolvedAt))
      }
      markupBuilder.ResolvedAt(resolvedAt)
   }
   
   private void addDataSize(MarkupBuilder markupBuilder, SysEvent event) {
      long dataSize = 0
      if(event.data) {
         dataSize = event.data.length()
      }
      
      markupBuilder.DataSize(dataSize)
   }
}
