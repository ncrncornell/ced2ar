package edu.ncrn.cornell.ced2ar.api;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import edu.ncrn.cornell.ced2ar.api.endpoints.*;

/**
 *Intercepts request to /rest/* URL's and forwards them to the appropriate classes
 *@author NCRN Project Team
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Jeremy Williams, Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
 
public class Main extends Application {
	public final static String apiUrl = "/";
	public final static String version = "1.3.0";
		
	/**
	 * Method createInboundRoot.
	 * @return Restlet
	 */
	@Override
	public synchronized Restlet createInboundRoot()  {
		Router router = new Router(getContext());
		router.attach("", Welcome.class);
		router.attach(apiUrl, Welcome.class);
		router.attach(apiUrl + "search", Search.class);
		router.attach(apiUrl + "codebooks", Codebooks.class);
		router.attach(apiUrl + "codebooks/", Codebooks.class);
		router.attach(apiUrl + "codebooks/{codebookId}", Codebook.class);
		router.attach(apiUrl + "codebooks/{codebookId}/", Codebook.class);
		router.attach(apiUrl + "codebooks/{codebookId}/access", Access.class);
		router.attach(apiUrl + "codebooks/{codebookId}/access/", Access.class);
		router.attach(apiUrl + "codebooks/{codebookId}/filedesc", FileDesc.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/filedesc/", FileDesc.class);
		router.attach(apiUrl + "codebooks/{codebookId}/docdesc", DocDesc.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/docdesc/", DocDesc.class);
		router.attach(apiUrl + "codebooks/{codebookId}/haspdf", PdfChecker.class);
		router.attach(apiUrl +" codebooks/{codebookId}/haspdf/", PdfChecker.class);
		router.attach(apiUrl + "codebooks/{codebookId}/release", CodebookRelease.class);
		router.attach(apiUrl +" codebooks/{codebookId}/release/", CodebookRelease.class);
		router.attach(apiUrl + "codebooks/{codebookId}/studydesc", StudyDesc.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/studydesc/", StudyDesc.class);
		router.attach(apiUrl + "codebooks/{codebookId}/titlepage", TitlePage.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/titlepage/", TitlePage.class);
		router.attach(apiUrl + "codebooks/{codebookId}/variables", Vars.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/variables/", Vars.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/versions", Versions.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/versions/", Versions.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableName}", Var.class);
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableName}/", Var.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableName}/access", AccessVar.class);
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableName}/access/", AccessVar.class);
		router.attach(apiUrl + "codebooks/{codebookId}/vargroups", VarGrps.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/vargroups/", VarGrps.class);
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableName}/versions", VarVersions.class);
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableName}/versions/", VarVersions.class);
		router.attach(apiUrl + "codebooks/{codebookId}/vargroups/{varGrpID}", VarGrp.class);
		router.attach(apiUrl + "codebooks/{codebookId}/vargroups/{varGrpID}/", VarGrp.class);
		router.attach(apiUrl + "codebooks/{codebookId}/vargroups/{varGrpID}/vars", VarGrpVars.class);
		router.attach(apiUrl + "codebooks/{codebookId}/vargroups/{varGrpID}/vars/", VarGrpVars.class);
		
		router.attach(apiUrl + "schemas/{name}", Schema.class);
		router.attach(apiUrl + "schemas/{name}/", Schema.class);
		router.attach(apiUrl + "schemas/{name}/doc/{type}", SchemaDocType.class);
		router.attach(apiUrl + "schemas/{name}/doc/{type}/", SchemaDocType.class);
		
		router.attach(apiUrl + "prov", Prov.class);
		router.attach(apiUrl + "prov/", Prov.class);
		
		return router;
	}
}