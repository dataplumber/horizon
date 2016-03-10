package gov.nasa.horizon.common.api.serviceprofile;

import java.util.Date;

/**
 * Interface to Operation class.
 *
 * @author T. Huang
 * @version $Id $
 */
public interface SPOperation extends Accessor {

   Long getOperationId();

   void setOperationId(long operationId);

   String getAgent();

   void setAgent(String agent);

   String getOperation();

   void setOperation(String operation);

   Date getOperationStartTime();

   void setOperationStartTime(Date start);

   void setOperationStartTime(long start);

   Date getOperationStopTime();

   void setOperationStopTime(Date stop);

   void setOperationStopTime(long stop);

   String getCommand();

   void setCommand(String command);

   String getArguments();

   void setArguments(String arguments);

}
