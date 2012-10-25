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
package edu.wisc.doit.tcrypt;

import java.io.File;
import java.io.FileWriter;
import java.security.KeyPair;

import org.bouncycastle.openssl.PEMWriter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@org.springframework.stereotype.Controller
public class HomeController {

	// Inject this later
	private final TokenKeyPairGenerator bouncyCastleKeyPairGenerator;
	private final TcryptHelper tcryptHelper;
	private AuthenticationState remoteUser;
	
	public HomeController() {
		tcryptHelper = new TcryptHelper();
		bouncyCastleKeyPairGenerator = new BouncyCastleKeyPairGenerator();
		remoteUser = new AuthenticationState();
	}

	@RequestMapping("/")
	public ModelAndView handleRequest() throws Exception {
		
		return new ModelAndView("tcryptCreateKey");
	}

	@RequestMapping("/create")
	public ModelAndView createServiceKey(
			@RequestParam("serviceName") String serviceName,
			@RequestParam("keyLength") int keyLength) throws Exception {

		KeyPair generateKeyPair = bouncyCastleKeyPairGenerator.generateKeyPair();
		ModelAndView modelAndView = new ModelAndView("tcryptCreatedKey");
		
		final File privateKeyFile = new File(tcryptHelper.getFileLocationToSaveOnServer(serviceName, remoteUser.getCurrentUserName(), Constants.PRIVATE_SUFFIX));
		FileWriter privateKeyFileWriter = new FileWriter(privateKeyFile);
		
		final File publicKeyFile = new File(tcryptHelper.getFileLocationToSaveOnServer(serviceName, remoteUser.getCurrentUserName(), Constants.PUBLIC_SUFFIX));
		FileWriter publicKeyFileWriter = new FileWriter(publicKeyFile);
		
		try {
			final PEMWriter privatePemWriter = new PEMWriter(privateKeyFileWriter);
			privatePemWriter.writeObject(generateKeyPair.getPrivate());
			privatePemWriter.flush();
			privatePemWriter.close();
			
			final PEMWriter publicPemWriter = new PEMWriter(publicKeyFileWriter);
	        publicPemWriter.writeObject(generateKeyPair.getPublic());
	        publicPemWriter.flush();
	        publicPemWriter.close();
		}
		
		catch(Exception e){
			modelAndView.addObject("error", Constants.KEY_NOT_CREATED);
			return modelAndView;
		}
		
		finally {
			publicKeyFile.setReadOnly(); // changing permissions to readonly
			privateKeyFile.setReadOnly(); // changing permissions to readonly
			publicKeyFileWriter.close();
			privateKeyFileWriter.close();
		}
		
		modelAndView.addObject("serviceName", serviceName);
		return modelAndView;
	}
}
