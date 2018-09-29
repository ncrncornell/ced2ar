package edu.cornell.ncrn.ced2ar.eapi.prov.oldmodel;

import java.io.Serializable;

public class Activity implements Serializable{
	private static final long serialVersionUID = 8577712043977801082L;
	
	public static final String NODE_PROV_ACTIVITY = "prov:activity";
	public static final String PROV_ATTR_ID = "prov:id";
	public static final String NODE_PROV_LABEL = "prov:label";
	public static final String NODE_PROV_LOCATION = "prov:location";
	public static final String NODE_PROV_TYPE = "prov:type";
	public static final String PROV_NODE_TYPE = "2";
	
	private String id;
	private String label;
	private String location;
	private String type;
	private String startTime;
	private String endTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	@Override
	public String toString() {
		return "Activity [id=" + id + ", label=" + label + ", location="
			+ location + ", type=" + type + ", startTime=" + startTime
			+ ", endTime=" + endTime + ", getId()=" + getId()
			+ ", getLabel()=" + getLabel() + ", getLocation()="
			+ getLocation() + ", getType()=" + getType()
			+ ", getStartTime()=" + getStartTime() + ", getEndTime()="
			+ getEndTime() + ", getClass()=" + getClass() + ", hashCode()="
			+ hashCode() + ", toString()=" + super.toString() + "]";
	}
}