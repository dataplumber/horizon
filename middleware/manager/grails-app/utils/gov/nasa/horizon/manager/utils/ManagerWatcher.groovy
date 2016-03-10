/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.utils

import grails.util.Holders
import groovy.util.logging.Log4j
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.Watcher.Event.EventType

/**
 * Implementation of ZooKeeper Watcher callback object
 *
 * @author T. Huang
 * @version $Id: $
 */
@Log4j
public class ManagerWatcher implements Watcher {
   def grailsApplication = Holders.grailsApplication
   def managerWatcherService = grailsApplication.mainContext.managerWatcherService

   public ManagerWatcher() {
   }

   /**
    * Method called by ZooKeeper Watcher callback.
    */
   public void process(WatchedEvent event) {
      log.trace("ManagerWatcher: received event " + event.type + " " + event.state)
      /*
       * Watcher code to execute when the event is a node being removed. Usually happens when watching a Queued job node.
       */
      if (event.getType().equals(EventType.NodeDeleted)) {
         log.trace("ManagerWatcher: node removed " + event.path)

         /*
          * Confirms it's from a ingest/archive job queue. The other time this might happen in this current block:
          * We might set a watch on a node's data (readProcessNode), but it might already be done. The watch can trigger
          * when we delete the node, but we want to ignore that.
          */
         if (event.path.contains("queue") && (event.path.contains("ingest") || event.path.contains("archive"))) {
            managerWatcherService.handleZkWatcher(event.path, this)
         }
      }
      /*
       * Watcher code to execute when a watched node has data change (nominally when a job process goes from in process to complete).
       */
      else if (event.type.equals(EventType.NodeDataChanged)) {
         log.trace("ManagerWatcher: node changed " + event.path)
         String[] names = event.path.split("/")
         String productTypeName = names[-2]
         String productName = names[-1]
         managerWatcherService.handleZkWatcher(productTypeName + "/" + productName, this)
      }
   }
}
