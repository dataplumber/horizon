package gov.nasa.horizon.archive.tool;
////Copyright 2008, by the California Institute of Technology.
////ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//
//package gov.nasa.horizon.archive.tool;
//
//import gov.nasa.horizon.archive.core.ArchiveProperty;
//import gov.nasa.horizon.archive.core.ArchivePublish;
//import gov.nasa.horizon.archive.core.Command;
//import gov.nasa.horizon.archive.core.Daemon;
//import gov.nasa.horizon.archive.core.JMSSession;
//import gov.nasa.horizon.archive.exceptions.ArchiveException;
//import gov.nasa.horizon.archive.exceptions.ArchiveException.ErrorType;
//import gov.nasa.horizon.archive.external.ServiceProfile;
//
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.MessageListener;
//import javax.jms.TopicSubscriber;
//import javax.naming.NamingException;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * This class contains the JMS AIP subscriber waiting for Ingest's AIP.
// * 
// * @author clwong
// * @version
// * $Id: AipSubscribe.java 8382 2011-09-07 20:39:18Z gangl $
// *
// */
//public class AipSubscribe implements MessageListener {
//	
//	private static JMSSession jms = null;
//	private static Log log = null;
//	
//	public AipSubscribe() throws JMSException, NamingException {
//		jms = new JMSSession(ArchiveProperty.AIP_POST);
//		TopicSubscriber recv = jms.getSession()
//				.createSubscriber(jms.getTopic());
//		recv.setMessageListener(this);
//	}
//	
//	@SuppressWarnings("static-access")
//	public void onMessage(Message msg) {
//		String status;
//		String responseMessage = null;
//		
//		// acknowlege onto aip/ack topic
//		try {
//			responseMessage = jms.toString(msg);
//			ArchivePublish archivePublish = ArchivePublish.getInstance(ArchiveProperty.AIP_ACK);
//			archivePublish.publish(responseMessage);
//			archivePublish.disconnect();
//		} catch (JMSException e) {
//			log.error("publish acknowlegement threw JMSException",e);
//			return;
//		} catch (NamingException e) {
//			log.error("publish acknowlegement threw NamingException",e);
//			return;
//		} catch (Exception e) {
//			log.error("publish acknowlegement threw Exception",e);
//		}
//		// process the AIP message
//		try {
//			status = Command.processAIP(jms.toString(msg));
//			log.info(status);
//			responseMessage = ServiceProfile.getServiceProfile().toString();
//		} catch (ArchiveException e1) {
//			if (e1.getErrorStatus()==ErrorType.SERVICE_PROFILE)
//				responseMessage = jms.toString(msg);
//			else responseMessage = ServiceProfile.getServiceProfile().toString();
//			log.error(e1.getErrorStatus()+": "+e1.getMessage());
//		} catch (Exception e) {
//			log.error("Exception:", e);
//		}
//		// post the aip with status onto aip/complete topic
//		try {
//			ArchivePublish archivePublish = ArchivePublish.getInstance();
//			archivePublish.publish(responseMessage);
//			archivePublish.disconnect();
//		} catch (JMSException e) {
//			log.error("publishStatus threw JMSException",e);
//		} catch (NamingException e) {
//			log.error("publishStatus threw NamingException",e);
//		} catch (Exception e) {
//			log.error("publishStatus threw Exception",e);
//		}
//	}
//	
//	public JMSSession getJms() {
//		return jms;
//	}
//
//	public static void main(String args[]) {
//
//		ArchiveProperty.getInstance();
//		AipSubscribe aipSub = null;
//		try {
//			log = LogFactory.getLog(AipSubscribe.class);
//			aipSub = new AipSubscribe();
//		} catch (JMSException e) {
//			log.fatal("Problem startup AipSubscribe: JMSException ", e);
//			System.exit(1);
//		} catch (NamingException e) {
//			log.fatal("Problem startup AipSubscribe: NamingException ", e);
//			System.exit(2);
//		} catch (Exception e) {
//			log.fatal("Problem startup AipSubscribe: Exception ", e);
//			System.exit(3);
//		}
//		Daemon.init();
//		log.info("AipSubscriber started.");
//		while (!Daemon.isShutdownRequested()) {
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				System.exit(0);
//			}
//		}
//		
//		// close down connection
//		try {
//			log.info("AipSubscriber stopped.");
//			aipSub.getJms().disconnect();
//		} catch (JMSException e) {
//			log.warn("Problem close JMS session.", e);
//		}	
//	}
//
//}
