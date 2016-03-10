package gov.nasa.horizon.inventory.api

/**
 * This class contains constants used by Inventory Program Set.
 *
 * @author clwong
 *
 * @version
 */
public class Constant {
   
   public enum AccessType {
      //PUBLIC {PREVIEW, OPEN, RETRIED, REDUCED}
      //RESTRICTED {SHARED, CONTROLLED}
      OPEN("OPEN"), PREVIEW("PREVIEW"),SIMULATED("SIMULATED"), RETIRED("RETIRED"), REDUCED("REDUCED"), PUBLIC("PUBLIC"), PRIVATE("PRIVATE"), RESTRICTED("RESTRICTED"), SHARED("SHARED"),CONTROLLED("CONTROLLED"), DORMANT("DORMANT");  
      private AccessType(String type) {}
    };

    public enum VersionPolicy{
      ALL("ALL"), LATEST("LATEST");
      private String Policy;
      private VersionPolicy(String type){
         this.Policy = type;
      }
      public String toString(){
         return this.Policy;
      }
      public static VersionPolicy VersionPolicy(String val){
         if(val.equals("ALL"))
            return ALL;
         else if(val.equals("LATEST"))
            return LATEST;
         else
            return null;
      }
    }
    
   public enum ProductStatus {
      ONLINE("ONLINE"), OFFLINE("OFFLINE");
      private String Status;
      private GranuleStatus(String type) {
         
         this.Status = type;
      }
      
      public String toString()
      {
         return this.Status;
      }
      
      public static ProductStatus ValueOf(String val)
      {
         if(val.equals("ONLINE"))
            return ONLINE;
         else
            return OFFLINE;
         //return null;
      }
      
      public String getID()
      {
         return this.Status;
      }
    };
    
    public enum ProductArchiveType {
      DATA("DATA"), IMAGE("IMAGE"), GRIB("GRIB"), CHECKSUM("CHECKSUM"), METADATA("METADATA");
      private ProductArchiveType(String type) {}
    }
    
   public enum ProductArchiveStatus {
       ONLINE("ONLINE"), CORRUPTED("CORRUPTED"), DELETED("DELETED"), MISSING("MISSING"), IN_PROCESS("IN-PROCESS"), ANOMALY("ANOMALY");    
       private final String type;
      private ProductArchiveStatus(String type) {
         this.type = type;
      }
      public String toString(){
         return this.type;
      }
    };
    
    public enum AppendBasePathType {
      YEAR_MONTH_DAY("YEAR-MONTH-DAY"),YEAR_DOY("YEAR-DOY"), YEAR("YEAR"), YEAR_MONTH("YEAR-MONTH"), YEAR_WEEK("YEAR-WEEK"), BATCH("BATCH"), CYCLE("CYCLE"), NONE("NONE");    
       private final String type;
      private AppendBasePathType(String type) {
         this.type = type;
      }
      public String toString(){
         return this.type;
      }
    };
    
    public enum LocationPolicyType {
      ARCHIVE_PUBLIC("ARCHIVE-PUBLIC"), ARCHIVE_PRIVATE("ARCHIVE-PRIVATE"), ARCHIVE_RESTRICTED("ARCHIVE-RESTRICTED"),
      ARCHIVE("ARCHIVE"), LOCAL_FTP("LOCAL-FTP"), LOCAL_OPENDAP("LOCAL-OPENDAP"), LOCAL("LOCAL"), 
      REMOTE_FTP("REMOTE-FTP"),REMOTE_HTTP("REMOTE-HTTP"), REMOTE_OPENDAP("REMOTE-OPENDAP"), REMOTE("REMOTE"), ARCHIVE_PREVIEW("ARCHIVE-PREVIEW"), 
   ARCHIVE_OPEN("ARCHIVE-OPEN"), ARCHIVE_SIMULATED("ARCHIVE-SIMULATED"), ARCHIVE_RETIRED("ARCHIVE-RETIRED"), ARCHIVE_REDUCED("ARCHIVE-REDUCED"), 
   ARCHIVE_SHARED("ARCHIVE-SHARED"), ARCHIVE_CONTROLLED("ARCHIVE-CONTROLLED");
       private final String type;
      private LocationPolicyType(String type) {
         this.type = type;
      }
      public String toString(){
         return this.type;
      }
    }

}
