package db;

import bus.Fields;
import bus.Catalog;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.jdbc.object.MappingSqlQuery;
//import org.springframework.jdbc.object.SqlUpdate;
//import org.springframework.jdbc.core.SqlParameter;
//import org.springframework.jdbc.datasource.DataSourceUtils;
import oracle.jdbc.OracleDriver;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Collections;
import java.util.Comparator;
import java.io.FileInputStream;
import java.io.IOException;

public class CatalogDaoJdbc implements CatalogDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private DataSource ds;

    private String dbURL = null;
    private String dbUSER = null;
    private String dbPW = null;
    private String NA = null;
    
    //----------- getDBURL ------------
    // Get info. from messages.properties
    // and initialize the vars dbURL, dbUSER, dbPW, NA
    //
    public String getDBURL() {
      if(dbURL == null || NA == null) {
        String catalina_home = System.getenv("CATALINA_HOME");
        logger.info("****** CATALINA_HOME is: "+catalina_home);

        String propertyFile = catalina_home+"/webapps/streamlining/WEB-INF/messages.properties";
        logger.info("****** propertyFile is: "+propertyFile);

        Properties p = new Properties();
        try{
          p.load(new FileInputStream(propertyFile));
        }
        catch (IOException e){
          logger.info("****** Error: "+propertyFile+" cannot be found!");
        }
        // For the embedded hsql db
        //dbURL = p.getProperty("db.url")+";shutdown=true";
        // For Oracle db
        dbURL = p.getProperty("db.url");
        dbUSER = p.getProperty("db.user");
        dbPW = p.getProperty("db.pw");
        NA = p.getProperty("NA");
      }
      logger.info("XXX dbURL: "+dbURL);

      return dbURL;
    }


    //-------- getCatalogFromDB ----------
    // Query the db, get the fields of each row,
    // and insert the rows to a catalog
    //
    public Catalog getCatalogFromDB() {
    	
    	Connection c = null;
        Fields af;
        Catalog ac = new Catalog();
    	List fields = new ArrayList();
        StringBuffer sB = new StringBuffer();
        String astr;

        Comp comp= new Comp();

    	try {
          dbURL = getDBURL();
          logger.info("XXX in getFieldList() ----- dbUSER: "+dbUSER);
          logger.info("XXX in getFieldList() ----- dbPW: "+dbPW);
          DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
          c = DriverManager.getConnection(dbURL, dbUSER, dbPW);
          //logger.info("XXX in getFieldList() ----- c is: "+c);
          logger.info("XXXX - Connected to DB streamlining ...");
          //sB.append("select * from catalog where id = ?");
          sB.append("select * from catalog");
          PreparedStatement ps = c.prepareStatement(sB.toString());
          //ps.setString(1, id);
          ResultSet rs = ps.executeQuery();

          while(rs.next()) {
             logger.info("XXXX - " +
                       rs.getString("id") + ", " +
                       rs.getString("Cycle") + ", " +
                       rs.getString("Version") + ", " +
                       rs.getString("Data_Author") + ", " +
                       rs.getString("CNES_Approved") + ", " +
                       rs.getString("NASA_Approved") + ", " +
                       rs.getString("GDR_Release_Date") + ", " +
                       rs.getString("SGDR_Staged") + ", " +
                       rs.getString("SGDR_Release_Date") + ", " +
                       rs.getString("GDRnetCDF_Staged") + ", " +
                       rs.getString("GDRnetCDF_Release_Date") + ", " +

                       rs.getString("SGDRnetCDF_Staged") + ", " +
                       rs.getString("SGDRnetCDF_Release_Date") + ", " +

                       rs.getString("SSHAnetCDF_Staged") + ", " +
                       rs.getString("SSHAnetCDF_Release_Date") + ", " +
                       rs.getString("Email_ID"));

             af = new Fields();
             af.setId(rs.getInt("id"));
             af.setCycle(rs.getString("Cycle"));
             af.setVersion(rs.getString("Version"));
             af.setDataAuthor(rs.getString("Data_Author"));

             astr = rs.getString("CNES_Approved");
             if(astr.equals("Y") || astr.equals("y"))
               af.setCNESapproved(true);
             if(astr.equals("N") || astr.equals("n"))
               af.setCNESapproved(false);

             astr = rs.getString("NASA_Approved");
             if(astr.equals("Y") || astr.equals("y"))
               af.setNASAapproved(true);
             if(astr.equals("N") || astr.equals("n"))
               af.setNASAapproved(false);

             astr = rs.getString("GDR_Release_Date");
             if(astr == null || astr.equals(""))
               astr = NA;
             af.setGDRreleaseDate(astr);

             astr = rs.getString("SGDR_Staged");
             if(astr.equals("Y") || astr.equals("y"))
               af.setSGDRstaged(true);
             if(astr.equals("N") || astr.equals("n"))
               af.setSGDRstaged(false);

             astr = rs.getString("SGDR_Release_Date");
             if(astr == null || astr.equals(""))
               astr = NA;
             af.setSGDRreleaseDate(astr);

             astr = rs.getString("GDRnetCDF_Staged");
             if(astr.equals("Y") || astr.equals("y"))
               af.setGDRnetCDFstaged(true);
             if(astr.equals("N") || astr.equals("n"))
               af.setGDRnetCDFstaged(false);

             astr = rs.getString("GDRnetCDF_Release_Date");
             if(astr == null || astr.equals(""))
               astr = NA;
             af.setGDRnetCDFreleaseDate(astr);






             astr = rs.getString("SGDRnetCDF_Staged");
             if(astr.equals("Y") || astr.equals("y"))
               af.setSGDRnetCDFstaged(true);
             if(astr.equals("N") || astr.equals("n"))
               af.setSGDRnetCDFstaged(false);

             astr = rs.getString("SGDRnetCDF_Release_Date");
             if(astr == null || astr.equals(""))
               astr = NA;
             af.setSGDRnetCDFreleaseDate(astr);




             astr = rs.getString("SSHAnetCDF_Staged");
             if(astr.equals("Y") || astr.equals("y"))
               af.setSSHAnetCDFstaged(true);
             if(astr.equals("N") || astr.equals("n"))
               af.setSSHAnetCDFstaged(false);

             astr = rs.getString("SSHAnetCDF_Release_Date");
             if(astr == null || astr.equals(""))
               astr = NA;
             af.setSSHAnetCDFreleaseDate(astr);

             astr = rs.getString("Email_ID");
             if(astr == null || astr.equals(""))
               astr = NA;
             af.setEmailId(astr);

             if( rs.getString("Cycle") != null &&
                 rs.getString("Version") != null &&
                 rs.getString("Data_Author") != null)
               fields.add(af);
          }
        }
        catch (SQLException ex) {
            logger.info("****** Error: SQLException: "+ex);
            try { c.close(); } catch (SQLException e) { }
        } finally {
            // properly release our connection
            try { c.close(); } catch (SQLException e) { }
            logger.info("Connection to DB is released.");
        }

        // Sort the fields 
        Collections.sort(fields, comp);

        // Give the fields list to the catalog
        ac.setFields(fields);

        return ac;
    }


    //-------- setCatalogToDB ----------
    // Given a catalog, update the db with the fields
    // CNES_Approved, NASA_Approved, and SGDR_Staged
    // from the catalog
    //
    public void setCatalogToDB(Catalog cat) {
    	
    	Connection c = null;
        Statement statement = null;
        ResultSet resultSet = null;
        StringBuffer sB = null;
        String ac;
        int row_id = 0;
        Fields f = null;
        PreparedStatement ps = null;

        List fds = cat.getFields();
        ListIterator li = fds.listIterator();

        try {
          dbURL = getDBURL();
          c = DriverManager.getConnection(dbURL, dbUSER, dbPW);
          sB = new StringBuffer();
            
          while (li.hasNext()) {

            sB = new StringBuffer();
            sB.append("update catalog set CNES_Approved = ?,NASA_Approved= ? where id = ?");
            ps = c.prepareStatement(sB.toString());

            f = (Fields) li.next();
            //logger.info("----- f.getCNESapproved(): " + f.getCNESapproved());
            //logger.info("----- f.getNASAapproved(): " + f.getNASAapproved());
            //logger.info("----- f.getSGDRstaged(): " + f.getSGDRstaged());

            String cycle = f.getCycle();
            String version = f.getVersion();
            String auth  = f.getDataAuthor();
            
            String dbQuery = "SELECT * FROM catalog WHERE Cycle = '"+cycle+"' AND Version = '"+version+"' AND Data_Author = '"+auth+"'";
            PreparedStatement ps1 = c.prepareStatement(dbQuery.toString());
            resultSet = ps1.executeQuery();
            //logger.info("dbQuery: "+dbQuery);
            while(resultSet.next()){
              row_id = resultSet.getInt("id");
            }

            //logger.info("----- row_id =  " + row_id);
            ps.setInt(3, row_id);

            ac = "N";

            if(f.getCNESapproved() == true)
              ac = "Y";
            else if(f.getCNESapproved() == false)
              ac = "N";

            ps.setString(1, ac);

            if(f.getNASAapproved() == true)
              ac = "Y";
            else if(f.getNASAapproved() == false)
              ac = "N";

            ps.setString(2, ac);

            int ret = ps.executeUpdate();
            //logger.info("----- executeUpdate update catalog return value =  " + ret);
          }
       	} catch (SQLException ex) {
          // something has failed and we print a stack trace to analyse the error
          logger.info("****** In setCatalogToDB() - SQLException: "+ex.getMessage());
          ex.printStackTrace();
          // ignore failure closing connection
          try { c.close(); } catch (SQLException e) { }
        } finally {
          // properly release our connection
          // DataSourceUtils.releaseConnection(c, ds);
          try { c.close(); } catch (SQLException e) { }
          logger.info("In  setCatalogToDB() - connection is released");
        }
    }


    public void setDataSource(DataSource ds) {
        this.ds = ds;
    }

}

class Comp implements Comparator {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

   public int compare (Object o1, Object o2) {
     Fields f1 = (Fields)o1, f2 = (Fields)o2;
     String v1 = f1.getVersion();
     String c1 = f1.getCycle();
     String v2 = f2.getVersion();
     String c2 = f2.getCycle();

     /* Sort by first Version (v) in descending order,
        and then by Cycle (c) in descending order. */

     if(v1.compareTo(v2) > 0) {
       //logger.info("XXX compare returns -1");
       return -1;
     }
     else if (v1.compareTo(v2) < 0) {
       //logger.info("XXX compare returns +1");
       return 1;
     }
     else {
       if(c1.compareTo(c2) > 0) {
         //logger.info("XXX compare returns -1");
         return -1;
       }
       else if(c1.compareTo(c2) < 0) {
         //logger.info("XXX compare returns +1");
         return 1;
       }
       else {
         //logger.info("XXX compare returns 0");
         return 0;
       }
     }
   }
}
 
