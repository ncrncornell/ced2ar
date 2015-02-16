package edu.ncrn.cornell.ced2ar.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
/**
 * Model class representing User Attributes
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */

public class OAUserDetail extends User{
	private static final long serialVersionUID = -7076219055793732778L;

	private String email;
    private String name;

    public OAUserDetail(String username, Collection<GrantedAuthority> authorities) {
        super(username, "unused", authorities);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}