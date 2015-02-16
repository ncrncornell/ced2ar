package edu.ncrn.cornell.ced2ar.security.idmgmt.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;
import org.junit.Assert;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Permission;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-beans.xml")
public class PermissionServiceImpl {
	@Autowired
	PermissionService permissionService;

	@Test
	public void getPermissions() {
		List<Permission> permissions = permissionService.getPermissions();
		Assert.assertNotNull(permissions);
	}
	
	@Test
	public void getPermission() {
		Permission permission = permissionService.getPermission("VIEW_CODEBOOK");
		Assert.assertNotNull(permission);
		
		permission = permissionService.getPermission("view_Codebook");
		Assert.assertNotNull(permission);
		
		permission = permissionService.getPermission("UPDATE_CODEBOOK");
		Assert.assertNotNull(permission);
	}
	@Test
	public void addPermission() {
		Permission p = new Permission();
		p.setPermissionId("TestPerm");
		p.setPermissionName("TestPerm Name");
		p.setPermissionDesc("This is TestPerm");
		String response = permissionService.addPermission(p);
		
		Assert.assertNotNull(response);
		
		Permission permission = permissionService.getPermission("TESTPERM");
		Assert.assertNotNull(permission);

		permission = permissionService.getPermission("TestPerm");
		Assert.assertNotNull(permission);

	}
	
	@Test
	public void updatePermission() {
		Permission p = new Permission();
		p.setPermissionId("TestPerm");
		p.setPermissionName("TestPerm Name is Set");
		p.setPermissionDesc("This is TestPermis Set");
		String response = permissionService.updatePermission(p);
		assert (StringUtils.isEmpty(response)==true);
	}
	
	
	

}
