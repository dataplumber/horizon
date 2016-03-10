/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import java.util.Date;
import java.util.List;

/**
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: GranuleHistory.java 1239 2008-05-30 07:04:36Z thuang $
 */
public interface SPProductHistory extends Accessor {

   String getVersion();

   void setVersion(String version);

   Date getCreateDate();

   void setCreateDate(Date date);

   void setCreateDate(long date);

   Date getLastRevisionDate();

   void setLastRevisionDate(Date date);

   void setLastRevisionDate(long date);

   String getRevisionHistory();

   void setRevisionHistory(String history);

   SPSourceProduct createSourceProduct();

   void clearSourceProducts();

   void addSourceProduct(SPSourceProduct sourceProduct);

   List<SPSourceProduct> getSourceProducts();
}
