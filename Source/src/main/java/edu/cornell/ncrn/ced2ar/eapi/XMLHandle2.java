package edu.cornell.ncrn.ced2ar.eapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * TODO: This is a test class for now, but will eventually replace XMLHandle
 * @author NCRN Project Team
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 *
 */
public class XMLHandle2 {
	
	private static final Logger _logger = Logger.getLogger(XMLHandle2.class);
	private Document _doc;
	private DocumentBuilder _builder;
	private boolean _hasChanged = false;
	
	public XMLHandle2(String rawXML) throws ParserConfigurationException, 
	SAXException, IOException{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();	
		try {
			_builder = domFactory.newDocumentBuilder();
			_doc = _builder.parse(new ByteArrayInputStream(rawXML.getBytes()));		
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			//TODO: log issue
			throw e;
		}
	}
	
	/**
	 * Adds a new variable to a codebook
	 * @param baseHandle
	 * @param version
	 * @param varName
	 * @param xml
	 * @return
	 */
	public void addVar(String varXML){
		addVars(new String[] {varXML});
	}
	
	/**
	 * Add several variables to a codebook
	 * @param baseHandle
	 * @param version
	 * @param vars
	 */
	public void addVars(String[] vars){
		try {
			for(String var : vars) addVarSub(var);
		} catch (SAXException | IOException | XPathExpressionException e) {
			e.printStackTrace();
			//TODO: Log errors
		}
	}
	
	/**
	 * Adds a new var to the codebook, checking to make sure name and ID are unique
	 * @param varXML
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	private void addVarSub(String varXML) 
	throws SAXException, IOException, XPathExpressionException{
		Element node = _builder
		.parse(new ByteArrayInputStream(varXML.getBytes()))
		.getDocumentElement();
	
		if(node.getAttribute("ID") != null){
			String newID = node.getAttribute("ID");
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList matchingIDs = (NodeList) xpath.evaluate("/codeBook/dataDscr/var[@ID='"+newID+"']",
			_doc.getDocumentElement(), XPathConstants.NODESET);
					
			if(matchingIDs.getLength() > 0){
				//System.out.println("Var ID '"+newID+"' already used ");
				return;
			}
		}
		
		String newName = node.getAttribute("name");
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList matchingIDs = (NodeList) xpath.evaluate("/codeBook/dataDscr/var[@name='"+newName+"']",
		_doc.getDocumentElement(), XPathConstants.NODESET);
				
		if(matchingIDs.getLength() > 0){
			//System.out.println("Var name '"+newName+"' already used ");
			return;
		}
		
		Node importedNode = _doc.importNode(node, true);
		_doc.getElementsByTagName("dataDscr").item(0).appendChild(importedNode);
		setHasChanged(true);
	}
	
	public void removeVars(String[] varNames){
		for(String varName : varNames){
			try {
				removeVar(varName);
			} catch (XPathExpressionException e) {
				//TODO: log
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Removes a variable from the codebook
	 * @param varName
	 * @throws XPathExpressionException
	 */
	public void removeVar(String varName) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node removedVar = ((NodeList) xpath.evaluate("/codeBook/dataDscr/var[@name='"+varName+"']",
		_doc.getDocumentElement(), XPathConstants.NODESET)).item(0);		
		//System.out.println("Removing "+varName);
		_doc.getElementsByTagName("dataDscr").item(0).removeChild(removedVar);
		setHasChanged(true);
	}
	
	/**
	 * Gets a variable's xml as a String
	 * @param varName
	 * @return
	 */
	public String getVar(String varName){
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			Node var = ((NodeList) xpath.evaluate("/codeBook/dataDscr/var[@name='"+varName+"']",
			_doc.getDocumentElement(), XPathConstants.NODESET)).item(0);

			StringWriter writer = new StringWriter();	
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(var), new StreamResult(writer));
			//Still returns xmlns...
			return writer.toString();
				
		} catch (XPathExpressionException | TransformerFactoryConfigurationError 
		| TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return null;
	}	

//Utilities	
	
	/**
	 * Outputs document as a String
	 */
	public String toString(){	
		StringWriter xmlOut = null;
		try {
			OutputFormat format = new OutputFormat (_doc); 
			format.setPreserveSpace(false);
			format.setPreserveEmptyAttributes(false);
			xmlOut = new StringWriter();    
			XMLSerializer serializer = new XMLSerializer(xmlOut,format);
			serializer.serialize(_doc);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				xmlOut.close();
			} catch (IOException e) {
				xmlOut = null;
			}
		}
		return xmlOut.toString();
	}
	
	public void close(){
		//TODO: Necessary to implement?
	}
	
//Mutators
	
	public boolean getHasChanged(){
		return _hasChanged;
	}
	
	private void setHasChanged(boolean b){
		_hasChanged = b;
	}
}
