package edu.ncrn.cornell.ced2ar.security.idmgmt.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.UserRoleDao;

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
public class UserRoleServiceImpl {
	@Autowired
	UserRoleService userRoleService;

	//@Test
	public void getUserRoles() {
		List<String> roles = userRoleService.getRoles("user@domain.com");
		Assert.assertTrue(roles!=null && roles.size()==2);
		
		roles = userRoleService.getRoles("user2@domain.com");
		Assert.assertTrue(roles!=null && roles.size()==1);
		
		roles = userRoleService.getRoles("suser2@domain.com");
		Assert.assertTrue(roles==null);
	}
	//@Test
	public void isUserInRole() {
		Boolean inRole  = userRoleService.isUserInRole("user@domain.com", "ROLE_ADMIN");
		Assert.assertTrue(inRole);
		
		inRole  = userRoleService.isUserInRole("user@domain.com", "ROLE_USER");
		Assert.assertTrue(inRole);
		
		inRole  = userRoleService.isUserInRole("user@domain.com", "ROLE_USERs");
		Assert.assertFalse(inRole);
		
		inRole  = userRoleService.isUserInRole("user2@domain.com", "ROLE_USER");
		Assert.assertTrue(inRole);

		inRole  = userRoleService.isUserInRole("user2@domain.com", "ROLE_ADMIN");
		Assert.assertFalse(inRole);
	}

	@Test
	public void addRole() {
		String response = userRoleService.addRole("user@domain.com", "ROLE_PRW");
		Assert.assertTrue(StringUtils.isEmpty(response));
		
		response = userRoleService.addRole("user@domain.com", "ROLE_ZSx");
		Assert.assertTrue(response.equalsIgnoreCase(UserRoleDao.ERROR_USER_IS_IN_ROLE_ALREADY));
	}	
}