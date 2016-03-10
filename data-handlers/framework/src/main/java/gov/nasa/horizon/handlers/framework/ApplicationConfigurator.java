/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Application configurator interface.  This method handles processing of
 * user configuration possibly include command line parsing.
 *
 * @author T. Huang
 * @version $Id: $
 */
public abstract class ApplicationConfigurator {

   protected Map<String, ProductType> productTypes = new HashMap<String, ProductType>();
   protected String userProductType;
   protected Date userStart;
   protected Date userEnd;
   protected String user;
   protected String pass;
   protected String repo;
   private ComponentConfigurator componentConfigurator;
   protected boolean hasError;

   public ApplicationConfigurator() {

   }

   public void setup() throws DataHandlerException {
      this.componentConfigurator = new ComponentConfigurator();
      this.componentConfigurator.setup();
      this.configure();
   }

   public Map<String, ProductType> getProductTypes() {
      return this.productTypes;
   }

   public String getUserProductType() {
      return this.userProductType;
   }

   public Date getUserStart() {
      return this.userStart;
   }

   public Date getUserEnd() {
      return this.userEnd;
   }

   public String getUser() {
      return this.user;
   }

   public String getPass() {
      return this.pass;
   }

   public String getRepo() {
      return this.repo;
   }

   public FileHandlerFactory getFileHandlerFactory() throws DataHandlerException {
      return this.componentConfigurator.getFileHandlerFactory();
   }

   public MetadataHarvesterFactory getMetadataHarvesterFactory() throws DataHandlerException {
      return this.componentConfigurator.getMetadataHarvesterFactory();
   }

   public ProductTypeFactory getProductTypeFactory() throws DataHandlerException {
      return this.componentConfigurator.getProductTypeFactory();
   }

   public WorkspaceFactory getWorkspaceFactory() throws DataHandlerException {
      return this.componentConfigurator.getWorkspaceFactory();
   }

   public boolean hasError() {
      return this.hasError;
   }

   protected abstract boolean configure() throws DataHandlerException;

}
