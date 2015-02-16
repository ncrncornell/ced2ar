package edu.ncrn.cornell.ced2ar.security.idmgmt.service;

import java.util.List;

import edu.ncrn.cornell.ced2ar.security.idmgmt.model.RolePermission;
/**
 * Interface that defines methods for RolePermissionService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface RolePermissionService {
	 public RolePermission getRolePermission(String roleId);
	 public RolePermission getRolePermissions();
	 public List<String> getCodebookHandles(String roleId, String permissionId);
}
