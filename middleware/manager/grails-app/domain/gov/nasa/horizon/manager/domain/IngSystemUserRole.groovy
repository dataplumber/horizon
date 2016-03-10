/*
* Copyright (c) 2008-2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.domain

import gov.nasa.horizon.ingest.api.Constants

/**
 * Definition of System User Role domain class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngSystemUserRole {

   /**
    * System user reference
    */
   IngSystemUser user

   /**
    * Access role reference
    */
   IngAccessRole role


   boolean canAdd(IngProductTypeRole productTypeRole) {
      if (!productTypeRole) return false

      if (role.capabilities >= Constants.WRITE_ALL) {
         return true
      }

      return (productTypeRole.role.capabilities & Constants.ADD)
   }

   boolean canRead(IngProductTypeRole productTypeRole) {
      if (!productTypeRole) return false

      if (role.capabilities >= Constants.READ_ALL) {
         return true
      }

      return (productTypeRole.role.capabilities & Constants.GET)
   }


   static constraints = {
      user(nullable: false)
      role(nullable: false)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_systemuserrole_id_seq']
   }
}
