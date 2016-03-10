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
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;


public class read {
  public static void main(String [] args) {

    //String dbURL = "jdbc:oracle:thin:@seadb.jpl.nasa.gov:1526:DAACDEV";
    // for LINUX
    //    "jdbc:hsqldb:/home/er/jason_streamline/db/streamlining;shutdown=true";
    
    // for MAC
        //"jdbc:hsqldb:/Users/pan/work/jpl/PODAAC/webapps/streamlining/db/streamlining;shutdown=true";

        Connection c = null;
        StringBuffer sB = new StringBuffer();
	String id="1";
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
	  //Class.forName("org.hsqldb.jdbcDriver");
	  Class.forName("oracle.jdbc.driver.OracleDriver");
          c = DriverManager.getConnection(dbURL, dbUser, dbPass );
          //System.out.println("XXXX - Connected to DB streamlining ...");
          //sB.append("select * from catalog where id = ?");
          sB.append("select * from catalog");
          PreparedStatement ps = c.prepareStatement(sB.toString());
          //ps.setString(1, id);
          ResultSet rs = ps.executeQuery();

          while(rs.next()) {
            System.out.println("XXXX - " + 
	               rs.getString("id") + ", " +
	               rs.getString("Cycle") + ", " +
	               rs.getString("Version") + ", " +
	               rs.getString("Data_Author") + ", " +
	               rs.getString("CNES_Approved") + ", " +
	               rs.getString("NASA_Approved") + ", " +
	               rs.getString("GDR_Release_Date") + ", " +
	               rs.getString("SGDR_Staged") + ", " +
	               rs.getString("SGDR_Release_Date") + ", " +
		       rs.getString("SGDRnetCDF_Staged") + ", " +
	               rs.getString("SGDRnetCDF_Release_Date") + ", " +
	               rs.getString("GDRnetCDF_Staged") + ", " +
	               rs.getString("GDRnetCDF_Release_Date") + ", " +
	               rs.getString("SSHAnetCDF_Staged") + ", " +
	               rs.getString("SSHAnetCDF_Release_Date") + ", " +
                       rs.getString("Email_ID"));
          }
        }
        catch (SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
            try { c.close(); } catch (SQLException e) { }
	}
	catch (ClassNotFoundException cx) {
            cx.printStackTrace();
            System.out.println("****** Error: ClassNotFoundException: "+cx);
        } finally {
            // properly release our connection
            try { c.close(); } catch (SQLException e) { }
            //System.out.println("Connection to DB is released.");
        }
  }
}
