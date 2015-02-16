package edu.ncrn.cornell.ced2ar.api.endpoints;

import java.util.Map;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.SearchConstructor;

/** 
 *For the /search endpoint
 *Helps construct args to create SearchConstructor instance
  *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class Search extends ServerResource{
	
	private static final Logger logger = Logger.getLogger(Search.class);

	/**
	 * Method doInit. Overridden method
	 * @throws ResourceException
	 */
	@Override
		public void doInit() throws ResourceException {
	    setNegotiated(true);
	} 
	
	/**
	 * Method represent. Calls query building logic basex on search parameters. Retrieves results of Query in XML, CSV or Json form.
	 * @param variant Variant specifies the return form
	 * @return Representation the XML content returned from the query
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Get("xml|csv|json")
	public Representation represent(Variant variant) {
		
		Representation rep = null;
		
		//Determines if http header 'partial-text' is set to true
		boolean isPartial = false;
		try{
			Series requestHeaders = (Series) getRequest().getAttributes().get("org.restlet.http.headers");
			String partial = requestHeaders.getFirstValue("partial-text");
			isPartial = partial.toLowerCase().equals("true") ? true : false;
		}catch(NullPointerException e){}
		
		
		//Determines if http header 'query-exp' is set to true
		boolean queryExp = false;
		try{
			Series requestHeaders = (Series) getRequest().getAttributes().get("org.restlet.http.headers");
			String partial = requestHeaders.getFirstValue("query-exp");
			isPartial = partial.toLowerCase().equals("true") ? true : false;
		}catch(NullPointerException e){}
		
		
		//Check to make sure correct params are provided before attempting to build query
		Map<String,String> args = getQuery().getValuesMap();
		String invalid = Utilities.validateSearchParams(args);
		if(!invalid.equals(""))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, invalid);

		//Determines content type and defaults to XML if none specified or content type is invalid
		String sType = null;
		MediaType type = variant.getMediaType();
		if (type.equals(MediaType.TEXT_XML) || type.equals(MediaType.APPLICATION_XML)) {
			sType = "xml";
		} else if (type.equals(MediaType.TEXT_CSV)) {
			sType = "csv"; 
		} else if (type.equals( MediaType.APPLICATION_JSON)) {
			sType = "json";
		} else {
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}	
		
		//Constructs Search Instance
		String r  =  getQuery().getFirstValue("return");
		String w =  getQuery().getFirstValue("where");
		String s =  getQuery().getFirstValue("sort");
		String l = getQuery().getFirstValue("limit");
		
		//Throw error if csv type requested for item other than variables
		if(sType.equals("csv") && !r.equals("variables")){
			String message = "CSV type is only supported for variables";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		SearchConstructor search = new SearchConstructor(r, w, s, l, sType, isPartial, queryExp);
		if(!search.valid()){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, search.getError());
		}
		
		String xquery = search.buildXquery();
		if(r.equals("variables")){
			String count = search.getCount();
			//Adds count header
			Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
			if (responseHeaders == null) {
			    responseHeaders = new Series(Header.class);
			    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
			}
			responseHeaders.add(new Header("Count", BaseX.getXML(count)));
		}

		logger.debug("Search API endpoint XQuery:");
		logger.debug(xquery);
		String content = BaseX.getXML(xquery);

		//If content type isn't XML, or CSV, the data is still in XML
		if(!sType.equals("xml") || !sType.equals("csv")){
			if(sType.equals("json")){
				content = Utilities.xmlToJson(content);
			}	
		}
		if(sType.equals("xml")){ 
			content = "<?xml version='1.0' encoding='UTF-8'?>"+content;
		}	
		rep = new StringRepresentation(content,type);
		return rep;	
	}	
}