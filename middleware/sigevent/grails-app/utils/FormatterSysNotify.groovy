import grails.converters.JSON
import grails.web.JSONBuilder
//import grails.util.JSonBuilder
import org.apache.commons.lang.StringEscapeUtils

import gov.nasa.horizon.common.api.util.DateTimeUtility

/**
 * FormatterSysNotify
 */
class FormatterSysNotify extends Formatter {
   public FormatterSysNotify(boolean complete = false) {
      super(complete)
   }
   
   protected void formatWithBuilderSupport(BuilderSupport builderSupport, List<Object> objects) throws Exception {
      builderSupport.SysNotifies() {
         for(object in objects) {
            def entry = populateEntry(object)
            
            SysNotify() {
               Id(entry.Id)
               Category(entry.Category)
               Type(entry.Type)
               Method(entry.Method)
               Contact(entry.Contact)
               Rate(entry.Rate)
               RemindRate(entry.RemindRate)
               MessageFormat(entry.MessageFormat)
               Content(entry.Content)
               LastReport(entry.LastReport)
               LastNotified(entry.LastNotified)
               LastRemind(entry.LastRemind)
               Note(entry.Note)
            }
         }
      }
   }
   
   protected String formatInJson(List<Object> objects) throws Exception {
      def list = populateEntries(objects)

      def jsBuilder = new JSONBuilder()
      JSON json = jsBuilder.build {
         SysNotifies {
            list.each {
               SysNotify(it)
            }
         }
      }
      return json.toString()
      
/*
      def stringWriter = new StringWriter()
      def jsonBuilder = new JSonBuilder(stringWriter)
      jsonBuilder.Message() {
         SysNotifies() {
            list.each {
               SysNotify(it)
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
         entry += '"type": "SysNotify",'
         
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
      
      String message = "SysNotifies:\n"
      list.each {entry ->
         message += "\tSysNotify:\n"
         
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
      map.put("Method", object.method)
      map.put("Contact", object.contact)
      map.put("Rate", object.rate)
      map.put("MessageFormat", object.messageFormat)
      map.put("Content", object.content)
      map.put(
         "LastReport",
         (object.lastReport) ? object.lastReport : ""
      )
      map.put(
         "LastNotified",
         (object.lastNotified) ? object.lastNotified : ""
      )
      map.put(
         "LastRemind",
         (object.lastRemind) ? object.lastRemind : ""
      )
      map.put(
         "RemindRate",
         (object.remindRate) ? object.remindRate : ""
      )
      map.put("Note", (object.note) ? object.note : "")
      
      return map
   }
}
