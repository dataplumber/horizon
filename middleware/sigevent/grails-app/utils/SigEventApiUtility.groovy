
/**
 * SigEventApiUtility
 */
class SigEventApiUtility {
   static def grailsApplication
   private static final String DataUri = grailsApplication.config.sigevent_api_data_uri
   
   public static String getDataUrl(def id) {
      return SigEventApiUtility.DataUri+"?id="+id
   }
}
