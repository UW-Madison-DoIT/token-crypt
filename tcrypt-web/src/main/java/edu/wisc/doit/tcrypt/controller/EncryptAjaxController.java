package edu.wisc.doit.tcrypt.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wisc.doit.tcrypt.BouncyCastleTokenEncrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;
import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.EncryptToken;
import edu.wisc.doit.tcrypt.vo.ServiceKey;

@Controller
public class EncryptAjaxController {
	
	protected static final Logger logger = LoggerFactory.getLogger(EncryptAjaxController.class);
	
	private TCryptServices tcryptServices;
	private HashMap<String,TokenEncrypter> tokenEncrypters;
	
	@Autowired
	public EncryptAjaxController(TCryptServices tcryptServices) {
		this.tcryptServices = tcryptServices;
		tokenEncrypters = new HashMap<String,TokenEncrypter>();
	}
	
	@RequestMapping(value = "/encryptAjax", method = RequestMethod.POST)
	public @ResponseBody EncryptToken encryptTextAjax(@ModelAttribute (value="encryptToken") EncryptToken token, BindingResult result, HttpServletResponse response) {

		try {
			TokenEncrypter tokenEncrypter = getTokenEncrypter(token.getServiceKeyName());
			token.setEncryptedText(tokenEncrypter.encrypt(token.getUnencryptedText()));
		} catch (Exception e) {
			logger.error("Could not encrypt text",e);
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			token.setErrorMessage("There was an issue encrypting the text, please contact an admin if this behavior continues");
		}
		
		return token;
	}
	
	//	private method

	private TokenEncrypter getTokenEncrypter (String serviceName) throws ServiceErrorException, IOException {
		
		TokenEncrypter tokenEncrypter = null;
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
		return tokenEncrypter;
	}
	
}
