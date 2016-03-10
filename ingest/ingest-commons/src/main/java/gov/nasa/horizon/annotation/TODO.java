/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.annotation;

/**
 * Custom annotation for TODO items
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: TODO.java 1846 2008-09-16 18:34:41Z thuang $
 */
public @interface TODO {
   public enum Priority {
      HIGH, NORMAL, LOW
   };

   Priority priority() default Priority.NORMAL;

   String note();

   String owner() default "";
}
