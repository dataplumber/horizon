package gov.nasa.horizon.common.api;

import gov.nasa.horizon.common.api.util.StringUtility;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StringUtilTest {

   @Before
   public void setup() {

   }

   @Test
   public void testStringUtil() {
      System.out.println("Testing String Utility.");
      System.out.println("Test 1");
      String response = StringUtility.cleanPaths("file://rootpath", "relpath", "filename");
      System.out.println(response);
      assertTrue(response.equals("file://rootpath/relpath/filename"));

      System.out.println("Test 2");
      response = StringUtility.cleanPaths("file://rootpath", null, "filename");
      System.out.println(response);
      assertTrue(response.equals("file://rootpath/filename"));

      System.out.println("Test 3");
      response = StringUtility.cleanPaths(null, "relpath", "filename");
      System.out.println(response);
      assertTrue(response.equals("relpath/filename"));

      System.out.println("Test 4");
      response = StringUtility.cleanPaths("file://rootpath", "relpath", null);
      System.out.println(response);
      assertTrue(response.equals("file://rootpath/relpath"));

      System.out.println("Test 5");
      response = StringUtility.cleanPaths(null, null, null);
      System.out.println(response);
      assertTrue(response == null);


   }
}
