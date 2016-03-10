/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Makes a test file of specified size.
 * 
 * @author T. Huang, {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class FileMaker {
   private static Log _logger = LogFactory.getLog(FileMaker.class);

   private ByteBuffer _buffer = ByteBuffer.allocate(1024 * 1024);
   private long _numWrite = 0;
   private String _filename;

   /**
    * Constructor
    * 
    * @param args the command line args
    */
   public FileMaker(String[] args) {
      if (!this._parseArgs(args))
         System.exit(0);
   }

   /**
    * Run method
    */
   public void run() {
      try {
         FileChannel filechannel =
               new FileOutputStream(this._filename).getChannel();

         this._buffer.clear();

         for (int i = 0; i < this._buffer.capacity(); ++i)
            this._buffer.put((byte) 0);

         for (int i = 0; i < this._numWrite; ++i) {
            this._buffer.flip();
            filechannel.write(this._buffer);
         }
         filechannel.close();
      } catch (Exception e) {
         FileMaker._logger.error(e.getMessage());
      }

   }

   /**
    * Parse the command line args
    * 
    * @param args the command line args
    * @return boolean true if parsed successfully, false otherwise
    */
   private boolean _parseArgs(String[] args) {
      GetOpt getOpt = new GetOpt(args, "n:s:H");

      String str;
      try {
         while ((str = getOpt.nextArg()) != null) {
            // it's so happened that all the test arguments begin with different
            // letter, so we can just check by that. In general, we can also
            // use String.equals() method to do the comparison.
            switch (str.charAt(0)) {
            case 'H':
               FileMaker._logger.info("Usage: " + this.getClass().getName()
                     + " -n <filename> -s <size in MB> -H");
               return false;
            case 'n':
               this._filename = getOpt.getArgValue();
               break;
            case 's':
               this._numWrite = Integer.parseInt(getOpt.getArgValue());
               break;
            default:
               return false;
            }
         }
      } catch (ParseException pEx) {
         return false;
      }
      return true;
   }

   /**
    * Main method
    * 
    * @param args the command line args
    */
   public static void main(String[] args) {
      FileMaker fm = new FileMaker(args);
      fm.run();
   }
}
