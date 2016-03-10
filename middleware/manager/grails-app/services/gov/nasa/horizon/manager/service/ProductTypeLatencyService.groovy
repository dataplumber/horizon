/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.common.api.util.DateTimeUtility
import gov.nasa.horizon.sigevent.api.EventType

/**
 * Service to check Product Type data latency
 *
 * TODO: this was port from legacy HORIZON.  Needs revisit
 *
 * @author T. Huang
 * @version $Id: $
 */
class ProductTypeLatencyService {
   private static final String SIG_EVENT_CATEGORY = "LATENCY"
   boolean transactional = false
   def inventoryService
   def sigEventService

   public void checkDataLatency() {
      log.debug("checkDataLatencyService() - Started")

      List<Map> productTypeStatusList = inventoryService.productTypeStatusList
      for (Map productTypeStatus : productTypeStatusList) {
         log.debug("\tProcessing: " + productTypeStatus['dataStream'])

         String message = null
         String data = null

         Integer dataFrequency = productTypeStatus['dataFrequency'] as Integer
         String accessType = productTypeStatus['accessType']
         if ((dataFrequency) && !('DORMANT'.equalsIgnoreCase(accessType))) {
            Date lastIngestTime = productTypeStatus['lastIngestTime'] as Date
            if (lastIngestTime) {
               Date lastIngestTimeDate = new Date(lastIngestTime.getTime())
               Date currentTime = productTypeStatus['currentTime'] as Date

               long timeElapsedInSecond = (currentTime.getTime() - lastIngestTimeDate.getTime()) / 1000
               long dataFrequencyInSecond = (dataFrequency * 60 * 60)
               if (timeElapsedInSecond > dataFrequencyInSecond) {
                  message = productTypeStatus['dataStream'] + ": Products are overdue for this data stream"
                  data = productTypeStatus['dataStream'] + ": Products are overdue for " +
                        String.format("%.2f", ((timeElapsedInSecond - dataFrequencyInSecond) / (60.0f * 60.0f))) + " hours. Ingest time of latest granule " +
                        productTypeStatus['name'] + " is " + DateTimeUtility.parseDate(lastIngestTimeDate)
               }
            } else {
               message = productTypeStatus['dataStream'] + ": No products found for this data stream"
            }
         }

         log.debug("\tDone processing: " + productTypeStatus['dataStream'])
         if (message) {
            sigEventService.send(
                  SIG_EVENT_CATEGORY,
                  EventType.Error,
                  message,
                  data
            )
         }
      }

      log.debug("checkDataLatencyService() - Ended")
   }
}
