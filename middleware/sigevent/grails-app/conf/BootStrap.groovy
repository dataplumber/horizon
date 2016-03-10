
import grails.util.*
import gov.nasa.horizon.sigevent.SysEventGroup
import gov.nasa.horizon.sigevent.SysUser
import gov.nasa.horizon.sigevent.SysRole

class BootStrap {

   def grailsApplication
   def quartzScheduler

   def init = { servletContext ->
      log.debug "*** BootStrap, Environment.current: ${Environment.current}"

      switch (GrailsUtil.environment) {
         case 'local':
            populate('sigevent.bootstrap.dev.xml')
            break
         case 'thuang':
         case 'development':
            populate('sigevent.bootstrap.dev.xml')
            break
         case 'test':
            populate('sigevent.bootstrap.test.xml')
            break
         case 'testing':
            populate('sigevent.bootstrap.test.xml')
            break
         case 'production':
            populate('sigevent.bootstrap.pro.xml')
            break
         case 'operation':
            populate('sigevent.bootstrap.pro.xml')
            break
         case 'smap_cal_val':
            populate('sigevent.bootstrap.smap.xml')
            break
      }
      quartzScheduler.start()
   }
   
   def destroy = {
   }
   
   private void populate(String bootstrapFile) {
      def filePath = "resources/${bootstrapFile}"
      try {
         def map = new XmlSlurper().parseText(grailsApplication.getParentContext().getResource("classpath:$filePath").inputStream.text)
         map.categories.category.each {categoryObj ->
            def category = categoryObj.text()
            def group = SysEventGroup.findByCategory(category)
            if(!group) {
               log.debug("Adding "+category)
               
               def types = map.types.category.findAll{it.@name.text().equals(category) }//.size() > 0 ? map.types.{$category} : map.defaultType
               if(types.size() <= 0) {
                  types = map.defaultTypes
               }
               def purgeRate = map.purgeRates.category.findAll{it.@name.text().equals(category)} //? map.purgeRates.${category} : map.defaultPurgeRate
               if(purgeRate.size() <=0) {
                  purgeRate = map.defaultPurgeRate
               }
               types.type.each {typeObj ->
                  group = new SysEventGroup("type": typeObj.text(), "category": category, "purgeRate": purgeRate.text() as Integer)
                  if(!group.save(flush: true)) {
                     log.debug("Failed to populate EventGroup: "+group.errors.allErrors.join())
                  }
               }
            }
         }
         
         map.roles.role.each {entry ->
            def role = SysRole.findByName(entry.name.text())
            if(!role) {
               log.info("Adding role: "+entry.name.text())
               
               Integer admin = entry.admin.text() as Boolean ? 1 : 0
               role = new SysRole("name": entry.name.text(), "admin": admin)
               if(!role.save(flush: true)) {
                  log.error("Failed to populate Role: "+role.errors.allErrors.join())
               }
            }
         }
         
         map.users.user.each {entry ->
            def user = SysUser.findByUsername(entry.username.text())
            if(!user) {
               def role = SysRole.findByName(entry.role.text())
               if(!role) {
                  log.info("Role is missing, skipping: "+entry.role.text())
               } else {
                  log.info("Adding user: "+entry.username.text())
                  
                  def password = DigestUtility.getDigest("SHA-1", entry.password.text())
                  user = new SysUser(
                     "username": entry.username.text(),
                     "password": password.toLowerCase(),
                     "role": role
                  )
                  if(!user.save(flush: true)) {
                     log.error("Failed to populate User: "+user.errors.allErrors.join())
                  }
               }
            }
         }
      }
      catch (IOException e) {
         log.error "Unable to process bootstrap file ${filePath}", e
      }
   }
}
