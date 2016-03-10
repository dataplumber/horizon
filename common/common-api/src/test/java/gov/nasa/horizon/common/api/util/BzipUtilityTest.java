package gov.nasa.horizon.common.api.util;

import gov.nasa.horizon.common.api.util.BzipUtility;
import gov.nasa.horizon.common.api.util.ChecksumUtility;
import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class BzipUtilityTest {

   @Before
   public void setup() {

   }

   @Test
   public void testBzipUtility_0() {
      // Notes:
      //
      //  1.  This test only works with the following versions specified in the pom.xml.  If a different version is used
      //      care should be taken to update the values for the variable EXPECTED_CHECKSUM to reflect the actual values.
      //
      //        </dependency>
      //         <dependency>
      //          <groupId>org.apache.commons</groupId>
      //          <artifactId>commons-compress</artifactId>
      //          <version>1.1</version>
      //        </dependency>
      //         <dependency>
      //          <groupId>commons-io</groupId>
      //          <artifactId>commons-io</artifactId>
      //          <version>2.0</version>
      //
      //  2.  The EXPECTED_CHECKSUM values are for this version only.
      //  3.  The two files assigned to variable bzipDecompressedFilename exists.
      //  4.  The decompression and reading of the compressed file for checksum may take couple of minutes.
      //      This test ran by the developer took 2 minutes and 23 seconds on lapinta development machine.:
      //
      //         common/common-api 132 % mvn -Dtest=BzipUtilityTest test
      //
      //         Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
      //
      //         [INFO] ------------------------------------------------------------------------
      //         [INFO] BUILD SUCCESS
      //         [INFO] ------------------------------------------------------------------------
      //         [INFO] Total time: 2:23.417s
      //         [INFO] Finished at: Thu Dec 16 10:56:24 PST 2010
      //         [INFO] Final Memory: 12M/300M
      //         [INFO] ------------------------------------------------------------------------


      String bzipDecompressedFilename = null;
      String bzipCompressedFilename = null;

      // Compress file 1.

      bzipDecompressedFilename = System.getProperty("common.test.path") + File.separator + "ascat_20100506_231201_metopa_18402_eps_o_250_1019_ovw.l2.nc";
      bzipCompressedFilename = System.getProperty("common.test.path") + File.separator + "ascat_20100506_231201_metopa_18402_eps_o_250_1019_ovw.l2.nc.bz2";

      try {
         BzipUtility.compress(new FileInputStream(bzipDecompressedFilename), new FileOutputStream(bzipCompressedFilename));

// The checksum of the newly created BZIP2 file should be 3f5223e54460ac0821773f12f4489f4f
         String EXPECTED_CHECKSUM = "3f5223e54460ac0821773f12f4489f4f";

         String resultedChecksum = ChecksumUtility.getDigest(DigestAlgorithm.MD5, new File(bzipCompressedFilename));

         System.out.println("BzipUtilityTest:EXPECTED_CHECKSUM [" + EXPECTED_CHECKSUM + "]");
         System.out.println("BzipUtilityTest:resultedChecksum  [" + resultedChecksum + "]");

         if (resultedChecksum.equals(EXPECTED_CHECKSUM)) {
            assertTrue(true);
         } else {
            assertTrue(false);
         }

      } catch (IOException an_exception) {
         an_exception.printStackTrace();
         assertTrue(false);
      }
      assertTrue(true);

      // To skip the next test, set to 2 == 2.
      // To include the next test, set to 2 == 3
      if (2 == 3) {
         return;
      }

      // Compress file 2.

      bzipDecompressedFilename = System.getProperty("common.test.path") + File.separator + "ascat_20100912_metopa_eps_o_250_ovw.l3.nc";
      bzipCompressedFilename = System.getProperty("common.test.path") + File.separator + "ascat_20100912_metopa_eps_o_250_ovw.l3.nc.bz2";

      try {
         BzipUtility.compress(new FileInputStream(bzipDecompressedFilename), new FileOutputStream(bzipCompressedFilename));

// The checksum of the newly created BZIP2 file should be 930fe1716881d03018da94fff7da7f93

         String EXPECTED_CHECKSUM = "930fe1716881d03018da94fff7da7f93";

         String resultedChecksum = ChecksumUtility.getDigest(DigestAlgorithm.MD5, new File(bzipCompressedFilename));

         System.out.println("BzipUtilityTest:EXPECTED_CHECKSUM [" + EXPECTED_CHECKSUM + "]");
         System.out.println("BzipUtilityTest:resultedChecksum  [" + resultedChecksum + "]");

         if (resultedChecksum.equals(EXPECTED_CHECKSUM)) {
            assertTrue(true);
         } else {
            assertTrue(false);
         }
      } catch (IOException an_exception) {
         an_exception.printStackTrace();
         assertTrue(false);
      }
      assertTrue(true);
   }
}
