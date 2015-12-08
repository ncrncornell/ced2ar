package edu.ncrn.cornell.ced2ar.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.web.classes.Builder;
import edu.ncrn.cornell.ced2ar.web.classes.Cacher;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;
import edu.ncrn.cornell.ced2ar.web.classes.Query;
 
/**
 *Handles requests for search results page
 *Parses input, calls classes to build xquery and use API
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Search {
	
	private static final Logger logger = Logger.getLogger(Search.class);
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;	
	
	@Autowired
	private Loader loader;
	
  /**
   *Handles all search queries
   * @param q all fields
   * @param n variable name
   * @param l variable label
   * @param f full description
   * @param c concept/codebook description
   * @param t variable type
   * @param pageNumber page to show
   * @param sortCol column to sort by. if none specified, b
   * @param sortDir ascending or descending sort
   * @param show number of variables to show
   * @param session HttpSession
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param model Model
   * @return String the results jsp
   */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
	 public String search(@RequestParam(value = "q", defaultValue = "") String q,//all fields
		@RequestParam(value = "n", defaultValue = "") String n,//variable name
		@RequestParam(value = "l", defaultValue = "") String l,//label
		@RequestParam(value = "f", defaultValue = "") String f,//full description
		@RequestParam(value = "c", defaultValue = "") String c,//concept or codebook instructions
		@RequestParam(value = "t", defaultValue = "") String t, //variable type
		@RequestParam(value = "d-1341904-p", defaultValue = "1") String pageNumber, //page number
		@RequestParam(value = "d-1341904-s", defaultValue = "") String sortCol, //field to sort by 
		@RequestParam(value = "d-1341904-o", defaultValue = "") String sortDir, //direction to sort by
		@RequestParam(value = "s", defaultValue = "") String show, //page number
		HttpSession session, HttpServletRequest request,HttpServletResponse response, Model model) throws Exception {
    	
		model.addAttribute("type", "search");
			response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
	        response.setHeader("Pragma","no-cache");
	        response.setDateHeader("Expires", 0);
				
		 	boolean notNull = !q.equals("") || !n.equals("") || !l.equals("") 
			|| !f.equals("") || !c.equals("") || !t.equals("");
		 	
		 	//Ensures one of the search parameters was filled out
		 	if(notNull){
		 		Builder build = new Builder(pageNumber, show, sortCol, sortDir);	
		 		
		 		String apiURI = loader.getPath() + "/rest/search?return=variables&where=";

				String filter = "";
				//Checks if filter is calls builder to construct
				if (session.getAttribute("filter") != null) {
					@SuppressWarnings("unchecked")
					ArrayList<String[]> filters = (ArrayList<String[]>) session.getAttribute("filter");
					filter = build.getFilter(filters);
				}
				String queryOut = "";
				String queryResponse = "";
				
				boolean isAll = q.equals("*") && n.equals("") && l.equals("") 
				&& f.equals("") && c.equals("") && t.equals("");
				
				
			
				if(isAll){
					queryResponse = "*";
				}else{
					//Format query to return in form (what user sees in the search bar)
					Query query = new Query(q,n,l,f,c,t);
					if(!query.getError().equals("NONE")) session.setAttribute("info_splash", query.getError());
					String[] sq = {q,n,l,f,c,t};
					String[] sqi = {"q","n","l","f","c","t"};
					
					queryResponse  = "";
					for(int i = 0; i <=5; i++){
						if(!sq[i].equals("")){
							queryResponse += i == 0 ? sq[i] : sqi[i] + "=" + sq[i];
							queryResponse += " ";
						}
					}
					queryOut = query.getQuery();
				}
				//sorting done on application side now, allows caching to be reused
				apiURI +=filter + queryOut; //"&sort="+build.getSort();
				String limit = build.getLimit();
				model.addAttribute("searched", true);
				
				//Checks to make sure attributes from Query class actually built string
				//Would not build if  URI attributes are invalid or all empty
				if(queryResponse.length() != 0){
					logger.debug("search to URL " + apiURI);
					Cacher cacher = null;
					try{
						//Builds cacher to either make new request or pull from cached result
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
						model.addAttribute("query",queryResponse);	
						model.addAttribute("subTitl"," Search Results - " + queryResponse);
						model.addAttribute("metaDescr"," Search results for query '" + queryResponse +"'");
					}catch(NumberFormatException e){
						logger.warn("Search error");
						logger.warn(e.getMessage().toString());				
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						model.addAttribute("error","Error retrieving data");	
					}finally{
						//if(cacher != null) cacher.close();
					}
				}
			}		 	
		 	return "/WEB-INF/views/view.jsp";
	 }
    
	 /**
	  *Handles all reading queries
	  * @param q String all fields
	  * @param n String variable name
	  * @param l String label
	  * @param f String full desciption
	  * @param c String concent or codebook instructions
	  * @param t String variable type
	  * @param pageNumber String page number
	  * @param session HttpSession
	  * @param response HttpServletResponse
	  * @param request HttpServletRequest
	  * @param model Model
	  * @return String
	  */
	 @RequestMapping(value = "/read", method = RequestMethod.GET)
	 public String read(@RequestParam(value = "q", defaultValue = "") String q,//all fields
		@RequestParam(value = "n", defaultValue = "") String n,//variable name
		@RequestParam(value = "l", defaultValue = "") String l,//label
		@RequestParam(value = "f", defaultValue = "") String f,//full description
		@RequestParam(value = "c", defaultValue = "") String c,//concept or codebook instructions
		@RequestParam(value = "t", defaultValue = "") String t, //variable type
		@RequestParam(value = "p", defaultValue = "1") String pageNumber, //page number
		@RequestParam(value = "d-1341904-s", defaultValue = "") String sortCol, //field to sort by 
		@RequestParam(value = "d-1341904-o", defaultValue = "") String sortDir, //direction to sort by
		HttpSession session, HttpServletResponse response, HttpServletRequest request, Model model) {
	    	response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
	        response.setHeader("Pragma","no-cache");
	        response.setDateHeader("Expires", 0);
		 	model.addAttribute("hasMath", true);
		 	String apiURI = loader.getPath() + "/rest/search?return=variables&where=";
		 	String queryOut = "";
		 	String queryResponse = "";
		    int pn = 1;
		 	try{
				pn =Integer.parseInt(pageNumber);
			}
			catch(NumberFormatException e){}
		 	String pns = Integer.toString(pn) + "-" + Integer.toString(pn);
		 	
		 	boolean notNull = !q.equals("") || !n.equals("") || !l.equals("") 
			|| !f.equals("") || !c.equals("") || !t.equals("");
		 	//Ensures one of the search parameters was filled out
		 	if(notNull){
		 		Builder build = new Builder(pageNumber, "1");
				String filter = "";
				//Checks if filter is callsT* builder to construct
				if (session.getAttribute("filter") != null) {
					@SuppressWarnings("unchecked")
					ArrayList<String[]> filters = (ArrayList<String[]>) session.getAttribute("filter");
					filter = build.getFilter(filters);
				}
				
				boolean isAll = q.equals("*") && n.equals("") && l.equals("") 
				&& f.equals("") && c.equals("") && t.equals("");
				
				if(isAll){
					queryResponse = "*";
				}else{
					//Format query to return in form (what user sees in the search bar)
					Query query = new Query(q,n,l,f,c,t);
					if(!query.getError().equals("NONE")) session.setAttribute("info_splash", query.getError());
					String[] sq = {q,n,l,f,c,t};
					String[] sqi = {"q","n","l","f","c","t"};
					
					queryResponse  = "";
					for(int i = 0; i <=5; i++){
						if(!sq[i].equals("")){
							queryResponse += i == 0 ? sq[i] : sqi[i] + "=" + sq[i];
							queryResponse += " ";
						}
					}
					queryOut = query.getQuery();				
				}
				apiURI +=filter + queryOut;
				//
				if(queryResponse.length() != 0){
					Cacher cacher = null;
					try{
						//Gets varname and handle of current variable 
						cacher = new Cacher(session);
						int sc = sortCol.equals("") ? -1 : Integer.parseInt(sortCol) -1;//Parsing sort dir to int, and subtracting one to line up with indexing
						boolean rs = sortDir.equals("2") ? false : true;//If sorting is reversed
						String[] varInfo = cacher.fetchFromCache(apiURI, pns, sc, rs).get(0);
						int count = cacher.getCount();
						//Fetches from codebook rather than performing search in XML format
						String apiURI2= loader.getPath() + "/rest/codebooks/";
						String handle = varInfo[3]+varInfo[4];
						apiURI2+=handle+"/variables/"+varInfo[0];
						String[] xmlRep = Fetch.getShortXML(apiURI2);	
					
						try{
							String xml = xmlRep[0];				
							xml = xml.replace("xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"", "");
							xml = xml.replace("xhtml:", "");
							
							String path = context.getRealPath("/xsl/read.xsl");//Local file path to find XSL doc		
							Parser xp = new Parser(xml, path);		
							
							model.addAttribute("handle", handle);
							model.addAttribute("varname", varInfo[0]);
							model.addAttribute("data", xp.getData());
							model.addAttribute("query",queryResponse);						
							model.addAttribute("pageNumber", pn);
							model.addAttribute("count", count);
							model.addAttribute("subTitl"," - Reading View");
							model.addAttribute("metaDescr"," Search results for query '" + queryResponse +"'");
						}catch(NumberFormatException e){
							logger.warn("Read error");
							logger.warn(e.getMessage().toString());	
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							model.addAttribute("error","Error retrieving data");
						}
				}finally{
					//if(cacher != null) cacher.close();
				}	
		 	}
		 }
		 return "/WEB-INF/ajaxViews/read.jsp";
	 }
}