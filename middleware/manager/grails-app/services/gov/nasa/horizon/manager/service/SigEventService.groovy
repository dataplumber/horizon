/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.common.api.util.DateTimeUtility
import gov.nasa.horizon.sigevent.api.EventType
import gov.nasa.horizon.sigevent.api.SigEvent
import gov.nasa.horizon.sigevent.api.SigEventGroup

/**
 *
 * @author T. Huang
 * @version $Id: $
 */
class SigEventService {
   def grailsApplication

   private static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
   boolean transactional = false

   def send(
         String category,
         EventType eventType,
         String description,
         String data,
         String hostname = null
   ) {
      def timeStamp = DateTimeUtility.parseDate(UTC_FORMAT, new Date())

      def sigEvent = new SigEvent(grailsApplication.config.horizon_sigevent_url as String)
      sigEvent.create(
            eventType,
            category,
            grailsApplication.config.manager_federation + " manager",
            grailsApplication.config.manager_federation + " manager",
            ((hostname) ? hostname : InetAddress.localHost.hostName),
            description,
            null,
            data
      )
   }

   def createGroup(String category, EventType eventType, long purgeRate) {
      def sigEventGroup = new SigEventGroup(grailsApplication.config.horizon_sigevent_url as String)
      def response = sigEventGroup.create(eventType, category, purgeRate)
      return !response.hasError()
   }
}
