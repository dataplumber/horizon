package gov.nasa.horizon.ingest.api.protocol
/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Aug 22, 2008
 * Time: 1:26:50 AM
 * To change this template use File | Settings | File Templates.
 */
class ProtocolException extends Exception {
   private static final long serialVersionUID = 1L;

   public ProtocolException() {
      super()
   }

   public ProtocolException(String message) {
      super(message)
   }

   public ProtocolException(String message, Throwable cause) {
      super(message, cause)
   }

   public ProtocolException(Throwable cause) {
      super(cause)
   }
}
