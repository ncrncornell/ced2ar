package edu.ncrn.cornell.ced2ar.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.core.io.ClassPathResource;

/**
 * This class handles updating and reading property=value pairs 
 * Requires ced2ar-web-config.properties file in class path otherwise throws an exception.
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */
public class PropertiesBase{
	protected PropertiesConfiguration  properties;
	
	public PropertiesBase(String propertiedFileName){
		try{
			ClassPathResource cpr = new ClassPathResource(propertiedFileName);
			properties = new PropertiesConfiguration(cpr.getFile());
		}catch(Exception e){
			throw new RuntimeException("Fatal Error. Unable to read configiration file: " + propertiedFileName , e);
		}
	}
	
	/**
	 * This method reads all the properties from the properties file and returns them as a map.
     * The passwords are decoded.    
	 * @return
	 */
	public Map<String, String> getPropertiesMap(){
		Map<String, String> propertiesMap = new HashMap<String,String>();
    	List<String> keys = getKeys();
    	for(String key : keys){
    		String value =getValue(key);
    		propertiesMap.put(key, value);
    	}
    	return propertiesMap;	
	}

	/**
	 * Method getKeys.
	 * @return List<String> list of keys
	 */
	public List<String> getKeys(){
		List<String> list = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Iterator keysIterator = properties.getKeys(); 
		while(keysIterator.hasNext()){
		   list.add((String)keysIterator.next());
		}
		return list;
	}

	/**
	 * Method getValues.
	 * @return List<String> list of values
	 */
	public List<String> getValues(){
		List<String> list = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Iterator keysIterator = properties.getKeys(); 
		while(keysIterator.hasNext()){
		   list.add((String)properties.getProperty((String)keysIterator.next()));
		}
		return list;
	}

	/**
	 * Method getValue.
	 * @param key String the key for the desired value
	 * @return String the value specified by the key
	 */
	public String getValue(String key){
		   return (String)properties.getProperty(key);
	}
	
	public List<String> getValueAsList(String key){
		Object obj = properties.getProperty(key);
		if(obj == null){
			ArrayList<String> list = new ArrayList<String>();
			return list;
		}else if(obj instanceof List){
			return (List<String>) obj;	
		}else{
			ArrayList<String> list = new ArrayList<String>();
			list.add((String)obj);
			return list;
		}
	}
	
	/**
	 * Method addProperty. (Adds or updates a property=value pair.
	 * @param key String key for the property
	 * @param value String value for the property
	 * @throws Exception
	 */
	public void addProperty(String key, String value) throws Exception{
		properties.setProperty(key, value);
		properties.save();
	}
}