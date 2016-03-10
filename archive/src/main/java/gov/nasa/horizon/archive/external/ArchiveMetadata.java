//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.external;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.common.api.util.StringUtility;
import gov.nasa.horizon.inventory.model.ProductType;
import gov.nasa.horizon.inventory.model.Product;
import gov.nasa.horizon.inventory.model.ProductArchive;
import gov.nasa.horizon.inventory.model.ProductReference;
import gov.nasa.horizon.inventory.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class contains rolling store information.
 *
 * @author clwong
 * $Id: ArchiveMetadata.java 5680 2010-08-26 17:24:57Z niessner $
 */
public class ArchiveMetadata {

	private String dataClass;
	private Integer dataDuration;
	private Map<String, String> remoteBaseLocation = new TreeMap<String, String>();
	private Map<String, String> localBaseLocation = new TreeMap<String, String>();
	private List<String> archivePathList = new ArrayList<String>();
	private ProductType productType;
	private Product product;
	
	public String getDataClass() {
		return dataClass;
	}
	public void setDataClass(String dataClass) {
		this.dataClass = dataClass;
	}
	public Integer getDataDuration() {
		return dataDuration;
	}
	public void setDataDuration(Integer dataDuration) {
		this.dataDuration = dataDuration;
	}
	public Map<String, String> getRemoteBaseLocation() {
		return remoteBaseLocation;
	}
	public void setRemoteBaseLocation(Map<String, String> remoteBaseLocation) {
		this.remoteBaseLocation = remoteBaseLocation;
	}
	public Map<String, String> getLocalBaseLocation() {
		return localBaseLocation;
	}
	public void setLocalBaseLocation(Map<String, String> localBaseLocation) {
		this.localBaseLocation = localBaseLocation;
	}
	public List<String> getArchivePathList() {
		return archivePathList;
	}
	public void setArchivePathList(List<String> archivePathList) {
		this.archivePathList = archivePathList;
	}
	public ProductType getProductType() {
		return productType;
	}
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	/*
	public long getProductSpace(List<String> dels) {
		
		long size = 0;
		try {
   		Set<ProductArchive> archiveSet = service.getProductArchiveSet();
   		for (ProductArchive archive : archiveSet) {
   			
   			for(String d : dels){
   			if(archive.getName().equals(d))
   				size += archive.getFileSize();
   			}
   		}
		}
		catch(InventoryException e) {
		   
		}
		return size;
	}*/
	/*
	public String printProductReferences() {
		String printString = "\n\n----- Product References [ID="+product.getProductId()+"] -----"
							+"\ntype:path:status\n----------------";
		Set<ProductReference> refSet = product.getProductReferenceSet();
		for (ProductReference ref : refSet) {
			printString += "\n"+ref.getType()+":"+ref.getPath()+":"+ref.getStatus();
		}
		return printString;
	}*/
	
	public String printProductArchives() {
		String printString = "\n\n----- Product Archives [ID="+product.getId()+"] -----"
							+"\ntype:path:status\n----------------";
		InventoryQuery iq = InventoryFactory.getInstance().getQuery();
		List<Long> idList = new ArrayList<Long>();
		idList.add(product.getId());
		List<AIP> archiveSet = iq.getArchiveAIPByProduct(idList);
		for (AIP archive : archiveSet) {
			printString += "\n"+archive.getType()+":"+ archive.getDestination()+":"+archive.getStatus();
		}
		return printString;
	}
}
