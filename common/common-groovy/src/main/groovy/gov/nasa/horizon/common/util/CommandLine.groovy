/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.util



/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class CommandLine {
   Map optionValues
   Map inputValues

   public CommandLine() {
      optionValues = [:]
      inputValues = [:]
   }

   public String getOptionValue(Option option) {
      return optionValues.get(option)
   }
   
   public boolean hasOption(Option option) {
      return optionValues.containsKey(option)
   }
   
   public List getInputValues(Input input) {
      return inputValues.get(input)
   }
   
   public boolean hasInput(Input input) {
      return inputValues.containsKey(input)
   }
}
