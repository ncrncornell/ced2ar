package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *Class to handle var group edits
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
//TODO: Implement this class
public class VarGrp extends ServerResource{
	
	@Autowired
	private ServletContext context;
	
	private static final Logger logger = Logger.getLogger(VarGrp.class);
	
	/**
	 * Adds or edits a var group
	 * @param entity
	 * @return
	 */
	@Post
	public Representation editGrp(Representation entity){
		String id = ((String) getRequestAttributes().get("id")).toLowerCase();
		String baseHandle = ((String) getRequestAttributes().get("baseHandle")).toLowerCase();
		String version = ((String) getRequestAttributes().get("version")).toLowerCase();
		String handle = baseHandle+version;
		
		if(entity == null){ 	
			String message = "Form required. Required args: name, labl, and txt.";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		if(MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
			String message = "Bad arguments. Required name, labl, and txt.";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}

	    String name = "";
		String labl = "";
		String txt = "";
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
	    factory.setSizeThreshold(10485760);
	    RestletFileUpload upload = new RestletFileUpload(factory); 
	    List<FileItem> files;
		try{
			files = upload.parseRepresentation(entity);
			
			 //Parses form into fields
		    for(FileItem f : files){		    	
		    	if(f.isFormField() && f.getFieldName().equals("name")){
		    		name = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");
		    	}else if(f.isFormField() && f.getFieldName().equals("labl")){
		    		labl = f.getString().replaceAll("[^A-Za-z0-9 \\-\\,\\.]", "");
		    	}else if(f.isFormField() && f.getFieldName().equals("txt")){
		    		txt = f.getString().replaceAll("[^A-Za-z0-9 \\-\\,\\.]", "");
		    	}
		    	else{
		    		String message = "Bad arguments. Required name, labl, and txt.";
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		    	}	
		    }
			
		}catch (FileUploadException e) {
			String message = "Bad arguments. Required name, labl, and txt.";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		String replaceStmt = "";	
		if(!name.equals("")){
			replaceStmt += replaceStmt.equals("") ? "" : ", ";
			replaceStmt += "replace value of node $c/@name with '"+name+"'";
		}
		if(!labl.equals("")){
			replaceStmt += replaceStmt.equals("") ? "" : ", ";
			replaceStmt += "replace value of node $c/labl with '"+labl+"'";
		}
		if(!txt.equals("")){
			replaceStmt += replaceStmt.equals("") ? "" : ", ";
			replaceStmt += "replace value of node $c/txt with '"+txt+"'";
		}

		String xquery = "let $d := collection('CED2AR/"+handle+"')/codeBook/dataDscr"
		+" let $g := $d/varGrp[@ID='"+id+"']"
		+" return if($g) then"
		+" let $x:= copy $c := $g modify("+replaceStmt+") return $c"
		+" return replace node $g with $x"
		+" else insert node element varGrp {"
		+" attribute ID {'"+id+"'},"
		+" attribute name {'"+name+"'},"
		+" element label {'"+labl+"'},"
		+" element txt {'"+txt+"'}"
		+" } as first into $d";
		BaseX.write(xquery);
		
		Representation response = new StringRepresentation("XML successsfully replaced", MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}	
	
	/**
	 * Removes a var group
	 * @param entity
	 * @return
	 */
	@Delete
	public Representation deleteGrp(Representation entity) {
		String id = ((String) getRequestAttributes().get("id")).toLowerCase();
		String baseHandle = ((String) getRequestAttributes().get("baseHandle")).toLowerCase();
		String version = ((String) getRequestAttributes().get("version")).toLowerCase();
		String handle = baseHandle + version;
		
		String xquery = "let $n := collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp[@ID='"+id+"'] "+
		"return delete node $n";
		
		BaseX.write(xquery);
		Representation response = new StringRepresentation("Group "+id+" successfully removed", MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}		
}