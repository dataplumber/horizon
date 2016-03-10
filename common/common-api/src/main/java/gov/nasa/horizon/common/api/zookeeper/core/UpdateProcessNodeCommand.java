package gov.nasa.horizon.common.api.zookeeper.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class UpdateProcessNodeCommand implements Command {

	String nodeName;
	String message;
	
	private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
	String response = null;
	
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(UpdateProcessNodeCommand.class);
	
	
	public UpdateProcessNodeCommand(String node, String msg){
		nodeName = node;
		message = msg;
	}
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {
		
		Stat stat = null;
		if(firstRun){
			firstRun = false;
			stat = zk.setData(nodeName, message.getBytes(), 0);
		}
		else{
			//setup retry code here.
			//check to see if the response is already set, if not add the job?
		}
		if(stat != null)
			return new CommandResponse(true);
		else
			return new CommandResponse(false);
	}

	@Override
	public String getCommandName() {
		return "updateProcessNode("+nodeName+", "+message+")";
	}

}
