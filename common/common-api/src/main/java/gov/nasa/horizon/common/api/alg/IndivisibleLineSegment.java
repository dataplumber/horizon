/***************************************************************************
*
* Copyright 2010, by the California Institute of Technology. ALL
* RIGHTS RESERVED. United States Government Sponsorship acknowledged.
* Any commercial use must be negotiated with the Office of Technology
* Transfer at the California Institute of Technology.
*
* @version $Id: IndivisibleLineSegment.java 6159 2010-11-05 20:34:26Z qchau $
*
****************************************************************************/


package gov.nasa.horizon.common.api.alg;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndivisibleLineSegment {
   // This class encapsulate what a line segment looks like: an array of PointPair objects.
   //
   // Notes:
   //
   //   1. The m_ prefix signifies private variables. 
   //   2. The i_ prefix signifies input variables.
   //   3. The r_ or o_ prefixes signifies output variables.

       private static Log _logger = LogFactory.getLog(IndivisibleLineSegment.class);

       private ArrayList<PointPair> m_line = new ArrayList<PointPair>(0);

       public IndivisibleLineSegment() {
       }

       public int getLineLength() {
           return m_line.size();
       }

       public ArrayList<PointPair> getLine() {
           return m_line;
       }

       public PointPair getPoint(int i_index) {
           // Returns a PointPair object given an index.  With this, we can abstract how a line is implemented.
           if (m_line != null) {
               return m_line.get(i_index);
           } else {
               return null;
           }
       }

       public IndivisibleLineSegment(ArrayList<PointPair> i_line) {
           // Instantiate a IndivisibleLineSegment object with an array of PointPair objects. 
           m_line = i_line; 
       }

       public String toString() {
           return ("DUMMY");
       }

       public ArrayList<PointPair> splitLine(int i_startIndex, int i_endIndex) {
           // Extract a slice of points from the line in this object given a range of indices.

//System.out.println("IndivisibleLineSegment:splitLine:INFO, i_startIndex [" + i_startIndex + "]");
//System.out.println("IndivisibleLineSegment:splitLine:INFO, i_endIndex   [" + i_endIndex + "]");
//System.out.println("IndivisibleLineSegment:splitLine:INFO, m_line.size() [" + m_line.size() + "]");

           ArrayList<PointPair> o_portion = new ArrayList<PointPair>(0); 

//System.out.println("IndivisibleLineSegment:splitLine:INFO, pre m_line.subList()");
//           List<PointPair> a_portion = m_line.subList(i_startIndex,i_endIndex); 
//System.out.println("IndivisibleLineSegment:splitLine:INFO, post m_line.subList()");
//

           if (m_line == null) {
System.out.println("IndivisibleLineSegment:WARN, m_line is null");
               return o_portion;
           }

           int sliceIndex = i_startIndex;

           while ((sliceIndex < getLineLength()) &&
                  (sliceIndex <= i_endIndex)         ) {
//System.out.println("add: " + m_line.get(sliceIndex));
               // Get the PointPair object from m_line.
               PointPair t_point = (m_line.get(sliceIndex));

               // Use PointPair object to build a new PointPair object so we can append it to the line portion to return.
               o_portion.add(new PointPair(t_point.getLatitude(),t_point.getLongitude()));
               sliceIndex++;
           } 

//System.out.println("IndivisibleLineSegment:INFO, o_portion [" + a_portion + "]");
//System.out.println("IndivisibleLineSegment:INFO, o_portion.size() [" + a_portion.size() + "]");
//System.out.println("IndivisibleLineSegment:INFO, o_portion [" + o_portion + "]");

//System.out.println("IndivisibleLineSegment:INFO, o_portion.size() [" + o_portion.size() + "]");

           return (o_portion);
       }
}
