//import grails.util.JSonBuilder

import org.apache.commons.lang.StringEscapeUtils

/**
 * ResponseJson
 */
class ResponseJson extends Response {
   public String getInternetMediaType() {
      return "application/json"
   }
   
   public String toString() {
      String response = "{\"Response\": {"
      
      parameters.eachWithIndex {key, parameter, index ->
         response += "\""+key+"\": "
         
         String value = parameter.value
         if(parameter.doEscape) {
            value = "\""+StringEscapeUtils.escapeJavaScript(value)+"\""
         }
         response += value
         
         if(index < (parameters.size() - 1)) {
            response += ","
         }
      }
      
      response += "}}"
      
      return response
      
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
