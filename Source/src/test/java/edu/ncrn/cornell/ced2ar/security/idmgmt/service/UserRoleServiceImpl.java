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


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-beans.xml")
public class UserRoleServiceImpl {
	@Autowired
	UserRoleService userRoleService;

	//@Test
	public void getUserRoles() {
		List<String> roles = userRoleService.getRoles("kvrayudu@gmail.com");
		Assert.assertTrue(roles!=null && roles.size()==2);
		
		roles = userRoleService.getRoles("bperry@GMAIL.com");
		Assert.assertTrue(roles!=null && roles.size()==1);
		
		roles = userRoleService.getRoles("sbperry@GMAIL.com");
		Assert.assertTrue(roles==null);
	}
	//@Test
	public void isUserInRole() {
		Boolean inRole  = userRoleService.isUserInRole("kvrayudu@gmail.com", "ROLE_ADMIN");
		Assert.assertTrue(inRole);
		
		inRole  = userRoleService.isUserInRole("kvrayudu@gmail.com", "ROLE_USER");
		Assert.assertTrue(inRole);
		
		inRole  = userRoleService.isUserInRole("kvrayudu@gmail.com", "ROLE_USERs");
		Assert.assertFalse(inRole);
		
		inRole  = userRoleService.isUserInRole("bperry@gmail.com", "ROLE_USER");
		Assert.assertTrue(inRole);

		inRole  = userRoleService.isUserInRole("bperry@gmail.com", "ROLE_ADMIN");
		Assert.assertFalse(inRole);

	}

	@Test
	public void addRole() {
		String response = userRoleService.addRole("kvrayudu@gmail.com", "ROLE_PRW");
		Assert.assertTrue(StringUtils.isEmpty(response));
		
		response = userRoleService.addRole("kvrayudu@gmail.com", "ROLE_ZSx");
		Assert.assertTrue(response.equalsIgnoreCase(UserRoleDao.ERROR_USER_IS_IN_ROLE_ALREADY));

		
	}	
	
}
