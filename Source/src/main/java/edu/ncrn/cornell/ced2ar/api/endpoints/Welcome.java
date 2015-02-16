package edu.ncrn.cornell.ced2ar.api.endpoints;

import java.util.HashMap;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.api.Main;

/**
 *Welcome message for root endpoint 
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Welcome extends ServerResource{

	/**
	 * Method represent. Retrieves the welcome message
	 * @return Representation the html welcome message 
	 */
	@Get
	public Representation represent() {
		HashMap<String,String> pageData = new HashMap<String,String>();
		pageData.put("version", Main.version.toString());
		pageData.put("apiUrl", Main.apiUrl);
		String welcomeHtml = "<body><h2>Welcome!</h2><a href='../docs/api'>API Documentation</a></body>";
		Representation home = new StringRepresentation(welcomeHtml, MediaType.TEXT_HTML);
		return home;
	}
}