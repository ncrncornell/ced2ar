package edu.ncrn.cornell.ced2ar.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;




import edu.ncrn.cornell.ced2ar.web.classes.Builder;
import edu.ncrn.cornell.ced2ar.web.classes.Cacher;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;

/**
 *Handles requests for search results page
 *Parse input, calls classes to build xquery and use API
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

@Controller
public class Browse {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Loader loader;
	
	private static final Logger logger = Logger.getLogger(Browse.class);

	 /**
	  * Method browse.
	  * Fetches browsing results alphabetically based off of input from frontend. 
	  * By default, this is every variable that begins with the selected letter, 
	  * sorted by name, showing up to 10 at a time
	  * 
	  * @param a String  - Letter that the selected variables start with
	  * @param pageNumber String - current page that is being browsed
	  * @param sortCol String  - current column that sorting is applied to (null by default)
	  * @param sortDir String - direction which sorting is enable (null by default)
	  * @param show String -Number of variables to show per page
	  * @param session HttpSession
	  * @param model Model
	  * @param response HttpServletResponse
	  * @return String */
    @RequestMapping(value = "/browse", method = RequestMethod.GET)
	 public String browse(@RequestParam(value = "a", defaultValue = "") String a,//letter that variable starts with
		@RequestParam(value = "d-1341904-p", defaultValue = "1") String pageNumber, //page number
		@RequestParam(value = "d-1341904-s", defaultValue = "") String sortCol, //field to sort by 
		@RequestParam(value = "d-1341904-o", defaultValue = "") String sortDir, //direction to sort by
		@RequestParam(value = "s", defaultValue = "") String show, //page number
		HttpSession session, Model model,HttpServletResponse  response) {
    		//Ensures that client is not caching these pages in their browser
	    	response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
	        response.setHeader("Pragma","no-cache");
	        response.setDateHeader("Expires", 0);
	        
		 	model.addAttribute("type","browse");
			model.addAttribute("subTitl","Browsing");
		 	if(!a.equals("") && a.length() == 1){
		 		Builder build = new Builder(pageNumber, show, sortCol, sortDir);
		 		
		 		String apiURI = loader.getPath() + "/rest/";
				apiURI  += "search?return=variables&where=";
				
				String filter = "";
				//Checks if filter is calls builder to construct
				if (session.getAttribute("filter") != null) {
					@SuppressWarnings("unchecked")
					ArrayList<String[]> filters = (ArrayList<String[]>) session.getAttribute("filter");
					filter = build.getFilter(filters);
				}

				String query = "variablename="+a+"*";
				apiURI +=filter + query;
				String limit = build.getLimit();
				model.addAttribute("searched", true);
				
				//Checks to make sure attributes from Query class actually built string
				//Would not build if  URI attributes are invalid or all empty
				Cacher cacher = null;
				try{
					cacher = new Cacher(session);
					int sc = sortCol.equals("") ? -1 : Integer.parseInt(sortCol) -1;//Parsing sort dir to int, and subtracting one to line up with indexing
					boolean rs = sortDir.equals("2") ? false : true;//If sorting is reversed
					List<String[] > data = cacher.fetchFromCache(apiURI,limit,sc,rs);
					int count = cacher.getCount();
					int size = build.getPageSize();
					int rsv = (size * (Integer.parseInt(pageNumber)-1))+1;
					model.addAttribute("rsv", rsv);
					model.addAttribute("count", count);
					model.addAttribute("size", size);
					model.addAttribute("data", data);	
					model.addAttribute("currentLetter", a);	
					model.addAttribute("metaDesc","Browsing variables that start with the letter " + a.toUpperCase());
					
				}catch(NullPointerException | NumberFormatException e){
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					model.addAttribute("error","Error retrieving data");	
					logger.error("Error retrieving data from browse " +e.getMessage());
				}finally{
					//if(cacher != null) cacher.close();
				}
			}
		 return "/WEB-INF/views/view.jsp";
	 }
	 
	 /**
	  * Method browseAll.
	  * Fetches browsing. results based off of input from frontend. 
	  * By default, this is every variable, sorted by name, showing up to 10 at a time
	  * 
	  * @param pageNumber String - current page that is being browsed
	  * @param sortCol String  - current column that sorting is applied to (null by default)
	  * @param sortDir String - direction which sorting is enable (null by default)
	  * @param show String -Number of variables to show per page
	  * @param session HttpSession
	  * @param model Model
	 
	  * @param response HttpServletResponse
	  * @return String */
	 @RequestMapping(value = "/all", method = RequestMethod.GET)
	 public String browseAll(
		//The numeric param names are hashes based of HTML element IDs  
		@RequestParam(value = "d-1341904-p", defaultValue = "1") String pageNumber, //page number.
		@RequestParam(value = "d-1341904-s", defaultValue = "") String sortCol, //field to sort by 
		@RequestParam(value = "d-1341904-o", defaultValue = "") String sortDir, //direction to sort by
		@RequestParam(value = "s", defaultValue = "") String show,//Number of 
		HttpSession session, Model model, HttpServletResponse response) {
			//Ensures that client is not caching these pages in their browser
	    	response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
	        response.setHeader("Pragma","no-cache");
	        response.setDateHeader("Expires", 0);
	        
		 	model.addAttribute("type","all");
		 		 	
	 		Builder build = new Builder(pageNumber, show, sortCol, sortDir);
	 		String apiURI = loader.getPath()+"/rest/search?return=variables";
			
			String filter = "";
			//Checks if filter is calls builder to construct
			if (session.getAttribute("filter") != null) {
				@SuppressWarnings("unchecked")
				ArrayList<String[]> filters = (ArrayList<String[]>) session.getAttribute("filter");
				filter = build.getFilter(filters);
			}
			
			if(!filter.trim().equals("")){
				filter = "&where=" + filter;
			}
			
			apiURI +=filter;
			String limit = build.getLimit();
			//Checks to make sure attributes from Query class actually built string
			//Would not build if  URI attributes are invalid or all empty
			Cacher cacher = null;
			try{
				cacher = new Cacher(session);
				int sc = sortCol.equals("") ? -1 : Integer.parseInt(sortCol) -1;//Parsing sort dir to int, and subtracting one to line up with indexing
				boolean rs = sortDir.equals("2") ? false : true;//If sorting is reversed
				List<String[] > data = cacher.fetchFromCache(apiURI,limit,sc,rs);
				int count = cacher.getCount();
				int size = build.getPageSize();
				int rsv = (size * (Integer.parseInt(pageNumber)-1))+1;
				model.addAttribute("rsv", rsv);
				model.addAttribute("count", count);
				model.addAttribute("size", size);
				model.addAttribute("data", data);	
				model.addAttribute("subTitl","Browsing");
				model.addAttribute("metaDesc","Browsing all variables currently on CED2AR");
			}catch(NullPointerException | NumberFormatException e){
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				model.addAttribute("error","Error retrieving data");	
				logger.error("Error retrieving data from browse all " +e.getMessage());
			}finally{
				//if(cacher != null) cacher.close();
			}
		 	return "/WEB-INF/views/view.jsp";
	 }
}