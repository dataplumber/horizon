package gov.nasa.horizon.common.api.serviceprofile;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 7/21/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SPSourceProduct extends Accessor {
   enum SPMetadateaRepo {
      ECHO_REST, ECHO_OPENSEARCH, PODAAC_OPENSEARCH
   }

   String getProductType();

   void setProductType(String productType);

   String getProduct();

   void setProduct(String product);

   String getMetadataRepo();

   void setMetadataRepo(SPMetadateaRepo repo);
}
