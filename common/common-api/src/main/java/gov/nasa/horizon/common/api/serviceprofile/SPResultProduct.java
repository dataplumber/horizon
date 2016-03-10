package gov.nasa.horizon.common.api.serviceprofile;

import java.util.List;

/**
 * Wrapper class for resultset product file class
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface SPResultProduct extends Accessor {

   Integer getProductTypeId();

   void setProductTypeId(int productTypeId);

   String getProductType();

   void setProductType(String productType);

   Integer getProductId();

   void setProductId(int productId);

   String getProductName();

   void setProductName(String productName);

   List<SPProductFile> getProductFiles();

   void clearProductFiles();

   SPProductFile createProductFile();

   void addProductFile(SPProductFile productFile);

}
