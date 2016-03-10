/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import gov.nasa.horizon.common.api.file.FileProduct;

import java.util.List;

/**
 * Interface for file handlers.  This is the interface used product type to
 * process newly discovered product files
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface FileHandler {

   void preprocess();

   void process(List<FileProduct> files) throws DataHandlerException;

   void postprocess();

   void onError(Throwable throwable);

}
