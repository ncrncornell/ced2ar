package edu.ncrn.cornell.ced2ar.merge;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Connector;
import edu.ncrn.cornell.ced2ar.api.data.Connector.RequestType;
import edu.ncrn.cornell.ced2ar.api.rest.queries.CodebookData;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle2;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;

/*
 * TODO:store missing, removed, and same vars
 * TODO:compare vars function
 * TODO:switch to turn this module off
 */
public class MergeMultiAction extends MultiAction{

	@Autowired
	ServletContext servletContext;
	
	private static final Logger logger = Logger.getLogger(MergeMultiAction.class);
		
	/**
	 * Fetches list of codebooks from the local instance
	 * @param properties
	 * @param context
	 * @return
	 */
	public Event loadCodebooks(MergeProperties properties, RequestContext context){
		CodebookData codebookData = new CodebookData();
		String[] codebooks = codebookData.getCodebooks("versions2").split(";");
		properties.setCodebooks(codebooks);
		return success();
	}

	/**
	 * Compares variables in remote codebook to current, and generates a list of shared and unique variables
	 * @param properties
	 * @param context
	 * @return
	 */
	public Event compareVars(MergeProperties properties, RequestContext context) {
		String[] codebook = context.getRequestParameters().get("codebook").split("\\.");
		String handle =  codebook[0] + codebook[1];
		properties.setBaseHandle(codebook[0]);
		properties.setVersion(codebook[1]);

		String baseURI = properties.getLocalRepo();
		String remoteURI = properties.getRemoteRepo();
		
		//TODO: we could handle the query to api endpoints like this
		try{
			matchVars(properties, baseURI, remoteURI, handle);	
			diffVars(properties, handle);
		}catch(HttpException e){
			return error();
		}
		
		return success();
	}
	
	/**
	 * Adds variables from remote codebook to local
	 * @param properties
	 * @param context
	 * @return
	 */
	public Event addVars(MergeProperties properties, RequestContext context){
		
		String remoteXML = properties.getRemoteDDI();		
		String localXML = properties.getLocalDDI();	
		
		try {
			XMLHandle2 xhRemote = new XMLHandle2(remoteXML);
			XMLHandle2 xhLocal = new XMLHandle2(localXML);
			
			//Remove variables
			String[] variablesRemove = context.getRequestParameters().getArray("variablesRemove");	
			if(variablesRemove != null && variablesRemove.length > 0){
				xhLocal.removeVars(variablesRemove);
			}
			
			//Add new variables
			String[] variablesAdd = context.getRequestParameters().getArray("variablesAdd");	
			if(variablesAdd != null && variablesAdd.length > 0){
				for(String variable : variablesAdd){	
					String varXML = xhRemote.getVar(variable);
					xhLocal.addVar(varXML);	
				}
			}
			
			if(xhLocal.getHasChanged()){
				BaseX.put(properties.getHandle(), xhLocal.toString());
			}
		
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return success();
	}
	
	public Event doMerge(MergeProperties properties, RequestContext context){
		try{
			String var = properties.popDiffVars();
			
			//TODO: I would like to get this from the cached XML		
			//String localXML = properties.getLocalDDI();
			//String remoteXML = properties.getRemoteDDI();
			
			String handle = properties.getHandle();
			String apiURILocal = properties.getLocalRepo()+"/rest/codebooks/"+ handle + "/variables/"+var;
			String apiURIRemote = properties.getRemoteRepo()+"/rest/codebooks/"+ handle + "/variables/"+var;

			Connector connLocal = new Connector(apiURILocal);
			connLocal.buildRequest(RequestType.GET);
			connLocal.setHeader("partial-text", "true");
			String localRep = connLocal.execute();
			int responseLocal = connLocal.getResponseCode();
			
			Connector connRemote = new Connector(apiURIRemote);
			connRemote.buildRequest(RequestType.GET);
			connRemote.setHeader("partial-text", "true");
			String remoteRep = connRemote.execute();
			int responseRemote = connRemote.getResponseCode();
			
			String path = servletContext.getRealPath("/xsl/variableMerge.xsl");
			
			if(responseRemote >= 400 || responseLocal >= 400){
				//TODO: this goes uncaught
				return error();
			}
				
			Parser xp = new Parser(localRep, path, 0);
			properties.setLocalSnippet(xp.getData());		
			
			Parser xp2 = new Parser(remoteRep, path, 0);
			properties.setRemoteSnippet(xp2.getData());
			
		}catch(NoSuchElementException e){
			//LinkedList is empty
			return no();
		}
		return yes();
	}	
	
	public Event mergeTitlepage(MergeProperties properties, RequestContext context){
		String handle = properties.getHandle();
		String apiURILocal = properties.getLocalRepo()+"/rest/codebooks/"+handle+"/titlepage";
		String apiURIRemote = properties.getRemoteRepo()+"/rest/codebooks/"+handle+"/titlepage";

		Connector connLocal = new Connector(apiURILocal);
		connLocal.buildRequest(RequestType.GET);
		String localRep = connLocal.execute();
		int responseLocal = connLocal.getResponseCode();
		
		Connector connRemote = new Connector(apiURIRemote);
		connRemote.buildRequest(RequestType.GET);
		String remoteRep = connRemote.execute();
		int responseRemote = connRemote.getResponseCode();
		
		String path = servletContext.getRealPath("/xsl/titlepageMerge.xsl");
		
		if(responseRemote >= 400 || responseLocal >= 400){
			//TODO: this goes uncaught
			return error();
		}
		
		Parser xp = new Parser(localRep, path, 0);
		properties.setLocalSnippet(xp.getData());		
		
		Parser xp2 = new Parser(remoteRep, path, 0);
		properties.setRemoteSnippet(xp2.getData());
		
		return success();
	}
	
	public Event finish(MergeProperties properties, RequestContext context){
		return success();
	}
	
//Private Methods
	private void matchVars(MergeProperties properties, String baseURI,String remoteURI, String handle) throws HttpException{
		
		String apiURILocal = baseURI + "/rest/codebooks/"+ handle+"/variables";
		String apiURIRemote = remoteURI + "/rest/codebooks/"+ handle+"/variables";
		
		//TODO: Needs new API endpoint for format header to work
		Connector connLocal = new Connector(apiURILocal);
		connLocal.buildRequest(RequestType.GET);
		connLocal.setHeader("format", "name");
		String localRep = connLocal.execute();
		int responseLocal = connLocal.getResponseCode();
		
		Connector connRemote = new Connector(apiURIRemote);
		
		connRemote.buildRequest(RequestType.GET);
		connRemote.setHeader("format", "name");
		String remoteRep = connRemote.execute();
		int responseRemote = connRemote.getResponseCode();
		
		/*TODO: Need to update CED2AR instances with fixed API so the /codebooks/{handle}/variables 
		 * endpoint throws a 404 if the codebook doesn't exist*/
		if(responseLocal >= 400 || responseRemote >= 400){
			//Codebook does not exist or there was an error on local or remote
			throw new HttpException();
		}
		
		System.out.println("Matching vars...");
		
		Set<String> localVars = new HashSet<String>(Arrays.asList(localRep.trim().split(","))) ;
		Set<String> remoteVars = new HashSet<String>(Arrays.asList(remoteRep.trim().split(",")));
		
		Set<String> localUnique = new HashSet<String>(localVars);
		Set<String> remoteUnique = new HashSet<String>(remoteVars);
		Set<String> sharedVars = localVars;
		
		sharedVars.retainAll(remoteVars);
		localUnique.removeAll(remoteVars);
		remoteUnique.removeAll(localVars);
		
		properties.setUniqueRemoteVars(remoteUnique);
		properties.setUniqueLocalVars(localUnique);
		properties.setSetSharedVars(sharedVars);
	}
	
	private void diffVars(MergeProperties properties, String handle){
		
		String apiURILocal = properties.getLocalRepo() + "/rest/codebooks/"+ handle+"?type=noNamespaces";
		String apiURIRemote = properties.getRemoteRepo() + "/rest/codebooks/"+ handle+"?type=noNamespaces";
		Set<String> sharedVars = properties.getSharedVars();
		
		//TODO: Needs new API endpoint for format header to work
		Connector connLocal = new Connector(apiURILocal);
		connLocal.buildRequest(RequestType.GET);
		String localRep = connLocal.execute();		
		
		int responseLocal = connLocal.getResponseCode();
		
		Connector connRemote = new Connector(apiURIRemote);
		connRemote.buildRequest(RequestType.GET);
		String remoteRep = connRemote.execute();
		int responseRemote = connRemote.getResponseCode();
		
		if(responseLocal >= 400 || responseRemote >= 400){
			//Codebook does not exist or there was an error on local or remote
			return;
		}
		
		properties.setLocalDDI(localRep);
		properties.setRemoteDDI(remoteRep);
			
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document local = docBuilder.parse(new ByteArrayInputStream(localRep.getBytes()));
			
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			Document remote = docBuilder.parse(new ByteArrayInputStream(remoteRep.getBytes()));
						
			int sameCount = 0;
			int diffCount = 0;
			LinkedList<String> changedVars = new LinkedList<String>();
			
			XPath xpath = XPathFactory.newInstance().newXPath();

			OutputFormat format = new OutputFormat (); 
			format.setPreserveSpace(false);
			format.setPreserveEmptyAttributes(false);
						
			for(String varName : sharedVars){
				varName = varName.trim();//Last var has line break
				Element varLocal = (Element) xpath.evaluate("/codeBook/dataDscr/var[@name='"+varName+"']", local
				, XPathConstants.NODE);
				Element varRemote = (Element) xpath.evaluate("/codeBook/dataDscr/var[@name='"+varName+"']", remote
				, XPathConstants.NODE);
				
				//TODO: Not the most elegant solution and this doesn't take into account attributes changed, 
				//but this shouldn't matter for the CED2AR UI right now
				String varLocalText = varLocal.getTextContent().trim().replaceAll("\\s+", " ");
				String varRemoteText = varRemote.getTextContent().trim().replaceAll("\\s+", " ");

				if(!varLocalText.equals(varRemoteText)){
					System.out.println("*** variable '" + varName + "' has changed");
					changedVars.add(varName);
					diffCount++;
				}else{
					sameCount++;
				}
			}
			properties.setDiffVars(changedVars);
			System.out.println(sameCount + " variables unchanged");
			System.out.println(diffCount + " variables changed");
						
		}catch(SAXException | IOException | 
		XPathExpressionException | ParserConfigurationException e){
			e.printStackTrace();
		}		
	}
}