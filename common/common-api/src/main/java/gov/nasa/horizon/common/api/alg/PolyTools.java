/***************************************************************************
*
* Copyright 2010, by the California Institute of Technology. ALL
* RIGHTS RESERVED. United States Government Sponsorship acknowledged.
* Any commercial use must be negotiated with the Office of Technology
* Transfer at the California Institute of Technology.
*
* @version $Id: PolyTools.java 6721 2011-01-27 18:12:36Z qchau $
*
****************************************************************************/


package gov.nasa.horizon.common.api.alg;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import gov.nasa.horizon.common.api.util.FileUtility;

import java.lang.Math;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PolyTools {
   // This class was originally written in Python by T. Stough and translated to Java here.
   // Many of the function and variable names were kept the same for ease of reading and comparison between Python and Java.
   //
   // Notes:
   //
   //   1. The m_ prefix signifies private variables. 
   //   2. The i_ prefix signifies input variables.
   //   3. The r_ or o_ prefixes signifies output variables.

   private static Log _logger = LogFactory.getLog(PolyTools.class);

   public static enum POLY_ALGORITHM_TYPE {
          EUC_DIST_POINT2_LIN_SQUARE,  // Call the eucDistPt2LinSq() function.
          GC_DIST_X_TRACK              // Calls the GeoTools.gcDistXtrack function.
   }

   private static PolyTools m_instance = new PolyTools();
   private static int m_KML_HEIGHT = 2500000;

   private static StringBuilder m_kml_poly_builder = new StringBuilder(); // Care must be taken to build a new StringBuilder() object for every instance otherwise memory leak will take place after about 880 iterations.

   // Constants to build the KML outout file.

   private static String KML_POLY_PART_1 = "\n" +
"        <Placemark>\n" +
"                <name>";

   private static String KML_POLY_PART_2 = "</name>\n" +
"                <styleUrl>#msn_ylw-pushpin</styleUrl>\n" +
"                <Polygon>\n" +
"                        <altitudeMode>relativeToGround</altitudeMode>\n" +
"                        <outerBoundaryIs>\n" +
"                                <LinearRing>\n" +
"                                        <coordinates>";

   // The actual coordinates will go here between KML_POLY_PART_2 and KML_POLY_PART_2.

   private static String KML_POLY_PART_3 = "</coordinates>\n" +
"                                </LinearRing>\n" +
"                        </outerBoundaryIs>\n" +
"                </Polygon>\n" +
"        </Placemark>\n";

   private static int m_num_b_equals_to_zero = 0; // Useful for debugging.

   private class Tuple {
      // A way to organize a pair of numbers: double, int
      // where the double is the value and the int is the index.
      // There may be a more generalize solution but in these tools, we only need to keep track of such a set of 2 numbers.

      private double m_value = 0.0;
      private int    m_index = 0;

      public Tuple() {
      }

      public Tuple(double i_value, int i_index) {
         m_value = i_value;
         m_index = i_index;
      }

      public double getValue() {
          return m_value;
      }

      public int getIndex() {
          return m_index;
      }
   }

   private class LineTuple {
      // A way to organize a pair of things: SplittableLineSegment, int
      private SplittableLineSegment m_line = null; 
      private int    m_index = 0;

      public LineTuple() {
      }

      public LineTuple(SplittableLineSegment i_line, int i_index) {
         if (i_line== null) {
             _logger.error("Input variable i_line is null. Cannot perform constructor LineTuple(SplittableLineSegment i_line, int i_index) properly.");
         }

         m_line  = i_line;
         m_index = i_index;
      }

      public SplittableLineSegment getLine() {
          // Can also return a null object.
          return m_line;
      }

      public int getIndex() {
          return m_index;
      }
   }

   private class KeyQueuedList  {
       ArrayList<Double> m_key_list = new ArrayList<Double>(0);
       ArrayList<SplittableLineSegment> m_key_idx_list = new ArrayList<SplittableLineSegment>(0);
       ArrayList<SplittableLineSegment> m_items  = new ArrayList<SplittableLineSegment>(0); 

       public KeyQueuedList () {
       }

       public int getLength() {
           return m_items.size();
       }

       public void append(Double i_key, SplittableLineSegment i_item) {
           insertItem(getLength()-1,i_key,i_item, false);
       }

       public int bisect(Double i_key) {
           int o_pos_of_key = 0; 
           if (m_key_list.size() == 0) {
//System.out.println("  bisect:m_key_list.size() is zero, setting o_pos_of_key to 0");
               o_pos_of_key = 0; 
           } else {
               // Find where the i_key occurs in m_key_list.

               int where_key_is = java.util.Collections.binarySearch(m_key_list,i_key);
//System.out.println("bisect:INFO, where_key_is " + where_key_is + ", i_key " + i_key);

               // If cannot find key (signified by -1), figure out where to insert. 

               if (where_key_is <= -1) {
                   // If the key is larger than the last key, we insert at the end.
                   if (i_key >= m_key_list.get(m_key_list.size()-1)) {
                       o_pos_of_key = m_key_list.size(); 
//System.out.println("bisect:INFO, i_key " + i_key + " is greater than last key: " + m_key_list.get(m_key_list.size()-1) + ", setting o_pos_of_key to " + o_pos_of_key);
                   } else if (i_key < m_key_list.get(0)) {
                       // If the key is smaller than the first key, we insert in the beginning.
                       o_pos_of_key = 0;
//System.out.println("bisect:INFO, i_key " + i_key + " is less than than first key: " + m_key_list.get(0) + ", setting o_pos_of_key to " + o_pos_of_key);
                   } else {
                       // If the key is some where in the middle of the array, we have to find where it occurs.
                       o_pos_of_key = this._findMiddle(i_key);
                   }
               } else {
                   if (where_key_is > 0) {
                       o_pos_of_key = where_key_is - 1; 
//System.out.println("bisect:INFO, i_key " + i_key + " is in m_key_list at greater zero index " + where_key_is + ", setting o_pos_of_key to " + o_pos_of_key);
                   } else {
                       o_pos_of_key = where_key_is;
//System.out.println("bisect:INFO, i_key " + i_key + " is in m_key_list at zero index index " + where_key_is + ", setting o_pos_of_key to " + o_pos_of_key);
                   }
               }

           }

//System.out.println("bisect:INFO, m_key_list.size() = [" + m_key_list.size() + "]");
//System.out.println("bisect:INFO, i_key = [" + i_key + "]");
//System.out.println("bisect:INFO, o_pos_of_key = [" + o_pos_of_key + "]");
//for (int index = 0; index < m_key_list.size(); index++) {
//System.out.println("  bisect:INFO, pre insertion m_key_list[" + index + "]  [" + m_key_list.get(index) + "]");
//}
//System.out.println("bisect:INFO, o_pos_of_key = [" + o_pos_of_key + "]");
           return o_pos_of_key;
       }

       private int _findMiddle(Double i_key) {
          // Given a key, we'll search in sort m_key_list to find where to insert into m_key_list.
          int o_middleIndex = 0;

          int index = 0;
          boolean foundLargerKey = false;
          Double a_keyFromKeyList = null;

          // Look through until we find a key in the list that is larger than our input key.

//System.out.println("_findMiddle:INFO, i_key = [" + i_key + "]");

          while ((index < m_key_list.size()) && (foundLargerKey == false)) {
             a_keyFromKeyList = m_key_list.get(index);  // Get a key from list
             if (i_key <= a_keyFromKeyList) {           // Compare it with our input key.
                  foundLargerKey = true;                // If our key is smaller, we are done. 
                  o_middleIndex = index; 
             }
             index++;
          }

//System.out.println("_findMiddle:INFO, o_middleIndex = [" + o_middleIndex + "]");

          return o_middleIndex;
       }

       public void insertItem(int i_indexToInsert, Double i_key, SplittableLineSegment i_item, boolean i_dbgFlag) {
           // Insert the item in the item list and insert the key in the sorted
           // key stack.

           // Find the position where we should insert this new item.
           // If pos is -1, then the insertion is in the beginning.

           int pos = bisect(i_key); // This is the middle of the m_key_list array.

           m_key_list.add(pos,i_key);
           m_key_idx_list.add(pos,i_item);

if (i_dbgFlag) {
System.out.println("    insertItem:INFO, m_key_list.size() [" + m_key_list.size() + "]");
for (int index = 0; index < m_key_list.size(); index++) {
System.out.println("    insertItem:INFO, m_key_list[" + index + "]  [" + m_key_list.get(index) + "]");
}
}
//System.out.println("Stopping in insertItem function.");
//System.out.println("i_dbgFlag = " + i_dbgFlag);
//System.exit(0);

           m_items.add(i_indexToInsert,i_item);

if (i_dbgFlag) {
System.out.println("insertItem:INFO, pos = [" + pos + "]");
System.out.println("insertItem:INFO, i_key = [" + i_key + "]");
System.out.println("insertItem:INFO, i_item = [" + i_item + "]");
System.out.println("insertItem:INFO, i_indexToInsert = [" + i_indexToInsert + "], i_key [" + i_key + "]");
}

       }

       public LineTuple popKey() {
           LineTuple r_tuple = null;

           // Do a santity check to make sure we have a valid list to look through.
           if (m_items == null) {
              if (_logger.isDebugEnabled()) {
//                  System.out.println("popKey:ERROR, m_items is null.");
                  _logger.warn("m_items is null.  Returning null object for popKey() function.");
              }
              return r_tuple;
           }

           // If the m_key_list is empty, we can't pop, must return null.
           if (m_items.size() == 0) {
              if (_logger.isDebugEnabled()) {
//                  System.out.println("popKey:ERROR, m_items.size() is zero.");
                  _logger.warn("m_items is zero size.  Returning null object for popKey() function.");
              }
              return r_tuple;
           }

           // Get the key on top.
           Double key     = m_key_list.remove(m_key_list.size()-1);

//System.out.println("popKey:INFO, key = [" + key + "]");
//System.out.println("popKey:INFO, post remove m_key_list.size() = [" + m_key_list.size() + "]");

           // Get the key-index pair on top.

           SplittableLineSegment key_idx = m_key_idx_list.remove(m_key_idx_list.size()-1);

//System.out.println("popKey:INFO, post remove m_key_idx_list.size() = [" + m_key_idx_list.size() + "]");

           // Use the key-index pair to get to the index
           int i_idx = m_items.indexOf(key_idx);

//System.out.println("popKey:INFO, key_idx.getSplitIdx() = [" + key_idx.getSplitIdx()  + "]");
//System.out.println("popKey:INFO, i_idx = [" + i_idx + "]");
//System.out.println("popKey:INFO, key_idx.toString() [" + key_idx.toString()  + "]");
//for (int index = 0; index < m_items.size(); index++) {
//System.out.println("popKey:INFO, m_items[" + index + "]  [" + m_items.get(index) + "]");
//}

           if (i_idx == -1) {
               if (_logger.isDebugEnabled()) {
//               System.out.println("popKey:ERROR, Cannot find key_idx from m_items.");
                   _logger.warn("Cannot find key_idx from m_items.");
               }
               return r_tuple; 
           }

//System.out.println("popKey:INFO, pre m_items.remove()");
//System.out.println("popKey:INFO, old m_items.size() " + m_items.size());

           // Finally, use the index to get to the actual item.

           SplittableLineSegment item    = m_items.remove(i_idx);

//System.out.println("popKey:INFO, post m_items.remove()");
//System.out.println("popKey:INFO, new m_items.size() " + m_items.size());

           r_tuple = new LineTuple (item,i_idx);
           return r_tuple;
       }

       public Double maxKey() {
//System.out.println("maxKey:WARN, m_key_list.size() is zero.");

           // Merely return the last item since we know how objects are inserted into this list.
           if (m_key_list.size() == 0) {
               if (_logger.isDebugEnabled()) {
                   _logger.warn("m_key_list.size() is zero size, returning 0 from maxKey() function.");
               }
               return (new Double(0));
           }
           int key = m_key_list.size() - 1;

           return (m_key_list.get(key));
       }

       public String toString() {
//           String o_string = "nkeys:" + m_key_list.size() + ", nkeyidxs: " + m_key_idx_list.size() + ", nitems: " + sum of item lengths: " + m_items.size();+ ", sum of item lengths: " + m_items.size();

           StringBuffer sbuf =  new StringBuffer();
           sbuf.append("nkeys: " + m_key_list.size() + ",nkeyidxs: " + m_key_idx_list.size() + ", nitems: " +  m_items.size());
           for (int index = 0; index < m_key_list.size(); index++) {
              int i_idx = m_items.indexOf(m_key_idx_list.get(index));
              sbuf.append(", k(" + index + ") " + m_key_list.get(index) + " -> " + i_idx);
           }
           int size = 0;
           for (int item = 0; item <  m_items.size(); item++) {
               size += m_items.get(item).getLineLength();
           }

           sbuf.append(", sum of item lengths: " + size);
           return sbuf.toString();
       }

       public ArrayList<SplittableLineSegment> getItems() {
           return (m_items);
       }
   } // end KeyQueuedList class

   private class SplittableLineSegment {
       private int m_splitIdx;
       private IndivisibleLineSegment m_line = null; 

       public SplittableLineSegment() {
       }

       public int getLineLength() {
           if (m_line == null) {
               _logger.warn("Variable m_line is null, returning 0 from getLineLength() function.");
              return 0;
           } else {
              return m_line.getLineLength();
           }
       }

       public IndivisibleLineSegment getLine() {
           // Can be null.
           return m_line;
       }

       public int getSplitIdx() {
           return m_splitIdx;
       }

       public SplittableLineSegment(int i_idx,IndivisibleLineSegment i_line) {
           if (i_line == null) {
              _logger.warn("Variable i_line is null, cannot create constructor(SplittableLineSegment(int i_idx,IndivisibleLineSegment i_line) properly.");
           }
           m_splitIdx = i_idx;
           m_line     = i_line;
       }

       public String toString() {
//System.out.print(String.format("(%.2f, %.2f) ",a_point.getLatitude(),a_point.getLongitude()));
//           String o_string = new String("len: " + m_line.getLineLength() + ", s_idx: " + m_splitIdx + ", 1st: (" + m_line.getPoint(0).getLatitude() + ", " + m_line.getPoint(0).getLongitude() + "), last: (" + m_line.getPoint(m_line.getLineLength() -1 ).getLatitude() + ", " + m_line.getPoint(m_line.getLineLength() - 1).getLongitude() + ")");

           String o_string = String.format("len: %d, s_idx: %d, 1st: (%.3f, %.3f), last: (%.3f, %.3f)",
                                           m_line.getLineLength(),
                                           m_splitIdx,
                                           m_line.getPoint(0).getLatitude(),
                                           m_line.getPoint(0).getLongitude(),
                                           m_line.getPoint(m_line.getLineLength() - 1).getLatitude(),
                                           m_line.getPoint(m_line.getLineLength() - 1).getLongitude());
           return o_string;
       }

       public ArrayList<IndivisibleLineSegment> splitLine() {
           ArrayList<IndivisibleLineSegment> o_linesSplitted = new  ArrayList<IndivisibleLineSegment>(0);
           if (m_line == null) {
               _logger.warn("Variable m_line is null, returning 0 from splitLine() function.");
               return null;
           } else {
               // Split the m_line into a and b at the split point and return 2 line portions.
               ArrayList<PointPair> a = m_line.splitLine(0,m_splitIdx);                // Get from 0 to split point
               ArrayList<PointPair> b = m_line.splitLine(m_splitIdx,getLineLength());  // Get from split point to end. 

//System.out.println("SplittableLineSegment:splitLine:a.size() " + a.size());
//System.out.println("SplittableLineSegment:splitLine:b.size() " + b.size());

               o_linesSplitted.add(new IndivisibleLineSegment(a));
               o_linesSplitted.add(new IndivisibleLineSegment(b));
           }
           return o_linesSplitted;
       }
   } // end SplittableLineSegment class 

   public PolyTools() {
   }

   public static PolyTools getInstance() {
       // There should be only once instance of this object.
       // Note: because this object is called many times, we have to clear out the StringBuilder object
       //       by creating a new one every time.
       m_kml_poly_builder = new StringBuilder();
       return PolyTools.m_instance;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private Tuple _findLineSplitPoint(IndivisibleLineSegment i_line, PolyTools.POLY_ALGORITHM_TYPE i_distFn, String findName, boolean i_dbgFlag)
   {
       // Given a poly-line, this determines which point in the poly-line
       // deviates most from the line defined by the first and last point in the
       // poly-line and returns the great circle distance from the point to the
       // line.

       // If the line is only two points, there is no distance, return 0,0
       Tuple o_splitPoints = null; 
       if (i_line == null) {
//           if (_logger.isDebugEnabled()) {
               _logger.warn("Variable i_line is null, returning 0 from _findLineSplitPoint() function.");
//               System.out.println("_findLineSplitPoint:WARN, i_line is null. findName " + findName);
//           }
           return o_splitPoints;
       }

       if (i_line.getLineLength() < 3) {
//           if (_logger.isDebugEnabled()) {
//                System.out.println("_findLineSplitPoint:WARN, i_line.getLineLength() is less than 3. findName " + findName);
//                System.out.println("_findLineSplitPoint:WARN, returning zeroed tuple. findName " + findName);
//                _logger.warn("Variable i_line length is less than 3, returning zeroed tuple. findName " + findName + " from _findLineSplitPoint() function.");
//           }
           o_splitPoints = new Tuple();
           return o_splitPoints; 
       }

//       System.out.println("_findLineSplitPoint:INFO, i_line.getLineLength() is greater or equal to 2:" + i_line.getLineLength());

       // Compute the distance from each intermediary point to the simple
       // line.

       ArrayList<Double> r_dist = new ArrayList<Double>(i_line.getLineLength()); 

if (i_dbgFlag) {
       System.out.println("_findLineSplitPoint:INFO, i_line.getLineLength() = " + i_line.getLineLength());
       System.out.println("_findLineSplitPoint:INFO, r_dist = " + r_dist);
       System.out.println("_findLineSplitPoint:INFO, r_dist.size = " + r_dist.size());
       System.out.println("_findLineSplitPoint:INFO, pre for loop");
}

       // For each line, calculate the distant and add it to the array of distances.
 
       for (int line_index = 1; line_index < i_line.getLineLength(); line_index++) {
if (i_dbgFlag) {
System.out.println("_findLineSplitPoint:INFO, inside for loop.");
System.out.println("_findLineSplitPoint:INFO, line_index = " + line_index);
System.out.println("_findLineSplitPoint:INFO, pre gcDistXtrack.");
}

           double line_squared = 0.0; 

           // Use the appropriate function based on what the user asked for.

           if (i_distFn == POLY_ALGORITHM_TYPE.EUC_DIST_POINT2_LIN_SQUARE) {
               line_squared = GeoTools.getInstance().gcDistXtrack(i_line.getPoint(0), i_line.getPoint(i_line.getLineLength()-1), i_line.getPoint(line_index));
           } else if (i_distFn == POLY_ALGORITHM_TYPE.GC_DIST_X_TRACK) {
               line_squared = this.eucDistPt2LinSq(i_line.getPoint(0), i_line.getPoint(i_line.getLineLength()-1), i_line.getPoint(line_index));
           } else {
               System.err.println("_findLineSplitPoint:ERROR, Function name " + i_distFn + " not supported at the moment.  Program exiting.");
               System.err.println("_findLineSplitPoint:Supported functions are CALL_FUNCTION_GCDISTXTRACK, CALL_FUNCTION_EUCDISTPT2LINSQ.");
               System.exit(1);
           }

if (i_dbgFlag) {
System.out.println("_findLineSplitPoint:INFO, post gcDistXtrack.");
System.out.println("_findLineSplitPoint:INFO, line_squared [" + line_squared + "]");
}

           r_dist.add(new Double(line_squared));
if (i_dbgFlag) {
System.out.println("_findLineSplitPoint:INFO, r_dist after add() " + r_dist.size());
}
       }
//if (2 == 2) return o_splitPoints;

       // Get the max distance and index of the maximum deviating point.

       Double max_dist = this.max(r_dist);

if (i_dbgFlag) {
System.out.println("_findLineSplitPoint:INFO, max_dist [" + max_dist + "]");
System.out.println("_findLineSplitPoint:INFO, r_dist.size() [" + r_dist.size() + "]");
}

       // Need to add 1 because index doesn't include first item in line

       int max_index = r_dist.indexOf(max_dist) + 1;

if (i_dbgFlag) {
System.out.println("_findLineSplitPoint:INFO, max_index [" + max_index + "]");
}

       // Add both of these values to our returning structure.

       o_splitPoints = new Tuple(max_dist.doubleValue(),max_index);

       return (o_splitPoints);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public Double max(ArrayList<Double> t) {

//System.out.println("max:INFO, Entering");

     Double maximum = null;
     Object obj = Collections.max(t);
//System.out.println("max:INFO, obj [" + obj + "]");
     maximum = (Double) obj;
     return maximum;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public  ArrayList<PointPair> computeOutputLine(ArrayList<SplittableLineSegment> i_lineList_items) {
       // Extract the line from the list of items.  This is abstracted because
       // the line items are a bit hairy.  Maybe I'll class-ify the items.
       ArrayList<PointPair> o_out_line = new ArrayList<PointPair>(0); 

       // Get the last point of the last segment.

       IndivisibleLineSegment lastLineSegment = (i_lineList_items.get(i_lineList_items.size() - 1)).getLine();
       PointPair lastPoint       = lastLineSegment.getLine().get(lastLineSegment.getLineLength()-1);

       // For each line, get the first point and add it to the output line o_out_line list.

       Iterator listTraveller = i_lineList_items.iterator();
       SplittableLineSegment a_splittableLine = null;
       PointPair pointToAdd = null;

       // Loop until we run out of line.

       while (listTraveller.hasNext()) {
           a_splittableLine = (SplittableLineSegment) listTraveller.next();     // Get the SplittableLineSegment from the list.
           IndivisibleLineSegment a_indivibleLine = a_splittableLine.getLine(); // Get the actual line from the object.
           PointPair first_point = a_indivibleLine.getPoint(0);                 // Get the first point only.

           // Add the first point to the output line o_out_line list.

           pointToAdd = new PointPair(first_point.getLatitude(),first_point.getLongitude());
           o_out_line.add(pointToAdd);
      }

      // Add the last point to the output line o_out_line list.

      pointToAdd = new PointPair(lastPoint.getLatitude(),lastPoint.getLongitude());
      o_out_line.add(pointToAdd);

      return o_out_line;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public void dumpOutputLine(ArrayList<SplittableLineSegment> i_lineList_items) {
       // Debug function to dump the line to stdout
       ArrayList<PointPair> out_line = computeOutputLine(i_lineList_items);

       System.out.print("Out line: ");

       for (int index = 0; index < out_line.size(); index++) {
           PointPair a_point = out_line.get(index);
           System.out.print(String.format("(%.3f %.3f) ",a_point.getLatitude(),a_point.getLongitude()));
       }

       System.out.println("");
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public String getLineAsLongString(ArrayList<PointPair> i_line) {
       // Given an array of point pairs, return the line as one long string.  Useful in debugging and unit test.
       StringBuilder a_line_builder = new StringBuilder();

       for (int index = 0; index < i_line.size(); index++) {
           PointPair a_point = i_line.get(index);
           a_line_builder.append(String.format("(%.3f %.3f) ",a_point.getLatitude(),a_point.getLongitude()));
       }

       return a_line_builder.toString();
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public String getLineAsLongStringWithoutParenthesis(ArrayList<PointPair> i_line) {
       // Given an array of point pairs, return the line as one long string.  Useful in debugging and unit test.
       // We chosen to have 10 places after the decimals to match the precision from the granule file itself.
       StringBuilder a_line_builder = new StringBuilder();

       for (int index = 0; index < i_line.size(); index++) {
           PointPair a_point = i_line.get(index);
           a_line_builder.append(String.format("%.10f %.10f ",a_point.getLatitude(),a_point.getLongitude()));
       }

       return a_line_builder.toString();
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public String kmlOutputLine(ArrayList<SplittableLineSegment> i_lineList_items, String i_placemarkName) {
       // Debug function to dump the line as a KML polygon.  This assumes that
       // the line that you're simplifying is a polygon with a first and last
       // point being the same.

       ArrayList<PointPair> out_line = computeOutputLine(i_lineList_items);
       String r_String = this._createKMFromPointPair(out_line,i_placemarkName);
       return r_String;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public void create_KML_andAddToOutputLine(ArrayList<PointPair> i_pointpairs, String i_placemarkName) {
        String r_String = this._createKMFromPointPair(i_pointpairs,i_placemarkName);
        this._appendToKLM_String(r_String);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private String _createKMFromPointPair(ArrayList<PointPair> i_pointpairs, String i_placemarkName) {

       PointPair a_point = null;
       StringBuilder a_kml_poly_builder = new StringBuilder();

       // Add the first few parts of the KML body.

       a_kml_poly_builder.append(KML_POLY_PART_1);
       a_kml_poly_builder.append(i_placemarkName);

       a_kml_poly_builder.append(KML_POLY_PART_2);
       // Add the beginning LinearRing tag.

       // coordinates are in comma separated triplets of lat lon height (where height is the m_KML_HEIGHT variable):
       // 
       //    name = <string>
       //    coordlist = "lat1, lon1, h1, lat2, lon2, h2, ... latn, lonn, hn"

       int index = 0;
       while (index < i_pointpairs.size()) {

           a_point = i_pointpairs.get(index);  // Get a point from the line.

           // For each element in coordlist, we add the appropriate tag:
           //
           //                         <LinearRing>
           //                                 <coordinates>%(coordlist)s</coordinates>
           //                         </LinearRing>

//           coordlist.add(new String((Double.toString(a_point.getLongitude()) + ", " + Double.toString(a_point.getLatitude()) + "," + Double.toString(m_KML_HEIGHT))));
//System.out.println("kmlAppendToOutputLine:INFO: Adding [" + index + "] " + (new String((Double.toString(a_point.getLongitude()) + ", " + Double.toString(a_point.getLatitude()) + ", " + Integer.toString(m_KML_HEIGHT)))));
//           coordlist.add(new String("</coordinates>\n"));

             // Do not add the comma if this is the first item. 
             if (index == 0) {
                 //a_kml_poly_builder.append(new String((Double.toString(a_point.getLongitude()) + ", " + Double.toString(a_point.getLatitude()) + ", " + Integer.toString(m_KML_HEIGHT))));
                 a_kml_poly_builder.append(String.format("%.6f, %.6f, %d",a_point.getLongitude(),a_point.getLatitude(),m_KML_HEIGHT));
             } else {
                 //a_kml_poly_builder.append(new String(", " + (Double.toString(a_point.getLongitude()) + ", " + Double.toString(a_point.getLatitude()) + ", " + Integer.toString(m_KML_HEIGHT))));
                 a_kml_poly_builder.append(String.format(", %.6f, %.6f, %d",a_point.getLongitude(),a_point.getLatitude(),m_KML_HEIGHT));
             }

           index++;
       }

//System.out.println("kmlAppendToOutputLine:INFO: num points added = [" + (index+1) + "]");

       // Add the closing LinearRing tag.
//       coordlist.add(new String("</LinearRing>\n"));

       // Add the last part of the KML body.

       a_kml_poly_builder.append(KML_POLY_PART_3);

       return (a_kml_poly_builder.toString());
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public double eucDistPt2LinSq(PointPair i_lp1, PointPair i_lp2, PointPair i_p) {
     // See distSq computation in: http://ryba4.com/python/ramer-douglas-peucker

//     System.out.println("eucDistPt2LinSq:INFO, i_lp1 [" + i_lp1.toString() + "]");
//     System.out.println("eucDistPt2LinSq:INFO, i_lp2 [" + i_lp2.toString() + "]");
//     System.out.println("eucDistPt2LinSq:INFO, i_p   [" + i_p.toString()   + "]");

//     System.out.println("eucDistPt2LinSq:INFO, calculating PointPair a");

     PointPair a = new PointPair(i_p.getLatitude() - i_lp1.getLatitude(),
                                 i_p.getLongitude() - i_lp1.getLongitude());

////     System.out.println("eucDistPt2LinSq:INFO, a [" + a.toString() + "]");
//     System.out.println("eucDistPt2LinSq:INFO, calculating PointPair b");

     PointPair b = new PointPair(i_lp2.getLatitude() - i_lp1.getLatitude(),
                                 i_lp2.getLongitude() - i_lp1.getLongitude());

//     System.out.println("eucDistPt2LinSq:INFO, b [" + b.toString() + "]");

     // Degenerate case where the line is actually zero length.

     //if (b.equals(0.0,0.0)) {
     if (b.equals(new PointPair(0.0,0.0))) {
        System.out.println("b is zero");
        m_num_b_equals_to_zero++;
        return (Math.pow(a.getLatitude(),2) + Math.pow(a.getLongitude(),2));
     }

//     System.out.println("a.getLatitude() [" + a.getLatitude() + "]");
//     System.out.println("a.getLongitude() [" + a.getLongitude() + "]");
//     System.out.println("b.getLatitude() [" + b.getLatitude() + "]");

System.out.println("PolyTools::eucDistPt2LinSq: The sum of squared latitude and longitude = " + ((Math.pow(b.getLatitude(),2) + Math.pow(b.getLongitude(),2)) <= 0.0));
if ((Math.pow(b.getLatitude(),2) + Math.pow(b.getLongitude(),2)) <= 0.0) {
   _logger.warn("The sum of squared latitude and longitude are zero.  b.getLatitude() = " + b.getLatitude() + ", b.getLongitude() = " + b.getLongitude());
} 
//     System.out.println("b.getLongitude() [" + b.getLongitude() + "]");

     return ( (Math.pow(a.getLatitude(),2) + Math.pow(a.getLongitude(),2)) - ( (a.getLatitude() * b.getLatitude() + a.getLongitude() * b.getLongitude())   /
                                                                (Math.pow(b.getLatitude(),2) + Math.pow(b.getLongitude(),2)) 
                                                              )
            );
   }

   public int getNumPointsBIsZeroed() {
       return m_num_b_equals_to_zero;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private void _verifyFirstAndPastPointsAreSame(IndivisibleLineSegment i_line)
   {
       // Do a sanity check to make sure we have a complete circle, where the first point is same as the last point.

       // First, check for null-ness.

       if (i_line == null) {
           System.err.println("PolyTools:_verifyFirstAndPastPointsAreSame:ERROR, Variable i_line null.");
           System.err.println("Cannot continue.  Program exiting to give user a chance to correct input.");
           _logger.error("PolyTools:_verifyFirstAndPastPointsAreSame:ERROR, Variable i_line null.");
           System.exit(1);
       }

       PointPair firstPoint = i_line.getPoint(0);
       PointPair lastPoint  = i_line.getPoint(i_line.getLineLength()-1);

//       PointPair lastPoint  = new PointPair(new Double(77),new Double(88));

       // Exit if the points do not match.

       if (!(firstPoint.equals(lastPoint))) {
           System.err.println("PolyTools:_verifyFirstAndPastPointsAreSame:ERROR, First point and last point should be the same.  They are not. First point[" + firstPoint.toString() + "], last point [" + lastPoint.toString() + "]");
           System.err.println("Cannot continue.  Program exiting to give user a chance to correct input.");
           _logger.error("PolyTools:_verifyFirstAndPastPointsAreSame:ERROR, First point and last point should be the same.  They are not. First point[" + firstPoint.toString() + "], last point [" + lastPoint.toString() + "]");
           System.exit(1);
       }
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public ArrayList<PointPair> ramerDouglasStough(IndivisibleLineSegment        i_line, 
                                                  double                        i_dist_thresh,
                                                  int                           i_max_points,
                                                  PolyTools.POLY_ALGORITHM_TYPE i_distFn,
                                                  boolean                       i_kmlOutputFlag, 
                                                  boolean                       i_dbgFlag)
   {
      // This function implements the Ramer-Douglas-Peucker algorithm as
      // described in:
      //
      //  http://ryba4.com/python/ramer-douglas-peucker
      //

      // With a twist...  First, all points are assumed to be (lat, lon) and
      // all distances are computed on the great circle.  Next, we take a
      // line, distance threshold, AND max number of points in the resulting
      // reduced line.  Rather than a recursive implementation, this is a
      // greedy algorithm which keeps a list of line segments to operate on.
      // It chooses the line segment with the greatest deviant point to
      // divide next.  If there are no segments exceeding the threshold, we
      // complete.  If our output line will have the max number of points, we
      // complete.  With unlimited points, it should be the same as the
      // original algorithm.  With limited points, it should essentially end
      // up setting the dist thresh to the appropriate value.

      if (i_kmlOutputFlag) {
          // TODO: Not sure if a new StringBuilder is to be created here or at top.
      }

      // Do a sanity check to make sure the first and last points are the same.  If not, exit and give the user a chance to correct the input.
      _verifyFirstAndPastPointsAreSame(i_line);

      //  We're using a KeyQueuedList of the split point and line segment
      //  keyed off of the deviation and queued to return max distance.

      KeyQueuedList lineList = new KeyQueuedList();

      if (lineList == null) {
          System.out.println("ramerDouglasStough:lineList is null.");
          return null;
      }

      // Initialize the list with the whole line.  Note: items are made up of
      // a tuple containing the max_index (split point) and the line.

      Tuple splitPoint = this._findLineSplitPoint(i_line,i_distFn,"Initial",i_dbgFlag);

      if (i_dbgFlag) {
      System.out.println("i_line.getLineLength  [" + i_line.getLineLength() + "]");
      System.out.println("getNumPointsBIsZeroed [" + this.getNumPointsBIsZeroed() + "]");
      System.out.println("ramerDouglasStough:INFO, splitPoint.getValue() [" + splitPoint.getValue() + "]");
      System.out.println("ramerDouglasStough:INFO, splitPoint.getIndex() [" + splitPoint.getIndex() + "]");
      }

      SplittableLineSegment item = new SplittableLineSegment(splitPoint.getIndex(), i_line);
//if (2 == 2) return (r_ret);
  
      lineList.insertItem(0, splitPoint.getValue(), item, i_dbgFlag);

      // If our max deviation is > the threshold and we have fewer than
      // max_points, keep working.  If the poly-line is irreducable then we
      // will end up with a list of two point segments which all have the
      // deviance of 0.

      int iteration = 0;

//System.out.println("  max key: " + lineList.maxKey());
//System.exit(1);
      boolean done_with_loop = false; // Provide a way to exit the loop if cannot pop a key.
      if (i_dbgFlag) {
System.out.println("  max key: " + lineList.maxKey());
System.out.println(">>>>>>>> Outside while loop <<<<<<<<<<");
      }

      while (((lineList.maxKey() > i_dist_thresh) && ((lineList.getLength() + 1) < i_max_points)) &&
             (done_with_loop == false)) {
          if (i_dbgFlag) {
            System.out.println("  max key: " + lineList.maxKey());
            System.out.println("list dump: " + lineList);
            this.dumpOutputLine(lineList.getItems());
          }
//System.out.println("  >>>>>>>> Inside while loop <<<<<<<<<<");
//System.out.println("    >> iteration " + iteration);

          // Pop the top key/item and unpack the item into idx (split point)
          // and line.

//done_with_loop = true;
          LineTuple a_tuple = lineList.popKey();

          if (a_tuple == null) {
              System.out.println("while: a_tuple is null, continue.");
              System.out.println("while: Cannot popKey.");
              done_with_loop = true;
              continue;
          }

//System.out.println("while: a_tuple: [" + a_tuple + "]");

          SplittableLineSegment line_item = a_tuple.getLine();
          int idx = line_item.getSplitIdx();
          IndivisibleLineSegment line = line_item.getLine();

//System.out.println("while: idx : [" + idx + "]");
//System.out.println("while: line: [" + line + "]");
//System.out.println("while: line.getLineLength() [" + line.getLineLength() + "]");

          // We're splitting this line into two segments, line_a and line_b.

          ArrayList<IndivisibleLineSegment> line_pair = line_item.splitLine();

//System.out.println("while: line_pair.size() [" + line_pair.size() + "]");
//System.out.println("while: line_pair[0].getLineLength() [" + line_pair.get(0).getLineLength() + "]");
//System.out.println("while: line_pair[1].getLineLength() [" + line_pair.get(1).getLineLength() + "]");

          // For each a and b, find the max point and its index.  Then, insert
          // each segment back into the list at the same place we popped.

//System.out.println("while: pre left_tuple = this._findLineSplitPoint");

          Tuple left_tuple = this._findLineSplitPoint(line_pair.get(0),i_distFn,"Iteration " + iteration + " for left_tuple",i_dbgFlag);

//System.out.println("while: post left_tuple = this._findLineSplitPoint");

          item = new SplittableLineSegment(left_tuple.getIndex(), line_pair.get(0));

          int a_idx = a_tuple.getIndex();

//System.out.println("while: a_idx : [" + a_idx + "]");
//System.out.println("while: calling insertItem with left_tuple");

          lineList.insertItem(a_idx, left_tuple.getValue(), item, i_dbgFlag);

          if (i_dbgFlag) {
              System.out.println("  line_a: " + item.toString());
              _logger.debug("  line_a: " + item.toString());
          }

          // Note that the first point in b is the same as the last point in
          // a.  Line segment b gets inserted at key_idx + 1 so it will be next
          // to a.

//System.out.println("while: pre right_tuple = this._findLineSplitPoint");

          Tuple right_tuple = this._findLineSplitPoint(line_pair.get(1),i_distFn,"Iteration " + iteration + " for right_tuple",i_dbgFlag);

//System.out.println("while: post right_tuple = this._findLineSplitPoint");

          item = new SplittableLineSegment(right_tuple.getIndex(), line_pair.get(1));

          int b_idx = a_tuple.getIndex() + 1;

//System.out.println("while: b_idx : [" + b_idx + "]");
//System.out.println("while: calling insertItem with right_tuple");

          lineList.insertItem(b_idx, right_tuple.getValue(), item, i_dbgFlag);

          if (i_dbgFlag) {
              System.out.println("  line_b: " + item.toString());
              _logger.debug("  line_b: " + item.toString());
          }

          if (i_kmlOutputFlag) {
//              if (iteration == 0) {
                  this._buildPolygonString(lineList.getItems(),"Iteration " + Integer.toString(iteration));
//              }
          }

          iteration = iteration + 1;
      }

      if (i_dbgFlag) {
          System.out.println("  iteration: " + iteration);
          _logger.debug("  iteration: " + iteration);
      }

      // Once we've completed the iteration, the final line is extracted by
      // taking the first point of every segment and the last point of the
      // last segment.  This works because the last point in each segment is
      // the first in the next.

      if (i_dbgFlag) {
          this.dumpOutputLine(lineList.getItems());
      }

      ArrayList<PointPair> out_line = computeOutputLine(lineList.getItems());

      // If we happen to output a line of max points notify the user of the
      // effective distance threshold.

      if (out_line.size() == i_max_points) {
          System.out.println("NOTICE: max_points reached, effective dist threshold: " + lineList.maxKey());
          _logger.info("NOTICE: max_points reached, effective dist threshold: " + lineList.maxKey());
      }
  
      // System.out.println(KML_HEADER);
      // System.out.println(this.getPolygonString());
      // System.out.println(KML_FOOTER);
      //kmlDocToFile("output.kml");

      return out_line;
   }

   public void kmlDocToFile(String i_outputFilename)
   {
       boolean fileWriteStatus = FileUtility.writeAll(i_outputFilename,(KML_HEADER + this.getPolygonString() + KML_FOOTER));
   }

   private void _buildPolygonString(ArrayList<SplittableLineSegment> i_items, String i_placemarkName)
   {
       m_kml_poly_builder.append(this.kmlOutputLine(i_items, i_placemarkName));
   }

   private void _appendToKLM_String(String i_stringToAdd)
   {
       m_kml_poly_builder.append(i_stringToAdd);
   }

   public String getPolygonString()
   {
       return (m_kml_poly_builder.toString());
   }

   public IndivisibleLineSegment loadLineFromFile(String i_fname) {
   
      // Load a poly-line from a single line file of space-separated lat/lon
      // pairs.

      // For operation, this won't be necessary since the lat/lon pair will be read from the granule data file.

      StringBuffer rawLines = null;

      try {
          BufferedReader in_reader = new BufferedReader(new FileReader(i_fname));
          String one_line;
          rawLines = new StringBuffer();
          // For each line, read it and append to rawLines object.
          while ((one_line = in_reader.readLine()) != null) {
              rawLines.append(one_line);
          }
          in_reader.close();
      } catch (IOException an_exception) {
          an_exception.printStackTrace();
          _logger.error("An IOException while opening or reading input file " + i_fname);
      }

      // We now have the entire poly-line in memory, we parse the tokens into lat and lon pairs.

      String[] rawCoords = rawLines.toString().split(" ");

      // Print a few elements for verification only if we have at least 10.

      if (rawCoords.length > 10) {
          if (2 == 3) { //TODO: For now, we comment this out.
          System.out.println("rawCoords[0] [" +  rawCoords[0] + "]");
          System.out.println("rawCoords[1] [" +  rawCoords[1] + "]");
          System.out.println("rawCoords[2] [" +  rawCoords[2] + "]");
          System.out.println("rawCoords[3] [" +  rawCoords[3] + "]");
          System.out.println("rawCoords[4] [" +  rawCoords[4] + "]");
          System.out.println("rawCoords[5] [" +  rawCoords[5] + "]");
          System.out.println("rawCoords[6] [" +  rawCoords[6] + "]");
          System.out.println("rawCoords[7] [" +  rawCoords[7] + "]");
          System.out.println("rawCoords[n-2] [" +  rawCoords[rawCoords.length-2] + "]");
          System.out.println("rawCoords[n-1] [" +  rawCoords[rawCoords.length-1] + "]");
          }
      } else {
          System.err.println("PolyTools:loadLineFromFile:ERROR, File " + i_fname + " does not contain enough points.  Function loadLineFromFile() returning null.");
          _logger.error("File " + i_fname + " does not contain enough points.  Function loadLineFromFile() returning null.");
          return null;
      }

      int index = 0;

      // Build the lat/lon pair from what was read from the poly-line file.

      ArrayList<PointPair> listLatLon = new ArrayList<PointPair>((int) (rawCoords.length / 2));

      while (index < rawCoords.length) {

          // First before accessing the 2nd element, do a sanity check to make sure we won't be reading past the rawCoords array
          // because we are extracting 2 elements at a time for each iteration in this while loop.

          if ((index+1) == rawCoords.length) {
              System.err.println("PolyTools:loadLineFromFile:ERROR, Cannot read past end of rawCoords array with index: " + (index+1) + ".  The array only contain " + rawCoords.length + " elements.  Program exiting.");
              System.exit(1);
          }

          // Get two sequential numbers and make a PointPair out of them.
          // Note: We use new PointPair because the array listLatLon requires it.

          listLatLon.add(new PointPair(new Double(Double.valueOf(rawCoords[index])),
                                       new Double(Double.valueOf(rawCoords[index+1]))
                                      ) 
                        );

          index = index + 2;  // We increment by 2 since the previous statement extracted 2 elements from rawCoords array.
      }

      // Print a few elements for verification only if we have at least 10.
      if (listLatLon.size() > 10) {
          if (2 == 3) { //TODO: For now, we comment this out.
          System.out.println("listLatLon[0] [x:" +  listLatLon.get(0).getLatitude() + "],y:[" + listLatLon.get(0).getLongitude() + "]");
          System.out.println("listLatLon[1] [x:" +  listLatLon.get(1).getLatitude() + "],y:[" + listLatLon.get(1).getLongitude() + "]");
          System.out.println("listLatLon[2] [x:" +  listLatLon.get(2).getLatitude() + "],y:[" + listLatLon.get(2).getLongitude() + "]");
          System.out.println("listLatLon[n-2] [x:" +  listLatLon.get(listLatLon.size()-2).getLatitude() + "],y:[" + listLatLon.get(listLatLon.size()-2).getLongitude() + "]");
          System.out.println("listLatLon[n-1] [x:" +  listLatLon.get(listLatLon.size()-1).getLatitude() + "],y:[" + listLatLon.get(listLatLon.size()-1).getLongitude() + "]");
          }
      }

      IndivisibleLineSegment o_lineSegment = new IndivisibleLineSegment(listLatLon);

      return o_lineSegment;
   }

   // Templates for KML Generation
   //
   // Because Java does not allow multi-line constants, we concatenate each line together with \n at the end of the line.
   // Whenever a '"' is required, we escape it with '\' character, otherwise, the content of this header and footer are identical to
   // the Python code.

   private static String KML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
"<Document>\n" +
"        <name>KmlFile</name>\n" +
"        <StyleMap id=\"msn_ylw-pushpin\">\n" +
"                <Pair>\n" +
"                        <key>normal</key>\n" +
"                        <styleUrl>#sn_ylw-pushpin</styleUrl>\n" +
"                </Pair>\n" +
"                <Pair>\n" +
"                        <key>highlight</key>\n" +
"                        <styleUrl>#sh_ylw-pushpin</styleUrl>\n" +
"                </Pair>\n" +
"        </StyleMap>\n" +
"        <Style id=\"sh_ylw-pushpin\">\n" +
"                <IconStyle>\n" +
"                        <scale>1.3</scale>\n" +
"                        <Icon>\n" +
"                                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\n" +
"                        </Icon>\n" +
"                        <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\n" +
"                </IconStyle>\n" +
"                <LineStyle>\n" +
"                        <color>ff1410ff</color>\n" +
"                        <width>2</width>\n" +
"                </LineStyle>\n" +
"                <PolyStyle>\n" +
"                        <fill>0</fill>\n" +
"                </PolyStyle>\n" +
"        </Style>\n" +
"        <Style id=\"sn_ylw-pushpin\">\n" +
"                <IconStyle>\n" +
"                        <scale>1.1</scale>\n" +
"                        <Icon>\n" +
"                                <href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\n" +
"                        </Icon>\n" +
"                        <hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\n" +
"                </IconStyle>\n" +
"                <LineStyle>\n" +
"                        <color>ff1410ff</color>\n" +
"                        <width>2</width>\n" +
"                </LineStyle>\n" +
"                <PolyStyle>\n" +
"                        <fill>0</fill>\n" +
"                </PolyStyle>\n" +
"        </Style>";

    private static String KML_FOOTER = "</Document>\n" +
                                  "</kml>";

}
