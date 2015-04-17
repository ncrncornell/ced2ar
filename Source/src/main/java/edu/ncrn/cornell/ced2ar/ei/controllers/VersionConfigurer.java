package edu.ncrn.cornell.ced2ar.ei.controllers;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.ncrn.cornell.ced2ar.eapi.GitCodebook;
import edu.ncrn.cornell.ced2ar.eapi.PDFGenerator;
import edu.ncrn.cornell.ced2ar.eapi.VersionControl;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;

/**
 * Controller class for codebook version status page that displays local GIT
 * codebooks status with respect to remote GIT codebooks
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

@Controller
public class VersionConfigurer {
	private static final Logger logger = Logger.getLogger(VersionConfigurer.class);
	@Autowired
	private HttpSession session;
	
	@Autowired
	private Loader loader;
	
	/**
	 * Resets session data after codebook list is modified
	 */
	public void clearCodebookCache(Model model){
		String baseURI = loader.getPath() + "/rest/";
		session.removeAttribute("fL");
		session.removeAttribute("filter");
		session.removeAttribute("filterShow");
		session.removeAttribute("filterHeader");
		session.removeAttribute("verboseFilter");
		session.removeAttribute("codebooks");
		session.removeAttribute("searchCache");
		loader.refreshCodebooks(baseURI);	
		TreeMap<String, String[]> codebooks = loader.getCodebooks(baseURI);
		model.addAttribute("codebooks", codebooks);
	}

	/**
	 * Action method that shows statuses of existing local repository 
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/gitStatus", method = RequestMethod.GET)
	 public ModelAndView showCodebookVersionStatus(HttpServletRequest request,HttpServletResponse response, Model model){
		VersionControl versionControl = VersionControl.getInstance();
		List<GitCodebook> codebooks = null;
		try {
			codebooks = versionControl.getCodebookStatusInfo();
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			ex.printStackTrace();
		}
		model.addAttribute("subTitl","Git Status");
		model.addAttribute("pageWidth", 1);
		return new ModelAndView("/WEB-INF/editViews/gitStatus.jsp","codebooks",codebooks);
	}
	
 
	/**
	 * Performs a push to remote action   
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/pushtoremote", method = RequestMethod.POST)
	 public void pushToRemote(HttpServletRequest request,HttpServletResponse response) {
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.pushToRemote();
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			ex.printStackTrace();
		}
	}
	
	/**
	 * Performs pull from remote action 
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/pullfromremote", method = RequestMethod.POST)
	 public void pullFromRemote(HttpServletRequest request,HttpServletResponse response ,Model model){
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.pullFromRemoteAndSynchWithBaseX();
		}
		catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			ex.printStackTrace();
		}
	}

	/**
	 * This method merges the conflicted codebook; merging is done preferring remote copy over local.   
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/merge", method = RequestMethod.POST)
	 public void mergePreferingRemote(HttpServletRequest request,HttpServletResponse respons) {
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.merge();
		}
		catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			ex.printStackTrace();
		}
	}

	/**
	 * This method removes the invalid replaces codebook with local copy   
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/removeRemote", method = RequestMethod.POST)
	 public void removeRemote(HttpServletRequest request,HttpServletResponse response){
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.replaceRemoteCopyWithLocal();
		}
		catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			ex.printStackTrace();
		}
	}
	
	/**
	 * Adds a codebook to BaseX
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/ingestintobasex", method = RequestMethod.POST)
	 public void ingestCodebook(HttpServletRequest request,HttpServletResponse response,Model model){
		VersionControl versionControl = VersionControl.getInstance();
		try{
			String codebook = request.getParameter("codebook");
			String version = request.getParameter("version");
			if(StringUtils.isNotEmpty(codebook) && StringUtils.isNotEmpty(version))
			versionControl.addCodebookToBaseX(codebook,version,false);
			clearCodebookCache(model);
		}catch(IOException ex){
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			ex.printStackTrace();
		}
	
	}
}