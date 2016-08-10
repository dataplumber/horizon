//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.external.InventoryFactory;
//import gov.nasa.horizon.inventory.api.Constant.ProductStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains methods to process verify requests.
 *
 * @author clwong
 * $Id: Verify.java 8718 2011-11-16 16:12:49Z gangl $
 */
public class Verify {
	private static Log log = LogFactory.getLog(Verify.class);
	private static Map<String,Map<Long, List<String>>> result = null;
					// result stored as [status, [id, <paths>]]
	private static final int MAX_PRODUCT_REQUEST_ALLOW =1000;
	private static boolean doChecksum = true;
	private static String ONLINE = "ONLINE";
	private static String OFFLINE = "OFFLINE";
	

	public static void verifyByProductType(Long productTypeId, List<Long> productIdList) {
		log.debug("verfiyByProductType: productTypeId="+productTypeId);
		long time = System.currentTimeMillis();
		Date startTime = new Date();

		Integer productSize = InventoryFactory.getInstance().getQuery().getProductSizeByProductType(productTypeId);
		if (productSize>MAX_PRODUCT_REQUEST_ALLOW || productSize > productIdList.size()) {
			verifyByProduct(productIdList);
			return;
		}
		List<AIP> archiveList = new ArrayList<AIP>();
		List<AIP> refList = new ArrayList<AIP>();
		int archiveVerifyCount = 0;
		int referenceVerifyCount = 0;
		String archiveResultFormat = "";
		String refResultFormat = "";

		if (productSize>0) {
			archiveList = InventoryFactory.getInstance().getQuery().getArchiveAIPByProductType(productTypeId);
			if (archiveList.size()>0) {
				archiveVerifyCount = verifyArchive(archiveList);
				if (archiveVerifyCount<0) {
					archiveVerifyCount = 0;
					archiveResultFormat = "\n\nVerification skipped.\n";
				} else archiveResultFormat = "\n\n"+formatResult();
			}					

			refList = InventoryFactory.getInstance().getQuery().getReferenceAIPByProductType(productTypeId);
			if (refList.size()>0) {
				referenceVerifyCount = verifyReference(refList);
				if (referenceVerifyCount<0) {
					referenceVerifyCount = 0;
					refResultFormat = "\n\nVerification skipped.\n";
				} else refResultFormat = "\n\n"+formatResult();
			}
		}

		time = System.currentTimeMillis() - time;

		log.info(
				"\n\n===== "+startTime+"   Archive Verification Summary =====\n"
				+"\nProductType Id\t:\t"+productTypeId
				+"\nNo. Products\t:\t"+productSize
				+"\nNo. Archives\t:\t"+archiveList.size()+"\tFailures:\t"+archiveVerifyCount
				+archiveResultFormat
				+"\nNo. References\t:\t"+refList.size()+"\tFailures:\t"+referenceVerifyCount
				+refResultFormat
				+"\nProcess Time\t:\t"+time/1000. + " seconds"
				+"\n\n===== "+new Date()+"   End of Summary ===================\n"
		);
	}
	
	public static void verifyByProduct(List<Long> productIdList, boolean verifyChecksum){
		doChecksum = verifyChecksum;
		Verify.verifyByProduct(productIdList);
	}
	
	public static void verifyByProduct(List<Long> productIdList) {
		int productSize = productIdList.size();
		log.debug("verifyByProduct:"+productSize);
		if (productSize>MAX_PRODUCT_REQUEST_ALLOW) {
			log.warn("Number of products["+productSize
					+"] requested exceeds limit ["+MAX_PRODUCT_REQUEST_ALLOW+"]!");		
			for (int i=0; i<productSize; i+=MAX_PRODUCT_REQUEST_ALLOW) {
				verifyByProduct(
						new ArrayList<Long>(productIdList.subList(i,
						i+MAX_PRODUCT_REQUEST_ALLOW-1<productSize?i+MAX_PRODUCT_REQUEST_ALLOW:productSize))
						);
			}
			return;
		}
		long time = System.currentTimeMillis();
		Date startTime = new Date();
		
		List<AIP> archiveList = new ArrayList<AIP>();
		List<AIP> refList = new ArrayList<AIP>();
		int archiveVerifyCount = 0;
		int referenceVerifyCount = 0;
		String archiveResultFormat = "";
		String refResultFormat = "";
		if ((productIdList != null) && (!productIdList.isEmpty())) {
			try {
				archiveList = InventoryFactory.getInstance().getQuery().getArchiveAIPByProduct(productIdList);
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
			if ((archiveList != null) && (!archiveList.isEmpty())) {
				archiveVerifyCount = verifyArchive(archiveList,doChecksum);
				if (archiveVerifyCount<0) {
					archiveVerifyCount = 0;
					archiveResultFormat = "\n\nVerification skipped.\n";
				} else archiveResultFormat = "\n\n"+formatResult();
			}					
		
			//refList = null;//TODO - InventoryFactory.getInstance().getQuery().getReferenceAIPByProduct(productIdList);
			if ((refList != null) && (!refList.isEmpty())) {
				referenceVerifyCount = verifyReference(refList);
				if (referenceVerifyCount<0) {
					referenceVerifyCount = 0;
					refResultFormat = "\n\nVerification skipped.\n";
				} else refResultFormat = "\n\n"+formatResult();
			}
		}
		time = System.currentTimeMillis() - time;
		
		log.info(
				"\n\n===== "+startTime+"   Archive Verification Summary =====\n"
				+"\nNo. Products\t:\t"+productSize
				+"\nNo. Archives\t:\t"+archiveList.size()+"\tFailures:\t"+archiveVerifyCount
				+archiveResultFormat
				//+"\nNo. References\t:\t"+refList.size()+"\tFailures:\t"+referenceVerifyCount
				//+refResultFormat
				+"\nProcess Time\t:\t"+time/1000. + " seconds"
				+"\n\n===== "+new Date()+"   End of Summary ===================\n"
		);
	}
	
	private static int verifyArchive(List<AIP> archiveList, boolean checksum) {
		doChecksum = checksum;
		return verifyArchive(archiveList);
	}
	
	private static int verifyArchive(List<AIP> archiveList) {
		log.debug("verifyArchive:"+archiveList.size());
		int failCount = 0;
		result = new TreeMap<String,Map<Long, List<String>>>();
		List<Long> failProductIdList = new ArrayList<Long>();
		List<AIP> verifyList = new ArrayList<AIP>();
		for (AIP archive : archiveList) {
			if (ArchiveData.isInProcess(archive)) {
				resultAdd(archive);
			} else {
				verifyList.add(archive);
				log.debug("setting checksum to " + doChecksum);
				if (!ArchiveData.verify(archive, doChecksum)) {
					failCount++;
					if (!failProductIdList.contains(archive.getProductId()))
						failProductIdList.add(archive.getProductId());
					resultAdd(archive);
				}
				else{
					log.debug("Archive verified.");
					log.debug("***Status: " + archive.getStatus());
					if(!archive.getStatus().equals("ONLINE") && !archive.getStatus().equals("DELETED")){
						log.debug("Product is not online, update status.");
						archive.setStatus("ONLINE");
					}
				}
			}
		}
		InventoryFactory.getInstance().getAccess().updateAIPProductArchiveStatus(verifyList);
		//TODO replace hardcoded string with constant
		InventoryFactory.getInstance().getAccess().updateVerifyProductStatus(failProductIdList, "OFFLINE");
		return failCount;
	}
	
	private static int verifyReference(List<AIP> refList) {
		log.debug("verifyReference: "+refList.size());
		int failCount = 0;
		result = new TreeMap<String,Map<Long, List<String>>>();
		List<AIP> verifyList = new ArrayList<AIP>();
		for (AIP ref : refList) {
			if (ArchiveData.isInProcess(ref)) {
				resultAdd(ref);
			} else {
				verifyList.add(ref);
				if (!ArchiveData.verify(ref)) {
					failCount++;
					//List<AIP> tempList = new ArrayList<AIP>();
					//tempList.add(ref);
					//log.debug("updating reference...");
					//InventoryFactory.getInstance().getAccess().updateAIPProductReferenceStatus(tempList);
					resultAdd(ref);
				}
			}
		}
		//InventoryFactory.getInstance().getAccess().updateAIPProductReferenceStatus(verifyList);
		return failCount;
	}
	
	private static void resultAdd(AIP aip) {
		if (aip==null) return;
		if (!result.containsKey(aip.getStatus())) {
			result.put(aip.getStatus(), new TreeMap<Long, List<String>>());
		}
		Map<Long,List<String>> statusMap = result.get(aip.getStatus());
		if (!statusMap.containsKey(aip.getProductId())){
			statusMap.put(aip.getProductId(), new ArrayList<String>());
		}
		statusMap.get(aip.getProductId()).add(aip.getDestination().toString());
		if(aip.getNote() != null){
			if(aip.getNote().contains("ANOMALY")){
				statusMap.get(aip.getProductId()).add(aip.getNote().toString());
			}
		}
	}
	
	private static String formatResult() {
		Iterator<Entry<String,Map<Long, List<String>>>> 
			resultIterator = result.entrySet().iterator();
		String report = "";
		StringBuffer buf = new StringBuffer();
		while (resultIterator.hasNext()) {
			Entry<String, Map<Long, List<String>>> result = resultIterator.next();
			// <staus, [id, <paths>]>
			buf.append(result.getKey()+":\t{ProductId=[Paths]}\n\t"
					+result.getValue().toString()+"\n");
			/*report +=result.getKey()+":\t{ProductId=[Paths]}\n\t"
					+result.getValue().toString()+"\n";*/			
		}
		report = buf.toString();
		return report;
	}
}
