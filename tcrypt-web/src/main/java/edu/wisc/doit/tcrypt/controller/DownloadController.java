package edu.wisc.doit.tcrypt.controller;

import java.io.FileWriter;
import java.security.Key;

import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;

import org.bouncycastle.openssl.PEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class DownloadController extends BaseController {
	
	private TCryptServices tcryptServices;
	
	@Autowired
	public DownloadController(TCryptServices tcryptServices) {
		this.tcryptServices = tcryptServices;
	}
	
	@RequestMapping("/download")
	public void downloadKey(@RequestParam("serviceName") String serviceName,@RequestParam("keyType") String keyType, HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			ServiceKey sk = (ServiceKey) request.getSession().getAttribute("serviceKey_" + serviceName);
			
		    response.setContentType("text/plain");
		    response.setHeader("Content-Disposition", "attachment; filename=\"" + keyType + sk.getServiceName() + ".pem" + "\"");
		    //response.setContentLength(keyAsBytes.length);
		    //FileCopyUtils.copy(in, response.getWriter());
			//FileCopyUtils.copy(sk.getPrivateKey().getEncoded(), response.getOutputStream());
		    
		    //Once this is fixed in the API replace with above
		    final PEMWriter pemWriter = new PEMWriter(response.getWriter());
		    Key myKey = "private".equals(keyType) ? sk.getPrivateKey() : sk.getPublicKey();
		    pemWriter.writeObject(myKey);
			pemWriter.close();
		    
		} catch (Exception e) {
			logger.error("Issue downloading the key " + keyType,e);
			throw new Exception (e);
		}
	}
}
