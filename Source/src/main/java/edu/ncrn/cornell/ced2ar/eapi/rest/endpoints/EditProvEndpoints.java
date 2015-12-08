package edu.ncrn.cornell.ced2ar.eapi.rest.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ncrn.cornell.ced2ar.eapi.rest.queries.EditProvData;

/**
 * Provides RESTful endpoints for /codebooks. Calls CodebookData class.
 * 	
 * @author Cornell University, Copyright 2012-2015
 * @author Ben Perry
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */ 

@RestController
public class EditProvEndpoints {
	
	private final static Logger logger = Logger.getLogger(EditProvEndpoints.class.getName());
	private final static String API_PREFIX = "/erest";//TODO: put somewhere else

//Endpoints 
//TODO: Document endpoint methods
	
	@RequestMapping(value = API_PREFIX + "/prov/nodes/{id}", method = RequestMethod.POST)
	public String editNode(HttpServletRequest request, HttpServletResponse response, 
	@PathVariable("id") String id,
	@RequestParam(value="nodeType") String nodeType,
	@RequestParam(value="newNode", defaultValue="0") boolean newNode,
	@RequestParam(value="uri", defaultValue="") String uri,
	@RequestParam(value="label", defaultValue="") String label,
	@RequestParam(value="date", defaultValue="") String date){
		EditProvData editProvData = new EditProvData();
		int responseCode = editProvData.editNode(id, label, nodeType, uri, date, newNode);
		response.setStatus(responseCode);
		if(responseCode != 200){
			//TODO:should be a warning for some responses
			//logger.error(editProvData.getError());
			return "Error editing prov node " + id + " " + editProvData.getError();
		}
		return "Succesful prov node update " + id;
	}
	
	@RequestMapping(value = API_PREFIX + "/prov/nodes/{id}", method = RequestMethod.DELETE)
	public String deleteNode(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id){
		EditProvData editProvData = new EditProvData();
		int responseCode = editProvData.deleteNode(id);
		response.setStatus(responseCode);
		if(responseCode != 200){ 
			logger.error(editProvData.getError());
			return "Error deleting prov node " + id + " " + editProvData.getError();			
		};
		return "Deleted prov node " + id;
	}

	@RequestMapping(value = API_PREFIX + "/prov/edges/{id}", method = RequestMethod.POST)
	public String editEdge(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id,
	@RequestParam(value="edgeType", required=false) String edgeType, 
	@RequestParam(value="source", required=false) String source,
	@RequestParam(value="target", required=false) String target, 
	@RequestParam(value="uniqueEdge", defaultValue="0") boolean uniqueEdge, 
	@RequestParam(value="delete", defaultValue="0") boolean delete){
		if(!delete && (edgeType == null || source == null || target == null)){
			response.setStatus(400);
			return "edgeType, source, and target parameters must be set if delete paramater if false";
		}
		
		EditProvData editProvData = new EditProvData();
		int responseCode = editProvData.editProvEdge(id, edgeType, source, target, uniqueEdge, delete);
		response.setStatus(responseCode);
		if(responseCode != 200){
			logger.error(editProvData.getError());
			return "Error deleting prov edge " + id + " " + editProvData.getError();
		}
		return "Updated prov edge " + id;
	}
}