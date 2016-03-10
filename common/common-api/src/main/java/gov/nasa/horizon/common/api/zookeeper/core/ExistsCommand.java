package gov.nasa.horizon.common.api.zookeeper.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import gov.nasa.horizon.common.api.zookeeper.api.Command;

public class ExistsCommand implements Command {

	String nodeName;
	boolean firstRun = true;
	private static Log log = LogFactory.getLog(ExistsCommand.class);
	
	
	public ExistsCommand(String node){
		nodeName = node;
	}
	
	@Override
	public CommandResponse execute(ZooKeeper zk) throws InterruptedException,KeeperException {
		Stat stat = null;
		
		if(firstRun){
			firstRun = false;
			stat = zk.exists(nodeName, false);
		}
		else{
			//setup retry code here.
			stat = zk.exists(nodeName, false);
		}
		if(stat==null)
			return new CommandResponse(false);
		else
			return new CommandResponse(true);
	}

	@Override
	public String getCommandName() {
		return "exists("+nodeName+")";
	}

}
