package gov.nasa.horizon.sigevent

/**
 * This domain class captures all outgoing messages and the method of delivery.  This class
 * is by intention to be denormalized, that is, the 'method' and 'contact' fields are copied from
 * the SysNotify domain class.  This allows for all pending messages to continue to deliver even
 * after a notifiction subscription has been removed.
 */
class SysOutgoing {

   /**
    * The event level to distinguish the event from an information message
    * to an alert message
    */
   String type

   /**
    * User defined event category that will be filtered by any subscriber
    * e.g. SECURITY category is to capture any security event
    */
   String category
   
   /**
    * The method for notification
    */
   String method

   /**
    * The actual contact info - email address or JMS topic
    */
   String contact
   
   /**
    * The time when this outgoing message was created
    */
   Long created
   
   /**
    * The message to be sent
    */
   String message
   
   /**
    * Reference to SysNotify
    */
   SysNotify notify
   
   /**
    * Message type - NEW, REMINDER, or RESOLVED
    */
   String messageType
   
   static constraints = {
      type(nullable: false, inList: ['INFO', 'WARN', 'ERROR'])
      category(nullable: false, blank: false, size: 1..40)
      method(nullable: false, inList: ['EMAIL', 'JMS', 'MULTICAST', 'TWITTER'])
      contact(nullable: false, blank: false, size: 1..40)
      created(nullable: false)
      message(nullable: false)
      notify(nullable: true)
      messageType(nullable: false, inList: ['NEW', 'REMINDER', 'RESOLVED'])
   }
   
   static mapping = {
      columns {
         message type: 'text'
      }
       
      id generator: 'sequence', params: [sequence: 'sys_outgoing_id_seq']
   }
}
