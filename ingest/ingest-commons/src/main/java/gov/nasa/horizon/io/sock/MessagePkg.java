package gov.nasa.horizon.io.sock;

import java.util.UUID;

/**
 * Message package class. Used to return an error number and a text message read
 * from a stream.
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: MessagePkg.java 1846 2008-09-16 18:34:41Z thuang $
 */
public class MessagePkg {
   private String _message;
   private int _errno;
   private UUID _id;
   private int _transactionId;

   /**
    * Constructor
    * 
    * @param errno the error message number.
    * @param message the error message text.
    */
   public MessagePkg(int errno, UUID id, int transactionId, String message) {
      this._errno = errno;
      this._id = id;
      this._transactionId = transactionId;
      this._message = message;
   }

   /**
    * Accessor method to get the error number.
    * 
    * @return the error number of the message
    */
   public int getErrno() {
      return this._errno;
   }
   
   public UUID getId() {
      return this._id;
   }
   
   public int getTransactionId() {
      return this._transactionId;
   }

   /**
    * Accessor method to get the message text
    * 
    * @return the message text
    */
   public String getMessage() {
      return this._message;
   }

   /**
    * Override toString() method to return a complete message.
    * 
    * @return the message error number and text
    */
   public String toString() {
      return new String("" + _errno + ": " + _message);
   }
}
