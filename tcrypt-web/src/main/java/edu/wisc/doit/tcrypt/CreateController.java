package edu.wisc.doit.tcrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;

import org.bouncycastle.openssl.PEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CreateController {

	private TokenKeyPairGenerator bouncyCastleKeyPairGenerator;
	private IKeysKeeper tcryptHelper;
	private AuthenticationState authenticationState;
	
	@Autowired
	public CreateController(IKeysKeeper tcryptHelper, TokenKeyPairGenerator bouncyCastleKeyPairGenerator, AuthenticationState authenticationState) {
		this.tcryptHelper = tcryptHelper;
		this.bouncyCastleKeyPairGenerator = bouncyCastleKeyPairGenerator;
		this.authenticationState = authenticationState;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createServiceKey() {
		ModelAndView mv = new ModelAndView("createServiceKeyBefore");
		return mv;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView createServiceKey(
			@RequestParam("createServiceName") String serviceName,
			@RequestParam("keyLength") int keyLength) throws Exception {

		KeyPair generateKeyPair = bouncyCastleKeyPairGenerator.generateKeyPair();
		ModelAndView modelAndView = new ModelAndView("createServiceKeyDownload");
		
		//error checking
		if(tcryptHelper.checkIfKeyExistsOnServer(serviceName, authenticationState.getCurrentUserName()))
		{
			modelAndView.addObject("error", Constants.KEY_ALREADY_FOUND);
			return modelAndView;
		}
		
		final File privateKeyFile = new File(tcryptHelper.getKeyLocationToSaveOnServer(serviceName, authenticationState.getCurrentUserName(), Constants.PRIVATE_SUFFIX));
		FileWriter privateKeyFileWriter = new FileWriter(privateKeyFile);
		
		final File publicKeyFile = new File(tcryptHelper.getKeyLocationToSaveOnServer(serviceName, authenticationState.getCurrentUserName(), Constants.PUBLIC_SUFFIX));
		FileWriter publicKeyFileWriter = new FileWriter(publicKeyFile);
		
		try {
			generateAndWriteKeys(generateKeyPair, privateKeyFileWriter,	publicKeyFileWriter);
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

	private void generateAndWriteKeys(KeyPair generateKeyPair,
			FileWriter privateKeyFileWriter, FileWriter publicKeyFileWriter)
			throws IOException {
		final PEMWriter privatePemWriter = new PEMWriter(privateKeyFileWriter);
		privatePemWriter.writeObject(generateKeyPair.getPrivate());
		privatePemWriter.flush();
		privatePemWriter.close();
		
		final PEMWriter publicPemWriter = new PEMWriter(publicKeyFileWriter);
		publicPemWriter.writeObject(generateKeyPair.getPublic());
		publicPemWriter.flush();
		publicPemWriter.close();
	}

}
