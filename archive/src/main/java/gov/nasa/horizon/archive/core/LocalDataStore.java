//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.archive.external.FileUtil;
//import gov.nasa.horizon.inventory.api.Constant.ProductArchiveStatus;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains implementation of interface DataStore.
 * It store files locally.
 *
 * @author clwong
 *
 * @version
 * $Id: LocalDataStore.java 5358 2010-07-23 17:18:20Z gangl $
 */
class LocalDataStore implements DataStore {
	
	private static Log log = LogFactory.getLog(LocalDataStore.class);
	private static boolean doChecksum = true;
	
	//TODO - Remove for constant class in inventory api
   private static final String ONLINE = "ONLINE";
   private static final String OFFLINE = "OFFLINE";
   private static final String MISSING = "MISSING";
   private static final String IN_PROCESS = "IN_PROCESS";
   private static final String DELETED = "DELETED";
   private static final String ANOMALY = "ANOMALY";
   private static final String CORRUPTED = "CORRUPTED";
	
	public void setChecksumOption(boolean checksum){
		doChecksum=checksum;
	}
	
	public boolean verify(URL url) throws ArchiveException {
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new ArchiveException(
					MISSING+": "
					+"URL "+url.toString()+" URISyntaxException!"
					+"\n"+e.getMessage());
		}
		if (!file.exists()) {
			throw new ArchiveException(
					MISSING+": "
					+file.getPath() + ": not existed");
		}
		
		if (!file.isFile()) {
			throw new ArchiveException(
					MISSING+": "
					+file.getPath() + ": not a file");
		}
		return true;
	}
	
   public boolean verify(URI uri) throws ArchiveException {
      File file = null;
      
      file = new File(uri.getPath());

      if (!file.exists()) {
         throw new ArchiveException(MISSING, MISSING + ": " + file.getPath() + ": not existed");
      }

      if (!file.isFile()) {
         throw new ArchiveException(MISSING, MISSING + ": " + file.getPath() + ": not a file");
      }
      return true;
   }
	
	@SuppressWarnings("static-access")
	public boolean verify(URI uri, long fileSize, String checksum, String checksumType) 
								throws ArchiveException {
		log.debug("verify: "+uri);	
		try {
			verify(uri);	
		} catch (ArchiveException ex) {
			throw new ArchiveException(ex.getProductStatus(),
					ex.getMessage());
		}
		File file = new File(uri.getPath());
		// verify checksum
		if(doChecksum){
			if (!FileUtil.validateChecksum(file, checksum, checksumType)) {
				throw new ArchiveException(CORRUPTED+": "
						+file.getPath() + ": checksum not matched");
			}
		}
		else{
			log.debug("Skipping checksum option");
		}
		
		// verify file size
		if (file.length() != fileSize) {
			throw new ArchiveException(CORRUPTED+": "
					+file.getPath() + ": file size unmatched");
		}

				
		return true;
	}
}
