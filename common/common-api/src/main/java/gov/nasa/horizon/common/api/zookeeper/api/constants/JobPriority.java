package gov.nasa.horizon.common.api.zookeeper.api.constants;

public enum JobPriority {

	HIGH(1),NORMAL(2),LOW(3),DEFAULT(2);
	
	private Integer val;
	
	public Integer getValue(){
		return val;
	}
	
	private JobPriority(Integer val){
		this.val = val;
	}
	
	public String toString(){
		return val.toString();
	}
}
