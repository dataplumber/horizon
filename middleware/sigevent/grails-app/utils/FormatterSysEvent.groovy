import grails.converters.JSON
import grails.web.JSONBuilder
//import groovy.xml.MarkupBuilder
//import grails.util.JSonBuilder

import org.apache.commons.lang.StringEscapeUtils

import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.common.api.util.DateTimeUtility

/**
 * FormatterSysEvent
 */
class FormatterSysEvent extends Formatter {
   public FormatterSysEvent(boolean complete = false) {
      super(complete)
   }
   
   protected void formatWithBuilderSupport(BuilderSupport builderSupport, List<Object> objects) throws Exception {
      builderSupport.SysEvents() {
         for(object in objects) {
            def entry = populateEntry(object)
            
            SysEvent() {
               Id(entry.Id)
               Category(entry.Category)
               Type(entry.Type)
               DataSize(entry.DataSize)
               DataUrl(entry.DataUrl)
               Computer(entry.Computer)
               Description(entry.Description)
               FirstReceived(entry.FirstReceived)
               LastReceived(entry.LastReceived)
               Occurrence(entry.Occurrence)
               Pid(entry.Pid)
               Provider(entry.Provider)
               Resolution(entry.Resolution)
               ResolvedAt(entry.ResolvedAt)
               Source(entry.Source)
               
               if(complete) {
                  Data(entry.Data)
               }
            }
         }
      }
   }
   
   protected String formatInJson(List<Object> objects) throws Exception {
      def list = populateEntries(objects)

      def jsBuilder = new JSONBuilder()
      JSON json = jsBuilder.build {
         SysEvents {
            list.each {
               SysEvent(it)
            }
         }
      }
      return json.toString()
      
/*
      def stringWriter = new StringWriter()
      def jsonBuilder = new JSonBuilder(stringWriter)
      jsonBuilder.Message() {
         SysEvents() {
            list.each {
               SysEvent(it)
            }
         }
      }
      
      return stringWriter.toString()
*/    
   }
   
   protected String formatInDojoJson(List<Object> objects) throws Exception {
      def list = populateEntries(objects)
      
      def entries = []
      list.each {map ->
         def entry = "{"
         entry += '"type": "SysEvent",'
         
         map.eachWithIndex {key, value, index ->
            entry += '"'+StringEscapeUtils.escapeJavaScript(key)+'": '
            entry += '"'+StringEscapeUtils.escapeJavaScript(value.toString())+'"'
            
            if(index < (map.size() - 1)) {
               entry += ","
            }
         }
         entry += "}"
         
         entries.add(entry)
      }
      
      return entries.join(",")
   }
   
   protected String formatInText(List<Object> objects) throws Exception {
      def list = populateEntries(objects)
      
      String message = "SysEvents:\n"
      list.each {entry ->
         message += "\tSysEvent:\n"
         
         entry.each {key, value ->
            message += "\t\t"+key+": "+value+"\n"
         }
      }
      
      return message
   }
   
   private List<Map> populateEntries(List<Object> objects) {
      def list = []
      objects.each {object ->
         def map = populateEntry(object)
         list.add(map)
      }
      
      return list
   }
   
   private Map populateEntry(Object object) {
      def map = [:]
      map.put("Id", object.id)
      map.put("Category", object.group.category)
      map.put("Type", object.group.type)
      map.put("DataSize", (object.data) ? object.data.length() : 0)
      map.put("DataUrl", SigEventApiUtility.getDataUrl(object.id))
      map.put("Computer", object.computer)
      map.put("Description", object.description)
      map.put("FirstReceived", object.firstReceived)
      map.put("LastReceived", object.lastReceived)
      map.put("Occurrence", object.occurrence)
      map.put("Pid", (object.pid) ? object.pid : "")
      map.put("Provider", object.provider)
      map.put("Resolution", (object.resolution) ? object.resolution : "")
      map.put("ResolvedAt", (object.resolvedAt) ? object.resolvedAt : "")
      map.put("Source", object.source)
      
      if(complete) {
         map.put("Data", (object.data) ? object.data : "")
      }
      
      return map
   }
}
