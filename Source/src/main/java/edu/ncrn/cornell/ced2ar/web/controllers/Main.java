package edu.ncrn.cornell.ced2ar.web.controllers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ncrn.cornell.ced2ar.web.classes.Loader;

/**
 *Handles requests for the home page and static pages
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Main {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private Loader loader;
	
	/**
	 * Method home.
	 * Redirects to the search page
	 * @return String the search page location
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "redirect:search";
    } 

	/**
	 * Method info.
	 * Shows about CED2AR page
	 * @param model Model
	 * @return String the info page location
	 */
	@RequestMapping(value = "/about", method = RequestMethod.GET)
    public String info(Model model) {
		model.addAttribute("subTitl","About");
		model.addAttribute("metaDesc","Information about the CED2AR project.");
        return "/WEB-INF/views/about.jsp";
    } 

	/**
	 * Method doc.
	 * Shows main documentation page
	 * @param model Model
	 * @return String the documentation page location
	 */
	@RequestMapping(value = "/docs", method = RequestMethod.GET)
    public String doc(Model model) {
		model.addAttribute("subTitl","Documents");
		model.addAttribute("metaDesc","Documentation for the CED2AR project");
        return "/WEB-INF/views/doc.jsp";
    } 
	
	/**
	 * Method api.
	 * Display API documentation
	 * @param session HttpSession
	 * @param model Model
	 * @return String the api documentation page location
	 */
	@RequestMapping(value = "/docs/api", method = RequestMethod.GET)
    public String api(HttpSession session, Model model){
		session.setAttribute("apiUri", "");
		model.addAttribute("subTitl","API");
		model.addAttribute("metaDesc","API documentation for the CED2AR project");
		String[][] crumbs = new String[][] {{"Documentation","docs"},{"API",""}};
		model.addAttribute("crumbs",crumbs);
        return "/WEB-INF/views/api.jsp";
    } 
	
	/**
	 * Method faq.
	 * Displays FAQ page
	 * @param model Model
	 * @return String the faq page location
	 */
	@RequestMapping(value = "/docs/faq", method = RequestMethod.GET)
    public String faq(Model model){
		model.addAttribute("subTitl","FAQ");
		model.addAttribute("metaDesc","FAQs for the CED2AR project");
		String[][] crumbs = new String[][] {{"Documentation","docs"},{"FAQ",""}};
		model.addAttribute("hasMath","true");
		model.addAttribute("crumbs",crumbs);
        return "/WEB-INF/views/faq.jsp";
    } 
	
	/**
	 * Method ddiNCRN.
	 * Shows specs for DDI + NCRN schema
	 * @param model Model
	 * @return String the ddi ncrn page location
	 */
	@RequestMapping(value = "/docs/ddi-ncrn", method = RequestMethod.GET)
    public String ddiNCRN(Model model) {
		model.addAttribute("subTitl","DDI 2.5.1 + NCRN");
		model.addAttribute("metaDesc","DDI 2.5.1 + NCRN specifications");
		String[][] crumbs = new String[][] {{"Documentation","docs"},{"DDI 2.5.1 + NCRN",""}};
		model.addAttribute("crumbs",crumbs);
        return "/WEB-INF/views/ddiNCRN.jsp";
    } 
	
	/**
	 * Method splash.
	 * Redirects to any page and selects codebook for filtering
	 * @param c String codebook to filter 
	 * @param r String request comes from
	 * @param session HttpSession
	 * @return String
	 */
	@RequestMapping(value = "/splash", method = RequestMethod.GET)
    public String splash(@RequestParam(value = "c", defaultValue = "") String c, 
    	@RequestParam(value = "landing", defaultValue = "") String r, HttpSession session) {
		session.setAttribute("info_splash","Filtering for " + c + " active");
		if(!r.equals("")){
			return "filterCodebook?cb="+c+"&r="+r+"&update=1";
		}
        return "filterCodebook?cb="+c+"&r=search&update=1";       
    } 

	/**
	 * Method landing.
	 * Redirects to all variables without splash message*
	 * @param c String the codebook
	 * @return String
	 */
	@RequestMapping(value = "/landing", method = RequestMethod.GET)
    public String landing(@RequestParam(value = "c", defaultValue = "") String c) {
			return "filterCodebook?cb="+c+"&r=all&updateV=1";
    } 
}