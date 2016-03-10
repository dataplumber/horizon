/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of local workspace class. 
 *
 * @author T. Huang
 * @version $Id: $
 */
public class LocalWorkspaceFactory implements WorkspaceFactory {

	private static Log logger = LogFactory.getLog(LocalWorkspaceFactory.class);

   public LocalWorkspaceFactory() {
      logger.debug ("LocalWorkspaceFactory created");
   }

   @Override
   public Workspace createWorkspace(String productType) {
      return new LocalWorkspace(System.getProperty("horizon.local.staging"), productType);
   }
}
