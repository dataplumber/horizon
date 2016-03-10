import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * SigEventApiUtility
 */
class SigEventApiUtility {
   private static final String DataUri = ConfigurationHolder.config.sigevent_api_data_uri
   
   public static String getDataUrl(def id) {
      return SigEventApiUtility.DataUri+"?id="+id
   }
}
