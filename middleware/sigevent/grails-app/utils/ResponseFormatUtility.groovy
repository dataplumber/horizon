/**
 * ResponseFormatUtility
 */
class ResponseFormatUtility {
   private static final ResponseFormat Default = ResponseFormat.Xml
   
   public static ResponseFormat get(String value) {
      ResponseFormat responseFormat = ResponseFormat.detect(value)
      if(!responseFormat) {
         responseFormat = Default
      }
      
      return responseFormat
   }
}
