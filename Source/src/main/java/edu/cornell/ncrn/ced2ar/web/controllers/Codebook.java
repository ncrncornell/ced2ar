package edu.cornell.ncrn.ced2ar.web.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.api.data.Fetch;
import edu.cornell.ncrn.ced2ar.api.rest.queries.CodebookData;
import edu.cornell.ncrn.ced2ar.web.classes.Loader;
import edu.cornell.ncrn.ced2ar.web.classes.Parser;
 
/**
 *Handle all requests related to codebooks and variables
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Codebook {
	public static final String EXPORT_STAT_PACKAGE_SAS = "SAS";
	public static final String EXPORT_STAT_PACKAGE_STATA = "STATA";
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Config config;
	
	@Autowired
	private Loader loader;
	
	private static final Logger logger = Logger.getLogger(Codebook.class);
	
//Utilties
	
	/**
	 *Retrieves a specific title page and caches it
	 * @param handle String the codebook to retrieve
	 * @return String the retrieved codebook
	 */
	//TODO: Remember to clear cache when updating
	@Cacheable( value="codebook", key="handle")
	public String getTitlePage(String handle){		
		//String baseURI = loader.getPath() + "/rest/";
		//String apiURI = baseURI + "codebooks/"+ handle + "/titlepage";		
		//logger.debug("fetching title page for " + apiURI);		
		CodebookData codebookData = new CodebookData();
		String xml = codebookData.getTitlePage(handle, "xml","namespaces");
		
		//Temp fix for white space chopping issues with BaseX
		xml = xml.replaceAll("(\\S)(<ExtLink)", "$1 <ExtLink");
		xml = xml.replaceAll("(</ExtLink>)(\\S)", "</ExtLink> $2");
		return xml;
	} 
	
//Endpoints
	/**
	 *Shows a page which lists all codebooks
	 * @param model Model the current model
	 * @return String the codebooks jsp 
	 */
	@RequestMapping(value = "/codebooks", method = RequestMethod.GET)
	public String showCodebooks(Model model){
		model.addAttribute("subTitl","All Codebooks");
		String baseURI = loader.getPath() + "/rest/";
		try{			
			TreeMap<String,String[]>  codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}catch(Exception e){
			model.addAttribute("error","Error retrieving data");	
			model.addAttribute("type","error");				
			return "/WEB-INF/views/view.jsp";
		}	
		return "/WEB-INF/views/codebooks.jsp";	
	}

	/**
	 * Displays the titlepage for a codebook
	 * @param handle the codebook name
	 * @param print the print option
	 * @param model	the current model
	 * @return String codebook jsp
	 */
	@RequestMapping(value = "/codebooks/{c}", method = RequestMethod.GET)
	public String showCodebook(@PathVariable(value = "c") String handle, //codebookname
		@RequestParam(value = "print", defaultValue = "n") String print,//print option		
		Model model, HttpServletResponse response) {	
			if(!loader.hasCodebook(handle)){
				String latest = loader.fetchDefault(handle);
				if(!latest.equals("")){
					logger.debug("Found latest version of handle "+handle+" version "+latest);
					String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
					//TODO: Find more elegant solution. Trailing slash causes circular view path exception and results in 302
					if(usedURL.endsWith("/")){
						return "redirect:/codebooks/"+handle+"/v/"+latest;
					}else{
						//needs absolute url
						String reURL = context.getContextPath() +"/codebooks/"+handle+"/v/"+latest;
						response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
						response.setHeader("Location", reURL);
						logger.debug("Redirecting to url " + reURL + "\n");
						return "";
					}
				}
				session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
				return "redirect:/";
			}	
			try{
				 model.addAttribute("hasMath", true);
				 String xml = getTitlePage(handle);
				 
				 //Adds download pdf link
				 String baseURI = loader.getPath() + "/rest/";
				 String[] indexInfo = loader.getCodebooks(baseURI).get(handle);
				 String docTestURI = baseURI +"codebooks/"+handle+"/haspdf";
				 String hasDoc = Fetch.get(docTestURI).trim();	
				 if(hasDoc.equals("1")){
					 model.addAttribute("pdf", loader.getBuildName() + "/pdf/"+handle+".pdf");
				 } 
				 	 
				 String path = context.getRealPath("/xsl/codebook.xsl");//Local file path to find XSL doc
				 
				 //TODO: Shouldn't need anymore
				 /*
				 xml = xml.replaceFirst("<codeBook", "<codeBook  "+ 
                 " xmlns:dc=\"http://purl.org/dc/terms/\""+
                 " xmlns:ex=\"http://example.com/ns/ex#\""+
                 " xmlns:prov=\"http://www.w3.org/ns/prov#\""+
                 " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""+
                 " xmlns:tr=\"http://example.com/ns/tr#\""+
                 " xmlns:xhtml=\"http://www.w3.org/1999/xhtml\""+
                 " xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\""+
                 " xmlns:saxon=\"http://xml.apache.org/xslt\""+
                 " xmlns:ced2ar=\"http://ced2ar.org/ns/core#\""+
                 " xmlns:file=\"http://ced2ar.org/ns/file#\""+
                 " xmlns:type=\"http://ced2ar.org/ns/type#\""+
                 " xmlns:RePEc=\"https://ideas.repec.org/#\""+
                 " xmlns:repeca=\"https://ideas.repec.org/e/#\""+
                 " xmlns:ns0=\"http://purl.org/dc/elements/1.1/\""+
                 " xmlns:exn=\"http://ced2ar.org/ns/external#\""+
                 " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""+
                 " xmlns:act=\"http://ced2ar.org/ns/activities#\""+
                 " xmlns:fn=\"http://www.w3.org/2005/xpath-functions\""+
                 " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
				 */
				 
				 Parser xp = new Parser(xml, path, 0);
				 String count = xp.getAttrValue("/codeBook", "variables");
				 if(count != null){
					 
					 if(indexInfo[3].equals("deprecated")){
						 String latest = loader.fetchDefault(indexInfo[0]);
						 model.addAttribute("newVersion",latest);
					 }
					 model.addAttribute("baseHandle",indexInfo[0]);
					 
					 String title = xp.getValue("/codeBook/docDscr/citation/titlStmt/titl");

					 //Crowdsourcing
					 model.addAttribute("crowdsourceSwitch",Config.getInstance().getCrowdSourcingRole());
					 String remoteURL = Config.getInstance().getRemoteURL() 
					 + "/codebooks/"+indexInfo[0]+"/v/"+indexInfo[1];
					 model.addAttribute("remoteServerURL",remoteURL);
					 
					 model.addAttribute("handle", handle);
					 model.addAttribute("codebook", xp.getData());
					 model.addAttribute("codebookUse",indexInfo[2]);
					 model.addAttribute("count", count);
					 model.addAttribute("codebookTitl",title);
					 model.addAttribute("subTitl",title);
					 model.addAttribute("metaDesc","Cover page for the " + title + " codebook");
					 model.addAttribute("metaKeywords",","+title);	 
					 String[][] crumbs = new String[][] {{title,""}};
					 model.addAttribute("crumbs", crumbs);
					 
					 //Add print attr if needed
					 if(print.equals("y")){
						DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
						Date dateStamp = new Date();
						String timeStamp = dateFormat.format(dateStamp);
						model.addAttribute("timeStamp", timeStamp);
						model.addAttribute("print", true);
					 }	
					 return "/WEB-INF/views/codebook.jsp";
				 }
			 }catch(NullPointerException | NumberFormatException e){		
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				model.addAttribute("error","Error retrieving data");	
				model.addAttribute("type","error");
				logger.error(e.getMessage());
				return "redirect:/";
			}
		 return "redirect:/";
	}
	
	/**
	 * Redirects to codebook function when version is specified
	 * @param model current model
	 * @param baseHandle base handle of the codebook
	 * @param version version of the codebook
	 * @param print print option
	 * @return the codebook jsp
	 */
	@RequestMapping(value = "/codebooks/{c}/v/{v}", method = RequestMethod.GET)
	public String showCodebookV(Model model, @PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version, @RequestParam(value = "print", defaultValue = "n") String print, HttpServletResponse response){
		 model.addAttribute("baseHandle", baseHandle);
		 model.addAttribute("version", version);
		 
		 String handle = baseHandle+version;	
		 return showCodebook(handle,print,model, response);
	}
	
	@RequestMapping(value = "/codebooks/{c}/v/{v}/versions", method = RequestMethod.GET)
	public String showCodebookGit(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, Model model){
		if(!config.isGitEnabled()){
			return "redirected:/codebooks/"+baseHandle+"/v/"+version;
		}
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle+version;	
		TreeMap<String,String[]> codebooks = loader.getCodebooks(baseURI);
		String title = codebooks.get(handle)[4];
		String codebookURL = "codebooks/"+baseHandle+"/v/"+version+"/";	 
		String[][] crumbs = new String[][] {{title,codebookURL},{"Versions",""}};
		
		model.addAttribute("crumbs",crumbs);
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version", version);
		model.addAttribute("title", title);
		model.addAttribute("subTitl","Commits - "+ handle.toUpperCase());
		 
		String data = Fetch.get(baseURI+"codebooks/"+handle+"/versions");
		List<String[]> versions= new ArrayList<String[]>(); 
		
		if(data != null && !data.equals("")){
			String[] commits = data.split(";");
			for(String commit : commits){
				String[] commitData = commit.split(",");
				Long epoch = Long.valueOf(commitData[1].trim()).longValue() * 1000;
				SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
				String timeStamp = format.format(epoch);
				versions.add(new String[]{commitData[0], timeStamp, commitData[2], commitData[3]});
			}
			model.addAttribute("gitURL",config.getRemoteRepoURL());
			model.addAttribute("versions", versions);	
		}
	
		return "/WEB-INF/views/versions.jsp";
	}
	
	@RequestMapping(value = "/codebooks/{c}/v/{v}/versions2", method = RequestMethod.GET)
	public String showCodebookGit2(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, Model model){
		if(!config.isGitEnabled()){
			return "redirected:/codebooks/"+baseHandle+"/v/"+version;
		}
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle+version;	
		TreeMap<String,String[]> codebooks = loader.getCodebooks(baseURI);
		String title = codebooks.get(handle)[4];
		String codebookURL = "codebooks/"+baseHandle+"/v/"+version+"/";	 
		String[][] crumbs = new String[][] {{title,codebookURL},{"Variable Versions",""}};
		
		model.addAttribute("crumbs",crumbs);
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version", version);
		model.addAttribute("title", title);
		model.addAttribute("subTitl","Commits - "+ handle.toUpperCase());
		 
		try{
			String data = Fetch.get(baseURI+"codebooks/"+handle+"/versions?type=vars").trim();
			List<String[]> versions= new ArrayList<String[]>(); 
			if(data != null && !data.equals("")){
				String[] commits = data.trim().split(";");
				for(String commit : commits){
					commit = commit.trim();
					String[] commitData = commit.split(",");	
					Long epoch = Long.valueOf(commitData[2].trim()).longValue() * 1000;
					SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
					String timeStamp = format.format(epoch);
					versions.add(new String[]{commitData[0].trim(), commitData[1].trim(), timeStamp, commitData[3].trim()});
				}
				model.addAttribute("gitURL",config.getRemoteRepoURL());
				model.addAttribute("versions", versions);	
				model.addAttribute("baseHandle", baseHandle);	
				model.addAttribute("codebookVersion", version);				
			}
		}catch(NullPointerException e){
			//No commits
			model.addAttribute("gitURL",config.getRemoteRepoURL());
			model.addAttribute("baseHandle", baseHandle);	
			model.addAttribute("codebookVersion", version);
			
		}
		return "/WEB-INF/views/versions2.jsp";
	}

	/**
	 * Redirects to show all vars in codebook when URL is manipulated
	 * @param model current model
	 * @param handle the name of the codebook
	 * @return String page displaying all vars in the codebook
	 */
	@RequestMapping(value = "/codebooks/{c}/vars", method = RequestMethod.GET)
	public String showVarsInCodebook(Model model, @PathVariable(value = "c") String handle){
		 return "redirect:/landing?c="+handle;
	}
	
	/**
	 * *Redirects showVarsInCodebook to version number for codebook is specified
	 * @param model current model
	 * @param baseHandle base name of the codebook
	 * @param version version of the codebook
	 * @param print print option
	 * @return page displaying all vars in the codebook
	 */
	@RequestMapping(value = "/codebooks/{c}/v/{v}/vars", method = RequestMethod.GET)
	public String showVarsInCodebookV(Model model, @PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version, @RequestParam(value = "print", defaultValue = "n") String print){
		 String handle = baseHandle+version;	
		 return showVarsInCodebook(model,handle);
	}
	
	/**
	 * Displays information about all variables in the codebook.
	 * This URL is now unpublished. But, used in pdf generation process.
	 * @param handle codebook handle - base handle plus version ie, ssb6
	 * @param model current model
	 * @return page displaying all variables in the specified codebook
	 */
	@RequestMapping(value = "/codebooks/{c}/allvars", method = RequestMethod.GET)
	public String showAllVariables(@PathVariable(value = "c") String handle, Model model, HttpServletResponse response) {
		String apiURI = loader.getPath() + "/rest/codebooks/"+ handle + "/variables";
		try{		
			String xml = Fetch.getShortXML(apiURI)[0];
			xml = xml.replace("&nbsp;", "");
			String path = context.getRealPath("/xsl/variables.xsl");//Local file path to find XSL doc
			Parser xp = new Parser(xml, path, 0);
			model.addAttribute("results", xp.getData());		
			model.addAttribute("print",true);
			model.addAttribute("subTitl",handle.toUpperCase() + " Variable List");
		 }catch(NullPointerException | NumberFormatException e){
			logger.warn(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			model.addAttribute("error","Error retrieving data");	
			model.addAttribute("type","error");			
			return "/WEB-INF/views/view.jsp";
		 }
		 return "/WEB-INF/views/details.jsp"; 
	}
	
	/**
	 * Redirects to showAllVariables
	 * @param handle
	 * @param model
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/codebooks/{c}/v/{v}/allvars", method = RequestMethod.GET)
	public String showAllVariablesV(Model model, HttpServletResponse response,
	@PathVariable(value = "c") String baseHandle, @PathVariable(value = "v") String version) {
		String handle = baseHandle + version;
		return showAllVariables(handle,model,response);
	}
	
	/**
	 * Displays a specific variable
	 * @param variable variable name
	 * @param handle codebook handle - basehandle plus version ie, ssb6
	 * @param print whether or not to show print mode either null or y
	 * @param batchSession whether or not to expire session early for PDF generator
	 * @param model current model
	 * @return page displaying a specific variable
	 */
	@RequestMapping(value = "/codebooks/{c}/vars/{n}", method = RequestMethod.GET)
	public String showVariable(@PathVariable(value = "n") String variable,//variable name
	@PathVariable(value = "c") String handle, 
	@RequestParam(value = "print", defaultValue = "n") String print,//print option
	Model model, HttpServletResponse response) {
		//Temp solution to remove trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(usedURL.endsWith("/")){ 
			return "redirect:/codebooks/"+handle+"/vars/"+variable;
		}
		
		if(!loader.hasCodebook(handle)){
			String latest = loader.fetchDefault(handle);
			if(!latest.equals("")){	
				logger.debug("Found latest version of handle "+handle+" version "+latest);
				return "redirect:/codebooks/"+handle+"/v/"+latest+"/vars/"+variable;
			}else{
				session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
				return "redirect:/";
			}
		}
		
		String apiURI = loader.getPath() + "/rest/codebooks/"+ handle + "/variables/"+variable;
		try{
			 String baseURI = loader.getPath() + "/rest/";
			 String[] indexInfo = loader.getCodebooks(baseURI).get(handle);
			 String xml = Fetch.getShortXML(apiURI)[0];
	 
			 if(!Parser.containsNode(xml,"//var")){
				 session.setAttribute("error","Variable does not exist");		
				 model.addAttribute("type","error");
				 return "redirect:/all";
			 }
			 String path = context.getRealPath("/xsl/variable.xsl");//Local file path to find XSL doc
			 Parser xp = new Parser(xml, path, 0);
			 
			 //Add print attr if needed
			 if(print.equals("y")){
				 model.addAttribute("print", true);
			 }	
			 
			 String codebookName = xp.getValue("/codeBook//titl");
			 String baseHandle = indexInfo[0];
			 String version = indexInfo[1];
			 String codebookURL = "codebooks/"+baseHandle+"/v/"+version+"/";	 
			 
			 String[][] crumbs = new String[][] {{codebookName,codebookURL},{variable,""}};
			 String formatedVarName = variable.substring(0,1).toUpperCase() + variable.substring(1).toLowerCase();
			 
			 String descPreview = xp.getValue("/codeBook/var/txt");
			 if(descPreview != null && descPreview.length() >= 250 ){
				 descPreview = descPreview.substring(0,249) + "...";
			 }
			 
			 if(indexInfo[3].equals("deprecated")){
				 String latest = loader.fetchDefault(indexInfo[0]);
				 model.addAttribute("newVersion",latest);
			 }
			 
			 //Crowdsourcing
			 model.addAttribute("crowdsourceSwitch",Config.getInstance().getCrowdSourcingRole());
			 String remoteURL = Config.getInstance().getRemoteURL() 
					 + "/codebooks/"+indexInfo[0]+"/v/"+version+"/vars/"+variable;
			 model.addAttribute("remoteServerURL",remoteURL);
			 model.addAttribute("crowdsourceCompareURL",loader.getBuildName()+"/codebooks/"
					 +indexInfo[0]+"/v/"+version+"/vars/"+variable+"/diff");
			 			 
			 model.addAttribute("baseHandle",indexInfo[0]);
			 model.addAttribute("version",version);
			 model.addAttribute("hasMath", true);
			 model.addAttribute("crumbs", crumbs);
			 model.addAttribute("results", xp.getData());		
			 model.addAttribute("type", "var");			 
			 model.addAttribute("var", variable);		
			 model.addAttribute("subTitl",formatedVarName + " ("+handle.toUpperCase()+")");
			 model.addAttribute("metaDesc",descPreview);
			 model.addAttribute("metaKeywords",","+formatedVarName+","+codebookName);

		 }catch(NullPointerException | NumberFormatException e){
			logger.warn(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			session.setAttribute("error","Error retrieving data");		
			model.addAttribute("type","error");
			return "/WEB-INF/views/view.jsp";
		 }
		 return "/WEB-INF/views/details.jsp"; 
	}

	/**
	 * Show a variable within a specific version of a codebook
	 * @param variable the variable to display
	 * @param baseHandle name of the codebook holding the variable
	 * @param version desired version of the codebook
	 * @param print  print option
	 * @param model current model
	 * @return page displaying specific variable
	 */
	@RequestMapping(value = "/codebooks/{c}/v/{v}/vars/{n}", method = RequestMethod.GET)
	public String showVariableV(
	@PathVariable(value = "n") String variable,//variable name
	@PathVariable(value = "c") String baseHandle,//base handle
	@PathVariable(value = "v") String version,//version number 
	@RequestParam(value = "print", defaultValue = "n") String print,//print option
	Model model, HttpServletResponse response){
		//Temp solution to remove trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(usedURL.endsWith("/")){ 
			return "redirect:/codebooks/"+baseHandle+"/v/"+version+"/vars/"+variable;
		}

		String handle = baseHandle+version;	
		return showVariable(variable,handle,print,model,response);
	}
	
	//TODO:Var version not working for user
	@RequestMapping(value = "/codebooks/{c}/v/{v}/vars/{n}/versions", method = RequestMethod.GET)
	public String showVarGit(Model model, @PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version, @PathVariable(value = "n") String var){
		if(!config.isGitEnabled()){
			return "redirected:/codebooks/"+baseHandle+"/v/"+version;
		}
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle+version;	
		TreeMap<String,String[]> codebooks = loader.getCodebooks(baseURI);
		String title = codebooks.get(handle)[4];
		String codebookURL = "codebooks/"+baseHandle+"/v/"+version+"/";	 
		String varURL = "codebooks/"+baseHandle+"/v/"+version+"/vars/"+var;	 ;
		String[][] crumbs = new String[][] {{title,codebookURL},{var,varURL},{"Versions",""}};

		model.addAttribute("crumbs",crumbs);
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version", version);
		model.addAttribute("title", title);
		model.addAttribute("type","var");
		model.addAttribute("var", var);
		model.addAttribute("subTitl","Commits - "+ var + " ("+handle.toUpperCase()+")");
		
		String data = Fetch.get(baseURI+"codebooks/"+handle+"/variables/"+var+"/versions");
		List<String[]> versions= new ArrayList<String[]>(); 
		if(data != null && !data.equals("")){
			String[] commits = data.split(" ");
			for(String commit : commits){
				String[] commitData = commit.split("\\.");
				Long epoch = Long.valueOf(commitData[1].trim()).longValue() * 1000;
				SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
				String timeStamp = format.format(epoch);
				versions.add(new String[]{commitData[0], timeStamp, commitData[3]});
			}
			model.addAttribute("gitURL",config.getRemoteRepoURL());
			model.addAttribute("versions", versions);	
		}
		return "/WEB-INF/views/varVersions.jsp";
	}

//AJAX Endpoints

	/**
	 * Changes what codebooks to filter by
	 * @param c String[] array of specific codebook versions, and whether or not they're filtered
	 * @param ct String[] array of base handles for codebooks, and whether or not they're filtered
	 * @param v String[] array of base handles that have been toggle open in the UI
	 * @param redirect String location to redirect after loading
	 * @param versionUpdate String if equal to 1, a version of a codebook where checked/unchecked
	 * @param baseUpdate String if equal to 1, base hande of codebooks where checked/unchecked
	 * @param toggleUpdate String if equal to 1, toggling was done
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/filterCodebook", method = RequestMethod.GET)
    public String filter(@RequestParam(value = "cb", defaultValue = "") String[] c,
    @RequestParam(value = "ctb", defaultValue = "") String[] ct,
    @RequestParam(value = "vs", defaultValue = "") String[] v, 
    @RequestParam(value = "r", defaultValue = "") String redirect,
    @RequestParam(value = "updateV", defaultValue = "") String versionUpdate,
    @RequestParam(value = "updateB", defaultValue = "") String baseUpdate,
    @RequestParam(value = "updateT", defaultValue = "") String toggleUpdate,
    HttpServletRequest  request,HttpServletResponse response, Model model) {
		response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
        response.setHeader("Pragma","no-cache");
        
		String baseURI = loader.getPath() + "/rest/";
		TreeMap<String, String[]> cList = loader.getCodebooks(baseURI);
		
		int filterSize  = 0;
		try{
			@SuppressWarnings("unchecked")
			ArrayList<String[]> temp = (ArrayList<String[]>) session.getAttribute("filter");
			if(temp == null) filterSize = 0;
			else filterSize = temp.size();
		}catch(ClassCastException|NullPointerException e){}
		
		//Save the toggle state of a filter
		if(!StringUtils.join(v).equals("") || toggleUpdate.equals("1")){
			ArrayList<String> vNew = new ArrayList<String>(Arrays.asList(v));
	      	session.setAttribute("filterShow",vNew);
		}

		if(versionUpdate.equals("1") || baseUpdate.equals("1") || filterSize == 0){	
			logger.debug("Fetching new filters...");		
			session.removeAttribute("filter");//Resets previous filters			
			ArrayList<String[]> filtered = new ArrayList<String[]>();
			ArrayList<String> cNew = new ArrayList<String>(Arrays.asList(c));
			ArrayList<String> cHeaders = new ArrayList<String>(Arrays.asList(ct));
			String lastBaseHandle = "";
			boolean allSelected = true;
			for(String cb[] : cList.values()){	
				String baseHandle=cb[0].replaceAll("\\s+", "");	
				String handle = baseHandle+cb[1];
				String selected  = cNew.contains(handle) ? "true" : "false";
				
				//Individual version of codebook is being checked/uncheck
				if(versionUpdate.equals("1") ){
					//If Basehandle checked, and version is removed, uncheck basehandle
					if(selected.equals("false") && cHeaders.contains(baseHandle)){
						cHeaders.remove(baseHandle);
					}
					if(!lastBaseHandle.equals(baseHandle)){
						if(allSelected){
							cHeaders.add(lastBaseHandle);
						}
						lastBaseHandle = baseHandle;
						allSelected = true;
					}
				}
				//Basehandle for multiple codebooks are being checked/uncheck
				else if(baseUpdate.equals("1")){
					//Basehandle checked, selected all versions
					if(cHeaders.contains(baseHandle)){
						selected="true";	
					}
					//Basehandle unchecked, remove all versions
					else if(selected.equals("true") && !cHeaders.contains(baseHandle)){	
						selected="false";
					}
				}
				
				if(!selected.equals("true"))
					allSelected = false;
				
				filtered.add(new String[] {cb[0]+cb[1],cb[0],cb[1],cb[2],cb[4],selected});					
			}	
			
			//For last baseHandle
			if(allSelected){
				cHeaders.add(lastBaseHandle);
			}
			
			session.setAttribute("filter", filtered);		
			session.setAttribute("filterHeader", cHeaders);	
		}
		
		if(!redirect.equals("")){
            return "redirect:"+redirect;
		}
		return "/WEB-INF/ajaxViews/filterCodebook.jsp";
    } 
	
   /**
    *Retrieves string that show what codebooks are being searched near search bar
    *@return String
    */
    @RequestMapping(value = "/filterVerbose", method = RequestMethod.GET)
    public String verboseFilter() {
        return "/WEB-INF/ajaxViews/verboseFilter.jsp";        
    }       

//Stats export
    
    /**
     * 
     * @param codebookId Codebook Handle
     * @param versionId Version Id
     * @param response	
     * @return String Representing SAS code for all variable values in the codebook
     */
    @RequestMapping(value = "/codebooks/{codebookId}/v/{versionId}/vars/exportToSAS", method = RequestMethod.GET, produces="text/plain")
    @ResponseBody
	public String exportVariablesSAS(@PathVariable(value = "codebookId") String codebookId, @PathVariable(value = "versionId") String versionId,HttpServletResponse response) {
    	String data = getAllValuesExportCode(codebookId,versionId,EXPORT_STAT_PACKAGE_SAS);

    	String fileName = codebookId+versionId+".sas";
		response.setContentType("application/txt");
		response.setHeader("Content-Disposition","attachment; filename="+fileName);
		return data;
    }

    /**
     * @param codebookId Codebook Handle
     * @param versionId version Id
     * @param response
     * @return String Representing STATA code for all variable values in the codebook
     */
    @RequestMapping(value = "/codebooks/{codebookId}/v/{versionId}/vars/exportToSTATA", method = RequestMethod.GET, produces="text/plain")
    @ResponseBody
	public String exportVariablesSTATA(@PathVariable(value = "codebookId") String codebookId,@PathVariable(value = "versionId") String versionId,HttpServletResponse response) {
    	String data = getAllValuesExportCode(codebookId,versionId,EXPORT_STAT_PACKAGE_STATA);
    	String fileName = codebookId+versionId+".do";
		response.setContentType("application/txt");
		response.setHeader("Content-Disposition","attachment; filename="+fileName);
		return data;
    }

    /**
     * @param codebookId Codebook Handle
     * @param variableId variable id
     * @param versionId	version id
     * @param response	
     * @return  String Representing SAS  code for variable values in the codebook	
     */
    @RequestMapping(value = "/codebooks/{codebookId}/v/{versionId}/vars/{variableId}/exportToSAS", method = RequestMethod.GET, produces="text/plain")
    @ResponseBody
	public String exportVariableSAS(@PathVariable(value = "codebookId") String codebookId,@PathVariable(value = "variableId") String variableId,@PathVariable(value = "versionId") String versionId,HttpServletResponse response){
		String data = getVariableValuesExportCode(codebookId,versionId,variableId, EXPORT_STAT_PACKAGE_SAS);
		String fileName = codebookId+versionId+"_"+variableId+".sas";
		response.setContentType("application/txt");
		response.setHeader("Content-Disposition","attachment; filename="+fileName);
		return data;
    }
    
    /**
     * @param codebookId CodebookId Handle
     * @param variableId 	
     * @param versionId Version of the codebook
     * @param response	
     * @return String Representing STATA  code for variable values in the codebook
     */
	@RequestMapping(value = "/codebooks/{codebookId}/v/{versionId}/vars/{variableId}/exportToSTATA", method = RequestMethod.GET, produces="text/plain")
	@ResponseBody
	public String exportVariableSTATA(@PathVariable(value = "codebookId") String codebookId,@PathVariable(value = "variableId") String variableId,@PathVariable(value = "versionId") String versionId,HttpServletResponse response) {
		String data = getVariableValuesExportCode(codebookId,versionId,variableId, EXPORT_STAT_PACKAGE_STATA);
		String fileName = codebookId+versionId+"_"+variableId+".do";
		response.setContentType("application/txt");
		response.setHeader("Content-Disposition","attachment; filename="+fileName);
		return data;
	}
	
	/**
	 * @param codebookId Codebook Handle
	 * @param versionId	Codebook Version Id
	 * @param variableId Variable Id
	 * @param statisticalPackage	SAS or STATA
	 * @return String Representing STATA or SAS code for variable value in the codebook
	 */
	private String getVariableValuesExportCode(String codebookId, String versionId, String variableId, String statisticalPackage) {
		String data = "";
		String apiURI = loader.getPath()+ "/rest/codebooks/"+codebookId+versionId+"/variables/"+variableId;
		String xml = Fetch.getShortXML(apiURI)[0];
				
		if(statisticalPackage.equalsIgnoreCase("STATA")) {
			String path = context.getRealPath("/xsl/stataVariable.xsl");
			Parser xp = new Parser(xml, path, 0);
			data  = xp.getData();
		}
		else if(statisticalPackage.equalsIgnoreCase("SAS")) {
			String path = context.getRealPath("/xsl/sasVariable.xsl");
			Parser xp = new Parser(xml, path, 0);
			data  = xp.getData();
		}
		return data;		
	}

	/**
	 * @param codebookId codebook handle
	 * @param versionId	VersionId of the codebook
	 * @param statisticalPackage SAS or STATA
	 * @return String Representing STATA or SAS code for all variable values in the codebook 
	 */
	private String getAllValuesExportCode(String codebookId, String versionId,  String statisticalPackage)  {
		String apiURI 	= loader.getPath()+ "/rest/codebooks/"+codebookId+versionId + "?type=noNamespaces";
		String codebook	= Fetch.getXML(apiURI)[0];
		String data = "";

		if(statisticalPackage.equalsIgnoreCase(EXPORT_STAT_PACKAGE_STATA)) {
			String path = context.getRealPath("/xsl/stataVariables.xsl");
			Parser xp = new Parser(codebook, path, 0);
			data  = xp.getData();
		}
		else if(statisticalPackage.equalsIgnoreCase(EXPORT_STAT_PACKAGE_SAS)) {
			String path = context.getRealPath("/xsl/sasVariables.xsl");
			Parser xp = new Parser(codebook, path, 0);
			data  = xp.getData();
		}
		return data;
	}


	/**
	 * Returns a page listing all codebooks and the studies within them.
	 * @param model Model the current model
	 * @return String the studies jsp 
	 */
	@RequestMapping(value = "/codebooks/studies", method = RequestMethod.GET)
	public String showStudies(Model model){
		model.addAttribute("subTitl","All Codebook Studies");
		String baseURI = loader.getPath() + "/rest/";
		try{			
			TreeMap<String,String[]>  studies = loader.getStudies(baseURI);
			session.setAttribute("studies", studies);
		}catch(Exception e){
			model.addAttribute("error","Error retrieving data");	
			model.addAttribute("type","error");				
			return "/WEB-INF/views/view.jsp";
		}	
		return "/WEB-INF/views/studies.jsp";	
	}

	/**
	 * Returns a page displaying the contents of a codebook in a horizontal tabbed layout.  
	 * The tabs are the complex elements in the DDI codeBookType (/docDscr, /stdyDscr, /fileDscr, /dataDscr, /otherMat).
	 * The Study tab is active/displayed because the user selected a Study from the list on the previous page 
	 * (instead of the usual docDscr...titl). 
	 *   
	 * This method is based on the showCodebook and showCodebookV methods.  I tried to keep the code as close as 
	 * possible to both methods.
	 * 
	 * @param model current model
	 * @param baseHandle base handle of the codebook
	 * @param version version of the codebook
	 * @param print print option
	 * @return the codebook tabbed study jsp
	 */
	@RequestMapping(value = "/codebooks/{c}/v/{v}/study", method = RequestMethod.GET)
	public String showStudyV(Model model, @PathVariable(value = "c") String baseHandle,
		@PathVariable(value = "v") String version, 
		@RequestParam(value = "print", defaultValue = "n") String print, 
		HttpServletResponse response){
		 model.addAttribute("baseHandle", baseHandle);
		 model.addAttribute("version", version);
		 
		 String handle = baseHandle+version;	
		 /**
		  * We already have the version, so just put it in handle to keep the code as close as possible to showCodebook()
		  * The showCodebook code below uses: handle + version.  
		  * set handle to baseHandle to keep the code as close as possible to showCodebook()
		  */

		 		/**
		 		 * This may need work.  I was not able to test it out.  I just added "/study" to two redirects
		 		 * fetchDefault only really gets the version number.  We should have it by the time we get here.
		 		 */
				if(!loader.hasCodebook(handle)){
					String latest = loader.fetchDefault(handle);
					if(!latest.equals("")){
						logger.debug("Found latest version of handle "+handle+" version "+latest);
						String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
						//TODO: Find more elegant solution. Trailing slash causes circular view path exception and results in 302
						if(usedURL.endsWith("/")){
							logger.debug("Adding study to redirect");
							return "redirect:/codebooks/"+handle+"/v/"+latest+"/study";
						}else{
							//needs absolute url
							String reURL = context.getContextPath() +"/codebooks/"+handle+"/v/"+latest+"/study";
							response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
							response.setHeader("Location", reURL);
							logger.debug("Redirecting to url " + reURL + "\n");
							return "";
						}
					}
					session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
					return "redirect:/";
				}	
				try{
					 model.addAttribute("hasMath", true);
					 // Returns codebook's: handle, variables (count), /docDscr, /stdyDscr, /fileDscr
					 String xml = getTitlePage(handle);
					 
					 //Adds download pdf link
					 String baseURI = loader.getPath() + "/rest/";
					 String[] indexInfo = loader.getCodebooks(baseURI).get(handle);

					 // Keep the same codebook pdf per Lars.
					 String docTestURI = baseURI +"codebooks/"+handle+"/haspdf";
					 String hasDoc = Fetch.get(docTestURI).trim();	
					 if(hasDoc.equals("1")){
						 model.addAttribute("pdf", loader.getBuildName() + "/pdf/"+handle+".pdf");
					 } 

					 String path = context.getRealPath("/xsl/codebook.xsl");//Local file path to find XSL doc
					 
					 // Removed, commented out namespace...

					 Parser xp = new Parser(xml, path, 0);
					 // New DDI parses.  dataDscr is skipped because use a link the codebook page does today.

					 // For now, keep the study logic the same as the showCodebook logic, for this if block.
					 String count = xp.getAttrValue("/codeBook", "variables");
					 if(count != null){
						 
						 if(indexInfo[3].equals("deprecated")){
							 String latest = loader.fetchDefault(indexInfo[0]);
							 model.addAttribute("newVersion",latest);
						 }
						 model.addAttribute("baseHandle",indexInfo[0]);
						 
						 String title = xp.getValue("/codeBook/docDscr/citation/titlStmt/titl");
						 String stdyTitle = xp.getValue("/codeBook/stdyDscr/citation/titlStmt/titl");

						 //Crowdsourcing
						 model.addAttribute("crowdsourceSwitch",Config.getInstance().getCrowdSourcingRole());
						 String remoteURL = Config.getInstance().getRemoteURL() 
						 + "/codebooks/"+indexInfo[0]+"/v/"+indexInfo[1];
						 model.addAttribute("remoteServerURL",remoteURL);
						 
						 model.addAttribute("handle", handle);
						 model.addAttribute("codebook", xp.getData());
						 
						 model.addAttribute("codebookUse",indexInfo[2]);
						 model.addAttribute("count", count);
						 model.addAttribute("codebookTitl",title);
						 model.addAttribute("subTitl",stdyTitle);
						 model.addAttribute("metaDesc","Study page for the " + title + " codebook");
						 model.addAttribute("metaKeywords",","+title);	 
						 String[][] crumbs = new String[][] {{title,"codebooks/"+baseHandle+"/v/"+version}};
						 model.addAttribute("crumbs", crumbs);

						 /**
						  * Adding tabs in the jsp to break out the codebook into the main complex types for the DDI codeBookType:
						  *   docDscr	- /docDscr returned by getTitlePage()
						  *   stdyDscr	- /stdyDscr returned by getTitlePage()
						  *   fileDscr	- /fileDscr returned by getTitlePage()
						  *   dataDscr	- Only a variable COUNT is returned by getTitlePage() today.
						  *   			  Put the View Variables link in the Data tab to make it work like the current codebook page.
						  *   otherMat	- Not found in any source code.  (Only mentioned .xsd's)
						  *   			  1/18/17 - Lars wants to keep the tab, but possibly balance out tab content 
						  *   				via .xsl's.  Added otherMat.xsl with only a count expression, so they can add 
						  *   				content to the .xsl if they want.
						  *   			  Remember the current underlying code does not return any /otherMat elements.
						  *
						  * If the tab has been disabled, skip processing it to improve performance.
						  * 
						  * For each tab: 
						  * 	Set the stylesheet, parse the xml to get the tab content, put the html content into the model.
						  * 	Also, pass into the model weather the tab is enbabled (true) and the label to dispaly on the tab.
						  */				 	 

						if(config.getUiNavTabDoc()) {
							String pathDocDscr = context.getRealPath("/xsl/doc.xsl");
							Parser xpDocDscr = new Parser(xml, pathDocDscr, 0);
							model.addAttribute("codebookDocDscr", xpDocDscr.getData());
							model.addAttribute("uiNavTabDoc",config.getUiNavTabDoc());
							model.addAttribute("uiNavTabDocLabel",config.getUiNavTabDocLabel());
						}

						 /**
						  * Do NOT set the Study tab to false (in the config file.)
						  * The user gets to this page by selecting a Study Title on the studies.jsp page.
						  * The Study tab content is displayed when the study.jsp page is opened.
						  */
						if(config.getUiNavTabStdy()) {
							String pathStdyDscr = context.getRealPath("/xsl/study.xsl");
							Parser xpStdyDscr = new Parser(xml, pathStdyDscr, 0);
							model.addAttribute("codebookStdyDscr", xpStdyDscr.getData());
							model.addAttribute("uiNavTabStdy",config.getUiNavTabStdy());
							model.addAttribute("uiNavTabStdyLabel",config.getUiNavTabStdyLabel()); 
						}

						if(config.getUiNavTabFile()) {
							String pathFileDscr = context.getRealPath("/xsl/file.xsl");
							Parser xpFileDscr = new Parser(xml, pathFileDscr, 0);
							model.addAttribute("codebookFileDscr", xpFileDscr.getData());
							model.addAttribute("uiNavTabFile",config.getUiNavTabFile());
							model.addAttribute("uiNavTabFileLabel",config.getUiNavTabFileLabel());

						}

						if(config.getUiNavTabData()) {
							// dataDscr is skipped.  The current codebook jsp page displays a link to the View Variables page.
							model.addAttribute("uiNavTabData",config.getUiNavTabData());
							model.addAttribute("uiNavTabDataLabel",config.getUiNavTabDataLabel());
						}
						 
						if(config.getUiNavTabOtherMat()) {
							String pathOtherMat = context.getRealPath("/xsl/otherMat.xsl");
							Parser xpOtherMat = new Parser(xml, pathOtherMat, 0);
							model.addAttribute("codebookOtherMat", xpOtherMat.getData());
							model.addAttribute("uiNavTabOtherMat",config.getUiNavTabOtherMat());
							model.addAttribute("uiNavTabOtherMatLabel",config.getUiNavTabOtherMatLabel());
						}


						 if(print.equals("y")){
							DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
							Date dateStamp = new Date();
							String timeStamp = dateFormat.format(dateStamp);
							model.addAttribute("timeStamp", timeStamp);
							model.addAttribute("print", true);
						 }	

						 return "/WEB-INF/views/study.jsp";
					 }
				 }catch(NullPointerException | NumberFormatException e){		
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						model.addAttribute("error","Error retrieving data");	
						model.addAttribute("type","error");
						logger.error(e.getMessage());
						return "redirect:/";
					}
				 return "redirect:/";
	}


}