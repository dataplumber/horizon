//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.distribute.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;

import sun.net.www.protocol.ftp.FtpURLConnection;

/**
 * This class contains URL related methods.
 *
 * @author clwong
  * $Id: URL.java 2206 2008-10-31 16:51:25Z clwong $
 */
public class URL {
	
	private static Log log = LogFactory.getLog(URL.class);
			
	public static boolean verify(String urlString) {
		java.net.URL url = null;
		long time = System.currentTimeMillis();
		try {
			url = new java.net.URL(urlString);
			URLConnection conn = url.openConnection();
			String protocol = url.getProtocol();
			if (protocol.equals("http")) {
				HttpURLConnection httpConn = (HttpURLConnection) conn;
				if ((httpConn.getResponseCode()>=200 && httpConn.getResponseCode()<300) ||
						httpConn.getResponseCode()==503) {
					log.debug(httpConn.getResponseMessage()+"["+httpConn.getResponseCode()+"]");
					return true;
				}
				log.error(httpConn.getResponseMessage()+"["+httpConn.getResponseCode()+"]");
			} else if (protocol.equals("ftp")){
				// to check for file existence
				FtpURLConnection ftpConn = (FtpURLConnection) conn;
				try {
					ftpConn.getInputStream().close();
					return true;
				} catch (Exception e) {};
				
				// to resolve if a ftp directory
				FTP.getInstance();
				FileObject fileObj = FTP.resolveFile(urlString);
				FTP.release();
				if (fileObj==null) url.openStream().close();
				else if (fileObj.exists()) {
					fileObj.close();
					return true;			
				}
			}
			else {				
				url.openStream().close();
				return true;
			}
		}
		catch (MalformedURLException ex ) {
			log.error(" IOException:"+ex);
		}
		catch (IOException ex) {
			log.error(" IOException:"+ex);
		}
		log.error("Time="+(System.currentTimeMillis() - time));
		log.error(url+" failed verification!");
		return false;		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DistributeProperty.getInstance();
		// testing
		args = new String[] {"ftp://snowwhite2.jpl.nasa.gov/data/clwong/"};
		if (URL.verify(args[0])) System.out.println("available");
		else System.out.println("unavalable");
	}
}
