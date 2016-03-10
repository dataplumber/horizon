package gov.nasa.horizon.common.api;

import gov.nasa.horizon.common.api.metadatamanifest.MetadataField;
import gov.nasa.horizon.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.horizon.common.api.metadatamanifest.MetadataManifestException;
import org.junit.Before;
import org.junit.Test;

public class MetadataManifestTest {

   @Before
   public void setup() {

   }

   @Test
   public void parseDatasetXml() {
      String xml = null;
      xml = "<dataset type=\"update\">\n" +
            "<field name=\"DATASET.SHORT_NAME\" required=\"true\" type=\"string\">VALUE1</field>\n" +
            "<field name=\"DATASET_COVERAGE.WEST_LONG\" required=\"true\" type=\"real\">-55.91</field>\n" +
            "</dataset>\n";

      try {
         MetadataManifest mf = new MetadataManifest(xml);
         System.out.println("Manifest Object: " + mf.getObjectType().toString());
         System.out.println("Manifest Type: " + mf.getActionType().toString());
         System.out.println("Manifest XML: " + mf.getManifest());

         System.out.println("Listing of Fields:Type:Values");
         for (MetadataField m : mf.getFields()) {
            System.out.println(m.getName() + ":" + m.getType() + ":" + m.getValue());
         }
      } catch (MetadataManifestException e) {
         System.out.println("Error parsing dataset: " + e.getMessage());
         e.printStackTrace();

         assert (false);
      }
   }
}
