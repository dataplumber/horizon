package gov.nasa.horizon.sigevent

class SysNotify {
   /**
    * The method for notification
    */
   String method

   /**
    * The actual contact info - email address or JMS topic
    */
   String contact

   /**
    * The rate of notification.  This is in units of minutes
    */
   Long rate

   /**
    * The rate at which to send reminder notification.  This is in units of hours
    */
   Integer remindRate

   /**
    * The message format
    */
   String messageFormat

   /**
    * The content to be delivered.
    */
   String content
   
   /**
    * The last time an outgoing message was generated
    */
   Long lastReport
   
   /**
    * The last time this subscriber was notified
    */
   Long lastNotified

   /**
    * The last time this subscriber was reminded of unresolved events
    */
   Long lastRemind

   /**
    * Any note associate with this subscriber
    */
   String note

   static belongsTo = [group: SysEventGroup]

   static constraints = {
      method(nullable: false, inList: ['EMAIL', 'JMS', 'MULTICAST', 'TWITTER'])
      contact(nullable: false, blank: false, size: 1..40)
      rate(nullable: false, min: 1L)
      messageFormat(nullable: false, inList:['TEXT', 'XML', 'JSON', 'TWEET'])
      content(nullable: false, inList:['COMPLETE', 'DESCRIPTION'])
      lastReport(nullable: true)
      lastNotified(nullable: true)
      lastRemind(nullable: true)
      note(nullable: true, size: 1..256)
      remindRate(nullable: true, min: 0)
   }
   
   static mapping = {
      id generator: 'sequence', params: [sequence: 'sys_notify_id_seq']
   }
}
