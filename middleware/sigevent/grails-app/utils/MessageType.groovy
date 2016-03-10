/**
 * MessageType
 */
public enum MessageType {
   NEW, REMINDER, RESOLVED
   
   public static MessageType detect(String value) {
      return MessageType.values().find{it.name() == value}
   }
}
