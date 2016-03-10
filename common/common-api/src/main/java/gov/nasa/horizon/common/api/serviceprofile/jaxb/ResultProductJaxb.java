package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.ProductFile;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.ProductFiles;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Resultset.ResultProduct;
import gov.nasa.horizon.common.api.serviceprofile.SPProductFile;
import gov.nasa.horizon.common.api.serviceprofile.SPResultProduct;

import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 7/31/13
 * Time: 12:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResultProductJaxb extends AccessorBase implements SPResultProduct {

   private ResultProduct _jaxbObj;

   public ResultProductJaxb() {
      this._jaxbObj = new ResultProduct();
   }

   public ResultProductJaxb(ResultProduct jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public ResultProductJaxb(SPResultProduct resultProduct) {
      this.setProductTypeId(resultProduct.getProductTypeId());
      this.setProductType(resultProduct.getProductType());
      this.setProductId(resultProduct.getProductId());
      this.setProductName(resultProduct.getProductName());
      for (SPProductFile productFile : resultProduct.getProductFiles()) {
         this.addProductFile(productFile);
      }
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
      final ResultProductJaxb other = (ResultProductJaxb) obj;
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
   public Integer getProductTypeId() {
      if (this._jaxbObj.getProductTypeId() != null) {
         return this._jaxbObj.getProductTypeId().intValue();
      }
      return null;
   }

   @Override
   public void setProductTypeId(int productTypeId) {
      this._jaxbObj.setProductTypeId(new Integer(productTypeId).longValue());
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
   public Integer getProductId() {
      if (this._jaxbObj.getProductId() != null) {
         return this._jaxbObj.getProductId().intValue();
      }
      return null;
   }

   @Override
   public void setProductId(int productId) {
      this._jaxbObj.setProductId(new Integer(productId).longValue());
   }

   @Override
   public String getProductName() {
      return this._jaxbObj.getProductName();
   }

   @Override
   public void setProductName(String productName) {
      this._jaxbObj.setProductName(productName);
   }

   protected List<ProductFile> _getProductFile() {
      if (this._jaxbObj.getProductFiles() == null) {
         this._jaxbObj.setProductFiles(new ProductFiles());
      }
      return this._jaxbObj.getProductFiles().getProductFile();
   }

   @Override
   public List<SPProductFile> getProductFiles() {
      List<SPProductFile> result = new Vector<SPProductFile>();
      for (ProductFile pf : this._getProductFile()) {
         result.add(new ProductFileJaxb(pf));
      }
      return result;
   }

   @Override
   public void clearProductFiles() {
      this._getProductFile().clear();
   }

   @Override
   public SPProductFile createProductFile() {
      ProductFileJaxb pf = new ProductFileJaxb();
      pf.setOwner(this);
      return pf;
   }

   @Override
   public void addProductFile(SPProductFile productFile) {
      if (productFile == null) return;

      SPProductFile pf = productFile;
      if (!(pf.getImplObj() instanceof ProductFile)) {
         pf = new ProductFileJaxb(pf);
      }
      this._getProductFile().add((ProductFile) pf.getImplObj());
   }
}
