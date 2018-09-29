package edu.cornell.ncrn.ced2ar.web.controllers;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.cornell.ncrn.ced2ar.api.data.Config;
import edu.cornell.ncrn.ced2ar.web.classes.Loader;

/**
 *Handles requests relating to the comparison view
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Kyle Brumsted
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
@Controller
public class Feedback {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Config config;
	
	@Autowired
	private Loader loader;
	

//Utilities
	/**
	 *Checks if a post request is spam
	 * @param req HttpServletRequest the current request
	 * @param hp String the honeypot
	 * @return boolean whether the request is spam or not
	 */
	 protected boolean checkSpam(HttpServletRequest req, String hp){
		 boolean isSpam = false;
		 //String remoteIP = req.getLocalAddr();
		 String userAgent = req.getHeader("user-agent").trim();
		 //If honeypot is filled out or userAgent is empty, assume spam
		 if(!hp.equals("") || userAgent.equals("")){
			isSpam = true;
		 }
		 return isSpam;
	 }
	 
	 /**
	  *Sends the bug report
	  * @param msg String the message to be sent
	  * @return boolean whether or not the transmission was successful
	  */
	 private boolean sendReport(String msg){
		String[] bugReportInfo = config.getBugReportInfo();
		final String recipient = bugReportInfo[0];
		final String username = bugReportInfo[1];
		//final String password = bugReportInfo[2];
		final String password  = config.getDecodedBugReportPwd();
		 
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
		"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(username, password);
			}
		});
		
		try{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(recipient));
			message.setRecipients(Message.RecipientType.TO,  InternetAddress.parse(recipient));
			message.setSubject("BUG REPORT SUBMITTED "+ new Date().toString());
			message.setText(msg);
			Transport.send(message);
			return true;
			
		}catch(MessagingException e){
			e.printStackTrace();
			return false;
		}	
	 }	
	
//Bug Endpoints	
	/**
	 *Display bug report page 
	 * @param model Model the current model
	 * @param lastPage String the previous page visited
	 * @return String the page displaying the bugreport form
	 **/
	@RequestMapping(value = "/bugreport", method = RequestMethod.GET)
	public String bugReport(Model model, @RequestParam(value="lastpage", defaultValue = "") String lastPage){
		if(!config.isBugReportEnable()){
			return "redirect:/search";
		}
		//String[][] crumbs = {{"Bug Report",""}};
		//model.addAttribute("crumbs",crumbs);
		model.addAttribute("subTitl","Bug Report");	
		//model.addAttribute("lastPage", lastPage);
		return "/WEB-INF/views/bugreport.jsp";	
	}
	
	/**
	 *Processes a bug report
	 * @param request HttpServletRequest the current request
	 * @param session HttpSession the current session
	 * @param bd String the description of the bug
	 * @param rs String the reproduction steps
	 * @param yn String the name of the submitter
	 * @param ye String the email of the submitter
	 * @param hp String the honeypot
	 * @param lp String the previous page visited
	 * @param bt String the bug type
	 * @param model Model the current model
	 * @return String the previous page visited 
	 */
	 //TODO: send HTTP POST request to write in BaseX
	 @RequestMapping(value = "/bugreport", method = RequestMethod.POST)
	 public String submitReport(HttpServletRequest request, HttpSession session, @RequestParam(value="bugDescription", defaultValue = "") String bd,
	 @RequestParam(value="reproductionSteps", defaultValue = "") String rs, @RequestParam(value="yourName", defaultValue = "") String yn,
	 @RequestParam(value="yourEmail", defaultValue = "") String ye, @RequestParam(value="reportHP", defaultValue = "") String hp,
	 @RequestParam(value="bugType", defaultValue = "") String bt,  Model model){
		if(!config.isBugReportEnable()){
			return "redirect:/search";
		}
		if(checkSpam(request, hp)){
			return "redirect:http://google.com";
		}
		 
		//sanitize input
		if(bd.equals("")){
		session.setAttribute("error", "Bug Description is a required field. Try Again.");
			return "redirect:/bugreport";
		}
		bd = bd.replaceAll("[!@#$%^&*<>?/=+_]", "");
		if(rs.equals("")){
		session.setAttribute("error", "Steps to Reproduce is a required field. Try Again.");
			return "redirect:/bugreportp";
		}
		
		rs = rs.replaceAll("[!@#$%^&*<>?/=+_]", "");
		if(!yn.matches("^([a-zA-Z]'?[- a-zA-Z]+)?$")){
			session.setAttribute("error", "Name can only contain text and hyphens or apostrophes. Try Again.");
			return "redirect:/bugreport";
		}
		if(!ye.matches("^([a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4})?$")){
			session.setAttribute("error", "Invalid Email Adress. Try Again.");
			return "redirect:/bugreport";
		}
		 
		if(yn.equals("")) yn = "NONE SUBMITTED";
		if(ye.equals("")) ye = "NONE SUBMITTED";
		 
		//Construct the bugReport Message
		String message = "Bug Type: " + bt + "\n\n" +
		"Description: " + bd + "\n\n" +
		"Reproduction Steps: " + rs + "\n\n" +
		"Submitter Name: " + yn + "\n\n" +
		"Submitter Email: " + ye + "\n\n" +
		"Submitter User Agent: " + request.getHeader("user-agent").trim() + "\n\n" +
		"Server IP: " + request.getLocalAddr() + "\n\n" +
		"Time of Submission: " + new Date() + "\n\n"; 
		
		//email the message
		if(sendReport(message)){
			session.setAttribute("info_splash", "Your report has been submitted successfully. Thank you.");
		}else{
			session.setAttribute("error", "There was an internal error when submitting your report. Sorry, please try again.");
			return "redirect:/bugreport";
		}
		return "redirect:/bugreport";
	 } 
	 
//Registration Endpoints			
	@RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model){
		return "redirect:/search";
		/*
		*TODO: Current not used, but still a functional page
		model.addAttribute("subTitl","Registration");	
		return "/WEB-INF/editViews/register.jsp";
		*/
    } 

	@RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerPost(@RequestParam(value="email", defaultValue = "") String email,
	    @RequestParam(value="fName", defaultValue = "") String fName, 
	    @RequestParam(value="lName", defaultValue = "") String lName,
	    @RequestParam(value="org", defaultValue = "") String org, 
	    @RequestParam(value="hp", defaultValue = "") String hp, 
	    HttpServletRequest request, HttpSession session){

			//Check if spam
			if(checkSpam(request, hp)){
				return "redirect:http://google.com";
			}
		
			fName = fName.replaceAll("[^A-Za-z0-9\\-\\. ]", "").trim();
			lName = lName.replaceAll("[^A-Za-z0-9\\-\\. ]", "").trim();
			org = org.replaceAll("[^A-Za-z0-9\\-\\. ]", "").trim();	
			
			//Checks if fields are empty
			if(fName.equals("") | lName.equals("") | org.equals("")){
				session.setAttribute("error", "All fields are required");
				return "redirect:/register";
			}
			//Validates email address
			if(!EmailValidator.getInstance().isValid(email)){
				session.setAttribute("error", "Invalid email address");
				return "redirect:/register";
			}
			
			//TODO:Disabled for now
			/*
			//Checks if email address in use
			if(QueryUtil.hasAccount(email)){
				session.setAttribute("error", "Email address already registered");
				return "redirect:/register";
			}
			QueryUtil.createAccount(email, fName, lName, org);
			*/
			
			session.setAttribute("info_splash",email + " registered successfully");
			return "redirect:/register";
    }  
}