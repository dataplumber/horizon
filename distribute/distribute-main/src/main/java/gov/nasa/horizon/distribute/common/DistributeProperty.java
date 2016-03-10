//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.distribute.common;

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
 * This class sets up system properties for Distribute Program Set.
 *
 * @author clwong
 *
 * @version
 * $Id: DistributeProperty.java 2206 2008-10-31 16:51:25Z clwong $
 */
public class DistributeProperty {

	private static Log log = null;
	private static Properties distributeProps;
	
	public DistributeProperty () {};
	
	private synchronized static void init() {
		distributeProps = new Properties();
		String distributeConfigFilename = System.getProperty("distribute.config.file");
		if (distributeConfigFilename != null) {
			try {
				distributeProps.load(new FileInputStream(
						distributeConfigFilename));
			} catch (IOException e) {
				e.printStackTrace();
			}
			// log4j properties override the one from config file
			if (System.getProperty("log4j.configuration") != null) {
			   if (!System.getProperty("log4j.configuration").equals(""))
				   distributeProps.remove("log4j.configuration");
			} else 
				System.setProperty("log4j.configuration", "file://" +
						System.getProperty("user.dir")+"/log4j.properties");
			Enumeration propNames = distributeProps.propertyNames();
			while (propNames.hasMoreElements()) {
				String name = propNames.nextElement().toString();
				System.setProperty(name, distributeProps.getProperty(name));
			}
			//System.getProperties().list(System.out);
			try {
				PropertyConfigurator.configure(new URL(System.getProperty("log4j.configuration")));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			log = LogFactory.getLog(DistributeProperty.class);
			log.info("Using configuration from " + distributeConfigFilename);
			log.info("Using log4j.configuration from "+System.getProperty("log4j.configuration"));	
		}
		distributeProps = new Properties(System.getProperties());
	}
	
	public static Properties getInstance() {
		if (distributeProps == null) init();
		return distributeProps;
	}
}
