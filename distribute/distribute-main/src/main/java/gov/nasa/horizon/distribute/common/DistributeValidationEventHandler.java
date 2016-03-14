//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.distribute.common;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class validates xml schema.
 *
 * @author clwong
 *
 */
public class DistributeValidationEventHandler implements ValidationEventHandler {
	
	private static Log log = LogFactory.getLog(DistributeValidationEventHandler.class);

	/* (non-Javadoc)
	 * @see javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.ValidationEvent)
	 */
	public boolean handleEvent(ValidationEvent ve) {
		ValidationEventLocator locator = ve.getLocator();
		if (ve.getSeverity() == ValidationEvent.FATAL_ERROR
				|| ve.getSeverity() == ValidationEvent.ERROR) {			
			//log message from valdation event
			log.info("Invalid document: " + locator.getURL());
			log.info("Error: " + ve.getMessage());
//			log.info("Error: " + ve.getLinkedException().getCause());
//			ve.getLinkedException().printStackTrace();
			//log.info("Error: " + );
			//Output line and column number
			log.info("Error at column " + locator.getColumnNumber() + ", line "
					+ locator.getLineNumber());
			return false;
		}
		log.info(locator.getURL()+" validated.");
		return true;
	}

}