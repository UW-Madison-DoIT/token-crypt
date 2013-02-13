package edu.wisc.doit.tcrypt;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

public class HomeControllerTest {
	private HomeController homeController;
	
	@Before
    public void setup() throws IOException {
		homeController = new HomeController(new KeysKeeper("keys"));
    }
	
	@Test
	public void shouldHandleSlashHandleMapping() throws Exception{
		ModelAndView handleRequest = homeController.handleRequest();
		assertEquals(handleRequest.getViewName(),"tcryptCreateKey");
	}

}
