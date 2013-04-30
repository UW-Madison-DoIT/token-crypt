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

import java.io.PrintWriter;
import java.security.Key;
import java.security.KeyPair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.openssl.PEMWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DownloadController extends BaseController
{
	@RequestMapping("/download")
	public void downloadKey(@RequestParam("serviceName") String serviceName, @RequestParam("keyType") String keyType, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try
		{
		    KeyPair sk = (KeyPair) request.getSession().getAttribute("serviceKey_" + serviceName);
			
		    response.setContentType("application/x-pem-file");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + keyType + "-" + serviceName + ".pem" + "\"");
			
			Key key = "private".equalsIgnoreCase(keyType) ? sk.getPrivate() : sk.getPublic();
			
			try (final PEMWriter pemWriter = new PEMWriter(new PrintWriter(response.getOutputStream()))) {
			    pemWriter.writeObject(key);
			}
		}
		catch (Exception e)
		{
			logger.error("Issue downloading the key " + keyType, e);
			throw new Exception(e);
		}
	}
}
