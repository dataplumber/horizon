/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: BoundingRegion.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface SPBoundingRegion extends Accessor {

   /**
    * Method to return the name of the bounding region
    * 
    * @return the bounding region name
    */
   String getRegionName();

   /**
    * Method to set the name of the bounding region.
    * 
    * @param name the bounding region name
    */
   void setRegionName(String name);
}
