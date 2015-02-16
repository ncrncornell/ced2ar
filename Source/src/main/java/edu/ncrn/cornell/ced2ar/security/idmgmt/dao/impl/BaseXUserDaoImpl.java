package edu.ncrn.cornell.ced2ar.security.idmgmt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.security.idmgmt.dao.UserDao;
import edu.ncrn.cornell.ced2ar.security.idmgmt.model.User;
/**
 * Implementation class of UserDao for BaseX datastore.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class BaseXUserDaoImpl implements UserDao {
	private static final Logger logger = Logger.getLogger(BaseXUserDaoImpl.class);
	
	public String getCIMDump() {
		return "";
	}
	/**
	 * BaseX implementation of getUser. Fetches user info from BaseX and returns User Object or null
	 * Query Example:  http://localhost:8080/BaseX73/rest/?command=xquery for $reguser in cllection('cims')//cims//users//user  where $reguser/userId = "kvrayudu@gmail.com"  and $reguser/activeUser = "Y" return concat($reguser/userId/text() ,":",$reguser/firstName/text(),":",$reguser/lastName/text(),":",$reguser/activeUser/text())
	 * 	This method retrieves only active user.
	 */
	@Override
	public User getUser(String userId) {
		logger.debug("Start getUser method " + userId);
		return getUser(userId,false);  // Fetch if the user is active.
	}

	/**
	 * BaseX implementation of getUser. Fetches user info from BaseX and returns User Object or null
	 * This method retrieves both active of inactive users based on the value of parameter.
	 */
	@Override
	public User getUser(String userId, boolean includeInactive) {
		logger.debug("Start getUser method " + userId + ":" + includeInactive);
		String xQuery  = getUserQuery(userId, includeInactive);
		logger.debug("Query " + xQuery);
		String userInfo = BaseX.getXML(xQuery);
		logger.debug("userInfo " + userInfo);
		if(StringUtils.isEmpty(userInfo)) 
			return null;
		else 
			return getUserFromTokenizedString(userInfo);
	}
	
	
	/**
	 * BaseX implementation of getUsers. Returns all active users.  Return null if there are noo users
	 */
	@Override
	public List<User> getUsers() {
		logger.debug("Start getUsers method " );
		return getUsers(false);
	}
	
	/**
	 * BaseX implementation of getUsers.  Return all active or all users based on the paramter.
	 * Returns null if there are no users in the baseX.
	 */
	@Override
	public List<User> getUsers(boolean includeInactive) {
		logger.debug("Start getUsers method " + includeInactive);
		String xQuery = this.getUsersQuery(includeInactive);  
		logger.debug("Query " + xQuery);
		String usersInfo = BaseX.getXML(xQuery);
		logger.debug("usersInfo " + usersInfo);
		if(StringUtils.isEmpty(usersInfo)) {
			return null;
		}
		else{
			List<User> users = new ArrayList<User>();
			String[] usersString = usersInfo.split(" ");
			logger.debug("usersString " + usersString);
			for(int i=0; i<usersString.length;i++) {
				users.add(getUserFromTokenizedString(usersString[i]));
			}
			return users;
		}
	}

	/**
	 *  BaseX implementation of addUser.  Adds user to datastore. If the user already exists, this method return an error message
	 */
	@Override
	public String addUser(User user) {
		logger.debug("Start addUser method " + user);
		if(getUser(user.getUserId(),true) != null) { // Verify if the user already exists
			logger.debug("User already exists");
			return ERROR_USER_EXISTS;
		}
		String userNode = "<user><userId>" +user.getUserId().toLowerCase()+"</userId>"+
						  "<firstName>" +user.getFirstName()+"</firstName>"+
						  "<lastName>" +user.getLastName()+"</lastName>"+
						  "<activeUser>" +user.getActiveUserAsString()+"</activeUser></user>";
		logger.debug("userNode " + userNode);
		
		String xQuery =  "for $regusers in collection('cims')//cims/users " +
				 		 "return (insert node " + userNode + " into $regusers)";
		logger.debug("xQuery " + xQuery);
		
		String response = BaseX.httpGetWriter("rest?query=",xQuery);
		logger.debug("response " + response);
		return response;
	}

	
	/**
	 *  BaseX implementation of updateUser.  updates user in the datastore. If the user doesn't exist, this method will not have any effect
	 */
	
	@Override
	public String updateUser(User user) {
		logger.debug("Start updateUser method " + user);
		String xQuery = "for $regusers in collection('cims')//cims//users/user "+
						"let $user := $regusers/userId  return "+
						"if($user = \""+user.getUserId().toLowerCase()+"\") then"+
						"(replace value of node $regusers/activeUser  with \""+user.getActiveUserAsString()+"\" ,"+
						"replace value of node $regusers/firstName  with \""+user.getFirstName()+"\"," +
						"replace value of node $regusers/lastName  with \""+user.getLastName()+"\") " + 
						"else  ()"  ;
		logger.debug("xQuery " + xQuery);
		String response = BaseX.httpGetWriter("rest?query=",xQuery);
		logger.debug("response " + response);
		return response;
	}
	
	private User getUserFromTokenizedString(String tokenizedUserString) {
		logger.debug("Start getUserFromTokenizedString method " + tokenizedUserString);
		User user = new User();
		String[] tokens = tokenizedUserString.split(":");
		logger.debug("tokens " + tokens);
		user.setUserId(tokens[0]==null?"":tokens[0].trim());
		user.setFirstName(tokens[1]==null?"":tokens[1].trim());
		user.setLastName(tokens[2]==null?"":tokens[2].trim());
		user.setActiveUser(tokens[3]==null?false:tokens[3].trim().equalsIgnoreCase("Y"));
		logger.debug("user " + user);
		return user;
	}
	
	private String getUserQuery(String userId, boolean includeInactive) {
		logger.debug("Start getUser method " + userId);
		String xQuery ="";
		
		if(includeInactive) { // Query String for both active and inactive users
			xQuery = "for $reguser in collection('cims')//cims//users//user  " + 
					"where $reguser/userId = \""+userId.toLowerCase()+"\""+  
					"return concat($reguser/userId/text() ,"
					+ "\":\",$reguser/firstName/text(),\":\",$reguser/lastName/text(),"
					+ "\":\",$reguser/activeUser/text())";
		}
		else { // Query String for Active users only.
			xQuery = "for $reguser in collection('cims')//cims//users//user  " + 
					"where $reguser/userId = \""+userId.toLowerCase()+"\"  and $reguser/activeUser = \"Y\" " +
					"return concat($reguser/userId/text() ,"
					+ "\":\",$reguser/firstName/text(),\":\",$reguser/lastName/text(),"
					+ "\":\",$reguser/activeUser/text())";
			
		}
		logger.debug("Query " + xQuery);
		return xQuery;
		
	}

	
	private String getUsersQuery(boolean includeInactive) {
		String xQuery="";
		if(includeInactive) { 
			xQuery =  "for $regusers in collection('cims')//cims//users/user "+
					 "return concat($regusers/userId/text() ,\":\"," +
					 "$regusers/firstName/text(),\":\","+
					 "$regusers/lastName/text()," + 
					 "\":\",$regusers/activeUser/text())";
		}
		else {
			xQuery =  "for $regusers in collection('cims')//cims//users/user "+
					"where $regusers/activeUser = \"Y\" " +
					 "return concat($regusers/userId/text() ,\":\"," +
					 "$regusers/firstName/text(),\":\","+
					 "$regusers/lastName/text()," + 
					 "\":\",$regusers/activeUser/text())";
		}
		
		return xQuery;
	}
	
}
