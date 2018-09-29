package edu.cornell.ncrn.ced2ar.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * This class fetches Users from properties file
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class AuthoriedUsers extends PropertiesBase{
	public static final String ROLES_KEY = "ced2ar.roles";
	
	public AuthoriedUsers(){
		super("ced2ar-user-config.properties");
	}
	
	public List<String> getAllRoles(){
		List<String> roleList = this.getValueAsList(ROLES_KEY);
		return roleList;
	}
	public List<String> getAllUsers(){
		List<String> roles = getAllRoles();
		List<String> users = new ArrayList<String>();
		for(String role: roles){
			users.addAll(getAllUsers(role));
		}
		return users;
	}
	public List<String> getAllUsers(String roleName){
		List<String> userList = this.getValueAsList("authorized."+roleName+".users");
		return userList;
	}
	
	public boolean isUserRegistered(String userName){
		boolean registred = false;
		List<String>users = getAllUsers();
		for(String user : users){
			if(user.trim().equalsIgnoreCase(userName)){
				registred = true;
				break;
			}
		}
		return registred;
	}
	
	public boolean isUserRegistered(String userName,String roleName){
		boolean registred = false;
		List<String>users = getAllUsers(roleName);
		for(String user : users){
			if(user.trim().equalsIgnoreCase(userName)){
				registred = true;
				break;
			}
		}
		return registred;
	}
}