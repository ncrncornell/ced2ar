package edu.ncrn.cornell.ced2ar.api.restnew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.ncrn.cornell.ced2ar.api.Utilities;
import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;

/**
 * 
 * @author Venky Kambhampaty
 *
 * 	This class is being used for the new API endpoint, WIP
 *  Contains methods that retrieve data from BaseX.
 *  These methods can be called directly without going through http endpoints. 
 *
 */
public class CodebookData {
	private final static Logger logger = Logger.getLogger(CodebookData.class.getName());
	
	
	/**  
	 * @return String returns all codebooks. parameter idType is used  controls information retrieved  				
	 * id-type return value
	 * empty string(default) full name of the codebooks is returned.
	 * fn key (handle) of the codebooks is returned
	 * version handle, version and ful name is returned
	 * accesshandle of the codebook along with access restritions	
	 */
	public String getCodebooks(String idType) {
		logger.debug("getCodebooks() start" );
		if(StringUtils.isEmpty(idType)) idType = "";
		logger.debug("idType=" + idType);
		String xquery = getXQueryForListOfCodebooks(idType);
		logger.debug("xQuery = " + xquery);
		String returnValue =BaseX.getXML(xquery);
		logger.debug("returnValue=" + returnValue);
		return returnValue;
	}
	/**
	 * Returns full codebook
	 * @param type
	 * @param codebookId
	 * @return Return the contents of the codebook from the specified Type.
	 */
	public String getCodebook(String type,String codebookId,String mediaType ) {
		String xquery ="";
		String results = "";
		XMLHandle xh = null;

		switch(type){
			//Formats the namespace in the same order as BaseX.put() does with XMLHandle
			//Not the best solution
			case "git":
				xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return $codeBook";
				xh = new XMLHandle(BaseX.getXML(xquery,false),Config.getInstance().getSchemaURI());
				xh.addNamespace();
				results = xh.docToString();
			break;
			//Includes commit hashes
			case "gitNotes":
				xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return $codeBook";
				xh = new XMLHandle(BaseX.getXML(xquery,false),Config.getInstance().getSchemaURI());
				String commits = QueryUtil.getCommits(codebookId,"full");
				if(!commits.equals("")){
					//Just latest commit
					String remoteRepo = Config.getInstance().getRemoteRepoURL();
					String[] commitData = commits.split(" ")[0].split("\\.");
					String xpath = "/codeBook/docDscr/citation/verStmt/notes[@elementVersionDate='"+commitData[1]+"']";
					xh.addReplace(xpath,remoteRepo+" "+commitData[0], true, true, false, true);
					
				}
				xh.addNamespace();			
				results = xh.docToString();
			break;
			default:
				//Added namespace when retrieving entire codebook
				//Note that BaseX reorderes these attributes anyway
				xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return "
				+" <codeBook xmlns=\"ddi:codebook:2_5\"" 
				+" xmlns:dc=\"http://purl.org/dc/terms/\""  
				+" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\""  
				+" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\""  
				+" xmlns:ns0=\"http://purl.org/dc/elements/1.1/\""  
				+" xmlns:saxon=\"http://xml.apache.org/xslt\""  
				+" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\""  
				+" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""  
				+" xsi:schemaLocation=\"ddi:codebook:2_5 http://www.ncrn.cornell.edu/docs/ddi/2.5.NCRN/schemas/codebook.xsd\">" 		
				+" {$codeBook/*}</codeBook>";
				results =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + BaseX.getXML(xquery,false).replace("xmlns=\"\"", "");
			break;
		}
		results = convertContent(results, mediaType);
		return results;
	}


	
	
	/**
	 * 
	 * @param codebookId String path value representing the codebook handle  for which access restrictions are being retrieved
	 * @return	Returns access restrictions for the code book identified in the path variable codebookId
	 *  
	 */
	public String getCodebookAccess(String codebookId) {
		logger.debug("getCodebookAccess() start" );
		String xQuery =this.getCodebookAccessXQuery(codebookId);
		logger.debug("xQuery = " + xQuery);
		String accessLevels = BaseX.getXML(xQuery);
		logger.debug("accessLevels = " + accessLevels);
		return accessLevels;
	}

	/**
	 * 	
	 * @param codebookId  String path value representing the codebook handle 
	 * @param request HttpServletRequest
	 * @return  Retrieves file descriptor for a codebook and returns it
	 * 
	 */
	public String getCodebookFileDesc(String codebookId,String mediaType) {
		logger.debug("getCodebookFileDesc() start" );
		String xQuery =this.getCodebookFileDescXQuery(codebookId);
		logger.debug("xQuery = " + xQuery);
		String fileDesc = BaseX.getXML(xQuery);
		fileDesc  = convertContent(fileDesc,mediaType);
		
		logger.debug("fileDesc for  " + codebookId+" = " + fileDesc);
		return fileDesc;
	}

	/**
	 * 
	 * @param codebookId  String path value representing the codebook handle 
	 * @param request HttpServletRequest
	 * @return doc descriptor for a codebook
	 */
	public String getCodebookDocDesc(String codebookId,  String mediaType) {
		logger.debug("getCodebookDocDesc() start" );
		String xQuery =this.getCodebookDocDescXQuery(codebookId);
		logger.debug("xQuery = " + xQuery);
		String docDesc = BaseX.getXML(xQuery);
		docDesc  = convertContent(docDesc,mediaType);
		logger.debug("docDesc for  " + codebookId+" = " + docDesc);
		return docDesc;
	}
	
	/**
	 * 
	 * @param codebookId Codebook handle
	 * @param i include parameter to list the releases that should include.  
	 * @param request
	 * @return Retuns Codebook in XML format
	 */
	@RequestMapping(value =Constants.GET_CODEBOOK_RELEASE, method = RequestMethod.GET, produces={"application/xml", "application/json"})
	public String getCodebookRelease(String codebookId, String i, String mediaType) {
		logger.debug("getCodebookRelease() start" );
		List<String> includeList = new ArrayList<String>();
		if(!StringUtils.isEmpty(i)) {
			if(i.matches("^.*[^a-zA-Z0-9, ].*$")){
				String message = "Invalid include parameter. Must only contain alphanumeric values, seperated by commas";
				throw new RuntimeException(message);	 
			}
			includeList = Arrays.asList(i.split(","));
		}
		
		String xQuery =this.getCodebookReleaseXQuery(codebookId);
		logger.debug("xQuery = " + xQuery);
		String codebookRelease = BaseX.getXML(xQuery);
		
		//We only deliver XML content. Conversion to JSON can throw an exception when removeRestricted is called.
		XMLHandle handle = new XMLHandle(codebookRelease,Config.getInstance().getSchemaURI());
		handle.removeRestricted(includeList, true);
		String release = handle.docToString();
		codebookRelease  = convertContent(codebookRelease,mediaType);

		logger.debug("codebookRelease for  " + codebookId+" = " + release);
		return release;
	}
		
	/**
	 * 
	 * @param codebookId 
	 * @param request
	 * @return Returns Study Descriptor for the codebook
	 */

	public String getCodebookStudyDesc(String codebookId,  String mediaType) {
		logger.debug("getCodebookStudyDesc() start" );
		String xQuery =this.getCodebookStudyDescXQuery(codebookId);
		logger.debug("xQuery = " + xQuery);
		String codebookStudyDesc= BaseX.getXML(xQuery);
		codebookStudyDesc  = convertContent(codebookStudyDesc, mediaType);
		logger.debug("codebookRelease for  " + codebookId+" = " + codebookStudyDesc);
		return codebookStudyDesc;
	}
	
	/**
	 * 
	 * @param codebookId
	 * @param request
	 * @return Title page of the codebook
	 */

	public String getCodebookTitlePage(String codebookId, String mediaType) {
		logger.debug("getCodebookTitlePage() start" );
		String xQuery =this.getCodebookTitlePageXQuery(codebookId);
		logger.debug("xQuery = " + xQuery);
		String codebookTitlePage= BaseX.getXML(xQuery);
		codebookTitlePage  = convertContent(codebookTitlePage,mediaType);
		logger.debug("codebookTitlePage for  " + codebookId+" = " + codebookTitlePage);
		return codebookTitlePage;
	}
	
	/**
	 * @param codebookId
	 * @param mediaType
	 * @return List of Codebook variables in the format requested.
	 */
	public String getCodebookVariables(String codebookId,  String mediaType  ) {
		logger.debug("getCodebookVariables() start" );
		String xQuery =getCodebookVariablesXQuery(codebookId,mediaType);
		logger.debug("xQuery = " + xQuery);
		String codebookVariables= BaseX.getXML(xQuery);
		if(mediaType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
			codebookVariables  = "["+codebookVariables +"]";
		}
		else {
			codebookVariables = convertContent(codebookVariables,mediaType);
		}
		logger.debug("codebookVariables for  " + codebookId+" = " + codebookVariables);
		return codebookVariables;
	}
	/**
	 * @param codebookId Codebook Handle
	 * @return number of varaibles in the codebook as string. If there are none, an empty String is returned
	 */
	public String getCodebookVariablesCount(String codebookId) {
		logger.debug("getCodebookVariablesCount() start" );
		String countVariablesXquery = getCodebookVariablesCountXQuery(codebookId);
		String codebookVariablesCount = BaseX.getXML(countVariablesXquery);
		if(StringUtils.isEmpty(codebookVariablesCount)) codebookVariablesCount= "";
		return codebookVariablesCount;
	}	

	public String getCodebookVersions(String codebookId, String type) {
		logger.debug("getCodebookVersions() start" );
		//make sure that the type is not null
		if(StringUtils.isEmpty(type)) type = "";
		String commits = QueryUtil.getCommits(codebookId,type);
		return commits;
	}

	public String getCodebookVariable(String codebookId, String variableName, String mediaType, boolean isPartial) {
		logger.debug("getCodebookVariable() start" );
		String xQuery = getCodebookVariableXQuery(codebookId,variableName,mediaType,isPartial);
		logger.debug("xQuery = " + xQuery);
		String variable= BaseX.getXML(xQuery);
		variable = convertContent(variable, mediaType);
		logger.debug("codebookVariable for  " + codebookId +":"+ variableName +" = " + variable);
		return variable;
	}

	/**
	 * 
	 * @param codebookId codebook handle
	 * @param variableName variable name in the codebook
	 * @return Access Level of the variable 
	 */
	public String getCodebookVariableAccess(String codebookId, String variableName) {
		logger.debug("getCodebookVariableAccess() start" );
		String xQuery = getCodebookVariableAccessXQuery(codebookId,variableName);
		logger.debug("xQuery = " + xQuery);
		String access= BaseX.getXML(xQuery);
		logger.debug("codebookVariableAccess for  " + codebookId +":"+ variableName +" = " + access);
		return access;
	}	
	
	/**
	 * 
	 * @param codebookId
	 * @param variableName
	 * @param type
	 * @return Variable commits
	 */
	public String getCodebookVariableVersions(String codebookId, String variableName,String type) {
		logger.debug("getCodebookVariableVersions() start" );
		if(StringUtils.isEmpty(type)) type = "";
		String commits = QueryUtil.getVarCommits(codebookId, variableName,type);
		return commits;
	}

	/**
	 * 
	 * @param codebookId codebook handle
	 * @param isPartial
	 * @return variable groups in the codebook
	 */
	public String getCodebookVariableGroups(String codebookId, boolean isPartial,String mediaType) {
		logger.debug("getCodebookVariableGroups() start" );
		String xQuery = getCodebookVariableGroupsXQuery(codebookId,isPartial);
		logger.debug("xQuery = " + xQuery);
		String groups= BaseX.getXML(xQuery);
		groups = convertContent(groups, mediaType);
		logger.debug("codebookVariableGroup for  " + codebookId +" = " + groups);
		return groups;
	}

	/**
	 * 
	 * @param codebookId
	 * @param varGrpID
	 * @return variable group information
	 */
	public String getCodebookVariableGroup(String codebookId,String varGrpID, boolean isPartial, String mediaType) {
		logger.debug("getCodebookVariableGroup() start" );
		String xQuery = getCodebookVariableGroupXQuery(codebookId,varGrpID,isPartial);
		logger.debug("xQuery = " + xQuery);
		String group= BaseX.getXML(xQuery);
		group = convertContent(group, mediaType);
		
		logger.debug("codebookGroup for  " + codebookId +" = " + group);
		return group;
	}

	/**
	 * 
	 * @param codebookId codebook handle
	 * @param varGrpID group id
	 * @param isPartial
	 * @return Variables in the group
	 */
	
	public String getCodebookGroupVariables(String codebookId,String varGrpID,boolean isPartial ,String mediaType) {
		logger.debug("getCodebookGroupVariables() start" );
		String xQuery = getCodebookGroupVariablesXQuery(codebookId,varGrpID,isPartial,mediaType);
		logger.debug("xQuery = " + xQuery);
		String groupVariables= BaseX.getXML(xQuery);
		groupVariables= convertContent(groupVariables, mediaType);
		logger.debug("groupVariables for  " + codebookId +" = " + groupVariables);
		return groupVariables;
	}


	public String getSchema(String schemaName) {
		logger.debug("getSchema() start" );
		String xQuery = getSchemaXQuery(schemaName);
		logger.debug("xQuery = " + xQuery);
		String schema= BaseX.getXML(xQuery);
		return schema;
	}

	public String getSchemaDocType(String schemaName,String type) {
		logger.debug("getSchemaDocType() start" );
		String xQuery = getSchemaDocTypeXQuery(schemaName,type);
		logger.debug("xQuery = " + xQuery);
		String schemaDocType= BaseX.getXML(xQuery);
		schemaDocType= convertContent(schemaDocType, MediaType.APPLICATION_XML_VALUE);
		return schemaDocType;
	}



	private String getSchemaDocTypeXQuery(String schemaName,String type) {
		String xquery="let $schema:= collection('schemas/"+schemaName+"')"
				+" for $element in $schema/xs:schema/xs:element"
				+" where $element[@name='"+type+"']"
				+" return $element/xs:annotation/xs:documentation";

		
		return xquery;
	}

	private String getSchemaXQuery(String schemaName) {
		String xquery="let $schema:= collection('schemas/"+schemaName+"')"
		+" return $schema";
		
		return xquery;
	}

	public String getProv() {
		logger.debug("getProv() start" );
		String returnValue = BaseX.httpGet("/rest/prov/", "prov.json");
		logger.debug("returnValue = " + returnValue);
		return returnValue;
	}	
	
	/**
	 * 
	 * @param codebookId
	 * @param varGrpID
	 * @param isPartial
	 * @return BaseX query that fetches variables in a group
	 */
	private String getCodebookGroupVariablesXQuery(String codebookId,String varGrpID, boolean isPartial,String mediaType) {
		String xquery ="";
		if(isPartial){
			String returnShort = "return $var";
			if("text/csv".equals(mediaType)){
				returnShort = "return string-join((data($var/@name),',',"
				+ "replace($var/labl/text(),'[&lt;|&gt;|;|,]',''),';&#xa;'),'') ";
			}
			
			xquery = "let $codeBook := for $cB in collection('CED2AR/"+ codebookId +"')/codeBook "+
				" return $cB"+
				" let $ids := for $group in $codeBook/dataDscr/varGrp "+
				" where $group/@ID = '"+varGrpID+"' "+
				" return tokenize(data($group/@var),' ') "+
				" return for $id in $ids "+
				" for $var in $codeBook/dataDscr/var "+
				" where $var/@ID = $id "+
				" order by $var/@name "+returnShort;
		}else if("text/csv".equals(mediaType)){
			xquery = " let $codeBook :=  collection('CED2AR/"+ codebookId +"')/codeBook"
			+" let $ids := for $group in $codeBook/dataDscr/varGrp "
			+" where $group/@ID = '"+varGrpID+"'  return tokenize(data($group/@var),' ') "
			+" for $var in $codeBook/dataDscr/var"
			+" let $labl:= replace($var/labl/text(),'[&lt;|&gt;|;|,]','')"
			+" order by $var/@name return"
			+" if(exists(index-of($ids, $var/@ID))) then"
			+" string-join((data($var/@name),',',data($var/@ID),',',$labl,',true;&#xa;'),'')"
			+" else string-join((data($var/@name),',',data($var/@ID),',',$labl,',false;&#xa;'),'')";		
		}else{
			xquery = "let $codeBook := for $cB in collection('CED2AR/"+ codebookId +"')/codeBook "+
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
	
	/**
	 * 
	 * @param codebookId
	 * @param varGrpID
	 * @param isPartial
	 * @return baseX query that fetches variable group information
	 */
	
	private String getCodebookVariableGroupXQuery(String codebookId,String varGrpID, boolean isPartial) {
		String xquery = "";
		String returnShort = "return $group";
		
		if(isPartial){
			xquery = "let $codeBook:=  collection('CED2AR/"+codebookId+"')/codeBook"+
					" return for $group in $codeBook/dataDscr/varGrp "+
					" where $group/@ID = '"+varGrpID+"' "+ returnShort;
		}else{
			xquery = "let $codeBook:=  collection('CED2AR/"+codebookId+"')/codeBook "+
					"let $ids := for $group in $codeBook/dataDscr/varGrp "+
					"where $group/@ID = '"+varGrpID+"' "+
					"return tokenize(data($group/@var),' ') "+
					"let $group := for $group in $codeBook/dataDscr/varGrp "+
					"where $group/@ID = '"+varGrpID+"' return $group return "+
					"<codeBook handle='"+codebookId+"'>{$codeBook/docDscr/citation/titlStmt/titl}<dataDscr>{$group} "+
					"{for $id in $ids  for $var in $codeBook/dataDscr/var "+
					"where $var/@ID = $id "+
					"order by $var/@name return $var} </dataDscr></codeBook> ";
		}
		
		return xquery;
	}

	/**
	 * 
	 * @param codebookId
	 * @param isPartial
	 * @return baseX query that fetches variable groups
	 */
	private String getCodebookVariableGroupsXQuery(String codebookId,boolean isPartial) {
		String xquery = "";
		
		if(isPartial){
			xquery = "let $titl := collection('CED2AR/"+codebookId+"')/codeBook/docDscr/citation/titlStmt/titl "+
			"let $grps := for $varGrp in collection('CED2AR/"+codebookId+"')/codeBook/dataDscr/varGrp "+
			" order by $varGrp/@name return <varGrp name='{$varGrp/@name}' ID='{$varGrp/@ID}'/> "+
		    " return <codeBook handle='"+codebookId+"'>{$titl} {$grps}</codeBook>";		
		}else{
			xquery = "for $codeBook in collection('CED2AR/"+codebookId+"')/codeBook "
			+ " return <codeBook> { $codeBook/docDscr }<dataDscr> { "
			+" $codeBook/dataDscr/varGrp } </dataDscr></codeBook>";
		}	
		return xquery;
	}

	/**
	 * 
	 * @param codebookId
	 * @param variableName
	 * @return baseX query as string to fetch access level of the variable in the codebool
	 */
	private String getCodebookVariableAccessXQuery(String codebookId,String variableName) {
		String xquery = "let $codebook := collection('CED2AR/"+codebookId+"')/codeBook "+ 
						"for $v in $codebook/dataDscr/var "+
						"where $v/@name = '"+variableName+"' return data($v/@access)";
		return xquery;
	}
	
	/**
	 * 
	 * @param codebookId
	 * @param variableName
	 * @param partialText
	 * @return Returns details of var
	 */
	private String getCodebookVariableXQuery(String codebookId,String variableName ,String mediaType,boolean isPartial) {
		String xquery = " let $codebook := collection('CED2AR/"+codebookId+"')/codeBook ";
		
		if(isContentDeliveryInCSV(mediaType)){
			xquery += "return " + 
			"for $var in $codebook/dataDscr/var " +
			"where $var[lower-case(@name) = '" + variableName.toLowerCase() + "'] "+
			"return string-join((data($var/@name),','," +
			"replace($var/labl/text(),',|;',''),';&#xa;'),'') ";
		}else if(!isContentDeliveryInCSV(mediaType) && isPartial){
			xquery += "return " + 
			"let $v:= for $var in $codebook/dataDscr/var " +
			"where $var[lower-case(@name) = '" + variableName.toLowerCase() + "'] "+
			"return $var "+
			"let $g := for $group in $codebook/dataDscr/varGrp "+
			"for $id in tokenize(data($group/@var),' ') "+
			"where $id =data($v/@ID) "+
			"return <group id='{data($group/@ID)}' name='{data($group/@name)}'/> "+
			"let $f := for $file in $codebook/fileDscr "+
			"for $id in tokenize(data($v/@files),' ') where $id =data($file/@ID) "+
			"return $file "+
			"return  <codeBook  handle='"+codebookId+"' xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"> "+
			"{$codebook/docDscr/citation/titlStmt/titl}<groups>{$g}</groups><files>{$f}</files>{$v}</codeBook>";
		}else{
			xquery += "return " + 
			"<codeBook handle='"+codebookId+"' xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"> { $codebook/docDscr } { " +
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
		}
		
		return xquery;
	}

	/**
	 * 
	 * @param codebookId
	 * @param request
	 * @return Returns the XQuery String that fetches the number of variables in a book
	 */
	private String getCodebookVariablesCountXQuery(String codebookId) {
		String xqueryCount = "let $codebook := collection('CED2AR/"+codebookId+"')/codeBook"
		+ " return count(for $var in $codebook/dataDscr/var return data($var/@name))";
		return xqueryCount;
	}
	
	/**
	 * 
	 * @param codebookId Codebook Handle
	 * @param mediaType 
	 * @return xquery string that fetches variables in a code book.  
	 * 		   MediaType determines which format the variables will be returned. 
	 */
	private String getCodebookVariablesXQuery(String codebookId,String mediaType) {
		
		String returnShort = "return $codebook/dataDscr";
		
		if(isContentDeliveryInCSV(mediaType)){
			returnShort = " for $var in $codebook/dataDscr/var"
			+ " let $l:= replace($var/labl/text(),'&lt;|n&gt;|;|,','')"
			+ " let $a:= $var/@access"
			+ " order by data($var/@name)"
			+ " return string-join((data($var/@name),',',$l,',',$a,';&#xa;'),'') ";
		}else if(isContentDeliveryInJASON(mediaType)){
			returnShort = " let $vars := for $var in $codebook/dataDscr/var"
			+"	let $l:= replace($var/labl/text(),'[^a-zA-Z ]','') "
			+"	let $lA := concat(\"&#34;tokens&#34;:[&#34;\",replace($l,' ', '&#34;,&#34;'),\"&#34;]\") "
			
			+ " order by data($var/@name)"
			+ " return string-join((\"{&#34;name&#34;:&#34;\",data($var/@name),"
			+ "\"&#34;,&#34;label&#34;:&#34;\",$l,\"&#34;"
			+ ",\",$lA,\"}\"),'') "
			+ " return string-join($vars,',')";
		}
		String xquery ="let $codebook := collection('CED2AR/"+codebookId+"')/codeBook " + returnShort;
		return xquery;
	}

	
	/**
	 * 
	 * @param codebookId Codebook Handle
	 * @return BaseX query that fetches the codebook Title Page 
	 */
	private String getCodebookTitlePageXQuery(String codebookId) {
		String  xquery = "for $codebook in collection('CED2AR/"+codebookId+"')/codeBook "
				+ " let $count := count($codebook/dataDscr/var)"
				+" return <codeBook handle='"+codebookId+"' variables='{$count}'>{$codebook/docDscr} {$codebook/stdyDscr} {$codebook/fileDscr}</codeBook>";
		return xquery;
	}

	/**
	 * 
	 * @param codebookId Codebook Handle
	 * @return BaseX query that fetches the codebook Study Desc
	 */
	private String getCodebookStudyDescXQuery(String codebookId) {
		String xquery = " for $ced2ar in collection('CED2AR/"+codebookId+"')/codeBook return $ced2ar/stdyDscr";
		return xquery;
	}


	/**
	 * 
	 * @param codebookId Codebook Handle
	 * @return BaseX query that fetches the codebook contents
	 */
	private String getCodebookReleaseXQuery(String codebookId) {
		String xquery = " let $codeBook:= collection('CED2AR/"+codebookId+"')/codeBook return $codeBook";
		return xquery;
	}
	
	/**
	 * This method add a header if the content is xml or wraps the content in [] if JSON
	 * @param request
	 * @param content
	 * @return String value representing the new content
	 */
	private String convertContent(String content,String mediaType) {
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
	private boolean isContentDeliveryInXML(String mediaType) {
		boolean isXML = false;
		
		if(mediaType.equalsIgnoreCase("") || mediaType.equalsIgnoreCase(MediaType.APPLICATION_XML_VALUE))isXML = true;
		return isXML;
	}

	/**
	 * @param request
	 * @return Returns  true if media type is csv
	 */
	private boolean isContentDeliveryInCSV(String mediaType) {
		boolean isCSV = false;
		if(mediaType.equalsIgnoreCase("text/csv"))isCSV = true;
		return isCSV;
	}
	/**
	 * @param request
	 * @return Returns  true if media type is JSON
	 */
	private boolean isContentDeliveryInJASON(String mediaType) {
		boolean isCSV = false;
		if(mediaType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE))isCSV = true;
		return isCSV;
	}
	
	
	/**
	 * 
	 * @param nameType
	 * @return xquery string that retrieves all codebooks. 
	 */
	private String getXQueryForListOfCodebooks(String nameType) {
		String xquery ="";
		switch (nameType){
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
			case "fn":
				xquery = "for $c in collection('index')/codeBooks/codeBook"
				+" for $v in $c/version"
				+" let $h := string-join(($c/@handle,$v/@v),'')"
				+" order by $h"
				+" return $h";		
			break;
			//Retrieves the fullName
			default:
				xquery = "for $codebook in collection('index')/codeBooks/codeBook/version"
				+" order by $codebook/fullName "
				+" return data($codebook/fullName) ";
			break;
		}
		return xquery;
	}	
	/**
	 * 
	 * @param codebookId
	 * @return baseX query String that retrieves access level of a specisifed codebook
	 *  
	 */
	private String getCodebookAccessXQuery(String codebookId) {
		String xquery = "let $codebook := collection('CED2AR/"+codebookId+"')/codeBook "+ 
		"for  $a in $codebook/stdyDscr/dataAccs "+
		"where $a/@ID != '' return  data($a/@ID)";
		return xquery;
	}
	/**
	 * 
	 * @param codebookId Code book Handle
	 * @return Query String that fetches the Document description of the codebook
	 */
	private String getCodebookDocDescXQuery(String codebookId) {
		String xquery = "for $ced2ar in collection('CED2AR/"+codebookId+"')/codeBook return $ced2ar/docDscr";
		return xquery;
	}

	/**
	 * 
	 * @param codebookId
	 * @return baseX query String that retrieves File Desc. of the specified codebook.
	 */
	private String getCodebookFileDescXQuery(String codebookId) {
		String xquery = "for $ced2ar in collection('CED2AR/"+codebookId+"')/codeBook return $ced2ar/fileDscr";
		return xquery;
	}
}