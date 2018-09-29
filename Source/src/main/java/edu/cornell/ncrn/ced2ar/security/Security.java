package edu.cornell.ncrn.ced2ar.security;

import javax.servlet.ServletContext;

import org.apache.commons.lang.RandomStringUtils;
import org.basex.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.auth.Service;
import edu.cornell.ncrn.ced2ar.auth.oauth2.AuthProvider;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.PermissionDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.RoleDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.RolePermissionDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.UserDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.UserRoleDao;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.BaseXPermissionDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.BaseXRoleDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.BaseXRolePermissionDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.BaseXUserDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.BaseXUserRoleDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.PropertiesPermissionDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.PropertiesRoleDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.PropertiesRolePermissionDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.PropertiesUserDaoImpl;
import edu.cornell.ncrn.ced2ar.security.idmgmt.dao.impl.PropertiesUserRoleDaoImpl;

/**
 * This class configures the Security of CED2AR.  Configuration includes Authentication Provider, 
 * Security of the CED2AR resources.
 * @author Cornell University, Copyright 2012-2015 
 * @author Ben Perry, Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
@PropertySource(value = { "classpath:ced2ar-web-config.properties" })
@Configuration
@EnableWebMvcSecurity
@EnableGlobalAuthentication
@EnableOAuth2Client
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {
	
	Config config = Config.getInstance();
	
	@Autowired
	Environment env;
	
	@Autowired
	private ServletContext context;	
	
	@Autowired
	private OAuth2ClientAuthenticationProcessingFilter googleOAuth2ClientAuthenticationProcessingFilter;
	
	@Autowired
	private OAuth2ClientAuthenticationProcessingFilter orcidOAuth2ClientAuthenticationProcessingFilter;
	
	@Autowired
	OAuth2ClientContextFilter oAuth2ClientContextFilter;
	
	@Autowired
	AuthProvider authProvider;

	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	ShaPasswordEncoder shaE = new ShaPasswordEncoder();
    	if(context.getAttribute("mainHash") == null){
    		String password = "";
    		if(config.getPwdIsRandom()){
    			password = RandomStringUtils.random(12, true, true);
    			context.setAttribute("mainHash", shaE.encodePassword(password, ""));
    			System.out.println("\n===== Password for CED2AR =====");
    	    	System.out.println(password);	 	
    	    	System.out.print("=============================== \n" );	    
    		}else{
    			context.setAttribute("mainHash", config.getPwdHash());
    		}
    	}
    	
     	String hash = (String) context.getAttribute("mainHash");
      	
     	//TODO:Add salt
     	if(config.getPwdIsRandom()){	
     		String readerEncoded = config.getBaseXReaderHash();
     		String readerPassword = (Base64.decode(readerEncoded).split(":"))[1];
     		String readerHash = shaE.encodePassword(readerPassword, "");
     		
	        auth.inMemoryAuthentication().passwordEncoder(shaE)
	        .withUser("admin").password(hash).roles("USER","ADMIN")
	        .and()
	        .withUser("reader").password(readerHash).roles("USER","ADMIN");
	      
     	}else{
     		auth.inMemoryAuthentication().passwordEncoder(shaE)
	        .withUser("admin").password(hash).roles("USER","ADMIN")
	        .and()
	        .withUser("user").password(hash).roles("USER","USER");
     	}
    }
    
    @Override
	protected void configure(HttpSecurity http) throws Exception {
    	Config config = Config.getInstance();  	
    	if(config.getPwdIsRandom()){ 		
    		configureRandomAuthentication(http);
    	}else{
    		if(config.isAuthenticationTypeOpenId())
    			configureOpenIdAuthentication(http);
    		else if(config.isAuthenticationTypeOauth2()){
    			if(config.isGoogleOauth2Supported())
    				configureGoogleOAuth2Authentication(http);
    			if(config.isOrcidOauth2Supported())
    				configureOrcidOAuth2Authentication(http);
    		}else{ 
    			configureDefaultAuthentication(http);
    		}
    	}
	}

    /**
     * @param http
     * @throws Exception
     * googleOAuth2Filter generates UserRedirectException exception which is handled by  oAuth2ClientContextFilter.
	 * oAuth2ClientContextFilter then generates a post request to populate Authentication Object.
	 */
    private void configureGoogleOAuth2Authentication(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
			.antMatchers("/config").access("hasRole('ROLE_ADMIN')")
	 		.antMatchers("/edit").access("hasRole('ROLE_ADMIN')")
	 		.antMatchers("/edit/codebooks").access("hasRole('ROLE_ADMIN')")
	 		.antMatchers("/monitoring/**").access("hasRole('ROLE_ADMIN')")		
	 		.antMatchers("/google_oauth2_login").anonymous()
	 		.antMatchers("/login").anonymous() 		
	 		.antMatchers("/edit/**").access("hasRole('ROLE_USER')")
	 		
	 		.and()
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login")
			.defaultSuccessUrl("/")
				.and()
				.csrf().disable()
				.logout()
			.and()
			.addFilterAfter(oAuth2ClientContextFilter,ExceptionTranslationFilter.class)
			.addFilterBefore(googleOAuth2ClientAuthenticationProcessingFilter,FilterSecurityInterceptor.class)
			.authenticationProvider(authProvider);

			if (config.getAccessMode().equals("AdminOnly")) {
				System.out.println("Running system in AdminOnly mode"); // DEBUG
				http.authorizeRequests()
						.antMatchers("/search").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/search/**").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/codebooks").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/codebooks/**").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/groups").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/groups/**").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/all").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/all/**").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/browse").access("hasRole('ROLE_ADMIN')")
						.antMatchers("/browse/**").access("hasRole('ROLE_ADMIN')");
			}
    }
    
    private void configureOrcidOAuth2Authentication(HttpSecurity http) throws Exception {		
			http.authorizeRequests()
			.antMatchers("/config").access("hasRole('ROLE_ADMIN')")
	 		.antMatchers("/edit").access("hasRole('ROLE_ADMIN')")
	 		.antMatchers("/edit/codebooks").access("hasRole('ROLE_ADMIN')")
	 		.antMatchers("/edit/**").access("hasRole('ROLE_USER')")
	 		.antMatchers("/monitoring/**").access("hasRole('ROLE_ADMIN')")
	 		.antMatchers("/orcid_oauth2_login").anonymous()
	 		.antMatchers("/login").anonymous()
	 		.and()
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login")
			.defaultSuccessUrl("/")
				.and()
				.csrf().disable()
				.logout()
			.and()
			.addFilterAfter(oAuth2ClientContextFilter,ExceptionTranslationFilter.class)
			.addFilterBefore(orcidOAuth2ClientAuthenticationProcessingFilter,FilterSecurityInterceptor.class)
			.authenticationProvider(authProvider);
    }
    
    /**
     * Default Authentication uses in memory login.
     * @param http
     * @throws Exception
     */
    private void configureDefaultAuthentication(HttpSecurity http) throws Exception {
		   http
		   .authorizeRequests()
	   		.antMatchers("/config").access("hasRole('ROLE_ADMIN')")
     		.antMatchers("/edit").access("hasRole('ROLE_ADMIN')")
     		.antMatchers("/edit/codebooks").access("hasRole('ROLE_ADMIN')")
 			.antMatchers("/edit/**").access("hasRole('ROLE_USER')")
 			.antMatchers("/monitoring/**").access("hasRole('ROLE_ADMIN')")
	 		.and()
			.formLogin()
			.and()
			.httpBasic()
			.and()
			.csrf().disable(); 
    }
    
    /**
     * Random password authentication locks everything
     * @param http
     * @throws Exception
     */
    private void configureRandomAuthentication(HttpSecurity http) throws Exception {
		   http
		   	.authorizeRequests()
		    .antMatchers("/**").access("hasRole('ROLE_ADMIN')")
			.and()
			.formLogin()
			.and()
			.httpBasic()
			.and()
			.csrf().disable(); 
    }

	/**
	 * Configures Open Id Authentication
	 * @param http
	 * @throws Exception
	 */
    private void configureOpenIdAuthentication(HttpSecurity http) throws Exception {
		Service userDetails = new Service();
		 http
		 .authorizeRequests()
		 .antMatchers("/config").access("hasRole('ROLE_ADMIN')")
		 .antMatchers("/edit").access("hasRole('ROLE_ADMIN')")
		 .antMatchers("/edit/codebooks").access("hasRole('ROLE_ADMIN')")
		 .antMatchers("/edit/**").access("hasRole('ROLE_USER')")
		 .antMatchers("/monitoring/**").access("hasRole('ROLE_ADMIN')")
		 
           .and()
			.csrf().disable()
			.openidLogin().loginPage("/login").failureUrl("/denied")
       		.authenticationUserDetailsService(userDetails)
               .attributeExchange("https://www.google.com/.*")
                   .attribute("email")
                       .type("http://axschema.org/contact/email")
                       .required(true)
                       .and()
                   .attribute("firstname")
                       .type("http://axschema.org/namePerson/first")
                       .required(true)
                       .and()
                   .attribute("lastname")
                       .type("http://axschema.org/namePerson/last")
                       .required(true);    		 
    }
    
	@Bean(name="authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
	@Bean(name ="userDao" )
	public UserDao getUserDao(){
		UserDao userDao = null;
		if(config.isAuthorizationStoragePropertiesFile()){
			userDao = new PropertiesUserDaoImpl();	
		}
		else if(config.isAuthorizationStorageBaseX()){
			userDao = new BaseXUserDaoImpl();
		}
		
		return userDao;
	}
	@Bean(name ="roleDao" )
	public RoleDao getRoleDao(){
		RoleDao roleDao = null;
		
		if(config.isAuthorizationStoragePropertiesFile()){
			roleDao = new PropertiesRoleDaoImpl();	
		}
		else if(config.isAuthorizationStorageBaseX()){
			roleDao = new BaseXRoleDaoImpl();
		}
		return roleDao;
	}
	
	@Bean(name ="userRoleDao" )
	public UserRoleDao getUserRoleDao(){
		UserRoleDao userRoleDao = null;
		if(config.isAuthorizationStoragePropertiesFile()){
			userRoleDao = new PropertiesUserRoleDaoImpl();
		}
		else if(config.isAuthorizationStorageBaseX()){
			userRoleDao = new BaseXUserRoleDaoImpl();
		}
		return userRoleDao;
	}
	@Bean(name ="permissionDao" )
	public PermissionDao getPermissionDao(){
		PermissionDao permissionDao = null;
		if(config.isAuthorizationStoragePropertiesFile()){
			permissionDao = new PropertiesPermissionDaoImpl();
		}
		else if(config.isAuthorizationStorageBaseX()){
			permissionDao = new BaseXPermissionDaoImpl();
		}
		return permissionDao;
	}
	@Bean(name ="rolePermissionDao" )
	public RolePermissionDao getRolePermissionDao(){
		RolePermissionDao rolePermissionDao = new PropertiesRolePermissionDaoImpl();
		if(config.isAuthorizationStoragePropertiesFile()){
			rolePermissionDao = new PropertiesRolePermissionDaoImpl();
		}
		else if(config.isAuthorizationStorageBaseX()){
			rolePermissionDao = new BaseXRolePermissionDaoImpl();
		}
		return rolePermissionDao;
	}
}