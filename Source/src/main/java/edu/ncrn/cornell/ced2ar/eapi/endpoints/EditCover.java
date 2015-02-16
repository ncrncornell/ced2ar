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
 * Class to handle codebook edit requests
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class EditCover extends ServerResource{
	
	private static final Logger logger = Logger.getLogger(EditCover.class);
	
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
		String user = "anonymous";
		String value = "";
		String field = "";
		String append = "";
		String baseHandle = (String) getRequestAttributes().get("baseHandle");
	    String version = ((String) getRequestAttributes().get("version")).replaceAll("[^A-Za-z0-9\\- ]", "");
		String handle = baseHandle+version;
		
		int index = 0;
		boolean doesAppend = false;
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
				  	case "index":
				  		String i =  QueryUtil.sanatize(f.getString());
				  		try{
				  			index = Integer.parseInt(i);
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
						"Required: field = <field to replace> "+
				    	"and value = <new value to use>";
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			  	}
		  	}else{
		  		String message = "Bad arguments. "+
				"Required: field = <field to replace> "+
		    	"and value = <new value to use>";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		  	}
	  	}
	  
	  	//List of acceptable elements or attributes to edit
		Hashtable<String,String[]> validFields = new Hashtable<String,String[]>();
		
		validFields.put("version",
			new String[] {"1","/docDscr/citation/prodStmt/prodDate","Version"});
		validFields.put("docProducer",
			new String[] {"1","/docDscr/citation/prodStmt/producer","Document Producer"});
		validFields.put("stdyProducer",
			new String[] {"3","/stdyDscr/citation/prodStmt/producer["+index+"]","Study Producer"});
		validFields.put("distrbtr",
			new String[] {"3","/stdyDscr/citation/distStmt/distrbtr["+index+"]","Distributor"});
		validFields.put("distrbtrURL",
			new String[] {"4","/stdyDscr/citation/distStmt/distrbtr["+index+"]/@URI","Distributor URL"});
		validFields.put("docCit",
			new String[] {"1","/docDscr/citation/biblCit","Document Citation"});
		validFields.put("docCitURL",
			new String[] {"1","/docDscr/citation/biblCit/ExtLink","Document Citation URL"});
		validFields.put("stdyCit",
			new String[] {"1","/stdyDscr/citation/biblCit","Study Citation"});
		validFields.put("stdyCitURL",
			new String[] {"1","/stdyDscr/citation/biblCit/ExtLink","Study Citation URL"});
		validFields.put("abstract",
			new String[] {"1","/stdyDscr/stdyInfo/abstract","Abstract"});
		validFields.put("confDec",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/confDec","Access Requirements"});
		validFields.put("confDecURL",
			new String[] {"2","/stdyDscr/dataAccs[1]/useStmt/confDec/@URI","Access Requirements URL"});
		validFields.put("accessRstr",
			new String[] {"3","/stdyDscr/dataAccs["+index+"]/useStmt/restrctn","Access Restrictons"});
		validFields.put("accessPermReq",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/specPerm","Access Permission Requirement"});
		validFields.put("citReq",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/citReq","Citation Requirements"});		
		validFields.put("disclaimer",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/disclaimer","Disclamer"});
		validFields.put("contact",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/contact","Contact"});
		validFields.put("method",
			new String[] {"1","/stdyDscr/method/dataColl/collMode","Methodology"});		
		validFields.put("sources",
			new String[] {"3","/stdyDscr/method/dataColl/sources/dataSrc["+index+"]","Sources"});		
		validFields.put("relMat",
			new String[] {"3","/stdyDscr/othrStdyMat/relMat["+index+"]","Related Material"});
		validFields.put("relPubl",
			new String[] {"3","/stdyDscr/othrStdyMat/relPubl["+index+"]","Related Publications"});
		validFields.put("relStdy",
			new String[] {"3","/stdyDscr/othrStdyMat/relStdy["+index+"]","Related Studies"});				
		validFields.put("docSrcBib",
			new String[] {"1","/docDscr/docSrc/biblCit","Document Source Citation"});
		validFields.put("accessCond",
            new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/conditions","Access Conditions"});
		
		//New fields to get
		validFields.put("titl",
	        new String[] {"1","/docDscr/citation/titlStmt/titl","Title"});
		validFields.put("accessRstrID",
			new String[] {"4","/stdyDscr/dataAccs["+index+"]/@ID","Data Access Level ID"});
		
		//!Arrays.asList(validFields).contains(field)
		if(!validFields.containsKey(field)){
			String message = "Bad field given";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		}
		
		//Access Level ID must be lowercase alphanumeric
		if(field.equals("accessRstrID")){
			value = value.toLowerCase().replaceAll("[^a-z0-9]", "");
		}
		
		String type = validFields.get(field)[0];
		String path = validFields.get(field)[1];
		boolean replaceChildren = true;
		
		if(type.equals("3") || type.equals("4")){
			if(index <= 0)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Index required for this field. (Starting from 1)");	    		
		}
		
		//Auto updates version
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		timestamp+= " (auto-generated)";
	
		if(doesAppend || value.contains("<") || value.contains(">")){
			XMLHandle xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());
			xh.addReplace("/codeBook"+path, value, doesAppend, true, false, replaceChildren);
			BaseX.put(handle, xh.docToString().replaceAll("&lt;", "<").replaceAll("&gt;", ">"));
		}else{
			//Can save item by making xquery replace statement
			value = value.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			String xquery = "let $path := collection('CED2AR/"+handle+"')/codeBook"+path+
			" return replace value of node $path with '"+value+"'";
			BaseX.write(xquery);
		}
		
		String xquery2 = "let $path := collection('CED2AR/"+handle+"')"
		+"/codeBook/docDscr/citation/verStmt/version/@date"
		+" return replace value of node $path with '"+timestamp+"'";
		BaseX.write(xquery2);		
		
		if(Config.getInstance().isGitEnabled()){
			QueryUtil.insertPending(handle, "cover", "/codeBook"+path,user);
		}
		
		//Update fullname
		if(field.equals("titl")){
			QueryUtil.updateFullName(baseHandle, version, value);
		}
		
		Representation response = new StringRepresentation("XML successsfully replaced", MediaType.TEXT_PLAIN);
		this.setStatus(Status.SUCCESS_OK);
		return response;
	}	
}