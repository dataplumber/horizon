//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * This class sets up system properties for Archive Program Set.
 *
 * @author clwong
 * @version
 * $Id: ArchiveProperty.java 10123 2012-06-07 15:31:38Z gangl $
 */
public class ArchiveProperty {

	private static Log log = LogFactory.getLog(ArchiveProperty.class);
	private static Properties archiveProps = null;
	public static final String AIP_POST = "topic.aip.post";
	public static final String AIP_STATUS = "topic.aip.status";
	public static final String AIP_ACK = "topic.aip.ack";
	public static final String JMS_USERNAME = "jms.username";
	public static final String JMS_PASSWORD = "jms.password";
	public static final String INVENTORY_URL = "inventory.ws.url";
	public static final String INVENTORY_PORT = "inventory.ws.port";
	public static final String INVENTORY_WS_USER = "inventory.ws.user";
	public static final String INVENTORY_WS_PASSWORD = "inventory.ws.password";
	
	public ArchiveProperty () {};
	
	private synchronized static void init() {
		
		archiveProps = new Properties();
		String archiveConfigFilename = System.getProperty("archive.config.file");
		log.debug("config file: " + archiveConfigFilename);
		if (archiveConfigFilename != null) {
			try {
				archiveProps.load(
						new FileInputStream(archiveConfigFilename));
			} catch (IOException e) {
				e.printStackTrace();
			}
			// log4j properties override the one from config file
			if (System.getProperty("log4j.configuration") != null) {
			   if (!System.getProperty("log4j.configuration").equals(""))
				  archiveProps.remove("log4j.configuration");
			} else 
				System.setProperty("log4j.configuration", "file://" +
						System.getProperty("user.dir")+"/log4j.properties");
			Enumeration propNames = archiveProps.propertyNames();
			log.debug("Processing Enumerations");
			
			while (propNames.hasMoreElements()) {
				
				String name = propNames.nextElement().toString();
				System.setProperty(name, archiveProps.getProperty(name));
				log.debug("name/value: " +name + ", " +archiveProps.getProperty(name));
			}
			//System.getProperties().list(System.out);
			try {
				PropertyConfigurator.configure(new URL(System.getProperty("log4j.configuration")));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			log = LogFactory.getLog(ArchiveProperty.class);
			log.info("Using configuration from " + archiveConfigFilename);
			log.info("Using log4j.configuration from "+System.getProperty("log4j.configuration"));		
		}
		
		// JNDI properties
		if (System.getProperty("java.naming.factory.initial") == null)
			System.setProperty("java.naming.factory.initial",
					"org.jnp.interfaces.NamingContextFactory");
		if (System.getProperty("java.naming.factory.url.pkgs") == null)
			System.setProperty("java.naming.factory.url.pkgs",
					"org.jboss.naming");
		if (System.getProperty("java.naming.provider.url") == null)
			System.setProperty("java.naming.provider.url", "localhost:1099");
		
		// JMS Topic properties
		if (System.getProperty(AIP_POST) == null)
			System.setProperty(AIP_POST, "topic/horizon/aip/post");
		if (System.getProperty(AIP_STATUS) == null)
			System.setProperty(AIP_STATUS, "topic/horizon/aip/complete");
		if (System.getProperty(AIP_ACK) == null)
			System.setProperty(AIP_ACK, "topic/horizon/aip/ack");
		
		// JMS Topic properties
		if (System.getProperty(JMS_USERNAME) == null)
			System.setProperty(JMS_USERNAME, "guest");
		if (System.getProperty(JMS_PASSWORD) == null)
			System.setProperty(JMS_PASSWORD, "");
		if(System.getProperty(INVENTORY_URL) == null){
			log.debug("INVENTORY URL Property is null.");
			System.setProperty(INVENTORY_URL, "");
		}
		if(System.getProperty(INVENTORY_PORT) == null){
			log.debug("INVENTORY PORT Property is null, defaulting to 8443.");
			System.setProperty(INVENTORY_PORT, "8443");
		}
		
		if(System.getProperty(INVENTORY_WS_USER) == null)
			System.setProperty(INVENTORY_WS_USER, "NaN");
		if(System.getProperty(INVENTORY_WS_PASSWORD) == null)
			System.setProperty(INVENTORY_WS_PASSWORD, "NaN");
		
		archiveProps = new Properties(System.getProperties());
		
	}
	
	public static Properties getInstance() {
		if (archiveProps == null){
			init();
			log.debug("Archive props initialized.");
		}
		return archiveProps;
	}
}
