package edu.cornell.ncrn.ced2ar.api.rest.endpoints;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cornell.ncrn.ced2ar.api.rest.queries.CodebookData;
import edu.cornell.ncrn.ced2ar.api.rest.queries.CodebookScore;

/**
 * Provides RESTful endpoints for /codebooks. Calls CodebookData class.
 * 	
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty, Ben Perry
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */ 

//TODO: CodebookData foreach method
@RestController
public class CodebooksEndpoints {
	
	public static final String API_PREFIX = "/rest";
	private final static Logger logger = Logger.getLogger(CodebooksEndpoints.class.getName());
	private final CodebookData codebookData = new CodebookData();

//Endpoints
	/**
	 * Returns welcome message for API root
	 * @param handle
	 * @param request
	 * @return
	 */
	@RequestMapping(value = API_PREFIX, method = RequestMethod.GET, produces={"application/xml","application/json"})
	public String getWelcome(HttpServletRequest request, String handle) {
		String returnValue = "<body><h2>Welcome!</h2><a href='../docs/api'>API Documentation</a></body>";
		return returnValue;
	}

	/**
	 * Calls codebookData.getCodebooks to retrieve a list of codebooks
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = API_PREFIX+"/codebooks", method = RequestMethod.GET, 
	produces={"application/xml","text/plain","application/json"})
	public String getCodebooks(HttpServletRequest request) {
		logger.debug("getCodebooks() start");
		String idType = request.getHeader("id-type");
		if(getMediaType(request).equals("application/json")){
			if(idType == null || !idType.equals("json2")){
				idType = "json";
			}
		}else if(StringUtils.isEmpty(idType)){
			idType = "";
		}	
		return codebookData.getCodebooks(idType);
	}
	
	/**
	 * Calls codebookData.getCodebook to retrieve a specific codebook
	 * @param request
	 * @param handle - codebook handle
	 * @param type - format type for the codebook. See codebookData.getCodebook for specific types
	 * @param master - if true, will retrieve copy of codebook from master database if avalible
	 * @return
	 */
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}", method = RequestMethod.GET)
	public String getCodebook(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("handle") String handle, @RequestParam(value="type", defaultValue="") String type, 
	@RequestParam(value="database", defaultValue="CED2AR") String database){
		String returnValue = codebookData.getCodebook(type,handle,this.getMediaType(request),database);
		if(StringUtils.isEmpty(returnValue)){
			logger.error(codebookData.getError());
			response.setStatus(404);
		}
		return returnValue;
	}


	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/access", method = RequestMethod.GET)
	public String getCodebookAccess(HttpServletRequest request , @PathVariable("handle") String handle) {
		String accessLevels = codebookData.getCodebookAccess(handle);
		return accessLevels;
	}
	
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/filedesc", method = RequestMethod.GET)
	public String getFileDesc(HttpServletRequest request , @PathVariable("handle") String handle) {	
		String fileDesc = codebookData.getCodebookFileDesc(handle, getMediaType(request));
		return fileDesc;
	}	
		
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/docdesc", method = RequestMethod.GET)
	public String getDocDesc(HttpServletRequest request , @PathVariable("handle") String handle) {
		String docDesc = codebookData.getCodebookDocDesc(handle, getMediaType(request));
		return docDesc;
	}

	
	@RequestMapping(value = API_PREFIX+ "/codebooks/{handle}/haspdf", method = RequestMethod.GET)
	public String hasPdf(HttpServletRequest request , @PathVariable("handle") String handle) {
		String found = "0";		
		ServletContext context  = request.getServletContext();
		String path = context.getRealPath("/pdf/"+handle+".pdf");
		File f = new File(path);	 
		if(f.exists()){
			logger.debug("Found PDF " + path );
			found= "1";		
		}
		return found;
	}

	/**
	 * 
	 * @param handle Codebook handle
	 * @param i include parameter to list the releases that should include.  
	 * @param request
	 * @return Codebook
	 */
	@RequestMapping(value = API_PREFIX+ "/codebooks/{handle}/release", method = RequestMethod.GET, 
	produces={"application/xml", "application/json"})
	public String getCodebookRelease(HttpServletRequest request,
	@PathVariable("handle") String handle, @RequestParam("i") String i) {
		String release = codebookData.getCodebookRelease(handle, i, getMediaType(request));
		return release;
	}
	
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/studydesc", method = RequestMethod.GET)
	public String getStudyDesc(HttpServletRequest request , @PathVariable("handle") String handle) {
		String studyDesc = codebookData.getStudyDesc(handle, getMediaType(request)); 
		return studyDesc;
	}

	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/titlepage", method = RequestMethod.GET)
	public String getTitlePage(@PathVariable("handle") String handle, HttpServletRequest request) {
		String titlePage= codebookData.getTitlePage(handle, getMediaType(request));
		return titlePage;
	}
	
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/variables", method = RequestMethod.GET)
	public String getVariables(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("handle") String handle) {
		String format = request.getHeader("format");
		if(StringUtils.isEmpty(format)) format = "";		
		String codebookVariables= codebookData.getCodebookVariables(handle, getMediaType(request),format);
		String codebookVariablesCount = codebookData.getVariablesCount(handle);
		if(StringUtils.isEmpty(codebookVariablesCount)) codebookVariablesCount= "";
		response.addHeader("count", codebookVariablesCount);
		
		if(StringUtils.isEmpty(codebookVariables)){
			logger.error(codebookData.getError());
			response.setStatus(404);
		}
		
		return codebookVariables;
	}
		
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/versions", method = RequestMethod.GET, 
	produces={"application/xml","application/json","text/csv"})
	public String getCodebookVersions(HttpServletRequest request, @PathVariable("handle") String handle, 
	@RequestParam(value="type", defaultValue="") String type) {		
		String commits = codebookData.getCodebookVersions(handle,type);
		return commits;
	}
	
	/**
	 * Endpoint to retrieve a single variable in a codebook
	 * @param request
	 * @param response
	 * @param handle
	 * @param variableName
	 * @return
	 */
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/variables/{variableName}", method = RequestMethod.GET, 
	produces={"application/xml","application/json","text/csv"})
	public String getVariable(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("handle") String handle, @PathVariable("variableName") String variableName){
		String master = request.getHeader("master");
		if(StringUtils.isEmpty(master)) master = "";
		String database = master.toLowerCase().equals("true") ? "CED2ARMaster" : "CED2AR";
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;
		String variable= codebookData.getVariable(handle,variableName,this.getMediaType(request),isPartial,database);
		if(variable == null ) response.setStatus(404);
		return variable;
	}

	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/variables/{variableName}/access", method = RequestMethod.GET, 
	produces={"application/xml","application/json"})
	public String getVariableAccess(HttpServletRequest request, @PathVariable("handle") String handle, 
		@PathVariable("variableName") String variableName) {
		String access= codebookData.getVariableAccess(handle, variableName);
		return access;
	}

	@RequestMapping(value = API_PREFIX+ "/codebooks/{handle}/variables/{variableName}/versions", method = RequestMethod.GET, 
	produces={"application/xml","application/json","text/csv"})
	public String getVariableVersions(@PathVariable("handle") String handle, 
	@PathVariable("variableName")String variableName, HttpServletRequest request) {
		String type = request.getHeader("type");
		if(StringUtils.isEmpty(type)) type = "";
		String commits = codebookData.getVariableVersions(handle, variableName,type);
		return commits;
	}


	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/vargroups", method = RequestMethod.GET, 
	produces={"application/xml","application/json"})
	public String getVariableGroups(HttpServletRequest request, @PathVariable("handle") String handle) {
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;
		
		String groups= codebookData.getVariableGroups(handle,isPartial,this.getMediaType(request));
		logger.debug("codebookVariableGroup for  " + handle +" = " + groups);
		return groups;
	}
	
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/vargroups/{varGrpID}", method = RequestMethod.GET,
	produces={"application/xml","application/json"})
	public String getVariableGroup(HttpServletRequest request, @PathVariable("handle") String handle,
	@PathVariable("varGrpID") String varGrpID) {
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;		
		String group= codebookData.getVariableGroup(handle,varGrpID,isPartial,this.getMediaType(request));
		return group;
	}
	
	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/vargroups/{varGrpID}/vars", method = RequestMethod.GET,
	produces={"application/xml","application/json", "text/csv"})
	public String getGroupVariables(@PathVariable("handle") String handle,
	@PathVariable("varGrpID") String varGrpID,HttpServletRequest request) {
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;
		String groupVariables= codebookData.getGroupVariables(handle,varGrpID,isPartial,this.getMediaType(request));
		return groupVariables;
	}
	
	@RequestMapping(value = API_PREFIX+ "/schemas/{name}", method = RequestMethod.GET,
	produces={"application/xml","application/json","text/csv"})
	public String getSchema(HttpServletRequest request, @PathVariable("name") String name) {	
		String returnValue = codebookData.getSchema(name);
		return returnValue;
	}

	@RequestMapping(value = API_PREFIX+ "/schemas/{name}/doc/{type}", method = RequestMethod.GET,
	produces={"application/xml","application/json","text/csv"})
	public String getSchemaDocType(HttpServletRequest request,
	@PathVariable("name")String name,@PathVariable("type") String type) {
		String returnValue = codebookData.getSchemaDocType(name,type);
		return returnValue;
	}

	@RequestMapping(value = API_PREFIX+"/prov", method = RequestMethod.GET,
	produces={"application/xml","application/json","text/csv"})
	public String getProv(HttpServletRequest request) {
		String returnValue = codebookData.getProv();
		return returnValue;
	}

	@RequestMapping(value = API_PREFIX+"/codebooks/{handle}/score", method = RequestMethod.GET,
	produces={"application/json"})
	public String getCodebookScore(HttpServletRequest request, @PathVariable("handle") String handle) {
		CodebookScore codebookScore = null;
		try{
			codebookScore = new CodebookScore();
			String score = codebookScore.getCodebookScore(handle);
			if(score == null) return codebookScore.getError();
			return score;
		}finally{
			codebookScore = null;
		}
	}	
	
//Utilities
	
	/**
	 * Accept Header represents the media type in which the content would be delivered
	 * @param request
	 * @return returns accept header value. if no value exists, returns empty String.
	 */
	private String getMediaType(HttpServletRequest request) {
		String acceptHeader = request.getHeader("accept");
		if(StringUtils.isEmpty(acceptHeader)) {
			acceptHeader = "";
		}
		return acceptHeader;
	}
}