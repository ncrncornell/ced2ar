package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.util.WeakHashMap;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.eapi.Main;

/**
 * Welcome message for root endpoint
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Welcome extends ServerResource{
	
	/**
	 * Welcome message
	 * @return the message
	 */
	@Get
	public Representation represent() {
		WeakHashMap<String,String> pageData = new WeakHashMap<String,String>();
		pageData.put("version", Main.version.toString());
		String welcomeHtml = "<body><h2>Welcome!</h2><p>CED2AR Edit API v1.1</p></body>";
		Representation home = new StringRepresentation(welcomeHtml, MediaType.TEXT_HTML);
		return home;
	}
}