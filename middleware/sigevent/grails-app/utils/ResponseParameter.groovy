/**
 * ResponseParameter
 */
public class ResponseParameter {
   String name
   String value
   boolean doEscape
   
   public ResponseParameter(String name, String value, boolean doEscape = true) {
      this.name = name
      this.value = value
      this.doEscape = doEscape
   }
}
