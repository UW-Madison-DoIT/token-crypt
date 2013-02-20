package edu.wisc.doit.tcrypt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


public class BaseController {
	protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@ExceptionHandler(Exception.class)
    public ModelAndView handleException (Exception e) {
	    logger.error("Something Bad Happened: ", e);
		return new ModelAndView("genericError");
    }
}
