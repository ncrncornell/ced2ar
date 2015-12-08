package edu.ncrn.cornell.ced2ar.web.classes;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 *Parses XML and DATA from API
 *Can transform XML with XSLT, and/or extract specific element/attribute values
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Parser {
	
	private static final Logger logger = Logger.getLogger(Parser.class);
	
	private String RAW_DATA;
	private String DATA;
	private String PATH;

	/**
	 *For use without XSLT transformation
	 * @param d String the data
	 */
	public Parser(String d){
		this.RAW_DATA = d;
		this.DATA = d;
	}
	
	/**
	 *For use with XSLT
	 * @param xml String the raw data
	 * @param p String path to XSLT file
	 **/
	public Parser(String xml, String p){
		this.RAW_DATA = xml;
		this.PATH = p;
		parseXML();
	}
	
	/**
	 *For use with XSLT, and XML that needs to be sanitized
	 * @param xml String the raw data
	 * @param p String path to XSLT file
	 * @param cleanLevel int level of cleaning that needs to be done
	 **/
	public Parser(String xml, String p, int cleanLevel){
		xml = clean(xml,cleanLevel);
		this.RAW_DATA = xml;
		this.PATH = p;
		parseXML();
	}
	
	private String clean(String xml, int cleanLevel){
		switch(cleanLevel){
			case 1:
				xml = xml.replaceAll("<br|<HR.*?>", "");
			break;
			default:
				 xml = xml.replace("xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"", "");
				 xml = xml.replace("xhtml:", "");
				 xml = xml.replace("</ExtLink>", "</a>");
				 xml = xml.replace("<ExtLink URI", " <a href");
				 xml = xml.replace("<p>", "<p class=\"lb2\">");
				 xml = xml.replace("</emph>", "</em>");
				 xml = xml.replace("<emph>", " <em>");		 
				 xml = xml.trim().replaceAll(" +", " ");
				 xml = xml.replace(" .", ".");
				 xml = xml.replace(" ,", ",");
				 xml = xml.replace(" )", ")");
			break;
		}
		return xml;
	}

	/**
	 *Parses out variables for display with XSLT*
	 **/
	private void parseXML(){
		StringReader input = new StringReader(this.RAW_DATA);
		StringWriter output = new StringWriter();
		TransformerFactoryImpl tFac = new net.sf.saxon.TransformerFactoryImpl();
		
		try {
			File xsl = new File(this.PATH);
			Transformer trn = tFac.newTransformer(new StreamSource(xsl));
			trn.transform(new StreamSource(input), new StreamResult(output));
			this.DATA = output.toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Formats XML to be used in displaytag interface, but parses entire list of results in limited list.
	 * @param sort index of field to sort by
	 * @return the formatted data
	 */
	public List<String[]> getDisplayTagDataAccs(int sort, boolean isReverse){
		if(sort < 0 || sort  > 3){
			sort = 0;
		}
		
		Map<String, String[]> sorted = null;
		List<String[]> out = null; 
		try{
			if(isReverse){
				sorted = new TreeMap<String, String[]>(Collections.reverseOrder());
			}else{
				sorted = new TreeMap<String, String[]>();
			}			
			out = new ArrayList<String[]>(); 
			String strippedData = StringEscapeUtils.unescapeHtml(this.DATA);
			
			String vars[] = strippedData.split(";");
			if(vars.length == 0)
				return null;
		
			for(String var : vars){
				if(!var.trim().equals("")){
					try{	
						String[] fields = var.split(",");	
						String accessLevel = "";
						String label = "";
						String sortIndex = "-"+fields[0];
						try{
							 sortIndex  = fields[sort].trim().toLowerCase()+fields[0];
							 if(sortIndex == null || sortIndex.equals("")){
								 sortIndex = "-"+fields[0];
							 }
						}catch(ArrayIndexOutOfBoundsException e){}	
						try{
							accessLevel = fields[2];
						}catch(ArrayIndexOutOfBoundsException e){}
						try{
							label = fields[1];
						}catch(ArrayIndexOutOfBoundsException e){}	
						
						sorted.put(sortIndex, new String[] {fields[0].trim(),label,accessLevel});
					}catch(ArrayIndexOutOfBoundsException e){
						logger.error("Array out of bounds for value of "+var);
					}
				}
			}

			//Returns subset needed to show on page
			for(Map.Entry<String, String[]> e: sorted.entrySet()){
					out.add(e.getValue());		
			}
		}finally{
			sorted.clear();
		}
		return out;
	}
	
	/**
	 * Formats XML to be used in displaytag interface for vargrp editing
	 * @param sort index of field to sort by
	 * @return the formatted data
	 */
	public List<String[]> getDisplayTagDataVarGrp(int sort, boolean isReverse){
		if(sort < 0 || sort  > 3){
			sort = 0;
		}
		
		Map<String, String[]> sorted = null;
		List<String[]> out = null; 
		try{
			if(isReverse){
				sorted = new TreeMap<String, String[]>(Collections.reverseOrder());
			}else{
				sorted = new TreeMap<String, String[]>();
			}			
			out = new ArrayList<String[]>(); 
			String strippedData = StringEscapeUtils.unescapeHtml(this.DATA);
			
			String vars[] = strippedData.split(";");
			if(vars.length == 0)
				return null;
		
			for(String var : vars){
				if(!var.trim().equals("")){
					try{	
						String[] fields = var.split(",");	
						String label = "";
						String sortIndex = "-"+fields[0];
						try{
							 sortIndex  = fields[sort].trim().toLowerCase()+fields[0];
							 if(sortIndex == null || sortIndex.equals("")){
								 sortIndex = "-"+fields[0];
							 }
						}catch(ArrayIndexOutOfBoundsException e){}	
						try{
							label = fields[2];
						}catch(ArrayIndexOutOfBoundsException e){}	
						
						sorted.put(sortIndex, new String[] {fields[0].trim(),fields[1].trim(),label,fields[3].trim()});
					}catch(ArrayIndexOutOfBoundsException e){
						logger.error("Array out of bounds for value of "+var);
					}
				}
			}

			//Returns subset needed to show on page
			for(Map.Entry<String, String[]> e: sorted.entrySet()){
					out.add(e.getValue());		
			}
		}finally{
			sorted.clear();
		}
		return out;
	}

	/**
	 * Formats XML to be used in displaytag interface, but parses entire list of results in limited list
	 * @param start index to start parsing the data
	 * @param stop index to stop parsing the data
	 * @return the formatted data
	 */
	public List<String[]> getDisplayTagDataLimit(int start, int stop){
		start--;
		stop--;
		List<String[]> out = new ArrayList<String[]>(); 
		try{
			String strippedData = StringEscapeUtils.unescapeHtml(this.DATA);
			String vars[] = strippedData.split(";");
			for(int i = start;i <= stop;i++){
				String[] fields = vars[i].split(",");		
				out.add(new String[] {fields[0].trim(),fields[1],fields[2],fields[3],fields[4]});
			}
		}catch(ArrayIndexOutOfBoundsException|NullPointerException e){}//No results
		return out;
	}
	
	/**
	 * Formats XML to be used in displaytag interface, but parses entire list of results in limited list.
	 * @param start index to start parsing the data				
	 * @param stop index to stop parsing the data
	 * @param sort index of field to sort by
	 * @return the formatted data
	 */
	public List<String[]> getDisplayTagDataLimit(int start, int stop, int sort, boolean isReverse){
		if (sort == -1){
			return getDisplayTagDataLimit(start,stop);//No sorting, return scored order
		}	
		else if(sort < 0 || sort  > 4){
			sort = 0;
		}
		Map<String, String[]> sorted = null;
		List<String[]> out = null; 
		try{
			if(isReverse){
				sorted = new TreeMap<String, String[]>(Collections.reverseOrder());
			}else{
				sorted = new TreeMap<String, String[]>();
			}			
			out = new ArrayList<String[]>(); 
			String strippedData = StringEscapeUtils.unescapeHtml(this.DATA);
			String vars[] = strippedData.split(";");
			if(vars.length == 0)
				return null;
		
			for(String var : vars){
				String[] fields = var.split(",");	
				if(fields.length  < 2)
					break;
				String sortIndex  = fields[sort].trim().toLowerCase();
				sortIndex+=fields[3]+fields[4];//Map will otherwise remove variables with the same name cross codebook
				sorted.put(sortIndex, new String[] {fields[0].trim(),fields[1],fields[2],fields[3],fields[4]});
			}

			int i = 1;
			//Returns subset needed to show on page
			for(Map.Entry<String, String[]> e: sorted.entrySet()){
				if(i > stop){
					break;
				}else if(i >= start){
					out.add(e.getValue());		
				}			
				i++;
			}
		}finally{
			sorted.clear();
		}
		return out;
	}
	
	
	/**				
	 *Returns content from specific xpath statement
	 * @param path String the xpath statement
	 * @return String the value of the node located where the xpath specifies
	 */
	public String getValue(String path){
		InputSource source = new InputSource(new StringReader(this.RAW_DATA));	
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(source);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(path);
			NodeList result =(NodeList)  expr.evaluate(doc, XPathConstants.NODESET);
			Element e = (Element) result.item(0);
			try{
				return e.getTextContent();
			}catch(NullPointerException e2){ 
				return null;
			}			 		
		} catch (IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}		
		return null;		
	}
	
	/**
	 * Returns an array of values from a plural path
	 * @param path String the xpath location of the desired nodes
	 * @return ArrayList<String> the values at each of the nodes pointed to by the xpath
	 **/
	public ArrayList<String> getValues(String path){
		InputSource source = new InputSource(new StringReader(this.DATA));	
		ArrayList<String> out = new ArrayList<String>();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(source);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(path);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList vars = (NodeList) result;
			for (int i = 0; i < vars.getLength(); i++) {
				Element e = (Element) vars.item(i);
				out.add(e.getTextContent());
			}    
		} catch (IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			return null;
		}
		return out;
	}
		
	/**
	 *Returns content from specific xpath statement
	 * @param path String xpath pointing to the desired node
	 * @param attr String the attribute to obtain
	 * @return String the value of the attribute at the specified node
	 **/
	public String getAttrValue(String path, String attr){
		InputSource source = new InputSource(new StringReader(this.RAW_DATA));	
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(source);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(path);
			NodeList result =(NodeList)  expr.evaluate(doc, XPathConstants.NODESET);
			Element e = (Element) result.item(0);
			try{
				return e.getAttribute(attr);
			}catch(NullPointerException e2){ 
				return null;
			}	 		
		} catch (IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}		
		return null;		
	}
	/**
	 * Parses either element value or attribute value
	 * @param path
	 * @return
	 */
	public String getValue2(String p){
		if(p.contains("//")) return null;//No double slashes allowed
		String[] path = p.split("/");
		if(path[path.length-1].contains("@")){
			String basePath = StringUtils.join(Arrays.copyOfRange(path, 0, path.length-1),"/");
			String attr = path[path.length-1].replaceAll("[^A-Za-z0-9]", "");
			return getAttrValue(basePath, attr);
		}		
		return getValue(p);
	}
	
	/**
	 * Returns content from specific xpath statement without stripping out structure
	 * @param path the xpath specifying the desired element
	 * @return the content of the specified element
	 */
	public String getNode(String path){
		InputSource source = new InputSource(new StringReader(this.RAW_DATA));	
		DocumentBuilder builder;
		Document doc;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.parse(source);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(path);
			NodeList result =(NodeList)  expr.evaluate(doc, XPathConstants.NODESET);
			Node n = result.item(0);
			StringWriter writer = null;
			Transformer transformer;
			
			try{
				NodeList children = n.getChildNodes();		
				writer = new StringWriter();
				transformer  = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty(OutputKeys.INDENT,"yes");
				for (int i = 0; i < children.getLength(); i++) {
			       transformer.transform(new DOMSource(children.item(i)), new StreamResult(writer));
			    }				
				String xml = writer.toString();			
				return xml;
			}catch(NullPointerException | TransformerFactoryConfigurationError | TransformerException e2){ 
				return null;
			}finally{
			
				writer.close();
			}
			 		
		}catch(IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
			logger.error(e.getMessage() + " - on content: ");
			logger.error(this.RAW_DATA);
		}
		return null;		
	}
	
//Get Field Functions
	
	/**
	 *Returns transformed DATA
	 * @return String
	 */
	public String getData(){
		//Renders apostrophe
		this.DATA = this.DATA.replaceAll("&amp;apos;", "'");
		return this.DATA;
	}
	
	/**
	 *Returns raw DATA
	 * @return String
	 */
	public String getRawData(){
		return this.RAW_DATA;
	}
	
//Utility Functions
	
	/**
	 *Checks if xml contains any specific nodes at least once
	 * @param xml String the xml to check
	 * @param node String the node to check for
	 * @return boolean whether or not the xml contains the node
	 */
	public static boolean containsNode(String xml, String node){
		InputSource source = new InputSource(new StringReader(xml));	
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(source);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(node);

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList vars = (NodeList) result;
			if(vars.getLength() == 0)
					return false;
		} catch (IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 *Parses out variables for display with XSLT in static way
	 * @param d String the xml data
	 * @param p String the xsl transform
	 * @return String the newly formatted xml
	 */
	public static String transXml(String d, String p){
		StringReader input = new StringReader(d);
		StringWriter output = new StringWriter();
		TransformerFactoryImpl tFac = new net.sf.saxon.TransformerFactoryImpl();
		try {
			File xsl = new File(p);
			Transformer trn = tFac.newTransformer(new StreamSource(xsl));
			trn.transform(new StreamSource(input), new StreamResult(output));
			return output.toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}