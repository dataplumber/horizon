package gov.nasa.horizon.ingest.api

public class SessionException extends Exception {
   Errno errno

   SessionException(String message, Errno errno) {
      super(message)
      this.errno = errno
   }

   SessionException (String message, Throwable exception) {
      super(message, exception)
   }
   
   SessionException (String message, Errno errno, Throwable exception) {
      super(message, exception)
      this.errno = errno
   }

}
