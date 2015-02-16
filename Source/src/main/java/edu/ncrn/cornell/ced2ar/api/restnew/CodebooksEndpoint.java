package edu.ncrn.cornell.ced2ar.api.restnew;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Part of the new API, all WIP
 * This class is Spring Restful replacement for following classes edu.ncrn.cornell.ced2ar.api.endpoints package   
 * Welcome,Codebook, Codebooks, Access , FileDesc, DocDesc, PdfChecker,CodebookRelease
 * StudyDesc, TitlePage, Vars, Versions, Var,AccessVar, VarGrps, VarGrp,VarGrpVars, 
 * VarVersions, Prov
 * 		
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */ 

@RestController
public class CodebooksEndpoint {
	private final static Logger logger = Logger.getLogger(CodebooksEndpoint.class.getName());
	
	@RequestMapping(value =Constants.GET_WELCOME, method = RequestMethod.GET, produces={"application/xml", "application/json"})
	public String getWelcome(String codebookId, HttpServletRequest request  ) {
		logger.debug("getWelcome() start" );
		String returnValue = "<body><h2>Welcome!</h2><a href='../docs/api'>API Documentation</a></body>";
		logger.debug("returnValue=" + returnValue);
		return returnValue;
	}

	
	/**
	 * This method is the Spring endPoint
	 * @return String returns all codebooks. A Request header 'id-type' is used  controls information retrieved
	 * 		   				
	 * 			id-type							return value
	 * 			empty string(default)	full name of the codebooks is returned.
	 * 			fn				 		key (handle) of the codebooks is returned
	 * 			version			 		handle, version and ful name is returned
	 * 			access					handle of the codebook along with access restritions
	 * 			
	 */
	@RequestMapping(value =Constants.GET_ALL_CODEBOOKS, method = RequestMethod.GET)
	public String getCodebooks(HttpServletRequest request) {
		logger.debug("getCodebooks() start" );
		String idType = request.getHeader("id-type");
		if(StringUtils.isEmpty(idType)) idType = "";
		CodebookData baseXCodebook = new CodebookData();
		return baseXCodebook.getCodebooks(idType);
	}
	
	/**
	 * 
	 * @param codebookId String path value representing the codebook handle to retrieve
	 * @param type	String parameter controls the return value 
	 * @param request	HttpServletRequest
	 * 
	 * @return	Returns full codebook. Return value is controlled by the type paramter
	 * 			if type is git, then the value returned is Formatted  namespace in the same order as BaseX.put() does with XMLHandle.
	 * 			if type is gitNotes, then the value returned included commit hashes
	 * 			if type is empty (default) the value returned contains added namespace  
	 */
	
	@RequestMapping(value =Constants.GET_CODEBOOK, method = RequestMethod.GET)
	public String getCodebook(@PathVariable("codebookId")String codebookId, @RequestParam(value="type", defaultValue="")String type,  HttpServletRequest request  ) {
		logger.debug("getCodebook() start" );
		CodebookData baseXCodebook = new CodebookData();
		String  returnValue = baseXCodebook.getCodebook(type,codebookId,this.getMediaType(request));
		logger.debug("codeBook = " + returnValue);
		return returnValue;
	}
	
	
	
	/**
	 * 
	 * @param codebookId String path value representing the codebook handle  for which access restrictions are being retrieved
	 * @param request HttpServletRequest
	 * @return	Returns access restrictions for the code book identified in the path variable codebookId
	 *  
	 */
	
	@RequestMapping(value =Constants.GET_CODEBOOK_ACCESS, method = RequestMethod.GET)
	public String getCodebookAccess(@PathVariable("codebookId")String codebookId,  HttpServletRequest request  ) {
		logger.debug("getCodebookAccess() start" );
		CodebookData baseXCodebook = new CodebookData();
		String accessLevels = baseXCodebook.getCodebookAccess(codebookId);
		logger.debug("accessLevels = " + accessLevels);
		return accessLevels;
	}

	
	/**
	 * 	
	 * @param codebookId  String path value representing the codebook handle 
	 * @param request HttpServletRequest
	 * @return  Retrieves file descriptor for a codebook and returns it
	 * 
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_FILE_DESC, method = RequestMethod.GET)
	public String getCodebookFileDesc(@PathVariable("codebookId")String codebookId,  HttpServletRequest request  ) {
		logger.debug("getCodebookFileDesc() start" );
		CodebookData baseXCodebook = new CodebookData();
		String fileDesc  =baseXCodebook.getCodebookFileDesc(codebookId, getMediaType(request));
		logger.debug("fileDesc for  " + codebookId+" = " + fileDesc);
		return fileDesc;
	}
	
	
	/**
	 * 
	 * @param codebookId  String path value representing the codebook handle 
	 * @param request HttpServletRequest
	 * @return doc descriptor for a codebook
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_DOC_DESC, method = RequestMethod.GET)
	public String getCodebookDocDesc(@PathVariable("codebookId")String codebookId,  HttpServletRequest request  ) {
		logger.debug("getCodebookDocDesc() start" );
		CodebookData baseXCodebook = new CodebookData();
		String docDesc = baseXCodebook.getCodebookDocDesc(codebookId, getMediaType(request));
		logger.debug("docDesc for  " + codebookId+" = " + docDesc);
		return docDesc;
	}


	/**
	 * @param codebookId  String path value representing the codebook handle 
	 * @param request HttpServletRequest
	 * @return Returns 1 if pdf exists for the codebook; 0 otherwise.
	 */
	
	@RequestMapping(value =Constants.GET_CODEBOOK_HAS_PDF, method = RequestMethod.GET)
	public String hasPdf(@PathVariable("codebookId")String codebookId,  HttpServletRequest request  ) {
		//TODO This method does not have corresponding local method in BaseXCodebook 
		logger.debug("hasPdf() start" );
		String found = "0";		
		ServletContext context  = request.getServletContext();
		String path = context.getRealPath("/pdf/"+codebookId+".pdf");
		File f = new File(path);	 
		if(f.exists()){
			logger.debug("Found PDF " + path );
			found= "1";		
		}
		logger.debug("Returning " + found );
		return found;
	}
	
	/**
	 * 
	 * @param codebookId
	 * @param i include parameter to list the releases that should include.  
	 * @param request
	 * @return Returns Codebook in XML format
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_RELEASE, method = RequestMethod.GET, produces={"application/xml", "application/json"})
	public String getCodebookRelease(@PathVariable("codebookId")String codebookId, @RequestParam(value="i", defaultValue="")String i, HttpServletRequest request  ) {
		logger.debug("getCodebookRelease() start" );
		if(!StringUtils.isEmpty(i)) {
			if(i.matches("^.*[^a-zA-Z0-9, ].*$")){
				String message = "Invalid include parameter. Must only contain alphanumeric values, seperated by commas";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
			}
		}
		CodebookData baseXCodebook = new CodebookData();
		String release = baseXCodebook.getCodebookRelease(codebookId, i, getMediaType(request));
		logger.debug("codebookRelease for  " + codebookId+" = " + release);
		return release;
	}
	
	/**
	 * 
	 * @param codebookId 
	 * @param request
	 * @return Returns Study Descriptor for the codebook
	 */

	@RequestMapping(value =Constants.GET_CODEBOOK_STUDY_DESC, method = RequestMethod.GET)
	public String getCodebookStudyDesc(@PathVariable("codebookId")String codebookId,  HttpServletRequest request  ) {
		logger.debug("getCodebookStudyDesc() start" );
		CodebookData baseXCodebook = new CodebookData();
		String codebookStudyDesc = baseXCodebook.getCodebookStudyDesc(codebookId, getMediaType(request)); 
		logger.debug("codebookRelease for  " + codebookId+" = " + codebookStudyDesc);
		return codebookStudyDesc;
	}

	/**
	 * 
	 * @param codebookId
	 * @param request
	 * @return Title page of the codebook
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_TITLE_PAGE, method = RequestMethod.GET)
	public String getCodebookTitlePage(@PathVariable("codebookId")String codebookId, HttpServletRequest request) {
		logger.debug("getCodebookTitlePage() start" );
		CodebookData baseXCodebook = new CodebookData();
		String codebookTitlePage= baseXCodebook.getCodebookTitlePage(codebookId, getMediaType(request));
		logger.debug("codebookTitlePage for  " + codebookId+" = " + codebookTitlePage);
		return codebookTitlePage;
	}

	/**
	 * 
	 * @param codebookId
	 * @param request
	 * @param response
	 * @return List of Codebook variables in the format requested in accept header. Adds response header called count
	 * 		   representing the number of variables in the codebook.
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_VARIABLES, method = RequestMethod.GET)
	public String getCodebookVariables(@PathVariable("codebookId")String codebookId,  HttpServletRequest request,HttpServletResponse response ) {
		logger.debug("getCodebookVariables() start" );
		CodebookData baseXCodebook = new CodebookData();
		String codebookVariables= baseXCodebook.getCodebookVariables(codebookId, getMediaType(request));
		logger.debug("codebookVariables for  " + codebookId+" = " + codebookVariables);
		String codebookVariablesCount = baseXCodebook.getCodebookVariablesCount(codebookId);
		if(StringUtils.isEmpty(codebookVariablesCount)) codebookVariablesCount= "";
		response.addHeader("count", codebookVariablesCount);
		return codebookVariables;
	}

	
	
	@RequestMapping(value =Constants.GET_CODEBOOK_VERSIONS, method = RequestMethod.GET, produces={"application/xml", "application/json","text/csv"})
	public String getCodebookVersions(@PathVariable("codebookId")String codebookId, @RequestParam(value="type", defaultValue="")String type, HttpServletRequest request  ) {
		logger.debug("getCodebookVersions() start" );
		CodebookData baseXCodebook = new CodebookData();
		String commits = baseXCodebook.getCodebookVersions(codebookId,type);
		return commits;
	}
	
	

	@RequestMapping(value =Constants.GET_CODEBOOK_VARIABLE, method = RequestMethod.GET, produces={"application/xml", "application/json","text/csv"})
	public String getCodebookVariable(@PathVariable("codebookId")String codebookId, @PathVariable("variableName")String variableName, HttpServletRequest request  ) {
		logger.debug("getCodebookVariable() start" );
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;
		CodebookData baseXCodebook = new CodebookData();
		String variable= baseXCodebook.getCodebookVariable(codebookId,variableName,this.getMediaType(request),isPartial);
		logger.debug("codebookVariable for  " + codebookId +":"+ variableName +" = " + variable);
		return variable;
	}
	/**
	 * 
	 * @param codebookId
	 * @param variableName
	 * @param request
	 * @return Access level of the variable identified in the codebook
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_VARIABLE_ACCESS, method = RequestMethod.GET, produces={"application/xml", "application/json"})
	public String getCodebookVariableAccess(@PathVariable("codebookId")String codebookId, @PathVariable("variableName")String variableName, HttpServletRequest request  ) {
		logger.debug("getCodebookVariableAccess() start" );
		CodebookData baseXCodebook = new CodebookData();
		String access= baseXCodebook.getCodebookVariableAccess(codebookId, variableName);
		logger.debug("codebookVariableAccess for  " + codebookId +":"+ variableName +" = " + access);
		return access;
	}


	/**
	 * 	
	 * @param codebookId
	 * @param variableName
	 * @param request
	 * @return Versions of the codebook variable
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_VARIABLE_VERSIONS, method = RequestMethod.GET, produces={"application/xml", "application/json","text/csv"})
	public String getCodebookVariableVersions(@PathVariable("codebookId")String codebookId, @PathVariable("variableName")String variableName, HttpServletRequest request  ) {
		logger.debug("getCodebookVariableVersions() start" );
		String type = request.getHeader("type");
		if(StringUtils.isEmpty(type)) type = "";
		CodebookData baseXCodebook = new CodebookData();
		String commits = baseXCodebook .getCodebookVariableVersions(codebookId, variableName,type);
		return commits;
	}

	

	/**
	 * 
	 * @param codebookId codebook handle
	 * @param request
	 * @return variable groups in the codebook
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_VARIABLE_GROUPS, method = RequestMethod.GET, produces={"application/xml", "application/json"})
	public String getCodebookVariableGroups(@PathVariable("codebookId")String codebookId,HttpServletRequest request  ) {
		logger.debug("getCodebookVariableGroups() start" );
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;
		CodebookData baseXCodebook = new CodebookData();
		String groups= baseXCodebook.getCodebookVariableGroups(codebookId,isPartial,this.getMediaType(request));
		logger.debug("codebookVariableGroup for  " + codebookId +" = " + groups);
		return groups;
	}
	
	/**
	 * 
	 * @param codebookId codebook handle
	 * @param varGrpID variable group id
	 * @param request
	 * @return variable group information
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_VARIABLE_GROUP, method = RequestMethod.GET, produces={"application/xml", "application/json"})
	public String getCodebookVariableGroup(@PathVariable("codebookId")String codebookId,@PathVariable("varGrpID")String varGrpID,HttpServletRequest request  ) {
		logger.debug("getCodebookVariableGroup() start" );
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;
		CodebookData baseXCodebook = new CodebookData();
		String group= baseXCodebook.getCodebookVariableGroup(codebookId,varGrpID,isPartial,this.getMediaType(request));
		logger.debug("codebookGroup for  " + codebookId +" = " + group);
		return group;
	}
	
	/**
	 * 
	 * @param codebookId Codebook handle
	 * @param varGrpID variable group id
	 * @param request 
	 * @return Variables in the group
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_GROUP_VARIABLES, method = RequestMethod.GET, produces={"application/xml", "application/json", "text/csv"})
	public String getCodebookGroupVariables(@PathVariable("codebookId")String codebookId,@PathVariable("varGrpID")String varGrpID,HttpServletRequest request  ) {
		logger.debug("getCodebookGroupVariables() start" );
		boolean isPartial = false;
		String partialText = request.getHeader("partial-text");
		if(StringUtils.isEmpty(partialText)) partialText = "";
		isPartial = partialText.toLowerCase().equals("true") ? true : false;
		CodebookData baseXCodebook = new CodebookData();
		String groupVariables= baseXCodebook.getCodebookGroupVariables(codebookId,varGrpID,isPartial,this.getMediaType(request));

		logger.debug("groupVariables for  " + codebookId +" = " + groupVariables);
		return groupVariables;
	}
	
	
	/**
	 * 
	 * @param name
	 * @param request
	 * @return
	 */
	@RequestMapping(value =Constants.GET_SCHEMA, method = RequestMethod.GET, produces={"application/xml", "application/json","text/csv"})
	public String getSchema(@PathVariable("name")String name,HttpServletRequest request) {
		logger.debug("getSchema() start" );
		CodebookData baseXCodebook = new CodebookData();
		String returnValue = baseXCodebook.getSchema(name);
		logger.debug("returnValue = " + returnValue);
		return returnValue;
	}


	@RequestMapping(value =Constants.GET_SCHEMA_DOC_TYPE, method = RequestMethod.GET, produces={"application/xml", "application/json","text/csv"})
	public String getSchemaDocType(@PathVariable("name")String name,@PathVariable("type")String type,HttpServletRequest request) {
		logger.debug("getSchemaDocType() start" );
		CodebookData baseXCodebook = new CodebookData();
		String returnValue = baseXCodebook.getSchemaDocType(name,type);
		logger.debug("returnValue = " + returnValue);
		return returnValue;
	}


	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value =Constants.GET_PROV, method = RequestMethod.GET, produces={"application/xml", "application/json","text/csv"})
	public String getProv(HttpServletRequest request) {
		logger.debug("getProv() start" );
		CodebookData baseXCodebook = new CodebookData();
		String returnValue = baseXCodebook.getProv();
		logger.debug("returnValue = " + returnValue);
		return returnValue;
	}


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
