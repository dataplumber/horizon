/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This utility class is a wrapper class to all system-related process.
 * 
 * @author T. Huang, {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class SystemProcess {

    public static Log _logger = LogFactory.getLog(SystemProcess.class);

   /**
    * Method to execute a system command and capture its standard errors.
    * 
    * @param command the system command to be executed
    * @return the Errno object reference.
    */
   public static final Errno execute(String command) {
      Errno errno = null;
      ProcessRunner runner = new ProcessRunner(command);
      Thread p = new Thread(runner);
      try {
         p.start();
         p.join();
         errno = runner.getErrno();
      } catch (Exception e) {
         _logger.error(e.getMessage());
         _logger.trace(null, e);
         errno = new Errno(-1, e.getMessage());
      }

      return errno;
   }

   /**
    * Run process in a separate thread just in case we get into trouble, we can
    * still recover from the application main thread.
    */
   static final class ProcessRunner implements Runnable {

      private String _command = null;
      private Errno _errno = null;

      public ProcessRunner(String command) {
         this._command = command;
      }

      public Errno getErrno() {
         return this._errno;
      }

      public void run() {
         StringBuffer errMsg = new StringBuffer();
         Process p = null;

         try {
            p = Runtime.getRuntime().exec(this._command);

            BufferedReader is = new BufferedReader(new InputStreamReader(p
                  .getInputStream()));
            String line;
            boolean newline = false;
            while ((line = is.readLine()) != null) {
               _logger.info(line);
               newline = true;
            }
            if (newline)
               _logger.info("");
            is.close();

            newline = false;
            int stderr = p.waitFor();
            is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = is.readLine()) != null) {
               errMsg.append(line);
               _logger.error(line);
               newline = true;
            }
            if (newline)
               _logger.error("");
            is.close();

            // close output stream
            p.getOutputStream().close();

            this._errno = new Errno(stderr, errMsg.toString());
         } catch (Exception e) {
            this._errno = new Errno(-1, e.getMessage());
         } finally {
            if (p != null)
               p.destroy();
         }

      }
   }

}