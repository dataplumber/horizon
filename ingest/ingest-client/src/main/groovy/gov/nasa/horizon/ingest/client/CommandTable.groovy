/** ***************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 **************************************************************************** */

package gov.nasa.horizon.ingest.client;

/**
 * Utility class to create mapping between string command with enumerated command values.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}* @version $Id: CommandTable.java 536 2007-12-29 04:49:09Z thuang $
 */
public class CommandTable {

  private static final def _cmd = [
          'SUBMIT': 'horizon.util.submit',
          'TRANSFORM': 'horizon.util.transform',
          'SIP_SUBMIT': 'horizon.util.sipsubmit',
          'SPM_SUBMIT': 'horizon.util.spmsubmit',
          'SIP_SUBSCRIBE': 'horizon.util.sipsubscribe',
          'MSG_SUBSCRIBE': 'horizon.util.msgsubscribe',
          'INVALID_COMMAND': 'horizon.util.invalidcommand'
  ]

  private CommandTable() {
  }

  public static String getCommand(String commandString) {
    def cmd = _cmd.find {key, value ->
      value == commandString
    }

    if (!cmd) {
      return 'INVALID_COMMAND'
    }

    return cmd.key
  }

  public static String getCommandString(String command) {
    return _cmd[command];
  }
}
