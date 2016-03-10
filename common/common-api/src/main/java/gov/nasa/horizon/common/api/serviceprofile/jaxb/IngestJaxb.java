/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.FileDestination;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Ingest;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.IngestProductFiles;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.IngestProductFiles.IngestProductFile;
import gov.nasa.horizon.common.api.serviceprofile.SPFileDestination;
import gov.nasa.horizon.common.api.serviceprofile.SPIngest;
import gov.nasa.horizon.common.api.serviceprofile.SPIngestProductFile;

import java.util.List;
import java.util.Vector;

public class IngestJaxb extends AccessorBase implements SPIngest {

   private Ingest _jaxbObj;

   public IngestJaxb() {
      this._jaxbObj = new Ingest();
   }

   public IngestJaxb(Ingest jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public IngestJaxb(SPIngest ingest) {
      this.setOperationNote(ingest.getOperationNote());
      this.setOperationSuccess(ingest.isOperationSuccess());
      for (SPFileDestination fd : ingest.getDeletes()) {
         this.addDelete(fd);
      }
      for (SPIngestProductFile ipf : ingest.getIngestProductFiles()) {
         this.addIngestProductFile(ipf);
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
      final IngestJaxb other = (IngestJaxb) obj;
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
   public SPFileDestination createDestination() {
      return new FileDestinationJaxb();
   }

   protected List<FileDestination> _getDeletes() {
      if (this._jaxbObj.getDeletes() == null) {
         this._jaxbObj.setDeletes(new Ingest.Deletes());
      }
      return this._jaxbObj.getDeletes().getDelete();
   }

   @Override
   public void clearDeletes() {
      this._getDeletes().clear();
   }

   @Override
   public void addDelete(SPFileDestination delete) {
      SPFileDestination d = delete;
      if (!(d.getImplObj() instanceof FileDestination)) {
         d = new FileDestinationJaxb(d);
      }
      this._getDeletes().add((FileDestination) d.getImplObj());
   }

   @Override
   public List<SPFileDestination> getDeletes() {
      List<SPFileDestination> result = new Vector<SPFileDestination>();
      for (FileDestination fd : this._getDeletes()) {
         result.add(new FileDestinationJaxb(fd));
      }
      return result;
   }

   @Override
   public String getOperationNote() {
      return this._jaxbObj.getOperationNote();
   }

   @Override
   public void setOperationNote(String operationNote) {
      this._jaxbObj.setOperationNote(operationNote);
   }

   @Override
   public SPIngestProductFile createIngestProductFile() {
      IngestProductFileJaxb ipf = new IngestProductFileJaxb();
      ipf.setOwner(this);
      return ipf;
   }

   protected List<IngestProductFile> _getIngestProductFiles
         () {
      if (this._jaxbObj.getProductFiles() == null) {
         this._jaxbObj.setProductFiles(new IngestProductFiles());
      }
      return this._jaxbObj.getProductFiles().getIngestProductFile();
   }

   @Override
   public void clearIngestProductFiles() {
      this._getIngestProductFiles().clear();
   }

   @Override
   public void addIngestProductFile(SPIngestProductFile ingestProductFile) {
      SPIngestProductFile ipf = ingestProductFile;
      if (!(ipf.getImplObj() instanceof IngestProductFile)) {
         ipf = new IngestProductFileJaxb(ipf);
      }
      this._getIngestProductFiles().add((IngestProductFile) ipf.getImplObj());
   }

   @Override
   public List<SPIngestProductFile> getIngestProductFiles() {
      List<SPIngestProductFile> result = new Vector<SPIngestProductFile>();
      for (IngestProductFile ipf : this._getIngestProductFiles()) {
         result.add(new IngestProductFileJaxb(ipf));
      }
      return result;
   }

   @Override
   public Boolean isOperationSuccess() {
      return this._jaxbObj.isOperationSuccess();
   }

   @Override
   public void setOperationSuccess(Boolean flag) {
      this._jaxbObj.setOperationSuccess(flag);
   }
}
