package db;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.DataSourceUtils;
import oracle.jdbc.OracleDriver;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import bus.ReportContent;

public class ReportManagerDaoJdbc implements ReportManagerDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private DataSource ds;
    //private String emptyPrompt = "(No report has been submitted yet.)";
    private String emptyPrompt;
    private int numSections, numWeeks;
    
    private String dbURL = null;
    private String dbUSER = null;
    private String dbPW = null;

    //----------- getDBURL ------------
    public String getDBURL() {
      if(dbURL == null) {
        String catalina_home = System.getenv("CATALINA_HOME");
        //logger.info("****** CATALINA_HOME is: "+catalina_home);

        String propertyFile = catalina_home+"/webapps/report/WEB-INF/messages.properties";
        //logger.info("****** propertyFile is: "+propertyFile);

        Properties p = new Properties();
        try{
          p.load(new FileInputStream(propertyFile));
        }
        catch (IOException e){
          logger.info("****** Error: "+propertyFile+" cannot be found!");
        }
        //dbURL = p.getProperty("db.url")+";shutdown=true";
        dbURL = p.getProperty("db.url");
        dbUSER = p.getProperty("db.user");
        dbPW = p.getProperty("db.pw");
        emptyPrompt = p.getProperty("emptyPrompt");
      }
      //logger.info("XXX dbURL: "+dbURL);

      return dbURL;
    }

    //------------- getReportWeekListFromDB ------------
    public List getReportWeekListFromDB() {
        List weekList = null;
    	
    	Connection c = null;
        int num_weeks = getNumWeeks();  // num of weeks to put in the select tag

        //logger.info("XXX in getReportWeekListFromDB() ----- ");

    	try {
          dbURL = getDBURL();
          DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
          c = DriverManager.getConnection(dbURL, dbUSER, dbPW);
            
       	  String sqlQuery = "select report_week from reports order by report_week DESC";
          //logger.info("XXX sqlQuery: "+sqlQuery);
          PreparedStatement ps = c.prepareStatement(sqlQuery);
          ResultSet rs = ps.executeQuery();

          // construct a list of report_week dates
          // for the select tag
          weekList = new ArrayList();
          int count = 0;
          while(rs.next() && count < num_weeks) {
            System.out.println("XXXX - in the rs.next() loop");
            String dateStr = rs.getString("report_week");
            //logger.info("XXX rs.getString: "+rs.getString("report_week"));
            weekList.add(dateStr);
            count++;
          }
    	}
    	catch (SQLException ex) {
            logger.info("****** Error: SQLException: "+ex);           
            try { c.close(); } catch (SQLException e) { }
        } finally {
            // properly release our connection
            try { c.close(); } catch (SQLException e) { }
            logger.info("XXX In getReportWeekListFromDB, c is released\n\n\n\n\n");
        }

        return weekList;
    }
    
   
    //---------------- getReportContentFromDB --------------
    public ReportContent getReportContentFromDB(String whichWeek) {
    	Connection c = null;
    	StringBuffer sB = new StringBuffer();
        ReportContent reportContent= new ReportContent();
        List sectList = new ArrayList();
        List sectAppList = new ArrayList();
        String ss, sec;

        //logger.info("XXX in getReportContentFromDB() ----- ");

        reportContent.setReportWeek(whichWeek);
    	
    	try {
          dbURL = getDBURL();
          DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
          //c = DriverManager.getConnection(dbURL, "sa", "");
          c = DriverManager.getConnection(dbURL, dbUSER, dbPW);
          sB.append("select * from reports where report_week = ?");
          PreparedStatement ps = c.prepareStatement(sB.toString());
          ps.setString(1, whichWeek);
          //logger.info("XXX in getReportContentFromDB(), whichWeek is: "+whichWeek);
          ResultSet rs = ps.executeQuery();
            
          // if DB has the record
          if(rs.next()) {
            //logger.info("XXXX - in the rs.next() loop");
            for(int i=1; i<=getNumSections(); i++) {
              ss = "section"+i;
              //logger.info("XXX ss: "+ss);
              sec = rs.getString(ss);

              if(sec == null || sec.equals(""))
                sectList.add(emptyPrompt);
              else
                sectList.add(sec);

              sectAppList.add("");
            }
          }
          // if DB does not have the record
	  else {
              //logger.info("XXXX - in the else part ");
              for(int i=0; i<getNumSections(); i++) {
                sectList.add(emptyPrompt);
                sectAppList.add("");
              }
          }

          reportContent.setSections(sectList);
          reportContent.setSectionAppends(sectAppList);
    	}
    	catch (SQLException ex) {
            logger.info("****** Error: SQLException: "+ex);           
            try { c.close(); } catch (SQLException e) { }
        } finally {
            // properly release our connection
            try { c.close(); } catch (SQLException e) { }
            logger.info("c is released");           
        }

        //logger.info("XXX Before returning from getReportContentFromDB ...");
        return reportContent;
      }


    //---------------- appendReportContentFromDB --------------
    public synchronized void appendReportContentToDB(ReportContent rptC) {
    	Connection c = null;
        Statement statement = null;
        ResultSet resultSet = null;
        StringBuffer sB = null;
        String ss;

        String report_week = rptC.getReportWeek();
        //logger.info("XXX In appendReportContentToDB(), before calling rptC.getSections()");
        //List sects = rptC.getSections();
        List sectAppends = rptC.getSectionAppends();

        String sec, secapp, newsec;
        int j = 1;

        //ListIterator secIter = sects.listIterator();
        ListIterator secAppIter = sectAppends.listIterator();

        try {
          dbURL = getDBURL();
          DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
          //c = DriverManager.getConnection(dbURL, "sa", "");
          c = DriverManager.getConnection(dbURL, dbUSER, dbPW);
          String sqlQuery = null;
          //logger.info("XXXXX ----- In appendReportContentToDB(), report_week: " + report_week);
          
          sB = new StringBuffer();
          sB.append("select * from reports where report_week = ?");

          PreparedStatement ps = c.prepareStatement(sB.toString());
          ps.setString(1, report_week);
          ResultSet rs = ps.executeQuery();
            
          // If this report_week already exists in DB
          if(rs.next()) {
            sB = new StringBuffer();
            sB.append("update reports set section1 = ?,section2= ?,section3= ?,section4= ?,section5 = ?,section6 = ?,section7 = ?,section8 = ?,section9 = ?,section10 = ?,section11 = ?,section12 = ?,section13 = ?,section14 = ?,section15 = ?,section16 = ?,section17 = ?,section18 = ?,section19 = ?,section20 = ?,section21 = ?,section22 = ?,section23 = ?,section24 = ? where report_week = ?");

            ps = c.prepareStatement(sB.toString());

            j = 1;
            while(secAppIter.hasNext()) {
              //sec = (String) secIter.next();
              // Get sec from the DB
              ss = "section"+j;
              sec = rs.getString(ss);
              //logger.info("XXX In appendReportContentToDB(), sec: " + sec);
              secapp = (String) secAppIter.next();
              //logger.info("XXX In appendReportContentToDB(), secapp: " + secapp);

	      newsec = "";

	      if(sec != null && !sec.equals(""))
	        newsec += sec;

              if(secapp != null && !secapp.equals(""))
	        if(sec != null && !sec.equals(""))
                  newsec += "\n\n" + secapp;
                else
                  newsec = secapp;

              ps.setString(j, newsec);
              j++;
            }

            ps.setString(j, report_week);
          
            int ret = ps.executeUpdate();
            //logger.info("----- executeUpdate return value =  " + ret);
          }
          // If this report_week does not exist in DB
          else
          {
            int id = getNextId();
            sB = new StringBuffer();
            sB.append("insert into reports (id, report_week, section1, section2, section3, section4, section5, section6, section7, section8, section9, section10, section11, section12, section13, section14, section15, section16, section17, section18, section19, section20, section21, section22, section23, section24) ");
            sB.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            //logger.info("***** No record exists so we do INSERT----- single query is =  " + sqlQuery);

            ps = c.prepareStatement(sB.toString());
            ps.setInt(1,id);
            ps.setString(2,report_week);

            j = 1;
            while(secAppIter.hasNext()) {
              secapp = (String) secAppIter.next();
              ps.setString(j+2, secapp);

              j++;
            }

            int ret = ps.executeUpdate();
       
            //logger.info("----- executeUpdate return value =  " + ret);
          }
       	} catch (SQLException ex) {
          // something has failed and we print a stack trace to analyse the error
          logger.info("IN appendReportContentToDB() - SQLException!!!"+ex.getMessage());
          ex.printStackTrace();
          // ignore failure closing connection
          try { c.close(); } catch (SQLException e) { }
        } finally {
          // properly release our connection
          // DataSourceUtils.releaseConnection(c, ds);
          try { c.close(); } catch (SQLException e) { }
          logger.info("IN appendReportContentToDB() - connection is released");
        }
    }

    //--------------------- setReportContentToDB ---------------
    public void setReportContentToDB(ReportContent rptC) {
    	Connection c = null;
        Statement statement = null;
        ResultSet resultSet = null;
        StringBuffer sB = null;

        String report_week = rptC.getReportWeek();

        //logger.info("XXX In setReportContentToDB(), before calling rptC.getSections()");
        List sects = rptC.getSections();

        String sec;
        int j = 1;

        ListIterator secIter = sects.listIterator();

        try {
          dbURL = getDBURL();
          DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
          //c = DriverManager.getConnection(dbURL, "sa", "");
          c = DriverManager.getConnection(dbURL, dbUSER, dbPW);
          String sqlQuery = null;

          //logger.info("XXXXX ----- In setReportContentToDB(), report_week: " + report_week);
          
          sB = new StringBuffer();
          sB.append("select * from reports where report_week = ?");

          PreparedStatement ps = c.prepareStatement(sB.toString());
          ps.setString(1, report_week);
          ResultSet rs = ps.executeQuery();

          // if DB has this record
          if(rs.next()) {
            sB = new StringBuffer();
            sB.append("update reports set section1 = ?,section2= ?,section3= ?,section4= ?,section5 = ?,section6 = ?,section7 = ?,section8 = ?,section9 = ?,section10 = ?,section11 = ?,section12 = ?,section13 = ?,section14 = ?,section15 = ?,section16 = ?,section17 = ?,section18 = ?,section19 = ?,section20 = ?,section21 = ?,section22 = ?,section23 = ?,section24 = ? where report_week = ?");

            ps = c.prepareStatement(sB.toString());

            j = 1;
            while(secIter.hasNext()) {
              sec = (String) secIter.next();

	      if(sec == null)
	        sec = "";

              ps.setString(j, sec);
              j++;
            }
          
            ps.setString(j, report_week);

            int ret = ps.executeUpdate();
            //logger.info("----- executeUpdate return value =  " + ret);
          }
          // if DB does not have this record
          else
          {
            int id = getNextId();
            sB = new StringBuffer();
            sB.append("insert into reports (id, report_week, section1, section2, section3, section4, section5, section6, section7, section8, section9, section10, section11, section12, section13, section14, section15, section16, section17, section18, section19, section20, section21, section22, section23, section24) ");
            sB.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            //logger.info("****** No record exists so we do INSERT----- single query is =  " + sqlQuery);

            ps = c.prepareStatement(sB.toString());
            ps.setInt(1, id);
            ps.setString(2, report_week);

            j = 1;
            while(secIter.hasNext()) {
              sec = (String) secIter.next();
              ps.setString(j+2, sec);
              j++;
            }
       	
            int ret = ps.executeUpdate();
       
            //logger.info("----- executeUpdate return value =  " + ret);
          }
       	} catch (SQLException ex) {
          // something has failed and we print a stack trace to analyse the error
          logger.info("IN setReportContentToDB() - SQLException!!!"+ex.getMessage());
          ex.printStackTrace();
          // ignore failure closing connection
          try { c.close(); } catch (SQLException e) { }
        } finally {
          // properly release our connection
          // DataSourceUtils.releaseConnection(c, ds);
          try { c.close(); } catch (SQLException e) { }
          logger.info("IN setReportContentToDB() - connection is released");
        }
    }


    //------------- getNextId -------------
    public int getNextId() {
	Connection c = null;
    	int nextId = 0;
    	int currentId = 0;
    	
        //logger.info("XXX in getNextId() ----- ");

    	try {
          dbURL = getDBURL();
          DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
          //c = DriverManager.getConnection(dbURL, "sa", "");
          c = DriverManager.getConnection(dbURL, dbUSER, dbPW);
       	  String sqlQuery = "select id from reports order by id DESC";
          //logger.info("XXX sqlQuery: "+sqlQuery);
          PreparedStatement ps = c.prepareStatement(sqlQuery);
          ResultSet rs = ps.executeQuery();
            
          if(rs.next())
            currentId = rs.getInt("id");
          else
            currentId = 0;

          //logger.info("id = " +currentId);
          nextId = currentId + 1;
    	}
    	catch (SQLException ex) {
          logger.info("****** Error: in getNextId(), SQLException: "+ex);           
          try { c.close(); } catch (SQLException e) { }
        } finally {
          // properly release our connection
          try { c.close(); } catch (SQLException e) { }
          logger.info("c is released");           
        }

        return nextId;
      }


    public void setDataSource(DataSource ds) {
      this.ds = ds;
    }

    public int getNumSections() {
      numSections = 24;
      return numSections;
    }

    public int getNumWeeks() {
      numWeeks = 6;
      return numWeeks;
    }
}
