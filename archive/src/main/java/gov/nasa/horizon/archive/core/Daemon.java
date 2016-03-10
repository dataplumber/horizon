//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.core;


import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class handles java daemon process.
 * Part of sources borrowed from an article titled "Java Daemon" written by Peter Williams.
 *
 * @author clwong
 * @version
 */
public class Daemon {

	private static Log log = null;
	private static boolean shutdownRequested = false;
	private static Thread mainThread;
	
	public static void main(String[] args) {
		ArchiveProperty.getInstance();
		init();
		while (!isShutdownRequested()) {}
	}
	
	public static void init() {
		try {
			log = LogFactory.getLog(Daemon.class);
			mainThread = Thread.currentThread();
			daemonize();
			addDaemonShutdownHood();
		} catch (Throwable e) {
			log.fatal("Startup failed.", e);
		}
	}
	
	public static void daemonize() {
		getPidFile().deleteOnExit();
		System.out.close();
		System.err.close();
	}
	
	public static void shutdown() {
		shutdownRequested = true;
		try {
			log.info("shutdown");
			getMainDaemonThread().join();
		} catch (InterruptedException e) {
			log.error("Interrupted which waiting on main daemon thread to complete.");
		}
	}
	
	public static boolean isShutdownRequested() {
		return shutdownRequested;
	}

	public static void addDaemonShutdownHood() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Daemon.shutdown();
			}
		});
	}
	
	public static Thread getMainDaemonThread() {
		return mainThread;
	}
	
	public static File getPidFile()
	{
		String path = System.getProperty("daemon.pidfile");
		if (path != null){
			File pidFile = new File(path);
			log.info("daemon.pidfile="+path);
			return pidFile;
		}
		return null;
	}
}
