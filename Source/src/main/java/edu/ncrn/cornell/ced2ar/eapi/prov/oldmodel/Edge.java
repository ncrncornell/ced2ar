package edu.ncrn.cornell.ced2ar.eapi.prov.oldmodel;

import java.io.Serializable;

public class Edge implements Serializable{
	private static final long serialVersionUID = 6543660183198275561L;
	
	public static final String RELATIONSHIP_WAS_DERIVED_FROM = "wdf";
	public static final String RELATIONSHIP_WAS_GENERATED_BY = "wgb";
	public static final String RELATIONSHIP_WAS_INFORMED_BY = "wib";
	public static final String RELATIONSHIP_WAS_ATTRIBUTED_TO = "wat";
	public static final String RELATIONSHIP_ACT_ON_BEHALF_OF = "aob";	
	public static final String RELATIONSHIP_WAS_ASSOCIATED_WITH = "waw";	
	public static final String RELATIONSHIP_WAS_USED = "u";

	public static final String RELATIONSHIP_USED_BY = "ub"; //TODO Same as Used. Check this.
	public static final String RELATIONSHIP_CREATED = "c"; // TODO Same as wgb Check this

	public static final String NODE_WAS_DERIVED_FROM = "prov:wasDerivedFrom";
	public static final String NODE_WAS_GENERATED_BY="prov:wasGeneratedBy";
	public static final String NODE_WAS_ATTRIBUTED_TO = "prov:wasAttributedTo";
	public static final String NODE_ACT_ON_BEHALF_OF = "prov:actedOnBehalfOf";
	public static final String NODE_WAS_INFORMED_BY = "prov:wasInformedBy";//TODO Check this
	public static final String NODE_WAS_ASSOCIATED_WITH = "prov:wasAssociatedWith";
	public static final String NODE_WAS_USED = "prov:used";
	
	//	public static final String NODE_USED_BY = "prov:usedBy"; 
	//public static final String NODE_CREATED = "prov:created"; 
	public static final String NODE_GENERATED_ENTITY = "prov:generatedEntity";
	public static final String NODE_USED_ENTITY = "prov:usedEntity";
	public static final String ATTRIBUTE_PROV_REF = "prov:ref";
	public static final String NODE_DELEGATE = "prov:delegate";
	public static final String NODE_RESPONSIBLE = "prov:responsible";
	public static final String NODE_INFORMED = "prov:informed";
	public static final String NODE_INFORMANT = "prov:informant";

	private String id;
	private String edgeType;
	private String source;
	private String target;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEdgeType() {
		return edgeType;
	}
	public void setEdgeType(String edgeType) {
		this.edgeType = edgeType;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
}