/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import java.util.Date;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: Swath.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface SPSwath extends Accessor {
   Double getEquatorCrossingLongitude();

   Date getEquatorCrossingTime();

   Double getInclinationAngle();

   Double getSwathWidth();

   void setEquatorCrossingLongitude(double equatorCorssingLongitude);

   void setEquatorCrossingTime(Date date);

   void setEquatorCrossingTime(long date);

   void setInclinationAngle(double inclinationAngle);

   void setSwathWidth(double swathWidth);
}
