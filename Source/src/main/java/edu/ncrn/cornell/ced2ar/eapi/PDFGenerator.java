package edu.ncrn.cornell.ced2ar.eapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.Fetch;

/**
 *This Class Generates Codebooks in PDF format.
 *Typical usage of this class is by a JobScheduler.  For the testing purposes, you may use the main method locally.
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Venky Kambhampaty, Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class PDFGenerator {
	private static final Logger logger = Logger.getLogger(PDFGenerator.class);
	public String pdfDirectory;
	public HtmlCleaner htmlCleaner;
	private boolean pdfEnabled;
	private String baseURL;
	private boolean xrLogEnabled;
	
	public static void main(String a[]) throws Exception{

		ClassPathXmlApplicationContext appContext = null;
		try{
			appContext = new ClassPathXmlApplicationContext("ced2ar-web-beans.xml");
			BeanFactory beanFactory=appContext;
			PDFGenerator pdfGen = (PDFGenerator) beanFactory.getBean("pdfGenerator");
			pdfGen.pdfDirectoryExistanceCheck();
		}finally{
			appContext.close();
		}
	}
	
	@Autowired
	private ServletContext context;
	
	/**
	 * This method is called by Spring Scheduler and runs asychronously.
	 * This task calls generatePDF method to generate codeBoods in pdf format. 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@Async
	@Scheduled (cron="${pdfGenerationTaskCronExpression}")
	public void taskGeneratePDF()throws IOException,DocumentException {
		if(!pdfEnabled){
			logger.debug("PDF generation is disabled");
			return;
		}
		long startTime = System.currentTimeMillis();
		logger.info("Codebook PDF generation task started");
		generatePDF();
		long endTime = System.currentTimeMillis();
		logger.info("Codebook PDF generation task took " + ((endTime - startTime) / 1000) + " Seconds");	
	}
	
	/**
	 * Creates PDFs of all the codebooks in the BaseX 
	 * Fetches all the codebooks from BaseX and calls generatePDF(handle)
	 * @throws IOException
	 * @throws DocumentException
	 */	
	public void generatePDF(){
		Collection<String[]> codebooks = Fetch.getCodebooks(getBaseURL()+"rest/").values();
		logger.info("Found " + codebooks.size() + " Codebooks");

		/*
		 * FYI: To turn on org.xhtmlrenderer.* logging, for the flying saucer jar file, use:
		 *   setXrLogEnabled(true);
		 */

		for(String[] codebook : codebooks){
			String handle = codebook[0] + codebook[1];
			logger.info("Generating PDF for Codebook: " + handle);
			try{
				generatePDF(handle);
			}
			catch(Exception ex){
				logger.warn("Error in generating PDF for Codebook: " + handle + ". Proceeding to the next Codebook. Specific Exception ... " + ex.getMessage(), ex);
			}
		}
		/*
		 * Make sure we turn off the XRLog logger.
		 */
		setXrLogEnabled(false);
		logger.info("Done Generating Codebook PDFs");
	}
	
	/**
	 * Generates a PDF for a codebook that consists of
	 * 1. Titlepage
	 * 2. Groups (if any)
	 * 3. Variables
	 * Makes a series of RESTful api calls to fetch data and adds the returned string to a list.
	 * The list is passed to flying-saucer API to generate a PDF.
	 * PDF is saved as the name of the handle and in a location configured in the bean
	 * @param String handle Codebook Handle for which pdf will be generated.
	 * @throws DocumentException 
	 * @throws URISyntaxException 
	 * @throws FileNotFoundException 
	 */
	protected void generatePDF(String handle) throws DocumentException, 
	URISyntaxException, FileNotFoundException, UnsupportedEncodingException,IOException{
			List<String> input = new ArrayList<String>();

			/*
			 * This is a quick workaround to bypass the NullPointerException in the ced2ar.log, which is really an IOException being thrown when you have debugging on.
			 *   org.xhtmlrenderer.exception WARNING:: IO problem for http://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/../fonts/fontawesome-webfont.eot?v=4.4.0
			 *
			 * Replace:
			 *     <link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css" />
			 *  with:
			 *     <link rel="stylesheet" type="text/css" href="/ced2ar-web/font-awesome/css/font-awesome.min.css" />
			 *
			 * So we can generate out part of the pdf file.  (The funding runs out in 10 days.)
			 */
			String findStr = "/maxcdn.bootstrapcdn.com/font-awesome/4.4.0";
			String replaceStr = "ced2ar-web/font-awesome";
			logger.info("WORKAROUND: "+ findStr + "  REPLACED WITH: " + replaceStr);

			//Add the title page
		//	input.add(fetchPage("codebooks/" + handle));
			String codebookTitle = fetchPage("codebooks/" + handle);
			input.add(codebookTitle.replace(findStr, replaceStr));

			// Add Variable Groups Page	task5859.
			String groups = fetchPage("codebooks/"+ handle+"/groups/");
		//	if(!StringUtils.isEmpty(groups)) input.add(groups);
			if(!StringUtils.isEmpty(groups)) input.add(groups.replace(findStr, replaceStr));

			String vars = fetchPage("codebooks/" + handle + "/allvars");
		//	if(!StringUtils.isEmpty(vars)) input.add(vars);
			if(!StringUtils.isEmpty(vars)) input.add(vars.replace(findStr, replaceStr));

			OutputStream os = null;
			String str = "file:///"+this.getPdfDirectory()+ handle+ ".pdf";
			
			logger.info("Printing PDF to " + str);
			
			URI uri;
			try{
				pdfDirectoryExistanceCheck();
				uri = new URI(str.replace("\\", "/"));
				logger.info("Created uri="+ uri);
				File outputFile = new File(uri);
				os = new FileOutputStream(outputFile);

				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocumentFromString(input.get(0),getBaseURL());
				renderer.layout();
				renderer.createPDF(os, false);
				for (int i = 1; i < input.size(); i++) {
					renderer.setDocumentFromString(input.get(i),getBaseURL());
					renderer.layout();
					renderer.writeNextDocument();
				}
				renderer.finishPDF();
				logger.info("PDF with saved " + outputFile);
			}finally{
		      if (os != null) {
		        try {
		          os.close();
		        } catch (IOException e) {}
		      }
			}
	  }
	
	 /**
	  * Fetches a page from the website
	  * A request paramter batchSession=y is passed along with the url.The end point invalidates the session after 20 seconds.
	  * @see Codebook.java
	  * @param location
	  * @return
	  */
	private String fetchPage(String location) {
		URL url;
		try {
			String endPoint = getBaseURL()+location+"?print=y&batchSession=y";		
			url = new URL(endPoint);

			//TagNode tagNode = new HtmlCleaner(props).clean(url);
			TagNode tagNode = getHtmlCleaner().clean(url);
			
			//PrettyXmlSerializer serializer = new PrettyXmlSerializer(props);
			PrettyXmlSerializer serializer = new PrettyXmlSerializer(getCleanerProperties());
			
			return serializer.getAsString(tagNode);
		} catch (IOException e) {
			logger.error("This codebook does not have " + getBaseURL()+location+"?print=y&batchSession=y Returning an empty String. Actual message: " + e.getMessage(),e);
			return "";
		}
	}
	
	/**
	 * Sets the cleaning properties and returns htmlCleaner
	 * @return HtmlCleaner to clean the returned html from the resful calls.
	 */
	protected HtmlCleaner getHtmlCleaner(){
		if(htmlCleaner == null){
			CleanerProperties props = getCleanerProperties();
			htmlCleaner = new HtmlCleaner(props);
		}
		return htmlCleaner;
	}
	
	/**
	 * This method configures the cleaning properties 
	 * @return CleanerProperties that will be used to set in HtmlCleaner
	 */
	protected CleanerProperties getCleanerProperties(){
		CleanerProperties props = new CleanerProperties();
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		return props;
	}
	
	/**
	 * Makes sure that pdf directory exists.  If if doesn't, creates one.
	 */
	public void pdfDirectoryExistanceCheck() throws IOException{
		String path = getPdfDirectory();
		logger.info("Checking for existance of  pdf directory at: " + path  );
		File	 dir = new File(path);
		
		if(!dir.exists()){
			try{
				logger.info(path+" does not exist. Returning creating directory");
				dir.mkdirs();
				logger.info("Created Directory " + path);
			}
			catch(Exception ex){
				logger.warn("Error creating pdf directory. Specific Exception ... " + ex.getMessage(), ex);
				throw new RuntimeException("Error creating pdf directory. ",ex);
			}
		}
		else{
			logger.info(path+" Exists. Returning without any action");
		}
	}

//Getters and setters	
	
	public String getPdfDirectory() {
		return context.getRealPath("/pdf/") + "/";
	}

	private String getBaseURL(){
		if(baseURL == null){
			Config config = Config.getInstance();
			String port = Integer.toString(config.getPort());
			String webAppName = context.getContextPath();
			baseURL = "http://localhost:"+port+webAppName+"/";
		}
		return baseURL;
	}
	
	public void setBaseURL(String url){
		baseURL = url;		
	}
	
	public void setContext(ServletContext c){
		context = c;
	}
	
	public boolean ispdfEnabled() {
		return pdfEnabled;
	}
	public void setpdfEnabled(boolean enabled) {
		this.pdfEnabled = enabled;
	}

	public void setXrLogEnabled(boolean enabled) {
		/*
		 * Only set the property if the value has changed.
		 */
		if(this.xrLogEnabled != enabled) {
			if(enabled) {
				/**
				 * Turns on org.xhtmlrenderer.* logging.   Source: https://developer.jboss.org/thread/183873?_sscc=t
				 *   Currently, this only logs to the Console.  I could not find it in the tomcat logs
				 */
				System.setProperty("xr.util-logging.loggingEnabled", "true");
				logger.info("org.xhtmlrenderer logging enabled");
			} else {
				System.setProperty("xr.util-logging.loggingEnabled", "false");
				logger.info("org.xhtmlrenderer logging disabled");
			}
		}
		this.xrLogEnabled = enabled;
	}

	public boolean isXrLogEnabled() {
		return xrLogEnabled;
	}
}