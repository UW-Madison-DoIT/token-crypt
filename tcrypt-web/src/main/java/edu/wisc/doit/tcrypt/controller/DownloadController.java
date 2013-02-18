package edu.wisc.doit.tcrypt.controller;

import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Reader;
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
			Reader keyReader = tcryptServices.getKeyAsInputStreamReader(key);
			IOUtils.copy(keyReader, response.getOutputStream());
		}
		catch (Exception e)
		{
			logger.error("Issue downloading the key " + keyType, e);
			throw new Exception(e);
		}
	}
}
