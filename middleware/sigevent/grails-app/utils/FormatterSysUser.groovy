import grails.converters.JSON
import grails.web.JSONBuilder
//import groovy.xml.MarkupBuilder
//import grails.util.JSonBuilder

import org.apache.commons.lang.StringEscapeUtils

import gov.nasa.horizon.sigevent.SysUser

/**
 * FormatterSysUser
 */
public class FormatterSysUser extends Formatter {
   public FormatterSysUser(boolean complete = false) {
      super(complete)
   }
   
   protected void formatWithBuilderSupport(BuilderSupport builderSupport, List<Object> objects) throws Exception {
      builderSupport.SysUsers() {
         for(object in objects) {
            def entry = populateEntry(object)
            
            SysUser() {
               Id(entry.Id)
               Username(entry.Username)
               Role(entry.Role)
               Setting(entry.Setting)
            }
         }
      }
   }
   
   protected String formatInJson(List<Object> objects) throws Exception {
      def list = populateEntries(objects)

      def jsBuilder = new JSONBuilder()
      JSON json = jsBuilder.build {
         SysUsers {
            list.each {
               SysUser(it)
            }
         }
      }
      return json.toString()

/*      
      def stringWriter = new StringWriter()
      def jsonBuilder = new JSonBuilder(stringWriter)
      jsonBuilder.Message() {
         SysUsers() {
            list.each {
               SysUser(it)
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
         entry += '"type": "SysUser",'
         
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
      
      String message = "SysUsers:\n"
      list.each {entry ->
         message += "\tSysUser:\n"
         
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
      map.put("Username", object.username)
      map.put("Role", object.role.name)
      map.put("Setting", object.setting)
      
      return map
   }
}
