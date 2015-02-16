package edu.ncrn.cornell.ced2ar.security.idmgmt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.UserRoleDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.service.UserRoleService;
/**
 * Implementation class for UserRoleService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
public class UserRoleServiceImpl implements UserRoleService {
	@Autowired
	protected UserRoleDao userRoleDao;
	@Override
	public List<String> getRoles(String userId) {
		return userRoleDao.getRoles(userId);
	}

	@Override
	public String removeRole(String userId,String roleId) {
		// TODO Auto-generated method stub
		return userRoleDao.removeRole(userId,roleId);
	}

	@Override
	public String addRole(String userId,String roleId) {
		return userRoleDao.addRole(userId,roleId);
	}

	public UserRoleDao getUserRoleDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDao userRoleDao) {
		this.userRoleDao = userRoleDao;
	}
	public boolean isUserInRole(String userId,String roleId) {
		return userRoleDao.isUserInRole(userId, roleId);
	}
	
	
}
