import java.util.LinkedList;

public class DIFRecord{
  private LinkedList<DIFSubfield> subfields = new LinkedList<DIFSubfield>();
  private String name = "";
  
  public DIFRecord(){}
  
  public DIFRecord(DIFSubfield field){
	  subfields.add(field);
	  if(field.getName().equals(Global.ENTRY_ID_ACTUAL_NAME)){
		  if(field.size()!= 0)
			  this.name = field.getMembers().get(0).getData();
	  }
  }
  public DIFRecord(LinkedList<DIFSubfield> subfields){
	  this.subfields = subfields;
  }
  
  public LinkedList<DIFSubfield> getSubfields(){return subfields;}
  public void setSubfields(LinkedList<DIFSubfield> subfields){this.subfields = subfields;}
  public String getName(){return name;}
  public void setName(String name){this.name = name;}
  public void setNameToID(String entryIDName){
	  DIFSubfield sub = find(entryIDName);
	  if(sub != null && sub.size() != 0)
		  this.name = sub.getMembers().get(0).getName();
  }
  
  public DIFSubfield find(String name){
	  for(DIFSubfield sub : subfields)
		  if(sub.getName().equals(name))
			  return sub;
	  return null;
  }
  
  public void addField(DIFField field){
	  DIFSubfield sub = find(field.getName());
	  if(sub != null)
		  sub.add(field);
	  else
		  subfields.add(new DIFSubfield(field));
  }
  
  public void addField(LinkedList<DIFField> newFields){
	  for(DIFField field : newFields)
		  addField(field);
  }

  public void addSubfields(LinkedList<DIFSubfield> newSubfields){
	for(DIFSubfield sub : newSubfields)
		for(DIFField field : sub.getMembers())
			addField(field);
  }

  public LinkedList<Type> count(){
	  LinkedList<Type> ret = new LinkedList<Type>();
	  for(DIFSubfield sub : subfields)
		  ret.add(new Type(sub.getName(), sub.size()));
	  return ret;
  }
  
  public String toString(){
    String returnString = "<?xml version=" + '"' + Global.XML_VERSION + '"' + " encoding=" + '"' + Global.ENCODING + '"' + "?>\n";
    returnString = returnString + "<DIF xmlns=" + '"' + Global.DIF_XML_NS + '"' + " xmlns:dif=" + '"' + Global.DIF_XML_NS_DIF + '"' 
    + " xmlns:xsi=" + '"' + Global.DIF_XML_NS_XSI + '"' + " xsi:schemaLocation=" + '"' + Global.DIF_XSI_SCHEMA_LOCATION + '"' + ">\n"; 
    for (int i = 0; i < subfields.size(); i++)
      returnString = returnString + subfields.get(i).toString();
    return returnString + "</DIF>\n";
  }
}

class Type{
	public String name;
	public int occurrence;
	
	public Type(String name, int occurrence){
		this.name = name;
		this.occurrence = occurrence;
	}
	
	public String toString(){return "Name: " + name + " Occurrence: " + occurrence;}
}

