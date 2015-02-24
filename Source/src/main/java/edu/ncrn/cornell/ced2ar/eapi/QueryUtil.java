package edu.ncrn.cornell.ced2ar.eapi;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;

/**
 * Prepackaged queries and preparped functions that access BaseX and PgSQL
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry, Venky Kambhampaty, Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */

public class QueryUtil {

	 private static final Logger logger = Logger.getLogger(QueryUtil.class);
	
	//New Indexing functions
	
	/**
	 * Inserts a new codebook if codebook isn't present
	 * @param handle the codebook to insert
	 */
	public static void insertCodebookIndex(String handle, String label){
		String xquery = "insert node element codeBook{"
	    +" attribute handle {'"+handle+"'},"
	    +" attribute label {'"+label+"'}"
	    +"} into collection('index')//codeBooks";	
		BaseX.write(xquery);
	}
	
	/**
	 * Inserts a new codebook version
	 * @param handle String the codebook handle 
	 * @param label String a short description of the handle
	 * @param version String the codebook version
	 * @param shortName String the short name of the codebook
	 * @param fullName String the full name of the codebook
	 */
	//TODO: Make new version compatible
	public static void insertCodebookVersionIndex(String baseHandle, String version, String shortName, String fullName){
		String xquery = "insert node element version{"
	    +" attribute v {'"+version+"'},"
	    +" element fullName {'"+fullName+"'},"
	    +" element shortName {'"+shortName+"'}"
	    +"} into collection('index')//codeBooks/codeBook[@handle='"+baseHandle+"']";	
		BaseX.write(xquery);
	}	
	/**
	 * Updates an existing label
	 * @param handle
	 * @param label
	 */
	public static void updateLabel(String baseHandle, String label){
		String xquery = "for $c in collection('index')//codeBooks/codeBook"
			+" where $c[@handle='"+baseHandle+"']"
			+" return replace value of node $c/@label with '"+label+"'";
		BaseX.write(xquery);
	}	
	
	/**
	 * Checks to see if the index contains a codebook
	 * @param handle the codebook to check
	 * @return whether or not the index contains the specified codebook
	 */
	public static boolean hasCodebookIndex(String handle){		
		String xquery ="let $cb := collection('index')/codeBooks/codeBook[@handle='"+handle+"']"
		+"return count($cb) ";
		
		try{
			String response = BaseX.query(xquery);
			if(Integer.parseInt(response.trim()) == 0){
				return false;
			}
		}catch(NullPointerException e){
			return false;
		}
		return true;
	}
	
	/**
	 *Checks to see if codebook with a specific version is already listed in index 
	 *@param handle the codebook to check
	 *@param version the version to check 
	 *@return whether or not the index lists the codebook
	 **/
	public static boolean hasVersionIndex(String handle, String version){		
		String xquery ="let $cb := collection('index')/codeBooks/codeBook[@handle='"+handle+"']"
		+"/version[@v='"+version+"'] return count($cb) ";
		
		try{
			String response = BaseX.query(xquery);
			if(Integer.parseInt(response.trim()) == 0){
				return false;
			}
		}catch(NullPointerException e){
			return false;
		}
		return true;
	}
	
	/**
	 * Checks to see if codebook fullHandle given
	 * @param fullHandle the full handle to check
	 * @return whether or not the index contains the given full handle
	 */
	public static boolean hasVersionIndex2(String fullHandle){		
		String xquery ="for $c in collection('index')/codeBooks/codeBook"
		+ " for $v in $c/version"
		+ " let $h := string-join(($c/@handle,$v/@v),'')"
		+ " where $h = '"+fullHandle+"'"
		+"  return count($h) ";
		
		try{
			String response = BaseX.query(xquery);
			try{
				if(Integer.parseInt(response.trim()) == 0){
					return false;
				}
			}catch(NumberFormatException e){
				return false;//No codebooks, cannot parse response
			}
		}catch(NullPointerException e){
			return false;
		}
		return true;
	}
	
	public static void updateFullName(String baseHandle, String version, String title){
		String xquery = "let $c := collection('index')/codeBooks/"
		+"codeBook[@handle='"+baseHandle+"']/version[@v='"+version+"']"
		+" return replace value of node $c/fullName with '"+title+"'";
		BaseX.write(xquery);
	}
	
	/**
	 * Returns all handles in the index, in the form basehandle.version; i.e. ssb.5
	 * @return all full handles as an array of Strings
	 */
	public static String[] getFullHandles(){
		String xquery ="for $c in collection('index')/codeBooks/codeBook"
				+ " for $v in $c/version"
				+ " let $h := string-join(($c/@handle,$v/@v),'.')"
				+"  return $h ";
		return BaseX.query(xquery).split(" ");
	}
	
	/**
	 * Deletes a codebook (with version) from the index
	 * @param handle the codebook to delete
	 * @param version the version of the codebook to delete
	 */
	public static void deleteVersion(String handle, String version){
		String xquery = "for $v in collection('index')/codeBooks/codeBook[@handle='"+handle+"']/version "
				+ "where $v/@v=\""+version+"\" return delete node $v";
		BaseX.write(xquery);
		deleteBlankCodebooks();
	}
	
	/**
	 * Deletes codebooks that have no version
	 */
	public static void deleteBlankCodebooks(){
		String xquery = "for $c in collection('index')/codeBooks/codeBook where count($c/version) < 1 return delete node $c";
		BaseX.write(xquery);
	}
	
	/**
	 * Sets the use of a codebook version
	 * @param baseHandle String base handle of the codebook
	 * @param version String version of the codebook
	 * @param type String either "default", "deprecated" or "supported". An invalid type will default to "supported"
	 */
	public static void setUse(String baseHandle, String version, String type){	
		String xquery = "for $v in collection('index')/codeBooks/codeBook[@handle='"+baseHandle+"']/version[@v='"+version
				+"'] return if($v/@use) then replace value of node $v/@use with ";
		switch(type){
			//Constant of default attribute is that only one version can be the default
			case "default":
				xquery = "for $v in collection('index')/codeBooks/codeBook[@handle='"+baseHandle+"']/version"
				+" return if($v/@use) then if($v[@v = '"+version+"']) then"
				+" replace value of node $v/@use with 'default'"
				+" else replace value of node $v/@use with 'supported'"
				+" else if($v[@v = '"+version+"']) then insert node attribute use {'default'} into $v[@v = '"+version+"']"
				+" else insert node attribute use {'supported'} into $v";	
				break;
			case "deprecated":
				xquery+=" 'deprecated' else insert node attribute use {'deprecated'} into $v";
				break;
			default:
				xquery+=" 'supported' else insert node attribute use {'supported'} into $v";
				break;
		}
		BaseX.write(xquery);					 
	}
	
//git methods	
	
	/**
	 * Inserts a codebook level commit
	 * @param hash
	 * @param timeStamp
	 * @param handles
	 */
	protected static void insertCommit(String hash, String timeStamp, List<String> handles){
		//TODO: Maybe add user info with commit in BaseX
		//+" element user {'"+user+"'}"
		String codebooks = "";
		for(String handle:handles){
			codebooks += ", element codeBook {attribute handle {'"+handle+"'}}";
		}		
		String xquery = "insert node element commit {"
		+" attribute hash {'"+hash+"'},"
		+" attribute timestamp {'"+timeStamp+"'}"
		+codebooks
		+"}  into collection('git/git')/git/commits";
		BaseX.write(xquery);
	}
	
	/**
	 * Inserts variable level commit info
	 * @param hash
	 * @param handleVars
	 */
	protected static void insertVarCommit(String hash, Hashtable<String,List<String>> handleVars){
		Enumeration<String> enumration = handleVars.keys();
		while(enumration.hasMoreElements()) {
		    String handle = enumration.nextElement();
		    List<String> vars = handleVars.get(handle);
		    String varInsert = "";
		    for(int i = 0; i < vars.size(); i++){
				if(i != 0){
					varInsert+=", ";
				}
				varInsert += "element var {attribute name {'"+vars.get(i)+"'}}";
				
				String xquery = " for $c in collection('git')/git/commits"
					+" let $commit := $c/commit[@hash='"+hash+"']"
					+" let $codebook := $commit/codeBook[@handle='"+handle+"'] return"
					+" if($codebook) then"
					+"	insert nodes ("
					+	varInsert
					+"	) into $codebook"
					+" else insert node element codeBook {"
					+"	attribute handle {'"+handle+"'},"
					+ 	varInsert
					+" }  into $commit";
				logger.debug("Inserting var commit");
				logger.debug(xquery);
				BaseX.write(xquery);
			}  
		}	
	}
	
	/**
	 * Retrieves commits related to a codebook
	 * @param handle String codebook handle
	 * @param type String if equals hash, only returns hash, other
	 * @return
	 */
	public static String getCommits(String handle, String type){
		String xquery = "for $c in collection('git/git')/git/commits/commit"
		+" where $c/codeBook[@handle='"+handle+"']"
		+" order by number($c/@timestamp) descending";
		
		switch(type){
			case "hash":
				xquery+=" return string-join((data($c/@hash),' '))";
			break;
			default:
				xquery+=" return string-join(string-join((data($c/@hash),data($c/@timestamp)),'.'),' ')";
			break;
		}

		return BaseX.getXML(xquery);
	}
	
	/**
	 * Retrieves commits related to a variable
	 * @param handle String codebook handle
	 * @param var String variable name
	 *  * @param type String if equals hash, only returns hash, other
	 * @return
	 */
	public static String getVarCommits(String handle, String var, String type){	
		String xquery = "for $c in collection('git/git')/git/commits/commit"
		+" where $c/codeBook[@handle='"+handle+"']/var[@name='"+var+"']"
		+" order by number($c/@timestamp) descending";	
		switch(type){
		case "hash":
			xquery+=" return string-join((data($c/@hash),' '))";
		break;
		default:
			xquery+=" return string-join(string-join((data($c/@hash),data($c/@timestamp)),'.'),' ')";
		break;
	}
		
		return BaseX.getXML(xquery);
	}
	
	/**
	 * Caches message about an edit to be included in next commit
	 * @param handle
	 * @param type
	 * @param xpath
	 */
	public static void insertPending(String handle, String type, String xpath, String user){
		xpath = xpath.replace("'", "\"");
		String xquery = "insert node element edit {"
		+" attribute handle {'"+handle+"'},"
		+" attribute type {'"+type+"'},"	
		+" element user {'"+user+"'},"
		+" element xpath {'"+xpath+"'}"
		+"}  into collection('git/git')/git/pending";
		BaseX.write(xquery);
	}
	
	/**
	 * Caches message about an edit to be included in next commit
	 * @param handle
	 * @param type
	 * @param xpath
	 */
	public static void insertPending(String handle, String type, String xpath, String name, String user){
		xpath = xpath.replace("'", "\"");
		String xquery = "insert node element edit {"
		+" attribute handle {'"+handle+"'},"
		+" attribute type {'"+type+"'},"	
		+" attribute name {'"+name+"'},"
		+" element user {'"+user+"'},"
		+" element xpath {'"+xpath+"'}"
		+"}  into collection('git/git')/git/pending";
		BaseX.write(xquery);
	}
	
	/**
	 * Retrieves, then deletes pending edit info
	 * @return
	 */
	protected static String getPending(){
		String xquery = "for $e in collection('git/git')//git/pending/edit"
		+ " return if($e/@name) then"
		+ " let $c := string-join((data($e/@handle),$e/user,data($e/@type),data($e/@name)),',')"
		+ " return string-join(('{',$c,'}&#xa;'),'')"
		+ " else let $c := string-join((data($e/@handle),$e/user,data($e/@type)),',')"
		+ " return string-join(('{',$c,'}&#xa;'),'')";	
		String pending = BaseX.getXML(xquery,false);
		String delete = "for $e in collection('git/git')/git/pending/edit return delete node $e";
		BaseX.write(delete);
		return pending;
	}
	
//Group methods
	/**
	 * Picks a vargrp ID that isn't in use
	 * @param handle
	 * @return
	 */
	public static String pickGroupID(String handle){
		String xquery = "let $ids:= for $g in collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp" 
		+" let $i := number(replace(data($g/@ID),'[^0-9]',''))"
		+" where string($i) != \"NaN\""
		+" order by $i descending return $i"
		+" let $n := subsequence($ids,1,1)+1"
		+" return if(count($ids) gt 0)"
		+" then $n else 0";
		String groupID = "_"+BaseX.getXML(xquery);
		return groupID;
	}
	
//Other database methods		
	
	/**
	 * Retrieves number of variables with access levels and total number of variables
	 * @param handle
	 * @return
	 */
	public static String[] getVarAccessCount(String handle){
		String xquery = "let $v:= count(collection('CED2AR/"+handle+"')/codeBook/dataDscr/var)"
		+" let $va:= count(collection('CED2AR/"+handle+"')/codeBook/dataDscr/var[@access != ''])"
		+" return data(($va,$v))";
		String[] result = BaseX.getXML(xquery).split(" ");
		return result;	
	}
	
	/**
	 * Adds a bug report to the database
	 * @param bt bug type
	 * @param bd bug description
	 * @param rs reproduction steps
	 * @param n name
	 * @param e email
	 * @param ip IP address
	 * @param ts timestamp
	 * @param ua user agent
	 * @param lp last page
	 */
	public static void insertReport(String bt, String bd, String rs, String n, String e, String ip, String ts, String ua, String lp){
		String xquery = "insert node element bugReport{"
			+" element type {'"+bt+"'},"
			+" element description {'"+bd+"'},"
			+" element reproductionSteps {'"+rs+"'},"
			+" element name {'"+n+"'},"
			+" element email {'"+e+"'},"
			+" element ip {'"+ip+"'},"
			+" element userAgent {'"+ua+"'},"
			+" element timeStamp {'"+ts+"'},"
			+" element lastURL {'"+lp+"'}"
			+"} into collection ('bugs')//bugReports";
		if(BaseX.validateTest(xquery.replace("bugs", "test"), "")){
			BaseX.write(xquery);
		}
	}
	
	/**
	 * Encodes escaping characters to prevent xquery injection attacks
	 * @param s the string to be sanitized
	 * @return the encoded string
	 */
	public static String sanatize(String s){
		s = s.replace("&", "&amp;").replace("'", "&apos;");
		s = s.trim().replace(" +", " ");
		return s;
	}	
	
	/**
	 * Decodes characters that were escaped to prevent xquery injection attacks
	 * @param s the string to be unsanitized
	 * @return the decoded string
	 */
	public static String unsanitize(String s){
		s = s.replaceAll("&amp;", "&").replaceAll("&apos;", "'");
		s = s.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		return s;
	}	
}