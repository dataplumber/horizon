/**
 * NotifyMethod
 */
public enum NotifyMethod {
   Email("EMAIL"),
   Jms("JMS"),
   Multicast("MULTICAST"),
   Twitter("TWITTER")
   
   final String value
   
   public NotifyMethod(String value) {
      this.value = value
   }
   
   public static NotifyMethod detect(String value) {
      return NotifyMethod.values().find{it.value == name}
   }
}
