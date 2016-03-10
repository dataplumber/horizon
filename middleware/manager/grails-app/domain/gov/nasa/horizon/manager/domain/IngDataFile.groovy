/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of Data File domain class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngDataFile {

   /**
    * Name of the data file
    */
   String name

   /**
    * Provider information
    */
   IngRemoteSystem provider

   /**
    * Remote path to the file
    */
   String remotePath

   /**
    * The size of the file in bytes
    */
   Long fileSize

   /**
    * The MD5 checksum associated with the file
    */
   String checksum

   /**
    * The compression type used on the file, if any.
    */
   String compression = 'NONE'

   /**
    * Current lock level for the record
    */
   String currentLock = 'NONE'

   /**
    * The moment in time when the ingestion process was started
    */
   Long ingestStarted

   /**
    * The moment in time when the ingestion process was completed
    */
   Long ingestCompleted

   /**
    * Text description
    */
   String note

   static belongsTo = [product: IngProduct]

   static constraints = {
      name(unique: ['product', 'currentLock'], size: 1..125)
      provider(nullable: true)
      remotePath(nullable: false)
      fileSize(nullable: true, min: 0L)
      checksum(nullable: true, maxSize: 40)
      compression(nullable: true, inList: ['NONE', 'GZIP', 'BZIP2', 'ZIP'])
      currentLock(nullable: true, inList: ['NONE', 'GET', 'ADD', 'REPLACE', 'DELETE', 'RESERVED', 'RENAME'])
      ingestStarted(nullable: true)
      ingestCompleted(nullable: true)
      note(nullable: true, size: 1..1024)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_data_file_id_seq']
   }
}
