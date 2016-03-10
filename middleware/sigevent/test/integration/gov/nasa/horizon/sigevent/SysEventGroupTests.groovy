package gov.nasa.horizon.sigevent

class SysEventGroupTests extends GroovyTestCase {

   void setUp() {
      new SysEventGroup(type: 'WARN', category: 'TEST_SECURITY').save()
      new SysEventGroup(type: 'INFO', category: 'TEST_VALIDATION').save()
      new SysEventGroup(type: 'INFO', category: 'TEST_PRODUCT').save()

   }

   void testQuery() {
      def group = SysEventGroup.findByTypeAndCategory('WARN', 'TEST_SECURITY')
      assertEquals "The group should have a category.", 'TEST_SECURITY', group.category

      def groups = SysEventGroup.findAllByType('INFO')
      assertEquals "The return list should have two events.", true, (groups.size() >= 2)
   }

   void tearDown() {
      SysEventGroup.list()*.delete()
   }
}
