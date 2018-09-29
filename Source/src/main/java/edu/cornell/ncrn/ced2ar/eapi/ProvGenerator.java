package edu.cornell.ncrn.ced2ar.eapi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.api.data.Fetch;
import edu.cornell.ncrn.ced2ar.eapi.prov.oldmodel.Activity;
import edu.cornell.ncrn.ced2ar.eapi.prov.oldmodel.Agent;
import edu.cornell.ncrn.ced2ar.eapi.prov.oldmodel.Edge;
import edu.cornell.ncrn.ced2ar.eapi.prov.oldmodel.Entity;
/**
 * This class  handles prov document
 * 	1. Embeds prov into codebook
 * 	2. Creates and return prov xml from json. 
 * 
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty, Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class ProvGenerator {

	private static final Logger logger = Logger.getLogger(ProvGenerator.class);
	public static final String NODE_TYPE_ENTITY = "Entity";
	public static final String NODE_TYPE_AGENT = "Agent";
	public static final String NODE_TYPE_ACTIVITY = "Activity";
	public static final String EDGES = "edges";
	
	public static final String NODE_RELSTDY= "relStdy";
	public static final String NODE_PROV_ROOT = "prov:document";
	public static final String CODEBOOK_WITH_PROV_XSD_URI = "http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN.P/schemas/codebook.xsd";
	public static final String PROV_PARENT_XPATH = "codeBook/stdyDscr/othrStdyMat"; 
	public static final String PROV_RELSTDY_XPATH = "codeBook/stdyDscr/othrStdyMat/relStdy";
	
	public static final String PROV_DOCUMENT_ROOT_XPATH = "/codeBook/stdyDscr/othrStdyMat/relStdy/document";
	public static final String CED2AR_NAMESPACE = "ced2ar";

	/**
	 * updates prov.json from codebook
	 * @param is codebook inputstream
	 */
	public void updateProvFromCodebook(InputStream is) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, StandardCharsets.UTF_8);
			String codebookXML = writer.toString();
			updateProvFromCodebook(codebookXML);
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error adding prov from codebook.", ex);
		}
		
	}
	
	/**
	 * This method reads Prov data from codebook and update prov.json
	 * @param codebookXML
	 */
	public void updateProvFromCodebook(String codebookXML) {
		ByteArrayInputStream input = new ByteArrayInputStream(codebookXML.getBytes(StandardCharsets.UTF_8));
		XMLHandle xh = new XMLHandle(input,CODEBOOK_WITH_PROV_XSD_URI);
		NodeList provDocumentNode = xh.getNodeList(PROV_DOCUMENT_ROOT_XPATH);
		if(provDocumentNode == null || provDocumentNode.getLength() < 1) return ;
		NodeList provNodes = provDocumentNode.item(0).getChildNodes();
		
		for(int i=0;i<provNodes.getLength();i++) {
			Node provNode = provNodes.item(i);
			String name = provNode.getNodeName();
			if(name.equalsIgnoreCase(Entity.NODE_PROV_ENTITY)) {
				addProv(provNode,Entity.PROV_NODE_TYPE);
			}
			else if(name.equalsIgnoreCase(Agent.NODE_PROV_AGENT)) {
				addProv(provNode,Agent.PROV_NODE_TYPE);
			}
			else if(name.equalsIgnoreCase(Activity.NODE_PROV_ACTIVITY)) {
				addProv(provNode,Activity.PROV_NODE_TYPE);
			}
			else if(name.equalsIgnoreCase(Edge.NODE_WAS_DERIVED_FROM)) {
				//addWdfRelationship(provNode);
				addRelationship(provNode,Edge.RELATIONSHIP_WAS_DERIVED_FROM,Edge.NODE_GENERATED_ENTITY,Edge.NODE_USED_ENTITY);
			}
			else if(name.equalsIgnoreCase(Edge.NODE_WAS_GENERATED_BY)) {
				//addWgbRelationship(provNode);
				addRelationship(provNode,Edge.RELATIONSHIP_WAS_GENERATED_BY,Entity.NODE_PROV_ENTITY,Activity.NODE_PROV_ACTIVITY);
			}
			else if(name.equalsIgnoreCase(Edge.NODE_WAS_INFORMED_BY)) {
				//addWibRelationship(provNode);
				addRelationship(provNode,Edge.RELATIONSHIP_WAS_INFORMED_BY,Edge.NODE_INFORMED,Edge.NODE_INFORMANT);
			}
			else if(name.equalsIgnoreCase(Edge.NODE_WAS_ATTRIBUTED_TO)) {
				//addWatRelationship(provNode);
				addRelationship(provNode,Edge.RELATIONSHIP_WAS_ATTRIBUTED_TO,Entity.NODE_PROV_ENTITY,Agent.NODE_PROV_AGENT);
			}
			else if(name.equalsIgnoreCase(Edge.NODE_ACT_ON_BEHALF_OF)) {
				//addAobRelationship(provNode);
				addRelationship(provNode,Edge.RELATIONSHIP_ACT_ON_BEHALF_OF,Edge.NODE_DELEGATE,Edge.NODE_RESPONSIBLE);
			}
			else if(name.equalsIgnoreCase(Edge.NODE_WAS_ASSOCIATED_WITH)) {
				//addWawRelationship(provNode);
				addRelationship(provNode,Edge.RELATIONSHIP_WAS_ASSOCIATED_WITH,Activity.NODE_PROV_ACTIVITY,Agent.NODE_PROV_AGENT);
				
			}
			else if(name.equalsIgnoreCase(Edge.RELATIONSHIP_WAS_USED) ) {
//				addURelationship(provNode);ss
				addRelationship(provNode,Edge.RELATIONSHIP_WAS_USED,Activity.NODE_PROV_ACTIVITY,Entity.NODE_PROV_ENTITY);
				
			}
			else if(name.equalsIgnoreCase(Edge.RELATIONSHIP_USED_BY) ) {
				//ignore this same as RELATIONSHIP_WAS_USED
			}
			else if(name.equalsIgnoreCase(Edge.RELATIONSHIP_CREATED) ) {
				//ignore this. Same as WGB
			}
		}
	}

	/**
	 * This method creates a relationship in JSON
	 * @param node
	 * @param relationship
	 * @param sourceNode
	 * @param targetNode
	 */
	private void addRelationship(Node node,String relationship,String sourceNode, String targetNode) {
		String sourceValue = "";
		String targetValue = "";
		NodeList nodeList = node.getChildNodes();
		for(int i=0;i<nodeList.getLength();i++) {
			Node n = nodeList.item(i);
			String name = n.getNodeName();
			if(name.equals(sourceNode)) {
				NamedNodeMap attibuteMap = n.getAttributes();
				Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
				sourceValue = refNode.getNodeValue();
				sourceValue = this.stripNamespace(sourceValue);
			}
			else if(name.equals(targetNode)) {
				NamedNodeMap attibuteMap = n.getAttributes();
				Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
				targetValue = refNode.getNodeValue();
				targetValue = this.stripNamespace(targetValue);
				
			}
		}
		if(StringUtils.isNotBlank(sourceValue) && StringUtils.isNotBlank(targetValue)) {
			String baseURL = getBaseURL();
			
			int	code = Fetch.provEdge(baseURL,"-1", sourceValue, targetValue, relationship, "true");
			if(code == 200) {
				logger.info(relationship +" relationhip sourceValue:targetValue" + sourceValue  +" :" + targetValue + " added to prov");
			}
			else {
				logger.info(relationship +" relationhip sourceValue:targetValue" + sourceValue  +" :" + targetValue + " is NOT added to prov. The relationship may already exist or source or target nodes ae not defined.");
			}
			
		}
	}
	/**
	 * This method adds a prov Entity, Activity or Agent node. 
	 * @param node
	 * @param nodeType
	 */
	private void addProv(Node node, String nodeType) {
		NamedNodeMap attibuteMap = node.getAttributes();
		Node idNode = attibuteMap.getNamedItem("prov:id");
		String provId = idNode.getNodeValue();
		provId = stripNamespace(provId);
		NodeList nodeList = node.getChildNodes();
		String provLabel = "";
		//String provUri = "";
		for(int i=0;i<nodeList.getLength();i++) {
			Node n = nodeList.item(i);
			String name = n.getNodeName();
			String value = n.getTextContent();
			if(name.equals(Entity.NODE_PROV_LABEL)) {
				provLabel = value;
				provLabel = this.stripNamespace(provLabel);
			}
		}
		
		if(StringUtils.isEmpty(provLabel)) {
			provLabel = provId;
		}
		if(StringUtils.isNotEmpty(provId)) {
		int code = Fetch.provNode(getBaseURL(),provId, nodeType, provLabel, provId, "true","");
			if(code == 200) {
				logger.info("Prov Id " + provId  +" Prov Label " + provLabel + " added to prov");
			}
			else {
				logger.info("Prov Id " + provId  +" Prov Label " + provLabel + " already exists in prov");
			}
		}
	}

	private String getBaseURL(){
		Config config = Config.getInstance();
		String port = Integer.toString(config.getPort());
		String baseURL = "http://localhost:"+port;
		return baseURL;
	}

	
	/**
	 * 
	 * @param provUrl location of the prov document
	 * @param codebookXML	codebook in xml format 
	 * @return	xml of prov embedded codebook; returns codbookXML unchanged if there is an exception.   
	 */
	
	public String insertProvIntoCodebook(String provUrl,String codebookXML) {
		try {
	        ByteArrayInputStream input = new ByteArrayInputStream(codebookXML.getBytes());
	        Map<String, List> provData = getProvData(provUrl);
	        List<Entity> entities = (List<Entity>)provData.get(NODE_TYPE_ENTITY);
	        List<Agent> agents = (List<Agent>)provData.get(NODE_TYPE_AGENT);
	        List<Activity> activities = (List<Activity>)provData.get(NODE_TYPE_ACTIVITY);
	        List<Edge> edges = (List<Edge>)provData.get(EDGES);
	        
	        XMLHandle xh = new XMLHandle(input,CODEBOOK_WITH_PROV_XSD_URI);
	        
	        if(xh.hasElement(PROV_RELSTDY_XPATH+"/document")) {
	        	xh.deleteNode(PROV_RELSTDY_XPATH + "/document", true);
	        }
	        //if(xh.hasElement(PROV_DOCUMENT_ROOT_XPATH)) {
	        	//xh.deleteNode(PROV_DOCUMENT_ROOT_XPATH, true);
	        //}
	        //xh.addComplexNode(PROV_PARENT_XPATH, NODE_RELSTDY, "",NODE_PROV_ROOT, "", false,false);
	        xh.addProvDocumentNode(PROV_PARENT_XPATH+"/"+NODE_RELSTDY);
	        
	        for(Entity entity :entities) {
	        	xh.addEntityNode(PROV_DOCUMENT_ROOT_XPATH,entity);
			}
	        for(Activity activity :activities) {
	        	xh.addActivityNode(PROV_DOCUMENT_ROOT_XPATH,activity);
			}
	        for(Agent agent :agents) {
	        	xh.addAgentNode(PROV_DOCUMENT_ROOT_XPATH,agent);
			}
	        for(Edge edge :edges) {
	        	xh.addRelationshipNode(PROV_DOCUMENT_ROOT_XPATH, edge);
			}
	        
			if(xh.isValid()) {
				logger.debug("Added Prov to codebook. Validated the codebook. Returning Codebook with Prov" );
				String codebookWithProv =  xh.docToString();
				codebookWithProv = codebookWithProv.replace("xmlns=\"\"", "");
				return codebookWithProv;
			}
			else {
				logger.error("Invalid Codebook is Generated after adding Prov.  Returning codebook without prov" );
				return codebookXML;
			}
		}
		catch(Exception e) {
			logger.error("Error occured in inserting Prov to codebook. Returning without inserting prov." );
			e.printStackTrace();
			logger.error(e);
			return codebookXML;
		}
	}

	/**
	 * This methid fetches Prov from he URL.(expected format is JSON)
	 * Returns a map of Entities, Agents and Activities
	 * @param provUrl
	 * @return
	 * @throws Exception
	 */
	private Map<String, List> getProvData(String provUrl) throws Exception {
		Map<String, List> provData = new HashMap<String, List>();
		List<Entity> entities = new ArrayList<Entity>();
		List<Agent> agents = new ArrayList<Agent>();
		List<Activity> activities = new ArrayList<Activity>();
		List<Edge> edges = new ArrayList<Edge>();
        String jsonString = Fetch.get(provUrl);
		JSONObject jsonObject = new JSONObject(jsonString);
		HashMap<String, String> nodeTypes = getAllNodeTypes(jsonObject);
		JSONArray nodes = jsonObject.getJSONArray("nodes");
		for (int i = 0; i < nodes.length(); i++) {
			JSONObject node = nodes.getJSONObject(i);
			if (!node.has("nodeType")) continue;
			String nodeName = nodeTypes.get(node.getString("nodeType"));
			
			switch (nodeName) {
				case NODE_TYPE_ENTITY:
					Entity entity = new Entity();
					entity.setId(CED2AR_NAMESPACE+":"+getProvElementId(node));
					if (node.has("label")) {  
						String label = node.getString("label");
						entity.setLabel(label);
						entity.setTitle(label);
					}
					if (node.has("location")) {  
						String location = node.getString("location");
						entity.setLocation(location);
					}
					if (node.has("type")) {  
						String type = node.getString("type");
						entity.setType(type);
					}
					if (node.has("value")) {  
						String value = node.getString("value");
						entity.setValue(value);					
					}
					if (node.has("date")) {
						String date = node.getString("date");
						entity.setDate(date);
					}
					entities.add(entity);
					break;
				case NODE_TYPE_AGENT:
					Agent agent = new Agent();
					agent.setId(CED2AR_NAMESPACE+":"+getProvElementId(node));
					 if (node.has("label")) {  
						String label = node.getString("label");
						agent.setLabel(label);
					}
					if (node.has("location")) {  
						String location = node.getString("location");
						agent.setLocation(location);
					}
					if (node.has("type")) {  
						String type = node.getString("type");
						agent.setType(type);
					}
					if (node.has("givenName")) {  
						String givenName = node.getString("givenName");
						agent.setGivenName(givenName);
					}
					if (node.has("workInfoHomepage")) {  
						String workInfoHomepage = node.getString("workInfoHomepage");
						agent.setWorkInfoHomepage(workInfoHomepage);
					}
					agents.add(agent);
					break;
				case NODE_TYPE_ACTIVITY:
					Activity activity = new Activity();
					activity.setId(CED2AR_NAMESPACE+":"+getProvElementId(node));
					 if (node.has("label")) {  
						String label = node.getString("label");
						activity.setLabel(label);
					}
					if (node.has("location")) {  
						String location = node.getString("location");
						activity.setLocation(location);
					}
					if (node.has("type")) {  
						String type = node.getString("type");
						activity.setType(type);
					}
					activities.add(activity);
					break;
				default:
			}
		}
		JSONArray jsonEdges = jsonObject.getJSONArray("edges");
		for (int i = 0; i < jsonEdges.length(); i++) {
			JSONObject jsonEdge = jsonEdges.getJSONObject(i);
			Edge edge = new Edge();
			if (jsonEdge.has("id")) {  
				edge.setId(jsonEdge.getString("type"));
			}
			if (jsonEdge.has("source")) {  
				edge.setSource(CED2AR_NAMESPACE+":"+jsonEdge.getString("source"));
			}
			if (jsonEdge.has("target")) {  
				edge.setTarget(CED2AR_NAMESPACE+":"+jsonEdge.getString("target"));
			}
			if (jsonEdge.has("edgeType")) {  
				edge.setEdgeType(jsonEdge.getString("edgeType"));
			}
			edges.add(edge);
		}
		
		provData.put(NODE_TYPE_ENTITY, entities);
		provData.put(NODE_TYPE_AGENT, agents);
		provData.put(NODE_TYPE_ACTIVITY, activities);
		provData.put(EDGES, edges);
		return provData;
	}
	
	/**
	 * This method returns id of the prov
	 * @param node
	 * @return
	 * @throws JSONException
	 */
	private String getProvElementId(JSONObject node) throws JSONException{
		String id = "";
		/*
		if (node.has("uri")) //comeback
			id = node.getString("uri");
		else if (node.has("id"))
			id = node.getString("id");
			*/
		
		if (node.has("id"))
			id = node.getString("id");
		
		return id;
	}
	
	/**
	 * Returns avalible node types
	 * TODO: this can be hardcoded for now since there are three node types
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	private HashMap<String, String> getAllNodeTypes(JSONObject jsonObject)throws JSONException {
		HashMap<String, String> nodeTypes = new HashMap<String, String>();
		nodeTypes.put("0", "Entity");
		nodeTypes.put("1", "Agent");
		nodeTypes.put("2", "Activity");
		return nodeTypes;
	}
	
	private String stripNamespace(String str) {
		String returnString = "";
		if(StringUtils.isEmpty(str)) return str;
		String [] strArray = str.split(":");
		if(strArray.length==2) {
			returnString = strArray[1];
		}
		else {
			returnString  = str;
		}
		return returnString;
	}
}




















/*

private void addURelationship(Node node) {
	String activity = "";
	String entity = "";
	NodeList nodeList = node.getChildNodes();
	for(int i=0;i<nodeList.getLength();i++) {
		Node n = nodeList.item(i);
		String name = n.getNodeName();
		if(name.equals(Activity.NODE_PROV_ACTIVITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			activity = refNode.getNodeValue();
			activity = this.stripNamespace(activity);
		}
		else if(name.equals(Entity.NODE_PROV_ENTITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			entity = refNode.getNodeValue();
			entity = this.stripNamespace(entity);
		}
	}
	if(StringUtils.isNotBlank(activity) && StringUtils.isNotBlank(entity)) {
		int code = Fetch.provEdge("http://localhost:8080","-1", activity, entity, Edge.RELATIONSHIP_WAS_USED, "true");
		if(code == 200) {
			logger.info("U  relationhip activity:agent" + activity  +" :" + entity + " added to prov");
		}
		else {
			logger.info("U relationhip activity:agent" + activity  +" :" + entity + " NOT added to prov. The relationship may already exists");
		}
	}
}


private void addWawRelationship(Node node) {
	String activity = "";
	String agent = "";
	NodeList nodeList = node.getChildNodes();
	for(int i=0;i<nodeList.getLength();i++) {
		Node n = nodeList.item(i);
		String name = n.getNodeName();
		if(name.equals(Activity.NODE_PROV_ACTIVITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			activity = refNode.getNodeValue();
			activity = this.stripNamespace(activity);
		}
		else if(name.equals(Agent.NODE_PROV_AGENT)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			agent = refNode.getNodeValue();
			agent = this.stripNamespace(agent);
		}
	}
	if(StringUtils.isNotBlank(activity) && StringUtils.isNotBlank(agent)) {
		int code = Fetch.provEdge("http://localhost:8080","-1", activity, agent, Edge.RELATIONSHIP_WAS_ASSOCIATED_WITH, "true");
		if(code == 200) {
			logger.info("WAW relationhip activity:agent" + activity  +" :" + agent + " added to prov");
		}
		else {
			logger.info("WAW relationhip activity:agent" + activity  +" :" + agent + " NOT added to prov. The relationship may already exists");
		}
	}
}

private void addAobRelationship(Node node) {
	String delegate = "";
	String responsible = "";
	NodeList nodeList = node.getChildNodes();
	for(int i=0;i<nodeList.getLength();i++) {
		Node n = nodeList.item(i);
		String name = n.getNodeName();
		if(name.equals(Edge.NODE_DELEGATE)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			delegate = refNode.getNodeValue();
			delegate = this.stripNamespace(delegate);
		}
		else if(name.equals(Edge.NODE_RESPONSIBLE)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			responsible = refNode.getNodeValue();
			responsible = this.stripNamespace(responsible);
		}
	}
	if(StringUtils.isNotBlank(delegate) && StringUtils.isNotBlank(responsible)) {
		int code = Fetch.provEdge("http://localhost:8080","-1", delegate, responsible, Edge.RELATIONSHIP_ACT_ON_BEHALF_OF, "true");
		if(code == 200) {
			logger.info("AOB relationhip delegate:responsible " + delegate  +" :" + responsible + " added to prov");
		}
		else {
			logger.info("AOB relationhip delegate:responsible " + delegate  +" :" + responsible + " NOT added to prov. The relationship may already exists");
		}
	}
}

	
private void addWatRelationship(Node node) {
	String entity = "";
	String agent = "";
	NodeList nodeList = node.getChildNodes();
	for(int i=0;i<nodeList.getLength();i++) {
		Node n = nodeList.item(i);
		String name = n.getNodeName();
		if(name.equals(Entity.NODE_PROV_ENTITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			entity = refNode.getNodeValue();
			entity = this.stripNamespace(entity);
		}
		else if(name.equals(Agent.NODE_PROV_AGENT)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			agent = refNode.getNodeValue();
			agent = this.stripNamespace(agent);
		}
	}
	if(StringUtils.isNotBlank(entity) && StringUtils.isNotBlank(agent)) {
		int code = Fetch.provEdge("http://localhost:8080","-1", entity, agent, Edge.RELATIONSHIP_WAS_ATTRIBUTED_TO, "true");
		if(code == 200) {
			logger.info("WIB relationhip entity:agent " + entity  +" :" + agent + " added to prov");
		}
		else {
			logger.info("WIB relationhip entity:agent " + entity  +" :" + agent + " NOT added to prov. The relationship may already exists");
		}
	}
}



private void addWibRelationship(Node node) {
	String informedActivity = "";
	String informantActivity = "";
	NodeList nodeList = node.getChildNodes();
	for(int i=0;i<nodeList.getLength();i++) {
		Node n = nodeList.item(i);
		String name = n.getNodeName();
		if(name.equals(Edge.NODE_INFORMED)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			informedActivity = refNode.getNodeValue();
			informedActivity = this.stripNamespace(informedActivity);
		}
		else if(name.equals(Edge.NODE_INFORMANT)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			informantActivity = refNode.getNodeValue();
			informantActivity = this.stripNamespace(informantActivity);
		}
	}
	if(StringUtils.isNotBlank(informedActivity) && StringUtils.isNotBlank(informantActivity)) {
		int code = Fetch.provEdge("http://localhost:8080","-1", informedActivity, informantActivity, Edge.RELATIONSHIP_WAS_INFORMED_BY, "true");
		if(code == 200) {
			logger.info("WIB relationhip informedActivity:informantActivity " + informedActivity  +" :" + informedActivity + " added to prov");
		}
		else {
			logger.info("WIB relationhip informedActivity:informantActivity " + informedActivity  +" :" + informedActivity + " NOT added to prov. The relationship may already exists");
		}
	}
}


private void addWgbRelationship(Node node) {
	String activity = "";
	String entity = "";
	NodeList nodeList = node.getChildNodes();
	for(int i=0;i<nodeList.getLength();i++) {
		Node n = nodeList.item(i);
		String name = n.getNodeName();
		if(name.equals(Entity.NODE_PROV_ENTITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			entity = refNode.getNodeValue();
			entity = this.stripNamespace(entity);
		}
		else if(name.equals(Activity.NODE_PROV_ACTIVITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			activity = refNode.getNodeValue();
			activity = this.stripNamespace(activity);
		}
	}
	if(StringUtils.isNotBlank(activity) && StringUtils.isNotBlank(entity)) {
		int code = Fetch.provEdge("http://localhost:8080","-1", entity, activity, Edge.RELATIONSHIP_WAS_GENERATED_BY, "true");
		if(code == 200) {
			logger.info("WGB relationhip entity:activity " + entity  +" :" + activity + " added to prov");
		}
		else {
			logger.info("WGB relationhip entity:activity " + entity  +" :" + activity + " NOT added to prov. The relationship may already exists");
		}
	}
}

private void addWdfRelationship(Node node) {
	String generatedEntity = "";
	String usedEntity = "";
	NodeList nodeList = node.getChildNodes();
	for(int i=0;i<nodeList.getLength();i++) {
		Node n = nodeList.item(i);
		String name = n.getNodeName();
		if(name.equals(Edge.NODE_GENERATED_ENTITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			generatedEntity = refNode.getNodeValue();
			generatedEntity = this.stripNamespace(generatedEntity);
		}
		else if(name.equals(Edge.NODE_USED_ENTITY)) {
			NamedNodeMap attibuteMap = n.getAttributes();
			Node refNode = attibuteMap.getNamedItem(Edge.ATTRIBUTE_PROV_REF);
			usedEntity = refNode.getNodeValue();
			usedEntity = this.stripNamespace(usedEntity);
			
		}
	}
	if(StringUtils.isNotBlank(generatedEntity) && StringUtils.isNotBlank(usedEntity)) {
		int code = Fetch.provEdge("http://localhost:8080","-1", generatedEntity, usedEntity, Edge.RELATIONSHIP_WAS_DERIVED_FROM, "true");
		if(code == 200) {
			logger.info("WDF relationhip generatedEntity:usedEntity" + generatedEntity  +" :" + usedEntity + " added to prov");
		}
		else {
			logger.info("WDF relationhip generatedEntity:usedEntity" + generatedEntity  +" :" + usedEntity + " NOT added to prov. The relationship may already exists");
		}
		
	}
}
	public static void main(String[] a)  throws Exception{
		ProvGenerator pg = new ProvGenerator();
		InputStream is = new FileInputStream("C://java//info//rdf2json//dev//ssb7.xml");
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, StandardCharsets.UTF_8);
		String ssb7 = writer.toString();
		pg.updateProvFromCodebook(ssb7);
	}

*
*/