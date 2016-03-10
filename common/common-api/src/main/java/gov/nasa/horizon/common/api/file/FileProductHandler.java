/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.file;

import java.util.Set;

/**
 * Define the required interface for classes to process file product found by
 * the crawler.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 *
 */
public interface FileProductHandler {

   /**
    * Hook method that will be called for each scan.
    */
   void preprocess();

   /**
    * Callback method invoked by the crawler when new files are found.
    *
    * @param fileProducts the list of files found.
    */
   void onProducts(Set<FileProduct> fileProducts);

   /**
    * Hook method that will be called for each scan.
    */
   void postprocess();

   /**
    * Hook method is called by the crawler is an exception was thrown
    *
    * @param e  the exception
    */
   void onError(Throwable e);
}
