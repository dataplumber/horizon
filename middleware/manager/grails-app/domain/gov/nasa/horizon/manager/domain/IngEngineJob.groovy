/*
* Copyright (c) 2008-2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.domain

/**
 * Definition of Engine Job domain class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngEngineJob {

   /**
    * The product the engine is handling
    */
   IngProduct product

   /**
    * The operation to be performed
    */
   String operation

   /**
    * The previous state for the job in case a retry
    */
   String previousState

   /**
    * The moment in time when the job was assigned
    */
   Long assigned = new Date().time

   /**
    * The path to zookeeper queue node for this job
    */
   String path

   /**
    * Priority of the current job with lower numbers representing higher priority
    */
   Integer priority

   /**
    * The storage used for handling this job
    */
   IngStorage contributeStorage

   static belongsTo = [contributeStorage: IngStorage]

   static constraints = {
      product(nullable: false)
      operation(nullable: false, inList: ['ADD', 'ARCHIVE', 'REPLACE', 'DELETE', 'TRASH', 'INVENTORY'])
      previousState(nullable: false)
      assigned(nullable: false)
      path(nullable: true, size: 1..255)
      priority(nullable: false, range: 1..3)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_enginejob_id_seq']
   }
}
