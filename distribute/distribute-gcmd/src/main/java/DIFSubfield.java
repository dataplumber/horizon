import java.util.LinkedList;

public class DIFSubfield {
	private String name;
	private LinkedList<DIFField> members = new LinkedList<DIFField>();
	
	public DIFSubfield(){}
	
	public DIFSubfield(String name){
		this.name = name;
	}
	
	public DIFSubfield(DIFField field){
		this.name = field.getName();
		members.add(field);
	}
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public LinkedList<DIFField> getMembers() {return members;}
	public void add(DIFField member){members.add(member);}
	public int size(){return members.size();}
	
	public DIFField find(String name){
		for(DIFField member: members)
			if(member.getName().equals(name))
				return member;
		return null;
	}
	
	public String toString(){
		String ret = "";
		for(int i = 0; i < members.size(); i++){
			if(members.get(i).getLineNumber() != -1)
				ret = ret + members.get(i).getLineNumber() + members.get(i);
			else
				ret = ret + members.get(i);
		}
		return ret;
	}

}
