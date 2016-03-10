//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.archive.external.FileUtil;
//import gov.nasa.horizon.inventory.api.Constant.ProductArchiveStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains methods to manipulate the archived data.
 *
 * @author clwong
 * $Id: ArchiveData.java 10123 2012-06-07 15:31:38Z gangl $
 */
public class ArchiveData {
	
	private static Log log = LogFactory.getLog(ArchiveData.class);
	private static AIP aip = null;
	private static boolean doChecksum = true;
	private static boolean overwrite = false;
	
	//TODO Remove temporary constants and replace with inventory supplied constants
	private static final String ONLINE = "ONLINE";
	private static final String OFFLINE = "OFFLINE";
	private static final String MISSING = "MISSING";
	private static final String IN_PROCESS = "IN_PROCESS";
	private static final String DELETED = "DELETED";
	private static final String ANOMALY = "ANOMALY";
	

	public static String absolutePath(String path) {
		if (path==null) return null;
		String ret = toFile(path).getPath();
		log.debug("Absolute Path: "+ret);
		return ret;
	}
	
	public static long add() throws ArchiveException {
		log.debug("add");
		FileUtil.getInstance();
		FileUtil.copyFile(aip.getStaging(), aip.getDestination());
		FileUtil.release();
		long size = new File(aip.getDestination()).length();
		return size;
	}

	public static long add(AIP aip) throws ArchiveException {
		setAip(aip);
		return add();
	}
	
	// delete for good
	public static List<String> delete(List<String> deleteFiles) throws ArchiveException {
		log.debug("deleting: "+deleteFiles.size()+" files");
		List<String> deleted = new ArrayList<String>();
		FileUtil.getInstance();
		for (String deleteFile : deleteFiles) {
			log.debug("Deleting file: "+deleteFile);
			if (FileUtil.removeFile(deleteFile))
				deleted.add(deleteFile);
			else log.warn(deleteFile+" not deleted");
		}
		FileUtil.release();
		return deleted;
	}
	
	// delete for good
	public static List<String> delete(List<String> deleteFiles, String baseLocation) throws ArchiveException {
		List<String> deleted = delete(deleteFiles);
		// Assume all files have the same prefix base location
		String basePath = absolutePath(baseLocation);
		if (deleted != null && (!deleted.isEmpty())) {
			File file = new File(deleted.get(0));
			rmdir(file.getParentFile().getPath(), basePath);
		}
		return deleted;
	}

	// delete to transh bin
	public static List<String> delete(List<String> deleteFiles,
										String baseLocation, 
										String trashBasepath) {
		log.debug("delete to :"+trashBasepath);
		String basePath = absolutePath(baseLocation);
		if (basePath!=null && (!isDirectory(basePath)))	basePath = null;
		List<String> backups = new ArrayList<String>();
		for (String deleteFile : deleteFiles) {
			String fromPath = absolutePath(deleteFile);
			if (basePath==null) basePath = getBaseLocation(fromPath);
			String subpath = fromPath.replaceFirst(basePath, "");
			String toPath = absolutePath(trashBasepath) 
							+ (subpath.startsWith("/") ? subpath : "/"+subpath);
			if (rename(fromPath, toPath)) {
				backups.add(toPath);
				rmdir(new File(fromPath).getParentFile().getPath(), basePath);
			} else {
				log.warn(fromPath+" not backed up to "+toPath);
			}
		}
		return backups;
	}
	
   public static DataStore getDataStore(URI uri) throws ArchiveException {
      DataStore store = null;
      if (uri != null) {
         //String scheme = uri.getScheme();
         // if (scheme.equals("file")) {
            store = new LocalDataStore();
         // } else {
            //store = new RemoteDataStore();
         // }
      }
      return store;
   }

	public static String getBaseLocation(String path) {
		String[] patterns = new String[] { "/\\d{4}/\\d{3}/","/\\d{4}/\\d{2}/","/\\d{4}/\\d{1}/", "/\\d{4}/", "/c\\d{3}/" };
		String[] paths = null;
		for (String pattern : patterns) {
			paths = path.split(pattern);
			if (paths.length==2) return paths[0];
		}
		return null;
	}

	public static String getFilename(String path) {
		String[] patterns = new String[] { "/\\d{4}/\\d{3}/","/\\d{4}/\\d{2}/","/\\d{4}/\\d{1}/", "/\\d{4}/", "/c\\d{3}/" };
		String[] paths = null;
		for (String pattern : patterns) {
			paths = path.split(pattern);
			if (paths.length==2) 
				{
					log.debug("path returned: " + paths[1]);
				return paths[1];
				
				}
		}
		return null;
	}
	
	public static boolean isDirectory(String directory) {
		File dirFile = toFile(directory);
		if (dirFile==null || dirFile.getPath().equals("") ||
				(!dirFile.exists()) || (!dirFile.isDirectory())) {
			return false;
		}
		return true;
	}
	
	public static boolean isDirectory(URI uri) {
		return isDirectory((new File(uri)).getPath());
	}
	
	public static boolean isInProcess(AIP aip) {
		if (aip.getStatus().equals(IN_PROCESS))
			return true;
		else 
			return false;
	}
	
	public static List<String> move(List<String>archiveList, 
									String fromBaseLocation, String toBaseLocation, boolean force) 
									throws ArchiveException {
		log.debug("move:");
		List<String> moved = new ArrayList<String> ();
		String fromBasepath = absolutePath(fromBaseLocation);
		String toBasepath = absolutePath(toBaseLocation);
		if (fromBasepath != null  && fromBasepath.equals(toBasepath)) {
			log.warn("WARN: input basepath "+toBaseLocation+" is same as productType/product base location!");
		} else {
			String answer = "";
			for (String archive : archiveList) {
				String fromPath = absolutePath(archive);
				if (fromBasepath==null) fromBasepath = getBaseLocation(fromPath);
				//log.info("fromPath: " +fromPath);
				//log.info("fromBasepath: " +fromBasepath);
				//log.info("toBasepath: " +toBasepath);
				//String toPath = fromPath.replaceFirst(fromBasepath, toBasepath);
				String toPath = new String();
				
				if(!toBasepath.contains(fromBasepath+ File.separator))
				{
					
					//log.warn("Cannot derive new path for product: ProductType base path ("+fromBasepath+") is not a subpath of new product root_path ("+toBasepath+").");
					//if force, then continue without asking
					if(!force && !answer.equalsIgnoreCase("y") )
					{
						log.warn("Attempting to move a product outside of the parent productType's base path.");
						//no force option, ask user.
						InputStreamReader istream = new InputStreamReader(System.in) ;
				        BufferedReader bufRead = new BufferedReader(istream) ;
				        
						boolean proper = false;
				        do{
					        System.out.println("You are attempting to move a product outside of the parent productType's base path. Do you want to continue with relocating? (y/n): ");
				            try {
				            	answer = bufRead.readLine();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if(answer.equalsIgnoreCase("y"))
							{
								System.out.println("Received \"yes\" signal. Proceeding with relocation of product...");
								log.info("Received \"yes\" signal. Proceeding with relocation of product...");
								toPath= toBasepath + File.separator + archive.substring(archive.lastIndexOf("/"));
								proper=true;
							}
							else if(answer.equalsIgnoreCase("n"))
							{
								System.out.println("Received \"no\" signal. Skipping relocation of product...");
								log.info("Received \"no\" signal. Skipping relocation of product...");
								proper = true;
								break;
							}
							if(!proper)
								System.out.println("Please enter a 'y'es or 'n'o response. You entered: \""+answer+"\"");
				        }while(!proper);
				        if(answer.equalsIgnoreCase("n"))
				        	break;
				        	//System.out.println("Continuing with move...");
					}
					else if(answer.equalsIgnoreCase("y"))
					{
						//force option, go ahead and set the toPath
						log.info("\"yes\" enetered, proceeding with relocation of product.");
						toPath= toBasepath + File.separator + archive.substring(archive.lastIndexOf("/"));
					}
					else
					{
						//force option, go ahead and set the toPath
						log.info("Force option set, proceeding with relocation of product.");
						toPath= toBasepath + File.separator + archive.substring(archive.lastIndexOf("/"));
					}
				}
				else{
					//IT CONTAINS THE FORM PATH
					//ADD LOGIC HERE TO create TO_PATH when new basepath contains from_path
					log.debug("Archive-substring: " + archive.substring(archive.lastIndexOf("/")));
					toPath= toBasepath + File.separator + archive.substring(archive.lastIndexOf("/"));
					log.info("From path("+fromPath+") != to path("+toPath+")");
					
				}
				if (rename(fromPath, toPath)){
					moved.add(toPath);
				} else {
					log.warn(fromPath+" not relocated to "+toPath);
				}
			}
			if ((moved != null) && (!moved.isEmpty())) {
				File file = new File(moved.get(0));
				rmdir(file.getParentFile().getPath(), fromBasepath);
			}
		}
		return moved;
	}
	
	public static boolean rename(String fromPath, String toPath, boolean ow) {
			overwrite = ow;
			return rename(fromPath,toPath);
	}
	
	public static boolean rename(String fromPath, String toPath) {
		log.debug("rename:");
		try {
			//log.debug("File URL: " + new File(fromPath).toURL().toString());
			
			if (!verify(new File(fromPath).toURL())) {
				log.warn("Invalid "+fromPath);
				return false;
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
		//log.debug("Directory Check");
		File toFile = new File(toPath);
		if (toFile.isDirectory()) {
			log.warn("Cannot overwrite directory "+toFile.getPath());
			return false;
		}
		//log.debug("Overwrite Check");
		if (toFile.exists()) {
			if(!overwrite){
				log.warn(toPath + " already exists. Do not want to overwrite file "+toFile.getPath());
				return false;
			}
		}
		//log.debug("Parent Check");
		File parentFile = toFile.getParentFile();
		if (parentFile.exists()) 
		{
		   if (parentFile.isDirectory()) log.debug("Adding to "+parentFile.getPath());
		   else if (parentFile.isFile()) {
			  log.warn("Cannot overwrite "+parentFile.getPath());
			  return false;
		   }
		} else parentFile.mkdirs();
		//log.debug("Writing File...");
		if (new File(fromPath).renameTo(toFile)) return true;
		else{
			//rename to might not have worked across partitions, do a manual copy and delete
			log.debug("Direct Copy Check");
			if(copyFile(fromPath,toPath))
				return true;
			else
				return false;
		}
	}
	
        private static boolean copyFile(String srFile, String dtFile){
		    try{
		      File f1 = new File(srFile);
		      File f2 = new File(dtFile);
		      InputStream in = new FileInputStream(f1);
		      
		      //For Overwrite the file.
		      OutputStream out = new FileOutputStream(f2);
		
		      byte[] buf = new byte[1024];
		      int len;
		      while ((len = in.read(buf)) > 0){
		        out.write(buf, 0, len);
		      }
		      in.close();
		      out.close();
		      return true;
		     }
		    catch(FileNotFoundException ex){
		      log.warn(ex.getMessage() + " in the specified directory.");
		      return false;
		    }
		    catch(IOException e){
		      log.warn(e.getMessage());
		      return false;      
		    }
        }

	public static void rmdir(String path) {
		File pathFile = new File(path);

		if (path.equalsIgnoreCase("/")) {
			log.debug("WARN: traverse to root! "+path);
			return;
	    }
		if (path.equalsIgnoreCase(System.getProperty("user.dir"))) {
			log.debug("WARN: traverse to user home directory!");
			return;
		}
		// only delete empty directory
	    if (pathFile.isDirectory() && (pathFile.list().length==0)) {
	    	log.info("Deleted empty directory "+pathFile.getPath());
	    	pathFile.delete();
	    } else log.debug(path+" is not empty.");
	}
	
	public static void rmdir(String path, String basepath) {
		path = absolutePath(path);
		basepath = absolutePath(basepath);
		log.debug("rmdir:"+path+":"+basepath);
		// note that path & basePath must be in absobute path syntax
		if (path.startsWith(basepath) && (!path.equalsIgnoreCase(basepath))) {
			rmdir(path);
		    File pathFile = new File(path).getParentFile();
		    rmdir(pathFile.getPath(), basepath);
		}
	}
	
	
	public static void setAip(AIP aip) {
		ArchiveData.aip = aip;
	}
	
	public static File toFile(String path) {
		File file = null;
		try {
			if (path.startsWith("file:"))
				file = new File(new URI(path));
			else file = new File(path);
		} catch (Exception e) {}
		return file;
	}
	
	@SuppressWarnings("static-access")
	public static boolean verify() {
		log.debug("verify():"+aip.getDestination().toString());
		try {
			URI uri = aip.getDestination();
			log.debug("Creating new datastore from URI "+aip.getDestination().toString());
			DataStore store = getDataStore(uri);
			store.setChecksumOption(doChecksum);
			log.debug("setting checksum to " + doChecksum);
			if (store.verify(uri, aip.getFileSize(), aip.getChecksum(), aip.getAlgorithm())) {
				if(!aip.getStatus().equals(DELETED)){
					log.debug("current status: " + aip.getStatus()+", Product is currently online.");
					aip.setStatus(ONLINE);
					aip.setNote(aip.getStatus());
					return true;
				}
				else{
					log.debug("current status: " + aip.getStatus()+", Product is currently online. Should be deleted");
					aip.setStatus(ANOMALY);
					aip.setNote(aip.getStatus() + " - Product was marked deleted, but was found in filesystem.");
					return false;
				}
			} else {
				log.debug(uri+" not verified!");
			}
		} catch (ArchiveException ex) {
			//add check to see if this thing was deleted, if so, it's ok as long as the exception is "missing"
			log.debug("AE Exception - AIP status = " + aip.getStatus());
			log.debug("AE Exception - ex status = " + ex.getProductStatus());
			if(aip.getStatus().equals(DELETED) && ex.getProductStatus().toString().contains("MISSING")){
				log.debug("current status: " + aip.getStatus()+", Product is currently online.");
				//don't set the status to ONLINE, because they should stay 'deleted'
				//aip.setStatus(ProductArchiveStatus.ONLINE.toString());
				//aip.setNote(aip.getStatus());
				return true;
			}
			else if(aip.getStatus().equals(DELETED) && ex.getProductStatus().toString().contains("CORRUPTED")){
				log.debug("Deleted and Corrupted...current status: " + aip.getStatus()+", Product is currently online.");
				//don't set the status to ONLINE, because they should stay 'deleted'
				aip.setStatus(ANOMALY);
				log.debug(aip.getStatus() + " - Deleted product found in file system -  " + ex.getMessage());
				aip.setNote(aip.getStatus() + " - Deleted product found in file system -  " + ex.getMessage());
				return false;
			}else{
				aip.setStatus(ex.getProductStatus().toString());
				aip.setNote(ex.getMessage());
				log.debug(aip.getNote());
				log.debug("!!! ARchiveException- else -" + ex.getProductStatus());
			}
		} catch (Exception ex) {
			aip.setStatus(MISSING);
			aip.setNote(
					MISSING+": "
					+"URI "+aip.getDestination().toString()+ex.getCause()
					+"\n"+ex.getMessage());
			
			log.debug(aip.getNote(), ex);
		}
		return false;
	}
	
	//private static boolean doChecksum = true;
	
	public static boolean verify(AIP aip, boolean checksum) {
		doChecksum = checksum;
		setAip(aip);
		return verify();
	}
	
	public static boolean verify(AIP aip) {
		setAip(aip);
		return verify();
	}

	public static boolean verify(String uri) throws ArchiveException {
		try {
			return verify(new URI(uri));
		} catch (URISyntaxException e) {
			throw new ArchiveException(
					"URI "+uri.toString()+" URISyntaxException!"
					+"\n"+e.getMessage());
		}
	}
	
	public static boolean verify(URI uri) throws ArchiveException {
		try {
			return verify(uri.toURL());
		} catch (MalformedURLException e) {
			throw new ArchiveException(
					"URI "+uri.toString()+" MalformedURLException!"
					+"\n"+e.getMessage());
		}
	}
	
	public static boolean verify(URL url) throws ArchiveException {
		try {
			DataStore store = getDataStore(url.toURI());
//			log.debug ("URL: "+ url.toString());
//			log.debug ("URI: "+ url.toURI().toString());
			if (store.verify(url)) return true;
			else return false;
		} catch (ArchiveException e) {
			throw new ArchiveException(e.getMessage());
		} catch (URISyntaxException e) {
			throw new ArchiveException(
					"URL "+url.toString()+" URISyntaxException!"
					+"\n"+e.getMessage());
		}
	}

	public AIP getAip() {
		return aip;
	}
}
