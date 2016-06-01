package edu.ncrn.cornell.ced2ar.security.idmgmt.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class RolePermissionServiceImpl {
	@Autowired
	RolePermissionService rolePermissionService;
	
	@Test
	public void getCodebookHandles(){
		List<String> codebookHandles =  rolePermissionService.getCodebookHandles("ROLE_USER","VIEW_CODEBOOK");
		assert(codebookHandles != null && codebookHandles.size() ==2 );

		codebookHandles =  rolePermissionService.getCodebookHandles("ROLE_ADMIN","UPDATE_CODEBOOK");
		assert(codebookHandles != null && codebookHandles.size() ==1 );
		
		codebookHandles =  rolePermissionService.getCodebookHandles("ROLE_ADMINs","UPDATE_CODEBOOK");
		assert(codebookHandles != null);

		codebookHandles =  rolePermissionService.getCodebookHandles("ROLE_USERs","UPDATE_CODEBOOK");
		assert(codebookHandles != null);
	}
	

}
