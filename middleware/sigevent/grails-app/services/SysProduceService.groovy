import gov.nasa.horizon.sigevent.SysEvent
import gov.nasa.horizon.sigevent.SysNotify

/**
 * SysProduceService
 */
class SysProduceService {
   boolean transactional = false

   public List<String> constructMessage(SysNotify notify, List<SysEvent> events) {
      def messageFormat = MessageFormat.detect(notify.messageFormat)
      def eventMessage = EventMessageFactory.get(messageFormat)
      def messageContent = MessageContent.detect(notify.content)
      
      return eventMessage.construct(messageContent, events, notify)
   }
}
