package edu.ncrn.cornell.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.RolePermissionDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.RolePermission;

/**
 * Implementation class of RolePermissionDao for BaseX  datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class BaseXRolePermissionDaoImpl implements  RolePermissionDao{
	private static final Logger logger = Logger.getLogger(BaseXRolePermissionDaoImpl.class);
	
	@Override
	public RolePermission getRolePermission(String roleId) {
		
		return null;
	}

	@Override
	public RolePermission getRolePermissions() {
		return null;
	}

	/**
	 * BaseX implementation of getCodebookHandles.
	 * Returns the codebookHandles as a list for the permissionId and RoleId.
	 * Returns null if the no codebooks are found.
	 */
	@Override
	public List<String> getCodebookHandles(String roleId, String permissionId) {
		logger.debug("Start getCodebookHandles " + roleId + ":" + permissionId);
		
		String xQuery =  "for $roleperms in collection('cims')//rolePermissions//" +
						 "rolePermission[contains(roleId,\""+roleId+"\")]//" +
						 "permissionCodebook//permission[contains (permissionId,\""+permissionId+"\")]" +
						 "/codebooks/codebookHandle  return concat($roleperms//text(),\":\")";
		
		logger.debug("Query " + xQuery);
		String codebookHandles= BaseX.getXML(xQuery);
		logger.debug("codebookHandles " + codebookHandles);
		if(StringUtils.isEmpty(codebookHandles)) {
			return null;
		}
		else{
			List<String> codebookHandleList = new ArrayList<String>();
			String[] codebookHandleArray = codebookHandles.trim().split(":");
			logger.debug("codebookHandleArray " + codebookHandleArray);
			for(int i=0; i<codebookHandleArray.length;i++) {
				codebookHandleList.add(codebookHandleArray[i].trim());
			}
			logger.debug("codebookHandleList " + codebookHandleList);
			return codebookHandleList;
		}
	}
	

}
