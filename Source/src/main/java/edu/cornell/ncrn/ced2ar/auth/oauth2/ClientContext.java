package edu.cornell.ncrn.ced2ar.auth.oauth2;

import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * Always set access token to null. This way we can request authentication from google every time
 * and never use Access Token to get user info.
 * @see org.springframework.security.oauth2.client.DefaultOAuth2ClientContext#getAccessToken()  
 * 
 *  This is needed because
 *  1. User 'A' logs in using Google.  Google will issue an Access Token that is saved in the session.
 *  2. User 'A' logs out of Google and closes the browser and hands the computer to user 'B' thinking that he logged out. 
 *  3. When user 'B' access restricted resources in CED2AR, AuthFilter will find Authentication Token in 
 *     the session and uses it and login in as User 'A' by passing Google login page. 
 *  
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 

 */

public class ClientContext extends DefaultOAuth2ClientContext{
	private static final long serialVersionUID = -6269417799638098233L;

	public ClientContext(AccessTokenRequest accessTokenRequest) {
		super(accessTokenRequest);
	}
	
	public OAuth2AccessToken getAccessToken() {
		setAccessToken(null);
		return super.getAccessToken();
	}
}