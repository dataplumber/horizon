/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

/**
 * A helper interface object for accessing internals of the ServiceProfile
 * implementation.
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: Accessor.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface Accessor {

   /**
    * Getter to the internal implementation object (e.g. DOM/JAXB object
    * reference)
    * 
    * @return the internal implementation object.
    */
   Object getImplObj();

   /**
    * Getter to the immediate owner of the current element.
    * 
    * @return owner of the element
    */
   Object getOwner();

   /**
    * Setter of the owner of the element.
    * 
    * @param owner
    *           an owner object reference
    */
   void setOwner(Object owner);

}
