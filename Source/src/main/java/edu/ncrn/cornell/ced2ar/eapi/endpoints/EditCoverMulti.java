package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;

/**
 * Class to handle variable edit requests with multiple fields being changed at once
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class EditCoverMulti extends ServerResource{
	
	private static final Logger logger = Logger.getLogger(EditCoverMulti.class);
	
	/**
	 * Method is called an edits to variables are made. Checks to make sure a valid field is being
	 * edited, then updates the xml, and finally writes to BaseX
	 * @param entity entity holding the information about the edit being made
	 * @return Representation success or failure message
	 */
	@Post
	public Representation editCover(Representation entity) {
		System.out.println("Test");
		try{
			if( MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
				String message = "Invalid argument type. Must be multipart form.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}
		}catch(NullPointerException e){
			String message = "Bad arguments. "+
			"Required: paths = <paths to replace> and values = <new values to use>";
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
			"Required: paths = <paths to replace> and values = <new values to use>";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}

		String user = "anonymous";
		ArrayList<String> paths = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		
		boolean doesAppend = false;
		String baseHandle = (String) getRequestAttributes().get("baseHandle");
	    String version = ((String) getRequestAttributes().get("version")).replaceAll("[^A-Za-z0-9\\- ]", "");
		String handle = baseHandle+version;
		
		for(FileItem f : files){		
		  	if(f.isFormField()){
			  	switch(f.getFieldName()){
				  	case "paths":
				  		paths.add(QueryUtil.sanatize(f.getString()));
				  	break;
				  	case "values":
				  		values.add(QueryUtil.sanatize(f.getString()));
					break;
				  	case "append":
				  		if(QueryUtil.sanatize(f.getString()).equals("true")) doesAppend = true;
				  	break;
				  	case "user":
				  		user =  f.getString();
				  	break;
					default:
						String message = "Bad arguments. "+
						"Required: paths = <paths to replace> and values = <new values to use>";
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			  	}
		  	}else{
		  		String message = "Bad arguments. "+
		  		"Required: paths = <paths to replace> and values = <new values to use>";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		  	}
	  	}

		//Auto updates version
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		timestamp+= " (auto-generated)";
		
		XMLHandle xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());		
		for(int i = 0; i < paths.size(); i++){
			String p = paths.get(i);
			Boolean append = doesAppend;
			if(p.contains("@")) append = true;
			System.out.println("/codeBook/"+p);
			xh.addReplace("/codeBook/"+p, values.get(i), append, true, false, true);
			if(xh.getError() != null){
				String message = "Bad arguments xpath fields given: "+xh.getError();
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}
		}
	
		BaseX.put(handle, xh.docToString().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>"));
		String xquery = "let $path := collection('CED2AR/"+handle+"')"
			+"/codeBook/docDscr/citation/verStmt/version/@date"
			+" return replace value of node $path with '"+timestamp+"'";
		BaseX.write(xquery);
		
		if(Config.getInstance().isGitEnabled()){
			QueryUtil.insertPending(handle, "cover", "/codeBook", user);//TODO: Add more detailed xpath?
		}
		
		Representation response = new StringRepresentation("XML successsfully replaced", MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;	
	}
}