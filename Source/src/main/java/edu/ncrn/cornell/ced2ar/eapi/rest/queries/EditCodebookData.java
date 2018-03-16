package edu.ncrn.cornell.ced2ar.eapi.rest.queries;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ncrn.cornell.ced2ar.api.data.BaseX;
import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.rest.queries.CodebookData;
import edu.ncrn.cornell.ced2ar.eapi.ProvGenerator;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;

public class EditCodebookData {
	@Autowired
	private XMLHandle _xh = new XMLHandle(Config.getInstance().getSchemaURI());
	
	public final Logger logger = Logger.getLogger(EditCodebookData.class.getName());
	
	private String ERROR;
	
//Data Access
	
	public int postXMLHandle(XMLHandle xh,String baseHandle, String version, 
	String user, boolean isMaster, String commitPath){

	    try{
		    if(!xh.isValid()){
		    	setError("Uploaded file is invalid: "+xh.getError()); 		    	
		    	return 400;
		    }
	    }catch(NullPointerException e){
	    	setError("XML is not well-formed "); 
	    	System.out.println(ERROR);
	    	return 400;
	    }
	    
	    String handle = baseHandle + version;
	    BaseX.put(handle, xh.getRepoXML().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>"));

	    if(Config.getInstance().isGitEnabled()){
	    	 if(commitPath.startsWith("var")){
	    		 QueryUtil.insertPending(handle, "var", "/codeBook/"+commitPath,user);
	    	 }else{
	    		 QueryUtil.insertPending(handle, "edit", "/codeBook",user);
	    	 }
		}    
	    return 200;
	}

	/**
	 * Updates or adds a codebook from an XML file
	 * @param baseHandle - base handle of codebook
	 * @param version - version of codebook
	 * @param label - label for basehandle; optional if a codebook with a basehandle already exists
	 * @param user - user's email address. 
	 * @param isMaster - if true, upload the master db, otherwise, upload to working db
	 * @return
	 */
	//TODO: Add methods to check input
	//TODO: Add methods to make git functionality more modular
	//TODO: Clean up resource utilization
	//TODO: Test everything
	//TODO: Check if variables have names
	public int postCodebook(InputStream contents, String baseHandle, String version, 
	String label, String user, boolean isMaster, String commitPath){
		InputStream ins = contents;
		
		//Extremely important
		baseHandle = baseHandle.toLowerCase();
		version = version.toLowerCase();
		String handle = baseHandle+version;
		try{		
			//Handles must be 20 alphanumeric chars or fewer 
		    if(baseHandle.length() > 20 | !baseHandle.matches("^[a-zA-Z0-9\\-]*$")){
		    	setError("File handle must be alphanumeric and at most 20 characters."); 
		    	return 400;
		    }
		    
		    //Version must be 20 alphanumeric chars or fewer 
		    if(version.length() > 20 | version.matches(".*-\\W+.*")){
		    	setError("Version must be alphanumeric and at most 20 characters."); 
		    	return 400;
		    }
		    
		    //label must be 15 alphanumeric chars or fewer 
		    if(label!= null && label.length() > 20 | label.matches(".*-\\W+ .*")){
		    	setError("Label must be alphanumeric and at most 20 characters."); 
		    	return 400;
		    }

			XMLHandle xh = null;
			xh = new XMLHandle(ins,Config.getInstance().getSchemaURI());
			
		    //Adds missing elements required for vars
		    xh.addVarBlanks("txt");
		    xh.addVarBlanks("labl");
		    
		    //Adds var IDs if missing
		    xh.addIDs();
		    
		    //Adds docDscr title if not present
		    if(xh.getValue("/codeBook/docDscr/citation/titlStmt/titl") == null){
		    	String title = "";
		    	try{
		    		title = xh.getValue("/codeBook//titl").replaceAll("[^A-Za-z0-9\\-\\[\\]\\(\\)\\. ]", "");
		    	}catch(NullPointerException e){}
		    	if(title == null || title.equals("")){
		    		title = handle;
		    	}
		    	//TODO: Causes bug and adds two titles
		    	xh.addReplace("/codeBook/docDscr/citation/titlStmt/titl", title, true, true, false, false);
		    }
		
		    if(isMaster){
		    	if(!xh.isValid()){
		    		setError("Uploaded file is invalid"); 
			    	return 400;
			    }
		    	BaseX.putM(handle, xh.getRepoXML());
		    	return 200;
		    }
		    
		    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
			timestamp+= " (upload date)";
			
			//Error where blank version without date prevents insertion
			xh.addReplace("/codeBook/docDscr/citation/verStmt/version/@date", timestamp, false, true, true, true);	
			try{
				xh.getValue("/codeBook/docDscr/citation/verStmt/version/@date").equals(timestamp);
			}catch(NullPointerException e){
				xh.addReplace("/codeBook/docDscr/citation/verStmt/version/@date", timestamp, true, true, true, true);
			}
	
		    //String software="The Comprehensive Extensible Data Documentation and Access Repository 2.7";
		    //xh.addReplace("/codeBook/docDscr/citation/prodStmt/software[1]", software, true, true, false, true);
		    //Validates XML 
		    try{
			    if(!xh.isValid()){
			    	setError("Uploaded file is invalid"); 
			    	return 400;
			    }
		    }catch(NullPointerException e){
		    	setError("Uploaded file is not a well-formed XML document."); 
		    	return 400;
		    }
		    
		    logger.info("File is valid. Uploading to database...");
		    //Checks to see if codebook version exists
		    if(!QueryUtil.hasVersionIndex(baseHandle, version)){
		    	//shortName and fullName now generated one from DDI
		    	String shortName = "";
		    	try{
					shortName = shortName.trim();
					shortName= xh.getValue("//codeBook/docDscr/citation/titlStmt/altTitl");	
					shortName = shortName.replaceAll("[^A-Za-z0-9\\-]", "").toUpperCase();
					if(shortName.length()>=15)
						shortName = shortName.substring(0,14);
			    }catch(NullPointerException e){
			    	shortName = handle;
			    }
				String fullName = "";
			    try{
			    	fullName= xh.getValue("//codeBook/docDscr/citation/titlStmt/titl");			 
			    	fullName = fullName.replaceAll("[^A-Za-z0-9\\-\\[\\]\\(\\) ]", "").trim();
			    }catch(NullPointerException e){
			    	fullName = handle;
			    }
				    
			    //Checks to see if any version of codebook exists, if not add parent element
			    if(!QueryUtil.hasCodebookIndex(baseHandle)){
			    	if(label == null || label.equals("")){
			    		setError("Handle does not exist, so a label is required"); 
			    		return 400;
			    	}
			    	QueryUtil.insertCodebookIndex(baseHandle,label);
			    }else if(label != null && !label.equals("")){
			    	//Otherwise, update label
			    	QueryUtil.updateLabel(baseHandle, label);
			    }
			    //Inserts specific version
		    	QueryUtil.insertCodebookVersionIndex(baseHandle, version, shortName, fullName);
		    	
		    	//Calls prov generator to write to prov db
		    	ProvGenerator provGenerator = new ProvGenerator();
		    	provGenerator.updateProvFromCodebook(xh.getRepoXML());//Change to access string directly
		    }
		      
		    //Uploads codebook
		    BaseX.put(handle, xh.getRepoXML());
		    
		    //New BaseX config
		    //BaseX.put2(handle, xh.getRepoXML());
		   
		    if(Config.getInstance().isGitEnabled()){
		    	 if(commitPath.startsWith("var")){
		    		 QueryUtil.insertPending(handle, "var", "/codeBook/"+commitPath,user);
		    	 }else{
		    		 QueryUtil.insertPending(handle, "edit", "/codeBook",user);
		    	 }
			}    
		    return 200;
		}finally{	
			try{
				ins.close();
			} catch (IOException e) {
				ins = null;
			}
		}
	}
	
	/**
	 * Updates or adds an uploaded codebook
	 * @param contents
	 * @param baseHandle
	 * @param version
	 * @param label
	 * @param user
	 * @param isMaster
	 * @return
	 */
	public int postCodebook(InputStream contents, String baseHandle, String version, 
	String label, String user, boolean isMaster){
			return postCodebook(contents,  baseHandle, version, label, user, isMaster,"");
	}
	
	/**
	 * Deletes a codebook
	 * @param baseHandle
	 * @param version
	 * @return
	 */
	public int deleteCodebook(String baseHandle, String version){
	    String handle = baseHandle + version;
		try{
			QueryUtil.deleteVersion(baseHandle, version);
			BaseX.delete(handle);	    
			return 200;	    	
		}catch (NullPointerException e) {
			setError("Error deleting codebook");
	    	return 400;
		}			
	}
	
	/**
	 * Sets the access level for multiple variables at once
	 * @param baseHandle - baseHandle for codebook
	 * @param version - version for codebook
	 * @param access - access level to set variables to
	 * @param vars - array of varriables to update (optional is @all equals true)
	 * @param all - if true, will update all variables in the codebook to a specified access level
	 * @return - response code where 200 is success, 400 means a bad request, and all others are an error
	 */
	//TODO: errors for bad access level
	//TODO: warnings for non-existent vars
	public int setAccessLevels(String baseHandle, String version, String access, String[] vars, boolean all){
		String handle = baseHandle+version;
		//access can be empty
		if(vars == null && !all){
			setError("Bad arguments. Required vars={space seperated var list} and access={level to change to}");
			return 400;
		}
		
		String xquery = "for $c in collection('CED2AR/"+handle+"') for $v in $c/codeBook/dataDscr/var";
		
		//If every variable is selected, no need for a where statement
		int limit = 100;//Need to limit number of variables in where statement, or else HTTP request is too long
		if(!all){
			if(vars.length > limit){			
				String editStmt = "";
				if(access.equals("")){
					editStmt=" return delete node $v/@access";
				}else{
					editStmt= " return if($v/@access) then"
					+" replace value of node $v/@access with '"+access+"'"
					+" else insert node attribute access {'"+access+"'} into $v";
				}
				
				int splitSize = vars.length/limit;
				int remander = vars.length%limit;
				int i = 0;
				while(i < splitSize){					
					xquery = "for $c in collection('CED2AR/"+handle+"') for $v in $c/codeBook/dataDscr/var";
					int start = i > 0 ? (i * limit )- 1 : 0;
					String[] subVars = Arrays.copyOfRange(vars,start,((i+1)*limit-1));
					String varStmt = "";
					for(String var : subVars){
						if(!varStmt.equals("")){
							varStmt+="|| ";
						}
						varStmt += "$v[@name = '"+var+"'] ";
					}
					xquery+=" where "+varStmt+editStmt;
					
					BaseX.write(xquery);
					i++;
				}
				if(remander > 0){
					xquery = "for $c in collection('CED2AR/"+handle+"')"
					+" for $v in $c/codeBook/dataDscr/var";
					
					String[] subVars = Arrays.copyOfRange(vars,(i*limit)-1,vars.length);
					String varStmt = "";
					for(String var : subVars){
						if(!varStmt.equals("")){
							varStmt+="|| ";
						}
						varStmt += "$v[@name = '"+var+"'] ";
					}
					xquery+=" where "+varStmt+editStmt;
					BaseX.write(xquery);
				}
				
				logger.debug("Sucessfully updated access levels for "+vars);
				return 200;
			}else{
				String varStmt = "";
				for(String var : vars){
					if(!varStmt.equals("")){
						varStmt+="|| ";
					}
					varStmt += "$v[@name = '"+var+"'] ";
				}
				xquery+=" where "+varStmt;
			}
		}
		
		//If access is empty, remove attribute
		if(access.equals("")){
			xquery+=" return delete node $v/@access";
		}else{
			xquery+= " return if($v/@access) then"
			+" replace value of node $v/@access with '"+access+"'"
			+" else insert node attribute access {'"+access+"'} into $v";
		}

		BaseX.write(xquery);
		logger.debug("Sucessfully updated access levels for "+vars);
		return 200;
	}
	
	public int setAccessLevelsTop(String baseHandle, String version, String[] vars, String accesslevel){
		return setAccessLevels2(baseHandle,version,vars, new String[] {"all"}, accesslevel);
	}
	
	//TODO: Add rest enpoint
	public int setAccessLevels2(String baseHandle, String version, String[] vars, 
	String[] xpaths, String accessLevel){
		
		String handle = baseHandle + version;
		CodebookData codebookData = new CodebookData();
		String currentXML = codebookData.getCodebook(handle);

		XMLHandle xh = new XMLHandle(currentXML,Config.getInstance().getSchemaURI());
		
		//TODO: Better place for this
		Map<String,String> accessVars = new WeakHashMap<String,String>();
		
		accessVars.put("all", "");
		
		accessVars.put("mean", "/sumStat[@type='mean']");
		accessVars.put("medn", "/sumStat[@type='medn']");
		accessVars.put("mode", "/sumStat[@type='mode']");
		accessVars.put("vald", "/sumStat[@type='vald']");
		accessVars.put("invd", "/sumStat[@type='invd']");
		accessVars.put("min", "/sumStat[@type='min']");
		accessVars.put("max", "/sumStat[@type='max']");
		accessVars.put("stdev", "/sumStat[@type='stdev']");
		accessVars.put("sumStatOther", "/sumStat[@type='other']");
		
		accessVars.put("range", "/valrng/range");
		accessVars.put("valrng", "/valrng");
		
		accessVars.put("catgry", "/catgry");
		accessVars.put("freq", "/catgry/catStat[@type='freq']");
		accessVars.put("percent", "/catgry/catStat[@type='percent']");
		accessVars.put("crosstab", "/catgry/catStat[@type='crosstab']");
		accessVars.put("catStatOther", "/catgry/catStat[@type='other']");
		
		accessVars.put("labl", "/labl");
		accessVars.put("notes", "/notes");

		//Open as xml
		for(String var : vars){	
			String baseXPath = "/codeBook/dataDscr/var[@name='"+var+"']";		
			for(String field : xpaths){
				
				 if(!accessVars.containsKey(field)){
					 System.out.println("Bad access field given: "+field);
					 //Bad field
					 return 500;
				 }
				 
				 String xpath = baseXPath + accessVars.get(field) ;//
				 
				 //Not sure if needed
				 if(xh.hasElement(xpath)){
					if(field.equals("catgry")  || field.equals("freq") 
					|| field.equals("percent") || field.equals("crosstab") 
					|| field.equals("catStatOther")){
						//Need to blank first, then overwrite
						xh.addReplace(xpath+ "/@access", "", true, true, true, true);
						xh.addReplace(xpath+ "/@access", accessLevel, true, true, true, true);
					 }else{
						 xh.addReplace(xpath+ "/@access", accessLevel, false, true, true, true);
					 }
				 }
				 
				 //If min or max is being updated, need to set range to the same restriction
				 if(field.equals("min") || field.equals("max")){
					 xh.addReplace(baseXPath + accessVars.get("range") + "/@access", accessLevel, false, true, true, true);
					 xh.addReplace(baseXPath + accessVars.get("valrng") + "/@access", accessLevel, false, true, true, true);
				 }
				 
				 if(field.equals("range") || field.equals("valrng")){
					 xh.addReplace(baseXPath + accessVars.get("min") + "/@access", accessLevel, false, true, true, true);
					 xh.addReplace(baseXPath + accessVars.get("max") + "/@access", accessLevel, false, true, true, true);
				 }
			}
		}
		
		postXMLHandle(xh,baseHandle,version,"",false,"");
		return 200;
	}
	
	//Session merge
	/**
	 * Edits the doc or stdy desr
	 * @param baseHandle - baseHandle of codebook
	 * @param version - version of codebook
	 * @param field - field to edit
	 * @param value - value to replace with
	 * @param index - index of field
	 * @param delete - boolean to delete or not
	 * @param doesAppend - if the field is being appended
	 * @param user - user id
	 * @return
	 */
	//TODO: cleanup this method
	public int editCover(String baseHandle, String version, String field, String value,
	int index, boolean delete, boolean doesAppend, String user){
	
		String handle = baseHandle+version;

	  	//List of acceptable elements or attributes to edit
		Map<String,String[]> validFields = new HashMap<String,String[]>();
		
		validFields.put("version",
			new String[] {"1","/docDscr/citation/prodStmt/prodDate","Version"});
		validFields.put("docProducer",
			new String[] {"3","/docDscr/citation/prodStmt/producer["+index+"]","Document Producer"});
		validFields.put("stdyProducer",
			new String[] {"3","/stdyDscr/citation/prodStmt/producer["+index+"]","Study Producer"});
		validFields.put("distrbtr",
			new String[] {"3","/stdyDscr/citation/distStmt/distrbtr["+index+"]","Distributor"});
		validFields.put("distrbtrURL",
			new String[] {"4","/stdyDscr/citation/distStmt/distrbtr["+index+"]/@URI","Distributor URL"});
		validFields.put("docCit",
			new String[] {"1","/docDscr/citation/biblCit","Document Citation"});
		validFields.put("docCitURL",
			new String[] {"1","/docDscr/citation/biblCit/ExtLink","Document Citation URL"});
		validFields.put("stdyCit",
			new String[] {"1","/stdyDscr/citation/biblCit","Study Citation"});
		validFields.put("stdyCitURL",
			new String[] {"1","/stdyDscr/citation/biblCit/ExtLink","Study Citation URL"});
		validFields.put("abstract",
			new String[] {"1","/stdyDscr/stdyInfo/abstract","Abstract"});
		validFields.put("confDec",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/confDec","Access Requirements"});
		validFields.put("confDecURL",
			new String[] {"2","/stdyDscr/dataAccs[1]/useStmt/confDec/@URI","Access Requirements URL"});
		validFields.put("accessRstr",
			new String[] {"3","/stdyDscr/dataAccs["+index+"]/useStmt/restrctn","Access Restrictons"});
		validFields.put("accessPermReq",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/specPerm","Access Permission Requirement"});
		validFields.put("citReq",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/citReq","Citation Requirements"});		
		validFields.put("disclaimer",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/disclaimer","Disclamer"});
		validFields.put("contact",
			new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/contact","Contact"});
		validFields.put("method",
			new String[] {"1","/stdyDscr/method/dataColl/collMode","Methodology"});		
		validFields.put("sources",
			new String[] {"3","/stdyDscr/method/dataColl/sources/dataSrc["+index+"]","Sources"});		
		validFields.put("relMat",
			new String[] {"3","/stdyDscr/othrStdyMat/relMat["+index+"]","Related Material"});
		validFields.put("relPubl",
			new String[] {"3","/stdyDscr/othrStdyMat/relPubl["+index+"]","Related Publications"});
		validFields.put("relStdy",
			new String[] {"3","/stdyDscr/othrStdyMat/relStdy["+index+"]","Related Studies"});				
		validFields.put("docSrcBib",
			new String[] {"1","/docDscr/docSrc/biblCit","Document Source Citation"});
		validFields.put("accessCond",
            new String[] {"1","/stdyDscr/dataAccs[1]/useStmt/conditions","Access Conditions"});
		validFields.put("investigator", 
			new String[] {"3", "/stdyDscr/citation/rspStmt/AuthEnty["+index+"]", "Principal Investigator"});
		
		//New fields to get
		validFields.put("titl",
	        new String[] {"1","/docDscr/citation/titlStmt/titl","Title"});
		validFields.put("accessRstrID",
			new String[] {"4","/stdyDscr/dataAccs["+index+"]/@ID","Data Access Level ID"});
		
		//New May 2015
		validFields.put("fileDscrURL",
				new String[] {"4","/fileDscr["+index+"]/@URI","Dataset URL","1"});

		//New as of Jan 2018
		validFields.put("stdyTitl",new String[] {"1","/stdyDscr/citation/titlStmt/titl","Study Title"});

		if(!validFields.containsKey(field)){
			setError("Bad field given");
			return 400;
		}
		
		//Access Level ID must be lowercase alphanumeric
		if(field.equals("accessRstrID")){
			value = value.toLowerCase().replaceAll("[^a-z0-9]", "");
		}
		
		//Update fullname
		if(field.equals("titl")){
			value = value.replaceAll(",", "");
		}
		
		String type = validFields.get(field)[0];
		String path = validFields.get(field)[1];
		boolean replaceChildren = true;
		
		if(type.equals("3") || type.equals("4")){
			if(index <= 0){
				setError("Index required for this field. (Starting from 1)");
				return 400;
			}
		}
		
		//Auto updates version
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		timestamp+= " (auto-generated)";
		XMLHandle xh = null;
		try{
			
			/*
			if(field.equals("version")){
				doesAppend = false;
			}
			*/

			//Remove node
			if(delete){
				xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());
				xh.deleteNode("/codeBook"+path, true);
				BaseX.put(handle, xh.docToString().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>"));
			}else if(doesAppend || value.contains("<") || value.contains(">")){
				xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());
				xh.addReplace("/codeBook"+path, value, doesAppend, true, false, replaceChildren);
				
				BaseX.put(handle, 
					xh.docToString().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>")
					.replaceAll("&lt;ExtLink","<ExtLink")
					.replaceAll("&gt;http",">http")
				);
		
			}else{
				//Can save item by making xquery replace statement
				value = value.replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>");
				String xquery = "let $path := collection('CED2AR/"+handle+"')/codeBook"+path+
				" return replace value of node $path with '"+value+"'";
				BaseX.write(xquery);
			}
			
			String xquery2 = "let $path := collection('CED2AR/"+handle+"')"
			+"/codeBook/docDscr/citation/verStmt/version/@date"
			+" return replace value of node $path with '"+timestamp+"'";
			BaseX.write(xquery2);		
			
			if(Config.getInstance().isGitEnabled()){
				QueryUtil.insertPending(handle, "cover", "/codeBook"+path,user);
			}
			
			//Update fullname
			if(field.equals("titl")){	
				QueryUtil.updateFullName(baseHandle, version, value);
			}

		}finally{
			
		}
		return 200;
	}
	
	//Session merge
	/**
	 * Edits multiple fields in cover a once
	 * @param baseHandle - baseHandle of codebook
	 * @param version - version of codebook
	 * @param paths - fields to edit
	 * @param values - matching values to replace with
	 * @param doesAppend - if appending the fields
	 * @param user - user name 
	 * @return
	 */
	public int editCoverMulti(String baseHandle, String version, 
	ArrayList<String> paths, ArrayList<String> values, boolean doesAppend, String user){
		
		String handle = baseHandle + version;
		
		//Auto updates version
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		timestamp+= " (auto-generated)";
		
		XMLHandle xh = null;
		try{
			xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());		
			for(int i = 0; i < paths.size(); i++){
				String p = paths.get(i);
				boolean append = xh.hasElement("/codeBook/"+p) ? false : true;
				xh.addReplace("/codeBook/"+p, values.get(i), append, true, false, true);
				if(xh.getError() != null){
					setError("Bad arguments xpath fields given: "+xh.getError());
					return 400;  
				}
			}
		
			BaseX.put(handle, xh.docToString().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>"));
			String xquery = "let $path := collection('CED2AR/"+handle+"')"
				+"/codeBook/docDscr/citation/verStmt/version/@date"
				+" return replace value of node $path with '"+timestamp+"'";
			BaseX.write(xquery);
			
			if(Config.getInstance().isGitEnabled()) QueryUtil.insertPending(handle, "cover", "/codeBook", user);
			
		}finally{

		}
		return 200;
	}

	//Session merge
	/**
	 * Edits a variable
	 * @param baseHandle - baseHandle of codebook
	 * @param version - version of codebook
	 * @param var - variable's name
	 * @param field - field to edit
	 * @param value - value to replace with
	 * @param index - first index of variable path
	 * @param index2 - second index of variable path
	 * @param doesAppend - if a new element should be created
	 * @param delete - if the target element should be deleted
	 * @param ip - the ip address of the request
	 * @param user - the user who issued the request
	 * @return
	 */
	public int editVar(String baseHandle, String version, String var, String field, String value,
	int index, int index2, boolean doesAppend, boolean delete, String ip, String user){

		String handle = baseHandle+version;
		//TODO: sanitize fields
		
	  	//List of acceptable elements or attributes to edit
		Map<String,String[]> validFields = new HashMap<String,String[]>();
		
		validFields.put("topAcs", new String[] {"5","/var[@name='"+var+"']/@access","Top Level Access"});
		validFields.put("labl", new String[] {"1","/var[@name='"+var+"']/labl","Label"});
		validFields.put("lablAcs", new String[] {"5","/var[@name='"+var+"']/labl/@access","Label Access"});
		validFields.put("sumStat", new String[] {"5","/var[@name='"+var+"']/sumStat["+index+"]/@access","Summary Statistic Access"});
		validFields.put("valRange", new String[] {"5","/var[@name='"+var+"']/valrng["+index+"]/@access","Value Range Access"});
		validFields.put("range", new String[] {"5","/var[@name='"+var+"']/valrng["+index+"]/range["+index2+"]/@access","Range Access"});
		validFields.put("txt", new String[] {"1","/var[@name='"+var+"']/txt","Full Description"});		
		validFields.put("catgry", new String[] {"1","/var[@name='"+var+"']/catgry["+index+"]","Value Category"});
		validFields.put("catValu", new String[] {"1","/var[@name='"+var+"']/catgry["+index+"]/catValu","Value"});
		validFields.put("val", new String[] {"5","/var[@name='"+var+"']/catgry["+index+"]/@access","Value Access"});
		validFields.put("catLabl", new String[] {"1","/var[@name='"+var+"']/catgry["+index+"]/labl","Value Category Label"});
		validFields.put("catStat", new String[] {"5","/var[@name='"+var+"']/catgry["+index+"]/catStat["+index2+"]/@access","Value Statistic Access"});
		validFields.put("notes", new String[] {"1","/var[@name='"+var+"']/notes["+index+"]","Notes"});
		validFields.put("notesAccs", new String[] {"5","/var[@name='"+var+"']/notes["+index+"]/@access","Note Access"});
		validFields.put("qstn", new String[] {"1","/var[@name='"+var+"']/qstn","Question Text"});
		validFields.put("universe", new String[] {"1","/var[@name='"+var+"']/universe", "Universe"});
		validFields.put("anlysUnit", new String[] {"1", "/var[@name='"+var+"']/anlysUnit", "Analysis Unit"});
		
		//TODO: I think the ip param is irrevelant at this point, but was in the old eapi
		if(!ip.equals("")) logger.debug("Edit request to var from " + ip);
		
		if(!validFields.containsKey(field)){
			setError("Bad field given - '"+field+"'");
			return 400;
		}

		String path = validFields.get(field)[1];
		 	
		//Auto updates version
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		timestamp+= " (auto-generated)";
		
		if(doesAppend || delete || validFields.get(field)[0].equals("5") 
		|| value.contains("<") || value.contains(">")){
			XMLHandle xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());
	
			if(doesAppend && field.equals("catLabl")){
				//insert catvalu first
				String xp = "/codeBook/dataDscr" + validFields.get("catValu")[1]; 
				xh.addReplace(xp, Integer.toString(index), true, true, false, true);
			}
			
			if(validFields.get(field)[0].equals("5") || delete){
				//Always need to clear access first
				xh.deleteNode("/codeBook/dataDscr"+path, false);
			}
			
			if(!delete){
				xh.addReplace("/codeBook/dataDscr"+path, value, doesAppend, true, false, true);
			}
			
			BaseX.put(handle, xh.docToString().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>"));
		}else{
			//Can save item by making xquery replace statement
			value = value.replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>");
			String xquery = "let $path := collection('CED2AR/"+handle+"')/codeBook/dataDscr"+path+
			" return replace value of node $path with '"+value+"'";
			BaseX.write(xquery);			
		}
		
		String xquery2 = "let $path := collection('CED2AR/"+handle+"')"
		+"/codeBook/docDscr/citation/verStmt/version/@date"
		+" return replace value of node $path with '"+timestamp+"'";
		BaseX.write(xquery2);		
		
		if(Config.getInstance().isGitEnabled()) 
			QueryUtil.insertPending(handle, "var", "/codeBook/dataDscr"+path,var,user);

		return 200;
	}
	
	//Session merge
	/**
	 * Edits multipart fields in a variable 
	 * @param baseHandle
	 * @param version
	 * @param var
	 * @param paths
	 * @param values
	 * @param doesAppend
	 * @param user
	 * @return
	 */
	public int editVarMulti(String baseHandle, String version, String var,
	ArrayList<String> paths, ArrayList<String> values, boolean doesAppend, String user){
		
		String handle = baseHandle+version;
		
		//Auto updates version
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		timestamp+= " (auto-generated)";
		
		XMLHandle xh = null;
		try{
			xh = new XMLHandle(BaseX.get(handle),Config.getInstance().getSchemaURI());		
			
			for(int i = 0; i < paths.size(); i++){
				String p = paths.get(i);
				boolean append = doesAppend;
				if(p.contains("@")) append = true;
				xh.addReplace("/codeBook/dataDscr/var[@name='"+var+"']/"+p, values.get(i), append, true, false, true);
				if(xh.getError() != null){
					setError("Bad arguments xpath fields given: "+xh.getError());
					return 400;  
				}
			}
		
			BaseX.put(handle, xh.docToString().replaceAll("(&lt;)([^-\\s]+?)(&gt;)", "<$2>"));
			String xquery = "let $path := collection('CED2AR/"+handle+"')"
				+"/codeBook/docDscr/citation/verStmt/version/@date"
				+" return replace value of node $path with '"+timestamp+"'";
			BaseX.write(xquery);
			
			if(Config.getInstance().isGitEnabled())
				QueryUtil.insertPending(handle, "var", "/codeBook/dataDscr/var[@name="+var+"]",var,user);
			
		}finally{
			
		}
		return 200;
	}
	
	/**
	 * Edits or adds a variable group
	 * @param baseHandle
	 * @param version
	 * @param id
	 * @param name
	 * @param labl
	 * @param txt
	 * @return
	 */
	public int editVarGrp(String baseHandle, String version, String id, 
	String name, String labl, String txt){
		//TODO: versioning for this
		String handle = baseHandle+version;
		String replaceStmt = "";	
		
		if(!name.equals("")){
			replaceStmt += replaceStmt.equals("") ? "" : ", ";
			replaceStmt += "replace value of node $c/@name with '"+name+"'";
		}
		
		if(!labl.equals("")){
			replaceStmt += replaceStmt.equals("") ? "" : ", ";
			replaceStmt += "replace value of node $c/labl with '"+labl+"'";
		}
		
		if(!txt.equals("")){
			replaceStmt += replaceStmt.equals("") ? "" : ", ";
			replaceStmt += "replace value of node $c/txt with '"+txt+"'";
		}

		String xquery = "let $d := collection('CED2AR/"+handle+"')/codeBook/dataDscr"
		+" let $g := $d/varGrp[@ID='"+id+"']"
		+" return if($g) then"
		+" let $x:= copy $c := $g modify("+replaceStmt+") return $c"
		+" return replace node $g with $x"
		+" else insert node element varGrp {"
		+" attribute ID {'"+id+"'},"
		+" attribute name {'"+name+"'},"
		+" element labl {'"+labl+"'},"
		+" element txt {'"+txt+"'}"
		+" } as first into $d";
		BaseX.write(xquery);

		return 200;
	}
	
	/**
	 * Deletes a variable group 
	 * @param baseHandle
	 * @param version
	 * @param id
	 * @return
	 */
	public int deleteVarGrp(String baseHandle, String version, String id){
		String handle = baseHandle + version;
		String xquery = "let $n := collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp[@ID='"+id+"'] "+
		"return delete node $n";
		BaseX.write(xquery);
		return 200;
	}
	
	public int editVarGroupVars(String baseHandle, String version, String id, String vars,
	boolean append, boolean delete){

		//TODO: Check if threadsafe 
		String handle = baseHandle + version;
		
		String xquery = "let $g := collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp[@ID='"+id+"']";
		if(delete){
			xquery +=" return data($g/@var)";
			String curVar = BaseX.getXML(xquery).trim();
			 
			List<String> varList = new ArrayList<String>(Arrays.asList(curVar.split(" ")));
			List<String> varListR = new ArrayList<String>(Arrays.asList(vars.split(" ")));
			
			//TODO: Stringbuilder?
			String newVars = "";
			for(String var : varList){
				if(!varListR.contains(var)) newVars += newVars.equals("")? var : " "+var; 
			}

			String xquery2 = "let $g := collection('CED2AR/"+handle+"')/codeBook/dataDscr/varGrp[@ID='"+id+"']"
			+" let $cur := $g/@vars return replace value of node $g/@var with '"+newVars.trim()+"'";
			
			BaseX.write(xquery2);	
			return 200;
		}
		
		if(append){
			xquery+=" let $v := string-join((data($g/@var),'"+vars+"'),' ')";
		}else{
			xquery+="let $v := data('"+vars+"')";
		}
		
		xquery +="return if($g/@var) then "
			+ "replace value of node $g/@var with $v "
			+ "else insert node attribute var {$v} into $g";
		
		BaseX.write(xquery);
		return 200;
	}
	
	/**
	 * Sets the use of a codebook to  supported, default, or deprecated
	 * @param baseHandle
	 * @param version
	 * @param use
	 * @return
	 */
	public int editCodebookUse(String baseHandle, String version, String use){
		
	    if(baseHandle.equals("") || version.equals("")){
	    	//TODO: Make this check a method somewhere;
	    	setError("Basehandle and version cannot be empty");
	    	return 400;
	    }
		
		if(!use.equals("supported") && !use.equals("default") && !use.equals("deprecated")){
			setError("Invalid arguments. Required: use = [ supported | default | deprecated ]");
			return 400;
		}

		QueryUtil.setUse(baseHandle, version, use);
		return 200;
	}
	
//Utilties
	
	private void setError(String s){
		ERROR = s;
	}
	
	public String getError(){
		return ERROR;
	}	
}