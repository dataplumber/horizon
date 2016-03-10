/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import gov.nasa.horizon.common.api.serviceprofile.SPCommon.SPMessageLevel;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: $
 */
public interface SPNotification extends Accessor {

   String getAddress();

   String getEmail();

   String getFax();

   String getFirstName();

   String getLastName();

   SPMessageLevel getMessageLevel();

   String getMiddleName();

   String getPhone();

   String getRole();

   void setAddress(String address);

   void setEmail(String email);

   void setFax(String fax);

   void setFirstName(String firstName);

   void setLastName(String lastName);

   void setMessageLevel(SPMessageLevel messageLevel);

   void setMiddleName(String middleName);

   void setPhone(String phone);

   void setRole(String role);
}
