//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.tool;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;

import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.archive.external.FileUtil;


/**
 * This class traveses Archive Location for a file pattern of
 * ${archive_location}/YYYY/DOY/*.bz2
 *
 * @author clwong
 *
 * @version
 * $Id: ArchiveLocation.java 10123 2012-06-07 15:31:38Z gangl $
 */
public class ArchiveLocation {

	private static Log log = LogFactory.getLog(ArchiveLocation.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: archiveLocation");
			System.exit(0);
		}
		long time = System.currentTimeMillis();
	    ArchiveProperty.getInstance();
		URI rootURI = null;
		try {
			rootURI = new URI(args[0]);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		String filePatternString = ".*/\\d{4}/\\d{3}/.*\\.bz2";
		//String filePatternString = ".*/\\d{4}/.*\\.bz2";
		try {
			FileUtil.getInstance();
			FileObject rootObj = FileUtil.resolveFile(rootURI);
			System.out.println(rootURI+" resolved & verifying...");
			log.info(rootURI+" resolved & verifying...");
			System.out.println("Number of matched files = "+
			FileUtil.traverseArchiveLocation(rootObj, filePatternString));
			
			
			
			time = System.currentTimeMillis() - time;
		    System.out.println("testLocalArchivePattern took " + time/1000. + " seconds");
		    log.info("testLocalArchivePattern took " + time/1000. + " seconds");
		    FileUtil.release();
		} catch (ArchiveException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
