package edu.wisc.doit.tcrypt;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import static org.junit.Assert.assertEquals;
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
		ModelAndView mav = createController.createServiceKey("test", 2048, request);
		assertEquals(mav.getViewName(),"createServiceKeyBefore");
	}
	
	@Test
	public void testKeyCreationSuccess() throws Exception {
		ModelAndView mav = createController.createServiceKey("test", 2048, request);
		//assertEquals(mav.getViewName(),"createServiceKeyDownload");
	}

}
