package edu.cornell.ncrn.ced2ar.ei.controllers;

import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import edu.cornell.ncrn.ced2ar.api.Utilities;
import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.api.data.Fetch;
import edu.cornell.ncrn.ced2ar.api.rest.queries.CodebookData;
import edu.cornell.ncrn.ced2ar.eapi.neo4j.Neo4jUtil;
import edu.cornell.ncrn.ced2ar.eapi.rest.queries.EditCodebookData;
import edu.cornell.ncrn.ced2ar.eapi.rest.queries.EditProvData;
import edu.cornell.ncrn.ced2ar.web.classes.Loader;
import edu.cornell.ncrn.ced2ar.web.classes.Parser;

/**
 *Class to handle Prov editing
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
//TODO: cleanup and move items into API
//TODO: Integrate with Neo4j
public class EditProv {
	
	private static final Logger logger = Logger.getLogger(EditCodebooks.class);
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Loader loader;
	
	@Autowired
	Config config;
	
//Utilities	
	@Cacheable("prov")
	public JSONObject getProv(){
		String jsonData = Fetch.get(loader.getPath()+"/rest/prov");
		try{
			JSONObject json = (JSONObject) new JSONParser().parse(jsonData);
			return json;
		}catch(ParseException e){
			logger.error("Error parsing JSON from internal database: "+e.getMessage());
		}
		return null;
	}
	
	@Cacheable("provPreds")
	public JSONObject getFlatPreds(){
		JSONObject flatPreds = new JSONObject();
		JSONObject json = getProv();
		JSONObject predicates = (JSONObject) json.get("predicates");
		Iterator iter = predicates.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			JSONArray predCat = (JSONArray) entry.getValue();
			
			for(int i = 0; i < predCat.size(); i++){
				JSONObject pred = (JSONObject) predCat.get(i);
				flatPreds.put(pred.get("id"), pred.get("label"));
			} 
		}
		return flatPreds;
	}
	
	@CacheEvict(value = "prov", allEntries = true)
	public void clearProvCache() {}
	
//Endpoints Read - combined from Prov.java to share cachable points
	
	/**
	 * Displays Prov graph
	 * @param model Model the current model
	 * @return String
	 */
	@RequestMapping(value = "/prov", method = RequestMethod.GET)
    public String prov(Model model) {
		if(config.getDevFeatureProv()){
				model.addAttribute("prov", true);
				model.addAttribute("subTitl","Prov View");
				model.addAttribute("metaDesc"," - Network graph for provenance information.");
				model.addAttribute("pageWidth", 2);
				return "/WEB-INF/views/prov.jsp";		
		}else{
			return "redirect:/search";
		}
    }
	
	/**
	 * Displays New Prov graph
	 * @param model Model the current model
	 * @return String
	 */
	@RequestMapping(value = "/prov2", method = RequestMethod.GET)
    public String prov2(Model model,
    @RequestParam(value = "n", defaultValue = "") String n,
    @RequestParam(value = "i", defaultValue = "") String i,
    @RequestParam(value = "f", defaultValue = "") String f){
		if(config.getDevFeatureProv()){
			if(!n.equals("")){
				model.addAttribute("zoomOn", n);
			}
			if(!f.equals("")){
				model.addAttribute("filterNode", f);
			}
			if(i.equals("true")){
				model.addAttribute("inverted", true);
			}
			
			model.addAttribute("prov", true);
			model.addAttribute("subTitl","Prov View 2");
			model.addAttribute("metaDesc"," - Network graph for provenance information.");
			model.addAttribute("pageWidth", 1);
			return "/WEB-INF/views/prov2.jsp";		
		}else{
			return "redirect:/search";
		}
    }
	
	/**
	 * Travel the graph and adds connected parts
	 * @param root
	 * @param edgesNew
	 * @param edgesOld
	 * @param nodesNew
	 * @param nodesOld
	 * @return
	 */
	private JSONObject[] findParent(String[] roots, JSONObject edgesNew, JSONObject edgesOld,
	JSONObject nodesNew , JSONObject nodesOld){
		for(String root : roots){
			if(!nodesNew.containsKey(root)){
				nodesNew.put(root, nodesOld.get(root));
				for(Object e : edgesOld.entrySet()){
					Map.Entry<String,JSONArray> edge = (Map.Entry<String,JSONArray>) e;
					JSONArray edgeValue = edge.getValue();
					if(edgeValue.get(1).equals(root)){
						String key = edge.getKey();
						edgesNew.put(key, edgeValue);
						String parent = edgeValue.get(0).toString();
						if(!parent.equals(root)){
							findParent(new String[] {parent},edgesNew,edgesOld,nodesNew, nodesOld);
						}	
					}
				}
			}
		}
		return new JSONObject[] {nodesNew,edgesNew};
	}
	
	/**
	 * Focuses graph on object and subject
	 * @param obj
	 * @param sub
	 */
	private JSONArray[] selectTwoNodes(String obj, String sub, JSONArray edgesOld, JSONArray nodesOld){
		JSONArray edgesNew = new JSONArray();
		JSONArray nodesNew = new JSONArray();
		ArrayList<String> nodeIDs = new ArrayList<String>();
		for(int i = 0; i < edgesOld.size(); i++){
			JSONObject edge = (JSONObject) edgesOld.get(i);
			String source = (String) edge.get("source");
			String target = (String) edge.get("target");
			if(source.equals(obj) || source.equals(sub)){
				if(!nodeIDs.contains(target))
					nodeIDs.add(target);
				if(!nodeIDs.contains(source))
					nodeIDs.add(source);
				if(!edgesNew.contains(edge))
					edgesNew.add(edge);		
			}
			if(target.equals(obj) || target.equals(sub)){
				if(!nodeIDs.contains(target))
					nodeIDs.add(target);
				if(!nodeIDs.contains(source))
					nodeIDs.add(source);
				if(!edgesNew.contains(edge))
					edgesNew.add(edge);
			}
		}
		for(int i = 0; i < nodesOld.size(); i++){
			JSONObject node = (JSONObject) nodesOld.get(i);
			String id = (String) node.get("id");
			if(nodeIDs.contains(id) && !nodesNew.contains(node)){
				if(loader.hasCodebook(id)) node.put("isCodebook","true");
				nodesNew.add(node);
			}
		}
		return new JSONArray[] {nodesNew,edgesNew};
	}
	
	/**
	 * Returns filtered JSON.
	 * @param isShort String 
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/prov/data", method = RequestMethod.GET)
	@ResponseBody
    public String provData(@RequestParam(value = "short", defaultValue = "0") String isShort, 
    	@RequestParam(value = "roots", required = false) String[] roots){
		JSONParser parser = new JSONParser();  

		try {			 
			//TODO: Fetch from BaseX and/or somewhere else using API
			Object obj = parser.parse(new FileReader((context.getRealPath("/json/prov.json")))); 
			JSONObject json = (JSONObject) obj;
			JSONObject nodes = (JSONObject) json.get("nodes");
			JSONObject edges = (JSONObject) json.get("edges");
			JSONObject filtered = new JSONObject();
				
			if(roots != null){
				JSONObject[] newData = findParent(roots,new JSONObject(),edges,new JSONObject(),nodes);
				nodes = newData[0];
				edges = newData[1];
			} 
			
			if(isShort.equals("1")){
				ArrayList<String> delete = new ArrayList<String>();
				ArrayList<String> delete2 = new ArrayList<String>();				
				for(Object n : nodes.entrySet()){
					Map.Entry<String,JSONArray> node = (Map.Entry<String,JSONArray>) n;
					JSONArray nodeValues = node.getValue();
					String nodeKey = node.getKey();
					if(!nodeValues.get(1).equals("0"))
					{
						delete.add(nodeKey);
					}
				}
				for(Object e : edges.entrySet()){
					Map.Entry<String,JSONArray> edge = (Map.Entry<String,JSONArray>) e;
					JSONArray edgeValue = edge.getValue();
					String edgeKey = edge.getKey();
					if(!edgeValue.get(2).equals("4")){
						delete2.add(edgeKey);
					}
				}
				for(String s : delete){
					nodes.remove(s);
				}
				for(String s : delete2){
					edges.remove(s);
				}
			}else{
				ArrayList<String> delete = new ArrayList<String>();
				for(Object e : edges.entrySet()){
					Map.Entry<String,JSONArray> edge = (Map.Entry<String,JSONArray>) e;
					JSONArray edgeValue = edge.getValue();
					String edgeKey = edge.getKey();
					if(edgeValue.get(2).toString().equals("4")){
						delete.add(edgeKey);
					}
				}
				for(String s : delete){
					edges.remove(s);
				}
			}
			
			filtered.put("edges", edges);
			filtered.put("nodes", nodes);
			return filtered.toString();
	 
		} catch (IOException|ParseException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Travel the graph and adds connected parts
	 * @param root
	 * @param edgesNew
	 * @param edgesOld
	 * @param nodesNew
	 * @param nodesOld
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object> findParent2(String[] roots, JSONArray edgesNew, 
	JSONArray edgesOld, HashMap<String,Integer> newNodes, boolean searchUp, int distance){
		for(String root : roots){
			if(!newNodes.containsKey(root)){
				newNodes.put(root,Integer.valueOf(distance));
			}
			
			for(int i = 0; i < edgesOld.size(); i++){
				JSONObject edge = (JSONObject) edgesOld.get(i);
				String source = (String) edge.get("source");
				String target = (String) edge.get("target");	
				distance = newNodes.get(root);
				
				if(searchUp){
					if(source.equals(root)){
						if(!edgesNew.contains(edge)){
							if(distance > 0){
								edgesNew.add(edge);
								if(source != target){
									findParent2(new String[] {target},edgesNew,edgesOld,newNodes,true,--distance);
								}
							}
						}
					}else if(target.equals(root)){
						if(!edgesNew.contains(edge)){
							if(distance > 0){
								edgesNew.add(edge);
								if(source != target){
									findParent2(new String[] {source},edgesNew,edgesOld,newNodes,true,--distance);
								}
							}
						}
					}
				}else{
					if(source.equals(root)){
						if(!edgesNew.contains(edge)){
							if(distance > 0){
								edgesNew.add(edge);							
								if(source != target){
									findParent2(new String[] {target},edgesNew,edgesOld,newNodes,false,--distance);
								}
							}
						}
					}	
				}		
			}
		}
		
		List<Object> rlist = new ArrayList<Object>();
		rlist.add(edgesNew);
		rlist.add(new ArrayList<Object>(newNodes.keySet()));
		return  rlist;
	}
	
	/**
	 * Returns filtered JSON for prov 2 graph
	 * @param isShort String 
	 * @return String
	 */

	@RequestMapping(value = "/prov/data2", method = RequestMethod.GET)
	@ResponseBody
	//produces="application/json" causes 406 if accept header isn't set
    public String provData2(@RequestParam(value = "roots", required = false) String[] roots,
    	@RequestParam(value = "o", defaultValue = "") String objN,
    	@RequestParam(value = "s", defaultValue = "") String subN,
    	@RequestParam(value = "types", required=false) ArrayList<String> types,
		@RequestParam(value = "inverted", defaultValue = "") String inverted,
		@RequestParam(value = "travelUp", defaultValue ="0") boolean travelUp,
		@RequestParam(value = "d", defaultValue ="10") int distance){	
		
		String baseURI = loader.getPath() + "/rest/";
		if(context.getAttribute("codebooks") == null) loader.refreshCodebooks(baseURI);
		JSONObject json = null;
		JSONArray nodes = null;
		JSONArray edges = null;
		JSONArray newEdges = null;
		JSONArray newNodes = null;
		JSONObject filtered = null;
		
		try {					
			json = getProv();
			nodes = (JSONArray) json.get("nodes");
			edges = (JSONArray) json.get("edges");
			filtered = new JSONObject();
		
			newEdges=  new JSONArray();
			newNodes = new JSONArray();
			List<Object> newData = null;
			List<String> nn = null;
		
			if(roots != null){
				newData = findParent2(roots, newEdges, edges, new HashMap<String,Integer>(),travelUp,distance);	
				edges = (JSONArray) newData.get(0);
			    nn = (ArrayList<String>) newData.get(1);
			}			
			
			for(Object n : nodes){
				JSONObject node = (JSONObject) n;
				String id = (String) node.get("id");
				if(roots == null || nn.contains(id)){
					if(loader.hasCodebook(id)) node.put("isCodebook","true");
					if(types != null && types.size() > 0){
						if(types.contains(node.get("nodeType"))) newNodes.add(node);
					}else{
						newNodes.add(node);
					}					
				}
			}
			nodes = newNodes;
				
			//Reverse edges 
			if(inverted.equals("true")){
				for(Object e : edges){
					JSONObject edge = (JSONObject) e;
					String target = (String) edge.get("target");
					String source = (String) edge.get("source");
					edge.put("target",source);
					edge.put("source", target);
				}
			}
			
			filtered.put("edges", edges);
			filtered.put("nodes", nodes);

			return filtered.toString();
			
		}catch(NullPointerException e) {
			e.printStackTrace();
		}finally{
			 if(json != null) json.clear();
			 if(nodes != null) nodes.clear();
			 if(edges != null) edges.clear();
			 if(newEdges != null) newEdges.clear();
			 if(newNodes != null) newNodes.clear();
			 if(filtered != null) filtered.clear();
		}
		return null;
	}	
	
//Endpoints for editing
	
	@RequestMapping(value = "/edit/prov", method = RequestMethod.GET)
	public String provEditForm(@RequestParam(value = "o", defaultValue = "") String psObject,
		@RequestParam(value = "s", defaultValue = "") String psSubject,
		Model model, HttpSession session){
		JSONObject json = getProv();
		JSONArray nodes = (JSONArray) json.get("nodes");
		JSONObject predicates = (JSONObject) json.get("predicates");
		model.addAttribute("psObject", psObject);
		model.addAttribute("psSubject", psSubject);
		model.addAttribute("nodeList", nodes);
		model.addAttribute("predList", predicates);
		model.addAttribute("subTitl","Add Relationships - Prov");
		return "/WEB-INF/editViews/prov.jsp";
	}

	@RequestMapping(value = "/edit/prov/{obj:.+}", method = RequestMethod.GET)
	public String provObjForm(@PathVariable(value = "obj") String objectID, 
	@RequestParam(value = "d", defaultValue = "") String disclosure, Model model, 
	HttpSession session, HttpServletResponse response){	   
		//Removes trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(usedURL.endsWith("/")){ 
			return "redirect:/edit/prov/"+objectID;
		}
		
		if(context.getAttribute("codebooks") == null){
			String baseURI = loader.getPath() + "/rest/";
			TreeMap<String,String[]>  codebooks = loader.getCodebooks(baseURI);
			context.setAttribute("codebooks", codebooks);
		}
		
		JSONObject json = getProv();
		JSONArray nodes = (JSONArray) json.get("nodes");
		JSONArray edges = (JSONArray) json.get("edges");
		
		JSONObject node = null;
		
		for(int i = 0; i < nodes.size(); i++){
			JSONObject nodeTemp = (JSONObject) nodes.get(i);
			if(nodeTemp.get("id").equals(objectID)){
				node = nodeTemp;
				break;
			}
		}

		if(node == null){
			response.setStatus(404);
			session.setAttribute("error", "A node with the id '"+objectID+"' does not exist");
			return "redirect:/edit/prov";
		}
		
		JSONArray outEdges = new JSONArray();
		JSONArray inEdges = new JSONArray();
		
		for(int i = 0; i < edges.size(); i++){
			JSONObject edge = (JSONObject) edges.get(i);
			if(edge.get("source").equals(objectID)){
				outEdges.add(edge);
			}
			if(edge.get("target").equals(objectID)){
				inEdges.add(edge);
			}
		}
		
		JSONObject flatPreds = getFlatPreds();
		JSONArray nodeTypes = (JSONArray) json.get("nodeTypes");
		
		if(loader.hasCodebook((String) node.get("id"))){
			model.addAttribute("isCodebook",true);
		}
		
		model.addAttribute("subTitl",objectID.toUpperCase()+" Details");
		model.addAttribute("targetNode",node);
		model.addAttribute("outEdges",outEdges);
		model.addAttribute("inEdges", inEdges);
		model.addAttribute("nodeList", nodes);
		model.addAttribute("flatPreds", flatPreds);
		model.addAttribute("nodeTypes", nodeTypes);
		
		//Print disclosure form
		if(disclosure.equals("true")){
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			Date date = new Date();
			String rp = dateFormat.format(date);
			model.addAttribute("subTitl",objectID.toUpperCase()+" Disclosure");
			model.addAttribute("rp", rp);	
			String handle =  node.get("id").toString();
			
			//If node is a codebook
			if(loader.hasCodebook(handle)){
				CodebookData codebookData = new CodebookData();
				try{		
					String xml = codebookData.getCodebook("noNamespaces", handle, "XML", "CED2AR");
					xml = xml.replace("&nbsp;", "");
					String path = context.getRealPath("/xsl/disclosure.xsl");
					String path2 = context.getRealPath("/xsl/disclosureVars.xsl");
					
					Parser xp = new Parser(xml, path, 0);
					Parser xp2 = new Parser(xml, path2, 0);
				
					model.addAttribute("disclosure",xp.getData());	
					model.addAttribute("vars",xp2.getData());	
					
				}catch(NullPointerException | NumberFormatException e){
					logger.warn(e.getMessage());	
				}											
			}
			return "/WEB-INF/editViews/disclosure.jsp";	
		}
		
		return "/WEB-INF/editViews/provObj.jsp";
	}
	
	@RequestMapping(value = "/edit/prov/{obj:.+}/edit", method = RequestMethod.POST)
	@ResponseBody
	public String provObjFormAjax(@PathVariable(value = "obj") String objectID, 
	@RequestParam(value = "pk", defaultValue = "") String field,
	@RequestParam(value = "value", defaultValue = "") String value,
	Model model, HttpSession session, HttpServletResponse response){
		String host =  loader.getHostName();
		switch(field){
			case "label":
				if(value.equals("")){
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return "";
				}else{
					Fetch.provNode(host, objectID, "", value, "", "","");
				}
			break;
			case "uri":
				Fetch.provNode(host, objectID, "", "", value, "","");
			break;
			case "date":
				Fetch.provNode(host, objectID, "", "", "", "",value);
			break;
			
		}
		return "";
	}
	
	@RequestMapping(value = "/edit/prov/edgeremove", method = RequestMethod.POST)
	@ResponseBody
	public String provEdgeRemove(
	@RequestParam(value = "src", defaultValue = "") String source,
	@RequestParam(value = "tgt", defaultValue = "") String target,
	@RequestParam(value = "typ", defaultValue = "") String type,
	Model model, HttpSession session, HttpServletResponse response){
		String host =  loader.getHostName();
		int code = Fetch.provEdgeDelete(host, source, target, type);
		if(code != 200){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return "";
	}
	
	@RequestMapping(value = "/edit/prov", method = RequestMethod.POST)
	@ResponseBody
	public String provEditPost(Model model, HttpSession session, HttpServletResponse response,
	@RequestParam(value = "provObjExisting", defaultValue = "") String provObjExisting,
	@RequestParam(value = "provSubExisting", defaultValue = "") String provSubExisting,
			
	@RequestParam(value = "provObjClass", defaultValue = "") String provObjClass,
	@RequestParam(value = "objID", defaultValue = "") String objID,
	@RequestParam(value = "objLabel", defaultValue = "") String objLabel,
	@RequestParam(value = "objURI", defaultValue = "") String objURI,
	
	@RequestParam(value = "provSubClass", defaultValue ="") String provSubClass,
	@RequestParam(value = "subID", defaultValue = "") String subID,
	@RequestParam(value = "subLabel", defaultValue = "") String subLabel,
	@RequestParam(value = "subURI", defaultValue = "") String subURI,
	@RequestParam(value = "provPred") String provPred,
	
	@RequestParam(value = "objSelect") String objSelect,
	@RequestParam(value = "subSelect") String subSelect)
	{		

		String objectID = "";
		String subjectID = "";
		String host =  loader.getHostName();

		if(objSelect.equals("existingNode")){
			objectID = provObjExisting.split("#")[0];
		}else{
			if(provObjClass.equals("") || objID.equals("") 
			|| objLabel.equals("") || objURI.equals("")){
				return "Missing fields";
			}
			
			objID = objID.replaceAll("[^A-Za-z0-9]\\.\\-", "").toLowerCase();
			objLabel = objLabel.replaceAll("[^A-Za-z0-9 ]", "");
			
			if(!Utilities.validateURI(objURI)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Invalid object URI";
			}
			int code = Fetch.provNode(host, objID, provObjClass, objLabel, objURI, "true","");	
			if(code != 200){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Object ID already exists";
			}
			objectID = objID;
		}
		
		if(subSelect.equals("existingNode")){
			subjectID = provSubExisting.split("#")[0];
		}else{
			if(provSubClass.equals("") || subID.equals("") 
			|| subLabel.equals("") || subURI.equals("")){
				return "Missing fields";
			}
			
			subID = subID.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
			subLabel = subLabel.replaceAll("[^A-Za-z0-9 ]", "");
			
			if(!Utilities.validateURI(subURI)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Invalid subject URI";
			}	
			int code = Fetch.provNode(host, subID, provSubClass, subLabel, subURI, "true","");			
			if(code != 200){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Subject ID already exists";
			}
			subjectID = subID;		
		}
		
		int code = Fetch.provEdge(host, "-1", objectID, subjectID, provPred,"true");
		if(code != 200){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Relationship already exists";
		}
		
		clearProvCache();
		return "Prov triple successfully added";
	}
	
	@RequestMapping(value = "/edit/prov2", method = RequestMethod.GET)
	public String provEditForm2(Model model, HttpSession session, HttpServletRequest request){
		
		String jsonData = Fetch.get(loader.getPath()+"/rest/prov");
		try{
			JSONObject json = (JSONObject) new JSONParser().parse(jsonData);
			JSONArray nodes = (JSONArray) json.get("nodes");
			JSONObject predicates = (JSONObject) json.get("predicates");
			model.addAttribute("nodeList", nodes);
			model.addAttribute("predList", predicates);
		}catch(ParseException e){
			logger.error("Error parsing JSON from internal database: "+e.getMessage());
		}
		model.addAttribute("subTitl","Add Input - Prov");

		return "/WEB-INF/editViews/prov2.jsp";
	}
	
	@RequestMapping(value = "/edit/prov2", method = RequestMethod.POST)
	@ResponseBody
	public String provEditPost2(Model model, HttpSession session, HttpServletResponse response,
	@RequestParam(value = "objURL", defaultValue = "") String objURL,
	@RequestParam(value = "provObjClass", defaultValue = "") String provObjClass,
	@RequestParam(value = "objLabel", defaultValue = "") String objLabel,
	@RequestParam(value = "objLocal", defaultValue = "") String objLocal,
	@RequestParam(value = "idOverride", defaultValue = "") String idOverride)
	{		
		String objectID = "";
		String objectURI = "";
		String objectClass ="0";
		
		String host =  loader.getHostName();

		if(!objLocal.equals("")){
			objectID = objLocal;
			objectURI = "ftp://"+request.getRemoteAddr() + "/"+objectID;	
		}else if(!objURL.equals("")){
			//Parses URL into object ID
			if(!Utilities.validateURI(objURL)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Invalid subject URI";
			}else{
				objectURI = objURL;
				String[] urlParts = objURL.split("/");
				objectID = urlParts[urlParts.length-1].replaceAll("[^A-Za-z0-9]", "");
				if(objectID.length() > 24)
					objectID = objectID.substring(0,23);
			}
		}else{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Input location not selected";
		}
		
		//Check if trimmed label is empty
		objLabel= objLabel.trim();
		if(objLabel.equals("")){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "A label is required";
		}
		
		//Label must have valid characters
		objLabel= objLabel.replaceAll("[^A-Za-z0-9]","");
		if(objLabel.equals("")){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Invalid label input";
		}
		
		if(!idOverride.equals("")) objectID = idOverride;
		objectID = objectID.toLowerCase().replaceAll("\\.", "\\-").replaceAll("[^A-Za-z0-9\\-_]", "");
		JSONObject json = getProv();
		JSONArray nodes = (JSONArray) json.get("nodes");
		for(int i = 0; i < nodes.size(); i++){
			JSONObject node = (JSONObject) nodes.get(i);
			if(node.get("id").equals(objectID)){
				//Adds timestamp to object ID if duplicated
				response.setHeader("objectID", objectID);
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				return "duplicateID";
			}	
		}
		
		//Not %100 sure what classes to have, and what they should corresponds to in terms of prov
		if(provObjClass.equals("stat") || provObjClass.equals("script")){
			objectClass ="1";
		}else if(provObjClass.equals("doc")){
			objectClass ="2";
		}
		
		Fetch.provNode(host, objectID, objectClass, objLabel, objectURI, "true","");			
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("objectID", objectID);
		clearProvCache();
		return "Input successfully added. Entity id is "+objectID;
	}
	
	@RequestMapping(value = "/edit/prov3", method = RequestMethod.GET)
	public String provEditForm3(Model model, HttpSession session, HttpServletRequest request){

		if(session.getAttribute("codebooks") == null){
			String baseURI = loader.getPath() + "/rest/";
			loader.getCodebooks(baseURI);
		}
		
		model.addAttribute("pageWidth", 1);
		model.addAttribute("subTitl","Add Replication Results - ");
		return "/WEB-INF/editViews/prov3.jsp";
	
	}
	
	@RequestMapping(value = "/edit/prov3", method = RequestMethod.POST)
	@ResponseBody
	public String provEditPost3(Model model, HttpSession session, 
	HttpServletRequest request, HttpServletResponse response,
	@RequestParam(value = "inputType") String inputType,
	@RequestParam(value = "inputID") String inputID,
	@RequestParam(value = "inputLabel") String inputLabel,
	@RequestParam(value = "inputURI") String inputURI,
	@RequestParam(value = "progID") String progID,
	@RequestParam(value = "progLabel") String progLabel,
	@RequestParam(value = "progURI") String progURI,
	@RequestParam(value = "outputID") String outputID,
	@RequestParam(value = "outputLabel") String outputLabel,
	@RequestParam(value = "outputURI") String outputURI,
	@RequestParam(value = "codebook", defaultValue = "") String codebook
	){
		inputID = inputID.trim();
		progID = progID.trim();
		outputID = outputID.trim();
		
		boolean newInput = inputType.equals("existing") ? false : true;
	
		if(!Utilities.validateURI(inputURI)){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Invalid input URI";
		}
		
		if(!Utilities.validateURI(progURI)){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Invalid program URI";
		}
		
		if(!Utilities.validateURI(outputURI)){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Invalid ouput URI";
		}
		
		JSONObject json = getProv();
		JSONArray nodes = (JSONArray) json.get("nodes");

		for(Object n : nodes){
			JSONObject node = (JSONObject) n;
			String id = (String) node.get("id");
		
			if(id.equals(inputID) && newInput){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "For step #1, Input ID already exists";
			}else if(id.equals(progID) ){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "For step #2, Program ID already exists";
			}else if(id.equals(outputID) ){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "For step #3, Output ID already exists";
			}
		}
		
		
		
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date dateStamp = new Date();
		String rp = dateFormat.format(dateStamp);
		String date = rp+" (imported to CED2AR)";
		
		EditProvData editProvData = new EditProvData();
		
		if(newInput){
			editProvData.editNode(inputID, inputLabel, "0", inputURI, date, true);
			if(editProvData.getError() != null){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "For step #1, Input: " + editProvData.getError();
			}
		}
		
		editProvData.editNode(progID, progLabel, "1", progURI, date, true);
		if(editProvData.getError() != null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "For step #2, Program: " + editProvData.getError();
		}		

		editProvData.editNode(outputID, outputLabel, "0", outputURI, date, true);
		if(editProvData.getError() != null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "For step #3, Output: " + editProvData.getError();
		}	
				
		//TODO: Create links
		//TODO: Clear cached graph
		editProvData.editProvEdge("-1", "wgb", outputID, progID, true, false);
		if(editProvData.getError() != null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Error creating link between output and program: " + editProvData.getError();
		}	
		
		editProvData.editProvEdge("-1", "u", progID, inputID, true, false);
		if(editProvData.getError() != null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Error creating link between program and input: " + editProvData.getError();
		}	
				
		if(!codebook.equals("")){
			editProvData.editProvEdge("-1", "wdf", codebook, inputID, true, false);
			if(editProvData.getError() != null){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Error creating link between codebook and input: " + editProvData.getError();
			}	
		}
		
		clearProvCache();
		
		response.setStatus(HttpServletResponse.SC_OK);
		return "";
	
	}
	
	@RequestMapping(value = "/edit/prov3b", method = RequestMethod.GET)
	public String provEditForm3b(Model model, HttpSession session, HttpServletRequest request){
		
		if(session.getAttribute("codebooks") == null){
			String baseURI = loader.getPath() + "/rest/";
			loader.getCodebooks(baseURI);
		}
		
		model.addAttribute("pageWidth", 1);
		model.addAttribute("subTitl","Workflow View");
		return "/WEB-INF/editViews/prov3b.jsp";
	
	}
	
	@RequestMapping(value = "/prov3", method = RequestMethod.GET)
	public String prov3(Model model, HttpSession session, HttpServletRequest request, 
	@RequestParam(value = "start", defaultValue ="0") String start){
		
		if(session.getAttribute("codebooks") == null){
			String baseURI = loader.getPath() + "/rest/";
			loader.getCodebooks(baseURI);
		}
		
		model.addAttribute("startingNode", start);
		model.addAttribute("pageWidth", 1);
		model.addAttribute("subTitl","Workflow View");
		return "/WEB-INF/views/prov3.jsp";
	
	}
	
	@RequestMapping(value = "/prov4", method = RequestMethod.GET)
	public String prov4(Model model, HttpSession session, HttpServletRequest request, 
	@RequestParam(value = "start", defaultValue ="0") String start){		
		if(session.getAttribute("codebooks") == null){
			String baseURI = loader.getPath() + "/rest/";
			loader.getCodebooks(baseURI);
		}
		
		model.addAttribute("startingNode", start);
		model.addAttribute("pageWidth", 1);
		model.addAttribute("subTitl","Workflow View");
		return "/WEB-INF/views/prov4.jsp";	
	}
	
	@RequestMapping(value = "/prov/data3", method = RequestMethod.GET)
	@ResponseBody
	public String provData3(Model model, HttpSession session, HttpServletRequest request, 
	@RequestParam(value = "start", defaultValue = "0") int start,
	@RequestParam(value = "depth", defaultValue = "1") int depth){ 		
		return Neo4jUtil.selectNode2(start,depth);	
	}
	
	@RequestMapping(value = "/prov/data4", method = RequestMethod.GET)
	@ResponseBody
	public String provData4(Model model, HttpSession session, HttpServletRequest request, 
	@RequestParam(value = "start", defaultValue = "0") int start,
	@RequestParam(value = "depth", defaultValue = "1") int depth){ 		
		return Neo4jUtil.selectNode4(start,depth);	
	}

	@RequestMapping(value = "/prov/data/repec/authors", method = RequestMethod.GET)
	@ResponseBody
	public String repecAuthorSearch(Model model, HttpSession session, HttpServletRequest request, 
	@RequestParam(value = "query", defaultValue = "") String query,
	@RequestParam(value = "limit", defaultValue = "30") int limit){ 	
		return query.length() > 1 ? Neo4jUtil.searchAuthors(query, limit) : null;	
	}
	
	@RequestMapping(value = "/prov4b", method = RequestMethod.GET)
	public String repecLanding(Model model, HttpSession session, HttpServletRequest request,
	@RequestParam(value = "start", defaultValue ="45") String start){
		model.addAttribute("startingNode", start);
		model.addAttribute("pageWidth", 1);
		model.addAttribute("subTitl","Workflow View");
		return "/WEB-INF/views/repec/search.jsp";	
	}
}