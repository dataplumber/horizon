package gov.nasa.horizon.common.httpfetch.oceandata;

import gov.nasa.horizon.common.api.util.FileUtility;
import junit.framework.TestCase;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;


import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Jun 25, 2010
 * Time: 8:26:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientTest extends TestCase {

   public void testDownload() {

      //String url = "http://oceandata.sci.gsfc.nasa.gov/cgi/getfile/A20101602010162.L3m_3D_SST_9.bz2";
      //String filename = "A20100012010003.L3m_3D_SST4_9.bz2";
      String url = "http://oceandata.sci.gsfc.nasa.gov/cgi/getfile/Q20070012007008.L3m_8D_scat_wind_speed_1deg.bz2";
      String filename = "Q20070012007008.L3m_8D_scat_wind_speed_1deg.bz2";

      for (int i = 0; i < 10; ++i) {
         /*
         // Create and initialize HTTP parameters
         HttpParams params = new BasicHttpParams();
         ConnManagerParams.setMaxTotalConnections(params, 1);
         HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

         // Create and initialize scheme registry
         SchemeRegistry schemeRegistry = new SchemeRegistry();
         schemeRegistry.register(
                 new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

         // Create an HttpClient with the ThreadSafeClientConnManager.
         // This connection manager must be used if more than one thread will
         // be using the HttpClient.
         ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
         HttpClient httpclient = new DefaultHttpClient(cm, params);
         */

         HttpClient httpclient = new DefaultHttpClient();

         HttpHead httphead = new HttpHead(url);
         httphead.setHeader("Connection", "close");
         InputStream is = null;
         OutputStream os = null;
         HttpResponse resp = null;
         try {
            HttpResponse response = httpclient.execute(httphead);
            Header modHeader = response.getFirstHeader("Last-Modified");
            Header sizeHeader = response.getFirstHeader(
                  "Content-Length");
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Connection", "close");
            resp = httpclient.execute(httpget);
            is = resp.getEntity().getContent();
            os = new FileOutputStream(new File("/tmp/" + filename));
            FileUtility.downloadFromStream(is, os);
            System.out.println("Done writing file: " + "/tmp/" + filename);
            resp.getEntity().consumeContent();
         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            try {
               if (is != null) is.close();
               if (os != null) os.close();
               if (resp != null) resp.getEntity().consumeContent();
            } catch (IOException e) {
            }
            //cm.shutdown();
            httpclient.getConnectionManager().shutdown();
         }
      }

   }
}
