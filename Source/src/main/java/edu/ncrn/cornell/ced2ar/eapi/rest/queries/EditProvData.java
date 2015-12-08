package edu.ncrn.cornell.ced2ar.eapi.rest.queries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;

//This class should be replaced by neo4j
@Deprecated
public class EditProvData {
	@Autowired
	private XMLHandle _xh = new XMLHandle(Config.getInstance().getSchemaURI());
	
	public final Logger logger = Logger.getLogger(EditProvData.class.getName());
	
	private String ERROR;
	
//Data Access
	
	/**
	 * Adds node to the prov graph
	 * @param id
	 * @param label
	 * @param nodeType
	 * @param uri
	 * @param date
	 * @param newNode
	 * @return
	 */
	public int editNode(String id, String label, String nodeType, String uri, String date, boolean newNode){
		String jsonData = BaseX.httpGet("/rest/prov/", "prov.json");
		JSONObject json = null;
		JSONArray nodes = null;
		try{
			//TODO: validate nodeType
			
			if(date.equals("") && newNode){
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				Date dateStamp = new Date();
				String rp = dateFormat.format(dateStamp);
				date = rp+" (imported to CED2AR)";
			}
			
		    if(label == null){
		    	label = id;
		    }
			
			json = (JSONObject) new JSONParser().parse(jsonData);
			nodes = (JSONArray) json.get("nodes");

			for(int i = 0; i < nodes.size(); i++){
				JSONObject node = (JSONObject) nodes.get(i);
				String current = (String) node.get("id");
				if(current.equals(id)){
					if(newNode){
						setError("ID already exists");
						return 409;
					}else{
						if(label.equals(""))
							label = node.get("label").toString();
						if(uri.equals("")){
							try{
								uri = node.get("uri").toString();
							}catch(NullPointerException e){}
						}
						if(nodeType.equals("")) nodeType = node.get("nodeType").toString();
					}
					nodes.remove(node);
				}
			}
			
			JSONObject node = new JSONObject();
			
			//TODO: fix saftey
			node.put("id", id);
			node.put("label", label);
			node.put("nodeType", nodeType);
			node.put("uri", uri);
			node.put("date", date);
			
			nodes.add(node);
			json.put("nodes",nodes);
			
			BaseX.putB("prov.json",json.toJSONString(),"prov/");
			return 200;
				
		} catch (ParseException e) {
			setError("Error reading prov from database");
			return 400;
		}finally{
			try{
				json.clear();
				nodes.clear();
			}catch(NullPointerException e){}
		}
	}
	
	public int deleteNode(String id){
		String jsonData = BaseX.httpGet("/rest/prov/", "prov.json");
		JSONObject json = null;
		try {
			json = (JSONObject) new JSONParser().parse(jsonData);
			JSONArray edges = (JSONArray) json.get("edges");
			JSONArray nodes = (JSONArray) json.get("nodes");
			JSONArray edgesNew = new JSONArray();
					
			for(int i = 0; i < edges.size(); i++){
				JSONObject edge = (JSONObject) edges.get(i);
				if(!edge.get("source").equals(id) && !edge.get("target").equals(id)){
					edgesNew.add(edge);			
				}else{
					edges.remove(edge);
					logger.debug("removing edge " + edge.get("id"));
				}
			}
			
			for(int i = 0; i < nodes.size(); i++){
				JSONObject node = (JSONObject) nodes.get(i);
				if(node.get("id").equals(id)){
					nodes.remove(node);
					logger.debug("removing node " + node.get("id"));
					break;
				}
			}
			
			json.put("nodes",nodes);
			json.put("edges",edgesNew);
			
			BaseX.putB("prov.json",json.toJSONString(),"prov/");
			return 200;
		} catch (ParseException e) {
			setError("Error reading prov from database");
			return 400;
		} finally{
			json.clear();
		}
	}
	
	/**
	 * Edits or deletes a prov edge
	 * @param id
	 * @param edgeType
	 * @param source
	 * @param target
	 * @param uniqueEdge
	 * @param delete
	 * @return
	 */
	public int editProvEdge(String id, String edgeType, String source, String target, 
	boolean uniqueEdge, boolean delete){
		String jsonData = BaseX.httpGet("/rest/prov/", "prov.json");
		try{

			JSONObject json = (JSONObject) new JSONParser().parse(jsonData);
			JSONArray edges = (JSONArray) json.get("edges");
			
			if(uniqueEdge|| delete){	
				for(int i = 0; i < edges.size(); i++){
					JSONObject edge = (JSONObject) edges.get(i);
					String curSource = (String) edge.get("source");
					String curTarget = (String) edge.get("target");
					String curType = (String) edge.get("edgeType");
					if(curSource.equals(source) && curTarget.equals(target) && curType.equals(edgeType)){
						if(delete){
							id = (String) edge.get("id");
							break;
						}else{
							setError("Edge already exists");
							return 400;
						}
					}
				}	
			}
			
		    if(id.equals("-1")){
		    	JSONArray nodes = (JSONArray) json.get("nodes");
		    	boolean hasTarget = false;
		    	boolean hasSource = false;
		    	
		    	for(int i = 0; i < nodes.size(); i++){
					JSONObject node = (JSONObject) nodes.get(i);
					String current = (String) node.get("id");
					if(current.equals(source)){
						hasSource = true;
						if(hasTarget) break;
					}else if(current.equals(target)){
						hasTarget = true;
						if(hasSource) break;
					}
				}	
		    	
		    	if(!hasTarget){
		    		setError("Error target "+ target + " does not exist");
		    		return 400;

		    	}
		    	
		    	if(!hasSource){
		    		setError("Error Source "+ source + " does not exist");
		    		return 400;
		    	}
		    	
		    	
		    	int cur = 1;
		    	if(edges.size() > 0){
		    		 cur = Integer.parseInt(((JSONObject) edges.get(edges.size()-1)).get("id").toString());
		    	}
				id = Integer.toString(++cur);
			}else{
				for(int i = 0; i < edges.size(); i++){
					JSONObject edge = (JSONObject) edges.get(i);
					String current = (String) edge.get("id");
					if(current.equals(id)){
						edges.remove(edge);
						break;
					}
				}	
			}

			if(!delete){
				JSONObject edge = new JSONObject();
				edge.put("id",id);
				edge.put("source", source);
				edge.put("target", target);
				edge.put("edgeType", edgeType);
				
				//constants
				//String type = "arrow";
				//if(source.equals(target)) type = "curvedArrow";

				//edge.put("size", 1);
				//Make darker?
				//edge.put("color", "rgba(204,204,204,0.5)");
				//edge.put("weight", 1);
				//edge.put("type", type);
				edges.add(edge);
			}
			json.put("edges",edges);
			BaseX.putB("prov.json",json.toJSONString(),"prov/");

		}catch(ParseException e){
			setError("Error reading prov from database");
		}
		return 200;
	}
	
//Utilties
	private void setError(String s){
		ERROR = s;
	}
	
	public String getError(){
		return ERROR;
	}	
}
