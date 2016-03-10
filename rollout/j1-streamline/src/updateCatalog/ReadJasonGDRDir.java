package updateCatalog;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.log4j.*;



public class  ReadJasonGDRDir extends Object{
     static Category log = Category.getInstance(insert.class.getName());
     private String PassFilesArray[];
     private ArrayList<String> PassFilesList = new ArrayList<String> ();
          
     public ReadJasonGDRDir(String input_dir){
          initLog4j logger = new initLog4j(log);
          log = logger.getLog();
	  
	  String[] dir_list = null;
	  
          
          
          File dir = new File(input_dir);
	     
	  FilenameFilter filter_gdr_version = new FilenameFilter(){
               public boolean accept(File dir, String name){
	  	    if( name.matches("gdr_(nasa|cnes).+")){
	   		 File temp = new File(dir, name);     
          		 return temp.isDirectory();
	   	    }
	   	    else{
	   	    
	   		 return false;
	   	   }
              } 
          };

	  FilenameFilter filter_cycle_dirs = new FilenameFilter(){
               public boolean accept(File dir, String name){
	       
	   	    if( name.matches("c\\d{3}")){
	   		 File temp = new File(dir, name);     
          		 return temp.isDirectory();
	   	    }
	   	    else{
	   		 return false;
	   	    }
               } 
          };
	  
	  FilenameFilter remove_starting_dot = new FilenameFilter(){
	       public boolean accept(File d, String fn){
	  	    return !fn.startsWith(".");
	       }
	  
	  };
	  
	  
	  try{
	       log.info("looking for GDR Binary staged directories in " + input_dir);
	       dir_list  = dir.list(filter_gdr_version);
	       if(dir_list.length == 0){
	            log.warn("****** 0 GDR Binary staged directories found in: " + input_dir );
	       }
	       
	  }
	  
	  catch (NullPointerException e) {
               log.warn("****** Error: IOException: "+e);
	       if(dir_list == null ){
	            log.warn("****** 0 GDR Binary staged directories found in: " + input_dir );
		    //log.warn("****** EXITING!!!");
	       }
	       //System.exit(1);
          }
	  
	  
	  Pattern version_AorB_pattern = Pattern.compile("(NASA|CNES)$");
          Pattern NASAorCNES_pattern   = Pattern.compile("gdr_(nasa|cnes)_");
	       
	       
	  for (int i=0; i<dir_list.length; i++){
	       
	       // 5-2-11 YC:  removed /data to work with new archive layout
	       //File examine_gdr_version_dir = new File(dir,dir_list[i]+"/data");
	       File examine_gdr_version_dir = new File(dir,dir_list[i]);
	       String[] cycle_dirs = null;
	       
	       try {
	       
	            log.info("looking for cycle directories in: " + examine_gdr_version_dir.toString() );
	            cycle_dirs = examine_gdr_version_dir.list(filter_cycle_dirs);
		    if(cycle_dirs.length == 0){
		         //log.warn("***** 0 Cycle directories in: "+ dir.toString() + "/" + dir_list[i].toString() + "/data");
		         log.warn("***** 0 Cycle directories in: "+ dir.toString() + "/" + dir_list[i].toString() );
		    }
		    
	       }
	       catch (NullPointerException e) {
                    log.warn("****** Error: IOException: "+e);
		    if(cycle_dirs == null){
		         //log.warn("0 Cycle directories in: "+ dir.toString() + "/" + dir_list[i].toString() + "/data");
		         log.warn("0 Cycle directories in: "+ dir.toString() + "/" + dir_list[i].toString() );
		    }
               }
		    
	            for (int j=0; j<cycle_dirs.length; j++){
		    
	                 //String fullPath = input_dir+"/"+dir_list[i]+"/data/"+cycle_dirs[j];
	                 String fullPath = input_dir+"/"+dir_list[i]+"/"+cycle_dirs[j];
	                 File examine_cycle = new File(examine_gdr_version_dir,cycle_dirs[j]);
	                 String[] pass_files = examine_cycle.list(remove_starting_dot);
                         Matcher version_AorB_match = version_AorB_pattern.matcher(pass_files[0]);
			 
                         if(version_AorB_match.find()){
		              this.PassFilesList.add(fullPath + "/" + pass_files[0]);
                         }
                         else{
                              Matcher NASAorCNES_match = NASAorCNES_pattern.matcher(dir_list[i]);
                              if(NASAorCNES_match.find()){
                                   String auth = NASAorCNES_match.group(1);
                                   //System.out.println("FOUND NASA OR CNES!");
                                   auth = auth.toUpperCase();
                                   this.PassFilesList.add(fullPath + "/" + pass_files[0]+"."+auth);
                              }
                              //String auth = NASAorCNES_match.group(1);
                              //System.out.println("FIND NASA OR CNES:"+dir_list[i]);
                         }
	       
	            }
	       
	 }
	  
	  	
     }
     
     public String[] getList(){
          PassFilesArray = new String[PassFilesList.size()];
	  PassFilesArray = this.PassFilesList.toArray(PassFilesArray);
          return this.PassFilesArray;
     }
     
     ///used for testing
     /*public static void main (String [] args){
          ReadJasonGDRDir nasa_dir = new ReadJasonGDRDir("/home/er/store/jason1/restricted");
	  String[] files = nasa_dir.getList();
          //import java.util.regex.*;

	  for(int i=0; i<files.length; i++){
	       System.out.println(files[i]);
               

	  }
	  
	  
     }*/
     
     
}
