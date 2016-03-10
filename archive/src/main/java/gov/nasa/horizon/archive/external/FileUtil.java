//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.external;

import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.common.api.util.ChecksumUtility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.cache.DefaultFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.bzip2.Bzip2FileProvider;
import org.apache.commons.vfs2.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs2.provider.sftp.SftpFileProvider;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

/**
 * This class contains file utility methods re-using external tools.
 * 
 * @author clwong
 * 
 * @version 
 * $Id: FileUtil.java 5635 2010-08-18 19:16:30Z gangl $
 */
public class FileUtil {

	private static Log log = LogFactory.getLog(FileUtil.class);
	private static DefaultFileSystemManager fsManager = null;
	private static FileSystemOptions sftpOpts = null;
	private static FileSystemOptions opts = null;
	private static String username = null;
	private static String password = null;
	private static final String PRIVATE_KEY_FILE = 
						System.getProperty("user.home") + "/.ssh/id_dsa";

	public static void copyFile(FileObject fromFileObj, FileObject toFileObj)
			throws ArchiveException {
		log.debug("copyFile:"+fromFileObj.toString()+" to "+toFileObj.toString());
		String msg = "";
		if (fromFileObj==null || toFileObj==null) {
			msg += "Null FileObject!";
			return;
		}
		try {
			// check if directory
			if (toFileObj.exists() && toFileObj.getType()==FileType.FOLDER)
			{
				toFileObj = resolveFile(toFileObj.getName()
						+"/"+fromFileObj.getName().getBaseName());
			}
			if (!fromFileObj.exists()) {
				fromFileObj.close();
				toFileObj.close();
				msg += "Cannot find file, " + fromFileObj.getName().toString();
			} else {
				//toFileObj.copyFrom(fromFileObj, new AllFileSelector());
				FileUtil.copy(fromFileObj, toFileObj);
				if (!toFileObj.exists()) {
					fromFileObj.close();
					toFileObj.close();
					msg += toFileObj.getName().toString()+" not copied!";
				} return;
			}
		} catch (Exception e) {
			log.debug("copyFile:"+fromFileObj.toString()+" to "+toFileObj.toString());
			throw new ArchiveException(e.getMessage());
		}
		throw new ArchiveException(msg);
	}

	public static void copyFile(String fromPath, String toPath) throws ArchiveException {
      URI fromUriObj = null;
      if(fromPath != null && !fromPath.startsWith("file://")) {
         try {
            fromUriObj = new URI("file://"+fromPath);
         }
         catch(URISyntaxException e ) {
             log.debug("Could not resolve file: "+e.getMessage()); 
               throw new ArchiveException(fromPath.toString()+"\n"+e.getMessage());
         }
      }
      URI toUriObj = null;
      if(toPath != null && !toPath.startsWith("file://")) {
         try {
            toUriObj = new URI("file://"+toPath);
         }
         catch(URISyntaxException e ) {
             log.debug("Could not resolve file: "+e.getMessage()); 
               throw new ArchiveException(toPath.toString()+"\n"+e.getMessage());
         }
      }
      copyFile(fromUriObj, toUriObj);

	}

	public static void copyFile(URI fromURI, URI toURI) throws ArchiveException {
		copyFile(resolveFile(fromURI), resolveFile(toURI));
	}

	public static void copy(FileObject fromFileObj, FileObject toFileObj) throws ArchiveException {
		log.debug("copy:"+fromFileObj.toString()+" to "+toFileObj.toString());
		try {
			if (fromFileObj.exists()) {
				BufferedOutputStream bos = new BufferedOutputStream(toFileObj
						.getContent().getOutputStream());
				BufferedInputStream bis = new BufferedInputStream(fromFileObj
						.getContent().getInputStream());
				log.debug("Input/Output buffer created.");
				try {
					long bytesToRead = fromFileObj.getContent().getSize();
					byte[] buffer = new byte[4096];
					while (true) {
						int size = bis.read(buffer, 0, 4096);
						if (size < 0 || bytesToRead == 0)
							break;
						bytesToRead -= size;
						bos.write(buffer, 0, size);
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					throw new ArchiveException(e.getMessage());
				} finally {
					try {
						bis.close();
						bos.close();
					} catch (Exception e) {
						log.error(e.getMessage());
						throw new ArchiveException(e.getMessage());
					}
				}
				
			} else {
				log.warn(fromFileObj.toString() + " does not exists.");
				throw new ArchiveException(fromFileObj.toString() + " does not exists.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArchiveException(e.getMessage());
		}
	}

	// bz2URI must be local such as file://<bz2 compressed uri>
	public static FileObject getBz2File(URI bz2URI, URI decompURI)
			throws ArchiveException {
		FileObject decompFileObj = null;
		try {
			FileObject bz2FileObj = FileUtil.resolveFile(bz2URI.toString());
			String decompBaseName = 
				bz2FileObj.getName().getBaseName().replaceAll(".bz2$", "");
		    decompFileObj = FileUtil.resolveFile(decompURI);
		    if (decompFileObj.getType() == FileType.FOLDER) {
		    	decompFileObj = FileUtil.resolveFile(
		    		new String(decompFileObj.getName()+"/"+decompBaseName));
		    	if (decompFileObj.exists()) 
		    		decompFileObj.delete(new FileTypeSelector(FileType.FILE));
		    }
		    bz2FileObj = FileUtil.resolveFile(
		    		new String("bz2:"+bz2URI+"!"+decompBaseName));
			decompFileObj.copyFrom(bz2FileObj, new FileTypeSelector(FileType.FILE));
		} catch (FileSystemException e) {
			throw new ArchiveException(e.getMessage(), e.getCause());
		} catch (ArchiveException e) {
			throw new ArchiveException(e.getMessage(), e.getCause());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return decompFileObj;		
	}

	public static String getFileName(URI uri) {
		String path[] = uri.toString().split("/");
		return path[path.length-1];	
	}
	
	public static DefaultFileSystemManager getInstance() throws ArchiveException {
		if (fsManager == null) init();
		return fsManager;
	}

	public static DefaultFileSystemManager getInstance(String username, String password) {
		if (fsManager == null)
			try {
				FileUtil.username = username;
				FileUtil.password = password;
				init();
			} catch (Exception e) {
				log.error(e.getMessage(), e.getCause());
			}
		return fsManager;
	}
	
	public static synchronized void init() throws ArchiveException {
		if (fsManager == null) {		
			fsManager = new DefaultFileSystemManager();
			try {
				fsManager.addProvider("file", new DefaultLocalFileProvider());
				fsManager.addProvider("sftp", new SftpFileProvider());
				fsManager.addProvider("ftp", new FtpFileProvider());
				fsManager.addProvider("bz2", new Bzip2FileProvider());
				fsManager.setFilesCache(new DefaultFilesCache());
				fsManager.init();
			} catch (FileSystemException e) {
				throw new ArchiveException(e.getMessage(), e.getCause());
			}

			sftpOpts = new FileSystemOptions();
			UserAuthenticator sftpAuth = 
				new StaticUserAuthenticator(null, System.getProperty("user.name"), null);
			try {
				DefaultFileSystemConfigBuilder.getInstance()
						.setUserAuthenticator(sftpOpts, sftpAuth);
			} catch (FileSystemException e) {
				throw new ArchiveException(e.getMessage(), e.getCause());
			}

			try {
				SftpFileSystemConfigBuilder.getInstance().setIdentities(sftpOpts,
						new File[] { new File(PRIVATE_KEY_FILE) });
			} catch (FileSystemException e) {
				throw new ArchiveException(e.getMessage(), e.getCause());
			}
						
			if (username == null) {
				username = "anonymous";
				password = System.getProperty("user.name");
			}		
			UserAuthenticator auth = 
				new StaticUserAuthenticator(null, username, password);
			try {
				opts = new FileSystemOptions();
				DefaultFileSystemConfigBuilder.getInstance()
						.setUserAuthenticator(opts, auth);
			} catch (FileSystemException e) {
				throw new ArchiveException(e.getMessage(), e.getCause());
			}
			//FtpFileSystemConfigBuilder.getInstance().setDataTimeout(opts, dataTimeout)
			FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true);
			FtpFileSystemConfigBuilder.getInstance().setDataTimeout(opts, 30000);
			
		}
	}

	public static void release() {
		if (fsManager != null)
			fsManager.close();
		fsManager = null;
	}

	public static boolean removeFile(String path) throws ArchiveException {
      URI uriObj = null;
      if(path != null && !path.startsWith("file://")) {
         try {
            uriObj = new URI("file://"+path);
         }
         catch(URISyntaxException e ) {
             log.debug("Could not resolve file: "+e.getMessage()); 
               throw new ArchiveException(path.toString()+"\n"+e.getMessage());
         }
      }
      return removeFile(uriObj);
	}

	public static boolean removeFile(URI uri) throws ArchiveException {
		FileObject f;
		try {
			log.debug("removeFile:"+uri);
			f = resolveFile(uri);
			log.debug("resolved "+uri);
			if (f.exists()) {
				log.debug("existed "+uri);
				if(f.delete()){
				log.debug("deleted "+uri);
				return true;
				}
				else{
					log.debug("Delete returned false. No rhyme or reason.");
					return false;
				}
				
			}
			log.debug("File ["+uri.toString()+"] doesn't exist.");
			return false;
		} catch (ArchiveException e) {
			throw new ArchiveException(e.getMessage());
		} catch (FileSystemException e) {
			throw new ArchiveException(e.getMessage());
		}

	}
	
	public static FileObject resolveFile(String uri) throws ArchiveException, URISyntaxException {
      URI uriObj = null;
	   if(uri != null && !uri.startsWith("file://")) {
         try {
            uriObj = new URI("file://"+uri);
         }
         catch(URISyntaxException e ) {
             log.debug("Could not resolve file: "+e.getMessage()); 
               throw new ArchiveException(uri.toString()+"\n"+e.getMessage());
         }
      }
		return resolveFile(uriObj);
	}

	public static FileObject resolveFile(URI uri) throws ArchiveException{
		log.debug("Entering ResolveFile(URI)");
		if (uri==null) return null;
		   
		if(fsManager == null) 
		 	                fsManager = getInstance();               
		 		                log.debug("resolving file..."); 
		try {
			log.debug("Trying to resolve file: "+uri.toString());
			log.debug(uri.getScheme());
			
			if (uri.getScheme().equals("sftp")) {
				return fsManager.resolveFile(uri.toString(), sftpOpts);
			} else if (uri.getScheme().equals("ftp")) {
				return fsManager.resolveFile(uri.toString(), opts);
			} else  {
			   log.debug("Local file scheme found");
				return fsManager.resolveFile(uri.toString());
			}
		} catch (Exception e) {
			log.debug("Could not resolve file: "+e.getMessage()); 
			throw new ArchiveException(uri.toString()+"\n"+e.getMessage());
		}
	}

	public static long traverseArchiveLocation(FileObject root, String filePatternString)
			throws ArchiveException {
		long matches = 0;
		FileObject[] children;
		try {
			children = root.getChildren();
		} catch (FileSystemException e) {
			throw new ArchiveException(e.getMessage(), e.getCause());
		}
		if (children.length == 0)
			log.info(root + " is empty!");
		else
			log.debug(root + "| #children=" + children.length);
		int cnt = 0;
		for (FileObject f : children) {
			try {
				if (f.getType() == FileType.FOLDER) {
					matches += FileUtil.traverseArchiveLocation(f, filePatternString);
					continue;
				}
				if (!f.isHidden() && !Pattern.compile(filePatternString).matcher(
						f.getName().getPath()).matches())
					cnt++;
				else
					matches++;
			} catch (FileSystemException e) {
				log.debug(e.getMessage());
			}
		}
		if (cnt > 0)
			log.info(cnt + "/" + children.length
					+ " files'pattern not matched at " + root);

		return matches;
	}

	public static boolean validateChecksum(File file, String checksum,
			String algorithm) {
		return ChecksumUtility.validate(ChecksumUtility.DigestAlgorithm
				.toAlgorithm(algorithm), file, checksum);
	}
	
	private FileUtil() {}
	

}
