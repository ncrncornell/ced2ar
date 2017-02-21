package edu.ncrn.cornell.ced2ar.api.data;

import org.apache.log4j.Logger;

import edu.ncrn.cornell.ced2ar.api.data.Connector.RequestType;
import edu.ncrn.cornell.ced2ar.api.rest.queries.CodebookData;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

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

//TODO: Slowly phase out this class
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
		return getXML(uri,null);
	}
	
	/**
	 *Allows for static reference of retrieving XML from CED2AR API with count header
	 *Return a partial representation of the XML. Often, this is not need with the newer API
	 * @param uri String the location to request content from
	 * @return String[] the data in short XML form, paired with the count header
	 */
	public static String[] getShortXML(String uri){
		WeakHashMap<String,String> headers = new WeakHashMap<String,String>();
		headers.put("partial-text", "true");
		return getXML(uri,headers);
	}
	
	/**
	 * Retrieves XML from master branch
	 * @param uri
	 * @return
	 */
	public static String[] getMasterXML(String uri){
		WeakHashMap<String,String> headers = new WeakHashMap<String,String>();
		headers.put("partial-text", "true");
		headers.put("master", "true");
		return getXML(uri,headers);
	}
	
	/**
	 * Allows for static reference of retrieving XML from CED2AR API
	 * Provides the ability to set headers
	 * @param uri String the location to request content from
	 * @return String[] the data in short XML form, paired with the count header
	 */
	public static String[] getXML(String uri, WeakHashMap<String,String> headers){
		Connector c  = null;
		try{
			c = new Connector(uri);
			c.buildRequest(RequestType.GET);
			if(headers != null){
				Iterator<Map.Entry<String,String>> itr = headers.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry<String,String> pair = (Map.Entry<String,String>) itr.next();
					c.setHeader(pair.getKey(), pair.getValue());
				}
			}
			String count = c.getHeader("Count");
			String data = c.execute().replaceAll("&#x\\d+;", "");
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
		CodebookData codebookData = new CodebookData();
		TreeMap<String,String[]> out = new TreeMap<String,String[]>();
		String data = codebookData.getCodebooks("versions").trim();
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
	}
	
	/**
	 * Gets a list of all codebooks with access levels
	 * @return the list of codebooks
	 */
	public static HashMap<String,String[]> getCodebooksAccs(String baseURI){
		HashMap<String,String[]> out = new HashMap<String, String[]>();
		CodebookData codebookData = new CodebookData();
		String data = codebookData.getCodebooks("access").trim();
		String[] codeBooks = data.split(";");	
		for(String codeBook:codeBooks){
			String[] c = codeBook.replaceAll("\\s+","").split(",");
			out.put(c[0], Arrays.copyOfRange(c, 1, c.length));
		}
		return out;
	}		

	/**
	 *Gets a list of all codebook studies by handle, fullname and shortname 
	 *Returns a csv format
	 * @param baseURI String the location to request content from
	 * @return ArrayList<String[]> the data returned as a list with: handle, fullname and shortname of each codebook
	 */
	public static TreeMap<String,String[]> getStudies(String baseURI){
		CodebookData codebookData = new CodebookData();
		TreeMap<String,String[]> out = new TreeMap<String,String[]>();
		String data = codebookData.getCodebooks("studies").trim();
		String[] codeBooks = data.split(";");	
		try{
			for(String codeBook:codeBooks){
				String[] c = codeBook.split(",");		
				out.put(c[0].trim()+c[1], new String[] {c[0].trim(),c[1],c[2],c[3]});	
			}
		}catch(ArrayIndexOutOfBoundsException e){
				return null;
		}
		return out;	
	}
	

//Editing Functions
	
	/**
	 * Updates an existing codebook
	 * @param baseURI root directory
	 * @param handle the codebook handle
	 * @param fullName the full name of the codebook
	 * @param shortName the short name of the codebook
	 */
	//TODO: Not sure if this works test later
	public static String uploadCodebook(String host, InputStream inputStream, String baseHandle, String version, 
	String user, boolean isMaster){
		Connector con = null;
		try{
			con = new Connector(host,0,"/codebooks/"+baseHandle+"/"+version);
			con.buildRequest(RequestType.POST);
			con.setPostFile(inputStream, "file");
			con.setPostFormContent("user", user);
			if(isMaster) con.setPostFormContent("master", "true");
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
	 * Updates a users password in BaseX
	 * @param host String baseURI for 
	 * @param baseXURI String location of BaseX DB 
	 * @param oldCredentials String encoded credentials
	 * @param uid String user ID
	 * @param newPassword String new password to use
	 * @return
	 */
	//TODO:This is outdated
	@Deprecated
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
	//TODO:This is outdated
	@Deprecated
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
	 * Edits a prov edit
	 * @param host
	 * @param object
	 * @param subject
	 * @param type
	 * @return
	 */
	@Deprecated
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
	
	@Deprecated
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
	
	/**
	 * Creates or edits a prov node
	 * @param host
	 * @param objectID
	 * @param type
	 * @param label
	 * @param uri
	 * @param newNode
	 * @param date
	 * @return
	 */
	@Deprecated
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
	
	/**
	 * Deletes a prov node
	 * @param host
	 * @param objectID
	 * @return
	 */
	@Deprecated
	public static int deleteProvNode(String host, String objectID){
		Connector c = null;
		try{
			c = new Connector(host,0,"/prov/nodes/"+objectID);
			c.buildRequest(RequestType.DELETE);
			c.execute();
			int code = c.getResponseCode();
			return code;
		}finally{
			c.close();
		}
	}

}