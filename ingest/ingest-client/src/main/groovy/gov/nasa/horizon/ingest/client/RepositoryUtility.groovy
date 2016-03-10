/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.ingest.client;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class RepositoryUtility {
   private static Log _logger = LogFactory.getLog(RepositoryUtility.class);
   
   protected RepositoryUtility() {
   }
   
   public synchronized static File createDirectory(File directory) throws Exception {
      File file = File.createTempFile("Repository", "Store", directory);
      if(!file.delete()) {
         throw new Exception("Could not delete temporary file.");
      }
      
      File newDirectory = new File(file.getAbsolutePath());
      if(!newDirectory.mkdir()) {
         throw new Exception("Could not create new directory.");
      }
      
      return newDirectory;
   }
   
   public synchronized static void deleteDirectory(File directory, boolean ifEmpty) {
      if(directory.isDirectory()) {
         File[] files = directory.listFiles().findAll{!it.getName().startsWith(".")}
         if((!ifEmpty) || ((ifEmpty) && (files.length == 0))) {
            def deleteFiles
            deleteFiles = {file ->
               if(file.isDirectory()) {
                  file.listFiles().each {element ->
                     deleteFiles(element)
                  }
               }
               file.delete()
            }
            
            deleteFiles(directory)
         }
      }
   }
   
   public synchronized static void deleteDirectory(File directory) {
      deleteDirectory(directory, false)
   }
}
