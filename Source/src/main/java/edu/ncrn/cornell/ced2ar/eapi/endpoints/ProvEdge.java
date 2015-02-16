package edu.ncrn.cornell.ced2ar.eapi.endpoints;

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
public class ProvEdge extends ServerResource{
	
	@Post
	public Representation edit(Representation entity) {	

		try{
			if(entity == null){ 	
				String message = "Form required. Required fields \"source\" and \"target\" ";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}

			if(MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), false)){
				String message = "Bad arguments. Required fields \"source\" and \"target\" ";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
			}
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
		    factory.setSizeThreshold(10485760);
		    RestletFileUpload upload = new RestletFileUpload(factory); 
		    List<FileItem> files = upload.parseRepresentation(entity);
		    
		    String source = null;
		    String target = null;
		    String uniqueEdge = "";
		    String delete = "";
		    String edgeType = "";
		    String id = ((String) getRequestAttributes().get("id")).toLowerCase();
		    
		    //Parses form into fields
		    for(FileItem f : files){		    	
		    	if(f.isFormField() && f.getFieldName().equals("source")){
		    		source = f.getString().replaceAll("[^A-Za-z0-9 \\-\\.]", "");
		    	}else if(f.isFormField() && f.getFieldName().equals("target")){
		    		target = f.getString().replaceAll("[^A-Za-z0-9 \\-\\.]", "");
		    	}else if(f.isFormField() && f.getFieldName().equals("edgeType")){
		    		edgeType = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");
		    	}else if(f.isFormField() && f.getFieldName().equals("uniqueEdge")){
		    		uniqueEdge = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");
		    	}else if(f.isFormField() && f.getFieldName().equals("delete")){
		    		delete = f.getString().replaceAll("[^A-Za-z0-9 \\-]", "");
		    	}else{
		    		String message = "Bad arguments. Required fields \"source\" and \"target\" ";
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		    	}	
		    }

		    String jsonData = BaseX.httpGet("/rest/prov/", "prov.json");
		    
		    if(source == null || target == null){
		    	String message = "Bad arguments. Required fields \"source\" and \"target\" ";
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	    
		    }
			
			JSONObject json = (JSONObject) new JSONParser().parse(jsonData);
			JSONArray edges = (JSONArray) json.get("edges");
			if(uniqueEdge.equals("true") || delete.equals("true")){	
				for(int i = 0; i < edges.size(); i++){
					JSONObject edge = (JSONObject) edges.get(i);
					String curSource = (String) edge.get("source");
					String curTarget = (String) edge.get("target");
					String curType = (String) edge.get("edgeType");
					if(curSource.equals(source) && curTarget.equals(target) && curType.equals(edgeType)){
						if(delete.equals("true")){
							id = (String) edge.get("id");
							break;
						}else{
							String message = "Edge already exists";
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);	 
						}
					}
				}	
			}
			
		    if(id.equals("-1")){
		    	int cur = Integer.parseInt(((JSONObject) edges.get(edges.size()-1)).get("id").toString());
				id = Integer.toString(++cur);
			}else{
				for(int i = 0; i < edges.size(); i++){
					JSONObject edge = (JSONObject) edges.get(i);
					String current = (String) edge.get("id");
					if(current.equals(id)){
						edges.remove(edge);
						break;
					}
				}	
			}
		    

			if(!delete.equals("true")){
				JSONObject edge = new JSONObject();
				edge.put("id",id);
				edge.put("source", source);
				edge.put("target", target);
				edge.put("edgeType", edgeType);
				
				//constants
				String type = "arrow";
				if(source.equals(target)){
					type = "curvedArrow";
				}
			
				edge.put("size", 1);
				edge.put("color", "rgba(204,204,204,0.5)");
				edge.put("weight", 1);
				edge.put("type", type);
				edges.add(edge);
			}
			json.put("edges",edges);
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