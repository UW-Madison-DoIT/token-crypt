package edu.wisc.doit.tcrypt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExceptionController extends BaseController {
	  @ExceptionHandler(Exception.class)
	  public ModelAndView handleException (Exception e) {
		  return new ModelAndView("genericError");
	  }

}
