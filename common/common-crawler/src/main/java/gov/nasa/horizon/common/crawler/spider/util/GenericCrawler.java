package gov.nasa.horizon.common.crawler.spider.util;

import gov.nasa.horizon.common.api.file.FileFilter;
import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.ChecksumUtility;
import gov.nasa.horizon.common.api.util.DateTimeUtility;
import gov.nasa.horizon.common.api.util.Errno;
import gov.nasa.horizon.common.api.util.FileProductUtility;
import gov.nasa.horizon.common.api.util.SystemProcess;
import gov.nasa.horizon.common.crawler.spider.CrawlerException;
import gov.nasa.horizon.common.crawler.spider.CrawlerManager;
import gov.nasa.horizon.common.crawler.spider.provider.ProcessFileProduct;
import gov.nasa.horizon.common.crawler.spider.provider.VFSCrawler;
import gov.nasa.horizon.common.crawler.spider.provider.VFSCrawlerManager;
import gov.nasa.horizon.common.crawler.spider.registry.CrawlerRegistry;
import gov.nasa.horizon.common.crawler.spider.registry.provider.FileCrawlerRegistry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class GenericCrawler implements ProcessFileProduct
{
   static Options m_options;
   static CommandLine m_cmdLine = null;
   static boolean m_help = false;
   static boolean m_recursive = false;
   static String[] m_args;
   static int m_frequency = 0; // how often do we run the crawler
   static int count = 0; // how many files were found
   static String m_home = null;
   static String m_command = null;
   static String m_name = "genericCrawler";
   static String m_user = "guest";
   static String m_write_user = "guest";
   static String m_readDir = null;
   static String m_writeDir = null;
   static String m_tz = "GMT";
   static int m_cleanDays = 0;
   
   static int m_saveAfterNum = 10;
   static boolean m_passive = true;
   static Date m_startDate = null;
   static Date m_endDate = null;
   static String m_readAuth = null;
   static String m_writeAuth = null;
   static String m_pattern = null;
   static String m_stateFile = null;
   static String m_tempStorageDir = null;
   static String m_logFile = null;
   static boolean m_successListChanged = false;
   static boolean m_continuous = false;
   static boolean m_list = false;
   static boolean m_isCleaning = false;
   static CrawlerManager m_manager = null;
   static Set<FileProduct> m_pendingProductList = null;
   static Set<FileProduct> m_CleanList = null;
   static Set<FileProduct> m_successProcessedList = null;
   static Set<FileProduct> m_completedProcessedList = new HashSet<FileProduct>();
   static Log _logger = LogFactory.getLog(GenericCrawler.class);

   /**
    * @param args
    *           Program relies on System Property: user.home (must point to the
    *           directory that holds the /.ssh/id_dsa program relies on System
    *           Property: crawler.log.file program relies on System Property:
    *           crawler.read.user program relies on System Property:
    *           crawler.write.user
    * 
    * 
    *           the List option will override the clean option and the crawl
    *           option. if -l is used, no files will be copied over, and no
    *           cleaning will be done. The program will not output a statefile
    *           for the listing mode. It will use one that exists to determine
    *           files that have already been processed, so it is still a required
    *           field. When writing to or reading from file://path make sure it
    *           is absolute path, i.e. file:///abs/path/to/file (note the three
    *           slashes in beginning)
    */
   public static void main(String[] args)
   {
      m_args = args;
      // set up the parameters for the crawler
      try
      {
         _parseArgs();
         if (m_help || m_cmdLine.hasOption('h'))
         {
            _printUsage();
            return;
         }
      }
      catch (ParseException e)
      {
         _logger.error("Invalid option(s).");
         _printUsage();
         return;
      }
      _loadCmdLineOptions();

      if (m_logFile == null)
      {
         m_logFile = m_home + "/.crawler.log";
         System.out.println("No log file set for the crawler. Using "
               + m_logFile);
      }

      DailyRollingFileAppender fa = null;
      try
      {
         fa = new DailyRollingFileAppender(new org.apache.log4j.PatternLayout(
               "%d %-5p %c{1}:%L - %m%n"), m_logFile, "'.'yyyy-MM-dd");
         // fa.setDatePattern("'.'yyyy-ww"); //weekly
         fa.setDatePattern("'.'yyyy-MM-dd");// daily
         // fa.setDatePattern("'.'yyyy-MM-dd-HH-mm");//minutely
      }
      catch (IOException e1)
      {
         _logger.info(e1.getMessage());
      }
      BasicConfigurator.configure(fa);
      // Logger.getRootLogger().setLevel(Level.INFO);
      Logger.getLogger(GenericCrawler.class).setLevel(Level.INFO);
      // Logger.getLogger(GenericCrawler.class).

      _logger.info("Entering GenerateSubsystemDaemon::main()");

      _createCrawlerFile();
      // //////START CRAWLING
      do
      {

         m_manager = new VFSCrawlerManager();

         m_manager.start();
         // get's the list of previous entries on the statefile
         _logger.info("Attempting to restore statefile from " + m_stateFile);

         m_successProcessedList = _restoreProductsFromRegistry(m_stateFile);
         // set up the crawler to crawl the readdir, crawls and stores it's
         // values in m_pendingProductList
         _instantiateCrawler();

         if (m_list)
         {
            if (listCount == 0) System.out.println ("There were no new files to process from the last crawl.");
            else System.out.println ("End of Crawl Listing.");
         }
         else
         {
            if (m_completedProcessedList.size() > 0)
            {
               _logger.info("Saving products to registry");
               m_successProcessedList.addAll(m_completedProcessedList);
            }
 
            _saveProductsToRegistry(m_successProcessedList, m_stateFile);

            if (m_isCleaning) _cleanOutDatedFiles();

            _cleanUp();// cleans variables, nothing to do with the -c option
         }
 
         m_manager.stop();
         m_manager = null;

         m_continuous = _keepAliveFileExists();
         if (m_continuous && m_frequency != 0)
         {
            _logger.info("crawler is sleeping for " + m_frequency / 60000
                  + " minutes.");
            try
            {
               Thread.sleep(m_frequency);
            }
            catch (InterruptedException e)
            {
               _logger.info(e.getMessage());
            }
         }
         if (m_continuous && m_frequency != 0) m_continuous = _keepAliveFileExists(); // check
                                                                                      // after
                                                                                      // the
                                                                                      // sleep,
                                                                                      // in
                                                                                      // case
                                                                                      // the
                                                                                      // interrupt
                                                                                      // happened
                                                                                      // during
                                                                                      // that
                                                                                      // time.

      } while (m_continuous && m_frequency != 0);

      // /END CRAWLING
      _deleteKeepAliveFile();
      _logger.info("exiting");

      System.exit(0);
   }

   private static void _cleanOutDatedFiles()
   {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, m_cleanDays * -1);
      Long cleanTime = cal.getTimeInMillis();

      // create a VFS crawler to crawl the write directory for a list of all
      // files in it

      String[] l_regularExpressions = new String[1];
      l_regularExpressions[0] = new String(".*"); // all files
      FileFilter l_filter = new FileFilter(l_regularExpressions);

      VFSCrawler a_crawler = new VFSCrawler(m_writeDir);
      a_crawler.setName(m_name + ".cleaner");

      a_crawler.setAuthentication(m_write_user, null);
      a_crawler.registerProductSelector(l_filter);
      a_crawler.setRecursive(m_recursive);

      _logger.info("Fetching cleaning list...");
      m_CleanList = a_crawler.crawl();

      // go through the returned list and check to see if its modified time is
      // less than
      if (m_CleanList != null && m_CleanList.size() > 0)
      {

         // make sure the clean list only has files that we've crawled in it...
         // m_CleanList.retainAll(m_successProcessedList);

         _logger.info("Beginning cleaning process");
         Iterator<FileProduct> i = m_CleanList.iterator();
         while (i.hasNext())
         {
            FileProduct prod = (FileProduct) i.next();
            if (prod.getLastModifiedTime() < cleanTime)
            {
               _logger.info("Attempting to delete file " + prod.getName());
               if (prod.isWriteable())
               {
                  if (prod.delete())
                  {
                     _logger
                           .info("Successfully deleted file " + prod.getName());

                     // check if directory is empty
                     // only for local file systems

                     if (!prod.getFriendlyURI().startsWith("sftp")
                           && !prod.getFriendlyURI().startsWith("ftp"))
                     {

                        String directory = prod.getFriendlyURI();
                        if (directory.startsWith("file://"))
                        {
                           directory = directory.substring(7, directory
                                 .length());

                        }

                        int index = directory.indexOf(prod.getName());
                        directory = directory.substring(0, index);

                        File dir = new File(directory);
                        String children[] = dir.list();
                        if (children == null || children.length == 0)
                        {
                           if (dir.delete())
                           {
                              _logger.info("Deleted directory " + directory);
                           }
                           else _logger.info("could not delete directory "
                                 + directory);
                        }
                     }
                  }
                  else _logger.info("Unable to delete file " + prod.getName());
               }
               else _logger.info("Unable to delete file " + prod.getName()
                     + ". File is not deletable");
            }
         }
      }
   }

   private boolean _filterPendingListByDate (FileProduct prod)
   {
      _logger.info("Entering _filterPendingListByDate");
      long start = m_startDate.getTime();
      long end = 0;
      if (m_endDate == null) end = new Date().getTime();
      else end = m_endDate.getTime();
      
      boolean answer = convertTime(prod.getLastModifiedTime()) < start || end < convertTime(prod.getLastModifiedTime());
      _logger.info("start: " + start);
      _logger.info("end t: " + end);
      _logger.info("prodt: " + prod.getLastModifiedTime());
//      _logger.info("answr: " + answer);
      
      
      return answer;
   }

   private static int listCount = 0;
   private void _listFiles (FileProduct fp)
   {
	  Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	  c.setTimeInMillis(convertTime(fp.getLastModifiedTime()));
	  String tm = String.format("%1$tm/%1$te/%1$tY %1$tk:%1$tM:%1$tS %1$tZ", c);
      if (listCount == 0) System.out.println("Listing Crawled Files...");
      GenericCrawler.listCount++;
      System.out.println(GenericCrawler.listCount + ". Name: " + fp.getName() + "\n\tSize: "
               + fp.getSize() + "\n\tLast Modified Date: "
               + tm 
      );
      // run exec command on the files
      if (m_command != null)
      {
         _execForFile (fp, null);
      }
   }

   /*
    * This executes the command line process. It returns false upon an error in
    * command format as well as if the called program returns a value of 1. A
    * return value of false will prevent the File Product from being added to
    * the state file: this ensures the file will be re-crawled the next time the
    * crawler runs.
    */
   private static boolean _execForFile(FileProduct a, String writepath)
   {
      // replace the variables in the command

      String m_replCommand = new String(m_command);

      // $fileName, $FileSize, $modified, $checksum, $providerURL, $outputPath
      if (m_replCommand.contains("$fileName"))
      {
         if (a.getName() != null) m_replCommand = m_replCommand.replace(
               "$fileName", a.getName());
         else
         {
            _logger
                  .info("$fileName is null, cannot insert into command. Exiting command");
            return false;
         }
      }
      if (m_replCommand.contains("$fileSize"))
      {
         m_replCommand = m_replCommand.replace("$fileSize",
               ((Long) a.getSize()).toString());
      }
      if (m_replCommand.contains("$modified"))
      {
         m_replCommand = m_replCommand.replace("$modified", ((Long) a
               .getLastModifiedTime()).toString());
      }
      if (m_replCommand.contains("$providerURL"))
      {
         if (a.getFriendlyURI() != null) m_replCommand = m_replCommand.replace(
               "$providerURL", a.getFriendlyURI());
         else
         {
            _logger
                  .info("$providerURL is null, cannot insert into command. Exiting command");
            return false;
         }
      }
      if (m_replCommand.indexOf("$outputPath") > 0)
      {
         if (writepath != null) m_replCommand = m_replCommand.replace(
               "$outputPath", writepath);
         else
         {
            _logger
                  .info("$outputPath is null, cannot insert into command. Exiting command");
            return false;
         }
      }
      if (m_replCommand.contains("$checksum"))
      {

         String digest = null;
         File dig = new File(writepath);
         digest = ChecksumUtility.getDigest(ChecksumUtility.DigestAlgorithm
               .toAlgorithm("MD5"), dig);

         if (digest != null) m_replCommand = m_replCommand.replace("$checksum",
               digest);
         else
         {
            _logger
                  .info("$checksum is null, cannot insert into command. Exiting command");
            return false;
         }

      }
      _logger.info("Running command: " + m_replCommand);
      // exec the command
      Errno x = SystemProcess.execute(m_replCommand);
      // yeah

      /*
       * x.getId returns the id of the error returned. For our purposes we'll
       * use 0 as no error. If a 1 is returned, we'll retry the file the next
       * time we run If a 2 is returned, we'll skip the file Otherwise the
       * errors will be logged and processing will skip the file.
       */
      if (x.getId() != 0)
      {
         _logger.info("Error executing command: " + x.getMessage());
         if (x.getId() == 1)
         {
            // retry
            _logger
                  .info("Execution status returned 1. File will be retried upon next crawl.");
            return false;
         }
         if (x.getId() == 2)
         {
            // skip
            _logger
                  .info("Execution status returned 2. File will not be crawled again.");
            return true;
         }
      }

      return true;
   }

   private static void _deleteKeepAliveFile()
   {
      // delete crawler file
      File f = new File(m_home + "/.crawler." + m_name);
      // Attempt to delete it
      if (f.exists())
      {
         boolean success = f.delete();
         if (!success)
         {
            _logger
                  .info("Could not delete crawler file, please delete manually: "
                        + m_home + "/.crawler." + m_name);
         }
         else _logger.info("Keep alive file deleted.");
      }
   }

   private static boolean _keepAliveFileExists()
   {
      File crawlerFile = new File(m_home + "/.crawler." + m_name);
      if (crawlerFile.exists()) { return true; }
      _logger.info("CrawlerFile has been deleted, exiting the crawler.");
      return false;
   }

   // creates a .crawler.{name} file, delete to stop the crawler
   private static void _createCrawlerFile()
   {

      File crawlerFile = new File(m_home + "/.crawler." + m_name);
      if (crawlerFile.exists())
      {
         _logger.info("Logger lock file already exists, exiting");
         System.out
               .println("Logger File with this name already exists. Ensure no other crawlers are running with this name, delete the lockfile found at "
                     + m_home
                     + "/.crawler."
                     + m_name
                     + ", and run the command again.");
         System.exit(999);
      }
      else
      {

         try
         {
            FileWriter fw = new FileWriter(m_home + "/.crawler." + m_name);
            BufferedWriter out = new BufferedWriter(fw);
            out.write("Crawler file lock\n");
            out.flush();
            out.close();
            fw.close();
            _logger.info("Crawler file created in user's home directory.");
         }
         catch (IOException e)
         {

            _logger.info(e.toString());
            // e.printStackTrace();
            System.out
                  .println("Could not create crawler lock file, check logfile for more information. Exiting.");
            System.exit(2);
         }
      }
   }

   private static void _cleanUp()
   {

      m_completedProcessedList.clear();
      m_successListChanged = false;
      m_pendingProductList.clear();
      count = 0;
      listCount = 0;
   }

   private void _writeToDir (FileProduct a)
   {
      _logger
            .info("Number of items to process: 1");

      boolean addProd = true;
      _logger.info("File name: " + a.getName() + " File size: "
            + a.getSize() + " URI: " + a.getFriendlyURI());

      String path = removeRootURI(a.getFriendlyURI());
      _logger.info("Write path: " + m_writeDir + "/" + path);
      FileProductUtility vfs = new FileProductUtility(m_writeDir + "/"
            + path);
      vfs.setAuthentication(m_write_user, m_writeAuth);
      try
      {
    	  
    	 //can we put this ina  thread? 
         vfs.writeFile(a.getInputStream());
         a.close();

         if (m_command != null)
         {
            addProd = _execForFile(a, m_writeDir + "/" + path);
         }

      }
      catch (IOException e)
      {
         _logger.info("Could not write to file " + m_writeDir + "/" + path);
         _logger.info(e.getMessage());
         addProd = false;
         // e.printStackTrace();
      }
      catch (Exception e)
      {
         // could not write file...
         _logger.info("Could not write to file " + m_writeDir + "/" + path);
         _logger.info(e.getMessage());
         addProd = false;
      }
      finally
      {
         if (addProd) m_completedProcessedList.add(a);
         else
         {
            File fd = new File(m_writeDir + "/" + path);
            if (fd.exists())
            {
               if (fd.delete()) _logger.info("Deleted partial file because we did not successfull write it.");
            }
         }
         vfs.cleanUp();
      }
      if (addProd) _addToSuccessProcessedList(a);
   }

   private static String removeRootURI(String full)
   {

      String paths[] = full.split(m_readDir + "/");
      if (paths.length > 1) return paths[1];
      return paths[0];
   }

   private static void _instantiateCrawler()
   {
      _logger.info("Instatiate Crawler");
      String[] l_regularExpressions = new String[1];

      _logger.info("Pattern: " + m_pattern);
      if (m_pattern != null) l_regularExpressions[0] = new String(m_pattern);
      else l_regularExpressions[0] = new String(".*");

      _logger.info("URI: " + m_readDir);
      
      for(String s:l_regularExpressions){
    	  _logger.info("Pattern: " + s);
      }
      

      FileFilter l_filter = new FileFilter(l_regularExpressions);

      VFSCrawler a_crawler = new VFSCrawler(m_readDir,m_passive);
      a_crawler.setName(m_name);

      // check to see if there is an auth file for the write dir
      a_crawler.setAuthentication(m_user, m_readAuth);
      a_crawler.registerProductSelector(l_filter);
      a_crawler.setRecursive(m_recursive);
      a_crawler.registerDoHandler (new GenericCrawler());
      
      _logger.info("Crawling...");
      // Do the recursive crawling for .sip files.
      m_pendingProductList = a_crawler.crawl();
      _logger.info("Exiting _instantiateCrawler");
      a_crawler.stop();
      a_crawler = null;
   }

   private static void _loadCmdLineOptions()
   {
	   //save
	  if (m_cmdLine.hasOption("save"))
	  {
		  	try{
		  		m_saveAfterNum = Integer.valueOf(m_cmdLine.getOptionValue("save"));
		  	}
	         catch (NumberFormatException e)
	         {
	            System.out.println("Could not parse 'save-after' from value: "
	                  + m_cmdLine.getOptionValue("save") + ". Exiting.");
	            System.exit(255);
	         }
	  }
      if (m_cmdLine.hasOption("cmd"))
      {
         m_command = System.getProperty("exec.command");
      }
      if (m_cmdLine.hasOption("tz"))
      { 
    	  m_tz = m_cmdLine.getOptionValue("tz");
    	  System.out.println("Setting time zone to: " + m_tz);
      }
      if (m_cmdLine.hasOption('r'))
      {
         m_readDir = m_cmdLine.getOptionValue('r');

         if (m_readDir.endsWith("/"))
         {
            m_readDir = m_readDir.substring(0, m_readDir.length() - 1);
         }
      }
      if (m_cmdLine.hasOption('w'))
      {
         m_writeDir = m_cmdLine.getOptionValue('w');
         if (m_writeDir.endsWith("/"))
         {
            m_writeDir = m_writeDir.substring(0, m_writeDir.length() - 1);
         }
      }
      if (m_cmdLine.hasOption('p'))
      {
         m_pattern = m_cmdLine.getOptionValue('p');
         m_pattern = m_pattern.replaceAll("\\.", "\\\\\\.");
         m_pattern = m_pattern.replaceAll("#", ".*");
      }
      if (m_cmdLine.hasOption('s')) m_stateFile = m_cmdLine.getOptionValue('s');
      if (m_cmdLine.hasOption('A')) m_passive = false;
      if (m_cmdLine.hasOption('n')) m_name = m_cmdLine.getOptionValue('n');
      if (m_cmdLine.hasOption('c'))
      {
         try
         {
            m_cleanDays = new Integer(m_cmdLine.getOptionValue('c'));
            if (m_cleanDays < 0)
            {
               System.out
                     .println("Could not create clean value from negative number: "
                           + m_cmdLine.getOptionValue('c') + ". Exiting.");
               System.exit(255);
            }
            m_isCleaning = true;
         }
         catch (NumberFormatException e)
         {
            System.out.println("Could not parse Clean frequency from value: "
                  + m_cmdLine.getOptionValue('c') + ". Exiting.");
            System.exit(255);
         }
      }
      if (m_cmdLine.hasOption("S")) m_startDate = _createDateFromString(m_cmdLine
            .getOptionValue("S"));;
      if (m_cmdLine.hasOption("E")) m_endDate = _createDateFromString(m_cmdLine
            .getOptionValue("E"));
      if (m_cmdLine.hasOption('R')) m_recursive = true;
      if (m_cmdLine.hasOption('l')) m_list = true;
      if (m_cmdLine.hasOption('f'))
      {
         try
         {
            m_frequency = new Integer(m_cmdLine.getOptionValue('f')) * 60 * 1000;
            m_continuous = true;
         }
         catch (NumberFormatException e)
         {
            System.out.println("Could not parse frequency from value: "
                  + m_cmdLine.getOptionValue('f') + ". Exiting.");
            System.exit(255);
         }
      }
      if (m_cmdLine.hasOption("wu")) m_write_user = m_cmdLine
            .getOptionValue("wu");
      
      if (m_cmdLine.hasOption("ru")) m_user = m_cmdLine.getOptionValue("ru");
      if (m_cmdLine.hasOption("wup")){
    	  m_writeAuth = m_cmdLine.getOptionValue("wup");
    	  if(m_writeAuth.equals("DUMMY_PASSWORD")){
	    		m_writeAuth="";  
	    	  }
      }
      if (m_cmdLine.hasOption("rup"))
    	  {
	    	  m_readAuth = m_cmdLine.getOptionValue("rup");
	    	  if(m_readAuth.equals("DUMMY_PASSWORD")){
	    		m_readAuth="";  
	    	  }
    	  }
      if (m_cmdLine.hasOption("home")) m_home = m_cmdLine
            .getOptionValue("home");
      if (m_cmdLine.hasOption("log")) m_logFile = m_cmdLine
            .getOptionValue("log");
      if (m_endDate != null && m_startDate == null)
      {
         System.out
               .println("End date range supplied with no start date. Supply a 'start date' using the -S <yyyy-MM-dd'T'HH:mm:ss.SSS> option");
         System.exit(1);
      }
      if (m_writeDir == null && m_list == false)
      {
         System.out
               .println("No write directory specified (-w <arg>) and not using the list option (-l). Must specify one to run correctly.");
         System.exit(1);
      }

      // _logger.info("statefile: " +m_stateFile);

   }

   private static void _parseArgs() throws ParseException
   {
      String[] requiredOptions = new String[] {};
      CommandLineParser parser = new BasicParser();
      m_options = new Options();

      // Add the possible options:
      m_options.addOption("r", "readdir", true, "Properties file name");

      m_options
            .addOption(
                  "cmd",
                  "command",
                  false,
                  "Command to run when a new file is received. Pass parameters to the command using $fileName, $FileSize, $modified, $checksum, $providerURL, $outputPath");
      m_options.addOption("wup", "writeuserpass", true,
            "User's password for write directory authentication");
      m_options.addOption("rup", "readuserpass", true,
            "User's password for read directory authentication");
      m_options.addOption("wu", "writeuser", true,
            "User's name for write directory authentication");
      m_options.addOption("ru", "readuser", true,
            "User's name for read directory authentication");
      m_options.addOption("home", "home", true,
            "Absolute path to users home directory");
      m_options.addOption("log", "log", true,
            "Path + name of logfile to output information to");
      m_options.addOption("tz", "timezone", true,
      "The timezone for which start/stop times apply. 'Z' would mean GMT. 'PST' is the default.");
      m_options
            .addOption(
                  "l",
                  "list",
                  false,
                  "List the files from the target (read) location that have not yet been crawled.");
      m_options.addOption("R", "Recursive", false,
            "Set crawler to Recursive mode");
      m_options.addOption("A", "Active", false,
      "Set the crawlers FTP mode to \"active\"");
      m_options.addOption("w", "writedir", true, "Message Level");
      m_options.addOption("f", "frequency", true,
            "Frequency, in minutes, with which to run the crawler");
      m_options
            .addOption(
                  "c",
                  "clean",
                  true,
                  "The Cleaning frequency, in days, for deleting files from the write directory. For example -c 5 means any file that is more than 5 days old in the write directory will be deleted.");

      m_options
            .addOption(
                  "S",
                  "startdate",
                  true,
                  "The start date for the crawler to filter files on, in yyyy-MM-dd'T'HH:mm:ss.SSS format.");
      m_options
            .addOption(
                  "E",
                  "enddate",
                  true,
                  "The end date for the crawler to filter files on, in yyyy-MM-dd'T'HH:mm:ss.SSS format.");

      m_options.addOption("p", "pattern", true,
            "Pattern for filtering the read directory");
      m_options.addOption("save", "save-after", true,
      "Number of files to process before saving to the registry.");
      // m_options.addOption("d", "daemain_name", true,
      // "A name for this daemon instance");
      m_options.addOption("s", "states", true, "State file name");
      // m_options.addOption("c", "connection", true,
      // "Connection type [TCP, JMS]");
      m_options.addOption("h", "help", false, "Print this usage information");
      m_options
            .addOption(
                  "n",
                  "name",
                  true,
                  "Name for the crawler. Must be uniuque to any other crawlers running on the current system");

      requiredOptions = new String[] { "r", "s", "n", "home" };

      m_cmdLine = parser.parse(m_options, m_args);
      m_help = (!_hasRequiredOptions(requiredOptions));
   }

   private static boolean _hasRequiredOptions(String[] requiredOptions)
   {
      boolean result = true;

      for (String requiredOption : requiredOptions)
      {
         if (!m_cmdLine.hasOption(requiredOption))
         {
            result = false;
            System.out.println("Missing option: " + requiredOption);
            break;
         }
      }

      return result;
   }

   private static void _printUsage()
   {
      String _userScript = new String("start_crawler.csh");
      String USAGE_HEADER = new String("start_crawler");
      HelpFormatter formatter = new HelpFormatter();
      // System.out.println();

      formatter.printHelp(_userScript, USAGE_HEADER, m_options, null, true);
   }

   private static Set<FileProduct> _restoreProductsFromRegistry(
         String i_directoryStateFilename)
   {
      Set<FileProduct> r_products = new HashSet<FileProduct>(); 
      
      try
      {
         if (new File(i_directoryStateFilename).exists())
         {
            CrawlerRegistry registry = new FileCrawlerRegistry(
                  i_directoryStateFilename);
            r_products = registry.restore();
         }
      }
      catch (CrawlerException an_exception)
      {
         _logger.error(an_exception.getMessage());
         if (_logger.isDebugEnabled()) an_exception.printStackTrace();
      }

      return (r_products);
   }

   private static Date _createDateFromString(String i_date)
   {
      // DateFormat df = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
      System.out.println("Using timezone: " + m_tz);
	  Date date = null;
      // date = df.parse(i_date);
      date = DateTimeUtility.parseDateWithTZ(i_date,m_tz);
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	  c.setTimeInMillis(date.getTime());
	  String tm = String.format("%1$tm/%1$te/%1$tY %1$tk:%1$tM:%1$tS %1$tZ", c);
      System.out.println("Created time: " + tm);
      
      if (date == null)
      {
         System.out.println("Could not parse date from string: " + i_date);
         return null;
      }
      return date;
   }

   private static void _saveProductsToRegistry(Set<FileProduct> i_products,
         String i_directoryStateFilename)
   {
      try
      {
         CrawlerRegistry registry = new FileCrawlerRegistry(
               i_directoryStateFilename);
         registry.save(i_products);
      }
      catch (CrawlerException an_exception)
      {
         _logger.error(an_exception.getMessage());
         if (_logger.isDebugEnabled()) an_exception.printStackTrace();
      }
   }

   private static void _addToSuccessProcessedList(FileProduct i_oneProduct)
   {
      // Add one FileProduct to the list of m_successProcessedList for later
      // exclude list.

      // Assumption: m_successProcessedList is not null;

      if (m_successProcessedList != null)
      {
         // System.out.println("_addToSuccessProcessedList: m_successProcessedList.size() = ["
         // + m_successProcessedList.size() + "]");

         // Only add the file product if it hasn't been added before.
         if (!(m_successProcessedList.contains(i_oneProduct)))
         {

            boolean addToProcessedListSuccessful = m_successProcessedList
                  .add(i_oneProduct); // Save this for later exclude list.

            if (!addToProcessedListSuccessful) _logger
                  .error("Cannot add element to m_successProcessedList: ["
                        + i_oneProduct.toString() + "]");
            m_successListChanged = true;
         }

         // Don't forget to remove this entry if it had a failure before.
         // A previous failure is signaled by having an entry in the
         // AttemptsTable

         // AttemptsTable.getInstance().clearEntryIfExist(i_oneProduct.getFriendlyURI().toString());

      }
      else
      {
         _logger
               .error("Array m_successProcessedList is null. Cannot add element: ["
                     + i_oneProduct.toString() + "]");
      }
   }
   
   public long convertTime(long d){
	   TimeZone gmt = TimeZone.getTimeZone("GMT");
       Calendar tempCal = Calendar.getInstance();
       tempCal.setTimeInMillis(d);
       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
       cal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE), tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.SECOND));
       cal.set(Calendar.MILLISECOND, 0);
       
       return cal.getTimeInMillis();
   }
   
   public String displayTime(long d){
	   
	   TimeZone gmt = TimeZone.getTimeZone("GMT");
       Calendar tempCal = Calendar.getInstance();
       tempCal.setTimeInMillis(d);
       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
       cal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE), tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.SECOND));
       cal.set(Calendar.MILLISECOND, 0);
		
      
		
		DateFormat dfGMT = DateFormat.getDateTimeInstance();
	    dfGMT.setTimeZone(gmt);
	    
	    return dfGMT.format(cal.getTime());
	     
   }
   
   public void process (FileProduct fp)
   {
      boolean include = true;
      
      if (m_startDate != null) include &= !this._filterPendingListByDate (fp);

      include &= !GenericCrawler.m_successProcessedList.contains (fp);
      GenericCrawler.count++;
      
      if (include)
      {
         if (GenericCrawler.m_list) this._listFiles (fp);
         else
         {
            this._writeToDir (fp);
            
            //save statefile every x (10 by default) entries.
            if(m_completedProcessedList.size() > m_saveAfterNum){
            	_logger.info("Saving processed list to statefile...");
            	m_successProcessedList.addAll(m_completedProcessedList);
            	_saveProductsToRegistry(m_successProcessedList, m_stateFile);
            	m_completedProcessedList.clear();
            }     
         }
      }
   }
}
