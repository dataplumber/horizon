package util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import java.lang.*;

import util.*;

class archive_html {
  public void curl_connect() {
    int idx;
    Runtime rt=Runtime.getRuntime();
    String ls_str="";
    //System.out.println("inside cURL process");
    Process pr = null;

    try {
      String catalina_home = System.getenv("CATALINA_HOME");
      String propertyFile = catalina_home+"/webapps/report/WEB-INF/messages.properties";
      Properties pp = new Properties();
      try{
        pp.load(new FileInputStream(propertyFile));
      }
      catch (IOException e){
        System.out.println("****** Error: "+propertyFile+" cannot be found!");
      }
      String arch = pp.getProperty("archivedRpts");
      String back = pp.getProperty("back");

      String ofname;
      // html file name
      ofname = constructOutputFileName();
      FileOutputStream out;
      PrintStream p = null;
      try {
        out = new FileOutputStream(ofname);
        p = new PrintStream(out);
      } catch (Exception ex) {
        System.out.println("****** Error: Exception opening output file. "+ex);
      }

      // curl the report site
      String week = cal.dateOfMonBeforeLast();
      String urlpath="http://localhost:8080/report/htmlReport.htm?whichDate="+week;
      String CreateProcess ="";			
      System.out.println("****** url: "+urlpath);
      CreateProcess = "/usr/bin/curl " + urlpath;

      try {
        pr = rt.exec(CreateProcess);
      } catch (Exception ex) {
        System.out.println("****** Error: Exception executing curl. "+ex);
      }

      // read the stream from the curl process
      InputStream stderr = pr.getInputStream();
      InputStreamReader isr = new InputStreamReader(stderr);
      BufferedReader br = new BufferedReader(isr);
      String line = null;

      // write each line to output file
      while ((line = br.readLine())!= null ) {
        //System.out.println(line);
        if(!line.contains(arch) && !line.contains(back))
          p.println(line);
      }

      p.close();
    }
    catch(Exception ee) {
      System.out.println("error traced");
      ee.printStackTrace();
    }
  }

  // generate file name for the html file
  public String constructOutputFileName() {
    Calendar rightNow = Calendar.getInstance();
    String outputFileName = null;

    String tomcat_home = System.getenv("CATALINA_HOME");
    System.out.println(tomcat_home);

    outputFileName = tomcat_home + "/webapps/report/report_archive/" 
       + "rpt_"
       + rightNow.get(Calendar.YEAR)
       + parseData(rightNow.get(Calendar.MONTH)+1)
       + parseData(rightNow.get(Calendar.DAY_OF_MONTH))
       + ".html";
    System.out.println("output file name is : "+outputFileName);

    return outputFileName;
  }

  public String parseData(int data) {
    String r;

    if(data <= 9)
      return r="0"+String.valueOf(data);
    else
      return r=String.valueOf(data);
  }
}

class do_archive {
  public static void main(String args[]) {

    try {		
      archive_html t12= new archive_html();
      t12.curl_connect();
    } catch(Exception ee) {
      ee.printStackTrace();
    }
  }
}
