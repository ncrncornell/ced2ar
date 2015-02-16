package edu.ncrn.cornell.ced2ar.security.idmgmt.service;

import java.util.List;

import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Role;
/**
 * Interface that defines methods for RoleService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface RoleService {
	public Role getRole(String roleId);
	public List<Role> getRoles();
	public String addRole(Role role);
	public String updateRole(Role role);
}
