/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.crawler;

import java.io.File;

/**
 * This class provides a way to answer the question: Does this file exist on the
 * file system and can I read it?
 * 
 * Assumption(s):
 * 
 * 1. The input is a name of a file and not a directory.
 * 
 * @author qchau
 * @version $Id: FileExistChecker.java 937 2008-04-16 00:33:02Z thuang $
 */
public class FileExistChecker {

   private boolean m_doesFileExist = false;

   /**
    * Constructor
    * 
    * @param i_filenameToCheck
    */
   public FileExistChecker(String i_filenameToCheck) {
      m_doesFileExist = verifyFileReadable(i_filenameToCheck);
   }

   public boolean getFileCheckFlag() {
      return (m_doesFileExist);
   }

   /**
    * Function returns true if the file can be read or exist.
    * 
    * @param i_filenameToCheck
    * @return
    */
   private boolean verifyFileReadable(String i_filenameToCheck) {

      boolean r_isFileReadable = false;

      // Construct a File object for the given filename.
      File a_file = new File(i_filenameToCheck);

      // Check if the file is readable.
      if (a_file.canRead()) {
         r_isFileReadable = true;
      }
      return (r_isFileReadable);
   }
}
