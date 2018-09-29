package edu.cornell.ncrn.ced2ar.api.rest.queries;

import org.apache.log4j.Logger;

import edu.cornell.ncrn.ced2ar.api.data.BaseX;

/**
 *Makes a request to build an instance of the Search Constructor class
 *TODO: combine with Search Constructor
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class SearchData {
	
	private final static Logger logger = Logger.getLogger(SearchData.class.getName());
	
	/**
	 * Creates an instance of SearchConstructor and performs a search
	 * @param r - return type
	 * @param w - where conditions
	 * @param s - limit conditions
	 * @param l - limit conditions
	 * @param sType - content negotiation type, right now csv or xml are acceptable
	 * @param isPartial - if partial results are given
	 * @param queryExp - if query expansion is turned on
	 * @return
	 */
	//TODO: More robust error and reporting mechanisms. For now, if field[0] for response is null, field[1] has errors
	public String[] getSearch(String r, String w, String s, String l,
	String sType, boolean isPartial, boolean queryExp){
		SearchConstructor search = new SearchConstructor(r, w, s, l, sType, isPartial, queryExp);
		
		if(!search.valid()){
			logger.debug("Invalid API search - " + search.getError());
			return new String[]{null,search.getError()};
		}
		
		String xquery = search.buildXquery();
		
		String content = BaseX.getXML(xquery);
		String count = BaseX.getXML(search.getCount());
	
		return new String[]{content,count};
	}
}