package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *Retrieves access level for codebook
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Access extends ServerResource{
	/**
	 * Method represent. Retrieves access level for a codebook
	 * @return Representation the response to the request
	 */
	@Get
	public Representation represent() {
		String handle = (String) getRequestAttributes().get("codebookId");
		String xquery = "let $codebook := collection('CED2AR/"+handle+"')/codeBook "+ 
		"for  $a in $codebook/stdyDscr/dataAccs "+
		"where $a/@ID != '' return  data($a/@ID)";
		
		String accessLevels = BaseX.getXML(xquery);
		Representation response = new StringRepresentation(accessLevels, MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}
}