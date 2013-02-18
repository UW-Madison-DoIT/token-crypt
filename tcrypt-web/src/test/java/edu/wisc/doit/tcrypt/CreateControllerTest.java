package edu.wisc.doit.tcrypt;

import java.security.KeyPair;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.doit.tcrypt.controller.CreateController;
import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.exception.ServiceErrorException;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateControllerTest {
	@InjectMocks private CreateController createController;
	@Mock private HttpServletRequest request;
	@Mock private TCryptServices tcryptServices;
	
	@Test
	public void testInitReturnsCorrectView() throws Exception{
		ModelAndView handleRequest = createController.createServiceKeyInit();
		assertEquals(handleRequest.getViewName(),"createServiceKeyBefore");
	}
	
	@Test
	public void testKeyCreationFailure() throws Exception {
		when(tcryptServices.generateKeyPair(2048)).thenReturn(null);
		try {
			ModelAndView mav = createController.createServiceKey("test", 2048, request);
			assertNotNull(mav);
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
		//TODO Not sure how to test this since Mockito can't mock KeyPair class.
		/*when(tcryptServices.generateKeyPair(2048)).thenReturn(generatedKeyPair);
		ModelAndView mav = createController.createServiceKey("test", 2048, request);
		assertEquals(mav.getViewName(),"createServiceKeyDownload");*/
	}

}
