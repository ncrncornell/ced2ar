package edu.ncrn.cornell.ced2ar.auth.oauth2;

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
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.auth.OAUserDetail;

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
 *         Process of authentication is as follows: A check is made to see if
 *         OAUTH2 provider (ORCID) authorization code exists in the request. If
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
public class OrcidAuthFilter extends OAuth2ClientAuthenticationProcessingFilter {
	private static final Logger logger = Logger.getLogger(OrcidAuthFilter.class);
	protected OAuth2RestTemplate oauth2RestTemplate;
	protected Config config;
	protected Environment environment;

	private String orcidAuthURL;
	private String orcidTokenURL;
	private String orcidPersonInfoURL;

	public OrcidAuthFilter(Config config, Environment env, String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		this.config = config;
		this.environment = env;
		populateOauth2Properties();
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws AuthenticationException
	 * @throws IOException
	 * @throws ServletException
	 *             This method checks for authCode for ORCID oauth2 If
	 *             authorization code exists, it is used to fetch Authentication
	 *             Token; and authentication Token is used to get user email
	 *             address. email address is used to authenticate the user.
	 *
	 *             If authorization code does not exist, a post call is make to
	 *             authURL fetch one.
	 */
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		logger.debug("ORCID AuthFilter Triggered. authURL URL =" + orcidAuthURL);
		URI authorizationURI;
		try {
			authorizationURI = new URI(orcidAuthURL);
		} catch (URISyntaxException e) {
			logger.debug("Error in creating authorization URL. ", e);
			throw new RuntimeException("Error in creating authorization URL. ", e);
		}
		SecurityContext context = SecurityContextHolder.getContext();
		logger.debug("Got security context context=" + context);
		String authCode = request.getParameter("code");
		logger.debug("ORCID authCode =" + authCode);
		logger.debug("ORCID authCode =" + request.getParameterMap());
		if (StringUtils.isEmpty(authCode)) {
			logger.debug(
					"There is NO authorization code in the request. Client authentication filter is setting dummy authentication in the security context and requsting the authorization code.");
			context.setAuthentication(getDummyAuthentication());
			logger.debug("Remove any existing access tokens from the context.");
			restTemplate.getOAuth2ClientContext().setAccessToken(null);
			logger.debug("Fetching access token.  This should cause a redirect.");
			OAuth2AccessToken accessToken = restTemplate.getAccessToken();
			logger.debug("Fetched access token. Redirect failed.");
			return null;
		} else {
			try {
				logger.debug(
						"ORCID There is authorization code is present in the request. Making the authentication request with authorization Code.");
				OAuth2AccessToken accessToken = restTemplate.getAccessToken();
				logger.debug("ORCID successfully fetched access token");
				Map orcidMap = accessToken.getAdditionalInformation();
				logger.debug("Additional info from access token. " + orcidMap);
				String orcidId = (String) orcidMap.get("orcid");
				String orcidName = (String) orcidMap.get("name");
				logger.debug("ORCID Retrieved orcid id from accessToken " + orcidId);
				logger.debug("ORCID Retrieved orcidNamefrom accessToken " + orcidName);
				AuthToken authToken = getAuthToken(orcidId, orcidName);
				authToken.setAuthenticated(false);
				Authentication authentication = getAuthenticationManager().authenticate(authToken);
				context.setAuthentication(authentication);
				return authentication;
			} catch (InvalidRequestException e) {
				logger.error(orcidTokenURL);
				logger.error(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * @param forEntity
	 * @return email address This method fetched email address from ORCID
	 *         response
	 */
	private String fetchEmailFromPersonInfoResponseOricd(ResponseEntity<Object> forEntity) {
		try {
			Map map = (Map) forEntity.getBody();
			Map orcidProfile = (Map) map.get("orcid-profile");
			Map orcidBio = (Map) orcidProfile.get("orcid-bio");
			Map contactDetails = (Map) orcidBio.get("contact-details");
			ArrayList<Map<String, String>> emails = (ArrayList<Map<String, String>>) contactDetails.get("email");
			Map<String, String> emailMap = (Map<String, String>) emails.get(0);
			String email = (String) emailMap.get("value");
			return email;
		} catch (Exception e) {
			throw new RuntimeException(
					"Error parsing ResponseEntity.  Unable to retrieve user email address from ORCID" + forEntity, e);
		}
	}

	/**
	 * This method created a OAuth2Token using the email passed. The returned
	 * OAuth2Token will not have any roles. UserDetails service is used to fill
	 * in roles
	 * 
	 * @return String email
	 */
	private AuthToken getAuthToken(String orcidId, String name) {
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
		OAUserDetail justAuthenticatedUser = new OAUserDetail(orcidId, useRoles);
		justAuthenticatedUser.setName(name);
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

	/**
	 * Populates the properties needed for Oauth2 authentication if the
	 * authentication mechanism is Oauth2
	 * 
	 * @param env
	 */
	private void populateOauth2Properties() {
		if (config.isAuthenticationTypeOauth2() && config.isOrcidOauth2Supported()) {
			orcidAuthURL = environment.getProperty("orcidAuthURL");
			orcidTokenURL = environment.getProperty("orcidTokenURL");
			orcidPersonInfoURL = environment.getProperty("orcidPersonInfoURL");
		}
	}
}