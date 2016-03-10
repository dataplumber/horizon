package gov.nasa.horizon.archive.core;
////Copyright 2008, by the California Institute of Technology.
////ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//package gov.nasa.horizon.archive.core;
//
//import java.util.Date;
//
//import javax.jms.BytesMessage;
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.TextMessage;
//import javax.jms.Topic;
//import javax.jms.TopicConnection;
//import javax.jms.TopicConnectionFactory;
//import javax.jms.TopicPublisher;
//import javax.jms.TopicSession;
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * This class initializes and sets up JMS session.
// *
// * @author clwong
// *
// * @version
// * $Id: JMSSession.java 8382 2011-09-07 20:39:18Z gangl $
// */
//public class JMSSession {
//
//	private static Log log = LogFactory.getLog(JMSSession.class);
//	private static final Class<TextMessage> MESSAGE_TYPE = TextMessage.class;
//	
//	TopicConnection conn = null;
//	TopicSession session = null;
//	Topic topic = null;
//
//	public JMSSession(String topicName) throws JMSException, NamingException {
//    		this.initialize(topicName);
//	}
//
//	private void initialize(String topicName) throws JMSException, NamingException {
//		log.debug("initialize..."+topicName);
//		if (conn == null) {
//			Context context = new InitialContext();
//			TopicConnectionFactory tcf = (TopicConnectionFactory) context
//					.lookup("UIL2ConnectionFactory");
//			String username = System.getProperty(ArchiveProperty.JMS_USERNAME);
//			String topicProp = System.getProperty(topicName);
//			if (topicProp==null) topicProp = topicName;
//			log.info("Connecting "+topicProp
//					+"| "+ System.getProperty("java.naming.provider.url")
//					+"| "+ username);
//			if (username.equals(""))
//				conn = tcf.createTopicConnection();
//			else
//				conn = tcf.createTopicConnection(username, System
//						.getProperty(ArchiveProperty.JMS_PASSWORD));
//			topic = (Topic) context.lookup(topicProp);
//			session = conn.createTopicSession(false,
//					TopicSession.AUTO_ACKNOWLEDGE);
//			conn.start();
//		}
//	}
//
//	public String toString(Message message) {
//		String msgString = null;
//		if (message.getClass().getName().contains("TextMessage")) {
//			try {
//				msgString = ((TextMessage) message).getText();
//			} catch (JMSException e) {
//				e.printStackTrace();
//			}
//		}
//		else if (message.getClass().getName().contains("BytesMessage")) {
//			BytesMessage bmsg = (BytesMessage) message;
//			byte[] buffer = null;
//			try {
//				if (bmsg.getBodyLength() > Integer.MAX_VALUE) {
//					log.error("\n"+new Date()+": Message size too large!");
//				}
//				buffer = new byte[(int) bmsg.getBodyLength()];
//				bmsg.readBytes(buffer);	        
//			} catch (JMSException e) {
//				e.printStackTrace();
//			}
//			msgString = new String(buffer);
//		}
//	    return msgString;
//	}
//	
//	public void publish(Message msg) throws JMSException {
//		log.debug("publish..."+topic);
//		TopicPublisher send = session.createPublisher(topic);
//		send.publish(msg);
//		send.close();
//	}
//	
//	public void publish(String msgString) throws JMSException {
//		log.debug("publish...");
//		if (MESSAGE_TYPE.equals(TextMessage.class)) {
//			publishTextMessage(msgString);
//		} else publishBytesMessage(msgString);
//	}
//	
//	public void publishTextMessage(String msgString) throws JMSException{
//	    log.debug("publishTextMessage...");
//	    TextMessage textMsg = session.createTextMessage(msgString);
//		publish(textMsg);
//	}
//
//	public void publishBytesMessage(String msgString) throws JMSException{
//	    log.debug("publishBytesMessage...");
//		BytesMessage bmsg = session.createBytesMessage();
//		byte[] buffer = msgString.getBytes();
//		bmsg.writeBytes(buffer);
//		publish(bmsg);
//	}
//	
//	public void disconnect() throws JMSException {
//		log.debug("disconnect...");
//		if(session != null) {
//			session.close();
//		}
//
//		if(conn != null) {
//			conn.close();
//		}
//	}
//
//	/**
//	 * @return the conn
//	 */
//	public TopicConnection getConn() {
//		return conn;
//	}
//
//	/**
//	 * @param conn the conn to set
//	 */
//	public void setConn(TopicConnection conn) {
//		this.conn = conn;
//	}
//
//	/**
//	 * @return the session
//	 */
//	public TopicSession getSession() {
//		return session;
//	}
//
//	/**
//	 * @param session the session to set
//	 */
//	public void setSession(TopicSession session) {
//		this.session = session;
//	}
//
//	/**
//	 * @return the topic
//	 */
//	public Topic getTopic() {
//		return topic;
//	}
//
//	/**
//	 * @param topic the topic to set
//	 */
//	public void setTopic(Topic topic) {
//		this.topic = topic;
//	}
//	
//}
