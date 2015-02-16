package edu.ncrn.cornell.ced2ar.api.endpoints;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.representation.Variant;
import org.restlet.util.Series;

/**
 *For the /codebooks/{codebookId}/vargroups endpoint
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class VarGrps extends CodebookConstructor {
	/**
	 * Method represent. Retrieves all of the variable groups within a codebook
	 * @param variant Variant specifies the format to return the data in
	 * @return Representation the retrieved data in the specified format
	 */
	@Get("xml|json")
	public Representation represent(Variant variant){
		
		//Determines if http header 'partial-text' is set to true
		boolean isPartial = false;
		try{
			@SuppressWarnings("rawtypes")
			Series requestHeaders = (Series) getRequest().getAttributes().get("org.restlet.http.headers");
			String partial = requestHeaders.getFirstValue("partial-text");
			isPartial = partial.toLowerCase().equals("true") ? true : false;
		}catch(NullPointerException e){}
		
		super.codebookId = ((String) getRequestAttributes().get("codebookId")).toLowerCase();
		
		if(isPartial){
			super.xquery = "let $titl := collection('CED2AR/"+codebookId+"')/codeBook/docDscr/citation/titlStmt/titl "+
			"let $grps := for $varGrp in collection('CED2AR/"+codebookId+"')/codeBook/dataDscr/varGrp "+
			" order by $varGrp/@name return <varGrp name='{$varGrp/@name}' ID='{$varGrp/@ID}'/> "+
		    " return <codeBook handle='"+codebookId+"'>{$titl} {$grps}</codeBook>";		
		}else{
			super.xquery = "for $codeBook in collection('CED2AR/"+codebookId+"')/codeBook "
			+ " return <codeBook> { $codeBook/docDscr }<dataDscr> { "
			+" $codeBook/dataDscr/varGrp } </dataDscr></codeBook>";
		}
		return super.represent(variant);
	}
}