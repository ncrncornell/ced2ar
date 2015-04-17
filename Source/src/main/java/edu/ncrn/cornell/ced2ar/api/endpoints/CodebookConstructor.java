package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *Super Class for some child endpoints of /codebooks
 *@author NCRN Project Team
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class CodebookConstructor extends ServerResource {
	private String message;
	public String codebookId;
	public String xquery;
	
	
	/**
	 * Retrieves XML content for a codebook
	 * @param variant Variant specifies what type of media will be returned, XML or Json
	
	 * @return Representation */
	@Get("xml|json")
	public Representation represent(Variant variant) {
		if(codebookId == null || codebookId.length() == 0) {
			message = " \"" + codebookId + "\" is an invalid codebookId";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		if(!Utilities.codebookExists(codebookId)){
			String message = " \"" + codebookId + "\" does not exist";
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,message);
		}
		
		Representation codebook = null;
		if (MediaType.TEXT_XML.equals(variant.getMediaType()) || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			String result = BaseX.getXML(xquery);
			codebook = new StringRepresentation("<?xml version='1.0' encoding='UTF-8'?>" + result, MediaType.APPLICATION_XML);
			this.setStatus(Status.SUCCESS_OK);
			return codebook;
		} else if (MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
			codebook = new StringRepresentation(Utilities.xmlToJson(BaseX.getXML(xquery)), MediaType.APPLICATION_JSON);
			this.setStatus(Status.SUCCESS_OK);
			return codebook;
		} else {
			message = " \"" + variant.getMediaType() + "\" is not supported";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
	}
}
