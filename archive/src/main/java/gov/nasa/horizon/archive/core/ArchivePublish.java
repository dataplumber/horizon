package gov.nasa.horizon.archive.core;
////Copyright 2008, by the California Institute of Technology.
////ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//
//package gov.nasa.horizon.archive.core;
//
//import javax.jms.JMSException;
//import javax.naming.NamingException;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * This class is to publish a simple message onto JMS for Archive status.
// * @author clwong
// * @version
// * $Id: ArchivePublish.java 8382 2011-09-07 20:39:18Z gangl $
// */
//public class ArchivePublish {
//	private static Log log = LogFactory.getLog(ArchivePublish.class);
//	private static ArchivePublish archivePublish = new ArchivePublish();
//	private static JMSSession jms = null;
//	
//	private ArchivePublish() {};
//
//	public static ArchivePublish getInstance() throws JMSException, NamingException {
//		log.debug("getInstance...");
//		if (jms == null) {
//			jms = new JMSSession(ArchiveProperty.AIP_STATUS);
//		}
//		return archivePublish;
//	}
//
//	public static ArchivePublish getInstance(String topic) throws JMSException, NamingException {
//		log.debug("getInstance...");
//		if (jms == null) {
//			jms = new JMSSession(topic);
//		}
//		return archivePublish;
//	}
//
//	public void publish(String message) throws JMSException{
//	    log.debug("publish..."+message);
//		jms.publish(message);
//	}
//
//	public void disconnect() throws JMSException {
//		jms.disconnect();
//		jms = null;
//	}
//}