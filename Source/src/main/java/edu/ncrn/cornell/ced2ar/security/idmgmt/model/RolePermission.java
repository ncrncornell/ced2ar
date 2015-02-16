package edu.ncrn.cornell.ced2ar.security.idmgmt.model;

import java.io.Serializable;
import java.util.List;

/**
 * Model class that for RolePermission attributes.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */


public class RolePermission implements Serializable{
	private static final long serialVersionUID = 7225728372773838813L;
	private String roleId;
	private List<PermissionCodebook> permissionCodebook;
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public List<PermissionCodebook> getPermissionCodebook() {
		return permissionCodebook;
	}
	public void setPermissionCodebook(List<PermissionCodebook> permissionCodebook) {
		this.permissionCodebook = permissionCodebook;
	} 
}
