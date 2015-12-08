package edu.ncrn.cornell.ced2ar.security;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.auth.Service;
import edu.ncrn.cornell.ced2ar.auth.oauth2.AuthProvider;
import edu.ncrn.cornell.ced2ar.auth.oauth2.OrcidAuthFilter;

/**
 * This class configures the properties for OAUTH2 Authentication for Google.
 * Properties are read from the properties file.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

@Configuration
@PropertySource(value = { "classpath:ced2ar-web-config.properties" })
public class OrcidOauth2Config{
	
	@Autowired
	Environment env;
	
	@Autowired
	Config config;
	
	@Autowired
	private OAuth2ClientContextFilter oAuth2ClientContextFilter;

	@Resource
	@Qualifier("accessTokenRequest")
	private AccessTokenRequest accessTokenRequest;
	private String clientId;
	private String clientSecret;
	private String accessTokenURL;
	private String authURL;
	private String authCode;
	private String preEstabledURL;
	private String orcidDefaultFilterURL;
	private String infoReq;


    @Bean(name ="orcidOAuth2ClientAuthenticationProcessingFilter" )
	public OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter() {
    	populateOauth2Properties();
    	
    	if(StringUtils.isEmpty(orcidDefaultFilterURL))
    		orcidDefaultFilterURL = "/";
    	else
    		orcidDefaultFilterURL = "/"  +orcidDefaultFilterURL;

    	OAuth2RestTemplate oAuth2RestTemplate =  new OAuth2RestTemplate(orcidAuth2ProtectedResourceDetails(), new DefaultOAuth2ClientContext(accessTokenRequest));
    	OrcidAuthFilter authFilter = new OrcidAuthFilter(config,env,orcidDefaultFilterURL);
    	authFilter.setRestTemplate(oAuth2RestTemplate);
		return authFilter;
	}

    @Bean(name = "authProvider")
	public AuthProvider AuthProvider() {
		AuthProvider authProvider = new AuthProvider();
		return authProvider;
	}


    @Bean(name ="userDetailService")
    public Service getUserDetailService(){
        return new Service();
    }
    
    /**
     * This bean initializes all the properties needed to access Oauth2 provider (Google)
     */
    @Bean
	public OAuth2ProtectedResourceDetails orcidAuth2ProtectedResourceDetails() {
    	List<String> scopes = new ArrayList<String>();
		AuthorizationCodeResourceDetails auth2ProtectedResourceDetails = new AuthorizationCodeResourceDetails();
		auth2ProtectedResourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);
		auth2ProtectedResourceDetails.setAuthenticationScheme(AuthenticationScheme.form);
		auth2ProtectedResourceDetails.setGrantType(authCode);
		auth2ProtectedResourceDetails.setClientId(clientId);
		auth2ProtectedResourceDetails.setClientSecret(clientSecret);
		auth2ProtectedResourceDetails.setAccessTokenUri(accessTokenURL);
		scopes.add(infoReq);
		auth2ProtectedResourceDetails.setScope(scopes);
		auth2ProtectedResourceDetails.setUserAuthorizationUri(authURL);
		auth2ProtectedResourceDetails.setUseCurrentUri(false);
		auth2ProtectedResourceDetails.setPreEstablishedRedirectUri(preEstabledURL);
		return auth2ProtectedResourceDetails;
	}
 
    private void populateOauth2Properties() {
    	if(config.isAuthenticationTypeOauth2() && config.isOrcidOauth2Supported()) {
    		this.orcidDefaultFilterURL = env.getProperty("orcidDefaultFilterURL");
    		this.authCode = env.getProperty("authCode");
    		this.clientId = env.getProperty("orcidClientId");
    		this.clientSecret = env.getProperty("orcidClientSecret");
    		this.accessTokenURL = env.getProperty("orcidAccessTokenURL");
    		this.authURL = env.getProperty("orcidAuthURL");
    		this.preEstabledURL = env.getProperty("orcidPreEstabledURL");
    		this.infoReq = env.getProperty("orcidInfoReq");
    	}
    }  
}