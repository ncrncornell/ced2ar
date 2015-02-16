package edu.ncrn.cornell.ced2ar.security.idmgmt.dao;

import java.util.List;

import edu.ncrn.cornell.ced2ar.security.idmgmt.model.User;
/**
 * Interface that defines methods to access Users in the user management modules.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface UserDao {
	public static final String ERROR_USER_EXISTS = "User with that userId already exists in the datastore";
	
	/**
	 * Find and return the active User from the datastore.
	 * @param userId
	 * @return User object if user found; null if user is not found or user is inactive
	 */
	public User getUser(String userId);
	/**
	 * Find and return active or inactive user from the datastore
	 * @param userId
	 * @param includeInactive
	 * @return User object if user found; null if user is not found
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
	

	public String getCIMDump();  // This should be removed.
	
}
