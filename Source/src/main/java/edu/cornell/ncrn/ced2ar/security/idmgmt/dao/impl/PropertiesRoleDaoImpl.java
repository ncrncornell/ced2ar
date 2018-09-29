package edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.RoleDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.model.Role;
/**
 * Implementation class for RoleDao for .properties file datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class PropertiesRoleDaoImpl extends PropertiesDaoBase implements RoleDao{
	public static final String ROLES_KEY = "ced2ar.roles";

	@Override
	public Role getRole(String roleId) {
		Role role = null;
		List<String> roleList = this.getValueAsList(ROLES_KEY);
		for(String rid:roleList) {
			if(rid.equalsIgnoreCase(roleId)) {
				role = new Role();
				role.setRoleId(rid);
				role.setRoleName(rid);
				role.setRoleDesc(rid);
			}
		}
		return role;
	}

	@Override
	public List<Role> getRoles() {
		List<String> roleList = this.getValueAsList(ROLES_KEY);
		List<Role> roles = new ArrayList<Role>();
		for(String roleId:roleList) {
			Role role = new Role();
			role.setRoleId(roleId);
			role.setRoleName(roleId);
			role.setRoleDesc(roleId);
			roles.add(role);
		}
		return roles;
	}

	@Override
	public String addRole(Role role) {
		throw new RuntimeException("Unimplemented Method");
	}

	@Override
	public String updateRole(Role role) {
		throw new RuntimeException("Unimplemented Method");
	}

}
