import gov.nasa.horizon.common.api.util.DateTimeUtility
import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysNotify

/**
 * EventMessageTweet
 */
public class EventMessageTweet extends EventMessage {
   static def grailsApplication
   private static final int TwitterCharactersLimit = grailsApplication.config.sigevent_twitter_characters_limit
   private static final String EndingCharacters = "..."
   
   protected String constructDescription(List<SysEvent> events, SysNotify notify) {
      def list = []
      events.each {event ->
         def map = [:]
         addDescription(map, event)
         
         list.add(map)
      }
      
      return construct(list, notify)
   }
   
   protected String constructComplete(List<SysEvent> events, SysNotify notify) {
      return constructDescription(events, notify)
   }
   
   private String construct(List<Map> events, SysNotify notify) {
      String message = '@'+notify.contact+" "
      
      def entries = []
      events.each {event ->
         event.each {key, value ->
            entries.add(value)
         }
      }
      message += entries.join(";")
      
      int charactersLimit =  (EventMessageTweet.TwitterCharactersLimit - EventMessageTweet.EndingCharacters.length())
      if(message.length() > charactersLimit) {
         message = message.substring(0, charactersLimit) + EventMessageTweet.EndingCharacters
      }
      
      return message
   }
   
   private void addDescription(Map parameters, SysEvent event) {
      parameters.put("Description", event.description)
   }
}
