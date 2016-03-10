//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.distribute.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.UserAuthenticator;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.cache.DefaultFilesCache;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;

/**
 * This class transfer a file to ftp site.
 *
 * @author clwong
  * $Id: FTP.java 2073 2008-10-09 22:13:55Z clwong $
 */
public class FTP {

	private static Log log = LogFactory.getLog(FTP.class);
	private static DefaultFileSystemManager fsManager = null;
	private static FileSystemOptions opts = new FileSystemOptions();
	private static String ftpUsername = null;
	private static String ftpPassword = null;
	
	public static DefaultFileSystemManager getInstance() {
		if (fsManager == null)
			try {
				init();
			} catch (Exception e) {
				log.error(e.getMessage(), e.getCause());
			}
		return fsManager;
	}

	public static DefaultFileSystemManager getInstance(String username, String password) {
		if (fsManager == null)
			try {
				FTP.ftpUsername = username;
				FTP.ftpPassword = password;
				init();
			} catch (Exception e) {
				log.error(e.getMessage(), e.getCause());
			}
		return fsManager;
	}

	public static synchronized void init() throws FileSystemException {
		if (fsManager == null) {
			fsManager = new DefaultFileSystemManager();

			try {
				fsManager.addProvider("ftp", new FtpFileProvider());
				fsManager.setFilesCache(new DefaultFilesCache());
			} catch (Exception e) {
				log.error(e.getMessage(), e.getCause());
				return;
			}			
			if (ftpUsername == null) {
				ftpUsername = "anonymous";
				ftpPassword = System.getProperty("user.name");
			} else log.info("ftpUsername="+ftpUsername);
			UserAuthenticator auth = 
				new StaticUserAuthenticator(null, ftpUsername, ftpPassword);
			try {
				DefaultFileSystemConfigBuilder.getInstance()
				.setUserAuthenticator(opts, auth);
			} catch (Exception e) {
				log.error(e.getMessage(), e.getCause());
				return;
			}
			FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true);
		}
	}

	public static FileObject resolveFile(String uri) {
		try {
			return VFS.getManager().resolveFile(uri, opts);
		} catch (Exception e) {
			log.error("Failed to resolve file "+uri);
			log.error(e.getMessage(), e.getCause());
			return null;
		}
	}

	public static String copyFile(FileObject fromFileObj, FileObject toFileObj)
	{
		if (fromFileObj==null || toFileObj==null) {
			log.error("Null FileObject!");
			return null;
		}
		try {
			// check if directory
			if (toFileObj.exists() && toFileObj.getType()==FileType.FOLDER)
			{
				toFileObj = resolveFile(toFileObj.getName()
						+"/"+fromFileObj.getName().getBaseName());
			}
			if (fromFileObj.exists()) {
				toFileObj.copyFrom(fromFileObj, new AllFileSelector());
			} else {
				log.error("Cannot find file, "+ fromFileObj.getName().toString());
				return null;
			}
			fromFileObj.close();
			toFileObj.close();
		} catch (FileSystemException e) {
			log.error(e.getMessage(), e.getCause());
			return null;
		}
		return toFileObj.getName().toString();
    }

	public static String copyFile(String fromFile, String toFile) {
        return copyFile(resolveFile(fromFile), resolveFile(toFile));
 	}
	
	public static void release() {
		if (fsManager != null)
			fsManager.close();
		fsManager = null;
		setFtpUsername(null);
		setFtpPassword(null);
	}
	
	public static String getFtpUsername() {
		return ftpUsername;
	}

	public static void setFtpUsername(String ftpUsername) {
		FTP.ftpUsername = ftpUsername;
	}

	public static String getFtpPassword() {
		return ftpPassword;
	}

	public static void setFtpPassword(String ftpPassword) {
		FTP.ftpPassword = ftpPassword;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DistributeProperty.getInstance();
		FTP.getInstance();
		System.out.println(FTP.copyFile(args[0], args[1]));
	}
}
