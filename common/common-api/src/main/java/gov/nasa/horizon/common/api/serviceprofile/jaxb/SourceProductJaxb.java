package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.SourceProduct;
import gov.nasa.horizon.common.api.serviceprofile.SPSourceProduct;

/**
 * Wrapper class for SourceProduct binding
 *
 * @author T. Huang
 * @version $Id: $
 */
public class SourceProductJaxb extends AccessorBase implements SPSourceProduct {

   private SourceProduct _jaxbObj;

   public SourceProductJaxb() {
      this._jaxbObj = new SourceProduct();
   }

   public SourceProductJaxb(SourceProduct jaxb) {
      this._jaxbObj = jaxb;
   }

   public SourceProductJaxb(SPSourceProduct sourceProduct) {
      this._jaxbObj = new SourceProduct();
      this.setProductType(sourceProduct.getProductType());
      this.setProduct(sourceProduct.getProduct());
      this.setMetadataRepo(SPMetadateaRepo.valueOf(sourceProduct
            .getMetadataRepo()));
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final SourceProductJaxb other = (SourceProductJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   @Override
   public String getProductType() {
      return this._jaxbObj.getProductType();
   }

   @Override
   public void setProductType(String productType) {
      this._jaxbObj.setProductType(productType);
   }

   @Override
   public String getProduct() {
      return this._jaxbObj.getProduct();
   }

   @Override
   public void setProduct(String product) {
      this._jaxbObj.setProduct(product);
   }

   @Override
   public String getMetadataRepo() {
      return this._jaxbObj.getMetadataRepo();
   }

   @Override
   public void setMetadataRepo(SPMetadateaRepo repo) {
      this._jaxbObj.setMetadataRepo(repo.toString());
   }
}
