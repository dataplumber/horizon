package gov.nasa.horizon.sigevent

class SysEvent {
   /**
    * The initial time when the event was reported
    */
   Long firstReceived

   /**
    * The last receive time of the event
    */
   Long lastReceived

   /**
    * The number of occurrence(s) of the event
    */
   Long occurrence

   /**
    * The source of the event.  It might just be the name of the program that
    * reported the event
    */
   String source

   /**
    * The owner of the source of event
    */
   String provider = 'N/A'

   /**
    * The name or IP address of the computer that reported the event
    */
   String computer

   /**
    * The process ID
    */
   Integer pid

   /**
    * The description of the event
    */
   String description

   /**
    * Any relevant data associated with the event, such as input data, stacktrace, etc.
    */
   String data
   
   /**
    * A description on how the event got resolved.
    */
   String resolution = 'N/A'

   /**
    * The time when the event was resolved
    */
   Long resolvedAt

   static belongsTo = [group: SysEventGroup]

   static constraints = {
      firstReceived(nullable: false)
      lastReceived(nullable: false)
      occurrence(nullable: false)
      source(nullable: false, blank: false, size: 1..40)
      provider(nullable: false, blank: false, size: 1..40)
      computer(nullable: false, blank: false, size: 1..40)
      pid(nullable: true, min: 1)
      description(nullable: false, blank: false, size: 1..256)
      data(nullable: true)
      //resolved(nullable: false)
      resolution(nullable: true, blank: false, size: 1..256)
      resolvedAt(nullable: true)
   }
   
   static mapping = {
      columns {
         data type: 'text'
      }
      
      id generator: 'sequence', params: [sequence: 'sys_event_id_seq']
   }
}
