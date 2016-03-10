/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.util



/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class CommandLineHelp {
   private static final int LEFT_PREFIX_SPACE = 2
   private static final int PREFIX_VALUE_SPACE = 2

   public CommandLineHelp() {
   }

   public String format(String programName, List options, List inputs, boolean isLong=false) {
      String output = ""

      output += "Usage: "+programName+" "
      if(options.size() > 0) {
         String optionValue = "Options"
         if(options.find{it.required} == null) {
            optionValue = "["+optionValue+"]"
         } else {
            optionValue = "<"+optionValue+">"
         }
         output += optionValue+" "
      }
      inputs.each {
         String inputValue = it.name
         if(it.required) {
            inputValue = "<"+inputValue+">"
         } else {
            inputValue = "["+inputValue+"]"
         }
         output += inputValue+" "
      }
      output += "\n"

      if(options.size() > 0) {
         output += "where possible options include:\n"

         List prefixesList = []
         int prefixLength = 0;
         options.each {option ->
            List prefixes = new LinkedList(option.prefixes)
            if(!isLong) {
               prefixes = prefixes.findAll{!it.isLong}
            }
            if(prefixes.size() > 0) {
               String prefixText = ""
               prefixes.each {
                  if(prefixText != "") {
                     prefixText += ", "
                  }
                  prefixText += it.name
                  if(option.withValue) {
                     prefixText += it.valueSeparator+"<value>"
                  }
               }
               if(prefixText.length() > prefixLength) {
                  prefixLength = prefixText.length()
               }

               prefixesList.add([prefixText, option.description])
            }
            /*
            List values = new LinkedList()
            option.names.each{
               values.add(it+(option.withValue ? option.nameValueSeparator+"<Value>" : ""))
            }
            output += "  "+values.join(", ")
            output += "\t\t"+option.description+"\n"
            */
         }

         prefixesList.each {
            LEFT_PREFIX_SPACE.times{output += " "}
            output += it[0]
            ((prefixLength + PREFIX_VALUE_SPACE) - it[0].length()).times{output += " "}
            output += it[1]+"\n"
         }
      }

      return output
   }
}
