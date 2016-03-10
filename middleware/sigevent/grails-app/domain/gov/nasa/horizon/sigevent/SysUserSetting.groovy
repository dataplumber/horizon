package gov.nasa.horizon.sigevent

class SysUserSetting {
   String username
   String setting
   
   static constraints = {
      username(nullable: false, size: 1..40)
      setting(nullable: true)
   }
  
   static mapping = {
      id generator: 'sequence', params: [sequence: 'sys_user_setting_id_seq']
   }

}
