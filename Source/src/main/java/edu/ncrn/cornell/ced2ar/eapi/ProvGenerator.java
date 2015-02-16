package edu.ncrn.cornell.ced2ar.eapi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.ConfigurationProperties;
import edu.ncrn.cornell.ced2ar.api.data.Fetch;
/**
 * This class  handles prov document
 * 	1. Embeds prov into codebook
 * 	2. Creates and return prov xml from json. 
 * 
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class ProvGenerator {

	private static final Logger logger = Logger.getLogger(ProvGenerator.class);
	public static final String NODE_TYPE_ENTITY 	= "Entity";
	public static final String NODE_TYPE_AGENT 		= "Agent";
	public static final String NODE_TYPE_ACTIVITY 	= "Activity";
	
	
	public static final String NODE_PROV_ROOT		= "prov:document";
	public static final String NODE_PROV_ENTITY		= "prov:entity";
	public static final String NODE_PROV_AGENT		= "prov:agent";
	public static final String NODE_PROV_ACTIVITY	= "prov:activity";
	
	
	public static final String NODE_PROV_LABEL		= "prov:label";
	public static final String NODE_PROV_LOCATION	= "prov:location";
	public static final String NODE_PROV_TYPE		= "prov:type";
	public static final String NODE_PROV_VALUE		= "prov:value";
	
	public static final String NODE_PROV_TITLE		= "dc:title";
	public static final String NODE_PROV_DATE		= "dc:date";
	
	public static final String NODE_PROV_GIVEN_NAME					= "foaf:givenName";
	public static final String NODE_PROV_WORK_INFO_HOME_PAGE		= "foaf:workInfoHomepage";
	public static final String PROV_INSERT_NODE						= "stdyDscr";
	
	public static final String PROV_ATTR_ID	 = "prov:id";

	/**
	 * @param codebookWithProv  XML String that represents codebook with embedded prov.
	 * @return returns Prov in JSON format. If there an error or codebook does not contain prov this method returns empty string
	 */
	public String getProvJSONFromCodebook(String codebookWithProv) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setValidating(false);
	        dbf.setNamespaceAware(true);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        String utf8CodebookWithProv = new String(codebookWithProv.getBytes(),"UTF-8"); // convert the string to utf-8 format
	        ByteArrayInputStream input = new ByteArrayInputStream(utf8CodebookWithProv.getBytes());
			Document codebook = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			
	        Node provNodes 			= codebook.getElementsByTagName(NODE_PROV_ROOT).item(0);
	        if(provNodes==null) return ""; // There is  no embedded prov in the codebook
	        
			StringBuilder jsonString = new StringBuilder("{\n ");
			jsonString.append( "\"nodes\":[\n" );
	        NodeList provNodelist 	= provNodes.getChildNodes();
	        Document provDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	        Element root = this.getProcRootElement(provDocument);
	        for (int i = 0; i < provNodelist.getLength(); i++) {
	            Node node = provNodelist.item(i);
	    		String nodeName = node.getNodeName();
	    		switch (nodeName) {
	    			case NODE_PROV_ENTITY:
	    				String entityJson = geNodeEntityJSONString(node);
	    				if(StringUtils.isEmpty(entityJson)) continue;
	    				jsonString.append(entityJson);
	    				jsonString.append(",\n");
	    				break;
	    			case NODE_PROV_AGENT:
	    				String agentJson = getNodeAgentJSONString(node);
	    				if(StringUtils.isEmpty(agentJson)) continue;
	    				jsonString.append(agentJson);
	    				jsonString.append(",\n");
	    				break;
	    			case NODE_PROV_ACTIVITY:
	    				String activityJson = getNodeActivityJSONString(node);
	    				if(StringUtils.isEmpty(activityJson)) continue;
	    				jsonString.append(activityJson);
	    				jsonString.append(",\n");
	    				break;
	    			default:	
	    		}
	        }
	        jsonString.append( "],\n" );
	        jsonString.append(getNodeTypesJSON());
	        jsonString.append("\n}");
	        return jsonString.toString();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 
	 * @param codebookWithProv  Codebook String with embedded prov 
	 * @return Document that represents Prov portion. If codebook does not contain Prov, an empty Document is returned.
	 * 		 null is returned if there is an error.  
	 */
	public Document getProvDocumentFromCodebook(String codebookWithProv) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        Document codebook = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("C:/java/info/rdf2json/ssbwithprov.xml");
	        Node provNodes 			= codebook.getElementsByTagName(NODE_PROV_ROOT).item(0);
	        Document provDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	        
	        if(provNodes == null) {
	        	return provDocument;
	        }
	        // loop through the prov nodes and create a new document
	        NodeList provNodelist 	= provNodes.getChildNodes();
	        Element root = this.getProcRootElement(provDocument);
	        for (int i = 0; i < provNodelist.getLength(); i++) {
	            Node node = provNodelist.item(i);
	            Node copyNode = provDocument.importNode(node, true);
	            root.appendChild(copyNode);
	        }
	        provDocument.appendChild(root);
	        String provString = getDocumentAsString(provDocument);
			logger.info(provString);
	        return provDocument;
		}
		catch(Exception ex) {
			logger.info("Error occured getting Prov from Codebook." );
			logger.error(ex);
			return null;
		}
	}


	
	/**
	 * 
	 * @param provUrl location of the prov document
	 * @param codebookXML	codebook in xml format 
	 * @return	xml of prov embedded codebook; returns codbookXML unchanged if there is an exception.   
	 */
	
	public String insertProv(String provUrl,String codebookXML) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setValidating(false);
	        dbf.setNamespaceAware(true);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        ByteArrayInputStream input = new ByteArrayInputStream(codebookXML.getBytes());
	        Document codebook = db.parse(input);
	        removeNode(codebook,"relStdy");
	        insertProvDocument(codebook,provUrl);
			String codebookWithProv =  getDocumentAsString(codebook);
			return codebookWithProv.replace("xmlns=\"\"", "");
		}
		catch(Exception ex) {
			logger.info("Error occured in inserting Prov to codebook. Returning without inserting prov." );
			logger.error(ex);
			return codebookXML;
		}
	}
	
	/**
	 * 
	 * @param provUrl URL of the prov.  The contents of the URL are expected to be in JSON format 
	 * @return	Document that represent Prov
	 * @throws Exception
	 */
	public Document getProvDocument(String provUrl) throws Exception {
		
		DocumentBuilderFactory dbf 	= DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db 			= dbf.newDocumentBuilder();
        Document provDocument 		= db.newDocument();
        String jsonString 			= getProvString(provUrl);
		JSONObject jsonObject 	= new JSONObject(jsonString);
		HashMap<String, String> nodeTypes = getAllNodeTypes(jsonObject);

		Element provRootNode= this.getProcRootElement(provDocument);
	
		JSONArray nodes = jsonObject.getJSONArray("nodes");
		for (int i = 0; i < nodes.length(); i++) {
			JSONObject node = nodes.getJSONObject(i);
			if (!node.has("nodeType")) continue;
			String nodeName = nodeTypes.get(node.getString("nodeType"));
			
			switch (nodeName) {
				case NODE_TYPE_ENTITY:
					Element entityElement = provDocument.createElement(NODE_PROV_ENTITY);
					entityElement.setAttribute(PROV_ATTR_ID, this.getProvElementId(node));
					 if (node.has("label")) {  
						String label = node.getString("label");
						Element elementLabel = provDocument.createElement(NODE_PROV_LABEL);
						elementLabel.setTextContent(label);
						entityElement.appendChild(elementLabel);
					}
					if (node.has("location")) {  
						String label = node.getString("location");
						Element elementTitle = provDocument.createElement(NODE_PROV_LOCATION);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					if (node.has("type")) {  
						String label = node.getString("type");
						Element elementTitle = provDocument.createElement(NODE_PROV_TYPE);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					if (node.has("value")) {  
						String label = node.getString("value");
						Element elementTitle = provDocument.createElement(NODE_PROV_VALUE);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					if (node.has("date")) {
						String date = node.getString("date");
						Element elementDate = provDocument.createElement(NODE_PROV_DATE);
						elementDate.setTextContent(date);
						entityElement.appendChild(elementDate);
					}
					 if (node.has("label")) { // Add title at the end  
						String label = node.getString("label");
						Element elementTitle = provDocument.createElement(NODE_PROV_TITLE);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					provRootNode.appendChild(entityElement);
					break;
				case NODE_TYPE_AGENT:
					Element agentElement = provDocument.createElement(NODE_PROV_AGENT);
					agentElement.setAttribute(PROV_ATTR_ID, this.getProvElementId(node));
					 if (node.has("label")) {  
						String label = node.getString("label");
						Element elementLabel = provDocument.createElement(NODE_PROV_LABEL);
						elementLabel.setTextContent(label);
						agentElement.appendChild(elementLabel);
					}
					if (node.has("location")) {  
						String label = node.getString("location");
						Element elementTitle = provDocument.createElement(NODE_PROV_LOCATION);
						elementTitle.setTextContent(label);
						agentElement.appendChild(elementTitle);
					}
					if (node.has("type")) {  
						String label = node.getString("type");
						Element elementTitle = provDocument.createElement(NODE_PROV_TYPE);
						elementTitle.setTextContent(label);
						agentElement.appendChild(elementTitle);
					}
					
					if (node.has("givenName")) {  
						String givenName = node.getString("givenName");
						Element elementGivenName = provDocument.createElement(NODE_PROV_GIVEN_NAME);
						elementGivenName.setTextContent(givenName);
						agentElement.appendChild(elementGivenName);
					}
					if (node.has("workInfoHomepage")) {  
						String workInfoHomepage = node.getString("workInfoHomepage");
						Element elementWorkInfoHomepage = provDocument.createElement(NODE_PROV_WORK_INFO_HOME_PAGE);
						elementWorkInfoHomepage.setTextContent(workInfoHomepage);
						agentElement.appendChild(elementWorkInfoHomepage);
					}
					provRootNode.appendChild(agentElement);
					break;
					

				case NODE_TYPE_ACTIVITY:
					Element activityElement = provDocument.createElement(NODE_PROV_ACTIVITY);
					activityElement.setAttribute(PROV_ATTR_ID, this.getProvElementId(node));
					/*
					if (node.has("startTime")) {  
						String startTime = node.getString("startTime");
						Element elementStartTime = provDocument.createElement(NODE_PROV_LABEL);
						elementStartTime.setTextContent(startTime);
						activityElement.appendChild(elementStartTime);
					}
					if (node.has("endTime")) {  
						String endTime = node.getString("endTime");
						Element elementEndTime = provDocument.createElement(NODE_PROV_LABEL);
						elementEndTime.setTextContent(endTime);
						activityElement.appendChild(elementEndTime);
					}
					*/
					 if (node.has("label")) {  
						String label = node.getString("label");
						Element elementLabel = provDocument.createElement(NODE_PROV_LABEL);
						elementLabel.setTextContent(label);
						activityElement.appendChild(elementLabel);
					}
					if (node.has("location")) {  
						String label = node.getString("location");
						Element elementTitle = provDocument.createElement(NODE_PROV_LOCATION);
						elementTitle.setTextContent(label);
						activityElement.appendChild(elementTitle);
					}
					if (node.has("type")) {  
						String label = node.getString("type");
						Element elementTitle = provDocument.createElement(NODE_PROV_TYPE);
						elementTitle.setTextContent(label);
						activityElement.appendChild(elementTitle);
					}
					
					provRootNode.appendChild(activityElement);
					break;

				default:
			}
		}
		provDocument.appendChild(provRootNode);
		return provDocument;
	}

	
	/**
	 * 
	 * @param codebook Document
	 * @param nodeTagName Name of the node tag that would be deleted 
	 * This method removes a node and its children from the document if the node exists.
	 */
	private void removeNode(Document codebook,String nodeTagName ) {
		Node categories = codebook.getElementsByTagName(nodeTagName).item(0);
		if(categories !=null) {
			NodeList categorieslist = categories.getChildNodes();
			while (categorieslist.getLength() > 0) {
			    Node node = categorieslist.item(0);
			    node.getParentNode().removeChild(node);
			}
			// Normalize the DOM tree, puts all text nodes in the
			// full depth of the sub-tree underneath this node
			codebook.normalize();	
		}
	}

	
	private void insertProvDocument(Document codebookDocument,String provUrl) throws Exception {
        String jsonString 			= getProvString(provUrl);
		JSONObject jsonObject 		= new JSONObject(jsonString);
		Element releaseStudyElement = codebookDocument.createElement("relStdy");
		Element provRootNode 		= codebookDocument.createElement(this.NODE_PROV_ROOT);
		
		releaseStudyElement.appendChild(provRootNode);
		HashMap<String, String> nodeTypes = getAllNodeTypes(jsonObject);	 
	
		JSONArray nodes = jsonObject.getJSONArray("nodes");
		for (int i = 0; i < nodes.length(); i++) {
			JSONObject node = nodes.getJSONObject(i);
			if (!node.has("nodeType")) continue;
			String nodeName = nodeTypes.get(node.getString("nodeType"));
			
			switch (nodeName) {
				case NODE_TYPE_ENTITY:
					Element entityElement = codebookDocument.createElement(NODE_PROV_ENTITY);
					entityElement.setAttribute(PROV_ATTR_ID, this.getProvElementId(node));
					 if (node.has("label")) {  
						String label = node.getString("label");
						Element elementLabel = codebookDocument.createElement(NODE_PROV_LABEL);
						elementLabel.setTextContent(label);
						entityElement.appendChild(elementLabel);
					}
					if (node.has("location")) {  
						String label = node.getString("location");
						Element elementTitle = codebookDocument.createElement(NODE_PROV_LOCATION);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					if (node.has("type")) {  
						String label = node.getString("type");
						Element elementTitle = codebookDocument.createElement(NODE_PROV_TYPE);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					if (node.has("value")) {  
						String label = node.getString("value");
						Element elementTitle = codebookDocument.createElement(NODE_PROV_VALUE);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					if (node.has("date")) {
						String date = node.getString("date");
						Element elementDate = codebookDocument.createElement(NODE_PROV_DATE);
						elementDate.setTextContent(date);
						entityElement.appendChild(elementDate);
					}
					 if (node.has("label")) { // Add title at the end  
						String label = node.getString("label");
						Element elementTitle = codebookDocument.createElement(NODE_PROV_TITLE);
						elementTitle.setTextContent(label);
						entityElement.appendChild(elementTitle);
					}
					provRootNode.appendChild(entityElement);
					break;
				case NODE_TYPE_AGENT:
					Element agentElement = codebookDocument.createElement(NODE_PROV_AGENT);
					agentElement.setAttribute(PROV_ATTR_ID, this.getProvElementId(node));
					 if (node.has("label")) {  
						String label = node.getString("label");
						Element elementLabel = codebookDocument.createElement(NODE_PROV_LABEL);
						elementLabel.setTextContent(label);
						agentElement.appendChild(elementLabel);
					}
					if (node.has("location")) {  
						String label = node.getString("location");
						Element elementTitle =codebookDocument.createElement(NODE_PROV_LOCATION);
						elementTitle.setTextContent(label);
						agentElement.appendChild(elementTitle);
					}
					if (node.has("type")) {  
						String label = node.getString("type");
						Element elementTitle = codebookDocument.createElement(NODE_PROV_TYPE);
						elementTitle.setTextContent(label);
						agentElement.appendChild(elementTitle);
					}
					
					if (node.has("givenName")) {  
						String givenName = node.getString("givenName");
						Element elementGivenName = codebookDocument.createElement(NODE_PROV_GIVEN_NAME);
						elementGivenName.setTextContent(givenName);
						agentElement.appendChild(elementGivenName);
					}
					if (node.has("workInfoHomepage")) {  
						String workInfoHomepage = node.getString("workInfoHomepage");
						Element elementWorkInfoHomepage = codebookDocument.createElement(NODE_PROV_WORK_INFO_HOME_PAGE);
						elementWorkInfoHomepage.setTextContent(workInfoHomepage);
						agentElement.appendChild(elementWorkInfoHomepage);
					}
					provRootNode.appendChild(agentElement);
					break;
					

				case NODE_TYPE_ACTIVITY:
					Element activityElement = codebookDocument.createElement(NODE_PROV_ACTIVITY);
					activityElement.setAttribute(PROV_ATTR_ID, this.getProvElementId(node));
					 if (node.has("label")) {  
						String label = node.getString("label");
						Element elementLabel = codebookDocument.createElement(NODE_PROV_LABEL);
						elementLabel.setTextContent(label);
						activityElement.appendChild(elementLabel);
					}
					if (node.has("location")) {  
						String label = node.getString("location");
						Element elementTitle = codebookDocument.createElement(NODE_PROV_LOCATION);
						elementTitle.setTextContent(label);
						activityElement.appendChild(elementTitle);
					}
					if (node.has("type")) {  
						String label = node.getString("type");
						Element elementTitle = codebookDocument.createElement(NODE_PROV_TYPE);
						elementTitle.setTextContent(label);
						activityElement.appendChild(elementTitle);
					}
					
					provRootNode.appendChild(activityElement);
					break;

				default:
			}
		}
		NodeList nodeList = codebookDocument.getElementsByTagName("stdyDscr");
		nodeList.item(0).appendChild(releaseStudyElement);
	}

	/**
	 * 
	 * @return Returns nodeTypes Json format
	 * 			"nodeTypes":[{"name":"Entity","name":"Agent","name":"Activity"}]
	 */
	private String getNodeTypesJSON() {
		StringBuilder jsonString = new StringBuilder("\"nodeTypes\":[\n");
		jsonString.append("{");
		jsonString.append(getNameValuePairJSON("name","Entity"));
		jsonString.append(",");
		jsonString.append(getNameValuePairJSON("name","Agent"));
		jsonString.append(",");
		jsonString.append(getNameValuePairJSON("name","Activity"));
		jsonString.append("}");
		jsonString.append("\n]");
		return jsonString.toString();
	}

	
	/**
	 * @param node XML node representing Agent
	 * @return Returns JSON form of the Agent node.  
	 */
	private String getNodeAgentJSONString(Node node) {
		StringBuilder jsonString = new StringBuilder("");
		NamedNodeMap map =  node.getAttributes();
		Node agentNode = map.getNamedItem(PROV_ATTR_ID);
		if(agentNode!=null) {
			String id = agentNode.getTextContent();
			jsonString.append("{"+this.getNameValuePairJSON("id", id));
			NodeList nodeList = node.getChildNodes();
			if(nodeList!=null) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node n = nodeList.item(i);
					String nodeName = n.getNodeName().trim();
					String nodeText = n.getTextContent().trim();
					if(StringUtils.isNotEmpty(nodeName) && StringUtils.isNotEmpty(nodeText)) {
						if(nodeName.equalsIgnoreCase(NODE_PROV_LABEL)) 
							nodeName = "label";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_LOCATION)) 
							nodeName = "location";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_TYPE))  
							nodeName = "type";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_GIVEN_NAME))  
							nodeName = "givenName";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_WORK_INFO_HOME_PAGE))  
							nodeName = "workInfoHomepage";
						else
							continue;
						jsonString.append(",");
						jsonString.append(getNameValuePairJSON(nodeName,nodeText));
					}
				}
			}
			jsonString.append(",");
			jsonString.append("\"nodeType\":1}");	
		}
		
		return jsonString.toString();

	}
		

	/**
	 * 
	 * @param node XML node representing Entity
	 * @return Returns JSON form of the Entity node.  
	 * 
	 */
	private String geNodeEntityJSONString(Node node) {
		StringBuilder jsonString = new StringBuilder("");
		NamedNodeMap map =  node.getAttributes();
		Node entityNode = map.getNamedItem(PROV_ATTR_ID);
		if(entityNode!=null) {
			String id = entityNode.getTextContent();
			jsonString.append("{"+this.getNameValuePairJSON("id", id));
			
			NodeList nodeList = node.getChildNodes();
			if(nodeList!=null) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node n = nodeList.item(i);
					String nodeName = n.getNodeName().trim();
					String nodeText = n.getTextContent().trim();
					if(StringUtils.isNotEmpty(nodeName) && StringUtils.isNotEmpty(nodeText)) {
						if(nodeName.equalsIgnoreCase(NODE_PROV_LABEL)) 
							nodeName = "label";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_LOCATION)) 
							nodeName = "location";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_TYPE))  
							nodeName = "type";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_VALUE))  
							nodeName = "value";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_DATE))  
							nodeName = "date";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_TITLE)) 
							continue; //Title is label
						else
							continue;
						
						jsonString.append(",");
						jsonString.append(getNameValuePairJSON(nodeName,nodeText));
					}
				}
			}
			jsonString.append(",");
			jsonString.append("\"nodeType\":0}");	
		}
		
		return jsonString.toString();
		
	}
	
	/**
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @return returns equivalent json "name":"value" 
	 */
	private String getNameValuePairJSON(String name, String value) {
		return "\""+name+"\":\""+value+"\"";
	}
	/**
	 * 
	 * @param node XML node representing Activity
	 * @return Returns JSON form of the activity node.  
	 * 
	 */
	private String getNodeActivityJSONString(Node node) {
		StringBuilder jsonString = new StringBuilder("");
		NamedNodeMap map =  node.getAttributes();
		Node agentNode = map.getNamedItem(PROV_ATTR_ID);
		if(agentNode!=null) {
			String id = agentNode.getTextContent();
			jsonString.append("{"+this.getNameValuePairJSON("id", id));
			NodeList nodeList = node.getChildNodes();
			if(nodeList!=null) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node n = nodeList.item(i);
					String nodeName = n.getNodeName().trim();
					String nodeText = n.getTextContent().trim();
					if(StringUtils.isNotEmpty(nodeName) && StringUtils.isNotEmpty(nodeText)) {
						if(nodeName.equalsIgnoreCase(NODE_PROV_LABEL)) 
							nodeName = "label";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_LOCATION)) 
							nodeName = "location";
						else if(nodeName.equalsIgnoreCase(NODE_PROV_TYPE))  
							nodeName = "type";
						else
							continue;
						jsonString.append(",");
						jsonString.append(getNameValuePairJSON(nodeName,nodeText));
					}
				}
			}
			jsonString.append(",");
			jsonString.append("\"nodeType\":2}");	
		}
		return jsonString.toString();
	}

	private Element getProcRootElement(Document provDocument) {
		Element rootElement = provDocument.createElementNS("http://www.w3.org/ns/prov#",NODE_PROV_ROOT);
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dc", "http://purl.org/dc/elements/1.1/");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ex", "http://example.com/ns/ex#");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:tr", "http://example.com/ns/tr#");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:foaf", "http://xmlns.com/foaf/0.1/");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:repeca", "https://ideas.repec.org/e/#");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:exn", "http://ced2ar.org/ns/external#");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:file", "file:///home/vilhuber/Projects/PUMS/#");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:RePEc", "https://ideas.repec.org/#");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ced2ar", "http://ced2ar.org/ns/core#");
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:act", "http://ced2ar.org/ns/activities#");
		return rootElement;
	}
	
	private String getProvElementId(JSONObject node) throws JSONException{
		String id = "";
		if (node.has("uri"))
			id = node.getString("uri");
		else if (node.has("id"))
			id = node.getString("id");

		return id;
	}
	


	private HashMap<String, String> getAllNodeTypes(JSONObject jsonObject)throws JSONException {
		HashMap<String, String> nodeTypesHash = new HashMap<String, String>();
		JSONArray nodeTypes = jsonObject.getJSONArray("nodeTypes");
		for (int i = 0; i < nodeTypes.length(); i++) {
			JSONObject node = nodeTypes.getJSONObject(i);
			if (node.has("name")) {
				String nodeType = node.getString("name");
				nodeTypesHash.put("" + i, nodeType);
			}
		}
		return nodeTypesHash;
	}

	/**
	 * 
	 * @return fetches prov.json from baseX and returns it as a string
	 */
	private String getProvString(String provUrl) throws Exception {
		ConfigurationProperties configurationProperties = new  ConfigurationProperties();
		String adminHash = configurationProperties .getValue("baseXWriterHash");
		Config config = Config.getInstance();
		String port = Integer.toString(config.getPort());
		String jsonString =Fetch.get(provUrl,"Authorization", "Basic "+adminHash);
		return jsonString;
	}

	/**
	 * 
	 * @param DOM Document codebook
	 * @return String converted to pretty string
	 * @throws Exception
	 */
	private String getDocumentAsString(Document doc) throws Exception{
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(doc), new StreamResult(out));
        String results = out.toString();
        return results;
	}
	
	/**
	 * @param provDoc prov xml document string
	 * @return Validates against prov.xsd and returns true / false1
	 */
	private boolean validateProv(String provDoc) {
		try {
			SchemaFactory factory =SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new URL("http://www.w3.org/ns/prov.xsd"));
			Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(new StringReader(provDoc)));
	        return true;
		}
		catch(Exception ex) {
			logger.error("Error in validation ",ex);
			return false;
		}
	}

	
	public static void main(String argc[]) {
		StringBuilder sb = new StringBuilder("");
		try {
			ProvGenerator provGenerator = new ProvGenerator();
			
			String loc = "C:/java/info/rdf2json/ssbwithprov.xml";
			File codebookWithProv = new File(loc);
			StringWriter writer = new StringWriter();
			IOUtils.copy(new FileInputStream(codebookWithProv), writer, "UTF-8");
			//provGenerator.getProvJSON(writer.toString());
			String str = writer.toString();
			//System.out.println(str);
			provGenerator.getProvJSONFromCodebook(str);
			
			/*
			ProvGenerator provGenerator = new ProvGenerator();
			Document doc = provGenerator.getProvDocument("http://localhost:8080");
			String provDoc = provGenerator.getDocumentAsString(doc);
			System.out.println(provDoc);
		//	System.out.println(provGenerator.validateProv(provDoc));
			*/
			
            
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}




/*
 */

/*
//Document provDocument = getProvDocument(host);
//insertProvDocument(codebook,host);

//String provString =getDocumentAsString(provDocument);
NodeList nodeList = codebook.getElementsByTagName("stdyDscr");
Element releaseStudyElement = codebook.createElement("relStdy");



releaseStudyElement.setTextContent(provString);
nodeList.item(0).appendChild(releaseStudyElement);

		String loc = "C:/java/info/rdf2json/prov.json";
		File jsonFile = new File(loc);
		StringWriter writer = new StringWriter();
		IOUtils.copy(new FileInputStream(jsonFile), writer, "UTF-8");
		

*/

