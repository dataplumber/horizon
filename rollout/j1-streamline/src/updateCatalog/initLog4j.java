package updateCatalog;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class initLog4j{

  public Category log = null;
  private String LOG_PROPERTIES_FILE =null;
  static final String pathsFile = "./paths.properties";


  public initLog4j(Category cat)
  { 
    this.log = cat;
    initializeLogger();
  }
  public Category getLog(){
       return this.log;
  }
  private void initializeLogger()
  {
    Properties p = new Properties();
       try{
            p.load(new FileInputStream(pathsFile));
            LOG_PROPERTIES_FILE = p.getProperty("log4jproperties");
       }
       catch(IOException e){
            throw new RuntimeException("IOException: "+pathsFile);
       }


    Properties logProperties = new Properties();
    

    try
    {
      logProperties.load(new FileInputStream(LOG_PROPERTIES_FILE));
      PropertyConfigurator.configure(logProperties);
      //log.info("Logging initialized: "+LOG_PROPERTIES_FILE);
    }
    catch(IOException e)
    {
      throw new RuntimeException("Unable to load logging property " + LOG_PROPERTIES_FILE);
    }
  }
} 
