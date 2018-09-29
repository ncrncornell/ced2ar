package edu.cornell.ncrn.ced2ar.web.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.cornell.ncrn.ced2ar.web.form.BaseXDBChangeForm;
import edu.cornell.ncrn.ced2ar.web.form.PasswordChangeForm;
import edu.cornell.ncrn.ced2ar.web.form.PropertiesForm;

/**
 *Validates config values from property files
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class PropertiesValidator implements Validator{
	
	@Autowired(required=true)
	private HttpServletRequest request;
	
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"  
			   + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String INTEGER_PATTERN = "\\d+";
	
	
	private Pattern pattern;  
	private Matcher matcher; 
	 
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(PropertiesForm.class) || clazz.isAssignableFrom(PasswordChangeForm.class) || clazz.isAssignableFrom(BaseXDBChangeForm.class);
	}
	
	/**
	 * Validates the properties file.
	 * @param obj Object the properties form
	 * @param errors Errors the error object
	 */
	public void validate(Object obj, Errors errors){
		if(!(obj instanceof PropertiesForm)) return;
		
		PropertiesForm form = (PropertiesForm) obj;
		 
		// 
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['eAPI']", "eAPI.required", "A value is required for eAPI field.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['timeout']", "timeout.required", "A value for Timeout is required.");
		
		// UI customization
		// If a UI tab is enabled (checkbox is checked), then make sure there is a value in the corresponding label field.
   	   	if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("uiNavBarBrowseCodebook")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['uiNavBarBrowseCodebookLabel']", "uiNavBarBrowseCodebookLabel.required", "A Label value is required. (Tab is checked.)");

   	   	if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("uiNavBarBrowseStudy")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['uiNavBarBrowseStudyLabel']", "uiNavBarBrowseStudyLabel.required", "A Label value is required. (Tab is checked.)");

   	   	if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("uiNavTabDoc")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['uiNavTabDocLabel']", "uiNavTabDocLabel.required", "A Label value is required. (Tab is checked.)");

   	   	// uiNavTabStdy is ALWAYS true because it is displayed when the /study page is displayed.
   	   	// On line 185, in the configurationProperties.jsp page, the uiNavTabStdy checkbox is set to disabled=true
   	   	// Looks like this checkbox is NOT sent back to the server.
   	   	//    See: "quirk in HTML" on http://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/view.html#view-jsp-formtaglib-checkboxtag
   	   	// Make sure it always has a label.
   	//   	if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("uiNavTabStdy")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['uiNavTabStdyLabel']", "uiNavTabStdyLabel.required", "A Label value is required. (Tab is checked.)");

   	   	if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("uiNavTabFile")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['uiNavTabFileLabel']", "uiNavTabFileLabel.required", "A Label value is required. (Tab is checked.)");

   	   	if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("uiNavTabData")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['uiNavTabDataLabel']", "uiNavTabDataLabel.required", "A Label value is required. (Tab is checked.)");

		if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("uiNavTabOtherMat")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['uiNavTabOtherMatLabel']", "uiNavTabOtherMatLabel.required", "A Label value is required. (Tab is checked.)");

		// Services
   	   	if(StringUtils.isNotBlank((String)form.getPropertiesMap().get("data2ddiSvc")))
   			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['data2ddiUrl']", "data2ddiUrl.required", "A URL is required. (Tab is checked.)");

		// Validate only if the bug report radio button is enabled.
		if(shouldBugReportFieldsValidated(form)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['bugReportEmail']", "bugReportEmail.required", "Valid email address is required.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['bugReportSender']", "bugReportSender.required", "Valid email address is required.");
			
			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher((String)form.getPropertiesMap().get("bugReportEmail"));  
			if (!matcher.matches()) 
			    errors.rejectValue("propertiesMap['bugReportEmail']", "bugReportEmail.invalid",  "Invalid email address");
			matcher = pattern.matcher((String)form.getPropertiesMap().get("bugReportSender"));  
			if (!matcher.matches()) 
			    errors.rejectValue("propertiesMap['bugReportSender']", "bugReportSender.invalid",  "Invalid email address");
		}
		

		pattern = Pattern.compile(INTEGER_PATTERN);
		matcher = pattern.matcher((String)form.getPropertiesMap().get("timeout"));
		if (!matcher.matches()) 
		    errors.rejectValue("propertiesMap['timeout']", "timeout.required",  "Only positive integer values are allowed");  
	}
	
	/**
	 * 
	 * @param form
	 * @return Returns true if the bugreport radio button is checked.
	 */
	private boolean shouldBugReportFieldsValidated(PropertiesForm form ) {
		boolean validate = false;
		validate = ((String)form.getPropertiesMap().get("bugReportEnable")).equalsIgnoreCase("true");
		return validate;
		
		
	}
}

