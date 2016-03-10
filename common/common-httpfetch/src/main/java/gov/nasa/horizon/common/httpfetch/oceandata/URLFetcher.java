package gov.nasa.horizon.common.httpfetch.oceandata;

import gov.nasa.horizon.common.httpfetch.api.FetchException;
import gov.nasa.horizon.common.httpfetch.api.HttpFetcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Jun 14, 2010
 * Time: 12:57:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class URLFetcher {

   public static Log _logger = LogFactory.getLog(URLFetcher.class);

   private HttpFetcher fetcher = new HttpFetcher();

   List<String> getURLs(String rootURL) {
      List<String> result = new ArrayList<String>();
      //HttpFetcher fetcher = new HttpFetcher();
      try {
         String text = fetcher.getTextContent(rootURL);
         if (text != null && text.length() > 0) {
            Pattern p = Pattern.compile("Size([\\w|\\W]*<a href=\"([\\w|\\W]+)\">[\\w|\\W]*)+Page");
            Matcher m = p.matcher(text);
            if (m.find()) {
               String block = m.group(0);
               p = Pattern.compile("<a href=\"([\\w|\\W]+)\">");
               for (String line : block.split("\n")) {
                  URLFetcher._logger.debug("Parsing line: " + line);
                  Matcher m1 = p.matcher(line);
                  if (m1.find()) {
                     URLFetcher._logger.debug("match: " + m1.group(1));
                     if (!m1.group(1).startsWith("http://")) {
                        result.add(rootURL + "/" + m1.group(1));
                     } else {
                        result.add(m1.group(1));
                     }
                  }
               }
            }
         }

      } catch (FetchException e) {
         URLFetcher._logger.error(e.getMessage(), e);
      }
      //finally {
      //   fetcher.shutdown();
      //}

      return result;
   }
}
