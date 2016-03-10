/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile;
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException;

/**
 * @author T. Huang
 * @version $Id:$
 */
public interface MetadataHarvester {

   ServiceProfile createServiceProfile(Product product)
         throws ServiceProfileException;

}
