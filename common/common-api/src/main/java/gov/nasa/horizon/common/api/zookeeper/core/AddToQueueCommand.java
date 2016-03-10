package gov.nasa.horizon.common.api.zookeeper.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class AddToQueueCommand implements Command {

	String nodeName;
	String message;
	DistributedQueue queue;
	String response = null;
	Watcher watcher;
	Integer priority;
	
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(AddToQueueCommand.class);
	
	
	public AddToQueueCommand(String node, String msg, Integer pri, DistributedQueue dq, Watcher w){
		nodeName = node;
		message = msg;
		queue = dq;
		watcher = w;
		priority = pri;
	}
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {
		
		if(firstRun){
			firstRun = false;
			response = queue.offer(nodeName,priority, message.getBytes());
			log.debug("AddQueueCommand: got response: " + response);
			Stat s = zk.exists(response, watcher);
			if(s == null){
				//couldn't get the node to set a watch on it.
				log.debug("Couldn't get node to set watch, probably already being worked on.");
				KeeperException.NoNodeException nne = new KeeperException.NoNodeException("");
				throw new KeeperException.NoNodeException("NO_NODE");
			}
		}
		else{
			//setup retry code here.
			//check to see if the response is already set, if not add the job?
		}
		
		return new CommandResponse(response);
	}

	@Override
	public String getCommandName() {
		return "addtoQueue("+nodeName+", "+message+")";
	}

}
