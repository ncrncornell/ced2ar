package edu.ncrn.cornell.ced2ar.security.idmgmt.dao.impl;

import edu.ncrn.cornell.ced2ar.auth.PropertiesBase;

/**
 * Extension of ProperiesBase class which performs rudimentary read/write operations on .properties file
 * This class initializes the user properties file, which acts as a data repository for the authorized users
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */
public class PropertiesDaoBase extends PropertiesBase{
	public PropertiesDaoBase(){
		super("ced2ar-user-config.properties");
	}
}
