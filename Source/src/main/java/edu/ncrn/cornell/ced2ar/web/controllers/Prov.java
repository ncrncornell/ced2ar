package edu.ncrn.cornell.ced2ar.web.controllers;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;

/**
 *Handles requests to prov graphs
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Prov {
	
	private static final Logger logger = Logger.getLogger(Prov.class);
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	Config config;
	
	@Autowired
	Loader loader;
	
	//TODO: remove duplicate prov cacher
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
	
	@CacheEvict(value = "prov", allEntries = true)
	public void clearProvCache() {}

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
	private List<Object> findParent2(String[] roots, JSONArray edgesNew, JSONArray edgesOld, List<String> newNodes){
		for(String root : roots){
			if(!newNodes.contains(root)){
				newNodes.add(root);
			}
			
			for(int i = 0; i < edgesOld.size(); i++){
				JSONObject edge = (JSONObject) edgesOld.get(i);
				String source = (String) edge.get("source");
				if(source.equals(root)){
					if(!edgesNew.contains(edge)){
						edgesNew.add(edge);
						String target = (String) edge.get("target");
						if(source != target){
							findParent2(new String[] {target},edgesNew,edgesOld,newNodes);
						}
					}
				}
			}
		}
		List<Object> rlist = new ArrayList<Object>();
		rlist.add(edgesNew);
		rlist.add(newNodes);
		return  rlist;
	}
	
	/**
	 * Returns filtered JSON for prov 2 graph
	 * @param isShort String 
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/prov/data2", method = RequestMethod.GET)
	@ResponseBody
	// produces="application/json" causes 406 if accept header isn't set
    public String provData2(@RequestParam(value = "short", defaultValue = "0") String isShort, 
    	@RequestParam(value = "roots", required = false) String[] roots,
    	@RequestParam(value = "o", defaultValue = "") String objN,
    	@RequestParam(value = "s", defaultValue = "") String subN,
		@RequestParam(value = "inverted", defaultValue = "") String inverted){	
		
		try {					
			JSONObject json = getProv();
			JSONArray nodes = (JSONArray) json.get("nodes");
			JSONArray edges = (JSONArray) json.get("edges");
			JSONObject filtered = new JSONObject();
			//Select two nodes to focus on
			if(!objN.equals("") && !subN.equals("")){
				JSONArray[] objSub = selectTwoNodes(objN, subN, edges, nodes);
				nodes = objSub[0];
				edges = objSub[1];
			}else if(roots != null){			
				JSONArray newEdges = new JSONArray();
				JSONArray newNodes = new JSONArray();
				List<Object> newData = findParent2(roots, newEdges, edges, new ArrayList<String>());
				edges = (JSONArray) newData.get(0);
				List<String> nn = (ArrayList<String>) newData.get(1);
				
				for(Object n : nodes){
					JSONObject node = (JSONObject) n;
					String id = (String) node.get("id");
					if(nn.contains(id)){
						newNodes.add(node);
					}
				}
				nodes = newNodes;
			}
			
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
		}
		return null;
	}	
}