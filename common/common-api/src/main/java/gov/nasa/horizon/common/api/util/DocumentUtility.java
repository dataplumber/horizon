/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;


import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: DocumentUtility.java 244 2007-10-02 20:12:47Z axt $
 *
 */
public class DocumentUtility {
   protected DocumentUtility() {
   }
   
   
   public static DocumentBuilder getDocumentBuilder() {
      DocumentBuilder documentBuilder = null;
      
      try {
         DocumentBuilderFactory documentBuilderfactory = DocumentBuilderFactory.newInstance();
         documentBuilder = documentBuilderfactory.newDocumentBuilder();
      }
      catch(Exception exception) {}
      
      
      return documentBuilder;
   }
   
   
   public static List<Node> getDirectChildNodes(Node node, String name) {
      LinkedList<Node> nodes = new LinkedList<Node>();
      
      NodeList childList = node.getChildNodes();
      for(int childIndex = 0; childIndex < childList.getLength(); childIndex++) {
         Node child = childList.item(childIndex);
         if(child.getNodeName().equals(name)) {
            nodes.add(child);
         }
      }
      
      
      return nodes;
   }
   
   
   public static Node getDirectChildNode(Node node, String name) {
      Node targetNode = null;
      
      List<Node> nodes = getDirectChildNodes(node, name);
      if(nodes.size() == 1) {
         targetNode = nodes.get(0);
      }
      
      
      return targetNode;
   }
   
   
   public static boolean isDirectChildNodeDefined(Node node, String name) {
      Node targetNode = getDirectChildNode(node, name);
      return (targetNode != null) ? true : false;
   }
   
   
   public static Node getDirectAttribute(Node node, String name) {
      Node attribute = null;
      
      NamedNodeMap attributes = node.getAttributes();
      if(attributes != null) {
         attribute = attributes.getNamedItem(name);
      }
      
      
      return attribute;
   }
   
   
   public static Node prepareDirectNode(
      Document document,
      Node root,
      String name,
      String[] elementOrder
      ) {
      List<Node> nodes = DocumentUtility.getDirectChildNodes(root, name);
      Node node = null;
      if(nodes.size() > 0) {
         node = nodes.get(0);
      } else {
         // add the node
         node = document.createElement(name);
         
         // find out where to insert new node
         Node targetNode = null;
         boolean foundNewNodePosition = false;
         for(String elementName : elementOrder) {
            if(!foundNewNodePosition) {
               if(elementName.equals(name)) {
                  foundNewNodePosition = true;
               }
            } else {
               targetNode = getDirectChildNode(root, elementName);
               if(targetNode != null) {
                  break;
               }
            }
         }
         
         // insert new  node
         root.insertBefore(node, targetNode);
      }
      
      
      return node;
   }
}
