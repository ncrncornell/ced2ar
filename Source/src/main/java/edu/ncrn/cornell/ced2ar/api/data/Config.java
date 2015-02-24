package edu.ncrn.cornell.ced2ar.api.data;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Credentials and URLs for external resources such as BaseX
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Venky Kambhampaty, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Config {
	
	public static final String AUTHENTICATION_TYPE_OAUTH2 	= "OAUTH2";
	public static final String AUTHENTICATION_TYPE_OPENID 	= "OPENID";
	public static final String AUTHENTICATION_TYPE_DEFAULT 	= "DEFAULT";

	private String baseXDB; 	
	private String baseXReaderHash;
	private String baseXWriterHash;
	private String baseXAdminHash;
	
	private int timeout;//Timeout constant
	private boolean isRestricted;//Switch to determine if in a restricted environment 
	private int port;//Port on which application runs
	private String eAPI;//Name of web application that the editing API runs under. Needs to be local.
	
	//Email to send the bugReport to
	private String bugReportEmail;
	private String bugReportSender;
	private String bugReportPwd;
	private boolean bugReportEnable;
	
	private boolean devFeatureProv;
	private boolean devFeatureGoogleAnalytics;
	private boolean devFeatureEditing;
	
	//Schemas locations
	private String schemaURI;
	
	//Passwords
	private boolean pwdIsRandom;
	private String pwdHash;
	private boolean basexGenerateRandomPasswords;
	private String openAccess;
	
	//Git added so other classes have access
	private boolean isGitEnabled;
	private String remoteRepoURL;
	private String authenticationType;
	
	

//Getters and setters
	
	public boolean isBugReportEnable() {
		return bugReportEnable;
	}

	public void setBugReportEnable(boolean bugReportEnable) {
		this.bugReportEnable = bugReportEnable;
	}

	
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

	/**
	 * Method geteAPI.
	 * @return String
	 */
	public String geteAPI() {
		return eAPI;
	}

	/**
	 * Method seteAPI.
	 * @param eAPI String
	 */
	public void seteAPI(String eAPI) {
		this.eAPI = eAPI;
	}

	/**
	 * Method getBugReportEmail.
	 * @return String
	 */
	public String getBugReportEmail() {
		return bugReportEmail;
	}

	/**
	 * Method setBugReportEmail.
	 * @param bugReportEmail String
	 */
	public void setBugReportEmail(String bugReportEmail) {
		this.bugReportEmail = bugReportEmail;
	}

	/**
	 * Method getBugReportSender.
	 * @return String
	 */
	public String getBugReportSender() {
		return bugReportSender;
	}

	/**
	 * Method setBugReportSender.
	 * @param bugReportSender String
	 */
	public void setBugReportSender(String bugReportSender) {
		this.bugReportSender = bugReportSender;
	}

	/**
	 * Method getBugReportPwd.
	 * @return String
	 */
	public String getBugReportPwd() {
		return bugReportPwd;
	}
	
	public String getDecodedBugReportPwd(){
		return new String(Base64.decodeBase64(bugReportPwd));
	}

	/**
	 * Method setBugReportPwd.
	 * @param bugReportPwd String
	 */
	public void setBugReportPwd(String bugReportPwd) {
		this.bugReportPwd = bugReportPwd;
	}
	
	/**
	 * Method isRestricted.
	 * @return boolean
	 */
	public boolean  isRestricted(){
		return isRestricted;
	}
	/**
	 * Method getRestricted.
	 * @return boolean
	 */
	public boolean getRestricted() {
		return isRestricted;
	}

	/**
	 * Method setRestricted.w
	 * @param isRestricted boolean
	 */
	public void setRestricted(boolean isRestricted) {
		this.isRestricted = isRestricted;
	}

	/**
	 * Method getTimeout.
	 * @return int
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Method setTimeout.
	 * @param timeout int
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Method getBugReportInfo.
	 * @return String[]
	 */
	public String[] getBugReportInfo(){
		return new String[] {bugReportEmail, bugReportSender, bugReportPwd};
	}

	/**
	 * Method getDevFeatureProv.
	 * @return boolean
	 */
	public boolean getDevFeatureProv() {
		return devFeatureProv;
	}

	/**
	 * Method setDevFeatureProv.
	 * @param devFeatureProv boolean
	 */
	public void setDevFeatureProv(boolean devFeatureProv) {
		this.devFeatureProv = devFeatureProv;
	}

	/**
	
	/**
	 * Method getDevFeatureGoogleAnalytics.
	 * @return boolean
	 */
	public boolean getDevFeatureGoogleAnalytics() {
		return devFeatureGoogleAnalytics;
	}

	/**
	 * Method setDevFeatureGoogleAnalytics.
	 * @param devFeatureGoogleAnalytics boolean
	 */
	public void setDevFeatureGoogleAnalytics(boolean devFeatureGoogleAnalytics) {
		this.devFeatureGoogleAnalytics = devFeatureGoogleAnalytics;
	}
	
	public String getSchemaURI(){
		if(isRestricted){
			//TODO:Fix for all
			String local = Config.class.getResource("/schemas/codebook.xsd").toString();		
			return local;
		}
		return schemaURI;
	}

	public void setSchemaURI(String schemaURI) {
		this.schemaURI = schemaURI;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void setDevFeatureEditing(boolean b){
		this.devFeatureEditing = b;
	}
	
	public boolean getDevFeatureEditing(){
		return this.devFeatureEditing;
	}
	
	public void setPwdIsRandom(boolean b){
		this.pwdIsRandom = b;
	}
	
	public boolean getPwdIsRandom(){
		return this.pwdIsRandom;
	}
	
	public void setPwdHash(String s){
		this.pwdHash = s;
	}
	
	public String getPwdHash(){
		return this.pwdHash;
	}
	
	public boolean isBasexGenerateRandomPasswords() {
		return basexGenerateRandomPasswords;
	}

	public void setBasexGenerateRandomPasswords(boolean basexGenerateRandomPasswords) {
		this.basexGenerateRandomPasswords = basexGenerateRandomPasswords;
	}
	
	public String getRemoteRepoURL() {
		return remoteRepoURL;
	}

	public void setRemoteRepoURL(String remoteRepoURL) {
		this.remoteRepoURL = remoteRepoURL;
	}
	
	public boolean isGitEnabled() {
		return isGitEnabled;
	}
	
	public void setGitEnabled(boolean isGitEnabled) {
		this.isGitEnabled = isGitEnabled;
	}
	
	public String getOpenAccess() {
		return this.openAccess;
	}
	
	public void setOpenAccess(String s) {
		this.openAccess = s;
	}

	
	
	public boolean isAuthenticationTypeOauth2() {
		return getAuthenticationType().equalsIgnoreCase(AUTHENTICATION_TYPE_OAUTH2);
	}
	public boolean isAuthenticationTypeOpenId() {
		return getAuthenticationType().equalsIgnoreCase(AUTHENTICATION_TYPE_OPENID);
	}
	
	public boolean isAuthenticationTypeDefault() {
		boolean authenticationTypeDefault = false;
		if(getAuthenticationType().equalsIgnoreCase(AUTHENTICATION_TYPE_DEFAULT))
			authenticationTypeDefault = true;
		else {
				if(!getAuthenticationType().equalsIgnoreCase(AUTHENTICATION_TYPE_OAUTH2) || 
				   !getAuthenticationType().equalsIgnoreCase(AUTHENTICATION_TYPE_OPENID))
					authenticationTypeDefault = true;
		}
		return authenticationTypeDefault;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}

	public static Config getInstance(){		
		ClassPathXmlApplicationContext appContext = null;
		try{
			appContext = new ClassPathXmlApplicationContext("ced2ar-web-beans.xml");
			BeanFactory beanFactory=appContext;
			return (Config) beanFactory.getBean("config");
		}finally{
			if(appContext !=null) appContext.close();
		}
	}	
}