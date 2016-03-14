package gov.nasa.horizon.common.api.zookeeper.api;

import gov.nasa.horizon.common.api.zookeeper.api.constants.JobPriority;
import gov.nasa.horizon.common.api.zookeeper.api.constants.RegistrationStatus;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.zookeeper.Watcher;

public interface ZkAccess {

   /*
    * ZK GENERIC API
    */
   /**
    * Checks if ZK instance has been started and connected
    * 
    * @return Boolean describing the state of this session (true: connected, false: not-connected)
    */
   public boolean needsInit();

   /**
    * @return Map with the following elements: [id:Long, password:String]. This should be saved to re-acquire a lost session.
    */
   public Map<String, Object> getSessionInfo();

   /**
    * Check node's existance
    * 
    * @param nodeName - the path of a node to check for
    * @return true if the node exists, false if it doens't. You'll need to use the 'getNode' function to get the data of the node.
    * @throws IOException if unable to connect
    */
   public boolean nodeExists(String nodeName) throws IOException; //check to see if a node exists

   /**
    * These functions get the 'data' portion of a znode. Has getter (getNode), setter(updateNode), create(createNode), delete(removeNode)
    * 
    * @param nodeName - the path of a node to retrieve data from.
    * @param msg input message
    * @return - Data associated with a node
    * @throws IOException if unable to connect
    */
   public String createNode(String nodeName, String msg) throws IOException;

   public String createNode(String nodeName, String msg, Boolean isEphemeral) throws IOException;
   
   public String getNode(String nodeName) throws IOException;

   public boolean removeNode(String nodeName) throws IOException;
   
   public boolean updateNode(String nodeName, String msg) throws IOException;

   /**
    * This function creates a node in the process tree. Usually done by ingest/archive engines to track and store job status for manager to check.
    * 
    * @param nodeName - Path of the node to create
    * @param msg - the data portion. Can be something like 'in process' or other code.
    * @return String representation of the node created. Should be the same as nodeName
    * @throws IOException if unable to connect
    */
   public String createProcessNode(String nodeName, String msg) throws IOException;

   /**
    * This function updates a node in the process tree. Usually done by ingest/archive engines to track and store job status for manager to check.
    * 
    * @param nodeName - Path of the node to update
    * @param msg - the data portion. Can be something like 'Success' or 'Failure' or other code.
    * @return boolean true if successful, false if not updated.
    * @throws IOException if unable to connect
    */
   public boolean updateProcessNode(String nodeName, String msg) throws IOException;

   /**
    * Same as 'getNode'
    * 
    * @param nodeName - the path of a node to retrieve data from.
    * @param watcher - Watcher code to attach to the znode (for code callback)
    * @return - Data associated with a node
    * @throws IOException if unable to connect
    */
   public String readProcessNode(String nodeName, Watcher watcher) throws IOException;

   public boolean removeProcessNode(String nodeName) throws IOException;

   public void closeConnection() throws IOException;

   /*
    * PRODUCT GENERATION API
    */

   /**
    * Registers new generator in the product generation queue
    * 
    * @param federation - name of manager federation used for categorizing
    * @param generatorName - intended name of generator
    * @return String path of the new generator node if successful
    * @throws IOException if unable to connect
    */
   public String registerGenerator(String federation, String generatorName) throws IOException;
   
   public String registerGenerator(String federation, String generatorName, String msg) throws IOException;

   /**
    * Gets the registration value of an engine at the given federation. Should be string representation of Engine object.
    * 
    * @param federation - name of manager federation used for categorizing
    * @param generatorName - generator name
    * @return String the value of the node at the registered location
    * @throws IOException if unable to connect
    */
   public String getGeneratorRegistration(String federation, String generatorName) throws IOException;
   
   /**
    * Checks registration status of the specified generator
    * 
    * @param federation - name of manager federation used for categorizing
    * @param generatorName - name of generator
    * @return Returns status of generator according to the RegistrationStatus enumeration
    * @throws IOException if unable to connect
    */
   public RegistrationStatus checkGeneratorRegistration(String federation, String generatorName) throws IOException;
   
   public void setGeneratorRegistration(String federation, String generatorName, String msg) throws IOException;

   /**
    * Adds a specified job to the product generation queue (with optional priority)
    * 
    * @param federation - name of manager federation used for categorizing
    * @param msg - message to insert into node
    * @param w - Zookeeper watcher instance
    * @return Path of newly added msg o a node
    * @throws IOException if unable to connect
    */
   public String addToGenerationQueue(String federation, String msg, Watcher w) throws IOException;

   public String addToGenerationQueue(String federation, String msg, JobPriority p, Watcher w) throws IOException;

   /**
    * Get next appropriate job from a generator queue (with or without blocking)
    * 
    * @param federation - name of federation to look up
    * @return the name of the generation jbo
    * @throws IOException if unable to connect
    */
   public String getGenerationJob(String federation) throws IOException;

   public String getGenerationJobNoBlock(String federation) throws IOException;

   public Date getGenLastPosted(String productType) throws IOException;
   
   public Boolean setGenLastPosted(String productType, Date lastPosted) throws IOException;
   
   public Date getGenLastCompleted(String productType) throws IOException;
   
   public Boolean setGenLastCompleted(String productType, Date lastPosted) throws IOException;
   
   /*
    * MANAGER API
    */

   /**
    * @param storageEngine the name of the storage location this engine is using
    * @param engineName the name of the engine that is registering
    * @return path of the node created via registration
    * @throws IOException if unable to connect
    */
   public String registerEngine(String storageEngine, String engineName) throws IOException;

   /**
    * @param storageEngine - the name of the storage location this engine is using
    * @param engineName - the name of the engine that is checking registration
    * @return true if the engine is still registered, false if not.
    * @throws IOException if unable to connect
    */
   public RegistrationStatus checkEngineRegistration(String storageEngine, String engineName) throws IOException;

   /**
    * This function adds a job to the Ingest queue.
    * 
    * @param storageEngine the string name of the storage engine.  this is to be read from/to
    * @param msg - the data package bundled with the node.
    * @param w the input watcher object
    * @return job name
    * @throws IOException if unable to connect
    */
   public String addToIngestQueue(String storageEngine, String msg, Watcher w) throws IOException; //manager to add ingest job

   public String addToIngestQueue(String storageEngine, String msg, JobPriority priority, Watcher w) throws IOException; //manager to add ingest job

   /**
    * Wrapper method to grab a job from the Ingest Q
    * 
    * @param storageEngine the string name of the storage engine.  this is to be read from/to
    * @return the data associated with job.
    * @throws IOException if unable to connect
    */
   public String getIngestJob(String storageEngine) throws IOException; //ingest to get job

   /**
    * Wrapper method to grab a job from the Ingest Q without blocking
    * 
    * @param storageEngine the string name of the storage engine.  this is to be read from/to
    * @return the data associated with job.
    * @throws IOException if unable to connect
    */
   public String getIngestJobNoBlock(String storageEngine) throws IOException; //ingest to get job

   /**
    * This function adds a job to the Archive queue.
    * 
    * @param storageEngine the string name of the storage engine this is to be read from/to
    * @param msg - the data package bundled with the node.
    * @param w the watcher object
    * @return a job name
    * @throws IOException if unable to connect
    */
   public String addToArchiveQueue(String storageEngine, String msg, Watcher w) throws IOException; //manager to add archive job

   public String addToArchiveQueue(String storageEngine, String msg, JobPriority priority, Watcher w) throws IOException; //manager to add archive job

   /**
    * Wrapper method to grab a job from the Archive Q
    * 
    * @param storageEngine name of the storage engine
    * @return the data associated with job.
    * @throws IOException if unable to connect
    */
   public String getArchiveJob(String storageEngine) throws IOException; //archive to get a job

   /**
    * Wrapper method to grab a job from the Archive Q without blocking
    * 
    * @param storageEngine name of the storage engine
    * @return the data associated with job.
    * @throws IOException if unable to connect
    */
   public String getArchiveJobNoBlock(String storageEngine) throws IOException; //archive to get a job

}
