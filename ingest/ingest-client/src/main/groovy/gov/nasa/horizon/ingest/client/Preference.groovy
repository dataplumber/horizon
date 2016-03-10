/** ***************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 **************************************************************************** */
package gov.nasa.horizon.ingest.client

import gov.nasa.horizon.ingest.api.Constants
import gov.nasa.horizon.ingest.api.Domain
import gov.nasa.horizon.ingest.api.Keychain

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: Preference.groovy 2666 2009-02-18 00:15:45Z axt $
 */
public class Preference {
   /*
   private static final HORIZON_DIRECTORY_NAME = ".horizon"
   private static final LOGIN_FILE_NAME = ".login"
   */

   public Preference() {
      setUp()
   }

   public File getDomainFile() {
      return new File(System.getProperty(Constants.PROP_DOMAIN_FILE))
   }

   public Domain getDomain() throws Exception {
      Domain domain = null
      try {
         domain = new Domain()
         domain.load(getDomainFile().text)
      } catch (exception) {
         throw exception
      }

      return domain
   }

   public boolean hasLoginInformation() {
      File keychainFile = getHorizonKeychainFile()
      return keychainFile.exists()
   }

   public LoginInformation getLoginInformation(String federation) {
      LoginInformation loginInformation = null

      if (hasLoginInformation()) {
         try {
            Keychain keychain = new Keychain()
            File keychainFile = getHorizonKeychainFile()
            keychain.load(keychainFile.text)

            String userName = keychain.getFederationUsername(federation)
            String password = keychain.getFederationPassword(federation)
            if (userName && password) {
               loginInformation = new LoginInformation()
               loginInformation.userName = userName
               loginInformation.password = password
            }
         } catch (exception) {
            exception.printStackTrace()
         }
      }

      return loginInformation
   }

   public void setLoginInformation(String federation, LoginInformation loginInformation) {
      Keychain keychain = new Keychain()
      if (hasLoginInformation()) {
         File keychainFile = getHorizonKeychainFile()
         keychain.load(keychainFile.text)
      }

      keychain.addKey(federation, loginInformation.userName, loginInformation.password)
      keychain.toFile(getHorizonKeychainFile().getAbsolutePath())
   }

   private void setUp() {
      File horizonDirectory = getHorizonDirectory()
      if (!horizonDirectory.exists()) {
         horizonDirectory.mkdir()
      }
   }

   private File getHorizonDirectory() {
      String horizonDirectory = System.getProperty(Constants.PROP_RESTART_DIR)
      if (!horizonDirectory) {
         horizonDirectory = System.getProperty("user.home")
      }
      horizonDirectory += File.separator + Constants.RESTART_DIR

      return new File(horizonDirectory)
      /*
      String homeDirectory = System.getProperty("user.home");
      File horizonDirectory = new File(
         homeDirectory+File.separator+HORIZON_DIRECTORY_NAME
      )

      return horizonDirectory
      */
   }

   private File getHorizonKeychainFile() {
      File horizonDirectory = getHorizonDirectory()
      File keychainFile = new File(
            horizonDirectory.getAbsolutePath() + File.separator + Constants.KEYCHAIN_FILE
      )

      return keychainFile
   }
}
