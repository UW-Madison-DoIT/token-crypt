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

import edu.wisc.doit.tcrypt.controller.DownloadController;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.Reader;
import java.security.PublicKey;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DownloadControllerTest
{
	@InjectMocks
	private DownloadController downloadController;
	@Mock
	private TCryptServices tCryptServices;
	@Mock
	private ServiceKey serviceKey;
	@Mock
	private PublicKey publicKey;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private Reader publicKeyReader;

	@Test
	public void testDownloadAKey() throws Exception
	{
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.getSession().setAttribute("serviceKey_testkey", serviceKey);
		when(serviceKey.getPublicKey()).thenReturn(publicKey);
		downloadController.downloadKey("testkey", "public", request, response);
	}
}
