package edu.cornell.ncrn.ced2ar.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.cornell.ncrn.ced2ar.api.data.Fetch;
import edu.cornell.ncrn.ced2ar.web.classes.Loader;
import edu.cornell.ncrn.ced2ar.web.classes.Parser;
 
/**
 *Handles requests relating to the comparison view
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Compare {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;
  
	@Autowired
	private Loader loader;
	
	/**
	 *Return display page with variables to compare
	 *New method, combination of the old compare and compare2
	 *Now working off session data, not form args
	 * @param model Model
	 * @param session HttpSession
	 * @param print String
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/compare", method = RequestMethod.GET)
	public String compare(Model model, HttpSession session, @RequestParam(value = "print", defaultValue = "n") String print) {
		try{
			//Key is value for variable, value is index of variable that contains that value
			TreeMap<String,List<Integer>>  allValues= new TreeMap<String,List<Integer>>();
			List<String[]>  allHeaders= new ArrayList<String[]>();
			
			ArrayList<String> data = null;
			if(session.getAttribute("compare") != null){
				data = (ArrayList<String>) session.getAttribute("compare");
			}else{
				return "redirect:/";
			}
			
			if(data.size() != 0){			
				//Limit use to be four variable
				//for(int i = 0; i <= 3 && i <= data.length-1; i++){
				for(int i = 0; i <= data.size()-1; i++){
					String d = data.get(i);
					if(d.trim().length() > 0){
						String[] entry = d.split(" ");
						String variableName = entry[1];
						String handle = entry[0];
						String apiURI = loader.getPath() + "/rest/codebooks/"+handle + "/variables/"+variableName;					
						String xml = Fetch.getShortXML(apiURI)[0];
						Parser xp = new Parser(xml);
						
						String path = context.getRealPath("/xsl/compare.xsl");
						Parser xp2 = new Parser(xml, path);
						model.addAttribute("var"+i, xp2.getData());
						
						String codebookName = xp.getValue("/codeBook/titl");
						ArrayList<String> values = xp.getValues("/codeBook/var/catgry/labl");
						for(String v : values){
							List<Integer> newList = null;
							if(allValues.containsKey(v)){
								newList = allValues.get(v);							
							}else{
								newList = new ArrayList<Integer>();
							}
							newList.add(new Integer(i));
							allValues.put(v, newList);  
						}
						allHeaders.add(new String[] {handle, codebookName,variableName,Integer.toString(values.size())});
					}
			}
				
			if(print.equals("y")){
				model.addAttribute("print", true);
			}		
			model.addAttribute("hasMath", true);	
			model.addAttribute("pageWidth", 1);
			model.addAttribute("values",allValues);
			model.addAttribute("headers",allHeaders);
			model.addAttribute("subTitl","Comparing Variables");
			model.addAttribute("metaDesc","Comparison view for multiple variables");
			return "/WEB-INF/views/compare.jsp";
		 }
		}
		catch(ArrayIndexOutOfBoundsException|NullPointerException e){
			e.printStackTrace();
			
		}
		return "redirect:/";
	}
	
//AJAX requests 	

	/**
	 * Generates a compare filter for the sidebar
	 * @param variables variables to add to the filter
	 * @param model the current model
	 * @param session the current session
	 * @return String the page that displays the ajax filter
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/filterCompare", method = RequestMethod.GET)
	public String filterCompare(@RequestParam(value = "cc", defaultValue = "") String[] variables,//variables
		Model model, HttpSession session) {	
		ArrayList<String>  compare = new ArrayList<String>();
		if(session.getAttribute("compare") != null){
			compare = (ArrayList<String>) session.getAttribute("compare");
		}
		if(variables.length != 0){
			for(String variable: variables){
				if(!compare.contains(variable) && !variable.equals(""))
					compare.add(variable);
			 }
		}
		session.setAttribute("compare", compare);
		return "/WEB-INF/ajaxViews/filterCompare.jsp";
	}
	
	/**
	 * Resets the removes a specific variable from the compare filter
	 * @param var the variable to remove
	 * @param model the current model
	 * @param session the current session
	 * @return String the ajax page displaying the updated filter
	 */
	@RequestMapping(value = "/filterCompareRemove", method = RequestMethod.GET)
	public String compareRemove(@RequestParam(value = "rm", defaultValue = "") String var,//var to remove
		Model model, HttpSession session) {
		if(!var.equals("") && session.getAttribute("compare") != null){
			@SuppressWarnings("unchecked")
			ArrayList<String>  compare = (ArrayList<String>) session.getAttribute("compare");
			compare.remove(var);
			session.setAttribute("compare", compare);
		}
		return "/WEB-INF/ajaxViews/filterCompare.jsp";	
	}
	
	/**
	 * Resets the compare filter
	 * @param model the current model
	 * @param session the current session
	 * @return String the ajax page displayed the reset filter
	 */
	@RequestMapping(value = "/filterCompareReset", method = RequestMethod.GET)
	public String compareReset(Model model, HttpSession session) {
		session.removeAttribute("compare");
		return "/WEB-INF/ajaxViews/filterCompare.jsp";
	}
}