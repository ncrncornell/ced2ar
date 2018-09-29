package edu.cornell.ncrn.ced2ar.ei.controllers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.api.rest.queries.CodebookData;
import edu.cornell.ncrn.ced2ar.eapi.neo4j.Neo4jUtil;
import edu.cornell.ncrn.ced2ar.web.classes.Loader;
import edu.cornell.ncrn.ced2ar.web.classes.Parser;

/**
 * Class to handle the workflow replication
 *
 * @author Cornell University, Copyright 2012-2015
 * @author Ben Perry
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team
 */
@Controller
// TODO: cleanup and move items into API
// TODO: Integrate with Neo4j
public class EditWorkflow {

	private static final Logger logger = Logger.getLogger(EditWorkflow.class);

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

	// Endpoints

	/**
	 * Displays the main flow
	 * 
	 * @param model
	 *            Model the current model
	 * @return String
	 */
	@RequestMapping(value = "/edit/workflow", method = RequestMethod.GET)
	public String main(Model model,@RequestParam(value = "start", defaultValue="145252") String start) {
		if(session.getAttribute("codebooks") == null){
			String baseURI = loader.getPath() + "/rest/";
			loader.getCodebooks(baseURI);
		}
		
		model.addAttribute("startingNode", start);
		model.addAttribute("pageWidth", 1);
		model.addAttribute("subTitl","Workflow View");
		return "/WEB-INF/workflowViews/workflow.jsp";
	}
	
	/**
	 * Displays a node's details
	 * 
	 * @param model
	 *            Model the current model
	 * @return String
	 */
	@RequestMapping(value = "/edit/workflow/n/{id}", method = RequestMethod.GET)
	public String showNode(Model model, @PathVariable(value = "id") int id,
	@RequestParam(value = "d", defaultValue = "0") boolean disclosure) {

		// Removes trailing slash
		String usedURL = (String) request
				.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if (usedURL.endsWith("/")) {
			return "redirect:/edit/workflow/n/" + id;
		}

		String rawJSON = Neo4jUtil.selectNode3(id);
		JSONArray edges = new JSONArray();
		try {
			JSONObject obj = new JSONObject(rawJSON);
			JSONObject results = (JSONObject) ((JSONArray) obj.get("results")).get(0);
			edges = (JSONArray) results.get("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// type = type.substring(0, 1).toUpperCase() +
		// type.substring(1).toLowerCase();
		String rawJSON2 = Neo4jUtil.fetchNode(id);
		JSONObject details = new JSONObject();
		String type = "";
		try {
			JSONObject obj = new JSONObject(rawJSON2);
			JSONObject results = (JSONObject) ((JSONArray) obj.get("results"))
					.get(0);
			JSONObject rows = (JSONObject) ((JSONArray) results.get("data"))
					.get(0);
			JSONArray firstRow = (JSONArray) rows.get("row");
			details = (JSONObject) firstRow.getJSONObject(0);
			type = firstRow.get(1).toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		model.addAttribute("subTitl", "Node Details");
		model.addAttribute("type", type);
		model.addAttribute("details", details);
		model.addAttribute("edges", edges);
		model.addAttribute("id", id);
		
		if(disclosure){
			//TODO: Generate release request
			//Need handle, throw error if not local

			//DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			//Date date = new Date();
			//String rp = dateFormat.format(date);
			
			if(type.equals("Dataset")){
			
				CodebookData codebookData = new CodebookData();
				try{	
					String handle = details.getString("handle").replaceAll("\\.", "");
					if(!handle.equals("")){
						String xml = codebookData.getCodebook("noNamespaces", handle, "XML", "CED2AR");
						xml = xml.replace("&nbsp;", "");
						//TODO: add back var info
						//String path = context.getRealPath("/xsl/disclosure.xsl");
						String path2 = context.getRealPath("/xsl/disclosureVars.xsl");
						
						//Parser xp = new Parser(xml, path, 0);
						Parser xp2 = new Parser(xml, path2, 0);
					
						//model.addAttribute("disclosure",xp.getData());	
						model.addAttribute("vars",xp2.getData());	
						}
				}catch(JSONException|NullPointerException | NumberFormatException e){
					logger.warn(e.getMessage());	
				}											
			}

			return "/WEB-INF/workflowViews/disclosure.jsp";
		}

		return "/WEB-INF/workflowViews/node.jsp";
	}

	@RequestMapping(value = "/edit/workflow/add-chain", method = RequestMethod.GET)
	public String addChainForm(Model model) {
		String rawJSON = Neo4jUtil.fetchNodes("Dataset", 1000);
		String rawJSON2 = Neo4jUtil.fetchNodes("Provider", 1000);
		JSONArray rows = new JSONArray();
		JSONArray rows2 = new JSONArray();
		try {
			JSONObject obj = new JSONObject(rawJSON);
			JSONObject results = (JSONObject) ((JSONArray) obj.get("results"))
					.get(0);
			rows = (JSONArray) results.get("data");
			JSONObject obj2 = new JSONObject(rawJSON2);
			JSONObject results2 = (JSONObject) ((JSONArray) obj2.get("results"))
					.get(0);
			rows2 = (JSONArray) results2.get("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("subTitl", "Replicate a new Workflow");
		model.addAttribute("inputs", rows);
		model.addAttribute("providers", rows2);

		return "/WEB-INF/workflowViews/add.jsp";
	}

	@RequestMapping(value = "/edit/workflow/add-chain", method = RequestMethod.POST)
	@ResponseBody
	public String addChain(Model model, HttpServletResponse response,
	@RequestParam(value = "inputType") String inputType,
	@RequestParam(value = "inputName") String inputName,
	@RequestParam(value = "inputURI") String inputURI,
	@RequestParam(value = "progName") String progName,
	@RequestParam(value = "progURI") String progURI,
	@RequestParam(value = "outputName") String outputName,
	@RequestParam(value = "outputURI") String outputURI) {

		boolean newInput = inputType.equals("existing") ? false : true;

		inputName = inputName.replaceAll("[^a-zA-Z0-9\\-\\._ ]", "");
		progName = progName.replaceAll("[^a-zA-Z0-9\\-\\._ ]", "");
		outputName = outputName.replaceAll("[^a-zA-Z0-9\\-\\. ]", "");

		// TODO: Fix uri validation, disabling for now. Won't validate non-http or ftp uri's
		/*
		 * if(!Utilities.validateURI(inputURI)){
		 * response.setStatus(HttpServletResponse.SC_BAD_REQUEST); return
		 * "Invalid input URI"; }
		 * 
		 * if(!Utilities.validateURI(progURI)){
		 * response.setStatus(HttpServletResponse.SC_BAD_REQUEST); return
		 * "Invalid program URI"; }
		 * 
		 * if(!Utilities.validateURI(outputURI)){
		 * response.setStatus(HttpServletResponse.SC_BAD_REQUEST); return
		 * "Invalid ouput URI"; }
		 */
		

		if (inputName.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Input name cannot be blank";
		}
		

		if (progName.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Program name cannot be blank";
		}


		if (outputName.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Program name cannot be blank";
		}
		
		if (inputName.equalsIgnoreCase(outputName)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Input name cannot match output name";
		}

		if (newInput && Neo4jUtil.nodeCount("Input", inputName) > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Input name already exists";
		}

		if (Neo4jUtil.nodeCount("Program", progName) > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Program name already exists";
		}

		if (Neo4jUtil.nodeCount("Output", outputName) > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Output name already exists";
		}

		// TODO: gracefully handle errors and abort transaction
		// Add nodes
		if (newInput) {
			Neo4jUtil.insertDataset(inputName, inputName, "", "");
		}
		
		Neo4jUtil.insertProgram(progName, progName, "", "", "");
		Neo4jUtil.insertDataset(outputName, outputName, "", "");

		Neo4jUtil.insertEdgeUsedBy(inputName, progName);
		Neo4jUtil.insertEdgeProduced(progName, outputName);

		return "";
	}

// TODO: Move this and similar functions into the API?
	
	@RequestMapping(value ="/prov/data/workflow", method = RequestMethod.GET)
	@ResponseBody
	public String searchWorkflow(@RequestParam(value = "q") String query,
	@RequestParam(value = "t", defaultValue="") String type,
	@RequestParam(value = "l", defaultValue="0") int limit){
		if(limit <= 0) limit = 25;
		if(type.equals("")){
			return Neo4jUtil.workflowSearch(query, limit);
		}else{
			return Neo4jUtil.workflowSearch(query, limit,type);
		}
		
	}
	
	/**
	 * Edits a field for a node
	 * 
	 * @param response
	 * @param id
	 * @param type
	 * @param field
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/edit/workflow/n/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String editNode(HttpServletResponse response,
			@PathVariable(value = "id") String id,
			@RequestParam(value = "pk") String field,
			@RequestParam(value = "value") String value) {
		// TODO: Logic for approved fields
		value = value.replaceAll("[^a-zA-Z0-9\\-\\._ ]", "");
		Neo4jUtil.setProperty(id, field, value);
		return "";
	}
	
	@RequestMapping(value = "/edit/workflow/add", method = RequestMethod.GET)
	public String addNodeForm(Model model){
		model.addAttribute("subTitl","Add Node");
		return "/WEB-INF/workflowViews/addNode.jsp";
	}

	/**
	 * Adds a node
	 * @param response
	 * @param type
	 * @param name
	 * @param author
	 * @param uri
	 * @param doi
	 * @param handle
	 * @param notes
	 * @return
	 */
	@RequestMapping(value = "/edit/workflow/add", method = RequestMethod.POST)
	@ResponseBody
	public String addNode(HttpServletResponse response,
	@RequestParam(value = "type") String type,
	@RequestParam(value = "name") String name,
	@RequestParam(value = "author", defaultValue = "") String author,
	@RequestParam(value = "uri", defaultValue = "") String uri,
	@RequestParam(value = "doi", defaultValue = "") String doi,
	@RequestParam(value = "handle", defaultValue = "") String handle,
	@RequestParam(value = "notes", defaultValue = "") String notes) {

		type = type.substring(0, 1).toUpperCase()
		+ type.substring(1).toLowerCase();
		/*
		if (!type.equals("Provider") && !type.equals("Dataset")
				&& !type.equals("Program")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Invalid node type";
		}*/

		if (Neo4jUtil.nodeCount(type, name) > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return "Name already exists";
		}

		String r;		
		switch (type) {
			case "Provider":
				r = Neo4jUtil.insertProvider(name, uri);
			break;
			case "Dataset":
				r = Neo4jUtil.insertDataset(name, name, uri, doi, handle, notes);
			break;
			case "Program":
				r = Neo4jUtil.insertProgram(name, name, uri, author, notes);
			break;
			default:
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Invalid node type";	
		}
	
		JSONObject obj;
		try {
			obj = new JSONObject(r);
			JSONObject results = (JSONObject) ((JSONArray) obj.get("results")).get(0);
			JSONObject row = (JSONObject) ((JSONArray) results.get("data")).get(0);
			String id = row.getString("row");
			id = id.substring(1, id.length()-1);		
			response.setStatus(HttpServletResponse.SC_CREATED);
			return id;
		
		} catch (JSONException|NullPointerException e) {
			e.printStackTrace();
			return "Error connecting to the database";
		}
	}
	
	@RequestMapping(value = "/edit/workflow/edge", method = RequestMethod.GET)
	public String addEdgeForm(Model model){
		model.addAttribute("subTitl","Add Edge");
		return "/WEB-INF/workflowViews/addEdge.jsp";
	}
	
	@RequestMapping(value = "/edit/workflow/edge", method = RequestMethod.POST)
	@ResponseBody
	public String addEdge(HttpServletResponse response,
	@RequestParam(value = "source") String source,
	@RequestParam(value = "target") String target,
	@RequestParam(value = "type") String type){
		int s = Integer.parseInt(source.split("\\.")[0]);
		int t = Integer.parseInt(target.split("\\.")[0]);
		
		if(Neo4jUtil.isEdgeDuplicate(s, t, type)){
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			return "Edge already exists";
		}
		
		//TODO: add more predicates in the future
		switch(type){
			case "Produced":
				Neo4jUtil.insertEdgeProduced(s, t);
			break;
			case "Used_by":
				Neo4jUtil.insertEdgeUsedBy(s, t);
			break;
			case "Provides":
				Neo4jUtil.insertEdgeProvides(s, t);
			break;
			default:
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "Invalid predicate ";			
		}
		
		response.setStatus(HttpServletResponse.SC_CREATED);
		return "";
	}
	
	/**
	 * Deletes a node
	 * 
	 * @param model
	 *  Model the current model
	 * @return String
	 */
	// TODO:Not sure if we want this for now
	@RequestMapping(value = "/edit/workflow/n/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteNode(@PathVariable(value = "id") String id) {
		Neo4jUtil.deleteNode(id);
		return "Node deleted";
	}
	
	@RequestMapping(value = "/edit/workflow/e/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteEdge(@PathVariable(value = "id") int id) {
		Neo4jUtil.deleteEdge(id);
		return "Node deleted";
	}
	
	@RequestMapping(value = "/edit/workflow/n/{id}/score", method = RequestMethod.GET)
	@ResponseBody
	public int scoreNode(HttpServletResponse response,Model model, @PathVariable(value = "id") int id) {

		String rawJSON = Neo4jUtil.fetchNode(id);
		
		String rawJSON2 = Neo4jUtil.selectNode3(id);
		
		boolean hasUsedBy = false;
		boolean hasProduced = false;
		boolean hasProvides = false;
		float score = 0;
		String message = "";
		try {
			JSONObject obj = new JSONObject(rawJSON);
			JSONObject results = (JSONObject) ((JSONArray) obj.get("results")).get(0);
			JSONObject rows = (JSONObject) ((JSONArray) results.get("data")).get(0);
			JSONArray firstRow = (JSONArray) rows.get("row");
			String type = firstRow.get(1).toString();
			
			JSONObject details = (JSONObject) firstRow.get(0);
			
			JSONObject obj2 = new JSONObject(rawJSON2);
			JSONObject results2 = (JSONObject) ((JSONArray) obj2.get("results")).get(0);
			JSONArray rows2 = (JSONArray) results2.get("data");

			for(int i = 0; i < rows2.length(); i++){
				JSONArray row = (JSONArray) ((JSONObject) rows2.get(i)).get("row");
				String predicate = row.get(3).toString();
				switch(predicate){
					case "Used_by":
						hasUsedBy = true;
					break;
					case "Produced":
						hasProduced = true;
					break;
					case "Provides":
						hasProvides = true;
					break;
				}
			}	
			
			switch(type){
				case "Dataset":	
					//uri, doi, notes, metadata
					if(hasUsedBy || hasProduced){
						score += .6;
					}else{
						message += "<br /> Requires a 'used by' or 'produced' edge";
					}
					
					if(!details.get("uri").toString().trim().equals("")){
						score += .2;
					}else{
						message += "<br /> Requires a URI";
					}
					
					if(!details.get("doi").toString().trim().equals("")){
						score += .1;
					}else{
						message += "<br /> Requires a DOI";
					}
					
					if(!details.get("handle").toString().trim().equals("")){
						score += .1;
					}else{
						message += "<br /> Requires a link to the metadata";
					}
				break;
				case "Program":
					//200 chars enough?
					int noteLength = details.get("notes").toString().trim().length();
					
					if(hasUsedBy){
						score += .3;
					}else{
						message += "<br /> Requires a 'used by' edge";
					}
					
					if(hasProduced){
						score += .3;	
					}else{
						message += "<br /> Requires a 'produced' edge";
					}
					
					if(!details.get("uri").toString().trim().equals("")){
						score += .2;
					}else{
						message += "<br /> Requires a URI";
					}
					
					if(!details.get("author").toString().trim().equals("")){
						score += .1;
					}else{
						message += "<br /> Requires an Author";
					}
					
					score += noteLength/2000.0;
					if(noteLength < 200){
						message += "<br /> Requires a description in the notes field of 200+ characters";
					}
					
				break;
				case "Provider":
					if(hasProvides){ 
						score += .5;
					}else{
						message += "<br /> Requires at least one 'provides' edge";
					}
					
					if(!details.get("uri").toString().trim().equals("")){ 
						score += .5;
					}else{
						message += "<br /> Requires at URI";
					}
					
				break;
			}

		}catch(JSONException e){
			e.printStackTrace();	
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		//model.addAttribute("message", message);
		response.addHeader("message", message);
		return (int) (score * 100);
	}
}