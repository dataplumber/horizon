/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.ingest.client;

import gov.nasa.horizon.ingest.client.ServiceLocatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Wrapper interface for service lookup operations. This class assumes the
 * following properties have been set either through standard Properties object
 * or via command line configuration to the JVM
 * <ul>
 * <li>java.naming.factory.initial=org.jnp.interface.NamingContextFactory
 * <li>java.naming.provider.url=jnp://${JAVA_NAMING_HOSTNAME}:1099
 * <li>java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
 * </ul>
 * The values above uses JBoss naming factory implementation. It should work
 * with any standard J2EE Naming Service implementations.
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: ServiceLocator.java 244 2007-10-02 20:12:47Z axt $
 */
public class ServiceLocator {

   public static Log _logger = LogFactory.getLog(ServiceLocator.class);

   /**
    * This method returns the initial context object to the Naming Service.
    * 
    * @return reference to an initial context object
    * @throws ServiceLocatorException when failed to create the context
    */
   private static InitialContext _getInitialContext()
         throws ServiceLocatorException {
      InitialContext ctx = null;
      try {
         ctx = new InitialContext();
      } catch (NamingException e) {
         e.printStackTrace();
         throw new ServiceLocatorException(e);
      }
      return ctx;
   }

   public static def lookup(String name) throws ServiceLocatorException {
      def obj = null;
      ServiceLocator._logger.debug("Looking up service name: " + name);
      try {
         Context ctx = ServiceLocator._getInitialContext();
         Object tmp = ctx.lookup(name);
         if (tmp != null)
            obj = ctx.lookup(name);
      } catch (ClassCastException e) {
         throw new ServiceLocatorException(e);
      } catch (NamingException e) {
         throw new ServiceLocatorException(e);
      }
      return obj;
   }
}
