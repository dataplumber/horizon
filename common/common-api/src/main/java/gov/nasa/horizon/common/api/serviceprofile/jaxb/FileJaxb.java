package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.ChecksumType;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.CompressionType;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.DataFormat;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.File;
import gov.nasa.horizon.common.api.serviceprofile.SPCommon;
import gov.nasa.horizon.common.api.serviceprofile.SPFile;

import java.math.BigInteger;
import java.util.List;

/**
 * Wrapper class for File JAXB implementation
 *
 * @author T. Huang
 * @version $Id: $
 */
public class FileJaxb extends AccessorBase implements SPFile {

   private File _jaxbObj;

   public FileJaxb() {
      this._jaxbObj = new File();
   }

   public FileJaxb(File jaxb) {
      this._jaxbObj = jaxb;
   }

   public FileJaxb(SPFile file) {
      if (file.getImplObj() instanceof File) {
         this._jaxbObj = (File) file.getImplObj();
      } else {
         this.setChecksumType(file.getChecksumType());
         this.setChecksumValue(file.getChecksumValue());
         this.setCompressionType(file.getCompressionType());
         this.setDataFormat(file.getDataFormat());
         this.setCompressionType(file.getCompressionType());
         this.setName(file.getName());
         this.setSize(file.getSize());
         for (String link : file.getLinks()) {
            this.addLink(link);
         }
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
      final FileJaxb other = (FileJaxb) obj;
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

   protected File.Checksum _getChecksum() {
      File.Checksum checksum = this._jaxbObj.getChecksum();
      if (checksum == null) {
         checksum = new File.Checksum();
         this._jaxbObj.setChecksum(checksum);
      }
      return checksum;
   }

   public SPCommon.SPChecksumAlgorithm getChecksumType() {
      SPCommon.SPChecksumAlgorithm result = null;
      File.Checksum cs = this._getChecksum();
      if (cs.getType() != null) {
         result = SPCommon.SPChecksumAlgorithm.valueOf(cs.getType().value());
      }
      return result;
   }

   @Override
   public void setChecksumType(SPCommon.SPChecksumAlgorithm checksumType) {
      File.Checksum cs = this._getChecksum();
      cs.setType(ChecksumType.fromValue(checksumType.toString()));
   }

   @Override
   public String getChecksumValue() {
      String result = null;
      File.Checksum cs = this._getChecksum();
      if (cs.getValue() != null) {
         result = cs.getValue();
      }
      return result;
   }

   @Override
   public void setChecksumValue(String checksumValue) {
      File.Checksum cs = this._getChecksum();
      cs.setValue(checksumValue);
   }


   public SPCommon.SPCompressionAlgorithm getCompressionType() {
      if (this._jaxbObj.getCompression() != null) {
         return SPCommon.SPCompressionAlgorithm.valueOf(this._jaxbObj
               .getCompression().value());
      }
      return null;
   }

   @Override
   public void setCompressionType(SPCommon.SPCompressionAlgorithm compressionType) {
      this._jaxbObj.setCompression(CompressionType.fromValue(compressionType
            .toString()));
   }

   @Override
   public SPCommon.SPDataFormat getDataFormat() {
      if (this._jaxbObj.getFormat() != null) {
         return SPCommon.SPDataFormat.valueOf(this._jaxbObj.getFormat().value
               ());
      }
      return null;
   }

   @Override
   public void setDataFormat(SPCommon.SPDataFormat dataFormat) {
      this._jaxbObj.setFormat(DataFormat.fromValue(dataFormat.toString()));
   }

   protected List<String> _getLinks() {
      if (this._jaxbObj.getLinks() == null) {
         this._jaxbObj.setLinks(new File.Links());
      }
      return this._jaxbObj.getLinks().getLink();
   }

   @Override
   public void clearLinks() {
      this._getLinks().clear();
   }

   @Override
   public void addLink(String link) {
      this._getLinks().add(link);
   }

   @Override
   public List<String> getLinks() {
      return this._getLinks();
   }

   @Override
   public String getName() {
      return this._jaxbObj.getName();
   }

   @Override
   public void setName(String name) {
      this._jaxbObj.setName(name);
   }

   @Override
   public Long getSize() {
      return this._jaxbObj.getSize().longValue();
   }

   @Override
   public void setSize(Long size) {
      this._jaxbObj.setSize(BigInteger.valueOf(size));
   }
}
