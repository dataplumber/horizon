/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Agent;
import gov.nasa.horizon.common.api.serviceprofile.SPAgent;
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Implementation of Agent object using JAXB for XML marshalling and
 * unmarshalling.
 *
 * @author T. Huang
 * @version $Id: $
 */
public class AgentJaxb extends AccessorBase implements SPAgent {

   private Agent _jaxbObj;

   public AgentJaxb() {
      this._jaxbObj = new Agent();
   }

   public AgentJaxb(SPAgent agent) {
      this._jaxbObj = new Agent();
      this._jaxbObj.setName(agent.getName());
      this._jaxbObj.setAddress(agent.getAddress().toString());
      this._jaxbObj.setTime(BigInteger.valueOf(agent.getTime().getTime()));
   }

   public AgentJaxb(Agent jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final AgentJaxb other = (AgentJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized InetAddress getAddress() {
      try {
         return InetAddress.getByName(this._jaxbObj.getAddress());
      } catch (UnknownHostException e) {
         return null;
      }
   }

   public synchronized String getName() {
      return this._jaxbObj.getName();
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized Date getTime() {
      Date date = null;
      if (this._jaxbObj.getTime() != null)
         date = new Date(this._jaxbObj.getTime().longValue());

      return date;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setAddress(InetAddress address)
         throws ServiceProfileException {
      this._jaxbObj.setAddress(address.getHostAddress());
   }

   public synchronized void setName(String name)
         throws ServiceProfileException {
      this._jaxbObj.setName(name);

   }

   public synchronized void setTime(Date timestamp)
         throws ServiceProfileException {
      this._jaxbObj.setTime(BigInteger.valueOf(timestamp.getTime()));
   }

   public synchronized void setTime(long timestamp)
         throws ServiceProfileException {
      if (timestamp < 0)
         throw new ServiceProfileException("Invalid time value.");

      this.setTime(new Date(timestamp));
   }
}
