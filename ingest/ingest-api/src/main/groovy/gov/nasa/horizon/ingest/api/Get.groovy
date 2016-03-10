package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.QueryString
import java.net.URLConnection

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Aug 4, 2008
 * Time: 4:42:19 PM
 * To change this template use File | Settings | File Templates.
 */



class Get{
   String url
   QueryString queryString = new QueryString()
   URLConnection connection
   String text

   String getText() {
      def thisUrl = new URL(this.toString())
      connection = thisUrl.openConnection()
      if(connection.responseCode == 200) {
         return connection.content.text
      } else {
         return "Something bad happened\n" +
         "URL: " + this.toString() + "\n" +
         connection.responseCode + ": " +
         connection.responseMessage
      }
   }

   String toString() {
      return url + "?" + queryString.toString()
   }
}
