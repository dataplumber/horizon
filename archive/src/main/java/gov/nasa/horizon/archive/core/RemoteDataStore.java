//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.archive.external.FileUtil;
import gov.nasa.horizon.inventory.api.Constant.ProductArchiveStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;

/**
 * This class contains implementation of interface DataStore.
 * It store files from remote sites.
 *
 * @author clwong
 * $Id: RemoteDataStore.java 5629 2010-08-18 17:52:37Z niessner $
 */
class RemoteDataStore implements DataStore {
	
	private static Log log = LogFactory.getLog(RemoteDataStore.class);
	private static final int TIMEOUT = 10000;
	
	public void setChecksumOption(boolean checksum){
	}

	
	public boolean verify(URI uri, long fileSize, String checksum,
			String checksumType) throws ArchiveException {
		try {
			verify(uri.toURL());	
		} catch (ArchiveException ex) {
			throw new ArchiveException(ArchiveException.getProductStatus(), ex.getMessage());
		} catch (MalformedURLException ex) {
			throw new ArchiveException(
					ProductArchiveStatus.MISSING.toString(),
					ProductArchiveStatus.MISSING.toString()+": "
					+"URI "+uri.toString()+" MalformedURLException!"
					+"\n"+ex.getMessage());
		}
		return true;		
	}
	
	public boolean verify(URL url) throws ArchiveException
	{
	   long time = System.currentTimeMillis();
		URLConnection conn = GetConnection.getInstance().open (url);
		String protocol = url.getProtocol();

		if (protocol.equals("http")) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setConnectTimeout(TIMEOUT);
			String responseMessage = null;
			int responseCode = -1;
			try {
				responseMessage = httpConn.getResponseMessage();
				responseCode = httpConn.getResponseCode();
				log.debug("HTTP Response Code: " +httpConn.getResponseCode() );
				if ((responseCode>=200 && responseCode<300) || responseCode==503) {
					if (responseCode==503) log.debug(responseMessage+"["+responseCode+"]"+url);
					httpConn.disconnect();
					return true;
				}
			} catch (IOException ex) {
				throw new ArchiveException(ProductArchiveStatus.MISSING.toString(),
						ProductArchiveStatus.MISSING.toString()+": "
						+"URL "+url.toString()+" IOException!"
						+"\n"+ex.getMessage());
			}
			throw new ArchiveException(
					ProductArchiveStatus.MISSING.toString(),
					ProductArchiveStatus.MISSING.toString()+": "
					+url+": "+responseMessage
					+"["+responseCode+"]");
		} else if (protocol.equals("ftp")){
			FileObject fileObj;
            try {
                    log.debug("Attempting to resolve: " + url.toString());
                    fileObj = FileUtil.resolveFile(url.toString());
                    log.debug("attempted to resolve...");
                    if (fileObj==null) {
                            if (url != null) {
                                    url.openStream().close();
                                    return true;
                            }
                            log.debug("Throwing archive exeption.");
                            throw new ArchiveException(
                                            ProductArchiveStatus.MISSING.toString(),
                                            ProductArchiveStatus.MISSING.toString()+": "
                                            +url);
                    }
                    else if (fileObj.exists()) {
                            log.debug("verifyURL with VFS");
                            fileObj.close();
                            return true;
                    } else {
                            time = System.currentTimeMillis() - time;
                            if (time>TIMEOUT) log.info("Time="+time+":"+url);
                    }
            } catch (Exception e) {
                    log.debug(e.getMessage());
                    throw new ArchiveException(
                                    ProductArchiveStatus.MISSING.toString(),
                                    ProductArchiveStatus.MISSING.toString()+": "
                                    +"URL "+url.toString()+"\n"+e.getMessage());
            }
    }

		else {			
			log.debug("other than ftp & http!");
			try {
				url.openStream().close();
			} catch (Exception e) {
				throw new ArchiveException(
						ProductArchiveStatus.MISSING.toString(),
						ProductArchiveStatus.MISSING.toString()+": "
						+"URL "+url.toString()+"\n"+e.getMessage());
			}
			return true;
		}
		throw new ArchiveException(
				ProductArchiveStatus.MISSING.toString(),
				ProductArchiveStatus.MISSING.toString()+": "
				+url);
	}
}
