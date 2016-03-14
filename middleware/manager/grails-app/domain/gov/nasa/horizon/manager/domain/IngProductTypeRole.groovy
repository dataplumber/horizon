/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of Product Type Role class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngProductTypeRole {

   /**
    * The product type reference
    */
   IngProductType productType

   /**
    * The access role reference
    */
   IngAccessRole role

   static constraints = {
      productType(nullable: false)
      role(nullable: false)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_producttyperole_id_seq']
   }
}