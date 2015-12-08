package edu.ncrn.cornell.ced2ar.init;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import edu.ncrn.cornell.ced2ar.api.data.ConfigurationProperties;

/**
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
 
public class ConfigProperties implements Serializable {
	private static final long serialVersionUID = -3588355974904923791L;
	private static final Logger logger = Logger.getLogger(ConfigProperties.class);
	
	public static final String DEFAULT_ADMIN_PWD = "";
	public static final String DEFAULT_READER_PWD = "";
	public static final String DEFAULT_WRITER_PWD = "";
	
	public static final String PROPERTY_BASEXDB = "baseXDB";
	public static final String PROPERTY_READER_HASH = "baseXReaderHash";
	public static final String PROPERTY_WRITER_HASH = "baseXWriterHash";
	public static final String PROPERTY_ADMIN_HASH = "baseXAdminHash";

	public static final String PROPERTY_BUG_REPORT_ENABLE= "bugReportEnable";
	public static final String PROPERTY_TIMEOUT= "timeout";
	public static final String PROPERTY_RESTRICTED= "restricted";
	
	public static final String PROPERTY_DEV_FEATURE_PROV = "devFeatureProv";
	public static final String PROPERTY_DEV_FEATURE_GOOGLE_ANALYTICS = "devFeatureGoogleAnalytics";
	public static final String PROPERTY_DEV_FEATURE_EDITING = "devFeatureEditing";
	public static final String PROPERTY_CONFIG_INITIALIZED = "configInitialized";

	public static final String PROPERTY_BUG_REPORT_EMAIL = "bugReportEmail";
	public static final String PROPERTY_BUG_REPORT_SENDER= "bugReportSender";
	public static final String PROPERTY_BUG_REPORT_PWD= "bugReportPwd";
	
	//Flags
	private boolean usingSavedConfig;
	
	private String baseXDB;
	private String baseXReaderHash;
	private String baseXWriterHash;
	private String baseXAdminHash;
 
	private String timeout;
	private boolean restricted;
	
	private boolean devFeatureProv;
	private boolean devFeatureGoogleAnalytics; 
	private boolean devFeatureEditing;
	private boolean configInitialized;		
	
	private String newReaderPassword;
	private String confirmReaderPassword;
	private String newWriterPassword;
	private String confirmWriterPassword;
	private String newAdminPassword;
	private String confirmAdminPassword;
	
	private boolean bugReportEnable;
	private String bugReportEmail;
	private String bugReportSender;
	private String bugReportPwd;
	private String confirmBugReportPwd;

	private boolean keepReaderPassword;
	private boolean keepWriterPassword;
	private boolean keepAdminPassword;

	private Map<String, String> savedPropertiesMap = new HashMap<String,String>();
	private Map<String, String> deployedPropertiesMap = new HashMap<String,String>();
		
	public String getBaseXDB() {
		return baseXDB;
	}
	
	public void setBaseXDB(String baseXDB) {
		this.baseXDB = baseXDB;
	}
	
	public String getBaseXReaderHash() {
		return baseXReaderHash;
	}
	
	public void setBaseXReaderHash(String baseXReaderHash) {
		this.baseXReaderHash = baseXReaderHash;
	}
	
	public String getBaseXWriterHash() {
		return baseXWriterHash;
	}
	
	public void setBaseXWriterHash(String baseXWriterHash) {
		this.baseXWriterHash = baseXWriterHash;
	}
	
	public String getBaseXAdminHash() {
		return baseXAdminHash;
	}
	
	public void setBaseXAdminHash(String baseXAdminHash) {
		this.baseXAdminHash = baseXAdminHash;
	}
	
	public boolean isUsingSavedConfig() {
		return usingSavedConfig;
	}
	
	public void setUsingSavedConfig(boolean usingSavedConfig) {
		this.usingSavedConfig = usingSavedConfig;
	}

	public boolean isKeepReaderPassword() {
		return keepReaderPassword;
	}

	public void setKeepReaderPassword(boolean keepReaderPassword) {
		this.keepReaderPassword = keepReaderPassword;
	}

	public boolean isKeepWriterPassword() {
		return keepWriterPassword;
	}

	public void setKeepWriterPassword(boolean keepWriterPassword) {
		this.keepWriterPassword = keepWriterPassword;
	}

	public boolean isKeepAdminPassword() {
		return keepAdminPassword;
	}

	public void setKeepAdminPassword(boolean keepAdminPassword) {
		this.keepAdminPassword = keepAdminPassword;
	}

	public String getNewReaderPassword() {
		return newReaderPassword;
	}

	public void setNewReaderPassword(String newReaderPassword) {
		this.newReaderPassword = newReaderPassword;
	}

	public String getConfirmReaderPassword() {
		return confirmReaderPassword;
	}

	public void setConfirmReaderPassword(String confirmReaderPassword) {
		this.confirmReaderPassword = confirmReaderPassword;
	}

	public String getNewWriterPassword() {
		return newWriterPassword;
	}

	public void setNewWriterPassword(String newWriterPassword) {
		this.newWriterPassword = newWriterPassword;
	}

	public String getConfirmWriterPassword() {
		return confirmWriterPassword;
	}

	public void setConfirmWriterPassword(String confirmWriterPassword) {
		this.confirmWriterPassword = confirmWriterPassword;
	}

	public String getNewAdminPassword() {
		return newAdminPassword;
	}

	public void setNewAdminPassword(String newAdminPassword) {
		this.newAdminPassword = newAdminPassword;
	}

	public String getConfirmAdminPassword() {
		return confirmAdminPassword;
	}

	public void setConfirmAdminPassword(String confirmAdminPassword) {
		this.confirmAdminPassword = confirmAdminPassword;
	}

	public boolean isDevFeatureProv() {
		return devFeatureProv;
	}
	public void setDevFeatureProv(boolean devFeatureProv) {
		this.devFeatureProv = devFeatureProv;
	}
	public boolean isDevFeatureGoogleAnalytics() {
		return devFeatureGoogleAnalytics;
	}
	public void setDevFeatureGoogleAnalytics(boolean devFeatureGoogleAnalytics) {
		this.devFeatureGoogleAnalytics = devFeatureGoogleAnalytics;
	}
	public boolean isDevFeatureEditing() {
		return devFeatureEditing;
	}
	public void setDevFeatureEditing(boolean devFeatureEditing) {
		this.devFeatureEditing = devFeatureEditing;
	}

	public boolean isRestricted() {
		return restricted;
	}
	
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	
	public boolean isBugReportEnable() {
		return bugReportEnable;
	}
	
	public void setBugReportEnable(boolean bugReportEnable) {
		this.bugReportEnable = bugReportEnable;
	}

	public String getTimeout() {
		return timeout;
	}
	
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	
	public Map<String, String> getSavedPropertiesMap() {
		return savedPropertiesMap;
	}
	
	public void setSavedPropertiesMap(Map<String, String> savedPropertiesMap) {
		this.savedPropertiesMap = savedPropertiesMap;
	}
	
	public String getBugReportEmail() {
		return bugReportEmail;
	}
	
	public void setBugReportEmail(String bugReportEmail) {
		this.bugReportEmail = bugReportEmail;
	}
	
	public String getBugReportSender() {
		return bugReportSender;
	}
	
	public void setBugReportSender(String bugReportSender) {
		this.bugReportSender = bugReportSender;
	}
	
	public String getBugReportPwd() {
		return bugReportPwd;
	}
	
	public void setBugReportPwd(String bugReportPwd) {
		this.bugReportPwd = bugReportPwd;
	}
	
	public String getConfirmBugReportPwd() {
		return confirmBugReportPwd;
	}
	
	public void setConfirmBugReportPwd(String confirmBugReportPwd) {
		this.confirmBugReportPwd = confirmBugReportPwd;
	}
	
	public boolean getConfigInitialized() {
		return configInitialized;
	}
	
	public void setConfigInitialized(boolean configInitialized) {
		this.configInitialized = configInitialized;
	}
	
	public Map<String, String> getDeployedPropertiesMap() {
		return deployedPropertiesMap;
	}
	
	public void setDeployedPropertiesMap(Map<String, String> deployedPropertiesMap) {
		this.deployedPropertiesMap = deployedPropertiesMap;
	}
	
	public void validateCed2arProperties(ValidationContext context) {
		MessageContext messages = context.getMessageContext();

		if(StringUtils.isEmpty(timeout)){
			messages.addMessage(new MessageBuilder().error().defaultText("HTTP Timeout value is required.").build());
		}else {
			String INTEGER_PATTERN = "\\d+";
			Pattern pattern = Pattern.compile(INTEGER_PATTERN);
			Matcher matcher = pattern.matcher(timeout);
			if (!matcher.matches()) 
				messages.addMessage(new MessageBuilder().error().defaultText("HTTP Timeout value must be an integer.").build());
		}
		if(bugReportEnable){
			//private String bugReportPwd;
			String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" 
			+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			if(StringUtils.isEmpty(bugReportEmail)){
				messages.addMessage(new MessageBuilder().error().defaultText("Email to report bugs is required").build());
			}else{
				Matcher matcher = pattern.matcher(bugReportEmail);
				if (!matcher.matches()) 
					messages.addMessage(new MessageBuilder().error().defaultText("Invalid Email to report bugs address").build());
			}
			if(StringUtils.isEmpty(bugReportSender)){
				messages.addMessage(new MessageBuilder().error().defaultText("Bug Reports Sender email is required").build());
			}else{
				Matcher matcher = pattern.matcher(bugReportSender);
				if (!matcher.matches()) 
					messages.addMessage(new MessageBuilder().error().defaultText("Invalid Bug report sender email address").build());
				
			}
			if(StringUtils.isEmpty(bugReportPwd)){
				messages.addMessage(new MessageBuilder().error().defaultText("Bug Report password is required").build());
			}
			if(StringUtils.isEmpty(confirmBugReportPwd)){
				messages.addMessage(new MessageBuilder().error().defaultText("Confirm Bug Report password is required").build());
			}
			if(!confirmBugReportPwd.endsWith(bugReportPwd)){
				messages.addMessage(new MessageBuilder().error().defaultText("Bug Report passwords do not match").build());				
			}		
		}
	}

	/**
	 * Ensure that reader, writer and admin passwords are valid
	 * passwords must not be empty
	 * password and confirm password must match
	 * password must not be a default password
	 * @param context
	 */
	public void validateChangePasswords(ValidationContext context) {
		MessageContext messages = context.getMessageContext();
		
		if(!keepReaderPassword){
			// make sure new and confirm passwords are not empty
			if(StringUtils.isEmpty(newReaderPassword))
				messages.addMessage(new MessageBuilder().error().defaultText("New Reader Password is required.").build());
			if(StringUtils.isEmpty(confirmReaderPassword))
				messages.addMessage(new MessageBuilder().error().defaultText("Confirm Reader Password is required.").build());
			// make sure new and confirm passwords are same
			if(StringUtils.isNotEmpty(newReaderPassword) && StringUtils.isNotEmpty(confirmReaderPassword) ) {
				if(!confirmReaderPassword.equalsIgnoreCase(newReaderPassword))
					messages.addMessage(new MessageBuilder().error().defaultText("New and confirm reader passwords must match.").build());
			}
			// make sure new password is not default passoword
			if(!StringUtils.isEmpty(newReaderPassword)){
				if(newReaderPassword.equalsIgnoreCase(DEFAULT_READER_PWD)) {
					messages.addMessage(new MessageBuilder().error().defaultText("Reader Password is default password. Please use another password.").build());
				}
			}
		}

		if(!keepWriterPassword){
			if(StringUtils.isEmpty(newWriterPassword))
				messages.addMessage(new MessageBuilder().error().defaultText("New Writer Password is required.").build());
			if(StringUtils.isEmpty(confirmWriterPassword))
				messages.addMessage(new MessageBuilder().error().defaultText("Confirm Writer Password is required.").build());
			if(StringUtils.isNotEmpty(newWriterPassword) && StringUtils.isNotEmpty(confirmWriterPassword) ) {
				if(!confirmWriterPassword.equalsIgnoreCase(newWriterPassword))
					messages.addMessage(new MessageBuilder().error().defaultText("New and confirm writer passwords must match.").build());
			}
			if(!StringUtils.isEmpty(newWriterPassword)){
				if(newWriterPassword.equalsIgnoreCase(DEFAULT_WRITER_PWD)) {
					messages.addMessage(new MessageBuilder().error().defaultText("Writer Password is default password. Please use another password.").build());
				}
			}
		}

		if(!keepAdminPassword){
			if(StringUtils.isEmpty(newAdminPassword))
				messages.addMessage(new MessageBuilder().error().defaultText("New Admin Password is required.").build());
			if(StringUtils.isEmpty(confirmAdminPassword))
				messages.addMessage(new MessageBuilder().error().defaultText("Confirm Admin Password is required.").build());

			if(StringUtils.isNotEmpty(newAdminPassword) && StringUtils.isNotEmpty(confirmAdminPassword) ) {
				if(!confirmAdminPassword.equalsIgnoreCase(newAdminPassword))
					messages.addMessage(new MessageBuilder().error().defaultText("New and confirm admin passwords must match.").build());
			}
			if(!StringUtils.isEmpty(newAdminPassword)){
				if(newAdminPassword.equalsIgnoreCase(DEFAULT_ADMIN_PWD)) {
				messages.addMessage(new MessageBuilder().error().defaultText("Admin Password is default password. Please use another password.").build());
				}
			}
		}	
	}
		
	/**
	 * Validates BaseX URL. 
	 * Validates for empty string and url format
	 * @param context
	 */
	 public void validateBaseXConfirm(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        if (this.getBaseXDB() == null || this.getBaseXDB().equals("")) {
            messages.addMessage(new MessageBuilder().error().defaultText("BaseXDB URL is required.").build());
        }
        else {
			String[] schemes = {"http","https"};
			UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_2_SLASHES+ UrlValidator.NO_FRAGMENTS + UrlValidator.ALLOW_LOCAL_URLS);
			if(!urlValidator.isValid(getBaseXDB())){
				messages.addMessage(new MessageBuilder().error().defaultText("Invalid BaseX URL. Please enter valid URL.").build());
			}
        }
    }
	 
	 /**
	  * Validates all three baseX password fields are not empty
	  * @param context
	  */
	 public void validateConfirmBaseXDBPasswords(ValidationContext context) {
		MessageContext messages = context.getMessageContext();
		
		if (StringUtils.isEmpty(getBaseXReaderHash())) {
		    messages.addMessage(new MessageBuilder().error().defaultText("Reader Password is required").build());
		}
		
		if (StringUtils.isEmpty(getBaseXWriterHash())) {
		    messages.addMessage(new MessageBuilder().error().defaultText("Writer Password is required").build());
		}
		
		if (StringUtils.isEmpty(getBaseXAdminHash())) {
		    messages.addMessage(new MessageBuilder().error().defaultText("Admin Password is required").build());
		}
	 }
	 
	 /**
	  * Validates uploaded .properties file.
	  * @param context
	  */
	public void validateUploadSavedConfig(ValidationContext context) {
		 MessageContext messages = context.getMessageContext();
		 CommonsMultipartFile file = (CommonsMultipartFile)context.getUserValue("multipartFileUpload");
		 if(file!=null || file.getFileItem() != null) {
			 String fileName = file.getFileItem().getName();
			 if(StringUtils.isEmpty(fileName) || !fileName.endsWith(".properties")) {
				 messages.addMessage(new MessageBuilder().error().defaultText("Empty file or invalid properties file.").build());
			 }
			 else {
				 InputStream inputStream =null;
				 try {
					inputStream = file.getInputStream();
					ConfigurationProperties cp = new ConfigurationProperties(inputStream);
					Map<String,String> propertiesMap = cp.getPropertiesMap();
					
					if(!propertiesMap.containsKey(PROPERTY_BASEXDB) && !propertiesMap.containsKey(PROPERTY_READER_HASH) &&
						!propertiesMap.containsKey(PROPERTY_WRITER_HASH) &&!propertiesMap.containsKey(PROPERTY_ADMIN_HASH)) {
						messages.addMessage(new MessageBuilder().error().defaultText("Invalid Properties File.").build());
					}
					

				 }catch(Exception ex) {
					 logger.debug("Error reading config file " + ex.getMessage(),ex);
					 messages.addMessage(new MessageBuilder().error().defaultText("Invalid Properties File.").build());
				 } finally {
					try {
						if(inputStream != null) inputStream.close();
					}
					catch(Exception ex) {
						logger.error("Error closing saved config file inputstream. " +ex);
					}
				 }
			 }
		 } else {
			 messages.addMessage(new MessageBuilder().error().defaultText("Properties file is not loaded").build());
		 }
	 }
}