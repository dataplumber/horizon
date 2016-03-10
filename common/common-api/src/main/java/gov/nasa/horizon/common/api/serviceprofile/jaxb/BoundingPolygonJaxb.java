/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.BoundingPolygon;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Point;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.PointOrder;
import gov.nasa.horizon.common.api.serviceprofile.SPBoundingPolygon;
import gov.nasa.horizon.common.api.serviceprofile.SPPoint;
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of BoundingPolygon object using JAXB for XML marshalling and
 * unmarshalling.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: BoundingPolygonJaxb.java 1234 2008-05-30 04:45:50Z thuang $
 */
public class BoundingPolygonJaxb extends AccessorBase implements
      SPBoundingPolygon {

   private BoundingPolygon _jaxbObj;

   public BoundingPolygonJaxb() {
      this._jaxbObj = new BoundingPolygon();
   }

   public BoundingPolygonJaxb(SPBoundingPolygon boundingPolygon) {
      this._jaxbObj = new BoundingPolygon();
      this._setValues(boundingPolygon.getRegionName(), boundingPolygon
            .getPoints(), boundingPolygon.getPointOrder());
   }

   public BoundingPolygonJaxb(BoundingPolygon jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public BoundingPolygonJaxb(String region, List<SPPoint> points) {
      this._setValues(region, points, SPPointOrder.CLOCKWISE);
   }

   public BoundingPolygonJaxb(String region, List<SPPoint> points,
                              SPPointOrder order) throws ServiceProfileException {
      this._jaxbObj = new BoundingPolygon();
      this._setValues(region, points, order);
   }

   private synchronized void _setValues(String region, List<SPPoint> points,
                                        SPPointOrder order) {
      this.setRegionName(region);
      this.setPoints(points);
      this.setPointOrder(order);
   }

   public void addPoint(SPPoint point) {
      SPPoint newPoint = point;
      if (!(newPoint.getImplObj() instanceof Point)) {
         newPoint = new PointJaxb(point);
      }
      List<Point> points = this._jaxbObj.getPoint();
      points.add((Point) newPoint.getImplObj());
      point = newPoint;
   }

   public SPPoint createPoint(double latitude, double longitude) {
      return new PointJaxb(latitude, longitude);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final BoundingPolygonJaxb other = (BoundingPolygonJaxb) obj;
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

   public synchronized SPPointOrder getPointOrder() {
      return SPPointOrder.valueOf(this._jaxbObj.getOrder().toString());
   }

   public synchronized List<SPPoint> getPoints() {
      List<SPPoint> result = new LinkedList<SPPoint>();

      List<Point> points = this._jaxbObj.getPoint();
      if (points != null) {
         for (Point point : points) {
            result.add(new PointJaxb(point));
         }
      }
      return result;
   }

   public synchronized String getRegionName() {
      return this._jaxbObj.getRegion();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setPointOrder(SPPointOrder
                                                pointOrder) {
      this._jaxbObj.setOrder(PointOrder.valueOf(pointOrder.toString()));
   }

   public synchronized void setPoints(List<SPPoint> points) {
      List<Point> pointList = this._jaxbObj.getPoint();
      pointList.clear();
      for (SPPoint point : points) {
         Point pt = new Point();
         pt.setLatitude(point.getLatitude());
         pt.setLongitude(point.getLongitude());
         pointList.add(pt);
      }
   }

   public synchronized void setRegionName(String name) {
      this._jaxbObj.setRegion(name);

   }

}
