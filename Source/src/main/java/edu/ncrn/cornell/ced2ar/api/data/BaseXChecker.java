package edu.ncrn.cornell.ced2ar.api.data;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *Ensure BaseX has necessary DBs on startup
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Venky Kambhampaty, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class BaseXChecker implements Filter {
	private static final Logger logger = Logger.getLogger(BaseXChecker.class);
	boolean baseXChecked = false;

	@Autowired
	ServletContext context;
	
	@Override
	public void destroy() {
	
	}

	/**
	 *Filters requests to check if BaseX db is built correctly 
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	throws IOException, ServletException {
		if(!baseXChecked){	
			logger.debug("Checking BaseX integrity for application start");
			BaseX.integrityCheck();
			baseXChecked = true;
			
			//TODO: Switch for this or remove completely?
			//logger.debug("Starting password randomizer process ...");
			//BaseX.randomizePasswords();
			//logger.debug("Done password randomizer process.");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
}