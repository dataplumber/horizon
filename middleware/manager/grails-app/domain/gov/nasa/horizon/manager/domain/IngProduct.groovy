/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of Product domain class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngProduct {

   /**
    * Name of the product
    */
   String name

   /**
    * The agent that added ore replaced this product
    */
   String contributor

   /**
    * The ingest storage used for handling this product
    */
   IngStorage contributeStorage

   /**
    * Local path to the file relative to the storage location associated with the product
    */
   String localRelativePath

   /**
    * Remote path to the file relative to the storage location associated with the produt
    */
   String remoteRelativePath

   /**
    * When the record was created
    */
   Long created = new Date().time

   /**
    * The moment in time when the record was last updated
    */
   Long updated

   /**
    * The moment in time when the record was ingested
    */
   Long completed

   /**
    * The moment in time when the record was archived
    */
   Long archivedAt

   /**
    * Text describing archive information
    */
   String archiveNote

   /**
    * Current lock level for the record
    */
   String currentLock

   /**
    * Current state of the record
    */
   String currentState

   /**
    * The ID into the inventory catalog for this product
    */
   Long inventoryId

   /**
    * The number of retries allow for ingesting this product.
    * Ingestion will abort when the currentRetries is set down to zero
    */
   Integer currentRetries = 5

   /**
    * The number of readers if record is locked for READ
    */
   Integer readers

   /**
    * The version of this product
    */
   Integer versionNumber = 1

   /**
    * The version string provided by the provider
    */
   String versionString = ""

   /**
    * The initial submission record (in XML)
    */
   String initialText

   /**
    * The complete submission record (in XML)
    */
   String completeText

   /**
    * The archive information record (in XML)
    */
   String archiveText

   /**
    * Flag to indicate if the contributor needs to be notified
    */
   Boolean notify = false

   /**
    * Any description information
    */
   String note = ""

   static belongsTo = [productType: IngProductType]

   /**
    * A data product can contain a collection of associated data files
    * */
   static hasMany = [files: IngDataFile]

   static constraints = {
      name(unique: ['productType', 'versionString'], size: 1..125)
      contributor(nullable: false)
      contributeStorage(nullable: true)
      localRelativePath(nullable: true)
      remoteRelativePath(nullable: true)
      created(nullable: true)
      updated(nullable: true)
      completed(nullable: true)
      archivedAt(nullable: true)
      archiveNote(nullable: true, size: 1..3000)
      currentLock(nullable: true, inList: ['NONE', 'GET', 'ADD', 'ARCHIVE', 'INVENTORY', 'REPLACE', 'DELETE', 'PENDING_RESERVED', 'RESERVED', 'RENAME', 'TRASH', 'PURGE'])
      currentState(nullable: false, inList: ['PENDING', 'PENDING_STORAGE', 'ASSIGNED', 'STAGED', 'LOCAL_STAGED', 'INVENTORIED', 'PENDING_ARCHIVE', 'PENDING_ARCHIVE_STORAGE', 'ARCHIVED', 'ABORTED', 'RESERVED', 'PENDING_ASSIGNED'])
      inventoryId(nullable: true)
      currentRetries(range: 0..10)
      readers(nullable: true, min: 0)
      versionNumber(nullable: false, min: 1)
      versionString(nullable: true)
      initialText(nullable: true)
      completeText(nullable: true)
      archiveText(nullable: true)
      note(nullable: true, size: 1..3000)
   }

   /**
    * Custom mapping to make sure we have enough space to store the XML text
    */
   static mapping = {
      columns {
         initialText type: 'text'
         completeText type: 'text'
         archiveText type: 'text'
      }

      id generator: 'sequence', params: [sequence: 'ing_product_id_seq']
      versionNumber generator: 'sequence', params: [sequence: 'ing_product_version_seq']
   }
}
