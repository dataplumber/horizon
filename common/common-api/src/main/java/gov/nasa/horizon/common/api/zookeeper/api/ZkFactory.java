package gov.nasa.horizon.common.api.zookeeper.api;
import gov.nasa.horizon.common.api.zookeeper.core.ZkAccessImpl;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.zookeeper.Watcher;


public class ZkFactory {

	private static Log log = LogFactory.getLog(ZkFactory.class);
	static ZkAccess zk = null;
	
	public static ZkAccess getZk(String hosts, Map<String,Object> session) throws IOException{
		if(zk == null){
			log.debug("Creating new ZooKeeper");
			return init(hosts, session);
//			zk = init(hosts, session);
//			return zk;
			
			}
		else if(zk.needsInit()){
			log.debug("Creating new ZooKeeper");
			return init(hosts,session);
		}
		else{
			log.debug("Returning existing ZooKeeper");
			return zk;
		}
	}
	
	public static ZkAccess getZk(String hosts) throws IOException{
      if(zk == null){
         log.debug("Creating new ZooKeeper");
         return init(hosts,null);
//			zk =  init(hosts,  null);
//			return zk;
      }
      else if(zk.needsInit()){
         log.debug("Creating new ZooKeeper");
         return init(hosts,null);
      }
      else{
         log.debug("Returning existing ZooKeeper");
         return zk;
      }

   }

   public static ZkAccess getZk(String hosts, Watcher watcher) throws IOException{
      if(zk == null){
         log.debug("Creating new ZooKeeper");
         return init(hosts,null, watcher);
//			zk =  init(hosts,  null);
//			return zk;
      }
      else if(zk.needsInit()){
         log.debug("Creating new ZooKeeper");
         return init(hosts,null, watcher);
      }
      else{
         log.debug("Returning existing ZooKeeper");
         return zk;
      }

   }
	
	  public static ZkAccess getZk(String hosts, Map<String,Object> session, Watcher watcher) throws IOException{
	      if(zk == null){
	         log.debug("Creating new ZooKeeper");
	         return init(hosts,session, watcher);
//	       zk =  init(hosts,  null);
//	       return zk;
	      }
	      else if(zk.needsInit()){
	         log.debug("Creating new ZooKeeper");
	         return init(hosts,session, watcher);
	      }
	      else{
	         log.debug("Returning existing ZooKeeper");
	         return zk;
	      }
	         
	   }

	public static  ZkAccess init(String hosts,  Map<String,Object> session) throws IOException{
		//new zkAccessImpl();
		//it all goes here.
		zk = new ZkAccessImpl(hosts,session, null);
		return zk;
	}
	
	  public static  ZkAccess init(String hosts,  Map<String,Object> session, Watcher watcher) throws IOException{
	      //new zkAccessImpl();
	      //it all goes here.
	      zk = new ZkAccessImpl(hosts,session, watcher);
	      return zk;
	   }
	
	
}
