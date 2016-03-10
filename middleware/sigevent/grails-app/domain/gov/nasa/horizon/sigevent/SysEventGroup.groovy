package gov.nasa.horizon.sigevent

class SysEventGroup {
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
    * The frequency for purging registered events.
    * In unit of Day(s)
    */
   Long purgeRate

   static hasMany = [notifies: SysNotify, events: SysEvent]

   static constraints = {
      type(unique: ['category'], nullable: false, inList: ['INFO', 'WARN', 'ERROR'])
      category(nullable: false, blank: false, size: 1..100)
      purgeRate (nullable: false, min: 1L)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'sys_event_group_id_seq']
   }
}
