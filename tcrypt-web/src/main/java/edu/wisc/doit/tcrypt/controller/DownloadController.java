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

import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;

@Controller
public class DownloadController extends BaseController
{
	private TCryptServices tcryptServices;

	@Autowired
	public DownloadController(TCryptServices tcryptServices)
	{
		this.tcryptServices = tcryptServices;
	}

	@RequestMapping("/download")
	public void downloadKey(@RequestParam("serviceName") String serviceName, @RequestParam("keyType") String keyType, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try
		{
			ServiceKey sk = (ServiceKey) request.getSession().getAttribute("serviceKey_" + serviceName);
			response.setContentType("application/x-pem-file");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + keyType + "-" + sk.getServiceName() + ".pem" + "\"");
			Key key = "private".equalsIgnoreCase(keyType) ? sk.getPrivateKey() : sk.getPublicKey();
			tcryptServices.writeKeyToOutputStream(key, response.getOutputStream());
		}
		catch (Exception e)
		{
			logger.error("Issue downloading the key " + keyType, e);
			throw new Exception(e);
		}
	}
}
