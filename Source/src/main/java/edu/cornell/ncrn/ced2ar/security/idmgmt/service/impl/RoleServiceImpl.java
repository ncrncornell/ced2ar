package edu.cornell.ncrn.ced2ar.security.idmgmt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.RoleDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.model.Role;
import edu.cornell.ncrn.ced2ar.security.idmgmt.service.RoleService;
/**
 * Implementation class for RoleService
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	RoleDao roleDao;
	
	@Override
	public Role getRole(String roleId) {
		return roleDao.getRole(roleId);
	}

	@Override
	public List<Role> getRoles() {
		return roleDao.getRoles();
	}

	@Override
	public String addRole(Role role) {
		return roleDao.addRole(role);
	}

	@Override
	public String updateRole(Role role) {
		return roleDao.updateRole(role);
	}
}
