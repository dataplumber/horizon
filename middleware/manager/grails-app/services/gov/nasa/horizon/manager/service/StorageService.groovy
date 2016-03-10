/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.ingest.api.Stereotype
import gov.nasa.horizon.manager.domain.IngLocation
import gov.nasa.horizon.sigevent.api.EventType
import groovy.sql.Sql
import org.springframework.jdbc.core.JdbcTemplate

import javax.sql.DataSource

/**
 *
 * @author T. Huang
 * @version $Id: $
 */
class StorageService {
   private static final String SIG_EVENT_CATEGORY = "HORIZON"
   def sigEventService
   def dataSource
   boolean transactional = false

   public void checkStorage() {
      log.debug("checkStorage started")

      def storageList = IngLocation.list()
      storageList.each { storage ->
         log.debug("\tchecking storage: " + storage.localPath)

         if (storage.spaceUsed >= storage.spaceThreshold) {
            sendNotification(storage)
         }
      }

      log.debug("checkStorage stopped")
   }

   private void sendNotification(IngLocation storage) {
      log.warn("Storage ${storage.localPath} is hitting its threshold " +
                  "(${storage.spaceUsed} bytes / ${storage.spaceReserved} bytes). " +
                  "This can prevent granules from being ingested and archived.")
      sigEventService.send(
            SIG_EVENT_CATEGORY,
            EventType.Warn,
            "Storage ${storage.localPath} hitting its threshold",
            "Storage ${storage.localPath} is hitting its threshold " +
                  "(${storage.spaceUsed} bytes / ${storage.spaceReserved} bytes). " +
                  "This can prevent granules from being ingested and archived."
      )
   }

   public int getStorage(long totalSize, Stereotype stereotype, int priority) {
      def result = -1
      String spcall = "SELECT getStorage('${stereotype.toString()}', ${totalSize}, ${priority})"

      try {
      result = new JdbcTemplate(dataSource).queryForInt(spcall)
      } catch (Exception e) {
         log.error ("Unable to execute stored procedure '${spcall}'")
         log.debug(e.message, e)
      }
      
      checkStorage()
      
      return result

      /*
      Sql sql = new Sql(dataSource as DataSource)

      def result = -1
      try {
         sql.call("{?=call getStorage(?,?,?)}", [Sql.INTEGER, stereotype.toString(), totalSize, priority]) { storage -> result = storage }
      } catch (Exception e) {

      }
      return result
      */
   }

   public int getStorageByTypeAndPriority(Stereotype stereotype, int priority) {

      def result = -1
      String spcall = "SELECT getStorageByTypeAndPriority('${stereotype.toString()}', ${priority})"

      try {
         result = new JdbcTemplate(dataSource).queryForInt(spcall)
      } catch (Exception e) {
         log.error ("Unable to execute stored procedure '${spcall}'")
         log.debug(e.message, e)
      }
      return result

      /*
      Sql sql = new Sql(dataSource as DataSource)

      def result = -1
      try {
         sql.call("{?=call getStorageByTypeAndPriority(?,?)}", [Sql.INTEGER, stereotype.toString(), priority]) { storage -> result = storage }
      } catch (Exception e) {

      }
      return result
      */
   }

   public void updateStorage(long totalSize, long lastUsed, long id) {
      String spcall = "SELECT updateStorage(${totalSize}, ${lastUsed}, ${id})"

      try {
         new JdbcTemplate(dataSource).queryForInt(spcall)
      } catch (Exception e) {
         log.error ("Unable to execute stored procedure '${spcall}'")
         log.debug(e.message, e)
      }

      /*
      Sql sql = new Sql(dataSource as DataSource)
      sql.call("BEGIN updateStorage(?,?,?); END;", [totalSize, lastUsed, id])
      */
   }
}
