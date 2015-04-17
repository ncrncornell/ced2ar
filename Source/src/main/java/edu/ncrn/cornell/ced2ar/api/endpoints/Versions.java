package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;

/**
 *Handles git versions at the codebook level
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Versions extends ServerResource{

	/**
	 *Retrieves commits and time stamps
	 */
	@Get
	public Representation getVersions() {
		String type = "";	
		try{
			type  =  getQuery().getFirstValue("type");	
			if(type == null)
				type = "";
		}catch(NullPointerException e){}
		
		String codebookId = (String) getRequestAttributes().get("codebookId");
		if (codebookId == null || codebookId.length() == 0) {
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		if(!Utilities.codebookExists(codebookId)){
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,message);	
		}
		
		String commits = QueryUtil.getCommits(codebookId,type);
		return new StringRepresentation(commits, MediaType.TEXT_HTML);
	}
}