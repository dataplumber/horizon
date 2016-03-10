/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

/**
 * 
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: Pair.java 244 2007-10-02 20:12:47Z axt $
 */
public class Pair<T1, T2>
{
   private T1 _name;
   private T2 _value;
   
   public Pair(T1 name, T2 value)
   {
      _name = name;
      _value = value;
   }

   public T1 getName()
   {
      return _name;
   }

   public T2 getValue()
   {
      return _value;
   }
}
