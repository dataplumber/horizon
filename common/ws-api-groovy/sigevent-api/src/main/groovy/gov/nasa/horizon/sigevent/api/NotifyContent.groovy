/**
 * 
 */
package gov.nasa.horizon.sigevent.api



/**
 * @author axt
 *
 */
public enum NotifyContent {
   Complete("COMPLETE"),
   Description("DESCRIPTION");
   
   final String value
   
   public NotifyContent(String value) {
      this.value = value
   }
}
