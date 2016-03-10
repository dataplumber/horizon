package gov.nasa.horizon.common.api.alg;

import gov.nasa.horizon.common.api.serviceprofile.SPPoint;
import gov.nasa.horizon.common.api.serviceprofile.jaxb.PointJaxb;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

//import gov.nasa.horizon.common.api.serviceprofile.jaxb.PointJaxb;

public class PolyToolsTest {
   // How to run this test:
   //
   // mvn -Dtest=PolyToolsTest test

   @Before
   public void setup() {

   }

   @Test
   public void testPolyTools_0() {
      PointPair p1 = new PointPair(5.6, 7.7);
      PointPair p2 = new PointPair(5.6, 7.7);

      System.out.println("testPolyTools_0:p1.getLatitude() = " + p1.getLatitude());
      System.out.println("testPolyTools_0:p1.getLongitude() = " + p1.getLongitude());
      System.out.println("testPolyTools_0:p2.getLatitude() = " + p2.getLatitude());
      System.out.println("testPolyTools_0:p2.getLongitude() = " + p2.getLongitude());
      assertTrue(p1.equals(p2));
   }

   @Test
   public void testPolyTools_1() {
      SPPoint p1 = PointJaxb.createPoint();
      SPPoint p2 = PointJaxb.createPoint();
      p1.setLatitude(1.0);
      p1.setLongitude(2.0);

      p2.setLatitude(1.0);
      p2.setLongitude(2.0);

      System.out.println("testPolyTools_1:p1.getLatitude() = " + p1.getLatitude());
      System.out.println("testPolyTools_1:p1.getLongitude() = " + p1.getLongitude());
      System.out.println("testPolyTools_1:p2.getLatitude() = " + p2.getLatitude());
      System.out.println("testPolyTools_1:p2.getLongitude() = " + p2.getLongitude());

      // Test for equal to self.
      assertTrue(p1.equals(p1));

      // Test for equal of 2 different points but containing same latitude and longitude.
//      p1.setOwner(PolyToolsTest.class);
//      p2.setOwner(PolyToolsTest.class);
      assertTrue(p1.equals(p2));
   }

//      assertTrue(true);

   @Test
   public void testPolyTools_2() {
      // Using a polygon.txt, and the hard-coded parameters below, simplify the
      // polygon while dumping debug info and an "output.kml" file with
      // initial, intermediate, and final results

      assertTrue(true);

      double dist_thresh = 5.0; // km
      int max_points = 512;
      boolean dbgFlag = false;
      //boolean dbgFlag = false;
      boolean kmlOutputFlag = false;

      int KML_HEIGHT = 2500000;

      // the polygon.txt file contains a list of lat, lon pairs all comma separated

      System.out.println("testPolyTools_2 called");

      PolyTools polyTools = PolyTools.getInstance();

      IndivisibleLineSegment lineSegment = polyTools.loadLineFromFile(System.getProperty("common.test.path") + File.separator + "polygon.txt");

      ArrayList<PointPair> initial_line = lineSegment.getLine();
      System.out.println("initial_line.size() [" + initial_line.size() + "]");

      // Generate KML of initial polygon

      StringBuilder coordList = new StringBuilder();

      int index = 0;

      for (index = 0; index < initial_line.size(); index++) {
         // Get a point from the line via the index.
         PointPair a_point = initial_line.get(index);
         if (index == 0) {
            coordList.append(a_point.getLongitude() + ", " + a_point.getLatitude() + ", " + KML_HEIGHT);
         } else {
            coordList.append(", " + a_point.getLongitude() + ", " + a_point.getLatitude() + ", " + KML_HEIGHT);
         }
//System.out.println("index " + index + " (" + a_point.getLatitude() + ", " + a_point.getLongitude() + ")");
      }

//       System.out.println("coordList [");
//       System.out.println(coordList.toString());
//       System.out.println("]");

      if (kmlOutputFlag) {
         polyTools.create_KML_andAddToOutputLine(initial_line, "Initial Polygon");
      }

      // Simplify polygon as a poly line.
      // Get an array of PointPair back.

      ArrayList<PointPair> out_line = polyTools.ramerDouglasStough(lineSegment, dist_thresh, max_points, PolyTools.POLY_ALGORITHM_TYPE.EUC_DIST_POINT2_LIN_SQUARE,
            dbgFlag, kmlOutputFlag);

      // Write the entire kml doc to disk file output.kml in default directory.
      // Note: The file output.kml is very difficult to edit using vi due to lack of end of line character.
      //       Recommendation to use cat to view the file from command line.

      if (kmlOutputFlag) {
         polyTools.create_KML_andAddToOutputLine(out_line, "Final Polygon");
         polyTools.kmlDocToFile("output.kml");
//           System.out.println(polyTools.getPolygonString());
      }

//  coordlist = ", ".join([ ("%f, %f, %d" % (p[1], p[0], KML_HEIGHT)) for p in line])

      // The out_line content should look like this with the following assumption:
      //
      //    1.  The function getLineAsLongString() uses String.format("(%.3f %.3f) ",a_point.getLatitude(),a_point.getLongitude()) to print the point pair.
      //    2.  There is a space at the end of this variable constant expectedOutLine.
      //    3.  The content of polygon.txt has not been tampered with in any ways.

      // Note: This string variable should be one long string.
      String expectedOutLine = "(-74.260 75.920) (-48.340 11.650) (-32.480 3.730) (-16.330 -1.440) (-7.850 -3.590) (-2.110 -4.900) (3.920 -6.190) (9.690 -7.350) (15.650 -8.490) (21.250 -9.520) (26.850 -10.520) (32.640 -11.530) (38.150 -12.480) (42.580 -13.240) (47.540 -14.100) (52.680 -15.010) (58.000 -15.990) (62.410 -16.860) (67.000 -17.860) (72.660 -19.340) (77.780 -21.230) (82.970 -24.920) (83.840 -28.980) (84.640 -36.040) (85.140 -44.510) (85.430 -58.070) (85.300 -71.210) (84.950 -79.980) (84.190 -89.110) (83.580 -93.090) (82.610 -96.760) (81.650 -98.500) (80.670 -99.100) (79.760 -98.920) (78.790 -98.130) (77.990 -97.100) (77.140 -95.680) (76.180 -93.680) (75.200 -91.230) (74.420 -88.930) (73.640 -86.290) (72.940 -83.510) (47.780 -36.600) (28.800 -27.790) (20.290 -25.110) (11.090 -22.670) (2.110 -20.600) (-5.720 -18.980) (-11.120 -17.920) (-16.890 -16.850) (-22.390 -15.870) (-28.060 -14.890) (-33.280 -14.010) (-38.590 -13.130) (-48.020 -11.580) (-53.840 -10.610) (-58.410 -9.830) (-63.330 -8.950) (-67.800 -8.080) (-72.990 -6.920) (-78.340 -5.310) (-83.420 -2.470) (-88.010 9.730) (-86.740 10.560) (-85.570 13.300) (-84.350 16.970) (-83.080 21.280) (-81.940 25.500) (-80.930 29.550) (-79.960 33.660) (-78.930 38.420) (-78.040 42.930) (-77.180 47.760) (-76.370 52.930) (-75.750 57.500) (-75.290 61.480) (-74.800 66.690) (-74.440 71.920) (-74.260 75.920) ";
      String actualOutLine = polyTools.getLineAsLongString(out_line);

      // Print an error message if the two lines do not compare otherwise, just print an info message.
      if (!(actualOutLine.equals(expectedOutLine))) {
         System.out.println("PolyToolsTest:ERROR, The expectedOutLine does not match with actualOutLine. Test failed.");
      } else {
         System.out.println("PolyToolsTest:INFO, Here is the listing of expectedOutLine and actualOutLine. Test passed.  Both lines are identical.");
      }

      System.out.println("expectedOutLine [" + expectedOutLine.length() + "] [" + expectedOutLine + "]");
      System.out.println("actualOutLine   [" + actualOutLine.length() + "] [" + actualOutLine + "]");

      // Test will fail if the output lines do not match.

      assertTrue(actualOutLine.equals(expectedOutLine));
   }


   @Test
   public void testPolyTools_3() {
      // This test is similar to the previous test testPolyTools_1, but using a different polygon file.
      // This test uses polygon_from_ascat_data_file.txt, and the hard-coded parameters below, simplify the
      // polygon while dumping debug info and an "output.kml" file with
      // initial, intermediate, and final results

      assertTrue(true);

      double dist_thresh = 5.0; // km
      int max_points = 512;
      //boolean dbgFlag = true;
      boolean dbgFlag = false;
      //boolean kmlOutputFlag = true;
      boolean kmlOutputFlag = false;

      int KML_HEIGHT = 2500000;

      // the polygon.txt file contains a list of lat, lon pairs all comma separated

      System.out.println("testPolyTools_3 called");

      PolyTools polyTools = PolyTools.getInstance();

      IndivisibleLineSegment lineSegment = polyTools.loadLineFromFile(System.getProperty("common.test.path") + File.separator + "polygon_from_ascat_data_file_3.txt");

      ArrayList<PointPair> initial_line = lineSegment.getLine();
      System.out.println("initial_line.size() [" + initial_line.size() + "]");

      // Generate KML of initial polygon

      StringBuilder coordList = new StringBuilder();

      int index = 0;

      for (index = 0; index < initial_line.size(); index++) {
         // Get a point from the line via the index.
         PointPair a_point = initial_line.get(index);
         if (index == 0) {
            coordList.append(a_point.getLongitude() + ", " + a_point.getLatitude() + ", " + KML_HEIGHT);
         } else {
            coordList.append(", " + a_point.getLongitude() + ", " + a_point.getLatitude() + ", " + KML_HEIGHT);
         }
//System.out.println("index " + index + " (" + a_point.getLatitude() + ", " + a_point.getLongitude() + ")");
      }

//       System.out.println("coordList [");
//       System.out.println(coordList.toString());
//       System.out.println("]");

      if (kmlOutputFlag) {
         polyTools.create_KML_andAddToOutputLine(initial_line, "Initial Polygon");
      }

      // Simplify polygon as a poly line.
      // Get an array of PointPair back.

      ArrayList<PointPair> out_line = polyTools.ramerDouglasStough(lineSegment, dist_thresh, max_points, PolyTools.POLY_ALGORITHM_TYPE.EUC_DIST_POINT2_LIN_SQUARE,
            dbgFlag, kmlOutputFlag);

      // Write the entire kml doc to disk file output.kml in default directory.
      // Note: The file output.kml is very difficult to edit using vi due to lack of end of line character.
      //       Recommendation to use cat to view the file from command line.

      if (kmlOutputFlag) {
         polyTools.create_KML_andAddToOutputLine(out_line, "Final Polygon");
         polyTools.kmlDocToFile("output.kml");
//           System.out.println(polyTools.getPolygonString());
      }

//  coordlist = ", ".join([ ("%f, %f, %d" % (p[1], p[0], KML_HEIGHT)) for p in line])

      // The out_line content should look like this with the following assumption:
      //
      //    1.  The function getLineAsLongString() uses String.format("(%.3f %.3f) ",a_point.getLatitude(),a_point.getLongitude()) to print the point pair.
      //    2.  There is a space at the end of this variable constant expectedOutLine.
      //    3.  The content of polygon.txt has not been tampered with in any ways.

      // Note: This string variable should be one long string.
      String expectedOutLine = "(7.176 324.852) (14.582 322.933) (22.604 320.563) (30.997 317.618) (40.131 313.572) (57.212 300.798) (71.836 212.911) (47.663 167.325) (37.428 161.582) (28.036 157.746) (18.316 154.597) (9.182 152.118) (0.449 150.041) (-7.864 148.251) (-13.337 147.148) (-18.811 146.091) (-24.064 145.111) (-29.315 144.158) (-34.562 143.226) (-39.587 142.344) (-44.607 141.467) (-49.404 140.626) (-54.416 139.733) (-59.422 138.811) (-64.423 137.833) (-69.203 136.799) (-74.196 135.511) (-78.967 133.809) (-83.727 130.577) (-88.399 112.083) (-86.630 326.096) (-81.893 318.954) (-77.127 316.577) (-72.137 315.052) (-67.360 313.918) (-62.361 312.886) (-57.358 311.935) (-52.350 311.027) (-47.555 310.180) (-42.537 309.302) (-37.515 308.425) (-32.270 307.500) (-27.022 306.559) (-21.770 305.595) (-16.297 304.559) (-10.824 303.482) (-1.196 301.457) (2.247 316.758) (-5.616 318.573) (-13.891 320.701) (-22.560 323.268) (-31.801 326.570) (-50.406 336.809) (-72.640 28.486) (-53.511 111.704) (-34.039 123.648) (-23.745 127.486) (-14.215 130.349) (-5.286 132.646) (3.452 134.645) (13.516 136.735) (23.364 138.630) (32.768 140.352) (41.942 141.991) (51.102 143.636) (60.030 145.322) (64.597 146.264) (68.943 147.259) (73.285 148.433) (77.623 149.981) (82.169 152.694) (86.676 161.017) (88.657 293.845) (84.285 321.399) (79.965 325.332) (75.414 327.353) (70.856 328.731) (66.294 329.840) (61.729 330.814) (57.159 331.714) (52.586 332.570) (47.791 333.442) (43.211 334.261) (38.408 335.117) (28.792 336.847) (19.822 338.516) (10.630 340.324) (7.176 324.852) ";
      String actualOutLine = polyTools.getLineAsLongString(out_line);

      // Print an error message if the two lines do not compare otherwise, just print an info message.
      if (!(actualOutLine.equals(expectedOutLine))) {
         System.out.println("PolyToolsTest:ERROR, The expectedOutLine does not match with actualOutLine. Test failed.");
      } else {
         System.out.println("PolyToolsTest:INFO, Here is the listing of expectedOutLine and actualOutLine. Test passed.  Both lines are identical.");
      }

      System.out.println("expectedOutLine [" + expectedOutLine.length() + "] [" + expectedOutLine + "]");
      System.out.println("actualOutLine   [" + actualOutLine.length() + "] [" + actualOutLine + "]");

      // Test will fail if the output lines do not match.

      assertTrue(actualOutLine.equals(expectedOutLine));
   }

}
