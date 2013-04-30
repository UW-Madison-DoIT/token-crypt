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

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wisc.doit.tcrypt.TokenEncrypter;
import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.vo.EncryptToken;
import edu.wisc.doit.tcrypt.vo.ServiceKey;

@Controller
public class EncryptAjaxController {
	
	protected static final Logger logger = LoggerFactory.getLogger(EncryptAjaxController.class);
	
	private final IKeysKeeper keysKeeper;
	
	@Autowired
	public EncryptAjaxController(IKeysKeeper keysKeeper) {
		this.keysKeeper = keysKeeper;
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
		ServiceKey sk = keysKeeper.getServiceKey(serviceName);
		if (sk == null) {
		    throw new ServiceErrorException(serviceName, "error.serviceKeyNotFound");
		}

		return sk.getTokenEncrypter();
	}
}
