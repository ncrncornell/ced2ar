package edu.ncrn.cornell.ced2ar.security.idmgmt.service;

import java.util.List;
/**
 * Interface that defines methods for UserRoleService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public interface UserRoleService {
	public List<String> getRoles(String userId);
	public String removeRole(String userId,String roleId);
	public String addRole(String userId,String roleId);
	public boolean isUserInRole(String userId,String roleId);
}
