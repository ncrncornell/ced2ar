package edu.ncrn.cornell.ced2ar.ei.controllers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;

/**
 *Provides support for fetch tooltip from HTML description in schema
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

@Controller
public class Schema {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Loader loader;
	
	/**
	 * Gets help documentation for schema
	 * @param type the type of element for which the schema is needed
	 * @param model the current model
	 * @return the schema description ajax view
	 */
	@Cacheable( value="schemaDocType", key="#type")
	@RequestMapping(value = "/schema/doc/{type}", method = RequestMethod.GET)
    public String getSchemaDoc(@PathVariable(value = "type") String type, Model model) {

		String hostName = loader.getPath();
		String apiURI = hostName + "/rest/schemas/ddi/doc/" + type;
		String xml = Fetch.getXML(apiURI)[0];
		
		//Cleans up namespaces so HTML will render
		//TODO: write in function somewhere
		xml = xml.replace("xhtml:", "");
		xml = xml.replace("xs:", "");
		xml = xml.trim().replaceAll(" +", " ").trim();
		xml = xml.replaceFirst("<documentation.+?>", "<documentation>");
		xml = xml.replaceAll("</samp>", "</p>");
		xml = xml.replaceAll("<samp", "<p");

		String path = context.getRealPath("/xslEdit/schemaDoc.xsl");//Local file path to find XSL doc	
		Parser xp = new Parser(xml,path);
		model.addAttribute("doc",xp.getData());
        return "/WEB-INF/ajaxViews/schemaDoc.jsp";
    }	 
}