package updateCatalog;


//import java.util.HashSet;
//import java.util.List;
//import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.List;
import java.util.ArrayList;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;


public class setTest{
  public static void main(String [] args) {

    //String dbURL = "jdbc:oracle:thin:@seadb.jpl.nasa.gov:1526:DAACDEV";
    // for LINUX
    //    "jdbc:hsqldb:/home/er/jason_streamline/db/streamlining;shutdown=true";
    // for MAC
        //"jdbc:hsqldb:/Users/pan/work/jpl/PODAAC/webapps/streamlining/db/streamlining;shutdown=true";

        Connection c = null;
        StringBuffer sB = new StringBuffer();
        StringBuffer sB1 = new StringBuffer();
	String id="1";
	int maxID = 0;
        String dbURL = "";
        String dbUser = "";
        String dbPass = "";

       //get path information from etc File
       String etcFile = "./paths.properties";
       Properties p = new Properties();
       try{
            p.load(new FileInputStream(etcFile));
            dbURL = p.getProperty("dbURL"); 
            dbUser = p.getProperty("dbUser");
            dbPass = p.getProperty("dbPass");
       }
       catch(IOException e){
            System.out.println("IOException: "+e);	  
       }



        try {
	  Class.forName("oracle.jdbc.driver.OracleDriver");
          c = DriverManager.getConnection(dbURL, dbUser, dbPass);
          //System.out.println("XXXX - Connected to DB ...");
          //sB.append("select * from catalog where id = ?");
          
          String updateApproved = "Update catalog SET CNES_Approved = 'Y', NASA_Approved='Y' WHERE SGDR_Staged = 'Y'";
          PreparedStatement ps = c.prepareStatement(updateApproved.toString());
          int ret_val = ps.executeUpdate();
          System.out.println("XXXX - executed:"+updateApproved);


          System.out.println("XXXX - DB update Test done. ");
        }
        catch (SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
            try { c.close(); } catch (SQLException e) { }
	}
	catch (ClassNotFoundException cx) {
            System.out.println("****** Error: ClassNotFoundException: "+cx);
        } finally {
            // properly release our connection
            try { c.close(); } catch (SQLException e) { }
            //System.out.println("Connection to DB is released.");
        }
  }
}
