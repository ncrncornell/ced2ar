package edu.ncrn.cornell.ced2ar.api.endpoints;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletContext;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
*Checks to see if a pdf exists for a codebook
*  
*@author Cornell University, Copyright 2012-2015
*@author Ben Perry
*
*@author Cornell Institute for Social and Economic Research
*@author Cornell Labor Dynamics Institute
*@author NCRN Project Team 
*/
public class PdfChecker extends ServerResource{
	@Get
	public Representation represent() {
		String doc = (String) getRequestAttributes().get("codebookId");
		String found = "0";		
		getContext();
		ConcurrentMap<String,Object> props = Context.getCurrent().getAttributes();
		ServletContext context  = (ServletContext) props.get("org.restlet.ext.servlet.ServletContext");
		String path = context.getRealPath("/pdf/"+doc+".pdf");
		File f = new File(path);	 

		if(f.exists()){			
			found= "1";		
		}	
		Representation home = new StringRepresentation(found, MediaType.TEXT_HTML);
		return home;
	}
}