package edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.UserRoleDao;
/**
 * Implementation class for UserRoleDao for .properties file datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class PropertiesUserRoleDaoImpl extends PropertiesDaoBase implements UserRoleDao{
	public static final String ROLES_KEY = "ced2ar.roles";
	@Override
	public List<String> getRoles(String userId) {
		List<String> userInRoles = new ArrayList<String>();
		List<String> roleList = this.getValueAsList(ROLES_KEY);
		for(String roleId : roleList) {
			List<String> userList = getValueAsList("authorized."+roleId+".users");
			for(String uid:userList) {
				if(uid.equalsIgnoreCase(userId)) {
					userInRoles.add(roleId);
				}
			}
		}
		return userInRoles;
	}

	@Override
	public String removeRole(String userId, String roleId) {
		throw new RuntimeException("Unimplemented Method");
	}

	@Override
	public String addRole(String userId, String roleId) {
		throw new RuntimeException("Unimplemented Method");	}

	@Override
	public boolean isUserInRole(String userId, String roleId) {
		boolean userInRole = false;
		List<String> userRoles = getRoles(userId);
		for(String userRole:userRoles) {
			if(userRole.equalsIgnoreCase(roleId)) {
				userInRole  = true;
				break;
			}
		}
		return userInRole;
	}

}
