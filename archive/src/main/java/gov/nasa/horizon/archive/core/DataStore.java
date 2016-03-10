//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.exceptions.ArchiveException;

import java.net.URI;
import java.net.URL;

/**
 * This class contains interfaces to archive files.
 *
 * @author clwong
 *
 * $Id: DataStore.java 5359 2010-07-23 17:18:52Z gangl $
 */
public interface DataStore {
	
	public boolean verify (URI uri, long fileSize, 
							String checksum, String checksumType) 
							throws ArchiveException;
	
	public boolean verify (URL url) throws ArchiveException;
	
	public void setChecksumOption(boolean checksum);
}
