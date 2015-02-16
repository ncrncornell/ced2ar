
package edu.ncrn.cornell.ced2ar.security.idmgmt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.UserDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Permission;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Role;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.User;
import edu.ncrn.cornell.ced2ar.security.idmgmt.service.RolePermissionService;
import edu.ncrn.cornell.ced2ar.security.idmgmt.service.RoleService;
import edu.ncrn.cornell.ced2ar.security.idmgmt.service.UserRoleService;
import edu.ncrn.cornell.ced2ar.security.idmgmt.service.UserService;
/**
 * Implementation class for UserService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
public class UserServiceImpl implements UserService{
	@Autowired
	UserDao userDao;
	@Autowired
	RoleService roleService;
	@Autowired
	RolePermissionService rolePermissionService;
	@Autowired
	UserRoleService userRoleService;
	
	
	@Override
	public User getUser(String userId){
		return userDao.getUser(userId);
	}

	@Override
	public User getUser(String userId, boolean includeInactive) {
		return userDao.getUser(userId,includeInactive);
	}

	@Override
	public List<User> getUsers() {
		return userDao.getUsers();
	}

	@Override
	public List<User> getUsers(boolean includeInactive) {
		return userDao.getUsers(includeInactive);
	}

	@Override
	public String addUser(User user) {
		return userDao.addUser(user);
	}

	@Override
	public String updateUser(User user) {
		return userDao.updateUser(user);
	}

	@Override
	public String getCIMDump() {
		return userDao.getCIMDump();
	}

	@Override
	public boolean canView(String userId, String codebookHandle) {
		boolean canView = false;
		User user = this.getUser(userId);
		if(user==null || user.isActiveUser()==false) { // user does not exist or user is not active
			canView = false;
		}
		else {
			canView = hasPermission(userId,Permission.PERMISSION_VIEW_CODEBOOK,codebookHandle);
			if(!canView) { // if you have update permission, you can view code book
				canView = hasPermission(userId,Permission.PERMISSION_UPDATE_CODEBOOK,codebookHandle);
			}
		}
		
		return canView;
	}

	@Override
	public boolean canUpdate(String userId, String codebookHandle) {
		boolean canUpdate = false;
		User user = this.getUser(userId);
		if(user==null || user.isActiveUser()==false) { // user does not exist or user is not active
			canUpdate = false;
		}
		else {
			canUpdate = hasPermission(userId,Permission.PERMISSION_UPDATE_CODEBOOK,codebookHandle);
		}
		return canUpdate;
	}

	
	public boolean hasPermission(String userId, String permission,String codebookHandle) {
		boolean hasPermission = false;
		List<String> userRoles = userRoleService.getRoles(userId);
		if(userRoles == null || userRoles.isEmpty()) {  
			hasPermission = false;
		}
		else {
			for(String userRole : userRoles){
				List<String> codebookHandles  =rolePermissionService.getCodebookHandles(userRole, permission);
				if(codebookHandles == null || codebookHandles.isEmpty()) {
					hasPermission = false;
				}
				else {
					for(String handle : codebookHandles) {
						hasPermission = handle.equalsIgnoreCase(codebookHandle);
						if(hasPermission) break;
					}
				}
			}
		}
		return hasPermission;
	}
	
	@Override
	public boolean isUserRegistered(String userId) {
		boolean registered = true;
		User user  = getUser(userId);
		if(user == null || StringUtils.isEmpty(user.getUserId())) {
			registered = false;
		}
		return registered;
	}


	@Override
	public boolean isUserInAdminRole(String userId) {
		return userRoleService.isUserInRole(userId,Role.ROLE_ADMIN);
	}

	@Override
	public boolean isUserInUserRole(String userId) {
		return userRoleService.isUserInRole(userId,Role.ROLE_USER);
	}
}
