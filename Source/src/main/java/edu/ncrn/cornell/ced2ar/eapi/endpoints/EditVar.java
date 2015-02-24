package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
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
 * Class to handle variable edit Requests
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class EditVar extends ServerResource{
	
	private static final Logger logger = Logger.getLogger(EditVar.class);
	
	/**
	 * Method is called an edits to variables are made. Checks to make sure a valid field is being
	 * edited, then updates the xml, and finally writes to BaseX
	 * @param entity entity holding the information about the edit being made
	 * @return Representation success or failure message
	 */
	@Post
	public Representation editVar(Representation entity) {
		try{
			if( MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
				String message = "Invalid argument type. Must be multipart form.";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}
		}catch(NullPointerException e){
			String message = "Bad arguments. "+
			"Required: field = <field to replace> and value = <new value to use>";
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
	    	"Required: field = <field to replace> and value = <new value to use>";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		String user = "anonymous";
		String value = "";
		String field = "";
		String append = "";
		String delete = "";
		String ip = "";
		boolean doesAppend = false;
		int index = 1;
		int index2 = 1;
		String var = (String) getRequestAttributes().get("var");	
		String baseHandle = (String) getRequestAttributes().get("baseHandle");
	    String version = ((String) getRequestAttributes().get("version")).replaceAll("[^A-Za-z0-9\\- ]", "");
		String handle = baseHandle+version;
		//TODO: gather user information from request to show in the versioning who edited what.
		
		for(FileItem f : files){		
		  	if(f.isFormField()){
			  	switch(f.getFieldName()){
				  	case "value":
				  		value =  QueryUtil.sanatize(f.getString());
				  	break;
				  	case "field":
				  		field =  QueryUtil.sanatize(f.getString());
					break;
				  	case "append":
				  		append = QueryUtil.sanatize(f.getString());
				  		if(append.equals("true")) doesAppend = true;
				  	break;
				  	case "delete":
				  		delete = QueryUtil.sanatize(f.getString());
				  	break;
				  	case "ip":
				  		ip = QueryUtil.sanatize(f.getString());
				  	break;
				  	case "index":
				  		String i = QueryUtil.sanatize(f.getString());
				  		try{
				  			index = Integer.parseInt(i);
				  		}
				  		catch(NumberFormatException e){
				  			String message = "Bad arguments. Index must be an int";
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	
				  		}
				  	break;
				  	case "index2":
				  		String j = QueryUtil.sanatize(f.getString());
				  		try{
				  			index2 = Integer.parseInt(j);
				  		}
				  		catch(NumberFormatException e){
				  			String message = "Bad arguments. Index must be an int";
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	
				  		}
				  	break;
				  	case "user":
				  		user =  f.getString();
				  	break;
					default:
						String message = "Bad arguments. "+
						"Required: field = <field to replace> and value = <new value to use>";
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			  	}
		  	}else{
		  		String message = "Bad arguments. "+
				"Required: field = <field to replace> and value = <new value to use>";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		  	}
	  	}
		
	  	//List of acceptable elements or attributes to edit
		Hashtable<String,String[]> validFields = new Hashtable<String,String[]>();
		
		validFields.put("topAcs", new String[] {"5","/var[@name='"+var+"']/@access","Top Level Access"});
		validFields.put("labl", new String[] {"1","/var[@name='"+var+"']/labl","Label"});
		validFields.put("lablAcs", new String[] {"5","/var[@name='"+var+"']/labl/@access","Label Access"});
		validFields.put("sumStat", new String[] {"5","/var[@name='"+var+"']/sumStat["+index+"]/@access","Summary Statistic Access"});
		validFields.put("valRange", new String[] {"5","/var[@name='"+var+"']/valrng["+index+"]/@access","Value Range Access"});
		validFields.put("range", new String[] {"5","/var[@name='"+var+"']/valrng["+index+"]/range["+index2+"]/@access","Range Access"});
		validFields.put("txt", new String[] {"1","/var[@name='"+var+"']/txt","Full Description"});		
		validFields.put("catgry", new String[] {"1","/var[@name='"+var+"']/catgry["+index+"]","Value Category"});
		validFields.put("catValu", new String[] {"1","/var[@name='"+var+"']/catgry["+index+"]/catValu","Value"});
		validFields.put("val", new String[] {"5","/var[@name='"+var+"']/catgry["+index+"]/@access","Value Access"});
		validFields.put("catLabl", new String[] {"1","/var[@name='"+var+"']/catgry["+index+"]/labl","Value Category Label"});
		validFields.put("catStat", new String[] {"5","/var[@name='"+var+"']/catgry["+index+"]/catStat["+index2+"]/@access","Value Statistic Access"});
		validFields.put("notes", new String[] {"1","/var[@name='"+var+"']/notes["+index+"]","Notes"});
		validFields.put("notesAccs", new String[] {"5","/var[@name='"+var+"']/notes["+index+"]/@access","Note Access"});
		
		validFields.put("qstn", new String[] {"1","/var[@name='"+var+"']/qstn","Question Text"});

		
		if(!ip.equals(""))
			logger.debug("Edit request to var from " + ip);
		
		if(!validFields.containsKey(field)){
			String message = "Bad field";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}

		//String type = validFields.get(field)[0];
		String path = validFields.get(field)[1];
		 	
		//Auto updates version
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		timestamp+= " (auto-generated)";
		
		if(doesAppend || delete.equals("true") || validFields.get(field)[0].equals("5") 
		|| value.contains("<") || value.contains(">")){
			//XMLHandle xh = new XMLHandle(BaseX.get(handle),Config.getSchemaURI());
			XMLHandle xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());
	
			if(doesAppend && field.equals("catLabl")){
				//insert catvalu first
				String xp = "/codeBook/dataDscr" + validFields.get("catValu")[1]; 
				xh.addReplace(xp, Integer.toString(index), true, true, false, true);
			}
	
			if(delete.equals("true")){
				xh.deleteNode("/codeBook/dataDscr"+path, false);
			}else{
				xh.addReplace("/codeBook/dataDscr"+path, value, doesAppend, true, false, true);
			}
			BaseX.put(handle, xh.docToString().replaceAll("&lt;", "<").replaceAll("&gt;", ">"));
		}else{
			//Can save item by making xquery replace statement
			value = value.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			String xquery = "let $path := collection('CED2AR/"+handle+"')/codeBook/dataDscr"+path+
			" return replace value of node $path with '"+value+"'";
			BaseX.write(xquery);			
		}
		
		String xquery2 = "let $path := collection('CED2AR/"+handle+"')"
		+"/codeBook/docDscr/citation/verStmt/version/@date"
		+" return replace value of node $path with '"+timestamp+"'";
		BaseX.write(xquery2);		
		
		if(Config.getInstance().isGitEnabled()){
			QueryUtil.insertPending(handle, "var", "/codeBook/dataDscr"+path,var,user);
		}
					
		Representation response = new StringRepresentation("XML successsfully replaced", MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;	
	}
}