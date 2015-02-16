package edu.ncrn.cornell.ced2ar.web.form;

/**
 *Changes passwords for BaseX
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class BaseXDBChangeForm {
	private String adminPassword;
	private String readerPassword;
	private String writerPassword;
	
	private String baseXDBUrl;

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getReaderPassword() {
		return readerPassword;
	}

	public void setReaderPassword(String readerPassword) {
		this.readerPassword = readerPassword;
	}

	public String getWriterPassword() {
		return writerPassword;
	}

	public void setWriterPassword(String writerPassword) {
		this.writerPassword = writerPassword;
	}

	public String getBaseXDBUrl() {
		return baseXDBUrl;
	}

	public void setBaseXDBUrl(String baseXDBUrl) {
		this.baseXDBUrl = baseXDBUrl;
	}
}