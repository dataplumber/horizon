package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.external.ArchiveMetadata;
import gov.nasa.horizon.archive.external.InventoryAccess;
import gov.nasa.horizon.archive.external.InventoryFactory;
import gov.nasa.horizon.archive.external.InventoryQuery;
import gov.nasa.horizon.inventory.api.Constant.ProductStatus;
import gov.nasa.horizon.inventory.model.ProductType;
//import gov.nasa.horizon.inventory.model.ProductTypeElement;
import gov.nasa.horizon.inventory.model.ProductTypeLocationPolicy;
import gov.nasa.horizon.inventory.model.Product;
import gov.nasa.horizon.inventory.model.ProductArchive;
import gov.nasa.horizon.inventory.model.ProductReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Reassociate {
	private static Log log = LogFactory.getLog(Reassociate.class);
	
	private List<String> errorList = new ArrayList<String>();
	private List<String> msgList = new ArrayList<String>();
	private Map<String,String> locMap = new HashMap<String,String>();
	private Map<String,String> toMap = new HashMap<String,String>();
	InventoryQuery iq = InventoryFactory.getInstance().getQuery();
	InventoryAccess ia = InventoryFactory.getInstance().getAccess();
	private boolean testMode = true;
	private boolean interactive = true;
	//the default, overwrite files
	private boolean updateMetaOnMissing = false;
	private boolean overwrite = true;
	
	
	public void setTestMode(boolean val){
		this.testMode = val;
	}
	
	
	public Map<String,Object> productReassociate(Long productId, String toProductType){
		interactive = false;
		Product g = iq.getProductMetadata(productId).getProduct();

		log.debug("From ProductType:" +g.getPtId());
		ProductType fromD = iq.fetchProductType(g.getPtId());
		
		
		log.debug("To ProductType:" +toProductType);

		ProductType toD = iq.fetchProductTypeByPersistentId(toProductType);
		String toBasePath = null;
		
		//make sure locMaps are defined (location policies).
		for(ProductTypeLocationPolicy dlp : iq.getProductTypeLocationPolicies(fromD.getId())){
			locMap.put(dlp.getType(), dlp.getBasePath());
		}
		//fetch toBasePath
		for(ProductTypeLocationPolicy dlp : iq.getProductTypeLocationPolicies(toD.getId())){
			if(dlp.getType().contains("ARCHIVE"))
				toBasePath = dlp.getBasePath();
			toMap.put(dlp.getType(),dlp.getBasePath());
		}
		
		HashMap<String,Object> ret = new HashMap<String,Object>();
		
		if(toBasePath == null){
			log.info("No toBasePath Found. Exiting.");
			errorList.add("No 'to basepath' found for productType ["+toProductType+"]. Failed to reassociate product.");
			ret.put("type", "failure");
			ret.put("msgs", errorList);
			return ret;
		}
		
		log.info("Reassociating Product ["+g.getId()+":"+g.getName()+"]");
		reassociateProduct(g,toBasePath,toD, fromD);
		if(errorList.size() > 0 ){
			log.debug("Errors processing reassociate.");
			ret.put("type", "failure");
			ret.put("msgs", errorList);
			return ret;
		}
		else{
			log.debug("Successfully processed product");
			ret.put("type", "success");
			ret.put("msgs", msgList);
			return ret;
		}
		
	}
	
	public void reassociateProducts(ProductType fromD, ProductType toD,
			String gnp, boolean moveAll) {
		
		
		String toBasePath = null;
		List<Long> productIds;
		
		//get products by name/pattern
		if(moveAll){
			productIds = iq.getProductIdList(fromD.getId());
		}
		else{
			//find the products by pattern...
			productIds = new ArrayList<Long>();
			List<Product> gList = iq.locateProducts(fromD.getId(), gnp, null, null);
			for(Product g: gList){
				productIds.add(g.getId());
			}
		}
		log.info("Number of products to reassociate: " + productIds.size());
		
		//fetch fromBasePaths
		for(ProductTypeLocationPolicy dlp : iq.getProductTypeLocationPolicies(fromD.getId())){
			locMap.put(dlp.getType(), dlp.getBasePath());
		}
		//fetch toBasePath
		for(ProductTypeLocationPolicy dlp : iq.getProductTypeLocationPolicies(toD.getId())){
			if(dlp.getType().contains("ARCHIVE"))
				toBasePath = dlp.getBasePath();
			toMap.put(dlp.getType(),dlp.getBasePath());
		}
		if(toBasePath == null){
			log.info("No toBasePath Found. Exiting.");
			System.exit(99);
		}
		int count = 1;
		for(Long i : productIds){

			Product g  = iq.getProductMetadata(i).getProduct();
			System.out.println("Processing product "+count+" of " + productIds.size() + " ["+g.getId()+":"+g.getName()+"]");
			log.info("Processing product "+count+" of " + productIds.size() + " ["+g.getId()+":"+g.getName()+"]");
			reassociateProduct(g, toBasePath, toD,fromD);
			++count;
		}
		
		if(errorList.size() > 0){
		
			System.out.println("---------------------------------");
			System.out.println("Processing completed with errors:");
			System.out.println("---------------------------------");
			for(String s : errorList){
				System.out.println("\t" + s);
			}
			
			log.debug("---------------------------------");
			log.debug("Processing completed with errors:");
			log.debug("---------------------------------");
			for(String s : errorList){
				log.debug("\t" + s);
			}
		}
		else{
			System.out.println("-----------------------------------");
			System.out.println("Processing completed without errors");
			System.out.println("-----------------------------------");
			
			log.debug("-----------------------------------");
			log.debug("Processing completed without errors");
			log.debug("-----------------------------------");
		}
	}

	private void reassociateProduct(Product g, String toBasePath, ProductType toD, ProductType fromD){
		/*
		if(this.testMode){
			String fromLoc=null, toLoc=null;
			for(ProductArchive ga : iq.getProductArchiveSet()){
				//create old,new file locations
				fromLoc = g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName();
				if(interactive)
					System.out.println("\tFrom: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
				log.info("\tFrom: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
				msgList.add("["+g.getId()+"] From: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
				toLoc = toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName();
				if(interactive)
					System.out.println("\tto: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
				log.info("\tto: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
				msgList.add("["+g.getId()+"] to: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
			}
			return;
		}
		*/
		
		//offlineProduct
		//ia.updateProductArchiveStatus(g.getProductId(), "OFFLINE");
		
		//move files
		if(!moveFiles(g,toBasePath)){
			//ERROR OCCURED, should we skip the rest?
			log.warn("Error moving files. Product id ["+g.getId()+"] will abort processing (metadata has not been changed.");
			errorList.add("Error moving files. Product id ["+g.getId()+"] will abort processing (metadata has not been changed.)");
			
			return;
		}	
		log.debug("Set product root, productType to ["+toBasePath+"," + toD.getId()+"]");
		g.setRootPath(toBasePath);
		g.setPtId(toD.getId());
		String at = null;
		try{
			at = iq.getProductTypeAccessType(toD.getId());
		}catch(Exception e){
			log.debug("error getting productType info.");
			//at = iq.getProductTypeMetadata(toD.getId()).getProductType().getProductTypePolicy().getAccessType();
		}
		//TODO see if this was necessary at all
		//g.setAccessType(at);
		
		
		//see if there exists a product for the toProductType already
		log.debug("Delete Checks");
		Product exists =  iq.fetchProduct(toD.getId(), g.getName());
		if(exists != null){
			log.debug("Product exists in destination product type.Removing product["+exists.getId()+"]");

			ia.deleteProduct(exists, false);
			
		}
		
		
		ia.updateProductInfo(g);  //UNCOMMENT THIS 
		
		//update productRefs
		log.debug("Changing local product reference paths");
		//reReference(g,toMap, locMap);
		
		
		//TODO
		//update product_real, date, int, char, spatial
		//Productelements will need to be mapped to the new DEIDS and updated in the product_* tables
		log.debug("Chagning product elements to use new productType IDs");
		//reElement(g, toD, fromD);
	
		//set product to online
		//ia.updateProductArchiveStatus(g.getProductId(), "ONLINE");
	}
	
	//TODO reimplement when ready
	/*
	private void reElement(Product g, ProductType toD, ProductType fromD) {
		
		ia.reElement(g,toD,fromD);
//		Map<Integer,String> deIDmapping = new HashMap<Integer,String>();
//		for(ProductTypeElement toDE : toD.getProductTypeElementSet()){
//			
//			for(ProductTypeElement fromDE : fromD.getProductTypeElementSet()){
//				if(toDE.getElementDD().equals(fromDE.getElementDD()))
//					deIDmapping.put(fromDE.getDeId(), fromDE.getElementDD().getType()+","+toDE.getDeId());
//			}	
//		}
//		log.debug("DEID Mappings");
//		for(Entry<Integer,String> me : deIDmapping.entrySet()){
//			String[] ary = me.getValue().split(","); 
//			String type = ary[0].trim();
//			Integer deId = Integer.valueOf(ary[1].trim());
//			if(type.equals("time"))
//				type = "DATETIME";
//			
//			log.debug("map "+ me.getKey() + " to " + deId + "["+type+"]");
//			ia.reassociateProductElement(g.getProductId(),me.getKey(),deId,type);
//		}
	}

	private void reReference(Product g, Map<String, String> toMap,
			Map<String, String> locMap) {
		for(ProductReference ref : g.getProductReferenceSet()){
			if(ref.getType().contains("LOCAL")){
				log.debug("from: " + ref.getPath());
				//check to make sure the *Map.gets are not null
				String replaceWith = toMap.get(ref.getType());
				if(replaceWith == null)
				{
					errorList.add("No entry in \"toProductType\" location policy for:" + ref.getType());
					continue;
				}
				String replace = locMap.get(ref.getType());
				if(replace == null){
					errorList.add("No entry in \"fromProductType\" location policy for:" + ref.getType());
					//this should never happen
					continue;
				}

				String newRef = ref.getPath().replaceAll(replace, replaceWith);
				log.debug("newRef: " + newRef);
				//make sure newRef is different that oldRef
				if(newRef.equals(ref.getPath())){
					log.debug("newRef same as oldRef.");
					errorList.add("product ["+g.getProductId()+"] newReference same as old reference.");
					continue;
				}
				ia.updateProductReferencePath(g.getProductId(), ref.getPath(), newRef);
			}
		}
		
	}
   */
   private boolean moveFiles(Product g, String toBasePath) {

      boolean noErrors = true;
      log.debug("Product name: " + g.getName());
      String fromLoc = null, toLoc = null;
      for (ProductArchive ga : iq.getProductArchives(g.getId())) {
         // create old,new file locations
         fromLoc = g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName();
         log.debug("From: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
         toLoc = toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName();
         log.debug("to: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
         if (!ArchiveData.rename(fromLoc.substring(6), toLoc.substring(6), overwrite)) {
            errorList.add("Error moving product file [" + fromLoc + "] to [" + toLoc + "]. Run with debug mode for more details.");
            noErrors = false;
         } else
            log.info("Successfully moved product file [" + fromLoc + "] to [" + toLoc + "].");
      }

      // return true if we had an error.
      return noErrors;

   }

}
