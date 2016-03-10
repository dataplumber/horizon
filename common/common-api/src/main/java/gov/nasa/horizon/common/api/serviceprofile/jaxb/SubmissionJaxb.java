/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.*;
import gov.nasa.horizon.common.api.serviceprofile.*;

import java.util.List;
import java.util.Vector;

public class SubmissionJaxb extends AccessorBase implements SPSubmission {

   private Submission _jaxbObj;

   public SubmissionJaxb() {
      this._jaxbObj = new Submission();
   }

   public SubmissionJaxb(Submission jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public SubmissionJaxb(SPSubmission submission) {
      this.setHeader(submission.getHeader());
      this.setMetadata(submission.getMetadata());
      this.setIngest(submission.getIngest());
      this.setArchive((submission.getArchive()));
      for (SPNotification n : submission.getNotifications()) {
         this.addNotification(n);
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
      final SubmissionJaxb other = (SubmissionJaxb) obj;
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
   public SPHeader createHeader() {
      HeaderJaxb header = new HeaderJaxb();
      header.setOwner(this);
      return header;
   }

   @Override
   public SPMetadata createMetadata() {
      MetadataJaxb metadata = new MetadataJaxb();
      metadata.setOwner(this);
      return metadata;
   }

   @Override
   public SPIngest createIngest() {
      IngestJaxb ingest = new IngestJaxb();
      ingest.setOwner(this);
      return ingest;
   }

   @Override
   public SPIngest createArchive() {
      return this.createIngest();
   }

   @Override
   public SPNotification createNotification() {
      SPNotification notification = new NotificationJaxb();
      notification.setOwner(this);
      return notification;
   }

   @Override
   public SPHeader getHeader() {
      if (this._jaxbObj.getHeader() != null) {
         return new HeaderJaxb(this._jaxbObj.getHeader());
      }
      return null;
   }

   @Override
   public SPMetadata getMetadata() {
      if (this._jaxbObj.getMetadata() != null) {
         return new MetadataJaxb(this._jaxbObj.getMetadata());
      }
      return null;
   }

   @Override
   public SPIngest getIngest() {
      if (this._jaxbObj.getIngest() != null) {
         return new IngestJaxb(this._jaxbObj.getIngest());
      }
      return null;
   }

   @Override
   public SPIngest getArchive() {
      if (this._jaxbObj.getArchive() != null) {
         return new IngestJaxb(this._jaxbObj.getArchive());
      }
      return null;
   }

   @Override
   public List<SPNotification> getNotifications() {
      List<SPNotification> result = new Vector<SPNotification>();
      for (Notification n : this._jaxbObj.getNotification()) {
         result.add(new NotificationJaxb(n));
      }
      return result;
   }

   @Override
   public void clearNotifications() {
      if (this._jaxbObj.getNotification() != null) {
         this._jaxbObj.getNotification().clear();
      }
   }

   @Override
   public void setHeader(SPHeader header) {
      if (header == null) return;

      SPHeader h = header;
      if (!(h.getImplObj() instanceof Header)) {
         h = new HeaderJaxb(h);
      }
      this._jaxbObj.setHeader((Header) h.getImplObj());
   }

   @Override
   public void setMetadata(SPMetadata metadata) {
      if (metadata == null) return;

      SPMetadata m = metadata;
      if (!(m.getImplObj() instanceof Metadata)) {
         m = new MetadataJaxb(m);
      }
      this._jaxbObj.setMetadata((Metadata) m.getImplObj());
   }

   @Override
   public void setIngest(SPIngest ingest) {
      if (ingest == null) return;

      SPIngest i = ingest;
      if (!(i.getImplObj() instanceof Ingest)) {
         i = new IngestJaxb(i);
      }
      this._jaxbObj.setIngest((Ingest) i.getImplObj());
   }

   @Override
   public void setArchive(SPIngest archive) {
      if (archive == null) return;

      SPIngest a = archive;
      if (!(a.getImplObj() instanceof Ingest)) {
         a = new IngestJaxb(a);
      }
      this._jaxbObj.setArchive((Ingest) a.getImplObj());
   }

   @Override
   public void addNotification(SPNotification notification) {
      SPNotification n = notification;
      if (!(n.getImplObj() instanceof Notification)) {
         n = new NotificationJaxb(n);
      }
      this._jaxbObj.getNotification().add((Notification) n.getImplObj());
   }
}
