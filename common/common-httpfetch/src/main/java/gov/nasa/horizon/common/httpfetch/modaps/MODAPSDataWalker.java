package gov.nasa.horizon.common.httpfetch.modaps;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.file.FileProductHandler;
import gov.nasa.horizon.common.httpfetch.api.FetchException;
import gov.nasa.horizon.common.httpfetch.api.HttpFetcher;
import gov.nasa.horizon.common.httpfetch.api.HttpFileProduct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the MODAP site crawler to detect new files to download
 *
 * @author T. Huang
 * @version $Id $
 */
public class MODAPSDataWalker extends TimerTask {
   private static Log _logger = LogFactory.getLog(MODAPSDataWalker.class);

   private final ExecutorService pool;
   private final HttpFetcher fetcher;
   private Future<Integer> thread;
   private FileProductHandler handler;
   private List<String> fileExpressions;
   private Iterator<String> urls;
   private Boolean stop =false;

   // timeout value in seconds
   private int timeout = 20;

   /**
    * Constructor to create a single service thread in the background
    * for processing HTML results.
    */
   public MODAPSDataWalker() {
      this.pool = Executors.newFixedThreadPool(1);
      this.fetcher = new HttpFetcher();
      this.setTimeout(this.timeout);

   }

   public MODAPSDataWalker(Iterator<String> urls, List<String> fileExpressions,
                           FileProductHandler handler) {
      this();
      this.setURLs(urls);
      this.setFileExpressions(fileExpressions);
      this.setFileProductHandler(handler);
   }

   public void setURLs(Iterator<String> urls) {
      this.urls = urls;
   }

   public void setFileProductHandler(FileProductHandler handler) {
      this.handler = handler;
   }

   public void setFileExpressions(List<String> fileExpressions) {
      this.fileExpressions = fileExpressions;
   }

   public synchronized void setStop(Boolean stop) {
      this.stop = stop;
   }

   @Override
   public void run() {
      while (!this.stop && this.urls.hasNext()) {
         String url = this.urls.next();
         this.walk(url, this.fileExpressions, this.handler);
         if (this.stop) {
            this.cancel();
            break;
         }
         int stat = this.checkResult();
         MODAPSDataWalker._logger.debug(stat + " URLs found.");
      }
   }

   public void setTimeout(int timeout) {
      this.fetcher.setTimeout(timeout);
      this.timeout = timeout;
   }

   /**
    * The method to perform HTTP Walk.  This is a non-blocking method,
    * that is, the input walk request is immediately queue to the background
    * service thread.  This allows the calling program to perform other
    * operations while results are being processed and files are being
    * downloads.
    *
    * @param rootURL     the starting top-level URL
    * @param expressions the regular expression map <regex,
    *                    file type> to filter
    *                    embedded URLs.  If this parameter is NULL,
    *                    then all URLs will be returned.
    * @param handler     the HTTP file handler.  This handler will be called when
    *                    an URL that fits the specified regular expression is found.
    */
   public void walk(final String rootURL, final List<String> expressions,
                    final FileProductHandler
                          handler) {
      final List<Pattern> patterns = new Vector<>();
      for (String regex : expressions) {
         patterns.add(Pattern.compile(regex));
      }

      // create the callable object to be executed within the Thread.
      Callable<Integer> callable = new Callable<Integer>() {

         private int _count = 0;

         /**
          * This is a recursive method to process through the modap site until
          * URLs that fit the specified regular expression is found.
          *
          * @param rootURL the top-level URL to start retrieve HTML from
          * @return the number of URLs found
          */
         public synchronized int traverse(String rootURL) {
            try {
               String content = fetcher.getTextContent(rootURL);
               if (content != null && content.length() > 0) {
                  Set<FileProduct> files = new HashSet<>();

                  for (String line : content.split("\n")) {
                     // trying to parse HTML line
                     // <a href="RRGlobal_r02c00.2013222.aqua.1km.jpg">RRGlobal_r02c00.2013222.aqua.1km.jpg</a>           11-Aug-2013 01:24  132K
                     // To extract file link and file modification time.
                     Pattern p = Pattern.compile("<a href=\"([\\w|\\W]+)" +
                           "\">[\\w|\\W]+</a>\\s+(\\d{2}-\\w{3}-\\d{4})\\s+(\\d{2}:\\d{2})");
                     Matcher m = p.matcher(line);
                     Date modified = null;
                     if (m.find()) {
                        String fileRef = null;
                        for (Pattern rp : patterns) {
                           Matcher rm = rp.matcher(m.group(1));
                           if (rm.find()) {
                              fileRef = rm.group(0);
                              SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                              // Since MODAPS is in East Coast,
                              // we should force the time to reflect the timezone.
                              sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
                              String datestring = m.group(2) + " " + m.group(3);
                              try {
                                 modified = sdf.parse(datestring);
                              } catch (ParseException ex) {
                                 MODAPSDataWalker._logger.error("Invalid date string encountered: " + datestring);
                              }
                              break;
                           }
                        }
                        if (fileRef != null) {
                           MODAPSDataWalker._logger.debug("Match: " + fileRef);
                           try {
                              // checking to see if we already have a valid URL
                              new URL(fileRef);
                           } catch (MalformedURLException e) {
                              fileRef = rootURL + "/" + fileRef;
                           }
                           try {
                              HttpFileProduct hfp = fetcher.createFileProduct
                                    (fileRef);
                              if (modified != null) {
                                 hfp.setLastModifiedTime(modified.getTime());
                              }
                              files.add(hfp);
                              MODAPSDataWalker._logger.debug("Found: " + fileRef);
                           } catch (FetchException e) {
                              MODAPSDataWalker._logger.debug(e.getMessage(), e);
                           }
                           ++this._count;
                        }
                     }
                  }
                  if (files.size() > 0) {
                     if (handler != null) {
                        handler.onProducts(files);
                        files.clear();
                        handler.postprocess();
                     }
                  }
               }
            } catch (FetchException e) {
               MODAPSDataWalker._logger.debug(e.getMessage(), e);
            }
            return this._count;
         }

         /**
          * The is the hook method call by the Executor to start the thread.
          *
          * @return return the number of URLs found.
          * @throws Exception
          */
         public Integer call() throws Exception {
            return this.traverse(rootURL);
         }

      };

      // dispatch the callable to a thread.
      this.thread = this.pool.submit(callable);
   }

   /**
    * Method to wait for the working thread to finish and return the number of
    * URLs found.
    *
    * @return the number of URLs found or -1 if an error is encountered.
    */
   public int checkResult() {
      int result = -1;
      try {
         result = this.thread.get();
      } catch (InterruptedException | ExecutionException e) {
         if (this.thread == null)
            MODAPSDataWalker._logger.debug("this.thread is null");
         MODAPSDataWalker._logger.debug(e.getMessage(), e);
      }
      return result;
   }

   /**
    * Method to clear all memory resources allocated by this walker.
    */
   public void shutdown() {
      if (this.fetcher != null) {
         this.fetcher.shutdown();
      }
   }
}
