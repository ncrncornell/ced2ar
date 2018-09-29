	package edu.cornell.ncrn.ced2ar.init;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import edu.cornell.ncrn.ced2ar.api.data.ConfigurationProperties;
/**
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 * This filter class redirects the user to initFlow start page if the initialization process is not done.
 * Config parameter determines if the initialization is performed.
 * Requests for styles is not filtered
 *
 * if the initialization is previously performed, initFlow url is only available to admin users.
 *  
 */
 
public class InitConfigFilter implements Filter {
	private static final Logger logger = Logger.getLogger(InitConfigFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		logger.debug("Start Configuiration Filter");
		try{
			String uri = ((HttpServletRequest)request).getRequestURI();
			logger.debug("Request is made to uri " + uri);

			ConfigurationProperties CP = new ConfigurationProperties();
			String initialized = CP.getValue(ConfigProperties.PROPERTY_CONFIG_INITIALIZED);
			boolean configInitialized  = false;
			if(StringUtils.isNotEmpty(initialized)){
				logger.debug("Raw Config Initialized" + initialized + ":");
				configInitialized = initialized.trim().equalsIgnoreCase("true");
			}

			logger.debug("Initialization Process performed previously? " + configInitialized);

			//TODO: Should we have this in the rest of security routing
			if(configInitialized && uri.equalsIgnoreCase("/ced2ar-web/initFlow")
			|| uri.equalsIgnoreCase("/ced2ar-web/changePasswordFlow")
			|| uri.equalsIgnoreCase("/ced2ar-web/mergeFlow")			
			){
				Principal userPrincipal = ((HttpServletRequest)request).getUserPrincipal();
				if(userPrincipal == null){
					((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
				}
				else{
					boolean isAdmin = ((HttpServletRequest)request).isUserInRole("ROLE_ADMIN");
					if(!isAdmin){
						((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					else{
						logger.debug("Filter not applied. URI is styles of config is initialized");
						chain.doFilter(request, response);						
					}
				}
			}else{
			
				if(uri.startsWith("/ced2ar-web/styles") ||uri.startsWith("/ced2ar-web/scripts") || configInitialized ){ //All the requests for the styles to go through
					logger.debug("Filter not applied. URI is styles of config is initialized");
					chain.doFilter(request, response);
				}
				// if config not initialized and request is not for initflow then redirect
				else if(!configInitialized && !uri.equalsIgnoreCase("/ced2ar-web/initFlow")){
					logger.debug("Filter applied. Redirecting to initFlow. configInitialized=" + configInitialized + " uri=" + uri);
					((HttpServletResponse) response).sendRedirect("/ced2ar-web/initFlow");
				}
				else{
					logger.debug("Filter NOT applied. configInitialized=" + configInitialized + " uri=" + uri);
					chain.doFilter(request, response);
				}
			}
		}
		catch(Exception ex){
			logger.debug("Error: " + ex.getMessage(), ex);
			chain.doFilter(request, response);	
		}
	}
	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
	@Override
	public void destroy() {
	
	}
}

/*
 * Config bean is not getting refreshed after an update to the 
 * undelying properties file in a Server environment. 
 * However, Config bean is refreshing in local tomcat normally.
 * Workaround for this issue is to read the propery directly from the underlying properties file
 * using lower level code
	Config config = Config.getInstance();
	if(StringUtils.isNotEmpty(config.getConfigInitialized())){
		logger.debug("Raw Config Initialized" + config.getConfigInitialized() + ":");
		configInitialized = config.getConfigInitialized().trim().equalsIgnoreCase("true");
	}
*/