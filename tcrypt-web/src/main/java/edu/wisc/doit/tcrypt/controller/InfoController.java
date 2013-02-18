package edu.wisc.doit.tcrypt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InfoController {
	@RequestMapping(value = "/info", method = RequestMethod.GET) 
	public String getTestPage () {
		return "testjsp";
	}
}
