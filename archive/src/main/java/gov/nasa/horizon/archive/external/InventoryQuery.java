package gov.nasa.horizon.archive.external;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.inventory.api.InventoryException;
import gov.nasa.horizon.inventory.model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public interface InventoryQuery
{
   /*
    public ProductType fetchProductType (Integer id, String...properties);//done
    public List<String> findProductTypeByProductId (int id); //done
    public List<Integer> findProductList (List<Integer> idList); //list of product Ids, added to Inventory Service and working
    
    public List<AIP> getArchiveAIPByProductType (Integer productTypeId); //in inventory service, returns maps
    public List<AIP> getArchiveAIPByProduct(List<Integer> idList);//return maps
    
    public String getArchiveBaseLocation (Integer productTypeId);
    public String getArchiveBaseLocation (String productTypeName);
    public Map<Integer, String> getArchiveBaseLocation (List<Integer> granuleIdList);
    public ArchiveMetadata getProductTypeMetadata(Integer productTypeId);
    
    public Map<Integer, ArchiveMetadata> getProductTypeMetadata(List<Integer> granuleIdList);
    public List<Integer> getProductIdList(Integer productTypeId);
    public List<Integer> getProductIdList(Integer productTypeId, Calendar start, Calendar stop);
    
    public ArchiveMetadata getProductMetadata(Integer granuleId);
    public Integer getProductSizeByProductType(Integer productTypeId);
    
    public Integer getProductVersion (Integer id);
    public String getOnlineDataFilePath(Product granule);
    
    public List<AIP> getReferenceAIPByProductType (Integer productTypeId);
    public List<AIP> getReferenceAIPByProduct(List<Integer> idList);
    
    //public Set<ProductReference> getReferenceSet(Integer id);
    public Map<Integer, ArchiveMetadata> getRollingStore();
    
    public ArchiveMetadata getRollingStore(Integer productTypeId);
    public Map<Integer, ArchiveMetadata> getRollingStore(List<Integer> granuleIdList);
   
    
    public Product fetchProduct(Integer id);
    public ProductType fetchProductTypeByPersistentId(String perId);//done
    public List<Product> locateProducts(Integer Id, String pattern, Long startTime, Long stopTime);
	public List<Integer> fetchProductsByProductTypeAndPatter(Integer productTypeId, String gnp);
	*/
   public Product fetchProduct(Long id);

   public Product fetchProduct(Long ptId, String productName);
   
   public ProductType fetchProductTypeByPersistentId(String perId);

   public ProductType fetchProductType(Long ptId);

   public List<ProductArchive> getProductArchives(Long productId);
   
   public List<AIP> getArchiveAIPByProductType (Long productTypeId);
   
   public List<AIP> getArchiveAIPByProduct(List<Long> idList);
   
   public String getArchiveBaseLocation(Long productTypeId);

   public String getArchiveBaseLocation(String productTypeName);
   
   public Map<Long, String> getArchiveBaseLocation (List<Long> productIdList);

   public ArchiveMetadata getProductTypeMetadata(Long productTypeId);
   
   public Map<Long, ArchiveMetadata> getProductTypeMetadata (List<Long> productIdList);

   public List<Long> getProductIdList(Long productTypeId);

   public List<Long> getProductIdList(Long productTypeId, Calendar start, Calendar stop);

   public ArchiveMetadata getProductMetadata(Long productId);

   public Integer getProductSizeByProductType(Long ptId);
   
   public Integer getProductVersion(Long id);

   public String getOnlineDataFilePath(Product product);

   public List<AIP> getReferenceAIPByProductType(Long productTypeId);
   
   public Map<Long, ArchiveMetadata> getRollingStore ();
   
   public ArchiveMetadata getRollingStore(Long productTypeId) ;

   public Map<Long, ArchiveMetadata> getRollingStore (List<Long> productIdList);
   
   public List<Product> locateProducts(Long Id, String pattern, Long startTime, Long stopTime) ;

   public List<ProductTypeLocationPolicy> getProductTypeLocationPolicies(Long ptId);
   
   public String getProductTypeAccessType(Long ptId);
}
