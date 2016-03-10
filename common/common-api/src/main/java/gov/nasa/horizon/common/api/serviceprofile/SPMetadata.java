/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import gov.nasa.horizon.common.api.serviceprofile.SPCommon.SPDataPass;
import gov.nasa.horizon.common.api.serviceprofile.SPCommon.SPDayNight;

import java.util.Date;
import java.util.List;
import java.util.Properties;

public interface SPMetadata extends Accessor {

   String getAncillaryName();

   void setAncillaryName(String name);

   String getBatch();

   void setBatch(String batch);

   String getComment();

   void setComment(String comment);

   Integer getCycle();

   void setCycle(Integer cycle);

   SPDayNight getDayNightMode();

   void setDayNightMode(SPDayNight dayNight);

   Integer getDayOfYearStart();

   void setDayOfYearStart(Integer dayOfYearStart);

   void addExtra(String name, String value);

   void clearExtras();

   Properties getExtras();

   SPProductHistory createProductHistory();

   SPProductHistory getProductHistory();

   void setProductHistory(SPProductHistory history);

   Double getLatitudeStep();

   void setLatitudeStep(Double latitudeStep);

   String getLatitudeUnits();

   void setLatitudeUnits(String latitudeUnits);

   Double getLongitudeStep();

   void setLongitudeStep(Double longitudeStep);

   String getLongitudeUnits();

   void setLongitudeUnits(String longitudeUnits);

   Integer getNumberOfColumns();

   void setNumberOfColumns(Integer numberOfColumns);

   Integer getNumberOfLines();

   void setNumberOfLines(Integer numberOfLines);

   String getObservationMode();

   void setObservationMode(String observationMode);

   Integer getPass();

   void setPass(Integer pass);

   SPDataPass getPassType();

   void setPassType(SPDataPass pass);

   Integer getRevolution();

   void setRevolution(Integer revolution);

   SPPoint createPoint();

   void clearPoints();

   void addPoint(SPPoint point);

   List<SPPoint> getPoints();

   SPLine createLine();

   void clearLines();

   void addLine(SPLine line);

   List<SPLine> getLines();

   SPBoundingRectangle createBoundingRectangle();

   void clearBoundingRectangles();

   void addBoundingRectangle(SPBoundingRectangle boundingRectangle);

   List<SPBoundingRectangle> getBoundingRectangles();

   SPBoundingPolygon createBoundingPolygon();

   void clearBoundingPolygons();

   void addBoundingPolygon(SPBoundingPolygon boundingPolygon);

   List<SPBoundingPolygon> getBoundingPolygons();

   SPSwath createSwath();

   void clearSwaths();

   void addSwath(SPSwath swath);

   List<SPSwath> getSwaths();

   SPEllipse createEllipse();

   void clearEllipses();

   void addEllipse(SPEllipse ellipse);

   List<SPEllipse> getEllipses();

   Date getProductStartTime();

   void setProductStartTime(long startTime);

   Date getProductStopTime();

   void setProductStopTime(long stopTime);
   
   List<Date> getDataDays();
   
   String getPartialId();
   
   void setPartialId(String value);

}
