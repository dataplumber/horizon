package gov.nasa.horizon.common.api.metadatamanifest;

import gov.nasa.horizon.common.api.metadatamanifest.Constant.ActionType;
import gov.nasa.horizon.common.api.metadatamanifest.Constant.ObjectType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MetadataManifest {

	
	String FIELD = "field";
	String manifest; //original XML
	String note;
	ActionType type; //UPDATE, DELETE, CREATE, etc see CONSTANTS
	ObjectType object; //DATASET, GRANULE, COLLECTION, etc, see CONSTANTS
	Set<MetadataField> fields = new HashSet<MetadataField>();
	
	
	public MetadataManifest(){
		
	}
	
	public MetadataManifest(String xml) throws MetadataManifestException{
		this.setManifest(xml);
		parseXml(xml);	
	}
	

	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getManifest() {
		return manifest;
	}
	public void setManifest(String manifest) {
		this.manifest = manifest;
	}
	public String getActionType() {
		return type.toString();
	}
	public void setType(ActionType type) {
		this.type = type;
	}
	public String getObjectType() {
		return object.toString();
	}
	public void setObject(ObjectType object) {
		this.object = object;
	}
	public Set<MetadataField> getFields() {
		return fields;
	}
	public void setFields(Set<MetadataField> fields) {
		this.fields = fields;
	}
	
	public String generateXml(){
		StringBuilder sb = new StringBuilder();
		
		
		//asSortedList(this.fields);
		
		
		sb.append("<"+this.getObjectType().toLowerCase()+ " type=\""+this.getActionType().toLowerCase()+"\">\n");
		for(MetadataField f : asSortedList(this.fields)){
			sb.append("\t<field name=\""+f.getName()+"\" type=\""+f.getType()+"\" required=\""+f.isRequired()+"\">"+StringEscapeUtils.escapeHtml(f.getValue())+"</field>\n");
		}
		sb.append("</"+this.getObjectType().toLowerCase()+">");
		
		
		return sb.toString();
	}
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	public void parseXml(String xml) throws MetadataManifestException {
		this.setManifest(xml);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc=null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  try {
			 doc = db.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//setup complete
		 doc.getDocumentElement().normalize();
		 //System.out.println("Root element " + doc.getDocumentElement().getNodeName());
		 if(doc.getDocumentElement().getNodeName().equals("dataset"))
			 this.object = ObjectType.DATASET;
		 else if(doc.getDocumentElement().getNodeName().equals("granule"))
			 this.object=ObjectType.GRANULE;
		 else if (doc.getDocumentElement().getNodeName().equals("collection"))
			 this.object = ObjectType.COLLECTION;
		 else if (doc.getDocumentElement().getNodeName().equals("element"))
			 this.object = ObjectType.ELEMENT;
		 else if (doc.getDocumentElement().getNodeName().equals("source"))
			 this.object = ObjectType.SOURCE;
		 else if (doc.getDocumentElement().getNodeName().equals("sensor"))
			 this.object = ObjectType.SENSOR;
		 else if (doc.getDocumentElement().getNodeName().equals("provider"))
			 this.object = ObjectType.PROVIDER;
		 else if (doc.getDocumentElement().getNodeName().equals("project"))
			 this.object = ObjectType.PROJECT;
		 else if (doc.getDocumentElement().getNodeName().equals("contact"))
			 this.object = ObjectType.CONTACT;
		 else{
			 throw new MetadataManifestException("Unsupported Object type:"+doc.getDocumentElement().getNodeName()+". Must be one of type dataset, granule, or collection.");
		 }
		 
		 if(doc.getDocumentElement().getAttribute("type").equals("update"))
			 this.type =ActionType.UPDATE;
		 else if(doc.getDocumentElement().getAttribute("type").equals("create"))
			 this.type=ActionType.CREATE;
		 else if (doc.getDocumentElement().getAttribute("type").equals("delete"))
			 this.type = ActionType.DELETE;
		 else if (doc.getDocumentElement().getAttribute("type").equals("list"))
			 this.type = ActionType.LIST;
		 else if (doc.getDocumentElement().getAttribute("type").equals("template"))
			 this.type = ActionType.TEMPLATE;
		 else{
			 throw new MetadataManifestException("Unsupported operation type:"+doc.getDocumentElement().getAttribute("type")+". Must be one of type update,create, or delete.");
		 }
		 
		 NodeList nodeLst = doc.getElementsByTagName(FIELD);
		 for (int s = 0; s < nodeLst.getLength(); s++) {
			    Node node = nodeLst.item(s);
			    if (node.getNodeType() == Node.ELEMENT_NODE) {
			    	Element fieldNode = (Element) node;
			    	MetadataField mdf = new MetadataField();
			    	
			    	mdf.setValue(fieldNode.getTextContent());
			    	//System.out.println("val: " + fieldNode.getTextContent());
			    	
			    	mdf.setType(fieldNode.getAttribute("type"));
			    	mdf.setName(fieldNode.getAttribute("name"));
			    	mdf.setRequired(fieldNode.getAttribute("required"));
			    	this.getFields().add(mdf);
			    }

			  }

	}

	public boolean hasField(String fName) {
		if(fields.isEmpty())
			return false;
		
		for(MetadataField f : fields){
			if(f.getName().equals(fName))
				return true;
		}
		return false;
	}	
	
}
