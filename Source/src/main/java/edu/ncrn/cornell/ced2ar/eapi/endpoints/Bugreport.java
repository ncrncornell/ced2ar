package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;  
import org.restlet.representation.StringRepresentation;
//import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;

/**
 *Class to parse Bug Report insertion requests
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Bugreport extends ServerResource {

	/**
	 * Method parses entity and then inserts relevant information into the bug report database
	 * @param entity the entity containing all of the bug report information
	 * @return Representation success or failure message
	 */
	@Post
	public Representation addReport(Representation entity){
	
		String bugType = null;
		String description = null;
		String reproductionSteps = null;
		String name = null;
		String email = null;
		String ip = null;
		String timeStamp = null;
		String userAgent = null;
		String lastPage = null;
		
		try{	
			DiskFileItemFactory factory = new DiskFileItemFactory();
		    factory.setSizeThreshold(1048576);
		    RestletFileUpload upload = new RestletFileUpload(factory); 
		    List<FileItem> files = upload.parseRepresentation(entity);			  

		    //Parses form-data
		    for(FileItem f : files){		    	
		    	if(f.isFormField() && f.getFieldName().equals("bugType")){
		    		 bugType = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("bugDescription")){
		    		description = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("reproductionSteps")){
		    		reproductionSteps = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("yourName")){
		    		name = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("yourEmail")){
		    		email = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("ip")){
		    		ip = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("timestamp")){
		    		timeStamp = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("userAgent")){
		    		userAgent = f.getString(); 
		    	}
		    	else if(f.isFormField() && f.getFieldName().equals("lastPage")){
		    		lastPage = f.getString(); 
		    	}
		    	else{
		    		String message = "Bad arguments";
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		    	}	
		    }
		}
		catch(FileUploadException e ){
	    	String message = "Bad arguments";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
		}
		
		
		if(description == null || bugType == null || reproductionSteps == null || name == null 
			|| email == null || ip == null || timeStamp == null || userAgent == null || lastPage == null){
			
			String message = "Required arguments missing.";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		/*if(!bugType.matches("Coding|Design|Documentation|Hardware|Suggestion|Other")){
			String message = "Malformed input";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}*/	
		
		QueryUtil.insertReport(bugType, description, reproductionSteps, name, email, ip, timeStamp, userAgent, lastPage);
		
		Representation response = new StringRepresentation("Bug report added", MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}
}
