/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Point;
import gov.nasa.horizon.common.api.serviceprofile.SPPoint;

/**
 * Implementation of Point object using JAXB for XML marshalling and
 * unmarshalling.
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: PointJaxb.java 6133 2010-11-01 18:20:01Z qchau $
 */
public class PointJaxb extends AccessorBase implements SPPoint {

   private Point _jaxbObj;

   public static SPPoint createPoint() {
      return new PointJaxb();
   }

   public PointJaxb() {
      this._jaxbObj = new Point();
   }

   public PointJaxb(double latitude, double longitude) {
      this._jaxbObj = new Point();
      this.setLatitude(latitude);
      this.setLongitude(longitude);
   }

   public PointJaxb(SPPoint point) {
      this._jaxbObj = new Point();

      if (point == null)
         return;

      this.setLatitude(point.getLatitude());
      this.setLongitude(point.getLongitude());
   }

   public PointJaxb(Point jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (getClass() != obj.getClass())
         return false;
      final PointJaxb other = (PointJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized Double getLatitude() {
      return this._jaxbObj.getLatitude();
   }

   public synchronized Double getLongitude() {
      return this._jaxbObj.getLongitude();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setLatitude(double latitude) {
      this._jaxbObj.setLatitude(latitude);

   }

   public synchronized void setLongitude(double longitude) {
      this._jaxbObj.setLongitude(longitude);
   }

}
