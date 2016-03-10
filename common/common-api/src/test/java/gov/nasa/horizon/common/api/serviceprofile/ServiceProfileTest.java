/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * A collection of test for ServiceProfile creation and manipulation.
 *
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $$Id: ServiceProfileTest.java 644 2008-02-27 19:05:04Z thuang $$
 */
public class ServiceProfileTest {
   private static final String _SIP_FILE_NAME = "podaac-ingest-example01.xml";
   private static final String _SPM_FILE_NAME = "spa.0.testbed";

   private String _rootPath = "";

   private String _fileToString(String filename) throws IOException {
      FileInputStream input = null;
      try {
         input = new FileInputStream(new File(filename));
         FileChannel channel = input.getChannel();
         MappedByteBuffer buffer =
               channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
         Charset charset = Charset.forName("ISO-8859-1");
         CharsetDecoder decoder = charset.newDecoder();
         CharBuffer charBuffer = decoder.decode(buffer);
         return charBuffer.toString();
      } finally {
         if (input != null)
            input.close();
      }
   }

   @Before
   public void setup() {
      if (System.getProperty("common.test.path") != null)
         this._rootPath =
               System.getProperty("common.test.path") + File.separator;
   }

   /**
    * Test for updating communication agent information. This test uses GHRSST
    * MODIS SIP as input.
    */
   @Test
   public void testAgentUpdate() {
      try {

         ServiceProfileFactory spf = ServiceProfileFactory.getInstance();
         ServiceProfile serviceProfile = null;

         try {
            serviceProfile =
                  spf.createServiceProfileFromMessage(new File(this._rootPath
                        + ServiceProfileTest._SIP_FILE_NAME));

            assertNotNull(serviceProfile);
         } catch (Exception exception) {
            exception.printStackTrace();
            fail("Failed to create ServiceProfile object.");
         }

         SPSubmission submission = serviceProfile.getSubmission();
         assertNotNull(submission);
         SPIngest ingest = submission.getIngest();
         assertNotNull(ingest);

         SPHeader header = submission.getHeader();
         assertNotNull(header);

         List<SPOperation> operations = header.getOperations();
         SPOperation request = null;
         for (SPOperation operation : operations) {
            if (operation.getAgent().equals("GHRSST_HANDLER")) {
               if (operation.getOperation().equals("REQUEST")) {
                  request = operation;
               }
            }
         }

         if (request == null) {
            return;
         }


         System.out.println("requestedTime: "
               + request.getOperationStartTime().toString());
         request.setOperationStartTime(new Date());

         for (SPNotification notification : submission.getNotifications()) {
            System.out.println(notification);
         }

         System.out.println("\n");

         // add another notification
         SPNotification nt = submission.createNotification();
         nt.setLastName("Smith");
         nt.setFirstName("John");
         nt.setEmail("donotreply@jpl.nasa.gov");
         nt.setMessageLevel(SPCommon.SPMessageLevel.VERBOSE);

         submission.addNotification(nt);

         for (SPNotification notification : submission.getNotifications()) {
            System.out.println(notification);
         }

         // System.out.println(serviceProfile.toXML());
      } catch (Exception exception) {
         exception.printStackTrace();
         Assert.fail();
      }
   }

   /**
    * Testing the new ServiceProfile API to create a ServiceProfile using simple
    * API calls without any input data files.
    */
   @Test
   public void testBootstrapServiceProfile() {
      try {
         ServiceProfile sp =
               ServiceProfileFactory.getInstance().createServiceProfile();
         assertNotNull(sp);

         sp.setSubmisson(sp.createSubmission());

         SPAgent origin = sp.createAgent();
         origin.setName("qchau@sipsubmit");
         origin.setAddress(InetAddress.getByName("127.0.0.1"));
         origin.setTime(1192147092433L);
         sp.setMessageOriginAgent(origin);

         SPAgent target = sp.createAgent();
         target.setName("QSCAT:QSCAT_2");
         target.setAddress(InetAddress.getByName("127.0.0.1"));
         target.setTime(1192147092455L);
         sp.setMessageTargetAgent(target);

         SPSubmission submission = sp.getSubmission();
         SPIngest ingest = submission.createIngest();
         assertNotNull(ingest);
         submission.setIngest(ingest);

         SPHeader header = submission.createHeader();
         submission.setHeader(header);

         SPMetadata metadata = submission.createMetadata();
         submission.setMetadata(metadata);

         header.setProductType("JPL-L2P-MODIS_A");

         metadata.setBatch("1192145906-4");

         SPOperation operation = header.createOperation();
         operation.setAgent("GHRSST_HANDLER");
         operation.setOperation("REQUEST");
         operation.setOperationStartTime(1192147092397L);
         operation.setOperationStopTime(1192147095460L);
         header.addOperation(operation);

         header.setSubmissionId(3);

         metadata.setComment("Quicklook MODIS L2P created at the GHRSST Global Data Assembly Center (GDAC), Wed Aug 1 12:15:39 2007");

         SPNotification notification = submission.createNotification();
         notification.setLastName("Huang");
         notification.setFirstName("Thomas");
         notification.setEmail("Thomas.Huang@jpl.nasa.gov");
         submission.addNotification(notification);

         header.setProductName("20070801-MODIS_A-JPL-L2P-A2007213074500" +
               ".L2_LAC_GHRSST-v01");
         header.setProductId(1L);
         metadata.setProductStartTime(1185954308000L);
         metadata.setProductStopTime(1185954606000L);

         SPBoundingRectangle rectangle = metadata.createBoundingRectangle();
         metadata.addBoundingRectangle(rectangle);
         rectangle.setWestLongitude(76.309);
         rectangle.setNorthLatitude(-1.600);
         rectangle.setEastLongitude(101.738);
         rectangle.setSouthLatitude(-22.541);

         header.setCreateTime(1185970528000L);
         header.setVersion("0");

         SPIngestProductFile ipf = ingest.createIngestProductFile();
         ingest.addIngestProductFile(ipf);
         SPProductFile pf = ipf.createProductFile();
         ipf.setProductFile(pf);
         SPFile file = pf.createFile();
         pf.setFile(file);
         file.setDataFormat(SPCommon.SPDataFormat.NETCDF);
         file.addLink("ftp://seastorm.jpl.nasa.gov/home/qchau/mmr_drop/MODIS_A/2007/213/20070801-MODIS_A-JPL-L2P-A2007213074500.L2_LAC_GHRSST-v01.nc.bz2");
         file.setCompressionType(SPCommon.SPCompressionAlgorithm.BZIP2);
         file.setChecksumValue("7f87aa1f9f29662f992b657f5a54e1");
         file.setChecksumType(SPCommon.SPChecksumAlgorithm.MD5);
         System.out.println(file.getChecksumType());

         SPFileDestination fd = ipf.createFileDestination();
         ipf.setFileDestination(fd);
         fd.setLocation("file:///data/dev/users/thuang/dev/horizon_lapinta/staging/2007/10/11/213/20070801-MODIS_A-JPL-L2P-A2007213074500.L2_LAC_GHRSST-v01.nc.bz2");

         operation = header.createOperation();
         header.addOperation(operation);
         operation.setAgent("INGEST_ENGINE");
         operation.setOperation("INGEST");
         operation.setOperationStartTime(1192147092631L);
         operation.setOperationStopTime(1192147095458L);
      } catch (ServiceProfileException e) {
         e.printStackTrace();
      } catch (UnknownHostException e) {
         e.printStackTrace();
      }
   }

   /**
    * Test to quickly verify creation of a ServiceProfile object from an input
    * SIP file.
    */
   @Test
   public void testSIPFileTranslation() {
      try {
         ServiceProfile serviceProfile =
               ServiceProfileFactory.getInstance()
                     .createServiceProfileFromMessage(
                           new File(this._rootPath
                                 + ServiceProfileTest._SIP_FILE_NAME));
         assertNotNull(serviceProfile);
      } catch (ServiceProfileException e) {
         fail(e.getMessage());
      } catch (IOException e) {
         fail(e.getMessage());
      }
   }

   /**
    * Test to quickly verify creation of a ServiceProfile object from an input
    * text string.
    */
   @Test
   public void testSIPStringTranslation() {
      try {
         String buffer =
               this._fileToString(this._rootPath
                     + ServiceProfileTest._SIP_FILE_NAME);
         assertNotNull(buffer);
         ServiceProfile serviceProfile =
               ServiceProfileFactory.getInstance()
                     .createServiceProfileFromMessage(buffer);
         assertNotNull(serviceProfile);
      } catch (ServiceProfileException e) {
         fail(e.getMessage());
      } catch (IOException e) {
         fail(e.getMessage());
      }
   }
}
