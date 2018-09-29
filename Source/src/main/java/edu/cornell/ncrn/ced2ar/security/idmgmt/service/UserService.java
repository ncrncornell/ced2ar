package edu.cornell.ncrn.ced2ar.security.idmgmt.service;

import java.util.List;

import edu.cornell.ncrn.ced2ar.security.idmgmt.model.User;
/**
 * Interface that defines methods for UserService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface UserService {
	/**
	 * Find and return the active User from the datastore.
	 * @param userId
	 * @return User object if user found; null if user is not found or user is inactive
	 */
	public User getUser(String userId);
	
	/**
	 * 
	 * @param userId
	 * @param includeInactive  Fetches active or inactive user user 
	 * @return User object if user found; null if user is not found or user is inactive or active based on the boolean param
	 */
	
	public User getUser(String userId, boolean includeInactive);
	
	/**
	 * Fetch and return the list of all active users in the datastore
	 * @return List consisting of active User objects. Returns null if there are no users
	 */
	public List<User> getUsers();

	/**
	 * Fetch and return the list of all active and inactive users in the datastore
	 * @param includeInactive
	 * @return List consisting of active and inactive User objects. Returns null if there are no users
	 */
	public List<User> getUsers(boolean includeInactive);
	
	/**
	 * Add an user to the datastore. 
	 * @param user. User object to add to datastore
	 * @return Empty string if user is added successfully. String consisting of error message if adding user is not successfully 
	 */
	public String addUser(User user);
	
	
	
	/**
	 * Update the user in datastore
	 * @param user
	 * @return Empty string if user is updated successfully. String consisting of error message if updating user is not successful
	 */

	public String updateUser(User user);
	/**
	 * 
	 * @param userId
	 * @param codebookHandle
	 * @return true if the user has view or update permission.  User must be an active user
	 */
	public boolean canView(String userId,String codebookHandle);
	/**
	 * 
	 * @param userId
	 * @param codebookHandle
	 * @return true if the user has  update permission.  User must be an active user
	 */
	public boolean canUpdate(String userId,String codebookHandle);
	/**
	 * 
	 * @param userId
	 * @return returns true if the user is in admin role
	 */
	public boolean isUserInAdminRole(String userId);
	/**
	 * 
	 * @param userId
	 * @return returns true if the user is in user role
	 */
	public boolean isUserInUserRole(String userId);
	/**
	 * 
	 * @param userId
	 * @param permission
	 * @param codebookHandle
	 * @return Returns true if the user has specified permission 
	 */
	public boolean hasPermission(String userId, String permission,String codebookHandle);
	/**
	 * 
	 * @param userId
	 * @return returns true if the user in registered active or inactive
	 */
	public boolean isUserRegistered(String userId);
	
	public String getCIMDump();
}
