package edu.ncrn.cornell.ced2ar.api.endpoints;

//import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/** For the /codebooks endpoint
 * Retrieves a list off all codebooks
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Codebooks extends ServerResource {

	//private final static Logger logger = Logger.getLogger(Codebooks.class.getName());
	
    /**
     * Method doInit.
    
     * @throws ResourceException */
    @Override
    public void doInit() throws ResourceException {
        setNegotiated(true);
    } 
	
	/**
	 * Method represent. Retrieves a list of all codebooks
	 * @param variant Variant specifies the type that the data will be returned in
	 * @return Representation the data representation in either Json or XML*/
	@Get("xml|txt|json|csv")
	public Representation represent(Variant variant) {
	
		String nameType = "";
		try{
			@SuppressWarnings("rawtypes")
			Series requestHeaders = (Series) getRequest().getAttributes().get("org.restlet.http.headers");
			String partial = requestHeaders.getFirstValue("id-type");
			nameType = partial.toLowerCase().trim();
		}catch(NullPointerException e){}
	
		String xquery = "";		
		Representation codebookList = null;
		if(MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
			xquery = "let $cbs:= for $c in collection('index')/codeBooks/codeBook"
			+" for $v in $c/version"
			+" let $h := string-join(($c/@handle,$v/@v),'')"
			+" return string-join(('{&#34;name&#34;:&#34;',$h,'&#34;}'),'')"
			+" return string-join($cbs,',')";			
			codebookList = new StringRepresentation("[" + BaseX.getXML(xquery) + "]", MediaType.APPLICATION_JSON);
			this.setStatus(Status.SUCCESS_OK);
			return codebookList;
		}else{
				switch (nameType){
				//TODO:Re-evaluated the way codebook information is cached and sent
				//Returns the entire index
				case "index":				
					xquery = "for $c in collection('index') return $c";
				break;
				//Retrieves access levels
				case "access":
					xquery ="for $c in collection('index')/codeBooks/codeBook"
					+" for $v in $c/version"
					+" let $h := string-join(($c/@handle,$v/@v),'')"
					+" let $a := for $da in collection(string-join(('CED2AR/',$h,'')))/codeBook/stdyDscr/dataAccs"
					+" where $da/@ID != '' return  data($da/@ID)"
					+" let $accs := string-join($a,',')"
					+" order by $h"
					+" return string-join(($h,',',$accs,';&#xa;'),'')";			
				break;
				//Retrieves the handle, basehandle, version, shortName and fullName
				case "versions":
					xquery = "for $c in collection('index')/codeBooks/codeBook"
					+" for $v in $c/version"
					+" let $hA := string-join(($c/@handle,$v/@v,$c/@label),',')"
					+" order by $c/@handle, $v/@v"
					+" return string-join(($hA,',',$v/@use,',',$v/fullName,';&#xa;'),'')";								
				break;	
				case "fn":
					xquery = "for $c in collection('index')/codeBooks/codeBook"
					+" for $v in $c/version"
					+" let $h := string-join(($c/@handle,$v/@v),'')"
					+" order by $h"
					+" return $h";		
				break;
				//Retrieves the fullName
				default:
					xquery = "for $codebook in collection('index')/codeBooks/codeBook/version"
					+" order by $codebook/fullName "
					+" return data($codebook/fullName) ";
				break;
			}			
			codebookList = new StringRepresentation(BaseX.getXML(xquery), MediaType.TEXT_PLAIN);
			this.setStatus(Status.SUCCESS_OK);
			return codebookList;
		}	
	}
}