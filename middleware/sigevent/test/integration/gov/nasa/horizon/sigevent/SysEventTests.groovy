package gov.nasa.horizon.sigevent

class SysEventTests extends GroovyTestCase {

   void setUp() {
      def eg = new SysEventGroup(type: 'WARN', category: 'TEST_SECURITY')

      def notify = new SysNotify(
            method: 'EMAIL',
            contact: 'thomas.huang@jpl.nasa.gov',
            rate: 1L,
            note: 'Test notify')
      eg.addToNotifies(notify).save()

      def event = new SysEvent(
            received: new Date(),
            source: 'EventTests',
            provider: 'thuang',
            computer: java.net.InetAddress.localHost.hostAddress,
            description: 'test description')
      eg.addToEvents(event).save()
   }

   void testQuery() {
      def events = SysEvent.findAll(group: SysEventGroup.findByTypeAndCategory('WARN', 'TEST_SECURITY'))
      assertEquals("There should be one event returned.", 1, events.size())
      assertEquals("The description should match.", 'test description', events[0].description)
   }

   void tearDown() {
      SysEventGroup.list().each {
         it.events.each {event ->
            event.delete()
         }
         it.delete()
      }
   }

}
