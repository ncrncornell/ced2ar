package edu.ncrn.cornell.ced2ar.ei.controllers;

import java.util.Hashtable;
import java.util.List;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
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
public class EditVarGroups {
	
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
		
		//TODO:Move somewhere else
		/**
		 * Retrieves variables in JSON format for autocomplete
		 * @param handle
		 * @param response
		 * @return
		 */
		@RequestMapping(value = "/data/codebooks/{h}/vars", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public String jsonVarsInCodebook(@PathVariable(value = "h") String handle, HttpServletResponse response){
			String apiURL = loader.getPath()+"/rest/codebooks/"+handle+"/variables";
			String json = Fetch.getJson(apiURL)[0];
			response.setContentType("application/json");
			return json;
		}
		
		/**
		 * Retrieves variables in JSON format for autocomplete
		 * @param handle
		 * @param response
		 * @return
		 */
		@RequestMapping(value = "/data/codebooks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public String jsonCodebookList(HttpServletResponse response){
			String apiURL = loader.getPath()+"/rest/codebooks/";
			String json = Fetch.getJson(apiURL)[0];
			response.setContentType("application/json");
			return json;
		}
		
		@RequestMapping(value = "/data/prov/nodes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public String jsonProvNodes(HttpServletResponse response){
			JSONArray nodes = null;
			String jsonData = Fetch.get(loader.getPath()+"/rest/prov");
			try{
				JSONObject json = (JSONObject) new JSONParser().parse(jsonData);
				nodes = (JSONArray) json.get("nodes");
			}catch(ParseException e){
				logger.error("Error parsing JSON from internal database: "+e.getMessage());
			}
			response.setContentType("application/json");
			return nodes.toString();
		}
		
//Endpoints
	
	/**
	 * Shows form to add a group
	 * @param baseHandle
	 * @param version
	 * @param model
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/groups", method = RequestMethod.GET)
	public String addGroupForm(@PathVariable(value = "c") String baseHandle, @PathVariable(value = "v") String version, 
	Model model, HttpSession session){
		
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle+version;
		
		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	
		
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}else{
			codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
		}
		
		String title = codebooks.get(handle)[4];
		
		String codebookURL = "edit/codebooks/"+baseHandle+"/v/"+version+"/";
		String[][] crumbs = new String[][] {
			{title,codebookURL},
			{"Add Variable Group",""}
		};
		
		model.addAttribute("subTitl", "Edit Group ("+handle.toUpperCase()+")");
		model.addAttribute("crumbs", crumbs);
		model.addAttribute("title", title);
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version",version);
		return "/WEB-INF/editViews/groupAdd.jsp";
	}
	
	/**
	 * Endpoint that adds a group
	 * @param baseHandle
	 * @param version
	 * @param name
	 * @param label
	 * @param desc
	 * @param model
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/groups", method = RequestMethod.POST)
	public String addGroup(@PathVariable(value = "c") String baseHandle, @PathVariable(value = "v") String version, 
	@RequestParam(value="name", required = true) String name,
	@RequestParam(value="label", required = true) String label,
	@RequestParam(value="desc", required = true) String desc,
	Model model, HttpSession session, HttpServletResponse response){
		
		if(name.equals("") || label.equals("")){
			session.setAttribute("error2", "Name and label are required");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		String handle = baseHandle+version;
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version",version);
		
		String groupID = QueryUtil.pickGroupID(handle);
		String host = loader.getHostName();
		int code = Fetch.groupEdit(host, baseHandle, version, groupID, name, label, desc);
		
		if(code > 0 && code < 400){
			session.setAttribute("info_splash","Changes Saved. Added group '"+name+"'");
		}else if(code < 500){
			session.setAttribute("error", "Your request could not be completed.\nYour edit may have created an invalid document.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{
			session.setAttribute("error", "There was a problem connecting to the database.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/groups";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/groups/{id}", method = RequestMethod.GET)
	public String groupEditForm(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, @PathVariable(value = "id") String id,
	@RequestParam(value = "d-1341904-s", defaultValue = "") String sortCol, //field to sort by 
	@RequestParam(value = "d-1341904-o", defaultValue = "2") String sortDir, //direction to sort by
	Model model, HttpSession session, HttpServletResponse response) throws Exception{
		
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle+version;
		
		//Temp solution to remove trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(usedURL.endsWith("/")){ 
			return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/groups/"+id;
		}
		
		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	
	
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}else{
			codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
		}
		
		//Parsing sort dir to int, and subtracting one to line up with indexing
		//Does not use cacher class
		String apiURI = baseURI+"codebooks/"+handle+"/vargroups/"+id+"/vars";
		int sc = sortCol.equals("") ? -1 : Integer.parseInt(sortCol) -1;
		boolean rs = sortDir.equals("2") ? false : true;//If sorting is reversed
		String[] data = Fetch.getCSV(apiURI);
		Parser xp = new Parser(data[0]);
		List<String[]> results = xp.getDisplayTagDataVarGrp(sc,rs);
		
		String apiURI2 = baseURI+"codebooks/"+handle+"/vargroups/"+id;
		String xml = Fetch.getShortXML(apiURI2)[0];
		Parser xp2 = new Parser(xml);
		String groupName = xp2.getAttrValue("/varGrp", "name");
		String groupDesc= xp2.getValue("/varGrp/txt");
		
		String codebookURL = "edit/codebooks/"+baseHandle+"/v/"+version+"/";
		String[][] crumbs = new String[][] {
			{codebooks.get(handle)[4],codebookURL},
			{"Variable Groups","/codebooks/"+baseHandle+"/v/"+version+"/groups"},
			{groupName,""}
		};
	
		model.addAttribute("groupName", groupName);
		model.addAttribute("groupDesc", groupDesc);
		model.addAttribute("groupID", id);
		model.addAttribute("data", results);
		model.addAttribute("count", data[1]);
		model.addAttribute("crumbs", crumbs);
		model.addAttribute("codebookInfo", codebooks.get(handle));	
		model.addAttribute("subTitl","Editing "+groupName+" ("+handle.toUpperCase()+")");
		model.addAttribute("handle", handle);
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version", version);

		return "/WEB-INF/editViews/groupEdit.jsp";
	}
	
	/**
	 * Displays edit form for field in a group
	 * @param baseHandle
	 * @param version
	 * @param id
	 * @param field
	 * @param model
	 * @param session
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/groups/{id}/edit", method = RequestMethod.GET)
	public String groupEditForm2(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, @PathVariable(value = "id") String id,
	@RequestParam(value = "f", defaultValue = "") String field,
	Model model, HttpSession session, HttpServletResponse response) throws Exception{
		
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle+version;

	  	//List of acceptable elements or attributes to edit TODO: put this somewhere else, make sure garbage collection is working
		Hashtable<String,String[]> validFields = new Hashtable<String,String[]>();
		validFields.put("txt",new String[] {"1","/varGrp/txt","Group Description"});
		if(validFields.containsKey(field)){		
			String[] info = validFields.get(field);
			String apiURL = baseURI + "codebooks/"+handle+"/vargroups/"+id;
			String xml = Fetch.getShortXML(apiURL)[0];
			Parser xp = new Parser(xml);
			String curVal = xp.getValue(info[1]);
			model.addAttribute("curVal",curVal);	
			model.addAttribute("title",info[2]);	
		}

		return "/WEB-INF/ajaxViews/groupFieldEdit.jsp";
	}
	
	/**
	 * Edits a field in a group
	 * @param baseHandle
	 * @param version
	 * @param id
	 * @param field
	 * @param newValue
	 * @param model
	 * @param session
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/groups/{id}/edit", method = RequestMethod.POST)
	public String groupEditForm2P(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, @PathVariable(value = "id") String id,
	@RequestParam(value = "f", defaultValue = "") String field,
	@RequestParam(value = "newValue", defaultValue = "") String newValue,
	Model model, HttpSession session, HttpServletResponse response) throws Exception{
		
		String host = loader.getHostName();
		String name = "";
		String label = "";
		String txt = "";
		switch(field){
			case "name":
				name = newValue;
			break;
			case "label":
				label = newValue;
			break;
			case "txt":
				txt = newValue;
			break;
		}
		
		int code = Fetch.groupEdit(host, baseHandle, version, id, name, label, txt);
		if(code > 0 && code < 400){
			session.setAttribute("info_splash","Changes Saved. Added group '"+name+"'");
		}else if(code < 500){
			session.setAttribute("error", "Your request could not be completed.\nYour edit may have created an invalid document.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{
			session.setAttribute("error", "There was a problem connecting to the database.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/groups/"+id;
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/groups/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String groupEdit(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, @PathVariable(value = "id") String id,
	@RequestParam(value="var", required = true) String var,
	@RequestParam(value="add", required = true) boolean add,
	Model model, HttpSession session, HttpServletResponse response) throws Exception{		
		String host = loader.getHostName();
		int code = Fetch.groupVarChange(host, baseHandle, version, id, var, add);
		return Integer.toString(code);
	}
	
	//TODO: implement auto complete somewhere
	/**
	 * Test function
	 * @param baseHandle
	 * @param version
	 * @param id
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/autocomplete", method = RequestMethod.GET)
	public String autoComplete(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, Model model, HttpSession session){;
		String handle = baseHandle+version;

		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	

		model.addAttribute("varAutoComplete",true);
		model.addAttribute("handle", handle);
		return "/WEB-INF/editViews/autoComplete.jsp";
	}
}