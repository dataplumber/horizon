/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Ellipse;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Point;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Radius;
import gov.nasa.horizon.common.api.serviceprofile.SPEllipse;
import gov.nasa.horizon.common.api.serviceprofile.SPPoint;

/**
 * Implementation of Ellipse object using JAXB for XML marshalling and
 * unmarshalling.
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: EllipseJaxb.java 4425 2009-12-24 00:38:36Z thuang $
 */
public class EllipseJaxb extends AccessorBase implements SPEllipse {

   private Ellipse _jaxbObj = null;

   public EllipseJaxb() {
      this._jaxbObj = new Ellipse();
   }

   public EllipseJaxb(double centerLatitude, double centerLongitude,
         double radius, String radiusUnit) {
      this._jaxbObj = new Ellipse();
      this.setCenterPoint(new PointJaxb(centerLatitude, centerLongitude));
      this.setUnit(radiusUnit);
      this.setValue(radius);
   }

   public EllipseJaxb(SPEllipse ellipse) {
      if (ellipse.getImplObj() instanceof Ellipse) {
         this._jaxbObj = (Ellipse) ellipse.getImplObj();
      } else {
         this._jaxbObj = new Ellipse();
         this.setCenterPoint(ellipse.getCenterPoint());
         this.setUnit(ellipse.getUnit());
         this.setValue(ellipse.getValue());
      }
   }

   public EllipseJaxb(Ellipse jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final EllipseJaxb other = (EllipseJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized SPPoint getCenterPoint() {
      return new PointJaxb(this._jaxbObj.getCenter());
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized String getUnit() {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new Radius());
		}
      return this._jaxbObj.getRadius().getUnit();
   }

   public synchronized Double getValue() {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new Radius());
		}
      return this._jaxbObj.getRadius().getValue();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setCenterPoint(SPPoint centerPoint) {
      Point pt = new Point();
      pt.setLatitude(centerPoint.getLatitude());
      pt.setLongitude(centerPoint.getLongitude());
      this._jaxbObj.setCenter(pt);
   }

   public synchronized void setUnit(String unit) {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new Radius());
		}
      this._jaxbObj.getRadius().setUnit(unit);
   }

   public synchronized void setValue(double value) {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new Radius());
		}
      this._jaxbObj.getRadius().setValue(value);
   }
}
