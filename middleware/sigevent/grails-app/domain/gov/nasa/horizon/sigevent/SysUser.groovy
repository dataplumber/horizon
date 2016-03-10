package gov.nasa.horizon.sigevent

class SysUser {
   String username
   String password
   SysRole role
   
   static constraints = {
      username(nullable: false, size: 1..40)
      password(nullable: false, size: 1..100)
      role(nullable: false)
   }
 
   static mapping = {
      id generator: 'sequence', params: [sequence: 'sys_user_id_seq']
   }
}
