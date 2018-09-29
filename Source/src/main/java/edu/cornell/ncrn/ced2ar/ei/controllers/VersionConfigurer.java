package edu.cornell.ncrn.ced2ar.ei.controllers;

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

import edu.cornell.ncrn.ced2ar.eapi.GitCodebook;
import edu.cornell.ncrn.ced2ar.eapi.PDFGenerator;
import edu.cornell.ncrn.ced2ar.eapi.VersionControl;
import edu.cornell.ncrn.ced2ar.web.classes.Loader;

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
	 * 
	 */
	public void clearCodebookCache(Model model){
		logger.debug("Start");
		long start =System.currentTimeMillis();
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
		long end =System.currentTimeMillis();
		logger.debug("End Time in milliseconds: " + (end -start));
		
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
		logger.debug("Start ");
		VersionControl versionControl = VersionControl.getInstance();
		List<GitCodebook> codebooks = null;
		try {
			long start =System.currentTimeMillis();
			codebooks = versionControl.getCodebookStatusInfoE();
			//clearCodebookCache(model); 
			long end  =System.currentTimeMillis();			
			logger.debug("End Time in millis " + (end -start));
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			ex.printStackTrace();
		}
		model.addAttribute("subTitl","Version Control Status");
		model.addAttribute("pageWidth", 1);
		return new ModelAndView("/WEB-INF/editViews/gitStatus.jsp","codebooks",codebooks);
	}
	
	/**
	 * Adds a codebook to BaseX and sets session with appropriate message
	 * @param request
	 * @param response
	 * @return String that redirects to gitStataus Page
	 */
	@RequestMapping(value = "/edit/ingestintobasex", method = RequestMethod.POST)
	 public String ingestCodebook(HttpServletRequest request,HttpServletResponse response,Model model){
		VersionControl versionControl = VersionControl.getInstance();
		int returnValue = 0;
		String codebook = request.getParameter("codebook");
		String version = request.getParameter("version");
		try {
			if(StringUtils.isNotEmpty(codebook) && StringUtils.isNotEmpty(version)) {
				returnValue = versionControl.addCodebookToBaseX(codebook,version,false);
				clearCodebookCache(model);
			}
		}
		catch(Exception ex) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
		}
		if(returnValue == 200) 
			session.setAttribute("info_splash","Codebook " +codebook + " added to BaseX");
		else 
			session.setAttribute("info_splash","There is an error uploading the Codebook  "+ codebook + " into BaseX.  Codebook may be invalid.");
		
		return "redirect:/edit/gitStatus";
	}

	/**
	 * Performs a push to remote action   
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/commitpending", method = RequestMethod.POST)
	 public String commitPendingChanges(HttpServletRequest request,HttpServletResponse response) {
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.commitPendingChanges();
			session.setAttribute("info_splash","Changes are commited to local git.");
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			session.setAttribute("info_splash","There is an error in committing to local git. " + ex.getMessage());
		}
		return "redirect:/edit/gitStatus";
	}

	
	/**
	 * Performs a push to remote action   
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/pushtoremote", method = RequestMethod.POST)
	 public String pushToRemote(HttpServletRequest request,HttpServletResponse response) {
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.pushToRemote();
			session.setAttribute("info_splash","Codebooks from local git are pushed to remote");
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			session.setAttribute("info_splash","There is an error in pushing codebooks to remote git. " + ex.getMessage());
		}
		return "redirect:/edit/gitStatus";
	}
	
	/**
	 * Performs pull from remote action 
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/pullfromremote", method = RequestMethod.POST)
	 public String pullFromRemote(HttpServletRequest request,HttpServletResponse response ,Model model){
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.pullFromRemoteAndSynchWithBaseX();
			session.setAttribute("info_splash","Remote codebooks are pulled into locat git");
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			session.setAttribute("info_splash","There is an error in pulling remote codebooks into local git. Remote Codebook may be invalid. " + ex.getMessage());
		}
		return "redirect:/edit/gitStatus";
	}

	/**
	 * This method merges the conflicted codebook; merging is done preferring remote copy over local.   
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/mergeInFavorOfRemote", method = RequestMethod.POST)
	 public String mergePreferingRemote(HttpServletRequest request,HttpServletResponse respons) {
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.merge();
			session.setAttribute("info_splash","Merge Prefering Remote changes done.");
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			session.setAttribute("info_splash","There is an error in Merging. " + ex.getMessage());
		}
		return "redirect:/edit/gitStatus";
	}

	/**
	 * This method removes the invalid replaces codebook with local copy   
	 * @param request
	 * @param respons
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/removeRemote", method = RequestMethod.POST)
	 public String removeRemote(HttpServletRequest request,HttpServletResponse response){
		try {
			VersionControl versionControl = VersionControl.getInstance();
			versionControl.replaceRemoteCopyWithLocal();
			session.setAttribute("info_splash","Removed remote codebook and added local.");
		} catch (Exception ex ) {
			logger.error("There is an error processing the request: "+ ex.getMessage(),ex);
			session.setAttribute("info_splash","There is an error in removing remote. " + ex.getMessage());
		}
		return "redirect:/edit/gitStatus";
	}
	
}