/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

/**
 * Factory interface to create new metadata harvester instance
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface MetadataHarvesterFactory {

   MetadataHarvester createMetadataHarvester();

}
