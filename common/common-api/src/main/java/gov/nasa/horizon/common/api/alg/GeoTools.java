/***************************************************************************
*
* Copyright 2010, by the California Institute of Technology. ALL
* RIGHTS RESERVED. United States Government Sponsorship acknowledged.
* Any commercial use must be negotiated with the Office of Technology
* Transfer at the California Institute of Technology.
*
* @version $Id: GeoTools.java 6721 2011-01-27 18:12:36Z qchau $
*
****************************************************************************/

package gov.nasa.horizon.common.api.alg;

import java.lang.Math;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeoTools {
   // This class was originally written in Python by T. Stough and translated here to Java.
   // Many of the function and variable names were kept the same for ease of reading and comparison between Python and Java.
   //
   // Notes:
   //
   //   1. The m_ prefix signifies private variables. 
   //   2. The i_ prefix signifies input variables.
   //   3. The r_ or o_ prefixes signifies output variables.

   private static Log _logger = LogFactory.getLog(GeoTools.class);

   private double m_R_earth = 6371.0;            // Mean radius in km
   private double m_DEG2RAD = Math.PI / 180.0;   // Convert degrees to radiance
   private double m_RAD2DEG = 180.0 / Math.PI;   // Convert radiance to degrees

   private static GeoTools m_instance = new GeoTools();

   public GeoTools() {
   }

   public static GeoTools getInstance() {
       // There should be only once instance of this object.
       return GeoTools.m_instance;
   }

   private double _brngToDeg(PointPair i_p1, PointPair i_p2) {
       // Given two points, return the bearing between them in degrees.
       //  
       // see: http://www.movable-type.co.uk/scripts/latlong.html

       double lat1 = i_p1.getLatitude() * m_DEG2RAD;
       double lon1 = i_p1.getLongitude() * m_DEG2RAD;
       double lat2 = i_p2.getLatitude() * m_DEG2RAD;
       double lon2 = i_p2.getLongitude() * m_DEG2RAD;

       double dLon = lon2 - lon1;
     
       double y = Math.sin(dLon) * Math.cos(lat2);

       double x = (Math.cos(lat1) * Math.sin(lat2) -
                  (Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)));
     
       double o_bearing = Math.atan2(y, x) * m_RAD2DEG;

       return o_bearing;
   }

   private double _gcDist(PointPair i_p1, PointPair i_p2) {
       // This function implements the spherical law of cosines great circle
       // distance between two points.
       //
       // see: http://www.movable-type.co.uk/scripts/latlong.html

       double lat1 = i_p1.getLatitude() * m_DEG2RAD;
       double lon1 = i_p1.getLongitude() * m_DEG2RAD;
       double lat2 = i_p2.getLatitude() * m_DEG2RAD;
       double lon2 = i_p2.getLongitude() * m_DEG2RAD;

       double o_distance = Math.acos((Math.sin(lat1) * Math.sin(lat2)) + 
                                     (Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
                                    ) * m_R_earth;

       return o_distance;
   }

   public double gcDistXtrack(PointPair i_linep1, PointPair i_linep2, PointPair i_p) {
       // This function implements the cross-track distance between the point, i_p,
       // and the line defined by i_linep1 and i_linep2.
       //  
       // see: http://www.movable-type.co.uk/scripts/latlong.html

       double d13 = this._gcDist(i_linep1, i_p);

       // In the case of polygon point reduction, our base case has the two
       // end points being identical.  This leads to a degenerate line which
       // has no bearing, so we detect and return distance.

       double o_dxt = 0.0;

       if (i_linep1.equals(new PointPair(i_linep2.getLatitude(),i_linep2.getLongitude()))) {
//           System.out.println("i_linep1 is equal to i_linep2.");
//System.out.println("GeoTools::gcDistXtrack: i_linep1 is equal to i_linep2");
//System.out.println("GeoTools::gcDistXtrack: i_linep1.getLatitude()  = " + i_linep1.getLatitude());
//System.out.println("GeoTools::gcDistXtrack: i_linep1.getLongitude() = " + i_linep1.getLongitude());
//System.out.println("GeoTools::gcDistXtrack: i_linep2.getLatitude()  = " + i_linep2.getLatitude());
//System.out.println("GeoTools::gcDistXtrack: i_linep2.getLongitude() = " + i_linep2.getLongitude());
//System.out.println("GeoTools::gcDistXtrack: i_p.getLatitude()  = " + i_p.getLatitude());
//System.out.println("GeoTools::gcDistXtrack: i_p.getLongitude() = " + i_p.getLongitude());
//System.out.println("GeoTools::gcDistXtrack: d13 = " + d13);
           // Do one final tweak of d13 if i_p is the same as i_linep1, we reset it back to zero since
           // the function _gcDict() will return NaN value.

           if (i_linep1.equals(new PointPair(i_p.getLatitude(),i_p.getLongitude()))) {
               // This happen often enough, we will not log the warning as it would be too many.
//               _logger.warn("i_linep1 is equal to i_p.  i_linep1.getLatitude() = " + i_linep1.getLatitude() + ", i_linep1.getLongitude() = " + i_linep1.getLongitude() + ". Original d13 = " + d13);
               d13 = 0.0;
//               _logger.warn("i_linep1 is equal to i_p.  i_linep1.getLatitude() = " + i_linep1.getLatitude() + ", i_linep1.getLongitude() = " + i_linep1.getLongitude() + ". Setting d13 to 0.0");
           }

           return d13;
       } else {
           double brng13 = this._brngToDeg(i_linep1, i_p) * m_DEG2RAD;
     
           double brng12 = this._brngToDeg(i_linep1, i_linep2) * m_DEG2RAD;
     
//System.out.println("GeoTools::gcDistXtrack: brng13 = " + brng13);
//System.out.println("GeoTools::gcDistXtrack: brng12 = " + brng12);
           o_dxt = (Math.asin(Math.sin(d13/m_R_earth) *
                  Math.sin(brng13 - brng12)) * m_R_earth);
       }
     
//       System.out.println("GeoTools::gcDistXtrack: o_dxt = " + o_dxt);
//       System.out.println("GeoTools::gcDistXtrack:abs(o_dxt) = " + Math.abs(o_dxt));
//       System.exit(1);

       // The cross track distance is signed based on what side of the line
       // you're on, so, we return the abs().

       return Math.abs(o_dxt);
   }
}
