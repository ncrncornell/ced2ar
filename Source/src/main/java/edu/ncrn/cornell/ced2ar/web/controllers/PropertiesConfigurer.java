package edu.ncrn.cornell.ced2ar.web.controllers;

import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.ConfigurationProperties;
import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.form.BaseXDBChangeForm;
import edu.ncrn.cornell.ced2ar.web.form.PasswordChangeForm;
import edu.ncrn.cornell.ced2ar.web.form.PropertiesForm;
import edu.ncrn.cornell.ced2ar.web.validators.PropertiesValidator;

/**
 *This class manages deployed properties file.
 *Renders the config view to enable editing of properties
 *Renders Change password ajax views for the BaseX admin, reader and writer
 *Access to config screen is allowed only via localhost
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class PropertiesConfigurer {
	@Autowired
	private ServletContext context;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private ConfigurationProperties configurationProperties;
	@Autowired
	PropertiesValidator propertiesValidator;
	@Autowired
	Config config;
	
	private static String EXPORT_PROPERTIES_FILE_NAME="ced2ar.properties";
	/**
	 * Validates the user input before update are made.  
	 * 
	 * @param binder Spring Container Provided Binder that binds the Custom Validator
	 *  
	 */
	@InitBinder
	  protected void initBinder(WebDataBinder binder) {
	    binder.setValidator(propertiesValidator);
	  }
	
	/**
	 * This method allows for download of contents of properties file.
	 * @param response
	 * @return 
	 */
	@RequestMapping(value = "/downloadProperties", method = RequestMethod.GET, produces="text/plain")
	@ResponseBody
	public String downloadProperties(HttpServletResponse response) {
    	String properties =  configurationProperties.getProperties();
		response.setContentType("application/txt");
		response.setHeader("Content-Disposition","attachment; filename=" +EXPORT_PROPERTIES_FILE_NAME);
		return properties;
	}

	/**
	 *	Changes the url of the baseX.  Save the new url and encoded reader, writer and admin password in the .properties file.  
	 *		URL can't be the same as the old one.
	 *		Must have valid admin, reader and writer passwords 
	 * @param request
	 * @param response
	 * @param baseXDBUrl		new baseX url
	 * @param adminPassword		admin password
	 * @param readerPassword	reader password
	 * @param writerPassword	writer password.
	 * @return  Returns he same ajax view with appropriate message
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/processBaseXDBChange", method = RequestMethod.POST)
	 public ModelAndView processBaseXDBChange(HttpServletRequest request,HttpServletResponse response,
			 @RequestParam(value = "baseXDBUrl", defaultValue = "") String baseXDBUrl,
			 @RequestParam(value = "adminPassword", defaultValue = "") String adminPassword,
			 @RequestParam(value = "readerPassword", defaultValue = "") String readerPassword,
			 @RequestParam(value = "writerPassword", defaultValue = "") String writerPassword
			 )throws Exception{
		
			Map<String,String> messages = new WeakHashMap<String,String>();
			
			
			if(StringUtils.isEmpty(baseXDBUrl)){
				messages.put("missingBaseXUrl", "missingBaseXUrl");
			}
			if(StringUtils.isEmpty(adminPassword)){
				messages.put("missingAdminPassword", "missingAdminPassword");
			}
			if(StringUtils.isEmpty(readerPassword)){
				messages.put("missingReaderPassword", "missingReaderPassword");
			}
			if(StringUtils.isEmpty(writerPassword)){
				messages.put("missingWriterPassword", "missingWriterPassword");
			}
			// append backslash at the end url as needed.
			if(StringUtils.isNotEmpty(baseXDBUrl)) 
			if(!baseXDBUrl.substring(baseXDBUrl.length()-1).equalsIgnoreCase("/")){
				baseXDBUrl+="/";
			}
			String[] schemes = {"http","https"};
			UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_2_SLASHES+ UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_LOCAL_URLS);
			if(!urlValidator.isValid(baseXDBUrl)){
				messages.put("invalidBaseXURL", "invalidBaseXURL");
			}
			String currentBasexUrl = config.getBaseXDB();
			if(currentBasexUrl.equalsIgnoreCase(baseXDBUrl)){
				messages.put("baseXDBUrlNotChanged", "baseXDBUrlNotChanged");
			}
			
			
			if(messages.isEmpty()){ //no validation error found. proceed with basex db url change
				String changeBaseXDBUri = getServerPortAndPath(request) +"/" +config.geteAPI() + "/updatebasexdb";
				String encodedAdminCredentials 	=  new String(Base64.encodeBase64(("admin:"+adminPassword).getBytes()));
				String encodedReaderCredentials =  new String(Base64.encodeBase64(("reader:"+readerPassword).getBytes()));
				String writerReaderCredentials 	=  new String(Base64.encodeBase64(("writer:"+writerPassword).getBytes()));
				
				String returnValue="";
				try{
					returnValue = Fetch.changeBaseXDB(changeBaseXDBUri, baseXDBUrl, encodedAdminCredentials, encodedAdminCredentials, writerReaderCredentials);
				}
				catch(Exception ex){
					returnValue=ex.getMessage();
					if(StringUtils.isEmpty(returnValue))
						returnValue+="Error in processing BaseXDB Change.  It may be caused by invalid baseX DB URL.";  
					ex.printStackTrace();
					
				}
				
				if(StringUtils.isEmpty(returnValue)){ // Password is Changed in BaseX.  Now change Password in .properies file
					configurationProperties.addProperty("baseXAdminHash", encodedAdminCredentials);
					configurationProperties.addProperty("baseXReaderHash", encodedReaderCredentials);
					configurationProperties.addProperty("baseXWriterHash", writerReaderCredentials);
					request.getSession().invalidate();
				}
				else{
					messages.put("basexDBNotChanged", returnValue);
				}
			}
			return new ModelAndView("/WEB-INF/ajaxViews/passwordEditMessage.jsp","messages",messages);
	}	
	
	/**
	 * 
	 * Changes the password for bugReporter, BaseX admn, reader and writer
	 * For bugReporter password change, this method updates properties file. 
	 * For BaseX reader, writer and admin password changes, this method updates BaseX repository and properties file. 
	 * BugReporter password is saved a plain text
	 * BaseX passwords are actually save in the format of uid:password (basic authentication format) base64 encoded.
	 * 
	 * @param request  
	 * @param response
	 * @param String baseXDBUserId. Represents the user id of the BaseX whose password is being changed. 
	 * 								If no userId is passed bugreporter password is going to changed. 
	 * @param String currentPassword User entered current password. This is validated against with the current password in the properties file
	 * @param String newPassword	 User entered new password. This will be validated against confirmPassword
	 * @param String confirmPassword User entered confirm password. Should be same as newPassword
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/processPasswordChange", method = RequestMethod.POST)
	 public ModelAndView processPasswordChange(HttpServletRequest request,HttpServletResponse response,
			 @RequestParam(value = "baseXDBUserId", defaultValue = "") String baseXDBUserId,
			 @RequestParam(value = "currentPassword", defaultValue = "") String currentPassword,
			 @RequestParam(value = "newPassword", defaultValue = "") String newPassword,
			 @RequestParam(value = "confirmPassword", defaultValue = "") String confirmPassword, 
			 @RequestParam(value = "baseXDBUrlChange", defaultValue = "false") boolean baseXDBUrlChange,
			 @RequestParam(value = "baseXDBUrl", defaultValue = "") String baseXDBUrl)throws Exception{

		Map<String, String> propertiesMap =  configurationProperties.getPropertiesMap();
		Map<String,String> messages = new WeakHashMap<String,String>();

		if(StringUtils.isNotEmpty(baseXDBUserId)){// Change BaseX Password  
				String originalUid ="";
				String originalPwd ="";
				String originalUidPassword	  ="";
				
				// Extract original userId and Password from properties file with is in the format if uid:password
				if(baseXDBUserId.equals("admin")){
					originalUidPassword	 = propertiesMap.get("baseXAdminHash");
					originalUid 		= originalUidPassword.substring(0,originalUidPassword.indexOf(":"));
					originalPwd 		= originalUidPassword.substring(originalUidPassword.indexOf(":")+1);
				}else if(baseXDBUserId.equals("reader")){
					originalUidPassword	 = propertiesMap.get("baseXReaderHash");
					originalUid 		= originalUidPassword.substring(0,originalUidPassword.indexOf(":"));
					originalPwd 		= originalUidPassword.substring(originalUidPassword.indexOf(":")+1);
				}else if(baseXDBUserId.equals("writer")){
					originalUidPassword	 = propertiesMap.get("baseXWriterHash");
					originalUid 		= originalUidPassword.substring(0,originalUidPassword.indexOf(":"));
					originalPwd 		= originalUidPassword.substring(originalUidPassword.indexOf(":")+1);
				}
				
				// Error conditions checked
				if(!originalPwd.equals(currentPassword)){ 
					messages.put("invalidOriginal", "invalidOriginal");
				}
				if(!newPassword.equals(confirmPassword)){ 
					messages.put("newAndConfirmMismatch", "newAndConfirmMismatch");
				}
				
				config.geteAPI();
				
				
				if(messages.isEmpty()){ //no validation error found. Proceed with baseX password change
					String changePasswordUri = getServerPortAndPath(request) +"/" +
											   config.geteAPI() + "/changebasexpassword";
					String basexUrl = config.getBaseXDB();
					String encodedOldCredentials =  new String(Base64.encodeBase64((originalUidPassword).getBytes()));
					String returnValue="";
					try{
						returnValue = Fetch.changePassword(changePasswordUri,basexUrl, encodedOldCredentials, originalUid, newPassword);
					}
					catch(Exception ex){
						returnValue=ex.getMessage();
						ex.printStackTrace();
					}
					
					if(StringUtils.isEmpty(returnValue)){ // Password is Changed in BaseX.  Now change Password in .properies file 
						String encodedNewUidPwd 	= new String(Base64.encodeBase64((originalUid+":"+newPassword).getBytes()));
						if(baseXDBUserId.equals("admin")){
							configurationProperties.addProperty("baseXAdminHash", encodedNewUidPwd);
						}else if(baseXDBUserId.equals("reader")){
							configurationProperties.addProperty("baseXReaderHash", encodedNewUidPwd);
							
						}else if(baseXDBUserId.equals("writer")){
							configurationProperties.addProperty("baseXWriterHash", encodedNewUidPwd);
						}
						request.getSession().invalidate();
					}
					else{
						messages.put("invalidPassword", "invalidPassword");  
					}
			}
		}
		else{ // There is no userId, so proceed with changing bugReporter password
			String originalBugReporterPassword	 = propertiesMap.get("bugReportPwd");
			if(!originalBugReporterPassword.equals(currentPassword)){
				messages.put("invalidOriginal", "invalidOriginal");
			}
			if(!newPassword.equals(confirmPassword)){
				messages.put("newAndConfirmMismatch", "newAndConfirmMismatch");
			}
			if(messages.isEmpty()){ // No validation errors, proceed with password change in propeties file.
				String encodedBuReporterPwd 	= new String(Base64.encodeBase64((newPassword).getBytes()));
				configurationProperties.addProperty("bugReportPwd", encodedBuReporterPwd);
				request.getSession().invalidate();
			}
		}
		return new ModelAndView("/WEB-INF/ajaxViews/passwordEditMessage.jsp","messages",messages);
	}
	
	/**
	 *	 
	 *	Displays Ajax view of editPassword.
	 *	Edit view gathers current, new and confirm passwords and makes an Ajax call to url /processPasswordChange and displays appropriate messages
	 *	BaseX URL and UserId is passed to the view for BaseX password change.
	 *	BaseX URL and UserId will be empty for the BugReporter Password Change.
	 *	 
	 * @param session
	 * @param request
	 * @param response
	 *   
	 * @return
	 */
	@RequestMapping(value = "/editPassword", method = RequestMethod.GET)
	 public ModelAndView editPassword(HttpSession session, HttpServletRequest request, HttpServletResponse response){
		
		PasswordChangeForm passwordChangeForm = new PasswordChangeForm();
		Map<String, String> propertiesMap = configurationProperties.getPropertiesMap();
		if(propertiesMap.get("baseXAllowChangePasswords").equalsIgnoreCase("true")){
			passwordChangeForm.setBaseXDBUrl(propertiesMap.get("baseXDB"));
			return new ModelAndView("/WEB-INF/ajaxViews/passwordEdit.jsp","passwordChangeForm",passwordChangeForm);
		}
		else{ // Allow change Property is set to false. User are not allowed to change passwords.
			PropertiesForm propertiesForm = new PropertiesForm();
			propertiesForm.setPropertiesMap(propertiesMap);
			return new ModelAndView("/WEB-INF/views/configurationProperties.jsp","propertiesForm",propertiesForm);
		}
	}

	/**
	 *	 
	 *	Displays Ajax view of editPassword.
	 *	Edit view gathers current, new and confirm passwords and makes an Ajax call to 
	 *		url /processPasswordChange and displays appropriate messages
	 *	This method is used for changing BugReporter Password 
	 *	BaseX URL and UserId is passed to the view for BaseX password change.
	 *	BaseX URL and UserId will be empty for the BugReporter Password Change.
	 *	 
	 * @param session
	 * @param request
	 * @param response
	 *   
	 * @return
	 */
	@RequestMapping(value = "/editEmailPassword", method = RequestMethod.GET)
	 public ModelAndView editEmailPassword(HttpSession session, HttpServletRequest request, HttpServletResponse response){
		PasswordChangeForm passwordChangeForm = new PasswordChangeForm();
		passwordChangeForm.setEmailPasswordChange(true);
		return new ModelAndView("/WEB-INF/ajaxViews/passwordEdit.jsp","passwordChangeForm",passwordChangeForm);
	}

	@RequestMapping(value = "/changeBaseXDB", method = RequestMethod.GET)
	 public ModelAndView editURLPasswords(HttpSession session, HttpServletRequest request, HttpServletResponse response){
		BaseXDBChangeForm baseXDBChangeForm = new BaseXDBChangeForm();
		return new ModelAndView("/WEB-INF/ajaxViews/baseXDBChange.jsp","baseXDBChangeForm",baseXDBChangeForm);
	}

	
	/**
	 * Renders the config page that allows users to change properties 
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/config", method = RequestMethod.GET)
	 public ModelAndView config(HttpSession session, HttpServletRequest request,HttpServletResponse response) {
    	PropertiesForm propertiesForm = new PropertiesForm();
    	Map<String, String> propertiesMap =  configurationProperties.getPropertiesMap();
    	propertiesForm.setPropertiesMap(propertiesMap);
    	return new ModelAndView("/WEB-INF/views/configurationProperties.jsp","propertiesForm",propertiesForm);
	 }

	/**
	 * This method updates the properties except Password. This method only makes change to the properties file.
	 * After the properties are updated, session is invalidated so that new values of the properties takes effect 
	 * @param session
	 * @param request
	 * @param response
	 * @param PropertiesForm propertiesForm: container for holding the properties passed from the page  
	 * @param errors
	 * @return
	 * @throws Exception
	 */
    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ModelAndView  updateProperties(HttpSession session, HttpServletRequest request,HttpServletResponse response, @ModelAttribute("propertiesForm") PropertiesForm propertiesForm, BindingResult errors) throws Exception{
    	

    	propertiesValidator.validate(propertiesForm,errors);
    	if(errors.hasErrors()){
       		String devFeatureProv = (String)propertiesForm.getPropertiesMap().get("devFeatureProv");
       		if(StringUtils.isNotBlank(devFeatureProv))
       			propertiesForm.getPropertiesMap().put("devFeatureProv","true");

       		String devFeatureCommentSystem = (String)propertiesForm.getPropertiesMap().get("devFeatureCommentSystem");
       		if(StringUtils.isNotBlank(devFeatureCommentSystem))
       			propertiesForm.getPropertiesMap().put("devFeatureCommentSystem","true");

       		String devFeatureGoogleAnalytics = (String)propertiesForm.getPropertiesMap().get("devFeatureGoogleAnalytics");
       		if(StringUtils.isNotBlank(devFeatureGoogleAnalytics))
       			propertiesForm.getPropertiesMap().put("devFeatureGoogleAnalytics","true");

    		return new ModelAndView("/WEB-INF/views/configurationProperties.jsp","propertiesForm",propertiesForm);
    	}
    	
   		//configurationProperties.addProperty("configLocation", (String)propertiesForm.getPropertiesMap().get("configLocation"));
    	configurationProperties.addProperty("baseXDB", (String)propertiesForm.getPropertiesMap().get("baseXDB"));
   		configurationProperties.addProperty("eAPI", (String)propertiesForm.getPropertiesMap().get("eAPI"));
   		
   		configurationProperties.addProperty("bugReportEnable", (String)propertiesForm.getPropertiesMap().get("bugReportEnable"));
		configurationProperties.addProperty("bugReportEmail", (String)propertiesForm.getPropertiesMap().get("bugReportEmail"));
		configurationProperties.addProperty("bugReportSender", (String)propertiesForm.getPropertiesMap().get("bugReportSender"));
   		
		configurationProperties.addProperty("timeout", (String)propertiesForm.getPropertiesMap().get("timeout"));
		configurationProperties.addProperty("restricted", (String)propertiesForm.getPropertiesMap().get("restricted"));
   		
   		
   		String devFeatureProv = (String)propertiesForm.getPropertiesMap().get("devFeatureProv");
   		if(StringUtils.isNotBlank(devFeatureProv))
   			devFeatureProv="true";
   		else
   			devFeatureProv="false";

   		String devFeatureCommentSystem = (String)propertiesForm.getPropertiesMap().get("devFeatureCommentSystem");
   		if(StringUtils.isNotBlank(devFeatureCommentSystem))
   			devFeatureCommentSystem="true";
   		else
   			devFeatureCommentSystem="false";

   		String devFeatureGoogleAnalytics = (String)propertiesForm.getPropertiesMap().get("devFeatureGoogleAnalytics");
   		if(StringUtils.isNotBlank(devFeatureGoogleAnalytics))
   			devFeatureGoogleAnalytics="true";
   		else
   			devFeatureGoogleAnalytics="false";
   		
   		configurationProperties.addProperty("devFeatureProv", devFeatureProv);
   		configurationProperties.addProperty("devFeatureCommentSystem", devFeatureCommentSystem);
   		configurationProperties.addProperty("devFeatureGoogleAnalytics", devFeatureGoogleAnalytics);

   		propertiesForm.getPropertiesMap().put("devFeatureProv",devFeatureProv);
   		propertiesForm.getPropertiesMap().put("devFeatureCommentSystem",devFeatureCommentSystem);
   		propertiesForm.getPropertiesMap().put("devFeatureGoogleAnalytics",devFeatureGoogleAnalytics);
   		
   		session.invalidate();
   		request.getSession(true).setAttribute("info_splash", "Configuration changes are successfully saved and applied to the application.");
   		
    	return new ModelAndView("/WEB-INF/views/configurationProperties.jsp","propertiesForm",propertiesForm);
    }

    private String getServerPortAndPath(HttpServletRequest request){
		Loader loader = new Loader();	
		return loader.getHostName();
    }
}