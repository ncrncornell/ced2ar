package edu.ncrn.cornell.ced2ar.api.endpoints;


import javax.servlet.ServletContext;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.ProvGenerator;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;

/** 
 * For the {baseUrl}/codebooks/{codebookId} endpoint
 * Retrieves specific codebook in full
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Jeremy Williams
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Codebook extends ServerResource {
	
	@Autowired
	private ServletContext context;
	
	/**
	 * Retrieves the XML content of an entire codebook
	 * @param variant Variant specifies what media type to retrieve data in
	 * @return the representation of the data in either XML or Json
	 */
	@Get("xml|json")
	public Representation represent(Variant variant) {
		ProvGenerator provGenerator = null;
		String codebookId = (String) getRequestAttributes().get("codebookId");
		if (codebookId == null || codebookId.length() == 0) {
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
			
		String type = "";
		
		try{
			type  =  getQuery().getFirstValue("type");	
			if(type == null)
				type = "";
		}catch(NullPointerException e){}
		
		String xquery = "";
		String results = "";
		XMLHandle xh = null;
		switch(type){
			//Formats the namespace in the same order as BaseX.put() does with XMLHandle
			//Not the best solution
			case "git":
				xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return $codeBook";
				xh = new XMLHandle(BaseX.getXML(xquery,false),Config.getInstance().getSchemaURI());
				xh.addNamespace();
				results = xh.docToString();
			break;
			//Includes commit hashes
			case "gitNotes":
				xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return $codeBook";
				xh = new XMLHandle(BaseX.getXML(xquery,false),Config.getInstance().getSchemaURI());
				String commits = QueryUtil.getCommits(codebookId,"full");
				if(!commits.equals("")){
					//Just latest commit
					String remoteRepo = Config.getInstance().getRemoteRepoURL();
					String[] commitData = commits.split(" ")[0].split("\\.");
					String xpath = "/codeBook/docDscr/citation/verStmt/notes[@elementVersionDate='"+commitData[1]+"']";
					xh.addReplace(xpath,remoteRepo+" "+commitData[0], true, true, false, true);
					
					//All commits for instance
					/*
					for(String commit : commits.split(" ")){	
						String[] commitData = commit.split("\\.");
						String xpath = "/codeBook/docDscr/citation/verStmt/notes[@elementVersionDate='"+commitData[1]+"']";
						xh.addReplace(xpath,remoteRepo+" "+commitData[0], true, true, false, true);
					}*/
				}
				xh.addNamespace();			
				results = xh.docToString();
			break;
			//Does not add namespaces when fetching entire codebook
			case "noNamespaces":
				xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return <codeBook>"
				+" {$codeBook/*} </codeBook>";
				results =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false).replace("xmlns=\"\"", "");
			break;

			case "includeProv":
				xquery = getXQueryForCodebook(codebookId);
				String codeBookXML =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false).replace("xmlns=\"\"", "");
				provGenerator = new ProvGenerator();
		        results = provGenerator.insertProv(getRequest().getHostRef().toString()+"/BaseX73/rest/prov/prov.json",codeBookXML);
			break;
			case "getProv":
				xquery = getXQueryForCodebook(codebookId);
				String cbXML =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false).replace("xmlns=\"\"", "");
				provGenerator = new ProvGenerator();
				//First add prov to the codebook and then extract the prov.  This step will may not be needed if the codebook contains prov
				String codebookWithProv = provGenerator.insertProv(getRequest().getHostRef().toString()+"/BaseX73/rest/prov/prov.json",cbXML);
		        results = provGenerator.getProvJSONFromCodebook(codebookWithProv);
			break;
			default:
				//Added namespace when retrieving entire codebook
				//Note that BaseX reorderes these attributes anyway
				xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return "
				+" <codeBook xmlns=\"ddi:codebook:2_5\"" 
				+" xmlns:dc=\"http://purl.org/dc/terms/\""  
				+" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\""  
				+" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\""  
				+" xmlns:ns0=\"http://purl.org/dc/elements/1.1/\""  
				+" xmlns:saxon=\"http://xml.apache.org/xslt\""  
				+" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\""  
				+" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""  
				+" xsi:schemaLocation=\"ddi:codebook:2_5 http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN/schemas/codebook.xsd\">" 		
				+" {$codeBook/*}</codeBook>";
				results =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false).replace("xmlns=\"\"", "");
			break;
		}
		
		Representation codebook = null;
		
		if(MediaType.TEXT_XML.equals(variant.getMediaType()) || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			codebook = new StringRepresentation(results, MediaType.APPLICATION_XML);
			this.setStatus(Status.SUCCESS_OK);
			return codebook;
		}else if (MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
			codebook = new StringRepresentation(Utilities.xmlToJson(results), MediaType.APPLICATION_JSON);
			this.setStatus(Status.SUCCESS_OK);
			return codebook;
		}else{
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
	}
	
	
	private String getXQueryForCodebook(String codebookId) {
		String query = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return "
		+" <codeBook xmlns=\"ddi:codebook:2_5\"" 
		+"  xmlns:dc=\"http://purl.org/dc/terms/\""  
		+"  xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\""  
		+"  xmlns:fn=\"http://www.w3.org/2005/xpath-functions\""  
		+"  xmlns:ns0=\"http://purl.org/dc/elements/1.1/\""  
		+"  xmlns:saxon=\"http://xml.apache.org/xslt\""  
		+"  xmlns:xhtml=\"http://www.w3.org/1999/xhtml\""  
		+"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""  
		+"  xsi:schemaLocation=\"ddi:codebook:2_5 http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN.P/schemas/codebook.xsd\""
		+"  xmlns:prov=\"http://www.w3.org/ns/prov#\""
		+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
		+ " xmlns:ex=\"http://example.com/ns/ex#\" xmlns:tr=\"http://example.com/ns/tr#\""
		+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""
		+ " xmlns:repeca=\"https://ideas.repec.org/e/#\""
		+ " xmlns:exn=\"http://ced2ar.org/ns/external#\""
		+ " xmlns:RePEc=\"https://ideas.repec.org/#\""
		+ " xmlns:ced2ar=\"http://ced2ar.org/ns/core#\""
		+ " xmlns:act=\"http://ced2ar.org/ns/activities#\">"
		+" {$codeBook/*}</codeBook>";
		return query;
	}
}