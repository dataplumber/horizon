/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.util

import gov.nasa.horizon.common.util.CommandLineParser
import gov.nasa.horizon.common.util.CommandLine
import gov.nasa.horizon.common.util.Option

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class OptionTest extends GroovyTestCase {
   private static final String LINE = "--user=atsuya -p pass federation"
   
   public void test() {
      CommandLineParser clp = new CommandLineParser()

      Option optionUserName = new Option(
         name: "Username",
         description: "Username for something.",
         withValue: true,
         prefixes: [
            new Prefix(name: "-u"),
            new Prefix(name: "--user", valueSeparator: "=", isLong: true)
         ]
      )
      clp.options.add(optionUserName)
      
      Option optionPassword = new Option(
         name: "Password",
         description: "Password for something.",
         withValue: true,
         prefixes: [
            new Prefix(name: "-p"),
            new Prefix(name: "--password", valueSeparator: "=", isLong: true)
         ]
      )
      clp.options.add(optionPassword)
      
      Input inputFederation = new Input(
         name: "Federation",
         description: "Federation for something."
      )
      clp.inputs.add(inputFederation)

      /*
      Option option1 = new Option(
         names: ["-t", "--test"] as Set,
         description: "Test Option",
         withValue: true
      )
      clp.options.add(option1)

      Option option2 = new Option()
      option2.names = ["--help"] as Set
      option2.description = "Help Option"
      option2.withValue = false
      option2.nameValueSeparator = "="
      option2.required = true
      clp.options.add(option2)
      
      Input input1 = new Input()
      input1.names = ["arg1"] as Set
      input1.description = "Argument 1"
      input1.required = true
      clp.inputs.add(input1)
      
      Input input2 = new Input()
      input2.names = ["files"] as Set
      input2.description = "Files..."
      input2.required = true
      clp.inputs.add(input2)
      */

      try {
         println "Argument: "+LINE
         CommandLine commandLine = clp.parse(LINE.split("\\s+"))

         def printInputs = {
            for(entry in it) {
               println entry.key.name+": "+entry.value
            }
         }
         println "\nResult:"
         printInputs(commandLine.optionValues)
         printInputs(commandLine.inputValues)
         
         CommandLineHelp clh = new CommandLineHelp()
         println "\nFollowing is a short help.\n"
         println clh.format("TestProgram", clp.options, clp.inputs)
         println "Following is a long help.\n"
         println clh.format("TestProgram", clp.options, clp.inputs, true)
      } catch(exception) {
         exception.printStackTrace()
      }
   }

   public static void main(String[] args) {
      def optionTest = new OptionTest()
      optionTest.test
   }
}
