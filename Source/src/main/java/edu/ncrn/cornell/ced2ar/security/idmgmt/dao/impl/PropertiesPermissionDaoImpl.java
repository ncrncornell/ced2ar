package edu.ncrn.cornell.ced2ar.security.idmgmt.dao.impl;

import java.util.List;

import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.PermissionDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Permission;
/**
 * Implementation class for PermissionDao for .properties file datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 * 
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class PropertiesPermissionDaoImpl extends PropertiesDaoBase implements PermissionDao{

	@Override
	public Permission getPermission(String permissionId) {
		throw new RuntimeException("Unimplemented Method");	
	}

	@Override
	public List<Permission> getPermissions() {
		throw new RuntimeException("Unimplemented Method");	
	}

	@Override
	public String addPermission(Permission permission) {
		throw new RuntimeException("Unimplemented Method");	
	}

	@Override
	public String updatePermission(Permission permission) {
		throw new RuntimeException("Unimplemented Method");	
	}
	

}
