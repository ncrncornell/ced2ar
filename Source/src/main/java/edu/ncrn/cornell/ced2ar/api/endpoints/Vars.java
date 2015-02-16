package edu.ncrn.cornell.ced2ar.api.endpoints;

import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *For the /codebooks/{codebookid}/variables endpoint
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class Vars extends ServerResource {

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
	 * Method represent. Retrieves the data for all variables in a given codebook
	 * @param variant Variant specifies the format to return the data in
	 * @return Representation the data in the specified format
	 */
	
	@Get("xml|csv|json")
	public Representation represent(Variant variant) {	
		String codebookId = (String) getRequestAttributes().get("codebookId");
		
		if (codebookId == null || codebookId.length() == 0) {
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		codebookId = codebookId.toLowerCase();

		String xquery = "";
		String returnShort = "return $codebook/dataDscr";
		if(MediaType.TEXT_CSV.equals(variant.getMediaType())){
			returnShort = " for $var in $codebook/dataDscr/var"
			+ " let $l:= replace($var/labl/text(),'&lt;|n&gt;|;|,','')"
			+ " let $a:= $var/@access"
			+ " order by data($var/@name)"
			+ " return string-join((data($var/@name),',',$l,',',$a,';&#xa;'),'') ";
		}else if(MediaType.APPLICATION_JSON.equals(variant.getMediaType())){
			returnShort = " let $vars := for $var in $codebook/dataDscr/var"
			+"	let $l:= replace($var/labl/text(),'[^a-zA-Z ]','') "
			+"	let $lA := concat(\"&#34;tokens&#34;:[&#34;\",replace($l,' ', '&#34;,&#34;'),\"&#34;]\") "
			
			+ " order by data($var/@name)"
			+ " return string-join((\"{&#34;name&#34;:&#34;\",data($var/@name),"
			+ "\"&#34;,&#34;label&#34;:&#34;\",$l,\"&#34;"
			+ ",\",$lA,\"}\"),'') "
			+ " return string-join($vars,',')";
		}

		xquery = "let $codebook := collection('CED2AR/"+codebookId+"')/codeBook " + returnShort;
		Representation variableList = null;
		
		String xqueryCount = "let $codebook := collection('CED2AR/"+codebookId+"')/codeBook"
		+ " return count(for $var in $codebook/dataDscr/var return data($var/@name))";
		
		@SuppressWarnings("unchecked")
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
		    responseHeaders = new Series<Header>(Header.class);
		    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		responseHeaders.add(new Header("Count", BaseX.getXML(xqueryCount)));
		
		if(MediaType.TEXT_XML.equals(variant.getMediaType()) || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			variableList = new StringRepresentation("<?xml version='1.0' encoding='UTF-8'?>"+BaseX.getXML(xquery), MediaType.APPLICATION_XML);
			this.setStatus(Status.SUCCESS_OK);
			return variableList;
		}else if(MediaType.TEXT_CSV.equals(variant.getMediaType())){
			variableList = new StringRepresentation(BaseX.getXML(xquery), MediaType.TEXT_CSV);
			this.setStatus(Status.SUCCESS_OK);
			return variableList;
		}else if(MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
			variableList = new StringRepresentation("[" + BaseX.getXML(xquery) + "]", MediaType.APPLICATION_JSON);
			this.setStatus(Status.SUCCESS_OK);
			return variableList;
		}else{
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
	}
}