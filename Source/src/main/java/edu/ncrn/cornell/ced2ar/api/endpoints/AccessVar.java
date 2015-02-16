package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 * Retrieves access level for a specific variable
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class AccessVar extends ServerResource{
	
	/**
	 * Method represent. Retrieves access level for a specific variable
	 * @return Representation the response returned from the request
	 */
	@Get
	public Representation represent() {
		String handle = (String) getRequestAttributes().get("codebookId");
		String var = (String) getRequestAttributes().get("variableName");
		String xquery = "let $codebook := collection('CED2AR/"+handle+"')/codeBook "+ 
		"for $v in $codebook/dataDscr/var "+
		"where $v/@name = '"+var+"' return data($v/@access)";
		
		String accessLevels = BaseX.getXML(xquery);
		Representation response = new StringRepresentation(accessLevels, MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}
}