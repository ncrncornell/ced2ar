package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *Class to handle vars in a group
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

//TODO: Implement this class
public class VarGrpVars extends ServerResource{
	
	@Autowired
	private ServletContext context;
	
	private static final Logger logger = Logger.getLogger(VarGrpVars.class);

	/**
	 * Edits the list of variables that belong to a group
	 * @param entity
	 * @return
	 */
	@Post
	public Representation editVarInGrp(Representation entity){
		String id = ((String) getRequestAttributes().get("id")).toLowerCase();
		String baseHandle = ((String) getRequestAttributes().get("baseHandle")).toLowerCase();
		String version = ((String) getRequestAttributes().get("version")).toLowerCase();
		String handle = baseHandle + version;
		
		if(entity == null){ 	
			String message = "Form required.  Required field - vars";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		if(MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
			String message = "Bad arguments.  Required field - vars";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}

	    String vars = "";
		boolean append = true;
		boolean delete = false;
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
	    factory.setSizeThreshold(10485760);
	    RestletFileUpload upload = new RestletFileUpload(factory); 
	    List<FileItem> files;
		try{
			files = upload.parseRepresentation(entity);
			//Parses form into fields
			for(FileItem f : files){		    	
				if(f.isFormField() && f.getFieldName().equals("vars")){
					vars = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");
				}else if(f.isFormField() && f.getFieldName().equals("append")){
					append = Boolean.valueOf(f.getString()); 
				}else if(f.isFormField() && f.getFieldName().equals("delete")){
					delete = Boolean.valueOf(f.getString()); 
				}else{
					String message = "Bad arguments. Required field - vars";
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
				}	
			}
		}catch(FileUploadException e) {
			String message = "Bad arguments.  Required field - vars";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		String xquery = "let $g := collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp[@ID='"+id+"']";
		if(delete){
			xquery +=" return data($g/@var)";
			String curVar = BaseX.getXML(xquery).trim();
			List<String> varList = new ArrayList<String>(Arrays.asList(curVar.split(" ")));
			List<String> varListR = new ArrayList<String>(Arrays.asList(vars.split(" ")));
			
			String newVars = "";
			for(String var : varList){
				if(!varListR.contains(var)){
					newVars += newVars.equals("")? var : " "+var; 
				}
			}

			String xquery2 = "let $g := collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp[@ID='"+id+"']"
			+" let $cur := $g/@vars "
			+" return replace value of node $g/@var with '"+newVars.trim()+"'";
		
			BaseX.write(xquery2);	
			Representation response = new StringRepresentation("Successfully deleted vars in "+id, MediaType.TEXT_PLAIN);
			this.setStatus(Status.SUCCESS_OK);
			return response;
		}
		
		if(append){
			xquery+=" let $v := string-join((data($g/@var),'"+vars+"'),' ')";
		}else{
			xquery+="let $v := data('"+vars+"')";
		}
		
		xquery+= " return replace value of node $g/@var with $v";
		BaseX.write(xquery);
		
		Representation response = new StringRepresentation("Successfully changed vars in "+id, MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}	
}