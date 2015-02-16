package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.representation.Variant;

/**
 *For the /codebooks/{codebookId}/filedesc endpoint
 *Retrieves a specific file description
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class FileDesc extends CodebookConstructor {

	/**
	 * Method represent. Retrieves file descriptor for a codebook
	 * @param variant Variant specifies the type that the data will be returned in
	 * @return Representation the data representation in either Json or XML
	 */
	@Get("xml|json")
	public Representation represent(Variant variant){
		super.codebookId = (String) getRequestAttributes().get("codebookId");
		super.xquery = "for $ced2ar in collection('CED2AR/"+codebookId+"')/codeBook return $ced2ar/fileDscr";
		return super.represent(variant);
	}
}