package gov.nasa.horizon.archive.external;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.inventory.api.Constant.ProductArchiveType;
import gov.nasa.horizon.inventory.model.ProductType;
import gov.nasa.horizon.inventory.model.Product;

import java.util.List;
import java.util.Map;

public interface InventoryAccess
{
   /*
    public void deleteProduct(Product product, boolean dataOnly);
    public void remoteProduct (Product product);
    public void setProductReference(Product product, Map<String, String> baseLocationPolicy, String subpath);
    public void updateAIPArchiveStatus (List<AIP> aipList);
    public void updateAIPProductArchiveStatus (List<AIP> aipList);
    public void updateAIPProductReferenceStatus (List<AIP> aipList);
    public void updateProductArchiveChecksum(Integer productId,String name, String Sum);
    public void updateProductArchiveSize (Integer productId,String name, Long Size);
    public void updateProductArchiveStatus (Integer productId, String status);
    public void updateProductLocation (Product product, String archiveBaseLocation, String baseLocation);
    public boolean updateLocalReference(Product product, Map<String, String> localBaseLocationPolicy);
    public void updateVerifyProductStatus (List<Integer> productIdList, String status);
	public void updateProductInfo(Product g);
	public void updateProductReferencePath(Integer productId, String path,
			String newRef);
	public void reassociateProductElement(Integer key, Integer deId, Integer deId2, String type);
	public void reElement(Product g, ProductType toD, ProductType fromD);
	*/
   public boolean delete(Product product);

   public void deleteProduct(Product product, boolean dataOnly);

   public void remoteProduct(Product product);
   
   public void updateProductInfo(Product product);
   
   public void updateAIPArchiveStatus (List<AIP> aipList);

   public void updateAIPProductArchiveStatus (List<AIP> aipList);
   
   public void updateAIPProductArchiveStatus (AIP aip);

   public void updateProductArchiveChecksum(Long productId, String name, String sum);

   public void updateProductArchiveSize(Long productId, String name, Long Size);

   public void updateProductArchiveStatus(Long productId, String status);

   public void updateProductLocation(Product product, String archiveBaseLocation, String baseLocation);

   public void setProductArchiveStatus(Product product, String status);

   public void updateVerifyProductStatus(List<Long> productIdList, String status);
}
