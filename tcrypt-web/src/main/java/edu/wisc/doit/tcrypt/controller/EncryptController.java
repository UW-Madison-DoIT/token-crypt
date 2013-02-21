package edu.wisc.doit.tcrypt.controller;

import edu.wisc.doit.tcrypt.BouncyCastleTokenEncrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;
import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.exception.ValidationException;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import java.util.List;

@Controller
public class EncryptController extends BaseController {

	private TCryptServices tcryptServices;
	private HashMap<String,TokenEncrypter> tokenEncrypters;
	
	@Autowired
	public EncryptController(TCryptServices tcryptServices) {
		this.tcryptServices = tcryptServices;
		tokenEncrypters = new HashMap<String,TokenEncrypter>();
	}
	
	//Request actions
	
	@RequestMapping(value = "/encrypt/{selectedServiceName}", method = RequestMethod.GET)
	public ModelAndView encryptTextInit(@PathVariable String selectedServiceName) throws Exception {
		ModelAndView modelAndView = new ModelAndView("encryptTokenBefore");
		modelAndView.addObject("serviceNames", tcryptServices.getListOfServiceNames());
		modelAndView.addObject("selectedServiceName",selectedServiceName);
		return modelAndView;
	}

	@RequestMapping(value = "/encryptionServices", method = RequestMethod.GET)
	public @ResponseBody List<String> getShopInJSON()
	{
		return tcryptServices.getListOfServiceNames();
	}

	@RequestMapping(value = "/encrypt", method = RequestMethod.POST)
	public ModelAndView encryptText(
			@RequestParam("serviceNames") String serviceName,
			@RequestParam("text") String text) throws Exception {

		ModelAndView modelAndView = new ModelAndView("encryptTokenResult");

		TokenEncrypter tokenEncrypter;
		if(tokenEncrypters.containsKey(serviceName)) {
			tokenEncrypter = tokenEncrypters.get(serviceName);
		} else {
			ServiceKey sk = tcryptServices.readServiceKeyFromFileSystem(serviceName);
			if(sk != null  && sk.getPublicKey() != null) {
				tokenEncrypter = new BouncyCastleTokenEncrypter(sk.getPublicKey());
				tokenEncrypters.put(sk.getServiceName(), tokenEncrypter);
			} else {
				throw new ServiceErrorException(serviceName,"error.serviceKeyNotFound");
			}
			
		}
		final String token;
		try {
			token = tokenEncrypter.encrypt(text);
		} catch (Exception e) {
			logger.error("Could not encrypt text",e);
			throw new ValidationException("error.encryptionFailed");
		}
        modelAndView.addObject("serviceName", serviceName);
		modelAndView.addObject("encryptedText", token);
		return modelAndView;
	}
	
	//Exception Handlers
	
	@ExceptionHandler(ValidationException.class)
	public ModelAndView handleException(ValidationException e) throws Exception {
		ModelAndView mav;
		try {
			mav = encryptTextInit("");
		} catch (Exception e1) {
			logger.error("Error resetting view after error",e);
			throw new Exception(e);
		}
		
		mav.addObject(e.getErrorMessage());
		
		return mav;
	}
	
	@ExceptionHandler(ServiceErrorException.class)
	public ModelAndView handleException(ServiceErrorException e) throws Exception {
		ModelAndView mav;
		try {
			mav = encryptTextInit("");
		} catch (Exception e1) {
			logger.error("Error resetting view after error",e);
			throw new Exception(e);
		}
		
		mav.addObject("errorMessage",e.getErrorMessage());
		mav.addObject("zero",e.getServiceName());
		
		return mav;
	}
}
