package edu.ncrn.cornell.ced2ar.security.idmgmt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.RolePermissionDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.RolePermission;
import edu.ncrn.cornell.ced2ar.security.idmgmt.service.RolePermissionService;
/**
 * Implementation class for RolePermissionService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class RolePermissionServiceImpl implements RolePermissionService {
	@Autowired
	RolePermissionDao rolePermissionDao;
	
	@Override
	public RolePermission getRolePermission(String roleId) {
		throw new RuntimeException("Unimplemented Method");
	}

	@Override
	public RolePermission getRolePermissions() {
		throw new RuntimeException("Unimplemented Method");
	}

	@Override
	public List<String> getCodebookHandles(String roleId, String permissionId) {
		return rolePermissionDao.getCodebookHandles(roleId, permissionId);
	}
}