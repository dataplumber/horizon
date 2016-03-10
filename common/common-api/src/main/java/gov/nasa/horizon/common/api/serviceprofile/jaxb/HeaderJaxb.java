/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Header;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Operation;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.SubmitStatusValue;
import gov.nasa.horizon.common.api.serviceprofile.SPCommon;
import gov.nasa.horizon.common.api.serviceprofile.SPHeader;
import gov.nasa.horizon.common.api.serviceprofile.SPOperation;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class HeaderJaxb extends AccessorBase implements
      SPHeader {

   private Header _jaxbObj;

   public HeaderJaxb() {
      this._jaxbObj = new Header();
   }

   public HeaderJaxb(SPHeader header) {
      this.setCatalogOnly(header.isCatalogOnly());
      this.setCreateTime(header.getCreateTime());
      this.setOfficialName(header.getOfficialName());
      this.setProductId(header.getProductId());
      this.setProductName(header.getProductName());
      this.setProductType(header.getProductType());
      this.setProductTypeId(header.getProductTypeId());
      this.setReplace(header.getReplace());
      this.setStatus(header.getStatus());
      this.setSubmissionId(header.getSubmissionId());
      this.setVersion(header.getVersion());

      for (SPOperation operation : header.getOperations()) {
         this.addOperation(operation);
      }

   }

   public HeaderJaxb(Header header) {
      this._jaxbObj = header;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final HeaderJaxb other = (HeaderJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }


   public Long getProductTypeId() {
      return this._jaxbObj.getProductTypeId();
   }

   public void setProductTypeId(long id) {
      this._jaxbObj.setProductTypeId(id);
   }

   public String getProductType() {
      return this._jaxbObj.getProductType();
   }

   public void setProductType(String productType) {
      this._jaxbObj.setProductType(productType);
   }

   @Override
   public Long getProductTypeInventoryId() {
      return this._jaxbObj.getProductTypeInventoryId();
   }

   @Override
   public void setProductTypeInventoryId(long id) {
      this._jaxbObj.setProductTypeInventoryId(id);
   }

   public Long getProductId() {
      return this._jaxbObj.getProductId();
   }

   public void setProductId(long productId) {
      this._jaxbObj.setProductId(productId);
   }

   public String getProductName() {
      return this._jaxbObj.getProductName();
   }

   public void setProductName(String productName) {
      this._jaxbObj.setProductName(productName);
   }

   @Override
   public Long getProductInventoryId() {
      return this._jaxbObj.getProductInventoryId();
   }

   @Override
   public void setProductInventoryId(long id) {
      this._jaxbObj.setProductInventoryId(id);
   }

   public String getOfficialName() {
      return this._jaxbObj.getOfficialName();
   }

   public void setOfficialName(String officialName) {
      this._jaxbObj.setOfficialName(officialName);
   }

   public String getVersion() {
      return this._jaxbObj.getVersion();
   }

   public void setVersion(String version) {
      this._jaxbObj.setVersion(version);
   }

   public Date getCreateTime() {
      return new Date(this._jaxbObj.getCreateTime().longValue());
   }

   public void setCreateTime(Date createTime) {
      this.setCreateTime(createTime.getTime());
   }

   public void setCreateTime(long createTime) {
      this._jaxbObj.setCreateTime(BigInteger.valueOf(createTime));
   }

   public Long getSubmissionId() {
      return this._jaxbObj.getSubmissionId();
   }

   public void setSubmissionId(long submissionId) {
      this._jaxbObj.setSubmissionId(submissionId);
   }

   public String getStatus() {
      return this._jaxbObj.getStatus().toString();
   }

   public void setStatus(SPCommon.SPSubmissionStatus status) {
      this.setStatus(status.toString());
   }

   public void setStatus(String status) {
      this._jaxbObj.setStatus(SubmitStatusValue.valueOf(status.toUpperCase()));
   }

   public String getReplace() {
      return this._jaxbObj.getReplace();
   }

   public void setReplace(String productName) {
      this._jaxbObj.setReplace(productName);
   }

   public boolean isCatalogOnly() {
      return this._jaxbObj.isCatalogOnly();
   }

   public void setCatalogOnly(boolean flag) {
      this._jaxbObj.setCatalogOnly(flag);
   }

   protected List<Operation> _getOperations
         () {
      if (this._jaxbObj.getOperations() == null) {
         this._jaxbObj.setOperations(new gov.nasa.horizon.common.api
               .jaxb.serviceprofile.Header.Operations());
      }
      return this._jaxbObj.getOperations().getOperation();

   }

   public SPOperation createOperation() {
      return new OperationJaxb();
   }

   public synchronized List<SPOperation>
   getOperations() {
      Vector<SPOperation> result =
            new Vector<SPOperation>();
      if (this._jaxbObj.getOperations() != null) {
         List<Operation> operations = this._jaxbObj.getOperations()
               .getOperation
                     ();
         for (Operation operation : operations) {
            SPOperation temp = new
                  OperationJaxb
                  (operation);
            temp.setOwner(this);
            result.add(temp);
         }
      }
      return result;
   }

   public void addOperation(SPOperation operation) {
      if (!(operation.getImplObj() instanceof Operation)) {
         operation = new OperationJaxb(operation);
      }
      this._getOperations().add((Operation) operation.getImplObj());
      operation.setOwner(this);
   }

}
