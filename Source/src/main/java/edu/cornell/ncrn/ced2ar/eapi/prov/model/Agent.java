package edu.cornell.ncrn.ced2ar.eapi.prov.model;

import java.io.Serializable;

public class Agent implements Serializable{
	private static final long serialVersionUID = -2112741667319075191L;
	
	public static final String NODE_PROV_AGENT = "prov:agent";
	public static final String PROV_ATTR_ID = "prov:id";
	public static final String NODE_PROV_LABEL = "prov:label";
	public static final String NODE_PROV_LOCATION = "prov:location";
	public static final String NODE_PROV_TYPE = "prov:type";
	public static final String NODE_PROV_GIVEN_NAME = "foaf:givenName";
	public static final String NODE_PROV_WORK_INFO_HOME_PAGE = "foaf:workInfoHomepage";
	
	private String id;
	private String label;
	private String location;
	private String type;
	private String givenName;
	private String workInfoHomepage;
	
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
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getWorkInfoHomepage() {
		return workInfoHomepage;
	}
	public void setWorkInfoHomepage(String workInfoHomepage) {
		this.workInfoHomepage = workInfoHomepage;
	}
	@Override
	public String toString() {
		return "Agent [id=" + id + ", label=" + label + ", location="
				+ location + ", type=" + type + ", givenName=" + givenName
				+ ", workInfoHomepage=" + workInfoHomepage + "]";
	}
	
	
}
