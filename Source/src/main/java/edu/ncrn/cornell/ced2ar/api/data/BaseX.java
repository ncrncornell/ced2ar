package edu.ncrn.cornell.ced2ar.api.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.basex.api.client.ClientSession;

import edu.ncrn.cornell.ced2ar.api.data.Connector.RequestType;

/**
 * Handles data retrieval from BaseX web application
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Venky Kambhampaty, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class BaseX  {
	
	private static final Logger logger = Logger.getLogger(BaseX.class);
	
	/**
	 * Method getXML.
	 * @param xquery String the query to be executed
	 * @return String the result returned by the database
	 */
	public static String getXML(String xquery) {
		return getXML(xquery,true);
	}
	
	/**
	 * Method getXML.
	 * @param xquery String the query to be executed
	 * @return String the result returned by the database
	 */
	public static String getXML(String xquery, boolean decode) {
		Connector c = null;
		try{
			c = new Connector();
			c.buildRequest(RequestType.GET, "rest?query=", xquery);
			c.readerAuthorize();
			String xml = c.execute();
			if(decode){
				xml.replaceAll("&amp;", "&").replaceAll("&apos;", "'"); 
			}
			return xml;
		}finally{
			c.close();
		}
	}
	
	/**
	 * Fetch XML when BaseX is a service
	 * TODO:Current not used, but might be important for the future
	 * @return
	 */
	public static String getXML2(String xquery, boolean decode){
		String results = null;
		ClientSession session = null;
		try {
			//TODO: Switch and args for change auth level between writer and reader
			Config config =Config.getInstance();
			String encodedAuth = config.getBaseXWriterHash();
			String[] decodedAuth = (new String(Base64.decodeBase64(encodedAuth))).split(":");
			String user = decodedAuth[0];
			String password = decodedAuth[1];			
			session = new ClientSession("localhost", 1984, user, password);
			results = session.query(xquery).execute();
		} catch (IOException e) {
			logger.error("ERROR with xquery "+ xquery + "\n");
			e.printStackTrace();
			return null;
		}finally{
			if(session != null){
				try {
					session.close();
				} catch (IOException e) {}
			}
		}
		if(decode){
			results.replaceAll("&amp;", "&").replaceAll("&apos;", "'"); 
		}
		return results;
	}
	
	public static String getXML2(String xquery){
		return getXML2(xquery,true);
	}
	
	/**
	 * Adds codebook to ced2ar. For use with new BaseX setup
	 * @param handle
	 * @param contents
	 */
	public static void put2(String handle, String contents, String database){
		ClientSession session = null;
		try {
			//TODO: Switch and args for change auth level between writer and reader
			Config config =Config.getInstance();
			String encodedAuth = config.getBaseXWriterHash();
			String[] decodedAuth = (new String(Base64.decodeBase64(encodedAuth))).split(":");
			String user = decodedAuth[0];
			String password = decodedAuth[1];			
			session = new ClientSession("localhost", 1984, user, password);
			
			//Temp fix, new validation doesn't like missing namespace declarations
			contents = contents.replace("<xhtml:", "<");
			contents = contents.replace("</xhtml:", "</");
			
			InputStream stream = IOUtils.toInputStream(contents, "UTF-8");
			session.execute("open "+database);
			session.replace("/"+handle, stream);
		
		} catch (IOException e) {
			logger.error("ERROR with put");
			e.printStackTrace();
			return;
		}finally{
			if(session != null){
				try {
					session.close();
				} catch (IOException e) {}
			}
		}		
	}
	
	/*Tests to see if BaseX DB is reachable*/
	/**
	 * Method testConnection.
	 * @return boolean whether or not BaseX can be accessed
	 */
	public static boolean testConnection(){
		//Tests if BaseX can be accessed
		Connector c = new Connector();
		c.buildRequest(RequestType.GET);
		c.readerAuthorize();
		c.execute();
		int code = c.getResponseCode();
		c.close();
		if(code < 400 && code != -1){
			return true;
		}else{
			return false;
		}	
	}
	
	/**
	 * 
	 * Same as test connection except that this method does not use reader authorize.
	 * @return boolean whether or not BaseX can be accessed
	 */
	public static boolean checkBaseX(String uri){
		//Tests if BaseX can be accessed
		Connector c = new Connector(uri);
		c.buildRequest(RequestType.GET);
		c.execute();
		int code = c.getResponseCode();
		if(code < 400 && code != -1){
			return true;
		}else{
			return false;
		}	
	}
	/**
	 * Method validateConnection.
	 * @param uri String the location to be validated
	 * @param credentials String access credentials for the database (For basic Authentication Only)
	 * @return boolean whether or not the location uri and credentials is valid
	 */
	public static boolean validateConnection(String uri, String credentials){
		logger.info("connection uri:" + uri +"rest");
		Connector c = new Connector(uri +"rest");
		logger.info("Connection:" + c);
		c.buildRequest(RequestType.GET);
		logger.info("Built Request: Authorizing credentials = " + credentials);
		c.authorize(credentials);
		logger.info("Executing...");
		c.execute();
		int code = c.getResponseCode();
		logger.info("Code " + code);
		c.close();
		if(code < 400 && code != -1){
			return true;
		}else{
			return false;
		}	
	}
	
	/**
	 * Retrieves entire codebook
	 * @param handle the codebook to retrieve
	 * @return the codebook as a string
	 */
	//TODO: test handle removed
	public static String get(String handle){		
		String request = "rest/CED2AR/";
		return httpGet(request,handle);//.replaceAll("&amp;", "&").replaceAll("&apos;", "'");
	}
	
	/**
	 * executes a query on BaseX and gets the response
	 * @param xquery the query to execute
	 * @return the response from the database
	 */
	public static String query(String xquery){	
		return httpGet("rest?query=",xquery);
	}	
	
	/**
	 * Executes a query with no response
	 * @param xquery the query to execute
	 */
	public static void write(String xquery){	
		httpGetWriter("rest?query=",xquery);
	}	
		
	/**
	 * Executes a command on the BaseX database
	 * @param command the command to execute
	 */
	public static void command(String command){	
		httpGetWriter("rest?command=",command);
	}
	
	/**
	 * Executes a command on the BaseX database with a specified prefix
	 * allows the execution of commands on every database or document
	 * @param command the command to execute
	 * @param prefix the prefix (path) to follow /rest
	 */
	public static void command(String command, String prefix){	
		httpGetWriter("rest"+prefix+"?command=",command);
	}

	/**
	 * Deletes an entire codebook from BaseX
	 * @param handle the codebook to delete
	 */
	public static void delete(String handle){		
		httpGetWriter("rest/CED2AR/?command=","delete "+handle);
	}
	
	//TODO: error handling for put
	/**
	 * Replaces or writes new codebook to repo
	 * @param fileName the name of the codebook to add
	 * @param contents the contents of the codebook
	 * @param message GIT message to add
	 */
	public static void put(String fullHandle, String contents){
		put2(fullHandle,contents,"CED2AR");
		/**
		if(!QueryUtil.hasVersionIndex2(fullHandle)){ 
			httpGetWriter("rest/CED2AR/?command=","add TO "+fullHandle+" <xml/>"); 
		}
		httpPut("rest/CED2AR/",fullHandle, contents);*/
	}
	
	/**
	 * Adds codebook to master
	 * @param fullHandle
	 * @param contents
	 */
	public static void putM(String fullHandle, String contents){
		put2(fullHandle,contents,"CED2ARMaster");
		/*
		if(!QueryUtil.hasVersionIndex2(fullHandle)){ 
			httpGetWriter("rest/CED2ARMaster/?command=","add TO "+fullHandle+" <xml/>"); 
		}
		httpPut("rest/CED2ARMaster/",fullHandle, contents);*/
	}
	
	/**
	 * Replaces or writes new file to repo with a specified prefix
	 * @param fileName the name of the file to add
	 * @param contents the contents of the file
	 * @param prefix the location to which we upload the file
	 */
	public static void putB(String fileName, String contents, String prefix){
		httpPut("rest/"+prefix,fileName, contents);
	}

	/**
	 * Replaces or writes new file to repo with a specified prefix
	 * @param reqPrefix the location to which we upload the file
	 * @param fileName the name of the file to add
	 * @param contents the contents of the file
	 */
	public static void httpPut(String reqPrefix, String fileName, String contents){
		Connector c = new Connector();
		c.buildRequest(RequestType.PUT,reqPrefix,fileName);
		c.writerAuthorize();
		c.setPutContent(contents);
		c.execute();
		c.close();
	}

	/**
	 * Executes and HttpGet request with the given command prefix, returns the result as string
	 * @param reqPrefix the location where the command will be executed
	 * @param request the command to execute
	 * @return the response from the database
	 */
	public static String httpGet(String reqPrefix,String request){		
		Connector c = new Connector();
		c.buildRequest(RequestType.GET, reqPrefix, request);
		c.readerAuthorize();
		String s = c.execute();
		c.close();		
		return s;		
	}
	
	public static String httpGet(String reqPrefix,String request, String credentials){		
		Connector c = new Connector();
		c.buildRequest(RequestType.GET, reqPrefix, request);
		c.authorize(credentials);
		String s = c.execute();
		c.close();		
		return s;		
	}
	/**
	 * Same as httpGet, but uses writer credentials
	 * @param reqPrefix
	 * @param request
	 * @return
	 */
	public static String httpGetWriter(String reqPrefix,String request){		
		Connector c = new Connector();
		c.buildRequest(RequestType.GET, reqPrefix, request);
		c.writerAuthorize();
		String s = c.execute();
		c.close();
		return s;		
	}
	
	/**
	 * Same as httpGet, but uses writer credentials, and sends http response code
	 * @param reqPrefix
	 * @param request
	 * @return
	 */
	public int httpGetWriterRep(String reqPrefix,String request){		
		Connector c = new Connector();
		c.buildRequest(RequestType.GET, reqPrefix, request);
		c.writerAuthorize();		
		c.execute();
		c.close();		
		return c.getResponseCode();		
	}

	
	/**
	 * Checks the response code from a BaseX request
	 * @param reqPrefix the location for the command to be executed
	 * @param request the command to execute
	 * @return the response code from the execution
	 */
	public static int httpGetRep(String reqPrefix,String request){
		Connector c = new Connector();
		c.buildRequest(RequestType.GET, reqPrefix, request);
		//c.authorize();		
		c.readerAuthorize();
		c.execute();
		int rep = c.getResponseCode();
		c.close();		
		return rep;
	}
	
	/**
	 * Validates the insert query against the schema 
	 * @param query the insertion query
	 * @param handle the name of the file
	 * @return whether the insert statement is valid
	 */
	public static boolean validateTest(String query, String handle){
		/*TODO: Rewrite this method
		 
		Config config = Config.getInstance();
		String rootElem = "";
		String schemaURI = "";
		
		//check if we want to validate a comment, bug report, or a change
		if(query.contains("bug")){
			rootElem = "<bugReports/>";
			schemaURI = config.getReportSchemaURI();
		}
		
		//Create the test database
		command("CREATE DB test " + rootElem);
		
		//insert the new element defined by the insertion query
		write(query);
		
		//read the new element back in XML format
		String xmlDoc = get("test.xml");
		
		//Delete the test database 
		command("DROP DB test");
		
		//create validating object
		XMLHandle x = new XMLHandle(xmlDoc, schemaURI);

		//validate
		return x.isValid();
		*/
		return true;
	}
	
	/**
	 * Ensures BaseX has the proper databases upon startup
	 * Adds databases:
	 * -ced2ar
	 * -index - will blank index file
	 * -schemas - with specified schemas
	 * -versions - with version file
	 */
	public static void integrityCheck(){
		//Checks to see if ced2ar database is present
		//Needs to be all caps
		if(httpGetRep("rest/", "CED2AR") != 200){
			command("CREATE DB CED2AR");
		}
		
		//Checks to see if prov database is present
		//Needs to be all caps
		if(httpGetRep("rest/", "prov") != 200){
			command("CREATE DB prov");
			//TODO: Placeholder for now, pulls from dev server. 
			//Might not be needed in the future since we're using Neo4j
			command("store to prov.json http://104.131.43.251/docs/misc/json/blankprov.json","/prov");
		}
		
		//Will also throw 404 if not filled
		if(httpGetRep("rest/", "CED2ARMaster") != 200){
			command("CREATE DB CED2ARMaster");
		}
		
		//Checks to see if index database is present
		if(httpGetRep("rest/", "index") != 200){
			String xml = "<codeBooks xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"cdr http://www.ncrn.cornell.edu/docs/cdr/cdr.xsd\" />";
			command("CREATE DB index");
			command("add TO index "+xml,"/index/");
		}
		
		//Checks to see if schemas database is present
		if(httpGetRep("rest/", "schemas") != 200){	
			//Creates schemas database
			command("CREATE DB schemas");	
		}	
		
		//Checks to see if git database is present
		if(httpGetRep("rest/", "git") != 200){	
			//Creates schemas database
			command("CREATE DB git");	
			String xml ="<git><commits/><pending/></git>";
			command("add TO git "+xml,"/git/");
		}	

		// Checks to see if CIMS database is present
		/*
		if(httpGetRep("rest/", "cims") != 200){	
			//Creates schemas database
			//command("CREATE DB cims");	
			//String xml =getDefaultCIMSDatabseXML();
			//command("add TO git "+xml,"/git/");
		}	
		*/
		
		//Checks to see if users database is present
		/*
		if(httpGetRep("rest/", "users") != 200){	
			//Creates schemas database
			command("CREATE DB users");	
			String xml ="<users />";
			command("add TO users "+xml,"/users/");
		}*/
		
		//List of schemas to add to BaseX
		Map<String, String> schemas = new HashMap<String, String>();
		
		schemas.put("ddi", "http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN/schemas/codebook.xsd");
		//schemas.put("bugreports", "http://www.ncrn.cornell.edu/docs/cdr/bugreports.xsd");//Unused

		
		//Loop over list and add to BaseX if needed
		for (String name : schemas.keySet()) {		
			String schemaUrl = schemas.get(name);
			//If schema not present
			//if(httpGetRep("rest/schemas/", name) >= 400){//TODO: New BaseX doesn't return 404's
				//Tries to retrieve schema from web
				String xsd = "";
				Connector c = new Connector(schemaUrl);
				c.buildRequest(RequestType.GET);
				xsd = c.execute();
				//If copy on NCRN server not reachable, pull locally from project
				if(c.getResponseCode() >= 400){
					ClassLoader classloader = Thread.currentThread().getContextClassLoader();
					InputStream ins = classloader.getResourceAsStream("schemas/"+name+".xsd");
					StringWriter writer = new StringWriter();
					try {
						IOUtils.copy(ins, writer, "UTF-8");
						xsd = writer.toString();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//Adds schema
				putB(name, xsd, "schemas/");
			//}
		}			
	}

	/**
	 * Validate the connection with OLD credentials
	 * Change Password using uid and new Password.
	 * validate connection with new credentials.
	 * @param baseXUri  Uri of basex.  Ex. http://localhost:8080/BaseX73
	 *  
	 * @param oldCredentials Hashed uid:password  (OLD)
	 * @param uid user id.  Should be same as the one in oldCredentials base64 encoded uid:password 
	 * @param newPassword password  (NEW)  Plain Text
	 * 
	 * @return Return true if all the above steps are successful; return false otherwise.
	 */
	public static boolean changePassword(String baseXURI, String oldCredenials, String uid, String newPassword){
		boolean successful = true;
		successful = validateConnection(baseXURI, oldCredenials);
		if(!successful){
			logger.info("Invalid current credentials passed or connection timeout.  Password is not changed" );
			return successful;
		}
		
		logger.info("Changing password ... " );
		//String newPasswordInHash = getHashValue(newPassword);
//		httpGet("rest?command=","password "+ newPasswordInHash,oldCredenials);
		
		httpGet("rest?command=","password "+ newPassword,oldCredenials);
		String encodedNewUIDPwd 	= new String(Base64.encodeBase64((uid+":"+newPassword).getBytes()));

		successful = validateConnection(baseXURI, encodedNewUIDPwd);
		if(!successful){
			logger.info("Unable to connect with new credentials. Password is not changed." + baseXURI);
		}		
		return successful;
	}
	
/*
	public static boolean changeBaseXDB(String baseXURI, String adminCredentials, String readerCredentials, String writerCredentials){
		boolean successful = true;
		
		successful = validateConnection(baseXURI, adminCredentials);
		if(!successful){
			logger.info("Invalid admin credentials passed." );
			return successful;
		}
		successful = validateConnection(baseXURI, readerCredentials);
		if(!successful){
			logger.info("Invalid reader credentials passed." );
			return successful;
		}
		
		successful = validateConnection(baseXURI, writerCredentials);
		if(!successful){
			logger.info("Invalid writer credentials passed." );
			return successful;search
		}
		return successful;
	}
	*/

	
	
	protected static String getHashValue(String plainText){
		 try{
		  MessageDigest m = MessageDigest.getInstance("MD5");
		  m.reset();
		  m.update(plainText.getBytes());
		  byte[] digest = m.digest();
		  BigInteger bigInt = new BigInteger(1,digest);
		  String hashtext = bigInt.toString(16);
		  // Now we need to zero pad it if you actually want the full 32 chars.
		  while(hashtext.length() < 32 ){
		    hashtext = "0"+hashtext;
		  }
		  return hashtext;
		 }
		 catch(Exception ex){
			 logger.error("Error in creating hash value for password", ex);
			 return plainText;
		 }
	}


	/**
	 * BaseX Reader, Writer and Admin passwords are changed to some randomly generated passwords.
	 * Step 1. Change the admin password. If successful, write the encoded password in the properties file.  If failed return false;
	 * Step 2. Change the reader password. If successful, write the encoded password in the properties file.  If failed return false;
	 * Step 3. Change the writer password. If successful, write the encoded password in the properties file AND 
	 * 		   set basexGenerateRandomPasswords flag to false and return true. If failed return false;  
	 * @return true if the passwords are randomized. 
	 */
	 public static boolean randomizePasswords(){
		 boolean success = true;
		 Config config = Config.getInstance();
		 
		 if(!config.isBasexGenerateRandomPasswords()){
			logger.debug("Password randomizer exiting because PwdIsRandom property is false");
			return true;
		}
		 
		ConfigurationProperties configurationProperties = new ConfigurationProperties();
		Map<String, String> propertiesMap = configurationProperties.getPropertiesMap();
		logger.debug("Randomizing admin password ...");
		success=randomizePassword(propertiesMap,config,configurationProperties, "admin", "baseXAdminHash");
		 
		if(success)
			logger.debug("Successfully randomized admin password. Randomizing reader Password");
		else
			logger.debug("Error in randomizing admin password aborting randomizing reader and writer. Error message: " );
		 
		 
		if(success){
		 success=randomizePassword(propertiesMap,config,configurationProperties,"reader", "baseXReaderHash");
		 
		if(success)
			logger.debug("Successfully randomized reader password. Randomizing writer Password");
		else
			logger.debug("Error in randomizing reader password aborting randomizing  writer. Error message: " );
		 }

		 if(success){
			 success=randomizePassword(propertiesMap,config,configurationProperties,"writer", "baseXWriterHash");
			 if(success)
				 logger.debug("Successfully randomized writer password.");
			 else
				 logger.debug("Error in randomizing writer password. ");
		 }

		 
		if(success){
			try{
				configurationProperties.addProperty("basexGenerateRandomPasswords", "false");
			}
			catch(Exception ex){
				logger.debug("Successfully randomized password.  But failed to update .properties file. ", ex);
			}
		}
		 return success;	 
	}
	/**
	 * Private method. Sets passwords to a randomly generated password. 
	 * If successful adds the value to the .properties file and return true.
	 * If failed, returns false.
	 * @param propertiesMap
	 * @param config
	 * @param configurationProperties
	 * @param userId
	 * @param configurationKey
	 * @return
	 */
	private static boolean randomizePassword(Map<String, String> propertiesMap,Config config,ConfigurationProperties configurationProperties,String userId, String configurationKey){
		boolean success = true;
		
		String originalUidPassword = propertiesMap.get(configurationKey);
		String encodedCurrentCredentials =  new String(Base64.encodeBase64((originalUidPassword).getBytes()));
		String randomPassword = RandomStringUtils.random(12, true, true);
		
		try{
			String basexUrl = config.getBaseXDB();
			success = BaseX.changePassword(basexUrl, encodedCurrentCredentials, userId, randomPassword);
			if(success){
				String encodedNewCredentials =  new String(Base64.encodeBase64((userId+":"+randomPassword).getBytes()));
				configurationProperties.addProperty(configurationKey, encodedNewCredentials);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			success = false;
		}
		return success;
	}

	private static String getDefaultCIMSDatabseXML(){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<cims xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		sb.append("</cims>");
		return sb.toString();
	}
	
}