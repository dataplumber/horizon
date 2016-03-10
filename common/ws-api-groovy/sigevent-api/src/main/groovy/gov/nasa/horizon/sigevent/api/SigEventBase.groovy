/**
 * 
 */
package gov.nasa.horizon.sigevent.api

import org.apache.commons.httpclient.*
import org.apache.commons.httpclient.methods.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author axt
 *
 */
public abstract class SigEventBase { 
   private static Log logger = LogFactory.getLog(SigEventBase.class)

   private static final int SOCKET_TIME_OUT = 1000 * 60 * 5
   private static final int BUFFER_SIZE = 10240
   private static final String FORMAT_NAME = "format"
   private static final String FORMAT_VALUE = ResponseFormat.Xml.toString().toLowerCase()
   private String baseUrl
   private HttpClient httpClient
   private int socketTimeOut
   
   public SigEventBase(String baseUrl) {
      this(baseUrl, SigEventBase.SOCKET_TIME_OUT)
   }
   
   public SigEventBase(String baseUrl, int socketTimeOut) {
      this.baseUrl = baseUrl
      
      int timeOut = SigEventBase.SOCKET_TIME_OUT
      if(socketTimeOut >= 0) {
         timeOut = socketTimeOut
      }
      this.socketTimeOut = timeOut
      
      httpClient = new HttpClient()
   }
   
   protected Response getResponse(String url, Map parameters) {
      Map newParameters = new TreeMap(parameters)
      newParameters.put(FORMAT_NAME, FORMAT_VALUE)
      
      Response response = new Response()
      try {
         String responseString = getRawResponse(url, newParameters)
         logger.debug "raw: ${responseString}"
         
         def xml = new XmlParser().parseText(responseString)
         response.content = responseString
         if(xml.Type.text() != "OK") {
            response.error = xml.Content.text()
         }
      } catch(Exception exception) {
         logger.debug (exception.message,  exception)
         response.error = exception.toString()+": "+exception.message
      }
      
      return response
   }
   
   protected String getRawResponse(String url, Map parameters) throws Exception {
      logger.debug "go: ${url}"
      def postMethod = new PostMethod(url)
      postMethod.getParams().setParameter("http.socket.timeout", new Integer(socketTimeOut));
      parameters.each {key, value ->
         if(value != null) {
            postMethod.addParameter(key, value.toString())
            logger.debug "adding: ${key}=${value.toString()}"
         }
      }
      
      String response = ""
      InputStream inputStream = null
      InputStreamReader isr = null
      try {
         logger.debug "execute"
         int status = httpClient.executeMethod(postMethod)
         if(status != HttpStatus.SC_OK) {
            throw new Exception("Failed: "+postMethod.getStatusLine())
         }
         
         logger.debug "response"
         inputStream = postMethod.getResponseBodyAsStream()
         isr = new InputStreamReader(inputStream)
         
         int charsRead = 0
         char[] buffer = new char[BUFFER_SIZE]
         while((charsRead = isr.read(buffer, 0, buffer.length)) != -1) {
            response += new String(buffer, 0, charsRead)
         }
         //response = postMethod.getResponseBodyAsString()
      } catch(Exception exception) {
         throw exception
      } finally {
         try {
            if(isr) {
               isr.close()
            }
            if(inputStream) {
               inputStream.close()
            }
         } catch(Exception exception) {}
         
         postMethod.releaseConnection()
      }
      
      return response
   }
   
   protected String createApiUrl(String uri) {
      String url = baseUrl
      if(!url.endsWith("/")) {
         url += "/"
      }
      url += uri
      
      return url
   }
}
