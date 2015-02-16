package edu.ncrn.cornell.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.PermissionDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Permission;
/**
 * Implementation class of PermissionDao for BaseX  datastore.
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 * 
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
public class BaseXPermissionDaoImpl implements PermissionDao{
	private static final Logger logger = Logger.getLogger(BaseXPermissionDaoImpl.class);

	@Override
	public Permission getPermission(String permissionId) {
		logger.debug("Start getPermission " + permissionId);
		String xQuery =  "for $perms in collection('cims')//cims//permissions//permission "+
						 "where  $perms/permissionId = \""+permissionId.toUpperCase()+"\" return  " + 
						 "concat($perms/permissionId/text(),\":\" "+
						 ",$perms/permissionName/text() ,\":\","+
						 "$perms/permissionDesc/text() )";
		logger.debug("Query " + xQuery);
		String permissionInfo = BaseX.getXML(xQuery);
		logger.debug("permissionInfo " + permissionInfo);
		
		if(StringUtils.isEmpty(permissionInfo)) 
			return null;
		else 
			return getPermissionFromTokenizedString(permissionInfo);
	}

	@Override
	public List<Permission> getPermissions() {
		logger.debug("Start getPermissions ");
		String xQuery =  "for $perms in collection('cims')//cims//permissions//permission " +
						 "return concat($perms/permissionId/text(),\":\" ,"+
						 "$perms/permissionName/text() ,\":\","+
						 "$perms/permissionDesc/text() ,\":XX\")";
		logger.debug("Query " + xQuery);
		
		String permissionsInfo = BaseX.getXML(xQuery);
		logger.debug("permissionsInfo " + permissionsInfo);
		if(StringUtils.isEmpty(permissionsInfo)) {
			return null;
		}
		else{
			List<Permission> permissions = new ArrayList<Permission>();
			permissionsInfo = permissionsInfo.trim();
			String[] permissionString = permissionsInfo.split(":XX ");
			for(int i=0; i<permissionString.length;i++) {
				permissions.add(getPermissionFromTokenizedString(permissionString[i]));
			}
			return permissions;
		}
	}

	@Override
	public String addPermission(Permission permission) {
		logger.debug("Start addPermission  " + permission);
		
		if(this.getPermission(permission.getPermissionId())!=null) {
			logger.debug("Permission already exists  ");
			return ERROR_PERMISSION_EXISTS;
		}

		
		String permissionNode = "<permission><permissionId>" +permission.getPermissionId().toUpperCase()+"</permissionId>"+
				  "<permissionName>" +permission.getPermissionName()+"</permissionName>"+
				  "<permissionDesc>" +permission.getPermissionDesc()+"</permissionDesc></permission>";
		
		logger.debug("permissionNode" + permissionNode);
		
		String xQuery =  "for $regperms in collection('cims')//cims/permissions " +
		 		 "return (insert node " + permissionNode + " into $regperms)";
		
		logger.debug("xQuery" + xQuery);
		
		String response = BaseX.httpGetWriter("rest?query=",xQuery);
		logger.debug("response " + response);
		return response;
	}

	@Override
	public String updatePermission(Permission permission) {
		logger.debug("Start updatePermission  " + permission);
		
		String xQuery = "for $regperms in collection('cims')//cims//permissions/permission "+
				"let $perm := $regperms/permissionId  return "+
				"if($perm = \""+permission.getPermissionId().toUpperCase()+"\") then"+
				"(replace value of node $regperms/permissionName  with \""+permission.getPermissionName()+"\" ,"+
				"replace value of node $regperms/permissionDesc  with \""+permission.getPermissionDesc()+"\") " + 
				"else  ()"  ;
		logger.debug("xQuery" + xQuery);
		String response = BaseX.httpGetWriter("rest?query=",xQuery);
		logger.debug("response " + response);
		return response;
	}

	private Permission getPermissionFromTokenizedString(String tokenizedPermissionString) {
		logger.debug("Start getPermissionFromTokenizedString  " + tokenizedPermissionString);

		Permission permission = new Permission();
		String[] tokens = tokenizedPermissionString.split(":");
		permission.setPermissionId(tokens[0]==null?"":tokens[0].trim());
		permission.setPermissionName(tokens[1]==null?"":tokens[1].trim());
		permission.setPermissionDesc(tokens[2]==null?"":tokens[2].trim());
		logger.debug("permission  " + permission);
		return permission;
	}
}