/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

/**
 * Interface to the bounding rectangle.
 * 
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: BoundingRectangle.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface SPBoundingRectangle extends SPBoundingRegion {

   /**
    * Method to return the east longitude value
    * 
    * @return the east longitude value
    */
   Double getEastLongitude();

   /**
    * Method to return the north latitude value
    * 
    * @return the north latitude
    */
   Double getNorthLatitude();

   /**
    * Method to return the south latitude
    * 
    * @return the south latitude
    */
   Double getSouthLatitude();

   /**
    * Method to return the west longitude
    * 
    * @return the west longitude value
    */
   Double getWestLongitude();

   /**
    * Method to set the east longitude
    * 
    * @param east the east longitude value
    */
   void setEastLongitude(double east);

   /**
    * Method to return the north latitude
    * 
    * @param north the north latitude value
    */
   void setNorthLatitude(double north);

   /**
    * Method to return the south latitude
    * 
    * @param south the south latitude value
    */
   void setSouthLatitude(double south);

   /**
    * Method to return the west longitude
    * 
    * @param west tje west longitude value
    */
   void setWestLongitude(double west);
}
