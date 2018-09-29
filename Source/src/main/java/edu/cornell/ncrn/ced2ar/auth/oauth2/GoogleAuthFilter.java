package edu.cornell.ncrn.ced2ar.auth.oauth2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.util.StringUtils;

import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.auth.OAUserDetail;

/**
 * Email address is the userid.
 * 
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team
 * 
 *         Authorization method is called when a user tries to authorize.
 *         Process of authentication is as follows A check is made to see if
 *         OAUTH2 provider (google) authorization code exists in the request. if
 *         authorization does not exists, one will be fetched by posting a call
 *         to authURL. This call will fetch the code and
 *         OAuth2ClientAuthenticationProcessingFilter will redirect back. With
 *         authorization code, attemptAuthorization will attempt to fetch
 *         Authentication Token from OAUTH2 provider (google) by posting a call
 *         to tokenURL. Upon successful authentication, an Authentication Token
 *         will be returned which contains email address of the user. The Token
 *         is passed to Authentication Manager to authenticate. Authentication
 *         Manager will pass the Token to OAuth2AuthProvider fetch the user
 *         Roles. if the authenticated user is not registered with CED2AR,
 *         usernameNotFoundException is thrown
 * 
 *         Sometimes Authentication token persists the session. In that case,
 *         making a call to tokenURL will result in exception. In this case, a
 *         post call will be made to personInfoURL to fetch the authenticated
 *         user email.
 *
 */

@PropertySource(value = { "classpath:ced2ar-web-config.properties" })
public class GoogleAuthFilter extends OAuth2ClientAuthenticationProcessingFilter {
	private static final Logger logger = Logger.getLogger(GoogleAuthFilter.class);
	protected OAuth2RestTemplate oauth2RestTemplate;
	protected Config config;
	protected Environment environment;

	private String googleDefaultFilterURL;
	private String authCode;

	private String googleClientId;
	private String googleClientSecret;
	private String googleAccessTokenURL;
	private String googleAuthURL;
	private String googleTokenURL;
	private String googlePreEstabledURL;
	private String googleInfoReq;
	private String googlePersonInfoURL;

	public GoogleAuthFilter(Config config, Environment env, String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		this.config = config;
		this.environment = env;
		populateOauth2Properties();
	}

	/**
	 * @param request
	 * @param response
	 * 
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		return attemptAuthenticationGoogle(request, response);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws AuthenticationException
	 * @throws IOException
	 * @throws ServletException
	 * 
	 *             This method checks for authCode for google oauth2. If
	 *             authorization code exists, it is used to fetch Authentication
	 *             Token; and authentication Token is used to get user email
	 *             address. email address is used to authenticate the user.
	 *
	 *             If authorization code does not exist, a post call is make to
	 *             authURL fetch one. If the Authentication Token already
	 *             exists, it is used to fetch user email address by posting a
	 *             call to personInfoURL
	 * 
	 */
	private Authentication attemptAuthenticationGoogle(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		logger.debug("AuthFilter Triggered. authURL URL =" + googleAuthURL);

		URI authorizationURI;
		try {
			authorizationURI = new URI(googleAuthURL);
		} catch (URISyntaxException e) {
			logger.debug("Error in creating authorization URL. ", e);
			throw new RuntimeException("Error in creating authorization URL. ", e);
		}
		SecurityContext context = SecurityContextHolder.getContext();
		logger.debug("Got security context context=" + context);
		String authCode = request.getParameter("code");
		logger.debug("authCode =" + authCode);
		logger.debug("authCode =" + request.getParameterMap());
		if (StringUtils.isEmpty(authCode)) {
			logger.debug(
					"There is NO authorization code in the request. Client authentication filter is setting dummy authentication in the security context and requsting the authorization code.");
			context.setAuthentication(getDummyAuthentication());
			OAuth2AccessToken accessToken = restTemplate.getAccessToken();
			if (accessToken != null) {
				logger.debug("Access token exists. Fetching user information using it.");
				ResponseEntity<Object> forEntity = restTemplate.getForEntity(googlePersonInfoURL, Object.class);
				logger.debug("Fetched person info from  " + googlePersonInfoURL + " forEntity: " + forEntity);
				String email = fetchEmailFromPersonInfoResponse(forEntity);
				AuthToken authToken = getAuthToken(email);
				authToken.setAuthenticated(false);
				logger.debug("Created authentication token with no roles " + authToken);
				return getAuthenticationManager().authenticate(authToken);
			} else {
				logger.debug("Access token does not exist. Fetch authentication Token");
				restTemplate.postForEntity(authorizationURI, null, Object.class);
				return null;
			}
		} else {
			try {
				logger.debug(
						"There is authorization code is present in the request. Making the authentication request with authorization Code.");
				ResponseEntity<Object> forEntity = restTemplate.getForEntity(googleTokenURL, Object.class);// Exception
																											// Cause
				logger.debug("User is successfully authenticated by the OAUTH2 provider.  " + forEntity);
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) forEntity.getBody();
				String email = (String) map.get("email");
				AuthToken authToken = getAuthToken(email);
				authToken.setAuthenticated(false);
				Authentication authentication = getAuthenticationManager().authenticate(authToken);
				// Authentication Object is set in security Context
				context.setAuthentication(authentication);
				// Authentication auth =
				// SecurityContextHolder.getContext().getAuthentication();//TODO:
				// Not sure what this was actually doing, never used
				return authentication;
			} catch (InvalidRequestException e) {
				// Thrown when open.access == true on first logon
				logger.error(googleTokenURL);
				logger.error(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * 
	 * @param forEntity
	 *            contains user information returned by the google.
	 * @return String email address of the person authenticated.
	 * 
	 *         This method extracts email address from forEntity and returns it.
	 * 
	 */
	private String fetchEmailFromPersonInfoResponse(ResponseEntity<Object> forEntity) {
		try {
			@SuppressWarnings("rawtypes")
			Map map = (Map) forEntity.getBody();

			@SuppressWarnings("unchecked")
			ArrayList<Map<String, String>> emails = (ArrayList<Map<String, String>>) map.get("emails");

			Map<String, String> emailMap = (Map<String, String>) emails.get(0);
			String email = (String) emailMap.get("value");
			return email;
		} catch (Exception e) {
			// this will only happen if google changes the contents of
			// forEntirty
			throw new RuntimeException("Error parsing ResponseEntity" + forEntity, e);
		}
	}

	/**
	 * This method created a OAuth2Token using the email passed. The returned
	 * OAuth2Token will not have any roles. UserDetails service is used to fill
	 * in roles
	 * 
	 * @return String email
	 */
	private AuthToken getAuthToken(String email) {
		List<GrantedAuthority> useRoles = new ArrayList<GrantedAuthority>();
		List<String> roles = new ArrayList<String>();
		// All users are added ROLE_USER group
		if (config.getOpenAccess().equals("true")) {
			roles.add("ROLE_USER");
		}
		for (String role : roles) {
			GrantedAuthority authority = new SimpleGrantedAuthority(role);
			useRoles.add(authority);
		}
		OAUserDetail justAuthenticatedUser = new OAUserDetail(email, useRoles);
		AuthToken authenticationToken = new AuthToken(justAuthenticatedUser);
		return authenticationToken;
	}

	/**
	 * Oauth2 providers (Google) require an authenticated post request to get
	 * authorization code. This method creates dummy authorization for this
	 * purpose.
	 * 
	 * @return Authentication Object.
	 */
	private Authentication getDummyAuthentication() {
		logger.debug("Initializing Default Roles");
		List<GrantedAuthority> defaultRoles = new ArrayList<GrantedAuthority>(1);
		logger.debug("Creating dummy authentication object");
		String dummyUser = RandomStringUtils.random(8, true, true);
		String dummyPassword = RandomStringUtils.random(8, true, true);
		String dummyRole = RandomStringUtils.random(8, true, true);
		GrantedAuthority defaultRole = new SimpleGrantedAuthority(dummyRole);
		defaultRoles.add(defaultRole);
		Authentication dummyAuthentication = new UsernamePasswordAuthenticationToken(dummyUser, dummyPassword,
				defaultRoles);
		logger.debug("Returning dummy authentication object: " + dummyAuthentication);
		return dummyAuthentication;
	}

	public OAuth2RestTemplate getOauth2RestTemplate() {
		return oauth2RestTemplate;
	}

	public void setOauth2RestTemplate(OAuth2RestTemplate oauth2RestTemplate) {
		this.oauth2RestTemplate = oauth2RestTemplate;
	}

	@Autowired
	@Override
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
		logger.debug("Set authentication manager authenticationManager:" + authenticationManager);
	}

	public OAuth2ProtectedResourceDetails getGoogleAuth2ProtectedResourceDetails() {
		List<String> scopes = new ArrayList<String>();
		AuthorizationCodeResourceDetails auth2ProtectedResourceDetails = new AuthorizationCodeResourceDetails();
		auth2ProtectedResourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);
		auth2ProtectedResourceDetails.setAuthenticationScheme(AuthenticationScheme.form);
		auth2ProtectedResourceDetails.setGrantType(authCode);
		auth2ProtectedResourceDetails.setClientId(googleClientId);
		auth2ProtectedResourceDetails.setClientSecret(googleClientSecret);
		auth2ProtectedResourceDetails.setAccessTokenUri(googleAccessTokenURL);
		scopes.add(googleInfoReq);
		auth2ProtectedResourceDetails.setScope(scopes);
		auth2ProtectedResourceDetails.setUserAuthorizationUri(googleAuthURL);
		auth2ProtectedResourceDetails.setUseCurrentUri(false);
		auth2ProtectedResourceDetails.setPreEstablishedRedirectUri(googlePreEstabledURL);
		return auth2ProtectedResourceDetails;
	}

	/**
	 * Populates the properties needed for Oauth2 authentication if the
	 * authentication mechanism is Oauth2
	 * 
	 * @param env
	 */
	private void populateOauth2Properties() {
		if (config.isAuthenticationTypeOauth2() && config.isGoogleOauth2Supported()) {
			googleDefaultFilterURL = environment.getProperty("googleDefaultFilterURL");
			authCode = environment.getProperty("authCode");

			googleClientId = environment.getProperty("googleClientId");
			googleClientSecret = environment.getProperty("googleClientSecret");
			googleAccessTokenURL = environment.getProperty("googleAccessTokenURL");
			googleAuthURL = environment.getProperty("googleAuthURL");
			googleTokenURL = environment.getProperty("googleTokenURL");
			googlePreEstabledURL = environment.getProperty("googlePreEstabledURL");
			googleInfoReq = environment.getProperty("googleInfoReq");
			googlePersonInfoURL = environment.getProperty("googlePersonInfoURL");
		}
	}
}