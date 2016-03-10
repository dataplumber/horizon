package gov.nasa.horizon.distribute.subscriber.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.distribute.subscriber.api.DataRetriever;
import gov.nasa.horizon.inventory.api.*;
import gov.nasa.horizon.inventory.model.*;


public class BasicDataRetrieverNotifier implements DataRetriever {
	
	static Log _logger = LogFactory.getLog(BasicDataRetrieverNotifier.class);
	
        // Additional function to look and fetch from local file system first.

	
	public List<String> get(Product product, String outputDir) throws IOException {
	
		
		List<String> returned = new ArrayList<String>();
		
		if(product == null)
		{
			_logger.info("Product is null. Returning.");
			return returned;
		}
		
		if(product.getProductFiles().isEmpty())
		{
			_logger.info("No files exist in product["+product.getName()+"]. Returning.");
			return returned;
		}
		
		for(ProductArchive p : product.getProductFiles())
		{
			
			_logger.info("Attempting to fetch file " + p.getName());	
			_logger.info("FilePath: " + product.getRootPath() + File.separator + product.getRelPath() + File.separator + p.getName());
			
            String fullPathInputName   = "";
            if (product.getRootPath().startsWith("file")) {
                 // Get the name, ignoring "file://" token.
                fullPathInputName  = product.getRootPath().substring(7) + File.separator + p.getName();
            } else {
                fullPathInputName  = product.getRootPath()              + File.separator + p.getName();
            }
            returned.add(fullPathInputName.trim());
                       
			
/*			//try local FTP if we can't get the file locally
			String localFtpPath = null;
			boolean foundRef = false;
			for(GranuleReference ref: granule.getReferenceList())
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
				String file =  localFtpPath +"/"+g.getName();
				returned.add(file.trim());
				
//				
			}*/
			// TODO Auto-generated catch block
			//e.printStackTrace();	
		}
			
				//System.out.println("Cleaning up readers & writers");
		return returned;

	}

}
