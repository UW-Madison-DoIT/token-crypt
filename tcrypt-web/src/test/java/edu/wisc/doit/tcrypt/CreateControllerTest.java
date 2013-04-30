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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.doit.tcrypt.controller.CreateController;
import edu.wisc.doit.tcrypt.dao.impl.KeysKeeper;
import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.exception.ValidationException;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.services.impl.TCryptServicesImpl;


@RunWith(MockitoJUnitRunner.class)
public class CreateControllerTest {
	@InjectMocks private CreateController createController;
	@Mock private HttpServletRequest request;
	@Mock private HttpSession session;
	@Mock private TCryptServices tcryptServices;
	private KeyPair kp;
	private BouncyCastleKeyPairGenerator keyPairGenerator;
	
	@Test
	public void testInitReturnsCorrectView() throws Exception{
		ModelAndView handleRequest = createController.createServiceKeyInit();
		assertEquals(handleRequest.getViewName(),"createServiceKeyBefore");
	}
	
	@Test
	public void testKeyCreationFailure() throws Exception {
		when(tcryptServices.generateKeyPair(2048)).thenReturn(null);
		try {
			createController.createServiceKey("test", 2048, request);
			fail();
		} catch (Exception e) {
			if(e instanceof ServiceErrorException) { 
				assert(true);
			} else {
				fail();
			}
		}
	}
	
	@Test
	public void testValidation() throws Exception {
		//test underscore
		try {
			createController.createServiceKey("test_with_underscore", 2048, request);
		} catch (ValidationException e) {
			assertEquals("error.serviceNameUnderscore",e.getMessage());
		}
		
		//test not null
		try {
			createController.createServiceKey(null, 2048, request);
		} catch (ValidationException e) {
			assertEquals("error.serviceNameRequired",e.getMessage());
		}
		
		//test empty string
		try {
			createController.createServiceKey("", 2048, request);
		} catch (ValidationException e) {
			assertEquals("error.serviceNameRequired",e.getMessage());
		}
	}
	
	@Test
	public void testKeyCreationSuccess() throws Exception {
		//setup
		keyPairGenerator = new BouncyCastleKeyPairGenerator();
		ResourceBundle messages = ResourceBundle.getBundle("webapp");
		TCryptServices tcs = new TCryptServicesImpl(new KeysKeeper(messages.getString("edu.wisc.doit.tcrypt.path.keydirectory"), keyPairGenerator));
		kp = tcs.generateKeyPair(2048);
		//setup when statements
		when(tcryptServices.generateKeyPair(2048)).thenReturn(kp);
		when(request.getSession()).thenReturn(session);
		
		//test
		ModelAndView mav = createController.createServiceKey("test", 2048, request);
		assertEquals(mav.getViewName(),"createServiceKeyDownload");
	}

}
