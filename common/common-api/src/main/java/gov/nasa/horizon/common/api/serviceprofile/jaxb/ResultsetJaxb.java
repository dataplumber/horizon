package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Resultset;
import gov.nasa.horizon.common.api.serviceprofile.SPResultProduct;
import gov.nasa.horizon.common.api.serviceprofile.SPResultset;

import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 8/9/13
 * Time: 3:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResultsetJaxb extends AccessorBase implements SPResultset {

   private Resultset _jaxbObj;

   public ResultsetJaxb() {
      this._jaxbObj = new Resultset();
   }

   public ResultsetJaxb (Resultset jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public ResultsetJaxb (SPResultset resultset) {
      this._jaxbObj = new Resultset();
      this.setPageIndex(resultset.getPageIndex());
      this.setTotalPages(resultset.getTotalPages());
      for (SPResultProduct resultproduct: resultset.getResultProducts()) {
         this.addResultProduct(resultproduct);
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
      final ResultsetJaxb other = (ResultsetJaxb) obj;
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
   public Integer getPageIndex() {
      if (this._jaxbObj.getPageIndex() != null)
         return this._jaxbObj.getPageIndex().intValue();
      return null;
   }

   @Override
   public void setPageIndex(int pageIndex) {
      this._jaxbObj.setPageIndex(BigInteger.valueOf(pageIndex));
   }

   @Override
   public Integer getTotalPages() {
      if (this._jaxbObj.getTotalPages() != null) {
         return this.getTotalPages().intValue();
      }
      return null;
   }

   @Override
   public void setTotalPages(int totalPages) {
      this._jaxbObj.setTotalPages(BigInteger.valueOf(totalPages));
   }

   protected List<Resultset.ResultProduct> _getResultProducts() {
      return this._jaxbObj.getResultProduct();
   }

   @Override
   public List<SPResultProduct> getResultProducts() {
      List<SPResultProduct> result = new Vector<SPResultProduct>();
      for (Resultset.ResultProduct rp : this._getResultProducts()) {
         result.add(new ResultProductJaxb(rp));
      }
      return result;
   }

   @Override
   public SPResultProduct createResultProduct() {
      return new ResultProductJaxb();
   }

   @Override
   public void addResultProduct(SPResultProduct resultproduct) {
      SPResultProduct rp = resultproduct;
      if (!(rp.getImplObj() instanceof Resultset.ResultProduct)) {
         rp = new ResultProductJaxb(rp);
      }
      this._getResultProducts().add((Resultset.ResultProduct)rp.getImplObj());
   }

   @Override
   public void clearResultProducts() {
      this._getResultProducts().clear();
   }
}
