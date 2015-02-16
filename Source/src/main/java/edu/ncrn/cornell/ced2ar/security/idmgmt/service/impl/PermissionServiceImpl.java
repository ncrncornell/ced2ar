package edu.ncrn.cornell.ced2ar.security.idmgmt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.PermissionDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Permission;
import edu.ncrn.cornell.ced2ar.security.idmgmt.service.PermissionService;
/**
 * Implementation class for PermissionService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */


public class PermissionServiceImpl implements PermissionService{
	@Autowired
	PermissionDao permissionDao;
	
	public Permission getPermission(String permissionId) {
		return permissionDao.getPermission(permissionId);
	}
	
	public List<Permission> getPermissions(){
		return permissionDao.getPermissions();
	}

	@Override
	public String addPermission(Permission permission) {
		return permissionDao.addPermission(permission);	
	}

	@Override
	public String updatePermission(Permission permission) {
		return permissionDao.updatePermission(permission);
	}

}
