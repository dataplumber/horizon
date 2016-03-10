/*****************************************************************************
 * Copyright (c) 2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Operation;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.TimeStamp;
import gov.nasa.horizon.common.api.serviceprofile.SPOperation;

import java.math.BigInteger;
import java.util.Date;

/**
 * JAXB Operation class wrapper
 *
 * @author T. Huang
 * @version $Id: $
 */
public class OperationJaxb extends AccessorBase implements
      SPOperation {

   private Operation _jaxbObj;


   public OperationJaxb() {
      this._jaxbObj = new Operation();
   }

   public OperationJaxb(SPOperation details) {
      this._jaxbObj = new Operation();

      this.setAgent(details.getAgent());
      this.setOperation(details.getOperation());
      this.setOperationStartTime(details.getOperationStartTime());
      this.setOperationStopTime(details.getOperationStopTime());
      this.setCommand(details.getCommand());
      this.setArguments(details.getArguments());
   }

   public OperationJaxb(Operation jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final OperationJaxb other = (OperationJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   @Override
   public Long getOperationId() {
      return this._jaxbObj.getOperationId();
   }

   @Override
   public void setOperationId(long operationId) {
      this._jaxbObj.setOperationId(operationId);
   }

   public String getAgent() {
      return this._jaxbObj.getAgent();
   }

   public void setAgent(String agent) {
      this._jaxbObj.setAgent(agent);
   }

   public String getOperation() {
      return this._jaxbObj.getOperation();
   }

   public void setOperation(String operation) {
      this._jaxbObj.setOperation(operation);
   }

   public Date getOperationStartTime() {
      return new Date(this._jaxbObj.getTime().getStart().longValue());
   }

   public void setOperationStartTime(Date start) {
      this.setOperationStartTime(start.getTime());
   }

   public void setOperationStartTime(long start) {
      if (this._jaxbObj.getTime() == null) {
         this._jaxbObj.setTime(new TimeStamp());
      }
      this._jaxbObj.getTime().setStart(BigInteger.valueOf(start));
   }

   public Date getOperationStopTime() {
      return new Date(this._jaxbObj.getTime().getStop().longValue());
   }

   public void setOperationStopTime(Date stop) {
      if (this._jaxbObj.getTime() == null) {
         this._jaxbObj.setTime(new TimeStamp());
      }
      this.setOperationStopTime(stop.getTime());
   }

   public void setOperationStopTime(long stop) {
      this._jaxbObj.getTime().setStop(BigInteger.valueOf(stop));
   }

   @Override
   public String getCommand() {
      return this._jaxbObj.getCommand();
   }

   @Override
   public void setCommand(String command) {
      this._jaxbObj.setCommand(command);
   }

   @Override
   public String getArguments() {
      return this._jaxbObj.getArguments();
   }

   @Override
   public void setArguments(String arguments) {
      this._jaxbObj.setArguments(arguments);
   }
}
