package edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.RoleDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.UserDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.model.Role;
import edu.cornell.ncrn.ced2ar.security.idmgmt.model.User;
/**
 * Implementation class for UserDao for .properties file datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */


public class PropertiesUserDaoImpl extends PropertiesDaoBase implements UserDao {
	@Autowired
	RoleDao roleDao;
	
	@Override
	public User getUser(String userId) {
		User user = null;
		List<User> users = getUsers();
		for(User u:users) {
			if(u.getUserId().equalsIgnoreCase(userId)) {
				user = u;
				break;
			}
		}
		return user;
	}

	@Override
	public User getUser(String userId, boolean includeInactive) {
		return getUser(userId);
	}

	@Override
	public List<User> getUsers() {
		List<Role> roles = roleDao.getRoles();
		List<User> users = new ArrayList<User>();
		for(Role role: roles){
			List<String> userList = this.getValueAsList("authorized."+role.getRoleId()+".users");
			for(String u : userList) {
				User user = new User();
				user.setUserId(u);
				user.setFirstName(u);
				user.setLastName(u);
				user.setActiveUser(true);
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public List<User> getUsers(boolean includeInactive) {
		return getUsers();
	}

	@Override
	public String addUser(User user) {
		throw new RuntimeException("Unimplemented method");
	}

	@Override
	public String updateUser(User user) {
		throw new RuntimeException("Unimplemented method");
	}

	@Override
	public String getCIMDump() {
		throw new RuntimeException("Unimplemented method");
	}
	

}
