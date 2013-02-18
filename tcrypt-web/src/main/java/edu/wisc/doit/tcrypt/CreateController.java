package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.util.Date;

@Controller
public class CreateController extends BaseController {

	private TCryptServices tcryptServices;
	
	@Autowired
	public CreateController(TCryptServices tcryptServices) {
		this.tcryptServices = tcryptServices;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@ExceptionHandler(Exception.class)
	public ModelAndView createServiceKeyInit() {
		ModelAndView mv = new ModelAndView("createServiceKeyBefore");
		return mv;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ExceptionHandler(Exception.class)
	public ModelAndView createServiceKey(
			@RequestParam("serviceName") String serviceName,
			@RequestParam("keyLength") int keyLength,
			HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView("createServiceKeyDownload");
		
		try {
			//validation
			String validationResult = validate(serviceName);
			if(!validationResult.isEmpty()) {
				modelAndView = new ModelAndView ("createServiceKeyBefore");
				modelAndView.addObject("error", validationResult);
				return modelAndView;
			}
			
			//Generate keys
			KeyPair generatedKeyPair = tcryptServices.generateKeyPair(keyLength);
			String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "na";
			//Create ServiceKey Object and write public key out to FS
			ServiceKey sk = new ServiceKey(serviceName,keyLength,username,new Date(),generatedKeyPair.getPublic(),generatedKeyPair.getPrivate());
			tcryptServices.writeServiceKeyToFileSystem(sk);
			
			//Add serviceKey object on session (for download) and put the serviceName in the object list
			request.getSession().setAttribute("serviceKey_"+sk.getServiceName(), sk);
			modelAndView.addObject("serviceName", serviceName);
		} catch(Exception e) {
			logger.error("Issue during key creation: " + e.getMessage(),e);
			// TODO Will be refactored as part of Exception handling refactoring
/*
			modelAndView = new ModelAndView ("createServiceKeyBefore");
			modelAndView.getModelMap().addAttribute("serviceName", serviceName);
			modelAndView.getModelMap().addAttribute("keyLength", keyLength);
			modelAndView.addObject("error", Constants.KEY_NOT_CREATED);
*/
			return modelAndView;
		}

		return modelAndView;
	}
	private String validate(String serviceName) {
		String error = "";
		if(serviceName == null || serviceName.isEmpty()) {
			error = "A service name is required";			
		} else if (-1 != serviceName.indexOf("_")) {
			error = "A service name cannot contain a understore.";
		}
		//TODO : Validate the name doesn't exist already
		
		return error;
	}
}
