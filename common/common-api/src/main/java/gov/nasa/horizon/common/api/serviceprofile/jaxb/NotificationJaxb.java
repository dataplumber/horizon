/*****************************************************************************
 * Copyright (c) 2007-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.MessageFrequency;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Notification;
import gov.nasa.horizon.common.api.serviceprofile.SPCommon.SPMessageLevel;
import gov.nasa.horizon.common.api.serviceprofile.SPNotification;

/**
 * Implementation of Notification object using JAXB for XML marshalling and
 * unmarshalling.
 *
 * @author T. Huang
 * @version $Id: $
 */
public class NotificationJaxb extends AccessorBase implements SPNotification {

   private Notification _jaxbObj = null;

   public NotificationJaxb() {
      this._jaxbObj = new Notification();
   }

   public NotificationJaxb(SPNotification notification) {
      this._jaxbObj = new Notification();

      this.setAddress(notification.getAddress());
      this.setEmail(notification.getEmail());
      this.setFax(notification.getFax());
      this.setFirstName(notification.getFirstName());
      this.setLastName(notification.getLastName());
      this.setMiddleName(notification.getMiddleName());
      this.setMessageLevel(notification.getMessageLevel());
      this.setPhone(notification.getPhone());
      this.setRole(notification.getRole());
   }

   public NotificationJaxb(Notification jaxbObj) {
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
      final NotificationJaxb other = (NotificationJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized String getAddress() {
      return this._jaxbObj.getAddress();
   }

   public synchronized String getEmail() {
      return this._jaxbObj.getEmail();
   }

   public synchronized String getFax() {
      return this._jaxbObj.getFax();
   }

   public synchronized String getFirstName() {
      return this._jaxbObj.getFirstName();
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized String getLastName() {
      return this._jaxbObj.getLastName();
   }

   public synchronized SPMessageLevel getMessageLevel() {
      return SPMessageLevel.valueOf(this._jaxbObj.getMessageLevel().toString
            ());
   }

   public synchronized String getMiddleName() {
      return this._jaxbObj.getMiddleName();
   }

   public synchronized String getPhone() {
      return this._jaxbObj.getPhone();
   }

   public synchronized String getRole() {
      return this._jaxbObj.getRole();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setAddress(String address) {
      this._jaxbObj.setAddress(address);
   }

   public synchronized void setEmail(String email) {
      this._jaxbObj.setEmail(email);
   }

   public synchronized void setFax(String fax) {
      this._jaxbObj.setFax(fax);
   }

   public synchronized void setFirstName(String firstName) {
      this._jaxbObj.setFirstName(firstName);
   }

   public synchronized void setLastName(String lastName) {
      this._jaxbObj.setLastName(lastName);
   }

   public synchronized void setMessageLevel(SPMessageLevel messageLevel) {
      this._jaxbObj.setMessageLevel(MessageFrequency.valueOf(messageLevel
            .toString()));
   }

   public synchronized void setMiddleName(String middleName) {
      this._jaxbObj.setMiddleName(middleName);
   }

   public synchronized void setPhone(String phone) {
      this._jaxbObj.setPhone(phone);

   }

   public synchronized void setRole(String role) {
      this._jaxbObj.setRole(role);

   }

   public synchronized String toString() {
      return "Last Name : " + this._jaxbObj.getLastName() + "\n"
            + "First Name: " + this._jaxbObj.getFirstName() + "\n"
            + "Email     : " + this._jaxbObj.getEmail() + "\n"
            + "Message Level : " + this._jaxbObj.getMessageLevel();
   }

}
