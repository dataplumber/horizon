/**
 * 
 */
package gov.nasa.horizon.sigevent.api



/**
 * @author axt
 *
 */
public enum MessageFormat {
   Xml("XML"),
   Json("JSON"),
   Text("TEXT");
   
   final String value
   
   public MessageFormat(String value) {
      this.value = value
   }
}
