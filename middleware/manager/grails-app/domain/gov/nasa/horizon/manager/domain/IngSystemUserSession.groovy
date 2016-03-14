/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of System User Session domain class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngSystemUserSession {
   IngSystemUser user
   IngProductType productType
   String token
   Long issueTime
   Long expireTime

   static constraints = {
      user(nullable: false, unique: 'productType')
      productType(nullable: true)
      issueTime(nullable: false)
      expireTime(nullable: true)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_systemusersession_id_seq']
   }
}