package edu.ncrn.cornell.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.RoleDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.Role;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.User;
/**
 * Implementation class of RoleDao for BaseX  datastore.
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 * 
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class BaseXRoleDaoImpl implements RoleDao{
	private static final Logger logger = Logger.getLogger(BaseXRoleDaoImpl.class);
	/**
	 *  BaseX implementation of getRole
	 *  Returns Role object identified by Role Id.  Retirn null if none found
	 */
	@Override
	public Role getRole(String roleId) {
		logger.debug("Start getRole " + roleId);
		String xQuery = "for $regrole in collection('cims')//cims//roles//role  " + 
				"where $regrole/roleId = \""+roleId.toUpperCase()+"\" " +
				"return concat($regrole/roleId/text() ,"
				+ "\":\",$regrole/roleName/text(),\":\",$regrole/roleDesc/text())";
		
		logger.debug("Query " + xQuery);
		String roleInfo = BaseX.getXML(xQuery);
		logger.debug("roleInfo " + roleInfo);
		
		if(StringUtils.isEmpty(roleInfo)) 
			return null;
		else 
			return getRoleFromTokenizedString(roleInfo);
	}

	/**
	 * BaseX implementation of getRoles()
	 * Returns list of Roles. Null if no roles are available.
	 */
	
	@Override
	public List<Role> getRoles() {
		logger.debug("Start getRoles ");
		String xQuery =  "for $regroles in collection('cims')//cims//roles/role "+
						 "return concat($regroles/roleId/text() ," +
						 "\":\",$regroles/roleName/text(),\":\",$regroles/roleDesc/text(),\":XX\")";
		logger.debug("Query " + xQuery);
		String rolesInfo = BaseX.getXML(xQuery);
		logger.debug("rolesInfo " + rolesInfo);
		
		if(StringUtils.isEmpty(rolesInfo)) {
			return null;
		}
		else{
			List<Role> roles = new ArrayList<Role>();
			rolesInfo = rolesInfo.trim();
			String[] rolesString = rolesInfo.split(":XX ");
			logger.debug("rolesString " + rolesString);
			for(int i=0; i<rolesString.length;i++) {
				roles.add(getRoleFromTokenizedString(rolesString[i]));
			}
			logger.debug("roles " + roles);
			return roles;
		}
	}
	/**
	 *	BaseX implementaion of addRole.  If role already exists, returns a message. No action is done 
	 *	Role is added to the BaseX datastore
	 */
	@Override
	public String addRole(Role role) {
		logger.debug("Start addRole " + role);
		if(this.getRole(role.getRoleId())!=null) {
			logger.debug("Role already exists ");
			return ERROR_ROLE_EXISTS;
		}
		
		String roleNode = "<role>" +
								"<roleId>" + role.getRoleId().toUpperCase() + "</roleId>" +
								"<roleName>" + role.getRoleName() + "</roleName>" +
								"<roleDesc>" + role.getRoleDesc() + "</roleDesc>" +
				 		  "</role>";
		
		logger.debug("roleNode " + roleNode);
		
		String xQuery =  "for $regroles in collection('cims')//cims/roles " +
		 		 		 "return (insert node " + roleNode + "  into $regroles)";
		
		logger.debug("xQuery " + xQuery);
		String response = BaseX.httpGetWriter("rest?query=",xQuery);
		logger.debug("response " + response);
		return response;

	}

	/**
	 * BaseX implementation of updateRole.
	 * Updates the role. Role name will not be updated.
	 * If no role exists, this method does not perform any action.
	 */
	@Override
	public String updateRole(Role role) {
		logger.debug("Start updateRole " + role);
		
		String xQuery = "for $regroles in collection('cims')//cims//roles/role "+
				"let $role := $regroles/roleId  return "+
				"if($role = \""+role.getRoleId().toUpperCase()+"\") then"+
				"(replace value of node $regroles/roleName  with \""+role.getRoleName()+"\" ,"+
				"replace value of node $regroles/roleDesc  with \""+role.getRoleDesc()+"\") " + 
				"else  ()"  ;
		logger.debug("xQuery " + xQuery);
		
		String response = BaseX.httpGetWriter("rest?query=",xQuery);
		logger.debug("response " + response);
		return response;
	}
	/**
	 * Parses the role string and creates a Role object and Returns
	 * @param tokenizedRoleString String seperated by ":" as delimiter
	 * @return Role object
	 */
	private Role getRoleFromTokenizedString(String tokenizedRoleString) {
		logger.debug("Start getRoleFromTokenizedString " + tokenizedRoleString);
		Role role = new Role();
		String[] tokens = tokenizedRoleString.split(":");
		logger.debug("tokens " + tokens);
		role.setRoleId(tokens[0]==null?"":tokens[0].trim());
		role.setRoleName(tokens[1]==null?"":tokens[1].trim());
		role.setRoleDesc(tokens[2]==null?"":tokens[2].trim());
		logger.debug("role " + role);
		return role;
	}

}
