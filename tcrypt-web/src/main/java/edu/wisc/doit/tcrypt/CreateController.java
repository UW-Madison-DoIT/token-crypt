package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import org.bouncycastle.openssl.PEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;

@Controller
public class CreateController extends BaseController {

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
	@ExceptionHandler(Exception.class)
	public ModelAndView createServiceKeyInit() {
		ModelAndView mv = new ModelAndView("createServiceKeyBefore");
		return mv;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ExceptionHandler(Exception.class)
	public ModelAndView createServiceKey(
			@RequestParam("serviceName") String serviceName,
			@RequestParam("keyLength") int keyLength) throws Exception {

		ModelAndView modelAndView = new ModelAndView("createServiceKeyDownload");

/*
		try {
			//error checking
			if(tcryptHelper.checkIfKeyExistsOnServer(serviceName, authenticationState.getCurrentUserName()))
			{
				modelAndView.addObject("error", Constants.KEY_ALREADY_FOUND);
				return modelAndView;
			}
			
			KeyPair generateKeyPair = bouncyCastleKeyPairGenerator.generateKeyPair();
			
			final File privateKeyFile = new File(tcryptHelper.getKeyLocationToSaveOnServer(serviceName, authenticationState.getCurrentUserName(), Constants.PRIVATE_SUFFIX));
			FileWriter privateKeyFileWriter = new FileWriter(privateKeyFile);
			
			final File publicKeyFile = new File(tcryptHelper.getKeyLocationToSaveOnServer(serviceName, authenticationState.getCurrentUserName(), Constants.PUBLIC_SUFFIX));
			FileWriter publicKeyFileWriter = new FileWriter(publicKeyFile);
			
			try {
				generateAndWriteKeys(generateKeyPair, privateKeyFileWriter,	publicKeyFileWriter);
			} catch(Exception e) {
				logger.error("Issue during key writing: " + e.getMessage(),e);
				modelAndView = new ModelAndView ("createServiceKeyBefore");
				
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
			
		} catch(Exception e) {
			logger.error("Issue during key creation: " + e.getMessage(),e);
			modelAndView = new ModelAndView ("createServiceKeyBefore");
			modelAndView.getModelMap().addAttribute("serviceName", serviceName);
			modelAndView.getModelMap().addAttribute("keyLength", keyLength);
			modelAndView.addObject("error", Constants.KEY_NOT_CREATED);
			return modelAndView;
		}
*/
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
