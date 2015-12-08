package edu.ncrn.cornell.ced2ar.web.controllers;

import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;

import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;
 
/**
 *Handles requests relating to variable groups
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Groups {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Loader loader;
	
	/**
	 * Lists all codebooks with links to their variable groups
	 * @param model
	 * @param session	
	 * @return String
	 */
	@RequestMapping(value = "/groups", method = RequestMethod.GET)
	public String groupsBase(Model model, HttpSession session) {
		String baseURI = loader.getPath() + "/rest/";
		try{			
			TreeMap<String,String[]> codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
			model.addAttribute("subTitl", "Groups");
			String[][] crumbs = new String[][] {{"Variable Groups",""}};
			model.addAttribute("crumbs", crumbs);
		}catch(Exception e){
			model.addAttribute("error","Error retrieving data");	
			model.addAttribute("type","error");
			return "/WEB-INF/views/view.jsp";
		}				
		return "/WEB-INF/views/groups.jsp";
	}
	
	/**
	 * Displays all variable groups for a codebook
	 * @param handle
	 * @param model
	 * @param session
	 * @param print
	 * @return String
	 */
	@RequestMapping(value = "/codebooks/{h}/groups", method = RequestMethod.GET)
	public String groups(@PathVariable(value = "h") String handle, Model model, HttpSession session,
	@RequestParam(value = "print", defaultValue = "n") String print) {
		String baseURI = loader.getPath() + "/rest/";
		
		//Temp solution to add trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(!usedURL.endsWith("/")){ 
			return "redirect:/codebooks/"+handle+"/groups/";
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
		
		String baseHandle = codebooks.get(handle)[0];
		String version = codebooks.get(handle)[1];
		
		String apiURI = baseURI;
		if(print.equals("y"))
			model.addAttribute("print", true);
		try{
			apiURI += "codebooks/"+handle+"/vargroups";
			String xml = Fetch.getShortXML(apiURI)[0];
			String path = context.getRealPath("/xsl/groups.xsl");//Local file path to find XSL doc
			Parser xp = new Parser(xml, path);
			model.addAttribute("results", xp.getData());
			
			String codebookName = xp.getValue("/codeBook//titl");
			String codebookURL = "codebooks/"+handle+"/";
			//Gets baseHandle and version seperated out if possible
			if(model.containsAttribute("codebookURL")){
				codebookURL = (String) model.asMap().get("codebookURL");
			}
			
			String[][] crumbs = new String[][] {{codebookName,codebookURL},{"Variable Groups",""}};
			
			model.addAttribute("type", "group");
			model.addAttribute("baseHandle", baseHandle);
			model.addAttribute("version", version);
			model.addAttribute("crumbs", crumbs);
			model.addAttribute("subTitl", "Groups in " + codebookName);
			model.addAttribute("metaDscr", " - Groups for the " + codebookName + " codebook");
		}catch(NullPointerException | NumberFormatException e){		
			model.addAttribute("error","Error retrieving data");	
			model.addAttribute("type","error");
			return "/WEB-INF/views/view.jsp";
		}
		return "/WEB-INF/views/details.jsp";
	}
	
	/**
	 * Returns variable groups in codebook if version is specified
	 * @param handle
	 * @param print
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/codebooks/{h}/v/{v}/groups", method = RequestMethod.GET)
	public String groupsVersion(
	@PathVariable(value = "h") String  baseHandle, 
	@PathVariable(value = "v") String  version, 
	@RequestParam(value = "print", defaultValue = "n") String print,
	Model model, HttpSession session){
		//Temp solution to add trailing slash
		 String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		 if(!usedURL.endsWith("/")){ 
			 return "redirect:/codebooks/"+baseHandle+"/v/"+version+"/groups/";
		 }
		model.addAttribute("codebookURL", "codebooks/"+baseHandle+"/v/"+version+"/");
		String handle = baseHandle + version;
		return groups(handle,model,session,print);
	}
	
	/**
	 * Shows a specific variable group
	 * @param groupID - id of group
	 * @param handle - codebook handle
	 * @param print - if ==y , display as printed page
	 * @param model - spring model
	
	 * @param session HttpSession
	 * @return String
	 */
	@RequestMapping(value = "/codebooks/{h}/groups/{id}", method = RequestMethod.GET)
	public String group(@PathVariable(value = "id") String groupID,
		@PathVariable(value = "h") String  handle, 
		@RequestParam(value = "print",defaultValue = "n") 
		String print, Model model, HttpSession session){
		
			//Temp solution to add trailing slash
			String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
			if(!usedURL.endsWith("/")){ 
				return "redirect:/codebooks/"+handle+"/groups/"+groupID+"/";
			}
			
			String baseURI = loader.getPath()+ "/rest/";
			
			TreeMap<String,String[]> codebooks = null;
			if(session.getAttribute("codebooks") == null){
				codebooks = loader.getCodebooks(baseURI);
				session.setAttribute("codebooks", codebooks);
			}else{
				codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
			}
			
			String baseHandle = codebooks.get(handle)[0];
			String version = codebooks.get(handle)[1];
		
			if(!loader.hasCodebook(handle)){
				session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
				return "redirect:/";
			}
			
			String apiURI = baseURI;
			if(print.equals("y")){
				model.addAttribute("print", true);
			}
			try{
				apiURI += "codebooks/"+handle+"/vargroups/"+groupID; 
				String xml = Fetch.getXML(apiURI)[0];
				String path = context.getRealPath("/xsl/group.xsl");//Local file path to find XSL doc
				Parser xp = new Parser(xml, path);
				
				String codebookName = xp.getValue("/codeBook//titl");
				String codebookURL = "codebooks/"+handle+"/";
				//Gets baseHandle and version seperated out if possible
				if(model.containsAttribute("codebookURL")){
					codebookURL = (String) model.asMap().get("codebookURL");
				}
				
				String codebookURLG = codebookURL + "groups/";
				String groupName = xp.getAttrValue("/codeBook/dataDscr/varGrp", "name");
				
				String[][] crumbs = new String[][] {{codebookName,codebookURL},{"Variable Groups",codebookURLG},{groupName,""}};
				
				model.addAttribute("type", "groupD");
				model.addAttribute("baseHandle", baseHandle);
				model.addAttribute("version", version);
				model.addAttribute("groupID", groupID);
				model.addAttribute("crumbs", crumbs);
				model.addAttribute("results", xp.getData());
				model.addAttribute("subTitl", groupName + " Group (" + handle.toUpperCase() + ")");
				model.addAttribute("metaDesc","Contains details about the " + groupName + " group, in the "+handle+ " codebook");
			}catch(NullPointerException | NumberFormatException e){
				model.addAttribute("error","Error retrieving data");	
				model.addAttribute("type","error");
				return "/WEB-INF/views/view.jsp";
			}
			return "/WEB-INF/views/details.jsp";
	}	
	
	/**
	 * Shows a specific group when a codebook version is specified
	 * @param groupID
	 * @param handle
	 * @param print
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/codebooks/{h}/v/{v}/groups/{id}", method = RequestMethod.GET)
	public String groupVersion(
	@PathVariable(value = "id") String groupID,
	@PathVariable(value = "h") String  baseHandle, 
	@PathVariable(value = "v") String  version, 
	@RequestParam(value = "print",defaultValue = "n") 
	String print, Model model , HttpSession session) {
		//Temp solution to add trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(!usedURL.endsWith("/")){ 
			return "redirect:/codebooks/"+baseHandle+"/v/"+version+"/groups/"+groupID+"/";
		}
		
		String handle =baseHandle+version;
		model.addAttribute("codebookURL", "codebooks/"+baseHandle+"/v/"+version+"/");
		return group(groupID, handle, print, model, session);
	}	
}