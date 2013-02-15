package edu.wisc.doit.tcrypt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CreateControllerTest {
	@InjectMocks private CreateController createController;
	
	@Test
	public void testInitReturnsCorrectView() throws Exception{
		ModelAndView handleRequest = createController.createServiceKeyInit();
		assertEquals(handleRequest.getViewName(),"createServiceKeyBefore");
	}
	
	@Test
	public void testCreateKeyWorksAsExpected() throws Exception {
		ModelAndView mav = createController.createServiceKey("test", 2048);
		//TODO: API waiting until it is complete.
		//assertEquals(mav.getViewName(),"CreateServiceKeyDownload");
	}

}
