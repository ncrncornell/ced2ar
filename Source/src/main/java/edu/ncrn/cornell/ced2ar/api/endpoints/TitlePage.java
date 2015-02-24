package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.representation.Variant;

/** 
 *For the {baseUrl}/codebooks/{codebookId}/titlepage/ endpoint
 *Retrieves a specific document description
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class TitlePage extends CodebookConstructor {
	/**
	 * Method represent. Retrieves the title page for a given codebook
	 * @param variant Variant specifies the format to return the data in
	 * @return Representation the data retrieved in the specified format
	 */
	@Get("xml|json")
	public Representation represent(Variant variant){
		super.codebookId = (String) getRequestAttributes().get("codebookId");
		super.xquery = "for $codebook in collection('CED2AR/"+codebookId+"')/codeBook "
				+ " let $count := count($codebook/dataDscr/var)"
				+" return <codeBook xmlns:xhtml='http://www.w3.org/1999/xhtml' handle='"+codebookId+"' variables='{$count}'>{$codebook/docDscr} {$codebook/stdyDscr} {$codebook/fileDscr}</codeBook>";
		return super.represent(variant);
	}
}