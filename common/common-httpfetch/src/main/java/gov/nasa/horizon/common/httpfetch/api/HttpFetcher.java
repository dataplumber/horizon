/*****************************************************************************
 * Copyright (c) 2010-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.httpfetch.api;

import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm;
import gov.nasa.horizon.common.api.util.DateTimeUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the HTTP fetch class.  It provide utility methods to retrieve data from
 * remote HTTP links.
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class HttpFetcher {

   private static Log _logger = LogFactory.getLog(HttpFetcher.class);

   private HttpClient _httpclient = null;
   private boolean _shared = false;
   private static int DEFAULT_TIMEOUT = 20;  // in seconds

   /**
    * Default constructor to initialize the internal HTTP client
    */
   public HttpFetcher() {
      this._httpclient = new DefaultHttpClient();
      this.setTimeout(HttpFetcher.DEFAULT_TIMEOUT);
   }

   /**
    * Constructor to create default HTTP client and user-specified timeout
    * setting
    *
    * @param timeout in seconds
    */
   public HttpFetcher(int timeout) {
      this._httpclient = new DefaultHttpClient();
      this.setTimeout(HttpFetcher.DEFAULT_TIMEOUT);
   }

   /**
    * Constructor to create a fetcher instance that uses a shared HTTP
    * client object instance.
    *
    * @param httpclient the HTTP client object instance to be used
    */
   public HttpFetcher(HttpClient httpclient) {
      this._httpclient = httpclient;
      this._shared = true;
      this.setTimeout(HttpFetcher.DEFAULT_TIMEOUT);
   }

   /**
    * Constructor to create a fetcher instance that uses a shared HTTP
    * client object instance.
    *
    * @param httpclient the HTTP client object instance to be used
    * @param timeout    the user-specified timeout value
    */
   public HttpFetcher(HttpClient httpclient, int timeout) {
      this._httpclient = httpclient;
      this._shared = true;
      this.setTimeout(timeout);
   }

   /**
    * This method allows the user to set the time-out parameters to
    * smaller values so when the system hangs, the connections/sockets
    * will be closed after a smaller time, thus allow the crawler to
    * continue to do its job. See
    * http://blog.jayway.com/2009/03/17/configuring-timeout-with-apache-httpclient-40/
    *
    * @param timeout input default timeout value in seconds.
    */
   public void setTimeout(int timeout) {
      HttpParams httpParams = this._httpclient.getParams();
      HttpConnectionParams.setConnectionTimeout(httpParams,
            timeout * 1000);
      HttpConnectionParams.setSoTimeout(httpParams, timeout * 1000);
   }

   /**
    * Method to retrieve text contents from the specified URL
    *
    * @param url the remote URL link
    * @return the text from the remote URL link
    * @throws FetchException when errors occurs
    */
   public String getTextContent(String url) throws FetchException {
      StringBuilder content = new StringBuilder();

      HttpGet httpget = new HttpGet(url);
      HttpFetcher._logger.debug("executing request " + httpget.getURI());

      try {
         HttpResponse response = this._httpclient.execute(httpget);

         HttpFetcher._logger
               .debug("\n----------------------------------------\n"
                     + response.getStatusLine()
                     + "\n----------------------------------------");

         HttpEntity entity = response.getEntity();
         if (entity != null) {
            try {
               BufferedReader reader = new BufferedReader(new InputStreamReader(
                     entity.getContent()));
               char[] cbuf = new char[4092];
               int charsRead;
               while ((charsRead = reader.read(cbuf, 0, 4092)) > -1) {
                  content.append(cbuf, 0, charsRead);
               }
            } catch (IOException ex) {
               HttpFetcher._logger.debug(ex);
               throw new FetchException(ex.getMessage(), ex);
            } catch (RuntimeException ex) {
               httpget.abort();
               throw new FetchException(ex.getMessage(), ex);
            }
         }
      } catch (IOException ex) {
         HttpFetcher._logger.debug(ex);
      }

      return content.toString();
   }

   /**
    * Method to retrieve text content from the input URL and apply the input
    * regular expression to find any substrings that matches.
    *
    * @param url   the remote URL link
    * @param regex the regular expression to apply to the retrieved text content
    * @return the list of strings that matches the regular expression
    * @throws FetchException when an error is occurred
    */
   public List<String> getFilteredText(String url, String regex)
         throws FetchException {
      List<String> result = new ArrayList<>();
      String html = this.getTextContent(url);
      Pattern p = Pattern.compile(regex);
      for (String line : html.split("\n")) {
         Matcher m = p.matcher(line);
         if (m.find()) {
            result.add(m.group(1));
         }
      }
      return result;
   }

   /**
    * Create a file product object to represent a file to be retrieved through
    * HTTP protocol.  The object will be populated with metadata retrieved
    * from the HTTP header
    *
    * @param url the remote URL
    * @return the File Product object
    * @throws FetchException when an error is occurred
    * @see gov.nasa.horizon.common.api.file.FileProduct
    */
   public HttpFileProduct createFileProduct(String url) throws FetchException {
      HttpFileProduct result;
      HttpHead httphead = new HttpHead(url);

      try {
         HttpResponse response = this._httpclient.execute(httphead);
         Header modHeader = response.getFirstHeader("Last-Modified");
         Header sizeHeader = response.getFirstHeader(
               "Content-Length");
         Date modified = new Date(0);
         if (modHeader != null) {
            modified = DateTimeUtility
                  .parseDate("EEE, dd MMM yyyy hh:mm:ss zzz",
                        modHeader.getValue());
         } else {
            HttpFetcher._logger.debug(
                  "Missing 'Last-Modified' from HTTP response for " + url);
         }

         // To simulate bad header, set date to zero
         //modified = new Date(0);
         long size = 0;
         if (sizeHeader != null) {
            size = Long.parseLong(sizeHeader.getValue());
         } else {
            HttpFetcher._logger.debug(
                  "Missing 'Content-Length' from HTTP response for " + url);
         }

         // To simulate bad header, set size to zero.
         //size = 0;
         result = new HttpFileProduct(this._httpclient, url, url.substring(
               url.lastIndexOf("/") + 1, url.length()), modified.getTime(),
               size);
      } catch (IOException ex) {
         throw new FetchException(ex.getMessage(), ex);
      }
      return result;
   }

   /**
    * Create a file product object to represent a file to be retrieved through
    * HTTP protocol.  The object will be populated with metadata retrieved
    * from the HTTP header
    *
    * @param url             the file URL
    * @param digestAlgorithm the message digest algorithm to be used
    * @param digestValue     the optional pre-computed message digest value
    * @return a file product object reference
    * @throws FetchException if unable to access the remote file or invalid
    *                        HTTP header value.
    */
   public HttpFileProduct createFileProduct(String url,
                                            DigestAlgorithm digestAlgorithm,
                                            String digestValue)
         throws FetchException {
      HttpFileProduct result = this.createFileProduct(url);
      result.setDigestAlgorithm(digestAlgorithm);
      result.setDigestValue(digestValue);
      return result;
   }

   /**
    * Method to shutdown the http client and free any memory resources
    */
   public void shutdown() {
      if (!this._shared && this._httpclient != null) {
         this._httpclient.getConnectionManager().shutdown();
      }
   }

   /**
    * Method to access to the internal HTTP client object reference.
    *
    * @return the internal HTTP client object reference
    */
   public HttpClient getHttpClient() {
      return this._httpclient;
   }
}
