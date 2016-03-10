/**
 * ResponseText
 */
class ResponseText extends Response {
   public String getInternetMediaType() {
      return "text/plain"
   }
   
   public String toString() {
      String value = "Response:\n"
      
      parameters.each {key, parameter ->
         value += "\t"+key+": "+parameter.value+"\n"
      }
      
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
