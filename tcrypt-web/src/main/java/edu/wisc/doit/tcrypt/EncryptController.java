package edu.wisc.doit.tcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EncryptController {

	private IKeysKeeper tcryptHelper;
	private AuthenticationState authenticationState;
	private HashMap<String,TokenEncrypter> tokenEncrypters;
	
	@Autowired
	public EncryptController(IKeysKeeper tcryptHelper, AuthenticationState authenticationState) {
		this.tcryptHelper = tcryptHelper;
		this.authenticationState = authenticationState;
		tokenEncrypters = new HashMap<String,TokenEncrypter>();
	}
	
	@RequestMapping(value = "/encrypt", method = RequestMethod.GET)
	public ModelAndView encryptText() {
		ModelAndView modelAndView = new ModelAndView("encryptTokenBefore");
		
		/*Set<String> serviceNames =  tcryptHelper.getListOfServiceNames();

        if (!serviceNames.isEmpty())
        {
            modelAndView.addObject("serviceNames", formatForJavaScript(serviceNames));
        }*/
		return modelAndView;
	}
	
	@RequestMapping(value = "/encrypt", method = RequestMethod.POST)
	public ModelAndView encryptText(
			@RequestParam("encryptServiceName") String serviceName,
			@RequestParam("text") String text) throws Exception {

		ModelAndView modelAndView = new ModelAndView("encryptTokenResult");
        
		TokenEncrypter tokenEncrypter;
		if(tokenEncrypters.containsKey(serviceName))
			tokenEncrypter = tokenEncrypters.get(serviceName);
		else
		{
			String keyFileName = tcryptHelper.getKeyLocationToDownloadFromServer(serviceName, authenticationState.getCurrentUserName(), Constants.PUBLIC_SUFFIX);
			tokenEncrypter = new BouncyCastleTokenEncrypter(new InputStreamReader(new FileInputStream(new File(keyFileName))));
			tokenEncrypters.put(serviceName, tokenEncrypter);
		}
			
        final String token = tokenEncrypter.encrypt(text);
        modelAndView.addObject("serviceName", serviceName);
		modelAndView.addObject("encryptedText", token);
		return modelAndView;
	}
	
	private String formatForJavaScript(Set<String> serviceNames) {
		Iterator<String> iterator = serviceNames.iterator();
		String commaSeparated = "[ '" + iterator.next() + "'";
		for (; iterator.hasNext();) 
			commaSeparated =  commaSeparated + ", '" + iterator.next() + "'";
		commaSeparated = commaSeparated+ "]";
		return commaSeparated;
	}

}
