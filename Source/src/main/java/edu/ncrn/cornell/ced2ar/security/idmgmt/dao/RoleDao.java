package edu.ncrn.cornell.ced2ar.security.idmgmt.dao;

import java.util.List;

import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Role;

/**
 * Interface that defines methods to access Roles in the user management modules.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface RoleDao {
	public static final String ERROR_ROLE_EXISTS = "Role with that roleId already exists in the datastore";
	/**
	 * Find and return the Role from the datastore.
	 * @param roleId
	 * @return Role object if role found; null if role is not found   
	 */
	public Role getRole(String roleId);
	
	/**
	 * Fetch and return all the role in the datastore.
	 * @return List of role objects if roles found, null if none found
	 */
	public List<Role> getRoles();
	
	/**
	 * Add new role to the datastore.
	 * @param role
	 * @return Empty string if role is added successfully. String consisting of error message if there is error in adding user.
	 */
	public String addRole(Role role);
	
	/**
	 * Update role in the datastore.
	 * @param role
	 * @return Empty string if role is updated successfully. String consisting of error message if there role not updated successfully
	 */
	public String updateRole(Role role);
	
}
