package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.apache.log4j.Logger;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;

/**
 *Handles adding, deleting or modifying codebooks
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Codebook extends ServerResource{
	
	private static final Logger logger = Logger.getLogger(Codebook.class);
	
	/**
	 * Adds or replaces a codebook
	 * @param entity the entity containing the codebook and relevant information
	 * @return Representation success or failure message
	 */
	@Post
	public Representation handleUpload(Representation entity) {
		if(entity == null) {	    
			String message = "No arguments given. Required - file = {inputXML}";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	      
		}
		if(MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
			String message = "Invalid argument type. Must be multipart form.";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		InputStream ins = null;
		
		try{	
			DiskFileItemFactory factory = new DiskFileItemFactory();
		    factory.setSizeThreshold(10485760);
		    RestletFileUpload upload = new RestletFileUpload(factory); 
		    List<FileItem> files = upload.parseRepresentation(entity);
		  
		    FileItem file = null;
		    String baseHandle = ((String) getRequestAttributes().get("baseHandle")).toLowerCase();
		    
		    String version = ((String) getRequestAttributes().get("version")).replaceAll("[^A-Za-z0-9\\- ]", "").toLowerCase();
			String handle = baseHandle+version;
		    String label = null;

		    //Parses form into file and 
		    for(FileItem f : files){		    	
		    	if(!f.isFormField() && f.getFieldName().equals("file")){
		    		file = f;
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("label")){
		    		label = f.getString().replaceAll("[^A-Za-z0-9 \\-]", ""); 

		    	}else{
		    		String message = "Bad arguments. Required file = <inputXML>";
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		    	}	
		    }
		    
		    //Handles must be 20 alphanumeric chars or fewer 
		    if(baseHandle.length() > 15 | !baseHandle.matches("^[a-zA-Z0-9\\-]*$")){
		    	String message = "File handle must be alphanumeric and atmost 15 characters.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
		    }
		    
		    //Version must be 20 alphanumeric chars or fewer 
		    if(version.length() > 15 | version.matches(".*-\\W+.*")){
		    	String message = "Version must be alphanumeric and atmost 15 characters.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
		    }
		    
		    //label must be 15 alphanumeric chars or fewer 
		    if(label!= null && label.length() > 15 | label.matches(".*-\\W+ .*")){
		    	String message = "Label must be alphanumeric and atmost 15 characters.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
		    }
		    		
		    try{
		    	ins = file.getInputStream();   
		    }catch(NullPointerException e){
		    	String message = "Error. Did not recieve uploade file.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
		    }
		    
			XMLHandle xh = null;
			xh = new XMLHandle(ins,Config.getInstance().getSchemaURI());
			
		    //Adds missing elements required for vars
		    xh.addVarBlanks("txt");
		    xh.addVarBlanks("labl");
		    
		    //Adds var IDs if missing
		    xh.addIDs();
		    
		    //Adds docDscr title if not present
		    if(xh.getValue("/codeBook/docDscr/citation/titlStmt/titl") == null){
		    	String title = "";
		    	try{
		    		title = xh.getValue("/codeBook//titl").replaceAll("[^A-Za-z0-9\\-\\[\\]\\(\\)\\. ]", "");
		    	}catch(NullPointerException e){}
		    	if(title == null || title.equals("")){
		    		title = handle;
		    	}
		    	//TODO: Causes bug and adds two titles
		    	xh.addReplace("/codeBook/docDscr/citation/titlStmt/titl", title, true, true, false, false);
		    }
		    
		    //Adds default access levels if absent, 
		    //TODO: Maybe add in switch? removing for now since editing can handle this
		    /*
		    if(xh.getValue("/codeBook/stdyDscr/dataAccs/@ID") == null){ 
			    //TODO:Fix add replace to allow inserting parent with ID and child
			    String releasable="/codeBook/stdyDscr/dataAccs[2]/@ID";
			    String restricted="/codeBook/stdyDscr/dataAccs[3]/@ID";
			    xh.addReplace(releasable, "releasable", true, true, false, true);
			    xh.addReplace(restricted, "restricted", true, true, false, true);
			    xh.addReplace("/codeBook/stdyDscr/dataAccs[@ID='releasable']/useStmt/restrctn", 
			    	"Elements flaged with this access level can be released", true, true, false, true);
			    xh.addReplace("/codeBook/stdyDscr/dataAccs[@ID='restricted']/useStmt/restrctn", 
			    	"Elements flaged with this access level cannot be released", true, true, false, true);
		    }*/
		    
		    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
			timestamp+= " (upload date)";
			
			//Error where blank version without date prevents insertion
			xh.addReplace("/codeBook/docDscr/citation/verStmt/version/@date", timestamp, false, true, true, true);	
			try{
				xh.getValue("/codeBook/docDscr/citation/verStmt/version/@date").equals(timestamp);
			}catch(NullPointerException e){
				xh.addReplace("/codeBook/docDscr/citation/verStmt/version/@date", timestamp, true, true, true, true);
			}
   
		    String software="The Comprehensive Extensible Data Documentation and Access Repository 2.5";
		    xh.addReplace("/codeBook/docDscr/citation/prodStmt/software", software, true, true, false, false);
		    
		    //Validates XML 
		    try{
			    if(!xh.isValid()){
			    	String message = xh.getError();
			    	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
			    }
		    }catch(NullPointerException e){
			    String message = "File given is not a well formed XML document.";
			    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	
		    }
		    
		    logger.info("File is valid. Uploading to database...");
    
		    //Checks to see if codebook version exists
		    if(!QueryUtil.hasVersionIndex(baseHandle, version)){
		    	//shortName and fullName now generated one from DDI
		    	String shortName = "";
		    	try{
					shortName = shortName.trim();
					shortName= xh.getValue("//codeBook/docDscr/citation/titlStmt/altTitl");	
					shortName = shortName.replaceAll("[^A-Za-z0-9\\-]", "").toUpperCase();
					if(shortName.length()>=15)
						shortName = shortName.substring(0,14);
			    }catch(NullPointerException e){
			    	shortName = handle;
			    }
				String fullName = "";
			    try{
			    	fullName= xh.getValue("//codeBook/docDscr/citation/titlStmt/titl");			 
			    	fullName = fullName.replaceAll("[^A-Za-z0-9\\-\\[\\]\\(\\) ]", "").trim();
			    }catch(NullPointerException e){
			    	fullName = handle;
			    }
				    
			    //Checks to see if any version of codebook exists, if not add parent element
			    if(!QueryUtil.hasCodebookIndex(baseHandle)){
			    	if(label == null || label.equals("")){
			    		String message = "Handle does not exist, so a label is required";
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
			    	}
			    	QueryUtil.insertCodebookIndex(baseHandle,label);
			    }else if(label != null && !label.equals("")){
			    	//Otherwise, update label
			    	QueryUtil.updateLabel(baseHandle, label);
			    }
			    //Inserts specific version
		    	QueryUtil.insertCodebookVersionIndex(baseHandle, version, shortName, fullName);
		    }
		      
		    //Uploads codebook
		    BaseX.put(handle, xh.getRepoXML());
		    
		    //New BaseX config
		    //BaseX.put2(handle, xh.getRepoXML());
		    
		    Representation response = new StringRepresentation("Codebook updated", MediaType.TEXT_PLAIN);
		    this.setStatus(Status.SUCCESS_OK);
		    return response;
			
		}
		catch(FileUploadException | IOException e ){
	    	String message = "Invalid file name. Must be an XML file under 10mb.";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
		}
		finally{
			ins = null;
		}
	}
	
	/**
	 * Removes a codebook from CED2AR and index
	 * @param entity entity containing the information regarding which codebook to delete
	 * @return Representation success or failure message
	 */
	@Delete
	public Representation delete(Representation entity) {
			String baseHandle = (String) getRequestAttributes().get("baseHandle");
			String version = (String) getRequestAttributes().get("version");
			String handle = baseHandle+version;
			try{
				QueryUtil.deleteVersion(baseHandle, version);
				BaseX.delete(handle);	    
			    Representation response = new StringRepresentation("Codebook deleted", MediaType.TEXT_PLAIN);
				this.setStatus(Status.SUCCESS_OK);
				return response;	    	
			}catch (NullPointerException e) {
				String message = "Error delete codebook";
		    	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
			}			
	}
}