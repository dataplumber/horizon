package gov.nasa.horizon.archive.external.wsm;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.external.ArchiveMetadata;
import gov.nasa.horizon.archive.external.InventoryQuery;
import gov.nasa.horizon.common.api.util.StringUtility;
import gov.nasa.horizon.inventory.api.Constant.ProductArchiveStatus;
import gov.nasa.horizon.inventory.api.Constant.ProductArchiveType;
import gov.nasa.horizon.inventory.api.Constant.LocationPolicyType;

import gov.nasa.horizon.inventory.model.ProductType;
import gov.nasa.horizon.inventory.model.ProductTypeLocationPolicy;
import gov.nasa.horizon.inventory.model.ProductTypePolicy;
import gov.nasa.horizon.inventory.model.Product;
import gov.nasa.horizon.inventory.model.ProductArchive;
import gov.nasa.horizon.inventory.model.ProductReference;
import gov.nasa.horizon.inventory.api.InventoryApi;
import gov.nasa.horizon.inventory.api.InventoryException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

class Query implements InventoryQuery
{
    final private Log log = LogFactory.getLog(Query.class);
    final private InventoryApi service; 
    private String user = null;
    private String pass = null;
    
    Query (String URI)
    {
        service = new InventoryApi(URI);
        
        user=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_USER);
        pass=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_PASSWORD);
        service.setAuthInfo(user, pass);
    }
    
    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#fetchProduct(java.lang.Long)
    */
   //@Override
   public Product fetchProduct(Long id){
    	try {
			return service.getProduct(id);
		} catch (InventoryException e) {
			e.printStackTrace();
			log.error("Unable to connect to Inventory Service");
			log.debug("error:", e);
		}
		return null;
    }
    
   public Product fetchProduct(Long ptId, String productName) {
      try {
         return service.getProduct(ptId, productName);
      } catch (InventoryException e) {
         e.printStackTrace();
         log.error("Unable to connect to Inventory Service");
         log.debug("error:", e);
      }
      return null;
   }
    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#fetchProductTypeByPersistentId(java.lang.String)
    */
   @Override
   public ProductType fetchProductTypeByPersistentId(String perId) {
    	try {
			return service.getProductType(perId);
		} catch (InventoryException e) {
			e.printStackTrace();
			log.error("Unable to connect to Inventory Service");
			log.debug("error:", e);
		}
		return null;
    }
    
    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#fetchProductType(java.lang.Long, java.lang.String)
    */
   @Override
   public ProductType fetchProductType (Long ptId)
    {
        List<Long> ids = new ArrayList<Long>(1);
        ids.add(ptId);
        log.debug("Returned ProductType Size: " + ids.size());
//        if(ids.size() < 1){
//        	log.info("No productTypes returned.");
//        	return null;
//        }
        return this.fetchProductTypes (ids).get(0);
    }
    
    private ProductType fetchProductType (String shortName)
    {
        try {
			return service.getProductType(shortName);
		} catch (InventoryException e) {
			log.error("Unable to fetch ProductType from inventory WS: " + e.getMessage());
			log.debug("error:", e);
		}
        return null;
        
    }
    
    private List<ProductType> fetchProductTypes (List<Long> ids)
    {
       List<ProductType> dl = new ArrayList<ProductType>();
       
       for(Long id: ids){
    	   try {
			ProductType d = service.getProductType(id);
			dl.add(d);
		} catch (InventoryException e) {
			log.error("Unable to fetch ProductType from inventory WS: " + e.getMessage());
			log.debug("error:", e);
		}
       }
       return dl;
    }
	
    //TODO Find out if still needed
/*
    public List<Integer> fetchProductsByProductTypeAndPatter(Integer productTypeId, String gnp){
		return service.getProductsByProductTypeAndPattern(productTypeId,gnp);
	}
*/
    //TODO find if this is needed

   public List<Product> locateProducts(Long ptId, String pattern, Long startTime, Long stopTime) {
      // not implemented
      try {

         Calendar start = Calendar.getInstance();
         if (startTime != null)
            start.setTimeInMillis(startTime);
         else
            start = null;

         Calendar stop = Calendar.getInstance();
         if (stopTime != null)
            stop.setTimeInMillis(stopTime);
         else
            stop = null;
         Date startDate = null;
         Date stopDate = null;
         if(start != null)
            startDate = start.getTime();
         if(stop != null)
            stopDate = stop.getTime();
         List<Product> gs = service.getProductIdListAll(ptId, startDate, stopDate, 1, true, pattern);
//         List<Product> results = new ArrayList<Product>();
//         if(gs != null) {
//            for (Product xi : gs) {
//               results.add(service.getProduct(xi.getId()));
//            }
//         }
         return gs;

      } catch (InventoryException e) {
         log.error("Error fetching product list:" + e.getLocalizedMessage(),e);
         return null;
      }

   }
    
    
    private List<ProductReference> fetchProductReferences (Long productTypeId)
    {
    	List<ProductReference> list = null;
		try {
			list = service.getProductAllReferences(productTypeId);
		} catch (InventoryException e) {
			log.error("Error fetching Product List: " + e.getMessage());
			System.out.println("Error fetching Product List: " + e.getMessage());
			log.debug("error:", e);
			return null;
		} 
    	return list; //should be done?
    }
    
    private List<Product> fetchProducts (List<Long> ids)
    {
    	List<Product> gl = new ArrayList<Product>();
        for(Long i : ids){
        	try {
				gl.add(service.getProduct(i));
			} catch (InventoryException e) {
				log.debug("Error querying service: " + e.getMessage());
				//log.debug(e.getStackTrace().toString());
			}
        	
        }
        return gl;
    }
    
    //TODO Check if needed
    /*
    public List<String> findProductTypeByProductId (int id)
    {
        List<ProductType> productTypes;
        List<Integer> ids = new ArrayList<Integer>(1);
        List<String> result;
        
        productTypes = this.fetchProductTypes (ids);
        result = new ArrayList<String>(productTypes.size());
        for (ProductType ds : productTypes) result.add (ds.getShortName());
        return result;
    }
    
    public List<Long> findProductList (List<Long> idList)
    {
    	try {
			return service.findProductList(idList);
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			log.error("Error fetching product Id List: " + e.getMessage());
			return null;
		}
        //return this.request ("productVerify?" + this.toString ("productId=", idList)).getIdList().getIds();
    }
    */
    
    public List<ProductArchive> getProductArchives(Long productId) {
       List<ProductArchive> result = null;
       try{
          result = service.getProductArchives(productId);
       }
       catch(InventoryException e) {
          log.error("Could not find product "+productId, e);
       }
       return result;
    }
    
    public List<AIP> getArchiveAIPByProductType (Long productTypeId)
    {
        return this.getArchiveAIPByProduct (this.getProductIdList(productTypeId));
    }
    
   public List<AIP> getArchiveAIPByProduct(List<Long> idList) {
      List<AIP> result = new ArrayList<AIP>(idList.size());
      List<Product> products = new ArrayList<Product>(idList.size());
      for (Long i : idList) {
         Product g = null;
         try {
            g = service.getProduct(i);
         } catch (InventoryException e) {
            log.error("Error fetching Products");
            return null;
         }
         products.add(g);
      }

      for (Product g : products) {
         try {
            List<ProductArchive> gal = service.getProductArchives(g.getId());
            for (ProductArchive ga : gal) {

               ProductTypePolicy policy = service.getProductTypePolicy(g.getPtId());
               result.add(new AIP(g.getId(), new URI(StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(), ga.getName())), ga.getChecksum(), policy.getChecksumType(), ga.getFileSize(), ga.getType(), ga.getStatus()));

            }
         } catch (URISyntaxException urise) {
            this.log.error("getArchiveAIPByProductType: error getting archive info " + urise.getMessage(), urise);
         } catch (InventoryException e) {
            this.log.error("getArchiveAIPByProductType: error getting product type policy" + e.getMessage(), e);
         }
      }

      return result;
   }
    
    private String getArchiveBaseLocation (ProductType productType)
    {
        try {
           List<ProductTypeLocationPolicy> locationPolicySet = service.getProductTypeLocationPolicies(productType.getId());
           ProductTypePolicy ptPolicy = service.getProductTypePolicy(productType.getId());
           
           if (locationPolicySet == null || locationPolicySet.isEmpty()) return "";
           
           LocationPolicyType type = getArchiveLocationType(ptPolicy.getAccessType());
           
           for (ProductTypeLocationPolicy locationPolicy : locationPolicySet)
           {
               if (locationPolicy.getType().startsWith(type.toString())){
               	log.debug("lp.basepath: "+locationPolicy.getBasePath());
                   return locationPolicy.getBasePath();                
               }
           }
           
           this.log.error("Unable to find " + type + " base location!!!");
        }
        catch(InventoryException e) {
           this.log.error("Unable to find " + productType.getIdentifier() + " base location!!!", e);
        }
        return null;
    }

    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getArchiveBaseLocation(java.lang.Long)
    */
   @Override
   public String getArchiveBaseLocation (Long productTypeId)
    {
        try {
           ProductType pt = service.getProductType(productTypeId);
           return this.getArchiveBaseLocation (pt);
        }
        catch(InventoryException e) {
           log.error("Could not find ProductType "+productTypeId+" for archive baseLocation lookup");
        }
        return null;
    }

    //TODO Find if this is needed... definitely not a complete method
    
    public Map<Long, String> getArchiveBaseLocation (List<Long> productIdList)
    {
        List<Product> products = new ArrayList<Product>(productIdList.size());
        Map<Long,String> result = new TreeMap<Long,String>();
        Map<Long,String> dsBaseLocation = new TreeMap<Long,String>();
        
        log.debug("ProductId List is as follow:");
        log.debug(productIdList.get(0));
        for (Long productId: productIdList) {
           try {
              products.add(service.getProduct(productId));
           }
           catch(InventoryException e) {
              log.error("Could not retrieve product information for product "+productId+" when retriving archive base location");
           }
        }

        for (Product g : products)
        {
            Long ptId = g.getPtId();
            String baseLocation = null;
            
            if (dsBaseLocation.containsKey(ptId))
            {
                baseLocation = dsBaseLocation.get (ptId);         
            }
            else
            {
                baseLocation = this.getArchiveBaseLocation (ptId);
                dsBaseLocation.put (ptId, baseLocation);
            }
            log.debug("adding: " + baseLocation);
            result.put (g.getId(), baseLocation);
        }
        log.debug("result " + result.size());
        return result;
    }

    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getArchiveBaseLocation(java.lang.String)
    */
   @Override
   public String getArchiveBaseLocation (String productTypeName)
    {
        return this.getArchiveBaseLocation (this.fetchProductType (productTypeName));
    }

    private LocationPolicyType getArchiveLocationType (String accessType)
    {
        LocationPolicyType result = null;
        
        try { 
        	log.debug("searching for: " + accessType);
        	log.debug("converting to: "+"ARCHIVE_"+accessType);
        	result = LocationPolicyType.valueOf("ARCHIVE_"+accessType);
        	
        }
        catch (IllegalArgumentException iae)
        {
            log.debug("Accesstype not found: " + accessType);
        }
        catch (NullPointerException npe)
        {
            log.debug("Accesstype is null.");
        }
        
        return result;
    }

   private List<String> getArchivePathList(Product product) {
      try {
         List<ProductArchive> archiveSet = service.getProductArchives(product.getId());
         List<String> archiveList = new ArrayList<String>(archiveSet.size());

         for (ProductArchive archive : archiveSet) {
            archiveList.add(StringUtility.cleanPaths(product.getRootPath(), product.getRelPath(), archive.getName()));
         }
         return archiveList;
      } catch (InventoryException e) {
         log.error("Could not get product archive set for product " + product.getName());
      }
      return null;
   }

   private ArchiveMetadata getProductTypeMetadata(ProductType productType) {
      ProductTypePolicy policy = null;
      ArchiveMetadata result = null;
      // get policy
      try {
         policy = service.getProductTypePolicy(productType.getId());


         result = new ArchiveMetadata();
         result.setProductType(productType);
         result.setDataClass(policy.getDataClass());
         result.setDataDuration(policy.getDataDuration());
         //result.setRemoteBaseLocation(this.getRemoteBaseLocation(productType));
         result.setLocalBaseLocation(this.getLocalBaseLocation(productType));
         return result;
      } catch (InventoryException e) {
         // TODO Auto-generated catch block
         log.debug("stack trace: ", e);
      }
      return result;
   }

    /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getProductTypeMetadata(java.lang.Long)
    */
   @Override
   public ArchiveMetadata getProductTypeMetadata (Long productTypeId)
    {
        ArchiveMetadata result = null;
        ProductType productType = null;
        ProductTypePolicy policy = null;
        try {
           productType = service.getProductType(productTypeId);
           policy = service.getProductTypePolicy(productTypeId);
           result = new ArchiveMetadata();
           result.setProductType(productType);
           result.setDataClass (policy.getDataClass());
           result.setDataDuration (policy.getDataDuration());
           //result.setRemoteBaseLocation (this.getRemoteBaseLocation (productTypes.get (0)));
           result.setLocalBaseLocation(this.getLocalBaseLocation(productType));
           return result;
        }
        catch(InventoryException e) {
           log.debug("stack trace: ", e);
        }
        return result;
    }

    
    public Map<Long, ArchiveMetadata> getProductTypeMetadata (List<Long> productIdList)
    {
        ArchiveMetadata metadata = null;            
        List<Product> products = this.fetchProducts (productIdList);
        Map<Long, ArchiveMetadata> result = new TreeMap<Long, ArchiveMetadata>();
        Map<Long, ArchiveMetadata> productTypeCache = new TreeMap<Long,ArchiveMetadata>();

        for (Product g : products)
        {
           Long productTypeId = g.getPtId();
            
            metadata = null;
            
            if (productTypeCache.containsKey (productTypeId)) metadata = productTypeCache.get( productTypeId);         
            else
            {
                metadata = this.getProductTypeMetadata (productTypeId);
                if (metadata != null) productTypeCache.put (productTypeId, metadata);
            }
            
            if (metadata != null) result.put (g.getId(), metadata);
        }
        
        return result;
    }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getProductIdList(java.lang.Long)
    */
   @Override
   public List<Long> getProductIdList(Long productTypeId) {
      return getProductIdList(productTypeId, null, null);
   }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getProductIdList(java.lang.Long, java.util.Calendar, java.util.Calendar)
    */
   @Override
   public List<Long> getProductIdList(Long productTypeId, Calendar start, Calendar stop) {
      List<Long> results = new ArrayList<Long>();
      try {
         Date startTime = null;
         Date stopTime = null;
         if(start != null) {
            startTime = start.getTime();
         }
         if(stop !=null) {
            stopTime = stop.getTime();
         }
         List<Product> products = service.getProductIdListAll(productTypeId, startTime, stopTime, 1, true);
         for (Product product : products) {
            results.add(product.getId());
         }
         return results;
      } catch (Exception e) {//TODO InventoryException e) {
         log.error("Error fetching product list:" + e.getLocalizedMessage(), e);
      }
      return results;

   }
   
   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getProductMetadata(java.lang.Long)
    */
   @Override
   public ArchiveMetadata getProductMetadata(Long productId) {
      ArchiveMetadata result = null;
      try {

         result = new ArchiveMetadata();
         Product g = service.getProduct(productId);

         if (g != null) {
            result.setArchivePathList(getArchivePathList(g));
            result.setProduct(g);
         }
      } catch (InventoryException e) {
         log.error("Could not fetch product during metadata query", e);
      }
      return result;
   }

   
   public Integer getProductSizeByProductType(Long productTypeId) {
      try {
         return service.getProductCount(productTypeId);
      } catch (InventoryException e) {
         log.error("Error fetching product size: " + e.getMessage(), e);
         return null;
      }
   }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getProductVersion(java.lang.Long)
    */
   @Override
   public Integer getProductVersion(Long id) {
      Integer version  = null;
      try {
         Product p = service.getProduct(id);
         version = p.getVersionNum();
      } catch (InventoryException e) {
         log.error("Could not retrieve product when checking version number", e);
      }
      return version;
   }

    private Map<String, String> getLocalBaseLocation(ProductType productType) throws InventoryException
    {
        Map<String, String> result = new TreeMap<String, String>();
        List<ProductTypeLocationPolicy> locationPolicySet = service.getProductTypeLocationPolicies(productType.getId());
        ProductTypePolicy policy = service.getProductTypePolicy(productType.getId());
        

        for (ProductTypeLocationPolicy locationPolicy : locationPolicySet)
        {
            String policyType = locationPolicy.getType();
            
            log.debug("type: "+locationPolicy.getType());
            log.debug("base: "+locationPolicy.getBasePath());
            log.debug("dsId: "+locationPolicy.getPtId());
            
            if (!policyType.startsWith(LocationPolicyType.ARCHIVE.toString()) &&
                 policyType.startsWith(LocationPolicyType.LOCAL.toString()))
            {
                result.put(policyType, locationPolicy.getBasePath());
            }
            // ARCHIVE-{AccessType} location
            else if (policyType.equals(this.getArchiveLocationType(policy.getAccessType()).toString()))
            {
                result.put(LocationPolicyType.ARCHIVE.toString(), locationPolicy.getBasePath());
            }
        }
        return result;
    }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getOnlineDataFilePath(gov.nasa.horizon.inventory.model.Product)
    */
   @Override
   public String getOnlineDataFilePath(Product product) {
      String result = null;
      try {
         List<ProductArchive> productArchiveSet = service.getProductArchives(product.getId());
   
         for (ProductArchive archive : productArchiveSet) {
            if (archive.getType().equals(ProductArchiveType.DATA.toString()) && archive.getStatus().equals(ProductArchiveStatus.ONLINE.toString()))
               result = StringUtility.cleanPaths(product.getRootPath(), product.getRelPath(), archive.getName());
         }
      }
      catch(InventoryException e) {
         log.error("Stacktrace: ", e);
      }
      return result;
   }

   /* (non-Javadoc)
    * @see gov.nasa.horizon.archive.external.wsm.Test#getReferenceAIPByProductType(java.lang.Long)
    */
   @Override
   public List<AIP> getReferenceAIPByProductType(Long productTypeId) {
      List<AIP> result = new ArrayList<AIP>();
      try {
         List<ProductReference> refList = service.getProductAllReferences(productTypeId);
   
         if(refList != null) {
            for (ProductReference ref : refList) {
               try {
                  result.add(new AIP(ref.getProductId(), new URI(ref.getPath()), ref.getType(), ref.getStatus()));
               } catch (URISyntaxException urise) {
      
                  log.error("getReferenceAIPByProductType: error getting archive info " + urise.getMessage());
                  log.debug("error:", urise);
               }
            }
         }
      }
      catch(InventoryException e) {
         log.error("Stacktrace:", e);
      }
      return result;
   }

   //TODO Find if these methods are needed
   /* 
    public List<AIP> getReferenceAIPByProduct (List<Integer> idList)
    {
        List<AIP> result = new ArrayList<AIP>();
        List<Product> products = this.fetchProducts (idList);
        
        try
        {
            for (Product g : products)
            {
                for (ProductReference ref : g.getProductReferenceSet())
                {
                    result.add (new AIP(g.getProductId(), new URI(ref.getPath()),
                               ref.getType(), ref.getStatus()));      
                }
            }
         }
        catch (URISyntaxException urise)
        {
            this.log.error ("getReferenceAIPByProduct: error getting reference info "
                            + urise.getMessage());
        }

        return result;
    }

    public Set<ProductReference> getReferenceSet (Integer id)
    {
        List<Integer> ids = new ArrayList<Integer>(1);
        return this.fetchProducts (ids).get (0).getProductReferenceSet();
    }

    private Map<String, String> getRemoteBaseLocation(ProductType productType)
    {
        Map<String, String> remoteBaseLocation = new TreeMap<String, String>();
        Set<ProductTypeLocationPolicy> locationPolicySet = productType.getLocationPolicySet();          

        for (ProductTypeLocationPolicy locationPolicy : locationPolicySet)
        {
            String policyType = locationPolicy.getType();

            if (policyType.startsWith(LocationPolicyType.REMOTE.toString())) 
                remoteBaseLocation.put(policyType, locationPolicy.getBasePath());
        }
  
        return remoteBaseLocation;
    }
    */

    public Map<Long, ArchiveMetadata> getRollingStore ()
    {
        List<Long> ids = new ArrayList<Long>();
        Map<Long, ArchiveMetadata> result = new HashMap<Long, ArchiveMetadata>(ids.size());
        
        for (Long id : ids) result.put (id, this.getRollingStore (id)); 
        return result;
    }

   public ArchiveMetadata getRollingStore(Long productTypeId) {
      try {
         ProductType pt = service.getProductType(productTypeId);
         if (pt == null) {
            return new ArchiveMetadata();
         }
         return this.getProductTypeMetadata(pt);
      } catch (InventoryException e) {
         return new ArchiveMetadata();
      }
   }

    public Map<Long, ArchiveMetadata> getRollingStore (List<Long> productIdList)
    {
        ArchiveMetadata metadata = null;            
        Long productTypeId;
        List<Product> products = this.fetchProducts (productIdList);
        Map<Long, ArchiveMetadata> result = new HashMap<Long, ArchiveMetadata>(productIdList.size());
        Map<Long, ArchiveMetadata> productTypeCache = new TreeMap<Long,ArchiveMetadata>();

        for (Product g : products)
        {
            metadata = null;
            productTypeId = g.getPtId();
            if (productTypeCache.containsKey (productTypeId)) metadata = productTypeCache.get(productTypeId);         
            else
            {
                metadata = this.getProductTypeMetadata (g.getPtId());
                if (metadata != null) productTypeCache.put(productTypeId, metadata);
            }
            if (metadata != null) result.put (g.getId(), metadata);
        }

        return result;
    }

    public List<ProductTypeLocationPolicy> getProductTypeLocationPolicies(Long ptId) {
       List<ProductTypeLocationPolicy> result = null;
       try {
          result = service.getProductTypeLocationPolicies(ptId);
       }
       catch(InventoryException e) {
          log.error("Stacktrace: ", e);
       }
       return result;
    }
    
    public String getProductTypeAccessType(Long ptId) {
       String result = null;
       try {
          ProductTypePolicy policy = service.getProductTypePolicy(ptId);
          result = policy.getAccessType();
       }
       catch(InventoryException e) {
          log.error("Couldnt find Product Type "+ptId+" when trying to retrieve access type", e);
       }
       
       return result;
    }
    
    private String toString (String id, List<Long> ids)
    {
        StringBuilder list = new StringBuilder();
        
        for (Long i : ids)
        {
            list.append (id);
            list.append (i.toString());
            list.append ("&");
        }
        list.setLength (list.length() - 1);

        return list.toString();
    }
}
