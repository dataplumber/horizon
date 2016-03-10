/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.BoundingRectangle;
import gov.nasa.horizon.common.api.serviceprofile.SPBoundingRectangle;

/**
 * Implementation of BoundingRectangle object using JAXB for XML marshalling and
 * unmarshalling.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: BoundingRectangleJaxb.java 1234 2008-05-30 04:45:50Z thuang $
 */
public class BoundingRectangleJaxb extends AccessorBase implements
      SPBoundingRectangle {

   private BoundingRectangle _jaxbObj;

   public BoundingRectangleJaxb() {
      this._jaxbObj = new BoundingRectangle();
   }

   public BoundingRectangleJaxb(SPBoundingRectangle boundingRectangle) {
      if (boundingRectangle.getImplObj() instanceof BoundingRectangle) {
         this._jaxbObj = (BoundingRectangle) boundingRectangle.getImplObj();
      } else {
         this._jaxbObj = new BoundingRectangle();
         this._setValues(boundingRectangle.getEastLongitude(),
               boundingRectangle.getNorthLatitude(), boundingRectangle
               .getSouthLatitude(), boundingRectangle.getWestLongitude());
      }
   }

   public BoundingRectangleJaxb(BoundingRectangle jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public BoundingRectangleJaxb(double east, double north, double south,
                                double west) {
      this._jaxbObj = new BoundingRectangle();
      this._setValues(east, north, south, west);
   }

   private void _setValues(double east, double north, double south, double west) {
      this.setEastLongitude(east);
      this.setWestLongitude(west);
      this.setNorthLatitude(north);
      this.setSouthLatitude(south);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final BoundingRectangleJaxb other = (BoundingRectangleJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized Double getEastLongitude() {
      return this._jaxbObj.getEastLongitude();
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized Double getNorthLatitude() {
      return this._jaxbObj.getNorthLatitude();
   }

   public synchronized String getRegionName() {
      return this._jaxbObj.getRegion();
   }

   public synchronized Double getSouthLatitude() {
      return this._jaxbObj.getSouthLatitude();
   }

   public synchronized Double getWestLongitude() {
      return this._jaxbObj.getWestLongitude();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setEastLongitude(double east) {
      this._jaxbObj.setEastLongitude(east);
   }

   public synchronized void setNorthLatitude(double north) {
      this._jaxbObj.setNorthLatitude(north);
   }

   public synchronized void setRegionName(String name) {
      this._jaxbObj.setRegion(name);
   }

   public synchronized void setSouthLatitude(double south) {
      this._jaxbObj.setSouthLatitude(south);
   }

   public synchronized void setWestLongitude(double west) {
      this._jaxbObj.setWestLongitude(west);
   }
}
