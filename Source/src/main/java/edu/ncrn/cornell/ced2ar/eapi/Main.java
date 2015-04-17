package edu.ncrn.cornell.ced2ar.eapi;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import edu.ncrn.cornell.ced2ar.eapi.endpoints.AccessVars;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.BaseXDBManager;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.BaseXPasswordManager;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.Bugreport;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.Codebook;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.CodebookSettings; 
import edu.ncrn.cornell.ced2ar.eapi.endpoints.EditCover;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.EditCoverMulti;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.EditVar;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.EditVarMulti;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.ProvEdge;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.ProvNode;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.VarGrp;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.VarGrpVars;
import edu.ncrn.cornell.ced2ar.eapi.endpoints.Welcome;

/**
 *Class to handle the page routing
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Main extends Application {
        
	public final static String version = "1.0.0";
		
	@Override
	public synchronized Restlet createInboundRoot(){
	    Router mainRouter = new Router(this.getContext());
	    mainRouter.attach("", Welcome.class);
	    mainRouter.attach("/", Welcome.class);
	    mainRouter.attach("/bugreport", Bugreport.class);
	    mainRouter.attach("/bugreport/", Bugreport.class);	
	    mainRouter.attach("/codebooks/{baseHandle}/{version}", Codebook.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/", Codebook.class);    
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/accessvars", AccessVars.class);    
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/accessvars/", AccessVars.class);    
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/edit", EditCover.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/edit/", EditCover.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/editMulti", EditCoverMulti.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/editMulti/", EditCoverMulti.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/settings", CodebookSettings.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/settings/", CodebookSettings.class);    
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/vars/{var}/edit", EditVar.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/vars/{var}/edit/", EditVar.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/vars/{var}/editMulti", EditVarMulti.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/vars/{var}/editMulti/", EditVarMulti.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/vargrp/{id}", VarGrp.class);
	    mainRouter.attach("codebooks/{baseHandle}/{version}/vargrp/{id}/", VarGrp.class);
	    mainRouter.attach("/codebooks/{baseHandle}/{version}/vargrp/{id}/vars", VarGrpVars.class);
	    mainRouter.attach("codebooks/{baseHandle}/{version}/vargrp/{id}/vars/", VarGrpVars.class);

	    mainRouter.attach("/changebasexpassword", BaseXPasswordManager.class);
	    mainRouter.attach("/updatebasexdb", BaseXDBManager.class);
	    
	    mainRouter.attach("/prov/nodes/{id}",ProvNode.class);
	    mainRouter.attach("/prov/nodes/{id}/",ProvNode.class);
	    mainRouter.attach("/prov/edges/{id}",ProvEdge.class);
	    mainRouter.attach("/prov/edges/{id}/",ProvEdge.class);
	    
	    return mainRouter;
	}
}