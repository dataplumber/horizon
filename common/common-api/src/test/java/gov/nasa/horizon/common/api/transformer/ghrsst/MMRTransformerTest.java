package gov.nasa.horizon.common.api.transformer.ghrsst;

import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile;
import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MMRTransformerTest {
   private static Log _logger = LogFactory.getLog(MMRTransformerTest.class);

   private static final String _GOES_11_FILE_NAME =
         "FR-20070828-GOES11-OSDPD-L2P-GOES11_North_0000Z-v01.xml";

   private static final String _MODIS_A_FILE_NAME =
         "FR-20070801-MODIS_A-JPL-L2P-A2007213074500.L2_LAC_GHRSST-v01.xml";

   private static final String _MODIS_T_FILE_NAME =
         "FR-20070814-MODIS_T-JPL-L2P-T2007226164500.L2_LAC_GHRSST-v01.xml";

   private String _rootPath = "";

   private String _user;
   private String _pass;

   @Before
   public void setup() {
      if (System.getProperty("common.test.path") != null)
         this._rootPath =
               System.getProperty("common.test.path") + File.separator;
      if (System.getProperty("common.test.remoteuser") != null)
         this._user = System.getProperty("common.test.remoteuser");
      if (System.getProperty("common.test.remotepass") != null)
         this._pass = System.getProperty("common.test.remotepass");
   }

   @Test
   public void goes11Transform() {
      try {
         MMRTransformer transformer =
               new MMRTransformer("Thomas.Huang@jpl.nasa.gov",
                     "sftp://seastorm.jpl.nasa" +
                           ".gov/data/dev/ingest-test/ghrsst_test_data");
         transformer.setSearchPath(this._rootPath);
         ServiceProfile profile =
               transformer.transform(new URI(this._rootPath
                     + MMRTransformerTest._GOES_11_FILE_NAME));
         MMRTransformerTest._logger.info(profile.toString());
      } catch (MMRException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (ServiceProfileException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void modisATransform() {
      try {
         MMRTransformer transformer =
               new MMRTransformer("Thomas.Huang@jpl.nasa.gov",
                     "sftp://seastorm.jpl.nasa" +
                           ".gov/data/dev/ingest-test/ghrsst_test_data");
         transformer.setSearchPath(this._rootPath);
         ServiceProfile profile =
               transformer.transform(new URI(this._rootPath
                     + MMRTransformerTest._MODIS_A_FILE_NAME));
         MMRTransformerTest._logger.info(profile.toString());
      } catch (MMRException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (ServiceProfileException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void modisTTransform() {
      try {
         MMRTransformer transformer =
               new MMRTransformer("Thomas.Huang@jpl.nasa.gov",
                     "sftp://seastorm.jpl.nasa" +
                           ".gov/data/dev/ingest-test/ghrsst_test_data");
         transformer.setSearchPath(this._rootPath);
         ServiceProfile profile =
               transformer.transform(new URI(this._rootPath
                     + MMRTransformerTest._MODIS_T_FILE_NAME));
         MMRTransformerTest._logger.info(profile.toString());
      } catch (MMRException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (ServiceProfileException e) {
         e.printStackTrace();
      }
   }

   /**
    * A simple test to enable the calling program to configure the file set to
    * be used by the transformer.
    */
   @Ignore
   @Test
   public void fileSetTest() {
      try {
         MMRTransformer transformer =
               new MMRTransformer("Thomas.Huang@jpl.nasa.gov");

         String host = "sftp://seastorm.jpl.nasa.gov";
         String path = "/data/dev/ingest-test/ghrsst_test_data/";

         // String host = "ftp://kingdom.jpl.nasa.gov";
         // String path =
         // "/Users/ingest/Development/data/dev/ingest-test/ghrsst_test_data/";

         transformer.registerAuthentication(host, this._user, this._pass);

         String tmpPath = host + path;
         transformer.registerFile(MMRTransformer.FILE_TYPE.RAW, tmpPath
               + "20070828-GOES11-OSDPD-L2P-GOES11_South_2352Z-v01.nc.gz");
         transformer.registerFile(MMRTransformer.FILE_TYPE.RAW_CHECKSUM,
               tmpPath
                     + "20070828-GOES11-OSDPD-L2P-GOES11_South_2352Z-v01.nc.gz.md5");
         transformer.registerFile(MMRTransformer.FILE_TYPE.MMR, tmpPath
               + "FR-20070828-GOES11-OSDPD-L2P-GOES11_South_2352Z-v01.xml");
         transformer.registerFile(MMRTransformer.FILE_TYPE.MMR_CHECKSUM,
               tmpPath
                     + "FR-20070828-GOES11-OSDPD-L2P-GOES11_South_2352Z-v01.xml.md5");

         // example for creating a search path with remote and local URI
         String searchPath = host + path + ";" + this._rootPath;
         transformer.setSearchPath(searchPath);
         transformer.setRootURI("ftp://foobar/pub/mydata");

         ServiceProfile profile =
               transformer.transform(new URI(host + path
                     + MMRTransformerTest._GOES_11_FILE_NAME));
         MMRTransformerTest._logger.debug(profile.toString());
         profile.toFile("/tmp/foo.xml");
      } catch (MMRException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (ServiceProfileException e) {
         e.printStackTrace();
      }
   }
}
