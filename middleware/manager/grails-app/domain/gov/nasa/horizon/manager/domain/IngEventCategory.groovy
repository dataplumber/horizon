/*
* Copyright (c) 2008-2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.domain

class IngEventCategory {

   String name

   static constraints = {
      name(unique: true, size: 1..100)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_event_category_id_seq']
   }
}

