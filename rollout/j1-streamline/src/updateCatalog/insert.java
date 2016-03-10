package updateCatalog;

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
import java.util.HashMap;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import org.apache.log4j.*;

public class insert {
  private static Cycle InsertCycles[];
  static Category log = Category.getInstance(insert.class.getName());

  public static void main(String [] args) {
       Connection c = null;
       int maxID = 0;
       HashMap DB_CycleHash = new HashMap(); 
       String gdrDir = "";
       String sgdrDir = "";
       String dbURL = "";
       String dbUser = "";
       String dbPass = "";
       String tmpGPNDir = "";
       String tmpGPRDir = "";
       String tmpGPSDir = "";
       String tmpSGDRDir = "";
       File stagedSGDR = null;
       File stagedSGDRnetCDF = null;
       File stagedGDRnetCDF = null;
       File stagedSSHAnetCDF = null;
       Iterator DBCycleIterator = null;
       Cycle tempCycle = null;
       JasonDBWork dbcon = null;
       initLog4j logger = new initLog4j(log);
       log = logger.getLog();

       //get path information from etc File
       String etcFile = "./paths.properties";
       Properties p = new Properties();
       try{
            p.load(new FileInputStream(etcFile));
            gdrDir = p.getProperty("gdr_dir");
            sgdrDir = p.getProperty("sgdr_dir");
            dbURL = p.getProperty("dbURL"); 
            dbUser = p.getProperty("dbUser");
            dbPass = p.getProperty("dbPass");
       }
       catch(IOException e){
            log.warn("IOException: "+e);	  
       }

       try {
          dbcon = new JasonDBWork(dbURL, dbUser, dbPass);
          c = dbcon.openConnection();
          //log.info("XXXX - Connected to DB ...");
          
          //get all contents of catalog then put into cycle object then place objects into hashMap
          DB_CycleHash = dbcon.getCatalogAll(c);
          
          //get max id
          maxID = dbcon.getMaxID(c);
          
          //log.info("sniffing GDRs in: "+gdrDir.toString());
          ReadJasonGDRDir cnes_dir = new ReadJasonGDRDir(gdrDir.toString());
          String[] files = cnes_dir.getList();
          InsertCycles = new Cycle[files.length];
          for(int i=0; i<files.length; i++){
               InsertCycles[i] = new Cycle(files[i]);
          
	       // insert a row to the DB if row does not already exist
               if(!DB_CycleHash.containsKey(InsertCycles[i].createHashKey())){
                    //check and see if SGDR is staged for cycle in question
                    //if it is change cycle attribute "SGDR_staged" to true
                                 // 6-18-12 YC: add _geodetic after cnes|nasa author
                                 //InsertCycles[i].getAuthor().toLowerCase()+
                                 // 5-2-11 YC: removed /data to work with new archive
                                 //"/data/c"+InsertCycles[i].getCycle_num();
                    tmpSGDRDir = sgdrDir+"/sgdr_"+InsertCycles[i].getVersion().toLowerCase()+"_"+
                                 InsertCycles[i].getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+InsertCycles[i].getCycle_num();
                    tmpGPNDir  = gdrDir+"/gdr_netcdf_"+InsertCycles[i].getVersion().toLowerCase()+"_"+
                                 InsertCycles[i].getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+InsertCycles[i].getCycle_num();
                    tmpGPSDir  = sgdrDir+"/sgdr_netcdf_"+InsertCycles[i].getVersion().toLowerCase()+"_"+
                                 InsertCycles[i].getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+InsertCycles[i].getCycle_num();
                    tmpGPRDir  = gdrDir+"/gdr_ssha_netcdf_"+InsertCycles[i].getVersion().toLowerCase()+"_"+
                                 InsertCycles[i].getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+InsertCycles[i].getCycle_num();

                    //System.out.println("SGDRDIR: " + tmpSGDRDir);
                    //System.out.println("GPNDIR:  " + tmpGPNDir);
                    //System.out.println("GPSDIR:  " + tmpGPSDir);
                    //System.out.println("GPRDIR:  " + tmpGPRDir);


                    stagedSGDR = new File(tmpSGDRDir);
		    stagedSGDRnetCDF = new File(tmpGPSDir);
                    stagedGDRnetCDF = new File(tmpGPNDir);
                    stagedSSHAnetCDF = new File(tmpGPRDir);
                    if(stagedSGDR.exists()){
                         InsertCycles[i].setSGDR_staged("Y");
                    }
		    if(stagedSGDRnetCDF.exists()){
                         InsertCycles[i].setSGDRnetCDF_staged("Y");
                    }
                    if(stagedGDRnetCDF.exists()){
                          InsertCycles[i].setGDRnetCDF_staged("Y");
                    }
                    if(stagedSSHAnetCDF.exists()){
                          InsertCycles[i].setSSHAnetCDF_staged("Y");
                    }
                    log.info("Inserting "+InsertCycles[i].createHashKey()+" into catalog");
                    dbcon.insertCycle(c,InsertCycles[i]);
                    
               }
          } 
          
          //now update sgdr_stage SGDRs can be delivered anytime after GDR batches are delivered
          //must check for staged SGDRs everytime new cycles are inserted into db
          //implemented update as SQL update becaus HSQL 1.8 does note support updatable ResultSet 
          //Current database is now oracle but algorithm has not been changed from above -er 09/27
          DBCycleIterator = DB_CycleHash.keySet().iterator();
          while( DBCycleIterator.hasNext() ){
               tempCycle = (Cycle)DB_CycleHash.get(DBCycleIterator.next()); 
                                 // 6-18-12 YC: add _geodetic after cnes|nasa author
                                 //tempCycle.getAuthor().toLowerCase()+
                    tmpSGDRDir = sgdrDir+"/sgdr_"+tempCycle.getVersion().toLowerCase()+"_"+
                                 tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+tempCycle.getCycle_num();
                    tmpGPNDir  = gdrDir+"/gdr_netcdf_"+tempCycle.getVersion().toLowerCase()+"_"+
                                 tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+tempCycle.getCycle_num();
                    tmpGPSDir  = sgdrDir+"/sgdr_netcdf_"+tempCycle.getVersion().toLowerCase()+"_"+
                                 tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+tempCycle.getCycle_num();
                    tmpGPRDir  = gdrDir+"/gdr_ssha_netcdf_"+tempCycle.getVersion().toLowerCase()+"_"+
                                 tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                 "/c"+tempCycle.getCycle_num();

                    stagedSGDR = new File(tmpSGDRDir);
		    stagedSGDRnetCDF = new File(tmpGPSDir);
                    stagedGDRnetCDF = new File(tmpGPNDir);
                    stagedSSHAnetCDF = new File(tmpGPRDir);

               if(tempCycle.getSGDR_staged().equals("N")){
                   
                    if(stagedSGDR.exists()){
                         dbcon.updateSGDR_Staged(c,tempCycle);
                         log.info("updated SDGR_Staged: "+ tempCycle.createHashKey());
                    }
               }
	       if(tempCycle.getSGDRnetCDF_staged().equals("N")){
                   
                    if(stagedSGDRnetCDF.exists()){
                         dbcon.updateSGDRnetCDF_Staged(c,tempCycle);
                         log.info("updated SDGRnetCDF_Staged: "+ tempCycle.createHashKey());
                    }
               }
               if(tempCycle.getGDRnetCDF_staged().equals("N")){
                    if(stagedGDRnetCDF.exists()){
                         dbcon.updateGDRnetCDF_Staged(c,tempCycle);
                         log.info("updated GDRnetCDF_Staged: "+ tempCycle.createHashKey());
                    }
               }
               if(tempCycle.getSSHAnetCDF_staged().equals("N")){
                    if(stagedSSHAnetCDF.exists()){
                         dbcon.updateSSHAnetCDF_Staged(c,tempCycle);
                         log.info("updated SSHAnetCDF_Staged: "+ tempCycle.createHashKey());
                    }
               }
          }
          //log.info("XXXX - DB insertion done. ");
        }
        catch (NullPointerException e) {
            log.fatal("****** Error: NullPointerException: "+e);
	}
        finally {
            dbcon.closeConnection(c);
            //log.info("Connection to DB is released.");
        }
  }
}
