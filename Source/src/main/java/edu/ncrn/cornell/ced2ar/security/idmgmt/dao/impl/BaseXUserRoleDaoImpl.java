package edu.ncrn.cornell.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.RoleDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.UserDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.UserRoleDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Role;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.User;


/**
 * Implementation class of UserRoleDao for BaseX datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */


public class BaseXUserRoleDaoImpl implements UserRoleDao{
	private static final Logger logger = Logger.getLogger(BaseXUserRoleDaoImpl.class);
	@Autowired
	UserDao userDao;
	@Autowired
	RoleDao roleDao;
	
	@Override
	public List<String> getRoles(String userId) {
		logger.debug("Start getRoles method " + userId);
		User user = userDao.getUser(userId);
		if(user ==null || !user.isActiveUser()) { //if the user is no active or present return null
			return null;
		}
		
		String xQuery =  "for $reguserrole in collection('cims')"+
						 "//cims//userRoles//userRole[contains(userId, \""+userId.toLowerCase()+"\")]/roles/roleId " +
						 "return concat($reguserrole/text() ,\":\")";
		
		logger.debug("Query " + xQuery);
		String roles= BaseX.getXML(xQuery);
		logger.debug("roles " + roles);
		if(StringUtils.isEmpty(roles)) {
			return null;
		}
		else{
			List<String> roleList = new ArrayList<String>();
			String[] rolesArray = roles.trim().split(":");
			logger.debug("rolesArray " + rolesArray);
			for(int i=0; i<rolesArray.length;i++) {
				roleList.add(rolesArray[i].trim());
			}
			logger.debug("roleList " + roleList);
			return roleList;
		}
	}

	public boolean isUserInRole(String userId,String roleId) {
		logger.debug("Start isUserInRole method " + userId + ":" + roleId);
		User user = userDao.getUser(userId);
		if(user ==null || !user.isActiveUser()) { //if the user is no active or present return null
			return false;
		}

		boolean returnValue = false;
		logger.debug("Start. isUserInRole userId:roleId=" +   userId+":"+roleId);
		String xQuery =  "for $reguserrole in collection('cims')"+
				 "//cims//userRoles//userRole[contains(userId, \""+userId.toLowerCase()+"\")]/roles/roleId = \"" + 
				 roleId.toUpperCase() +  "\" return $reguserrole";
		logger.debug("Query = " + xQuery);
		long startTime = System.currentTimeMillis();
		String inRole= BaseX.getXML(xQuery);
		long endTime = System.currentTimeMillis();
		long elapsedTime =endTime-startTime;
		
		logger.debug("Time taken by the query = "+ elapsedTime + " milli-seconds");
		
		if(!StringUtils.isEmpty(inRole) && inRole.trim().equalsIgnoreCase("true") ) 
			returnValue = true;

		return returnValue;
	}
	@Override
	public String removeRole(String userId,String roleId) {
		throw new RuntimeException("Unimplemented Method");
	}

	@Override
	public String addRole(String userId,String roleId) {
		logger.debug("Start addRole method " + userId + ":" + roleId);
		User user = userDao.getUser(userId);
		if(user ==null || !user.isActiveUser()) { //if the user is no active or present return do not process further
			return "";
		}
		Role role = roleDao.getRole(roleId);
		if(role == null || role.getRoleId().isEmpty()) { // if Role doesn't exist, return without further processing
			return "";
		}
		if(isUserInRole(userId,roleId)){
			return ERROR_USER_IS_IN_ROLE_ALREADY;
		}
		
		String roleNode = "<roleId>" +roleId.toUpperCase()+"</roleId>";
		logger.debug("roleNode = " + roleNode);
		String xQuery =  "for $reguserrole in collection('cims')//cims//userRoles//userRole " +
						 "where   $reguserrole/userId = \""+userId.toLowerCase()+"\" "+
				 		 "return (insert node " + roleNode + " into $reguserrole/roles)";
		
		logger.debug("xQuery = " + xQuery);
		String response = BaseX.httpGetWriter("rest?query=",xQuery);
		logger.debug("response = " + response);
		return response;
	}


}
