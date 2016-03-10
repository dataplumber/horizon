/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler;

import java.io.File;

/**
 * This class provides a way to answer the question: Can I write this file to
 * the file system?
 * 
 * Assumption(s):
 * 
 * 1. The input is a name of a file and not a directory.
 * 
 * @author qchau
 * @version $Id: FileWriteChecker.java 937 2008-04-16 00:33:02Z thuang $
 */
public class FileWriteChecker {

   private boolean m_isFileWritable = false;

   /**
    * Constructor
    * 
    * @param i_filenameToCheck
    */
   public FileWriteChecker(String i_filenameToCheck) {
      m_isFileWritable = verifyDirectoryStateWritable(i_filenameToCheck);
   }

   public boolean getFileCheckFlag() {
      return (m_isFileWritable);
   }

   /**
    * Function returns true if the file can be written out to disk. Basically,
    * check to see this process has write permission on the directory.
    * 
    * @param i_filenameToCheck
    * @return
    */
   private boolean verifyDirectoryStateWritable(String i_filenameToCheck) {

      boolean r_isFileWritable = false;

      // Construct a File object for the given filename.
      File a_file = new File(i_filenameToCheck);

      // The directory name is all characters before the last '/'.
      // The file name is all characters after the last '/'.

      int lastSlash = a_file.getAbsolutePath().lastIndexOf('/');
      String l_directory = a_file.getAbsolutePath().substring(0, lastSlash);

      File a_directory = new File(l_directory);
      // Check if user has write permission in this directory.
      if (a_directory.canWrite()) {
         // Now that we have determined that the user can write in this
         // directory, we
         // check if the file name exist. If it is, we do the canWrite check.
         // Otherwise
         // we are done.
         if (a_file.exists()) {
            // Check if the user can modify this file.
            if (a_file.canWrite()) {
               r_isFileWritable = true;
            }
         } else {
            // We can write into this directory and the file does not exist yet,
            // we can
            // definitely write this file.

            r_isFileWritable = true;
         }
      }

      return (r_isFileWritable);
   }
}
