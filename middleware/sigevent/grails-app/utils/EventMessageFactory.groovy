/**
 * EventMessageFactory
 */
public class EventMessageFactory {
   private static final Map EventMessages = [
      (MessageFormat.Xml): new EventMessageXml(),
      (MessageFormat.Json): new EventMessageJson(),
      (MessageFormat.Text): new EventMessageText(),
      (MessageFormat.Tweet): new EventMessageTweet()
   ]
   
   public static EventMessage get(MessageFormat messageFormat) {
      def entry = EventMessages.find{it.key.getName() == messageFormat.getName()}
      return (entry) ? entry.value : null
   }
}
