package edu.ncrn.cornell.ced2ar.security.idmgmt.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import edu.ncrn.cornell.ced2ar.security.idmgmt.model.User;

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
public class UserServiceImpl {

	@Autowired
	UserService userService;
	
	//@Test
	public void getCIMDump(){
		String s = userService.getCIMDump();
		assert(StringUtils.isNotEmpty(s));
	}
	
	//@Test
	public void getUser() {
		User user=userService.getUser("user@domain.com");
		assert(user !=null);

		user = userService.getUser("user2@domain2.com");
		assert(user == null);
		
		user = userService.getUser("user@gmail.com");//This user is inactive 
		assert(user ==null);
		user = userService.getUser("user@gmail.com",true);
		assert(user != null);

		user = userService.getUser("user@gmail.com",false);
		assert(user == null);
	}

	//@Test
	public void getUsers() {
		List<User> users=userService.getUsers();
		assert(users.size() ==5);
		
		users = userService.getUsers(false);
		assert(users.size() ==5);
		
		users = userService.getUsers(true);
		assert(users.size() ==6);

	}

	@Test
	public void updateUser() {
		User user = new User();
		user.setFirstName("XXXX");
		user.setLastName("xxxx");
		user.setUserId("user@domain.com");
		user.setActiveUser(true);
		String response = userService.updateUser(user);
		User u = userService.getUser("user@domain.com");

		user.setFirstName("first");
		user.setLastName("last");
		user.setUserId("testuser@domain.com");
		user.setActiveUser(true);
		response =userService.updateUser(user);
		
		u = userService.getUser("testuser@domain.com");
	}
	
	//@Test
	public void addUser() {
		User user = new User();
		user.setFirstName("first");
		user.setLastName("last");
		user.setUserId("user@domain.com");
		user.setActiveUser(true);
		String returnValue = userService.addUser(user);
		Assert.isTrue(!StringUtils.isEmpty(returnValue));
		
		user = new User();
		user.setFirstName("HA");
		user.setLastName("HAHA");
		user.setUserId("user@gmail.com");
		user.setActiveUser(false);
		returnValue = userService.addUser(user);
		Assert.isTrue(!StringUtils.isEmpty(returnValue));
	}
	
	@Test
	public void testPermissions() {
		boolean canView = userService.canView("user@domain.com", "usa_00007");
		assert(canView==true);
		canView = userService.canView("user@domain.com", "qwi_01");
		assert(canView==true);

		canView = userService.canView("user@domain.com", "usa_00007a");
		assert(canView==false);
		
		canView = userService.canUpdate("user@domain.com", "qwi_01");
		assert(canView==true);

		canView = userService.canUpdate("user2@domain.com", "qwi_01");
		assert(canView==false);

		canView = userService.canView("user2@domain.com", "qwi_01");
		assert(canView==true);

		boolean isAdmin = userService.isUserInAdminRole("user@domain.com");
		boolean isUser = userService.isUserInUserRole("user@domain.com");
		isAdmin = userService.isUserInAdminRole("user2@domain.com");
	}
}