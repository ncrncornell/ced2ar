package edu.ncrn.cornell.ced2ar.api.endpoints;

import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.representation.Variant;
import org.restlet.util.Series;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *For the /codebooks/{codebookId}/vargroups/{varGroupID} endpoint
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class VarGrp extends ServerResource {

	private final static Logger logger = Logger.getLogger(Vars.class.getName());

    /**
     * Method doInit.
     * @throws ResourceException
     */
    @Override
    public void doInit() throws ResourceException {
        setNegotiated(true);
    } 
	
	/**
	 * Method represent. Retrieves the data for a particular variable group within a codebook
	 * @param variant Variant specifies the format to return the data in
	 * @return Representation the retrieved data in the specified format
	 */
	@Get("xml|json")
	public Representation represent(Variant variant) {	

		String codebookId = ((String) getRequestAttributes().get("codebookId")).toLowerCase();
		String varGrpID = (String) getRequestAttributes().get("varGrpID");
		
		if (codebookId == null || codebookId.length() == 0) {
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		if (varGrpID == null || varGrpID.length() == 0) {
			String message = " \"" + varGrpID + "\" is an invalid codebookId";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		//Determines if http header 'partial-text' is set to true
		boolean isPartial = false;
		try{
			@SuppressWarnings("rawtypes")
			Series requestHeaders = (Series) getRequest().getAttributes().get("org.restlet.http.headers");
			String partial = requestHeaders.getFirstValue("partial-text");
			isPartial = partial.toLowerCase().equals("true") ? true : false;
		}catch(NullPointerException e){}
		
		String returnShort = "return $group";

		String xquery = "let $codeBook:=  collection('CED2AR/"+codebookId+"')/codeBook"+
		" return for $group in $codeBook/dataDscr/varGrp "+
		" where $group/@ID = '"+varGrpID+"' "+ returnShort;
		
		 if(!isPartial){
			 xquery = "let $codeBook:=  collection('CED2AR/"+codebookId+"')/codeBook "+
						"let $ids := for $group in $codeBook/dataDscr/varGrp "+
						"where $group/@ID = '"+varGrpID+"' "+
						"return tokenize(data($group/@var),' ') "+
						"let $group := for $group in $codeBook/dataDscr/varGrp "+
						"where $group/@ID = '"+varGrpID+"' return $group return "+
						"<codeBook handle='"+codebookId+"'>{$codeBook/docDscr/citation/titlStmt/titl}<dataDscr>{$group} "+
						"{for $id in $ids  for $var in $codeBook/dataDscr/var "+
						"where $var/@ID = $id "+
						"order by $var/@name return $var} </dataDscr></codeBook> ";
		 }
		
		Representation variableList = null;
		
		if(MediaType.TEXT_XML.equals(variant.getMediaType()) || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			variableList = new StringRepresentation("<?xml version='1.0' encoding='UTF-8'?>"+BaseX.getXML(xquery), MediaType.APPLICATION_XML);
			this.setStatus(Status.SUCCESS_OK);
			return variableList;
		}else if(MediaType.APPLICATION_JSON.equals(variant.getMediaType())){	
			variableList = new StringRepresentation(Utilities.xmlToJson(BaseX.getXML(xquery)), MediaType.APPLICATION_JSON);
			this.setStatus(Status.SUCCESS_OK);
			return variableList;
		}else{
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
	}
}