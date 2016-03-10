import org.apache.commons.lang.math.NumberUtils

import gov.nasa.horizon.sigevent.SysUser
import gov.nasa.horizon.sigevent.SysUserSetting

/**
 * SysUserService
 */
class SysUserService {

   private static final Map AllowedParameters = [
      "update": [
         "setting": ""
      ]
   ]
   
   boolean transactional = false

   public SysUserSetting show(Map parameters) throws RuntimeException {
      def user = null
      if (parameters.username) {
         user = SysUserSetting.findByUsername(parameters.username)
         if(!user) {
            throw new RuntimeException("SysUserSetting does not exist with username: "+parameters.username)
         }
      } else {
         throw new RuntimeException("Missing username parameter.")
      }
      return user
   }
   
   public SysUserSetting update(Map parameters) throws RuntimeException {
      def user = null
      if (parameters.username) {
         user = SysUserSetting.findByUsername(parameters.username)
         if(!user) {
            user = new SysUserSetting(
               username: parameters.username
            )
            if (!user.save(flush: true)) {
               throw new RuntimeException("Failed to create new user setting with username: "+parameters.username)
            }
         }
         user.discard()
         user = SysUserSetting.lock(user.id)
         if(!user) {
            throw new RuntimeException("SysUserSetting does not exist with ID: "+user.id)
         }
      } else {
         throw new RuntimeException("Missing username parameter.")
      }
      
      SysUserService.AllowedParameters.update.each {key, value ->
         if(parameters."$key") {
            user."$key" = (value) ? Eval.x(parameters."$key", value) : parameters."$key"
         }
      }
      
      if(!user.save(flush: true)) {
         throw new RuntimeException("Failed to update SysUserSetting["+user.id+"]: "+user.errors.allErrors.join())
      }
      user.discard()
      
      return user
   }
}
