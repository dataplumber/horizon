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
//import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.io.File;
import java.util.Date;
import org.apache.log4j.*;


public class releaseData{
  static Category log = Category.getInstance(releaseData.class.getName());
  public static void main(String [] args) {
     
     String etcFile = "./paths.properties";
     JasonDBWork dbcon = null;
     Connection c = null;
     String gdrDir = "";
     String sgdrDir = "";
     String dbURL = "";
     String dbUser = "";
     String dbPass = "";
     String gdrReleaseDir = "";
     String sgdrReleaseDir = "";
     Cycle tempCycle = null;
     HashMap DB_CycleHash = new HashMap();
     Iterator DBCycleIterator = null;
     boolean moveSuccess = false;
     File source = null;
     File destination = null;
     initLog4j logger = new initLog4j(log);
     log = logger.getLog();





     //get path information from etFile
     Properties p = new Properties();
     try{
          p.load(new FileInputStream(etcFile));
          gdrDir = p.getProperty("gdr_dir");
          sgdrDir = p.getProperty("sgdr_dir");
          dbURL = p.getProperty("dbURL");
          dbUser = p.getProperty("dbUser");
          dbPass = p.getProperty("dbPass");
          gdrReleaseDir = p.getProperty("gdrRelease");
          sgdrReleaseDir = p.getProperty("sgdrRelease");
 
     }
     catch(IOException e){
         log.warn("IOException: "+e); 
     }

     try {
          dbcon = new JasonDBWork(dbURL, dbUser, dbPass);
          c = dbcon.openConnection(); 
          //log.info("XXXX - Connected to DB ...");
          
          DB_CycleHash = dbcon.getApprovedCycles(c);
          DBCycleIterator = DB_CycleHash.keySet().iterator();
          while( DBCycleIterator.hasNext() ){
               tempCycle = (Cycle)DB_CycleHash.get(DBCycleIterator.next());
               if(tempCycle.getCnes_approved().equals("Y") && tempCycle.getNasa_approved().equals("Y") &&
                  tempCycle.getGDR_release_date().equals("(N/A)") 
                 )
               {
                    // 6-18-12 YC: geodetic: jason1/shared/L2/gdr_cnes_c_geodetic, gdr_nasa_c_geodetic
                    //source = new File(gdrDir+"/gdr_"+tempCycle.getAuthor().toLowerCase()+"_"+tempCycle.getVersion().toLowerCase()+
                    // open geodetic: /store/jason1/open/L2/gdr_c_geodetic
                    //destination = new File(gdrReleaseDir+"/gdr_"+tempCycle.getVersion().toLowerCase()+"/data/c"+
                    // 5-2-11 YC: removed /data to work with new archive
                    //                     "/data/c"+tempCycle.getCycle_num());
                    source = new File(gdrDir+"/gdr_"+tempCycle.getAuthor().toLowerCase()+"_"+tempCycle.getVersion().toLowerCase()+"_geodetic"+
                                           "/c"+tempCycle.getCycle_num());
                    destination = new File(gdrReleaseDir+"/gdr_"+tempCycle.getVersion().toLowerCase()+"_geodetic"+"/c"+
                                         tempCycle.getCycle_num());
                    //move directory then update db
                    moveSuccess = source.renameTo(destination);
                    if(!moveSuccess){
                         log.warn("GDR MOVE FAILED from: "+source.toString()+" to: "+destination.toString());
                    }
                    else{
                         dbcon.updateGDR_ReleaseDate(c, tempCycle);
                         log.info("Cycle GDRs: "+tempCycle.createHashKey()+" released.");
                    }
               }
               //if sgdr_staged move sgdr data
               if( tempCycle.getCnes_approved().equals("Y") && tempCycle.getNasa_approved().equals("Y") && 
                   tempCycle.getSGDR_staged().equals("Y") && tempCycle.getSGDR_release_date().equals("(N/A)")
                 )
               {
                    // 6-18-12 YC: geodetic: jason1/shared/L2/sgdr_c_cnes_geodetic, sgdr_c_nasa_geodetic
                    //                       "_"+tempCycle.getAuthor().toLowerCase()+
                    // open geodetic: /store/jason1/open/L2/sgdr_c_geodetic
                    //destination = new File(sgdrReleaseDir+"/sgdr_"+tempCycle.getVersion().toLowerCase()+"/c"+
                    // 5-2-11 YC: removed /data to work with new archive
                    //                       "/data/c"+tempCycle.getCycle_num());
                    //destination = new File(sgdrReleaseDir+"/sgdr_"+tempCycle.getVersion().toLowerCase()+"/data/c"+
                    source = new File(sgdrDir+"/sgdr_"+tempCycle.getVersion().toLowerCase()+
                                           "_"+tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                           "/c"+tempCycle.getCycle_num());
                    destination = new File(sgdrReleaseDir+"/sgdr_"+tempCycle.getVersion().toLowerCase()+"_geodetic"+"/c"+
                                         tempCycle.getCycle_num());

                    //move directory then update db
                    moveSuccess = source.renameTo(destination);
                    if(!moveSuccess){
                         log.warn("SGDR MOVE FAILED from: "+source.toString()+" to: "+destination.toString());
                    }
                    else{
                         dbcon.updateSGDR_ReleaseDate(c, tempCycle);
                         log.info("Cycle SGDRs: "+tempCycle.createHashKey()+" released.");
                    } 
               }
	       //if sgdrnetCDF_staged move sgdr netCDF data
               if( tempCycle.getCnes_approved().equals("Y") && tempCycle.getNasa_approved().equals("Y") && 
                   tempCycle.getSGDRnetCDF_staged().equals("Y") && tempCycle.getSGDRnetCDF_release_date().equals("(N/A)")
                 )
               {
                    // 6-18-12 YC: geodetic: shared/L2/sgdr_netcdf_c_cnes_geodetic, sgdr_netcdf_c_nasa_geodetic
                    //                       "_"+tempCycle.getAuthor().toLowerCase()+
                    // open geodetic: /store/jason1/open/L2/sgdr_netcdf_c_geodetic
                    //destination = new File(sgdrReleaseDir+"/sgdr_"+tempCycle.getVersion().toLowerCase()+"/c"+
                    // 5-2-11 YC: removed /data to work with new archive
                    //                       "/data/c"+tempCycle.getCycle_num());
                    //destination = new File(sgdrReleaseDir+"/sgdr_netcdf_"+tempCycle.getVersion().toLowerCase()+"/data/c"+
                    source = new File(sgdrDir+"/sgdr_netcdf_"+tempCycle.getVersion().toLowerCase()+
                                           "_"+tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                           "/c"+tempCycle.getCycle_num());
                    destination = new File(sgdrReleaseDir+"/sgdr_netcdf_"+tempCycle.getVersion().toLowerCase()+"_geodetic"+"/c"+
                                         tempCycle.getCycle_num());

                    //move directory then update db
                    moveSuccess = source.renameTo(destination);
                    if(!moveSuccess){
                         log.warn("SGDRnetCDF MOVE FAILED from: "+source.toString()+" to: "+destination.toString());
                    }
                    else{
                         dbcon.updateSGDRnetCDF_ReleaseDate(c, tempCycle);
                         log.info("Cycle SGDRnetCDFs: "+tempCycle.createHashKey()+" released.");
                    } 
               }
               // if gdrnetCDF_staged move GDRnetCDF data
               if( tempCycle.getCnes_approved().equals("Y") && tempCycle.getNasa_approved().equals("Y") && 
                   tempCycle.getGDRnetCDF_staged().equals("Y") && tempCycle.getGDRnetCDF_release_date().equals("(N/A)")
                 )
               {
                    // 6-18-12 YC: geodetic: shared/L2/gdr_netcdf_c_cnes_geodetic, gdr_netcdf_c_nasa_geodetic
                    //                       "_"+tempCycle.getAuthor().toLowerCase()+
                    // open geodetic: /store/jason1/open/L2/gdr_netcdf_c_geodetic
                    //destination = new File(gdrReleaseDir+"/gdr_netcdf_"+tempCycle.getVersion().toLowerCase()+"/c"+
                    // 5-2-11 YC: removed /data to work with new archive
                                           //"/data/c"+tempCycle.getCycle_num());
                    //destination = new File(gdrReleaseDir+"/gdr_netcdf_"+tempCycle.getVersion().toLowerCase()+"/data/c"+
                    source = new File(gdrDir+"/gdr_netcdf_"+tempCycle.getVersion().toLowerCase()+
                                           "_"+tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                           "/c"+tempCycle.getCycle_num());
                    destination = new File(gdrReleaseDir+"/gdr_netcdf_"+tempCycle.getVersion().toLowerCase()+"_geodetic"+"/c"+
                                         tempCycle.getCycle_num());

                    //move directory then update db
                    moveSuccess = source.renameTo(destination);
                    if(!moveSuccess){
                         log.warn("GDRnetCDF MOVE FAILED from: "+source.toString()+" to: "+destination.toString());
                    }
                    else{
                         dbcon.updateGDRnetCDF_ReleaseDate(c, tempCycle);
                         log.info("Cycle GDRnetCDFs: "+tempCycle.createHashKey()+" released.");
                    } 
               }
               //if SSHAnetCDF_staged move SSHAnetCDF data
               if( tempCycle.getCnes_approved().equals("Y") && tempCycle.getNasa_approved().equals("Y") && 
                   tempCycle.getSSHAnetCDF_staged().equals("Y") && tempCycle.getSSHAnetCDF_release_date().equals("(N/A)")
                 )
               {
                    // 6-18-12 YC: geodetic: shared/L2/gdr_ssha_netcdf_c_cnes_geodetic, gdr_ssha_netcdf_c_nasa_geodetic
                    //                       "_"+tempCycle.getAuthor().toLowerCase()+
                    // open geodetic: /store/jason1/open/L2/gdr_ssha_netcdf_c_geodetic
                    //destination = new File(gdrReleaseDir+"/gdr_ssha_netcdf_"+tempCycle.getVersion().toLowerCase()+"/c"+
                    // 5-2-11 YC: removed /data to work with new archive
                    //                     "/data/c"+tempCycle.getCycle_num());
                    //destination = new File(gdrReleaseDir+"/gdr_ssha_netcdf_"+tempCycle.getVersion().toLowerCase()+"/data/c"+
                    source = new File(gdrDir+"/gdr_ssha_netcdf_"+tempCycle.getVersion().toLowerCase()+
                                           "_"+tempCycle.getAuthor().toLowerCase()+"_geodetic"+
                                           "/c"+tempCycle.getCycle_num());
                    destination = new File(gdrReleaseDir+"/gdr_ssha_netcdf_"+tempCycle.getVersion().toLowerCase()+"_geodetic"+"/c"+
                                         tempCycle.getCycle_num());

                    //move directory then update db
                    moveSuccess = source.renameTo(destination);
                    if(!moveSuccess){
                         log.warn("SSHAnetCDF MOVE FAILED from: "+source.toString()+" to: "+destination.toString());
                    }
                    else{
                         dbcon.updateSSHAnetCDF_ReleaseDate(c, tempCycle);
                         log.info("Cycle SSHAnetCDFs: "+tempCycle.createHashKey()+" released.");
                    } 
               }

               
          
          }

        }
        catch (NullPointerException ex) {
            log.fatal("****** Error: NullPointerException: "+ex);
	}
        finally {
            dbcon.closeConnection(c);
            //log.info("Connection to DB is released.");
        }
  }
}
