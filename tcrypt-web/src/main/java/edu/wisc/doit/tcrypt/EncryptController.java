package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

@Controller
public class EncryptController extends BaseController {

	private IKeysKeeper keysHelper;
	private AuthenticationState authenticationState;
	private HashMap<String,TokenEncrypter> tokenEncrypters;
	
	@Autowired
	public EncryptController(IKeysKeeper tcryptHelper, AuthenticationState authenticationState) {
		this.keysHelper = tcryptHelper;
		this.authenticationState = authenticationState;
		tokenEncrypters = new HashMap<String,TokenEncrypter>();
	}
	
	@RequestMapping(value = "/encrypt", method = RequestMethod.GET)
	public ModelAndView encryptTextInit() throws Exception {
		ModelAndView modelAndView = new ModelAndView("encryptTokenBefore");
		try {
			Set<String> serviceNames =  keysHelper.getListOfServiceNames();
	
	        if (!serviceNames.isEmpty())
	        {
	            modelAndView.addObject("serviceNames", formatForJavaScript(serviceNames));
	        }
		} catch (Exception e) {
			logger.error("Issue populating list of service names, recoverable error.",e);
			throw new Exception (e);
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/encrypt", method = RequestMethod.POST)
	@ExceptionHandler({Exception.class})
	public ModelAndView encryptText(
			@RequestParam("serviceNames") String serviceName,
			@RequestParam("text") String text) throws Exception {

		ModelAndView modelAndView = new ModelAndView("encryptTokenResult");

        try {
			TokenEncrypter tokenEncrypter;
			if(tokenEncrypters.containsKey(serviceName)) {
				tokenEncrypter = tokenEncrypters.get(serviceName);
			} else {
				ServiceKey sk = keysHelper.readServiceKeyFromFileSystem(serviceName);
				if(sk != null) {
					tokenEncrypter = new BouncyCastleTokenEncrypter(sk.getPublicKey());
					tokenEncrypters.put(sk.getServiceName(), tokenEncrypter);
				} else {
					throw new Exception("Issue finding the Service Key");
				}
				
			}
				
	        final String token = tokenEncrypter.encrypt(text);
	        modelAndView.addObject("serviceName", serviceName);
			modelAndView.addObject("encryptedText", token);
		
        } catch (Exception e) {
    		logger.error("Error encrypting text",e);
			// TODO Will be refactored as part of Exception handling refactoring
/*
        	modelAndView = new ModelAndView("encryptTokenBefore");
			modelAndView.addObject("error", Constants.ENCRYPTION_FAILED);
			modelAndView.getModelMap().addAttribute("serviceNames",serviceName);
			modelAndView.getModelMap().addAttribute("text",text);
*/
        }

		return modelAndView;
	}
	
	private String formatForJavaScript(Set<String> serviceNames) {
		Iterator<String> iterator = serviceNames.iterator();
		String commaSeparated = "[ '" + iterator.next() + "'";
		for (; iterator.hasNext();) 
			commaSeparated =  commaSeparated + ", '" + iterator.next() + "'";
		commaSeparated = commaSeparated+ "]";
		return commaSeparated;
	}

}
