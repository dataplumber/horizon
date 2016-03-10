package gov.nasa.horizon.common.api.zookeeper.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class GetNodeCommand implements Command {

	String nodeName;
	String response = null;
	Watcher watcher = null;
	
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(GetNodeCommand.class);
	
	
	public GetNodeCommand(String node){
		nodeName = node;
	}
	
	public GetNodeCommand(String node, Watcher w){
		nodeName = node;
		watcher = w;
	}
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {

		if(firstRun){
			firstRun = false;
			response = new String(zk.getData(nodeName,watcher, null));
		}
		else{
			//setup retry code here.
			if(response == null)
				response = new String(zk.getData(nodeName,watcher, null));
		}
		return new CommandResponse(response);
	}

	
	@Override
	public String getCommandName() {
		return "getNode("+nodeName+")";
	}

}
