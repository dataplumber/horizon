/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * A collection of tests to test various methods in creating a ServiceProfile
 * object using the ServiceProfileFactory class.
 *
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: ServiceProfileFactoryTest.java 557 2008-01-10 23:20:11Z thuang $
 */
public class ServiceProfileFactoryTest {
   private static final String SIP_FILE_NAME = "podaac-ingest-example01.xml";
   private static final String SPM_FILE_NAME = "QSCAT.BATCH.0910";

   private String _rootPath = "";

   @Before
   public void setup() {
      if (System.getProperty("common.test.path") != null)
         this._rootPath =
               System.getProperty("common.test.path") + File.separator;
   }

   /**
    * Test to create a ServiceProfile object from an input SIP XML file
    */
   @Test
   public void createServiceProfileFromSipWithFile() {
      // creating ServiceProfile object with File object
      ServiceProfileFactory spf = ServiceProfileFactory.getInstance();
      try {
         ServiceProfile serviceProfile =
               spf.createServiceProfileFromMessage(new File(this._rootPath
                     + SIP_FILE_NAME));
         assertNotNull(serviceProfile);
      } catch (Exception exception) {
         fail("Failed to create ServiceProfile object.");
      }
   }

   /**
    * Test to create a ServiceProfile object from an input SIP text string.
    */
   @Test
   public void createServiceProfileFromSipWithString() {
      // read in sip
      StringBuilder sipContent = new StringBuilder();

      try {
         FileReader fileReader =
               new FileReader(new File(this._rootPath + SIP_FILE_NAME));
         // new File(getClass()
         // .getResource(this._rootPath + SIP_FILE_NAME).toURI()));
         BufferedReader bufferedReader = new BufferedReader(fileReader);

         String line;
         while ((line = bufferedReader.readLine()) != null) {
            sipContent.append(line + "\n");
         }

         bufferedReader.close();
         fileReader.close();
      } catch (Exception exception) {
         fail("Failed to read in SIP.");
      }

      // create ServiceProfile object
      ServiceProfileFactory spf = ServiceProfileFactory.getInstance();
      try {
         ServiceProfile serviceProfile =
               spf.createServiceProfileFromMessage(sipContent.toString());
         assertNotNull(serviceProfile);
      } catch (Exception exception) {
         fail("Failed to create ServiceProfile object.");
      }
   }
}
