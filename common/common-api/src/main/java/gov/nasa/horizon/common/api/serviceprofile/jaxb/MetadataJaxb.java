/*****************************************************************************
 * Copyright (c) 2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;


import gov.nasa.horizon.common.api.jaxb.serviceprofile.*;
import gov.nasa.horizon.common.api.serviceprofile.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class MetadataJaxb extends AccessorBase implements SPMetadata {

   private Metadata _jaxbObj;

   public MetadataJaxb() {
      this._jaxbObj = new Metadata();
   }

   public MetadataJaxb(Metadata jaxb) {
      this._jaxbObj = jaxb;
   }

   public MetadataJaxb(SPMetadata metadata) {
      this._jaxbObj = new Metadata();
      this.setAncillaryName(metadata.getAncillaryName());
      this.setBatch(metadata.getBatch());
      this.setComment(metadata.getComment());
      this.setCycle(metadata.getCycle());
      this.setDayNightMode(metadata.getDayNightMode());
      this.setDayOfYearStart(metadata.getDayOfYearStart());
      Properties extras = metadata.getExtras();
      for (String key : extras.stringPropertyNames()) {
         this.addExtra(key, extras.getProperty(key));
      }
      SPProductHistory history = metadata.getProductHistory();
      if (history != null) {
         this.setProductHistory(history);
      }
      this.setLatitudeStep(metadata.getLatitudeStep());
      this.setLatitudeUnits(metadata.getLatitudeUnits());
      this.setLongitudeStep(metadata.getLongitudeStep());
      this.setLongitudeUnits(metadata.getLongitudeUnits());
      this.setNumberOfColumns(metadata.getNumberOfColumns());
      this.setNumberOfLines(metadata.getNumberOfLines());
      this.setObservationMode(metadata.getObservationMode());
      this.setPass(metadata.getPass());
      this.setPassType(metadata.getPassType());
      this.setRevolution(metadata.getRevolution());

      List<SPPoint> points = metadata.getPoints();
      for (SPPoint point : points) {
         this.addPoint(point);
      }

      List<SPLine> lines = metadata.getLines();
      for (SPLine line : lines) {
         this.addLine(line);
      }

      List<SPBoundingRectangle> rectangles = metadata.getBoundingRectangles();
      for (SPBoundingRectangle rectangle : rectangles) {
         this.addBoundingRectangle(rectangle);
      }

      List<SPBoundingPolygon> polygons = metadata.getBoundingPolygons();
      for (SPBoundingPolygon polygon : polygons) {
         this.addBoundingPolygon(polygon);
      }

      List<SPSwath> swaths = metadata.getSwaths();
      for (SPSwath swath : swaths) {
         this.addSwath(swath);
      }

      List<SPEllipse> ellipses = metadata.getEllipses();
      for (SPEllipse ellipse : ellipses) {
         this.addEllipse(ellipse);
      }

      this.setProductStartTime(metadata.getProductStartTime().getTime());
      this.setProductStopTime(metadata.getProductStopTime().getTime());
      
      //Do I need to set this here?
      //this.setDataDays(metadata.getDataDays());
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final MetadataJaxb other = (MetadataJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   @Override
   public String getAncillaryName() {
      return this._jaxbObj.getAncillaryName();
   }

   @Override
   public void setAncillaryName(String name) {
      this._jaxbObj.setAncillaryName(name);
   }

   @Override
   public String getBatch() {
      return this._jaxbObj.getBatch();
   }

   @Override
   public void setBatch(String batch) {
      this._jaxbObj.setBatch(batch);
   }

   @Override
   public String getComment() {
      return this._jaxbObj.getComment();
   }

   @Override
   public void setComment(String comment) {
      this._jaxbObj.setComment(comment);
   }

   @Override
   public Integer getCycle() {
      if (this._jaxbObj.getCycle() != null)
         return this._jaxbObj.getCycle().intValue();
      return null;
   }

   @Override
   public void setCycle(Integer cycle) {
      this._jaxbObj.setCycle(BigInteger.valueOf(cycle));
   }

   @Override
   public SPCommon.SPDayNight getDayNightMode() {
      if (this._jaxbObj.getDayNightMode() != null) {
         return SPCommon.SPDayNight.valueOf(this._jaxbObj.getDayNightMode()
               .toString());
      }
      return null;
   }

   @Override
   public void setDayNightMode(SPCommon.SPDayNight dayNight) {
      this._jaxbObj.setDayNightMode(DayNight.fromValue(dayNight.toString()));
   }

   @Override
   public Integer getDayOfYearStart() {
      if (this._jaxbObj.getDayOfYearStart() != null) {
         return this._jaxbObj.getDayOfYearStart().intValue();
      }
      return null;
   }

   @Override
   public void setDayOfYearStart(Integer dayOfYearStart) {
      this._jaxbObj.setDayOfYearStart(BigInteger.valueOf(dayOfYearStart));
   }

   protected List<DomainAttribute.Extra> _getExtras() {
      if (this._jaxbObj.getExtras() == null) {
         this._jaxbObj.setExtras(new DomainAttribute());
      }
      return this._jaxbObj.getExtras().getExtra();
   }

   @Override
   public void addExtra(String name, String value) {
      List<DomainAttribute.Extra> extras = this._getExtras();
      DomainAttribute.Extra extra = new DomainAttribute.Extra();
      extra.setName(name);
      extra.setValue(value);
      extras.add(extra);
   }

   @Override
   public void clearExtras() {
      this._getExtras().clear();
   }

   @Override
   public Properties getExtras() {
      Properties result = new Properties();
      List<DomainAttribute.Extra> extras = this._getExtras();
      for (DomainAttribute.Extra extra : extras) {
         result.setProperty(extra.getName(), extra.getValue());
      }
      return result;
   }

   @Override
   public SPProductHistory createProductHistory() {
      return new ProductHistoryJaxb();
   }

   @Override
   public SPProductHistory getProductHistory() {
      SPProductHistory result = null;
      if (this._jaxbObj.getHistory() != null) {
         result = new ProductHistoryJaxb(this._jaxbObj.getHistory());
      }
      return result;
   }

   @Override
   public void setProductHistory(SPProductHistory history) {
      SPProductHistory ph = history;
      if (!(ph.getImplObj() instanceof ProductHistory)) {
         ph = new ProductHistoryJaxb(ph);
      }
      this._jaxbObj.setHistory((ProductHistory) ph.getImplObj());
   }

   @Override
   public Double getLatitudeStep() {
      return this._jaxbObj.getLatitudeStep();
   }

   @Override
   public void setLatitudeStep(Double latitudeStep) {
      this._jaxbObj.setLatitudeStep(latitudeStep);
   }

   @Override
   public String getLatitudeUnits() {
      return this._jaxbObj.getLatitudeUnits();
   }

   @Override
   public void setLatitudeUnits(String latitudeUnits) {
      this._jaxbObj.setLatitudeUnits(latitudeUnits);
   }

   @Override
   public Double getLongitudeStep() {
      return this._jaxbObj.getLongitudeStep();
   }

   @Override
   public void setLongitudeStep(Double longitudeStep) {
      this._jaxbObj.setLongitudeStep(longitudeStep);
   }

   @Override
   public String getLongitudeUnits() {
      return this._jaxbObj.getLongitudeUnits();
   }

   @Override
   public void setLongitudeUnits(String longitudeUnits) {
      this._jaxbObj.setLongitudeUnits(longitudeUnits);
   }

   @Override
   public Integer getNumberOfColumns() {
      if (this._jaxbObj.getNumberOfColumns() != null)
         return this._jaxbObj.getNumberOfColumns().intValue();
      return null;
   }

   @Override
   public void setNumberOfColumns(Integer numberOfColumns) {
      this._jaxbObj.setNumberOfColumns(BigInteger.valueOf(numberOfColumns));
   }

   @Override
   public Integer getNumberOfLines() {
      if (this._jaxbObj.getNumberOfLines() != null)
         return this._jaxbObj.getNumberOfLines().intValue();
      return null;
   }

   @Override
   public void setNumberOfLines(Integer numberOfLines) {
      this._jaxbObj.setNumberOfLines(BigInteger.valueOf(numberOfLines));
   }

   @Override
   public String getObservationMode() {
      return this._jaxbObj.getObservationMode();
   }

   @Override
   public void setObservationMode(String observationMode) {
      this._jaxbObj.setObservationMode(observationMode);
   }

   @Override
   public Integer getPass() {
      if (this._jaxbObj.getPass() != null)
         return this._jaxbObj.getPass().intValue();
      return null;
   }

   @Override
   public void setPass(Integer pass) {
      this._jaxbObj.setPass(BigInteger.valueOf(pass));
   }

   @Override
   public SPCommon.SPDataPass getPassType() {
      if (this._jaxbObj.getPassType() != null)
         return SPCommon.SPDataPass.valueOf(this._jaxbObj.getPassType()
               .toString());
      return null;
   }

   @Override
   public void setPassType(SPCommon.SPDataPass pass) {
      this._jaxbObj.setPassType(DataPass.valueOf(pass.toString()));
   }

   @Override
   public Integer getRevolution() {
      if (this._jaxbObj.getRevolution() != null)
         return this._jaxbObj.getRevolution().intValue();
      return null;
   }

   @Override
   public void setRevolution(Integer revolution) {
      this._jaxbObj.setRevolution(BigInteger.valueOf(revolution));
   }

   @Override
   public SPPoint createPoint() {
      return new PointJaxb();
   }

   protected List<Point> _getPoints() {
      if (this._jaxbObj.getSpatialCoverage() == null) {
         this._jaxbObj.setSpatialCoverage(new SpatialDomain());
      }
      SpatialDomain sd = this._jaxbObj.getSpatialCoverage();
      if (sd.getPoints() == null) {
         sd.setPoints(new SpatialDomain.Points());
      }
      return sd.getPoints().getPoint();
   }

   @Override
   public void clearPoints() {
      this._getPoints().clear();
   }

   @Override
   public void addPoint(SPPoint point) {
      SPPoint p = point;
      if (!(p.getImplObj() instanceof Point)) {
         p = new PointJaxb(p);
      }
      this._getPoints().add((Point) p.getImplObj());
   }

   @Override
   public List<SPPoint> getPoints() {
      List<SPPoint> result = new Vector<SPPoint>();
      for (Point point : this._getPoints()) {
         result.add(new PointJaxb(point));
      }
      return result;
   }

   @Override
   public SPLine createLine() {
      return new LineJaxb();
   }

   protected List<Line> _getLine() {
      if (this._jaxbObj.getSpatialCoverage() == null) {
         this._jaxbObj.setSpatialCoverage(new SpatialDomain());
      }
      SpatialDomain sd = this._jaxbObj.getSpatialCoverage();
      if (sd.getLines() == null) {
         sd.setLines(new SpatialDomain.Lines());
      }
      return sd.getLines().getLine();
   }

   @Override
   public void clearLines() {
      this._getLine().clear();
   }

   @Override
   public void addLine(SPLine line) {
      SPLine l = line;
      if (!(l.getImplObj() instanceof Line)) {
         l = new LineJaxb(l);
      }
      this._getLine().add((Line) l.getImplObj());
   }

   @Override
   public List<SPLine> getLines() {
      List<SPLine> result = new Vector<SPLine>();
      for (Line line : this._getLine()) {
         result.add(new LineJaxb(line));
      }
      return result;
   }

   @Override
   public SPBoundingRectangle createBoundingRectangle() {
      return new BoundingRectangleJaxb();
   }

   protected List<BoundingRectangle> _getBoundingRectangles() {
      if (this._jaxbObj.getSpatialCoverage() == null) {
         this._jaxbObj.setSpatialCoverage(new SpatialDomain());
      }
      SpatialDomain sd = this._jaxbObj.getSpatialCoverage();
      if (sd.getRectangles() == null) {
         sd.setRectangles(new SpatialDomain.Rectangles());
      }
      return sd.getRectangles().getRectangle();
   }

   @Override
   public void clearBoundingRectangles() {
      this._getBoundingRectangles().clear();
   }

   @Override
   public void addBoundingRectangle(SPBoundingRectangle boundingRectangle) {
      SPBoundingRectangle br = boundingRectangle;
      if (!(br.getImplObj() instanceof BoundingRectangle)) {
         br = new BoundingRectangleJaxb(br);
      }
      this._getBoundingRectangles().add((BoundingRectangle) br.getImplObj());
   }

   @Override
   public List<SPBoundingRectangle> getBoundingRectangles() {
      List<SPBoundingRectangle> result = new Vector<SPBoundingRectangle>();
      for (BoundingRectangle rectangle : this._getBoundingRectangles()) {
         result.add(new BoundingRectangleJaxb(rectangle));
      }
      return result;
   }

   @Override
   public SPBoundingPolygon createBoundingPolygon() {
      return new BoundingPolygonJaxb();
   }

   protected List<BoundingPolygon> _getBoundingPolygons() {
      if (this._jaxbObj.getSpatialCoverage() == null) {
         this._jaxbObj.setSpatialCoverage(new SpatialDomain());
      }
      SpatialDomain sd = this._jaxbObj.getSpatialCoverage();
      if (sd.getPolygons() == null) {
         sd.setPolygons(new SpatialDomain.Polygons());
      }
      return sd.getPolygons().getPolygon();
   }

   @Override
   public void clearBoundingPolygons() {
      this._getBoundingPolygons().clear();
   }

   @Override
   public void addBoundingPolygon(SPBoundingPolygon boundingPolygon) {
      SPBoundingPolygon bp = boundingPolygon;
      if (!(bp.getImplObj() instanceof BoundingPolygon)) {
         bp = new BoundingPolygonJaxb(bp);
      }
      this._getBoundingPolygons().add((BoundingPolygon) bp.getImplObj());
   }

   @Override
   public List<SPBoundingPolygon> getBoundingPolygons() {
      List<SPBoundingPolygon> result = new Vector<SPBoundingPolygon>();
      for (BoundingPolygon polygon : this._getBoundingPolygons()) {
         result.add(new BoundingPolygonJaxb(polygon));
      }
      return result;
   }

   @Override
   public SPSwath createSwath() {
      return new SwathJaxb();
   }

   protected List<Swath> _getSwaths() {
      if (this._jaxbObj.getSpatialCoverage() == null) {
         this._jaxbObj.setSpatialCoverage(new SpatialDomain());
      }
      SpatialDomain sd = this._jaxbObj.getSpatialCoverage();
      if (sd.getSwaths() == null) {
         sd.setSwaths(new SpatialDomain.Swaths());
      }
      return sd.getSwaths().getSwath();
   }

   @Override
   public void clearSwaths() {
      this._getSwaths().clear();
   }

   @Override
   public void addSwath(SPSwath swath) {
      SPSwath s = swath;
      if (!(s.getImplObj() instanceof Swath)) {
         s = new SwathJaxb(s);
      }
      this._getSwaths().add((Swath) s.getImplObj());
   }

   @Override
   public List<SPSwath> getSwaths() {
      List<SPSwath> result = new Vector<SPSwath>();
      for (Swath swath : this._getSwaths()) {
         result.add(new SwathJaxb(swath));
      }
      return result;
   }

   @Override
   public SPEllipse createEllipse() {
      return new EllipseJaxb();
   }

   protected List<Ellipse> _getEllipse() {
      if (this._jaxbObj.getSpatialCoverage() == null) {
         this._jaxbObj.setSpatialCoverage(new SpatialDomain());
      }
      SpatialDomain sd = this._jaxbObj.getSpatialCoverage();
      if (sd.getEllipses() == null) {
         sd.setEllipses(new SpatialDomain.Ellipses());
      }
      return sd.getEllipses().getEllipse();
   }

   @Override
   public void clearEllipses() {
      this._getEllipse().clear();
   }

   @Override
   public void addEllipse(SPEllipse ellipse) {
      SPEllipse e = ellipse;
      if (!(e.getImplObj() instanceof Ellipse)) {
         e = new EllipseJaxb(e);
      }
      this._getEllipse().add((Ellipse) e.getImplObj());
   }

   @Override
   public List<SPEllipse> getEllipses() {
      List<SPEllipse> result = new Vector<SPEllipse>();
      for (Ellipse ellipse : this._getEllipse()) {
         result.add(new EllipseJaxb(ellipse));
      }
      return result;
   }

   protected TimeStamp _getTimeStamp() {
      if (this._jaxbObj.getTemporalCoverage() == null) {
         this._jaxbObj.setTemporalCoverage(new TimeStamp());
      }
      return this._jaxbObj.getTemporalCoverage();
   }

   @Override
   public Date getProductStartTime() {
      Date result = null;
      TimeStamp timeStamp = this._getTimeStamp();
      if (timeStamp.getStart() != null) {
         result = new Date(timeStamp.getStart().longValue());
      }
      return result;
   }

   @Override
   public void setProductStartTime(long startTime) {
      this._getTimeStamp().setStart(BigInteger.valueOf(startTime));
   }

   @Override
   public Date getProductStopTime() {
      Date result = null;
      TimeStamp timeStamp = this._getTimeStamp();
      if (timeStamp.getStop() != null) {
         result = new Date(timeStamp.getStop().longValue());
      }
      return result;
   }

   @Override
   public void setProductStopTime(long stopTime) {
      this._getTimeStamp().setStop(BigInteger.valueOf(stopTime));
   }  
   
   public List<Date> getDataDays() {
      List<Date> result = new ArrayList<Date>();

      if (this._jaxbObj.getDataDays() != null) {
         for (BigInteger dataDay : this._jaxbObj.getDataDays().getDataDay()) {
            result.add(new Date(dataDay.longValue()));
         }
      }
      return result;
   }
   
   protected List<BigInteger> _getDataDays() {
      if (this._jaxbObj.getDataDays() == null) {
         this._jaxbObj.setDataDays(new Metadata.DataDays());
      }
      return this._jaxbObj.getDataDays().getDataDay();
   }

   public synchronized void addDataDay(Long dataDay) {
      this._getDataDays().add(BigInteger.valueOf(dataDay));
   }
  
   public String getPartialId() {
	   return this._jaxbObj.getPartialId();
   }
   
   public void setPartialId(String value) {
	   this._jaxbObj.setPartialId(value);
   }
}
