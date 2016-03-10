package gov.nasa.horizon.common.api.zookeeper.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class GetFromQueueCommand implements Command {

	String nodeName;
	DistributedQueue queue;
	String response = null;
	
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(GetFromQueueCommand.class);
	
	
	public GetFromQueueCommand(String node, DistributedQueue dq){
		nodeName = node;
		queue = dq;
	}
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {
		if(firstRun){
			firstRun = false;
			response = new String(queue.take(nodeName));
		}
		else{
			//setup retry code here.
			//check to see if the response is already set, if not add the job?
		}
		return new CommandResponse(response);
	}

	@Override
	public String getCommandName() {
		return "getFromQueue("+nodeName+")";
	}

}
