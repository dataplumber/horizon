/***************************************************************************
 *
 * Copyright 2010, by the California Institute of Technology. ALL
 * RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology
 * Transfer at the California Institute of Technology.
 *
 * @version $Id: PointPair.java 6159 2010-11-05 20:34:26Z qchau $
 *
 ****************************************************************************/


package gov.nasa.horizon.common.api.alg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PointPair {
   // A way to organize a pair of doubles.
   //
   // Notes:
   //
   //   1. The m_ prefix signifies private variables.
   //   2. The i_ prefix signifies input variables.
   //   3. The r_ or o_ prefixes signifies output variables.

   private static Log _logger = LogFactory.getLog(PointPair.class);

   private double m_x = 0; // latitude
   private double m_y = 0; // longitude

   public PointPair() {
      //super();
   }

   public PointPair(double i_x, double i_y) {
      m_x = i_x;
      m_y = i_y;
   }

   public double getLatitude() {
      return m_x;
   }

   public double getLongitude() {
      return m_y;
   }

   public String toString() {
      String p_as_str = "x:" + (new Double(m_x)).toString() + ",y:" + (new Double(m_y)).toString();
      return p_as_str;
   }

   public boolean equals(PointPair i_p) {
      // Two points are only equal if their latitude and longitude compared.
      // Note: We do 2 kinds of comparison due to the precision of double.

      boolean r_twoPointsAreEqual = false;

      if (i_p == null) {
         _logger.error("Input variable i_p is null. Cannot perform equals() function properly.  Returning false.");
         return (r_twoPointsAreEqual);
      }

      // Do the normal compare.

      if (m_x == i_p.getLatitude() && m_y == i_p.getLongitude()) {
         r_twoPointsAreEqual = true;
      }

      // Do another sanity check by reducing the precision.

      if ((int) (m_x * 100) == (int) (i_p.getLatitude() * 100) &&
            (int) (m_y * 100) == (int) (i_p.getLongitude() * 100)) {
         r_twoPointsAreEqual = true;
      }

      // From Javadoc:
      //
      // public static int compare(double d1,
      //                           double d2)
      // Compares the two specified double values. The sign of the integer value returned is the same as that of the integer that would be returned by the call:
      //     new Double(d1).compareTo(new Double(d2))
      //
      //
      // Parameters:
      // d1 - the first double to compare
      // d2 - the second double to compare
      // Returns:
      // the value 0 if d1 is numerically equal to d2; a value less than 0 if d1 is numerically less than d2; and a value greater than 0 if d1 is numerically greater than d2.
      // Since:
      // 1.4

      // Do the compare using Double object if the previous did not compare.

      if ((Double.compare(m_x, i_p.getLatitude()) == 0) &&
            (Double.compare(m_y, i_p.getLongitude()) == 0)) {
         r_twoPointsAreEqual = true;
      }


      return r_twoPointsAreEqual;
   }

   public boolean equals(double i_x, double i_y) {
      boolean r_twoPointsAreEqual = false;

      // Do the normal compare.

      if (m_x == i_x && m_y == i_y) {
         r_twoPointsAreEqual = true;
      }

      // From Javadoc:
      //
      // public static int compare(double d1,
      //                           double d2)
      // Compares the two specified double values. The sign of the integer value returned is the same as that of the integer that would be returned by the call:
      //     new Double(d1).compareTo(new Double(d2))
      //
      //
      // Parameters:
      // d1 - the first double to compare
      // d2 - the second double to compare
      // Returns:
      // the value 0 if d1 is numerically equal to d2; a value less than 0 if d1 is numerically less than d2; and a value greater than 0 if d1 is numerically greater than d2.
      // Since:
      // 1.4

      // Do the compare using Double object if the previous did not compare.

      if ((Double.compare(m_x, i_x) == 0) &&
            (Double.compare(m_y, i_y) == 0)) {
         r_twoPointsAreEqual = true;
      }

      return r_twoPointsAreEqual;
   }
}
