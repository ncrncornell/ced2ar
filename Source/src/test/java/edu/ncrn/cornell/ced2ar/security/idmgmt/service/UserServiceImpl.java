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
		User user  =userService.getUser("kvrayudu@gmail.com");
		assert(user !=null);

		user  =userService.getUser("bperry@gmails.com");
		assert(user == null);
		
		user  =userService.getUser("lars@gmail.com");  //This user is inactive 
		assert(user ==null);
		user  =userService.getUser("lars@gmail.com",true);
		assert(user != null);

		user  =userService.getUser("lars@gmail.com",false);
		assert(user == null);
		
	}
	
	
	//@Test
	public void getUsers() {
		List<User> users  =userService.getUsers();
		assert(users.size() ==5);
		
		users  =userService.getUsers(false);
		assert(users.size() ==5);
		
		users  =userService.getUsers(true);
		assert(users.size() ==6);

	}

	@Test
	public void updateUser() {
		User user = new User();
		user.setFirstName("XXXX");
		user.setLastName("xxxx");
		user.setUserId("kvrayudu@gmail.com");
		user.setActiveUser(true);
		String response = userService.updateUser(user);
		
		User u = userService.getUser("kvrayudu@gmail.com");

		user.setFirstName("xxssss");
		user.setLastName("xxxx");
		user.setUserId("aaaakvrayudu@gmail.com");
		user.setActiveUser(true);
		response =userService.updateUser(user);
		
		u = userService.getUser("aaaakvrayudu@gmail.com");

		
	}
	
//	@Test
	public void addUser() {
		User user = new User();
		user.setFirstName("HA");
		user.setLastName("HAHA");
		user.setUserId("KVRAYUDU@gmail.com");
		user.setActiveUser(true);
		String returnValue = userService.addUser(user);
		Assert.isTrue(!StringUtils.isEmpty(returnValue));
		
		user = new User();
		user.setFirstName("HA");
		user.setLastName("HAHA");
		user.setUserId("LARS@gmail.com");
		user.setActiveUser(false);
		returnValue = userService.addUser(user);
		Assert.isTrue(!StringUtils.isEmpty(returnValue));
		
		
		
	}
	
	@Test
	public void testPermissions() {
		boolean canView = userService.canView("kvrayudu@gmail.com", "usa_00007");
		assert(canView==true);
		canView = userService.canView("kvrayudu@gmail.com", "qwi_01");
		assert(canView==true);

		canView = userService.canView("kvrayudu@gmail.com", "usa_00007a");
		assert(canView==false);
		
		canView = userService.canUpdate("kvrayudu@gmail.com", "qwi_01");
		assert(canView==true);

		canView = userService.canUpdate("bperry@gmail.com", "qwi_01");
		assert(canView==false);

		canView = userService.canView("bperry@gmail.com", "qwi_01");
		assert(canView==true);

		boolean isAdmin = userService.isUserInAdminRole("kvrayudu@gmail.com");
		boolean isUser = userService.isUserInUserRole("kvrayudu@gmail.com");
		isAdmin = userService.isUserInAdminRole("bperry@gmail.com");
	}
	

}
