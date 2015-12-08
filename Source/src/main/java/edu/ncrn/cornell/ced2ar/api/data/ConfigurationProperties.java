package edu.ncrn.cornell.ced2ar.api.data;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * This class handles updating and reading property=value pairs 
 * Requires ced2ar-web-config.properties file in class path otherwise throws an exception.
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class ConfigurationProperties implements Serializable{
	
	private static final long serialVersionUID = 8590552171445043286L;
	private PropertiesConfiguration  properties;
	
	public ConfigurationProperties(){
		try{
			ClassPathResource cpr = new ClassPathResource("ced2ar-web-config.properties");
			properties = new PropertiesConfiguration(cpr.getFile());
		}catch(Exception ex){
			throw new RuntimeException("Fatal Error.  Unable to read properties. " ,ex );
		}
	}
	
	public ConfigurationProperties(InputStream is){
		try{
			properties = new PropertiesConfiguration();
			properties.load(is);
		}catch(Exception ex){
			throw new RuntimeException("Fatal Error.  Unable to read properties. " ,ex );
		}
	}
	
	/**
	 *This method reads all the properties from the properties file and returns them as a map.
     * The passwords are decoded. (Passwords are in base64 encoded in the properties file)    
	 * @return
	 */
	public Map<String, String>  getPropertiesMap(){
    	Map<String, String> propertiesMap =  new HashMap<String,String>();
    	List<String> keys = getKeys();
    	
    	for(String key:keys){
    		String value =getValue(key);
    		propertiesMap.put(key, value);
    	}
    	decodePasswords(propertiesMap);
    	return propertiesMap;	
	}

	/**
	 * This method returns String representation of properties file.
	 * @return
	 */
	public String getProperties(){
    	StringBuffer sb = new StringBuffer("");
    	List<String> keys = getKeys();
    	
    	for(String key:keys){
    		String value =getValue(key);
    		if(StringUtils.isEmpty(value))
    			sb.append(key+"=\n");
    		else
    			sb.append(key+"="+value+"\n");
    	}
    	return sb.toString();	
	}

	/**
	 * @param propertiesMap 
	 * The passwords are Decoded. (Passwords are in base64 encoded in the properties file)
	 */
    public void decodePasswords(Map<String, String> propertiesMap){
    	String pwd0 = propertiesMap.get("baseXReaderHash");
    	String pwd1 = propertiesMap.get("baseXWriterHash");
    	String pwd2 = propertiesMap.get("baseXAdminHash");
    	String bugReporterPwd = propertiesMap.get("bugReportPwd");	
    	
    	propertiesMap.put("baseXReaderHash",new String(Base64.decodeBase64(pwd0)));
    	propertiesMap.put("baseXWriterHash",new String(Base64.decodeBase64(pwd1)));
    	propertiesMap.put("baseXAdminHash",new String(Base64.decodeBase64(pwd2)));
    	propertiesMap.put("bugReportPwd",new String(Base64.decodeBase64(bugReporterPwd)));
    }
    /**
     * Encodes password to base64 encoding.
     * @param password
     * @return
     */
    public String encodePassword(String password){
    	String encodedPassword 	=  new String(Base64.encodeBase64((password).getBytes()));
    	return encodedPassword;
    }
    
    public String decodeHash(String encodedStr){
    	return new String(Base64.decodeBase64(encodedStr));
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
	
	/**
	 * Method addProperty.  (Adds or updates a property=value pair.
	 * @param key String key for the property
	 * @param value String value for the property
	 * @throws Exception
	 */
	public void addProperty(String key, String value) throws Exception{
		properties.setProperty(key, value);
		properties.save();
	}
}