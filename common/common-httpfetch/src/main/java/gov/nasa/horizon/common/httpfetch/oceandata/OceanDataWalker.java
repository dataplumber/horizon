/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.httpfetch.oceandata;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.ChecksumUtility;
import gov.nasa.horizon.common.httpfetch.api.FetchException;
import gov.nasa.horizon.common.httpfetch.api.HttpFetcher;
import gov.nasa.horizon.common.httpfetch.api.HttpFileHandler;
import gov.nasa.horizon.common.httpfetch.api.HttpFileProduct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.HttpProcessor;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is designed to work with the http://oceandata.sci.gsfc.nasa.gov site.
 * It provides methods to retrieve file URLs from this provider site.  The method
 * on how the files will be stored is handled by the HttpFileHandler.
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class OceanDataWalker {
   private static Log _logger = LogFactory.getLog(OceanDataWalker.class);

   private final ExecutorService _pool;
   private final HttpFetcher _fetcher;
   private Future<Integer> _thread;
   private int m_timeoutsDefault = 20;

   /**
    * Constructor to create a single service thread in the background for processing
    * HTML results.
    */
   public OceanDataWalker() {
      this._pool = Executors.newFixedThreadPool(1);
      this._fetcher = new HttpFetcher();
   }

   public void setTimeout(int i_timeoutsDefault) {
      this._fetcher.setTimeout(i_timeoutsDefault);
      m_timeoutsDefault = i_timeoutsDefault; // Save default so it can be re-used.
   }

   /**
    * The method to perform HTTP Walk.  This is a non-blocking method, that is, the
    * input walk request is immediately queue to the background service thread.  This
    * allows the calling program to perform other operations while results are being
    * processed and files are being downloads.
    *
    * @param rootURL the starting top-level URL
    * @param regex   the regular expression to filter embedded URLs.  If this parameter is NULL, then all URLs will be returned.
    * @param handler the HTTP file handler.  This handler will be called when an URL that fits the specified regular expression is found.
    */
   public void walk(final String rootURL, final String regex, final HttpFileHandler handler) {

      final Pattern filePattern;
      if (regex != null && regex.length() > 0) {
         filePattern = Pattern.compile(regex);
      } else {
         filePattern = null;
      }

      // create the callable object to be executed within the Thread.
      Callable<Integer> callable = new Callable<Integer>() {

         private int _count = 0;

         public Map<String, String> processChecksum(String url) {
            Map<String, String> result = new HashMap<String, String>();
            try {
               String content = _fetcher.getTextContent(url);
               String[] lines = content.split("\n");
               for (int i = 0; i < lines.length; ++i) {
                  String[] tokens = lines[i].trim().split("\\s+");
                  if (tokens.length == 2) {
                     // each checksum line format: <checksum> <filename>
                     result.put(tokens[1].trim(), tokens[0].trim());
                  }
               }
            } catch (FetchException e) {

            }
            return result;
         }

         private String _getNameOnly(String i_fullPathname) {
            // Given a full path name of a file, returns the name only.

            String o_nameOnly = "";
            File a_file = new File(i_fullPathname);
            o_nameOnly = a_file.getName();
            return (o_nameOnly);
         }

         private String _getDirectoryOnly(String i_fullPathname) {
            // Given a full path name of a file, returns the directory name only.
            // Note: The file does not have to exist.

            String o_directoryOnly = "";
            o_directoryOnly = i_fullPathname.substring(0, i_fullPathname.lastIndexOf("/"));
            return (o_directoryOnly);
         }

         /**
          * This is a recursive method to process through the oceandata site until URLs
          * that fit the specified regular expression is found.
          *
          * @param rootURL the top-level URL to start retrieve HTML from
          * @return the number of URLs found that fit the specified regular epression
          */
         public int traverse(String rootURL) {
            try {
               String content = _fetcher.getTextContent(rootURL);
               Map<String, String> checksum = null;
               if (content != null && content.length() > 0) {
                  /**
                   * looking for checksum file
                   */
                  Pattern csp = Pattern.compile("SHA1 Checksums");
                  Matcher csm = csp.matcher(content);
                  if (csm.find()) {
                     checksum = this.processChecksum(rootURL + "/checksum.txt");

                     // If cannot find checksum.txt, we look for an alternative checksum file.
                     //
                     // If the URI was:
                     //  
                     //  http://oceandata.sci.gsfc.nasa.gov/Recent/Aquarius/L3m/Aquarius_L3m_20120507_Added.html
                     //
                     // then the checksum name is Aquarius_L3m_20120507_Added.cksum
                     //
                     //  http://oceandata.sci.gsfc.nasa.gov/Recent/Aquarius/L3m/Aquarius_L3m_20120507_Added.cksum
                     //
                     // Care must be taken to get the right parent directory to append the new name "Aquarius_L3m_20120507_Added.cksum"

                     if (checksum.size() == 0) {
                        String nameOnly = _getNameOnly(rootURL);
                        String nameWithoutExtension = nameOnly.substring(0, nameOnly.lastIndexOf("."));
                        String alternateChecksumFile = (_getDirectoryOnly(rootURL) + "/" + nameWithoutExtension + ".cksum");
                        checksum = this.processChecksum(alternateChecksumFile);
                     }

                     if (OceanDataWalker._logger.isDebugEnabled()) {
                        OceanDataWalker._logger.debug("Checksum Dump");
                        Iterator it = checksum.entrySet().iterator();
                        while (it.hasNext()) {
                           Map.Entry pairs = (Map.Entry) it.next();
                           OceanDataWalker._logger.debug(pairs.getKey() + " = " + pairs.getValue());
                        }
                     }
                  }
                  /**
                   * The oceandata site HTML pages have all the data URL in a table.  Prior to the table, it is the word
                   * 'Size' and following the table is the word 'Page'
                   */
                  Pattern p = Pattern.compile("Size([\\w|\\W]*<a href=\"([\\w|\\W]+)\">[\\w|\\W]*)+Page");
                  Matcher m = p.matcher(content);
                  if (m.find()) {
                     String block = m.group(0);
                     /**
                      * Now grabe the embedded URLs from the previously extracted HTML block
                      */
                     p = Pattern.compile("<a href=\"([\\w|\\W]+)\">");
                     for (String line : block.split("\n")) {
                        OceanDataWalker._logger.debug("Parsing line: " + line);
                        Matcher m1 = p.matcher(line);
                        if (m1.find()) {
                           OceanDataWalker._logger.debug("match: " + m1.group(1));
                           /**
                            * The oceandata site uses relative link for YEAR..DOY, almost like relative
                            * directory.  The operation below looks for a match that is starting with
                            * the prefix 'http://'.  This site uses 'http://' link for data files only.
                            */
                           if (!m1.group(1).startsWith("http://")) {
                              // if it is not starting with 'http://', then there is another level
                              // to walk.  This is to make the recursive call.
                              this.traverse(rootURL + "/" + m1.group(1));
                           } else {
                              /**
                               * If it is starting with 'http://', then check to see if the URL fits the
                               * input regular expression.
                               */
                              String tmpUrl = m1.group(1);
                              Matcher fileMatcher = null;
                              if (filePattern != null) {
                                 fileMatcher = filePattern.matcher(tmpUrl);
                              }
                              if (fileMatcher == null || fileMatcher.find()) {
                                 ++this._count;
                                 if (handler != null) {
                                    /**
                                     * If a valid URL is found and a HttpFileHandler is provided, then
                                     * dispatch the handler for file download and/or cache update.
                                     */
                                    HttpFileProduct hfp = _fetcher
                                          .createFileProduct(tmpUrl);
                                    hfp.setDigestAlgorithm(ChecksumUtility
                                          .DigestAlgorithm.SHA1);
                                    hfp.setDigestValue(checksum.get(hfp.getName()));
                                    Set<FileProduct> fp = new HashSet<>();
                                    fp.add(hfp);
                                    handler.onProducts(fp);
                                 } else {
                                    OceanDataWalker._logger.info("Found: " + tmpUrl);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } catch (FetchException e) {
               OceanDataWalker._logger.debug(e.getMessage(), e);
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
      this._thread = this._pool.submit(callable);
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
         result = this._thread.get();
      } catch (InterruptedException e) {
         OceanDataWalker._logger.debug(e.getMessage(), e);
      } catch (ExecutionException e) {
         OceanDataWalker._logger.debug(e.getMessage(), e);
      }
      return result;
   }

   /**
    * Method to clear all memory resources allocated by this walker.
    */
   public void shutdown() {
      if (this._fetcher != null) {
         this._fetcher.shutdown();
      }
   }

}
