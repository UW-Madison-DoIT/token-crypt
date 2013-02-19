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
				//expected, do nothing
			} else {
				fail();
			}
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
