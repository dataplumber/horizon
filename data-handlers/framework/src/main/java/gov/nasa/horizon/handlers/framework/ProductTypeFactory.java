/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import java.util.Map;

/**
 * Factory interface to create Product Type objects
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface ProductTypeFactory {
   ProductType createProductType(String name);

   Map<String, ProductType> createProductTypes(ApplicationConfigurator configurator, 
      String productTypeConfig);
}
