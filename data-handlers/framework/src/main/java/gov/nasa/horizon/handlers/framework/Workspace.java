/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

/**
 * @author T. Huang
 * @version $Id:$
 */
public interface Workspace extends Worker {

   enum Location {
      CACHE,
      CACHE_ROOT,
      VALIDATION_ERROR,
      VALIDATION_ERROR_ROOT,
      SUBMISSION_ERROR,
      SUBMISSION_ERROR_ROOT,
      PENDING_STAGE,
      PENDING_STAGE_ROOT,
      SUBMITTED_STAGE,
      SUBMITTED_STAGE_ROOT,
      DATA_STAGE,
      DATA_STAGE_ROOT
   }

   void setProductType(String name);

   String getProductType();

   void setWorkspaceRoot (String workspaceRoot);

   String getWorkspaceRoot ();

   String getLocation(Location location);
}
