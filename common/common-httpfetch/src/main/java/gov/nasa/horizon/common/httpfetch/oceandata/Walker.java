package gov.nasa.horizon.common.httpfetch.oceandata;


import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Jun 14, 2010
 * Time: 1:08:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Walker {

   private String _sat;
   private String _level;
   private String _inputURL;
   private String _outputPath;
   private String _interval;
   private String _kilometer;
   private String _cachePath;
   private String _sst;
   private String _year;



   public Walker() {
   }

   public void run() {
      URLFetcher ufetcher = new URLFetcher();
      List<String> results = new ArrayList<String>();
      List<String> urls = ufetcher.getURLs("http://oceandata.sci.gsfc.nasa.gov/MODISA/L3SMI/");
      while (urls.size() > 0) {
         String url = urls.remove(0);
         System.out.println (url);
         if (url.endsWith("bz2")) {
            Pattern p = Pattern.compile("(A|T)(\\d{4})(\\d{3})\\d*\\.L3m_(DAY|8D|MO|YR)_N{0,1}SST4_(4|9)\\.bz2");
            Matcher m = p.matcher(url);
            if (m.find())  {
               results.add(url);
            }
         } else {
            List<String> children = ufetcher.getURLs(url);
            if (children.size() > 0) {
               for (String child : children) {
                  urls.add(child);
               }
            }
         }
      }

      for (String url : results) {
         System.out.println(url);
      }
   }

   public int parseArgs(String[] args) throws ParseException {
      int stat = 0;
      CommandLineParser parser = new BasicParser();
      Options options = new Options();
      options.addOption ("s", "satellite", true, "The name of the satellite");
      options.addOption ("l", "level", true, "Processing level");
      options.addOption ("i", "interval", true, "Processing interval");
      options.addOption ("k", "kilometer", true, "Observation distances");
      options.addOption ("r", "inputURL", true, "Top level input remote URL");
      options.addOption ("o", "outputPath", true, "Top level output path");
      options.addOption ("s", "sst", true, "[sst | sst4]");
      options.addOption ("c", "cachePath", true, "Cache root directory");
      options.addOption ("y", "year", true, "Year");

      CommandLine commandLine = parser.parse(options, args);

      this._sat = commandLine.getOptionValue('s');
      this._level = commandLine.getOptionValue('l');
      this._interval = commandLine.getOptionValue('i');
      this._kilometer = commandLine.getOptionValue('k');
      this._inputURL = commandLine.getOptionValue('r');
      this._outputPath = commandLine.getOptionValue('o');
      this._cachePath = commandLine.getOptionValue('c');

      this._inputURL = this._inputURL + "/" + "MODIS" + this._sat.toUpperCase().charAt(0) + "/" + this._level.toUpperCase() + "/" + this._year; 
      this._outputPath = String.format("%s/%s/L3_mapped/%s/%s/%s/%skm/", commandLine.getOptionValue('o'),
            this._sat, this._sst, this._interval, this._kilometer, this._year);

      return stat;
   }

   public static void main (String[] args) {
      Walker walker = new Walker();
      try {
         walker.parseArgs(args);
      } catch (ParseException e) {
         System.exit(1);
      }
      walker.run();
      System.exit(0);
   }
}
