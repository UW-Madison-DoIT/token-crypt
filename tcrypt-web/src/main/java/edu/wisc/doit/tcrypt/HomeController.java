package edu.wisc.doit.tcrypt;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

@org.springframework.stereotype.Controller
public class HomeController implements Controller {

	@Override
	@RequestMapping("/")
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		return new ModelAndView("tcrypt.jsp");
	}
	
	@RequestMapping(value="/create", method = RequestMethod.POST)
	public ModelAndView create(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		return new ModelAndView("tcrypt.jsp");
	}

}
