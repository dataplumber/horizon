import grails.converters.JSON
import grails.web.JSONBuilder
//import groovy.xml.MarkupBuilder
//import grails.util.JSonBuilder

import org.apache.commons.lang.StringEscapeUtils

/**
 * FormatterSysConstant
 */
public class FormatterSysConstant extends Formatter {
   public FormatterSysConstant(boolean complete = false) {
      super(complete)
   }
   
   protected void formatWithBuilderSupport(BuilderSupport builderSupport, List<Object> objects) throws Exception {
      builderSupport.SysConstants() {
         for(object in objects) {
            SysConstant() {
               Value(object)
            }
         }
      }
   }
   
   protected String formatInJson(List<Object> objects) throws Exception {

      def jsBuilder = new JSONBuilder()
      JSON json = jsBuilder.build {
         SysConstants {
            objects.each {
               SysConstant(['Value': it])
            }
         }

      }
      return json.toString()
      
/*
      def stringWriter = new StringWriter()
      def jsonBuilder = new JSonBuilder(stringWriter)
      jsonBuilder.Message() {
         SysConstants() {
            objects.each {
               SysConstant(["Value": it])
            }
         }
      }
      
      return stringWriter.toString()
*/   
   }
   
   protected String formatInDojoJson(List<Object> objects) throws Exception {
      def entries = []
      
      objects.each {object ->
         def entry = "{"
         entry += '"type": "SysConstant",'
         entry += '"Value": '
         entry += '"'+StringEscapeUtils.escapeJavaScript(object.toString())+'"'
         entry += "}"
         
         entries.add(entry)
      }

      return entries.join(",")
   }
   
   protected String formatInText(List<Object> objects) throws Exception {
      String message = "SysConstants:\n"
      objects.each {
         message += "\tSysContant:\n"
         message += "\t\tValue: "+it+"\n"
      }
      
      return message
   }
}
