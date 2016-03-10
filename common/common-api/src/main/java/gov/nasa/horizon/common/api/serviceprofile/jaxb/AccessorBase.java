/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.serviceprofile.Accessor;

abstract class AccessorBase implements Accessor {
   private Object _owner = null;

   public abstract Object getImplObj();

   public synchronized Object getOwner() {
      return this._owner;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_owner == null) ? 0 : _owner.hashCode());
      return result;
   }

   public synchronized void setOwner(Object owner) {
      this._owner = owner;
   }

}
