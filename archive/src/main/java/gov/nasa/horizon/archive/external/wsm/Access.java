package gov.nasa.horizon.archive.external.wsm;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.archive.core.ArchiveData;
import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.external.InventoryAccess;
import gov.nasa.horizon.archive.external.InventoryFactory;
import gov.nasa.horizon.archive.xml.Packet;
import gov.nasa.horizon.inventory.api.InventoryApi;
import gov.nasa.horizon.inventory.api.Constant.ProductArchiveStatus;
import gov.nasa.horizon.inventory.api.Constant.ProductArchiveType;
import gov.nasa.horizon.inventory.api.Constant.ProductStatus;
import gov.nasa.horizon.inventory.api.Constant.LocationPolicyType;
import gov.nasa.horizon.inventory.api.InventoryException;
import gov.nasa.horizon.inventory.model.ProductType;
import gov.nasa.horizon.inventory.model.Product;
import gov.nasa.horizon.inventory.model.ProductReference;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

class Access implements InventoryAccess
{
	final private Log log = LogFactory.getLog(Query.class);
	final private InventoryApi service; 
	private String user = null;
	private String pass = null;
	
	public Access(String URI) {
		service = new InventoryApi(URI);
        user=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_USER);
        pass=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_PASSWORD);
        	
        service.setAuthInfo(user, pass);
	}

//	public Acess(String URI){
//
//	}
   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#delete(gov.nasa.horizon.inventory.model.Product)
    */
   @Override
   public boolean delete(Product product) {
      try {
         service.deleteProduct(product.getId(), false);
         return true;
      } catch (InventoryException e) {
         log.error("unable to delete product: " + product.getId());
         return false;
      }
   }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#deleteProduct(gov.nasa.horizon.inventory.model.Product, boolean)
    */
   @Override
   public void deleteProduct(Product product, boolean dataOnly) {
      // this.request ("product/delete?productId=" +
      // product.getProductId()+"&dataOnly="+dataOnly);
      if (dataOnly) {
         // if request not to delete metadata
         log.debug("delete archived data-only;");
         setProductArchiveStatus(product, ProductArchiveStatus.DELETED.toString()); // will
                                                                                    // work
         //removeLocalReference(product); // will work
         try {
            List<ProductReference> refSet = service.getProductAllReferences(product.getId());
            if (refSet == null || refSet.isEmpty()) {
               service.updateProductStatus(product.getId(), "OFFLINE");
            }
         } catch (InventoryException e) {
            log.error("Unable to update product status.");
         }
      } else {
         if (delete(product))
            log.debug("Metadata: deleted.");
      }
   }

    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#remoteProduct(gov.nasa.horizon.inventory.model.Product)
    */
   @Override
   public void remoteProduct (Product product)
    {
    	//this.request ("product/remote?productId=" + product.getProductId());
    	//removeLocalReference(product);
		setProductArchiveStatus(product, ProductArchiveStatus.DELETED.toString());
    }

    public void updateAIPArchiveStatus (List<AIP> aipList)
    {
    	log.debug("updateAIPArchiveStatus: aipsize="+aipList.size());
		for (AIP aip : aipList) {
			//set productArchive 
			//status, gId,name (dest substring), destination
			updateAIPProductArchiveStatus(aip);
			if (aip.getType().equals(ProductArchiveType.DATA.toString())) {
				//TODO - updateAIPProductReferenceStatus(aip);
			}
		}	
    }

    public void updateAIPProductArchiveStatus (List<AIP> aipList)
    {
    	log.debug("updateAIPProductArchiveStatus: aipsize="+aipList.size());
		// Use direct native sql instead;
		
		for (AIP aip : aipList) {
			updateAIPProductArchiveStatus(aip);
		}	
	   
    }
    public void updateAIPProductArchiveStatus (AIP aip){
       try {
          service.updateProductArchiveStatus(aip.getProductId(),aip.getStatus());
          //TODO update verify time as well (no verify time field implemented in 5.0.0)
       }
       catch(InventoryException e) {
          
       }
    }
/*
    private void updateAIPProductReferenceStatus (AIP aip){
//    	this.request ("AIPProductArchive/update?productId=" + aip.getProductId()
//				+"&status="+aip.getStatus()
//				+"&name="+aip.getDestination().toString().substring(aip.getDestination().toString().lastIndexOf(File.separator)+1));
    	service.updateProductAIPReference(aip.getProductId(),aip.getType(),aip.getDestination().toString(),aip.getDestination().toString().substring(aip.getDestination().toString().lastIndexOf(File.separator)+1),aip.getStatus());
    }
    
    public void updateAIPProductReferenceStatus (List<AIP> aipList)
    {
    	log.debug("updateAIPProductReferenceStatus: aipsize="+aipList.size());
    	for (AIP aip : aipList) {
    		log.debug("type: " + aip.getType());
    		//if(aip.getType().equals(ProductArchiveType.DATA.toString())){
    			updateAIPProductReferenceStatus(aip);
    		//}
		}	
    	
    }
*/
    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#updateProductArchiveChecksum(java.lang.Long, java.lang.String, java.lang.String)
    */
   @Override
   public void updateProductArchiveChecksum (Long productId, String name, String sum)
    {
		log.debug("updateProductArchiveChecksum "+productId);
		try {
			service.updateProductArchiveChecksum(productId, name, sum);
		} catch (InventoryException e) {
			log.error("error updating information: "+ e.getMessage());
		}
    }

    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#updateProductArchiveSize(java.lang.Long, java.lang.String, java.lang.Long)
    */
   @Override
   public void updateProductArchiveSize (Long productId, String name, Long Size)
    {
    	log.debug("updateProductArchiveSize "+productId);
    	try {
			service.updateProductArchiveSize(productId, name, Size);
		} catch (InventoryException e) {
			log.error("error updating information: "+ e.getMessage());
		}
    	
    }

    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#updateProductArchiveStatus(java.lang.Long, java.lang.String)
    */
   //@Override
   public void updateProductArchiveStatus (Long productId, String status)
    {
    	log.debug("updateProductArchiveSize "+productId);
    	try {
			service.updateProductArchiveStatus(productId, status);
		} catch (InventoryException e) {
			log.error("error updating information: "+ e.getMessage());
		}
    }

    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#updateProductLocation(gov.nasa.horizon.inventory.model.Product, java.lang.String, java.lang.String)
    */
   @Override
   public void updateProductLocation (Product product, String archiveBaseLocation, String baseLocation)
    {
    	log.debug("UpdateProductLocation");
    	String basepath = ArchiveData.absolutePath(baseLocation);

		//Only update the product root_path
//		if(!basepath.startsWith("file://"))
//			basepath = "file://" + basepath;
//		
		//call to update root_path
		try {
			service.updateProductRootPath(product.getId(), basepath);
		} catch (InventoryException e) {
			log.error("Error updating product basepath: "+e.getMessage());
			log.error("Will not remove local product references.");
			return;
		}
		
		//removeLocalReference(product);
    }
/*
    public boolean updateLocalReference (Product product, Map<String, String> localBaseLocationPolicy)
    {
    	log.debug("updateLocalReference:");
		if (product==null) return false;
		if (localBaseLocationPolicy==null) return false;
		String dataFilename = InventoryFactory.getInstance().getQuery().getOnlineDataFilePath(product);
		if (dataFilename != null) {
			String archiveBaseLocation =
				localBaseLocationPolicy.remove(LocationPolicyType.ARCHIVE.toString());
			if (archiveBaseLocation==null) {
				archiveBaseLocation =  ArchiveData.getBaseLocation(dataFilename);
			}
			String subpath = dataFilename.replaceFirst(archiveBaseLocation, "");
			removeLocalReference(product); //add db call for delete
			setProductReference(product, localBaseLocationPolicy, subpath); //add db call for delete
			//update(product);
			
			return true;
		} else {
			log.debug("ProductId="+product.getProductId()+": no ONLINE-DATA for references creation!");
			return false;
		}
    
    }

  //no actual updates done here, jsut setting info for the product object for the "update" call.
    public void setProductReference (Product product, Map<String, String> baseLocationPolicy, String subpath)
    {
    	
    	Set<ProductReference> newSet = new HashSet<ProductReference>(
				product.getProductReferenceSet()
				);
		Set<String> baseLocationTypes = baseLocationPolicy.keySet();
		for (String baseLocationType : baseLocationTypes) {
			ProductReference ref = new ProductReference();
			ref.setType(baseLocationType);
			
			String path = baseLocationPolicy.get(baseLocationType)
						+ (subpath.startsWith("/") ? subpath : "/"+subpath);
			log.info("new path: " + path);
			if (baseLocationType.trim().endsWith("OPENDAP")) {
				ref.setPath(path + ".html");
			} else
				ref.setPath(path);
			ref.setStatus(ProductArchiveStatus.ONLINE.toString());
			boolean found = false;
			for(ProductReference r: newSet)
			{
				if(ref.getPath().equals(r.getPath()))
				{
					log.debug("Remote reference \""+ref.getPath()+"\" already exists. Not adding new reference.");
					found = true;
					r.setStatus("ONLINE");
					try {
						service.updateProductReferenceStatus(r.getProductId(), r.getPath(), "ONLINE");
					} catch (InventoryException e) {
						log.error("Error for product reference (id, path): ["+r.getProductId()+","+r.getPath()+"]");
						log.error("Error updating product reference status: " + e.getMessage());
					}
				}
			}
			if(!found){
				
				if(ref.getStatus() == null)
					ref.setStatus("ONLINE");
				
				try {
					service.addProductReference(ref.getProductId(), ref.getPath(), ref.getStatus(), ref.getType(), ref.getDescription());
				} catch (InventoryException e) {
					log.error("Error for product reference (id, path): ["+ref.getProductId()+","+ref.getPath()+"]");
					log.error("Error adding product reference status: " + e.getMessage());
				}
			}
			
		}
		log.debug("Setting product references with following number of remote references: " + newSet.size());
		for(ProductReference r : newSet)
		{
			log.debug("Reference path to add: " +r.getPath());
		}
		product.setProductReferenceSet(newSet);
    	
    }
    
    public  void removeLocalReference(Product product) {
		removeReference(product, LocationPolicyType.LOCAL.toString());
	}
    
    public  void removeReference(Product product, String type) {
		Set<ProductReference> newSet = new HashSet<ProductReference>(
				product.getProductReferenceSet()
				);
		Set<ProductReference> currentSet = product.getProductReferenceSet();	
		
		
		//productId and 
		try {
			service.deleteProductLocalReference(product.getProductId(), type);
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			log.error("Error deleting product references.");
			return;
		}
		
		
		for (ProductReference ref : currentSet) {
			if (ref.getType().startsWith(type)) 
			{
				newSet.remove(ref);
				//call to remove a reference from product.
			}
		}
		product.setProductReferenceSet(newSet);
	}
    */
   
   
   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#setProductArchiveStatus(gov.nasa.horizon.inventory.model.Product, java.lang.String)
    */
   @Override
   public void setProductArchiveStatus(Product product, String status) {
      /*
       * Set<ProductArchive> archiveSet = product.getProductArchiveSet(); for
       * (ProductArchive archive : archiveSet) { archive.setStatus(status); }
       */
      updateProductArchiveStatus(product.getId(), "DELETED");
   }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.test#updateVerifyProductStatus(java.util.List, java.lang.String)
    */
   @Override
   public void updateVerifyProductStatus(List<Long> productIdList, String status) {
      log.debug("updateVerifyProductStatus: productIdList.size=" + productIdList.size());

      // String verifyTime = formatter.format(new Date());
      for (Long gId : productIdList) {
         // String inClause = "("+productIdList.get(i);
         // int nextSize = Math.min(i+INCLAUSE_LIMIT, size);
         // for (int j=i+1; j<nextSize; j++) inClause += ", " +
         // productIdList.get(j);
         // inClause += ")";
         try {
            service.updateProductArchiveStatus(gId, status);
         } catch (InventoryException e) {
            log.error("Error updating product[" + gId + "] status to '" + status + "'... " + e.getMessage());
         }
      }
   }

	public void updateProductInfo(Product g){
		//only changes productType_id and basepath.
		try {
			service.updateProduct(g.getId(), g.getRootPath(), g.getPtId());
		} catch (InventoryException e) {
			log.error("unable to reassociate product["+g.getId()+"]: " + e.getMessage());
		}
	
	}
/*
	public void reassociateProductElement(Integer gId,Integer key, Integer deId, String type){
		//don't need to do this...
		//rework this in future release.
		//this.request ("product/verifyUpdate?productId=");
	}
	
	
	@Override
	public void updateProductReferencePath(Integer productId, String path,
			String newRef) {
		
		try {
			service.updateProductReferencePath(productId, path, newRef);
		} catch (InventoryException e) {
				log.error("Error updating product reference Path [id, path]: ["+productId+","+path+"]");
				log.error("Error message: " + e.getMessage());
		}
	}
	
	@Override
	public void reElement(Product g, ProductType toD, ProductType fromD) {
		try {
			service.updateProductReassociateElement(g.getId(), fromD.getId(), toD.getId());
		} catch (InventoryException e) {
			log.error("Could not reasociate product elements: " + e.getMessage());
		}
	}*/
}
