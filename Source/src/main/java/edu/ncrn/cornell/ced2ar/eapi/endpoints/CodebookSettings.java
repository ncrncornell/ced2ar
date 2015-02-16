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
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;

/**
 * Class to handle editing codebook settings in index
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class CodebookSettings extends ServerResource{
		
	@Post
	public Representation codebookSettings(Representation entity) {
		try{
			if( MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
				String message = "Invalid argument type. Must be multipart form.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}
		}catch(NullPointerException e){
			String message = "Bad arguments. "+
			"Required: use = [ supported | default | deprecated ]";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
	    factory.setSizeThreshold(10485760);
	    RestletFileUpload upload = new RestletFileUpload(factory); 
	    List<FileItem> files = null;
		try {
			files = upload.parseRepresentation(entity);
		} catch (FileUploadException e) {
			String message = "Bad form data recieved";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		if(files.equals(null) || files.size() != 1 ){
	    	String message = "Invalid number of arguments. "+
	    			"Required, 1 argument: use = [ supported | default | deprecated ]";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}

		FileItem f = files.get(0);
		String use = "";
		if(f.isFormField() && f.getFieldName().equals("use")){
			use = f.getString();
			if(!use.equals("supported") && !use.equals("default") && !use.equals("deprecated")){
				String message = "Invalid arguments. "+
		    	"Required: use = [ supported | default | deprecated ]";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	  
			}
		}else{
			String message = "Invalid arguments. "+
	    	"Required: use = [ supported | default | deprecated ]";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	  
		}
		
		String baseHandle = ((String) getRequestAttributes().get("baseHandle")).toLowerCase();
	    String version = ((String) getRequestAttributes().get("version")).replaceAll("[^A-Za-z0-9\\- ]", "").toLowerCase();
	    if(baseHandle.equals("") || version.equals("")){
	    	String message = "Basehandle and version cannot be empty";
	    			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	  
	    }
		QueryUtil.setUse(baseHandle, version, use);
		String message = "Successfully changed";
		Representation home = new StringRepresentation(message, MediaType.TEXT_HTML);
		return home;
	}
}