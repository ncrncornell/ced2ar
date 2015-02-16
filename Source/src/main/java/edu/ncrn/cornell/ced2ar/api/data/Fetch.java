package edu.ncrn.cornell.ced2ar.api.data;

import org.apache.log4j.Logger;
import edu.ncrn.cornell.ced2ar.api.data.Connector.RequestType;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 *Handles URI retrieval for API access
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Fetch {
	
	private static final Logger logger = Logger.getLogger(Fetch.class);
	
//Basic methods
	/**
	 *Allows for static reference of retrieving raw data
	 * @param uri String the location to request content from
	 * @return String the content returned from the request
	 */
	public static String get(String uri){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			String data = c.execute();		
		return data;
		}finally{
			c.close();
		}
	}
	
	/**
	 *Allows for static reference of retrieving raw data
	 * @param uri String the location to request content from
	 * @param cred String credentials to authorize with
	 * @return String the content returned from the request
	 */
	public static String get(String uri, String cred){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.authorize(cred);
			c.buildRequest(RequestType.GET);
			String data = c.execute();		
		return data;
		}finally{
			c.close();
		}
	}

	/**
	 * Allows for static reference of retrieving raw data
	 * @param uri String the location to request content from
	 * @param header Field String header field to send with request
	 * @param headerValue String of value to set
	 * @returnString the content returned from the request
	 */
	public static String get(String uri, String headerField, String headerValue){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			c.setHeader(headerField, headerValue);
			String data = c.execute();		
		return data;
		}finally{
			c.close();
		}
	}	
	
	/**
	 *Allows for static reference of retrieving raw data
	 * @param uri String the location to request content from
	 * @param cred String credentials to authorize with
	 * @return String the content returned from the request
	 */
	public static String post(String uri, String cred){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.authorize(cred);
			c.buildRequest(RequestType.POST);
			String data = c.execute();		
		return data;
		}finally{
			c.close();
		}
	}
	
	
//Specific methods
	/**
	 *Allows for static reference of retrieving XML from CED2AR API with count header
	 * @param uri String the location to request content from
	 * @return String[] the data returned from the request paired with the count header
	 */
	public static String[] getXML(String uri){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			String data = c.execute().replaceAll("&#x\\d+;", "");
			String count = c.getHeader("Count");
			return new String[] {data,count};
		}finally{
			c.close();
		}
	}
	
	/**
	 *Allows for static reference of retrieving XML from CED2AR API with count header
	 *Return a partial representation of the XML. Often, this is not need with the newer API
	 * @param uri String the location to request content from
	 * @return String[] the data in short XML form, paired with the count header
	 */
	public static String[] getShortXML(String uri){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			c.setHeader("partial-text", "true");
			String data = c.execute().replaceAll("&#x\\d+;", "");
			String count = c.getHeader("Count");
			return new String[] {data,count};
		}finally{
			c.close();
		}
	}
	
	/**
	 *Allows for static reference of retrieving JSON from CED2AR API with count header
	 * @param uri String the location to request content from
	 * @return String[] the data in a JSON format, paired with the count header
	 */
	public static String[] getJson(String uri){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			c.setHeader("accept", "application/json");
			String data = c.execute();
			String count = c.getHeader("Count");
			return new String[] {data,count};
		}finally{
			c.close();
		}
	}
	
	/**
	 *Allows for static reference of retrieving XML from CED2AR API with count header
	 *Returns a regular CSV representation. Typically not used.
	 * @param uri String the location to request content from
	 * @return String[] the data in short CSV form, paired with the count header
	 * @throgetCodebooksws URIException  */
	public static String[] getCSV(String uri){
		Connector c = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			c.setHeader("accept", "text/csv");
			String data = c.execute();
			String count = c.getHeader("Count");
			return new String[] {data,count};
		}finally{
			c.close();
		}		
	}
	
	/**
	 *Allows for static reference of retrieving XML from CED2AR API with count header
	 *Returns a short CSV representation
	 * @param uri String the location to request content from
	 * @return String[] the data in short CSV form, paired with the count header
	 * @throgetCodebooksws URIException  */
	public static String[] getShortCSV(String uri){
		Connector c = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			c.setHeader("partial-text", "true");
			c.setHeader("accept", "text/csv");
			String data = c.execute();
			String count = c.getHeader("Count");
			return new String[] {data,count};
		}finally{
			c.close();
		}		
	}
	

	
	
	
	/**
	 *Gets a list of all codebooks by handle, fullname and shortname 
	 *Returns a csv format
	 * @param baseURI String the location to request content from
	 * @return ArrayList<String[]> the data returned as a list with handle, fullname and shortname of each codebook
	 */
	public static TreeMap<String,String[]> getCodebooks(String baseURI){
		Connector con = null;
		try{
			TreeMap<String,String[]> out = new TreeMap<String,String[]>();
			String uri =  baseURI + "codebooks";
			con = new Connector(uri);
			con.buildRequest(RequestType.GET);
			con.setHeader("id-type", "versions");
			String data = con.execute().trim();
			String[] codeBooks = data.split(";");	
			try{
				for(String codeBook:codeBooks){
					logger.debug("found codebook "+ codeBook);
					String[] c = codeBook.split(",");	
					out.put(c[0].trim()+c[1], new String[] {c[0].trim(),c[1],c[2],c[3],c[4]});		
				}
			}catch(ArrayIndexOutOfBoundsException e){
					logger.error("No codebooks found in BaseXDB");
					return null;
			}
		return out;
		}finally{
			con.close();
		}
	}
	
	/**
	 * Gets a list of all codebooks with access levels
	 * @return the list of codebooks
	 */
	public static Hashtable<String,String[]> getCodebooksAccs(String baseURI){
		Connector con = null;
		try{
			Hashtable<String,String[]> out = new Hashtable<String, String[]>();
			String uri =  baseURI + "codebooks";
			con = new Connector(uri);
			con.buildRequest(RequestType.GET);
			con.setHeader("id-type", "access");
			String data = con.execute().trim();
			if(con.getResponseCode() >= 400){
				return null;
			}
			String[] codeBooks = data.split(";");	
			for(String codeBook:codeBooks){
				String[] c = codeBook.replaceAll("\\s+","").split(",");
				out.put(c[0], Arrays.copyOfRange(c, 1, c.length));
			}
			return out;
		}finally{
			con.close();
		}
	}		
	
//Editing Functions
	/**
	 * Updates an existing codebook
	 * @param baseURI root directory
	 * @param handle the codebook handle
	 * @param fullName the full name of the codebook
	 * @param shortName the short name of the codebook
	 */
	public static String uploadCodebook(String host, InputStream file, String baseHandle, String version){
		Connector con = null;
		try{
			con = new Connector(host,0,"/codebooks/"+baseHandle+"/"+version);
			con.buildRequest(RequestType.POST);
			con.setPostFile(file, "file");
			String message = con.execute();
			if(con.getResponseCode() >= 400){
				con.close();
				return message;
			}
			return "";	
		}finally{
			con.close();
		}		
	}
	
	/**
	 * Uploads a new codebook
	 * @param host
	 * @param file
	 * @param baseHandle
	 * @param version
	 * @param label
	 * @return
	 */
	public static String uploadCodebook(String host, InputStream file, String baseHandle, String version, String label){
		Connector con = null;
		try{
			con = new Connector(host,0,"/codebooks/"+baseHandle+"/"+version);
			con.buildRequest(RequestType.POST);
			con.setPostFile(file, "file");
			con.setPostFormContent("label", label);
			String message = con.execute();
			if(con.getResponseCode() >= 400){
				con.close();
				return message;
			}
			return "";	
		}finally{
			con.close();
		}		
	}
	
	/**
	 * Removes a codebook
	 * @param host
	 * @param baseHandle
	 * @param version
	 * @return
	 */
	public static String deleteCodebook(String host, String baseHandle, String version){
		Connector con = null;
		try{
			con = new Connector(host,0,"/codebooks/"+baseHandle+"/"+version);	
			con.buildRequest(RequestType.DELETE);
			con.setPostFormContent("version", version);
			String message = con.execute();
			if(con.getResponseCode() >= 400){
				return message;
			}
			return "";	
		}finally{
			con.close();
		}		
	}
	
	/**
	 * Edits the title page
	 * @param handle the codebook to edit
	 * @param field the field being edited
	 * @param value the new value for the field
	 * @param append whether we append a new element or overwrite an existing one
	 * @param index index specifying the location of the element
	 * @return the response code from the request
	 */
	public static int editTitlePage(String host, String baseHandle, String version, 
	String field, String value, String append, int index, String user){
		Connector c = null;
		try{
			c = new Connector(host,0,"/codebooks/"+baseHandle+"/"+version+"/edit");
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("field", field);
			c.setPostFormContent("value", value);
			c.setPostFormContent("append", append);
			c.setPostFormContent("user", user);
			if(index != 0){
				c.setPostFormContent("index", Integer.toString(index));
			}
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	/**
	 * Edits a variable - uses apache HTTP client
	 * @param handle the codebook containing the variable
	 * @param var the variable to edit
	 * @param field the field being edited
	 * @param value the new value for the field
	 * @param append whether we append a new element or overwrite an existing one
	 * @param delete whether we are deleting the element or not
	 * @param ip the ip that made the request
	 * @param index1 first index specifying the location of the element
	 * @param index2 second index specifying the location of the element
	 * @return the response code from the request
	 */
	public static int editVar(String host, String baseHandle, String version, String var, 
	String field, String value, String append, String delete, String ip, String index1, String index2, String user){
		Connector c = null;
		try{
			c =	new Connector(host,0,"/codebooks/"+baseHandle+"/"+version+"/vars/"+var+"/edit");
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("field", field);
			c.setPostFormContent("value", value);
			c.setPostFormContent("append", append);
			c.setPostFormContent("delete", delete);
			c.setPostFormContent("user", user);
			c.setPostFormContent("ip", ip);
			if(!index1.equals("")){
		    	c.setPostFormContent("index",index1);
			    if(!index2.equals("")){
			    	c.setPostFormContent("index2",index2);
			    }
		    }  
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	/**
	 * Adds or edits a variable group
	 * @param handle
	 * @param id
	 * @param name
	 * @param label
	 * @param desc
	 */
	public static int groupEdit(String host, String baseHandle, String version, 
	String id, String name, String label, String desc){
		Connector c = null;
		try{
			c =	new Connector(host,0,"/codebooks/"+baseHandle+"/"+version+"/vargrp/"+id.trim());
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("name", name);
			c.setPostFormContent("labl", label);
			c.setPostFormContent("txt", desc);
			
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	/**
	 * Adds or removes a variable from a group
	 * @param host
	 * @param baseHandle
	 * @param version
	 * @param id
	 * @param var
	 * @param adding
	 * @return
	 */
	public static int groupVarChange(String host, String baseHandle, String version, 
		String id, String var, boolean adding){
		Connector c = null;
		try{
			c =	new Connector(host,0,"/codebooks/"+baseHandle+"/"+version+"/vargrp/"+id.trim()+"/vars");
			c.buildRequest(RequestType.POST);
			if(!adding){
				c.setPostFormContent("delete", "true");
			}
			c.setPostFormContent("vars", var);
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	
	/**
	 * Sets default version for a codebook
	 * @param host
	 * @param baseHandle
	 * @param version
	 * @return
	 */
	public static int setDefaultCodebook(String host, String baseHandle,String version){
		Connector c = null;
		try{
			c =	new Connector(host,0,"/codebooks/"+baseHandle+"/"+version+"/settings");
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("use", "default");
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	/**
	 * Updates a users password in BaseX
	 * @param host String baseURI for 
	 * @param baseXURI String location of BaseX DB 
	 * @param oldCredentials String encoded credentials
	 * @param uid String user ID
	 * @param newPassword String new password to use
	 * @return
	 */
	public static String changePassword(String host,String baseXURI, String oldCredentials, String uid,String newPassword){
		Connector c = null;
		try{
			String endPoint = host+"?oldCredenials="+oldCredentials +"&uid="+uid+"&newPassword="+newPassword+"&baseXUri="+baseXURI;		
			c = new Connector(endPoint);
			c.buildRequest(RequestType.POST);
			String result = c.execute();
			if(c.getResponseCode() >= 400){
				c.close();
				return result;
			}
			return "";
		}finally{
			c.close();
		}
	}	
	
	/**
	 * 
	 * Makes an end point request that passes admin, reader and writer credentials along with new URL to validate 
	 * @param host
	 * @param baseXURI
	 * @param adminCredentials
	 * @param readerCredentials
	 * @param writerCredentials
	 * @return
	 */
	public static String changeBaseXDB(String host,String baseXURI, String adminCredentials, String readerCredentials,String writerCredentials){
		Connector c = null;
		try{
			String endPoint = host+"?adminCredentials="+adminCredentials +"&readerCredentials="+readerCredentials+"&writerCredentials="+writerCredentials+"&baseXUri="+baseXURI;		
			c = new Connector(endPoint);
			c.buildRequest(RequestType.POST);
			String result = c.execute();
			if(c.getResponseCode() >= 400){
				c.close();
				return result;
			}
			return "";
		}finally{
			c.close();
		}
	}	

	/**
	 * Edits the access levels of multiple variable at once
	 * @param host
	 * @param baseHandle
	 * @param version
	 * @param access
	 * @param vars
	 * @return
	 */
	public static int accessVars(String host, String baseHandle, String version, String access, String vars, String all){
		Connector c = null;
		try{
			c = new Connector(host,0,"/codebooks/"+baseHandle+"/"+version+"/accessvars");
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("access", access);
			c.setPostFormContent("all", all);
			c.setPostFormContent("vars", vars);
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	/**
	 * Edits a prov edit
	 * @param host
	 * @param object
	 * @param subject
	 * @param type
	 * @return
	 */
	public static int provEdge(String host, String id, String object, String subject, String type, String uniqueEdge){
		Connector c = null;
		try{
			c = new Connector(host,0,"/prov/edges/"+id);
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("source", object);
			c.setPostFormContent("target", subject);
			c.setPostFormContent("edgeType", type);
			c.setPostFormContent("uniqueEdge", uniqueEdge);
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	public static int provEdgeDelete(String host, String object, String subject, String type){
		Connector c = null;
		try{
			c = new Connector(host,0,"/prov/edges/-1");
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("source", object);
			c.setPostFormContent("target", subject);
			c.setPostFormContent("edgeType", type);
			c.setPostFormContent("delete", "true");
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
	
	public static int provNode(String host, String objectID, String type, 
	String label, String uri, String newNode, String date){
		Connector c = null;
		try{
			c = new Connector(host,0,"/prov/nodes/"+objectID);
			c.buildRequest(RequestType.POST);
			c.setPostFormContent("uri", uri);
			c.setPostFormContent("label", label);
			c.setPostFormContent("newNode", newNode);
			c.setPostFormContent("nodeType", type);
			c.setPostFormContent("date", date);
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}
}