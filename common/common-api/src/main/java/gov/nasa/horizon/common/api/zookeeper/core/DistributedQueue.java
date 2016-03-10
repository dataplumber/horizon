package gov.nasa.horizon.common.api.zookeeper.core;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * 
 * A <a href="package.html">protocol to implement a distributed queue</a>.
 * 
 */

public class DistributedQueue {
	private static Log LOG = LogFactory.getLog(DistributedQueue.class);

    private final String dir;

    private ZooKeeper zookeeper;
    private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

    private final String prefix = "qn-";


    public DistributedQueue(ZooKeeper zookeeper, String dir, List<ACL> acl){
        this.dir = dir;

        if(acl != null){
            this.acl = acl;
        }
        this.zookeeper = zookeeper; 
    }



    /**
     * Returns a Map of the children, ordered by id.
     * @param watcher optional watcher on getChildren() operation.
     * @return map from id to child name for all children
     */
    //private TreeMap<Long,String> orderedChildren(String storageEngine, Watcher watcher) throws KeeperException, InterruptedException {
    	private TreeMap<String,String> orderedChildren(String storageEngine, Watcher watcher) throws KeeperException, InterruptedException {
//        TreeMap<Long,String> orderedChildren = new TreeMap<Long,String>();
    	TreeMap<String,String> orderedChildren = new TreeMap<String,String>();

        List<String> childNames = null;
        try{
        	LOG.debug("Getting child node list of "+dir +"/"+storageEngine);
            childNames = zookeeper.getChildren(dir +"/"+storageEngine, watcher);
        }catch (KeeperException.NoNodeException e){
            throw e;
        }

        for(String childName : childNames){
            try{
                //Check format
                if(!childName.regionMatches(0, prefix, 0, prefix.length())){
                    LOG.warn("Found child node with improper name: " + childName);
                    continue;
                }
                String suffix = childName.substring(prefix.length());
                //Long childId = new Long(suffix);
                String childId = suffix;
                orderedChildren.put(childId,childName);
            }catch(NumberFormatException e){
                LOG.warn("Found child node with improper format : " + childName + " " + e,e);
            }
        }

        return orderedChildren;
    }

    /**
     * Find the smallest child node.
     * @return The name of the smallest child node.
     */
    private String smallestChildName() throws KeeperException, InterruptedException {
        long minId = Long.MAX_VALUE;
        String minName = "";

        List<String> childNames = null;

        try{
            childNames = zookeeper.getChildren(dir, false);
        }catch(KeeperException.NoNodeException e){
            LOG.warn("Caught: " +e,e);
            return null;
        }

        for(String childName : childNames){
            try{
                //Check format
                if(!childName.regionMatches(0, prefix, 0, prefix.length())){
                    LOG.warn("Found child node with improper name: " + childName);
                    continue;
                }
                String suffix = childName.substring(prefix.length());
                long childId = Long.parseLong(suffix);
                if(childId < minId){
                    minId = childId;
                    minName = childName;
                }
            }catch(NumberFormatException e){
                LOG.warn("Found child node with improper format : " + childName + " " + e,e);
            }
        }


        if(minId < Long.MAX_VALUE){
            return minName;
        }else{
            return null;
        }
    }

    /**
     * Return the head of the queue without modifying the queue.
     * @return the data at the head of the queue.
     * @throws NoSuchElementException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] element(String storageEngine) throws NoSuchElementException, KeeperException, InterruptedException {
        TreeMap<String,String> orderedChildren;

        // element, take, and remove follow the same pattern.
        // We want to return the child node with the smallest sequence number.
        // Since other clients are remove()ing and take()ing nodes concurrently, 
        // the child with the smallest sequence number in orderedChildren might be gone by the time we check.
        // We don't call getChildren again until we have tried the rest of the nodes in sequence order.
        while(true){
            try{
                orderedChildren = orderedChildren(storageEngine, null);
            }catch(KeeperException.NoNodeException e){
                throw new NoSuchElementException();
            }
            if(orderedChildren.size() == 0 ) throw new NoSuchElementException();

            for(String headNode : orderedChildren.values()){
                if(headNode != null){
                    try{
                        return zookeeper.getData(dir+"/"+headNode, false, null);
                    }catch(KeeperException.NoNodeException e){
                        //Another client removed the node first, try next
                    }
                }
            }

        }
    }


    /**
     * Attempts to remove the head of the queue and return it.
     * @return The former head of the queue
     * @throws NoSuchElementException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] remove(String storageEngine) throws NoSuchElementException, KeeperException, InterruptedException {
        TreeMap<String,String> orderedChildren;
        // Same as for element.  Should refactor this.
        while(true){
            try{
                orderedChildren = orderedChildren(storageEngine,null);
            }catch(KeeperException.NoNodeException e){
                throw new NoSuchElementException();
            }
            if(orderedChildren.size() == 0) throw new NoSuchElementException();

            for(String headNode : orderedChildren.values()){
                String path = dir +"/"+ storageEngine +"/"+ headNode;
                try{
                    byte[] data = zookeeper.getData(path, false, null);
                    zookeeper.delete(path, -1);
                    return data;
                }catch(KeeperException.NoNodeException e){
                    // Another client deleted the node first.
                }
            }

        }
    }

    private class LatchChildWatcher implements Watcher {

        CountDownLatch latch;

        public LatchChildWatcher(){
            latch = new CountDownLatch(1);
        }

        public void process(WatchedEvent event){
            LOG.debug("Watcher fired on path: " + event.getPath() + " state: " + 
                    event.getState() + " type " + event.getType());
            latch.countDown();
        }
        public void await() throws InterruptedException {
            latch.await();
        }
    }

    /**
     * Removes the head of the queue and returns it, blocks until it succeeds.
     * @return The former head of the queue
     * @throws NoSuchElementException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] take(String storageEngine) throws KeeperException, InterruptedException {
        TreeMap<String,String> orderedChildren;
        // Same as for element.  Should refactor this.
        while(true){
            LatchChildWatcher childWatcher = new LatchChildWatcher();
            try{
                orderedChildren = orderedChildren(storageEngine,childWatcher);
            }catch(KeeperException.NoNodeException e){
                zookeeper.create(dir+"/"+ storageEngine, new byte[0], acl, CreateMode.PERSISTENT);
                continue;
            }
            if(orderedChildren.size() == 0){
                childWatcher.await();
                continue;
            }

            for(String headNode : orderedChildren.values()){
                String path = dir +"/"+ storageEngine +"/"+headNode;
                try{
                    byte[] data = zookeeper.getData(path, false, null);
                    zookeeper.delete(path, -1);
                    return data;
                }catch(KeeperException.NoNodeException e){
                    // Another client deleted the node first.
                }
            }
        }
    }

    
    private String create(String path, byte[] data) throws InterruptedException, KeeperException{
    	String s = null;
    	try {
    		LOG.debug("creating " + path);
			zookeeper.create(path, new byte[0], acl, CreateMode.PERSISTENT);
		} catch (KeeperException.NoNodeException e) {
			//recurse
			String newPath =  path.substring(0, path.lastIndexOf('/'));
			create(newPath, data);
			//must have returned successfully.
			LOG.debug("creating " + path);
			zookeeper.create(path, new byte[0], acl, CreateMode.PERSISTENT);
		}
    	return s;
    }
    
    /**
     * Inserts data into queue.
     * @param data
     * @return true if data was successfully added
     */
    public String offer(String storage, Integer priority, byte[] data) throws KeeperException, InterruptedException{

    	//adding this 'priority' to the queue will make it appear early or later depending on which priority is given (high number  = less priority)
    	String newPre = prefix +"" + priority + "-";
    	for(;;){
            try{

            	LOG.debug("adding to queue: " + dir+"/"+storage+"/"+newPre);
                String nodeName = zookeeper.create(dir+"/"+storage+"/"+newPre, data, acl, CreateMode.PERSISTENT_SEQUENTIAL);
                LOG.debug("got: "+nodeName);
                return nodeName;
            }catch(KeeperException.NoNodeException e){
                LOG.debug("Attempting to create parent directory.");
                String s = create(dir+"/"+storage, null);
                String nodeName = zookeeper.create(dir+"/"+storage +"/" + newPre, data, acl, CreateMode.PERSISTENT_SEQUENTIAL); //create the child directory
               return nodeName; 
            }
        }
    }

    /**
     * Returns the data at the first element of the queue, or null if the queue is empty.
     * @return data at the first element of the queue, or null.
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] peek(String storageEngine) throws KeeperException, InterruptedException{
        try{
            return element(storageEngine);
        }catch(NoSuchElementException e){
            return null;
        }
    }


    /**
     * Attempts to remove the head of the queue and return it. Returns null if the queue is empty.
     * @return Head of the queue or null.
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] poll(String storageEngine) throws KeeperException, InterruptedException {
        try{

            return remove(storageEngine);
        }catch(NoSuchElementException e){
            return null;
        }
    }



}
