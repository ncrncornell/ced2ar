package edu.cornell.ncrn.ced2ar.security.idmgmt.model;

import java.io.Serializable;
import java.util.List;
/**
 * Model class that for UserRole attributes.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class UserRole implements Serializable{
	private static final long serialVersionUID = -5374981516892672314L;
	
	private String userId;
	private List<String> roleIds;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<String> getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}
	@Override
	public String toString() {
		return "UserRoles [userId=" + userId + ", roleIds=" + roleIds + "]";
	}
}
