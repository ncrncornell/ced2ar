package edu.ncrn.cornell.ced2ar.api.rest.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.ProvGenerator;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;

/**
 * 
 * @author Venky Kambhampaty, Ben Perry
 *
 * 	This class is being used for the new API endpoint, WIP
 *  Contains methods that retrieve data from BaseX.
 *  These methods can be called directly without going through http endpoints. 
 *
 */

//TODO: This class needs general cleanup, improved modularity, and testing. 
//TODO: Have error property for class, set error and return null

public class CodebookData {
	
	//@Autowired
	//private XMLHandle _xh = new XMLHandle(Config.getInstance().getSchemaURI());
	
	public final Logger logger = Logger.getLogger(CodebookData.class.getName());
	
	private String ERROR;
		
	/**  
	 * @return Returns a list of codebooks	
	 * id-type return value
	 * empty string(default) full name of the codebooks is returned.
	 * fn key (handle) of the codebooks is returned
	 * version handle, version and ful name is returned
	 * accesshandle of the codebook along with access restritions	
	 */
	public String getCodebooks(String idType) {

		if(StringUtils.isEmpty(idType)) idType = "";
		
		String xquery = "";
		switch (idType){
			case "index":				
				xquery = "for $c in collection('index') return $c";
			break;
			//Retrieves access levels
			case "access":
				xquery ="for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $h := string-join(($c/@handle,$v/@v),'')"
				+" let $a := for $da in collection(string-join(('CED2AR/',$h,'')))/codeBook/stdyDscr/dataAccs"
				+" where $da/@ID != '' return  data($da/@ID)"
				+" let $accs := string-join($a,',')"
				+" order by $h"
				+" return string-join(($h,',',$accs,';&#xa;'),'')";			
			break;
			//Retrieves the handle, basehandle, version, shortName and fullName
			case "versions":
				xquery = "for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $hA := string-join(($c/@handle,$v/@v,$c/@label),',')"
				+" order by $c/@handle, $v/@v"
				+" return string-join(($hA,',',$v/@use,',',$v/fullName,';&#xa;'),'')";								
			break;
			//Retrieves the basehandle, version
			case "versions2":
				xquery = "let $vs := for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $hA := string-join(($c/@handle,$v/@v),'.')"
				+" order by $c/@handle, $v/@v"
				+" return $hA"
				+" return string-join($vs,';')";								
			break;
			//Retrieves a list of handles, space separated
			case "fn":
				xquery = "for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $h := string-join(($c/@handle,$v/@v),'')"
				+" order by $h"
				+" return $h";		
			break;
			//Retrieves the fullName
			case "title":
				xquery = "for $codebook in collection('index')/codeBooks/codeBook/version"
				+" order by $codebook/fullName "
				+" return data($codebook/fullName) ";
			break;
			case "json":
				xquery = "let $codebooks := for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $h := concat('\"',$c/@handle,'.',$v/@v,'\":\"',$c/@handle,$v/@v,'\"')"
				//+" let $h := concat('\"',$c/@handle,'.',$v/@v,'\":\"',$v/fullName,'\"')"
				+" order by $h return $h"
				+" return concat('{',string-join($codebooks,','),'}')";	
			break;
			case "json2":
				xquery = "let $codebooks := for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $h := concat('{\"value\":\"',$c/@handle,'.',$v/@v,'\",\"text\":\"',$c/@handle,$v/@v,'\"}')"
				+" order by $h return $h"
				+" return concat('[',string-join($codebooks,','),']')";	
			break;
			default:
				xquery = "for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $h := string-join(($c/@handle,$v/@v),'')"
				+" order by $h"
				+" return $h";		
			break;
		}
		return BaseX.getXML(xquery).trim();	
	}
		
	public String getCodebook(String handle) {
		return getCodebook("",handle,"XML","CED2AR");
	}
	
	/**
	 * Returns a full codebook
	 * @param type
	 * @param handle
	 * @return Return the contents of the codebook from the specified Type.
	 */
	public String getCodebook(String type, String handle, String mediaType, String codebookDatabase ) {
		if(handle == null || handle.length() == 0) {
			String message = " \"" + handle + "\" is an invalid handle";
			setError(message);
			return null;
		}
		
		if(!Utilities.codebookExists(handle)){
			String message = " \"" + handle + "\" was not found";
			setError(message);
			return null;
		}
		
		if(codebookDatabase.equals("CED2ARMaster")){
			if(!Utilities.codebookExists(handle,"CED2ARMaster")){
				String message = " \"" + handle + "\" was not found in the master branch";
				setError(message);
				return null;
			}
		}
		
		String xquery = "";
		String xml = "";
		String results = "";
		ProvGenerator provGenerator = null;

		XMLHandle xh = null;
		switch(type){
			//Formats the namespace in the same order as BaseX.put() does with XMLHandle
			//Not the best solution
			case "git":
				//TODO: nullpointer exception with _xh
				try{
					xquery = "let $codeBook:= collection('"+codebookDatabase+"/"+handle+"')/codeBook return $codeBook";
					xml = BaseX.getXML(xquery,false);
					xh = new XMLHandle(xml,Config.getInstance().getSchemaURI());
					xh.addNamespace();
					results = xh.docToString();
				}finally{
					xh.close();
				}
			break;
			//Includes commit hashes
			case "gitNotes":	
				xquery = "let $codeBook:= collection('"+codebookDatabase+"/"+handle+"')/codeBook return $codeBook";
				xml = BaseX.getXML(xquery,false);
				try{
					xh = new XMLHandle(xml,Config.getInstance().getSchemaURI());
					String commits = QueryUtil.getCommits(handle,"");
					
					if(!commits.equals("")){
						//Just latest commit
						String remoteRepo = Config.getInstance().getRemoteRepoURL();
						String[] commitData = commits.split(" ")[0].split("\\.");
						String xpath = "/codeBook/docDscr/citation/verStmt/notes[@elementVersionDate='"+commitData[1]+"']";
						xh.addReplace(xpath,remoteRepo+" "+commitData[0], true, true, false, true);
					}
	
					xh.addNamespace();		
					results = xh.docToString();
				}finally{
					xh.close();
				}
			break;
			//Does not add namespaces when fetching entire codebook
			case "noNamespaces":
				xquery = " let $codeBook:= collection('"+codebookDatabase+"/"+handle+"')/codeBook return <codeBook>"
				+" {$codeBook/*} </codeBook>";
				results =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false).replace("xmlns=\"\"", "");
			break;
			case "includeProv":
				xquery = " let $codeBook:= collection('"+codebookDatabase+"/"+handle+"')/codeBook return "
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
				String codeBookXML =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false).replace("xmlns=\"\"", "");
				provGenerator = new ProvGenerator();		
				String provURL ="localhost/ced2ar-web/prov/data2?roots="+handle;//TODO:update with local server name
				results = provGenerator.insertProvIntoCodebook(provURL,codeBookXML);
			break;
			default:
				//Added namespace when retrieving entire codebook
				//Note that BaseX reorderes these attributes anyway
				xquery = " let $codeBook:= collection('"+codebookDatabase+"/"+handle+"')/codeBook return "
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
				results =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false);
				
				//TODO: determine why XMLNS were replaced
				//.replace("xmlns=\"\"", "")
			break;
		}
		//TODO: Check other tags that need to be changed back
		results = results.replaceAll("<ul>", "<xhtml:ul>").replaceAll("</ul>", "</xhtml:ul>");
		results = results.replaceAll("<li>", "<xhtml:li>").replaceAll("</li>", "</xhtml:li>");
		results = convertContent(results, mediaType);
		
		return results;
	}

	/**
	 * 
	 * @param handle String path value representing the codebook handle  for which access restrictions are being retrieved
	 * @return	Returns access restrictions for the code book identified in the path variable handle
	 *  
	 */
	public String getCodebookAccess(String handle) {
		logger.debug("getCodebookAccess() start" );
		String xquery = "let $codebook := collection('CED2AR/"+handle+"')/codeBook "+ 
		"for  $a in $codebook/stdyDscr/dataAccs "+
		"where $a/@ID != '' return  data($a/@ID)";
		logger.debug("xquery = " + xquery);
		String accessLevels = BaseX.getXML(xquery);
		logger.debug("accessLevels = " + accessLevels);
		return accessLevels;
	}

	/**
	 * 	
	 * @param handle  String path value representing the codebook handle 
	 * @param request HttpServletRequest
	 * @return  Retrieves file descriptor for a codebook and returns it
	 * 
	 */
	public String getCodebookFileDesc(String handle, String mediaType) {	
		String xquery = "for $ced2ar in collection('CED2AR/"+handle+"')/codeBook return $ced2ar/fileDscr";
		String fileDesc = BaseX.getXML(xquery);
		fileDesc  = convertContent(fileDesc,mediaType);
		return fileDesc;
	}

	/**
	 * 
	 * @param handle  String path value representing the codebook handle 
	 * @param request HttpServletRequest
	 * @return doc descriptor for a codebook
	 */
	public String getCodebookDocDesc(String handle,  String mediaType) {
		logger.debug("getCodebookDocDesc() start" );
		String xquery = "for $ced2ar in collection('CED2AR/"+handle+"')/codeBook return $ced2ar/docDscr";
		logger.debug("xquery = " + xquery);
		String docDesc = BaseX.getXML(xquery);
		docDesc  = convertContent(docDesc,mediaType);
		logger.debug("docDesc for  " + handle+" = " + docDesc);
		return docDesc;
	}
	
	/**
	 * 
	 * @param handle Codebook handle
	 * @param i include parameter to list the releases that should include.  
	 * @param request
	 * @return Codebook
	 */
	public String getCodebookRelease(String handle, String i, String mediaType) {
		List<String> includeList = new ArrayList<String>();
		if(!StringUtils.isEmpty(i)) {
			if(i.matches("^.*[^a-zA-Z0-9, ].*$")){
				String message = "Invalid include parameter. Must only contain alphanumeric values, seperated by commas";
				throw new RuntimeException(message);	 
			}
			includeList = Arrays.asList(i.split(","));
		}
		
		String xquery = "let $codeBook:= collection('CED2AR/"+handle+"')/codeBook return $codeBook";
		logger.debug("xquery = " + xquery);
		String codebookRelease = BaseX.getXML(xquery);
		
		//We only deliver XML content. Conversion to JSON can throw an exception when removeRestricted is called.
		XMLHandle xh = new XMLHandle(codebookRelease,Config.getInstance().getSchemaURI());
		xh.removeRestricted(includeList, true);
		String release = xh.docToString();
		codebookRelease = convertContent(codebookRelease,mediaType);
		return release;
	}
		
	/**
	 * 
	 * @param handle 
	 * @param request
	 * @return Returns Study Descriptor for the codebook
	 */

	public String getStudyDesc(String handle,  String mediaType) {	
		String xquery = " for $ced2ar in collection('CED2AR/"+handle+"')/codeBook return $ced2ar/stdyDscr";
		String studyDesc= BaseX.getXML(xquery);
		studyDesc = convertContent(studyDesc, mediaType);
		return studyDesc;
	}
	
	/**
	 * 
	 * @param handle
	 * @param request
	 * @return Title page of the codebook
	 */

	public String getTitlePage(String handle, String mediaType) {
		String  xquery = "for $codebook in collection('CED2AR/"+handle+"')/codeBook "
		+ " let $count := count($codebook/dataDscr/var)"
		+" return <codeBook handle='"+handle+"' variables='{$count}'>{$codebook/docDscr} {$codebook/stdyDscr} {$codebook/fileDscr}</codeBook>";
		
		String titlePage = BaseX.getXML(xquery);
		titlePage = convertContent(titlePage,mediaType);
		return titlePage;
	}
	
	/**
	 * 
	 * @param handle
	 * @param mediaType
	 * @param type
	 * @return
	 */
	public String getTitlePage(String handle, String mediaType, String type){
		String xquery = "";
		switch(type){
			case "nameSpaces":
				 xquery = "for $codebook in collection('CED2AR/"+handle+"')/codeBook "+
				" let $count := count($codebook/dataDscr/var)"+
				" return <codeBook handle='"+handle+"'"+
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
				" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
				" variables='{$count}'>"+
				"{$codebook/docDscr} {$codebook/stdyDscr} {$codebook/fileDscr}</codeBook>";
						
			break;
			default:
				return getTitlePage(handle,mediaType);
		}
	
		String titlePage = BaseX.getXML(xquery);
		return convertContent(titlePage,mediaType);
	}
	
	/**
	 * @param handle
	 * @param mediaType
	 * @param format
	 * @return List of Codebook variables in the format requested.
	 */
	public String getCodebookVariables(String handle, String mediaType, String format) {
		
		String returnShort = "return $codebook/dataDscr";
		if(format.equalsIgnoreCase("name")){
			returnShort = "let $vars := for $var in $codebook/dataDscr/var"
			+ " order by data($var/@name)"
			+ " return $var/@name"
			+ " return string-join($vars,',')";
		}else if(isCSV(mediaType)){
			returnShort = " for $var in $codebook/dataDscr/var"
			+ " let $l:= replace($var/labl/text(),'&lt;|n&gt;|;|,','')"
			+ " let $a:= $var/@access"
			+ " order by data($var/@name)"
			+ " return string-join((data($var/@name),',',$l,',',$a,';&#xa;'),'') ";
		}else if(isJSON(mediaType)){
			returnShort = " let $vars := for $var in $codebook/dataDscr/var"
			+"	let $l:= replace($var/labl/text(),'[^a-zA-Z ]','') "
			+"	let $lA := concat(\"&#34;tokens&#34;:[&#34;\",replace($l,' ', '&#34;,&#34;'),\"&#34;]\") "
			
			+ " order by data($var/@name)"
			+ " return string-join((\"{&#34;name&#34;:&#34;\",data($var/@name),"
			+ "\"&#34;,&#34;label&#34;:&#34;\",$l,\"&#34;"
			+ ",\",$lA,\"}\"),'') "
			+ " return string-join($vars,',')";
		}
		String xquery ="let $codebook := collection('CED2AR/"+handle+"')/codeBook " + returnShort;
		String variables = BaseX.getXML(xquery);
		
		if(mediaType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
			variables  = "["+variables +"]";
		} else {
			variables = convertContent(variables,mediaType);
		}
		return variables;
	}
	
	/**
	 * @param handle
	 * @param mediaType
	 * @return List of Codebook variables in the format requested.
	 */
	public String getCodebookVariables(String handle, String mediaType) {
		return getCodebookVariables(handle, mediaType, "");
	}
	
	
	/**
	 * @param handle Codebook Handle
	 * @return number of varaibles in the codebook as string. If there are none, an empty String is returned
	 */
	//TODO: useful to cast as int?
	public String getVariablesCount(String handle) {
		logger.debug("getVariablesCount() start" );
		
		String xquery = "let $codebook := collection('CED2AR/"+handle+"')/codeBook"
		+ " return count(for $var in $codebook/dataDscr/var return data($var/@name))";
		
		String count = BaseX.getXML(xquery);
		if(StringUtils.isEmpty(count)) count = "";
		return count;
	}	

	public String getCodebookVersions(String handle, String type) {
		logger.debug("getCodebookVersions() start" );
		//make sure that the type is not null
		if(StringUtils.isEmpty(type)) type = "";
		String commits = QueryUtil.getCommits(handle,type);
		return commits;
	}

	public String getVariable(String handle, String variableName, String mediaType, boolean isPartial, String database) {
		logger.debug("getCodebookVariable() start" );
		String xquery = " let $codebook := collection('"+database+"/"+handle+"')/codeBook ";
		
		if(isCSV(mediaType)){
			xquery += "return " + 
			"for $var in $codebook/dataDscr/var " +
			"where $var[lower-case(@name) = '" + variableName.toLowerCase() + "'] "+
			"return string-join((data($var/@name),','," +
			"replace($var/labl/text(),',|;',''),';&#xa;'),'') ";
		}else{//if(!isCSV(mediaType) && isPartial)
			xquery += "let $v:= for $var in $codebook/dataDscr/var " +
			"where $var[lower-case(@name) = '" + variableName.toLowerCase() + "'] "+
			"return $var "+
			"let $g := for $group in $codebook/dataDscr/varGrp "+
			"for $id in tokenize(data($group/@var),' ') "+
			"where $id =data($v/@ID) "+
			"return <group id='{data($group/@ID)}' name='{data($group/@name)}'/> "+
			"let $f := for $file in $codebook/fileDscr "+
			"for $id in tokenize(data($v/@files),' ') where $id =data($file/@ID) "+
			"return $file "+
			"return if($v) then <codeBook  handle='"+handle+"' xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"> "+
			"{$codebook/docDscr/citation/titlStmt/titl}<groups>{$g}</groups><files>{$f}</files>{$v}</codeBook> "+
			"else $v";
		}
		//TODO: There's no reason to return the entire code with a single variable
		/*
		else{
			xquery += "return " + 
			"<codeBook handle='"+handle+"' xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"> { $codebook/docDscr } { " +
			"let $v:= for $var in $codebook/dataDscr/var " +
			"where  $var[lower-case(@name) = '" + variableName.toLowerCase() + "']	"+		
			"return $var "+
			"let $g := for $group in $codebook/dataDscr/varGrp "+
			"for $id in tokenize(data($group/@var),' ') "+
			"where $id =data($v/@ID) "+
			"return <group id='{data($group/@ID)}' name='{data($group/@name)}'/> "+
			"let $f := for $file in $codebook/fileDscr "+
			"for $id in tokenize(data($v/@files),' ') where $id =data($file/@ID) "+
			"return $file "+
			"return <dataDscr><groups>{$g}</groups><files>{$f}</files>{$v}</dataDscr> "+
			"}</codeBook>";
		}*/
		
		String variable= BaseX.getXML(xquery);
		if (variable == null || variable.equals("")) return null;
		variable = convertContent(variable, mediaType);
		return variable;
	}

	/**
	 * 
	 * @param handle codebook handle
	 * @param variableName variable name in the codebook
	 * @return Access Level of the variable 
	 */
	public String getVariableAccess(String handle, String variableName) {
		String xquery = "let $codebook := collection('CED2AR/"+handle+"')/codeBook "+ 
		"for $v in $codebook/dataDscr/var "+
		"where $v/@name = '"+variableName+"' return data($v/@access)";
		
		String access= BaseX.getXML(xquery);
		return access;
	}	
	
	/**
	 * 
	 * @param handle
	 * @param variableName
	 * @param type
	 * @return Variable commits
	 */
	public String getVariableVersions(String handle, String variableName, String type) {
		logger.debug("getCodebookVariableVersions() start" );
		if(StringUtils.isEmpty(type)) type = "";
		String commits = QueryUtil.getVarCommits(handle, variableName,type);
		return commits;
	}

	/**
	 * 
	 * @param handle codebook handle
	 * @param isPartial
	 * @return variable groups in the codebook
	 */
	public String getVariableGroups(String handle, boolean isPartial, String mediaType) {	
		String xquery = "";
		if(isPartial){
			xquery = "let $titl := collection('CED2AR/"+handle+"')/codeBook/docDscr/citation/titlStmt/titl "+
			"let $grps := for $varGrp in collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp "+
			" order by $varGrp/@name return <varGrp name='{$varGrp/@name}' ID='{$varGrp/@ID}'/> "+
		    " return <codeBook handle='"+handle+"'>{$titl} {$grps}</codeBook>";		
		}else{
			xquery = "for $codeBook in collection('CED2AR/"+handle+"')/codeBook "
			+ " return <codeBook> { $codeBook/docDscr }<dataDscr> { "
			+" $codeBook/dataDscr/varGrp } </dataDscr></codeBook>";
		}	
		
		String groups= BaseX.getXML(xquery);
		groups = convertContent(groups, mediaType);
		return groups;
	}

	/**
	 * 
	 * @param handle
	 * @param varGrpID
	 * @return variable group information
	 */
	public String getVariableGroup(String handle, String varGrpID, boolean isPartial, String mediaType) {
		
		String xquery = "";
		String returnShort = "return $group";
		
		if(isPartial){
			xquery = "let $codeBook:=  collection('CED2AR/"+handle+"')/codeBook"+
			" return for $group in $codeBook/dataDscr/varGrp "+
			" where $group/@ID = '"+varGrpID+"' "+ returnShort;
		}else{
			xquery = "let $codeBook:=  collection('CED2AR/"+handle+"')/codeBook "+
			"let $ids := for $group in $codeBook/dataDscr/varGrp "+
			"where $group/@ID = '"+varGrpID+"' "+
			"return tokenize(data($group/@var),' ') "+
			"let $group := for $group in $codeBook/dataDscr/varGrp "+
			"where $group/@ID = '"+varGrpID+"' return $group return "+
			"<codeBook handle='"+handle+"'>{$codeBook/docDscr/citation/titlStmt/titl}<dataDscr>{$group} "+
			"{for $id in $ids  for $var in $codeBook/dataDscr/var "+
			"where $var/@ID = $id "+
			"order by $var/@name return $var} </dataDscr></codeBook> ";
		}
		
		String group= BaseX.getXML(xquery);
		group = convertContent(group, mediaType);
		return group;
	}

	/**
	 * 
	 * @param handle codebook handle
	 * @param varGrpID group id
	 * @param isPartial
	 * @return Variables in the group
	 */
	
	public String getGroupVariables(String handle, String varGrpID,boolean isPartial, String mediaType) {
		logger.debug("getCodebookGroupVariables() start" );
		String xquery = getCodebookGroupVariablesxquery(handle,varGrpID,isPartial,mediaType);
		logger.debug("xquery = " + xquery);
		String groupVariables= BaseX.getXML(xquery);
		groupVariables= convertContent(groupVariables, mediaType);
		logger.debug("groupVariables for  " + handle +" = " + groupVariables);
		return groupVariables;
	}


	public String getSchema(String schemaName) {
		String xquery="let $schema:= collection('schemas/"+schemaName+"') return $schema";
		String schema= BaseX.getXML(xquery);
		return schema;
	}

	public String getSchemaDocType(String schemaName, String type) {
		String xquery="let $schema:= collection('schemas/"+schemaName+"')"
		+" for $element in $schema/xs:schema/xs:element"
		+" where $element[@name='"+type+"']"
		+" return $element/xs:annotation/xs:documentation";
		String schemaDocType= BaseX.getXML(xquery);
		schemaDocType= convertContent(schemaDocType, MediaType.APPLICATION_XML_VALUE);
		return schemaDocType;
	}

	public String getProv() {
		logger.debug("getProv() start" );
		String returnValue = BaseX.httpGet("/rest/prov/", "prov.json");
		logger.debug("returnValue = " + returnValue);
		return returnValue;
	}	
	
	/**
	 * 
	 * @param handle
	 * @param varGrpID
	 * @param isPartial
	 * @return BaseX query that fetches variables in a group
	 */
	public String getCodebookGroupVariablesxquery(String handle, String varGrpID, boolean isPartial, String mediaType) {
		String xquery ="";
		if(isPartial){
			String returnShort = "return $var";
			if("text/csv".equals(mediaType)){
				returnShort = "return string-join((data($var/@name),',',"
				+ "replace($var/labl/text(),'[&lt;|&gt;|;|,]',''),';&#xa;'),'') ";
			}
			
			xquery = "let $codeBook := for $cB in collection('CED2AR/"+ handle +"')/codeBook "+
			" return $cB"+
			" let $ids := for $group in $codeBook/dataDscr/varGrp "+
			" where $group/@ID = '"+varGrpID+"' "+
			" return tokenize(data($group/@var),' ') "+
			" return for $id in $ids "+
			" for $var in $codeBook/dataDscr/var "+
			" where $var/@ID = $id "+
			" order by $var/@name "+returnShort;
		}else if("text/csv".equals(mediaType)){
			xquery = " let $codeBook :=  collection('CED2AR/"+ handle +"')/codeBook"
			+" let $ids := for $group in $codeBook/dataDscr/varGrp "
			+" where $group/@ID = '"+varGrpID+"'  return tokenize(data($group/@var),' ') "
			+" for $var in $codeBook/dataDscr/var"
			+" let $labl:= replace($var/labl/text(),'[&lt;|&gt;|;|,]','')"
			+" order by $var/@name return"
			+" if(exists(index-of($ids, $var/@ID))) then"
			+" string-join((data($var/@name),',',data($var/@ID),',',$labl,',true;&#xa;'),'')"
			+" else string-join((data($var/@name),',',data($var/@ID),',',$labl,',false;&#xa;'),'')";		
		}else{
			xquery = "let $codeBook := for $cB in collection('CED2AR/"+ handle +"')/codeBook "+
			" return $cB"+
			" let $ids := for $group in $codeBook/dataDscr/varGrp "+
			" where $group/@ID = '"+varGrpID+"' "+
			" return tokenize(data($group/@var),' ') "+
			" return <codeBook> { $codeBook/docDscr }<dataDscr> {for $id in $ids "+
			" for $var in $codeBook/dataDscr/var "+
			" where $var/@ID = $id "+
			" order by $var/@name return $var} </dataDscr></codeBook> ";
		}
		return xquery;
	}

//Utilities
	/**
	 * This method add a header if the content is xml or wraps the content in [] if JSON
	 * @param request
	 * @param content
	 * @return String value representing the new content
	 */
	//TODO: Possbily remove support for JSON
	public String convertContent(String content, String mediaType) {
		if(mediaType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
			content = Utilities.xmlToJson(content);
		}
		else if(mediaType.equalsIgnoreCase(MediaType.APPLICATION_XML_VALUE)) {
			content = "<?xml version='1.0' encoding='UTF-8'?>" + content;
		}
		return content;
	}

	/**
	 * @param request
	 * @return Returns  true mediaType is xml 
	 */
	public boolean isXML(String mediaType) {
		return mediaType.equalsIgnoreCase(MediaType.APPLICATION_XML_VALUE) ? true : false;
	}

	/**
	 * @param request
	 * @return Returns  true if media type is csv
	 */
	public boolean isCSV(String mediaType) {
		return mediaType.equalsIgnoreCase("text/csv") ? true : false;
	}
	
	/**
	 * @param request
	 * @return Returns  true if media type is JSON
	 */
	public boolean isJSON(String mediaType) {
		return mediaType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE) ? true : false;
	}	
	
	private void setError(String s){
		ERROR = s;
	};

	public String getError(){
		return ERROR;
	}
}