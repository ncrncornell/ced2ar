package edu.ncrn.cornell.ced2ar.auth;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import edu.ncrn.cornell.ced2ar.security.idmgmt.service.UserService;

/**
 * User Service class that fetches user information and sets the user in the session
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */
public class Service implements UserDetailsService, AuthenticationUserDetailsService<OpenIDAuthenticationToken> {
	
	
	@Autowired
	private HttpSession session;
	
	private static final Logger logger = Logger.getLogger(Service.class);
	
	@Autowired
	private UserService userService;
	
	//private AuthoriedUsers authorizedUserProperties = new AuthoriedUsers();
	
	/**
	 * (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.AuthenticationUserDetailsService#loadUserDetails(org.springframework.security.core.Authentication)
	 * This method fetches user details from Openid token.
	 * Using userid, this method fetches user roles.  If the user is not found  UsernameNotFoundException is thrown
	 * 
	 * This method is only used by OpenId Authentication
	 * 
	 */
	public UserDetails loadUserDetails(OpenIDAuthenticationToken token) throws UsernameNotFoundException {
		logger.debug("Start loadUserDetails");
        String id = token.getIdentityUrl();
        logger.debug("id = " + id);
        String email = null;
        String firstName = null;
        String lastName = null;
        String fullName = null;
        
        List<OpenIDAttribute> attributes = token.getAttributes();
        for(OpenIDAttribute attribute : attributes){
            if(attribute.getName().equals("email")){
                email = attribute.getValues().get(0);
            }
            if(attribute.getName().equals("firstname")){
                firstName = attribute.getValues().get(0);
            }
            if(attribute.getName().equals("lastname")){
                lastName = attribute.getValues().get(0);
            }
        }
        if(StringUtils.isEmpty(email)){  // this should not happen 
        	throw new RuntimeException("Email not found");
        }
        	
        boolean authorized = userService.isUserRegistered(email);
        
        logger.debug("email:firstName:lastName:authorized="+email+":"+firstName+":"+lastName+":"+authorized);
        if(authorized){
            StringBuilder fullNameBldr = new StringBuilder();
            if (firstName != null) {
                fullNameBldr.append(firstName);
            }
            if (lastName != null) {
                fullNameBldr.append(" ").append(lastName);
            }
            fullName = fullNameBldr.toString();

            List<GrantedAuthority> authorities = null;
            
            boolean isAdmin = userService.isUserInAdminRole(email);
            //logger.debug("email:firstName:lastName:authorized="+email+":"+firstName+":"+lastName+":"+authorized);
            if(isAdmin){
            	authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,ROLE_USER");
            }
            else{
            	authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }
            
            OAUserDetail user = new OAUserDetail(id, authorities);
            user.setEmail(email);
            user.setName(fullName);
            return user;
        }else{
        	logger.debug(email + " does not have permission to view this page");
        	throw new UsernameNotFoundException("You have authenticated, but are not authorized to use CED2AR");
        }
	}

	/**
	 * @param String userId 
	 * This method fetches user roles using the user id.
	 */
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        if(StringUtils.isEmpty(userId)){  // this should not happen 
        	throw new RuntimeException("UserId (which is email address for ced2ar implementation) is empty.");
        }
    	return fetchUserDetails( userId);
	}
    
    /**
     * @param userId
     * @return UserDetails Object.  Contains userId and user Roles
     * @throws UsernameNotFoundException
     * 
     * Fetches user roles from the properties file and creates a UserDetail object to return
     */
    private UserDetails fetchUserDetails(String userId) throws UsernameNotFoundException {
		logger.debug("Start fetchUserDetails for userId " + userId );
        boolean authorized = userService.isUserRegistered(userId);
        logger.debug("userId:authorized="+userId+":"+authorized);
        session.setAttribute("userEmail", userId);
       
        if(authorized){
            List<GrantedAuthority> authorities = null;
            boolean isAdmin = userService.isUserInAdminRole(userId);
            logger.debug("userId:isAdmin="+userId+":"+isAdmin);
            if(isAdmin){
            	authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,ROLE_USER");
            }
            else{
            	authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }
            OAUserDetail user = new OAUserDetail(userId, authorities);
            user.setEmail(userId);
            user.setName(userId);
            session.setAttribute("userAuth", true);
            return user;
        }else{
        	throw new UsernameNotFoundException("User "+userId+" is authenticated,  but not authorized to use CED2AR. Please use the registartion form");        	
        }
    }
}