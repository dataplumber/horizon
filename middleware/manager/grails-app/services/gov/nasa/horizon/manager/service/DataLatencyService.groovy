/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service


import java.sql.Timestamp

import gov.nasa.horizon.common.api.util.DateTimeUtility

import gov.nasa.horizon.sigevent.api.EventType

/**
 * DataLatencyService
 */
class DataLatencyService {
   private static final String SIG_EVENT_CATEGORY = "LATENCY"
   boolean transactional = false
   def inventoryService
   def sigEventService
   
   public void checkDataLatency() {
      log.debug("checkDataLatencyService() - Started")
      
      List datasetStatusList = inventoryService.getProductTypeStatusList()
      for(Map datasetStatus : datasetStatusList) {
         log.debug("\tProcessing: "+datasetStatus['dataStream'])
         
         Date lastIngestTimeDate = null
         String message = null
         String data = null
        
         Integer dataFrequency = datasetStatus['dataFrequency']
         String accessType = datasetStatus['accessType']
         if((dataFrequency) && !('DORMANT'.equalsIgnoreCase(accessType))) {
            Date lastIngestTime = datasetStatus['lastIngestTime']
            if(lastIngestTime) {
               lastIngestTimeDate = new Date(lastIngestTime.getTime())
               Date currentTime = datasetStatus['currentTime']
            
               long timeElapsedInSecond = (currentTime.getTime() - lastIngestTimeDate.getTime()) / 1000
               long dataFrequencyInSecond = (dataFrequency * 60 * 60)
               if(timeElapsedInSecond > dataFrequencyInSecond) {
                  message = datasetStatus['dataStream']+": Products are overdue for this data stream"
                  data = datasetStatus['dataStream']+": Products are overdue for "+
                     String.format("%.2f", ((timeElapsedInSecond - dataFrequencyInSecond) / (60.0f * 60.0f)))+" hours. Ingest time of latest granule "+
                     datasetStatus['name'] + " is " + DateTimeUtility.parseDate(lastIngestTimeDate)
               }
            } else {
               message = datasetStatus['dataStream']+": No products found for this data stream"
            }
         }
         
         log.debug("\tDone processing: "+datasetStatus['dataStream'])
         if(message) {
            sigEventService.send(
               DataLatencyService.SIG_EVENT_CATEGORY,
               EventType.Error,
               message,
               data
            )
         }
      }
      
      log.debug("checkDataLatencyService() - Ended")
   }
}
