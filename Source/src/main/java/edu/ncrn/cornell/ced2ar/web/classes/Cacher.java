package edu.ncrn.cornell.ced2ar.web.classes;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Fetch;

/**
 *Stores recent searches in a users session state rather than make a BaseX call everytime
 *Variable list is in CSV format
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Cacher {
	
	private static final Logger logger = Logger.getLogger(Cacher.class);
	
	private int COUNT = 0;
	private LRUMap CACHE;
	private HttpSession SESSION;
	private int maxSize = 25;
	
	public Cacher(HttpSession s){
		SESSION = s;
		CACHE = (LRUMap) SESSION.getAttribute("searchCache");
		//If cache hasn't been built for a user yet
		if(CACHE == null){
			logger.debug("Cache not set. Building...");
			CACHE = new LRUMap(maxSize);
		}
	}
	
	/**
	 * Makes a call to the API if search not already cached, otherwise, parses cached results and limits to specific page
	 * Results are indexed in the CACHE LRUMap by API url
	 * @param apiURI the location of the data
	 * @param limit the number of items per page
	 * @param sortCol the column index to sort by 0 = variable name, 1 = label, 2 = codebook title
	 * @param isReverse whether or not sorting is reversed by default
	 * @return the data returned from the cache or the database
	 */
	public List<String[]> fetchFromCache(String apiURI, String limit, int sortCol, boolean isReverse){
		String[] fullResults = null;
		
		//Search already cached
		if(CACHE.containsKey(apiURI)){
			logger.debug("Results are cached");
			fullResults = (String[]) CACHE.get(apiURI);		
		}else{
			//Search not yet cached
			logger.debug("Results are NOT cached");
			String[] xml = Fetch.getShortCSV(apiURI);
			
			//Results are empty
			if(xml[1] == null || Integer.parseInt(xml[1]) <= 0){
				//Tests to see if BaseX connection is good, if so cacher caches empty results
				if(BaseX.testConnection())
					CACHE.put(apiURI, xml);
			}else{
				CACHE.put(apiURI, xml);
			}
			SESSION.setAttribute("searchCache", CACHE);
			fullResults = new String[] {xml[0],xml[1]};
		}
		
		Parser xp = new Parser(fullResults[0]);
		COUNT = Integer.parseInt(fullResults[1]);
		
		//Takes entire result set and limits to the specific subset requested
		String[] l = limit.split("-");
		int start = 0;
		int stop = 10;
		try{
			start = Integer.parseInt(l[0]);
			stop = Integer.parseInt(l[1]);		
		}catch(NumberFormatException e){}
		
		List<String[]> results = xp.getDisplayTagDataLimit(start, stop, sortCol, isReverse);

		return results;
	}
	
	/**
	 * Returns total number of variables for a search 
	 * @return the count
	 */
	public int getCount(){
		return COUNT;
	}	
	
	public void close(){
		CACHE.clear();
	}	
}