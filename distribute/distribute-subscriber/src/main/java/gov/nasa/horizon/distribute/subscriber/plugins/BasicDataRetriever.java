package gov.nasa.horizon.distribute.subscriber.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import gov.nasa.horizon.distribute.subscriber.api.DataRetriever;
import gov.nasa.horizon.inventory.model.Product;
import gov.nasa.horizon.inventory.model.ProductArchive;
import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.FileProductUtility;
import gov.nasa.horizon.common.api.util.Hostmap;
import gov.nasa.horizon.common.api.util.Hostmap.HostInfo;


public class BasicDataRetriever implements DataRetriever {
	
	static Log _logger = LogFactory.getLog(BasicDataRetriever.class);
	static Hostmap hm; 
	/*
        // Additional function to look and fetch from local file system first.
        private boolean _fetchLocalFile(Product i_granuleFile,
		                        String      i_granuleDir) {

            // The path in local file system looks something like this:
            //
            //     file:///data/dev/users/podaacdev/data/archive/store/modis/open/L3/terra/11um/9km/annual/2012
            //
            // The name in local file system looks something like this
            //
            //     T2012198204500.L2_LAC_SST.bz2 
            //
            // The i_granuleDir looks something like this:
            //
            //     /home/qchau/sandbox/distribute/distribute-subscriber/target/distribute-subscriber-4.1.0/subscriber/data/MODIS_TERRA_L3_SST_THERMAL_ANNUAL_9KM_NIGHTTIME/T2012198204500.L2_LAC_SST
            //
            // We can combine the path and name together to form the complete name and then it can be copied to i_granuleDir directory.

            boolean o_localFetchStatus = false; // Set to true if can copy the file from local disk.

            String granulePath = i_granuleFile.getPath();
            String granuleName = i_granuleFile.getName();
            String fullPathInputName   = "";
            String fullPathOutputName  = "";

            if (granulePath.startsWith("file")) {
                 // Get the name, ignoring "file://" token.
                fullPathInputName  = granulePath.substring(7) + File.separator + granuleName;
            } else {
                fullPathInputName  = granulePath              + File.separator + granuleName;
            }

            // If the file does not exist, we simply return false.

            File a_file = new File(fullPathInputName);
            if (a_file.exists() == false) return (o_localFetchStatus);
            
//System.out.println("BasicDataRetriever::_fetchLocalFile:granulePath [" + granulePath + "]");
//System.out.println("BasicDataRetriever::_fetchLocalFile:granuleName [" + granuleName + "]");

            // We know the file is there, attempt to copy it.

            try {
                fullPathOutputName  = i_granuleDir + File.separator + granuleName;
                InputStream  inStream = new FileInputStream(new File(fullPathInputName));
                OutputStream outStream = new FileOutputStream(new File(fullPathOutputName));
                byte[] buf = new byte[1024];
                int len;
                while ((len = inStream.read(buf)) > 0){
                    outStream.write(buf, 0, len);
                }
                inStream.close();
                outStream.close();

//                System.out.println("Successfully written to " + fullPathOutputName);
                _logger.info("Successfully written to " + fullPathOutputName);

                // We have copied the file successfully.
                o_localFetchStatus = true;

            } catch(FileNotFoundException ex){
                System.out.println(ex.getMessage() + " in the specified directory.");
                _logger.error(ex.getMessage() + " in the specified directory.");
//                System.exit(0);
            } catch(IOException e){
                System.out.println(e.getMessage());  
                _logger.error(e.getMessage());
//                System.exit(0);
            }
            return (o_localFetchStatus);
        }
        */
	
	public List<String> get(Product granule, String outputDir) throws IOException {
	   return null;
	/*
		String hostmap = System.getProperty("subscriber.hostmap");
		List<String> returned = new ArrayList<String>();
		if(hostmap == null)
		{
			//System.out.println("Could not load hostmap... Attempting to continue without loading.");
		}
		else{
			try {
				//hm = Hostmap.loadHostmap(hostmap);
				_logger.info("Hostmap loaded.");
			} catch (SAXException e2) {
				_logger.info("Could not parse hostmap.");
				System.exit(5);
				//e2.printStackTrace();
			}
		}

		if(granule == null)
		{
			_logger.info("Granule is null. Returning.");
			return returned;
		}
		
		//if dir doesn't exist, create it
		if(!new File(outputDir).exists())
		{
			
			_logger.info("Output path doesn't exist. Attempting to create \""+outputDir+"\"");
			boolean success = (new File(outputDir)).mkdir();
		    if (success) {
		    	_logger.info("Directory: " + outputDir + " created");
		    }
		    else
		    {
		    	_logger.info("Could not create directory. Exiting.");
		    	throw new IOException("Directory could not be written");
		    }
		}
		
		//check & create granule name directory under data
		if(granule.getName() == null)
		{
			_logger.info("Granule Name doesn't exist. Cannot create directory.");
			return returned;
		}
		String granuleDir = outputDir + "/" + granule.getName();
		
		File outputPathGranule = new File(granuleDir);
		if(!outputPathGranule.exists())
		{
			_logger.info("Granule Output path doens't exist. Attemptin to create \""+granuleDir+"\"");
			
		    if (outputPathGranule.mkdir()) {
		    	_logger.info("Directory: " + granuleDir + " created");
		    }
		    else
		    {
		    	_logger.info("Could not create directory. Exiting.");
		    	throw new IOException("Granule Directory could not be created");
		    }
		}
		else
		{
			//granule dir already exists...
			if(outputPathGranule.listFiles().length > 0)
			{	
				_logger.info("Granule directory already exists. Creating backup directory.");
				//create hidden dir
				String backupDir = granuleDir+"/."+(new Date()).getTime();
				File backupDirGranule = new File(backupDir);
				if(!(backupDirGranule.mkdir()))
				{
					_logger.info("Could not create backup directory...");
					throw new IOException("Could not create backup directory.");
				}
				
				//move files to backup dir
				for(File f :outputPathGranule.listFiles())
				{
					if(f.getName().startsWith("."))
					{
						continue;
					}
					//System.out.println("File name: " + f.getName());
					File bkup = new File(backupDirGranule, f.getName());
					if(!f.renameTo(bkup))
					{
						//System.out.println("Backup name: " + bkup.getName());
						_logger.info("Cannot move file to backup.");
						//System.out.println("Cannot move file to backup");
						throw new IOException("File cannot be moved to backup location.");
					}
				}
				
			}
			else
			{
				_logger.info("Granule directory already exists, but no files exist. Contining file retrieval.");
			}
		}
		
		if(granule.getFileList().isEmpty())
		{
			_logger.info("No files exist in granule["+granule.getName()+"]. Returning.");
			return returned;
		}
		
		for(GranuleFile g : granule.getFileList())
		{
			
			_logger.info("Attempting to fetch file " + g.getName());
			FileProductUtility vfsWriter = new FileProductUtility(granuleDir +"/"+g.getName());
			//String filename = g.getName();

			  if (_fetchLocalFile(g,granuleDir)){ 
				  	String fullPathInputName   = "";
		            

		            if (g.getPath().startsWith("file")) {
		                 // Get the name, ignoring "file://" token.
		                fullPathInputName  = g.getPath().substring(7) + File.separator + g.getName();
		            } else {
		                fullPathInputName  = g.getPath()              + File.separator + g.getName();
		            }
				  
				  returned.add(fullPathInputName);
           	   		continue;
              }
			//try local FTP if we can't get the file locally
			String localFtpPath = null;
			boolean foundRef = false;
			for(ProductReference ref: granule.getReferenceList())
			{
				if(ref.getType().equals("LOCAL-FTP"))
				{
					localFtpPath = ref.getPath();
					localFtpPath = localFtpPath.substring(0, localFtpPath.lastIndexOf("/"));					
					//System.out.println("Path to look for files in: " + localFtpPath);
					foundRef = true;
				}
				else
					continue;
				
				String user, auth = null;
				_logger.info("fileName: "+ g.getName());
				_logger.info("Geting file: " + localFtpPath +"/"+g.getName());
				
				FileProductUtility ftpReader = new FileProductUtility(localFtpPath +"/"+g.getName());
				//get hostmap info
				if(hm != null){
					for(HostInfo hi:hm.getHostInfo())
					{
						if(ref.getPath().contains(hi.getName()));
						{
							try{
								String[] auths = hi.getAuthentication().split(":");
								user = auths[0];
								auth = auths[1];
								//System.out.println("User: "+user+"\nAuth: "+auth);
								
								ftpReader.setAuthentication(user,auth);
							}catch(Exception npe)
							{
								_logger.info("Hostmap authenication is not in the form of \"user:pass\"");
								npe.printStackTrace();
							}
						}
						
					}
				}
				FileProduct fp = null;
				try {
					fp = ftpReader.getFile();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//could not get 
					_logger.info("could not retrieve the file from the FTP source");
					ftpReader.cleanUp();
					vfsWriter.cleanUp();
					throw new IOException("Could not read file from ftp source");
					//e1.printStackTrace();
				}
				InputStream is = null;
				try{
					is = fp.getInputStream();
				}catch(Exception e)
				{
					_logger.info("Could not open input stream");
					throw new IOException("could not open input stream");
				}
				
				try{
					vfsWriter.writeFile(is);
					_logger.info("File product successfull written");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					_logger.info("could not write the file to the local directory.");
					is.close();
					fp.close();
					ftpReader.cleanUp();
					vfsWriter.cleanUp();
					throw new IOException("Could not read file from ftp source");
					//e1.printStackTrace();
				}
				_logger.info("Cleaning up writers, readers");
				is.close();
				fp.close();
				ftpReader.cleanUp();
				vfsWriter.cleanUp();
				
				returned.add(localFtpPath +"/"+g.getName());
				
			}
			if(!foundRef)
			{
				_logger.info("Could not find any local-ftp references. Will not download files.");
				return returned;
			}
			// TODO Auto-generated catch block
			//e.printStackTrace();	
		}
			
				//System.out.println("Cleaning up readers & writers");
		return returned;
*/
	}

}
