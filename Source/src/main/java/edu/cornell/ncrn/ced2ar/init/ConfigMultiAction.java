package edu.cornell.ncrn.ced2ar.init;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.cornell.ncrn.ced2ar.api.data.BaseX;
import edu.cornell.ncrn.ced2ar.api.data.ConfigurationProperties;

public class ConfigMultiAction extends MultiAction {
	private static final Logger logger = Logger.getLogger(ConfigMultiAction.class);

	/**
	 * This method validates baseX connection.
	 * 
	 * @param configProperties
	 * @param context
	 * @return success or error
	 */
	public Event doCheckBaseXUsingDeployedConfig(ConfigProperties configProperties, RequestContext context) {
		setDeployedConfigurationProperties(configProperties);
		setCed2arProperties(configProperties, configProperties.getDeployedPropertiesMap());
		if (checkBaseXConnection(configProperties, false)) {
			logger.debug("Found valid BaseX passwords.");
			return success();
		} else {
			context.getMessageContext().addMessage(new MessageBuilder().error()
					.defaultText("Unable to connect to BaseX DB. Please check the URL").build());
			return error();
		}
	}

	/**
	 * This method validates baseX connection.
	 * 
	 * @param configProperties
	 * @param context
	 * @return success or error
	 */
	public Event doCheckBaseXUsingSavedConfig(ConfigProperties configProperties, RequestContext context) {

		if (checkBaseXConnection(configProperties, true)) {
			logger.debug("Found valid BaseX passwords.");
			return success();
		} else {
			context.getMessageContext().addMessage(new MessageBuilder().error()
					.defaultText("Unable to connect to BaseX DB. Please check the URL").build());
			return error();
		}
	}

	/**
	 * This method validates baseX connection.
	 * 
	 * @param configProperties
	 * @param context
	 * @return success or error
	 */
	public Event doCheckBaseXUsingUserEnteredValue(ConfigProperties configProperties, RequestContext context) {
		String userEnteredBaseXDBUrl = configProperties.getBaseXDB();
		if (BaseX.checkBaseX(userEnteredBaseXDBUrl)) {
			updateConfigProperty(ConfigProperties.PROPERTY_BASEXDB, userEnteredBaseXDBUrl, context);
			logger.debug("Found valid BaseX passwords.");
			return success();
		} else {
			context.getMessageContext().addMessage(new MessageBuilder().error()
					.defaultText("Unable to connect to BaseX DB. Please check the URL").build());
			return error();
		}
	}

	public Event doCheckBaseXPasswordsUsingUserEnteredValues(ConfigProperties configProperties,
			RequestContext context) {
		if (StringUtils.isEmpty(configProperties.getBaseXReaderHash())) {
			String userEnteredReaderPwd = configProperties.getNewReaderPassword();
			String userEnteredReaderCreds = new String(
					Base64.encodeBase64(("reader:" + userEnteredReaderPwd).getBytes()));
			boolean connected = BaseX.validateConnection(configProperties.getBaseXDB(), userEnteredReaderCreds);
			if (connected) {
				logger.debug("User Entered Reader Password is valid");
				configProperties.setBaseXReaderHash(userEnteredReaderCreds);
				updateConfigProperty(configProperties.PROPERTY_READER_HASH, userEnteredReaderCreds, context);

			} else {
				logger.debug("User Entered Reader Password is Invalid");
				context.getMessageContext()
						.addMessage(new MessageBuilder().error().defaultText("Invalid Reader Password.").build());
			}
		}

		if (StringUtils.isEmpty(configProperties.getBaseXWriterHash())) {
			String userEnteredWriterPwd = configProperties.getNewWriterPassword();
			String userEnteredWriterCreds = new String(
					Base64.encodeBase64(("writer:" + userEnteredWriterPwd).getBytes()));
			boolean connected = BaseX.validateConnection(configProperties.getBaseXDB(), userEnteredWriterCreds);

			if (connected) {
				logger.debug("User Entered Writer Password is valid");
				configProperties.setBaseXWriterHash(userEnteredWriterCreds);
				updateConfigProperty(configProperties.PROPERTY_WRITER_HASH, userEnteredWriterCreds, context);
			} else {
				logger.debug("User Entered Writer Password is Invalid");
				context.getMessageContext()
						.addMessage(new MessageBuilder().error().defaultText("Invalid Writer Password.").build());
			}
		}

		if (StringUtils.isEmpty(configProperties.getBaseXAdminHash())) {
			String userEnteredAdminPwd = configProperties.getNewAdminPassword();
			String userEnteredAdminCreds = new String(Base64.encodeBase64(("admin:" + userEnteredAdminPwd).getBytes()));
			boolean connected = BaseX.validateConnection(configProperties.getBaseXDB(), userEnteredAdminCreds);

			if (connected) {
				logger.debug("User Entered Admin Password is valid");
				configProperties.setBaseXAdminHash(userEnteredAdminCreds);
				updateConfigProperty(configProperties.PROPERTY_ADMIN_HASH, userEnteredAdminCreds, context);

			} else {
				logger.debug("User Entered Admin Password is Invalid");
				context.getMessageContext()
						.addMessage(new MessageBuilder().error().defaultText("Invalid Admin Password.").build());
			}
		}

		if (context.getMessageContext().getAllMessages().length > 0) {
			return error();
		} else {
			configProperties.setKeepReaderPassword(true);
			configProperties.setKeepWriterPassword(true);
			configProperties.setKeepAdminPassword(true);
			return success();
		}
	}

	/**
	 * This method checks to see if the passwords are valid. Passwords are
	 * either read from saved or default config. If saved config is used and
	 * password is not valid, default password is tried. Flags are set if
	 * default passwords are used. These flags will force users to change
	 * default pwd
	 * 
	 * @param configProperties
	 * @param useSavedConfig
	 * @return
	 */
	public Event doCheckBaseXPasswords(ConfigProperties configProperties, RequestContext context) {
		logger.info("Start checkBaseXPasswords");
		boolean useSavedConfig = (configProperties.getSavedPropertiesMap() != null
				&& configProperties.getSavedPropertiesMap().size() > 0);
		if (useSavedConfig) {
			// Reader PWD
			String readerCredsFromSaved = configProperties.getSavedPropertiesMap()
					.get(ConfigProperties.PROPERTY_READER_HASH);
			readerCredsFromSaved = new String(Base64.encodeBase64((readerCredsFromSaved).getBytes()));
			logger.debug("Check validity of reader pwd from Saved Properties file.");
			boolean connected = BaseX.validateConnection(configProperties.getBaseXDB(), readerCredsFromSaved);
			if (connected) {
				logger.debug("Reader Password from Saved config file is valid");
				configProperties.setBaseXReaderHash(readerCredsFromSaved);
			} else {
				configProperties.setBaseXReaderHash("");
				logger.debug("Reader Password from Saved config file is invalid.");
			}

			// Writer PWD
			String writerCredsFromSaved = configProperties.getSavedPropertiesMap()
					.get(ConfigProperties.PROPERTY_WRITER_HASH);
			writerCredsFromSaved = new String(Base64.encodeBase64((writerCredsFromSaved).getBytes()));
			logger.debug("Check validity of writer pwd from Saved Properties file.");
			connected = BaseX.validateConnection(configProperties.getBaseXDB(), writerCredsFromSaved);
			if (connected) {
				logger.debug("Writer Password from Saved config file is valid");
				configProperties.setBaseXWriterHash(writerCredsFromSaved);
			} else {
				configProperties.setBaseXWriterHash("");
				logger.debug("Writer Password from Saved config file is invalid.");
			}

			// Admin PWD
			String adminCredsFromSaved = configProperties.getSavedPropertiesMap()
					.get(ConfigProperties.PROPERTY_ADMIN_HASH);
			adminCredsFromSaved = new String(Base64.encodeBase64((adminCredsFromSaved).getBytes()));
			logger.debug("Check validity of admin pwd from Saved Properties file.");
			connected = BaseX.validateConnection(configProperties.getBaseXDB(), adminCredsFromSaved);
			if (connected) {
				logger.debug("Admin Password from Saved config file is valid");
				configProperties.setBaseXWriterHash(adminCredsFromSaved);
			} else {
				configProperties.setBaseXWriterHash("");
				logger.debug("Admin Password from Saved config file is invalid.");
			}
		}

		if (useSavedConfig) {
			if (StringUtils.isNotEmpty(configProperties.getBaseXReaderHash())
					&& StringUtils.isNotEmpty(configProperties.getBaseXWriterHash())
					&& StringUtils.isNotEmpty(configProperties.getBaseXAdminHash())) {
				logger.debug("All Passwords from Saved config file are valid.");
				configProperties.setKeepReaderPassword(true);
				configProperties.setKeepWriterPassword(true);
				configProperties.setKeepAdminPassword(true);
				return success();
			}
		}

		// Deployed

		String readerCredsFromDeployed = configProperties.getDeployedPropertiesMap()
				.get(ConfigProperties.PROPERTY_READER_HASH);
		readerCredsFromDeployed = new String(Base64.encodeBase64((readerCredsFromDeployed).getBytes()));
		logger.debug("Check validity of reader pwd from Deployed Properties file.");
		boolean connected = BaseX.validateConnection(configProperties.getBaseXDB(), readerCredsFromDeployed);
		if (connected) {
			logger.debug("Reader Password from Deployed config file is valid");
			configProperties.setBaseXReaderHash(readerCredsFromDeployed);
		} else {
			configProperties.setBaseXReaderHash("");
			logger.debug("Reader Password from Deployed config file is invalid.");
		}

		String writerCredsFromDeployed = configProperties.getDeployedPropertiesMap()
				.get(ConfigProperties.PROPERTY_WRITER_HASH);
		writerCredsFromDeployed = new String(Base64.encodeBase64((writerCredsFromDeployed).getBytes()));
		logger.debug("Check validity of writer pwd from Deployed Properties file.");
		connected = BaseX.validateConnection(configProperties.getBaseXDB(), writerCredsFromDeployed);
		if (connected) {
			logger.debug("Writer Password from Deployed config file is valid");
			configProperties.setBaseXWriterHash(writerCredsFromDeployed);
		} else {
			configProperties.setBaseXWriterHash("");
			logger.debug("Writer Password from Deployed config file is invalid.");
		}

		String adminCredsFromDeployed = configProperties.getDeployedPropertiesMap()
				.get(ConfigProperties.PROPERTY_ADMIN_HASH);
		adminCredsFromDeployed = new String(Base64.encodeBase64((adminCredsFromDeployed).getBytes()));
		logger.debug("Check validity of admin pwd from Default Properties file.");
		connected = BaseX.validateConnection(configProperties.getBaseXDB(), adminCredsFromDeployed);
		if (connected) {
			logger.debug("Admin Password from Deployed config file is valid");
			configProperties.setBaseXAdminHash(adminCredsFromDeployed);
		} else {
			configProperties.setBaseXAdminHash("");
			logger.debug("Admin Password from Deployed config file is invalid.");
		}

		if (StringUtils.isNotEmpty(configProperties.getBaseXReaderHash())
				&& StringUtils.isNotEmpty(configProperties.getBaseXWriterHash())
				&& StringUtils.isNotEmpty(configProperties.getBaseXAdminHash())) {
			logger.debug("All Passwords from Saved config file are valid.");
			configProperties.setKeepReaderPassword(true);
			configProperties.setKeepWriterPassword(true);
			configProperties.setKeepAdminPassword(true);
			return success();
		} else {
			StringBuffer message = new StringBuffer("Invalid ");
			int i = 0;
			if (StringUtils.isEmpty(configProperties.getBaseXReaderHash())) {
				message.append("Reader");
				i++;
			}
			if (StringUtils.isEmpty(configProperties.getBaseXWriterHash())) {
				if (i > 0)
					message.append(", Writer");
				else
					message.append("Writer");
				i++;
			}
			if (StringUtils.isEmpty(configProperties.getBaseXAdminHash())) {
				if (i > 0)
					message.append(", Admin");
				else
					message.append("Admin");
				i++;
			}
			message.append(" Password(s). Please enter correct passwords");
			context.getMessageContext()
					.addMessage(new MessageBuilder().error().defaultText(message.toString()).build());
			return error();
		}
	}

	/**
	 * This method BaseX Changes reader writer and admin passwords
	 * 
	 * @param configProperties
	 * @param context
	 * @return
	 */
	public Event doChangePasswords(ConfigProperties configProperties, RequestContext context) {

		if (!StringUtils.isEmpty(configProperties.getNewReaderPassword()) && !configProperties.isKeepReaderPassword()) {
			boolean readerSuccess = BaseX.changePassword(configProperties.getBaseXDB(),
					configProperties.getBaseXReaderHash(), "reader", configProperties.getNewReaderPassword());
			if (!readerSuccess) {
				context.getMessageContext().addMessage(
						new MessageBuilder().error().defaultText("Unable to update Reader Password.").build());
			}
			String readerCreds = "reader:" + configProperties.getNewReaderPassword();
			readerCreds = new String(Base64.encodeBase64((readerCreds).getBytes()));
			updateConfigProperty(configProperties.PROPERTY_READER_HASH, readerCreds, context);
		}
		if (!StringUtils.isEmpty(configProperties.getNewWriterPassword()) && !configProperties.isKeepWriterPassword()) {
			boolean writerSuccess = BaseX.changePassword(configProperties.getBaseXDB(),
					configProperties.getBaseXWriterHash(), "writer", configProperties.getNewWriterPassword());
			if (!writerSuccess) {
				context.getMessageContext().addMessage(
						new MessageBuilder().error().defaultText("Unable to update Writer Password.").build());
				String writerCreds = "writer:" + configProperties.getNewWriterPassword();
				writerCreds = new String(Base64.encodeBase64((writerCreds).getBytes()));
				updateConfigProperty(configProperties.PROPERTY_WRITER_HASH, writerCreds, context);
			}
		}
		if (!StringUtils.isEmpty(configProperties.getNewAdminPassword()) && !configProperties.isKeepAdminPassword()) {
			boolean adminSuccess = BaseX.changePassword(configProperties.getBaseXDB(),
					configProperties.getBaseXAdminHash(), "admin", configProperties.getNewAdminPassword());
			if (!adminSuccess) {
				context.getMessageContext().addMessage(
						new MessageBuilder().error().defaultText("Unable to update Admin Password.").build());
				String adminCreds = "admin:" + configProperties.getNewAdminPassword();
				adminCreds = new String(Base64.encodeBase64((adminCreds).getBytes()));
				updateConfigProperty(configProperties.PROPERTY_ADMIN_HASH, adminCreds, context);

			}
		}

		if (context.getMessageContext().getAllMessages().length > 0) {
			return error();
		} else {
			// context.getMessageContext().addMessage(new
			// MessageBuilder().error().defaultText("Passwords are changed
			// successfully.").build());
			return success();
		}
	}

	/**
	 * This method uploads saved config file and validates BaseX URL and
	 * Passwords
	 * 
	 * @param configProperties
	 * @return
	 */
	public Event doUploadSavedConfig(ConfigProperties configProperties,
			org.springframework.web.multipart.MultipartFile configFile, RequestContext context) {
		logger.debug("Start doUploadSavedConfig");
		InputStream inputStream = null;
		try {
			inputStream = configFile.getInputStream();
			ConfigurationProperties cp = new ConfigurationProperties(inputStream);
			configProperties.setSavedPropertiesMap(cp.getPropertiesMap());
			setDeployedConfigurationProperties(configProperties);
			setCed2arProperties(configProperties, cp.getPropertiesMap());
			return success();
		} catch (Exception ex) {
			logger.error("Error reading saved config file. " + ex);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception ex) {
				logger.error("Error closing saved config file inputstream. " + ex);
			}
		}
		return error();
	}

	public Event doUpdateCed2arProperties(ConfigProperties configProperties, RequestContext context) {

		// ServletExternalContext externalContext = (ServletExternalContext)
		// context.getExternalContext();
		// HttpSession session = ((HttpServletRequest)
		// externalContext.getNativeRequest()).getSession();

		ConfigurationProperties cp = new ConfigurationProperties();
		try {
			if (configProperties.isBugReportEnable())
				cp.addProperty(ConfigProperties.PROPERTY_BUG_REPORT_ENABLE, "true");
			else
				cp.addProperty(ConfigProperties.PROPERTY_BUG_REPORT_ENABLE, "false");
			cp.addProperty(ConfigProperties.PROPERTY_TIMEOUT, configProperties.getTimeout());

			if (configProperties.isRestricted())
				cp.addProperty(ConfigProperties.PROPERTY_RESTRICTED, "true");
			else
				cp.addProperty(ConfigProperties.PROPERTY_RESTRICTED, "false");

			if (configProperties.isDevFeatureProv())
				cp.addProperty(ConfigProperties.PROPERTY_DEV_FEATURE_PROV, "true");
			else
				cp.addProperty(ConfigProperties.PROPERTY_DEV_FEATURE_PROV, "false");

			if (configProperties.isDevFeatureGoogleAnalytics())
				cp.addProperty(ConfigProperties.PROPERTY_DEV_FEATURE_GOOGLE_ANALYTICS, "true");
			else
				cp.addProperty(ConfigProperties.PROPERTY_DEV_FEATURE_GOOGLE_ANALYTICS, "false");

			if (configProperties.isDevFeatureEditing())
				cp.addProperty(ConfigProperties.PROPERTY_DEV_FEATURE_EDITING, "true");
			else
				cp.addProperty(ConfigProperties.PROPERTY_DEV_FEATURE_EDITING, "false");

			cp.addProperty(ConfigProperties.PROPERTY_CONFIG_INITIALIZED, "true");

			cp.addProperty(ConfigProperties.PROPERTY_BUG_REPORT_EMAIL, configProperties.getBugReportEmail());
			cp.addProperty(ConfigProperties.PROPERTY_BUG_REPORT_SENDER, configProperties.getBugReportSender());
			cp.addProperty(ConfigProperties.PROPERTY_BUG_REPORT_PWD,
					new String(Base64.encodeBase64((configProperties.getBugReportPwd()).getBytes())));

			return success();
		} catch (Exception ex) {
			logger.error("Unable to configure CED2AR.  " + ex.getMessage(), ex);
			context.getMessageContext().addMessage(new MessageBuilder().error()
					.defaultText("Unable to configure CED2AR.  " + ex.getMessage()).build());
			return error();
		}
	}

	/**
	 * This method sets properties of the CED2AR that will be used. The
	 * properties are either read from saved config or default config
	 * 
	 * @param configProperties
	 * @param propertiesMap
	 */
	private void setCed2arProperties(ConfigProperties configProperties, Map<String, String> propertiesMap) {

		boolean bugReportEnable = false;
		if (propertiesMap.get(ConfigProperties.PROPERTY_BUG_REPORT_ENABLE).equalsIgnoreCase("true")) {
			bugReportEnable = true;
		}
		configProperties.setBugReportEnable(bugReportEnable);
		configProperties.setTimeout(propertiesMap.get(ConfigProperties.PROPERTY_TIMEOUT));
		boolean restricted = false;
		if (propertiesMap.get(ConfigProperties.PROPERTY_RESTRICTED).equalsIgnoreCase("true")) {
			restricted = true;
		}
		configProperties.setRestricted(restricted);

		boolean devProv = false;
		if (propertiesMap.get(ConfigProperties.PROPERTY_DEV_FEATURE_PROV).equalsIgnoreCase("true")) {
			devProv = true;
		}
		configProperties.setDevFeatureProv(devProv);

		boolean devGoogle = false;
		if (propertiesMap.get(ConfigProperties.PROPERTY_DEV_FEATURE_GOOGLE_ANALYTICS).equalsIgnoreCase("true")) {
			devGoogle = true;
		}
		configProperties.setDevFeatureGoogleAnalytics(devGoogle);

		boolean devEditing = false;
		if (propertiesMap.get(ConfigProperties.PROPERTY_DEV_FEATURE_EDITING).equalsIgnoreCase("true")) {
			devEditing = true;
		}
		configProperties.setDevFeatureEditing(devEditing);

		boolean configInitialized = false;
		if (propertiesMap.get(ConfigProperties.PROPERTY_CONFIG_INITIALIZED).equalsIgnoreCase("true")) {
			configInitialized = true;
		}

		String bugReportEmail = propertiesMap.get(ConfigProperties.PROPERTY_BUG_REPORT_EMAIL);
		if (bugReportEmail.startsWith("${") && bugReportEmail.endsWith("}")) {
			bugReportEmail = "";
		}
		configProperties.setBugReportEmail(bugReportEmail);

		String bugReportSenderEmail = propertiesMap.get(ConfigProperties.PROPERTY_BUG_REPORT_SENDER);
		if (bugReportSenderEmail.startsWith("${") && bugReportSenderEmail.endsWith("}")) {
			bugReportSenderEmail = "";
		}
		configProperties.setBugReportSender(bugReportSenderEmail);

		String bugReportPassword = propertiesMap.get(ConfigProperties.PROPERTY_BUG_REPORT_PWD);
		if (bugReportPassword.startsWith("${") && bugReportPassword.endsWith("}")) {
			bugReportPassword = "";
		}
		configProperties.setBugReportPwd(bugReportPassword);
		configProperties.setConfirmBugReportPwd(bugReportPassword);
		configProperties.setConfigInitialized(configInitialized);

	}

	// PRIVATE METHODS
	/**
	 * Updates deployed properties file
	 * 
	 * @param key
	 * @param value
	 * @param context
	 */
	private void updateConfigProperty(String key, String value, RequestContext context) {
		try {
			ConfigurationProperties cp = new ConfigurationProperties();
			cp.addProperty(key, value);
		} catch (Exception ex) {
			logger.error("Successfully changed password. But error in saving pwd in .proprties file. ", ex);
			context.getMessageContext()
					.addMessage(new MessageBuilder().error()
							.defaultText("Successfully changed password. But error in saving " + key
									+ " pwd in .proprties file. Please copy password and save it for future reference.")
					.build());
		}
	}

	/**
	 * This method loads config properties from default properties file
	 * 
	 * @param configProperties
	 */
	private void setDeployedConfigurationProperties(ConfigProperties configProperties) {
		ConfigurationProperties cp = new ConfigurationProperties();
		configProperties.setDeployedPropertiesMap(cp.getPropertiesMap());
		configProperties.setBaseXDB("");
		configProperties.setBaseXReaderHash("");
		configProperties.setBaseXWriterHash("");
		configProperties.setBaseXAdminHash("");
	}

	/**
	 * This method tests existence of baseX DB. When useSavedConfig is true, it
	 * uses url from saved config file. Sets the valid url in configProperties
	 * file.
	 * 
	 * @param configProperties
	 * @param useSavedConfig
	 * @return
	 */
	private boolean checkBaseXConnection(ConfigProperties configProperties, boolean useSavedConfig) {
		logger.info("Start testBaseXConnection");
		boolean validBaseX = false;
		String savedConfigBaseXDBUrl = "";
		if (useSavedConfig) {
			savedConfigBaseXDBUrl = configProperties.getSavedPropertiesMap().get(ConfigProperties.PROPERTY_BASEXDB);
			logger.debug("Testing BaseX DB connection from Saved Config File:  " + savedConfigBaseXDBUrl);
			validBaseX = BaseX.checkBaseX(savedConfigBaseXDBUrl);
			if (validBaseX) {
				logger.debug("BaseX URL from Saved Config file is valid:  " + savedConfigBaseXDBUrl);
				configProperties.setBaseXDB(savedConfigBaseXDBUrl);
				return true;
			} else {
				configProperties.setBaseXDB(savedConfigBaseXDBUrl);
				logger.debug("BaseX URL from Saved Config file is invalid:  " + savedConfigBaseXDBUrl);
			}
		}

		String deployedConfigBaseXDBUrl = configProperties.getDeployedPropertiesMap()
				.get(ConfigProperties.PROPERTY_BASEXDB);
		validBaseX = BaseX.checkBaseX(deployedConfigBaseXDBUrl);
		if (validBaseX) {
			logger.debug("BaseX URL from Deployed Config file is valid:  " + deployedConfigBaseXDBUrl);
			configProperties.setBaseXDB(deployedConfigBaseXDBUrl);
		} else {
			if (useSavedConfig)
				configProperties.setBaseXDB(savedConfigBaseXDBUrl);
			else
				configProperties.setBaseXDB(deployedConfigBaseXDBUrl);
			logger.debug("BaseX URL from Deployed Config file is invalid:  " + deployedConfigBaseXDBUrl);
		}
		return validBaseX;
	}
}
