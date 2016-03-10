/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.common.api.metadatamanifest.Constant
import gov.nasa.horizon.common.api.metadatamanifest.MetadataManifest
import gov.nasa.horizon.manager.domain.IngAccessRole
import gov.nasa.horizon.manager.domain.IngEventCategory
import gov.nasa.horizon.manager.domain.IngFederation
import gov.nasa.horizon.manager.domain.IngProductType
import gov.nasa.horizon.manager.domain.IngProductTypeRole
import gov.nasa.horizon.manager.domain.IngSystemUser
import gov.nasa.horizon.sigevent.api.EventType

/**
 * Product Type service use by the product type curation tool
 *
 * TODO: Ported from the old HORIZON implementation.  It still needs more work
 *
 * @author T. Huang
 * @version $Id: $
 */
class ProductTypeService {
   public static final String RESPONSE_OK = "OK"
   public static final String RESPONSE_ERROR = "ERROR"
   private static final String SIGEVENT_PRODUCTTYPE_ADDED = "HORIZON-PRODUCTTYPE-ADDED"
   private static final String SIGEVENT_PRODUCTTYPE_UPDATED = "HORIZON-PRODUCTTYPE-UPDATED"

   boolean transactional = true

   def grailsApplication
   def sigEventService
   def authenticationService

   public Map authenticate(String userName, String password) {
      def result = [:]
      result.Status = RESPONSE_ERROR
      result.Description = 'Username and password did not match.'
      result.Admin = "false"
      result.Role = null

      if(authenticationService.authenticate(userName, password)) {
         result.Status = RESPONSE_OK
         result.Description = "Authentication successful"
         result.Admin = "true"
      }

      return result
   }

   public void processProductTypeRequest(String userName, MetadataManifest metadataManifest, String previousProductTypeName) throws Exception {
      String productTypeName = getManifestFieldValue(metadataManifest, "productType_shortName")
      if(!productTypeName) {
         throw new Exception("Product Type name is not provided.")
      }

      def actionType = metadataManifest.getActionType()
      String methodName = null
      if(Constant.ActionType.CREATE.toString().equalsIgnoreCase(actionType)) {
         methodName = "addProductType"
      } else if(Constant.ActionType.UPDATE.toString().equalsIgnoreCase(actionType)) {
         methodName = "updateProductType"
      }

      if(methodName) {
         this."$methodName"(userName, productTypeName, previousProductTypeName, metadataManifest)
      } else {
         throw new Exception("Action type is not recognized.")
      }
   }

   private void addProductType(String userName, String productTypeName, String previousProductTypeName, MetadataManifest metadataManifest) throws Exception {
      def federationValue = grailsApplication.config.horizon_producttype_update_federation
      def purgeRateValue = grailsApplication.config.horizon_producttype_update_purge_rate

      def federation = IngFederation.findByName(federationValue)
      if(!federation) {
         throw new Exception("Federation not found: "+federationValue)
      }

      def systemUser = IngSystemUser.findByName(userName)
      if(!systemUser) {
         throw new Exception("User not found: "+userName)
      }

      def eventCategory = new IngEventCategory(name: productTypeName)
      if(!eventCategory.save(flush: true)) {
         throw new Exception("Failed to save IngEventCategory.")
      }

      def productType = new IngProductType()
      productType.federation = federation
      productType.name = productTypeName
      productType.locked = false
      productType.ingestOnly = false
      productType.relativePath = productTypeName+'/'
      productType.purgeRate = purgeRateValue
      productType.updatedBy = systemUser
      productType.note = productTypeName+' product'
      productType.eventCategory = eventCategory
      if(!productType.save(flush: true)) {
         throw new Exception("Failed to save IngProductType.")
      }

      def accessRoles = IngAccessRole.getAll()
      accessRoles.each {accessRole ->
         def productTypeRole = new IngProductTypeRole()
         productTypeRole.productType = productType
         productTypeRole.role = accessRole
         if(!productTypeRole.save(flush: true)) {
            throw new Exception("Failed to save IngProductTypeRole")
         }
      }

      def sigeventGroups = grailsApplication.config.horizon_producttype_update_sigevent
      def newGroups = []

      // TODO: this looks like a bug.  Needs to revisit
      EventType.each {eventType ->
         def entry = sigeventGroups.find{eventType.value.equalsIgnoreCase(it.type)}
         if(entry) {
            newGroups.add(['type': eventType, 'purgeRate': entry.purgeRate])
         }
      }
      newGroups.each {newGroup ->
         if(sigEventService.createGroup(productTypeName, newGroup.type, newGroup.purgeRate)) {
            log.info('Created sigevent category for new product type: '+productTypeName+', '+newGroup.type.value)
         } else {
            log.error('Failed to create sigevent category for new product type: '+productTypeName+', '+newGroup.type.value)
         }
      }

      sigEventService.send(SIGEVENT_PRODUCTTYPE_ADDED, EventType.Info, "New product type added: "+productTypeName, "New dataset added "+productTypeName+" by "+userName+": "+metadataManifest.getManifest())
      log.info("New product type added "+productTypeName+" by "+userName+": "+metadataManifest.getManifest())
   }

   private void updateProductType(String userName, String productTypeName, String previousProductTypeName, MetadataManifest metadataManifest) throws Exception {
      def productTypeId = getManifestFieldValue(metadataManifest, "producttype_producttypeId")
      def message = "Product Type updated: id="+productTypeId+", shortName="+productTypeName
      def description = message+" by "+userName+": "+metadataManifest.manifest

      sigEventService.send(SIGEVENT_PRODUCTTYPE_UPDATED, EventType.Info, message, description)
      log.info("Updating product type for manager is not supported yet but product type is updated: "+description)
   }

   private String getManifestFieldValue(MetadataManifest metadataManifest, String name) {
      def metadataField = metadataManifest.fields.find{it.name.equalsIgnoreCase(name)}
      return (metadataField) ? metadataField.value : null;
   }
}
