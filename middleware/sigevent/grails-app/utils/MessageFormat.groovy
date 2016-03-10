/**
 * MessageFormat
 */
public enum MessageFormat {
   Xml("XML"),
   Json("JSON"),
   Text("TEXT"),
   Tweet("TWEET")
   
   private final String name
   
   MessageFormat(String name) {
      this.name = name
   }
   
   public static MessageFormat detect(String name) {
      return MessageFormat.values().find{it.getName().toUpperCase() == name.toString().toUpperCase()}
   }
   
   public String getName() {
      return name
   }
}
