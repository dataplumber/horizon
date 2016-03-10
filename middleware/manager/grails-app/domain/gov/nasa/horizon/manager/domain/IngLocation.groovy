/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of Storage Location domain class
 *
 * @author Nga Chung
 * @version $Id: $
 */
class IngLocation {

   /**
    * The local directory path
    */
   String localPath

   /**
    * The remote path to access this location
    */
   String remotePath

   /**
    * The remote access protocol to be used to access this location
    */
   String remoteAccessProtocol

   /**
    * The max space reserved for this product type in bytes
    */
   Long spaceReserved

   /**
    * The current usage on the space reserved in bytes
    */
   Long spaceUsed = 0L

   /**
    * The threshold value used to trigger notification
    */
   Long spaceThreshold

   /**
    * The moment in time when the location was created
    */
   Long created = new Date().time

   /**
    * The moment in time when the location was last used
    */
   Long lastUsed

   /**
    * The stereotype of this location
    */
   String stereotype

   /**
    * The name of the host where this location can be accessed remotely
    */
   String hostname

   /**
    * Boolean to identify whether or not storage location space is active
    */
   Boolean active = true

   static hasMany = [storages: IngStorage]

   static constraints = {
      localPath(blank: false, size: 1..255)
      remotePath(blank: true, size: 1..255)
      remoteAccessProtocol(nullable: true, inList: ['FTP', 'SFTP', 'HTTP', 'HTTPS'])
      spaceReserved(nullable: false, min: 0L)
      spaceUsed(nullable: true, min: 0L)
      spaceThreshold(nullable: true, min: 0L)
      created(nullable: true)
      lastUsed(nullable: true)
      stereotype(nullable: false, inList: ['INGEST', 'ARCHIVE'])
      hostname(blank: false, size: 1..30)
      active(nullable: false)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_location_id_seq']
   }
}
