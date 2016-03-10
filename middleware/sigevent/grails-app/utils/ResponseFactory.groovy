/**
 * ResponseFactory
 */
class ResponseFactory {
   private static final Map Responses = [
      (ResponseFormat.Text): ResponseText.class,
      (ResponseFormat.Xml): ResponseXml.class,
      (ResponseFormat.Json): ResponseJson.class,
      (ResponseFormat.DojoJson): ResponseDojoJson.class,
      (ResponseFormat.Raw): ResponseRaw.class
   ]
   
   public static Response create(ResponseFormat responseFormat) {
      def entry = Responses.find{it.getKey().getName() == responseFormat.getName()}
      
      Response response = null
      if(entry) {
         response = (Response)entry.getValue().newInstance()
      }
      
      return response
   }
}
