/**
 * 
 */
package gov.nasa.horizon.sigevent.api



/**
 * @author axt
 *
 */
public enum ResponseFormat {
   Xml("XML"),
   Json("JSON"),
   Text("TEXT");
   
   final String value
   
   public ResponseFormat(String value) {
      this.value = value
   }
}
