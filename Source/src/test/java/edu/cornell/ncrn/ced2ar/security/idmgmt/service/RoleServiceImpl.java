package edu.cornell.ncrn.ced2ar.security.idmgmt.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import edu.cornell.ncrn.ced2ar.security.idmgmt.model.Role;

/**
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-beans.xml")
public class RoleServiceImpl {
	@Autowired
	RoleService roleService;

	//@Test
	public void getRole() {
		Role role = roleService.getRole("ROLE_ADMIN");
		assert(role!=null);
		role = roleService.getRole("ROLE_ADMINs");
		assert(role==null);
		role = roleService.getRole("ROLE_USER");
		assert(role!=null);
	}
	
	@Test
	public void getRoles() {
		List<Role> roles = roleService.getRoles();
		assert(roles!=null && roles.size() ==2);
	}
	
	
	@Test
	public void addRole() {
		Role role = new Role();
		role.setRoleId("ROLE_TEST");
		role.setRoleName("Test Role");
		role.setRoleDesc("Test Role. is to test add role method");
		
		String response = roleService.addRole(role);
		Assert.isTrue(StringUtils.isBlank(response));
		
		role.setRoleId("ROLE_TEST");
		role.setRoleName("Test Role");
		role.setRoleDesc("Test Role. is to test add role method");
		 response = roleService.addRole(role);
		 

	}
	//@Test
	public void updateRole() {
		Role role = new Role();
		role.setRoleId("ROLE_TEST");
		role.setRoleName("Test ROLE44333sss");
		role.setRoleDesc("Test ROLEsssss331231321321 is to test add role method");
		String response = roleService.updateRole(role);
		assert(StringUtils.isBlank(response));
		
		
	}



}
