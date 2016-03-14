package gov.nasa.horizon.archive.tool;
////Copyright 2008, by the California Institute of Technology.
////ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//package gov.nasa.horizon.archive.tool;
//
//import gov.nasa.horizon.archive.core.ArchiveProperty;
//import gov.nasa.horizon.archive.core.Daemon;
//import gov.nasa.horizon.archive.core.JMSSession;
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
// * This class subscribe for report for archive completion. 
// *
// * @author clwong
// * $Id: ArchiveMonitor.java 8382 2011-09-07 20:39:18Z gangl $
// */
//public class ArchiveMonitor implements MessageListener {
//
//	private static JMSSession jms = null;
//	private static Log log = null;
//
//	public ArchiveMonitor() throws JMSException, NamingException {
//		jms = new JMSSession(ArchiveProperty.AIP_STATUS);
//		TopicSubscriber recv = jms.getSession()
//				.createSubscriber(jms.getTopic());
//		recv.setMessageListener(this);
//	}
//	
//	public ArchiveMonitor(String topic) throws JMSException, NamingException {
//		log.info("ArchiveMonitor: "+topic);
//		jms = new JMSSession(topic);
//		TopicSubscriber recv = jms.getSession()
//				.createSubscriber(jms.getTopic());
//		recv.setMessageListener(this);
//	}
//
//	public void onMessage(Message msg) {
//		String message = jms.toString(msg);
//		try {
//			log.info("onMessage: Topic="+jms.getTopic().getTopicName());			
//			if ((System.getProperty(ArchiveProperty.AIP_ACK)).endsWith(
//					jms.getTopic().getTopicName())) {
//				log.info(System.getProperty(ArchiveProperty.AIP_ACK));
//				log.info("Archive Acknowlegement");
//			} else 
//			if ((System.getProperty(ArchiveProperty.AIP_STATUS)).endsWith(
//					jms.getTopic().getTopicName())) {
//				log.info(System.getProperty(ArchiveProperty.AIP_STATUS));
//				log.info(ServiceProfile.printArchiveProfile(message));
//			} else {
//				log.info(jms.toString(msg));
//			}
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		log.debug(ServiceProfile.printServiceProfile());
//	}
//
//	public static void main(String[] args) {
//		
//		ArchiveProperty.getInstance();
//		ArchiveMonitor monitor = null;
//		try {
//			log = LogFactory.getLog(ArchiveMonitor.class);
//			String topic = null;
//			for (String arg : args) {
//				if (arg.startsWith("-topic")) 
//					topic = arg.substring(arg.indexOf('=')+1);
//			}
//			if (topic != null) {
//				monitor = new ArchiveMonitor(topic);
//			}
//			else monitor = new ArchiveMonitor();
//		} catch (JMSException e) {
//			log.fatal("Problem startup ArchiveMonitor: JMSException ", e);
//			System.exit(1);
//		} catch (NamingException e) {
//			log.fatal("Problem startup ArchiveMonitor: NamingException ", e);
//			System.exit(2);
//		} catch (Exception e) {
//			log.fatal("Problem startup ArchiveMonitor: Exception ", e);
//			System.exit(3);
//		}
//		Daemon.init();
//		try {
//			log.info("Archive Monitor started on topic "+jms.getTopic().getTopicName());
//		} catch (JMSException e1) {
//		}
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
//			log.info("Archive Monitor stopped.");
//			monitor.getJms().disconnect();
//		} catch (JMSException e) {
//			log.warn("Problem close JMS session.", e);
//		}	
//	}
//
//	public JMSSession getJms() {
//		return jms;
//	}
//
//	public void setJms(JMSSession jms) {
//		ArchiveMonitor.jms = jms;
//	}
//}