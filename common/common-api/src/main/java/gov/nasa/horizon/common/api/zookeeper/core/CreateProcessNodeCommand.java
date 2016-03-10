package gov.nasa.horizon.common.api.zookeeper.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class CreateProcessNodeCommand implements Command {

	String nodeName;
	String message;
	boolean ephemeral = false;
	
	private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
	String response = null;
	
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(CreateProcessNodeCommand.class);
	
	
	public CreateProcessNodeCommand(String node, String msg){
		nodeName = node;
		message = msg;
		ephemeral = false;
	}
	
	public CreateProcessNodeCommand(String node, String msg, boolean isEphemeral){
		nodeName = node;
		message = msg;
		ephemeral = isEphemeral;
	}
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {
		
		if(firstRun){
			firstRun = false;
			try{
				if(!ephemeral)
					response = zk.create(nodeName, message.getBytes(), acl, CreateMode.PERSISTENT);
				else
					response = zk.create(nodeName, message.getBytes(), acl, CreateMode.EPHEMERAL);
			}catch(KeeperException.NoNodeException k){
				log.debug("Error creating node, Checking to see if it's a path issue.");
				if(k.code().equals(Code.NONODE)){
					String temp =  nodeName.substring(0,nodeName.lastIndexOf('/'));
               log.debug ("Unable to create node.  Try creating subdirectory: " + temp);
					CreateProcessNodeCommand newCommand = new CreateProcessNodeCommand(temp,"");
					//recurse
					newCommand.execute(zk);
					if(!ephemeral)
						response = zk.create(nodeName, message.getBytes(), acl, CreateMode.PERSISTENT);
					else
						response = zk.create(nodeName, message.getBytes(), acl, CreateMode.EPHEMERAL);
				}
				
			}
		}
		else{
			Thread.sleep(500);
			try{
				if(!ephemeral)
					response = zk.create(nodeName, message.getBytes(), acl, CreateMode.PERSISTENT);
				else
					response = zk.create(nodeName, message.getBytes(), acl, CreateMode.EPHEMERAL);
			}catch(KeeperException.NoNodeException k){
				log.debug("Error creating node, Checking to see if it's a path issue.");
				if(k.code().equals(Code.NONODE)){
					String temp =  nodeName.substring(0,nodeName.lastIndexOf('/'));
					log.debug("Failed creating node, creating subdirectory: "+temp);
					CreateProcessNodeCommand newCommand = new CreateProcessNodeCommand(temp,"");
					//recurse
					newCommand.execute(zk);
					if(!ephemeral)
						response = zk.create(nodeName, message.getBytes(), acl, CreateMode.PERSISTENT);
					else
						response = zk.create(nodeName, message.getBytes(), acl, CreateMode.EPHEMERAL);
				}
				
			}
		}
		return new CommandResponse(response);
	}

	@Override
	public String getCommandName() {
		return "createProcessNode("+nodeName+", "+message+")";
	}

}
