package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EncryptControllerTest {
	@InjectMocks private EncryptController encryptController;
	@Mock private IKeysKeeper keyKeeper;
	@Mock private AuthenticationState as;
	
	@Test
	public void shouldHandleSlashHandleMapping() throws Exception{
		ModelAndView handleRequest = encryptController.encryptTextInit();
		assertEquals(handleRequest.getViewName(),"encryptTokenBefore");
	}
	
	@Test
	public void testSubmittingEncryption() throws Exception {
/*
		when(keyKeeper.getKeyLocationToDownloadFromServer("serviceNameTest", as.getCurrentUserName(), "public")).thenReturn("test");
		ModelAndView mav = encryptController.encryptText("serviceNameTest","SuperSecretPassword");
		//TODO: wait for API to be complete so controller isn't doing the file processing.
*/
		//assertEquals(mav.getViewName(),"encryptTokenResult");
	}
}
