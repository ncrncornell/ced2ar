package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.representation.Variant;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *For the /prov endpoint
 *Retrieves prov information
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Prov extends ServerResource{
	
	//?command=store to prov.json http://ncrn.cornell.edu/docs/cdr/misc/prov.json
	@Get("json")
	public Representation represent(Variant variant){
		String json = BaseX.httpGet("/rest/prov/", "prov.json");
		Representation rep = new StringRepresentation(json, MediaType.APPLICATION_JSON);
		return rep;
	}
}