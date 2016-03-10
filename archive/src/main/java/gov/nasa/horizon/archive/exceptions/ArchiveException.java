//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.exceptions;

import gov.nasa.horizon.inventory.api.Constant.ProductArchiveStatus;

/**
 * @author clwong
 * 
 * @version $Id: ArchiveException.java 2223 2008-11-02 01:03:49Z clwong $
 */
@SuppressWarnings("serial")
public class ArchiveException extends Exception {

	public enum ErrorType {ARCHIVE, SERVICE_PROFILE, COPY_FILE};
	private static ErrorType errorStatus;
	private static String productStatus = "ONLINE";

	public ArchiveException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ArchiveException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ArchiveException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ArchiveException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param status
	 * @param message
	 */
	public ArchiveException(final String status,
			final String message) {
		super(message);
		ArchiveException.productStatus = status;
	}

	/**
	 * @param errorStatus
	 * @param status
	 * @param message
	 */
	public ArchiveException(final ErrorType errorStatus,
			final String message) {
		super(message);
		ArchiveException.errorStatus = errorStatus;
	}
	
	public static String getProductStatus() {
		return productStatus;
	}

	public static void setProductStatus(String productStatus) {
		ArchiveException.productStatus = productStatus;
	}

	public static ErrorType getErrorStatus() {
		return errorStatus;
	}

	public static void setErrorStatus(ErrorType errorStatus) {
		ArchiveException.errorStatus = errorStatus;
	}
}
