/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of local workspace
 *
 * @author T. Huang
 * @version $Id: $
 */
public class LocalWorkspace implements Workspace {
   protected static Log logger = LogFactory.getLog(LocalWorkspace.class);

   private Map<Location, String> location = new HashMap<>();
   private String workspaceRoot;

   String productType;

   public LocalWorkspace() {

   }

   public LocalWorkspace(String workspaceRoot, String productType) {
      this.workspaceRoot = workspaceRoot;
      this.productType = productType;
   }

   @Override
   public void setWorkspaceRoot(String workspaceRoot) {
      File wr = new File(workspaceRoot);
      this.workspaceRoot = wr.getAbsolutePath();
   }

   @Override
   public String getWorkspaceRoot() {
      return this.workspaceRoot;
   }

   @Override
   public void setup() throws DataHandlerException {

      this.location.put(Location.CACHE,
            String.format("%s%scache%s%s%s",
                  workspaceRoot, File.separator, File.separator, this.productType, File.separator));

      this.location.put(Location.CACHE_ROOT,
            String.format("%s%scache%s",
                  workspaceRoot, File.separator, File.separator));

      String tmp = String.format("%s%serror%s",
            workspaceRoot, File.separator, File.separator);
      this.location.put(Location.VALIDATION_ERROR,
            String.format("%svalidation%s%s",
                  tmp, File.separator, this.productType));
      this.location.put(Location.VALIDATION_ERROR_ROOT,
            String.format("%svalidation%s",
                  tmp, File.separator));

      this.location.put(Location.SUBMISSION_ERROR,
            String.format("%ssubmission%s%s",
                  tmp, File.separator, this.productType));
      this.location.put(Location.SUBMISSION_ERROR_ROOT,
            String.format("%ssubmission%s",
                  tmp, File.separator));

      tmp = String.format("%s%sstaging%s",
            workspaceRoot, File.separator, File.separator);
      this.location.put(Location.PENDING_STAGE,
            String.format("%spending%s%s",
                  tmp, File.separator, this.productType));
      this.location.put(Location.PENDING_STAGE_ROOT,
            String.format("%spending%s",
                  tmp, File.separator));

      this.location.put(Location.SUBMITTED_STAGE,
            String.format("%ssubmitted%s%s",
                  tmp, File.separator, this.productType));
      this.location.put(Location.SUBMITTED_STAGE_ROOT,
            String.format("%ssubmitted%s",
                  tmp, File.separator));

      this.location.put(Location.DATA_STAGE,
            String.format("%sdata%s%s",
                  tmp, File.separator, this.productType));
      this.location.put(Location.DATA_STAGE_ROOT,
            String.format("%sdata%s",
                  tmp, File.separator));

      for (String dir : this.location.values()) {
         File d = new File(dir);
         if (!d.exists()) {
            try {
               logger.debug("Creating workspace directory: " + dir);
               if (!d.mkdirs()) {
                  throw new DataHandlerException("Unable to create directory: " + dir);
               }
            } catch (SecurityException e) {
               throw new DataHandlerException("Unable to create directory: " + dir, e);
            }
         }
      }
   }


   @Override
   public void work() throws DataHandlerException {
      logger.debug("work");
   }

   @Override
   public synchronized void cleanup() throws DataHandlerException {
      for (String dir : this.location.values()) {
         File d = new File(dir);
         if (d.exists() && d.isDirectory() && (d.list().length == 0)) {
            logger.debug("Deleting workspace directory: " + dir);
            d.delete();
         }
      }
   }

   @Override
   public void setProductType(String name) {
      this.productType = name;
   }

   @Override
   public String getProductType() {
      return this.productType;
   }

   @Override
   public String getLocation(Location location) {
      return this.location.get(location);
   }

}
