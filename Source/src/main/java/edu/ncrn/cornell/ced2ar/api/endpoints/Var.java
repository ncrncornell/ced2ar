package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 *For the /codebooks/{codebookid}/variables/{variableName} endpoint * 
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class Var extends ServerResource{

    /**
     * Method doInit.
     * @throws ResourceException
     */
    @Override
    public void doInit() throws ResourceException {
        setNegotiated(true);
    } 

	/**
	 * Method represent. Retrieves the data for one variable within a codebook
	 * @param variant Variant specifies what format to return the data in
	 * @return Representation the data returned in the specified format
	 */
	@Get("xml|csv|json")
	public Representation represent(Variant variant) {	
		String codebookId = (String) getRequestAttributes().get("codebookId");

		if (codebookId == null || codebookId.length() == 0) {
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		codebookId = codebookId.toLowerCase();
		
		String variableName = (String) getRequestAttributes().get("variableName");

		if (variableName == null || variableName.length() == 0) {
			String message = " \"" + variableName + "\" is an invalid variableName";
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
		
		//Content Negotiation 
		String xquery = " let $codebook := collection('CED2AR/"+codebookId+"')/codeBook ";

		if(MediaType.TEXT_CSV.equals(variant.getMediaType())){
			xquery += "return " + 
			"for $var in $codebook/dataDscr/var " +
			"where $var[lower-case(@name) = '" + variableName.toLowerCase() + "'] "+
			"return string-join((data($var/@name),','," +
			"replace($var/labl/text(),',|;',''),';&#xa;'),'') ";
		}else if(!(MediaType.TEXT_CSV.equals(variant.getMediaType())) && isPartial){
			xquery += "return " + 
			"let $v:= for $var in $codebook/dataDscr/var " +
			"where $var[lower-case(@name) = '" + variableName.toLowerCase() + "'] "+
			"return $var "+
			"let $g := for $group in $codebook/dataDscr/varGrp "+
			"for $id in tokenize(data($group/@var),' ') "+
			"where $id =data($v/@ID) "+
			"return <group id='{data($group/@ID)}' name='{data($group/@name)}'/> "+
			"let $f := for $file in $codebook/fileDscr "+
			"for $id in tokenize(data($v/@files),' ') where $id =data($file/@ID) "+
			"return $file "+
			"return  <codeBook  handle='"+codebookId+"' xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"> "+
			"{$codebook/docDscr/citation/titlStmt/titl}<groups>{$g}</groups><files>{$f}</files>{$v}</codeBook>";
		}else{
			xquery += "return " + 
			"<codeBook handle='"+codebookId+"' xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"> { $codebook/docDscr } { " +
			"let $v:= for $var in $codebook/dataDscr/var " +
			"where  $var[lower-case(@name) = '" + variableName.toLowerCase() + "']	"+		
			"return $var "+
			"let $g := for $group in $codebook/dataDscr/varGrp "+
			"for $id in tokenize(data($group/@var),' ') "+
			"where $id =data($v/@ID) "+
			"return <group id='{data($group/@ID)}' name='{data($group/@name)}'/> "+
			"let $f := for $file in $codebook/fileDscr "+
			"for $id in tokenize(data($v/@files),' ') where $id =data($file/@ID) "+
			"return $file "+
			"return <dataDscr><groups>{$g}</groups><files>{$f}</files>{$v}</dataDscr> "+
			"}</codeBook>";
		}
		
		if (MediaType.TEXT_XML.equals(variant.getMediaType()) || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			Representation variable = new StringRepresentation("<?xml version='1.0' encoding='UTF-8'?>"+BaseX.getXML(xquery), MediaType.APPLICATION_XML);
			this.setStatus(Status.SUCCESS_OK);
			return variable;
		}else if(MediaType.TEXT_CSV.equals(variant.getMediaType())){
			Representation variable = new StringRepresentation(BaseX.getXML(xquery), MediaType.TEXT_CSV);
			this.setStatus(Status.SUCCESS_OK);
			return variable;
		}else if(MediaType.APPLICATION_JSON.equals(variant.getMediaType())){
			Representation variable = new StringRepresentation(Utilities.xmlToJson(BaseX.getXML(xquery)), MediaType.APPLICATION_JSON);
			this.setStatus(Status.SUCCESS_OK);
			return variable;
		} else {
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}	
	}
}