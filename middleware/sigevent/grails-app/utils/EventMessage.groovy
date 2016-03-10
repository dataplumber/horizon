import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysNotify

/**
 * EventMessage
 */
public abstract class EventMessage {
   private static final int DataLimit = 4000
   private static final Map Contents = [
      (MessageContent.Complete): "constructComplete",
      (MessageContent.Description): "constructDescription"
   ]
   
   public List<String> construct(
      MessageContent messageContent,
      List<SysEvent> events,
      SysNotify notify
      ) {
      def entry = Contents.find{it.key.getName() == messageContent.getName()}
      def messages = []
      
      def messagePackages = createMessagePackages(events, notify)
      messagePackages.each {messagePackage ->
         def message = this."$entry.value"(messagePackage, notify)
         messages.add(message)
      }
      
      return messages
   }
   
   protected abstract String constructDescription(List<SysEvent> events, SysNotify notify)
   
   protected abstract String constructComplete(List<SysEvent> events, SysNotify notify)
   
   private List createMessagePackages(List<SysEvent> events, SysNotify notify) {
      def messagePackages = []
      def messagePackage = []
      
      boolean isTweet = (MessageFormat.detect(notify.messageFormat).getName() == MessageFormat.Tweet.getName())
      int totalDataSize = 0
      events.eachWithIndex {event, index ->
         messagePackage.add(event)
         if(event.data) {
            totalDataSize += event.data.length()
         }
         
         if((totalDataSize >= EventMessage.DataLimit) ||
            ((index + 1) >= events.size()) || isTweet) {
            messagePackages.add(new LinkedList(messagePackage))
            
            messagePackage.clear()
            totalDataSize = 0
         }
      }
      
      return messagePackages
   }
}
