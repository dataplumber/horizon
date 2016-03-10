package gov.nasa.horizon.common.api.serviceprofile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 7/31/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SPResultset extends Accessor{

   Integer getPageIndex();

   void setPageIndex(int pageIndex);

   Integer getTotalPages();

   void setTotalPages(int totalPages);

   List<SPResultProduct> getResultProducts();

   SPResultProduct createResultProduct();

   void addResultProduct(SPResultProduct resultproduct);

   void clearResultProducts();
}
