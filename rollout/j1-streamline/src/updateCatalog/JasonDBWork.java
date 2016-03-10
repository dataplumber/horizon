package updateCatalog;


import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;


public class JasonDBWork extends Object{
     private Connection con = null;
     private String SQLStatement = "";
     private PreparedStatement ps = null;
     private String dbURL = "";
     private String dbUser = "";
     private String dbPass = "";
     private ResultSet rs = null;
     private HashMap<String,Cycle> DB_CycleHash = new HashMap<String,Cycle>();
     private Cycle tempCycle = null;
     private int maxID = 0;
     private Date now = null;
     private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
     
     
     public JasonDBWork(){
     
     }
     
     public JasonDBWork(String dbPath, String dbUserName, String dbPassword){
     
          this.dbURL = dbPath;
          this.dbUser = dbUserName;
          this.dbPass = dbPassword;
     
     }
     
     public Connection openConnection(){
     
         try{
               Class.forName("oracle.jdbc.driver.OracleDriver");
               con = DriverManager.getConnection(this.dbURL.toString(), this.dbUser.toString(), this.dbPass.toString() );
	       return con;
	  }
	  catch (SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
	    try { con.close(); } catch (SQLException e) { }
	    return null;
	  }
	  catch (ClassNotFoundException cx) {
            System.out.println("****** Error: ClassNotFoundException: "+cx);
	    return null;
          }
	  
	  
     
     }
     
     public ResultSet execStatement(Connection c, String SQLStatement){
          try{
               ps = c.prepareStatement(SQLStatement.toString());
	       rs = ps.executeQuery();
	       return rs;
	  }
	  catch(SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
	    return null;
            
	  }
          
     }
     
     public void updateSGDR_ReleaseDate(Connection c, Cycle tempCycle){
          try{
	       now = new Date();
               String updateReleaseDate = "UPDATE catalog SET SGDR_Release_Date = '"+formatter.format(now)+
                                               "' WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          try{
               String updateReleaseDate = "UPDATE catalog SET SGDR_Staged = 'N'"+
                                               " WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          
          
     }
     
     public void updateSGDRnetCDF_ReleaseDate(Connection c, Cycle tempCycle){
          try{
	       now = new Date();
               String updateReleaseDate = "UPDATE catalog SET SGDRnetCDF_Release_Date = '"+formatter.format(now)+
                                               "' WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          try{
               String updateReleaseDate = "UPDATE catalog SET SGDRnetCDF_Staged = 'N'"+
                                               " WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          
          
     }
     
     public void updateGDR_ReleaseDate(Connection c, Cycle tempCycle){
          try{
	           now = new Date();
               String updateReleaseDate = "UPDATE catalog SET GDR_Release_Date = '"+formatter.format(now)+
                                               "' WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
               
     }
     
     public void updateGDRnetCDF_ReleaseDate(Connection c, Cycle tempCycle){
          try{
	       now = new Date();
               String updateReleaseDate = "UPDATE catalog SET GDRnetCDF_Release_Date = '"+formatter.format(now)+
                                               "' WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      } 
          try{
               String updateReleaseDate = "UPDATE catalog SET GDRnetCDF_Staged = 'N"+
                                               "' WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      } 
               
     }
     
     public void updateSSHAnetCDF_ReleaseDate(Connection c, Cycle tempCycle){
          try{
	           now = new Date();
               String updateReleaseDate = "UPDATE catalog SET SSHAnetCDF_Release_Date = '"+formatter.format(now)+
                                               "' WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          try{
               String updateReleaseDate = "UPDATE catalog SET SSHAnetCDF_Staged = 'N"+
                                               "' WHERE Cycle = '"+tempCycle.getCycle_num()+
                                               "' AND Version = '"+ tempCycle.getVersion()+
                                               "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
               //System.out.println("executing:"+updateReleaseDate);
               ps = c.prepareStatement( updateReleaseDate.toString() );
               int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
               
     }
     
     public HashMap getApprovedCycles(Connection c){

          try{
	       String  get_all_rows = "Select * FROM catalog WHERE CNES_Approved = 'Y' AND NASA_Approved = 'Y'";
               ps = c.prepareStatement(get_all_rows.toString());
               rs = ps.executeQuery();
	       
	       SQLWarning warn = c.getWarnings();
	       while(warn != null){
	            System.out.println("SQLState: "+ warn.getSQLState());
		        System.out.println("Message: "+ warn.getMessage());
		        System.out.println("Vendor: "+ warn.getErrorCode());
		        System.out.println("");
		        warn = warn.getNextWarning();
	       }

               while(rs.next()){

                    tempCycle  = new Cycle(rs.getString("Cycle"), rs.getString("Version"), rs.getString("Data_Author"),
                                           rs.getString("CNES_Approved"), rs.getString("NASA_Approved"),
                                           rs.getString("GDR_Release_Date"), 
                                           rs.getString("SGDR_Staged"), rs.getString("SGDR_Release_Date"),
					   rs.getString("SGDRnetCDF_Staged"), rs.getString("SGDRnetCDF_Release_Date"),
                                           rs.getString("GDRnetCDF_Staged"), rs.getString("GDRnetCDF_Release_Date"),
                                           rs.getString("SSHAnetCDF_Staged"), rs.getString("SSHAnetCDF_Release_Date"),
                                           rs.getString("Email_ID"));

                    DB_CycleHash.put(tempCycle.createHashKey(),tempCycle);
               }
               
	       return DB_CycleHash;
	  }
	  catch(SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
	    return null;
            
	  }
          
     }
     
     public void updateSGDR_Staged(Connection c, Cycle tempCycle){
          try{
	           String updateRow = "UPDATE catalog SET SGDR_Staged = 'Y' WHERE Cycle = '"+tempCycle.getCycle_num()+
						  "' AND Version = '"+ tempCycle.getVersion()+
						  "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
	            ps = c.prepareStatement( updateRow.toString() );
	            int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          
     
     }
     
     public void updateSGDRnetCDF_Staged(Connection c, Cycle tempCycle){
          try{
	           String updateRow = "UPDATE catalog SET SGDRnetCDF_Staged = 'Y' WHERE Cycle = '"+tempCycle.getCycle_num()+
						  "' AND Version = '"+ tempCycle.getVersion()+
						  "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
	            ps = c.prepareStatement( updateRow.toString() );
	            int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          
     
     }
     
     public void updateGDRnetCDF_Staged(Connection c, Cycle tempCycle){
          try{
	           String updateRow = "UPDATE catalog SET GDRnetCDF_Staged = 'Y' WHERE Cycle = '"+tempCycle.getCycle_num()+
						  "' AND Version = '"+ tempCycle.getVersion()+
						  "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
	            ps = c.prepareStatement( updateRow.toString() );
	            int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          
     
     }
     
     public void updateSSHAnetCDF_Staged(Connection c, Cycle tempCycle){
          try{
	           String updateRow = "UPDATE catalog SET SSHAnetCDF_Staged = 'Y' WHERE Cycle = '"+tempCycle.getCycle_num()+
						  "' AND Version = '"+ tempCycle.getVersion()+
						  "' AND Data_Author = '"+ tempCycle.getAuthor()+"'";
	            ps = c.prepareStatement( updateRow.toString() );
	            int ret = ps.executeUpdate();
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	                
	      }
          
     
     }
     
     public void insertCycle(Connection c, Cycle insertCycle){
          try{
	           String insertRow = "INSERT INTO catalog ";
	           insertRow += "(id, Cycle, Version, Data_Author, CNES_Approved, NASA_Approved, GDR_Release_Date, SGDR_Staged, SGDR_Release_Date, SGDRnetCDF_Staged, SGDRnetCDF_Release_Date, GDRnetCDF_Staged, GDRnetCDF_Release_Date, SSHAnetCDF_Staged, SSHAnetCDF_Release_date, Email_ID) ";
	           insertRow += "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
 
	           ps = c.prepareStatement(insertRow.toString());
	           ps.setInt(1, this.maxID+1);
	           ps.setString(2,insertCycle.getCycle_num());
	           ps.setString(3, insertCycle.getVersion());
	           ps.setString(4, insertCycle.getAuthor());
	           ps.setString(5, insertCycle.getCnes_approved());
                   ps.setString(6, insertCycle.getNasa_approved());
                   ps.setString(7, insertCycle.getGDR_release_date());
                   ps.setString(8, insertCycle.getSGDR_staged());
                   ps.setString(9, insertCycle.getSGDR_release_date());
		   ps.setString(10, insertCycle.getSGDRnetCDF_staged());
                   ps.setString(11, insertCycle.getSGDRnetCDF_release_date());
                   ps.setString(12, insertCycle.getGDRnetCDF_staged());
                   ps.setString(13, insertCycle.getGDRnetCDF_release_date());
                   ps.setString(14, insertCycle.getSSHAnetCDF_staged());
                   ps.setString(15, insertCycle.getSSHAnetCDF_release_date());
                   ps.setString(16, insertCycle.getEmail_id());
                   this.maxID++;
                   int ret = ps.executeUpdate();
	  }
	  catch(SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
	                
	  }
          
     
     }
     public int getMaxID(Connection c){
          try{
	       String get_max_id = "SELECT MAX(id) FROM catalog";
               ps = c.prepareStatement(get_max_id.toString());
               rs = ps.executeQuery();
               if(rs.next()){this.maxID = rs.getInt(1);}
	       
               SQLWarning warn = c.getWarnings();
	       while(warn != null){
	            System.out.println("SQLState: "+ warn.getSQLState());
		    System.out.println("Message: "+ warn.getMessage());
		    System.out.println("Vendor: "+ warn.getErrorCode());
		    System.out.println("");
		    warn = warn.getNextWarning();
	       }
	       return this.maxID;
	  }
	  catch(SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
	    return this.maxID;
            
	  }
          
     }
     public HashMap getCatalogAll(Connection c){

          try{
	           String  get_all_rows = "SELECT * FROM catalog";
               ps = c.prepareStatement(get_all_rows.toString());
               rs = ps.executeQuery();
	       
	           SQLWarning warn = c.getWarnings();
	           while(warn != null){
	                System.out.println("SQLState: "+ warn.getSQLState());
		            System.out.println("Message: "+ warn.getMessage());
		            System.out.println("Vendor: "+ warn.getErrorCode());
		            System.out.println("");
		            warn = warn.getNextWarning();
	           }

               while(rs.next()){

                    tempCycle  = new Cycle(rs.getString("Cycle"), rs.getString("Version"), rs.getString("Data_Author"),
                                           rs.getString("CNES_Approved"), rs.getString("NASA_Approved"),
                                           rs.getString("GDR_Release_Date"), 
                                           rs.getString("SGDR_Staged"), rs.getString("SGDR_Release_Date"),
					   rs.getString("SGDRnetCDF_Staged"), rs.getString("SGDRnetCDF_Release_Date"),
                                           rs.getString("GDRnetCDF_Staged"), rs.getString("GDRnetCDF_Release_Date"),
                                           rs.getString("SSHAnetCDF_Staged"), rs.getString("SSHAnetCDF_Release_Date"),
                                           rs.getString("Email_ID"));

                    DB_CycleHash.put(tempCycle.createHashKey(),tempCycle);
               }
               
	           return DB_CycleHash;
	      }
	      catch(SQLException ex) {
               System.out.println("****** Error: SQLException: "+ex);
	           return null;
            
	      }
          
     }
     
     public void closeConnection( Connection c ){
          try{    
               c.close();
	  }
	  catch(SQLException ex) {
            System.out.println("****** Error: SQLException: "+ex);
	    //return null;
            
	  }
     
     }
     
     

}
