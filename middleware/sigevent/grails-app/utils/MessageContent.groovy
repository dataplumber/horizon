/**
 * MessageContent
 */
public enum MessageContent {
   Complete("COMPLETE"),
   Description("DESCRIPTION")
   
   private final String name
   
   MessageContent(String name) {
      this.name = name
   }
   
   public static MessageContent detect(String name) {
      return MessageContent.values().find{it.getName() == name}
   }
   
   public String getName() {
      return name
   }
}
