package gov.nasa.horizon.common.api.serviceprofile;

import java.util.Date;

/**
 * Interface to Product File to be ingested/archived
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface SPIngestProductFile extends Accessor {

   SPFileDestination createFileDestination();

   SPFileDestination getFileDestination();

   void setFileDestination(SPFileDestination fileDestination);

   SPProductFile createProductFile();

   SPProductFile getProductFile();

   void setProductFile(SPProductFile productFile);

   Date getIngestStartTime();

   void setIngestStartTime(long startTime);

   void setIngestStartTime(Date startTime);

   Date getIngestStopTime();

   void setIngestStopTime(long stopTime);

   void setIngestStopTime(Date stopTime);
}
