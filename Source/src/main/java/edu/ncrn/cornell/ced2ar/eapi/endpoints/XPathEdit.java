package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;
import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;

/**
 * Class to handle codebook edit requests
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class XPathEdit extends ServerResource{
	
	private static final Logger logger = Logger.getLogger(XPathEdit.class);
	private static boolean replaceChildren = true;
	
	/**
	 * Method is called an edits to title pages are made. Checks to make sure a valid field is being
	 * edited, then updates the xml, and finally writes to BaseX
	 * @param entity entity holding the information about the edit being made
	 * @return Representation success or failure message
	 */
	@Post
	public Representation editCodebook(Representation entity) {
		try{
			if( MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
				String message = "Invalid argument type. Must be multipart form.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}
		}catch(NullPointerException e){
			String message = "Error, requires form arguments";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
	    factory.setSizeThreshold(10485760);
	    RestletFileUpload upload = new RestletFileUpload(factory); 
	    List<FileItem> files = null;
		try {
			files = upload.parseRepresentation(entity);
		} catch (FileUploadException e1) {
			String message = "Bad form data recieved";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		if(!files.equals(null) && (files.size() == 0)){
	    	String message = "Missing required arguments. "+
	    	"Required: field = <field to replace> "+
	    	"and value = <new value to use>";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		String baseHandle = (String) getRequestAttributes().get("baseHandle");
	    String version = ((String) getRequestAttributes().get("version")).replaceAll("[^A-Za-z0-9\\- ]", "");
		String handle = baseHandle+version;
		
		if(!Utilities.codebookExists(handle)){
			String message = "This codebook does not exist";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		
		String xpath = "";	
		String value = "";

		boolean delete = false;
		boolean append = false;
		String user = "anonymous";
		
		for(FileItem f : files){		
		  	if(f.isFormField()){
			  	switch(f.getFieldName()){
				  	case "value":
				  		value =  QueryUtil.sanatize(f.getString());
				  	break;
				  	case "xpath":
				  		xpath =  QueryUtil.sanatize(f.getString());
					break;
				  	case "append":
				  		if(QueryUtil.sanatize(f.getString()).equals("true")) append = true;
				  	break;
					case "delete":
				  		if(QueryUtil.sanatize(f.getString()).equals("true")) delete = true;
				  	break;
				  	case "user":
				  		user =  f.getString();
				  	break;
					default:
						String message = "Bad arguments. "+
						"Required: xpath = <targeted xpath> "+
				    	"and value = <new value to use>";
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			  	}
		  	}else{
		  		String message = "Bad arguments. "+
				"Required: xpath = <targeted xpath> ";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		  	}
	  	}
		
		if(delete && append){
			String message = "Sorry, you can't delete and append the same value";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}

		try{
			xpath = xpath.replaceAll("[^A-Za-z0-9@/\\-\\[\\]=]", "");
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpathTest = xpathFactory.newXPath();
			XPathExpression expr = xpathTest.compile(xpath);
			
		} catch (XPathExpressionException e) {
			String message = "Bad xpath given: "+xpath;
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		if(xpath.equals("")){
			String message = "Required fields "+
			"Required: xpath = <targeted xpath> "+
	    	"and value = <new value to use>";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	  
		}
		if(value.equals("") && delete == false){
			String message = "Required value to replace or delete to be true";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	  
		}
		
		//TODO: Add more detailed restrictions in the future
	  	if(xpath.startsWith("codeBook/dataDscr")){
	  		String message = "XPath cannot edit dataDscr";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	  
	  	}
	  	
	  	if(xpath.startsWith("codeBook/")){
	  		xpath = "/" + xpath; 
	  	}
	  	
	  	if(delete){
	  		String xquery = "delete node collection('CED2AR/"+handle+"')"+xpath;
			BaseX.write(xquery);		
	  	}else if(append){
	  		XMLHandle xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());
	  		xh.addReplace(xpath, value, true, true, false, replaceChildren);
	  		BaseX.put(handle, xh.docToString().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>"));
	  		/*
	  		if(xh.isValid()){
	  			
	  		}else{
	  			String message = "Bad xpath, or DDI document is corrupt";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	  
	  		}*/
	  	}else{
	  		//Avoid opening XMLHandle to save time
			String xquery = "let $path := collection('CED2AR/"+handle+"')"+xpath+
			" return replace value of node $path with '"+value+"'";
			BaseX.write(xquery);
	  	}
	  	
	  	if(Config.getInstance().isGitEnabled()){
	  		QueryUtil.insertPending(handle, "cover",xpath,user);
		}
		
		//Update fullname
		if(xpath.equals("/docDscr/citation/titlStmt/titl")){
			QueryUtil.updateFullName(baseHandle, version, value);
		}
	  	
		Representation response = new StringRepresentation("XML successsfully replaced", MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}	
}