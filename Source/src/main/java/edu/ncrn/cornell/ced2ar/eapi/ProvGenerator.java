package edu.ncrn.cornell.ced2ar.eapi;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;
import edu.ncrn.cornell.ced2ar.eapi.prov.model.Activity;
import edu.ncrn.cornell.ced2ar.eapi.prov.model.Agent;
import edu.ncrn.cornell.ced2ar.eapi.prov.model.Entity;
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
	
	public static final String NODE_RELSTDY= "relStdy";
	public static final String NODE_PROV_ROOT = "prov:document";
	public static final String CODEBOOK_WITH_PROV_XSD_URI = "http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN.P/schemas/codebook.xsd";
	public static final String PROV_PARENT_XPATH = "codeBook/stdyDscr/othrStdyMat"; 
	public static final String PROV_RELSTDY_XPATH = "codeBook/stdyDscr/othrStdyMat/relStdy";
	public static final String PROV_DOCUMENT_ROOT_XPATH = "/codeBook/stdyDscr/othrStdyMat/relStdy/document";

	/**
	 * 
	 * @param provUrl location of the prov document
	 * @param codebookXML	codebook in xml format 
	 * @return	xml of prov embedded codebook; returns codbookXML unchanged if there is an exception.   
	 */
	
	public String insertProv(String provUrl,String codebookXML) {
		try {
	        ByteArrayInputStream input = new ByteArrayInputStream(codebookXML.getBytes());
	        Map<String, List> provData = getProvData(provUrl);
	        List<Entity> entities = (List<Entity>)provData.get(NODE_TYPE_ENTITY);
	        List<Agent> agents = (List<Agent>)provData.get(NODE_TYPE_AGENT);
	        List<Activity> activities = (List<Activity>)provData.get(NODE_TYPE_ACTIVITY);
	        
	        XMLHandle xh = new XMLHandle(input,CODEBOOK_WITH_PROV_XSD_URI);
	        if(xh.hasElement(PROV_RELSTDY_XPATH)) {
	        	xh.deleteNode(PROV_RELSTDY_XPATH, true);
	        }
	        xh.addComplexNode(PROV_PARENT_XPATH, NODE_RELSTDY, "",NODE_PROV_ROOT, "", false,false);

	        for(Entity entity :entities) {
	        	xh.addEntityNode(PROV_DOCUMENT_ROOT_XPATH,entity);
			}
	        for(Agent agent :agents) {
	        	xh.addAgentNode(PROV_DOCUMENT_ROOT_XPATH,agent);
			}
	        for(Activity activity :activities) {
	        	xh.addActivityNode(PROV_DOCUMENT_ROOT_XPATH,activity);
			}
	        
			if(xh.isValid()) {
				logger.error("Added Prov to codebook. Validated the codebook. Returning Codebook with Prov" );
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
	public Map<String, List> getProvData(String provUrl) throws Exception {
		Map<String, List> provData = new HashMap<String, List>();
		List<Entity> entities = new ArrayList<Entity>();
		List<Agent> agents = new ArrayList<Agent>();
		List<Activity> activities = new ArrayList<Activity>();
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
					entity.setId(this.getProvElementId(node));
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
					agent.setId(this.getProvElementId(node));
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
					activity.setId(this.getProvElementId(node));
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
		provData.put(NODE_TYPE_ENTITY, entities);
		provData.put(NODE_TYPE_AGENT, agents);
		provData.put(NODE_TYPE_ACTIVITY, activities);
		
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
}


