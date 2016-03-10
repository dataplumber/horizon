/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

/**
 * Worker object interface.  This is the required interface for DataHandler
 * workers
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface Worker {

   void setup() throws DataHandlerException;

   void work() throws DataHandlerException;

   void cleanup() throws DataHandlerException;

}
