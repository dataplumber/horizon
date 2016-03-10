/*
 * Created on Jun 11, 2010
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.nasa.horizon.common.crawler.spider.provider;

import gov.nasa.horizon.common.api.file.FileProduct;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProductSet implements ProcessFileProduct {

   private static Log _logger = LogFactory.getLog(ProductSet.class);

   private Set<FileProduct> theList;
   
   ProductSet () {
      this.theList =  Collections.synchronizedSet(new HashSet<FileProduct>());
      _logger.trace ("ProductSet created");
   }
   
   ProductSet (int expected) {
      this.theList =  Collections.synchronizedSet(new HashSet<FileProduct>(expected));
      _logger.trace ("ProductSet created");
   }
   
   public synchronized void clear() {
      this.theList.clear();
   }
   
   public synchronized void process(FileProduct fp) {
      synchronized (this.theList) { 
         this.theList.add (fp); 
         _logger.trace ("Adding " + fp.getName() + " to the list");
      }
   }

   public Set<FileProduct> getShallowCopy() {
      Set<FileProduct> result =  Collections.synchronizedSet(new HashSet<FileProduct>());
      
      synchronized (this.theList) { 
         result.addAll (this.theList); 
      }
      return result;
   }
}
