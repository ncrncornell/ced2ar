package edu.ncrn.cornell.ced2ar.web.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.ncrn.cornell.ced2ar.web.form.BaseXDBChangeForm;
import edu.ncrn.cornell.ced2ar.web.form.PasswordChangeForm;
import edu.ncrn.cornell.ced2ar.web.form.PropertiesForm;

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
		 

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['eAPI']", "eAPI.required", "A value is required for eAPI field.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertiesMap['timeout']", "timeout.required", "A value for Timeout is required.");
		  
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

