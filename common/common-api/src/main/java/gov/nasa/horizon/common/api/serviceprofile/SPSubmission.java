/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import java.util.List;

public interface SPSubmission extends Accessor {

   SPHeader createHeader();

   SPMetadata createMetadata();

   SPIngest createIngest();

   SPIngest createArchive();

   SPNotification createNotification();

   SPHeader getHeader();

   SPMetadata getMetadata();

   SPIngest getIngest();

   SPIngest getArchive();

   List<SPNotification> getNotifications();

   void clearNotifications();

   void setHeader(SPHeader header);

   void setMetadata(SPMetadata metadata);

   void setIngest(SPIngest ingest);

   void setArchive(SPIngest archive);

   void addNotification(SPNotification notification);
}
