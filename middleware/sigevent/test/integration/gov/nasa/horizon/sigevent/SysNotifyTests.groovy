package gov.nasa.horizon.sigevent

class SysNotifyTests extends GroovyTestCase {

   void setUp() {
      def notify = new SysNotify(
            method: 'EMAIL',
            contact: 'thomas.huang@jpl.nasa.gov',
            rate: 1L,
            note: 'Test notify')
      new SysEventGroup(type: 'WARN', category: 'TEST_SECURITY').addToNotifies(notify).save()
   }

   void testQuery() {
      def notifies = SysNotify.findAll(group: SysEventGroup.findByTypeAndCategory('WARN', 'TEST_SECURITY'))
      assertEquals("There should be one notify record.", 1, notifies.size())
      assertEquals("The contact should match", 'thomas.huang@jpl.nasa.gov', notifies[0].contact)
   }

   void tearDown() {
      SysEventGroup.list().each {
         it.events.each {event ->
            event.delete()
         }
         it.notifies.each {notify ->
            notify.delete()
         }
         it.delete()
      }
   }
}
