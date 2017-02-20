package edu.ncrn.cornell.ced2ar.ei.controllers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.Fetch;
import edu.ncrn.cornell.ced2ar.api.rest.queries.CodebookData;
import edu.ncrn.cornell.ced2ar.eapi.PDFGenerator;
import edu.ncrn.cornell.ced2ar.eapi.QueryUtil;
import edu.ncrn.cornell.ced2ar.eapi.concurency.*;
import edu.ncrn.cornell.ced2ar.eapi.rest.queries.EditCodebookData;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;

/**
 *Handles editing codebooks and variables
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class EditCodebooks {
	
	private static final Logger logger = Logger.getLogger(EditCodebooks.class);
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Loader loader;
	
	@Autowired
	private Config config;
	
//Utilties 	
	//TODO: load valid fields from properties file
	private Map<String,String[]> getVarFields(String var, String index, String index2){
		Map<String,String[]> validFields = new WeakHashMap<String,String[]>();
		validFields.put("topAcs", new String[] {"5","/codeBook/var[@name='"+var+"']/@access","Top Level Access", "1"});
		validFields.put("labl", new String[] {"1","/codeBook/var[@name='"+var+"']/labl","Label","1"});
		validFields.put("lablAcs", new String[] {"5","/codeBook/var[@name='"+var+"']/labl/@access","Label Access", "1"});
		validFields.put("sumStat", new String[] {"5","/codeBook/var[@name='"+var+"']/sumStat["+index+"]/@access","Summary Statistic Access", "1"});
		validFields.put("valRange", new String[] {"5","/codeBook/var[@name='"+var+"']/valrng["+index+"]/@access","Value Range Access", "1"});
		validFields.put("range", new String[] {"5","/codeBook/var[@name='"+var+"']/valrng["+index+"]/range["+index2+"]/@access","Range Access", "1"});
		validFields.put("txt", new String[] {"1","/codeBook/var/txt","Full Description", "0"});
		validFields.put("val", new String[] {"5","/codeBook/var/catgry["+index+"]/@access","Value Access", "1"});
		validFields.put("catValu", new String[] {"1","/codeBook/var/catgry["+index+"]/catValu","Value","1"});
		validFields.put("catLabl", new String[] {"1","codeBook/var[@name='"+var+"']/catgry["+index+"]/labl","Category Value Label","1"});
		validFields.put("catStat", new String[] {"5","/codeBook/var/catgry["+index+"]/catStat["+index2+"]/@access","Value Statistic Access", "1"});
		validFields.put("notes", new String[] {"1","/codeBook/var[@name='"+var+"']/notes["+index+"]","Notes","0"});
		validFields.put("notesAccs", new String[] {"5","/codeBook/var/notes["+index+"]/@access","Note Access", "1"});
		validFields.put("qstn", new String[] {"1","/codeBook/var/qstn","Question Text","0"});
		validFields.put("universe", new String[] {"1","/codeBook/var/universe", "Universe","0"});
		validFields.put("anlysUnit", new String[] {"1","/codeBook/var/anlysUnit", "Analysis Unit", "0"});
		
		return validFields;
	}
	
	private Map<String,String[]> getGroupedTitleFields(String[] index){
		Map<String,String[]> validFields = new WeakHashMap<String,String[]>();
		validFields.put("docProducer",
		new String[]{
			"docDscr/citation/prodStmt/producer["+index[0]+"]",
			"docDscr/citation/prodStmt/producer["+index[0]+"]/@abbr", 
			"docDscr/citation/prodStmt/producer["+index[0]+"]/@affiliation",
			"docDscr/citation/prodStmt/producer["+index[0]+"]/@role"
		});
		validFields.put("stdyProducer",
		new String[]{
			"stdyDscr/citation/prodStmt/producer["+index[0]+"]",
			"stdyDscr/citation/prodStmt/producer["+index[0]+"]/@abbr", 
			"stdyDscr/citation/prodStmt/producer["+index[0]+"]/@affiliation",
			"stdyDscr/citation/prodStmt/producer["+index[0]+"]/@role"
		});
		return validFields;
	}
	
	private Map<String,String[]> getGroupedTitleFieldsLabels(String[] index){
		Map<String,String[]> validFields = new WeakHashMap<String,String[]>();
		validFields.put("docProducer",
		new String[]{
			"Producer",
			"Abbrevation", 
			"Affiliation",
			"Role"
		});
		validFields.put("stdyProducer",
		new String[]{
			"Producer",
			"Abbrevation", 
			"Affiliation",
			"Role"
		});
		return validFields;
	}
	
	private Map<String,String[]> getGroupedVarFields(String[] index){
		Map<String,String[]> validFields = new WeakHashMap<String,String[]>();
		validFields.put("concept",
		new String[]{
			"concept["+index[0]+"]",
			"concept["+index[0]+"]/@vocab", 
			"concept["+index[0]+"]/@vocabURI"
		});
		return validFields;
	}
	
	private Map<String,String[]> getGroupedVarFieldsLabels(String[] index){
		Map<String,String[]> validFields = new WeakHashMap<String,String[]>();
		validFields.put("concept",
		new String[]{
			"Concept",
			"Vocabulary", 
			"Vocabulary URI"
		});
		return validFields;
	}
	
	private Map<String,String[]> getTitleFields(String index){
		Map<String,String[]> validFields = new WeakHashMap<String,String[]>();
		validFields.put("version",
		new String[] {"1","/codeBook/docDscr/citation/prodStmt/prodDate","Version","1"});
		validFields.put("docProducer",
		new String[] {"3","/codeBook/docDscr/citation/prodStmt/producer["+index+"]","Document Producer","1"});
		validFields.put("stdyProducer",
		new String[] {"3","/codeBook/stdyDscr/citation/prodStmt/producer["+index+"]","Study Producer","1"});
		validFields.put("distrbtr",
		new String[] {"3","/codeBook/stdyDscr/citation/distStmt/distrbtr["+index+"]","Distributor","1"});
		validFields.put("distrbtrURL",
		new String[] {"4","/codeBook/stdyDscr/citation/distStmt/distrbtr["+index+"]/@URI","Distributor URL","1"});
		validFields.put("docCit",
		new String[] {"1","/codeBook/docDscr/citation/biblCit","Document Citation","0"});
		validFields.put("docCitURL",
		new String[] {"1","/codeBook/docDscr/citation/biblCit/ExtLink","Document Citation URL","1"});
		validFields.put("stdyCit",
		new String[] {"1","/codeBook/stdyDscr/citation/biblCit","Study Citation","0"});
		validFields.put("stdyCitURL",
		new String[] {"1","/codeBook/stdyDscr/citation/biblCit/ExtLink","Study Citation URL","0"});
		validFields.put("abstract",
		new String[] {"1","/codeBook/stdyDscr/stdyInfo/abstract","Abstract","0"});
		validFields.put("confDec",
		new String[] {"1","/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec","Access Requirements","0"});
		validFields.put("confDecURL",
		new String[] {"2","/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec/@URI","Access Requirements URL","1"});
		validFields.put("accessRstr",
		new String[] {"3","/codeBook/stdyDscr/dataAccs["+index+"]/useStmt/restrctn","Access Restrictons","0"});
		validFields.put("accessCond",
		new String[] {"1","/codeBook/stdyDscr/dataAccs[1]/useStmt/conditions","Access Conditions","0"});
		validFields.put("accessPermReq",
		new String[] {"1","/codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm","Access Permission Requirement","0"});
		validFields.put("citReq",
		new String[] {"1","/codeBook/stdyDscr/dataAccs[1]/useStmt/citReq","Citation Requirements","0"});		
		validFields.put("disclaimer",
		new String[] {"1","/codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer","Disclaimer","0"});
		validFields.put("contact",
		new String[] {"1","/codeBook/stdyDscr/dataAccs[1]/useStmt/contact","Contact","0"});
		validFields.put("method",
		new String[] {"1","/codeBook/stdyDscr/method/dataColl/collMode","Methodology","0"});		
		validFields.put("sources",
		new String[] {"3","/codeBook/stdyDscr/method/dataColl/sources/dataSrc["+index+"]","Sources","0"});		
		validFields.put("relMat",
		new String[] {"3","/codeBook/stdyDscr/othrStdyMat/relMat["+index+"]","Related Material","0"});
		validFields.put("relPubl",
		new String[] {"3","/codeBook/stdyDscr/othrStdyMat/relPubl["+index+"]","Related Publications","0"});
		validFields.put("relStdy",
		new String[] {"3","/codeBook/stdyDscr/othrStdyMat/relStdy["+index+"]","Related Studies","0"});				
		validFields.put("docSrcBib",
		new String[] {"1","/codeBook/docDscr/docSrc/biblCit","Document Source Citation","0"});
		validFields.put("investigator",
		new String[] {"3", "/codeBook/stdyDscr/citation/rspStmt/AuthEnty["+index+"]","Principal Investigator", "1"});
		
		//New as of Oct 2014		
		validFields.put("titl",
		new String[] {"1","/codeBook/docDscr/citation/titlStmt/titl","Title","1"});
		validFields.put("accessRstrID",
		new String[] {"4","/codeBook/stdyDscr/dataAccs["+index+"]/@ID","Data Access Level ID","1"});
		
		//New as of May 2015
		validFields.put("fileDscrURL",
		new String[] {"4","/codeBook/fileDscr["+index+"]/@URI","Dataset URL","1"});
		
		return validFields;
	}
	
	/**
	 *Retrieves a specific title page and caches it
	 * @param handle String the codebook to retrieve
	 * @return String the retrieved codebook
	 */
	//TODO: Remember to clear cache when updating
	@Cacheable( value="codebook", key="handle")
	protected String getTitlePage(String handle){		
		CodebookData codebookData = new CodebookData();
		String xml = codebookData.getTitlePage(handle,"XML");
		xml = xml.replaceAll("(\\S)(<ExtLink)", "$1 <ExtLink");
		xml = xml.replaceAll("(</ExtLink>)(\\S)", "</ExtLink> $2");
		return xml;
	}
	
	/**
	 * HTMLcheck sanitizes the editing interface input by checking
	 * for legal HTML tags, and that the tags are well formed and
	 * closed, etc.
	 * @param inp  the input string to be cleaned
	 * @return  the cleaned string
	 * TODO: Might want to move somewhere else in a utility class perhaps
	 */
	public static String HTMLcheck(String inp, boolean remove){
		//construct cleaner
		Whitelist wl = Whitelist.none();

		//replace DDI HTML with regular HTML
		inp = htmlRegClean(inp);
			
		//define safe tags, clean input
		if(!remove){
			wl = wl.addTags("em","p","li","ul","a","xhtml:li","xhtml:ul");
			wl = wl.addAttributes("a","href","title","target");
		}
		
		String safe = Jsoup.clean(inp, wl);
		
		if(!remove){
			safe = safe.replaceAll("&quot;", "\"").replaceAll("&amp;", "&");
		}
		return safe;
	}
	
	/**
	 * Parses Regular HTML back into DDI HTML
	 * @param value
	 * @return
	 */
	public static String htmlDDIClean(String value){
		value = value.replaceAll("target=\".+\"", "");
		value = value.replaceAll("title=\".+\"", "");
		value = value.replaceAll("<br.*?>", "").replaceAll("<br.*?/>", "");
		value = value.replaceAll("title=\".+\"", "");
		value = value.replaceAll("<a href=","<ExtLink URI=").replaceAll("</a>","</ExtLink>");
		value = value.replaceAll("<ul>", "<xhtml:ul>").replaceAll("</ul>", "</xhtml:ul>");
		value = value.replaceAll("<li>", "<xhtml:li>").replaceAll("</li>", "</xhtml:li>");
		value = value.replaceAll("<em>", "<emph>").replaceAll("</em>", "</emph>");
		value = value.replaceAll("&nbsp;", " ");
		return value;
	}
	
	/**
	 *Removes special characters from input that may have been pasted 
	 */
	private static String specialCharClean(String s){	
		try{
			s = new String(Charset.forName("UTF-8").encode(s).array(), "UTF-8");
		}catch(UnsupportedEncodingException e){}
		s = s.replaceAll("&rdquo;", "\"").replaceAll("&ldquo;", "\"");
		s = s.replaceAll("â€œ","\"").replaceAll("â€�", "\"");
		s = s.replaceAll("&rsquo;", "'");
		s = s.replaceAll("&.+;", "");
		s = s.replaceAll("<br *?/>", " ");//br tags often added in after pasting
		
		return s.trim();
	}
	
	/**
	 * Parses DDI HTML into Regular HTML
	 * @param value
	 * @return
	 */
	private static String htmlRegClean(String value){	
		value = value.replaceAll("<xhtml:","<").replaceAll("</xhtml:","</");
		value = value.replaceAll("<ExtLink URI=","<a href=").replaceAll("</ExtLink>","</a>");
		value = value.replaceAll("<emph>", "<em>").replaceAll("</emph>", "</em>");
		return value;
	}
	
	/**
	 * Checks input for links and if it finds them, makes sure they are well formed
	 * @param field the field being edited; for certain fields we are given only the link without the URI prefix
	 * @param value the input itself, being either only a link or a large block containing 0 or more links
	 * @return boolean value of false if any malformed links are found
	 */
	private static boolean linkCheck(String field, String value){
		ArrayList<String> links = new ArrayList<String>();
		//String s = "";
		
		if(field.equals("distrbtrURL") || field.equals("confDecURL")){
			links.add(value);
		}
		else if(value.contains("URI=")){
			int i = 0;
			int start, end;
			start = value.indexOf("URI=", i);
			do{
				start += 5;
				end = value.indexOf("\"", start);
				links.add(value.substring(start,end));
				i = end;
				start = value.indexOf("URI=", i+1);
			}while(start > 0);
		}
		
		String linkExpr = "(http://|mailto:|https://)[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?";
		String mailExpr = "^(mailto:)?([a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4})?$";
		for(String link : links){
			if(!link.matches(linkExpr) && !link.matches(mailExpr)) return false;
		}	
		return true;
	}
	
	/**
	 * Retrieves the current field from the titlepage
	 * @param baseHandle
	 * @param version
	 * @param field
	 * @param index
	 * @param append
	 * @return
	 */
	private String[] getCurrentTitleField(String baseHandle, String version, String field, 
	String index, boolean append){
		String currentValue = "";
		String[] path = null;
		String handle = baseHandle+version;
		if(field.equals(""))
			return null;

		String xml = getTitlePage(handle);

		Map<String,String[]> validFields = null;
					
		try{
			validFields = getTitleFields(index);
			Parser xp = new Parser(xml);
			if(validFields.containsKey(field)){
				
				path = validFields.get(field);
				
				if(path[0].equals("2") || path[0].equals("4")){
					int pos = path[1].lastIndexOf("/");
					String p1 = path[1].substring(0,pos);
					String p2 = path[1].substring(pos,path[1].length()).replace("/@", "");
					currentValue = xp.getAttrValue(p1, p2);
				}else if(append){
					currentValue = "";
				}else{
					try{
						currentValue = xp.getNode(path[1]);
					}catch(NullPointerException e){
						currentValue = "";
					}
					
				}
				
				if(!append){ 
					currentValue = HTMLcheck(currentValue,false);							
				}	
			}
		}finally{
			validFields.clear();
		}
		
		return new String[] {currentValue,path[3],path[2]};
	}
	
	//TODO:Finish
	private String[] getVarPaths(String var, String field, String index, String index2, boolean append){

		//All valid fields to edit
		Map<String,String[]> validFields = null;
		try{	
			validFields = getVarFields(var,index,index2);
			
			if(validFields.containsKey(field)){ 
				return validFields.get(field);
			}
			
		}finally{
			validFields.clear();
		}
		return null;
	}
	
//Concurrency
	/**
	 * Checks if a field has pending changes
	 * @param baseHandle
	 * @param version
	 * @param session
	 * @param currentValue
	 * @param xpath
	 */
	private int checkPendingChanges(String baseHandle, String version, HttpSession session, 
	String currentValue, String xpath){

		String handle = baseHandle + version;
		Codebook concurencyCodebook = null;
		if(Codebooks.hasCodebook(handle)){
			concurencyCodebook = Codebooks.getCodebook(handle);
		}else{
			concurencyCodebook = new Codebook(baseHandle, version);
			Codebooks.addCodebook(concurencyCodebook);
		}			
		
		Field concurencyField = null;
		String sessionID = session.getId();
		
		String user = session.getAttribute("userEmail") != null ? 
		(String) session.getAttribute("userEmail") : "anonymous";			
		FieldState fieldState = new FieldState(sessionID, currentValue, user);
		int pendingChanges = concurencyCodebook.pendingChanges(xpath);
		
		if(pendingChanges > 0){
			concurencyField = concurencyCodebook.getField(xpath);
		}else{
			concurencyField = new Field(xpath);
		}
		
		concurencyField.addFieldState(sessionID,fieldState);
		concurencyCodebook.putPendingChange(xpath, concurencyField);
		
		return pendingChanges;
	}
	
	/**
	 * Checks if value has changed since opening editor
	 * @param baseHandle
	 * @param version
	 * @param field
	 * @param index
	 * @param session
	 */
	private void removePending(String baseHandle, String version, HttpSession session, String xpath){
		String handle = baseHandle + version;
		Codebook concurencyCodebook = null;
		
		if(Codebooks.hasCodebook(handle)){
			concurencyCodebook = Codebooks.getCodebook(handle);
		}else{
			concurencyCodebook = new Codebook(baseHandle, version);
			Codebooks.addCodebook(concurencyCodebook);
		}		

		if(!concurencyCodebook.hasPendingChange(xpath)){
			return;
		}	
		
		Field concurencyField = concurencyCodebook.getField(xpath);
		concurencyField.removeFieldState(session.getId());
	
	}
	
	private String getFieldState(String baseHandle, String version, HttpSession session, 
	String field){
		return getFieldState(baseHandle, version, session, field,"");
	}
	
	private String getFieldState(String baseHandle, String version, HttpSession session, 
	String field, String varName){
		
		String handle = baseHandle + version;
	
		Codebook concurencyCodebook = null;
		if(Codebooks.hasCodebook(handle)){
			concurencyCodebook = Codebooks.getCodebook(handle);
		}else{
			concurencyCodebook = new Codebook(baseHandle, version);
			Codebooks.addCodebook(concurencyCodebook);
			//TODO: this condition shouldn't be encountered
		}			
		
		field = varName.equals("") ? field : varName+"."+field;
		
		Field concurencyField = concurencyCodebook.getField(field);
		FieldState fs = concurencyField.getFieldState(session.getId());
		String userValue = fs.getFieldValue();
		
		return userValue;
	}
	
//Codebook Mappings
	/**
	 * Redirects to codeBookUpload
	 * @param model the current model
	 * @return codebooks
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String main(HttpSession session, Model model){	
        return "redirect:/edit/codebooks";
    }
	
	/**
	 * Provides a UI to upload codebook
	 * @param session the current session
	 * @param model the current model
	 * @return the codebooks jsp file
	 */
	@RequestMapping(value = "/edit/codebooks", method = RequestMethod.GET)
	public String codeBookUpload(HttpSession session, HttpServletResponse response, Model model){
		try{			
			String baseURI = loader.getPath() + "/rest/";
			TreeMap<String,String[]> codebooks = loader.getCodebooks(baseURI);
			model.addAttribute("codebooks", codebooks);	
			
			//TODO:To be used later for choosing an existing base handle when uploading a new codebook
			List<String> baseHandles = new ArrayList<String>();
			for(String[] codebook : codebooks.values()){
				if(!baseHandles.contains(codebook[0]))
					baseHandles.add(codebook[0]);
			}
			
			String index = Fetch.get(baseURI+"codebooks","id-type","index");
			context.setAttribute("index", index);
			String path = context.getRealPath("/xslEdit/indexSettings.xsl");
			Parser xp = new Parser(index,path);
					
			model.addAttribute("indexSettings",xp.getData());
			model.addAttribute("subTitl","Edit Settings");
			model.addAttribute("baseHandles",baseHandles);
		}catch(Exception e){
			logger.error(e.getMessage());
			model.addAttribute("error","Could not establish a connection to the database");	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return "/WEB-INF/editViews/codebooksEdit.jsp";
		}		
		return "/WEB-INF/editViews/codebooksEdit.jsp";	
	}

	/**
	 * Display a codebook's titlepage for editing
	 * @param baseHandle
	 * @param version
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}", method = RequestMethod.GET)
	public String editTitlePage(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version,Model model,HttpServletResponse response, HttpSession session){
			 //Temp solution to add trailing slash
			 String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
			 if(!usedURL.endsWith("/")){ 
				 return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/";
			 }
			 
			 String handle = baseHandle+version;
			 if(!loader.hasCodebook(handle)){
				session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
				return "redirect:/";
			 }	
		
			 try{		
				 String hostName = loader.getPath();
				 String xml = getTitlePage(handle);

				 String path = context.getRealPath("/xslEdit/codebookEdit.xsl");//Local file path to find XSL doc
				 Parser xp = new Parser(xml, path, 0);
				 String count = xp.getAttrValue("/codeBook", "variables");
				 if(count != null){					 
					 String codebookTitle =xp.getValue("/codeBook/docDscr/citation/titlStmt/titl");
					 String[][] crumbs = new String[][] {
							 {codebookTitle,""}
					 };
					 
					//Crowdsourcing
					 model.addAttribute("crowdsourceSwitch",Config.getInstance().getCrowdSourcingRole());
					 String remoteURL = Config.getInstance().getRemoteURL() 
					 + "/codebooks/"+baseHandle+"/v/"+version;
					 model.addAttribute("remoteServerURL",remoteURL);
					 
					 String title = xp.getValue("/codeBook/docDscr/citation/titlStmt/titl");
					 model.addAttribute("subTitl","Editing: " + title);
					 model.addAttribute("baseHandle",baseHandle);
					 model.addAttribute("version",version);
					 model.addAttribute("crumbs", crumbs);
					 model.addAttribute("hasMath", true);
					 model.addAttribute("handle", handle);
					 model.addAttribute("codebook", xp.getData());
					 model.addAttribute("count", count);
					 model.addAttribute("codebookTitl",codebookTitle);
					 model.addAttribute("apiURI",hostName+"/rest");
					 return "/WEB-INF/editViews/codebookEdit.jsp";
				}
		 }catch(NullPointerException | NumberFormatException e){	
			 	logger.error(e.getMessage());
				model.addAttribute("error","Could not establish a connection to the database");	
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		 }
		return "redirect:/";
	}	
	
	/**
	 * Displays a specific field in title page for editing (ajax)
	 * Type 1 single element
	 * Type 2 single attribute
	 * Type 3 plural element
	 * Type 4 plural attribute
	 * @param handle the codebook being edited
	 * @param field the field of the cover being edited
	 * @param index the index of the element if there is one
	 * @param append flag indicating whether we are editing an existing element or appending a new one
	 * @param model the current model
	 * @return the ajax view for editing the particular field of the cover
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/edit", method = RequestMethod.GET)
	public String editCodebookD(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,		
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i", defaultValue = "1") String index,
	@RequestParam(value = "a", defaultValue = "0") boolean append, Model model, HttpSession session) {

		String[] currentValue = getCurrentTitleField(baseHandle,version,field,index,append);
		int pendingChanges = checkPendingChanges(baseHandle,version,session,currentValue[0],field);
				
		model.addAttribute("pendingChanges", pendingChanges);
		model.addAttribute("editorType", currentValue[1]);
		model.addAttribute("field", field);
		model.addAttribute("index",index);	
		model.addAttribute("handle", baseHandle+version);	
		model.addAttribute("curVal", currentValue[0]);
		model.addAttribute("title", currentValue[2]);	
		model.addAttribute("append", append);
		model.addAttribute("hasMath",true);
	
		return "/WEB-INF/ajaxViews/codebookEdit.jsp";
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/edit-cancel", method = RequestMethod.GET)
	@ResponseBody
	public void editCodebookCancel(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,		
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i", defaultValue = "1") String index,
	@RequestParam(value = "a", defaultValue = "false") String append, Model model, HttpSession session) {
		removePending(baseHandle,version,session,field);
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/editMulti", method = RequestMethod.GET)
	public String editMultiTitlePage(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,	
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i") String[] index,
	@RequestParam(value = "a", defaultValue = "false") String append, 
	Model model, HttpSession session, HttpServletResponse response)
	{
		Map<String,String[]> validFields = null;
		try{
			String handle = baseHandle + version;
			if(index != null){
				try{
					for(String i : index) Integer.parseInt(i);
				}catch(NumberFormatException e){
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("Bad index request to "+handle+". Should be array of ints. Index is: "+index);
					return "redirect://edit/codebooks/{c}/v/{v}/vars/{var}";
				}
			}
			
			validFields = getGroupedTitleFields(index);
			if(!validFields.containsKey(field)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error("Invalid multi var field: "+field);
				return "redirect://edit/codebooks/{c}/v/{v}/vars/{var}";
			}
			
			String[] fieldLabels = getGroupedTitleFieldsLabels(index).get(field);
			
			String baseURI = loader.getPath();
			String apiURI = baseURI + "/rest/codebooks/"+ handle + "/titlepage";
			String xml = Fetch.getShortXML(apiURI)[0];
			
			ArrayList<String> values = new ArrayList<String>();
			String[] xpaths  = validFields.get(field);
			Parser xp = new Parser(xml);
			for(String xpath : xpaths){
				values.add(xp.getValue2("/codeBook/"+xpath));
			}
			
			//TODO: Limiting chooses to a controlled vocabulary 
			//TODO: Add schema doc or more detailed info for the fields?			
			model.addAttribute("field", field);	
			model.addAttribute("title", fieldLabels[0]);	
			model.addAttribute("values", values);	
			model.addAttribute("labels", fieldLabels);	
			model.addAttribute("append", append);	
			
		}finally{
			validFields.clear();
		}
		return "/WEB-INF/ajaxViews/editMulti.jsp";
	}	
	
	/**
	 * Sends the request to edit field in title page
	 * @param handle the codebook being edited
	 * @param value the new value to be inserted
	 * @param field the field being edited
	 * @param index the index of the element if one is specified
	 * @param append flag indicating whether we are updating an existing element or appending a new one
	 * @param model the current model
	 * @param session the current session
	 * @return a string redirect
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/edit", method = RequestMethod.POST)
	public String editCodebook(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, 
	@RequestParam(value="newValue", required = true) String value, 	
	@RequestParam(value="field", required = true) String field,
	@RequestParam(value = "index", defaultValue = "0") String index, 
	@RequestParam(value = "append", defaultValue = "0") boolean append, 
	Model model, HttpSession session, HttpServletResponse response) throws Exception{
		
		String currentValue = getCurrentTitleField(baseHandle,version,field,index,false)[0];
		String userValue = null;
		
		try{
			userValue = getFieldState(baseHandle, version, session, field);
			if(!userValue.equals(currentValue) && !currentValue.equals("")){
				
				//Change has been made during editing, init diff view
				response.addHeader("conflictingValue", currentValue);
				
				//Removes user's cached changes to allow overwrite
				removePending(baseHandle, version, session, field);
				return "";
			}
		}catch(NullPointerException e){
			//No concurency data cached
		}
		
		Map<String,String[]> validFields = null;
		String[] fieldValues = null;
		try{
			validFields = getTitleFields(index);
			if(validFields.containsKey(field)){ 
				fieldValues = validFields.get(field);	
			}else{
				session.setAttribute("error2", "Invalid request");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/";
			}
		}finally{
			validFields.clear();
		}
		
		switch(fieldValues[3]){
			//HTML
			case "0":
				value = specialCharClean(value);
				if(!linkCheck(field,value))
					throw new Exception("bad HTML");
				String oldval = value;
				value = HTMLcheck(value,false);
				
				if(oldval.replaceAll("[\\s]", "").length() != value.replaceAll("[\\s]", "").length()){
					throw new Exception("bad HTML");
				}

				value = htmlDDIClean(value);
				
			break;
			//Plain Text
			case "1":
				value = value.trim();
				//Access Level ID must be lowercase alphanumeric
				if(field.equals("accessRstrID"))
					value = value.toLowerCase().replaceAll("[^a-z0-9]", "");
				if(!linkCheck(field,value)) throw new Exception("Bad HTML");
				value = HTMLcheck(value,true);
				
			break;
		}

		String user = session.getAttribute("userEmail") != null ? 
		(String) session.getAttribute("userEmail") : "anonymous";

		EditCodebookData editCodebookData = new EditCodebookData();
		boolean delete = false;
		int code = editCodebookData.editCover(baseHandle, version, field, value, 
		Integer.parseInt(index), delete, append, user);
			
		if(code > 0 && code < 400){
			removePending(baseHandle, version, session, field);
			session.setAttribute("info_splash2","Changes Saved");
			//Need to refresh cached codebook data if changing titl
			//I don't remove search cache to maintain performance
			if(field.equals("titl")){
				String baseURI = loader.getPath() + "/rest/";
				session.removeAttribute("fL");
				session.removeAttribute("filter");
				session.removeAttribute("filterShow");
				session.removeAttribute("filterHeader");
				session.removeAttribute("verboseFilter");
				session.removeAttribute("codebooks");
				loader.refreshCodebooks(baseURI);	
				TreeMap<String, String[]> codebooks = loader.getCodebooks(baseURI);
				model.addAttribute("codebooks", codebooks);
			}
		}else if(code == 400){
			session.setAttribute("error2", "There was a problem connecting to the database.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}else{
			session.setAttribute("error2", "Your request could not be completed.\nYour edit may have created an invalid document.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/";
	}

	
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/editMulti", method = RequestMethod.POST)
	public String editCodebookMulti(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,
	@RequestParam(value = "field", defaultValue = "") String field,
	@RequestParam(value = "newValue", defaultValue = "") ArrayList<String> values, 
	@RequestParam(value = "i", defaultValue = "") String[] index,
	@RequestParam(value = "append", defaultValue = "0") boolean append,
	Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String,String[]> validFields = null;
		try{
			String handle = baseHandle + version;
			if(index != null){
				try{
					for(String i : index) Integer.parseInt(i);
				}catch(NumberFormatException e){
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("Bad index request to "+handle+". Should be array of ints. Index is: "+index);
					return "redirect://edit/codebooks/"+baseHandle+"/v/"+version+"/";
				}
			}
			
			validFields = getGroupedTitleFields(index);
			if(!validFields.containsKey(field)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error("Invalid multi var field: "+field);
				return "redirect://edit/codebooks/"+baseHandle+"/v/"+version+"/";
			}			

			String user = "anonymous";
			if(session.getAttribute("userEmail") != null){
				user = (String) session.getAttribute("userEmail");
			}
			
			ArrayList<String> paths = new ArrayList<String>(Arrays.asList(validFields.get(field)));

			int code = -1;
			EditCodebookData editCodebookData = new EditCodebookData();
			code = editCodebookData.editCoverMulti(baseHandle, version, paths, values, append, user);
	
			//Response
			if(code > 0 && code < 400){
				session.setAttribute("info_splash2","Changes Saved");
			}else if(code == 404){ 
				session.setAttribute("error2", "There was a problem connecting to the database.");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}else{
				session.setAttribute("error2", "Your request could not be completed.\nYour edit may have created an invalid document.");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			
		}finally{
			validFields.clear();
		}
		
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version;	
	}
	
	/**
	 * Display dialog to delete titlepage field
	 * @param baseHandle
	 * @param version
	 * @param field
	 * @param index
	 * @param append
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/delete", method = RequestMethod.GET)
	public String deleteCodebookFieldD(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,		
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i", defaultValue = "1") String index,
	@RequestParam(value = "a", defaultValue = "false") String append, Model model) {
		String handle =baseHandle+version;
		if(field.equals(""))
			return "redirect:/c/edit/codebooks/"+baseHandle+"/v/"+version+"/";		
		
		Map<String,String[]> validFields = null;
					
		try{
			validFields = getTitleFields(index);
			if(validFields.containsKey(field)){
				String[] path = validFields.get(field);
				model.addAttribute("field", field);
				model.addAttribute("index",index);	
				model.addAttribute("handle", handle);	
				model.addAttribute("title", path[2]);	
				model.addAttribute("append", append);
				model.addAttribute("hasMath",true);
			}else{
				return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/";	
			}		
		}finally{
			validFields.clear();
		}
		return "/WEB-INF/ajaxViews/codebookFieldDelete.jsp";
	}
	
	/**
	 * Display dialog to delete titlepage field
	 * @param baseHandle
	 * @param version
	 * @param field
	 * @param index
	 * @param append
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/delete", method = RequestMethod.POST)
	public String deleteCodebookField(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,		
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i", defaultValue = "1") int i,
	@RequestParam(value = "a", defaultValue = "false") String append, 
	Model model, HttpSession session, HttpServletResponse response) {
		String handle = baseHandle+version;
		if(field.equals(""))
			return "redirect:/c/edit/codebooks/"+baseHandle+"/v/"+version+"/";		
		
		Map<String,String[]> validFields = null;
					
		try{
			String index = Integer.toString(i);
			validFields = getTitleFields(index);
			if(validFields.containsKey(field)){
				String user = "anonymous";
				
				if(session.getAttribute("userEmail") != null){
					user = (String) session.getAttribute("userEmail");
				}

				EditCodebookData editCodebookData = new EditCodebookData();
				int code = editCodebookData.editCover(baseHandle, version, field, "",
				i, true, false, user);
				
				
				if(code > 0 && code < 400){
					session.setAttribute("info_splash2","Field Removed");
					if(field.equals("titl")){
						String baseURI = loader.getPath() + "/rest/";
						session.removeAttribute("fL");
						session.removeAttribute("filter");
						session.removeAttribute("filterShow");
						session.removeAttribute("filterHeader");
						session.removeAttribute("verboseFilter");
						session.removeAttribute("codebooks");
						loader.refreshCodebooks(baseURI);	
						TreeMap<String, String[]> codebooks = loader.getCodebooks(baseURI);
						model.addAttribute("codebooks", codebooks);
					}
				}else if(code == 400){
					session.setAttribute("error2", "There was a problem connecting to the database.");
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					session.setAttribute("error2", "Your request could not be completed.\nYour edit may have created an invalid document.");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			}else{
				return "redirect:/codebooks/"+handle+"/";	
			}		
		}finally{
			validFields.clear();
		}
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/";
	}
	
	
	/**
	 * Page for releasing data from codebook
	 * @param baseHandle
	 * @param version
	 * @param model
	 * @param session
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/release", method = RequestMethod.GET)
	public String release(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, 
	Model model, HttpSession session, HttpServletResponse response) throws Exception{
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle + version;
		
		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	
		
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}else{
			codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
		}
		
		HashMap<String,String[]> codebooksAccs = Fetch.getCodebooksAccs(baseURI);
		String[] accessLevels = codebooksAccs.get(handle);
		

		String codebookURL = "edit/codebooks/"+baseHandle+"/v/"+version+"/";
		String[][] crumbs = new String[][] {
			{codebooks.get(handle)[4],codebookURL},
			{"Release",""}
		};
		String[] varAccsCount = QueryUtil.getVarAccessCount(handle);
		
		model.addAttribute("accsCount", varAccsCount[0].trim());
		model.addAttribute("count", varAccsCount[1].trim());
		
		model.addAttribute("subTitl", "Release ("+handle.toUpperCase()+")");
		model.addAttribute("crumbs", crumbs);
		model.addAttribute("codebookInfo", codebooks.get(handle));	
		model.addAttribute("accessLevels", accessLevels);
		model.addAttribute("handle", handle);
		model.addAttribute("basehandle", baseHandle);
		model.addAttribute("version", version);
		
		return "/WEB-INF/editViews/release.jsp";
	}
	
	
	/**
	 * An endpoint to edit all the access attributes at once
	 * @param baseHandle
	 * @param version
	 * @param model
	 * @param session
	 * @param response
	 * @return
	 * @throws Exception
	 */
	//TODO: this is effectively depreciated since accessvars2 does the same thing, but better
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/accessvars", method = RequestMethod.GET)
	public String accessAllVars(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, 
	@RequestParam(value = "d-1341904-s", defaultValue = "") String sortCol, //field to sort by 
	@RequestParam(value = "d-1341904-o", defaultValue = "2") String sortDir, //direction to sort by
	Model model, HttpSession session, HttpServletResponse response) throws Exception{
		String handle = baseHandle + version;
		
		//Temp solution to remove trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(usedURL.endsWith("/")){ 
			return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/accessvars";
		}
		
		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	
		
		String baseURI = loader.getPath() + "/rest/";
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}else{
			codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
		}
		
		HashMap<String,String[]> codebooksAccs = Fetch.getCodebooksAccs(baseURI);
		String[] accessLevels = codebooksAccs.get(handle);
		
		//Parsing sort dir to int, and subtracting one to line up with indexing
		//Does not use cacher class
		String apiURI = baseURI+"codebooks/"+handle+"/variables";
		int sc = sortCol.equals("") ? -1 : Integer.parseInt(sortCol) -1;
		boolean rs = sortDir.equals("2") ? false : true;//If sorting is reversed
		String[] data = Fetch.getShortCSV(apiURI);
		Parser xp = new Parser(data[0]);
		List<String[]> results = xp.getDisplayTagDataAccs(sc,rs);
		
		String codebookURL = "edit/codebooks/"+baseHandle+"/v/"+version+"/";
		String[][] crumbs = new String[][] {
			{codebooks.get(handle)[4],codebookURL},
			{"Release",codebookURL+"release"},
			{"Apply Access Levels",""}
		};
		
		model.addAttribute("data",	results);
		model.addAttribute("count", data[1]);
		model.addAttribute("accessLevels", accessLevels);
		model.addAttribute("crumbs", crumbs);
		model.addAttribute("codebookInfo", codebooks.get(handle));	
		model.addAttribute("subTitl","Variable Access ("+handle.toUpperCase()+")");
		model.addAttribute("handle", handle);
		model.addAttribute("basehandle", baseHandle);
		model.addAttribute("version", version);

		return "/WEB-INF/editViews/accessVars.jsp";
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/accessvars", method = RequestMethod.POST)
	public String accessAllVarsP(@PathVariable(value = "c") String baseHandle,
		@PathVariable(value = "v") String version,
		@RequestParam(value = "sa", defaultValue = "0") boolean all,
		@RequestParam(value = "cc", defaultValue = "") String[] variables,
		@RequestParam(value = "accsLevels", defaultValue = "") String access, HttpSession session){
		if(variables.length == 0){
			return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/accessvars";
		}
		try{
			EditCodebookData editCodebookData = new EditCodebookData();
			int rep = editCodebookData.setAccessLevels(baseHandle, version, access,  variables, all);
			if(rep < 400){
				session.setAttribute("info_splash", "Access levels updated");
			}else{
				throw new NullPointerException("Response code was: " + rep);
			}
		}catch(NullPointerException e){
			session.setAttribute("error", "Problem updating access levels");
			logger.error("Problem updating access levels" + e.getMessage());
		}
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/accessvars";
	}
	
//New Access Levels
	
	/**
	 * An endpoint to edit all the access attributes at once
	 * @param baseHandle
	 * @param version
	 * @param model
	 * @param session
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/accessvars2", method = RequestMethod.GET)
	public String accessAllVars2(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, 
	@RequestParam(value = "d-1341904-s", defaultValue = "") String sortCol, //field to sort by 
	@RequestParam(value = "d-1341904-o", defaultValue = "2") String sortDir, //direction to sort by
	Model model, HttpSession session, HttpServletResponse response) throws Exception{
		String handle = baseHandle + version;
		
		//Temp solution to remove trailing slash
		String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if(usedURL.endsWith("/")){ 
			return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/accessvars2";
		}
		
		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	
		
		String baseURI = loader.getPath() + "/rest/";
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}else{
			codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
		}
		
		HashMap<String,String[]> codebooksAccs = Fetch.getCodebooksAccs(baseURI);
		String[] accessLevels = codebooksAccs.get(handle);
		
		//Parsing sort dir to int, and subtracting one to line up with indexing
		//Does not use cacher class
		String apiURI = baseURI+"codebooks/"+handle+"/variables";
		int sc = sortCol.equals("") ? -1 : Integer.parseInt(sortCol) -1;
		boolean rs = sortDir.equals("2") ? false : true;//If sorting is reversed
		String[] data = Fetch.getShortCSV(apiURI);
		Parser xp = new Parser(data[0]);
		List<String[]> results = xp.getDisplayTagDataAccs(sc,rs);
		
		String codebookURL = "edit/codebooks/"+baseHandle+"/v/"+version+"/";
		String[][] crumbs = new String[][] {
			{codebooks.get(handle)[4],codebookURL},
			{"Release",codebookURL+"release"},
			{"Apply Access Levels",""}
		};
		
		model.addAttribute("data",	results);
		model.addAttribute("count", data[1]);
		model.addAttribute("accessLevels", accessLevels);
		model.addAttribute("crumbs", crumbs);
		model.addAttribute("codebookInfo", codebooks.get(handle));	
		model.addAttribute("subTitl","Variable Access ("+handle.toUpperCase()+")");
		model.addAttribute("handle", handle);
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version", version);
		return "/WEB-INF/editViews/accessVars2.jsp";
	}

	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/accessvars2", method = RequestMethod.POST)
	@ResponseBody
	public String accessAllVarsP2(@PathVariable(value = "c") String baseHandle,
		@PathVariable(value = "v") String version,
		@RequestParam(value = "vars", defaultValue = "") String[] vars,
		@RequestParam(value = "subpaths", defaultValue = "") String[] subpaths,
		@RequestParam(value = "accsLevels", defaultValue = "") String accessLevel,
		@RequestParam(value = "subpaths_all",defaultValue = "false") boolean changeAll){

		//TODO: Switch for top level
		EditCodebookData editCodebookData = new EditCodebookData();	
		if(changeAll){
			editCodebookData.setAccessLevelsTop(baseHandle, version, vars, accessLevel);
		}
		//TODO: Should be change sub level as well?
		editCodebookData.setAccessLevels2(baseHandle, version, vars, subpaths, accessLevel);

		return "";
	}
	

		
	@RequestMapping(value = "/edit/codebooks/generatepdf", method = RequestMethod.GET)
	@ResponseBody    
	public String refreshPDFs(){
			PDFGenerator pg = new PDFGenerator();
			pg.setBaseURL(loader.getPath()+"/");
			pg.setContext(context);
			pg.generatePDF();
	        return "Done";        
	 }
	
	/**
	 * Page for releasing data from codebook
	 * @param baseHandle
	 * @param version
	 * @param model
	 * @param session
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/score", method = RequestMethod.GET)
	
	public String score(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, Model model, 
    HttpSession session, HttpServletResponse response) throws Exception{
		String baseURI = loader.getPath() + "/rest/";
		String handle = baseHandle + version;
		
		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	
		
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}else{
			codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
		}

		JSONObject scores = new JSONObject(Fetch.getJson(baseURI+"codebooks/"+handle+"/score")[0]);
		JSONObject vars = (JSONObject) scores.get("variables");
		
		JSONArray jsonLabl = (JSONArray) vars.get("labl");
		JSONArray jsonTxt = (JSONArray) vars.get("txt");
		JSONArray jsonCat = (JSONArray) vars.get("cat");
		JSONArray jsonSumStat = (JSONArray) vars.get("sumStat");	

		ArrayList<String> lablMissing = new ArrayList<String>();
		if(jsonTxt.length() > 0){
			for(int i=0; i<jsonLabl.length(); i++) {
				lablMissing.add(jsonLabl.getString(i));
			}
		}
		
		SortedMap<String,String> txtMissing = new TreeMap<String,String>();
		if(jsonTxt.length() > 0){
			for(int i=0; i<jsonTxt.length(); i++) {
				JSONObject txt = (JSONObject) jsonTxt.get(i);
				txtMissing.put(txt.getString("name"),txt.getString("length"));
			}
		}
		
		ArrayList<String> catMissing = new ArrayList<String>();
		if(jsonTxt.length() > 0){
			for(int i=0; i<jsonCat.length(); i++) {
				catMissing.add(jsonCat.getString(i));
			}
		}
		
		ArrayList<String> sumStatMissing = new ArrayList<String>();
		if(jsonTxt.length() > 0){
			for(int i=0; i<jsonSumStat.length(); i++) {
				sumStatMissing.add(jsonSumStat.getString(i));
			}
		}
		
		JSONObject vs = (JSONObject) vars.getJSONObject("scores");
		double lablScore = (double) vs.get("labl");
		double txtScore = (double) vs.get("txt");
		double catScore = (double) vs.get("cat");
		double sumStatScore = (double) vs.get("sumStat");
		double varTotalScore = (double) vs.get("overall");
		
		JSONObject ts = (JSONObject) scores.get("titlePage");
		JSONObject os = (JSONObject) scores.get("overallScore");
		String overallScore = os.get("total").toString();
		
		HashMap<String,Object> titlePageScores = new ObjectMapper().readValue(ts.toString(), HashMap.class);
		
		String codebookURL = "edit/codebooks/"+baseHandle+"/v/"+version+"/";
		String[][] crumbs = new String[][] {
			{codebooks.get(handle)[4],codebookURL},
			{"Score",""}
		};	
		
		//Adds scoring for prov
		//TODO: Implement here or in details view
		/*
		if(config.getDevFeatureProv()){
			
		}*/
		
		model.addAttribute("titlePageScores", titlePageScores);
		model.addAttribute("overallScore", overallScore);
		
		model.addAttribute("lablScore", lablScore);
		model.addAttribute("txtScore", txtScore);
		model.addAttribute("catScore", catScore );
		model.addAttribute("sumStatScore", sumStatScore);
		model.addAttribute("varTotalScore", varTotalScore);
		
		model.addAttribute("lablMissing", lablMissing);
		model.addAttribute("txtMissing", txtMissing);
		model.addAttribute("catMissing", catMissing);
		model.addAttribute("sumStatMissing", sumStatMissing);
			
		model.addAttribute("subTitl", "Scoring ("+handle.toUpperCase()+")");
		model.addAttribute("crumbs", crumbs);
		model.addAttribute("handle", handle);
		model.addAttribute("basehandle", baseHandle);
		model.addAttribute("version", version);
		
		return "/WEB-INF/editViews/score.jsp";
	}
	
//Variable Mappings
	
	/**
	 * Displays a specific variable for a codebook to be edited
	 * @param variable the variable to be displayed
	 * @param handle the codebook that contains the variable
	 * @param print print option
	 * @param model the current model
	 * @return the URL displaying the given variable
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{n}", method = RequestMethod.GET)
	public String showVariable(@PathVariable(value = "n") String variable,Model model,
	@PathVariable(value = "c") String baseHandle, @PathVariable(value = "v") String version, HttpServletResponse response){
		 //Temp solution to remove trailing slash
		 String usedURL = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		 if(usedURL.endsWith("/")){ 
			 return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/vars/"+variable;
		 }
		
		 String handle = baseHandle + version;
		 model.addAttribute("hasMath", true);

		 String apiURI = loader.getPath() + "/rest/codebooks/"+ handle + "/variables/"+variable;
		 try{
			 String xml = Fetch.getShortXML(apiURI)[0];
			 
			 if(!Parser.containsNode(xml,"//var"))
				 return "redirect:";

			 String path = context.getRealPath("/xslEdit/variableEdit.xsl");//Local file path to find XSL doc
			 Parser xp = new Parser(xml, path, 0);

			 String codebookName = xp.getValue("/codeBook//titl");
			 String codebookURL = "edit/codebooks/"+baseHandle+"/v/"+version+"/";
			 String[][] crumbs = new String[][]{
					 {codebookName,codebookURL},
					 {variable,""}
			 };
			 String formatedVarName = variable.substring(0,1).toUpperCase() + variable.substring(1).toLowerCase();
			 
			 model.addAttribute("crowdsourceSwitch",Config.getInstance().getCrowdSourcingRole());
			 String remoteURL = Config.getInstance().getRemoteURL() 
					 + "/codebooks/"+baseHandle+"/v/"+version+"/vars/"+variable;
			 model.addAttribute("remoteServerURL",remoteURL);
			 
			 model.addAttribute("crowdsourceCompareURL",loader.getBuildName()+"/codebooks/"
					 +baseHandle+"/v/"+version+"/vars/"+variable+"/diff");
			 
			 model.addAttribute("baseHandle",baseHandle);
			 model.addAttribute("version",version);
			 model.addAttribute("var", variable);
			 model.addAttribute("crumbs", crumbs);
			 model.addAttribute("results", xp.getData());	
			 model.addAttribute("subTitl","Editing: " + formatedVarName + " ("+handle.toUpperCase()+")");

		 }catch(NullPointerException | NumberFormatException e){
			model.addAttribute("error2","Error fetching data");		
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return "redirect:/edit";
		 }
		
		 return "/WEB-INF/editViews/variableEdit.jsp"; 
	}
	
	/**
	 * Displays field in variable for editing
	 * @param handle the codebook containing the variable
	 * @param var the variable to edit
	 * @param field the field to edit
	 * @param index the index specifying the location of the field
	 * @param index2 the second index specifying the location of the field
	 * @param append whether or not a new element is being appended (versus overwritten)
	 * @param model the current model
	 * @param session the current session			 
	 * @return and ajax editing window location
	 */

	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{var}/edit", method = RequestMethod.GET)
	public String editVariablePage(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,
	@PathVariable(value = "var") String var,		
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i", defaultValue = "1") String index,
	@RequestParam(value = "k", defaultValue = "1") String index2,
	@RequestParam(value = "a", defaultValue = "false") boolean append, 
	Model model, HttpSession session, HttpServletResponse response)
	{
		String handle = baseHandle + version;
		if(field.equals(""))
			return "redirect:/codebooks/"+baseHandle+"/v/"+version+"/vars/"+var+"/edit";	
		
		String baseURI = loader.getPath();
		String apiURI = baseURI + "/rest/codebooks/"+ handle + "/variables/"+var;
		String xml = Fetch.getShortXML(apiURI)[0];
		
		String[] path = null;
		String currentValue = "";
		
		int pendingChanges = 0;
		Parser xp = new Parser(xml);	
		
		path = getVarPaths(var, field, index, index2, append);
		
		if(path[0].equals("5")){				
			HashMap<String,String[]> codebooksAccs = Fetch.getCodebooksAccs(baseURI+"/rest/");
			String[] accessLevels = codebooksAccs.get(handle);
						
			int pos = path[1].lastIndexOf("/");
			String p1 = path[1].substring(0,pos);
			String p2 = path[1].substring(pos,path[1].length()).replace("/@", "");
			
			String curAccs = xp.getAttrValue(p1, p2);
			
			model.addAttribute("accessLevels", accessLevels);
			model.addAttribute("curAccs", curAccs);
			model.addAttribute("type", "accs");
		
			//Shouldn't need concurency for access levels
			
		}else if(path[0].equals("2") || path[0].equals("4")){
			//While this is not needed for now, leave code in
			int pos = path[1].lastIndexOf("/");
			String p1 = path[1].substring(0,pos);
			String p2 = path[1].substring(pos,path[1].length()).replace("/@", "");
			
			currentValue = xp.getAttrValue(p1, p2);
			model.addAttribute("type", "attr");
			
			pendingChanges = checkPendingChanges(baseHandle,version,session,currentValue,var+"."+field);
			
		}else{
			if(append){
				currentValue = "";	
			}else{
				try{
					currentValue = xp.getNode(path[1]);
				}catch(NullPointerException e){
					append = true;
				}
			}
			
			model.addAttribute("type", "elem");
			pendingChanges = checkPendingChanges(baseHandle,version,session,currentValue,var+"."+field);
		}
		
		if(append){ 
			currentValue = "";
		}else{
			currentValue = HTMLcheck(currentValue,false);
		}	

		model.addAttribute("pendingChanges", pendingChanges);
		model.addAttribute("editorType", path[3]);
		model.addAttribute("field", field);
		model.addAttribute("index",index);	
		model.addAttribute("handle", handle);	
		model.addAttribute("curVal", currentValue);
		model.addAttribute("title", path[2]);	
		model.addAttribute("append", append);		
	
		return "/WEB-INF/ajaxViews/varEdit.jsp";
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{var}/edit-cancel", method = RequestMethod.GET)
	@ResponseBody
	public String editVarCancel(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,		
	@PathVariable(value = "var") String var,	
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i", defaultValue = "1") String index,
	
	@RequestParam(value = "a", defaultValue = "false") String append, Model model, HttpSession session) {
		field = var+"."+field;
		removePending(baseHandle, version, session, field);
		return "";
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{var}/editMulti", method = RequestMethod.GET)
	public String editMultiVariablePage(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,
	@PathVariable(value = "var") String var,		
	@RequestParam(value = "f", defaultValue = "") String field, 
	@RequestParam(value = "i") String[] index,
	@RequestParam(value = "a", defaultValue = "false") String append, 
	Model model, HttpSession session, HttpServletResponse response)
	{
		Map<String,String[]> validFields = null;
		try{
			String handle = baseHandle + version;
			if(index != null){
				try{
					for(String i : index) Integer.parseInt(i);
				}catch(NumberFormatException e){
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("Bad index request to var "+var+" in "+handle+". Should be array of ints. Index is: "+index);
					return "redirect://edit/codebooks/"+baseHandle+"/v/"+version+"/vars/"+var+"";
				}
			}
			
			validFields = getGroupedVarFields(index);
			if(!validFields.containsKey(field)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error("Invalid multi var field: "+field);
				return "redirect://edit/codebooks/"+baseHandle+"/v/"+version+"/vars/"+var+"";
			}
			String[] fieldLabels = getGroupedVarFieldsLabels(index).get(field);
			
			String baseURI = loader.getPath();
			String apiURI = baseURI + "/rest/codebooks/"+ handle + "/variables/"+var;
			String xml = Fetch.getShortXML(apiURI)[0];
			
			ArrayList<String> values = new ArrayList<String>();
			String[] xpaths  = validFields.get(field);
			Parser xp = new Parser(xml);
			for(String xpath : xpaths){
				values.add(xp.getValue2("/codeBook/var/"+xpath));
			}
			//TODO: Limiting chooses to a controlled vocabulary 
			//TODO: Add schema doc or more detailed info for the fields?
			model.addAttribute("field", field);	
			model.addAttribute("title", fieldLabels[0]);			
			model.addAttribute("values", values);	
			model.addAttribute("labels", fieldLabels);	
			model.addAttribute("append", append);	
			
		}finally{
			validFields.clear();
		}
		return "/WEB-INF/ajaxViews/editMulti.jsp";
	}

	/**
	 * Submits an edit request for a field in a variable
	 * @param handle the codebook containing the variable
	 * @param var the variable to be edited
	 * @param field the field being edited
	 * @param index the index specifying the field location
	 * @param index2 the second index specifying the field location
	 * @param newTxt the new value for the field
	 * @param newAccs the access level
	 * @param append whether or not element is being appended (versus overwritten)
	 * @param model the current model
	 * @param session the current session
	 * @param request the request
	 * @return the variable page
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{var}/edit", method = RequestMethod.POST)
	public String editVariable(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,
	@PathVariable(value = "var") String var,	
	@RequestParam(value = "field", defaultValue = "") String field, 
	@RequestParam(value = "i", defaultValue = "0") int index,
	@RequestParam(value = "k", defaultValue = "0") int index2,
	@RequestParam(value="newValue", defaultValue = "") String value,
	@RequestParam(value = "append", defaultValue = "0") boolean append,
	Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//Gets current value of var
		String handle = baseHandle+version;
		String baseURI = loader.getPath();
		String apiURI = baseURI + "/rest/codebooks/"+ handle + "/variables/"+var;
		String xml = Fetch.getShortXML(apiURI)[0];
		Parser xp = new Parser(xml);
		
		String[] path = getVarPaths(var, field, Integer.toString(index), Integer.toString(index2), append);
		String currentValue = "";
		
		if(path[0].equals("5")){				
			//Stop	
		}else if(path[0].equals("2") || path[0].equals("4")){
			//While this is not needed for now, leave code in
			int pos = path[1].lastIndexOf("/");
			String p1 = path[1].substring(0,pos);
			String p2 = path[1].substring(pos,path[1].length()).replace("/@", "");
			
			currentValue = xp.getAttrValue(p1, p2);
		}else{
			if(append){
				currentValue = "";	
			}else{
				try{
					currentValue = xp.getNode(path[1]);
				}catch(NullPointerException e){
					append = true;
				}
			}
		}

		String userValue = null;
		try{	
			userValue = getFieldState(baseHandle, version, session, field, var);
	
			if(!userValue.equals(currentValue) && !currentValue.equals("")){				
				//Change has been made during editing, init diff view
				currentValue = currentValue.replace(" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"", "");
				response.addHeader("conflictingValue", currentValue);
				
				//Removes user's cached changes to allow overwrite
				removePending(baseHandle, version, session, var+"."+field);
				return "";
			}
		}catch(NullPointerException e){	 
			//No concurency data cached
		}
		

		//TODO: Remove generic throws Exception
		value = specialCharClean(value);

		String ip = request.getRemoteAddr();
		
		Map<String,String[]> validFields = null;
		String[] fieldValues = null;
		try{
			validFields = getVarFields(var,Integer.toString(index),Integer.toString(index2));
			if(validFields.containsKey(field)){ 
				fieldValues = validFields.get(field);	
			}else{
				session.setAttribute("error2", "Invalid request");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/vars/"+var+"/";	
			}
		}finally{
			validFields.clear();
		}
			
		String user = "anonymous";
		if(session.getAttribute("userEmail") != null){
			user = (String) session.getAttribute("userEmail");
		}
		boolean delete = false;
		int code = -1;
		
		/*
		 * Type 1 single element
		 * Type 2 single attribute
		 * Type 3 plural element
		 * Type 4 plural attribute
		 * Type 5 access level
		 */
		switch(fieldValues[0]){
			//Elements
			case "1":
				switch(fieldValues[3]){
					//HTML allowed
					case "0":
						if(!linkCheck(field,value)){
							throw new Exception("Bad HTML");
						}
						value = value.replace("<p>&nbsp;</p>", "");
						String oldval = value;
						value = HTMLcheck(value,false);
						if(oldval.replaceAll("[\\s]", "").length() != value.replaceAll("[\\s]", "").length()){
							throw new Exception("bad HTML");
						}
						value = htmlDDIClean(value);
						
					break;
					//Plain text
					case "1":
						value = value.trim();
						value = HTMLcheck(value,true);
					break;
				}
				
				EditCodebookData editCodebookData = new EditCodebookData();
				code = editCodebookData.editVar(baseHandle, version, var, field, value, 
				index, index2, append, delete, ip, user);

			break;
			//Access levels
			case "5":
				EditCodebookData editCodebookData2 = new EditCodebookData();
				code = editCodebookData2.editVar(baseHandle, version, var, field, value, 
				index,index2, append, delete, ip, user);
				break;
		}
		
		//Response
		if(code > 0 && code < 400){
			//Removes user's cached changes to allow overwrite
			removePending(baseHandle, version, session, var+"."+field);
			session.setAttribute("info_splash2","Changes Saved");		
			if(field.equals("labl")){
				session.removeAttribute("searchCache");
			}
		}else if(code == 404){ 
			session.setAttribute("error2", "There was a problem connecting to the database.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}else{
			session.setAttribute("error2", "Your request could not be completed.\nYour edit may have created an invalid document.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/vars/"+var+"/";	
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{var}/editMulti", method = RequestMethod.POST)
	public String editVariableMulti(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,
	@PathVariable(value = "var") String var,	
	@RequestParam(value = "field", defaultValue = "") String field,
	@RequestParam(value = "newValue", defaultValue = "") ArrayList<String> values, 
	@RequestParam(value = "i", defaultValue = "") String[] index,
	@RequestParam(value = "append", defaultValue = "0") boolean append,
	Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{		
		Map<String,String[]> validFields = null;
		try{
			String handle = baseHandle + version;
			if(index != null){
				try{
					for(String i : index) Integer.parseInt(i);
				}catch(NumberFormatException e){
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("Bad index request to var "+var+" in "+handle+". Should be array of ints. Index is: "+index);
					return "redirect://edit/codebooks/{c}/v/{v}/vars/{var}";
				}
			}
			
			validFields = getGroupedVarFields(index);
			if(!validFields.containsKey(field)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error("Invalid multi var field: "+field);
				return "redirect://edit/codebooks/{c}/v/{v}/vars/{var}";
			}			

			String user = "anonymous";
			if(session.getAttribute("userEmail") != null){
				user = (String) session.getAttribute("userEmail");
			}
			ArrayList<String> paths = new ArrayList<String>(Arrays.asList(validFields.get(field)));
			
			int code = -1;
			EditCodebookData editCodebookData = new EditCodebookData();
			code = editCodebookData.editVarMulti(baseHandle, version, var, paths,
			values, append, user);		

			//Response
			if(code > 0 && code < 400){
				session.setAttribute("info_splash2","Changes Saved");
			}else if(code == 404){ 
				session.setAttribute("error2", "There was a problem connecting to the database.");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}else{
				session.setAttribute("error2", "Your request could not be completed.\nYour edit may have created an invalid document.");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			
		}finally{
			validFields.clear();
		}
		
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/vars/"+var+"/";	
	}
	
	/**
	 * Method to display delete variable content page
	 * @param handle the codebook containing the variable
	 * @param var the variable to delete content from
	 * @param field the field to delete
	 * @param index the index specifying the location of the field
	 * @param index2 the second index specifying the location of the field
	 * @param model the current model
	 * @param session the current session
	 * @return the ajax view for confirming deletion
	 */
	 @RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{var}/delete", method = RequestMethod.GET)
    public String deleteVariablePage(@PathVariable(value = "c") String baseHandle,
    @PathVariable(value = "v") String version, @PathVariable(value = "var") String var,             
    @RequestParam(value = "f", defaultValue = "") String field, @RequestParam(value = "i", defaultValue = "1") String index,
    @RequestParam(value = "k", defaultValue = "1") String index2,
    Model model, HttpSession session)
    {
            String handle = baseHandle + version;
            if(field.equals(""))
                    return "";   
            
            //All valid fields to edit
            HashMap<String,String[]> validFields = new HashMap<String,String[]>();
            validFields.put("catgry", new String[] {"1","codeBook/var[@name='"+var+"']/catgry["+index+"]","Category"});
            
            if(validFields.containsKey(field)){
                    String[] inf = validFields.get(field);
                    String title = inf[2];
                    
                    model.addAttribute("field", field);
                    model.addAttribute("index",index);      
                    model.addAttribute("handle", handle);   
                    model.addAttribute("index2", index2);
                    model.addAttribute("title", title);     
                    validFields.clear();                                    
                    return "/WEB-INF/ajaxViews/deleteVarContent.jsp";
            }
            return "";           
    }

	/**
	 * Handles variable content deletion requests
	 * @param handle the codebook containing the variable
	 * @param var the variable to delete content from
	 * @param field the field to delete
	 * @param index the index specifying the location of the field
	 * @param index2 the second index specifying the location of the field
	 * @param model the current model
	 * @param session the current session
	 * @param request the request
	 * @return the variable page
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{var}/delete", method = RequestMethod.POST)
	public String deleteVariableContent(@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version, @PathVariable(value = "var") String var,		
	@RequestParam(value = "f", defaultValue = "") String field, @RequestParam(value = "i", defaultValue = "1") int index,
	@RequestParam(value = "k", defaultValue = "1") int index2,
	Model model, HttpSession session, HttpServletResponse response)
	{
		String ip = request.getRemoteAddr();
		
		boolean delete = true;
		boolean append = false;
		int code = -1;
		
		String user = "anonymous";
		if(session.getAttribute("userEmail") != null){
			user = (String) session.getAttribute("userEmail");
		}
				
		EditCodebookData editCodebookData = new EditCodebookData();
		code = editCodebookData.editVar(baseHandle, version, var, field, "",
		index, index2, append, delete, ip, user);
		
		if(code > 0 && code < 400){
			session.setAttribute("info_splash2","Item Deleted");
		}else if (code > 500){
			session.setAttribute("error2", "Your request could not be completed.\nYour edit may have created an invalid document.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{
			session.setAttribute("error2", "There was a problem connecting to the database.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return "redirect:/edit/codebooks/"+baseHandle+"/v/"+version+"/vars/"+var+"/";
	}
	
	/**
     * Checks if a version exists, if not clones
     * @param baseHandle
     * @param version
     * @param model
     * @param session
     * @param response
     * @return
     */
    @RequestMapping(value = "/edit/codebook/{c}/v/{v}/clone", method = RequestMethod.POST)
    @ResponseBody
	public String cloneCodebook(HttpSession session, HttpServletResponse response, 
	@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,
	@RequestParam(value = "vnew") String versionNew)
	{  	    	
    	if(context.getAttribute("codebooks") == null){
			String baseURI = loader.getPath() + "/rest/";
			TreeMap<String,String[]>  codebooks = loader.getCodebooks(baseURI);
			context.setAttribute("codebooks", codebooks);
		}
    	
    	String newHandle = baseHandle + version;

    	if(loader.hasCodebook(newHandle)){
    		return "Version '"+version+"' already exists for "+baseHandle;
    	}else{
    		//Clone codebook
    		//TODO:Not finished
    		return "";
    	}		
	}
    
//misc endpoints
//TODO:Remove everything related to the old API
	
	/**
	 * Retrieves variables in JSON format for autocomplete
	 * @param handle
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/data/codebooks/{h}/vars", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String jsonVarsInCodebook(@PathVariable(value = "h") String handle, HttpServletResponse response){
		String apiURL = loader.getPath()+"/rest/codebooks/"+handle+"/variables";
		String json = Fetch.getJson(apiURL)[0];
		response.setContentType("application/json");
		return json;
	}
	
	/**
	 * Retrieves variables in JSON format for autocomplete
	 * @param handle
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/data/codebooks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String jsonCodebookList(HttpServletResponse response){
		String apiURL = loader.getPath()+"/rest/codebooks/";
		String json = Fetch.getJson(apiURL)[0];
		response.setContentType("application/json");
		return json;
	}
	
	@RequestMapping(value = "/data/prov/nodes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String jsonProvNodes(HttpServletResponse response){
		JSONArray nodes = null;
		String jsonData = Fetch.get(loader.getPath()+"/rest/prov");
		try{
			JSONObject json = (JSONObject) new JSONParser().parse(jsonData);
			nodes = (JSONArray) json.get("nodes");
		}catch(ParseException|JSONException e){
			logger.error("Error parsing JSON from internal database: "+e.getMessage());
		}
		response.setContentType("application/json");
		return nodes.toString();
	} 		
	
	//TODO: Test controller, not sure if this could be useful
	/**
	 * Test function
	 * @param baseHandle
	 * @param version
	 * @param id
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/autocomplete", method = RequestMethod.GET)
	public String autoComplete(@PathVariable(value = "c") String baseHandle, 
	@PathVariable(value = "v") String version, Model model, HttpSession session){;
		String handle = baseHandle+version;

		if(!loader.hasCodebook(handle)){
			session.setAttribute("error","Codebook with handle '"+handle+"' does not exist");			
			return "redirect:/";
		}	

		model.addAttribute("varAutoComplete",true);
		model.addAttribute("handle", handle);
		return "/WEB-INF/editViews/autoComplete.jsp";
	}
}