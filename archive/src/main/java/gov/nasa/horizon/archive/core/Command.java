//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.archive.exceptions.ArchiveException.ErrorType;
import gov.nasa.horizon.archive.external.ArchiveMetadata;
import gov.nasa.horizon.archive.external.FileUtil;
import gov.nasa.horizon.archive.external.InventoryFactory;
import gov.nasa.horizon.archive.external.InventoryQuery;
import gov.nasa.horizon.inventory.api.Constant.ProductStatus;
import gov.nasa.horizon.inventory.api.Constant.LocationPolicyType;
import gov.nasa.horizon.inventory.api.*;
import gov.nasa.horizon.inventory.model.*;
//import gov.nasa.horizon.inventory.model.ProductType;
//import gov.nasa.horizon.inventory.model.Product;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides processes of requests coming into Archive Program Set.
 * 
 * @author clwong
 * @version $Id: Command.java 10123 2012-06-07 15:31:38Z gangl $
 *
 */
public class Command {
	private static Log log = LogFactory.getLog(Command.class);	
	private static final String ONLINE = "ONLINE";//ProductStatus.ONLINE.toString();
	private static final String OFFLINE = "OFFLINE";//ProductStatus.OFFLINE.toString();
	private static String arguments = "";
	private static Calendar start= Calendar.getInstance();
	private static Calendar stop = Calendar.getInstance();
	private static boolean startIsDefined = false;
	private static boolean stopIsDefined = false;
	private static boolean verifyChecksum = false;
	
	public static void delete(Long productId, boolean dataOnly, String baseLocation) {
		String msg = "\n\n===== "+new Date()+"  Product Deleted Files [ID="+productId+"] =====";
		ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
		if (metadata==null) {
			log.info(msg+"\nNo metadata found!\n=====");
			return;
		}
		log.debug(metadata.getArchivePathList().size()+" files found for product "+productId);
		List<String> archivePathList = metadata.getArchivePathList();
		if (!archivePathList.isEmpty()) {
			String path = archivePathList.get(0);
			if (baseLocation==null) baseLocation = ArchiveData.getBaseLocation(path);
			else if (!path.startsWith(baseLocation)) {
				baseLocation = ArchiveData.getBaseLocation(path);		
			}
			log.debug("Archive Base Location: " + baseLocation);
			
			List<String> deleted = null;
			try {
				deleted = ArchiveData.delete(archivePathList, baseLocation);
				log.info("archiveBaseLocation: " + baseLocation);

				for(String s : archivePathList){
					log.info("path: " + s);
				}
				
				String defData = System.getProperty("default.data.path");
				if(defData != null){
					for(int i=1; i<=10; i++){
						String mDir = System.getProperty("mirror.directory" + i);
						if(mDir == null)
							break;
						log.info("Check mirror directory: " + mDir);
						List<String> mirroredPathList = new ArrayList<String>();
						for(String s : archivePathList){
							mirroredPathList.add(s.replace(defData, mDir));
						}
						try{
						deleted.addAll(ArchiveData.delete(mirroredPathList));
						}catch(ArchiveException ae){
							continue;
						}
					}
				}

			} catch (ArchiveException e) {
				msg+= "\n"+e.getMessage();
			}
			if (deleted==null || deleted.isEmpty()) {
				msg += "\nNo files deleted!";
			} else {
				for (String file : deleted) msg += "\n"+file;
			}
		} else {
			msg += "\nNo files deleted!";
		}
		InventoryFactory.getInstance().getAccess().deleteProduct(metadata.getProduct(), dataOnly);
		if (dataOnly) {
			// refresh metadata
			metadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
			msg += "\n\nProduct Status: "+metadata.getProduct().getStatus();
			msg += metadata.printProductArchives();
			//msg += metadata.printProductReferences();
		} else {
			metadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
			if (metadata.getProduct()!=null) {
			   msg += "\nProduct metadata not deleted.";
			}
			else {
			   msg += "\nProduct metadata deleted.";
			}
		}
		log.info(msg+"\n\n===== "+new Date());
	}

	public static List<String> move(Long productId, String fromBaseLocation, String toBaseLocation, boolean force) {
		String msg = "\n\n===== "+new Date()+"  Product relocated Files [ID="+productId+"] =====";
		ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
		if (metadata==null) {
			log.info(msg+"\nNo metadata found!\n=====");
			return null;
		}
		List<String> archivePathList = metadata.getArchivePathList();
		List<String> movedList = new ArrayList<String>();
		if (!archivePathList.isEmpty()) {
			
			if (fromBaseLocation==null) {
				log.info("fromBaseLocation is null");
				if (archivePathList.isEmpty()) return null;
				fromBaseLocation = ArchiveData.getBaseLocation(archivePathList.get(0));
			}
			try {
				String toPathBase = null;
				if(metadata.getProduct().getRelPath() != null){
					toPathBase = toBaseLocation + File.separator + metadata.getProduct().getRelPath();
				}
				else{
					toPathBase = toBaseLocation;
				}
				movedList = ArchiveData.move(archivePathList, fromBaseLocation, toPathBase, force);
			} catch (ArchiveException e) {
				log.info("exception thrown");
				msg += e.getMessage();
			}
			
			if (movedList==null || movedList.isEmpty()) {
				msg += "\nNo files relocated!";
			} else {
				InventoryFactory.getInstance().getAccess().updateProductLocation(metadata.getProduct(), fromBaseLocation, toBaseLocation);
				for (String file : movedList) msg += "\n"+file;
				// refresh metadata
				metadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
				msg += "\n\nProduct Status: "+metadata.getProduct().getStatus();
				msg += metadata.printProductArchives();
				//msg += metadata.printProductReferences();
			}		
		} else {
			msg += "\nNo files relocated!";
		}
		log.info(msg+"\n\n===== "+new Date());
		return movedList;
	}
/*
	public static String processAIP (String msg) throws ArchiveException  {
		log.info("processAIP...");
		long time = System.currentTimeMillis();
		Date startTime = new Date();

		String statusMsg = "";
		Map<Integer, List<AIP>> aipById = new TreeMap<Integer, List<AIP>>();
		int archiveCount = 0;  // number of archive files attempted
		int passCount = 0; // number of archive files succeeded
		long totalFileSize = 0l;

		// Buffers to track success or failure archives
		// Integer for ProductId
		// String for "Destination<Status>"
		Map<Integer,List<String>> online = new TreeMap<Integer,List<String>>();
		Map<Integer,List<String>> offline = new TreeMap<Integer,List<String>>();   		
		try {
			ServiceProfile.createServiceProfile(msg);
		} catch (ArchiveException e) {
			throw new ArchiveException(
					ErrorType.SERVICE_PROFILE, e.getMessage());
		}                                  
		// process deletion
		List<String> savedList = ServiceProfile.deletes();

		try {
			aipById = ServiceProfile.createAIPById();
		} catch (ArchiveException e) {
			throw new ArchiveException (
					ErrorType.SERVICE_PROFILE, e.getMessage());
		}
		log.debug("Number of Products="+aipById.size());

		Set<Integer> ids = aipById.keySet();
		for (Integer id : ids) {
			Date archiveTime = new Date();
			List<AIP> aipList = aipById.get(id);
			// process files per product
			for (AIP aip : aipList) {
				aip.setArchiveProductStartDate(archiveTime);
				aip.setArchiveFileStartDate(new Date());

				long fz;
				try {
					fz = ArchiveData.add(aip);
				} catch (ArchiveException e) {
					throw new ArchiveException (
							ErrorType.COPY_FILE, e.getMessage());
				} 
				if (fz >= 0) {
					totalFileSize +=  fz;
					archiveCount++; // increment number of attempts to archive
					// Verfify after archive completes
					// Sort out success/failure attempts into online/offline buffers
					if (ArchiveData.verify(aip)) {  // verified archived file
						if (!offline.containsKey(id)) {
							// this product is not black-listed yet
							if (!online.containsKey(id))
								// first entry for this product; a good one
								online.put(id, new ArrayList<String>());
							online.get(id).add(aip.getDestination().toString()+"<"+aip.getStatus()+">");
						} else { 
							// other archived file not verified even that this file passes
							offline.get(id).add(aip.getDestination().toString()+"<"+aip.getStatus()+">");
						}
						log.debug("productId="+id+":"+aip.getDestination().toString()
								+"<"+aip.getStatus()+">");
						passCount++;
					} else {
						// this archived file failed verification
						if (!offline.containsKey(id)) {
							if (!online.containsKey(id)) {
								// first entry for this product; a bad one
								offline.put(id, new ArrayList<String>());
							} else {
								// first failed archived file so move all other verified to offline
								offline.put(id, online.get(id));
								online.remove(id);
							}            			
						}
						offline.get(id).add(aip.getDestination().toString()
								+"<"+aip.getStatus()+">");        		
						try {
							log.error(
									"productId="+id+":"+aip.getDestination().toString()
									+"<"+aip.getStatus()+">"
									+new String(FileUtil.removeFile(aip.getDestination()) ? " REMOVED! " : "")
							);
						} catch (ArchiveException e) {
							throw new ArchiveException(ErrorType.ARCHIVE, e.getMessage());
						}
					}
					aip.setArchiveFileEndDate(new Date());
				} // test for fz
			} // for aip
			InventoryFactory.getInstance().getAccess().updateAIPArchiveStatus(aipList);
			InventoryFactory.getInstance().getAccess().updateVerifyProductStatus(new ArrayList<Integer>(online.keySet()), ONLINE);
			archiveTime = new Date();
			for (AIP aip : aipList) aip.setArchiveProductEndDate(archiveTime);
		} // for product id
		ServiceProfile.updateArchiveProduct(aipById);
		time = System.currentTimeMillis() - time;
		log.info("Process Time:"+time/1000.+" seconds"+"\tBytes: "+totalFileSize);
		if (archiveCount>0) {
			statusMsg ="\n\n===== "+startTime+"   Archive Request Summary =====\n"	
			+"\nNo. Requested Files: "+aipById.size()+"\tArchived: "+archiveCount+"\tVerified: "+passCount
			+"\nNo. Products: "+(online.size()+offline.size())
			+"\t\t"+ONLINE+": "+online.size()+"\t"+OFFLINE+": "+offline.size()
			+ new String((online.size()>0) ? "\n\n"+ONLINE+":\t{ProductId=[Path<Status>]}\n\t"+online.toString() : "")
			+ new String((offline.size()>0) ? "\n\n"+OFFLINE+":\t{ProductId=[Path<Status>]}\n\t"+offline.toString() : "")
			+"\n\nProcess Time: "+time/1000.+" seconds"+"\tBytes: "+totalFileSize
			+"\n\n===== "+new Date()+"   End of Summary ==============\n"
			;
		}
		log.info("Cleaned up saved files.");
		ArchiveData.delete(savedList, System.getProperty("archive.trash"));
		return statusMsg ;
	}
*/
	public static void processDelete(CommandLine cmdLine) {
		log.info("processDelete: Arguments: "+arguments);
		Map<Long, List<Long>> processIdList = processIdList(cmdLine);
		if (processIdList==null || processIdList.isEmpty()) return;
		boolean dataOnly = cmdLine.hasOption("data-only");	
		List<Long> productIdList = processIdList.get(Long.valueOf(-1));
		log.info("Processing delete for products "+productIdList.get(0));
		if (productIdList==null) {
			Set<Long> productTypeIdList = processIdList.keySet();
			for (Long productTypeIdInt : productTypeIdList) {
			   Long productTypeId = Long.valueOf(productTypeIdInt);
				productIdList = processIdList.get(productTypeId);
				String baseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(productTypeId);
				log.debug("Baselocation: "+baseLocation);
				for (Long productId : productIdList) {
					delete(productId, dataOnly, baseLocation);
				}
			}
		} else {
			Map<Long, String> dsBaseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(productIdList);	
			for (Long productId : productIdList) {
				delete(productId, dataOnly, dsBaseLocation.get(productId));
			}		
		}
	}
	private static Long checkParam(String param,String option)
	{
		try{
			Long temp = Long.parseLong(param);
			
			if(temp < 1)
			{
				System.out.println("Error! "+ option + " option value: " + param + " cannot be a zero or negative value. Exiting.");
				System.exit(1);
			}
			
			return temp;
			
		}catch(NumberFormatException nfe)
			{
				System.out.println("Error! "+ option + " option value: " + param + " cannot be converted to an integer. Exiting.");
				System.exit(1);
			}
		catch(Exception e)
		{
			System.out.println("Unknown error converting command line parameter to integer. Exiting.");
			System.exit(-2);
		}
		return null;
	}
	
	private static Map<Long, List<Long>> processIdList(CommandLine cmdLine) {
		log.info("processIdList: ");
		List<Long> idList = new ArrayList<Long>();	
			if (cmdLine.hasOption("pt")) {
				idList.add(checkParam(cmdLine.getOptionValue("pt"),"-pt"));
			} else if (cmdLine.hasOption("p")) {
					idList.add(checkParam(cmdLine.getOptionValue("p"),"-p"));
						
			} else if (cmdLine.hasOption("ptl")) {
				String[] values = cmdLine.getOptionValues("ptl");
				for (String val : values) idList.add(checkParam(val,"-ptl"));
			} else if (cmdLine.hasOption("pl")) {
				String[] values = cmdLine.getOptionValues("pl");
				for (String val : values) idList.add(checkParam(val,"-pl"));
			} else if (cmdLine.hasOption("ptr") || cmdLine.hasOption("pr")) {
				String[] values = null;
				if (cmdLine.hasOption("ptr")) values = cmdLine.getOptionValues("ptr");
				else if (cmdLine.hasOption("pr")) values = cmdLine.getOptionValues("pr");
				Long beginVal = checkParam(values[0],"-pr or -ptr begin value");
				Long endVal = checkParam(values[1],"-pr or -er end value");
				Long startId = Math.min(beginVal, endVal);
				Long endId = Math.max(beginVal, endVal);
				for (Long i=startId; i<=endId; i++) idList.add(i);
			}
		log.debug("Processed id list has size "+idList.size());
		log.debug("Processed id list has "+idList.get(0));
		Map<Long, List<Long>> processIdList = new TreeMap<Long, List<Long>>();
		// productTypeId, productIds
		if (!idList.isEmpty()) {
			if (cmdLine.hasOption("pt") || cmdLine.hasOption("ptl") || cmdLine.hasOption("ptr")) {
				String[] indexes = null;
				if (cmdLine.hasOption("i")) indexes = cmdLine.getOptionValues("i");
				for (Long productTypeId : idList) {
					log.debug("Fetching info for productTypeId: " + productTypeId);
					List<Long> productIdList;
					if(startIsDefined){
						 productIdList = InventoryFactory.getInstance().getQuery().getProductIdList(productTypeId, start, stop);
					}
					else{
						 productIdList = InventoryFactory.getInstance().getQuery().getProductIdList(productTypeId);
					}
					log.debug("Number of products for " + productTypeId + ": " + productIdList.size());
					if (indexes!=null) {
						int beginVal = Integer.parseInt(indexes[0])-1;
						int endVal = Integer.parseInt(indexes[1]);
						productIdList = productIdList.subList(Math.min(beginVal, endVal), Math.max(beginVal, endVal));
					}
					if (productIdList.isEmpty()) {
						log.info("Products not found!");
					} else {
						processIdList.put(productTypeId, productIdList);
					}
				}					
			} else if (cmdLine.hasOption("p") || cmdLine.hasOption("pl") || cmdLine.hasOption("pr")) {
				List<Long> productIdList = idList;//TODO check for consequences - InventoryFactory.getInstance().getQuery().findProductList(idList);
				if (productIdList.isEmpty()) {
					log.info("Products not found!");
				} else {
					processIdList.put(Long.valueOf(-1), productIdList);
				}
			}
			if (processIdList.isEmpty()) {
				log.info("\n\n===== Cannot find any product/productType per request! =====");
				return null;
			}
		}
		return processIdList;
	}

	public static void processRelocate(CommandLine cmdLine) {
		log.info("processRelocate: Arguments: "+arguments);
		Map<Long, List<Long>> processIdList = processIdList(cmdLine);
		if (processIdList==null || processIdList.isEmpty()) return;
		String basepath = cmdLine.getOptionValue("bp");
		Boolean force = cmdLine.hasOption("f");
		
		System.out.println("force option set to: " + force.toString());
		
		List<Long> productIdList = processIdList.get(Long.valueOf(-1));
		if (productIdList==null) {
			Set<Long> productTypeIdList = processIdList.keySet();
			for (Long productTypeId : productTypeIdList) {
				productIdList = processIdList.get(productTypeId);
				String baseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(productTypeId);
				
				log.info("ProductType BaseLocation: " + baseLocation);
				
				for (Long productId : productIdList) {
					move(productId, baseLocation, basepath, force);
				}
			}
		} else {
			Map<Long, String>dsBaseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(productIdList);	
			for (Long productId : productIdList) {
				
				log.info("ProductType BaseLocation: " + dsBaseLocation.get(productId));
				move(productId, dsBaseLocation.get(productId), basepath, force);
			}	
		}
	}

	public static Calendar parseDate(String in){
		Calendar cal = Calendar.getInstance();
		String pattern = "MM/dd/yyyy";
	    SimpleDateFormat format = new SimpleDateFormat(pattern);
	    Date d = null;
	    try {
			d = format.parse(in);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing date \""+in+"\". Dates must be in format MM/dd/yyyy, such as 01/01/2010");
			System.exit(-1);
		}
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(d);
		TimeZone gmt = TimeZone.getTimeZone("GMT");
		cal.setTimeZone(gmt);
		cal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE) , 0,0,0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static void processRollingStore(CommandLine cmdLine) {
		log.info("processRollingStore: Arguments: "+arguments);
		
		if (cmdLine.hasOption("start")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("start");
			start = parseDate(date);
			startIsDefined=true;
			}
		if (cmdLine.hasOption("stop")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("stop");
			stop= parseDate(date);
			stopIsDefined=true;
		}
		
		if((startIsDefined && !stopIsDefined) || (stopIsDefined && !startIsDefined)){
			System.out.println("You cannot specifiy a start time without also specifying a stop time (or vice versa).");
			System.exit(-1);
		}
		
		Map<Long, List<Long>> processIdList = processIdList(cmdLine);
		if (processIdList==null) return;
		if (processIdList.isEmpty()) { // Scan all rolling-store productTypes
			Map<Long, ArchiveMetadata> productTypeMetadata = InventoryFactory.getInstance().getQuery().getRollingStore();
			log.debug("processRollingStore: size="+productTypeMetadata.size());
			Set<Long> productTypeIds = productTypeMetadata.keySet();
			for (Long productTypeId : productTypeIds) {
				List<Long> productIdList = InventoryFactory.getInstance().getQuery().getProductIdList(productTypeId);
				log.debug("productTypeId="+productTypeId+":productIdList size="+productIdList.size());
				log.debug(productIdList.get(0)+"--"+productIdList.get(productIdList.size()-1));
				ArchiveMetadata metadata = productTypeMetadata.get(productTypeId);
				for (Long productId : productIdList) {
					rollingStore(productId, metadata);
				}			
			}
		} else {
			List<Long> productIdList = processIdList.get(Long.valueOf(-1));
			if (productIdList==null) {
				Set<Long> productTypeIdList = processIdList.keySet();
				for (Long productTypeId : productTypeIdList) {
					productIdList = processIdList.get(productTypeId);
					ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getRollingStore(productTypeId);
					
					for (Long productId : productIdList) {
						rollingStore(productId, metadata);
					}
				}
			} else {
				Map<Long, ArchiveMetadata> productMetadata = InventoryFactory.getInstance().getQuery().getRollingStore(productIdList);
				for (Long productId : productIdList) {
					rollingStore(productId, productMetadata.get(productId));
				}
			}
		}
	}

	public static void processVerify(CommandLine cmdLine) {
		log.info("processVerify: Arguments: "+arguments);		
		Map<Long, List<Long>> processIdList = processIdList(cmdLine);
		if (processIdList==null || processIdList.isEmpty()) return;
		List<Long> productIdList = processIdList.get(Long.valueOf(-1));
		
		
		if (cmdLine.hasOption("cs")) {
			verifyChecksum = true;
		}
		
		// validate references against location policy
		if (cmdLine.hasOption("lp")) {
			/*if (productIdList==null) {
				Set<Long> productTypeIdList = processIdList.keySet();
				for (Long productTypeId : productTypeIdList) {
					productIdList = processIdList.get(productTypeId);
					ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getProductTypeMetadata(productTypeId);
					for (Long productId : productIdList) {
						validateReference( productId, metadata.getLocalBaseLocation());
					}
				}
			} else {
				Map<Long, ArchiveMetadata> metadata = InventoryFactory.getInstance().getQuery().getProductTypeMetadata(productIdList);
				for (Long productId : productIdList) {
					validateReference(productId, metadata.get(productId).getLocalBaseLocation());
				}
			}*/
		} else {
			if (productIdList==null) {
				Set<Long> productTypeIdList = processIdList.keySet();
				for (Long productTypeId : productTypeIdList) {
				   productIdList = processIdList.get(productTypeId);
					Verify.verifyByProductType(productTypeId, productIdList);
				}
			} else {
				log.debug("setting checksum to " + verifyChecksum);
				Verify.verifyByProduct(productIdList, verifyChecksum);
			}
		}
	}
	
	/*
	public static void validateReference (Long productId, Map<String,String>localBaseLocationPolicy) {
		log.debug("validateReference: productId="+productId);
		ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
		boolean updated = 
			InventoryFactory.getInstance().getAccess().updateLocalReference(metadata.getProduct(), localBaseLocationPolicy);			
		if (updated) {
			metadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
			log.info(
					"\n\nProduct Status: "+metadata.getProduct().getStatus()
					+metadata.printProductArchives()
					+metadata.printProductReferences()
					+"\n\n===== ");
		} else {
			log.info("validateReference: productId="+productId+" not updated!");
		}
	}*/
	
	public static void rollingStore(Long productId, ArchiveMetadata productTypeMetadata) {
		String msg = "\n\n===== "+new Date()+"  Rolling-Store Product [ID="+productId+"] =====";
		if (productTypeMetadata==null) 
			{
			log.info("Product productType metadata not retreived. Product [ID="+productId+"] likely not a rolling-store product.");
				return;
			}
		Map<String, String> localBaseLocationPolicy = productTypeMetadata.getLocalBaseLocation();
		Map<String, String> remoteBaseLocationPolicy = productTypeMetadata.getRemoteBaseLocation();
		if (localBaseLocationPolicy == null || remoteBaseLocationPolicy == null) 
			{
				log.info("Base Location or Rolling Store location not set, skipping file.");
				return;		
			}
		String archiveBaseLocation = localBaseLocationPolicy.get(LocationPolicyType.ARCHIVE_OPEN.toString());
		//if it's null, try preview
		if(archiveBaseLocation == null)
			 archiveBaseLocation = localBaseLocationPolicy.get(LocationPolicyType.ARCHIVE_PREVIEW.toString());
		//still null? Try simulated
		//if(archiveBaseLocation == null)
			// archiveBaseLocation = localBaseLocationPolicy.get(LocationPolicyType.ARCHIVE_SIMULATED.toString());
		// cache product
		ArchiveMetadata productMetadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
		if (productMetadata==null) return;
		if (productMetadata.getProduct()==null) return;
		
		// check expiration day
		Date ingestTime = productMetadata.getProduct().getCreateTime();
		Integer dataDuration = productTypeMetadata.getDataDuration();
		if (ingestTime==null || dataDuration==null) 
			{
				log.info("IngestTime or dataDuration not set, skipping file.");
				return;
			}
		long days = (new Date().getTime() - ingestTime.getTime())/(24L*60L*60L*1000L) - dataDuration;
		if (days<=0) {
			log.info(msg+"\nData not expired yet ["+days+" days].");
			return;
		}
		log.debug("productId="+productId+" expired ["+days+" days].");
				
		// get the archived path for DATA file
		List<String> archivePathList = productMetadata.getArchivePathList();
		if ((archivePathList==null) || archivePathList.isEmpty()) {
			log.info(msg+"\nUnable to determine archived data path!");
			return;
		}
		String dataFilePath = InventoryFactory.getInstance().getQuery().getOnlineDataFilePath(productMetadata.getProduct());
		if (dataFilePath==null) {
			log.info(msg+"\nUnable to find local archived online data!");
			return;
		}
		if (archiveBaseLocation==null) {
			archiveBaseLocation = ArchiveData.getBaseLocation(dataFilePath);
			localBaseLocationPolicy.put(LocationPolicyType.ARCHIVE.toString(), archiveBaseLocation);
		}
		String subpath = dataFilePath.replaceFirst(archiveBaseLocation, "");
		log.debug("subpath: " + subpath);
		log.debug("archiveBaseLocation: " + archiveBaseLocation);
		
		// Determine & copy archived files to ftp/sftp remote path
		/*String ftpRemotePath = 
							remoteBaseLocationPolicy.get(LocationPolicyType.REMOTE_FTP.toString())
							+ (subpath.startsWith("/") ? subpath : "/"+subpath);
		boolean existed = false;
		log.debug("ftpRemotePath: " + ftpRemotePath);
		
		
		if (ftpRemotePath != null) {
			try {
			
				if (ArchiveData.verify(ftpRemotePath)) existed = true;
			} catch (ArchiveException e) {};
		}
		if (!existed) {
			String ftpDir = ftpRemotePath.replaceFirst(ArchiveData.getFilename(dataFilePath), "");
			for (String archivePath : archivePathList) {
				// copy dataFilePath to ftpRemoteLocation	
				try {
					FileUtil.getInstance();
					FileUtil.copyFile(archivePath, ftpDir+ArchiveData.getFilename(archivePath));
					existed = true;
				} catch (ArchiveException e) {
					log.info(e.getMessage());
					
				}
				FileUtil.release();
			}
		}
		if (!existed) {
			log.info(msg+"\nRemote files not found!");
			return;
		}
		 */
		// verify the remote references for their existency
		Set<String> remoteLocationTypes = remoteBaseLocationPolicy.keySet();
		if (remoteLocationTypes==null || remoteLocationTypes.isEmpty()) {
			log.info(msg+"\nNo remote base location policy found!");
			return;
		}
		Map<String, String>remoteReferences = new TreeMap<String, String>();
		
		//log.
		for (String remoteLocationType : remoteLocationTypes) {
			String remotePath = remoteBaseLocationPolicy.get(remoteLocationType) 
								+ (subpath.startsWith("/") ? subpath : "/"+subpath);
			log.debug("RemotePath reference: " + remotePath);
			
			try {
				if (ArchiveData.verify(remotePath)) {
					remoteReferences.put(remoteLocationType,remoteBaseLocationPolicy.get(remoteLocationType));
					log.info("Verified: " + remotePath);
				}
			} catch (ArchiveException e) {
				log.info("Exception occurred verifying remote path: " + remotePath);
				
			};
		}
		if (remoteReferences.isEmpty()) {
			log.info(msg+"\n No remote references found.");
			return;
		}
		List<String> dels = null;
		try {
			//DELETE DATA
			dels =ArchiveData.delete(productMetadata.getArchivePathList(), archiveBaseLocation);
			/*
			 * ADD MIRROR DELETE CODE HERE
			 * 
			 */
		
			String defData = System.getProperty("default.data.path");
			if(defData != null){
				for(int i=1; i<=10; i++){
					String mDir = System.getProperty("mirror.directory" + i);
					if(mDir == null)
						break;
					log.info("Check mirror directory: " + mDir);
					List<String> mirroredPathList = new ArrayList<String>();
					for(String s : archivePathList){
						mirroredPathList.add(s.replace(defData, mDir));
					}
					try{
						dels.addAll(ArchiveData.delete(mirroredPathList));
					}catch(ArchiveException ae){
						continue;
					}
				}
			}
			else
				log.info("No Default Data Path listed...");
			
			
				log.info("archiveBaseLocation: " + archiveBaseLocation);
				for(String s : productMetadata.getArchivePathList()){
					log.info("path: " + s);
					
				}
			
			
			
		} catch (ArchiveException e) {
			msg += "\n"+e.getMessage();
		}
		log.debug("list size: "+ remoteReferences.size());
		//TODO (Check for necessity) - InventoryFactory.getInstance().getAccess().setProductReference(productMetadata.getProduct(), remoteReferences, subpath);
		
		//DELETE local references, set to deleted
		InventoryFactory.getInstance().getAccess().remoteProduct(productMetadata.getProduct());

		// refresh query
		productMetadata = InventoryFactory.getInstance().getQuery().getProductMetadata(productId);
		msg += "\n\nProduct Status: "+productMetadata.getProduct().getStatus()
			+productMetadata.printProductArchives()
			//+productMetadata.printProductReferences()
			+"\n\n===== ";
		log.info(msg);
		
		
		
	}

	protected Command() {}

	public static String getArguments() {
		return arguments;
	}

	public static void setArguments(String[] args) {
		arguments = "";
		for (int i=0; i<args.length; i++) arguments+=" "+args[i];
	}

	public static void processReassociate(CommandLine cmdLine) {
		
		//fd,td, pattern
		Long fromProductType = null;
		Long toProductType = null;
		String gnp = null; // productNamePattern
		boolean testOnly = false;
      try {
         fromProductType= Long.valueOf(cmdLine.getOptionValue("fpt"));
         toProductType = Long.valueOf(cmdLine.getOptionValue("tpt"));
         gnp = cmdLine.getOptionValue("pattern");
         testOnly = cmdLine.hasOption("test");
      } catch (Exception E) {
         System.out.println("Error parsing ProductType ID. A numeric ProductType Id must be entered to run the reassociate Tool.");
         log.info("Error parsing ProductType ID. A numeric ProductType Id must be entered to run the reassociate Tool.");
      }

		
		log.debug("From ProductType: " + fromProductType);
		log.debug("To ProductType  : " + toProductType);
		log.debug("Name Pattern: " + gnp);
		log.debug("Test mode is set to: " + testOnly);
		
		if(fromProductType==null || toProductType == null){
			System.out.println("From and To productType parameters must be valid.");
			System.exit(10);
		}
		
		//get From ProductType, (basepaths)
		InventoryQuery iq = InventoryFactory.getInstance().getQuery();
		ProductType fromD = iq.fetchProductType(fromProductType);
		//get To ProductType (basepaths)
		ProductType toD = iq.fetchProductType(toProductType);
		if(fromD == null){
			System.out.println("From ProductType does not exist. Check the ID ["+fromProductType+"] supplied and try again.");
			log.info("From ProductType does not exist. Check the ID ["+fromProductType+"] supplied and try again.");
			System.exit(10);
		}
		if(toD == null){
			System.out.println("To ProductType does not exist. Check the ID ["+toProductType+"] supplied and try again.");
			log.info("To ProductType does not exist. Check the ID ["+toProductType+"] supplied and try again.");
			System.exit(10);
		}
		if(toD.getId().equals(fromD.getId())){
			System.out.println("To and From productTypes are the same ["+toProductType+"]");
			log.info("To and From productTypes are the same ["+toProductType+"]");
			System.exit(11);
		}
		log.info("Processing from productType ["+fromD.getIdentifier()+":"+fromD.getId()+"] to productType ["+toD.getIdentifier()+":"+toD.getId()+"]");
		
		boolean moveAll = false;
		//if no pattern exists, we'll move all products. confirm this action.
		if(gnp == null){
			moveAll = true;
		}
		else if (gnp.equals("")){
			moveAll = true;
		}
		
		String answer = "";
		InputStreamReader istream = new InputStreamReader(System.in) ;
        BufferedReader bufRead = new BufferedReader(istream) ;
        boolean proper = false;
		if(moveAll && !testOnly){
			do{
				System.out.println("\nNo Product Pattern Name was specified. This will move all products in productType["+fromD.getIdentifier()+"] to productType["+toD.getIdentifier()+"]. Do you want to continue? [y/n]");
				log.info("Confirming move all products:");
				try {
		            	answer = bufRead.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(answer.equalsIgnoreCase("y"))
					{
						System.out.println("Received \"yes\" signal. Proceeding with reassociations of all products");
						log.info("Received \"yes\" signal. Proceeding with reassociations of all products");
						proper=true;
					}
					else if(answer.equalsIgnoreCase("n"))
					{
						System.out.println("Received \"no\" signal. Skipping reassociations");
						log.info("Received \"no\" signal. Skipping relocation of products");
						System.exit(500);
					}
					if(!proper)
						System.out.println("Please enter a 'y'es or 'n'o response. You entered: \""+answer+"\"");
			}while(!proper);
			log.debug("Continuing with reassociate after moveAll check.");
		}
		Reassociate r = new Reassociate();
		r.setTestMode(testOnly);
		r.reassociateProducts(fromD, toD, gnp, moveAll);
		
		//get products by name/pattern
		
		//create old,new file locations
		
		//confirm the move
		
		//move file from A to B
		
		//Update the following tables:
		//ProductArchive
		//ProductReference
		//Product (productType, root, rel?)
		//ProductMetaHistory
		//Productelements will need to be mapped to the new DEIDS and updated in the product_* tables
	}
	
	public static void processLocate(CommandLine cmdLine) {

		Long dId = null;
		Long startTime=null, stopTime = null;
		try{
		 dId = Long.valueOf(cmdLine.getOptionValue("productType"));
		}catch(Exception E){
			System.out.println("Error parsing ProductType ID. A numeric ProductType Id must be entered to run the Locate Tool.");
			log.info("Error parsing ProductType ID. A numeric ProductType Id must be entered to run the Locate Tool.");
		}
		String pattern = null; 
		if(cmdLine.hasOption("pattern")){
			pattern = cmdLine.getOptionValue("pattern");
		}
		else
			pattern=null;
		String outFile = null;
		if(cmdLine.hasOption("output")){
			outFile = cmdLine.getOptionValue("output");
		}
		
		if (cmdLine.hasOption("start")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("start");
			start = parseDate(date);
			startTime = start.getTimeInMillis();
			startIsDefined=true;
			}
		else 
			start = null;
		
		if (cmdLine.hasOption("stop")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("stop");
			stop= parseDate(date);
			stopTime = stop.getTimeInMillis();
			stopIsDefined=true;
		}
		else
			stop = null;
		
//		System.out.println("ProductType: " + dId);
//		System.out.println("Pattern: " + pattern);
		
		log.info("ProductType: " + dId);
		log.info("Pattern: " + pattern+"\n");
		
		if(startIsDefined){
			System.out.println("start: " + start.getTime());
			log.info("start: " + start.getTime());
		}
		if(stopIsDefined){
			System.out.println("stop: " + stop.getTime());
			log.info("stop: " + stop.getTime());
		}
		
		//do the search now
		List<Product> gl = InventoryFactory.getInstance().getQuery().locateProducts(dId, pattern, startTime, stopTime);
		if(gl == null){
			log.error("Error fetching products.");
			return;
		}
//		System.out.println("returned size: " + gl.size());
//		System.out.println("[product_id] product_name");
//		System.out.println("--------------\n");
		log.info("returned size: " + gl.size());
		log.info("[product_id] product_name");
		log.info("--------------\n");
		
		for(Product g: gl){
//			System.out.println("["+g.getId()+"] "+g.getName());
			log.info("["+g.getId()+"] "+g.getName());
			if(outFile != null){
			    //output to file here	
			}
		}
	}	
}
