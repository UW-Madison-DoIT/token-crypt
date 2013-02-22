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

import edu.wisc.doit.tcrypt.controller.EncryptController;
import edu.wisc.doit.tcrypt.services.TCryptServices;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EncryptControllerTest {
	@InjectMocks private EncryptController encryptController;
	@Mock private TCryptServices tcryptServices;
	
	@Test
	public void shouldHandleSlashHandleMapping() throws Exception{
		ModelAndView handleRequest = encryptController.encryptTextInit("");
		assertEquals(handleRequest.getViewName(),"encryptTokenBefore");
	}
	
	@Test
	public void testSubmittingEncryption() throws Exception {
/*
		when(keyKeeper.getKeyLocationToDownloadFromServer("serviceNameTest", as.getCurrentUserName(), "public")).thenReturn("test");
		ModelAndView mav = encryptController.encryptText("serviceNameTest","SuperSecretPassword");
		//TODO: wait for API to be complete so controller isn't doing the file processing.
*/
		//assertEquals(mav.getViewName(),"encryptTokenResult");
	}
}
