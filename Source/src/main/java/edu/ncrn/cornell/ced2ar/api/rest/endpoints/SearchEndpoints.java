package edu.ncrn.cornell.ced2ar.api.rest.endpoints;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ncrn.cornell.ced2ar.api.rest.queries.SearchData;

/**
 * Endpoint for the new search API
 * 		
 * @author Cornell University, Copyright 2012-2015
 * @author Ben Perry
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */ 
@RestController
public class SearchEndpoints {
	
	private final static Logger logger = Logger.getLogger(SearchEndpoints.class.getName());
	private final SearchData searchData = new SearchData();
	public static final String API_PREFIX = "/rest";//TODO: Better location for this
	
	//Removed JSON return type from search endpoint
	@RequestMapping(value = API_PREFIX+"/search", method = RequestMethod.GET,
	produces={"application/xml", "application/csv", "text/csv"})
	public String getSearch(HttpServletRequest request, HttpServletResponse response,
	@RequestParam(value = "return", defaultValue = "VARIABLES") String r,
	@RequestParam(value = "where", defaultValue = "") String w,
	@RequestParam(value = "search", defaultValue = "") String s,
	@RequestParam(value = "limit", defaultValue = "") String l){
		boolean isPartial = false;
		try{
			isPartial = Boolean.getBoolean(request.getHeader("partial-text"));
		}
		catch(NullPointerException e){}
		
		boolean queryExp = false;
		try{
			queryExp = Boolean.getBoolean(request.getHeader("query-exp"));
		}
		catch(NullPointerException e){}
		
		String sType = "xml";
		try{
			String rawType = request.getHeader("accept").toLowerCase();
			switch(rawType){
				case "text/csv":
					sType = "csv";
				break;
				case "application/csv":
					sType = "csv";
				break;
			}
		}
		catch(NullPointerException e){}
			
		String[] contentResponse = searchData.getSearch(r, w, s, l, sType, isPartial, queryExp);
		
		//An error occurred
		if(contentResponse[0] == null){
			try {
				response.sendError(400);
			} catch (IOException e) {
				logger.debug("error sending error "+e.getMessage());
				e.printStackTrace();
			}
			return contentResponse[1];
		}
		
		String content = contentResponse[0];
		String count = contentResponse[1];
		
		response.addHeader("count", count);
		return content;
	}
}