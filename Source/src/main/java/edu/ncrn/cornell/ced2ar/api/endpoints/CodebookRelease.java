package edu.ncrn.cornell.ced2ar.api.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;

/** 
 * For the {baseUrl}/codebooks/{codebookId} endpoint
 * Retrieves specific codebook and only includes released information
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class CodebookRelease extends ServerResource {
	
	/**
	 * Retrieves the XML content of an entire codebook
	 * @param variant Variant specifies what media type to retrieve data in
	 * @return the representation of the data in either XML or Json
	 */
	@Get("xml")
	public Representation represent() {
		String codebookId = (String) getRequestAttributes().get("codebookId");
		String xquery;
		
		if(codebookId == null || codebookId.length() == 0){
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		List<String> includeList = null;
		try{
			String include  =  getQuery().getFirstValue("i");
			if(include.matches("^.*[^a-zA-Z0-9, ].*$")){
				String message = "Invalid include parameter. Must only contain alphanumeric values, seperated by commas";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
			}
			includeList = Arrays.asList(include.split(","));
			if(includeList.size() <= 0 | includeList.get(0).trim().equals(""))
				throw new NullPointerException();
		}catch(NullPointerException e){
			includeList = new ArrayList<String>();
			//includeList.add("released");//Default level to release
		}
		
		//Added namespace when retrieving entire codebook
		xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return $codeBook";
		String codeBook = "<?xml version='1.0' encoding='UTF-8'?>" + BaseX.getXML(xquery);
		XMLHandle handle = new XMLHandle(codeBook,Config.getInstance().getSchemaURI());
		handle.removeRestricted(includeList, true);
		String release = handle.docToString();
		
		Representation response = null;
		response = new StringRepresentation(release, MediaType.APPLICATION_XML);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}
}