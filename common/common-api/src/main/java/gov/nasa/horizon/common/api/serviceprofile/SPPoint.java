/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: Point.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface SPPoint extends Accessor {
   Double getLatitude();

   Double getLongitude();

   void setLatitude(double latitude);

   void setLongitude(double longitude);
}
