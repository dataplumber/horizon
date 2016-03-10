/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of Remote System domain class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngRemoteSystem {

   /**
    * The root URI to the remote system
    */
   String rootUri

   /**
    * The organization this system belongs to
    */
   String organization

   /**
    * The user name to access this remote system
    */
   String username

   /**
    * The associated password
    */
   String password

   /**
    * The max number of concurrent connections can be made
    */
   Integer maxConnections = 1

   /**
    * The moment in time when this entry was created
    */
   Long created = new Date().getTime()

   /**
    * The moment in time when this was was updated
    */
   Long updated

   /**
    * The admin who updated this record last
    */
   String updatedBy

   /**
    * Text description
    */
   String note

   static constraints = {
      rootUri(blank: false, size: 1..255, unique: true)
      organization(blank: false)
      username(nullable: true)
      password(nullable: true)
      maxConnections(range: 1..32)
      created(nullable: true)
      updated(nullable: true)
      updatedBy(nullable: true)
      note(nullable: true)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_remotesystem_id_seq']
   }
}
