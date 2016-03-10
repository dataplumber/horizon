/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.util



/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class CommandLineParser {
   List options
   List inputs

   public CommandLineParser() {
      options = []
      inputs = []
   }

   public CommandLine parse(String[] args) {
      CommandLine commandLine = new CommandLine()

      //List unusedArguments = []
      LinkedList unusedArguments = new LinkedList(args as List)
      // for options
      options.each {option ->
         String optionValue = null
         if(option.withValue) {
            option.prefixes.each {prefix ->
               if(prefix.valueSeparator == " ") {
                  String targetArgument = unusedArguments.find {argument ->
                     prefix.name == argument
                  }
                  if(targetArgument != null) {
                     int index = unusedArguments.indexOf(targetArgument)
                     if((index + 1) < unusedArguments.size()) {
                        optionValue = unusedArguments[(index + 1)]
                        [targetArgument, optionValue].each{unusedArguments.remove(it)}
                     }
                  }
               } else {
                  String targetArgument = unusedArguments.find {argument ->
                     argument.startsWith(prefix.name+prefix.valueSeparator)
                  }
                  if(targetArgument != null) {
                     optionValue = targetArgument.split(prefix.valueSeparator)[1]
                     unusedArguments.remove(targetArgument)
                  }
               }
            }
         } else {
            String optionName = unusedArguments.find {argument ->
               option.prefixes.find{it.name == argument} != null
            }
            if(optionName != null) {
               optionValue = ""
               unusedArguments.remove(optionName)
            }
         }
         
         if(optionValue != null) {
            commandLine.optionValues.put(option, optionValue)
         }
      }
      
      // for inputs
      if(inputs.size() > 0) {
         int inputIndex
         for(inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
            if(inputIndex < unusedArguments.size()) {
               commandLine.inputValues.put(
                  inputs[inputIndex],
                  [unusedArguments[inputIndex]]
               )
            }
         }
         for(int i = inputIndex; i < unusedArguments.size(); i++) {
            List values = commandLine.inputValues.get(inputs[inputs.size() - 1])
            values.add(unusedArguments[i])
         }
      }
      
      // validate
      validate(commandLine)

      return commandLine
   }
   
   public void clear() {
      options.clear()
      inputs.clear()
   }
   
   private void validate(CommandLine commandLine) {
      def action = { map, inputs ->
         inputs.findAll{it.required == true}.each {
            if(!map.containsKey(it)) {
               throw new Exception("Missing argument: "+it.name)
            }
         }
      }
      action(commandLine.optionValues, options)
      action(commandLine.inputValues, inputs)
   }
}
