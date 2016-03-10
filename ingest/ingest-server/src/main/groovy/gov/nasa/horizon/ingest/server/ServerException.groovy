package gov.nasa.horizon.ingest.server
/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Sep 14, 2008
 * Time: 2:16:41 AM
 * To change this template use File | Settings | File Templates.
 */
class ServerException extends Exception {
   int errno

   ServerException(String message, int errno) {
      super(message)
      this.errno = errno
   }

   ServerException (String message, Throwable exception) {
      super(message, exception)
   }

}
