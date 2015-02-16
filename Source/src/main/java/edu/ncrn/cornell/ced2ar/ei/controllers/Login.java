package edu.ncrn.cornell.ced2ar.ei.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
/**
*Maps to login pages 
*@author Cornell University, Copyright 2012-2015
*@author Ben Perry
*
*@author Cornell Institute for Social and Economic Research
*@author Cornell Labor Dynamics Institute
*@author NCRN Project Team 
*/
@Controller
public class Login {
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(HttpSession session, HttpServletRequest request,HttpServletResponse response, Model model) {
		model.addAttribute("subTitl","Login");
		return new ModelAndView("/WEB-INF/editViews/login.jsp");
	}
	
	@RequestMapping(value = "/denied", method = RequestMethod.GET)
	public ModelAndView denied(HttpSession session, HttpServletRequest request,HttpServletResponse response, Model model) {
		response.setStatus(401);
		return new ModelAndView("/WEB-INF/errors/401.jsp");
	}
}