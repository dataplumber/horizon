/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import java.util.List;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: BoundingPolygon.java 6179 2010-11-09 20:36:28Z thuang $
 */
public interface SPBoundingPolygon extends SPBoundingRegion {
   enum SPPointOrder {
      CLOCKWISE, COUNTERCLOCKWISE
   }

   /**
    * Method to add endpoint for the ploygon.
    * 
    * @param point the endpoint
    */
   void addPoint(SPPoint point);

   /**
    * Method to create an endpoint
    * 
    * @param latitude the latitude value
    * @param longitude the longitude value
    * @return a SPPoint object
    */
   SPPoint createPoint(double latitude, double longitude);

   /**
    * Method to return the ordering of the points
    * 
    * @return the ordering {e.g. CLOCKWISE, COUNTERCLOCKWISE}
    */
   SPPointOrder getPointOrder();

   /**
    * Method to return the collection of endpoints
    * 
    * @return the endpoints
    */
   List<SPPoint> getPoints();

   /**
    * Method to set the ordering of the points
    * 
    * @param pointOrder the point ordering
    */
   void setPointOrder(SPPointOrder pointOrder);

   /**
    * Method to set the endpoints of the polygon
    * 
    * @param points the endpoints
    */
   void setPoints(List<SPPoint> points);
}
