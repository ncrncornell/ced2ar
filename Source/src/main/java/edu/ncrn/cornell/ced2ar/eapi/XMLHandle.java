package edu.ncrn.cornell.ced2ar.eapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.parser.XSOMParser;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.prov.oldmodel.Activity;
import edu.ncrn.cornell.ced2ar.eapi.prov.oldmodel.Agent;
import edu.ncrn.cornell.ced2ar.eapi.prov.oldmodel.Edge;
import edu.ncrn.cornell.ced2ar.eapi.prov.oldmodel.Entity;

/**
 * Class dealing with XML interaction
 * @author NCRN Project Team
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Kyle Brumsted, Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class XMLHandle {
	private static final Logger logger = Logger.getLogger(XMLHandle.class);
	private InputStream _xmlStream = null;
	private String _schemaURI = null;
	private XSOMParser _schemaParser = null;
	private Document _doc = null;
	private String error = null;
	private String repoXML = null;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

	/**
	 * Constructor
	 * @param ins input stream with xml content
	 * @param sURI schema URI
	 */
	public XMLHandle(InputStream ins, String sURI){	
		_xmlStream = ins;
		_schemaURI= sURI;
		_schemaParser = getParser();
		_doc = loadDoc();
	}
	
	/**
	 * Constructor
	 * @param xmlContent the XML string
	 * @param sURI schema URI
	 */
	public XMLHandle(String xmlContent, String sURI){
		_xmlStream = new ByteArrayInputStream(xmlContent.getBytes());
		
	    _schemaURI= sURI;
		_schemaParser = getParser();
	    
		_doc = loadDoc();
	}
	
	public XMLHandle(String sURI){
		_schemaURI= sURI;
		_schemaParser = getParser();
	}
	
//Constructor dependency functions
	/**
	 * Creates parser instance for schema
	 * @return the parser
	 */
	private XSOMParser getParser(){
		XSOMParser parser = new XSOMParser();
		try {
			parser.parse(_schemaURI);
		} catch (SAXException e) {
			error = "Error fetching schema associated with XML file.";
			e.printStackTrace();
			return null;
		}
		return parser;	
	}
	
	/**
	 * Creates instance of XML document for parsing
	 * @return the Document
	 */
	private Document loadDoc(){
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();		
        try {
        	DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(_xmlStream);		
			return doc;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			error = "Error loading document. File given is not well formed XML.";
			e.printStackTrace();
		}
        return  null;
	}

//Function dependency Functions
	
	/**
	 * Creates instance of XML document for parsing
	 * @param xpathString the xpath where the element would reside
	 * @return whether or not the document has the element
	 */
	public Boolean hasElement(String xpathString){
		if(countIdentifiedNodes(xpathString) > 0) return true;
		return false;
	}
	
	/**
	 * Finds the definition of a complex xml type as defined in the schema
	 * @param elementName the type to be defined
	 * @return the definition
	 */
	@SuppressWarnings("rawtypes")
	private XSParticle[] getComplexType(String elementName) {
		XSParticle[] pArray = null;
		XSSchemaSet xSchema = null;
		try {
			xSchema = _schemaParser.getResult();
			Iterator itr = xSchema.iterateSchema();
			while (itr.hasNext()) {
				XSSchema s = (XSSchema) itr.next();
				Iterator jtr = s.iterateElementDecls();
				while (jtr.hasNext()) {
					XSElementDecl e = (XSElementDecl) jtr.next();
					if (e.getName().equals(elementName) && e.getType().isComplexType()) {
						XSComplexType xsComplexType = s.getComplexType(e.getType().getName());
						XSContentType xsContentType = xsComplexType.getContentType();
						XSParticle particle = xsContentType.asParticle();
						if (particle != null) {
							XSTerm term = particle.getTerm();
							if (term.isModelGroup()) {
								XSModelGroup xsModelGroup = term.asModelGroup();
								pArray = xsModelGroup.getChildren();							
							}
						}
					}
				}
			}			
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return pArray;
	}
	
//Public editing and reading functions
	
	/**
	 * Deletes either the first node identified by xpathString, or all nodes
	 * identified by xpathString, depending on the value of deleteAll
	 * @param xpathString the node to be deleted
	 * @param deleteAll whether to delete all nodes specified by xpathString,
	 * or just delete the first one.
	 */
	public void deleteNode(String xpathString, boolean deleteAll){
        NodeList nodes = getNodeList(xpathString);
		if(nodes.getLength() > 1 && !deleteAll)
		if(nodes.getLength() == 0) return;
		int i = 0;
		do{
			Node node = nodes.item(i);
			try{
				if(node.getNodeType() == Node.ATTRIBUTE_NODE){
					Attr att = (Attr)node;
					Element parent = att.getOwnerElement();
					parent.removeAttribute(att.getNodeName());
				}else{
					Node parent = node.getParentNode();
					parent.removeChild(node);
				}
			}catch(NullPointerException e){}//Path is already deleted
			i++;
		}while(deleteAll && i < nodes.getLength());
	}
		
	/**
	 * Updates the version number of the variable, or creates one if there isn't one.
	 * @param varName the name of the variable to update
	 */
	public void updateVarVersion(String varName){
		String xpathString = "/codeBook/dataDscr/var[@name='"+varName+"']/verStmt/version";
		if(!hasElement(xpathString)){
			String bookVerPath = "/codeBook/stdyDscr/citation/verStmt/version";
			if(!hasElement(bookVerPath)){
				addReplace(bookVerPath, "1.0", true, true, false, true);
			}
			NodeList nodes = getNodeList(bookVerPath);
			if(nodes.getLength() != 1){
				deleteNode(bookVerPath, true);
				updateVarVersion(varName);
				return;
			}
			Node n = nodes.item(0);
			String cVer = n.getTextContent();
			if(cVer == null || "".equals(cVer)){
				addReplace(bookVerPath, "1.0", false, true, false, true);
				updateVarVersion(varName);
				return;
			}
			addReplace(xpathString, cVer+".0.0", true, true, false, true);	
			addReplace(xpathString+"/@date", new Date().toString(), true, true, false, true);
		}
		
		//if it already exists, we take the version number, parse it increment it and then re-insert it
		NodeList nds = getNodeList(xpathString);
		if(nds.getLength() != 1){
			deleteNode(xpathString, true);
			updateVarVersion(varName);
			return;
		}
		Node node = nds.item(0);
		String vVer = node.getTextContent();
		if(vVer == null || "".equals(vVer)){
			deleteNode(xpathString, true);
			updateVarVersion(varName);
			return;
		}
		String[] nums = vVer.split("\\.");
		int last = Integer.parseInt(nums[nums.length-1]);
		last += 1;
		nums[nums.length-1] = ""+last;
		String newVer = nums[0];
		for(int i = 1; i < nums.length; i++){
			newVer += "."+nums[i];
		}
		addReplace(xpathString, newVer, false, false, false, true);
		addReplace(xpathString+"/@date", new Date().toString(), false, true, false, true);
		
	}
	
	/**
	 * Updates the version number of the codeBook, or creates one if there isn't one.
	 */
	public void updateCodebookVersion(){
		String xpathString = "/codeBook/stdyDscr/citation/verStmt/version";
		updateVersion(xpathString);
	}
	
	/**
	 * Checks if the given xpath conforms to one of the accepted regexs
	 * @param xpath the string to check
	 * @return boolean whether it matches
	 */
	public boolean checkXpath(String xpath){
		
		if(xpath.matches("///")) return false;
		
		if(xpath.endsWith("/")) return false;
		
		String[] terms = xpath.split("/");
		if(terms.length <= 2) return false;
		String current;
		
		for( int i = 0 ; i < terms.length ; i++ ){
			current = terms[i];
			if(current == null || current.equals("")) continue;
			if(current.matches(".[!#$%&*+_\\(\\)\\^-].*")) return false;//Term Matches:
			if(!(current.matches("[0-9a-zA-Z_]+") ||//letters
				  current.matches("@[0-9a-zA-Z_]+") ||//@letters
				  current.matches("[0-9a-zA-Z_]+\\[[0-9]+\\]") ||//letters[numbers]			
				  current.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+\\]") ||//letters[letters]
				  current.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+\\]\\[[0-9]+\\]") ||//letters[letters][numbers]
				  current.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+='[0-9a-zA-Z_]+'\\]") ||//letters[letters='letters']
				  current.matches("[0-9a-zA-Z_]+\\[@[0-9a-zA-Z_]+='[\\.0-9a-zA-Z_]+'\\]") ||//letters[@letters='letters'] - needs to include decimal for attr
				  current.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+='[0-9a-zA-Z_]+'\\]\\[[0-9]+\\]") ||//letters[letters='letters'][numbers]
				  current.matches("[0-9a-zA-Z_]+\\[@[0-9a-zA-Z_]+='[\\.0-9a-zA-Z_]+'\\]\\[[0-9]+\\]") || //letters[@letters='letters'][numbers] - needs to include decimal for attr
				  current.matches("[0-9a-zA-Z_]+\\[last\\(\\)+\\]")//letters[last()]
			)) return false;
			
		}
		return true;
	}
	
	/**
	 * Updated version of add replace
	 * ARGS
	 * @param xpathString full xpath to change. Can include indices and point to elements or attributes 
	 * @param value value to write at xpath
	 * @param doesAppend  when true, function will add a new element if that xpath exists. 
	 * @param isRecursive if ancestors of target element do not exist, will create. Cannot use //in xpath  when true
	 * If false, and appends need to be made, will throw error. 
	 * @param updateAll  if true, we apply the change to every node that the xpath matches, if false we only change the first one.
	 * @param replaceChildren TODO: Write description of this param
	 */
	public void addReplace(String xpathString, String value, boolean doesAppend, 
	boolean isRecursive, boolean updateAll, boolean replaceChildren){
		//get rid of parenthesis
		//xpathString = xpathString.replaceAll("\\(\\)","");
		//System.out.println("Add replace called for "+xpathString +" with value '" + value + "' and doesAppend is "+doesAppend);
				
		//Check xpath is in form we can handle
		if(!checkXpath(xpathString)){
			//System.out.println("cannot handle " + xpathString);
			error = "xpath does not match accepted xpath regexes!";
			return;
		}
		
		//Check xpath isn't recursive and has wildcard
		if(xpathString.contains("//") && isRecursive){
			error = "XPath cannot contain wildcard // when is recursive set to true";
			return;
		}	
		
		String[] xpathArray = xpathString.split("/");
		//The target references to the element or attribute which the function is attempting to modify
		//ie - the last object specified in the xpath statement
		String target = xpathArray[xpathArray.length-1];
		
		//Parses the xpath to the parent of the target
		int lastSlash = xpathString.lastIndexOf("/");
		String xpathStringP = xpathString.substring(0,lastSlash);
		
		//If not recursive and parents don't exist, can't add or replace.
		if(!isRecursive && !hasElement(xpathStringP)){		
			error = "Cannot add ancestors for target when the argument 'isRecursive' is set to false."
					+" If you intended to add the required ancestors, set 'isRecursive' to true.";
			return ;
		}
		
		//If recursive, and parent doesn't exist, recursively add parent nodes
		if(isRecursive && !hasElement(xpathStringP)){
			//System.out.println("calling addReplace recursively with: "+xpathStringP);
			addReplace(xpathStringP,"",true,true,false, replaceChildren);
		}
		
		//We add access attributes if they are being edited and don't exist!
		if(target.equals("@access")){
			if(!hasElement(xpathString))
				doesAppend = true;
		}
		
		/*
		 * NOT SURE ABOUT HAVING INDICES
		 * SHOULD DOESAPPEND OVERRIDE AN INDEX, OR VICE VERSA?
		 * RIGHT NOW DOESAPPEND OVERRIDES INDICES
		 */
		
		//logic to add structure
		if(doesAppend){
			if(target.matches("[0-9a-zA-Z_]+")){//letters		
				//System.out.println("match found for letters");
				addNode(xpathStringP, target, value, updateAll);
			}else if(target.matches("[0-9a-zA-Z_]+\\[last\\(\\)+\\]")){//letters[last()]
				//TODO:Not %100 stable?
				//System.out.println("match found for last()");
				String child = target.substring(target.indexOf("[")+1, target.indexOf("]"));
				addComplexNode(xpathStringP, target, value, child, "", false, updateAll);
			}else if(target.matches("@[0-9a-zA-Z_]+")){//@letters
				//System.out.println("match found for @letters");
				target = target.replaceAll("@", "");
				if("".equals(value)) deleteNode(xpathString, updateAll);
				else addAttribute(xpathStringP, target, value, updateAll);
			}
			else if(target.matches("[0-9a-zA-Z_]+\\[[0-9]+\\]")){//letters[numbers]
				//System.out.println("match found for letters[numbers]");
				addNode(xpathStringP, target.replaceAll("\\[[0-9]+\\]", ""), value, updateAll);
			}
			else if(target.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+\\]")){//letters[letters]
				//System.out.println("match found for letters[letters]");
				String noPred = target.replaceAll("\\[[0-9a-zA-Z_]+\\]", "");
				String child = target.substring(target.indexOf("[")+1, target.indexOf("]"));
				addComplexNode(xpathStringP, noPred, value, child, "", false, updateAll);
			}
			else if(target.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+\\]\\[[0-9]+\\]")){//letters[letters][numbers]
				//System.out.println("match found for letters[letters][numbers]");
				String noPred = target.replaceAll("\\[[0-9a-zA-Z_]+\\]\\[[0-9]+\\]","");
				String child = target.substring(target.indexOf("[")+1, target.indexOf("]"));
				addComplexNode(xpathStringP, noPred, value, child, "", false, updateAll);
			}
			else if(target.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+='[0-9a-zA-Z_]+'\\]")){//letters[letters='letters']
				//System.out.println("match found for letters[letters='letters']");
				String[] nodeAndPred = target.split("\\[");
				nodeAndPred[1] = nodeAndPred[1].replaceAll("\'", "").replaceAll("\"", "").replaceAll("\\]", "");
				String node = nodeAndPred[0];
				String[] subelemAndValue = nodeAndPred[1].split("=");
				String subElem = subelemAndValue[0];
				String subVal = subelemAndValue[1];
				addComplexNode(xpathStringP, node, value, subElem, subVal, false, updateAll);
			}
			else if(target.matches("[0-9a-zA-Z_]+\\[@[0-9a-zA-Z_]+='[\\.0-9a-zA-Z_]+'\\]")){//letters[@letters='letters']
				//System.out.println("match found for letters[@letters='letters']");
				String[] nodeAndAttr = target.split("\\[");
				nodeAndAttr[1] = nodeAndAttr[1].replaceAll("@", "").replaceAll("\\]", "").replaceAll("\"", "").replaceAll("\'", "");
				String node = nodeAndAttr[0];
				String[] attrAndValue = nodeAndAttr[1].split("=");
				String attr = attrAndValue[0];
				String attVal = attrAndValue[1];
				addComplexNode(xpathStringP, node, value, attr, attVal, true, updateAll);
			}
			else if(target.matches("[0-9a-zA-Z_]+\\[[0-9a-zA-Z_]+='[0-9a-zA-Z_]+'\\]\\[[0-9]+\\]")){//letters[letters='letters'][numbers]
				//System.out.println("match found for letters[letters='letters'][numbers]");
				target = target.replaceAll("\\[[0-9]+\\]", "");
				String[] nodeAndPred = target.split("\\[");
				nodeAndPred[1] = nodeAndPred[1].replaceAll("\'", "").replaceAll("\"", "").replaceAll("\\]", "");
				String node = nodeAndPred[0];
				String[] subelemAndValue = nodeAndPred[1].split("=");
				String subElem = subelemAndValue[0];
				String subVal = subelemAndValue[1];
				addComplexNode(xpathStringP, node, value, subElem, subVal, false, updateAll);
			}
			else if(target.matches("[0-9a-zA-Z_]+\\[@[0-9a-zA-Z_]+='[\\.0-9a-zA-Z_]+'\\]\\[[0-9]+\\]")){//letters[@letters='letters'][numbers]
				//System.out.println("match found for letters[@letters='letters'][numbers]");
				target = target.replaceAll("\\[[0-9]+\\]", "");
				String[] nodeAndAttr = target.split("\\[");
				nodeAndAttr[1] = nodeAndAttr[1].replaceAll("@", "").replaceAll("\\]", "").replaceAll("\"", "").replaceAll("\'", "");
				String node = nodeAndAttr[0];
				String[] attrAndValue = nodeAndAttr[1].split("=");
				String attr = attrAndValue[0];
				String attVal = attrAndValue[1];
				addComplexNode(xpathStringP, node, value, attr, attVal, true, updateAll);
			}
			//if more formats are necessary, add more else if statements here.
			else{
				logger.error("Input xpath does not match any of the accepted formats -" + target);
				return;
			}
			return;
		}
		
		//If doc has target element and doesAppend = false, replace the value
		//Or if we try to set an attribute to "", delete the attribute
		if(hasElement(xpathString) && !doesAppend){
			if(target.matches("@[a-zA-Z_]+")){
				if("".equals(value)){
					deleteNode(xpathString, updateAll);
					return;
				}
			}
			replaceValue(xpathString, value, updateAll, replaceChildren);
			return;
		}		
	}
	
	/**
	 * returns the list of nodes pointed to by the xpathString
	 * @param xpathString the location of the node(s)
	 * @return the NodeList of nodes specified by xpathString if there is any
	 */
	public NodeList getNodeList(String xpathString){
		NodeList nodes = null;
		XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;
        try {
			expr = xpath.compile(xpathString);
			nodes = (NodeList) expr.evaluate(_doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
			error = e.getMessage();
			e.printStackTrace();
		}
        return nodes;
	}
	
	/**
	 * counts the number of nodes pointed to by the xpathString
	 * @param xpathString the location of the node(s)
	 * @return the number residing at the given location
	 */
	private int countIdentifiedNodes(String xpathString){
		int count = -1;
		NodeList nodes = getNodeList(xpathString);
		count = nodes.getLength();
		return count;
	}
	
	/**
	 * replaces the values in the node(s) pointed to by xpathString with a new value
	 * @param xpathString the node(s) to replace
	 * @param value the new value to be inserted
	 * @param updateAll whether or not to replace all nodes pointed to by xpathString
	 * @param replaceChildren whether or not to keep children
	 */
	private void replaceValue(String xpathString, String value, boolean updateAll, boolean replaceChildren){
		         
		NodeList nodes = getNodeList(xpathString);
		if(nodes == null || nodes.getLength() < 1) return;
		int i = 0;
		Node node;
		do{
			node = nodes.item(i);
			if(replaceChildren){
				node.setTextContent(value);
				break;
			}
			NodeList children = node.getChildNodes();
			Node child;
			boolean foundText = false;
			for(int j = 0; j < children.getLength(); j++){
				child = children.item(j);
				if(child.getNodeType() == Node.TEXT_NODE){
					child.setTextContent(value);
					foundText = true;
					break;
				}
			}
			if(!foundText){
				Node newNode = _doc.createTextNode(value);
				node.insertBefore(newNode, node.getFirstChild());
			}
			i++;
		}while(updateAll && i < nodes.getLength());
	}
	
	/**
	 *	Adds prov:document node at the xPath specified. 
	 * @param xPath
	 */
	public void addProvDocumentNode(String xPath){
		NodeList nodes = getNodeList(xPath);		
		if(nodes == null || nodes.getLength() < 1) return;
		Node parent = nodes.item(0);
		Element newNode = _doc.createElement(ProvGenerator.NODE_PROV_ROOT);
		parent.appendChild(newNode);		
	}
	

	
	
	/**
	 * adds a node with children to the xml document
	 * @param xpathStringP The xpath for the parent of the node to be added
	 * @param target the node to be added
	 * @param value the value of the new node
	 * @param subTarget the subchild/attribute to be added
	 * @param subValue the value of the new subchild/attribute
	 * @param isAttr whether or not the sub-structure is an attribute
	 * @param updateAll whether or not to add this new structure to all of the nodes pointed to by xpathStringP
	 */
	public void addComplexNode(String xpathStringP, String target, String value, String subTarget, String subValue, boolean isAttr, boolean updateAll){
		NodeList nodes = getNodeList(xpathStringP);
		if(nodes == null || nodes.getLength() < 1) return;
		int i = 0;
		do{
			Node prnt = nodes.item(i);
			Element newNode;
			newNode = _doc.createElement(target);
			newNode.setTextContent(value);
			if(isAttr){
				newNode.setAttribute(subTarget, subValue);				
			}else {
				Element newSubNode = _doc.createElement(subTarget);
				newSubNode.setTextContent(subValue);
				newNode.appendChild(newSubNode);
			}
			Node nextSibling = getNextSibling(xpathStringP+"/"+target);
			if(nextSibling == null){
				prnt.appendChild(newNode);
			}else{
				prnt.insertBefore(newNode, nextSibling);
			}
			i++;
		}while(updateAll && i < nodes.getLength());
	}

	/*
	 *     <xs:complexType name="Generation">
        <xs:sequence>
            <xs:element name="entity" type="prov:IDRef"/>
            <xs:element name="activity" type="prov:IDRef" minOccurs="0"/>
            <xs:element name="time" type="xs:dateTime" minOccurs="0"/>
            <!-- prov attributes -->
            <xs:element ref="prov:label" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element ref="prov:location" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element ref="prov:role" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element ref="prov:type" minOccurs="0" maxOccurs="unbounded"/>
            <xs:any namespace="##other" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>

		  <prov:entity prov:ref="exc:composition1"/>
	      <prov:activity prov:ref="exc:compose1"/>


	

	 */
	/**
	 * This method inserts relationship nodes in the the codebook.
	 * @param xPath XPath where the new relationship node going to be inserted.
	 * @param edge Relationship Object
	 */
	public void addRelationshipNode(String xPath, Edge edge){
		if(StringUtils.isEmpty(edge.getEdgeType())) return;
		NodeList nodes = getNodeList(xPath);		
		if(nodes == null || nodes.getLength() < 1) return;
		Node parent = nodes.item(0);

		if(edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_WAS_DERIVED_FROM) ) {
			/*
			 * 	Example of wasDerivedFrom node
			       <prov:wasDerivedFrom>
      					<prov:generatedEntity prov:ref="dataset2"/>
      					<prov:usedEntity prov:ref="dataset1"/>
    				</prov:wasDerivedFrom>
			 */
			Element newNode = _doc.createElement(Edge.NODE_WAS_DERIVED_FROM);
			Element generatedEntityNode = _doc.createElement(Edge.NODE_GENERATED_ENTITY);
			generatedEntityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			Element usedEntityNode = _doc.createElement(edge.NODE_USED_ENTITY);
			usedEntityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getTarget());
			newNode.appendChild(generatedEntityNode);
			newNode.appendChild(usedEntityNode);
			parent.appendChild(newNode);
		}
		else if(edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_WAS_GENERATED_BY) || edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_CREATED)){
			/*
			 * Example of wasGeneratedBy node
			    <prov:wasGeneratedBy>
			      <prov:entity prov:ref="exc:composition1"/>
			      <prov:activity prov:ref="exc:compose1"/>
			    </prov:wasGeneratedBy>
			*/
			Element newNode = _doc.createElement(Edge.NODE_WAS_GENERATED_BY);
			Element entityNode = _doc.createElement(Entity.NODE_PROV_ENTITY);
			entityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			Element activityNode = _doc.createElement(Activity.NODE_PROV_ACTIVITY);
			activityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getTarget());
			newNode.appendChild(entityNode);
			newNode.appendChild(activityNode);
			parent.appendChild(newNode);
		}
		else if(edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_WAS_ATTRIBUTED_TO) ){
			/*
			 * Example of wasAttributedTo node
			  	    <prov:wasAttributedTo>
	      				<prov:entity prov:ref="exc:chart1"/>
	      				<prov:agent prov:ref="exc:derek"/>
	    			</prov:wasAttributedTo>
 			 */
			Element newNode = _doc.createElement(Edge.NODE_WAS_ATTRIBUTED_TO);
			Element entityNode = _doc.createElement(Entity.NODE_PROV_ENTITY);
			entityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			Element agentNode = _doc.createElement(Agent.NODE_PROV_AGENT);
			agentNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			newNode.appendChild(entityNode);
			newNode.appendChild(agentNode);
			parent.appendChild(newNode);
		}
		else if(edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_ACT_ON_BEHALF_OF) ){
			/*
			 * Example of wasAttributedTo node
			    <prov:actedOnBehalfOf>
			      <prov:delegate prov:ref="exc:derek"/>
			      <prov:responsible prov:ref="exc:chartgen"/>
			    </prov:actedOnBehalfOf>
 			 */
			Element newNode = _doc.createElement(Edge.NODE_ACT_ON_BEHALF_OF);
			Element delegateNode = _doc.createElement(Edge.NODE_DELEGATE);
			delegateNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			Element responsibleNode = _doc.createElement(Edge.NODE_RESPONSIBLE);
			responsibleNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			newNode.appendChild(delegateNode);
			newNode.appendChild(responsibleNode);
			parent.appendChild(newNode);
		}
		else if(edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_WAS_INFORMED_BY) ){
			Element newNode = _doc.createElement(Edge.NODE_WAS_INFORMED_BY);
			Element informedNode = _doc.createElement(Edge.NODE_INFORMED);
			informedNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			Element informantNode = _doc.createElement(Edge.NODE_INFORMANT);
			informantNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			newNode.appendChild(informedNode);
			newNode.appendChild(informantNode);
			parent.appendChild(newNode);
		}
		else if(edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_WAS_ASSOCIATED_WITH) ){
			Element newNode = _doc.createElement(Edge.NODE_WAS_ASSOCIATED_WITH);
			Element activityNode = _doc.createElement(Activity.NODE_PROV_ACTIVITY);
			activityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			Element agentNode = _doc.createElement(Agent.NODE_PROV_AGENT);
			agentNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			newNode.appendChild(activityNode);
			newNode.appendChild(agentNode);
			parent.appendChild(newNode);
		}
		else if(edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_WAS_USED) || edge.getEdgeType().equalsIgnoreCase(Edge.RELATIONSHIP_USED_BY)){
			Element newNode = _doc.createElement(Edge.NODE_WAS_USED);
			Element activityNode = _doc.createElement(Activity.NODE_PROV_ACTIVITY);
			activityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			Element entityNode = _doc.createElement(Entity.NODE_PROV_ENTITY);
			entityNode.setAttribute(Edge.ATTRIBUTE_PROV_REF,edge.getSource());
			newNode.appendChild(activityNode);
			newNode.appendChild(entityNode);
			parent.appendChild(newNode);
		}

	}
	
	/**
	 * This method adds a single Entity Node in the codebook to the path identified by xPath
	 * It is assumed that xPath exists
	 * @param xPath
	 * @param entity
	 */
	public void addEntityNode(String xPath, Entity entity){
		NodeList nodes = getNodeList(xPath);		
		if(nodes == null || nodes.getLength() < 1) return;
		Node parent = nodes.item(0);
		Element newNode = _doc.createElement(Entity.NODE_PROV_ENTITY);
		newNode.setAttribute(Entity.PROV_ATTR_ID,entity.getId());
		
		if(StringUtils.isNotEmpty(entity.getLabel())) {
			Element labelNode = _doc.createElement(Entity.NODE_PROV_LABEL);
			labelNode.setTextContent(entity.getLabel());
			newNode.appendChild(labelNode);
		}
		if(StringUtils.isNotEmpty(entity.getLocation())) {
			Element locationNode = _doc.createElement(Entity.NODE_PROV_LOCATION);
			locationNode.setTextContent(entity.getLocation());
			newNode.appendChild(locationNode);
		}
		if(StringUtils.isNotEmpty(entity.getType())) {
			Element typeNode = _doc.createElement(Entity.NODE_PROV_TYPE);
			typeNode.setTextContent(entity.getType());
			newNode.appendChild(typeNode);
		}
		if(StringUtils.isNotEmpty(entity.getValue())) {
			Element valueNode = _doc.createElement(Entity.NODE_PROV_VALUE);
			valueNode.setTextContent(entity.getValue());
			newNode.appendChild(valueNode);
		}
		if(StringUtils.isNotEmpty(entity.getDate())) {
			Element dateNode = _doc.createElement(Entity.NODE_PROV_DATE);
			dateNode.setTextContent(entity.getDate());
			newNode.appendChild(dateNode);
		}
		if(StringUtils.isNotEmpty(entity.getTitle())) {
			Element titleNode = _doc.createElement(Entity.NODE_PROV_TITLE);
			titleNode.setTextContent(entity.getTitle());
			newNode.appendChild(titleNode);
		}
		parent.appendChild(newNode);		
	}
	
	/**
	 * This method adds a single Agent Node in the codebook to the path identified by xPath
	 * It is assumed that xPath exists
	 * @param xPath
	 * @param agent
	 */
	public void addAgentNode(String xPath, Agent agent){
		NodeList nodes = getNodeList(xPath);		
		if(nodes == null || nodes.getLength() < 1) return;
		Node parent = nodes.item(0);
		Element newNode = _doc.createElement(Agent.NODE_PROV_AGENT);
		newNode.setAttribute(Entity.PROV_ATTR_ID,agent.getId());

		if(StringUtils.isNotEmpty(agent.getLabel())) {
			Element labelNode = _doc.createElement(Agent.NODE_PROV_LABEL);
			labelNode.setTextContent(agent.getLabel());
			newNode.appendChild(labelNode);
		}
		if(StringUtils.isNotEmpty(agent.getLocation())) {
			Element locationNode = _doc.createElement(Agent.NODE_PROV_LOCATION);
			locationNode.setTextContent(agent.getLocation());
			newNode.appendChild(locationNode);
		}
		if(StringUtils.isNotEmpty(agent.getType())) {
			Element typeNode = _doc.createElement(Agent.NODE_PROV_TYPE);
			typeNode.setTextContent(agent.getType());
			newNode.appendChild(typeNode);
		}
		/*
		if(StringUtils.isNotEmpty(agent.getGivenName())) {
			Element givenNameNode = _doc.createElement(Agent.NODE_PROV_GIVEN_NAME);
			givenNameNode.setTextContent(agent.getGivenName());
			newNode.appendChild(givenNameNode);
		}
		if(StringUtils.isNotEmpty(agent.getWorkInfoHomepage())) {
			Element wihpNode = _doc.createElement(Agent.NODE_PROV_WORK_INFO_HOME_PAGE);
			wihpNode.setTextContent(agent.getWorkInfoHomepage());
			newNode.appendChild(wihpNode);
		}
		*/
		parent.appendChild(newNode);		
	}

	/**
	 * This method adds a single Entity Node in the codebook to the path identified by xPath
	 * It is assumed that xPath exists
	 * @param xPath
	 * @param activity
	 */
	public void addActivityNode(String xPath, Activity activity){
		NodeList nodes = getNodeList(xPath);		
		if(nodes == null || nodes.getLength() < 1) return;
		Node parent = nodes.item(0);
		Element newNode = _doc.createElement(Activity.NODE_PROV_ACTIVITY);
		newNode.setAttribute(Entity.PROV_ATTR_ID,activity.getId());

		if(StringUtils.isNotEmpty(activity.getLabel())) {
			Element labelNode = _doc.createElement(Activity.NODE_PROV_LABEL);
			labelNode.setTextContent(activity.getLabel());
			newNode.appendChild(labelNode);
		}
		if(StringUtils.isNotEmpty(activity.getLocation())) {
			Element locationNode = _doc.createElement(Activity.NODE_PROV_LOCATION);
			locationNode.setTextContent(activity.getLocation());
			newNode.appendChild(locationNode);
		}
		if(StringUtils.isNotEmpty(activity.getType())) {
			Element typeNode = _doc.createElement(activity.NODE_PROV_TYPE);
			typeNode.setTextContent(activity.getType());
			newNode.appendChild(typeNode);
		}
		/*
		 * TODO Add StartTime and endTime
		*/
		parent.appendChild(newNode);		
	}

	
	/**
	 * adds a new node to the XML document
	 * @param xpathP the xpath for the parent of the new node
	 * @param target the new node to be added
	 * @param value the value for the new node
	 * @param updateAll whether or not to add this node to every element pointed to by xpathP
	 */
	private void addNode(String xpathP, String target, String value, boolean updateAll){
		NodeList nodes = getNodeList(xpathP);		
		if(nodes == null || nodes.getLength() < 1) return;
		int i = 0;
		do{
			Node prnt = nodes.item(i);
			Element newNode;
			newNode = _doc.createElement(target);
			newNode.setTextContent(value);
			Node nextSibling = getNextSibling(xpathP+"/"+target);
			if(error != null){
				error = null;
				return;
			}else if(nextSibling == null){
				prnt.appendChild(newNode);
			}else{
				prnt.insertBefore(newNode, nextSibling);
			}
			i++;
		}while(updateAll && i < nodes.getLength());
	}
	
	/**
	 * adds an attribute to an existing node
	 * @param xpathP the node to which the attribute will be added
	 * @param attr the attribute the be added
	 * @param value the value of the new attribute
	 * @param updateAll whether or not to add this attribute to all nodes pointed to by xpathP
	 */
	private void addAttribute(String xpathP, String attr, String value, boolean updateAll){
	    NodeList nodes = getNodeList(xpathP);
		Element node;
		
		int stop;
		if(updateAll || nodes.getLength() == 0){
			stop = nodes.getLength();
		}else{
			stop = 1;
		}
		
		for(int i = 0; i < stop; i++){
			if(nodes.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
			node = (Element)nodes.item(i);
			if(!node.hasAttribute(attr))
				node.setAttribute(attr, value);
		}		
	}
	
	/**
	 * gets the next sibling as defined by the schema, so the new element can be placed in the proper ordering of elements
	 * @param xpathString the new element that we will be adding
	 * @return the node before which we should replace the new element, or null if there is no such node
	 */
	private Node getNextSibling(String xpathString){
		String nextSibling = null; //Sibling that is in doc and closest after child
		boolean schemaHasChild = false;//If loop has found child element in schema
		
		String[] xpathArray = xpathString.split("/");
		int xpathLength = xpathArray.length;
		
		String parent = xpathArray[xpathLength - 2];
		String child = xpathArray[xpathLength - 1];
		parent = parent.replaceAll("\\[.+\\]", "");
		child = child.replaceAll("\\[.+\\]", "");
		
		String xpathStringP = xpathString.substring(0,xpathString.lastIndexOf("/"));
		try{
			ArrayList<XSParticle> particles = new ArrayList<XSParticle>();
			Collections.addAll(particles, getComplexType(parent));	
			for (int i = 0; i < particles.size(); i++) {
				XSParticle p = (XSParticle) particles.get(i);
				XSTerm pterm = p.getTerm();
				if(pterm instanceof XSModelGroupDecl){
					//System.out.println("model group decl found");
					XSModelGroupDecl dec = (XSModelGroupDecl)pterm;
					XSModelGroup grp = dec.getModelGroup();
					particles.addAll(i+1, Arrays.asList(grp.getChildren()));
					continue;
				}
				else if(pterm instanceof XSModelGroup){
					//System.out.println("model group found");
					XSModelGroup grp = (XSModelGroup) pterm;
					particles.addAll(i+1, Arrays.asList(grp.getChildren()));
					continue;
				}
				String elementName = pterm.asElementDecl().getName();	
				if(elementName.trim().equals(child.trim())){
					schemaHasChild = true;
				}else if(schemaHasChild && nextSibling == null && hasElement(xpathStringP+"/"+elementName)){
					//If child found, checks to see if next sibling in schema exists in current doc
					nextSibling = elementName;
					//this break might not be allowed; more testing needs to be done!
					break;
				}
			}
			Node node = null;
			if(!schemaHasChild){
				error = "Attempted to insert invalid element. Please check schema";
				return null;
			}
			if(nextSibling == null){
				return null;
			}else{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				try{
					XPathExpression expr = xpath.compile(xpathStringP+"/"+nextSibling);
					NodeList nodes = (NodeList) expr.evaluate(_doc, XPathConstants.NODESET);
					node =  nodes.item(0);
					return node;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return node;
		}catch(NullPointerException e){
			return null;
		}		
	}
	
	/**
	 * updates the version of either a var or a codebook
	 * @param xpathString the version to update
	 */
	private void updateVersion(String xpathString){
		if(!hasElement(xpathString)){
			addReplace(xpathString, "1.0", false, true, false, true);
		}else{
			double version = 0.9;
			try{
				version = Double.parseDouble(getValue(xpathString));
			}catch(NumberFormatException | NullPointerException e){}
			version += 0.1;
			addReplace(xpathString, Double.toString(version), false, true, false, true);
		}	
		addReplace(xpathString+"/@date", new Date().toString(), false, true, false, true);
	}
	
	/**
	 * Adds blank elements to every variable 
	 * @param child the child to add to the vars
	 * TODO: Consider removing this and replacing with addReplace
	 */
	public void addVarBlanks(String child){
		XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;
        try {
			expr = xpath.compile("//codeBook/dataDscr/var");
			NodeList nodes = (NodeList) expr.evaluate(_doc, XPathConstants.NODESET);
			
    		ArrayList<XSParticle> particles = new ArrayList<XSParticle>();
    		Collections.addAll(particles, getComplexType("var"));	
    		
    		
			for (int i = 0; i < nodes.getLength(); i++) {
				Element e = (Element) nodes.item(i);
				//If var doesn't already have child
				if(e.getElementsByTagName(child).getLength() == 0){	
					
					Text text = _doc.createTextNode(""); 
					Node newNode = _doc.createElement(child); 
		        	newNode.appendChild(text);
					boolean schemaHasChild = false;//If cur var has child
					boolean inserted = false;//If child has been added yet
					//For each child of var in schema
					int k = 0;
		    		while (k < particles.size() && !inserted) {
		    			XSParticle p = (XSParticle) particles.get(k);
		    			XSTerm pterm = p.getTerm();
		    			String elementName = pterm.asElementDecl().getName();	

		    			if(elementName.equals(child)){
		    				schemaHasChild = true;
		    			}else if(schemaHasChild){
		    				//If next listed element in schema is present, add new child before
		    				Node nextSibling;
		    				if((nextSibling = e.getElementsByTagName(elementName).item(0)) != null){
		    					e.insertBefore(newNode,nextSibling);
		    					inserted = true;
		    				}
		    			}
		    			k++;
		    		}	    		
		    		if(!inserted){
		    			//No element present that should go after child
			    		if(schemaHasChild){
			    			e.appendChild(newNode);
			    		}else{
			    			//Throw exception
			    			error = "Child " + child + " not found in schema for as child for var";
			    			return;
			    		}
		    		}
				}		
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
  		//System.out.println(docToString());
		
	}
	
	/**
	 * Adds ids to variables without them
	 * @param child the child to add to the vars
	 * TODO: Consider removing this and replacing with addReplace
	 */
	public void addIDs(){
		XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;

        try {
			expr = xpath.compile("//codeBook/dataDscr/var");
			NodeList nodes = (NodeList) expr.evaluate(_doc, XPathConstants.NODESET);
			
    		ArrayList<XSParticle> particles = new ArrayList<XSParticle>();
    		Collections.addAll(particles, getComplexType("var"));	
    		
			for (int i = 0; i < nodes.getLength(); i++) {
				Element e = (Element) nodes.item(i);
				//If var doesn't already have child
				if(e.getAttribute("ID").isEmpty()){
					String name = e.getAttribute("name");
					String id = "v_"+name;
					e.setAttribute("ID", id);
					//System.out.println("adding id "+id+" for "+name);
				}		
			}
		}catch(XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * formats the DOM document into a string
	 * @return the document as a string
	 */
	public String docToString(){
		OutputFormat format = new OutputFormat (_doc); 
		format.setPreserveSpace(false);
		format.setPreserveEmptyAttributes(false);
		StringWriter xmlStrOut = new StringWriter();    
		XMLSerializer serializer = new XMLSerializer(xmlStrOut,format);
		String out = null;
		try {
			serializer.serialize(_doc);
			out= xmlStrOut.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				xmlStrOut.close();
			} catch (IOException e) {}
		}
		return out;		
	}
	
	/**Retrieves documents in native class
	 * @return the document
	 * */
	public Document getDoc()
	{
		return _doc;
	}

	/**
	 * Loads document for instances without _xmlStream set in constructor
	 * @return the document
	 * */
	public void initDoc(String xml)
	{
		_xmlStream = new ByteArrayInputStream(xml.getBytes());
		_doc = loadDoc();
	}
	
	/**Returns error
	 * @return the error string*/
	public String getError(){
		return error;
	}
	
	/**Gets repo quality xml that can be uploaded to BaseX
	 * @return the XML string
	 * */
	public String getRepoXML(){
		return repoXML;
	}

	/**
	 * Gets the value of a node
	 * @param expression the xpath expression specifying the node for which the value will be retrieved
	 * @return the value at the node specified by expression
	 */
	public String getValue(String expression){
		//Really not sure why, but this block of code allows getValue to work
		//Otherwise, getValue always returns null
		Element codebookOld = (Element) ((Document) _doc).getDocumentElement(); 
		Element codebook  = _doc.createElement("codeBook");	
		while (codebookOld.hasChildNodes()) {
			codebook.appendChild(codebookOld.getFirstChild());
		}
		codebookOld.getParentNode().replaceChild(codebook, codebookOld);
		
		boolean isAttr = false;
        String[] levels = expression.split("/");
        String lastLevel = levels[levels.length-1];
        try{
	        if(lastLevel.startsWith("@")){
	        	isAttr = true;
	        	lastLevel = lastLevel.substring(1);
	        } 
	        if(isAttr){ 	
	    		expression = StringUtils.join(Arrays.copyOfRange(levels, 0,levels.length-1),"/");
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile(expression);
				NodeList results =(NodeList)  expr.evaluate(_doc, XPathConstants.NODESET);
				for(int i = 0; i <= results.getLength(); i++){
					Element e = (Element) results.item(i);
					if(e.getAttribute(lastLevel) != null && (!e.getAttribute(lastLevel).equals(""))){
						return e.getAttribute(lastLevel);
					}					
				}
	        }else{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile(expression);
				NodeList results =(NodeList)  expr.evaluate(_doc, XPathConstants.NODESET);
				for(int i = 0; i < results.getLength(); i++){
					Node n = results.item(i);
					if(n.getTextContent() != null){
						return n.getTextContent();
					}
				}
	        }
   		}catch (XPathExpressionException e) {
			e.printStackTrace();
		}catch(NullPointerException e2){}
		
		return null;	
	}
	
	/**
	 * creates a repo quality version of the document
	 * Validates the XML according to the schema provided during construction
	 * @return whether or not the document is valid according to the schema
	 */
	public boolean isValid(){
		String xmlToTest = null;
		StringWriter xmlStrOut = new StringWriter ();    
		try{
			//for codebooks, we need to set these attributes
			//if(this._schemaURI.equals(Config.getSchemaURI())){
			if(this._schemaURI.equals(Config.getInstance().getSchemaURI())){
				
				//When casting to element would sometimes throw java.lang.ClassCastException: org.apache.xerces.dom.DeferredCommentImpl cannot be cast to org.w3c.dom.Element				
				//Element codebookOld = (Element) _doc.getFirstChild();
				Element codebookOld = (Element) ((Document) _doc).getDocumentElement(); 
				Element codebook  = _doc.createElement("codeBook");	
				
				while (codebookOld.hasChildNodes()) {
					codebook.appendChild(codebookOld.getFirstChild());
				}

				codebookOld.getParentNode().replaceChild(codebook, codebookOld);
				OutputFormat format = new OutputFormat (_doc); 
				
				XMLSerializer serializer   = new XMLSerializer (xmlStrOut,format);
				serializer.serialize(_doc);
				repoXML = xmlStrOut.toString();
				
				//Add switch if restricted to use local versions
				//codebook.setAttribute("xmlns", "ddi:codebook:2_5");
				codebook.setAttribute("xmlns:dc", "http://purl.org/dc/terms/");
				codebook.setAttribute("xmlns:dcmitype", "http://purl.org/dc/dcmitype/");
				codebook.setAttribute("xmlns:fn", "http://www.w3.org/2005/xpath-functions");
				codebook.setAttribute("xmlns:ns0", "http://purl.org/dc/elements/1.1/");
				codebook.setAttribute("xmlns:saxon", "http://xml.apache.org/xslt"); 
				codebook.setAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");
				codebook.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				codebook.setAttribute("xsi:schemaLocation", "ddi:codebook:2_5 http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN.P/schemas/codebook.xsd");
				codebook.setAttribute("xmlns:prov", "http://www.w3.org/ns/prov");
				codebook.setAttribute("xmlns:ex", "http://example.com/ns/ex#");
				codebook.setAttribute("xmlns:foaf", "http://xmlns.com/foaf/0.1/");
				codebook.setAttribute("xmlns:tr", "http://example.com/ns/tr#");
				codebook.setAttribute("xmlns:ced2ar", "http://ced2ar.org/ns/core#");
				codebook.setAttribute("xmlns:RePEc", "https://ideas.repec.org/#");
				codebook.setAttribute("xmlns:repeca", "https://ideas.repec.org/e/#");
				codebook.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
				codebook.setAttribute("xmlns:act", "http://ced2ar.org/ns/activities#");
				
				//This is a work around. 
				//Adding the xmlns attr on codebook also adds a blank xmlns to the immediate children			
				format = new OutputFormat(_doc); 
				xmlStrOut = new StringWriter();    
				serializer   = new XMLSerializer (xmlStrOut,format);
				serializer.serialize(_doc);
				
				xmlToTest = xmlStrOut.toString().replaceFirst("<codeBook", "<codeBook xmlns=\"ddi:codebook:2_5\"");
			
				
				//This is a work around. 
				//xhtml elements should be recognized without the namespace specification
				//but for now we will add it so that documents validate.
				xmlToTest = xmlToTest.replaceAll("<ul>", "<xhtml:ul>");
				xmlToTest = xmlToTest.replaceAll("</ul>", "</xhtml:ul>");
				xmlToTest = xmlToTest.replaceAll("<li>", "<xhtml:li>");
				xmlToTest = xmlToTest.replaceAll("</li>", "</xhtml:li>");
				xmlToTest = xmlToTest.replaceAll("<h1>", "<xhtml:h1>");
				xmlToTest = xmlToTest.replaceAll("</h1>", "</xhtml:h1>");
				xmlToTest = xmlToTest.replaceAll("<h2>", "<xhtml:h2>");
				xmlToTest = xmlToTest.replaceAll("</h2>", "</xhtml:h2>");
				xmlToTest = xmlToTest.replaceAll("<h3>", "<xhtml:h3>");
				xmlToTest = xmlToTest.replaceAll("</h3>", "</xhtml:h3>");
				xmlToTest = xmlToTest.replaceAll("<h4>", "<xhtml:h4>");
				xmlToTest = xmlToTest.replaceAll("</h4>", "</xhtml:h4>");
				xmlToTest = xmlToTest.replaceAll("<h5>", "<xhtml:h5>");
				xmlToTest = xmlToTest.replaceAll("</h5>", "</xhtml:h5>");
				xmlToTest = xmlToTest.replaceAll("<h6>", "<xhtml:h6>");
				xmlToTest = xmlToTest.replaceAll("</h6>", "</xhtml:h6>");
				xmlToTest = xmlToTest.replaceAll("<ol>", "<xhtml:ol>");
				xmlToTest = xmlToTest.replaceAll("</ol>", "</xhtml:ol>");
				xmlToTest = xmlToTest.replaceAll("<dl>", "<xhtml:dl>");
				xmlToTest = xmlToTest.replaceAll("</dl>", "</xhtml:dl>");
				xmlToTest = xmlToTest.replaceAll("<pre>", "<xhtml:pre>");
				xmlToTest = xmlToTest.replaceAll("</pre>", "</xhtml:pre>");
				xmlToTest = xmlToTest.replaceAll("<blockquote>", "<xhtml:blockquote>");
				xmlToTest = xmlToTest.replaceAll("</blockquote>", "</xhtml:blockquote>");
				xmlToTest = xmlToTest.replaceAll("<address>", "<xhtml:address>");
				xmlToTest = xmlToTest.replaceAll("</address>", "</xhtml:address>");
				xmlToTest = xmlToTest.replaceAll("<hr>", "<xhtml:hr>");
				xmlToTest = xmlToTest.replaceAll("</hr>", "</xhtml:hr>");
				//might also need to add logic for <div> and <table>, but those elements 
				//also exist in ddi namespace. 
				
				
				// if there is prov, add namespaces for validation
				xmlToTest =  xmlToTest.replaceFirst("<prov:document", "<prov:document "+
	                    " xmlns:dc=\"http://purl.org/dc/terms/\""+
	                    " xmlns:ex=\"http://example.com/ns/ex#\""+
	                    " xmlns:prov=\"http://www.w3.org/ns/prov#\""+
	                    " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""+
	                    " xmlns:tr=\"http://example.com/ns/tr#\""+
	                    " xmlns:xhtml=\"http://www.w3.org/1999/xhtml\""+
	                    " xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\""+
	                    " xmlns:saxon=\"http://xml.apache.org/xslt\""+
	                    " xmlns:ced2ar=\"http://ced2ar.org/ns/core#\""+
	                    " xmlns:file=\"http://ced2ar.org/ns/file#\""+
	                    " xmlns:type=\"http://ced2ar.org/ns/type#\""+
	                    " xmlns:RePEc=\"https://ideas.repec.org/#\""+
	                    " xmlns:repeca=\"https://ideas.repec.org/e/#\""+
	                    " xmlns:ns0=\"http://purl.org/dc/elements/1.1/\""+
	                    " xmlns:exn=\"http://ced2ar.org/ns/external#\""+
	                    " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""+
	                    " xmlns:act=\"http://ced2ar.org/ns/activities#\""+
	                    " xmlns:fn=\"http://www.w3.org/2005/xpath-functions\""+
	                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
				
				
				//System.out.println("[DEBUG]:: "+xmlToTest.substring(0, 400));
			//for comments or bug reports we don't need more attributes
			}else{				
				OutputFormat format = new OutputFormat(_doc);
				XMLSerializer serializer = new XMLSerializer(xmlStrOut, format);
				serializer.serialize(_doc);
				repoXML = xmlStrOut.toString();
				xmlToTest = xmlStrOut.toString();
			}					
		}catch(IOException e){
	    	String msg = "Error parsing XML" +e.getMessage();
		    error = msg;
			return false;
		}finally{
			try {
				xmlStrOut.close();
			} catch (IOException e) {
				xmlStrOut = null;
			}
		}
		
		//The following block performs validation steps	
		URL schemaFile = null;
		try {
			schemaFile = new URL(_schemaURI);
		} catch (MalformedURLException e) {
	    	String msg = "Error fetching schema" +e.getMessage();
		    error = msg;
			return false;
		}
		
	    Source xmlFile = new StreamSource(new java.io.StringReader(xmlToTest));
	    //System.out.println(xmlToTest.substring(0,xmlToTest.length()/2));
	    try {
		    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		    Schema schema = schemaFactory.newSchema(schemaFile);
		    Validator validator = schema.newValidator();
		    validator.validate(xmlFile);
	    }catch (SAXException e) {
	    	e= (SAXParseException) e;
	    	String msg = "Invalid XML " + e.getMessage();
		    error = msg;
		    System.out.println("[PARSING ERROR]:: "+error);
		    return false;
	    }catch (IOException e){
	    	String msg = "Internal Parsing Error During Validation";
		    error = msg;
	    	return false;	
	    }
    	return true;
    } 	
	

	
	/**
	 * Removes restricted metadata for release
	 * Variables need access attribute that equals release to be released
	 * Sub elements of variables can either have no access tag, or an access equal to released
	 */
	public void removeRestricted(List<String> include, boolean namespace){
		//Really not sure why, but this block of code allows getValue to work
		//Otherwise, getVlaue always returns null
		Element codebookOld = (Element) ((Document) _doc).getDocumentElement(); 
		Element codebook  = _doc.createElement("codeBook");	
		while (codebookOld.hasChildNodes()) {
			codebook.appendChild(codebookOld.getFirstChild());
		}
		codebookOld.getParentNode().replaceChild(codebook, codebookOld);
		
		String expression = "/codeBook/dataDscr/var";
		//If no access levels included, delete all vars
		if(include.size() == 0){
			deleteNode(expression,true);
	
		}else{
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr;
			try {
				expr = xpath.compile(expression);
				NodeList vars =(NodeList)  expr.evaluate(_doc, XPathConstants.NODESET);
				for(int i = 0; i < vars.getLength(); i++){
					Element var = (Element) vars.item(i);
					if(var.hasAttribute("access") && include.contains(var.getAttribute("access"))){
						deleteRestrictedChildren(var, include);
					}else{
						String deleteExpression = expression + "[@name='"+var.getAttribute("name")+"']";
						deleteNode(deleteExpression,false);
					}
				}							
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}	
		}
		
		//Add Namespace info
		if(namespace){
			codebook.setAttribute("xmlns", "ddi:codebook:2_5");
			codebook.setAttribute("xmlns:dc", "http://purl.org/dc/terms/");
			codebook.setAttribute("xmlns:dcmitype", "http://purl.org/dc/dcmitype/");
			codebook.setAttribute("xmlns:fn", "http://www.w3.org/2005/xpath-functions");
			codebook.setAttribute("xmlns:ns0", "http://purl.org/dc/elements/1.1/");
			codebook.setAttribute("xmlns:saxon", "http://xml.apache.org/xslt"); 
			codebook.setAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");
			codebook.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			codebook.setAttribute("xsi:schemaLocation", "ddi:codebook:2_5 http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN.P/schemas/codebook.xsd");
			codebook.setAttribute("xmlns:prov", "http://www.w3.org/ns/prov");
			codebook.setAttribute("xmlns:ex", "http://example.com/ns/ex#");
			codebook.setAttribute("xmlns:foaf", "http://xmlns.com/foaf/0.1/");
			codebook.setAttribute("xmlns:tr", "http://example.com/ns/tr#");
			codebook.setAttribute("xmlns:ced2ar", "http://ced2ar.org/ns/core#");
			codebook.setAttribute("xmlns:RePEc", "https://ideas.repec.org/#");
			codebook.setAttribute("xmlns:repeca", "https://ideas.repec.org/e/#");
			codebook.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
			codebook.setAttribute("xmlns:act", "http://ced2ar.org/ns/activities#");
		}
		
		//Removes whitespace left behind
		XPath xp = XPathFactory.newInstance().newXPath();
		NodeList nl;
		try {
			nl = (NodeList) xp.evaluate("//text()[normalize-space(.)='']", _doc, XPathConstants.NODESET);
			for (int i=0; i < nl.getLength(); ++i) {
			    Node node = nl.item(i);
			    node.getParentNode().removeChild(node);
			}
		}catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Recursively removes elements with access attribute other than released
	 * Intended to run at variable level
	 * @param Node parent
	 */
	private void deleteRestrictedChildren(Node parent, List<String> include){
		if(parent.hasChildNodes()){
			NodeList children = parent.getChildNodes();
			for(int i = 0; i < children.getLength(); i++){
				Node c = children.item(i);
				try{
					Element child = (Element) c;
					if(child.hasAttribute("access")){
						String currentAccs = child.getAttribute("access");
						if(!include.contains(currentAccs)){
							parent.removeChild(c);
						}
					}else{
						deleteRestrictedChildren(c,include);
					}
				}catch(ClassCastException|NullPointerException e){}
			}
		}
	}
	
	/**
	 *Adds namespace for export 
	 */
	public void addNamespace(){
		//Really not sure why, but this block of code allows getValue to work
		//Otherwise, getVlaue always returns null
		Element codebookOld = (Element) ((Document) _doc).getDocumentElement(); 
		Element codebook  = _doc.createElement("codeBook");	
		while (codebookOld.hasChildNodes()) {
			codebook.appendChild(codebookOld.getFirstChild());
		}
		codebookOld.getParentNode().replaceChild(codebook, codebookOld);
		
		//Order is not preserved, so this will always show a diff in git
		codebook.setAttribute("xmlns", "ddi:codebook:2_5");
		codebook.setAttribute("xmlns:dc", "http://purl.org/dc/terms/");
		codebook.setAttribute("xmlns:dcmitype", "http://purl.org/dc/dcmitype/");
		codebook.setAttribute("xmlns:fn", "http://www.w3.org/2005/xpath-functions");
		codebook.setAttribute("xmlns:ns0", "http://purl.org/dc/elements/1.1/");
		codebook.setAttribute("xmlns:saxon", "http://xml.apache.org/xslt"); 
		codebook.setAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");
		codebook.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		codebook.setAttribute("xsi:schemaLocation", "ddi:codebook:2_5 http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN.P/schemas/codebook.xsd");
		codebook.setAttribute("xmlns:prov", "http://www.w3.org/ns/prov");
		codebook.setAttribute("xmlns:ex", "http://example.com/ns/ex#");
		codebook.setAttribute("xmlns:foaf", "http://xmlns.com/foaf/0.1/");
		codebook.setAttribute("xmlns:tr", "http://example.com/ns/tr#");
		codebook.setAttribute("xmlns:ced2ar", "http://ced2ar.org/ns/core#");
		codebook.setAttribute("xmlns:RePEc", "https://ideas.repec.org/#");
		codebook.setAttribute("xmlns:repeca", "https://ideas.repec.org/e/#");
		codebook.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
		codebook.setAttribute("xmlns:act", "http://ced2ar.org/ns/activities#");
		
	}
	
	/**
	 * Removes all resource for garbage collection
	 */
	public void close(){
		try {
			if(_xmlStream != null) _xmlStream.close();
		} catch (IOException e) {}
	}
}