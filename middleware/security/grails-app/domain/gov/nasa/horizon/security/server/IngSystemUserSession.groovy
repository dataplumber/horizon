package gov.nasa.horizon.security.server
/*
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */

/**
 * Definition of System User Session domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngSystemUserSession.groovy 2173 2008-10-29 20:06:48Z thuang $
 */
class IngSystemUserSession {
   IngSystemUser user
//   IngProductType productType
   Integer productTypeId
   String token
   Long issueTime
   Long expireTime

   static constraints = {
      user(nullable: false, unique: 'productTypeId')
      productTypeId(nullable: true)
      issueTime(nullable: false)
      expireTime(nullable: true)
   }

   static mapping = {
      //version false
	  table 'ING_SYSTEM_USER_SESSION'
      id generator:'sequence', params:[sequence:'ing_systemusersession_id_seq']            
   }
}
