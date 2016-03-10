/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.util



/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class Prefix {
   String name
   String valueSeparator
   boolean isLong
   
   public Prefix() {
      name = ""
      valueSeparator = " "
      isLong = false
   }
}
