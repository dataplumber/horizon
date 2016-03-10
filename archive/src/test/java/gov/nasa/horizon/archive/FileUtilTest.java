//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive;

import gov.nasa.horizon.archive.core.AIP;
import gov.nasa.horizon.archive.core.ArchiveData;
import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.archive.external.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Before;
import org.junit.Test;


/**
 * This class tests file utilities including VFS and FileUtils.
 * @author clwong
 * @version
 * $Id: FileUtilTest.java 10123 2012-06-07 15:31:38Z gangl $
 */
public class FileUtilTest extends TestCase {

	@Before
    public void setUp() {
        ArchiveProperty.getInstance();
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("test.properties" ));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Enumeration propNames = p.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = propNames.nextElement().toString();
			System.setProperty(name, p.getProperty(name));
		}
    }
	
	public void createTestFile(){
		File f = new File("/data/archive/testFile");
		try {
			FileWriter outFile = new FileWriter(f);
			outFile.write("testFile");
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOverwrite(){
		String from = "/data/archive/testFile";
		String to = "/data/archive/overwrite"; 
		
		File f = new File(to);
		f.delete();
		
		
		createTestFile();
		boolean succ = false;
		succ = ArchiveData.rename(from, to, false);
		assertEquals(succ,true);
		
		//should fail
		createTestFile();
		succ = false;
		succ = ArchiveData.rename(from,to,false);
		assertEquals(succ,false);
		
		createTestFile();
		succ = false;
		succ = ArchiveData.rename(from,to,true);
		assertEquals(succ,true);
		
		f = new File(to);
		f.delete();
		
	}
	
//	@Test
//	public void testDeletePartition(){
//		try {
//			FileUtil.getInstance();
//			FileUtil.removeFile("/data/dev/gangl/deleteTestFile.txt");
//			
//		} catch (ArchiveException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * This is a unit test to retrieve data from remote site.
//	 */
//	@Test
//	public void testCopyRemoteFile() {
//		try {
//			FileUtil.getInstance();	
//			AIP aip = new AIP();
//			aip.setStaging(new URI(System.getProperty("source.meta.filename")));
//			aip.setDestination(new URI("file:///tmp/archiveTempFile"));
//			FileUtil.copyFile(aip.getStaging(), aip.getDestination());
//			assertTrue(ArchiveData.verify(aip));		
//        	FileUtil.removeFile(aip.getDestination());
//        	FileUtil.release();
//		} catch (ArchiveException e) {
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * This is a unit test to travese Archive Location for a file pattern of
//	 * ${archive_location}/YYYY/DOY/*.bz2
//	 */
//	@Test
//	public void testArchiveLocation() {
//		URI rootURI = null;
//		try {
//			rootURI = new URI(System.getProperty("archive.base.path"));
//		} catch (URISyntaxException e1) {
//			e1.printStackTrace();
//		}
//		String filePatternString = ".*/\\d{4}/\\d{3}/.*\\.bz2";
//		//String filePatternString = ".*/\\d{4}/.*\\.bz2";
//		try {
//			FileUtil.getInstance();
//			FileObject rootObj = FileUtil.resolveFile(rootURI);
//			System.out.println(rootURI+" resolved & verifying...");
//			long time = System.currentTimeMillis();
//			
//			System.out.println("Number of matched files = "+
//					FileUtil.traverseArchiveLocation(rootObj, filePatternString));
//			time = System.currentTimeMillis() - time;
//		    System.out.println("testLocalArchivePattern took " + time/1000. + " seconds");
//		    FileUtil.release();
//		} catch (ArchiveException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	/**
//	 * This is a unit test to decompress bz2 file.
//	 */
//	public void testDecompressBZ2() {
//		FileObject decompFileObj = null;
//		try {
//			URI bz2URI = new URI(System.getProperty("local.staging"));
//			decompFileObj = FileUtil.getBz2File(bz2URI, new URI("/tmp"));
//			assertTrue(decompFileObj.exists());
//			FileUtil.removeFile(new URI(decompFileObj.getName().toString()));
//		} catch (URISyntaxException e2) {
//			e2.printStackTrace();
//		} catch (ArchiveException e) {
//			e.printStackTrace();
//		} catch (FileSystemException e) {
//			e.printStackTrace();
//		}
//	}
}
