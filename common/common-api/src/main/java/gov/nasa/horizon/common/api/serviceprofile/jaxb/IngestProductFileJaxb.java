package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.FileDestination;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.IngestProductFiles.IngestProductFile;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.ProductFile;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.TimeStamp;
import gov.nasa.horizon.common.api.serviceprofile.SPFileDestination;
import gov.nasa.horizon.common.api.serviceprofile.SPIngestProductFile;
import gov.nasa.horizon.common.api.serviceprofile.SPProductFile;

import java.math.BigInteger;
import java.util.Date;

/**
 * Wrapper class to Ingest Product File Jaxb
 *
 * @author T. Huang
 * @version $Id: $
 */
public class IngestProductFileJaxb extends AccessorBase implements
      SPIngestProductFile {

   private IngestProductFile _jaxbObj;

   public IngestProductFileJaxb() {
      this._jaxbObj = new IngestProductFile();
   }

   public IngestProductFileJaxb(IngestProductFile jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public IngestProductFileJaxb(SPIngestProductFile ingestProductFile) {
      this.setFileDestination(ingestProductFile.getFileDestination());
      this.setProductFile(ingestProductFile.getProductFile());
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
      final IngestProductFileJaxb other = (IngestProductFileJaxb) obj;
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
   public SPFileDestination createFileDestination() {
      FileDestinationJaxb fd = new FileDestinationJaxb();
      fd.setOwner(this);
      return fd;
   }

   @Override
   public SPFileDestination getFileDestination() {
      if (this._jaxbObj.getDestination() != null)
         return new FileDestinationJaxb(this._jaxbObj.getDestination());
      return null;
   }

   @Override
   public void setFileDestination(SPFileDestination fileDestination) {
      if (fileDestination == null) return;

      SPFileDestination fd = fileDestination;
      if (!(fd.getImplObj() instanceof FileDestination)) {
         fd = new FileDestinationJaxb(fd);
      }
      this._jaxbObj.setDestination((FileDestination) fd.getImplObj());
   }

   @Override
   public SPProductFile createProductFile() {
      ProductFileJaxb pf = new ProductFileJaxb();
      pf.setOwner(this);
      return pf;
   }

   @Override
   public SPProductFile getProductFile() {
      if (this._jaxbObj.getProductFile() != null) {
         return new ProductFileJaxb(this._jaxbObj.getProductFile());
      }
      return null;
   }

   @Override
   public void setProductFile(SPProductFile productFile) {
      if (productFile == null) return;

      if (productFile.getImplObj() instanceof ProductFile) {
         this._jaxbObj.setProductFile((ProductFile) productFile.getImplObj());
      } else {
         ProductFileJaxb pf = new ProductFileJaxb(productFile);
         this._jaxbObj.setProductFile((ProductFile) pf.getImplObj());
      }
   }

   private TimeStamp _getIngestTime() {
      TimeStamp ingestTime = this._jaxbObj.getIngestTime();
      if (ingestTime == null) {
         ingestTime = new TimeStamp();
         this._jaxbObj.setIngestTime(ingestTime);
      }
      return ingestTime;
   }

   @Override
   public Date getIngestStartTime() {
      Date result = null;

      if (this._jaxbObj.getIngestTime() != null &&
            this._jaxbObj.getIngestTime().getStart() != null) {
         result = new Date(this._jaxbObj.getIngestTime().getStart().longValue());
      }
      return result;
   }

   @Override
   public void setIngestStartTime(long startTime) {
      this._getIngestTime().setStart(BigInteger.valueOf(startTime));
   }

   @Override
   public void setIngestStartTime(Date startTime) {
      this.setIngestStartTime(startTime.getTime());
   }

   @Override
   public Date getIngestStopTime() {
      Date result = null;

      if (this._jaxbObj.getIngestTime() != null &&
            this._jaxbObj.getIngestTime().getStop() != null) {
         result = new Date(this._jaxbObj.getIngestTime().getStop().longValue());
      }
      return result;
   }

   @Override
   public void setIngestStopTime(long stopTime) {
      this._getIngestTime().setStop(BigInteger.valueOf(stopTime));
   }

   @Override
   public void setIngestStopTime(Date stopTime) {
      this.setIngestStopTime(stopTime.getTime());
   }
}
