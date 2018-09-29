package edu.cornell.ncrn.ced2ar.eapi.prov.oldmodel;

import java.io.Serializable;

public class Entity implements Serializable{
	private static final long serialVersionUID = 3450249021717056356L;
	
	public static final String NODE_PROV_ENTITY = "prov:entity";
	public static final String NODE_PROV_LABEL = "prov:label";
	public static final String NODE_PROV_LOCATION = "prov:location";
	public static final String NODE_PROV_TYPE = "prov:type";
	public static final String NODE_PROV_VALUE = "prov:value";
	public static final String NODE_PROV_TITLE = "dc:title";
	public static final String NODE_PROV_DATE = "dc:date";
	public static final String PROV_ATTR_ID = "prov:id";
	public static final String PROV_NODE_TYPE = "0";

	public static final String JSON_PROV_LABEL = "label";
	public static final String JSON_PROV_LOCATION = "location";
	public static final String JSON_PROV_TYPE = "type";
	public static final String JSON_PROV_VALUE = "value";
	public static final String JSON_PROV_TITLE = "title";
	public static final String JSON_PROV_DATE = "date";
	public static final String JSON_ATTR_ID = "id";

	
	private String id;
	private String label;
	private String location;
	private String type;
	private String value;
	private String date;
	private String title;
	
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return "Entity [id=" + id + ", label=" + label + ", location="
			+ location + ", type=" + type + ", value=" + value + ", date="
			+ date + ", title=" + title + "]";
	}
}