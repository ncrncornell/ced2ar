package edu.ncrn.cornell.ced2ar.eapi.endpoints;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

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
public class BaseXPasswordManager extends ServerResource{
	
	/**
	 * Adds or replaces a codebook
	 * @param entity the entity containing the codebook and relevant information
	 * @return Representation success or failure message
	 */
	@Post
	public Representation handleChangePassword() {
		Form form = this.getReference().getQueryAsForm();

		String baseXUri = form.getFirstValue("baseXUri",true);
		String oldCredenials = form.getFirstValue("oldCredenials",true);
		String uid = form.getFirstValue("uid",true);
		String newPassword = form.getFirstValue("newPassword",true);
		
		boolean success = BaseX.changePassword(baseXUri, oldCredenials, uid, newPassword);
		Representation response  = null;
		if(success){
			response = new StringRepresentation("Password changed successfully", MediaType.TEXT_PLAIN);
			this.setStatus(Status.SUCCESS_OK);
		}
		else{
			response = new StringRepresentation("Password changed failed", MediaType.TEXT_PLAIN);
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		return response;	

		
	}
	

}