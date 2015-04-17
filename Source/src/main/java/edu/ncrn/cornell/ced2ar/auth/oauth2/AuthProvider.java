package edu.ncrn.cornell.ced2ar.auth.oauth2;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.auth.OAUserDetail;
import edu.ncrn.cornell.ced2ar.auth.Service;

/**
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 * 
 * When a user is authenticated by OAUTH2 provider (Google) we are fetching email address 
 * of the user and using it as userId in CED2AR 
 * 
 * Purpose of this class is to fetch the details such as Role(s) of the 
 * just authenticated user and add it to the token, if the user is registered.
 * 
 * If the user is not a registered user, this class will throw userNotFoundException 
 */

public class AuthProvider  implements AuthenticationProvider {
	private static final Logger logger = Logger.getLogger(AuthProvider.class);
	
	@Autowired(required = true)
	private Service userDetailService;
	
	/**
	 * Fetch roles that the user is authorized and add them to the authentication token and return it, if the user is registered.
	 * If the user is not registered, throw  usernameNotFoundException
	 * (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.debug("Authentication Started");
		AuthToken token = (AuthToken) authentication;
		logger.debug("OAuth2Token = " + token);
		OAUserDetail user = (OAUserDetail) token.getPrincipal();		
		try{
			user = (OAUserDetail) userDetailService.loadUserByUsername(user.getUsername());
			logger.debug("User with token  " + token + " is successfully authorized and a registered user of CED2AR. Assigning roles to the user ...");
			token = new AuthToken(user);
			token.setAuthenticated(true);
			logger.debug("User with token " + token + " logged in ");
		}catch(UsernameNotFoundException usernameNotFoundException){
			//All users are added ROLE_USER group
			Config config = Config.getInstance();
			if(config.getOpenAccess().equals("true")){
				//token = new AuthToken(user);
				token.setAuthenticated(true);
			}else{
				logger.debug("User trying google/login is not registered");
				token = null;
				throw usernameNotFoundException;
			}
		}
		return token;
	}

	/**
	 * 	Sets this class to accept OAuth2Token
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return AuthToken.class.isAssignableFrom(authentication);
	}
}