package edu.ncrn.cornell.ced2ar.web.form;

/**
 *Password Change Form Util
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class PasswordChangeForm {
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;
	
	private String baseXDBUrl;
	private String baseXDBUserId;

	private boolean emailPasswordChange;
	
	public boolean isEmailPasswordChange() {
		return emailPasswordChange;
	}
	public void setEmailPasswordChange(boolean emailPasswordChange) {
		this.emailPasswordChange = emailPasswordChange;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword(){
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getBaseXDBUrl() {
		return baseXDBUrl;
	}
	public void setBaseXDBUrl(String baseXDBUrl) {
		this.baseXDBUrl = baseXDBUrl;
	}
	public String getBaseXDBUserId() {
		return baseXDBUserId;
	}
	public void setBaseXDBUserId(String baseXDBUserId) {
		this.baseXDBUserId = baseXDBUserId;
	}	
}