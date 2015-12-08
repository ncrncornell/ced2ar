package edu.ncrn.cornell.ced2ar.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *A set of utility classes to be referenced statically
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class Utilities {
	/**
	/*Check to make sure search parameters are valid and return field is present and valid
	 *Prevents instance of SearchConstructor from being created if base args are bad
	 * @param args Map<String,String> the field,value map of arguments
	 * @return String the empty string on success or an error string on failure
	 **/
	public static String validateSearchParams(Map<String,String> args){
		List<String> valid = Arrays.asList("return", "where", "sort", "limit"); 
		List<String> validReturn = Arrays.asList("variables", "codebooks", "documentdescriptions"); 
		boolean hasReturn = false;
		for(String key : args.keySet()) {
		   if(!valid.contains(key.toLowerCase())){
			   return key.toLowerCase() + " is not a valid parameter";
		   }else if(key.toLowerCase().equals("return")){
			   hasReturn = true;
			   if(!validReturn.contains(args.get(key)))
				   return "Return parameter is not valid";
		   }
		}
		if(!hasReturn)
			return "Must specify a return parameter";
		return "";
	}
	
	/**
	 * Method xmlToJson Converts XML to JSON.
	 * @param xmlString String the JSON string that is to be converted to JSON
	 * @return String the string now in JSON format
	 */
	//TODO: This method alone requires a seperate java package. Try to remove.
	@Deprecated
	public static String xmlToJson(String xmlString) {
		String json = null;
		try {
			JSONObject jsonObj = null;
			jsonObj = XML.toJSONObject(xmlString);
			json = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return json;
	}
	
	/**
	 * Checks to see if handle exists
	 * @return
	 */
	public static boolean codebookExists(String h){
		return codebookExists(h,"CED2AR");
	}
	
	/**
	 * Checks to see if handle exists
	 * @return
	 */
	public static boolean codebookExists(String h,String database){
		String xquery = "for $c in collection('"+database+"') return fn:base-uri($c)";	
		String[] handles = BaseX.getXML(xquery, false).replace("\n", " ").split(" ");
		for(String handle : handles){
			handle = handle.replace(database+"/", "").trim().toLowerCase();
			if(handle.equals(h)){ 
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check to see if a URI is validate
	 * @param uri
	 * @return
	 */
	public static boolean validateURI(String uri){
		//String[] types = {"http","https","ftp","file"};
		//UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_LOCAL_URLS
		//TODO: not validating URI's from local file system, or with file extension
		String[] schemes = {"http","https","ftp"};
		UrlValidator validator = new UrlValidator(schemes);
		return validator.isValid(uri);
	}	
}