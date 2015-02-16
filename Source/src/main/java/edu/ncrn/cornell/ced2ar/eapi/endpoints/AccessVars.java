package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.util.Arrays;
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

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *An endpoint to edit multiple codebooks at the same time
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class AccessVars extends ServerResource{
	
	/**
	 * Changes multiple variables in a codebook to a specified access level
	 * @return the message
	 */
	@Post
	public Representation access(Representation entity){
		try{
			if( MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
				String message = "Invalid argument type. Must be multipart form.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}
		}catch(NullPointerException e){
			String message = "Error, requires form arguments";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		String baseHandle = ((String) getRequestAttributes().get("baseHandle")).toLowerCase();		    
		String version = ((String) getRequestAttributes().get("version")).replaceAll("[^A-Za-z0-9\\- ]", "").toLowerCase();
		String handle = baseHandle+version;
		
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
		String all = "false";	
		String access = "";
		String[] vars = null;
		for(FileItem f : files){		    	
	    	if(f.isFormField() && f.getFieldName().equals("access")){
	    		access = f.getString().replaceAll("[^A-Za-z0-9 \\-]", ""); 
	    	}
	    	else if(f.isFormField() && f.getFieldName().equals("vars")){
	    		vars = f.getString().split(" "); 

	    	}else if(f.isFormField() && f.getFieldName().equals("all")){
	    		all = f.getString(); 

	    	}else{
	    		String message = "Bad arguments. Required vars={space seperated var list} and access={level to change to}";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
	    	}	
	    }
		
		//access can be empty
		if(vars == null){
			String message = "Bad arguments. Required vars={space seperated var list} and access={level to change to}";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	
		}
		
		String xquery = "for $c in collection('CED2AR/"+handle+"')"
		+" for $v in $c/codeBook/dataDscr/var";
		
		//If every variable is selected, no need for a where statement
		int limit= 100;
		if(!all.equals("true")){
			if(vars.length > limit){			
				String editStmt = "";
				if(access.equals("")){
					editStmt=" return delete node $v/@access";
				}else{
					editStmt= " return if($v/@access) then"
					+" replace value of node $v/@access with '"+access+"'"
					+" else insert node attribute access {'"+access+"'} into $v";
				}
				
				int splitSize = vars.length/limit;
				int remander = vars.length%limit;
				int i = 0;
				while(i < splitSize){					
					xquery = "for $c in collection('CED2AR/"+handle+"')"
					+" for $v in $c/codeBook/dataDscr/var";
					int start = i > 0 ? (i * limit )-1 : 0;
					String[] subVars = Arrays.copyOfRange(vars,start,((i+1)*limit-1));
					String varStmt = "";
					for(String var : subVars){
						if(!varStmt.equals("")){
							varStmt+="|| ";
						}
						varStmt += "$v[@name = '"+var+"'] ";
					}
					xquery+=" where "+varStmt+editStmt;
					
					BaseX.write(xquery);
					i++;
				}
				if(remander > 0){
					xquery = "for $c in collection('CED2AR/"+handle+"')"
					+" for $v in $c/codeBook/dataDscr/var";
					
					String[] subVars = Arrays.copyOfRange(vars,(i*limit)-1,vars.length);
					String varStmt = "";
					for(String var : subVars){
						if(!varStmt.equals("")){
							varStmt+="|| ";
						}
						varStmt += "$v[@name = '"+var+"'] ";
					}
					xquery+=" where "+varStmt+editStmt;
					BaseX.write(xquery);
				}
				
				String response = "Sucessfully updated access levels for "+vars;
				Representation home = new StringRepresentation(response, MediaType.TEXT_PLAIN);
				return home;
			}else{
				String varStmt = "";
				for(String var : vars){
					if(!varStmt.equals("")){
						varStmt+="|| ";
					}
					varStmt += "$v[@name = '"+var+"'] ";
				}
				xquery+=" where "+varStmt;
			}
		}
		
		//If access is empty, remove attribute
		if(access.equals("")){
			xquery+=" return delete node $v/@access";
		}else{
			xquery+= " return if($v/@access) then"
			+" replace value of node $v/@access with '"+access+"'"
			+" else insert node attribute access {'"+access+"'} into $v";
		}

		BaseX.write(xquery);
		String response = "Sucessfully updated access levels for "+vars;
		Representation home = new StringRepresentation(response, MediaType.TEXT_PLAIN);
		return home;
	}
}