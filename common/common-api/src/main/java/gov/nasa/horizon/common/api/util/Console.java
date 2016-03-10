package gov.nasa.horizon.common.api.util;

import java.io.IOException;

/**
 * This is a utility class that has only one method to allow user to type in
 * their password without being echoed back on the console.
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: Console.java 244 2007-10-02 20:12:47Z axt $
 */
public class Console {

   /**
    * Method to read in a user password.
    * 
    * @param prompt The command prompt string (e.g. 'Password:')
    * @return The user input password string
    * @throws IOException When failed to read user input.
    */
   public static String readPassword(String prompt) throws IOException {
      String password = "";

      // creates the eraser thread
      Eraser eraser = new Eraser(prompt);
      Thread eraserThread = new Thread(eraser);
      eraserThread.start();

      while (true) {
         // this is a blocking call until the user press' enter.
         char c = (char) System.in.read();

         // Now the user is done inputing, we should stop the eraser
         eraser.quit();

         // a special case for platforms that reads \r\n for end of line.
         if (c == '\r') {
            c = (char) System.in.read();
            if (c == '\n')
               break;
            else
               continue;
         } else if (c == '\n')
            break;
         else
            password += c;
      }
      return password;
   }

   /**
    * Inner eraser class that continues to refresh the input line with user
    * prompt and blanks.
    * 
    * @author T. Huang
    *         <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
    * @version $Id: Console.java 244 2007-10-02 20:12:47Z axt $
    */
   private static class Eraser implements Runnable {

      private boolean _stop = false;
      private String _userPrompt;

      /**
       * Constructor to set the user prompt to blank.
       * 
       */
      public Eraser() {
         this._userPrompt = "";
      }

      /**
       * Constructor to set the user prompt.
       * 
       * @param prompt The user prompt.
       */
      public Eraser(String prompt) {
         this._userPrompt = prompt;
      }

      /**
       * Method to keeps on erasing the input line until it is signaled to stop.
       */
      public void run() {
         while (!this._stop) {
            try {
               Thread.sleep(10);
            } catch (InterruptedException e) {
               // no-op;
            }

            if (!this._stop) {
               System.out.print("\r" + this._userPrompt + " \r"
                        + this._userPrompt);
               System.out.flush();
            }
         }
      }

      /**
       * Method to set the stop flag.
       * 
       */
      public void quit() {
         this._stop = true;
      }
   }
}
