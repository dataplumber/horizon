/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import java.net.URI;
import java.util.Date;
import java.util.List;

public interface SPIngest extends Accessor {

   SPFileDestination createDestination();

   void clearDeletes();

   void addDelete(SPFileDestination delete);

   List<SPFileDestination> getDeletes();

   String getOperationNote();

   void setOperationNote(String operationNote);

   SPIngestProductFile createIngestProductFile();

   void clearIngestProductFiles();

   void addIngestProductFile(SPIngestProductFile ingestProductFile);

   List<SPIngestProductFile> getIngestProductFiles();

   Boolean isOperationSuccess();

   void setOperationSuccess(Boolean flag);

}
