/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

/**
 * @author T. Huang
 * @version $Id:$
 */
public abstract class ProductTypeImpl implements ProductType {
   @Override
   public String getCache() {
      return this.getWorkspace().getLocation(Workspace.Location.CACHE);
   }

   @Override
   public String getCacheRoot() {
      return this.getWorkspace().getLocation(Workspace.Location.CACHE_ROOT);
   }

   @Override
   public String getDataStorage() {
      return this.getWorkspace().getLocation(Workspace.Location.DATA_STAGE);
   }

   @Override
   public String getDataStorageRoot() {
      return this.getWorkspace().getLocation(Workspace.Location.DATA_STAGE_ROOT);
   }

   @Override
   public String getMetadataPending() {
      return this.getWorkspace().getLocation(Workspace.Location.PENDING_STAGE);
   }

   @Override
   public String getMetadataPendingRoot() {
      return this.getWorkspace().getLocation(Workspace.Location.PENDING_STAGE_ROOT);
   }

   @Override
   public String getMetadataSubmitted() {
      return this.getWorkspace().getLocation(Workspace.Location.SUBMITTED_STAGE);
   }

   @Override
   public String getMetadataSubmittedRoot() {
      return this.getWorkspace().getLocation(Workspace.Location.SUBMITTED_STAGE_ROOT);
   }

   @Override
   public String getSubmissionError() {
      return this.getWorkspace().getLocation(Workspace.Location.SUBMISSION_ERROR);
   }

   @Override
   public String getSubmissionErrorRoot() {
      return this.getWorkspace().getLocation(Workspace.Location.SUBMISSION_ERROR_ROOT);
   }

   @Override
   public String getValidationError() {
      return this.getWorkspace().getLocation(Workspace.Location.VALIDATION_ERROR);
   }

   @Override
   public String getValidationErrorRoot() {
      return this.getWorkspace().getLocation(Workspace.Location.VALIDATION_ERROR_ROOT);
   }

   @Override
   public void cleanup() {}
}
