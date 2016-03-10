package gov.nasa.horizon.common.httpfetch.oceandata;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.FileProductUtility;
import gov.nasa.horizon.common.crawler.spider.CrawlerException;
import gov.nasa.horizon.common.crawler.spider.registry.CrawlerRegistry;
import gov.nasa.horizon.common.crawler.spider.registry.provider.FileCrawlerRegistry;
import gov.nasa.horizon.common.httpfetch.api.FetchException;
import gov.nasa.horizon.common.httpfetch.api.HttpFetcher;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModisDataFetcher {

   public static Log _logger = LogFactory.getLog(ModisDataFetcher.class);
   private String version = "2.2.0";
   private Set<String> urls = new HashSet<String>();
   private String __configFile;
   private HttpFetcher fetcher = new HttpFetcher();
   private Calendar begin = null;
   private Set<FileProduct> cacheList = new HashSet<FileProduct>();
   private String _stateFile = null;
   private String downloadDir = null;
   Options options = new Options();

   public ModisDataFetcher() {
      _logger.info("Initialized log.");
   }

   public void addCrawlableUrl(String s) {
      urls.add(s);
   }


   public void traverse(String rootURL) {
      List<String> result = new ArrayList<String>();
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
                        //result.add(rootURL + "/" + m1.group(1));
                        traverse(rootURL + "/" + m1.group(1));
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
      for (String res : result) {
         _logger.info("Processing link: [" + res + "];");
         processFileProduct(res, rootURL);

      }
      //Save the product registry each time we complete a subsection of the URL
      _saveProductsToRegistry(cacheList, _stateFile);
   }

   public void processFileProduct(String url, String parent) {
      FileProduct fp = null;
      try {
         fp = fetcher.createFileProduct(url);
      } catch (FetchException e) {
         // TODO Auto-generated catch block
      }
      if (fp == null) {
         _logger.info("Error retrieving file. Skipping file.");
         return;
      }

      long fpLmt = fp.getLastModifiedTime();
//			String fpName = fp.getName();
//			long fpSize = fp.getSize();

      // the last modified time is older than the start time specified
      if (begin != null && fpLmt < begin.getTimeInMillis()) {
         _logger.info("File [" + fp.getName() + "] is older than start time specified, skipping.");
         try {
            fp.close();
         } catch (IOException e) {
         }
         return;
      }

      //We can simply do an add and check the return value, is this what we want though?
      //If we add and it fails, we don't get the file. What if we add and then fail to get the file? do we then remove?
      //How do we add the old file back to it?
      //We'll leave it this way for now.
      if (checkRegistry(fp)) {
         //System.out.println("File not in regristry, getting file.");
         //getFile
         if (saveFile(fp, parent)) {
            _logger.info("File [" + url + "] sucessfully saved.");
            cacheList.add(fp);
         }
      } else {
         System.out.println(parent);
         _logger.info("Forgoing file [" + fp.getName() + "]. Already Downloaded.");
      }

      try {
         fp.close();
      } catch (IOException e) {
      }

   }

   private static String removeRootURI(String full) {

      String paths[] = full.split("http://oceandata.sci.gsfc.nasa.gov/");
      if (paths.length > 1) return paths[1];
      return paths[0];
   }

   private boolean saveFile(FileProduct fp, String url) {


      String path = removeRootURI(url);
      FileProductUtility vfs = new FileProductUtility(downloadDir + File.separator
            + path + File.separator + fp.getName());

      //System.out.println(downloadDir + File.separator
      //       + path + File.separator + fp.getName());
      try {
         vfs.writeFile(fp.getInputStream());
         fp.close();

      } catch (IOException e) {
         _logger.info("Could not write to file " + downloadDir + "/" + path);

         return false;
         // e.printStackTrace();
      } catch (Exception e) {
         // could not write file...
         _logger.info("Could not write to file " + downloadDir + "/" + path);
         return false;
      } finally {

         vfs.cleanUp();
      }

      return true;
   }

   private boolean checkRegistry(FileProduct fp) {
      if (cacheList.contains(fp))
         return false;
      else
         return true;
   }

   public void run() {

      for (String rootURL : urls) {

         _logger.info("Crawling url: " + rootURL);
         cacheList = _restoreProductsFromRegistry(_stateFile);
         traverse(rootURL);
         //_saveProductsToRegistry(cacheList, _stateFile);
      }
      _logger.info("Finished crawling urls listed in " + __configFile);
      _logger.info("Exiting...");
      fetcher.shutdown();
   }

   /**
    * @param args
    */
   public static void main(String[] args) {
      ModisDataFetcher mdf = new ModisDataFetcher();
      mdf.loadOptions();
      try {
         mdf.parseArgs(args);
      } catch (ParseException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      mdf.loadConfig();
      mdf.run();

   }

   private void loadConfig() {
      //open file
      File cfg = new File(__configFile);
      if (!cfg.exists()) {
         System.out.println("Error: The config file (-c) specified does not exist.");
         printHelp();
         System.exit(0);
      }
      //exists
      FileReader fr = null;
      try {
         fr = new FileReader(cfg);
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      BufferedReader br = new BufferedReader(fr);
      String _input;
      try {
         while ((_input = br.readLine()) != null) {
            addCrawlableUrl(_input);
         }

         br.close();
         if (fr != null)
            fr.close();

      } catch (IOException ioe) {
         System.out.println("Error: The config file (-c) specified could not be read.");
         printHelp();
         System.exit(0);
      }


   }

   public void printHelp() {
//		String _userScript = "modisHTTPCrawler.sh";
//	    String USAGE_HEADER = "modisHTTPCrawler";
      HelpFormatter formatter = new HelpFormatter();
      // System.out.println();

      formatter.printHelp("modisHTTPCrawler.sh", "modisHTTPCrawler", options, null, true);
   }

   public void loadOptions() {
      options.addOption("sd", "start-date", true, "A start date which no files older than will be downloaded. Format: MM/dd/yyy");
      options.addOption("c", "config", true, "Config file of URLS to check.");
      options.addOption("h", "help", false, "Print the help information for this file.");
      options.addOption("v", "version", false, "Print the version info for this tool and exit.");
      options.addOption("s", "statefile", true, "Full path to the cachefile.");
      options.addOption("d", "download-dir", true, "Full path to the download directory.");
   }

   public int parseArgs(String[] args) throws ParseException {
      int stat = 0;
      CommandLineParser parser = new BasicParser();
      CommandLine commandLine = parser.parse(options, args);

      if (commandLine.hasOption('h')) {
         printHelp();
         System.exit(0);
      }

      if (commandLine.hasOption("sd")) {
         //use the date supplied by the caller
         String date = commandLine.getOptionValue("sd");
         String pattern = "MM/dd/yyyy";
         SimpleDateFormat format = new SimpleDateFormat(pattern);
         Date d = null;
         try {
            d = format.parse(date);
         } catch (java.text.ParseException e) {
            System.out.println("The wrong date format was used. Should be of the format MM/dd/yyyy for example, 04/09/2010");
            printHelp();
            System.exit(0);
         }
         Calendar tempCal = Calendar.getInstance();
         tempCal.setTime(d);
         TimeZone gmt = TimeZone.getTimeZone("GMT");
         begin = Calendar.getInstance();
         begin.setTimeZone(gmt);
         begin.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE), 0, 0, 0);
         begin.set(Calendar.MILLISECOND, 0);

      }


      if (commandLine.hasOption('v')) {
         System.out.println();
         System.out.println("ModisDataFetcher version " + version);
         System.out.println();
         System.exit(0);
      }

      this._stateFile = this.__configFile = commandLine.getOptionValue('s');
      this.downloadDir = this.__configFile = commandLine.getOptionValue('d');

      this.__configFile = commandLine.getOptionValue('c');
      if (__configFile == null) {
         System.out.println("Error: A config file must be specified to use this tool.");
         printHelp();
         System.exit(0);
      }

      if (this.downloadDir == null) {
         System.out.println("Error: A download directory (-d) must be specified.");
         printHelp();
         System.exit(0);
      }
      File fp = new File(downloadDir);
      if (!fp.exists()) {
         System.out.println("Error: The download directory specified does not exist.");
         printHelp();
         System.exit(0);
      }

      if (this._stateFile == null) {
         System.out.println("Error: A statefile (-s) must be specified.");
         printHelp();
         System.exit(0);
      }

      return stat;
   }

   private static Set<FileProduct> _restoreProductsFromRegistry(
         String i_directoryStateFilename) {
      Set<FileProduct> r_products = new HashSet<FileProduct>();

      try {
         if (new File(i_directoryStateFilename).exists()) {
            CrawlerRegistry registry = new FileCrawlerRegistry(
                  i_directoryStateFilename);
            r_products = registry.restore();
         }
         //TODO ELSE throw error
      } catch (CrawlerException an_exception) {
         _logger.error(an_exception.getMessage());
         if (_logger.isDebugEnabled()) an_exception.printStackTrace();
      }
      return (r_products);
   }

   private static void _saveProductsToRegistry(Set<FileProduct> i_products,
                                               String i_directoryStateFilename) {
      try {
         CrawlerRegistry registry = new FileCrawlerRegistry(
               i_directoryStateFilename);
         registry.save(i_products);
      } catch (CrawlerException an_exception) {
         _logger.error(an_exception.getMessage());
         if (_logger.isDebugEnabled()) an_exception.printStackTrace();
      }
   }

}
