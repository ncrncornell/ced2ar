package edu.ncrn.cornell.ced2ar.auth.oauth2;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import edu.ncrn.cornell.ced2ar.auth.OAUserDetail;
/**
 * Class representing Authentication Token.
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */

public class AuthToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 8254831403638075928L;
	private OAUserDetail registeredUser;

	public AuthToken(Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
	}

	public AuthToken(OAUserDetail registeredUser) {
		super(registeredUser.getAuthorities());
		this.registeredUser = registeredUser;
	}

	@Override
	public Object getCredentials() {
		return "NOT_REQUIRED";
	}

	@Override
	public Object getPrincipal() {
		return registeredUser;
	}

	public OAUserDetail getOAUserDetail() {
		return registeredUser;
	}

	public void setOAUserDetail(OAUserDetail registeredUser) {
		this.registeredUser = registeredUser;
		setDetails(registeredUser);
	}
}