package edu.ncrn.cornell.ced2ar.ei.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.UrlValidator;
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

import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;

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
	
//Utilities
	
	/**
	 * Check to see if a URI is validate
	 * @param uri
	 * @return
	 */
	//TODO: Move somewhere else
	public static boolean validateURI(String uri){
		//String[] types = {"http","https","ftp","file"};
		//UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_LOCAL_URLS
		//TODO: not validating URI's from local file system, or with file extension
		UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		return validator.isValid(uri);
	}
	
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
			Map.Entry entry = (Map.Entry)iter.next();
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
	
//Endpoints
	
	@RequestMapping(value = "/edit/prov", method = RequestMethod.GET)
	public String provEditForm(@RequestParam(value = "o", defaultValue = "") String psObject,
		@RequestParam(value = "s", defaultValue = "") String psSubject,
		Model model, HttpSession session){
		//TODO: have preselected object 
		//TODO: have inverse relationships
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
			model.addAttribute("print",true);
			model.addAttribute("subTitl",objectID.toUpperCase()+" Disclosure");
			model.addAttribute("rp", rp);	
			String handle =  node.get("id").toString();
			
			//If node is a codebook
			if(loader.hasCodebook(handle)){
				String apiURI = loader.getPath() + "/rest/codebooks/"+ handle+"?type=noNamespaces";
				try{		
					String xml = Fetch.getShortXML(apiURI)[0];
					xml = xml.replace("&nbsp;", "");
					String path = context.getRealPath("/xsl/disclosure.xsl");//Local file path to find XSL doc
					Parser xp = new Parser(xml, path, 0);
					model.addAttribute("vars", xp.getData());	
					
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
			
			if(!validateURI(objURI)){
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
			
			if(!validateURI(subURI)){
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
			if(!validateURI(objURL)){
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
		
		if(!idOverride.equals(""))
			objectID = idOverride;
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
}