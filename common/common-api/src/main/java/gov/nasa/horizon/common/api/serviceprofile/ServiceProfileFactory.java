/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import gov.nasa.horizon.common.api.serviceprofile.jaxb.ServiceProfileJaxb;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: ServiceProfileFactory.java 2668 2009-02-18 00:33:44Z axt $
 *
 */
public class ServiceProfileFactory {
   public static Log _logger = LogFactory.getLog(ServiceProfileFactory.class);

   private static ServiceProfileFactory _instance = new ServiceProfileFactory();

   /**
    * Gets an instance of ServiceProfileFactory object.
    *
    * @return ServiceProfileFactory object.
    */
   public static ServiceProfileFactory getInstance() {
      return ServiceProfileFactory._instance;
   }

   protected ServiceProfileFactory() {
   }

   protected <T extends Reader> ServiceProfile _load(T reader)
         throws ServiceProfileException {
      return ServiceProfileJaxb.loadServiceProfile(reader);
   }

   /**
    * Creates an empty ServiceProfile object to be filled by user application.
    *
    * @return a ServiceProfile object
    */
   public ServiceProfile createServiceProfile() throws ServiceProfileException {
      return ServiceProfileJaxb.createServiceProfile();
   }


   /**
    * Creates ServiceProfile object from new XML message.
    *
    * @param file File object representing XML message.
    * @return ServiceProfile object on success.
    * @throws ServiceProfileException If there is an error when creating
    *            ServiceProfile object.
    * @see #createServiceProfileFromMessage(String)
    */
   public ServiceProfile createServiceProfileFromMessage(File file)
         throws IOException, ServiceProfileException {
      ServiceProfileFactory._logger.trace("load xml file: " + file.getName());
      FileReader reader = new FileReader(file);
      ServiceProfile result = null;
      try {
         result = this._load(reader);
      } finally {
         reader.close();
      }
      return result;
   }

   /**
    * Creates ServiceProfile object from new XML message.
    *
    * @param contents XML document.
    * @return ServiceProfile object on success.
    * @throws ServiceProfileException If there is an error when creating
    *            ServiceProfile object.
    * @see #createServiceProfileFromMessage(File)
    */
   public ServiceProfile createServiceProfileFromMessage(String contents)
         throws ServiceProfileException {
      ServiceProfile result = null;
      StringReader reader = new StringReader(contents);
      try {
         result = this._load(reader);
      } finally {
         reader.close();
      }
      return result;
   }

}
