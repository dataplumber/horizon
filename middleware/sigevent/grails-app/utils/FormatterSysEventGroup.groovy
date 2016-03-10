import grails.converters.JSON
import grails.web.JSONBuilder
//import groovy.xml.MarkupBuilder
//import grails.util.JSonBuilder

import org.apache.commons.lang.StringEscapeUtils

import gov.nasa.horizon.sigevent.SysEventGroup

/**
 * FormatterSysEventGroup
 */
class FormatterSysEventGroup extends Formatter {
   public FormatterSysEventGroup(boolean complete = false) {
      super(complete)
   }
   
   protected void formatWithBuilderSupport(BuilderSupport builderSupport, List<Object> objects) throws Exception {
      builderSupport.SysEventGroups() {
         for(object in objects) {
            def entry = populateEntry(object)
            
            SysEventGroup() {
               Id(entry.Id)
               Type(entry.Type)
               Category(entry.Category)
               PurgeRate(entry.PurgeRate)
            }
         }
      }
   }
   
   protected String formatInJson(List<Object> objects) throws Exception {
      def list = populateEntries(objects)

      def jsBuilder = new JSONBuilder()
      JSON json = jsBuilder.build {
         SysEventGroups {
            list.each {
               SysEventGroup(it)
            }
         }
      }
      return json.toString()

/*      
      def stringWriter = new StringWriter()
      def jsonBuilder = new JSonBuilder(stringWriter)
      jsonBuilder.Message() {
         SysEventGroups() {
            list.each {
               SysEventGroup(it)
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
         entry += '"type": "SysEventGroup",'
         
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
      
      String message = "SysEventGroups:\n"
      list.each {entry ->
         message += "\tSysEventGroup:\n"
         
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
      map.put("Type", object.type)
      map.put("Category", object.category)
      map.put("PurgeRate", object.purgeRate)
      
      return map
   }
}
