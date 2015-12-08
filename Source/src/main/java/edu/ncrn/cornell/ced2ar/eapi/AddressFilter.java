package edu.ncrn.cornell.ced2ar.eapi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Class to filter requests based on IP address
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class AddressFilter implements Filter {
	
	@Override
	public void destroy() {
	
	}

	/**
	 *Filters requests and only accepts those from localhost. Otherwise, application will respond with 404.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		String addressRemote = request.getRemoteAddr();
		//String addressLocal = request.getLocalAddr();
		
		if(addressRemote.equals("0:0:0:0:0:0:0:1") || addressRemote.equals("127.0.0.1")){
			chain.doFilter(request, response);
		}else{
			HttpServletResponse hsr= (HttpServletResponse) response;
			hsr.sendError(404);
			chain.doFilter(request, hsr);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
}