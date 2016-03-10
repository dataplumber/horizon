/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Component Configurator uses SpringFramework to load component factories
 *
 * @author T. Huang
 * @version $Id: $
 */
public class ComponentConfigurator implements Worker {
   protected static Log logger = LogFactory.getLog(ComponentConfigurator.class);

   private AbstractApplicationContext ctx;

   public void setup() throws DataHandlerException {
      String springConfig = System.getProperty("dh.spring.config");
      logger.debug("Load spring config: " + springConfig);
      try {
         if (springConfig != null) {
            ctx = new FileSystemXmlApplicationContext(springConfig);

         } else {
            springConfig = "dh_components.xml";
            ctx = new ClassPathXmlApplicationContext(springConfig);
         }
      } catch (BeansException ex) {
         ex.printStackTrace();
         throw new DataHandlerException("Unable to load spring " +
               "configuration file: " + springConfig, ex);
      }
   }

   @Override
   public void work() throws DataHandlerException {
      logger.debug(this.getClass() + "work method.");
   }

   FileHandlerFactory getFileHandlerFactory() throws DataHandlerException {
      String name = "dh.fileHandler.factory";
      Object obj = this.ctx.getBean(name);
      if (obj == null) {
         throw new DataHandlerException("Missing handler component: " + name);
      }
      return (FileHandlerFactory) obj;
   }

   MetadataHarvesterFactory getMetadataHarvesterFactory() throws DataHandlerException {
      String name = "dh.metadata.factory";
      Object obj = this.ctx.getBean(name);
      if (obj == null) {
         throw new DataHandlerException("Missing handler component: " + name);
      }
      return (MetadataHarvesterFactory) obj;
   }

   ProductTypeFactory getProductTypeFactory() throws DataHandlerException {
      String name = "dh.productType.factory";
      Object obj = this.ctx.getBean(name);
      if (obj == null) {
         throw new DataHandlerException("Missing handler component: " + name);
      }
      return (ProductTypeFactory) obj;
   }

   WorkspaceFactory getWorkspaceFactory() throws DataHandlerException {
      String name = "dh.workspace.factory";
      Object obj = this.ctx.getBean(name);
      if (obj == null) {
         throw new DataHandlerException("Missing handler component: " + name);
      }
      return (WorkspaceFactory) obj;
   }

   @Override
   public void cleanup() throws DataHandlerException {

   }
}
