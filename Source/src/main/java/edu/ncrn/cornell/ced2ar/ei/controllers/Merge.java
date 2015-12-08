package edu.ncrn.cornell.ced2ar.ei.controllers;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.Connector;
import edu.ncrn.cornell.ced2ar.api.data.Connector.RequestType;
import edu.ncrn.cornell.ced2ar.api.rest.queries.CodebookData;
import edu.ncrn.cornell.ced2ar.eapi.XMLHandle;
import edu.ncrn.cornell.ced2ar.eapi.rest.queries.EditCodebookData;
import edu.ncrn.cornell.ced2ar.merge.MergeProperties;
import edu.ncrn.cornell.ced2ar.web.classes.Loader;
import edu.ncrn.cornell.ced2ar.web.classes.Parser;

/**
 *Class to handle codebook merging
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Merge {
	
	private static final Logger logger = Logger.getLogger(EditCodebooks.class);
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Loader loader;
	
	@Autowired
	Config config;
	
//Constants
	private Map<String,String> getVarXpaths(String var, int index){
		Map<String,String> varXPaths = new WeakHashMap<String,String>();
		varXPaths.put("mergeELabel", "/codeBook/dataDscr/var[@name='"+var+"']/labl");
		
		varXPaths.put("mergeEConcept", "/codeBook/dataDscr/var[@name='"+var+"']/concept");
		varXPaths.put("mergeEConceptVocab", "/codeBook/dataDscr/var[@name='"+var+"']/concept/@vocab");
		varXPaths.put("mergeEConceptURI", "/codeBook/dataDscr/var[@name='"+var+"']/concept/@vocabURI");
		
		varXPaths.put("mergeEStatValid", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='vald']");
		varXPaths.put("mergeEStatInvalid", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='invd']");
		varXPaths.put("mergeEStatMin", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='min']");
		varXPaths.put("mergeEStatMax", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='max']");
		varXPaths.put("mergeEStatMean", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='mean']");
		varXPaths.put("mergeEStatMedn", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='medn']");
		varXPaths.put("mergeEStatStdev", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='stdev']");
		varXPaths.put("mergeEStatMin", "/codeBook/dataDscr/var[@name='"+var+"']/sumStat[@type='mode']");
		
		varXPaths.put("mergeEValRngMin", "/codeBook/dataDscr/var[@name='"+var+"']/valrng/@min");
		varXPaths.put("mergeEValRngMax", "/codeBook/dataDscr/var[@name='"+var+"']/valrng/@max");
		
		varXPaths.put("mergeEQuestion", "/codeBook/dataDscr/var[@name='"+var+"']/qstn");
		varXPaths.put("mergeETxt", "/codeBook/dataDscr/var[@name='"+var+"']/txt");
		varXPaths.put("mergeECatValu", "/codeBook/dataDscr/var/catgry["+index+"]/catValu");
		varXPaths.put("mergeECatLabl", "/codeBook/dataDscr/var[@name='"+var+"']/catgry["+index+"]/labl");
		
		//TODO: Not used
		//varXPaths.put("mergeECodInst", "");
		
		varXPaths.put("mergeENote", "/codeBook/var[@name='"+var+"']/notes["+index+"]");
		
		return varXPaths;			
	}
	
	private Map<String,String> getTitleXpaths(int index){
		Map<String,String> xpaths = new WeakHashMap<String,String>();

		xpaths.put("mergeEprodDate",  "/codeBook/docDscr/citation/prodStmt/prodDate");
		xpaths.put("mergeEdocProducer", "/codeBook/docDscr/citation/prodStmt/producer["+index+"]");
		xpaths.put("mergeEstdyProducer", "/codeBook/stdyDscr/citation/prodStmt/producer["+index+"]");
		xpaths.put("mergeEdistrbtr", "/codeBook/stdyDscr/citation/distStmt/distrbtr["+index+"]");
		xpaths.put("mergeEdistrbtrURL", "/codeBook/stdyDscr/citation/distStmt/distrbtr["+index+"]/@URI");
		xpaths.put("mergeEdocCit", "/codeBook/docDscr/citation/biblCit");
		//xpaths.put("docCitURL", "/codeBook/docDscr/citation/biblCit/ExtLink");
		xpaths.put("mergeEstdyCit", "/codeBook/stdyDscr/citation/biblCit");
		//xpaths.put("stdyCitURL", "/codeBook/stdyDscr/citation/biblCit/ExtLink");
		xpaths.put("mergeEabstract", "/codeBook/stdyDscr/stdyInfo/abstract");
		xpaths.put("mergeEconfDec", "/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec");
		//xpaths.put("confDecURL", "/codeBook/stdyDscr/dataAccs[1]/useStmt/confDec/@URI");
		//xpaths.put("accessRstr", "/codeBook/stdyDscr/dataAccs["+index+"]/useStmt/restrctn");
		
		xpaths.put("mergeEaccessCond", "/codeBook/stdyDscr/dataAccs[1]/useStmt/conditions");
		xpaths.put("mergeEaccessPermReq", "/codeBook/stdyDscr/dataAccs[1]/useStmt/specPerm");
		xpaths.put("mergeEcitReq", "/codeBook/stdyDscr/dataAccs[1]/useStmt/citReq");		
		xpaths.put("mergeEdisclaimer", "/codeBook/stdyDscr/dataAccs[1]/useStmt/disclaimer");
		xpaths.put("mergeEcontact", "/codeBook/stdyDscr/dataAccs[1]/useStmt/contact");
		
		xpaths.put("mergeEmethod", "/codeBook/stdyDscr/method/dataColl/collMode");		
		xpaths.put("mergeEdataSrc", "/codeBook/stdyDscr/method/dataColl/sources/dataSrc["+index+"]");		
		xpaths.put("mergeErelMat", "/codeBook/stdyDscr/othrStdyMat/relMat["+index+"]");
		xpaths.put("mergeErelPubl", "/codeBook/stdyDscr/othrStdyMat/relPubl["+index+"]");
		xpaths.put("mergeErelStdy", "/codeBook/stdyDscr/othrStdyMat/relStdy["+index+"]");				
		xpaths.put("mergeEdocSrcBib", "/codeBook/docDscr/docSrc/biblCit");

		return xpaths;			
	}
	
//Endpoints
	
	
	@RequestMapping(value = "/codebooks/{c}/v/{v}/vars/{n}/diff",method = RequestMethod.GET)
	public String diffVariable(Model model,
	@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version, 
	@PathVariable(value = "n") String variable){

		String handle = baseHandle + version;		
		String baseURI = loader.getPath();
		
		String apiURILocal = baseURI + "/rest/codebooks/"+ handle + "/variables/"+variable;
		String apiURIRemote = Config.getInstance().getRemoteURL() 
		+ "/rest/codebooks/"+ handle + "/variables/"+variable;

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
		
		String path = context.getRealPath("/xsl/variableMerge.xsl");//Local file path to find XSL doc
		
		if(responseLocal >= 400 && responseRemote >= 400){
			//TODO: show error message
			//Var exist on neither local nor remote
		}else{
			//TODO: XSLT to transform
			if(responseLocal < 400){
				Parser xp = new Parser(localRep, path, 0);
				model.addAttribute("local", xp.getData());				
			}
			if(responseRemote < 400){
				Parser xp2 = new Parser(remoteRep, path, 0);
				model.addAttribute("remote", xp2.getData());
			}
		}
		
		TreeMap<String,String[]> codebooks = null;
		if(session.getAttribute("codebooks") == null){
			codebooks = loader.getCodebooks(baseURI);
			session.setAttribute("codebooks", codebooks);
		}else{
			codebooks = (TreeMap<String, String[]>) session.getAttribute("codebooks");
		}
		
		String codebookURL = "codebooks/"+baseHandle+"/v/"+version+"/";
		String[][] crumbs = new String[][] {
			{codebooks.get(handle)[4],codebookURL},
			{variable,codebookURL+"vars/"+variable},
			{"Difference",""}
		};
		
		model.addAttribute("subTitl", "Diff "+variable);	
		model.addAttribute("pageWidth", 1);
		
		model.addAttribute("baseHandle", baseHandle);
		model.addAttribute("version", version);
		model.addAttribute("var", variable);

		
		
		model.addAttribute("crumbs", crumbs);
		
		return "/WEB-INF/mergeViews/varDiff.jsp";
	}	
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{n}/merge",method = RequestMethod.GET)
	public String mergeVariables(Model model,
	@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version, 
	@PathVariable(value = "n") String variable){

		String handle = baseHandle + version;		
		String baseURI = loader.getPath();
		
		String apiURILocal = baseURI + "/rest/codebooks/"+ handle + "/variables/"+variable;
		String apiURIRemote = Config.getInstance().getRemoteURL() 
		+ "/rest/codebooks/"+ handle + "/variables/"+variable;

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
		
		String path = context.getRealPath("/xsl/variableMerge.xsl");//Local file path to find XSL doc
		
		if(responseLocal >= 400 && responseRemote >= 400){
			//TODO: show error message
			//Var exist on neither local nor remote
		}else{
			//TODO: XSLT to transform
			if(responseLocal < 400){
				Parser xp = new Parser(localRep, path, 0);
				model.addAttribute("local", xp.getData());				
			}
			if(responseRemote < 400){
				Parser xp2 = new Parser(remoteRep, path, 0);
				model.addAttribute("remote", xp2.getData());
			}
		}
		model.addAttribute("subTitl", "Merging "+variable);	
		model.addAttribute("pageWidth", 1);
		return "/WEB-INF/mergeViews/varMerge.jsp";
	}	
	
	/**
	 * Merges changes at a variable level
	 * @param response
	 * @param baseHandle
	 * @param version
	 * @param variable
	 * @param replacements
	 * @return
	 */
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/vars/{n}/merge",method = RequestMethod.POST)
	@ResponseBody
	public String mergeVariablesV(HttpServletResponse response,
	@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version, 
	@PathVariable(value = "n") String variable,
	@RequestParam(value = "replacements") JSONObject replacements){
		int rep = doMerge(baseHandle,version,replacements,"var",variable);
		if(rep > 400){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		return "";
	}
	
	@RequestMapping(value = "/edit/codebooks/{c}/v/{v}/merge",method = RequestMethod.POST)
	@ResponseBody
	public String mergeTitlePage(HttpServletResponse response,
	@PathVariable(value = "c") String baseHandle,
	@PathVariable(value = "v") String version,
	@RequestParam(value = "replacements") JSONObject replacements){

		int rep = doMerge(baseHandle,version,replacements,"titlepage");
		if(rep > 400){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
		return "";
	}
	
	private int doMerge(String baseHandle, String version, JSONObject replacements, String type){
		return doMerge(baseHandle,version,replacements,type,"");
	}
	
	private int doMerge(String baseHandle, String version, JSONObject replacements, String type, String variable){
		String handle = baseHandle + version;
		String user = session.getAttribute("userEmail") != null ? 
		(String) session.getAttribute("userEmail") : "anonymous";
		
		CodebookData codebookData = new CodebookData();
		String currentXML = codebookData.getCodebook(handle);

		EditCodebookData editCodebookData = new EditCodebookData();

		XMLHandle xh = new XMLHandle(currentXML,config.getSchemaURI());
		
		Iterator<String> itr = replacements.keys();
		while(itr.hasNext()){
			
	        String key = (String) itr.next();
	        try {
				String value = replacements.getString(key).trim();
				int index = 0;
				if(Character.isDigit(key.charAt(key.length() - 1))){
					int dIndex = key.length() - 1;
					while(Character.isDigit(key.charAt(dIndex)) && dIndex > 0){
						dIndex--;
					}
					index = Integer.parseInt(key.substring(dIndex+1, key.length()));
					key = key.substring(0, dIndex+1);					
				}	
								
				
				Map<String,String> xpaths = null;
				if(type.equals("var")){
					xpaths = getVarXpaths(variable,index);
				}else if(type.equals("titlepage")){
					xpaths = getTitleXpaths(index);
				}
				
				String path = xpaths.get(key);
				
				////Will leave HTML on for now
				value = EditCodebooks.HTMLcheck(value,false);
				value = EditCodebooks.htmlDDIClean(value);

				xh.addReplace(path,value, false, true, true, true);
			
			} catch (JSONException e) {				
				e.printStackTrace();
			}
		}
		
		int code = editCodebookData.postXMLHandle(xh, baseHandle, version, user, false, "var[@name='"+variable+"']");
		
		if(code > 400){
			return code;
		}
		
		return 200;
	}
}