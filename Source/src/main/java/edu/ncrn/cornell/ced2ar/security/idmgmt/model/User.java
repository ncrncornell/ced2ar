package edu.ncrn.cornell.ced2ar.security.idmgmt.model;

import java.io.Serializable;
/**
 * Model class that for User attributes.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class User implements Serializable{
	private static final long serialVersionUID = -103073226097659840L;
	private String userId;
	private String firstName;
	private String lastName;
	private boolean activeUser;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean isActiveUser() {
		return activeUser;
	}
	public void setActiveUser(boolean activeUser) {
		this.activeUser = activeUser;
	}
	public String getActiveUserAsString() {
		String returnValue = "N";
		if(activeUser) returnValue = "Y";
		return returnValue;
	}
	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", activeUser=" + activeUser + "]";
	}
}
