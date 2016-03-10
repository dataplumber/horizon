/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import java.util.Date;
import java.util.List;

public interface SPHeader extends Accessor {

   Long getProductTypeId();

   void setProductTypeId(long id);

   String getProductType();

   void setProductType(String productType);

   Long getProductTypeInventoryId();

   void setProductTypeInventoryId(long id);

   Long getProductId();

   void setProductId(long productId);

   String getProductName();

   void setProductName(String productName);

   Long getProductInventoryId();

   void setProductInventoryId(long id);

   String getOfficialName();

   void setOfficialName(String officialName);

   String getVersion();

   void setVersion(String version);

   Date getCreateTime();

   void setCreateTime(Date createTime);

   void setCreateTime(long createTime);

   Long getSubmissionId();

   void setSubmissionId(long submissionId);

   String getStatus();

   void setStatus(SPCommon.SPSubmissionStatus status);

   void setStatus(String status);

   String getReplace();

   void setReplace(String productName);

   boolean isCatalogOnly();

   void setCatalogOnly(boolean flag);

   SPOperation createOperation();

   List<SPOperation> getOperations();

   void addOperation(SPOperation operation);

}
