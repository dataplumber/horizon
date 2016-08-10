/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/

import gov.nasa.horizon.common.api.zookeeper.api.ZkAccess
import gov.nasa.horizon.common.api.zookeeper.api.ZkFactory
import gov.nasa.horizon.common.api.zookeeper.api.constants.JobPriority
import gov.nasa.horizon.ingest.api.Encrypt
import gov.nasa.horizon.ingest.api.Lock
import gov.nasa.horizon.ingest.api.State
import gov.nasa.horizon.manager.domain.*
import grails.util.Environment

/**
 * Bootstrap call that gets executed at Manager startup.  It depends on the
 * Grails environment specified at boot time to determine which configuration
 * file to load.  It requires all config file to be under WEB-INF/data folder.
 * The names of the current bootstrap configuration files can be found under the
 * 'switch' statement.
 *
 * @author T. Huang
 * @version $Id: $
 */
class BootStrap {
   def grailsApplication
   private static final int ENGINE_JOBS_PER_PAGING = 10

   def quartzScheduler
   def storageService

   def init = { servletContext ->
      log.debug "*** BootStrap, Environment.current: ${Environment.current}"

      environments {
         postgresql {
            log.debug '*** detected postgresql'
            this.config('manager.bootstrap.dev.xml')
         }
         development {
            log.debug '*** detected development'
            this.config('manager.bootstrap.dev.xml')
         }
         test {
            log.debug '*** detected test'
            this.config('manager.bootstrap.test.xml')
         }
         production {
            log.debug '*** detected production'
            this.config('manager.bootstrap.pro.xml')
         }
         sit {
            log.debug '*** detected sit'
            this.config('manager.bootstrap.dev.xml')
         }
         smap_cal_val {
            log.debug '*** detected SMAP Cal/Val'
            this.config('manager.bootstrap.smap.xml')
         }
      }

      log.debug 'Cleaning up EngineJobs'
      this.cleanEngineJobs()
      log.debug 'Done cleaning up EngineJobs'
      quartzScheduler.start()
   }
   def destroy = {
   }

   void config(String bootstrapFile) {
      def filePath = "resources/${bootstrapFile}"
      try {
         def bootstrap = new XmlSlurper().parseText(grailsApplication.getParentContext().getResource("classpath:$filePath").inputStream.text)

         // register federation
         String federation = bootstrap.federation.text().trim()
         def fed = IngFederation.findByName(federation)
         if (!fed) {
            fed = new IngFederation(
                  name: federation,
                  note: "${federation} Federation",
            )
         }
         fed.updated = new Date().time
         fed.hostname = System.getProperty('server.host') ?: 'localhost'
         fed.port = (System.getProperty('server.port.https') ?: 8443) as Integer
         if (!fed.save(flush: true)) {
            fed.errors.each {
               log.error it
            }
         }

         IngSystemUser admin = null

         // register users
         bootstrap.users.user.each {
            String username = it.username.text().trim()
            String password = it.password.text().trim()
            String role = it.role.text().trim()
            IngSystemUser user = IngSystemUser.findByName(username)
            IngAccessRole accessRole = IngAccessRole.findByName(role)
            if (!user) {
               if (!accessRole) {
                  accessRole = new IngAccessRole(
                        name: role,
                        capabilities: it.capabilities.text().toInteger()
                  )
                  if (!accessRole.save(flush: true)) {
                     accessRole.errors.each {
                        log.error it
                     }
                  }
               }
               user = new IngSystemUser(
                     name: username,
                     password: Encrypt.encrypt(password),
                     fullname: it.name.text().trim(),
                     email: it.email.text().trim(),
                     admin: it.admin.text().toBoolean(),
                     readAll: it.readAll.text().toBoolean(),
                     writeAll: it.writeAll.text().toBoolean(),
                     note: username
               )
               if (!user.save(flush: true)) {
                  user.errors.each {
                     log.error it
                  }
               }
               if (!admin) {
                  admin = user
               }
               IngSystemUserRole userRole = new IngSystemUserRole(
                     user: user,
                     role: accessRole
               )
               if (!userRole.save(flush: true)) {
                  userRole.errors.each {
                     log.error it
                  }
               }
            }
            else {
                if (!admin) {
                    admin = user
                }
            }
         }

         // register product types
         bootstrap.productTypes.productType.each {
            String ptname = it.name.text().trim()
            String ptevent = it.event?.text()?.trim()
            Boolean localStaged = false
            if ((it.localStaged?.text()?.trim())?.size() != 0) {
               localStaged = it.localStaged.text().trim().toBoolean()
            }
            Integer purgeRate = 0
            if ((it.purgeRate?.text()?.trim())?.size() != 0) {
               purgeRate = (it.purgeRate?.text()?.trim()).toInteger()
            }
            IngProductType productType = IngProductType.findByName(ptname)
            String event = (ptevent.size() > 0 ? ptevent : ptname)
            IngEventCategory category = IngEventCategory.findByName(event)
            if (!category) {
               category = new IngEventCategory(
                     name: event
               )
               if (!category.save(flush: true)) {
                  category.errors.each {
                     log.error it
                  }
               }
            }
            Integer productPriority = JobPriority.DEFAULT.value
            if ((it.priority?.text()?.trim())?.size() != 0) {
               productPriority = JobPriority.valueOf(it.priority?.text()?.trim()).value
            }
            if (!productType) {
               productType = new IngProductType(
                     federation: fed,
                     name: ptname,
                     locked: false,
                     ingestOnly: false,
                     relativePath: "${ptname}${File.separator}",
                     purgeRate: purgeRate,
                     localStaged: localStaged,
                     updatedBy: admin,
                     updated: new Date().time,
                     note: "${ptname} product",
                     eventCategory: category,
                     priority: productPriority
               )
               if (!productType.save(flush: true)) {
                  productType.errors.each {
                     log.error it
                  }
               }

               List<IngAccessRole> accessRoles = IngAccessRole.getAll()
               accessRoles.each { IngAccessRole accessRole ->
                  IngProductTypeRole ptRole = new IngProductTypeRole(
                        productType: productType,
                        role: accessRole
                  )
                  if (!ptRole.save(flush: true)) {
                     ptRole.errors.each {
                        log.error it
                     }
                  }
               }
            } else {
               if (!productType.eventCategory || !productType.eventCategory.equals(category)) {
                  log.trace("Updated event category for ${productType.name}")
                  productType.eventCategory = category
               }
               if (productType.federation.id != fed.id) {
                  log.trace("Update fedeation for ${productType.name}")
                  productType.federation = fed
               }

               String pr = it.priority?.text()?.trim()
               if (pr && pr.size() > 0) {
                  Integer priority = JobPriority.valueOf(pr).value
                  if (productType.priority != priority) {
                     productType.priority = priority
                     log.trace("Changing priority for ${productType.name}")
                  }
               }
               if (productType.isDirty()) {
                  log.trace("Save ${productType.name}")
                  productType.save(flush: true)
               }
            }
         }

         // register storage locations
         bootstrap.locations.location.each {
            String localPath = it.localPath.text().trim()
            IngLocation location = IngLocation.findByLocalPath(localPath)
            if (!location) {
               location = new IngLocation(
                     stereotype: it.stereotype.text().trim(),
                     spaceReserved: it.spaceReserved.text().toLong(),
                     spaceThreshold: it.spaceThreshold.text().toLong(),
                     localPath: localPath,
                     remotePath: it.remotePath.text().trim(),
                     hostname: it.hostname.text().trim(),
                     remoteAccessProtocol: it.protocol.text().trim()
               )
               if (!location.save(flush: true)) {
                  location.errors.each {
                     log.error it
                  }
               }
            }
         }

         // register storage
         bootstrap.storages.storage.each {
            String localPath = it.localPath.text().trim()
            IngLocation location = IngLocation.findByLocalPath(localPath)
            if (!location) {
               log.error("Location with local path ${localPath} does not exists. Cannot map storage ${it.name} to this location.")
            } else {
               String stname = it.name.text().trim()
               IngStorage storage = IngStorage.findByName(stname)
               String priority = 'NORMAL'
               if (it.priority?.text()?.trim()?.size() > 0) {
                  priority = it.priority.text().trim().toUpperCase()
               }
               Integer storagePriority = JobPriority.valueOf(priority).value
               if (!storage) {
                  storage = new IngStorage(
                        name: stname,
                        priority: storagePriority,
                        location: location
                  )
                  if (!storage.save(flush: true)) {
                     storage.errors.each {
                        log.error it
                     }
                  }
               } else {
                  if (storage.priority != storagePriority) {
                     storage.priority = storagePriority
                     log.debug("Changing priority for storage ${it.name} to ${storagePriority}")
                  }
                  if (storage.isDirty()) {
                     log.trace("Save storage ${storage.name}")
                     storage.save(flush: true)
                  }
               }
            }
         }

         // register remote systems
         bootstrap.remoteSystems.remoteSystem.each {
            String rootUri = it.rootUri.text().trim()
            IngRemoteSystem remoteSystem = IngRemoteSystem.findByRootUri(rootUri)
            Integer maxConnections = 1
            if (it.maxConnections?.text()?.trim()?.size() > 0) {
               maxConnections = it.maxConnections.text().trim().toInteger()
            }
            if (!remoteSystem) {
               remoteSystem = new IngRemoteSystem(
                     rootUri: rootUri,
                     organization: it.organization.text().trim(),
                     username: it.username.text().trim(),
                     password: it.password.text().trim(),
                     maxConnections: maxConnections,
                     created: new Date().time,
                     updated: new Date().time,
                     updatedBy: admin.name
               )
               if (!remoteSystem.save(flush: true)) {
                  remoteSystem.errors.each {
                     log.error it
                  }
               }
            }
         }
         grailsApplication.config.put('manager_federation', federation)
      } catch (IOException e) {
         log.error "Unable to process bootstrap file ${filePath}", e
      }
   }

   private void cleanEngineJobs() {
      def engineJobsCount = IngEngineJob.createCriteria().count {
         product {
            productType {
               federation {
                  eq("name", grailsApplication.config.manager_federation as String)
               }
            }
         }
      }
      //def engineJobsCount = IngEngineJob.createCriteria().count(query)
      Integer engineJobsIndex = 0
      while (engineJobsIndex < engineJobsCount) {
         def engineJobs = IngEngineJob.createCriteria().list(
               max: ENGINE_JOBS_PER_PAGING,
               offset: engineJobsIndex,
               order: "desc",
               sort: "assigned",
         ) {
            product {
               productType {
                  federation {
                     eq("name", grailsApplication.config.manager_federation as String)
                  }
               }
            }
         }
         engineJobs.each { IngEngineJob engineJob ->
            ZkAccess zk = ZkFactory.getZk(grailsApplication.config.horizon_zookeeper_url as String)

            if (engineJob.path) {
               if (!zk.removeNode(engineJob.path)) {
                  zk.removeProcessNode(engineJob.product.productType.name + "/" + engineJob.product.name)
               }
            }

            // put storage back as needed
            if ([Lock.ADD, Lock.REPLACE, Lock.ARCHIVE].find { it == Lock.valueOf(engineJob.operation) }
                  && [State.PENDING, State.PENDING_STORAGE, State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].find { it == State.valueOf(engineJob.previousState) }) {
               long totalSize = engineJob.product.files.sum { it.fileSize } as long
               storageService.updateStorage(-totalSize, new Date().time, engineJob.contributeStorage.id)
            }

            try {
               IngProduct product = IngProduct.get(engineJob.product.id)
               product.currentLock = engineJob.operation
               product.currentState = engineJob.previousState
               if (!product.save(flush: true)) {
                  throw new Exception("Failed to save product: " + product.errors.allErrors.join(' \n'))
               }

               engineJob.delete()
               log.debug("Cleaned EngineJob[" + engineJob.id + "].")
            } catch (Exception exception) {
               log.error("Failed to rollback enginejob.", exception)
            }
         }

         engineJobsIndex += ENGINE_JOBS_PER_PAGING
      }
   }
}
