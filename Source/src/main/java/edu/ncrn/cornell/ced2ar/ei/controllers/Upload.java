package edu.ncrn.cornell.ced2ar.ei.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.api.data.FileUpload;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;

/**
 * Class to handle codebook uploads
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Upload {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Loader loader;
	
//Utilities
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

//Endpoints	
	/**
	 * uploads a new codebook
	 * @param uploadForm the file upload object
	 * @param handle the handle of the codebook being uploaded
	 * @param version the version of the codebook
	 * @param shortName the short name of the codebook
	 * @param fullName the full name of the codebook
	 * @param model the current model
	 * @param session the current session
	 * @return redirect
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/edit/add/codebook", method = RequestMethod.POST)
	public String uploadNewCodebook(@ModelAttribute("f") FileUpload uploadForm, 
	@RequestParam("handle") String handle, 
	@RequestParam("version") String version, 
	@RequestParam(value = "label", defaultValue = "") String label,
	Model model, HttpSession session) {
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			try{			
				String baseURI = loader.getPath() + "/rest/";
				codebooks = loader.getCodebooks(baseURI);
			}catch(Exception e){
				model.addAttribute("error","Could not establish a connection to the database");	
				return "redirect:/edit";
			}
		}else{
			codebooks = (TreeMap<String,String[]>) session.getAttribute("codebooks");
		}
		
		//Checks to make sure codebook does not exist already
		if(codebooks != null){   // if there are no codebooks, no need to check for the existance.
			if(codebooks.containsKey(handle+version)){
				session.setAttribute("error", "A codebook with that handle and version already exists");
				return "redirect:/edit";
			}		
		}
		
		if(handle.equals("")){
			session.setAttribute("error", "A handle is required");
			return "redirect:/edit";
		}
		handle = handle.trim();
		
		if(handle.equals("")){
			session.setAttribute("error", "A label is required");
			return "redirect:/edit";
		}
		label.trim();
		
		if(!handle.matches("^[a-zA-Z0-9\\-]*$")){
			session.setAttribute("error", "A handle can only contain alphanumeric characters, or hyphens");
			return "redirect:/edit";
		}
		
		if(!label.matches("^[a-zA-Z0-9\\- ]*$")){
			session.setAttribute("error", "A label can only contain alphanumeric characters, hyphens, or spaces");
			return "redirect:/edit";
		}
		
		if(uploadForm == null){
			session.setAttribute("error", "No file uploaded");
			return "redirect:/edit";
		}
		
		try {
			MultipartFile file = uploadForm.getFile();	
			InputStream ins = file.getInputStream();
			String host = loader.getHostName();
			String rep = Fetch.uploadCodebook(host, ins, handle,version, label);
			if(rep.equals("")){
				String message = "Upload complete, "+handle+" ("+version+") was sucessfully added.&nbsp;"
						+"<a href='"+loader.getBuildName()+"/codebooks/"+handle+"/v/"+version+"'>View codebook</a>";
				session.setAttribute("info_splash", message);
				session.removeAttribute("error");
				clearCodebookCache(model);		
			}else{
				String path = context.getRealPath("/xsl/apiError.xsl");//Local file path to find XSL doc
				Parser xp = new Parser(rep,path,1);
				String error = xp.getData();
				//error will appear as "message" is an http directly from BaseX, not API
				if(error == null || error.trim().startsWith("message")){
					session.setAttribute("error", "An error has occured while attemtping to upload this codebook. The XML may be malformed.");						
				}else{
					session.setAttribute("error", xp.getData());
				}			
			}		
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return "redirect:/edit";
	}
	
	/**
	 * updates an existing codebook
	 * @param uploadForm the file upload object
	 * @param handle the codebook being updated
	 * @param model the current model
	 * @param session the current session
	 * @return redirect
	 */
	@RequestMapping(value = "/edit/update/codebook", method = RequestMethod.POST)
	public String updateCodebook(@ModelAttribute("f") FileUpload uploadForm, 
	@RequestParam("handle") String handle, Model model, HttpSession session) {
		
		if(handle.equals("")){
			session.setAttribute("error", "Please select a codebook to modify");
			return "redirect:/edit";
		}
		String[] handleInfo = handle.split("\\.");
		if(uploadForm == null){
			session.setAttribute("error", "No file uploaded");
			return "redirect:/edit";
		}		
		InputStream ins = null;
		try {
			MultipartFile file = uploadForm.getFile();			
			ins = file.getInputStream();
			String host = loader.getHostName();
			String rep = Fetch.uploadCodebook(host,ins, handleInfo[0], handleInfo[1]);
			if(rep.equals("")){
				String message = "Upload complete, "+handleInfo[0]+" ("+handleInfo[1]+") was sucessfully modified. &nbsp;"
				+"<a href='"+loader.getBuildName()+"/codebooks/"+handleInfo[0]+"/v/"+handleInfo[1]+"'>View codebook</a>";
				session.setAttribute("info_splash", message);
				session.removeAttribute("error");
				clearCodebookCache(model);	
			}else{
					String path = context.getRealPath("/xsl/apiError.xsl");//Local file path to find XSL doc
					Parser xp = new Parser(rep,path, 1);
					String error = xp.getData();
					//error will appear as "message" is an http directly from BaseX, not API
					if(error == null || error.trim().startsWith("message")){
						session.setAttribute("error", "An error has occured while attemtping to upload this codebook. The XML may be malformed.");						
					}else{	
						session.setAttribute("error", xp.getData());
					}			
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				ins.close();
			} catch (IOException e) {};
		}	
		return "redirect:/edit/codebooks#t2";
	}
	
	@RequestMapping(value = "/edit/delete/codebook", method = RequestMethod.POST)
	public String deleteCodebook(@RequestParam("handle") String handle, Model model, HttpSession session) {
		if(handle.equals("")){
			session.setAttribute("error", "Please select a codebook to modify");
			return "redirect:/edit/codebooks#t3";
		}
		String[] handleInfo = handle.split("\\.");
		
		String host = loader.getHostName();
		String rep = Fetch.deleteCodebook(host,handleInfo[0], handleInfo[1]);
		if(rep.equals("")){
			session.setAttribute("info_splash", "Deleted "+handleInfo[0]+ " (" + handleInfo[1]+ ")");
			session.removeAttribute("error");
			clearCodebookCache(model);
		}else{
				String path = context.getRealPath("/xsl/apiError.xsl");//Local file path to find XSL doc
				Parser xp = new Parser(rep,path, 1);
				String error = xp.getData();
				//error will appear as "message" is an http directly from BaseX, not API
				if(error == null || error.trim().startsWith("message")){
					session.setAttribute("error", "An error has occured while attemtping to delete "+handleInfo[0]+handleInfo[1]);						
				}else{	
					session.setAttribute("error", xp.getData());
				}			
		}					
		return "redirect:/edit/codebooks#t3";
	}
	
	/**
	 * Changes the default version for a codebook
	 * @param d
	 * @return
	 */
	@RequestMapping(value = "/edit/index", method = RequestMethod.POST)
	@ResponseBody
	public int indexSettings(Model model, @RequestParam("default") String d) {
		if(d == null || d.equals(""))
			return 400;
		String[] handleInfo = d.split("\\.");
		String baseHandle = handleInfo[0];
		String version = handleInfo[1];
		String baseURI = loader.getHostName();
		Fetch.setDefaultCodebook(baseURI, baseHandle, version);
		clearCodebookCache(model);
		return 200;
	}
	
	/**
	 * Refreshes current list of codebooks 
	 * @return redirect
	 */
	@RequestMapping(value = "/update/refresh", method = RequestMethod.GET)
	public String updateCodebook(Model model){
		clearCodebookCache(model);
		session.setAttribute("info_splash", "Codebook cache refreshed");
		return "redirect:/";
	}	
}