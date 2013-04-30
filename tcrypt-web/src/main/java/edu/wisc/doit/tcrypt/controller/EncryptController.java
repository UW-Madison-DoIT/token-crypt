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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.doit.tcrypt.BouncyCastleFileEncrypter;
import edu.wisc.doit.tcrypt.FileEncrypter;
import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.exception.ValidationException;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;

@Controller
public class EncryptController extends BaseController {

    private final Map<String,FileEncrypter> fileEncrypters = new ConcurrentHashMap<String, FileEncrypter>();
	private final TCryptServices tcryptServices;
	
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
	public ModelAndView encryptFile(@RequestParam("fileToEncrypt") MultipartFile file, @RequestParam("selectedServiceName") String serviceName, HttpServletResponse response) throws Exception {
	    if (file.isEmpty()) {
    		ModelAndView modelAndView = encryptTextInit();
    		modelAndView.addObject("selectedServiceName",serviceName);
    		return modelAndView;
	    }
		
	    final FileEncrypter fileEncrypter = this.getFileEncrypter(serviceName);
	    final String filename = FilenameUtils.getName(file.getOriginalFilename());
	    
	    response.setHeader("Content-Type", "application/x-tar");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".tar" + "\"");
	    
	    final ServletOutputStream outputStream = response.getOutputStream();
	    fileEncrypter.encrypt(filename, file.getInputStream(), outputStream);
	    outputStream.close();
		
	    return null;
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
		
		mav.addObject(e.getMessage());
		
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
		
		mav.addObject("errorMessage",e.getMessage());
		mav.addObject("zero",e.getServiceName());
		
		return mav;
	}
	


    private FileEncrypter getFileEncrypter (String serviceName) throws ServiceErrorException, IOException {
        
        FileEncrypter fileEncrypter = fileEncrypters.get(serviceName);
        if (fileEncrypter != null) {
            return fileEncrypter;
        }
        
        ServiceKey sk = tcryptServices.readServiceKeyFromFileSystem(serviceName);
        if (sk == null || sk.getPublicKey() == null) {
            throw new ServiceErrorException(serviceName, "error.serviceKeyNotFound");
        }
        
        fileEncrypter = new BouncyCastleFileEncrypter(sk.getPublicKey());
        fileEncrypters.put(sk.getServiceName(), fileEncrypter);

        return fileEncrypter;
    }
}
