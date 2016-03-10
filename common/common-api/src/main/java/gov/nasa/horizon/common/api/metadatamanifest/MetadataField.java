package gov.nasa.horizon.common.api.metadatamanifest;

public class MetadataField implements Comparable<MetadataField>{

	private String type = new String();
	private String name = new String();
	private String value = new String();
	private boolean required = false;
	
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public void setRequired(String required) {
		if(required.toUpperCase().equals("TRUE"))
			this.required=true;
		else
			this.required = false;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setValue(Double value) {
		if(value==null)
			this.value="";
		else
		{
			try{this.value = value.toString();}
			catch(NullPointerException npe)
			{
				this.value="";
			}
		}
	}
	public void setValue(Integer value) {
		if(value==null)
			this.value="";
		else
		{
			try{this.value = value.toString();}
			catch(NullPointerException npe)
			{
				this.value="";
			}
		}
	}
	public void setValue(Character value) {
		if(value==null)
			this.value="";
		else
		{
			try{this.value = value.toString();}
			catch(NullPointerException npe)
			{
				this.value="";
			}
		}
	}
	public void setValue(Float value) {
		if(value==null)
			this.value="";
		else
		{
			try{this.value = value.toString();}
			catch(NullPointerException npe)
			{
				this.value="";
			}
		}
	}
	public void setValue(Long value) {
		if(value==null)
			this.value="";
		else
		{
			try{this.value = value.toString();}
			catch(NullPointerException npe)
			{
				this.value="";
			}
		}
	}
	@Override
	public int compareTo(MetadataField arg0) {
		    return this.name.compareToIgnoreCase( arg0.name );
	}
}
