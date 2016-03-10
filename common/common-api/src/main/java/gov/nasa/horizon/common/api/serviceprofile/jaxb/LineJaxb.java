package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Line;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Point;
import gov.nasa.horizon.common.api.serviceprofile.SPLine;
import gov.nasa.horizon.common.api.serviceprofile.SPPoint;

import java.util.List;

public class LineJaxb extends AccessorBase implements SPLine {

   private Line _jaxbObj;

   public LineJaxb() {
      this._jaxbObj = new Line();
   }

   public LineJaxb(SPLine line) {
      this._jaxbObj = new Line();
      this.setEndPoints(line.getStartPoint(), line.getEndPoint());
   }

   public LineJaxb(Line jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public SPPoint createPoint(double latitude, double longitude) {
      return new PointJaxb(latitude, longitude);
   }

   public SPPoint getEndPoint() {
      SPPoint result = null;
      List<Point> points = this._jaxbObj.getPoint();
      if (points.size() == 2) {
         result = new PointJaxb(points.get(1));
      }
      return result;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public SPPoint getStartPoint() {
      SPPoint result = null;
      List<Point> points = this._jaxbObj.getPoint();
      if (points.size() == 2) {
         result = new PointJaxb(points.get(0));
      }
      return result;

   }

   public void setEndPoints(SPPoint startPoint, SPPoint endPoint) {
      List<Point> points = this._jaxbObj.getPoint();
      points.clear();

      SPPoint temp = new PointJaxb(startPoint);
      temp.setOwner(this);
      points.add((Point) temp.getImplObj());

      temp = new PointJaxb(endPoint);
      temp.setOwner(this);
      points.add((Point) temp.getImplObj());
   }
}
