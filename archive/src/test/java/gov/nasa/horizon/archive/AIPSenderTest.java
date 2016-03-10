//package gov.nasa.horizon.archive;
//
//import gov.nasa.horizon.archive.core.ArchiveProperty;
//import gov.nasa.horizon.archive.core.JMSSession;
//import gov.nasa.horizon.archive.external.FileUtil;
//import gov.nasa.horizon.common.api.serviceprofile.Agent;
//import gov.nasa.horizon.common.api.serviceprofile.BasicFileInfo;
//import gov.nasa.horizon.common.api.serviceprofile.BoundingRectangle;
//import gov.nasa.horizon.common.api.serviceprofile.CompleteContent;
//import gov.nasa.horizon.common.api.serviceprofile.Granule;
//import gov.nasa.horizon.common.api.serviceprofile.GranuleFile;
//import gov.nasa.horizon.common.api.serviceprofile.GranuleHistory;
//import gov.nasa.horizon.common.api.serviceprofile.IngestDetails;
//import gov.nasa.horizon.common.api.serviceprofile.IngestProfile;
//import gov.nasa.horizon.common.api.serviceprofile.Notification;
//import gov.nasa.horizon.common.api.serviceprofile.ProductProfile;
//import gov.nasa.horizon.common.api.serviceprofile.ServiceProfile;
//import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileException;
//import gov.nasa.horizon.common.api.serviceprofile.ServiceProfileFactory;
//import gov.nasa.horizon.common.api.serviceprofile.SubmissionHeader;
//import gov.nasa.horizon.common.api.serviceprofile.Common.ChecksumAlgorithm;
//import gov.nasa.horizon.common.api.serviceprofile.Common.FileClass;
//import gov.nasa.horizon.common.api.serviceprofile.Common.MessageLevel;
//import gov.nasa.horizon.common.api.serviceprofile.Common.Status;
//import gov.nasa.horizon.common.api.util.ChecksumUtility;
//import gov.nasa.horizon.inventory.api.DataManager;
//import gov.nasa.horizon.inventory.api.DataManagerFactory;
//import gov.nasa.horizon.inventory.api.Inventory;
//import gov.nasa.horizon.inventory.api.InventoryFactory;
//import gov.nasa.horizon.inventory.api.Query;
//import gov.nasa.horizon.inventory.api.QueryFactory;
//import gov.nasa.horizon.inventory.api.Constant.AccessType;
//import gov.nasa.horizon.inventory.api.Constant.AppendBasePathType;
//import gov.nasa.horizon.inventory.api.Constant.LocationPolicyType;
//import gov.nasa.horizon.inventory.exceptions.InventoryException;
//import gov.nasa.horizon.inventory.model.Dataset;
//import gov.nasa.horizon.inventory.model.DatasetCoverage;
//import gov.nasa.horizon.inventory.model.DatasetLocationPolicy;
//import gov.nasa.horizon.inventory.model.DatasetPolicy;
//import gov.nasa.horizon.inventory.model.Provider;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.URI;
//import java.net.UnknownHostException;
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.Properties;
//import java.util.Set;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//
///**
// * This class creates test AIP based on test.properties and publishes it onto JMS aip/post.
// *
// * @author clwong
// *
// * @version
// */
//public class AIPSenderTest extends TestCase {
//	
//	private static Dataset ds = null;
//	private static Query q = null;
//
//	@Before
//	public void setUp() {
//		ArchiveProperty.getInstance();
//		Properties p = new Properties();
//		try {
//			p.load(new FileInputStream("test.properties"));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Enumeration propNames = p.propertyNames();
//		while (propNames.hasMoreElements()) {
//			String name = propNames.nextElement().toString();
//			System.setProperty(name, p.getProperty(name));
//		}
//
//		// ---------- setup a test dataset ------------------
//		long time = System.currentTimeMillis();
//		// test dataset's existence
//		q = QueryFactory.getInstance().createQuery();
//		ds = q.findDatasetByShortName("ARCHIVE-TEST-DATASET");
//		if (ds == null) {
//			// no such dataset and create one with test data
//			// for other fields
//			ds = new Dataset();
//			ds.setShortName("ARCHIVE-TEST-DATASET");
//			ds.setLongName("ARCHIVE-TEST-DATASET-"+time);
//			ds.setProcessingLevel("2P");
//			ds.setTemporalResolution("Test temporal");
//			ds.setLatitudeResolution(180.0);
//			ds.setLongitudeResolution(90.0);
//			System.out.println("New dataset name="+ds.getShortName());
//		} else {
//			ds = q.fetchDatasetById(ds.getDatasetId(), "provider", "locationPolicySet");
//		}
//		Provider pr = ds.getProvider();
//		if (pr == null || pr.getShortName() == null) {
//			pr = q.findProviderByShortName("CLWONG");
//			if (pr==null) {
//				pr = new Provider();
//				pr.setShortName("CLWONG");
//				pr.setLongName("Cynthia L. Wong");
//				pr.setType("ARCHIVE-TEST");
//				System.out.println("New provider name="+pr.getShortName());
//			}				
//			ds.setProvider(pr);
//		}
//		Set<DatasetLocationPolicy> locationPolicySet = ds
//				.getLocationPolicySet();
//		if (locationPolicySet == null || locationPolicySet.isEmpty()) {
//			String basePath = System.getProperty("archive.base.path")+"/"+time;
//			File basePathFile = new File(basePath);
//			basePathFile.mkdir();
//			DatasetLocationPolicy locationPolicy = new DatasetLocationPolicy();
//			locationPolicy.setBasePath("file://"+basePath);
//			locationPolicy.setType(LocationPolicyType.ARCHIVE_PUBLIC.toString());
//			ds.add(locationPolicy);
//			locationPolicy = new DatasetLocationPolicy();
//			locationPolicy.setBasePath("file://"+basePath);
//			locationPolicy.setType(LocationPolicyType.ARCHIVE_PRIVATE.toString());
//			ds.add(locationPolicy);
//			locationPolicy = new DatasetLocationPolicy();
//			locationPolicy.setBasePath("file://"+basePath);
//			locationPolicy.setType(LocationPolicyType.ARCHIVE_RESTRICTED.toString());
//			ds.add(locationPolicy);
//			System.out.println("New location policy="+locationPolicy.getBasePath());
//			locationPolicy = new DatasetLocationPolicy();
//			locationPolicy.setBasePath("ftp://localhost"+basePath);
//			locationPolicy.setType(LocationPolicyType.LOCAL_FTP.toString());
//			ds.add(locationPolicy);
//			locationPolicy = new DatasetLocationPolicy();
//			locationPolicy.setBasePath("http://localhost"+basePath);
//			locationPolicy.setType(LocationPolicyType.LOCAL_OPENDAP.toString());
//			ds.add(locationPolicy);
//			locationPolicy = new DatasetLocationPolicy();
//			//locationPolicy.setBasePath("sftp://lapinta.jpl.nasa.gov/home/clwong/horizon/testData/remote");
//			locationPolicy.setBasePath(System.getProperty("remote.archive"));
//			locationPolicy.setType(LocationPolicyType.REMOTE_FTP.toString());
//			ds.add(locationPolicy);
//			locationPolicy = new DatasetLocationPolicy();
//			locationPolicy.setBasePath("http://lapinta.jpl.nasa.gov/home/clwong/horizon/testData/remote");
//			locationPolicy.setType(LocationPolicyType.REMOTE_OPENDAP.toString());
//			ds.add(locationPolicy);
//		}
//		
//		DatasetPolicy policy = null;
//		Dataset dsPolicy = q.fetchDatasetById(ds.getDatasetId(), "datasetPolicy");
//		if (dsPolicy != null) policy = dsPolicy.getDatasetPolicy();
//		else {
//			if (policy==null) policy = new DatasetPolicy();
//			policy.setAccessType(AccessType.PRIVATE.toString());
//			policy.setBasePathAppendType(AppendBasePathType.BATCH.toString());
//			policy.setChecksumType("MD5");
//			policy.setDataClass("ROLLING-STORE");
//			policy.setDataDuration(90);
//			policy.setDataFormat("DATA");
//			policy.setCompressType("GZIP");
//			policy.setSpatialType("NONE");	// ORACLE, BACKTRACK, or NONE
//			policy.setAccessConstraint("READ");
//			policy.setUseConstraint("READ");
//			policy.setDataset(ds);
//			ds.setDatasetPolicy(policy);
//			System.out.println("New dataset policy="+policy.getBasePathAppendType());
//		}
//		
//		DatasetCoverage coverage = null;
//		Dataset dsCoverage = q.fetchDatasetById(ds.getDatasetId(), "datasetCoverage");
//		if (dsCoverage != null) coverage = dsCoverage.getDatasetCoverage();
//		else {
//			if (coverage==null) coverage = new DatasetCoverage();
//			coverage.setEastLon(180.);
//			coverage.setWestLon(-180.);
//			coverage.setNorthLat(90.);
//			coverage.setSouthLat(-90.);
//			coverage.setStartTime(new Date());
//			coverage.setDataset(ds);
//			ds.setDatasetCoverage(coverage);
//			System.out.println("New dataset coverage start time="+coverage.getStartTime());
//		}
//
//		DataManager manager = DataManagerFactory.getInstance().createDataManager();
//		manager.addProvider(pr);
//		manager.addDatasetPolicy(policy);
//		manager.addDatasetCoverage(coverage);
//		manager.addDataset(ds);
//		System.out.println("DatasetId="+ds.getDatasetId());
//	}
//
//	/**
//	 * This is a unit test to send an AIP to Archive Subscriber.
//	 */
//	public void testSendAIP() {
//
//		ServiceProfile sp = null;
//		String aipFilename = System.getProperty("test.aipfile");
//		System.out.println(aipFilename);
//		long time = System.currentTimeMillis();
//
//		System.out.println("Generate SIP...");
//		try {
//			sp = ServiceProfileFactory.getInstance().createServiceProfile();
//			sp.setProductProfile(sp.createProductProfile());
//
//			Agent origin = sp.createAgent();
//			origin.setAgent("clwong@sipsubmit");
//			try {
//				origin.setAddress(InetAddress.getByName("127.0.0.1"));
//			} catch (UnknownHostException e1) {
//				e1.printStackTrace();
//			}
//			origin.setTime(time);
//			sp.setMessageOriginAgent(origin);
//
//			Agent target = sp.createAgent();
//			target.setAgent("ARCHIVE:ARCHIVE");
//			try {
//				target.setAddress(InetAddress.getByName("127.0.0.1"));
//			} catch (UnknownHostException e1) { 
//				e1.printStackTrace();
//			}
//			target.setTime(time);
//			sp.setMessageTargetAgent(target);
//
//			ProductProfile pp = sp.getProductProfile();
//			IngestProfile ip = sp.getProductProfile().createIngestProfile();
//			pp.setIngestProfile(ip);
//
//			SubmissionHeader header = ip.createHeader();
//			ip.setHeader(header);
//
//			CompleteContent content = ip.createCompleteContent();
//			ip.setCompleteContent(content);
//
//			header.setProject("ARCHIVE");
//			header.setBatch("2008");
//			header.setContributorEmail("Cynthia.Wong@jpl.nasa.gov");
//			header.setContributorMessageLevel(MessageLevel.VERBOSE);
//			header.setRequested(time);
//			header.setProcessStartTime(time);
//			header.setSubmissionId((new Date()).toString());
//			header.setStatus(Status.STAGED); // required
//			header.setComment("File created as ARCHIVE Test Data");
//
//			Notification notification = pp.createNotification();
//			notification.setLastName("Wong");
//			notification.setFirstName("Cynthia");
//			notification.setEmail("Cynthia.Wong@jpl.nasa.gov");
//			notification.setMessageLevel(MessageLevel.SILENT); // required
//			pp.addNotification(notification);
//
//			Integer numGranules = new Integer(System.getProperty("number.granules"));
//			long day = 24*3600*1000L;
//			
//			// Setup data files at staging
//			String localStaging = System.getProperty("local.staging");
//			String remoteStaging = System.getProperty("remote.staging");
//
//			String dataFilename = System.getProperty("source.data.filename");
//			long dataFilesize = 0;
//			String dataChecksum = null;
//			URI sourceDataURI = null;
//			URI dataLocalStaging = null;
//			URI dataRemoteStaging = null;
//			if (dataFilename != null) {
//				File dataFile = new File(dataFilename);
//				try {
//					sourceDataURI = new URI(dataFilename);
//					dataLocalStaging = new URI(localStaging+"/"+dataFile.getName());
//					/*
//					if (!(new File(dataLocalStaging)).exists()) {
//						System.out.println("local copying...");
//						FileUtil.copyFile(dataFile, new File(dataLocalStaging));
//					}
//					*/
//					dataRemoteStaging = new URI(remoteStaging+"/"+dataFile.getName());
//					FileUtil.getInstance();
//					if (!(FileUtil.resolveFile(dataRemoteStaging)).exists()) {
//						System.out.println("remote copying...");
//						FileUtil.copyFile(FileUtil.resolveFile(sourceDataURI),
//								FileUtil.resolveFile(dataRemoteStaging));
//					}
//					FileUtil.release();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				//dataFilesize = dataFile.length()+1; //create error in file size;
//				dataFilesize = dataFile.length();
//				dataChecksum = ChecksumUtility.getDigest(
//						ChecksumUtility.DigestAlgorithm.toAlgorithm("MD5"), dataFile);
//				System.out.println(dataFilename+" "+dataFilesize+" "+dataChecksum);
//			}
//
//			String replaceFilename = System.getProperty("replace.data.filename");
//			long replaceFilesize = 0;
//			String replaceChecksum = null;
//			URI replaceDataURI = null;
//			URI replaceLocalStaging = null;
//			URI replaceRemoteStaging = null;
//			if (replaceFilename != null) {
//				File replaceFile = new File(replaceFilename);
//				try {
//					replaceDataURI = new URI(replaceFilename);
//					replaceLocalStaging = new URI(localStaging+"/"+replaceFile.getName());
//					/*
//					if (!(new File(replaceLocalStaging)).exists()) {
//						System.out.println("local copying...");
//						FileUtil.copyFile(replaceFile, new File(replaceLocalStaging));
//					}*/
//					replaceRemoteStaging = new URI(remoteStaging+"/"+replaceFile.getName());
//					FileUtil.getInstance();
//					if (!(FileUtil.resolveFile(replaceRemoteStaging)).exists()) {
//						System.out.println("remote copying...");
//						FileUtil.copyFile(FileUtil.resolveFile(replaceDataURI),
//								FileUtil.resolveFile(replaceRemoteStaging));
//					}
//					FileUtil.release();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				//replaceFilesize = replaceFile.length()+1; //create error in file size;
//				replaceFilesize = replaceFile.length();
//				replaceChecksum = ChecksumUtility.getDigest(
//						ChecksumUtility.DigestAlgorithm.toAlgorithm("MD5"), replaceFile);
//				System.out.println(replaceFilename+" "+replaceFilesize+" "+replaceChecksum);
//			}
//			
//			String dataChecksumFilename = System.getProperty("source.checksum.data.filename");
//			long dataChecksumFilesize = 0;
//			String dataChecksumChecksum = null;
//			URI sourceDataChecksumURI = null;
//			URI dataChecksumLocalStaging = null;
//			URI dataChecksumRemoteStaging = null;
//			if (dataChecksumFilename != null) {
//				File dataChecksumFile = new File(dataChecksumFilename);
//				try {
//					sourceDataChecksumURI = new URI(dataChecksumFilename);
//					dataChecksumLocalStaging = new URI(localStaging+"/"+dataChecksumFile.getName());
//					/*if (!(new File(dataChecksumLocalStaging)).exists()) {
//						System.out.println("local copying...");
//						FileUtil.copyFile(
//								dataChecksumFile, 
//								new File(dataChecksumLocalStaging));
//					}*/
//					dataChecksumRemoteStaging = 
//						new URI(remoteStaging+"/"+dataChecksumFile.getName());
//					FileUtil.getInstance();
//					if (!(FileUtil.resolveFile(dataChecksumRemoteStaging)).exists()) {
//						System.out.println("remote copying...");
//						FileUtil.copyFile(FileUtil.resolveFile(sourceDataChecksumURI),
//								FileUtil.resolveFile(dataChecksumRemoteStaging));
//					}
//					FileUtil.release();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				dataChecksumFilesize = dataChecksumFile.length();
//				dataChecksumChecksum = ChecksumUtility.getDigest(
//						ChecksumUtility.DigestAlgorithm.toAlgorithm("MD5"), dataChecksumFile);
//				//dataChecksumChecksum = "cf40a1de3f93b4a025409b5efa5aa000"; // create checksum error
//				System.out.println(dataChecksumFilename+" "+dataChecksumFilesize+" "
//						+dataChecksumChecksum);
//			}
//
//			String replaceChecksumFilename = System.getProperty("replace.checksum.data.filename");
//			long replaceChecksumFilesize = 0;
//			String replaceChecksumChecksum = null;
//			URI replaceDataChecksumURI = null;
//			URI replaceChecksumLocalStaging = null;
//			URI replaceChecksumRemoteStaging = null;
//			if (replaceChecksumFilename != null) {
//				File replaceChecksumFile = new File(replaceChecksumFilename);
//				try {
//					replaceDataChecksumURI = new URI(replaceChecksumFilename);
//					replaceChecksumLocalStaging = new URI(localStaging+"/"+replaceChecksumFile.getName());
//					/*if (!(new File(replaceChecksumLocalStaging)).exists()) {
//						System.out.println("local copying...");
//						FileUtil.copyFile(
//								replaceChecksumFile, 
//								new File(replaceChecksumLocalStaging));
//					}*/
//					replaceChecksumRemoteStaging = 
//						new URI(remoteStaging+"/"+replaceChecksumFile.getName());
//					FileUtil.getInstance();
//					if (!(FileUtil.resolveFile(replaceChecksumRemoteStaging)).exists()) {
//						System.out.println("remote copying...");
//						FileUtil.copyFile(FileUtil.resolveFile(replaceDataChecksumURI),
//								FileUtil.resolveFile(replaceChecksumRemoteStaging));
//					}
//					FileUtil.release();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				replaceChecksumFilesize = replaceChecksumFile.length();
//				replaceChecksumChecksum = ChecksumUtility.getDigest(
//						ChecksumUtility.DigestAlgorithm.toAlgorithm("MD5"), replaceChecksumFile);
//				//replaceChecksumChecksum = "cf40a1de3f93b4a025409b5efa5aa000"; // create checksum error
//				System.out.println(replaceChecksumFilename+" "+replaceChecksumFilesize+" "
//						+replaceChecksumChecksum);
//			}
//
//			String metaFilename = System.getProperty("source.meta.filename");
//			long metaFilesize = 0;
//			String metaChecksum = null;
//			URI sourceMetaURI = null;
//			URI metaLocalStaging = null;
//			URI metaRemoteStaging = null;
//			if (metaFilename != null) {
//				File metaFile = new File(metaFilename);
//				try {
//					sourceMetaURI = new URI(metaFilename);
//					metaLocalStaging = new URI(localStaging+"/"+metaFile.getName());
//					/*if (!(new File(metaLocalStaging)).exists()) {
//						System.out.println("local copying...");
//						FileUtil.copyFile(metaFile, new File(metaLocalStaging));
//					}*/
//					metaRemoteStaging = new URI(remoteStaging+"/"+metaFile.getName());
//					FileUtil.getInstance();
//					if (!(FileUtil.resolveFile(metaRemoteStaging)).exists()) {
//						System.out.println("remote copying...");
//						FileUtil.copyFile(FileUtil.resolveFile(sourceMetaURI),
//								FileUtil.resolveFile(metaRemoteStaging));
//					}
//					FileUtil.release();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				metaFilesize = metaFile.length();
//				metaChecksum = ChecksumUtility.getDigest(
//						ChecksumUtility.DigestAlgorithm.toAlgorithm("MD5"), metaFile);
//				System.out.println(metaFilename+" "+metaFilesize+" "+metaChecksum);
//			}
//			
//			String metaChecksumFilename = System.getProperty("source.checksum.meta.filename");
//			long metaChecksumFilesize = 0;
//			String metaChecksumChecksum = null;
//			URI sourceMetaChecksumURI = null;
//			URI metaChecksumLocalStaging = null;
//			URI metaChecksumRemoteStaging = null;
//			if (metaChecksumFilename != null) {
//				File metaChecksumFile = new File(metaChecksumFilename);
//				try {
//					sourceMetaChecksumURI = new URI(metaChecksumFilename);
//					metaChecksumLocalStaging = new URI(localStaging+"/"+metaChecksumFile.getName());
//					/*if (!(new File(metaChecksumLocalStaging)).exists()) {
//						System.out.println("local copying...");
//						FileUtil.copyFile(
//								metaChecksumFile, 
//								new File(metaChecksumLocalStaging));
//					}*/
//					metaChecksumRemoteStaging = 
//						new URI(remoteStaging+"/"+metaChecksumFile.getName());					
//					FileUtil.getInstance();
//					if (!(FileUtil.resolveFile(metaChecksumRemoteStaging)).exists()) {
//						System.out.println("remote copying...");
//						FileUtil.copyFile(FileUtil.resolveFile(sourceMetaChecksumURI),
//								FileUtil.resolveFile(metaChecksumRemoteStaging));
//					}
//					FileUtil.release();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				metaChecksumFilesize = metaChecksumFile.length();
//				metaChecksumChecksum = ChecksumUtility.getDigest(
//						ChecksumUtility.DigestAlgorithm.toAlgorithm("MD5"), metaChecksumFile);
//				System.out.println(metaChecksumFilename+" "+metaChecksumFilesize+" "
//						+metaChecksumChecksum);
//			}
//
//			System.out.println("Creating test metadata for "+numGranules+" granules...");
//			for (int i = 1; i <= numGranules; i++) {
//				Granule granule = content.createGranule();
//				content.addGranule(granule);
//
//				granule.setCreateTime(time-i*day);
//				granule.setName("ARCHIVE-TEST-GRANULE-" + i);
//				granule.setDatasetName("ARCHIVE-TEST-DATASET");
//				granule.setProductType("ARCHIVE-TEST-PRODUCT");
//				granule.setId(1L);
//				granule.setTemporalCoverageStartTime(time-i*day);
//				granule.setTemporalCoverageStopTime(time-i*day);
//
//				BoundingRectangle rectangle = granule.createBoundingRectangle();
//				rectangle.setWestLongitude(76.309);
//				rectangle.setNorthLatitude(-1.600);
//				rectangle.setEastLongitude(101.738);
//				rectangle.setSouthLatitude(-22.541);
//				granule.setBoundingRectangle(rectangle);
//
//				granule.setVersion("1");
//				
//				GranuleHistory history = granule.createGranuleHistory();
//				history.setVersion("1.0");
//				history.setCreateDate(new Date());
//				history.setLastRevisionDate(new Date());
//				history.setRevisionHistory("revision history");
//				granule.setGranuleHistory(history);
//
//				// test replacement
//				gov.nasa.horizon.inventory.model.Granule g = 
//								q.findGranule(granule.getName(), ds);
//				if (g != null) {
//					System.out.println("Replacement...");
//					granule.setReplace(granule.getName());
//					granule.setName(granule.getName());			// test with same granule name
//					//granule.setName(granule.getName()+"-R");
//					
//					dataFilename = replaceFilename;
//					dataFilesize = replaceFilesize;
//					dataChecksum = replaceChecksum;
//					sourceDataURI = replaceDataURI;
//					dataLocalStaging = replaceLocalStaging;
//					dataRemoteStaging = replaceRemoteStaging;
//
//					dataChecksumFilename = replaceChecksumFilename;
//					dataChecksumFilesize = replaceChecksumFilesize;
//					dataChecksumChecksum = replaceChecksumChecksum;
//					sourceDataChecksumURI = replaceDataChecksumURI;
//					dataChecksumLocalStaging = replaceChecksumLocalStaging;
//					dataChecksumRemoteStaging = replaceChecksumRemoteStaging;
//				}
//
//				if (dataFilename != null) {
//					GranuleFile file = granule.createFile();
//					granule.addFile(file);
//
//					BasicFileInfo source = file.createSource();
//					file.setName("ARCHIVE-TEST-DATA");
//					file.setType(FileClass.DATA);
//					file.addSource(source); 
//					source.setLink(sourceDataURI);
//					//source.setCompressionAlgorithm(CompressionAlgorithm.BZIP2);
//					source.setSize(dataFilesize);
//					source.setChecksum(dataChecksum);
//					source.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//
//					IngestDetails details = file.createIngestDetails();
//					details.setIngestStartTime(System.currentTimeMillis()-100*day);
//					file.setIngestDetails(details);
//					details.setSource(source);
//					details.setLocalStaging(dataLocalStaging);
//					details.setRemoteStaging(dataRemoteStaging);
//					details.setSize(dataFilesize);
//					details.setChecksum(dataChecksum);
//					details.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//					details.setIngestStopTime(System.currentTimeMillis());
//				}// if dataFilename
//
//				if (dataChecksumFilename != null) {
//					GranuleFile file = granule.createFile();
//					granule.addFile(file);
//
//					BasicFileInfo source = file.createSource();
//					file.setName("ARCHIVE-TEST-DATA-MD5");
//					file.setType(FileClass.CHECKSUM);
//					file.addSource(source);
//					source.setLink(sourceDataChecksumURI);
//					//source.setCompressionAlgorithm(CompressionAlgorithm.BZIP2);
//					source.setSize(dataChecksumFilesize);
//					source.setChecksum(dataChecksumChecksum);
//					source.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//
//					IngestDetails details = file.createIngestDetails();
//					details.setIngestStartTime(System.currentTimeMillis()-100*day);
//					file.setIngestDetails(details);
//					details.setSource(source);
//					details.setLocalStaging(dataChecksumLocalStaging);
//					details.setRemoteStaging(dataChecksumRemoteStaging);
//					details.setSize(dataChecksumFilesize);
//					details.setChecksum(dataChecksumChecksum);
//					details.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//					details.setIngestStopTime(System.currentTimeMillis());
//				}// if checksumFilename
//
//				if (metaFilename != null) {
//					GranuleFile file = granule.createFile();
//					granule.addFile(file);
//
//					BasicFileInfo source = file.createSource();
//					file.setName("ARCHIVE-TEST-META");
//					file.setType(FileClass.METADATA);
//					file.addSource(source); 
//					source.setLink(sourceMetaURI);
//					//source.setCompressionAlgorithm(CompressionAlgorithm.BZIP2);
//					source.setSize(metaFilesize);
//					source.setChecksum(metaChecksum);
//					source.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//
//					IngestDetails details = file.createIngestDetails();
//					details.setIngestStartTime(System.currentTimeMillis()-100*day);
//					file.setIngestDetails(details);
//					details.setSource(source);
//					details.setLocalStaging(metaLocalStaging);
//					details.setRemoteStaging(metaRemoteStaging);
//					details.setSize(metaFilesize);
//					details.setChecksum(metaChecksum);
//					details.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//					details.setIngestStopTime(System.currentTimeMillis());
//				}// if metadataFilename
//
//				if (metaChecksumFilename != null) {
//					GranuleFile file = granule.createFile();
//					granule.addFile(file);
//
//					BasicFileInfo source = file.createSource();
//					file.setName("ARCHIVE-TEST-META-MD5");
//					file.setType(FileClass.CHECKSUM);
//					file.addSource(source);
//					source.setLink(sourceMetaChecksumURI);
//					//source.setCompressionAlgorithm(CompressionAlgorithm.BZIP2);
//					source.setSize(metaChecksumFilesize);
//					source.setChecksum(metaChecksumChecksum);
//					source.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//
//					IngestDetails details = file.createIngestDetails();
//					details.setIngestStartTime(System.currentTimeMillis()-100*day);
//					file.setIngestDetails(details);
//					details.setSource(source);
//					details.setLocalStaging(metaChecksumLocalStaging);
//					details.setRemoteStaging(metaChecksumRemoteStaging);
//					details.setSize(metaChecksumFilesize);
//					details.setChecksum(metaChecksumChecksum);
//					details.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
//					details.setIngestStopTime(System.currentTimeMillis());
//				}// if checksumFilename
//
//			} // for i loop
//			header.setProcessStopTime(System.currentTimeMillis());
//
//			try {
//				sp.toFile(aipFilename+".xml");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} catch (ServiceProfileException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("Generate AIP...");
//		try {
//			Inventory inventory = InventoryFactory.getInstance()
//					.createInventory();
//			inventory.storeServiceProfile(sp);
//		} catch (InventoryException e) {
//			e.printStackTrace();
//			fail("InventoryException: "+e.getMessage());
//		}
//
//		// Compose ByteMessage
//		System.out.println("Publish message...");
//		try {
//			sp.toFile(aipFilename + ".aip.xml");
//		} catch (ServiceProfileException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		try {
//			JMSSession jms = new JMSSession(ArchiveProperty.AIP_POST);
//			jms.publish(sp.toString());			
//			jms.disconnect();
//		} catch (Exception e) {
//			System.getProperties().list(System.out);
//			e.printStackTrace();
//		}
//		System.out.println("Test Complete - aip sent");
//	}
//}
