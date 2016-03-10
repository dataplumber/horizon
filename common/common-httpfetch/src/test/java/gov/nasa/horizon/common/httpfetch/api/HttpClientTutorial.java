package gov.nasa.horizon.common.httpfetch.api;

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Jun 10, 2010
 * Time: 1:31:19 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This example demonstrates the recommended way of using API to make sure
 * the underlying connection gets released back to the connection manager.
 */
public class HttpClientTutorial {

   public List<String> urls = new ArrayList<String>();

   public List<String> getURLs(HttpClient httpclient, String path, String regex) throws Exception {

      HttpGet httpget = new HttpGet(path);
      System.out.println("executing request " + httpget.getURI());
      HttpResponse response = httpclient.execute(httpget);

      System.out.println("----------------------------------------");
      System.out.println(response.getStatusLine());
      System.out.println("----------------------------------------");

      // Get hold of the response entity
      HttpEntity entity = response.getEntity();

      // If the response does not enclose an entity, there is no need
      // to bother about connection release
      if (entity != null) {
         BufferedReader reader = new BufferedReader(
               new InputStreamReader(entity.getContent()));
         String buf;
         try {
            Pattern p = Pattern.compile(regex);

            while ((buf = reader.readLine()) != null) {
               Matcher m = p.matcher(buf);
               if (m.find()) {
                  urls.add(m.group(1));
               }
               //result += buf;
            }
         } catch (IOException ex) {

            // In case of an IOException the connection will be released
            // back to the connection manager automatically
            throw ex;

         } catch (RuntimeException ex) {

            // In case of an unexpected exception you may want to abort
            // the HTTP request in order to shut down the underlying
            // connection and release it back to the connection manager.
            httpget.abort();
            throw ex;

         } finally {

            // Closing the input stream will trigger connection release
            reader.close();
            entity.consumeContent();

         }
      }

         return urls;
   }

   public void printInfo (HttpClient httpclient, String path) throws Exception {
      //HttpClient httpclient = new DefaultHttpClient();

      HttpHead httphead = new HttpHead(path);
      System.out.println("executing request " + httphead.getURI());
      HttpResponse response = httpclient.execute(httphead);
      System.out.println ("Content-Length: " + response.getFirstHeader("Content-Length").getValue());
      System.out.println ("Last-Modified: " + response.getFirstHeader("Last-Modified").getValue());
      //httpclient.getConnectionManager().shutdown();
   }

   public static void main(String[] args) throws Exception {
      HttpClient httpclient = new DefaultHttpClient();

      HttpClientTutorial tutorial = new HttpClientTutorial();

      for (int i=100; i<=160; ++i) {
         String path = String.format ("http://oceandata.sci.gsfc.nasa.gov/MODISA/L3SMI/2010/%03d/", i);
         List<String> result = tutorial.getURLs(httpclient, path, "(http://.+/(A|T)(\\d{4})(\\d{3})\\d*\\.L3m_(DAY|8D|MO|YR)_N{0,1}SST4_(4|9)\\.bz2)");
      }

      for (String url : tutorial.urls) {
         tutorial.printInfo (httpclient, url);
      }

      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      httpclient.getConnectionManager().shutdown();
   }

}
