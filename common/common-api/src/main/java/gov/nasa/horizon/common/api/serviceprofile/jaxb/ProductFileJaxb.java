package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.File;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.FileClass;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.ProductFile;
import gov.nasa.horizon.common.api.serviceprofile.SPCommon;
import gov.nasa.horizon.common.api.serviceprofile.SPFile;
import gov.nasa.horizon.common.api.serviceprofile.SPProductFile;

/**
 * Wrapper class for ProductFile jaxb
 *
 * @author T. Huang
 * @version $Id: $
 */
public class ProductFileJaxb extends AccessorBase implements SPProductFile {

   private ProductFile _jaxbObj;

   public ProductFileJaxb() {
      this._jaxbObj = new ProductFile();
   }

   public ProductFileJaxb(ProductFile jaxb) {
      this._jaxbObj = jaxb;
   }

   public ProductFileJaxb(SPProductFile productFile) {
      if (productFile.getImplObj() instanceof ProductFile) {
         this._jaxbObj = (ProductFile) productFile.getImplObj();
      } else {
         this.setFile(productFile.getFile());
         this.setFileType(productFile.getFileType());
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
      final ProductFileJaxb other = (ProductFileJaxb) obj;
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
   public SPFile createFile() {
      return new FileJaxb();
   }

   @Override
   public void setFile(SPFile file) {
      SPFile f = file;
      if (!(f.getImplObj() instanceof File)) {
         f = new FileJaxb(f);
      }
      this._jaxbObj.setFile((File) f.getImplObj());
   }

   @Override
   public SPFile getFile() {
      SPFile result = null;
      if (this._jaxbObj.getFile() != null) {
         result = new FileJaxb(this._jaxbObj.getFile());
      }
      return result;
   }

   @Override
   public void setFileType(SPCommon.SPFileClass fileType) {
      this._jaxbObj.setType(FileClass.fromValue(fileType.toString()));
   }

   @Override
   public SPCommon.SPFileClass getFileType() {
      if (this._jaxbObj.getType() != null)
         return SPCommon.SPFileClass.valueOf(this._jaxbObj.getType().value());
      return null;
   }
}
