/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

import gov.nasa.horizon.common.api.zookeeper.api.constants.JobPriority

/**
 * Definition of Storage Location domain class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngStorage {

   /**
    * A unique storage name (e.g. use the ingest enging host)
    */
   String name

   /**
    * The moment in time when the location was created
    */
   Long created = new Date().time

   /**
    * The priority of products this storage can handle
    */
   Integer priority

   /**
    * Returns JobPriority enum corresponding to this product type priority integer value
    */
   JobPriority getStorageJobPriority() {
      if (this.priority) {
         return JobPriority.values().find { it.value == this.priority }
      }
      return null
   }

   static transients = ['storageJobPriority']

   static belongsTo = [location: IngLocation]

   static constraints = {
      name(unique: true, size: 1..255)
      created(nullable: true)
      priority(nullable: true, range: 1..3)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_storage_id_seq']
   }
}
