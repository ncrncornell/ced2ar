package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *Welcome message for root endpoint *
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremey Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Schema extends ServerResource{

	/**
	 * Method represent. Retrieves schema from BaseX
	 * @return Representation data and success status
	 */
	@Get
	public Representation represent() {
		String schemaName = (String) getRequestAttributes().get("name");		
		String xquery="let $schema:= collection('schemas/"+schemaName+"')"
		+" return $schema";

		Representation schemaDoc = new StringRepresentation(BaseX.getXML(xquery), MediaType.APPLICATION_XML);
		this.setStatus(Status.SUCCESS_OK);
		return schemaDoc;
	}
}