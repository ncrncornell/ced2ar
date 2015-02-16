package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
 * Edits a prov node
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class ProvNode extends ServerResource{
	
	@Post
	public Representation edit(Representation entity) {	

		String id = ((String) getRequestAttributes().get("id")).toLowerCase();
		String jsonData = BaseX.httpGet("/rest/prov/", "prov.json");
		try{
			String label = null;
			String nodeType = "";
			String uri = "";
			String date = "";
			String newNode = "false";
			if(entity != null){ 		
				if(MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
					String message = "Bad arguments. Options fields: label, nodeType, newNode, uri";
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
				}
				DiskFileItemFactory factory = new DiskFileItemFactory();
			    factory.setSizeThreshold(10485760);
			    RestletFileUpload upload = new RestletFileUpload(factory); 
			    List<FileItem> files = upload.parseRepresentation(entity);
			    
			    //Parses form into file and 
			    for(FileItem f : files){		    	
			    	if(f.isFormField() && f.getFieldName().equals("label")){
			    		label = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");			    	
			    	}else if(f.isFormField() && f.getFieldName().equals("nodeType")){
			    		nodeType = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");
			    	}else if(f.isFormField() && f.getFieldName().equals("newNode")){
			    		newNode = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");
			    	}else if(f.isFormField() && f.getFieldName().equals("uri")){
			    		uri = f.getString();
			    	}else if(f.isFormField() && f.getFieldName().equals("date")){
			    		date = f.getString();
			    	}else{
			    		String message = "Bad arguments. Options fields: label, nodeType, newNode, uri";
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			    	}	
			    }
			}
			
			if(date.equals("") && newNode.equals("true")){
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				Date dateStamp = new Date();
				String rp = dateFormat.format(dateStamp);
				date = rp+" (imported to CED2AR)";
			}
			
		    if(label == null){
		    	label = id;
		    }
			
			JSONObject json = (JSONObject) new JSONParser().parse(jsonData);
			JSONArray nodes = (JSONArray) json.get("nodes");

			for(int i = 0; i < nodes.size(); i++){
				JSONObject node = (JSONObject) nodes.get(i);
				String current = (String) node.get("id");
				if(current.equals(id)){
					if(newNode.equals("true")){
						String message = "Node already exists";
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
					}else{
						if(label.equals(""))
							label = node.get("label").toString();
						if(uri.equals("")){
							try{
								uri = node.get("uri").toString();
							}catch(NullPointerException e){}
						}
						if(nodeType.equals(""))
							nodeType = node.get("nodeType").toString();
					}
					nodes.remove(node);
				}
			}
			
			
				
			JSONObject node = new JSONObject();
			node.put("id", id);
			node.put("label", label);
			node.put("nodeType", nodeType);
			node.put("uri", uri);
			node.put("date", date);
			
			nodes.add(node);
			json.put("nodes",nodes);
			BaseX.putB("prov.json",json.toJSONString(),"prov/");
				
		}catch(FileUploadException|ParseException e){
			String message = "Error reading prov from database";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
		}
		
		String message = "Updated prov";
		Representation rep = new StringRepresentation(message, MediaType.TEXT_PLAIN);
		return rep;
	}
}