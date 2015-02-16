package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *Returns documentations for a specific type in a schema
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class SchemaDocType extends ServerResource{

	/**
	 * Method represent. Retrieves schema documentation for a specific type within a schema
	 * @return Representation the data in XML form
	 */
	@Get
	public Representation represent() {
		String schemaName = (String) getRequestAttributes().get("name");
		String type = (String) getRequestAttributes().get("type");
		
		String xquery="let $schema:= collection('schemas/"+schemaName+"')"
		+" for $element in $schema/xs:schema/xs:element"
		+" where $element[@name='"+type+"']"
		+" return $element/xs:annotation/xs:documentation";
	
		Representation schemaDoc = new StringRepresentation(BaseX.getXML(xquery), MediaType.APPLICATION_XML);
		this.setStatus(Status.SUCCESS_OK);
		return schemaDoc;
	}
}