/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Swath;
import gov.nasa.horizon.common.api.serviceprofile.SPSwath;

import java.math.BigInteger;
import java.util.Date;

public class SwathJaxb extends AccessorBase implements SPSwath {

   private Swath _jaxbObj;

   public SwathJaxb() {
      this._jaxbObj = new Swath();
   }

   public SwathJaxb(double inclinationAngle, double width) {
      this._jaxbObj = new Swath();
      this._setValues(inclinationAngle, width);
   }

   public SwathJaxb(double inclinationAngle, double width,
         double equatorCrossingLongitude, long equatorCrossingTime) {
      this._setValues(inclinationAngle, width, equatorCrossingLongitude,
            equatorCrossingTime);
   }

   public SwathJaxb(SPSwath swath) {
      if (swath.getImplObj() instanceof Swath) {
         this._jaxbObj = (Swath) swath.getImplObj();
      } else {
         this._jaxbObj = new Swath();
         this._setValues(swath.getInclinationAngle(), swath.getSwathWidth());
         if (swath.getEquatorCrossingLongitude() != null)
            this.setEquatorCrossingLongitude(swath
                  .getEquatorCrossingLongitude());
         if (swath.getEquatorCrossingTime() != null)
            this.setEquatorCrossingTime(swath.getEquatorCrossingTime());
      }
   }

   public SwathJaxb(Swath jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   private synchronized void _setValues(double inclinationAngle, double width) {
      this.setInclinationAngle(inclinationAngle);
      this.setSwathWidth(width);
   }

   private synchronized void _setValues(double inclinationAngle, double width,
         double equatorCrossingLongitude, long equatorCrossingTime) {
      this._setValues(inclinationAngle, width);
      this.setEquatorCrossingLongitude(equatorCrossingLongitude);
      this.setEquatorCrossingTime(new Date(equatorCrossingTime));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final SwathJaxb other = (SwathJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized Double getEquatorCrossingLongitude() {
      return this._jaxbObj.getEquatorCrossingLongitude();
   }

   public synchronized Date getEquatorCrossingTime() {
      if (this._jaxbObj.getEquatorCrossingTime() == null)
         return null;

      return new Date(this._jaxbObj.getEquatorCrossingTime().longValue());
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized Double getInclinationAngle() {
      return this._jaxbObj.getInclinationAngle();
   }

   public synchronized Double getSwathWidth() {
      return this._jaxbObj.getSwathWidth();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setEquatorCrossingLongitude(
         double equatorCorssingLongitude) {
      this._jaxbObj.setEquatorCrossingLongitude(equatorCorssingLongitude);

   }

   public synchronized void setEquatorCrossingTime(Date date) {

      this._jaxbObj.setEquatorCrossingTime(BigInteger.valueOf(date.getTime()));
   }

   public void setEquatorCrossingTime(long date) {
      this._jaxbObj.setEquatorCrossingTime(BigInteger.valueOf(date));
   }

   public synchronized void setInclinationAngle(double inclinationAngle) {
      this._jaxbObj.setInclinationAngle(inclinationAngle);
   }

   public synchronized void setSwathWidth(double swathWidth) {
      this._jaxbObj.setSwathWidth(swathWidth);
   }

}
