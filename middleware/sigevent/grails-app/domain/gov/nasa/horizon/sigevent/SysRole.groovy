package gov.nasa.horizon.sigevent

class SysRole {
   String name
   Integer admin
   
   static constraints = {
      name(nullable: false, size: 1..100)
      admin(nullable: false, min: 0)
   }
   
   static mapping = {
      id generator: 'sequence', params: [sequence: 'sys_role_id_seq']
   }

}
