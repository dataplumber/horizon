package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.common.api.ssl.SSLFactory

import java.io.IOException
import java.net.URLConnection

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class Post {
   private static Log log = LogFactory.getLog(Post.class)

   String url
   URLConnection connection
   Writer writer
   String text
   String body

   String getText() {
      String result = ""
      try {
         result = getUncheckedText()
      } catch (Exception e) {
         log.trace(e.message, e)
      }
      return result
   }

   String getUncheckedText() throws IOException {
      def thisUrl = new URL(url)
      String result = ""
      try {
         connection = thisUrl.openConnection()
         if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(SSLFactory.getCustomSSLFactory().getSocketFactory())
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
               public boolean verify(String hostname, SSLSession session) {
                  return true
               }
            })
         }
         connection.setRequestMethod("POST")
         connection.setRequestProperty("Content-Type", "application/xml")
         connection.doOutput = true

         writer = new OutputStreamWriter(connection.outputStream)
         writer.write(body)
         writer.flush()
         connection.connect()
         result = connection.content.text
         log.trace("result = ${result}")
      } finally {
         if (writer) {
            writer.close()
         }
      }
      return result
   }

   String toString() {
      return "POST:\n" + url + "\n" + body
   }
}
