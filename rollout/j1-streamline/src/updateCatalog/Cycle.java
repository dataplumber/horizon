package updateCatalog;

import java.util.regex.*;
import java.lang.String;

public class Cycle extends Object{
     private String cycle_num; //nnn cycle number
     private String version; //A, B, C... etc
     private String author; //nasa or cnes
     private String cnes_approved;
     private String nasa_approved;
     private String GDR_release_date;
     private String SGDR_staged;
     private String SGDR_release_date;
     private String SGDRnetCDF_staged;
     private String SGDRnetCDF_release_date;
     private String email_id;
     private String GDRnetCDF_staged;
     private String GDRnetCDF_release_date;
     private String SSHAnetCDF_staged;
     private String SSHAnetCDF_release_date;
     
     public Cycle(){
     
     }
     
     /*public Cycle(){
          
	  this.cycle_num = "";
	  //this.version = "";
	  this.author = "";
	  this.cnes_approved = false;
	  this.nasa_approved = false;
	  this.GDR_release_date = "";
	  this.SGDR_staged = false;
	  this.SGDR_release_date = "";
	  this.email_id = "";
     
     }*/
     public Cycle(String cycle_num, String version, String author,
                  String cnes_approved, String nasa_approved,
		  String GDR_release_date, 
                  String SGDR_staged, String SGDR_release_date,
		  String SGDRnetCDF_staged, String SGDRnetCDF_release_date,
                  String GDRnetCDF_staged, String GDRnetCDF_release_date,
                  String SSHAnetCDF_staged, String SSHAnetCDF_release_date,
                  String email_id){
          
	  this.cycle_num = cycle_num;
	  this.version = version;
	  this.author = author;
	  this.cnes_approved = cnes_approved;
	  this.nasa_approved = nasa_approved;
	  this.GDR_release_date = GDR_release_date;
	  this.SGDR_staged = SGDR_staged;
	  this.SGDR_release_date = SGDR_release_date;
	  this.SGDRnetCDF_staged = SGDRnetCDF_staged;
	  this.SGDRnetCDF_release_date = SGDRnetCDF_release_date;
	  this.GDRnetCDF_staged = GDRnetCDF_staged;
	  this.GDRnetCDF_release_date = GDRnetCDF_release_date;
          this.SSHAnetCDF_staged = SSHAnetCDF_staged;
	  this.SSHAnetCDF_release_date = SSHAnetCDF_release_date;
          this.email_id = email_id;
     
     }
     
     public Cycle(String filename){
          String ver = "";
	  String cn = "";
	  String auth = "";
	  
	  
          Pattern pattern = Pattern.compile("JA1_GDR_2P(\\w)P(\\d{3})_\\d{3}.(NASA|CNES)");
	  Matcher matcher = pattern.matcher(filename);
	  boolean matchFound = matcher.find();
	  
	  if(matchFound){
	       
	       ver = matcher.group(1);
	       ver = ver.toUpperCase();
	       cn = matcher.group(2);
	       auth = matcher.group(3);
	       
	  }
	  
	  this.cycle_num = cn;
	  this.version = ver;
	  this.author = auth;
	  this.cnes_approved = "N";
	  this.nasa_approved = "N";
	  this.GDR_release_date = "(N/A)";
	  this.SGDR_staged = "N";
	  this.SGDR_release_date = "(N/A)";
	  this.SGDRnetCDF_staged = "N";
	  this.SGDRnetCDF_release_date = "(N/A)";
          this.GDRnetCDF_staged = "N";
	  this.GDRnetCDF_release_date = "(N/A)";
          this.SSHAnetCDF_staged = "N";
	  this.SSHAnetCDF_release_date = "(N/A)";
	  this.email_id = "(N/A)";
     
     }
     
     public void print(){
          System.out.println("CYCLE NUM: "+cycle_num);
	  System.out.println("Version: "+version);
	  System.out.println("Data Author: "+author);
	  System.out.println("CNES Approved:"+cnes_approved);
	  System.out.println("NASA Approved:"+nasa_approved);
	  System.out.println("GDR Release Date: "+GDR_release_date);
	  System.out.println("SGDR Staged: "+SGDR_staged);
	  System.out.println("SGDR Release Date: "+SGDR_release_date);
	  System.out.println("SGDRnetCDF Staged: "+SGDR_staged);
	  System.out.println("SGDRnetCDF Release Date: "+SGDR_release_date);
	  System.out.println("GDRnetCDF Staged: "+GDRnetCDF_staged);
	  System.out.println("GDRnetCDF Release Date: "+GDRnetCDF_release_date);
          System.out.println("SSHAnetCDF Staged: "+SSHAnetCDF_staged);
	  System.out.println("SSHAnetCDF Release Date: "+SSHAnetCDF_release_date);
	  System.out.println("Email ID: "+email_id);
     }

     public String createHashKey(){
          return (String) this.cycle_num + "-" + this.author +"-" + this.version;
     }
     
     public void setCycle_num(String cycle_num){
          this.cycle_num = cycle_num;
     }
     
     public void setVersion(String version){
          this.version = version;
     }
     
     public void setAuthor(String author){
          this.author = author;
     }
     
     public void setCNES_approved(String CNES_approved){
          this.cnes_approved = CNES_approved;
     }
     
     public void setNASA_approved(String NASA_approved){
          this.nasa_approved = NASA_approved;
     }
     
     public void setGDR_release_date(String GDR_release_date){
          this.GDR_release_date = GDR_release_date;
     }
          
     public void setSGDR_staged(String SGDR_staged){
          this.SGDR_staged = SGDR_staged;
     }
     
     public void setSGDR_release_date(String SGDR_release_date){
          this.SGDR_release_date=SGDR_release_date;
     }
     
     public void setSGDRnetCDF_staged(String SGDRnetCDF_staged){
          this.SGDRnetCDF_staged = SGDRnetCDF_staged;
     }
     
     public void setSGDRnetCDF_release_date(String SGDRnetCDF_release_date){
          this.SGDRnetCDF_release_date=SGDRnetCDF_release_date;
     }
     public void setGDRnetCDF_staged(String GDRnetCDF_staged){
          this.GDRnetCDF_staged = GDRnetCDF_staged;
     }
     
     public void setGDRnetCDF_release_date(String GDRnetCDF_release_date){
          this.GDRnetCDF_release_date=GDRnetCDF_release_date;
     }
     
     public void setSSHAnetCDF_staged(String SSHAnetCDF_staged){
          this.SSHAnetCDF_staged = SSHAnetCDF_staged;
     }
     
     public void setSSHAnetCDF_release_date(String SSHAnetCDF_release_date){
          this.SSHAnetCDF_release_date=SSHAnetCDF_release_date;
     }
 
     public void setEmail_id(String email_id){
          this.email_id = email_id;
     }
     
     public String getCycle_num(){
          return this.cycle_num;
     }
     
     public String getVersion(){
          return this.version;
     }
     
     public String getAuthor(){
          return this.author;
     }
     
     public String getCnes_approved(){
          return this.cnes_approved;
     }
     
     public String getNasa_approved(){
          return this.nasa_approved;
     }
     
     public String getGDR_release_date(){
          return this.GDR_release_date;
     }
     
     public String getSGDR_staged(){
          return this.SGDR_staged;
     }
     
     public String getSGDR_release_date(){
          return this.SGDR_release_date;
     }
       
     public String getSGDRnetCDF_staged(){
          return this.SGDRnetCDF_staged;
     }
     
     public String getSGDRnetCDF_release_date(){
          return this.SGDRnetCDF_release_date;
     }
    
     public String getGDRnetCDF_staged(){
          return this.GDRnetCDF_staged;
     }
     
     public String getGDRnetCDF_release_date(){
          return this.GDRnetCDF_release_date;
     }

     public String getSSHAnetCDF_staged(){
          return this.SSHAnetCDF_staged;
     }
     
     public String getSSHAnetCDF_release_date(){
          return this.SSHAnetCDF_release_date;
     }
 
     public String getEmail_id(){
          return this.email_id;
     }

}
