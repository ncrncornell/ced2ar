package edu.cornell.ncrn.ced2ar.web.form;

import java.util.HashMap;
import java.util.Map;

/**
 *Handles mapping to properties form
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class PropertiesForm {
	Map<String, String> propertiesMap =  new HashMap<String,String>();

	/**
	 * Method getPropertiesMap.
	 * @return Map<String,String>
	 */
	public Map<String, String> getPropertiesMap() {
		return propertiesMap;
	}

	/**
	 * Method setPropertiesMap.
	 * @param propertiesMap Map<String,String>
	 */
	public void setPropertiesMap(Map<String, String> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}
}