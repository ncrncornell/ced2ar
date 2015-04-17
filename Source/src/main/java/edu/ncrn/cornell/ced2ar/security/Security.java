package edu.ncrn.cornell.ced2ar.security;

import javax.servlet.ServletContext;

import org.apache.commons.lang.RandomStringUtils;
import org.basex.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.auth.Service;
import edu.ncrn.cornell.ced2ar.auth.oauth2.AuthProvider;

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
@Configuration
@EnableWebMvcSecurity
@EnableGlobalAuthentication
@EnableOAuth2Client
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {
	@Autowired
	private ServletContext context;	
	
	@Autowired
	OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter;
	
	@Autowired
	OAuth2ClientContextFilter oAuth2ClientContextFilter;
	
	@Autowired
	AuthProvider authProvider;
	
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	Config config = Config.getInstance();
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
    		else if(config.isAuthenticationTypeOauth2())
    			configureOAuth2Authentication(http);
    		else 
    			configureDefaultAuthentication(http);
    	}
	}

    /**
     * @param http
     * @throws Exception
     * googleOAuth2Filter generates UserRedirectException exception which is handled by  oAuth2ClientContextFilter.
	 * oAuth2ClientContextFilter then generates a post request to populate Authentication Object.
	 */
    private void configureOAuth2Authentication(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		//TODO: add args for select specific paths
		/*
		.antMatchers("/search").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/search/**").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/codebooks").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/codebooks/**").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/groups").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/groups/**").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/all").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/all/**").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/browse").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/browse/**").access("hasRole('ROLE_ADMIN')")
		*/
		.antMatchers("/config").access("hasRole('ROLE_ADMIN')")
 		.antMatchers("/edit").access("hasRole('ROLE_ADMIN')")
 		.antMatchers("/edit/codebooks").access("hasRole('ROLE_ADMIN')")
 		.antMatchers("/edit/**").access("hasRole('ROLE_USER')")
 		.antMatchers("/monitoring/**").access("hasRole('ROLE_ADMIN')")
 		.antMatchers("/google_oauth2_login").anonymous()
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
		.addFilterBefore(oAuth2ClientAuthenticationProcessingFilter,FilterSecurityInterceptor.class)
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
		   		/*
		   		.antMatchers("/search").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/search/**").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/codebooks").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/codebooks/**").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/groups").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/groups/**").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/all").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/all/**").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/browse").access("hasRole('ROLE_ADMIN')")
				.antMatchers("/browse/**").access("hasRole('ROLE_ADMIN')")
				*/
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
}