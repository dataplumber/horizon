package gov.nasa.horizon.security.server
//import gov.nasa.jpl.horizon.api.Constants

/*
* Copyright (c) 2008 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/

/**
 * Definition of System User Role domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngSystemUserRole.groovy 2173 2008-10-29 20:06:48Z thuang $
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


//   boolean canAdd(IngProductTypeRole productTypeRole) {
//      if (!productTypeRole) return false
//
//      if (role.capabilities >= Constants.WRITE_ALL) {
//         return true
//      }
//
//      return (productTypeRole.role.capabilities & Constants.ADD)
//   }
//
//   boolean canRead(IngProductTypeRole productTypeRole) {
//      if (!productTypeRole) return false
//
//      if (role.capabilities >= Constants.READ_ALL) {
//         return true
//      }
//
//      return (productTypeRole.role.capabilities & Constants.GET)
//   }


   static constraints = {
      user(nullable: false)
      role(nullable: false)
   }

   static mapping = {
      //version false
	   table 'ING_SYSTEM_USER_ROLE'
      id generator:'sequence', params:[sequence:'ing_systemuserrole_id_seq']            
   }
}
