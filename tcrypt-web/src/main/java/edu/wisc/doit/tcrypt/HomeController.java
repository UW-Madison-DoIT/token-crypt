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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.openssl.PEMWriter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@org.springframework.stereotype.Controller
public class HomeController {

	// Inject this later
	private final TokenKeyPairGenerator bouncyCastleKeyPairGenerator = new BouncyCastleKeyPairGenerator();

	@RequestMapping("/")
	public ModelAndView handleRequest() throws Exception {

		return new ModelAndView("tcryptCreateKey");
	}

	@RequestMapping("/create")
	public ModelAndView createServiceKey(
			@RequestParam("serviceName") String serviceName,
			@RequestParam("keyLength") int keyLength) throws Exception {

		// Get it from the login session
		String remoteUser = "bbadger";

		final Date generationTimestamp = new Date();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyyMMdd_HHmmss");
		final String keyFilePrefix = serviceName + "_" + remoteUser + "_"
				+ simpleDateFormat.format(generationTimestamp) + "-";

		KeyPair generateKeyPair = bouncyCastleKeyPairGenerator.generateKeyPair();

		//configure directory via properties [Directory Not Configured]
		final File privateKeyFile = new File("", keyFilePrefix + "private.pem");
		final PEMWriter privatePemWriter = new PEMWriter(new FileWriter(privateKeyFile));
		privatePemWriter.writeObject(generateKeyPair.getPrivate());
		privatePemWriter.flush();
		privatePemWriter.close();
		
		//configure directory via properties [Directory Not Configured]
		final File publicKeyFile = new File("", keyFilePrefix + "public.pem");
        final PEMWriter publicPemWriter = new PEMWriter(new FileWriter(publicKeyFile));
        publicPemWriter.writeObject(generateKeyPair.getPublic());
        publicPemWriter.flush();
        publicPemWriter.close();

		ModelAndView modelAndView = new ModelAndView("tcryptCreatedKey");
		modelAndView.addObject("privateKey", privateKeyFile);
		modelAndView.addObject("publicKey", publicKeyFile);
		return modelAndView;
	}
}
