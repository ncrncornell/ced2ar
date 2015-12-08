package edu.ncrn.cornell.ced2ar.api.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

/**
 * HTTP connections class
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Connector{
	
	private static final Logger logger = Logger.getLogger(Connector.class);
	
	/**
	 * Enum for the type of request
	 */
	public enum RequestType{ GET, POST, PUT, DELETE };
	
	private String host;
	private CloseableHttpClient client;
	private CloseableHttpResponse response;
	
	private RequestType mode = null;
	private MultipartEntity entity = null;
	private HttpGet hGet = null;
	private HttpPost hPost = null;
	private HttpPut hPut = null;
	private HttpDelete hDelete = null;
	
	/**
	 *Constructor with default host
	 */
	protected Connector(){
		Config config = Config.getInstance();
		    
		//this.host = config.getbaseXInfo()[0];
		this.host = config.getBaseXDB();
				
		RequestConfig requestConfig = RequestConfig.custom()
			    .setSocketTimeout(config.getTimeout())
			    .setConnectTimeout(config.getTimeout())
			    .build();
		this.client = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig).build();
	}

	/** 
	 * Constructor with provided host	
	 * @param uri String the URI to connect to
	 * @throws URIException  */
	public Connector(String uri){		
		Config config = Config.getInstance();
		try {
			this.host = URIUtil.encodeQuery(uri);
		} catch (URIException e) {
			e.printStackTrace();
			return;
		}
		RequestConfig requestConfig = RequestConfig.custom()
			    .setSocketTimeout(config.getTimeout())
			    .setConnectTimeout(config.getTimeout())
			    .build();
		client = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig).build();
	}
	
	/**
	 * Constructor with server name and index of sub application. Used for accessing docs and admin
	 * @param server name of server
	 * @param i index application to access
	 * @param postFix to add after application name
	 */
	public Connector(String server, int i, String postFix){		
		Config config = Config.getInstance();
		String app = "";
		switch(i){
			default:
				app = "/"+config.geteAPI();
		    break;
		}
		try {
			this.host = URIUtil.encodeQuery(server+app+postFix);
		} catch (URIException e) {
			e.printStackTrace();
			return;	
			
		}
		RequestConfig requestConfig = RequestConfig.custom()
			    .setSocketTimeout(config.getTimeout())
			    .setConnectTimeout(config.getTimeout())
			    .build();
		client = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig).build();
	}
	
	/** 
	 * Builds the http request object according the the requestType provided
	 * with strings prefix and request appended to host  
	 * @param type RequestType the type of request to be executed
	 * @param prefix String the location beyond the base URI to be access (i.e. /ced2ar)
	 * @param request String the request to be executed
	 */
	public void buildRequest(RequestType type, String prefix, String request){
		try {
			request = URLEncoder.encode(request, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return;
		}
		
		this.mode = type;
		switch(type){
		case GET:
			this.hGet = new HttpGet(this.host+prefix+request);
			break;
		case DELETE:
			this.hDelete = new HttpDelete(this.host+prefix+request);
			break;
		case POST:
			this.hPost = new HttpPost(this.host+prefix+request);
			break;
		case PUT:
			this.hPut = new HttpPut(this.host+prefix+request);
			break;
		default:
			break;
		}		
	}
	
	/**
	 * overloaded method; builds the http request with no additions to the host 
	 * @param type RequestType the type of request to be executed
	 */
	public void buildRequest(RequestType type){
		
		this.mode = type;
		switch(type){
		case GET:
			this.hGet = new HttpGet(this.host);
			break;
		case DELETE:
			this.hDelete = new HttpDelete(this.host);
			break;
		case POST:
			this.hPost = new HttpPost(this.host);
			break;
		case PUT:
			this.hPut = new HttpPut(this.host);
			break;
		default:
			break;
		}		
	}	
	
	/**
	 * Sets header fields for the given http request object.
	 * assumes that buildRequest has already been called.
	 * @param field String the header field to be set
	 * @param value String the value of the field
	 */
	public void setHeader(String field, String value){
		
		if(this.mode == null){
			return;
		}		
		switch(this.mode){
		case GET:
			this.hGet.addHeader(field,value);
			break;
		case DELETE:
			this.hDelete.addHeader(field,value);
			break;
		case POST:
			this.hPost.addHeader(field,value);
			break;
		case PUT:
			this.hPut.addHeader(field,value);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Sets the file content for put requests
	 * if the type isn't PUT, method returns.
	 * @param contents String the contents of the put request to be added
	 */
	public void setPutContent(String contents){
		if(this.mode != RequestType.PUT) return;
		this.hPut.setEntity(new ByteArrayEntity(contents.getBytes()));
	}
	
	/**
	 * Adds a field and value to the form for Post request.
	 * @param field String the field to be set in the Post form
	 * @param value String the value corresponding to the field
	 */
	public void setPostFormContent(String field, String value){
		if(this.entity == null) this.entity = new MultipartEntity();
		try {
			this.entity.addPart(field, new StringBody(value));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds body content to a post request
	 */
	public void setPostBody(String content)
	{
		if(this.mode != RequestType.POST) return;
		try {
			this.hPost.setEntity(new ByteArrayEntity(content.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * adds an inputStream as a file to the form for the post request
	 * @param ins the inputStream of the file
	 * @param fileName the file name
	 */
	public void setPostFile(FileUpload fileUpload, String fileName){
		if(this.entity == null) this.entity = new MultipartEntity();
		MultipartFile file = fileUpload.getFile();			
		InputStream ins;
		try {
			ins = file.getInputStream();
			InputStreamBody inb = new InputStreamBody(ins, fileName);
			this.entity.addPart("file", inb);
		} catch (IOException e) {
			logger.error("Error setting post file from FileUpload");
			e.printStackTrace();
		}	
	}
	
	/**
	 * adds an inputStream as a file to the form for the post request
	 * @param ins the inputStream of the file
	 * @param fileName the file name
	 */
	public void setPostFile(InputStream ins, String fileName){
		if(this.entity == null) this.entity = new MultipartEntity();
		InputStreamBody inb = new InputStreamBody(ins, fileName);
		this.entity.addPart("file", inb);
	}
	
	/**
	 * Shortcut method to add authorization
	 * Authorizes using reader hash
	 */
	public void readerAuthorize(){
		//this.setHeader("Authorization", "Basic "+Config.getInstance().getbaseXInfo()[1]);
		this.setHeader("Authorization", "Basic "+Config.getInstance().getBaseXReaderHash());
	}

	/**
	 * Shortcut method to add authorization
	 * Authorizes using writer hash
	 */
	public void writerAuthorize(){
		logger.debug("Writer auth using: Basic "+Config.getInstance().getBaseXWriterHash());
		this.setHeader("Authorization", "Basic "+Config.getInstance().getBaseXWriterHash());
	}

	/**
	 * Shortcut method to add authorization
	 * Authorizes using writer hash
	 */
	public void adminAuthorize(){
		this.setHeader("Authorization", "Basic "+Config.getInstance().getBaseXAdminHash());
	}

	/**
	 * Method authorize.
	 * @param credentials String non-default authorization credentials
	 */
	public void authorize(String  credentials){
		this.setHeader("Authorization", "Basic "+credentials);
	}

	/**
	 * Retrieves a header from the response object.
	 * Assumes that execute() has been called and a response has been set.
	 * @param field String the header field to be retrieved
	 * @return String the header value
	 */
	public String getHeader(String field){
		if(response != null){
			Header h = response.getFirstHeader(field);
			if(h != null) return h.getValue();
		}
		return "";
	}
	
	/**
	 * returns the response code from the request
	 * Assumes that a request has been executed
	 * If response is empty, returns -1
	 * @return int the response code
	 */
	public int getResponseCode(){
		if(response!= null) return response.getStatusLine().getStatusCode();
		
		return -1;
	}
	
	/**
	 * Executes the HTTP request, returns the string that was build from the response data
	 * @return String the HTTTP response
	 */
	public String execute(){
		/*This is a work around to set an authenication header when running in local mode 
		 *or else request to /rest get denied */
		Config config = Config.getInstance();
		if((this.getHeader("") == null || this.getHeader("Authorization").equals("")) 
		&& config.getPwdIsRandom() == true && this.host.startsWith("http://localhost")){
			readerAuthorize();
		}
		
		BufferedReader bd = null;
		StringBuilder sb = null;
		HttpEntity ent = null;
		try{
			response = getResponse();
			logger.debug(response.getStatusLine());
			
            ent = response.getEntity();
            if(this.entity != null && this.mode == RequestType.POST) this.entity = null;
            if(ent != null && this.mode != RequestType.PUT){
            	bd = new BufferedReader( new InputStreamReader(ent.getContent()));
            	sb = new StringBuilder();
            	String line = null;
            	try{
            		while((line = bd.readLine()) != null){
            			sb.append(line + "\n");
            		}
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            }
        }catch(IOException e){
        	e.printStackTrace();
        }finally{
        	try{
				if(bd != null) bd.close();
				if(ent != null)EntityUtils.consume(ent);	
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		if(sb == null) return null;
		else return sb.toString();
		
		
	}
	
	/**
	 * Closes streams and HTTP objects
	 * To be called at the end of use of any Connector object
	 */
	public void close(){
		try{
			if(this.entity != null){
				EntityUtils.consume(this.entity);
				this.entity = null;
			}
			if(this.response != null){
				this.response.close();
				this.response = null;
			}
			this.client.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * private method to get the HttpResponse object by executing the given request
	 * @return CloseableHttpResponse the Http Response object
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private CloseableHttpResponse getResponse() throws ClientProtocolException, IOException{
		switch(this.mode){
		case GET:
			logger.debug("Executing request " + this.hGet.getRequestLine());
			return client.execute(this.hGet);
		case DELETE:
			logger.debug("Executing request " + this.hDelete.getRequestLine());
			return client.execute(this.hDelete);
		case POST:
			//hPost.setEntity(this.entity);
			if (hPost.getEntity() == null) hPost.setEntity(this.entity);
			logger.debug("Executing request " + this.hPost.getRequestLine());
			return client.execute(this.hPost);
		case PUT:
			logger.debug("Executing request " + this.hPut.getRequestLine());
			return client.execute(this.hPut);
		default:
			return null;
		}
	}
}