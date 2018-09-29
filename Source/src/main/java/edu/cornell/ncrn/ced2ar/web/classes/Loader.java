package edu.cornell.ncrn.ced2ar.web.classes;

import java.util.Collection;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import edu.cornell.ncrn.ced2ar.api.data.BaseX;
import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.api.data.Fetch;
import edu.cornell.ncrn.ced2ar.api.rest.queries.CodebookData;

/**
 *Class to initialize variables upon startup
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Loader implements InitializingBean{
	
	private static final Logger logger = Logger.getLogger(Loader.class);
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private Config config;
	
	@Autowired
	private HttpSession session;
	
	/**
	 *This is called when the web application starts up. Sets network mode and google analytics.
	 */	
	public void afterPropertiesSet() {					
		//Tests if outside HTTP access is allowed
		//Checks context to avoid duplicate call
		if(context.getAttribute("restricted") == null){
			boolean restricted = config.isRestricted();		
			context.setAttribute("restricted", restricted);	
			if(restricted){
				System.out.println("CED2AR is starting in restricted mode." );
			}else{
				System.out.println("CED2AR is starting in open network mode." );
			}	
		}
		
		//Tests if Google Analytics should be enabled
		if(config.getDevFeatureGoogleAnalytics()){
			context.setAttribute("analytics", true);	
		}		
		
		//Tests if editing UI should be enabled
		if(config.getDevFeatureEditing()){
			context.setAttribute("editing", true);	
		}
		
		//Tests if prov should be enabled
		if(config.getDevFeatureProv()){
			context.setAttribute("prov", true);	
		}
		
		
		//Tests if git should be enabled
		if(config.isGitEnabled()){
			context.setAttribute("git", true);	
		}
		
		//Tests if bug report should be enabled
		if(config.isBugReportEnable()) {
			context.setAttribute("bugReportEnabled", true);
		}
		
		//Tests if Nav Bar item Browse by Codebook should be enabled and sets Label name for item
		if(config.getUiNavBarBrowseCodebook()) {
			context.setAttribute("uiNavBarBrowseCodebook", true);
			context.setAttribute("uiNavBarBrowseCodebookLabel", config.getUiNavBarBrowseCodebookLabel());
		}

		//Tests if Nav Bar item Browse by Study should be enabled and sets Label name for item
		if(config.getUiNavBarBrowseStudy()) {
			context.setAttribute("uiNavBarBrowseStudy", true);
			context.setAttribute("uiNavBarBrowseStudyLabel", config.getUiNavBarBrowseStudyLabel());
		}

	}
	
	/**
	 *Returns if web application has network access
	 * @return boolean whether network is restricted or not
	 */
	public boolean isRestricted(){
		return (boolean) context.getAttribute("restricted");
	}
	
//Server location Functions	
	/**
	 * Returns just the port number of the web application
	 * @return String the build name
	 */
	public String getPort(){
		Config config = Config.getInstance();
		return Integer.toString(config.getPort());
	}
	
	/**
	 *Retrieves the names of the web server, plus the port number, plus the application name
	 * @param context ServletContext the context to the build name from
	 * @return String the host name and build name together
	 */
	public String getPath(){
		return getHostName() + getBuildName();		
	}

	/**
	 * Returns just the name of the server (localhost:80, localhost:8080 etc...)
	 * @param context ServletContext context to get build name from
	 * @return String the host name
	 */
	public String getHostName(){
		String hostName = "http://localhost:8080";
		if((hostName = (String) context.getAttribute("hostName")) == null){
			if(getPort().equals("443")){
				hostName = "https://localhost";
			}else{
				hostName = "http://localhost:"+getPort();
			}
			context.setAttribute("hostName", hostName);
		}
		context.setAttribute("hostName", hostName);
		return hostName;
	}
	
	/**
	 * Returns just the name of the web application (/ced2ar-web)
	 * @param context ServletContext context to obtain the build name from
	 * @return String the build name
	 */
	public String getBuildName(){
		return context.getContextPath();
	}	
	
//Codebook functions		
	/**
	 * Retrieves listing of codebooks
	 * @param baseURI - url of server
	 * @return ArrayList<String[]> the codebooks
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<String,String[]> getCodebooks(String baseURI){
		TreeMap<String,String[]> codebooks = null;
		if((codebooks = (TreeMap<String,String[]>) context.getAttribute("codebooks")) == null){
			refreshCodebooks(baseURI);
			codebooks = (TreeMap<String,String[]>) context.getAttribute("codebooks");
			if(codebooks == null || codebooks.size() == 0){
				String currentError = (String) session.getAttribute("error");
				String connectionError = "Could not connect to BaseX";
				if(BaseX.testConnection())
					connectionError = "CED2AR has no codebooks";
				if(currentError == null){
					currentError = "";
				}else if(currentError.equals(connectionError)){
					return codebooks;
				}else{
					connectionError = "<p>"+connectionError+"</p>";
				}		
				session.setAttribute("error",currentError + connectionError);		
			}
		}
		return codebooks;
	}
	
	/**
	 * Updates list of codebooks
	 * @param baseURI location to retrieve codebooks from
	 */
	public void refreshCodebooks(String baseURI){
		logger.debug("Codebook refresh called.");
		try{
			TreeMap<String, String[]> codebooks = Fetch.getCodebooks(baseURI);
			context.setAttribute("codebooks", codebooks);
			if(config.getDevFeatureProv()){
				//TODO: Add Switch to disable
				//TODO: Why is this even being called?
				//loadProvCodebooks(codebooks);
			}
		}catch(NullPointerException e){}
	}
	
	/**
	 * Loads codebooks into prov if not already present
	 * @param codebooks
	 */

	private void loadProvCodebooks(TreeMap<String, String[]> codebooks){
		for(String codebook[] : codebooks.values()){	
			String baseHandle=codebook[0].replaceAll("\\s+", "");
			String version=codebook[1].replaceAll("\\s+", "");
			String handle=baseHandle+version;
			String uri = getPath() + "/rest/codebooks/"+handle;
			String label = baseHandle +" "+version;
			Fetch.provNode(getHostName(), handle, "0", label, uri, "true","");
		}
	}
	
	/**
	 * Checks to see if codebook exists	
	 * @param handle String the name of the codebook
	 * @return boolean whether or not the codebook exists
	 */
	public boolean hasCodebook(String handle){
		@SuppressWarnings("unchecked")
		TreeMap<String, String[]> codebooks = (TreeMap<String, String[]>) context.getAttribute("codebooks");
		boolean hasCodebook = false;
		handle = handle.toLowerCase();
		if(codebooks != null && codebooks.size() > 0){
			if(codebooks.containsKey(handle))
				return true;
		}else{
			return true;//codebook refresh not called yet
		}
		return hasCodebook;
	}
	
	/**
	 * Tries to see if a handle is a baseHandle, and attempts to locate specific version
	 * @param baseHandle the handle to check
	 * @return the version string if it isn't already part of the handle
	 */
	public String fetchDefault(String baseHandle){		
		baseHandle = baseHandle.toLowerCase();
		String version = "";
		@SuppressWarnings("unchecked")
		Collection<String[]> codebooks = ((TreeMap<String,String[]>) context.getAttribute("codebooks")).values();
		char h1 = baseHandle.charAt(0);
		for (String[] codebook : codebooks){
			if(codebook[0].toLowerCase().equals(baseHandle)){
				version = codebook[1];
				if(codebook[3].equals("default")){
					return version;
				}
			}else{
				//If given handle is greater than the current handle in loop,
				//then return since list is alphabetical
				char h2 = codebook[0].charAt(0);
				if(h1 < h2){
					return version;
				}
			}
		}
		return version;
	}


	/**
	 * Retrieves listing of codebook studies
	 * @param baseURI - url of server
	 * @return ArrayList<String[]> the studies
	 */	
	@SuppressWarnings("unchecked")
	public TreeMap<String,String[]> getStudies(String baseURI){
		/**
		 * FYI: This method is a copy of the getCodebooks above.
		 * 
		 * Having a hard time tracking down error "CED2AR has no codebook studies".
		 * Added a lot of debug statements to help track down issue.
		 */
		logger.debug("getStudies() called");
		TreeMap<String,String[]> studies = null;
		if((studies = (TreeMap<String,String[]>) context.getAttribute("studies")) == null){
			logger.debug("getStudies() 1: studies attribute == null");
			refreshStudies(baseURI);
			studies = (TreeMap<String,String[]>) context.getAttribute("studies");
			logger.debug("getStudies() 2: after refreshStudies size: " + studies.size());
			if(studies == null || studies.size() == 0){
				String currentError = (String) session.getAttribute("error");
				String connectionError = "Could not connect to BaseX";
				
				if(currentError == null) {
					logger.debug("getStudies() 3: currentError == null");
				}else{
					logger.debug("getStudies() 3a: currentError: " + currentError);
				}
				logger.debug("getStudies() 3: connectionError: "+ connectionError);
				
				/**
				 * This logic here seems a little strange.  I THINK they were trying to say:
				 *   IF the studies (codebooks) list is empty AND we can connect to BaseX, 
				 *   THEN that means that there are no studies in BaseX.
				 *   
				 * The initial 2.9.0 (and 2.8.3) version was returning the error
				 * "CED2AR has no codebook studies".  That's why all debug lines are here.
				 */
				if(BaseX.testConnection()) {
					logger.debug("getStudies() 4: connectionError: "+ connectionError);
					connectionError = "CED2AR has no codebook studies";
					logger.debug("getStudies() 4a: connectionError: "+ connectionError);
				}else{
					logger.warn("getStudies() testConnection() returned false.  Could not connect to BaseX");					
					logger.debug("getStudies() 4b: connectionError: "+ connectionError);
				}
				
				if(currentError == null){
					currentError = "";
					logger.debug("getStudies() 5: currentError == null  currentError set to empty string");
				}else if(currentError.equals(connectionError)){
					logger.debug("getStudies() 5a: currentError.equals(connectionError)  returning studies");
					return studies;
				}else{
					connectionError = "<p>"+connectionError+"</p>";
					logger.debug("getStudies() 5b: connectionError: " + connectionError);
				}		
				session.setAttribute("error",currentError + connectionError);	
				logger.debug("getStudies() 6: error attribute: " + currentError + connectionError);
			}
		}
		logger.debug("getStudies() return studies size: " + studies.size());
		return studies;
	}

	/**
	 * Updates list of codebook studies
	 * @param baseURI location to retrieve codebook studies from
	 */
	public void refreshStudies(String baseURI){
		logger.debug("refreshStudies() called");
		try{
			TreeMap<String, String[]> studies = Fetch.getStudies(baseURI);
			context.setAttribute("studies", studies);
			logger.debug("refreshStudies(): refreshed studies size: " + studies.size());
			if(config.getDevFeatureProv()){
				//TODO: Add Switch to disable
				//TODO: Why is this even being called?
				//loadProvCodebooks(codebooks);
			}
		}catch(NullPointerException e){}
	}
	
}