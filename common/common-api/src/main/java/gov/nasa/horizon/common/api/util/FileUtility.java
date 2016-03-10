/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is an utility class that helps to deal with reading/writing
 * of file.
 * 
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: FileUtility.java 244 2007-10-02 20:12:47Z axt $
 *
 */
public class FileUtility {
   private static final int _BUFFER_SIZE = 1024;
   
   /**
    * Creates new instance.
    *
    */
   protected FileUtility() {
   }
   
   /**
    * Reads all of content in given file.
    * 
    * @param file File object representing the file to be read.
    * @return String object on success, null otherwise.
    */
   public static String readAll(File file) {
      StringBuilder content = new StringBuilder();
      FileReader fileReader = null;
      
      try {
         fileReader = new FileReader(file);
         
         char[] buffer = new char[_BUFFER_SIZE];
         int charactersRead = 0;
         while((charactersRead = fileReader.read(buffer, 0, buffer.length)) != -1) {
            content.append(buffer, 0, charactersRead);
         }
      } catch(Exception exception) {
         content.delete(0, content.length());
         content = null;
      } finally {
         try {
            if(fileReader != null) {
               fileReader.close();
            }
         } catch(Exception exception) {
         }
      }
      
      
      return (content == null) ? null : content.toString();
   }
   
   /**
    * Overloaded.
    * 
    * @param path A path to the file to be read.
    * @return String object on success, null otherwise.
    * @see #readAll(File)
    */
   public static String readAll(String path) {
      return readAll(new File(path));
   }
   
   /**
    * Writes a content to given file.
    * 
    * @param file File object representing the file to write the content to.
    * @param content Content to be written.
    * @return True on success, false otherwise.
    */
   public static boolean writeAll(File file, String content) {
      FileWriter fileWriter = null;
      boolean succeeded = true;
      
      try {
         fileWriter = new FileWriter(file);
         fileWriter.write(content);
      } catch(Exception exception) {
         succeeded = false;
      } finally {
         try {
            if(fileWriter != null) {
               fileWriter.close();
            }
         } catch(Exception exception) {
         }
      }
      
      
      return succeeded;
   }
   
   /**
    * Overloaded.
    * 
    * @param path A path to the file to write the content to.
    * @param content Content to be written.
    * @return True on success, false otherwise.
    * @see #writeAll(File, String)
    */
   public static boolean writeAll(String path, String content) {
      return writeAll(new File(path), content);
   }
   
   public static void downloadFromStream (InputStream is, OutputStream os) throws IOException {
      byte[] buffer = new byte[4092];
      int bytesRead = 0;
      while ((bytesRead = is.read(buffer, 0, 4092)) > -1) {
         os.write(buffer, 0, bytesRead);
      }
   }
}
