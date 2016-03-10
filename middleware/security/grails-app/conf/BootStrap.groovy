
import java.util.logging.Logger;

import javax.security.sasl.RealmCallback;

import gov.nasa.horizon.security.server.*;
import gov.nasa.horizon.security.server.utils.*

class BootStrap {

   def grailsApplication
   
   def init = { servletContext ->
      //log.debug "*** BootStrap, Environment.current: ${Environment.current}"
      
      environments {
         development {
            log.debug '*** detected development'
            this.config('security.bootstrap.dev.xml')
         }
         test {
            log.debug '*** detected test'
            this.config('security.bootstrap.test.xml')
         }
         production {
            log.debug '*** detected production'
            this.config('security.bootstrap.pro.xml')
         }
         smap_cal_val {
            log.debug '*** detected development'
            this.config('security.bootstrap.smap.xml')
         }
      }
      
   }
   
   def config = { bootstrapFile ->
      
      def filePath = "resources/${bootstrapFile}"
      try {
         def bootstrap = new XmlSlurper().parseText(grailsApplication.getParentContext().getResource("classpath:$filePath").inputStream.text)

      bootstrap.verifiers.verifier.each{
         def v = Verifier.findByName(it as String)
         if(v==null) {
            Verifier ldapV = new Verifier();
            ldapV.setName(it as String);
            ldapV.save(flush: true);
         }
      }

      bootstrap.realms.realm.each { realm ->

         def rlm = Realm.findByName(realm.name as String)
         if(rlm == null){
            Realm r = new Realm();
            r.name= realm.name
            r.description=realm.description as String
            r.tokenExpiration= Integer.valueOf(realm.tokenExpire as String)
            def v = Verifier.findByName(realm.verifier as String)
            r.setVerifier(v);
            r.save(flush: true);
            realm.roles.role.each {
               Role role = new Role();
               role.setRoleName(it.name as String)
               role.setRoleGroup(it.group as String)
               r.addToRoles(role)
            }
            r.save(flush: true);
         }
      }

      bootstrap.users.user.each {user ->
         String username = user.username
         String password = user.password
         String role = user.role
         IngSystemUser userObj = IngSystemUser.findByName(username as String)
         IngAccessRole accessRole = IngAccessRole.findByName(role as String)
         if (!userObj) {
            if (!accessRole) {
               accessRole = new IngAccessRole(
                     name: role,
                     capabilities: Integer.valueOf( user.capabilities as String)
                     )
               if (!accessRole.save(flush: true)) {
                  accessRole.errors.each { log.error it }
               }
            }
            userObj = new IngSystemUser(
                  name: username,
                  password: Encrypt.encrypt(password) as String,
                  fullname: user.name as String,
                  email: user.email as String,
                  admin: user.admin as String,
                  readAll: user.readAll as Boolean,
                  writeAll: user.writeAll as Boolean,
                  note: username as String
                  )
            if (!userObj.save(flush: true)) {
               userObj.errors.each { log.error it }
            }
            IngSystemUserRole userRole = new IngSystemUserRole(
                  user: userObj,
                  role: accessRole
                  )
            if (!userRole.save(flush: true)) {
               userRole.errors.each { log.error it }
            }
         }
      }

      } catch (IOException e) {
         log.error "Unable to process bootstrap file ${filePath}", e
      }
      
   }
   def destroy = {
   }
}
