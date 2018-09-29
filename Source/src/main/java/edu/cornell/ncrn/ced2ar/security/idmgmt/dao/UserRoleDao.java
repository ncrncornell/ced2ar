package edu.cornell.ncrn.ced2ar.security.idmgmt.dao;

import java.util.List;
/**
 * Interface that defines methods to access UserRoles in the user management modules.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface UserRoleDao {
	public final String ERROR_USER_IS_IN_ROLE_ALREADY = "User is already in the role";
	/**
	 * 
	 * @param userId
	 * @return list of roles the user is in.  Returns null if the user is not in any roles.
	 * 		returns null if the user is inactive
	 */
	public List<String> getRoles(String userId);
	
	public String removeRole(String userId,String roleId);
	/**
	 * 
	 * @param userId
	 * @param roleId
	 * @return Adds the role to user if the user is active. on Inactive user this method will not perform any action.
	 */
	public String addRole(String userId,String roleId);
	/**
	 * 
	 * @param userId
	 * @param roleId
	 * @return retirns true if the user is in role.  Returns false if the user is inactive or not in the role.
	 */
	public boolean isUserInRole(String userId,String roleId);
}
