package edu.cornell.ncrn.ced2ar.security.idmgmt.model;

import java.io.Serializable;

/**
 * Model class for Permission attributes.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class Permission implements Serializable{
	private static final long serialVersionUID = -1605029115486909858L;

	public static final String PERMISSION_VIEW_CODEBOOK 	= "VIEW_CODEBOOK";
	public static final String PERMISSION_UPDATE_CODEBOOK 	= "UPDATE_CODEBOOK";
	
	private String permissionId;
	private String permissionName;
	private String permissionDesc;
	
	public String getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getPermissionDesc() {
		return permissionDesc;
	}
	public void setPermissionDesc(String permissionDesc) {
		this.permissionDesc = permissionDesc;
	}
	@Override
	public String toString() {
		return "Permission [permissionId=" + permissionId + ", permissionName="
				+ permissionName + ", permissionDesc=" + permissionDesc + "]";
	}
}
