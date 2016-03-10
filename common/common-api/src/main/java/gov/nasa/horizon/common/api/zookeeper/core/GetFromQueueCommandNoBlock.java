package gov.nasa.horizon.common.api.zookeeper.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class GetFromQueueCommandNoBlock implements Command {

	String nodeName;
	DistributedQueue queue;
	String response = null;
	
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(GetFromQueueCommandNoBlock.class);
	
	
	public GetFromQueueCommandNoBlock(String node, DistributedQueue dq){
		nodeName = node;
		queue = dq;
	}
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {
		if(firstRun){
			firstRun = false;
			try{
				response = new String(queue.poll(nodeName));
			}catch(NullPointerException npe){
				response = null;
			}
			catch(java.util.NoSuchElementException e){
				log.debug("No Such element...");
				response = null;
			}
		}
		else{
			//setup retry code here.
			//check to see if the response is already set, if not add the job?
			log.debug("Retry here...");
			
			
		}
		return new CommandResponse(response);
	}

	@Override
	public String getCommandName() {
		return "getFromQueueNoBlock("+nodeName+")";
	}

}
