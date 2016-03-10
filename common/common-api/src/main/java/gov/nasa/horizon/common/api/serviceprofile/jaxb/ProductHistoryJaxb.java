/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.ProductHistory;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.SourceProduct;
import gov.nasa.horizon.common.api.serviceprofile.SPProductHistory;
import gov.nasa.horizon.common.api.serviceprofile.SPSourceProduct;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * @author T. Huang
 * @version $Id: $
 */
public class ProductHistoryJaxb extends AccessorBase implements
      SPProductHistory {

   private ProductHistory _jaxbObj;

   public ProductHistoryJaxb() {
      this._jaxbObj = new ProductHistory();
   }

   public ProductHistoryJaxb(SPProductHistory history) {
      this._jaxbObj = new ProductHistory();
      this.setVersion(history.getVersion());
      this.setCreateDate(history.getCreateDate());
      this.setLastRevisionDate(history.getLastRevisionDate());
      this.setRevisionHistory(history.getRevisionHistory());
   }

   public ProductHistoryJaxb(ProductHistory jaxbObj) {
      this._jaxbObj = jaxbObj;
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
      final ProductHistoryJaxb other = (ProductHistoryJaxb) obj;
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
   public String getVersion() {
      return this._jaxbObj.getVersion();
   }

   @Override
   public void setVersion(String version) {
      this._jaxbObj.setVersion(version);
   }

   @Override
   public Date getCreateDate() {
      return new Date(this._jaxbObj.getCreateDate().longValue());
   }

   @Override
   public void setCreateDate(Date date) {
      if (date == null) return;
      this._jaxbObj.setCreateDate(BigInteger.valueOf(date.getTime()));
   }

   @Override
   public void setCreateDate(long date) {
      this._jaxbObj.setCreateDate(BigInteger.valueOf(date));
   }

   @Override
   public Date getLastRevisionDate() {
      return new Date(this._jaxbObj.getLastRevisionDate().longValue());
   }

   @Override
   public void setLastRevisionDate(Date date) {
      if (date == null) return;
      this._jaxbObj.setLastRevisionDate(BigInteger.valueOf(date.getTime()));
   }

   @Override
   public void setLastRevisionDate(long date) {
      this._jaxbObj.setLastRevisionDate(BigInteger.valueOf(date));
   }

   @Override
   public String getRevisionHistory() {
      return this._jaxbObj.getRevisionHistory();
   }

   @Override
   public void setRevisionHistory(String history) {
      this._jaxbObj.setRevisionHistory(history);
   }

   @Override
   public SPSourceProduct createSourceProduct() {
      return new SourceProductJaxb();
   }

   protected List<SourceProduct> _getSourceProducts() {
      if (this._jaxbObj.getSources() == null) {
         this._jaxbObj.setSources(new ProductHistory.Sources());
      }
      return this._jaxbObj.getSources().getSource();
   }

   @Override
   public void clearSourceProducts() {
      this._getSourceProducts().clear();
   }

   @Override
   public void addSourceProduct(SPSourceProduct sourceProduct) {
      SPSourceProduct sp = sourceProduct;
      if (!(sp.getImplObj() instanceof SourceProduct)) {
         sp = new SourceProductJaxb(sp);
      }
      this._getSourceProducts().add((SourceProduct) sp.getImplObj());
   }

   @Override
   public List<SPSourceProduct> getSourceProducts() {
      List<SPSourceProduct> result = new Vector<SPSourceProduct>();
      for (SourceProduct sourceProduct : this._getSourceProducts()) {
         result.add(new SourceProductJaxb(sourceProduct));
      }
      return result;
   }
}
