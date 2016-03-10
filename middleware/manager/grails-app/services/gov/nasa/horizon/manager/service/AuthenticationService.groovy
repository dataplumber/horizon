/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.ingest.api.Encrypt
import gov.nasa.horizon.manager.domain.IngSystemUser
import gov.nasa.horizon.security.client.SecurityAPIFactory
import gov.nasa.horizon.security.client.api.SecurityAPI

/**
 * @author T. Huang
 * @version $Id: $
 */
class AuthenticationService {
   def grailsApplication

   boolean transactional = true

   boolean isLoggedIn(session) {
      return (session['monitor.user'] != null)
   }

   void logIn(session, user) {
      session['monitor.user'] = user
   }

   void logOut(session) {
      session['monitor.user'] = null
   }

   IngSystemUser verifyUser(String username, String password) {
      return IngSystemUser.findWhere(name: username, password: Encrypt.encrypt(password))
   }

   boolean authenticate(String username, String password) {
      def host = grailsApplication.config.gov.nasa.horizon.security.host
      def port = grailsApplication.config.gov.nasa.horizon.security.port
      def realm = grailsApplication.config.gov.nasa.horizon.security.realm

      SecurityAPI sapi =  SecurityAPIFactory.getInstance(host, port)
      return sapi.authenticate(username, password, realm)
   }
}
