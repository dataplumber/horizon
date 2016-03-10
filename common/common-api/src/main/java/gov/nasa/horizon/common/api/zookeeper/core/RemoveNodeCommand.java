package gov.nasa.horizon.common.api.zookeeper.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class RemoveNodeCommand implements Command {

	String nodeName;
	boolean response = false;

	
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(RemoveNodeCommand.class);
	
	
	public RemoveNodeCommand(String node){
		nodeName = node;
	}
	
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {

		if(firstRun){
			firstRun = false;
			zk.delete(nodeName, -1);
			response = true;
		}
		else{
			//setup retry code here.
			//check to see if the response is already set, if not add the job?
		}
		return new CommandResponse(response);
	}

	
	@Override
	public String getCommandName() {
		return "getNode("+nodeName+")";
	}

}
