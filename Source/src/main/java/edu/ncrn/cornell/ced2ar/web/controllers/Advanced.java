package edu.ncrn.cornell.ced2ar.web.controllers;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.ncrn.cornell.ced2ar.web.classes.AdvancedParse;

/**
 * Handles requests that are from the advanced search gui 
 * and redirects them to search controller found in Search.java search() 
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

@Controller
public class Advanced {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;
	
	/**
	 *This is the controller for the page itself
	 * @param model Model the current model
	 * @return String the advnaced search jsp location
	 */
	@RequestMapping(value = "/advanced_search", method = RequestMethod.GET)
    public String advancedForm(Model model) {
		model.addAttribute("subTitl","Advanced Search");
		model.addAttribute("metaDesc","Advanced search interface for variables");
        return "/WEB-INF/views/advanced.jsp";
    }

	 /**
	  * Method advancedSearch.
	  * Parses input from advanced search and calls search() in Search.java 
	  * Can search the followings fields:  
	  * 	q - All fields
	  * 	n - Variable name
	  * 	l - Label
	  * 	f - Full Description
	  * 	c - Codebook Instructions
	  * 	t - Variable Concept
	  * With the condition that all variables match, at least one variable matches, or no variables match
	  * @param session HttpSession the current session
	  * @param model Model the current model
	  * @return String the redirect back to the search with query parameters properly filled*/
	@RequestMapping(value = "/advanced", method = RequestMethod.GET)
	public String advancedSearch(HttpSession session,Model model){
		ArrayList<String> uriParams = new ArrayList<String>();
		String[] types = {"all", "any","none"};
		String[] params = {"q","n","l","f","c","t"};
		
		for(int i = 0; i <= 5; i++){
			ArrayList<String> typeArgs = new ArrayList<String>();
			for(String type : types){
				String id = type+"-"+Integer.toString(i+1);
				String value =request.getParameter(id);
				if(!value.equals("null") && !value.equals("")){
					String out = AdvancedParse.main(value, type);
					typeArgs.add(out);
				}
			}
			if(typeArgs.size() != 0){
				String joined = StringUtils.join(typeArgs.toArray(),", ");
				uriParams.add(params[i] + "=" + joined);
			}
		} 
		//Must have form filled out
		if(uriParams.isEmpty()){
			return "advanced_search";
		}
		return "redirect:/search?"+StringUtils.join(uriParams.toArray(),"&");
	} 
}