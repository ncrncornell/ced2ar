package edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl;

import java.util.List;

import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.RolePermissionDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.model.RolePermission;

/**
 * Implementation class for RolePermissionDao for .properties file datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 * 
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
public class PropertiesRolePermissionDaoImpl extends PropertiesDaoBase implements RolePermissionDao{

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
		throw new RuntimeException("Unimplemented Method");	
	}


}
