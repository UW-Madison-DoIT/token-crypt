/**
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.wisc.doit.tcrypt.controller;

import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.exception.ValidationException;
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
import java.util.List;

@Controller
public class CreateController extends BaseController {

	private TCryptServices tcryptServices;
	
	@Autowired
	public CreateController(TCryptServices tcryptServices) {
		this.tcryptServices = tcryptServices;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createServiceKeyInit() {
		return new ModelAndView("createServiceKeyBefore");
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView createServiceKey(
			@RequestParam("serviceName") String serviceName,
			@RequestParam("keyLength") int keyLength,
			HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView("createServiceKeyDownload");
		
		//validation
		String validationResult = validate(serviceName);
		if(!validationResult.isEmpty()) {
			throw new ValidationException(validationResult);
		}
		
		try {
		
			//Generate keys
			KeyPair generatedKeyPair = tcryptServices.generateKeyPair(keyLength);
			if(generatedKeyPair == null) {
				throw new Exception("Error generating key pair");
			}
			String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "na";
			
			//Create ServiceKey Object and write public key out to FS
			ServiceKey sk = new ServiceKey(serviceName,keyLength,username,new Date(),generatedKeyPair.getPublic(),generatedKeyPair.getPrivate());
			tcryptServices.writeServiceKeyToFileSystem(sk);
			
			//Add serviceKey object on session (for download) and put the serviceName in the object list
			request.getSession().setAttribute("serviceKey_"+sk.getServiceName(), sk);
			modelAndView.addObject("serviceName", serviceName);
		} catch (Exception e) {
			logger.error("An error occurred when creating a service key",e);
			throw new ServiceErrorException(serviceName,"error.createServiceKey");
		}

		return modelAndView;
	}
	
	@ExceptionHandler(ValidationException.class)
	public ModelAndView handleException(ValidationException e) throws Exception {
		ModelAndView mav  = createServiceKeyInit();
		mav.addObject("errorMessage", e.getErrorMessage());		
		return mav;
	}
	
	@ExceptionHandler(ServiceErrorException.class)
	public ModelAndView handleException(ServiceErrorException e) throws Exception {
		ModelAndView mav  = createServiceKeyInit();

		mav.addObject("errorMessage",e.getErrorMessage());
		mav.addObject("zero",e.getServiceName());
		
		return mav;
	}
	
	private String validate(String serviceName) {
		String error = "";
		if(serviceName == null || serviceName.isEmpty()) {
			error = "error.serviceNameRequired";			
		} else if (-1 != serviceName.indexOf("_")) {
			error = "error.serviceNameUnderscore";
		} else {
			//Validate if service exists
			List<String> serviceNames =  tcryptServices.getListOfServiceNames();
			if(serviceNames.contains(serviceName)) {
				error = "error.serviceNameExists";
			}
		}
		
		return error;
	}
}
