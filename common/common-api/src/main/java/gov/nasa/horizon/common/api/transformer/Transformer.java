/*****************************************************************************
 * Copyright (c) 2007-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.transformer;

import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile;
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

/**
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public interface Transformer {

   ServiceProfile transfrom(Properties sources) throws ServiceProfileException;

   ServiceProfile transform(String contents) throws ServiceProfileException;

   ServiceProfile transform(URI uri) throws IOException,
         ServiceProfileException;

}
