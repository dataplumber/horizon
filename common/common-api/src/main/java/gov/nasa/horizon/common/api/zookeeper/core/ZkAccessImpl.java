package gov.nasa.horizon.common.api.zookeeper.core;

import gov.nasa.horizon.common.api.zookeeper.core.DistributedQueue;
import gov.nasa.horizon.common.api.zookeeper.api.Command;
import gov.nasa.horizon.common.api.zookeeper.api.ZkAccess;
import gov.nasa.horizon.common.api.zookeeper.api.constants.JobPriority;
import gov.nasa.horizon.common.api.zookeeper.api.constants.RegistrationStatus;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

public class ZkAccessImpl implements ZkAccess {

   private ZooKeeper zk = null;
   private Watcher watcher;
   private long sessionId = 0L;
   private byte[] sessionPass = null;
   private DistributedQueue archiveDQ;
   private DistributedQueue ingestDQ;
   private DistributedQueue generateDQ;
   private String hosts;

   private static JobPriority DEFAULT_PRIORITY = JobPriority.DEFAULT;

   private static Log log = LogFactory.getLog(ZkAccessImpl.class);
   private static int TIMEOUT = 120000; //2 minutes

   private static final String ARCHIVE_QUEUE = "/manager/queue/archive";
   private static final String INGEST_QUEUE = "/manager/queue/ingest";
   private static final String GENERATE_QUEUE = "/manager/queue/generate";

   private static final String JOB_PROCESS_ROOT = "/manager/jobs";
   private static final String ENGINE_REGISTRATION_ROOT = "/engines";
   private static final String GENERATE_REGISTRATION_ROOT = "/generators";
   
   private static final String GENERATE_STATUS = "/tie/status";
   private static final String LAST_POSTED = "lastposted";
   private static final String LAST_COMPLETED = "lastcompleted";
         

   public ZkAccessImpl(String hosts, Map<String, Object> session, Watcher watcher) throws IOException {
      this.watcher = watcher;
      this.hosts = hosts;
      if (session != null) {
         log.debug("Session information provided.");
         sessionId = (Long) session.get("id");
         String pass = (String) session.get("password");
         sessionPass = pass.getBytes();
      }
      if(watcher == null) {
         log.debug("Attempted to create new ZK .... but watcher is null");
      }
      else {
         log.debug("Attempted to create new ZK with watcher :"+watcher.toString());
      }
      try {
         if (sessionPass == null)
            zk = new ZooKeeper(hosts, TIMEOUT, watcher);
         else
            zk = new ZooKeeper(hosts, TIMEOUT, watcher, sessionId, sessionPass);
      } catch (IOException e) {
         throw e;
      }

      int count = 0;
      do {
         sessionId = zk.getSessionId();
         sessionPass = zk.getSessionPasswd();
         try {
            Thread.sleep(10000);
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            log.debug(e);
         }
         count++;
         if (count > 10) {
            throw new IOException("Could not connect to zookeeper instance.");
         }
      } while (zk.getState().equals(States.CONNECTING));

      if (zk.getState().equals(States.CLOSED)) {
         try {
            zk.close();
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
         }
         zk = null;
         throw new IOException("Could not connect to zookeeper instance. Connection Closed");
      }

      sessionId = zk.getSessionId();
      sessionPass = zk.getSessionPasswd();
      log.debug("***session info: " + sessionId + ":" + new String(sessionPass));
      log.debug("State: " + zk.getState().toString());
      //initialize the queues for this process.
      archiveDQ = new DistributedQueue(this.zk, ARCHIVE_QUEUE, null);
      ingestDQ = new DistributedQueue(this.zk, INGEST_QUEUE, null);
      generateDQ = new DistributedQueue(this.zk, GENERATE_QUEUE, null);
   }

   private void reconnect() throws IOException {
      try {
         log.debug("attempting reconnection...");
         if (sessionPass == null)
            zk = new ZooKeeper(hosts, TIMEOUT, null);
         zk = new ZooKeeper(hosts, TIMEOUT, null, sessionId, sessionPass);

      } catch (IOException e) {
         log.debug("Error reconnecting...", e);
         //			throw e;
      }
      //sessionId = 0;
      int count = 0;
      do {
         sessionId = zk.getSessionId();
         sessionPass = zk.getSessionPasswd();
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            log.debug(e);
         }
         count++;
         if (count > 10) {
            throw new IOException("Could not connect to zookeeper instance.");
         }
         //}while(sessionId == 0);
      } while (zk.getState().equals(States.CONNECTING));
      if (zk.getState().equals(States.CLOSED)) {
         log.debug("SESSION CLOSED");
         try {
            zk.close();
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            log.debug(e);
         }
         zk = null;
         throw new IOException("Could not connect to zookeeper instance. Session expired");
      } else {
         sessionId = zk.getSessionId();
         sessionPass = zk.getSessionPasswd();
      }

      if (sessionPass != null)
         log.debug("***session info: " + sessionId + ":" + new String(sessionPass));
      log.debug("State: " + zk.getState().toString());
      log.debug("Reconnection succeeded.");
   }

   public boolean needsInit() {
      if (zk == null)
         return true;
      else
         return false;
   }

   public Map<String, Object> getSessionInfo() {
      HashMap<String, Object> map = new HashMap<String, Object>(2);
      if (sessionId != 0L || sessionPass != null) {

         map.put("id", sessionId);
         map.put("password", new String(sessionPass));
         return map;
      }
      //else we need to get the new one.
      sessionId = zk.getSessionId();
      sessionPass = zk.getSessionPasswd();

      map.put("id", sessionId);
      map.put("password", new String(sessionPass));
      return map;
   }

   private CommandResponse runCommand(Command command) throws IOException {
      int retries = 5;
      int timeout = 1000;

      while (retries > 0) {
         try {
            CommandResponse cr = command.execute(zk);
            return cr;

         } catch (InterruptedException e) {
            //e.printStackTrace();
            log.debug("InteruptedException recieved while running " + command.getCommandName());

         } catch (KeeperException e) {
            //System.out.println("***** KEEPER EXCEPTION DURING COMMAND!");
            log.debug("Error processing command:" + e.getMessage());
            //e.printStackTrace();
            if (e.code().equals(Code.CONNECTIONLOSS)) {
               //this wasn't working as expected, because the zk client handles connection loss scenarios. we're just waiting for it to kick in.
               //reconnect();
               log.warn("Connection loss...");
               //We do this so that we get as many chances to reconnect within the client timeout period. 
               //If we have a timeout of 12 seconds, and kept it at waiting 1 second between attempts,
               //we'd burn though our retry allotment with 6 seconds left for zk to connect to a quorum member.
               //zk won't even try until 4 seconds have elapsed in this scenario, so we want to wait at least:
               //12 (timeout) /5 (retries) seconds so we try when we've presumably connected to another keeper.
               timeout = TIMEOUT / retries;
            } else if (e.code().equals(Code.NONODE)) {
               if (e.getPath().equals("NO_NODE")) {
                  //watcher couldn't be set...
                  //but the write to the queue worked
                  throw new IOException("WATCHER_NOT_SET");
               }
               timeout = 1000;
            } else if (e.code().equals(Code.SESSIONEXPIRED)) {
               log.info("Zookeeper Session Expired: Reconnecting using sessionId [" + sessionId + "]");
               timeout = 1000;
               reconnect();
            }
         }
         //try it again.
         try {
            Thread.sleep(timeout);
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         retries--;
      }
      throw new IOException("Max attempts failed; Could not run command " + command.getCommandName());
   }

   public String createNode(String nodeName, String msg) throws IOException {
      return createNode(nodeName, msg, false);
   }

   public String createNode(String nodeName, String msg, Boolean isEphemeral) throws IOException {
      if(!nodeName.startsWith("/"))
         nodeName = JOB_PROCESS_ROOT + "/" + nodeName;
      CreateProcessNodeCommand cpn = new CreateProcessNodeCommand(nodeName, msg);
      CommandResponse cr = runCommand(cpn);
      return cr.getStringResponse();
   }
   
   public String getNode(String nodeName) throws IOException {
      GetNodeCommand gnc = new GetNodeCommand(nodeName);
      CommandResponse cr = runCommand(gnc);
      if (cr == null)
         return null;
      return cr.getStringResponse();
   }
   
   public boolean removeNode(String nodeName) throws IOException {
      String address = nodeName;
      RemoveNodeCommand rnc = new RemoveNodeCommand(address);
      CommandResponse cr = runCommand(rnc);
      return cr.isBooleanResponse();
   }
   
   public boolean updateNode(String nodeName, String msg) throws IOException {
      String address = nodeName;
      UpdateProcessNodeCommand cpn = new UpdateProcessNodeCommand(address, msg);
      CommandResponse cr = runCommand(cpn);
      return cr.isBooleanResponse();
   }
   
   public boolean nodeExists(String nodeName) throws IOException {
      ExistsCommand ec = new ExistsCommand(nodeName);
      CommandResponse cr = runCommand(ec);
      return cr.isBooleanResponse();
   }

   
   public boolean processNodeExists(String nodeName) throws IOException {

      String pnode = JOB_PROCESS_ROOT + "/" + nodeName;
      log.debug("Checking path: " + pnode);
      ExistsCommand ec = new ExistsCommand(pnode);
      CommandResponse cr = runCommand(ec);
      return cr.isBooleanResponse();
   }

   public String addToIngestQueue(String storageEngine, String msg, Watcher w) throws IOException {
      return addToIngestQueue(storageEngine, msg, DEFAULT_PRIORITY, w);
   }

   public String addToIngestQueue(String storageEngine, String msg, JobPriority priority, Watcher w) throws IOException {
      AddToQueueCommand a2q = new AddToQueueCommand(storageEngine, msg, priority.getValue(), ingestDQ, w);
      CommandResponse cr = runCommand(a2q);
      if (cr == null)
         return null;
      return cr.getStringResponse();
   }

   public String addToArchiveQueue(String storageEngine, String msg, Watcher w) throws IOException {
      return addToArchiveQueue(storageEngine, msg, DEFAULT_PRIORITY, w);
   }

   public String addToArchiveQueue(String storageEngine, String msg, JobPriority priority, Watcher w) throws IOException {
      AddToQueueCommand a2q = new AddToQueueCommand(storageEngine, msg, priority.getValue(), archiveDQ, w);
      CommandResponse cr = runCommand(a2q);
      if (cr == null)
         return null;
      return cr.getStringResponse();
   }

   public String getArchiveJob(String storageEngine) throws IOException {
      GetFromQueueCommand gfq = new GetFromQueueCommand(storageEngine, archiveDQ);
      CommandResponse cr = runCommand(gfq);
      return cr.getStringResponse();
   }

   public String getIngestJob(String storageEngine) throws IOException {
      GetFromQueueCommand gfq = new GetFromQueueCommand(storageEngine, ingestDQ);
      CommandResponse cr = runCommand(gfq);
      return cr.getStringResponse();
   }

   public String getArchiveJobNoBlock(String storageEngine) throws IOException {
      GetFromQueueCommandNoBlock gfq = new GetFromQueueCommandNoBlock(storageEngine, archiveDQ);
      CommandResponse cr = runCommand(gfq);
      return cr.getStringResponse();
   }

   public String getIngestJobNoBlock(String storageEngine) throws IOException {
      GetFromQueueCommandNoBlock gfq = new GetFromQueueCommandNoBlock(storageEngine, ingestDQ);
      CommandResponse cr = runCommand(gfq);
      return cr.getStringResponse();
   }

   public String createProcessNode(String nodeName, String msg) throws IOException {
      String address = JOB_PROCESS_ROOT + "/" + nodeName;
      CreateProcessNodeCommand cpn = new CreateProcessNodeCommand(address, msg);
      CommandResponse cr = runCommand(cpn);
      return cr.getStringResponse();
   }

   public boolean updateProcessNode(String nodeName, String msg) throws IOException {
      String address = JOB_PROCESS_ROOT + "/" + nodeName;
      UpdateProcessNodeCommand cpn = new UpdateProcessNodeCommand(address, msg);
      CommandResponse cr = runCommand(cpn);
      return cr.isBooleanResponse();
   }

   public String readProcessNode(String nodeName, Watcher watch) throws IOException {
      String address = JOB_PROCESS_ROOT + "/" + nodeName;
      GetNodeCommand gnc = new GetNodeCommand(address, watch);
      CommandResponse cr = runCommand(gnc);
      return cr.getStringResponse();
   }

   public boolean removeProcessNode(String nodeName) throws IOException {
      String address = JOB_PROCESS_ROOT + "/" + nodeName;
      RemoveNodeCommand rnc = new RemoveNodeCommand(address);
      CommandResponse cr = runCommand(rnc);
      return cr.isBooleanResponse();
   }

   public void closeConnection() throws IOException {
      try {
         sessionId = zk.getSessionId();
         sessionPass = zk.getSessionPasswd();
         zk.close();
      } catch (InterruptedException e) {
         log.error("Error closing zookeeper instance.");
         e.printStackTrace();
      }
   }

   @Override
   public String registerEngine(String storageEngine, String engineName) throws IOException {
      String nodeRegister = ENGINE_REGISTRATION_ROOT + "/" + storageEngine + "/" + engineName;
      CreateProcessNodeCommand cpn = new CreateProcessNodeCommand(nodeRegister, "Registered for processing at " + new Date().getTime(), true);
      CommandResponse cr = runCommand(cpn);
      return cr.getStringResponse();

   }

   @Override
   public RegistrationStatus checkEngineRegistration(String storageEngine, String engineName) throws IOException {
      String nodeRegister = ENGINE_REGISTRATION_ROOT + "/" + storageEngine + "/" + engineName;
      if (!nodeExists(nodeRegister)) {
         return RegistrationStatus.OFFLINE;
      } else {
         String s = getNode(nodeRegister);
         log.trace("returned data from registration node: " + s);
         if (s == null) {
            log.error("Registration node at \"" + nodeRegister + "\" has no data!");
            return RegistrationStatus.PAUSED;
         }
         s = s.trim();
         if (s.toUpperCase().equals("PAUSE") || s.toUpperCase().equals("PAUSED"))
            return RegistrationStatus.PAUSED;
         else
            return RegistrationStatus.READY;
      }
   }
   
   
   /*
    * Product Generation API
    */
   public String registerGenerator(String federation, String generatorName) throws IOException {
      return registerGenerator(federation, generatorName, "Registered for generation at " + new Date().getTime());
   }
   
   public String registerGenerator(String federation, String generatorName, String msg) throws IOException {
      String nodeRegister = GENERATE_REGISTRATION_ROOT + "/" + federation + "/" + generatorName;
      CreateProcessNodeCommand cpn = new CreateProcessNodeCommand(nodeRegister, msg, true);
      CommandResponse cr = runCommand(cpn);
      return cr.getStringResponse();
   }

   public RegistrationStatus checkGeneratorRegistration(String federation, String generatorName) throws IOException {
      String nodeRegister = GENERATE_REGISTRATION_ROOT + "/" + federation + "/" + generatorName;
      if (!nodeExists(nodeRegister)) {
         return RegistrationStatus.OFFLINE;
      } else {
         String s = getNode(nodeRegister);
         log.trace("returned data from registration node: " + s);
         if (s == null) {
            log.error("Registration node at \"" + nodeRegister + "\" has no data!");
            return RegistrationStatus.PAUSED;
         }
         //s = s.trim();
         if (s.contains("__PAUSE__") || s.contains("__PAUSED__"))
            return RegistrationStatus.PAUSED;
         else
            return RegistrationStatus.READY;
         
      }
   }
   
   public String getGeneratorRegistration(String federation, String generatorName) throws IOException {
      String nodeRegister = GENERATE_REGISTRATION_ROOT + "/" + federation + "/" + generatorName;
      if (!nodeExists(nodeRegister)) {
         throw new IOException("No engine found at location: "+nodeRegister);
      } else {
         String s = getNode(nodeRegister);
         log.trace("returned data from registration node: " + s);
         if (s == null) {
            log.warn("Registration node at \"" + nodeRegister + "\" has no data!");
         }
         return s;
         
      }
   }
   
   public void setGeneratorRegistration(String federation, String generatorName, String msg) throws IOException {
      String nodeRegister = GENERATE_REGISTRATION_ROOT + "/" + federation + "/" + generatorName;
      if (!nodeExists(nodeRegister)) {
         throw new IOException("No engine found at location: "+nodeRegister);
      } else {
         try {
            zk.setData(nodeRegister, msg.getBytes(), -1);
         } catch (KeeperException e) {
            log.error("KeeperException: ", e);
            e.printStackTrace();
         } catch (InterruptedException e) {
            log.error("InteruptedException Exception: ", e);
            e.printStackTrace();
         }
      }
   }


   public String addToGenerationQueue(String federation, String msg , Watcher w) throws IOException {
      return addToGenerationQueue(federation, msg, DEFAULT_PRIORITY, w);
   }

   public String addToGenerationQueue(String federation, String msg, JobPriority p, Watcher w) throws IOException {
      AddToQueueCommand a2q = new AddToQueueCommand(federation, msg, p.getValue(), generateDQ, w);
      CommandResponse cr = runCommand(a2q);
      if (cr == null)
         return null;
      return cr.getStringResponse();
   }


   public String getGenerationJob(String federation) throws IOException {
      GetFromQueueCommand gfq = new GetFromQueueCommand(federation, generateDQ);
      CommandResponse cr = runCommand(gfq);
      return cr.getStringResponse();
   }

   public String getGenerationJobNoBlock(String federation) throws IOException {
      GetFromQueueCommandNoBlock gfq = new GetFromQueueCommandNoBlock(federation, generateDQ);
      CommandResponse cr = runCommand(gfq);
      return cr.getStringResponse();
   }
   
   public Date getGenLastPosted(String productType) throws IOException{
      Date response = null;
      String nodeName = GENERATE_STATUS + "/" + productType + "/" + LAST_POSTED;
      if(nodeExists(nodeName)) {
         String nodeData = getNode(nodeName);
         if(nodeData != null)
            response = new Date(Long.valueOf(nodeData));
      }
      return response;
   }
   
   public Boolean setGenLastPosted(String productType, Date lastPosted) throws IOException{
      String nodeName = GENERATE_STATUS + "/" + productType + "/" + LAST_POSTED;
      String msg = String.valueOf(lastPosted.getTime());
      Boolean status = false;
      
      if(!nodeExists(nodeName)) {
         //Returns string (path of creation or null)
         String response = createNode(nodeName, msg);
         status = (response != null && response != "") ? true : status;
      }
      else {
         Date current = getGenLastCompleted(productType);
         if(current == null || lastPosted.after(current)) 
            status = updateNode(nodeName, msg);
      }
      return status;
   }
   
   public Date getGenLastCompleted(String productType) throws IOException{
      Date response = null;
      String nodeName = GENERATE_STATUS + "/" + productType + "/" + LAST_COMPLETED;
      if(nodeExists(nodeName)) {
         String nodeData = getNode(nodeName);
         if(nodeData != null)
            response = new Date(Long.valueOf(nodeData));
      }
      return response;
   }
   
   public Boolean setGenLastCompleted(String productType, Date lastPosted) throws IOException{
      String nodeName = GENERATE_STATUS + "/" + productType + "/" + LAST_COMPLETED;
      String msg = String.valueOf(lastPosted.getTime());
      Boolean status = null;
      
      if(!nodeExists(nodeName)) {
         //Returns string (path of creation or null)
         String response = createNode(nodeName, msg);
         status = (response != null && response != "") ? true : status;
      }
      else {
         //Return bool (success or not)
         status = updateNode(nodeName, msg);
      }
      return status;
   }
}
