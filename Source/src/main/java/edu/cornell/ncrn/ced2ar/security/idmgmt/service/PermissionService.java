package edu.cornell.ncrn.ced2ar.security.idmgmt.service;

import java.util.List;

import edu.cornell.ncrn.ced2ar.security.idmgmt.model.Permission;
/**
 * Interface that defines methods for PermissionService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
public interface PermissionService {
	public Permission getPermission(String userId);
	public List<Permission> getPermissions();
	public String addPermission(Permission permission);
	public String updatePermission(Permission permission);
}
