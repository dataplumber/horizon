package gov.nasa.horizon.distribute.subscriber;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.common.api.util.DateTimeUtility;
import gov.nasa.horizon.common.api.util.Errno;
import gov.nasa.horizon.common.api.util.GetOpt;
import gov.nasa.horizon.common.api.util.SystemProcess;
import gov.nasa.horizon.distribute.common.DistributeProperty;
import gov.nasa.horizon.distribute.subscriber.api.*;
import gov.nasa.horizon.distribute.subscriber.cache.SubscriberXml;
import gov.nasa.horizon.inventory.model.*;
import gov.nasa.horizon.sigevent.api.SigEvent;
import gov.nasa.horizon.sigevent.api.Response;
import gov.nasa.horizon.sigevent.api.EventType;

public class Subscriber {

	private gov.nasa.horizon.distribute.subscriber.api.Subscriber dataSubscriber;
	private gov.nasa.horizon.distribute.subscriber.api.DataRetriever dataRetriever;
	private String outputBasePath, execString;
	private String dataDir, errorDir;
	
	private String datasetNames;
	private int sleepTime = 0;
	//private String usage = "";
	private Options m_options;
	private String[] m_args;
	private HashSet<ProductType> listOfProductTypes = new HashSet<ProductType>();
	private HashSet<Product> removeList;
	private String m_executionErrorCategory;
	private List<String> m_sigMessages = new ArrayList<String>();
	
	
	//Sig Event items
	static String m_sigEventUrl = null;
	static SigEvent m_sigEventObject = null;  // Used for reporting significant events.
	
	private String subscriberClassName, dataRetrieverClassName;
	static Log _logger = LogFactory.getLog(Subscriber.class);
	
	private Date sTime,lastRunTime;
	
	
	public Subscriber(String[] args)
	{
		m_args = args;
		//empty constructor for now
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Subscriber(args).start();
		
	}

	protected void start()
	{
		_logger.info("Starting subscription at " + new Date().toString() );
		//Beginning of subscription program
		_createOptions();
		_loadConfig();
		_loadCmdLineOpts();
		
		if(!this._hasRequiredOptions())
		{
			_printUsage();
			System.exit(-1);
		}
		
		
		_registerSigEvent();
		
		
		_createDataErrorDirectories();
		
		_createProductTypesFromNames();
		_createProductTypeDirectories();
		
		
		
		dataSubscriber = _setUpSubscriber();
		dataRetriever = _setUpDataRetriever();
		
		//main program area
		//setLastRunTime();
		while(true)
		{

			setLastRunTime();
			_logger.info("Beginning subscription request.");
			for(ProductType ds : listOfProductTypes)
			{

                                 // Start out with an empty list of sigevent messages for every iteration.
                                 if (m_sigMessages != null) {
                                     m_sigMessages.clear();
                                 }

				removeList = new HashSet<Product>();
				
				//TODO uncomment this
				/*
				if(dataSubscriber.list(ds, ds.getLastListingTime()))
				{
					//_logger.info("Setting Last Listing Time["++"] for dataset " +ds.getIdentifier());
					ds.setLastListingTime(lastRunTime);
				}
				else
				{
					_logger.info("No new granules for dataset \""+ds.getIdentifier()+"\" were found in the previous query.");
				}*/
												
				for(Product g: ds.getProducts())
				{
					List<String> gotten = new ArrayList<String>();
					_logger.info("Fetching granule: " + g.getName());
					_logger.info("Output directory: " + dataDir +File.separator + ds.getIdentifier());  // Added output directory so we know where the file got fetched to.
					try{
						gotten = dataRetriever.get(g, dataDir +File.separator + ds.getIdentifier());
						
					}
					catch(IOException ioe)
					{
						//no granule, fire off a sig event
						//System.out.println("IOException raised...");
						_logger.error("IO Exception Thrown, Product["+g.getId()+"] "+g.getName()+" not retrieved.");
						_postSig("Error Retrieving Product["+g.getId()+"] "+g.getName()+". " + ioe.getMessage());
						//_postSig(EventType.Error, m_executionErrorCategory,"Error Retrieving Product["+g.getId()+"] "+g.getName()+".", ioe.getMessage(), ds.getIdentifier());
					}finally{
						//move this to after execOnProduct if we want to go back to caching.
						removeList.add(g);
					}
					
					try{
						if(!gotten.isEmpty())
						{
							_logger.info("Successfully retrieved file");
							if(execString!=null)
							{					
								_logger.info("preparing to execute external command");
								String fileList = "";
								for(String s: gotten){
									fileList += s + ",";
								}
								if(fileList.endsWith(","));
								{
									fileList = fileList.substring(0, fileList.length()-1);
								}
								
								String name = g.getName();
								String status = g.getStatus();
								String dname = ds.getIdentifier();
								execOnEachProduct(name, status, dname, fileList);
							}
							//if we got the granule and executed on it successfully, remove it from our list. Otherwise we'll try to get it again.
							//removeList.add(g);
						}
						else
						{
							//error getting the granule?
							//String i_category, String i_description, String i_data
							_postSig("Error Retrieving Product["+g.getId()+"] "+g.getName()+".");
							//_postSig(EventType.Error, m_executionErrorCategory,"Error Retrieving Product["+g.getId()+"] "+g.getName()+".", "No Data to Report", ds.getIdentifier());
						}
					}
					catch(Exception e)
					{
						//write the granule info to an outputBasePath + error + granuleName directory
						//fire off a sig event
						_postSig("Error on external execution of script on Product["+g.getId()+"] "+g.getName()+". " + e.getMessage());
						//_postSig(EventType.Error, m_executionErrorCategory,"Error on external execution of script on Product["+g.getId()+"] "+g.getName()+".", e.getMessage(), ds.getIdentifier());
						//System.out.println("Exception raised");						
					}
					
						//addToCache() || writeToErr();
					//end for each granule
			    }
				ds.getProducts().removeAll(removeList);
				if(!ds.getProducts().isEmpty())
				{
					_logger.info("Writing unfetched granules to cache.");
					//TODO uncomment
					//SubscriberXml.toXml(ds.getProducts(), dataDir +File.separator + ds.getIdentifier() + File.separator + "cacheFile.xml");
				}
				else{
					//delete the cacheFile?
					File cf = new File(dataDir +File.separator + ds.getIdentifier() + File.separator + "cacheFile.xml");
					if(cf.exists())
					{
						if(cf.delete())
						{
							_logger.info("No unfetched granules. Removing cache file.");
						}
					}
					
				}
				//backup existing ds.getFile to cache because we failed to get those.
				//post dataset specific sig events
				_postSigMessages(ds.getIdentifier());
			}
			
			
			if(sleepTime != 0)
			try {
				_logger.info("Sleeping for " + sleepTime/60000 + " minutes." );
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//end main program area
		
	}

	private void _registerSigEvent() {
		
		if (m_sigEventUrl == null) {
		   _logger.error("Field sigEventLocation is null in config file");
           System.err.println("Field sigEventLocation is null in config file");
           System.exit(0);
       }

       // Since the URL is valid, we also create one instance of SigEvent object to be used through out the life of the program.

        m_sigEventObject = new SigEvent(m_sigEventUrl);

       if (m_sigEventObject == null) {
           _logger.error("Cannot create SigEvent object from m_sigEventUrl [" + m_sigEventUrl + "].  Program exiting.");
           System.err.println("Cannot create SigEvent object from m_sigEventUrl [" + m_sigEventUrl + "].  Program exiting.");
           System.exit(0);
       }     
    }

		

	private void _createDataErrorDirectories() {
		
		if(!outputBasePath.endsWith(File.separator))
			outputBasePath = outputBasePath + File.separator;
		
		dataDir = outputBasePath + "data";
		errorDir = outputBasePath + "error";
		
		File f_dataDir = new File(dataDir);
		File f_errorDir = new File(errorDir);
		
		if(!f_dataDir.exists())
		{
			if(f_dataDir.mkdir())
				_logger.info("Created data directory at '"+dataDir+"'");
			else
			{
				System.out.println("Could not create data directory '"+dataDir+"'; Check permissions and run the subscriber again.");
				System.exit(5);
			}
			
		}
		if(!f_errorDir.exists())
		{
			if(f_errorDir.mkdir())
				_logger.info("Created error directory at '"+errorDir+"'");
			else
			{
				System.out.println("Could not create error directory '"+errorDir+"'; Check permissions and run the subscriber again.");
				System.exit(5);
			}
			
		}
		
	}
	
	 private void _postSigMessages(String datasetName){
		 StringBuilder sb = new StringBuilder();
		 for(String s : m_sigMessages){
			 sb.append(s);
			 sb.append('\n');
		 }
		 m_sigMessages = new ArrayList<String>();
		 if(sb.toString().equals(""))
			 _logger.debug("No messages to send.");
		 else
			 _postSig(EventType.Error, m_executionErrorCategory,"Subscriber error report [dataset:"+datasetName+" ]", sb.toString(), datasetName);
		 
		 
	 }
	
	 private void _postSig(String message){
		 m_sigMessages.add(message);
	 }
	 
	 private static void _postSig(EventType i_eventType, String i_category, String i_description, String i_data, String source)
     {
         
         source = "DISTRIBUTE-SUBSCRIBER";  // Reset to see if the error will go away.  It does.  A note about this field: There may be an upper limit of about 20 characters or less.

         String provider = "JPL";        // Dummy provider.
         String computer = null;  // Dummy computer.
         Integer pid = new Integer(0); // Dummy process id.
         
         try {
        	  InetAddress addr = InetAddress.getLocalHost();
        	  byte[] ipAddr = addr.getAddress();
        	  String hostname = addr.getHostName();
        	  computer = hostname;
        	  
        } catch (UnknownHostException e) {
        	
        }
         
 
         // Regardless of the category, we post the significant event to "ALL" category.
         Response sigResponse = m_sigEventObject.create(i_eventType, i_category, source, provider, computer, i_description, null, i_data);
 
         // Just log to error if we cannot report significant event.  Re-submit if category is not "ALL".
         if (sigResponse.hasError()) {
             //_logger.error("Cannot report SigEvent due these reason(s): key = value ");
             _logger.error("Cannot report SigEvent due to: " + sigResponse.getError().toString());
         }
     }
	
	private void _createProductTypeDirectories() {
	
		for(ProductType d: listOfProductTypes)
		{
			File dsDir = new File(dataDir + File.separator + d.getIdentifier());
			if(!dsDir.exists())
			{
				if(dsDir.mkdir())
					_logger.info("Created dataset directory '"+d.getIdentifier()+"'");
				else
				{
					System.out.println("Could not create dataset directory '"+d.getIdentifier()+"'; Check permissions and run the subscriber again.");
					System.exit(5);
				}	
			}
			else
			{
				//if it exists, refresh the cache?
				String cacheName = dataDir + File.separator + d.getIdentifier() + File.separator + "cacheFile.xml";
				File cacheFile = new File(cacheName);
				if(cacheFile.exists())
				{
					_logger.info("Adding previously failed granules to dataset list from cache.");
					//TODO uncomment
					//d.getProductFiles().addAll(SubscriberXml.fromXml(cacheName));
				}
				
			}
		}
	}

	private void execOnEachProduct(String gName, String gStatus, String dsName, String fileList) throws Exception {
		/*
		 * Parameters that can be used in the exec:
		 * $gDir
		 * $gStatus
		 * $gName
		 * $dsName
		 * $gList
		 */
		String cmd = null;
		cmd = execString;
		if(cmd.contains("$gName"))
		{
			cmd = cmd.replace("$gName", gName);
			//System.out.println("Replacing Name");
		}
		if(cmd.contains("$gStatus"))
		{
			cmd = cmd.replace("$gStatus", gStatus);
			//System.out.println("Replacing $gStatus");
		}
		if(cmd.contains("$dsName"))
		{
			cmd = cmd.replace("$dsName", dsName);
		//	System.out.println("Replacing $dsName");
		}
		if(cmd.contains("$gDir"))
		{
			cmd = cmd.replace("$gDir", dataDir +"/"+dsName+"/" + gName);
			//System.out.println("Replacing $gDir");
		}
		if(cmd.contains("$gList"))
		{
			cmd = cmd.replace("$gList", fileList);
			//System.out.println("Replacing $gDir");
		}

		_logger.info("Executing command: " + cmd);
		Errno err = SystemProcess.execute(cmd);
		if(err.getId() != 0)
		{
			_logger.info("An error occured while executing the system process.");
			_logger.info("Error ["+err.getId()+"] discription: " + err.getMessage());
			throw new Exception("System process returned an error code of " + err.getId());
		}
	}

	private void _createProductTypesFromNames() {
		
		for(String name : datasetNames.split(","))
		{
			ProductType d = new ProductType(name);
			
			//TODO uncomment
			//d.setLastListingTime(sTime);
			//System.out.println("setting default lrt: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(d.getLastListingTime()));
			listOfProductTypes.add(d);
		}
	}

	private void setLastRunTime() {
		// TODO Auto-generated method stub
		lastRunTime = new Date();
	}

	private DataRetriever _setUpDataRetriever() {
		ClassLoader clazzLoader;
		Class clazz;
		clazzLoader = this.getClass().getClassLoader();
		try {
			clazz = clazzLoader.loadClass(dataRetrieverClassName);
			return (gov.nasa.horizon.distribute.subscriber.api.DataRetriever) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}
	
		return null;
	}

	private gov.nasa.horizon.distribute.subscriber.api.Subscriber _setUpSubscriber() {
		
		ClassLoader clazzLoader;
		Class clazz;
		clazzLoader = this.getClass().getClassLoader();
		try {
			clazz = clazzLoader.loadClass(subscriberClassName);
			return (gov.nasa.horizon.distribute.subscriber.api.Subscriber) clazz.newInstance();
	
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
			System.exit(-10);
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(-10);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(-10);
		}
	
		return null;
	}

	private void _loadConfig() {
		
		DistributeProperty.getInstance();
		outputBasePath = System.getProperty("distribute.output.base.path");
		execString = System.getProperty("distribute.exec.cmd");
		sleepTime = 60 *1000 * Integer.parseInt(System.getProperty("distribute.sleep.minutes"));
		m_sigEventUrl = System.getProperty("sig.event.url");
		subscriberClassName = System.getProperty("distribute.subscriber.class");
		dataRetrieverClassName = System.getProperty("distribute.data.retriever.class");
		m_executionErrorCategory = System.getProperty("execution.sig.category");
		
		//System.out.println("output: " + outputBasePath + "\nexecString: " + execString+ "\nsleepTime(milis): " + sleepTime);
		
	}
	
	private void _loadCmdLineOpts()
	{
		GetOpt opt = new GetOpt(m_args, "hd:");
        String str = null;
        try {
          while ((str = opt.nextArg()) != null) {
           switch (str.charAt(0)) {
           case 'h':
              this._printUsage();
              break;
           case 'd':
              datasetNames = opt.getArgValue();
              break;
           case 's':
        	  String startTime = opt.getArgValue();
        	  sTime = this._createDateFromString(startTime);
        	  break;
           default:
              System.out.println("Error: Undefined command line parameter used.");
           }
        }
        } catch (java.text.ParseException e) {
			
			e.printStackTrace();
		}
	}
	
	private void _createOptions(){  
        m_options = new Options();
        
        // Add the possible options:
        m_options.addOption("d", null, true, "The name of the dataset you wish to subscribe to, or a coma delimited set of database names");
        m_options.addOption("s", null, true, "The start time for the crawler to use. Defaults to the current time if not specified. Format: yyyy-MM-dd'T'HH:mm:ss.SSS");
    }
	 
	 private  boolean _hasRequiredOptions() {
		//String[] requiredOptions = new String[] {"d", "e"};
        boolean result = true;
        
        if(outputBasePath == null)
        {
        	System.out.println("Output base path is not set. Cannot run subscriber.");
        	System.out.println("Exiting...");
        	System.exit(5);
        	result = false;
        }
        if(execString == null)
        {
        	System.out.println("No 'invoke' command supplied. No external process will be called upon successful granule transfer.");
        	//sTime = new Date();
        }
        //datasetname, sub/DR class names,
        if(sTime == null)
        {
        	System.out.println("Start time not set. Defaulting to present time.");
        	sTime = new Date();
        }
        if(m_executionErrorCategory == null && execString != null)
        {
        	System.out.println("No external execution category defined for external command. Please deinfe an external error cateogry in your config file.");
        	result = false;
        }
        
        if(datasetNames==null)
        {
        	System.out.println("ERROR: In order to subscribe to a dataset, a dataset name must be provided with the -d <name> option.");
        	result = false;
        }
                return result;
	 }
	 
	 private  Date _createDateFromString(String i_date)
	 {
		 
        Date date = null;
		date =DateTimeUtility.parseDate(i_date);
	 	if(date == null)
	 	{
	 		System.out.println("Could not parse date from string: "+i_date);
			return null;
	 	}		            
		return date;
	 }
	 
	 private  void _printUsage() {
        String _userScript = new String("dataSubscriber.sh");
        String USAGE_HEADER = new String("dataSubscriber");
        HelpFormatter formatter = new HelpFormatter();
        //System.out.println();
        
        formatter.printHelp(_userScript, USAGE_HEADER,
                            m_options, null, true);
    }
	
	
}


