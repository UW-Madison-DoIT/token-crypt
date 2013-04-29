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

import edu.wisc.doit.tcrypt.BouncyCastleTokenEncrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;
import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.exception.ValidationException;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.EncryptToken;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

@Controller
public class EncryptController extends BaseController {

	private TCryptServices tcryptServices;
	
	@Autowired
	public EncryptController(TCryptServices tcryptServices) {
		this.tcryptServices = tcryptServices;
	}
	
	//Request actions
	
	@RequestMapping(value = "/encrypt", method = RequestMethod.GET)
	public ModelAndView encryptTextInit() throws Exception {
		ModelAndView modelAndView = new ModelAndView("encryptToken");
		modelAndView.addObject("serviceNames", tcryptServices.getListOfServiceNames());
		return modelAndView;
	}
	
	@RequestMapping(value = "/encrypt/{selectedServiceName:.*}", method = RequestMethod.GET)
	public ModelAndView encryptTextInit(@PathVariable String selectedServiceName) throws Exception {
		ModelAndView modelAndView = encryptTextInit();
		modelAndView.addObject("selectedServiceName",selectedServiceName);
		return modelAndView;
	}
	
	@RequestMapping(value = "/encryptionServices", method = RequestMethod.GET)
	public @ResponseBody List<String> getShopInJSON()
	{
		return tcryptServices.getListOfServiceNames();
	}
	
	@RequestMapping(value = "/encryptFile", method = RequestMethod.POST) 
	public ModelAndView encryptFile(@RequestParam("filename") String filename, @RequestParam("fileToEncrypt") MultipartFile file, @RequestParam("selectedServiceName") String serviceName, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = encryptTextInit();
		modelAndView.addObject("selectedServiceName",serviceName);
		
		if(!file.isEmpty()) {
			//TODO TCRYPT-58 encrypt and push back to user
			//response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".enc" + "\"");
			//tcryptServices.encryptFile(serviceName, file, response.getOutputStream());
		}
		
		return modelAndView;
	}
	
	//Exception Handlers
	
	@ExceptionHandler(ValidationException.class)
	public ModelAndView handleException(ValidationException e) throws Exception {
		ModelAndView mav;
		try {
			mav = encryptTextInit();
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
			mav = encryptTextInit();
		} catch (Exception e1) {
			logger.error("Error resetting view after error",e);
			throw new Exception(e);
		}
		
		mav.addObject("errorMessage",e.getErrorMessage());
		mav.addObject("zero",e.getServiceName());
		
		return mav;
	}
	
	
}
