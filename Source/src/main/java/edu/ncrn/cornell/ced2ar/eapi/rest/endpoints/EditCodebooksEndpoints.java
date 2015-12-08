package edu.ncrn.cornell.ced2ar.eapi.rest.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.ncrn.cornell.ced2ar.api.data.FileUpload;
import edu.ncrn.cornell.ced2ar.eapi.rest.queries.EditCodebookData;

/**
 * Provides RESTful endpoints for /codebooks. Calls CodebookData class.
 * 	
 * @author Cornell University, Copyright 2012-2015
 * @author Ben Perry
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */ 

@RestController
public class EditCodebooksEndpoints {
	
	private final static Logger logger = Logger.getLogger(EditCodebooksEndpoints.class.getName());
	private final static String API_PREFIX = "/erest";//TODO: put somewhere else

//Endpoints //TODO: Document endpoint methods
	
	/**
	 * Returns welcome message for API root
	 * @param handle
	 * @param request
	 * @return
	 */
	@RequestMapping(value = API_PREFIX, method = RequestMethod.GET, produces={"application/xml","application/json"})
	public String getWelcome(HttpServletRequest request, String handle) {
		String returnValue = "<body><h2>Welcome!</h2><p>This is the editing API</p></body>";
		return returnValue;
	}

	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}", method = RequestMethod.POST)
	public String postCodebook(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version, 
	@ModelAttribute("file") FileUpload uploadForm, @RequestParam(value="label",required = false)  String label,
	@RequestParam(value="user") String user, @RequestParam(value="master", defaultValue = "0") boolean master) {
		InputStream ins = null;
		try {
			EditCodebookData codebookData = new EditCodebookData();
			MultipartFile file = uploadForm.getFile();	
			ins = file.getInputStream();
			int responseCode = codebookData.postCodebook(ins, baseHandle, version, label, user, master);
			response.setStatus(responseCode);
			if(responseCode != 200){
				return codebookData.getError();
			}
		} catch (IOException|NullPointerException e) {
			response.setStatus(400);
			return "Error reading input stream.";
		}
		return "Codebook successfully updated";
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}", method = RequestMethod.DELETE)
	public String deleteCodebook(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version) {
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode =codebookData.deleteCodebook(baseHandle, version);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error deleting codebook - "+baseHandle+ " "+version;
		}
		return "Successfully deleted codebook - "+baseHandle+ " "+version;
	}	
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/accessvars", method = RequestMethod.POST)
	public String access(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,
	@RequestParam(value="vars", required=false) String vars, @RequestParam(value="access") String access,
	@RequestParam(value="all", defaultValue = "0") boolean all){
		
		if(all != true && StringUtils.isEmpty(vars)){
			response.setStatus(400);
			return "Paramater all must be equal to true, or vars parameter must not be null";
		}
		
		String[] v = vars == null ? null : vars.split(" ");
	
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.setAccessLevels(baseHandle, version, access, v , all);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error changing access levels "
			+baseHandle+ " "+version + " - " +codebookData.getError();	
		}
		return "Sucecsfully changed access levels "+baseHandle+ " "+version;
	}	
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/edit", method = RequestMethod.POST)
	public String editCover(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,
	@RequestParam(value="field") String field, @RequestParam(value="value") String value,
	@RequestParam(value="user", defaultValue="anonymous") String user, 
	@RequestParam(value="index", defaultValue = "0") int index, 
	@RequestParam(value="append", defaultValue = "0") boolean doesAppend, 
	@RequestParam(value="delete", defaultValue = "0") boolean delete){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.editCover(baseHandle, version, field, value, index, delete, doesAppend, user);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error editing cover "
			+baseHandle+ " "+version + " - " +codebookData.getError();
		}
		return "Succesful update for "+baseHandle+ " "+version;
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/editMulti", method = RequestMethod.POST)
	public String editCoverMulti(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,
	@RequestParam(value="paths") ArrayList<String> paths, @RequestParam(value="values") ArrayList<String> values,
	@RequestParam(value="user", defaultValue="anonymous") String user,
	@RequestParam(value="append", defaultValue = "0") boolean doesAppend){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.editCoverMulti(baseHandle, version, paths, values, doesAppend, user);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error editing multifield block in the cover "
			+baseHandle+ " "+version + " - " +codebookData.getError();
		}
		return "Succesful update for "+baseHandle+ " "+version;
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/vars/{var}/edit", method = RequestMethod.POST)
	public String editVar(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,	
	@PathVariable("var") String var, @RequestParam(value="field") String field,
	@RequestParam(value="value") String value, @RequestParam(value="append", defaultValue = "0") boolean doesAppend,
	@RequestParam(value="index", defaultValue = "0") int index, @RequestParam(value="index2", defaultValue = "0") int index2, 
	@RequestParam(value="delete", defaultValue = "0") boolean delete, 
	@RequestParam(value="user", defaultValue = "anonymous") String user,
	@RequestParam(value="ip", defaultValue = "anonymous") String ip){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.editVar(baseHandle, version, var, field, 
		value, index, index2, doesAppend, delete, ip, user);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error editing "+baseHandle+" "+version +" for var " 
			+ var + " - " +codebookData.getError();
		}
		return "Succesful update for "+baseHandle+" "+version +" for var " + var;
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/vars/{var}/editMulti", method = RequestMethod.POST)
	public String editVarMulti(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,	
	@PathVariable("var") String var, @RequestParam(value="paths") ArrayList<String> paths, 
	@RequestParam(value="values") ArrayList<String> values, 
	@RequestParam(value="user", defaultValue="anonymous") String user, 
	@RequestParam(value="append", defaultValue = "0") boolean doesAppend){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.editVarMulti(baseHandle, version, var, paths, values, doesAppend, user);
		response.setStatus(responseCode);
		
		if(responseCode != 200){
			return "Error editing multifield "+baseHandle+" "+version +" for var " 
			+ var + " - " +codebookData.getError();
		}
		
		return "Succesful mutlifield update for "+baseHandle+" "+version +" for var " + var;
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/vargrp/{id}", method = RequestMethod.POST)
	public String editVarGrp(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,
	@PathVariable("id") String id, @RequestParam(value="name", defaultValue = "") String name,
	@RequestParam(value="labl", defaultValue = "") String labl,
	@RequestParam(value="txt", defaultValue = "") String txt){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.editVarGrp(baseHandle, version, id, name, labl, txt);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error editing "+baseHandle+" "+version +" for vargrp " 
			+ id + " - " + codebookData.getError();	
		}
		return "Successful edit on "+baseHandle+" "+version +" for vargrp " + id ;	
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/vargrp/{id}", method = RequestMethod.DELETE)
	public String deleteVarGrp(HttpServletRequest request, HttpServletResponse response,
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,
	@PathVariable("id") String id){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.deleteVarGrp(baseHandle, version, id);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error editing "+baseHandle+" "+version +" for vargrp " 
			+ id + " - " + codebookData.getError();	
		}
		return "Deleted vargrp "+id+" from "+baseHandle+" "+version;
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/vargrp/{id}/vars", method = RequestMethod.POST)
	public String editVarsInGrp(HttpServletRequest request, HttpServletResponse response, 
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,
	@PathVariable("id") String id, @RequestParam(value="vars") String vars, 
	@RequestParam(value="append", defaultValue="1") boolean append,
	@RequestParam(value="delete", defaultValue="0") boolean delete){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.editVarGroupVars(baseHandle, version, id, vars, append, delete);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error editing "+baseHandle+" "+version +" for vars in vargrp " 
			+ id + " - " + codebookData.getError();	
		}
		return "Edited vars in vargrp "+id+" from "+baseHandle+" "+version;
	}
	
	@RequestMapping(value = API_PREFIX + "/codebooks/{baseHandle}/{version}/settings", method = RequestMethod.POST)
	public String editCodebookUse(HttpServletRequest request, HttpServletResponse response, 
	@PathVariable("baseHandle") String baseHandle, @PathVariable("version") String version,
	@RequestParam("use") String use){
		EditCodebookData codebookData = new EditCodebookData();
		int responseCode = codebookData.editCodebookUse(baseHandle, version, use);
		response.setStatus(responseCode);
		if(responseCode != 200){
			return "Error changed "+baseHandle+" "+version +" use - " + codebookData.getError();	
		}
		return "Succesfully changed "+baseHandle+" "+version +" use";
	}

//Utilities
	
	/**
	 * Accept Header represents the media type in which the content would be delivered
	 * @param request
	 * @return returns accept header value. if no value exists, returns empty String.
	 */
	private String getMediaType(HttpServletRequest request) {
		String acceptHeader = request.getHeader("accept");
		if(StringUtils.isEmpty(acceptHeader)) acceptHeader = "";
		return acceptHeader;
	}
}