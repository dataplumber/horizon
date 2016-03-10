package gov.nasa.horizon.common.api.serviceprofile;

public interface SPLine extends Accessor {

   /**
    * Method to create an endpoint
    * 
    * @param latitude the latitude value
    * @param longitude the longitude value
    * @return
    */
   SPPoint createPoint(double latitude, double longitude);

   /**
    * Method to get the ending point of a line
    * 
    * @return the ending point
    */
   SPPoint getEndPoint();

   /**
    * Method to get the starting point of a line
    * 
    * @return the starting point
    */
   SPPoint getStartPoint();

   /**
    * Method to set the end points of a line
    * 
    * @param startPoint the starting point
    * @param endPoint the end point
    */
   void setEndPoints(SPPoint startPoint, SPPoint endPoint);
   
}
