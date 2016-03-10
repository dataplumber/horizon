import java.util.LinkedList;

/**
 * Class to represent a DIF field containing "slots" for each 
 * one of its subfields. DIFFields are represented with an 
 * n-ary tree structure with DIFRecords holding the highest level 
 * complex DIFFields, and each DIFField is capable of having 
 * subfields that are DIFFields as well.
 * 
 * Terminal DIFFields do not have any subfields and thus will 
 * have data associated with it.
 * 
 * Complex DIFFields are composed of other complex DIFFields 
 * and/or terminal DIFFields and do not have any data associated 
 * with it 
 * 
 **/
public class DIFField{
	/**Name of the DIFField**/
	protected String name; 
	/**Minimum occurrence of DIFField. For validation.**/
	protected String minOccurs; 
	/**Maximum occurrence of DIFField. For validation.**/
	protected String maxOccurs; 
	/**The type of DIFField. For validation but not really important**/
	protected String type;
	/**The subfields of the current DIFField**/
	protected LinkedList<DIFSubfield> subfields;
	/**The String data the DIFField contains**/
	protected String data;
	protected int whitespace = 2;
	public int lineNumber = -1;
	
	/**Default Constructor**/
	public DIFField(){this(null, null, null, null, new LinkedList<DIFSubfield> (), null);}
	
	/**Constructor**/
	public DIFField(String name){this(name, null, null, null, new LinkedList<DIFSubfield> (), null);}
	
	/**Constructor**/
	public DIFField(String name, String data){this(name, null, null, null, new LinkedList<DIFSubfield> (), data);}
	
	/**Constructor**/
	public DIFField(String name, String minOccurs, String maxOccurs, String type, 
			LinkedList<DIFSubfield>  subfields, String data){
		this.name = name;
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
		this.type = type;
		this.subfields = subfields;
		this.data = data;
	}
	
	public String getName(){return name;}
	public String getMinOccurs(){return minOccurs;}
	public String getMaxOccurs(){return maxOccurs;}
	public String getType() {return type;}
	public LinkedList<DIFSubfield>   getSubfields(){return subfields;}
	public int getLineNumber() {return lineNumber;}
	public String getData(){return data;}

	public void setName(String name){this.name = name;}
	public void setMinOccurs(String minOccurs){this.minOccurs = minOccurs;}
	public void setMaxOccurs(String maxOccurs){this.maxOccurs = maxOccurs;}
	public void setType(String type) {this.type = type;}
	public void setSubfields(LinkedList<DIFSubfield>  subfields){this.subfields = subfields;}
	public void setLineNumber(int lineNumber) {this.lineNumber = lineNumber;}
	
	/**
	 * Finds the subfield with the name in question. Return null if not found.
	 * @param name the name in question
	 * @return the subfield with the name inputed or null if not found 
	 **/
	public DIFSubfield findSubfieldList(String name){
		for(DIFSubfield sub : subfields)
			if(sub.getName().equals(name))
				return sub;
		return null;
	}
	
	/**
	 * Method to add a new subfield. Will create new subfield slot if one does not already exist
	 * @param child the new subfield
	 **/
	public void add(DIFSubfield child){
		DIFSubfield subfield = findSubfieldList(child.getName());
		//if subfield category does not already exist create one
		if(subfield == null)
			subfields.add(child);
		//if subfield category does exist
		else
		    subfield.add(new DIFField(child.getName(), null, null, null, subfields, null));
	}
	
	/**
	 * Method to add a list of new subfields. Will create new subfield slot if one does not already exist
	 * @param children the list of new subfields
	 **/
	public void add(LinkedList<DIFSubfield> children){
		for(DIFSubfield child : children)
			add(child);
	}
	
	/**
	 * Method to add a new subfield at a specific index. Will 
	 * create new subfield slot if one does not already exist
	 * @param index the index to insert the new subfield
	 * @param child the new subfield
	 **/
	public void add(int index, DIFSubfield child){
		DIFSubfield subfield = findSubfieldList(child.getName());
		//if subfield category does not already exist create one
		if(subfield == null)
			subfields.add(index, child);
		//if subfield category does exist
		else
		    subfield.add(new DIFField(child.getName(), null, null, null, subfields, null));
	}
	
	/**
	 * Method to return a copy of the current DIFField.
	 * @return a copy of the current DIFField
	 **/
	public DIFField copy(){
		String newName = null, newMinOccurs = null, newMaxOccurs = null, newType = null, newData = null;
		if(name != null) newName = name;
		if(minOccurs != null) newMinOccurs = minOccurs;
		if(maxOccurs != null) newMaxOccurs = maxOccurs;
		if(type != null) newType = type;
		if(data != null) newData = data;
		return new DIFField(newName, newMinOccurs, newMaxOccurs, 
				newType, new LinkedList<DIFSubfield>(subfields), newData);
	}
	
	/**
	 * Method to determine if the DIFField is empty
	 * @return true if all the fields are null, false otherwise
	 **/
	public boolean isEmpty(){
		if (name == null && minOccurs == null && maxOccurs == null && type == null && subfields.isEmpty())
			return true;
		return false;
	}
	
	/**
	 * Return the String with the number of whitespace equal to the attribute whitespace
	 * @return a String with number of whitespaces equal to the attribute whitespace
	 **/
	protected String whitespace(){
		String ret = "";
		for(int i = 0; i < whitespace; i++)
			ret = ret + " ";
		return ret;
	} 
	
	/**
	 * Returns the size of the subfields
	 * @return the size of the subfields
	 **/
	public int size(){
		return subfields.size();
	}
	
	/**
	 * @param minOccurs the minimum occurrence for the DIF field.
	 * @return true is given number is a valid minimum, false otherwise
	 **/
	public boolean isValidMinimum(int minOccurs){
		int thisMinOccurs = Integer.parseInt(this.minOccurs);
		if(minOccurs >= thisMinOccurs)
			return true;
		return false;
	}
	
	/**
	 * @param maxOccurs the maximum occurrence for the DIF field.
	 * @return true is given number is a valid maximum, false otherwise
	 **/
	public boolean isValidMaximum(int maxOccurs){
		if(this.maxOccurs.equals("unbounded"))
			return true;
		int thisMaxOccurs = Integer.parseInt(this.maxOccurs);
		if(maxOccurs <= thisMaxOccurs)
			return true;
		return false;
	}
	
	/**
	 * @return a LinkedList of Types representing the subclasses necessary for debugging 
	 **/
	public LinkedList<Type> count(){
		LinkedList<Type> ret = new LinkedList<Type>();
		for(DIFSubfield sub : subfields)
			  ret.add(new Type(sub.getName(), sub.size()));
		  return ret;
	}
	
	/**
	 * @param name the name in question
	 * @return the index of the given name
	 **/
	public int indexOf(String name){
		for(int i = 0; i < subfields.size(); i++)
			if(subfields.get(i).getName().equals(name))
				return i;
		return -1;
	}
	
	/**
	 * Orders the fields based on the names
	 * @param names		how to order the fields
	 */
	public void toOrder(String[]names){
		LinkedList<DIFSubfield> newSubfields = new LinkedList<DIFSubfield>();
		for(String name : names){
			int index = indexOf(name);
			if(index != -1)
				newSubfields.add(subfields.get(index));
		}
		subfields = newSubfields;
	}
	
	/**
	 * @return String representation of the DIFField and its 
	 * constituents without regard to minimum and maximum occurrences
	 * and type  
	 **/
	public String toString(){
		String ret = ""; 
		if(lineNumber != -1)
			ret = ret + lineNumber;
		
		String innerData = "";
		
		if(data != null)
			innerData = data;
		else{
			for(DIFSubfield sub: subfields){
				if(sub.size() != 0)
					for(DIFField member : sub.getMembers()){
						member.whitespace = this.whitespace + 2;
						innerData = innerData + member;
					}
			}
		}
		
		if(!Converter.isEmpty(innerData)){	
			ret = ret + whitespace() + "<" + name + ">";
			
			if(data != null)
				ret = ret + innerData;
			else
				ret = ret + "\n" + innerData + whitespace();
			
			ret = ret + "</" + name + ">\n";
		}
		return ret;
	}
	
	/**
	 * @return String representation of the DIFField and its 
	 * constituents including minimum and maximum occurrences
	 * and type  
	 **/
	public String toString(int i){
		String ret = "Name: " + name + "\n" 
		+ "MinOccurs: " + minOccurs + "\n"
		+ "MaxOccurs: " + maxOccurs + "\n";
		if(type != null)
			ret = ret + "Type: " + type + "\n";
		if(!subfields.isEmpty()){
			ret = ret + "Subfields:";
			for(DIFSubfield subfield: subfields)
				ret = ret + " " + subfield.getName();
		}
		for(DIFSubfield subfield: subfields)
			for(DIFField field : subfield.getMembers())
				ret = ret + "\n\n" +field.toString(0);
		return ret + "\n";
	}
}
