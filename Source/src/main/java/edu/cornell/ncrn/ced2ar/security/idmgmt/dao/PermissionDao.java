package edu.cornell.ncrn.ced2ar.security.idmgmt.dao;

import java.util.List;

import edu.cornell.ncrn.ced2ar.security.idmgmt.model.Permission;

/**
 * Interface that defines methods to access permissions in the user management modules.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface PermissionDao {
	public static final String ERROR_PERMISSION_EXISTS = "Permission with that permissionId already exists in the datastore";
	
	/**
	 * Returns Permission object for the permissionId. If Permission not found returns null.
	 * @param userId
	 * @return Permission Object if found; null if not found
	 */
	public Permission getPermission(String permissionId);
	
	/**
	 * Returns all permissions in the datastore. Returns null if there are no permissions
	 * @return List of Permission Objects
	 */
	public List<Permission> getPermissions();
	
	/**
	 * Adds a permission to the datastore. 
	 * @param permission. Permission object to add to datastore
	 * @return Empty string if permission is added successfully. String consisting of error message if adding permission is not successfully 
	 */
	public String addPermission(Permission permission);

	
	/** 
	 * Updates Permission in datastore
	 * @param Permission
	 * @return Empty string if Permission is updated successfully. String consisting of error message if update of permission is not successful
	 */
	public String updatePermission(Permission permission);
}
