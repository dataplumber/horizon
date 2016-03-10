import groovy.xml.MarkupBuilder

import org.apache.commons.lang.StringEscapeUtils

/**
 * ResponseXml
 */
class ResponseXml extends Response {
   public String getInternetMediaType() {
      return "application/xml"
   }
   
   public String toString() {
      String value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      
      value += "<Response>"
      parameters.each {key, parameter ->
         value += "<"+key+">"
         value += (parameter.doEscape) ? StringEscapeUtils.escapeXml(parameter.value) : parameter.value
         value += "</"+key+">"
      }
      value += "</Response>"
      
      return value
      
      /*
      def stringWriter = new StringWriter()
      def markupBuilder = new MarkupBuilder(stringWriter)
      
      markupBuilder.reponse() {
         type(getType())
         content(getContent())
      }
      
      return stringWriter.toString()
      */
   }
}
