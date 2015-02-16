package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *Handles adding, deleting or modifying codebooks
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class BaseXDBManager extends ServerResource{
	
	/**
	 * Changes Basex DB
	 * @return Representation success or failure message
	 */
	@Post
	public Representation handleBaseXDBChange() {
		Form form = this.getReference().getQueryAsForm();

		String baseXUri = form.getFirstValue("baseXUri",true);
		String adminCredenials 	= form.getFirstValue("adminCredentials",true);
		String readerCredenials = form.getFirstValue("readerCredentials",true);
		String writerCredenials = form.getFirstValue("writerCredentials",true);


		Representation response  = null;
		StringBuffer sb = new StringBuffer();
		
		boolean readerSuccess = BaseX.validateConnection(baseXUri, readerCredenials);
		
		if(!readerSuccess){
			sb.append("Reader Password");
				
		}

		
		boolean adminSuccess = BaseX.validateConnection(baseXUri, adminCredenials);
		if(!adminSuccess){
			if(StringUtils.isEmpty(sb.toString())){
				sb.append("Admin Password");
			}
			else{
				sb.append(", Admin Password");
			}
		}
		
		boolean writerSuccess = BaseX.validateConnection(baseXUri, writerCredenials);
		if(!writerSuccess){
			if(StringUtils.isEmpty(sb.toString())){
				sb.append("Writer Password");
			}
			else{
				sb.append(", Writer Password");
			}
		}

		if(adminSuccess&&readerSuccess&writerSuccess){
			response = new StringRepresentation("BaseXDB changed successfully", MediaType.TEXT_PLAIN);
			this.setStatus(Status.SUCCESS_OK);
		}
		else{
			response = new StringRepresentation(sb.toString(), MediaType.TEXT_PLAIN);
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		return response;	
	}
	

}